//import static JsonUtil.*;

import java.sql.Timestamp;

import static spark.Spark.*;


public class RidesharingAPI {

	public RidesharingAPI(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO,
                          final SharedRideDAO sharedRideDAO) {

      post("/createRide", (req, res) -> rideSuggestionDAO.createRideSuggestion(
                new RideSuggestion(Integer.parseInt(req.queryParams("userId")), req.queryParams("startPoint"),
                        req.queryParams("destinationPoint"), Timestamp.valueOf(req.queryParams("rideTime")),
                        Integer.parseInt(req.queryParams("timeLag")), Integer.parseInt(req.queryParams("capacity")),
                        Integer.parseInt(req.queryParams("capacity")))), JsonUtil.json());

      post("/getRidesList", (req, res) -> rideSuggestionDAO.getRides(Integer.parseInt(req.queryParams("userId"))),
              JsonUtil.json());

      post("/cancelRide", (req, res) -> rideSuggestionDAO.delete(Integer.parseInt(req.queryParams("rideId"))),
              JsonUtil.json());

      post("/register", (req, res) -> userDAO.createUser(new User(req.queryParams("login"), req.queryParams("password"),
                      req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone"))),
              JsonUtil.json());
//todo change result 0/-1
      post("/getCurrentUser", (req, res) -> userDAO.getUser(Integer.parseInt(req.queryParams("rideId"))),
              JsonUtil.json());
//todo change result 0/-1
      post("/saveProfile", (req, res) -> userDAO.update(new User(req.queryParams("login"), req.queryParams("password"),
                      req.queryParams("firstName"), req.queryParams("lastName"), req.queryParams("phone"))),
              JsonUtil.json());



        post("/getRide", (req, res) -> rideSuggestionDAO.getRide(Integer.parseInt(req.queryParams("rideId"))),
                JsonUtil.json());

//        post("/joinRide", (req, res) -> sharedRideDAO.createRideSuggestion(Integer.parseInt(req.queryParams("rideId"))),
//                JsonUtil.json());
//
//        post("/unjoinRide", (req, res) -> sharedRideDAO.createRideSuggestion(Integer.parseInt(req.queryParams("rideId"))),
//                JsonUtil.json());


//		get("/users/:login", (req, res) -> {
//			String login = req.params(":login");
//			User user = userDAO.getUser(login);
//			if (user != null) {
//				return user;
//			}
//			res.status(400);
//			return new ResponseError("No user with login '%s' found", login);
//		}, JsonUtil.json());

//        get("/users/:id", (req, res) -> {
//            Integer id = Integer.parseInt(req.params(":id"));
//            User user = userDAO.getUser(id);
//            if (user != null) {
//                return user;
//            }
//            res.status(400);
//            return new ResponseError("No user with login '%s' found", id.toString());
//        }, JsonUtil.json());

        post("/register", (req, res) -> userDAO.createUser(
                new User(req.queryParams("login"), req.queryParams("password"), req.queryParams("firstName"),
                        req.queryParams("lastName"), req.queryParams("phone"))
        ), JsonUtil.json());


		after((req, res) -> res.type("application/json"));

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(JsonUtil.toJson(new ResponseError(e)));
		});
	}
}
    /*
+   Json createRide(login, startPoint, destinationPoint, startTimeMin, startTimeMax, capacity)
+    Json getRidesList(id userId)
+   Json cancelRide(id rideId)
    Json acceptPassenger(rideId, passengerId) //maybe later
+   Json register(string login, string password, string firstName, string lastName)
+   Json getUser(id userId)
+    Json saveProfile(...)
    Json getRidesList(startPoint, destination, departureTimeMin, departureTimeMax)
    Json joinRide(rideId)
    Json unjoinRide(id rideId)
    Json subscribeToRide(id userId)
+    Json getRide(id rideId)
    Json joinRide(rideId)
    Json unjoinRide(id rideId)
    Json getUserSubscriptions(id userId)
    Json unsubscribe(id subscriptionId)
    Json login(string login, string password)
    */