import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * Author: shim.
 * Creation date: 8/13/15.
 */
public class TelegramBotResponses {
    private final static Logger logger = LogManager.getLogger(TelegramBotResponses.class);
    public final static String KAZAN = "%D0%9A%D0%B0%D0%B7%D0%B0%D0%BD%D1%8C";
    public final static String INNOPOLIS = "%D0%98%D0%BD%D0%BD%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%81";
    public final static String ABOUT = "I am your Innopolis RideSharing bot."
            + System.lineSeparator() + "I am here to help you to find your way to Kazan or Innopolis."
            + System.lineSeparator() + "Version 0.997beta"
            + System.lineSeparator() + "Yours Sincerely, RideSharing bot";
    public final static String START = "Welcome, Your Excellency!"
            + System.lineSeparator() + "I am your Innopolis RideSharing bot. I am here to help you to find your way to Kazan or Innopolis. You can control me by sending the following:"+ System.lineSeparator() +
            "/about - Information about me, your RideSharing bot"+ System.lineSeparator() +
            "/join - Let us join a ride! Please, reply to my humble message on ride options with Join."+ System.lineSeparator()+
            "/show - I am here to satisfy your thirst for adventures, let me show you the rides"+ System.lineSeparator() +
            "/create - You are the RideCreator, Father. Do it, create it. Amen."+ System.lineSeparator() +
            "/cancel - Your Excellency, you can do everything, even cancel your creation."
            + System.lineSeparator() + "Your Highness, you are the Best!"+ System.lineSeparator() +"May the Ride be with you.";
    public final static String ERROR = "I am sorry, Your Highness. I am too imperfect, I cannot understand you."+ System.lineSeparator() +
            "I am only a RideSharing bot created by a human, and you are the divine creation.";
    public final static String JOIN_INFO = "Please, kindly select the ride you wish to join by tapping Show. Then reply to the message with the ride you want to join by tapping Join. With your presence the ride will be superb.";
    public final static String JOIN_PROVIDE_USERNAME = "In order to give a touch of divinity to the ride, please provide your notable username to Telegram: Settings -> Username. https://telegram.org/faq#q-what-are-usernames-how-do-i-get-one.";
    public final static String CREATE_PROVIDE_USERNAME = "In order to create a magnificent ride, please provide your notable username to Telegram: Settings -> Username. https://telegram.org/faq#q-what-are-usernames-how-do-i-get-one.";
    public final static String CREATE_SPECIFY_PARAMETERS = "In order to create a triumphant ride please specify a Destination point as the first word and Departure time as the second word. Here is my humble examples: \"Kazan 14:50\" or \"Innopolis 20:40\", or \""+ KAZAN +" 19:00\", or \""+ INNOPOLIS +" 8:00\"";
    public final static String CREATE_WRONG_TIMEFORMAT = "Please specify the second word - starting time of your journey - as hh:mm, here is my modest example: 14:30";
    public final static String CREATE_WRONG_CITY = "Please specify the first word - Destination point of your journey - as Kazan or Innopolis, here is my modest examples: \"Kazan 14:50\" or \"Innopolis 20:40\", or \""+ KAZAN +" 19:00\", or \""+ INNOPOLIS +" 8:00\"";
    public final static String CREATE_WRONG_PARAMETERS = "In order to create a triumphant ride please specify a Destination point as the first word and Departure time as the second word. Here is my humble examples: \"Kazan 14:50\" or \"Innopolis 20:40\", or \""+ KAZAN +" 19:00\", or \""+ INNOPOLIS +" 8:00\"";
    public final static String DELETE_INFO = "Your Highness, please select the ride you want to cancel. To do this, please tap Show and then reply to the message with Cancel.";
    //todo change description
    public final static String NOTIFY_WRONG_PARAMETERS = "Your Highness, please type \"notify on\" or \"notify off\" in order to get notifications about new rides or not.";
    public final static String NOTIFY_ON = "Your Highness, from now you will be notified about new rides.";
    public final static String NOTIFY_OFF = "Your Highness, from now you will not get notifications about new rides.";



    private static final byte[] utf8BytesJoin = new byte[4];
    private static final byte[] utf8BytesCancel = new byte[4];
    private static final byte[] utf8BytesCreate = new byte[4];
    private static final byte[] utf8BytesShow = new byte[4];
    public static String createSymbol;
    public static String joinSymbol;
    public static String cancelSymbol;
    public static String showSymbol;
    static {
        utf8BytesJoin[0] = (byte)0xF0;
        utf8BytesJoin[1] = (byte)0x9F;
        utf8BytesJoin[2] = (byte)0x8E;
        utf8BytesJoin[3] = (byte)0x8E;
        utf8BytesCancel[0] = (byte)0xF0;
        utf8BytesCancel[1] = (byte)0x9F;
        utf8BytesCancel[2] = (byte)0x99;
        utf8BytesCancel[3] = (byte)0x8A;
        utf8BytesCreate[0] = (byte)0xF0;
        utf8BytesCreate[1] = (byte)0x9F;
        utf8BytesCreate[2] = (byte)0x9A;
        utf8BytesCreate[3] = (byte)0x80;
        utf8BytesShow[0] = (byte)0xF0;
        utf8BytesShow[1] = (byte)0x9F;
        utf8BytesShow[2] = (byte)0x94;
        utf8BytesShow[3] = (byte)0xAE;
        try {
            joinSymbol = new String(utf8BytesJoin, "UTF-8");
            cancelSymbol = new String(utf8BytesCancel, "UTF-8");
            showSymbol  = new String(utf8BytesShow, "UTF-8");
            createSymbol = new String(utf8BytesCreate, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    public final static String KEYBORD_CREATE_ICON = "%F0%9F%9A%80 Create";
    public final static String KEYBORD_SHOW_ICON = "%F0%9F%94%AE Show";
    public final static String KEYBORD_JOIN_ICON = "%F0%9F%8E%8E Join";
    public final static String KEYBORD_CANCEL_ICON = "%F0%9F%99%8A Cancel";

    public final static String AVAILABLE_RIDES_HEADER = "%F0%9F%9A%97 AVAILABLE RIDES %F0%9F%9A%97";
    public final static String DRIVER_RIDES_HEADER = "%F0%9F%9A%98 YOU ARE A DRIVER %F0%9F%9A%98";
    public final static String PASSENGER_RIDES_HEADER = "%F0%9F%92%BA YOU ARE A PASSENGER %F0%9F%92%BA";


    public final static String NO_AVAILABLE_RIDES_HEADER = "%F0%9F%9A%97 NO AVAILABLE RIDES %F0%9F%9A%97";
    public final static String NO_DRIVER_RIDES_HEADER = "%F0%9F%9A%98 NO RIDES WHERE YOU ARE A DRIVER %F0%9F%9A%98";
    public final static String NO_PASSENGER_RIDES_HEADER = "%F0%9F%92%BA NO RIDES WHERE YOU ARE A PASSENGER %F0%9F%92%BA";

    //%F0%9F%9A%97 redcar
    //%F0%9F%92%BA
}
