//import static JsonUtil.*;

import java.sql.Timestamp;

import static spark.Spark.*;


public class UserController {

	public UserController(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO) {

		get("/users", (req, res) -> userDAO.getUser("shim4ik.ilya@gmail.com"), JsonUtil.json());

//example: http://localhost:4567/users/shim4ik.ilya@gmail.com
		get("/users/:login", (req, res) -> {
			String login = req.params(":login");
			User user = userDAO.getUser(login);
			if (user != null) {
				return user;
			}
			res.status(400);
			return new ResponseError("No user with login '%s' found", login);
		}, JsonUtil.json());

//example: http://localhost:4567/create_ride/shim4ik.ilya@gmail.com/Kazan/Innopolis/2015-07-25-12-12-00/2015-07-25-12-42-00/3
        get("/create_ride/:login/:startPoint/:destinationPoint/:startTimeMin/:startTimeMax/:capacity", (req, res) -> {
            String login = req.params(":login");
            String startPoint = req.params(":startPoint");
            String destinationPoint = req.params(":destinationPoint");
            String startTimeMin = req.params(":startTimeMin");
            String startTimeMax = req.params(":startTimeMax");
            String capacity = req.params(":capacity");
            RideSuggestion rideSuggestion = new RideSuggestion();
            rideSuggestion.setUserLogin(login);
            rideSuggestion.setStartPoint(startPoint);
            rideSuggestion.setDestinationPoint(destinationPoint);
            rideSuggestion.setStartTimeMin(Timestamp.valueOf(startTimeMin));
            rideSuggestion.setStartTimeMax(Timestamp.valueOf(startTimeMax));
            rideSuggestion.setCapacity(Integer.getInteger(capacity));
            rideSuggestionDAO.createRideSuggestion(rideSuggestion);
            return "OK";
        }, JsonUtil.json());
//
//		post("/users", (req, res) -> userService.createUser(
//				req.queryParams("name"),
//				req.queryParams("email")
//		), JsonUtil.json());
//
//		put("/users/:id", (req, res) -> userService.updateUser(
//				req.params(":id"),
//				req.queryParams("name"),
//				req.queryParams("email")
//		), JsonUtil.json());

		after((req, res) -> res.type("application/json"));

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(JsonUtil.toJson(new ResponseError(e)));
		});
	}
}
    /*
    Json createRide(login, startPoint, destinationPoint, startTimeMin, startTimeMax, capacity)
    Json getRidesList(id userId)
    Json cancelRide(id rideId)
    Json acceptPassenger(rideId, passengerId)
    Json register(string login, string password, string firstName, string lastName)
    Json getUser(id userId)
    Json saveProfile(...)
    Json getRidesList(startPoint, destination, departureTimeMin, departureTimeMax)
    Json joinRide(rideId)
    Json unjoinRide(id rideId)
    Json subscribeToRide(id userId)
    Json getRide(id rideId)
    Json joinRide(rideId)
    Json unjoinRide(id rideId)
    Json register(string login, string password, string firstName, string lastName)
    Json getUserSubscriptions(id userId)
    Json unsubscribe(id subscriptionId)
    Json login(string login, string password)
    */