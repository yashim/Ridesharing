import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

	private Map<String, User> users = new HashMap<>();

	public List<User> getAllUsers() {
		return new ArrayList<>(users.values());
	}

	public User getUser(String id) {
		return users.get(id);
	}

	public User createUser(String name, String email) {
		failIfInvalid(name, email);
		User user = new User();
		users.put(user.getLogin(), user);
		return user;
	}

	public User updateUser(String id, String name, String email) {
		User user = users.get(id);
		if (user == null) {
			throw new IllegalArgumentException("No user with id '" + id + "' found");
		}
		failIfInvalid(name, email);
		user.setFirstName(name);
		user.setLogin(email);
		return user;
	}

	private void failIfInvalid(String name, String email) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'name' cannot be empty");
		}
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Parameter 'email' cannot be empty");
		}
	}
}
