import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Request;
import spark.Spark;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spark.Spark.*;


public class RidesharingAPI {

    private final Logger logger = LogManager.getLogger(ConnectionFactory.class);

    public RidesharingAPI(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO,
                          final SharedRideDAO sharedRideDAO, final TokenDAO tokenDAO) {
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
                    if(isNull(Arrays.asList("userId", "token","rideId"), req)){
                        return getCurrentUserResult;
                    }
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return getCurrentUserResult;
                        }
                    return userDAO.getUser(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        post("/saveProfile", (req, res) -> {
                    Hashtable<String, String> saveProfileResult = new Hashtable<>();
                    saveProfileResult.put("Status", "-1");
                    if(isNull(Arrays.asList("userId", "token", "login", "password","firstName", "lastName", "phone"), req)){
                        return saveProfileResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        return saveProfileResult;
                    }
                    return userDAO.update(new User(req.queryParams("login"), req.queryParams("password"),
                            req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone")), userId);
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

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            logger.error(e.getMessage());
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }

    private boolean isNull(List<String> parameters, Request request){
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
}
