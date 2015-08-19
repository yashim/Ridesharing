import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;
import telegrambotapi.types.Message;
import telegrambotapi.types.ReplyKeyboardMarkup;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.*;


public class RidesharingAPI {

    private final Logger logger = LogManager.getLogger(RidesharingAPI.class);

    public RidesharingAPI(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO,
                          final SharedRideDAO sharedRideDAO, final TokenDAO tokenDAO, final DeviceDAO deviceDAO) {
//        Spark.setSecure();
        Spark.setPort(8443);
        Gson g = new Gson();
        Spark.options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        Spark.before((request,response)-> response.header("Access-Control-Allow-Origin", "*"));

        post("/createRide", (req, res) -> {
            Hashtable<String, String> createRideResult = new Hashtable<>();
            createRideResult.put("Status", "-1");
            if(isNull(Arrays.asList("userId", "token", "startPoint", "destinationPoint", "rideTime", "timeLag",
                    "capacity"), req)) {
                return createRideResult;
            }
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (token == null || !token.equals(req.queryParams("token"))){
                return createRideResult;
            }
            return rideSuggestionDAO.createRideSuggestion(
                    new RideSuggestion(Integer.parseInt(req.queryParams("userId")), req.queryParams("startPoint"),
                            req.queryParams("destinationPoint"), Timestamp.valueOf(req.queryParams("rideTime")),
                            Integer.parseInt(req.queryParams("timeLag")), Integer.parseInt(req.queryParams("capacity")),
                            Integer.parseInt(req.queryParams("capacity"))));
        }, JsonUtil.json());

        post("/getRidesList", (req, res) -> {
            Hashtable<String, String> getRidesListResult = new Hashtable<>();
            getRidesListResult.put("Status", "-1");
            if(isNull(Arrays.asList("userId", "token"), req)){
                return getRidesListResult;
            }
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (token == null || !token.equals(req.queryParams("token"))){
                return getRidesListResult;
            }
            return rideSuggestionDAO.getRides(Integer.parseInt(req.queryParams("userId")));
        }, JsonUtil.json());

        post("/cancelRide", (req, res) -> {
            Hashtable<String, String> cancelRideResult = new Hashtable<>();
            cancelRideResult.put("Status", "-1");
            if(isNull(Arrays.asList("userId", "token","rideId"), req)){
                return cancelRideResult;
            }
            int userId = Integer.parseInt(req.queryParams("userId"));
            String token = tokenDAO.getToken(userId);
            if (token == null || !token.equals(req.queryParams("token"))){
                return cancelRideResult;
            }
            return rideSuggestionDAO.delete(Integer.parseInt(req.queryParams("rideId")), userId);
        }, JsonUtil.json());

        post("/register", (req, res) ->{
//                    try {
//                        sendPush();
//                    try {
//                        sendPush("Test Message", DeviceType.IOS, "tokentokentokentoken");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        System.out.println(e.getMessage());
//                        System.out.println(e.getLocalizedMessage());
//                        System.out.println(e.toString());
//                        //System.Diagnostics.Debug.WriteLine(new StreamReader(ex.Response.GetResponseStream()).ReadToEnd());
//                    }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    Hashtable<String, String> registerResult = new Hashtable<>();
                    registerResult.put("Status", "-1");
                    if(isNull(Arrays.asList("login", "password","firstName", "lastName", "phone"), req)){
                        return registerResult;
                    }
                    if (!checkEmailFormat(req.queryParams("login"))){
                        return registerResult;
                    }
                    String phone = req.queryParams("phone");
                    phone = phone.replace("-","").replace("(","").replace(")","").replace(" ","").replace("+","");
                    if (!checkPhoneFormat(phone)) {
                        return registerResult;
                    }
                    return (userDAO.createUser(new User(req.queryParams("login"), req.queryParams("password"),
                            req.queryParams("firstName"), req.queryParams("lastName"), phone)));
                },
                JsonUtil.json());


        post("/registerDevice", (req, res) ->{
                    Hashtable<String, String> registerResult = new Hashtable<>();
                    registerResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token","pushToken","os"), req)){
                        return registerResult;
                    }

                    return (deviceDAO.createDevice(new Device(0, Integer.parseInt(req.queryParams("userId")),
                            req.queryParams("pushToken"), req.queryParams("os"), new Timestamp(new java.util.Date().getTime()))));
                },
                JsonUtil.json());

