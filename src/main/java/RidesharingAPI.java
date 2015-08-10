import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private boolean sendPush(String message, DeviceType deviceType, String tokens) throws IOException {
        String contents;
        switch (deviceType){
            case IOS:{
                contents = "{"
                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
                        + "\"contents\": {\"en\": \"" + message + "\"},"
                        +"\"include_ios_tokens\": " + tokens + ", "
                        + "\"isIos\": true"
                        + "}";
                break;
            }
            case ANDROID:{
                contents = "{"
                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
                        + "\"contents\": {\"en\": \""+ message +"\"},"
                        +"\"include_android_reg_ids\": " + tokens + ", "
                        + "\"isAndroid\": true"
                        + "}";
                break;
            }
            case WINDOWS_PHONE:{
                contents = "{"
                        + "\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\","
                        + "\"contents\": {\"en\": \""+ message +"\"},"
                        +"\"include_wp_uris\": " + tokens + ", "
                        + "\"isWP:\": true"
                        + "}";
                break;
            }
            default:
                return false;
        }

        String url = "https://onesignal.com/api/v1/notifications";
        String method = "POST";
        String contentType = "application/json";

        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Content-Length", ""+contents.length());
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestProperty("Authorization", "Basic " + "MzJmZWQ2MmEtM2I4My0xMWU1LWI1YzktNWY1MTUzMGI2Y2Fi");


        OutputStream os = conn.getOutputStream();
        DataOutputStream wr = new DataOutputStream(os);
        wr.writeBytes (contents);
        wr.flush ();
        wr.close ();

        try {

            InputStream is = conn.getInputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        }catch(IOException e){

        }
        return true;
    }

    private boolean sendPushback(String message) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        String url = "https://gamethrive.com/api/v1/notifications";
        HttpPost post = new HttpPost(url);
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Authorization", "Basic MzJmZWQ2MmEtM2I4My0xMWU1LWI1YzktNWY1MTUzMGI2Y2Fi");

//        Map<String, Object> data = new HashMap<>();
//        data.put("en", "Test Ridesharing Pus");
//        JSONObject obj = new JSONObject();
//        obj.put("app_id", "32fed59e-3b83-11e5-b5c8-9f93493279d9");
//        obj.put("contents", data);
////        obj.put("app_id", "32fed59e-3b83-11e5-b5c8-9f93493279d9");
//        obj.put("isIos", "true");
//        obj.put("isAndroid", "true");

        String jsonStr = "{\"app_id\": \"32fed59e-3b83-11e5-b5c8-9f93493279d9\", "
                //+ "\"included_segments\": [\"All\"],"
//                + "\"isAndroid\": true,"
                + "\"data\": {\"foo\": \"bar\"},"
//                + "\"isIos\": true, "
                //+ "\"send_after\": \"Fri May 02 2015 00:00:00 GMT-0700 (PDT)\","
                + "\"contents\": {\"en\": \"" + message + "\"}"
                + "}";
        //System.out.println(jsonStr);
        //System.out.println(obj);
//        byte[] byteArray = obj.toString().getBytes("UTF-8");
        byte[] byteArray = jsonStr.getBytes("UTF-8");
        System.out.println(Arrays.toString(byteArray));
        post.setEntity(new ByteArrayEntity(byteArray));
        HttpResponse response = client.execute(post);


        int responseCode = response.getStatusLine().getStatusCode();

        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

//        org.apache.http.client.fluent.Request.Post("https://onesignal.com/api/v1/notifications")
//                .bodyForm(Form.form().add("id", "10").build())
//                .execute()
//                .returnContent();

        return true;
    }
}
