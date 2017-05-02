package com.jayavijayjayavelu.news_gateway;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jayavijayjayavelu on 4/29/17.
 */

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private boolean running = true;
    public static String dataURL =
            "https://newsapi.org/v1/sources?language=en&country=us&apiKey=17756624c11144828e89e36b625bb2d0";
    private MainActivity mainActivity;
    private Context context;
    public static ArrayList<NewsSource> totalSourceList;

    public NewsService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        //sendMessage("Service Started");
        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        if(dataURL.contains("https://newsapi.org/v1/sources")) {
            Thread t1 = new Thread() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; running; i++) {
                        Log.d(TAG, "run: " + i);
                        try {
                            Uri dataUri = Uri.parse(dataURL);
                            String urlToUse = dataUri.toString();
                            try {
                                URL url = new URL(urlToUse);

                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                InputStream is = conn.getInputStream();
                                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line).append('\n');
                                }
                            } catch (Exception e) {

                            }
                            sendMessageA(sb.toString());
                            Thread.sleep(2000000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sendMessageA("Service Done Sending Broadcasts");

                    Log.d(TAG, "run: Ending loop");
                }
            };
            t1.start();
        }else if(dataURL.contains("https://newsapi.org/v1/articles")) {
            Thread t2 = new Thread() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; running; i++) {
                        Log.d(TAG, "run: " + i);
                        try {
                            Uri dataUri = Uri.parse(dataURL);
                            String urlToUse = dataUri.toString();
                            try {
                                URL url = new URL(urlToUse);

                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                InputStream is = conn.getInputStream();
                                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line).append('\n');
                                }
                            } catch (Exception e) {

                            }
                            sendMessageB(sb.toString());
                            Thread.sleep(2000000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    sendMessageB("Service Done Sending Broadcasts");

                    Log.d(TAG, "run: Ending loop");
                }
            };
            t2.start();
        }

        return Service.START_NOT_STICKY;
    }

    private void sendMessageA(String msg) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.NEWS_SOURCE);
        intent.putExtra(MainActivity.SERVICE_DATA, msg);
        sendBroadcast(intent);
    }
    private void sendMessageB(String msg) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.NEWS_ARTICLE);
        intent.putExtra(MainActivity.SERVICE_DATA, msg);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

}