        post("/login", (req, res) -> {
                    Hashtable<String, String> loginResult = new Hashtable<>();
                    loginResult.put("Status", "-1");
                    if(isNull(Arrays.asList("login", "password"), req)){
                        return loginResult;
                    }
                    return tokenDAO.login(req.queryParams("login"), req.queryParams("password"));
                },
                JsonUtil.json());

        post("/getCurrentUser", (req, res) -> {
                    Hashtable<String, String> getCurrentUserResult = new Hashtable<>();
                    getCurrentUserResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token"), req)){
                        return getCurrentUserResult;
                    }
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return getCurrentUserResult;
                        }
                    return userDAO.getUser(Integer.parseInt(req.queryParams("userId")));
                },
                JsonUtil.json());

        post("/saveProfile", (req, res) -> {
                    Hashtable<String, String> saveProfileResult = new Hashtable<>();
                    saveProfileResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "login","firstName", "lastName", "phone"), req)){
                        return saveProfileResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return saveProfileResult;
                    }
                    if (!checkEmailFormat(req.queryParams("login"))){
                        return saveProfileResult;
                    }
                    String phone = req.queryParams("phone");
                    phone = phone.replace("-","").replace("(","").replace(")","").replace(" ","").replace("+","");
                    if (!checkPhoneFormat(phone)) {
                        return saveProfileResult;
                    }
                    return userDAO.update(new User(req.queryParams("login"), "",
                            req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone")), userId);
                },
                JsonUtil.json());

        post("/updatePassword", (req, res) -> {
                    Hashtable<String, String> saveProfileResult = new Hashtable<>();
                    saveProfileResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "password", "newPassword"), req)){
                        return saveProfileResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return saveProfileResult;
                    }
                    return userDAO.updatePassword(req.queryParams("password"),req.queryParams("newPassword"), userId);
                },
                JsonUtil.json());


        post("/getRide", (req, res) -> {
                    Hashtable<String, String> getRideResult = new Hashtable<>();
                    getRideResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "userId"), req)){
                        return getRideResult;
                    }
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return getRideResult;
                    }
                    return rideSuggestionDAO.getRide(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        //return sharedRideId or SuggestionId
        post("/joinRide", (req, res) -> {

                    Hashtable<String, String> joinRideResult = new Hashtable<>();
                    joinRideResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "userId"), req)){
                        return joinRideResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return joinRideResult;
                    }
                    return sharedRideDAO.joinRide(Integer.parseInt(req.queryParams("rideId")), userId, 1) ;
                },
                JsonUtil.json());

        post("/unjoinRide", (req, res) -> {
                    Hashtable<String, String> unjoinRideResult = new Hashtable<>();
                    unjoinRideResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "userId"), req)){
                        return unjoinRideResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return unjoinRideResult;
                    }
                    return sharedRideDAO.delete(Integer.parseInt(req.queryParams("rideId")),Integer.parseInt(req.queryParams("userId")));
                },
                JsonUtil.json());
        post("/unjoinRide", (req, res) -> {
                    Hashtable<String, String> unjoinRideResult = new Hashtable<>();
                    unjoinRideResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "userId"), req)){
                        return unjoinRideResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return unjoinRideResult;
                    }
                    return sharedRideDAO.delete(Integer.parseInt(req.queryParams("rideId")),Integer.parseInt(req.queryParams("userId")));
                },
                JsonUtil.json());

        post("/ridesharingBot", (req, res) -> {
            try {
                logger.info(req.body());
                JsonElement jsonElement = new JsonParser().parse(req.body());
                Message requestMessage = g.fromJson(jsonElement.getAsJsonObject().getAsJsonObject("message").toString(),
                        Message.class);
                if (requestMessage.getText() == null)
                    return "OK";
                String text = requestMessage.getText().toLowerCase();
                int chatId = requestMessage.getChat().getId();
                String login = "";
                if (requestMessage.getFrom().getUsername() != null || !requestMessage.getFrom().getUsername().equals(""))
                    login = requestMessage.getFrom().getUsername();

                //String[] params = text.split(" ");
                if (text.startsWith("/start") || text.startsWith("/help")) {
                    try {
                        if (!userDAO.exist(chatId)) {
                            String firstName = requestMessage.getFrom().getFirstName()==null?"":requestMessage.getFrom().getFirstName();
                            String lastName = requestMessage.getFrom().getLastName()==null?"":requestMessage.getFrom().getLastName();
                            userDAO.createUserFromTelegram(new User(login, "", firstName,
                                    lastName, ""), chatId);
                        }
                    } catch (SQLException e) {
                        sendPost(requestMessage.getChat().getId(), TelegramBotResponses.ERROR, getReplyMarkup());
                        return "OK";
                    }
                    sendPost(requestMessage.getChat().getId(), TelegramBotResponses.START, getReplyMarkup());
                    return "OK";
                }
                if (text.startsWith("/about")) {
                    sendPost(requestMessage.getChat().getId(), TelegramBotResponses.ABOUT, getReplyMarkup());
                    return "OK";
                }
                if (text.startsWith(TelegramBotResponses.showSymbol + " show") ||text.startsWith("/show") ||
                        text.startsWith("show")) {
                    //sendPost(chatId, "%F0%9F%99%89 I am thinking... Please wait", getReplyMarkup());
                    Hashtable<RideSuggestionType, List<RideDetails>> rides = rideSuggestionDAO.getRidesByChatId(chatId);
                    List<RideDetails> upcomingRides = rides.get(RideSuggestionType.UNDEFINED);
                    String responseMessage = "";
                    if (upcomingRides.size() == 0) {
                        responseMessage = TelegramBotResponses.NO_AVAILABLE_RIDES_HEADER + System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                    } else {
                        responseMessage = TelegramBotResponses.AVAILABLE_RIDES_HEADER + System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                        for (RideDetails rideDetails : upcomingRides) {
                            responseMessage = formatGetRidesResponse(rideDetails);
                            sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                        }
                    }
                    upcomingRides = rides.get(RideSuggestionType.PASSENGER);
                    if (upcomingRides.size() == 0) {
                        responseMessage = TelegramBotResponses.NO_PASSENGER_RIDES_HEADER + System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                    } else {
                        responseMessage = TelegramBotResponses.PASSENGER_RIDES_HEADER + System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                        for (RideDetails rideDetails : upcomingRides) {
                            responseMessage = formatGetRidesResponse(rideDetails);
                            sendPost(requestMessage.getChat().getId(), formatGetRidesResponseWithRoute(rideDetails), getReplyMarkup());
                        }
                    }
                    upcomingRides = rides.get(RideSuggestionType.DRIVER);
                    if (upcomingRides.size() == 0) {
                        responseMessage = TelegramBotResponses.NO_DRIVER_RIDES_HEADER + System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                    } else {
                        responseMessage = TelegramBotResponses.DRIVER_RIDES_HEADER+ System.lineSeparator();
                        sendPost(requestMessage.getChat().getId(), responseMessage, getReplyMarkup());
                        for (RideDetails rideDetails : upcomingRides) {
                            responseMessage = formatGetRidesResponse(rideDetails);
                            sendPost(requestMessage.getChat().getId(), formatGetRidesResponseWithRoute(rideDetails), getReplyMarkup());
                        }
                    }
                    return "OK";
                }
                if (text.startsWith("/create") || text.startsWith("create") ||
                        text.startsWith(TelegramBotResponses.createSymbol + " create") || text.startsWith("new") ||
                        text.startsWith("Еду")) {
                    if (requestMessage.getFrom().getUsername() == null || requestMessage.getFrom().getUsername().equals("")) {
                        sendPost(requestMessage.getChat().getId(), TelegramBotResponses.CREATE_PROVIDE_USERNAME, getReplyMarkup());
                        return "OK";
                    }
                    sendPost(chatId, TelegramBotResponses.CREATE_SPECIFY_PARAMETERS, getReplyMarkup());
                    return "OK";
                }
                if (text.startsWith("kazan") || text.startsWith("казань") || text.startsWith("innopolis") || text.startsWith("иннополис")) {

                    if (requestMessage.getFrom().getUsername() == null || requestMessage.getFrom().getUsername().equals("")) {
                        sendPost(requestMessage.getChat().getId(), TelegramBotResponses.CREATE_PROVIDE_USERNAME, getReplyMarkup());
                        return "OK";
                    }
                    User user = userDAO.getUserByChatId(chatId);
                    if(user.getLogin()==null ||user.getLogin().equals("")||
                            user.getLastName()==null ||user.getLastName().equals("") ||
                            user.getFirstName()==null ||user.getFirstName().equals("")){
                        String username = requestMessage.getFrom().getFirstName()==null?"":requestMessage.getFrom().getUsername();
                        String firstName = requestMessage.getFrom().getFirstName()==null?"":requestMessage.getFrom().getFirstName();
                        String lastName = requestMessage.getFrom().getLastName()==null?"":requestMessage.getFrom().getLastName();
                        user.setLogin(username);
                        user.setLastName(lastName);
                        user.setFirstName(firstName);
                        if(userDAO.update(chatId, username, firstName,lastName).get("Status").equals("-1")){
                            sendPost(chatId, TelegramBotResponses.CREATE_PROVIDE_USERNAME, getReplyMarkup());
                            return "OK";
                        }
                    }
                    String[] params = text.split(" ");
                    if (params.length > 2 || params.length < 1) {
                        sendPost(chatId, TelegramBotResponses.CREATE_SPECIFY_PARAMETERS, getReplyMarkup());
                        return "OK";
                    }
                    RideSuggestion rideSuggestion = new RideSuggestion();
                    if (params[0].toLowerCase().equals("kazan") || params[0].toLowerCase().equals("казань")){
                        rideSuggestion.setDestinationPoint("kazan");
                    }else if( params[0].toLowerCase().equals("innopolis") || params[0].toLowerCase().equals("иннополис")){
                        rideSuggestion.setDestinationPoint("innopolis");
                    }
                    else {
                        sendPost(chatId, TelegramBotResponses.CREATE_WRONG_CITY, getReplyMarkup());
                        return "OK";
                    }
                    int hours = 0;
                    int minutes = 0;
                    try {
                        String[] timeArr = params[1].split(":");
                        hours = Integer.parseInt(timeArr[0]);
                        minutes = Integer.parseInt(timeArr[1]);
                        if (hours < 0 || hours > 24 || minutes < 0 || minutes > 60)
                            throw new NumberFormatException();
                    } catch (Exception e) {
                        sendPost(chatId, TelegramBotResponses.CREATE_WRONG_TIMEFORMAT, getReplyMarkup());
                        return "OK";
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    //todo exclude magic number 1, edit timezone
//                cal.set(Calendar.HOUR_OF_DAY, hours-1);
                    cal.set(Calendar.HOUR_OF_DAY, hours);
                    cal.set(Calendar.MINUTE, minutes);
                    if (cal.getTime().before(new Date()))
                        cal.add(Calendar.DAY_OF_MONTH, 1);

                    rideSuggestion.setRideTime(new Timestamp(cal.getTime().getTime()));

                    if (rideSuggestion.getDestinationPoint().equals("kazan") || rideSuggestion.getDestinationPoint().equals("казань")) {
                        rideSuggestion.setStartPoint("innopolis");
                    } else if (rideSuggestion.getDestinationPoint().equals("innopolis") || rideSuggestion.getDestinationPoint().equals("иннополис")) {
                        rideSuggestion.setStartPoint("kazan");
                    }
                    rideSuggestion.setCapacity(3);
                    rideSuggestion.setFreeSeatsNumber(3);
//                    user = userDAO.getUserByChatId(chatId);
                    if (user.getId() == 0)
                        return "OK";
                    rideSuggestion.setUserId(user.getId());
                    String rideId = rideSuggestionDAO.createRideSuggestion(rideSuggestion).get("RideId");
                    if (rideId == null) {
                        logger.error("rideId == null");
                        return "OK";
                    }
                    rideSuggestion.setRideSuggestionId(Integer.parseInt(rideId));
                    sendPost(requestMessage.getChat().getId(), "%F0%9F%8E%89 You successfully created the ride!" + System.lineSeparator() +
                            formatRideSuggestion(rideSuggestion), getReplyMarkup());
                    return "OK";
                }
                //todo notify passengers
                if (text.startsWith(TelegramBotResponses.cancelSymbol + " cancel") || text.startsWith("/cancel") ||
                        text.startsWith("cancel")){
                    if (requestMessage.getReplyToMessage() == null) {
                        sendPost(chatId, TelegramBotResponses.DELETE_INFO, getReplyMarkup());
                        return "OK";
                    }
                    String replyMessage = requestMessage.getReplyToMessage().getText();
                    if (!replyMessage.startsWith("ID")) {
                        sendPost(chatId, TelegramBotResponses.DELETE_INFO, getReplyMarkup());
                        return "OK";
                    }
                    int rideId = Integer.parseInt(replyMessage.substring(4, replyMessage.indexOf(System.lineSeparator())));
                    int userId = userDAO.getUserByChatId(chatId).getId();
                    try {
                        if (rideSuggestionDAO.exist(rideId, userId)) {
                            if (rideSuggestionDAO.delete(rideId, userId).get("Status").equals("-1")) {
                                sendPost(chatId, "%F0%9F%90%B5 Sorry, you cannot cancel this ride", getReplyMarkup());
                                return "OK";
                            }
                        } else {
                            if (sharedRideDAO.delete(rideId, userId) == -1) {
                                sendPost(chatId, "%F0%9F%90%B5 Sorry, you cannot cancel this ride", getReplyMarkup());
                                return "OK";
                            }
                            //sendPost(requestMessage.getChat().getId(), "You successfully deleted a ride with ID:"+rideId+"!", getReplyMarkup());
                        }
                        sendPost(chatId, " %F0%9F%8E%89 You successfully canceled the ride with ID: " + rideId + "! Please, notify your driver or passengers if there are any", getReplyMarkup());
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                        return "OK";
                    }
                    return "OK";

                }
                if (text.startsWith(TelegramBotResponses.joinSymbol+" join")|| text.startsWith("join") ||
                        text.startsWith("/join")) {
                    if (requestMessage.getReplyToMessage() == null) {
                        sendPost(chatId, TelegramBotResponses.JOIN_INFO, getReplyMarkup());
                        return "OK";
                    }
                    if (requestMessage.getFrom().getUsername() == null || requestMessage.getFrom().getUsername().equals("")) {
                        sendPost(requestMessage.getChat().getId(), TelegramBotResponses.JOIN_PROVIDE_USERNAME, getReplyMarkup());
                        return "OK";
                    }
                    String replyMessage = requestMessage.getReplyToMessage().getText();
                    int rideId = Integer.parseInt(replyMessage.substring(4, replyMessage.indexOf(System.lineSeparator())));
                    //todo seats amount
                    User user = userDAO.getUserByChatId(chatId);
                    if(user.getLogin()==null ||user.getLogin().equals("")||
                            user.getLastName()==null ||user.getLastName().equals("") ||
                            user.getFirstName()==null ||user.getFirstName().equals("")){
                        String username = requestMessage.getFrom().getFirstName()==null?"":requestMessage.getFrom().getUsername();
                        String firstName = requestMessage.getFrom().getFirstName()==null?"":requestMessage.getFrom().getFirstName();
                        String lastName = requestMessage.getFrom().getLastName()==null?"":requestMessage.getFrom().getLastName();
                        user.setLogin(username);
                        user.setLastName(lastName);
                        user.setFirstName(firstName);
                        if(userDAO.update(chatId, username, firstName,lastName).get("Status").equals("-1")){
                            sendPost(chatId, TelegramBotResponses.CREATE_PROVIDE_USERNAME, getReplyMarkup());
                            return "OK";
                        }
                    }
                    RideSuggestion rideSuggestion = rideSuggestionDAO.getRideSuggestion(rideId);
                    if (rideSuggestion == null ) {
                        sendPost(requestMessage.getChat().getId(), "%F0%9F%90%B5 I am sorry, this magic ride does not exist", getReplyMarkup());
                        return "OK";
                    }
                    if (rideSuggestion.getUserId() == user.getId()) {
                        sendPost(requestMessage.getChat().getId(), "%F0%9F%90%B5 I am sorry, you cannot join your own ride", getReplyMarkup());
                        return "OK";
                    }
                    sharedRideDAO.joinRide(rideId, user.getId(), 1);
                    //todo
                    RideDetails rideDetails = rideSuggestionDAO.getRide(rideId);
                    sendPost(requestMessage.getChat().getId(), "%F0%9F%8E%89 You successfully joined the ride with regarded @" + rideDetails.getDriverLogin() + " " + rideDetails.getDriverName() + " " + rideDetails.getDriverLastName(), getReplyMarkup());
                    //todo
                    sendPost(rideDetails.getChatId(), "@" + user.getLogin() + " " + user.getFirstName() + " " + user.getLastName() + " joined your heaven-sent ride!", getReplyMarkup());
                    return "OK";
                }
                sendPost(chatId, TelegramBotResponses.ERROR, getReplyMarkup());
                return "OK";
            }catch(Exception e){
                logger.error(e.getMessage() + " : " + e.getCause()+" : " + e.toString());
                return "OK";
            }
        }, JsonUtil.json());


        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            logger.error(e.getMessage());
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }



    private boolean isNull(List<String> parameters, spark.Request request){
        for(String parameter : parameters){
            if(request.queryParams(parameter)==null)
                return true;
        }
        return false;
    }

    private boolean checkEmailFormat(final String email){
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean checkPhoneFormat(String phone){
        final String PHONE_PATTERN = "\\d{11}";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private Hashtable<DeviceType, String> getDeviceInfo(String userId){
        Hashtable<DeviceType, String> resultDeviceInfo = new Hashtable<>();
        resultDeviceInfo.put(DeviceType.ANDROID, "");



        return resultDeviceInfo;
    }
//    private boolean sendJoinPush(int passangerId, int driverId, int rideSuggestionId){
//        Map<String, String> contents = new HashMap<>();
//        contents.put("en","Test English Message");
//        String[] includeIosTokens = {"Hello"};
//        String[] includeAndroidRegIds = null;
//        String[] includeWpUris = null;
//        PushNotification pushNotification = new PushNotification(contents, true, includeIosTokens, false,
//                includeAndroidRegIds ,false, includeWpUris);
//        try {
//            pushNotification.sendPush();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

//    private boolean sendPush(String message, DeviceType deviceType, String tokens) throws IOException {
//        String contents;
//        switch (deviceType){
//            case IOS:{
//                contents = "{"
//                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
//                        + "\"contents\": {\"en\": \"" + message + "\"},"
//                        +"\"include_ios_tokens\": " + tokens + ", "
//                        + "\"isIos\": true"
//                        + "}";
//                break;
//            }
//            case ANDROID:{
//                contents = "{"
//                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
//                        + "\"contents\": {\"en\": \""+ message +"\"},"
//                        +"\"include_android_reg_ids\": " + tokens + ", "
//                        + "\"isAndroid\": true"
//                        + "}";
//                break;
//            }
//            case WINDOWS_PHONE:{
//                contents = "{"
//                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
//                        + "\"contents\": {\"en\": \""+ message +"\"},"
//                        +"\"include_wp_uris\": " + tokens + ", "
//                        + "\"isWP:\": true"
//                        + "}";
//                break;
//            }
//            default:
//                return false;
//        }
//
//        String url = "https://onesignal.com/api/v1/notifications";
//        String method = "POST";
//        String contentType = "application/json";
//
//        URL u = new URL(url);
//        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
//        conn.setRequestMethod(method);
//        conn.setRequestProperty("Content-Type", contentType);
//        conn.setRequestProperty("Content-Length", ""+contents.length());
//        conn.setUseCaches(false);
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//
//        conn.setRequestProperty("Authorization", "Basic " + "MzJmZWQ2MmEtM2I4My0xMWU1LWI1YzktNWY1MTUzMGI2Y2Fi");
//
//
//        OutputStream os = conn.getOutputStream();
//        DataOutputStream wr = new DataOutputStream(os);
//        wr.writeBytes (contents);
//        wr.flush ();
//        wr.close ();
//
//        try {
//
//            InputStream is = conn.getInputStream();
//
//        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//        String line;
//        StringBuffer response = new StringBuffer();
//        while((line = rd.readLine()) != null) {
//            response.append(line);
//            response.append('\r');
//        }
//        rd.close();
//        }catch(IOException e){
//
//        }
//        return true;
//    }

    private boolean sendPost(int chatId, String message, String replyMarkup){
        String url = "https://api.telegram.org/bot86148492:AAGLv840yestS5KiGODS-K0SZ2OWyp8IJ3c/sendMessage";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String urlParameters = "chat_id=" + chatId + "&text=" + message+"&reply_markup="+replyMarkup;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            if(responseCode!=200)
                return false;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    String formatGetRidesResponse(RideDetails rideDetails){
        String responseMessage = "";
        responseMessage += "ID: " + rideDetails.getRideSuggestionId() + System.lineSeparator();
        responseMessage += "Route from " + rideDetails.getStartPoint().toUpperCase() +" to "+ rideDetails.getDestinationPoint().toUpperCase() + System.lineSeparator();
        responseMessage += "Departure time: " + new SimpleDateFormat("EEEE, dd MMMM HH:mm").format(rideDetails.getRideTime()) + System.lineSeparator();
        responseMessage += "Driver: " + rideDetails.getDriverName() +" "+ rideDetails.getDriverLastName() + System.lineSeparator();
        //responseMessage += "Phone: %2b" + rideDetails.getDriverPhone() + System.lineSeparator();
        return responseMessage;
    }
    String formatGetRidesResponseWithRoute(RideDetails rideDetails){
        String responseMessage = "";
        responseMessage += "ID: " + rideDetails.getRideSuggestionId() + System.lineSeparator();
        responseMessage += "Route from " + rideDetails.getStartPoint().toUpperCase() +" to "+ rideDetails.getDestinationPoint().toUpperCase() + System.lineSeparator();
        responseMessage += "Departure time: " + new SimpleDateFormat("EEEE, dd MMMM HH:mm").format(rideDetails.getRideTime()) + System.lineSeparator();
        responseMessage += "Driver: " + rideDetails.getDriverName() +" "+ rideDetails.getDriverLastName() + System.lineSeparator();
        //responseMessage += "Phone: %2b" + rideDetails.getDriverPhone() + System.lineSeparator();
        return responseMessage;
    }
    String formatRideSuggestion(RideSuggestion rideSuggestion){
        String responseMessage = "";
        responseMessage += "ID: " + rideSuggestion.getRideSuggestionId() + System.lineSeparator();
        responseMessage += "Departure time: " + new SimpleDateFormat("EEEE, dd MMMM HH:mm").format(rideSuggestion.getRideTime()) + System.lineSeparator();
        responseMessage += "Route from " + rideSuggestion.getStartPoint().toUpperCase() +" to "+ rideSuggestion.getDestinationPoint().toUpperCase() + System.lineSeparator();
        //responseMessage += "Free seats number: " + rideSuggestion.getFreeSeatsNumber() + System.lineSeparator();
        return responseMessage;

    }
    String getReplyMarkup(){
        ReplyKeyboardMarkup.Builder builder = new ReplyKeyboardMarkup.Builder();
        builder.row(TelegramBotResponses.KEYBORD_JOIN_ICON, TelegramBotResponses.KEYBORD_SHOW_ICON);//create getrideslist delete join
        builder.row(TelegramBotResponses.KEYBORD_CREATE_ICON, TelegramBotResponses.KEYBORD_CANCEL_ICON);//, "New Ride");//, "Join", "Unjoin", "Delete Ride");
        builder.setResizeKeyboard();
        return builder.build().serialize();
    }
}
