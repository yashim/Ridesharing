/**
 * Author: shim.
 * Creation date: 8/13/15.
 */
public class TelegramBotResponses {
    public final static String ABOUT = "I am RideSharing bot at Innopolis."
            + System.lineSeparator() + "I will help you find your way to Kazan or Innopolis. And remember — you are the best!"
            + System.lineSeparator() + "Version 0.99beta";
    public final static String START = "Welcome your greatness!"
            + System.lineSeparator() + " I am RideSharing bot at Innopolis. I can help you find car to Kazan or Innopolis."
            + System.lineSeparator() + " My lord, you are the best!";
    public final static String ERROR = "I'm sorry, my lord. I cannot understand your."+ System.lineSeparator() +
            " I am only a Ridesharing bot created by human, but you are divine creation.";
    public final static String JOIN_INFO = "Select the ride you want to join. In order to join ride please use /show. Then reply to the message which contains the ride you want to join with text /join.";
    public final static String JOIN_PROVIDE_USERNAME = "In order to join a ride please provide your username to Telegram. https://telegram.org/faq#q-what-are-usernames-how-do-i-get-one number.";
    public final static String CREATE_PROVIDE_USERNAME = "In order to create a ride please provide your username to Telegram: Settings -> Username. https://telegram.org/faq#q-what-are-usernames-how-do-i-get-one number.";
    public final static String CREATE_SPECIFY_PARAMETERS = "In order to create ride please specify Destination as first parameter and Departure time as second parameter. For example: /create Kazan 14:50";
    public final static String CREATE_WRONG_TIMEFORMAT = "Please specify second ride time parameter as hh:mm, for example 14:30";
    public final static String CREATE_WRONG_PARAMETERS = "In order to create ride please specify Destination as first parameter and Departure time as second parameter. For example: /create Kazan 14:50 or new Kazan 18:00";
    public final static String DELETE_INFO = "Select the ride you want to delete. In order to delete ride please use /show. Then reply to the message which contains the ride you want to delete with text /delete.";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";
//    public final static String JOIN_PROVIDE_USERNAME = "";





}
