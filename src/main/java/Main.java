public class Main {
	public static void main(String[] args) {
		new UserController(new UserDAO(), new RideSuggestionDAO());

	}
}
