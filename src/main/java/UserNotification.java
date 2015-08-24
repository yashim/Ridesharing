import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegrambotapi.types.ReplyKeyboardMarkup;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author: shim.
 * Creation date: 8/24/15.
 */
public class UserNotification implements Runnable {
    public static CopyOnWriteArrayList<Subscription> subscriptionsList = new CopyOnWriteArrayList<>();

    protected Connection connection;
    protected PreparedStatement preparedStatement;

    private final Logger logger = LogManager.getLogger(UserNotification.class);

    static{
        initUserList();
    }
    private RideSuggestion rideSuggestion;

    public UserNotification(RideSuggestion rideSuggestion) {
        this.rideSuggestion = rideSuggestion;
    }

    public void sendNotifications(RideSuggestion rideSuggestion){
        for(Subscription subscription : subscriptionsList){
//            sendPost(user.getChatId(), "Testing broadcasting", getReplyMarkup());
            System.out.println(subscription.getChatId() +" there is new ride to " + rideSuggestion.getDestinationPoint()
                    + " at " + new SimpleDateFormat("EEEE, dd MMMM HH:mm").format(rideSuggestion.getRideTime()));

        }
    }

    private static void initUserList(){
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
        subscriptionsList = subscriptionDAO.getAllSubscriptions();
    }

    private static boolean sendPost(int chatId, String message, String replyMarkup){
        String url = "https://api.telegram.org/bot86148492:AAGLv840yestS5KiGODS-K0SZ2OWyp8IJ3c/sendMessage";
        URL obj;
        try {
            obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String urlParameters = "chat_id=" + chatId + "&text=" + message+"&reply_markup="+replyMarkup;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            if(responseCode!=200)
                return false;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String getReplyMarkup(){
        ReplyKeyboardMarkup.Builder builder = new ReplyKeyboardMarkup.Builder();
        builder.row(TelegramBotResponses.KEYBORD_JOIN_ICON, TelegramBotResponses.KEYBORD_SHOW_ICON);//create getrideslist delete join
        builder.row(TelegramBotResponses.KEYBORD_CREATE_ICON, TelegramBotResponses.KEYBORD_CANCEL_ICON);//, "New Ride");//, "Join", "Unjoin", "Delete Ride");
        builder.setResizeKeyboard();
        return builder.build().serialize();
    }

    @Override
    public void run() {
        sendNotifications(rideSuggestion);
    }
}
