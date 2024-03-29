public class User {

	private String firstName;
    private String lastName;
	private String login;
    private String password;
    private String phone;
    private int id;
    private int chatId;
    private UserType type;
    private boolean enableNotifications;

    public User() {
    }

    public User(String login, String password, String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.phone = phone;
        this.type = UserType.UNDIFINED;
        this.enableNotifications = false;
    }
    public User(String login, String password, String firstName, String lastName, String phone, UserType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.phone = phone;
        this.type = type;
        this.enableNotifications = false;
    }
    public User(String login, String password, String firstName, String lastName, String phone, UserType type,
                boolean enableNotifications) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.phone = phone;
        this.type = type;
        this.enableNotifications = enableNotifications;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String email) {
        this.login = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}
