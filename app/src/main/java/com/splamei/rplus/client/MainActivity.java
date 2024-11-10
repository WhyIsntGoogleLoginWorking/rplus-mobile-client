package com.splamei.rplus.client;

import android.Manifest;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.content.Intent;
import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String myVerCode = "1000";
    public static String urlToLoad = "veemo.uk/r-plus";

    WebView webView;

    WebView loginView;
    WebViewClient webViewClient;
    WebViewClient loginClient;

    boolean hasShownAuth = false;

    public static final String UPDATE_CHANNEL_ID = "update_channel";
    public static final String NOTICES_CHANNEL_ID = "notices_channel";
    public static final String CHANNEL_ID = "testing_channel";
    public static final String MISC_CHANNEL_ID = "misc_channel";

    RequestQueue ExampleRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createChannel(this, UPDATE_CHANNEL_ID, "Update", "Notifications for updates to the client", NotificationManager.IMPORTANCE_HIGH);
        createChannel(this, NOTICES_CHANNEL_ID, "Notices", "Notices for the client", NotificationManager.IMPORTANCE_HIGH);
        createChannel(this, CHANNEL_ID, "Dev Testing Channel", "The notification channel for testing dev stuff", NotificationManager.IMPORTANCE_DEFAULT);
        createChannel(this, MISC_CHANNEL_ID, "Misc", "General Notifications for the client", NotificationManager.IMPORTANCE_DEFAULT);

        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, "more")
                .setShortLabel("About")
                .setLongLabel("About the client")
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon))
                .setRank(0)
                .setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.veemo.uk/r-plus-splamei-client/")))
                .build();

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut);

        ExampleRequestQueue = Volley.newRequestQueue(MainActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1008);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int UI_OPTIONS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);
        }

        webView = findViewById(R.id.mainWeb);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);


        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccess(true);
        //webView.getSettings().setSupportMultipleWindows(false);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setInitialScale(1);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) RPlusSplameiClient/130.0.6723.102 Mobile Safari/537.36");


        loginView = findViewById(R.id.loginWeb);
        loginView.setVisibility(View.GONE);
        loginView.getSettings().setJavaScriptEnabled(true);
        loginView.getSettings().setAllowContentAccess(true);
        loginView.getSettings().setUseWideViewPort(true);
        loginView.getSettings().setLoadsImagesAutomatically(true);


        loginView.getSettings().setLoadWithOverviewMode(true);
        loginView.getSettings().setDomStorageEnabled(true);
        loginView.setHorizontalScrollBarEnabled(false);
        loginView.getSettings().setDatabaseEnabled(true);
        loginView.getSettings().setBuiltInZoomControls(true);
        loginView.getSettings().setDisplayZoomControls(false);
        loginView.getSettings().setAllowFileAccess(true);
        loginView.setScrollbarFadingEnabled(false);
        loginView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        loginView.setInitialScale(1);
        loginView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36");

        webViewClient = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if((url.contains("rhythm-plus.com") && !url.contains("auth.rhythm-plus.com")) || url.contains(urlToLoad)){
                    // load my page
                    return false;
                }
                else if (url.contains("auth.rhythm-plus.com")){
                    hasShownAuth = false;

                    webView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                    loginView.setWebViewClient(loginClient);

                    loginView.loadUrl(url);

                    loginView.clearHistory();

                    Toast.makeText(MainActivity.this, "Please wait while the sign in page loads", Toast.LENGTH_SHORT).show();

                    return true;
                }

                // Create an Intent to open the link in the default browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // Set the flag to open the link in a new window or tab
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                ImageView imageView = findViewById(R.id.splashImg);
                imageView.setVisibility(View.INVISIBLE);

                ImageView backImg = findViewById(R.id.backImg);
                backImg.setVisibility(View.INVISIBLE);

                webView.setVisibility(View.VISIBLE);
            }
        };

        loginClient = new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                if(url.contains("auth.rhythm-plus.com/__/auth/handler?state=")){
                        webView.setVisibility(View.VISIBLE);
                        loginView.setVisibility(View.GONE);

                            Toast.makeText(MainActivity.this, "Welcome to Rhythm Plus!", Toast.LENGTH_SHORT).show();


                        loginView.loadUrl("about:blank");
                }
            }
        };

        webView.setWebViewClient(webViewClient);
        webView.loadUrl("https://" + urlToLoad); //https://www.veemo.uk/r-plus

        String url = "https://www.veemo.uk/net/r-plus/mobile/ver";
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (fileExists(MainActivity.this, "checkCode.dat")){
                    if (!readFile(MainActivity.this, "checkCode.dat").strip().equals(response)){
                        saveToFile(MainActivity.this, "checkCode.dat", response);
                        newUpdate(MainActivity.this, response.strip());
                    }
                }
                else{
                    saveToFile(MainActivity.this, "checkCode.dat", response);
                    newUpdate(MainActivity.this, response.strip());
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ExampleRequestQueue.add(ExampleStringRequest);

        String urlNotices = "https://www.veemo.uk/net/r-plus/mobile/notices";
        StringRequest NoticesStringRequest = new StringRequest(Request.Method.GET, urlNotices, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String regex = "[;]";
                    String[] splitNotices;

                    splitNotices = response.split(regex);

                    String seenNotices = readFile(MainActivity.this, "seenNotices.dat").strip();

                    if (!seenNotices.contains(splitNotices[3]) && !splitNotices[0].equals("NONE")) {
                        Toast.makeText(MainActivity.this, "New Notice! It's been sent in a notification (if enabled)", Toast.LENGTH_SHORT).show();
                        saveToFile(MainActivity.this, "seenNotices.dat", splitNotices[3]);
                        if (!Objects.equals(splitNotices[2], "NONE")) {
                            sendNotificationWithURL(MainActivity.this, NOTICES_CHANNEL_ID, splitNotices[0], splitNotices[1], NotificationCompat.PRIORITY_DEFAULT, splitNotices[2], "More Info");
                        } else {
                            sendNotifcation(MainActivity.this, NOTICES_CHANNEL_ID, splitNotices[0], splitNotices[1], NotificationCompat.PRIORITY_DEFAULT);
                        }
                    }
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error getting notices!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            ExampleRequestQueue.add(NoticesStringRequest);
        }
        else{
            Toast.makeText(this, "To see notices and updates, please enable notifications", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.getVisibility() == View.VISIBLE){
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                        return true;
                    }
                    else if (loginView.getVisibility() == View.VISIBLE){
                        if (loginView.canGoBack()) {
                            loginView.goBack();
                        }
                        else{
                            webView.setVisibility(View.VISIBLE);
                            loginView.setVisibility(View.GONE);

                            loginView.loadUrl("about:blank");
                        }
                        return true;
                    }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public static void createChannel(Context context, final String ID, String title, String description, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.getNotificationChannel(ID) == null){
                NotificationChannel channel = new NotificationChannel(ID, title, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.MAGENTA);
                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendNotifcation(Context context, final String ID, String title, String message, int importance){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (notificationManagerCompat.areNotificationsEnabled()){
            notificationManagerCompat.notify(1455, builder.build());
        }
    }

    public static void sendNotificationWithURL(Context context, final String ID, String title, String message, int importance, String url, String buttonText) {
        // Create an Intent to open the URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance)
                .setContentIntent(pendingIntent) // Set the pending intent for the notification
                .addAction(R.drawable.ic_stat_name, buttonText, pendingIntent); // Add the "Update" action

        // Create a NotificationManagerCompat
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        // Check if notifications are enabled
        if (notificationManagerCompat.areNotificationsEnabled()) {
            notificationManagerCompat.notify(1455, builder.build());
        }
    }

    public static String readFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    public static void saveToFile(Context context, String fileName, String content) {
        try (FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            outputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean fileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public static void newUpdate(Context context, String responce){
        if (!myVerCode.contains(responce)) {
            sendNotificationWithURL(context, UPDATE_CHANNEL_ID, "New Update", "The is a new update to the client. Tap or press the button to update. You won't be alerted about this update again.", NotificationCompat.PRIORITY_DEFAULT, "https://www.veemo.uk/r-plus-download", "Update");
            Toast.makeText(context, "Theres a new update!", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Check the GitHub Repo to update the client", Toast.LENGTH_LONG).show();
        }
    }
}
