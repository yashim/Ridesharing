public class Main {
	public static void main(String[] args) {
		new RidesharingAPI(new UserDAO(), new RideSuggestionDAO(), new SharedRideDAO(), new TokenDAO(), new DeviceDAO());
    }
}
