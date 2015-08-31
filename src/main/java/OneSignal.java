import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: shim.
 * Creation date: 8/26/15.
 */
public class OneSignal {
    private final static String registerUrl = "https://onesignal.com/api/v1/players";
    private final static String pushUrl = "https://onesignal.com/api/v1/notifications";
    private final static String appId = "32fed59e-3b83-11e5-b5c8-9f93493279d9";
    public static void registerDevice(String identifier, int deviceType) {
        String message = "{\"app_id\" : \"" + appId + "\", \"identifier\":\"" + identifier + "\", \"device_type\":" + deviceType + "}";
        try {
            URL u = new URL(registerUrl);
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", ""+message.length());
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.writeBytes (message);
            wr.flush ();
            wr.close ();
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void sendPush(String token, String message){
        PushMessage pushMessage = new PushMessage();
        pushMessage.setIos(true);
        Map<String, String> data = new HashMap<>();
        data.put("en", message);
        pushMessage.setContents(data);
        pushMessage.setIncludeIosTokens(new String[]{token});
        String pushMessageString = pushMessage.build();
        System.out.println(pushMessageString);
        try {
            URL u = new URL(pushUrl);
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", ""+pushMessageString.length());
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.writeBytes (pushMessageString);
            wr.flush ();
            wr.close ();
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}


class PushMessage{
    @SerializedName("app_id")
    private final String appId = "32fed59e-3b83-11e5-b5c8-9f93493279d9";

    @SerializedName("contents")
    private Map<String,String> contents;

    @SerializedName("isIos")
    private boolean isIos;

    @SerializedName("include_ios_tokens")
    private String[] includeIosTokens;

    @SerializedName("isAndroid")
    private boolean isAndroid;

    @SerializedName("include_android_reg_ids")
    private String[] includeAndroidRegIds;

    @SerializedName("isWP")
    private boolean isWP;

    @SerializedName("include_wp_uris")
    private String[] includeWpUris;

    public PushMessage(){
    }

    public PushMessage(Map<String, String> contents, boolean isIos, String[] includeIosTokens, boolean isAndroid, String[] includeAndroidRegIds, boolean isWP, String[] includeWpUris) {
        this.contents = contents;
        this.isIos = isIos;
        this.includeIosTokens = includeIosTokens;
        this.isAndroid = isAndroid;
        this.includeAndroidRegIds = includeAndroidRegIds;
        this.isWP = isWP;
        this.includeWpUris = includeWpUris;
    }

    public Map<String, String> getContents() {
        return contents;
    }

    public void setContents(Map<String, String> contents) {
        this.contents = contents;
    }

    public boolean isIos() {
        return isIos;
    }

    public void setIos(boolean isIos) {
        this.isIos = isIos;
    }

    public String[] getIncludeIosTokens() {
        return includeIosTokens;
    }

    public void setIncludeIosTokens(String[] includeIosTokens) {
        this.includeIosTokens = includeIosTokens;
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setAndroid(boolean isAndroid) {
        this.isAndroid = isAndroid;
    }

    public String[] getIncludeAndroidRegIds() {
        return includeAndroidRegIds;
    }

    public void setIncludeAndroidRegIds(String[] includeAndroidRegIds) {
        this.includeAndroidRegIds = includeAndroidRegIds;
    }

    public boolean isWP() {
        return isWP;
    }

    public void setWP(boolean isWP) {
        this.isWP = isWP;
    }

    public String[] getIncludeWpUris() {
        return includeWpUris;
    }

    public void setIncludeWpUris(String[] includeWpUris) {
        this.includeWpUris = includeWpUris;
    }

    public String build(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}


//curl --include \
//        --request POST \
//        --header "Content-Type: application/json" \
//        --data-binary '{"app_id" : "32fed59e-3b83-11e5-b5c8-9f93493279d9",
//        "identifier":"ce777617da7f548fe7a9ab6febb56cf39fba6d382000c0395666288d961ee566",
//        "device_type":0}' \
//        https://onesignal.com/api/v1/players

