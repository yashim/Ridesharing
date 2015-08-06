import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;

import java.sql.Timestamp;
import java.util.Hashtable;

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
            if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                    req.queryParams("startPoint")== null || req.queryParams("destinationPoint")==null ||
                    req.queryParams("rideTime") == null || req.queryParams("timeLag")== null ||
                    req.queryParams("capacity")==null ){
                Hashtable<String, String> registerResult = new Hashtable<>();
                registerResult.put("Status", "-1");
                return registerResult;
            }
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (token == null || !token.equals(req.queryParams("token"))){
                Hashtable<String, String> createRideResult = new Hashtable<>();
                createRideResult.put("Status", "-1");
                return createRideResult;
            }
            return rideSuggestionDAO.createRideSuggestion(
                    new RideSuggestion(Integer.parseInt(req.queryParams("userId")), req.queryParams("startPoint"),
                            req.queryParams("destinationPoint"), Timestamp.valueOf(req.queryParams("rideTime")),
                            Integer.parseInt(req.queryParams("timeLag")), Integer.parseInt(req.queryParams("capacity")),
                            Integer.parseInt(req.queryParams("capacity"))));
        }, JsonUtil.json());

        post("/getRidesList", (req, res) -> {
            if ( req.queryParams("userId")==null || req.queryParams("token") == null ){
                Hashtable<String, String> registerResult = new Hashtable<>();
                registerResult.put("Status", "-1");
                return registerResult;
            }
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (token == null || !token.equals(req.queryParams("token"))){
                Hashtable<String, String> getRidesListResult = new Hashtable<>();
                getRidesListResult.put("Status", "-1");
                return getRidesListResult;
            }
            return rideSuggestionDAO.getRides(Integer.parseInt(req.queryParams("userId")));
        }, JsonUtil.json());

        post("/cancelRide", (req, res) -> {
            if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                    req.queryParams("rideId") == null ){
                Hashtable<String, String> registerResult = new Hashtable<>();
                registerResult.put("Status", "-1");
                return registerResult;
            }
            int userId = Integer.parseInt(req.queryParams("userId"));
            String token = tokenDAO.getToken(userId);
            if (token == null || !token.equals(req.queryParams("token"))){
                Hashtable<String, String> cancelRideResult = new Hashtable<>();
                cancelRideResult.put("Status", "-1");
                return cancelRideResult;
            }
            return rideSuggestionDAO.delete(Integer.parseInt(req.queryParams("rideId")), userId);
        }, JsonUtil.json());

        //TODO add email and phone format checks
        post("/register", (req, res) ->{
                    String login = req.queryParams("login");
                    String password = req.queryParams("password");
                    String firstName = req.queryParams("firstName");
                    String lastName = req.queryParams("lastName");
                    String phone = req.queryParams("phone");
                    if ( login==null || password == null || firstName == null || lastName == null || phone == null ){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    return (userDAO.createUser(new User(login, password, firstName, lastName, phone)));
                },
                JsonUtil.json());

        post("/login", (req, res) -> {
                    String login = req.queryParams("login");
                    String password = req.queryParams("password");
                    if ( login==null || password == null ){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    return tokenDAO.login(req.queryParams("login"), req.queryParams("password"));
                },
                JsonUtil.json());

        post("/getCurrentUser", (req, res) -> {
                    if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                            req.queryParams("rideId") == null){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (token == null || !token.equals(req.queryParams("token"))){
                        Hashtable<String, String> getCurrentUserResult = new Hashtable<>();
                        getCurrentUserResult.put("Status", "-1");
                        return getCurrentUserResult;
                        }
                    return userDAO.getUser(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        post("/saveProfile", (req, res) -> {
                    if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                            req.queryParams("login") == null || req.queryParams("password")==null ||
                            req.queryParams("firstName") == null || req.queryParams("lastName") == null ||
                            req.queryParams("phone") == null){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        Hashtable<String, String> saveProfileResult = new Hashtable<>();
                        saveProfileResult.put("Status", "-1");
                        return saveProfileResult;
                    }
                    return userDAO.update(new User(req.queryParams("login"), req.queryParams("password"),
                            req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone")), userId);
                },
                JsonUtil.json());


        post("/getRide", (req, res) -> {
                    if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                            req.queryParams("rideId") == null ){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (token == null || !token.equals(req.queryParams("token"))){
                        Hashtable<String, String> getRideResult = new Hashtable<>();
                        getRideResult.put("Status", "-1");
                        return getRideResult;
                    }
                    return rideSuggestionDAO.getRide(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        //return sharedRideId or SuggestionId
        post("/joinRide", (req, res) -> {
                    if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                            req.queryParams("rideId") == null ){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        Hashtable<String, String> joinRideResult = new Hashtable<>();
                        joinRideResult.put("Status", "-1");
                        return joinRideResult;
                    }
                    return sharedRideDAO.joinRide(Integer.parseInt(req.queryParams("rideId")), userId, 1) ;
                },
                JsonUtil.json());

        post("/unjoinRide", (req, res) -> {
                    if ( req.queryParams("userId")==null || req.queryParams("token") == null ||
                            req.queryParams("rideId") == null ){
                        Hashtable<String, String> registerResult = new Hashtable<>();
                        registerResult.put("Status", "-1");
                        return registerResult;
                    }
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (token == null || !token.equals(req.queryParams("token"))){
                        Hashtable<String, String> unjoinRideResult = new Hashtable<>();
                        unjoinRideResult.put("Status", "-1");
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
}
