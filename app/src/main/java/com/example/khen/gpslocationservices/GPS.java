package com.example.khen.gpslocationservices;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;



public class GPS extends AppCompatActivity {
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;
    public String notifs;

    EditText UsernameEt, PasswordEt;


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    textView.append("\n"+intent.getExtras().get("coordinates"));
                    Intent notification_intent = new Intent(context,GPS.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,0,notification_intent,0);
                    builder = new NotificationCompat.Builder(context);
                    notifs = intent.getStringExtra("coordinates");
                    builder.setSmallIcon(R.mipmap.coordinates)
                            .setAutoCancel(true)
                            .setContentTitle("Coordinates")
                            .setContentText(notifs)
                            .setContentIntent(pendingIntent);
                    notificationManager.notify(notification_id,builder.build());
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Intent i = new Intent(getApplicationContext(),GPS_Service.class);
        startService(i);

        UsernameEt = (EditText)findViewById(R.id.etUsername);
        PasswordEt = (EditText)findViewById(R.id.etPassword);


        textView = (TextView) findViewById(R.id.textView);
        context = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon,R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.notif_title, notifs);
        remoteViews.setProgressBar(R.id.progressBar,100,50,true);
        notification_id = (int) System.currentTimeMillis();
        Intent button_intent = new Intent("button_clicked");
        button_intent.putExtra("id",notification_id);

        Intent intents = new Intent(this,GPS.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(GPS.class);
        stackBuilder.addNextIntent(intents);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

    }
    public void OnLogin(View view){
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();
        String type ="login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,username,password);
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest
                .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            } else{
                runtime_permissions();
            }
        }
    }
}
