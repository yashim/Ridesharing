//import static JsonUtil.*;

import java.sql.Timestamp;

import static spark.Spark.*;


public class RidesharingAPI {

	public RidesharingAPI(final UserDAO userDAO, final RideSuggestionDAO rideSuggestionDAO) {

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

        post("/create_ride", (req, res) -> rideSuggestionDAO.createRideSuggestion(
                new RideSuggestion(req.queryParams("login"), req.queryParams("startPoint"), req.queryParams("destinationPoint"),
                        Timestamp.valueOf(req.queryParams("startTimeMin")), Timestamp.valueOf(req.queryParams("startTimeMax")),
                        Integer.parseInt(req.queryParams("capacity")), Integer.parseInt(req.queryParams("capacity")))
        ), JsonUtil.json());

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