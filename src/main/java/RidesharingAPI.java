import java.sql.Timestamp;

import static spark.Spark.*;


public class RidesharingAPI {

    public RidesharingAPI(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO,
                          final SharedRideDAO sharedRideDAO, final TokenDAO tokenDAO) {

        post("/createRide", (req, res) -> {
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (!token.equals(req.queryParams("token")))
                return -1;
            return rideSuggestionDAO.createRideSuggestion(
                    new RideSuggestion(Integer.parseInt(req.queryParams("userId")), req.queryParams("startPoint"),
                            req.queryParams("destinationPoint"), Timestamp.valueOf(req.queryParams("rideTime")),
                            Integer.parseInt(req.queryParams("timeLag")), Integer.parseInt(req.queryParams("capacity")),
                            Integer.parseInt(req.queryParams("capacity"))));
        }, JsonUtil.json());

        post("/getRidesList", (req, res) -> {
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (!token.equals(req.queryParams("token")))
                return -1;
            return rideSuggestionDAO.getRides(Integer.parseInt(req.queryParams("userId")));
        }, JsonUtil.json());

        post("/cancelRide", (req, res) -> {
            String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
            if (!token.equals(req.queryParams("token")))
                return -1;
            return rideSuggestionDAO.delete(Integer.parseInt(req.queryParams("rideId")));
        }, JsonUtil.json());

        post("/register", (req, res) -> userDAO.createUser(new User(req.queryParams("login"), req.queryParams("password"),
                        req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone"))),
                JsonUtil.json());

        post("/login", (req, res) -> tokenDAO.login(req.queryParams("login"), req.queryParams("password")),
                JsonUtil.json());

        //todo change result 0/-1
        post("/getCurrentUser", (req, res) -> {
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (!token.equals(req.queryParams("token")))
                        return -1;
                    return userDAO.getUser(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        //todo change result 0/-1
        post("/saveProfile", (req, res) -> {
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (!token.equals(req.queryParams("token")))
                        return -1;
                    return userDAO.update(new User(req.queryParams("login"), req.queryParams("password"),
                            req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone")));
                },
                JsonUtil.json());


        post("/getRide", (req, res) -> {
                    String token = tokenDAO.getToken(Integer.parseInt(req.queryParams("userId")));
                    if (!token.equals(req.queryParams("token")))
                        return -1;
                    return rideSuggestionDAO.getRide(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        //return sharedRideId or SuggestionId
        post("/joinRide", (req, res) -> {
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (!token.equals(req.queryParams("token")))
                        return -1;
                    return sharedRideDAO.joinRide(Integer.parseInt(req.queryParams("rideId")), userId, 1) ;
                },
                JsonUtil.json());

        post("/unjoinRide", (req, res) -> {
                    int userId = Integer.parseInt(req.queryParams("userId"));
                    String token = tokenDAO.getToken(userId);
                    if (!token.equals(req.queryParams("token")))
                        return -1;
                    return sharedRideDAO.delete(Integer.parseInt(req.queryParams("rideId")));
                },
                JsonUtil.json());

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(JsonUtil.toJson(new ResponseError(e)));
        });
    }
}
