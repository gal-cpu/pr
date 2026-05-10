package com.example.pr.services;




import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

    public class FCMHelper {

        // 🔴 הכנס כאן את SERVER KEY של Firebase
        private static final String SERVER_KEY =
                "YOUR_FIREBASE_SERVER_KEY";

        private static final String FCM_URL =
                "https://fcm.googleapis.com/fcm/send";

        public static void sendPushNotification(
                String token,
                String title,
                String message
        ) {

            new Thread(() -> {

                try {

                    URL url = new URL(FCM_URL);

                    HttpURLConnection connection =
                            (HttpURLConnection) url.openConnection();

                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    connection.setRequestMethod("POST");

                    connection.setRequestProperty(
                            "Authorization",
                            "key=" + SERVER_KEY
                    );

                    connection.setRequestProperty(
                            "Content-Type",
                            "application/json"
                    );

                    // JSON של ההתראה
                    JSONObject json = new JSONObject();

                    json.put("to", token);

                    JSONObject notificationObj = new JSONObject();

                    notificationObj.put("title", title);
                    notificationObj.put("body", message);

                    json.put("notification", notificationObj);

                    OutputStream outputStream =
                            connection.getOutputStream();

                    outputStream.write(json.toString().getBytes());

                    outputStream.flush();
                    outputStream.close();

                    int responseCode = connection.getResponseCode();

                    Log.d("FCM", "Response Code : " + responseCode);

                    connection.disconnect();

                } catch (Exception e) {

                    Log.e("FCM", "Error : " + e.getMessage());
                }

            }).start();
        }
    }




