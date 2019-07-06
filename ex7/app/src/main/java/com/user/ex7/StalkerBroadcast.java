package com.user.ex7;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import androidx.core.app.NotificationCompat;

import static androidx.core.content.ContextCompat.getSystemService;


public class StalkerBroadcast extends BroadcastReceiver {
    private static final String PHONE_FIELD = "number_save";
    private static final String PREDEFINED_FIELD = "text_save";
    private static final String TAG = "My Stalker Broadcast";
    private static final String CHANNEL_ID = "STALKER";
    private static final String channel_name = "sms_channel";
    private static final String channel_description = "sending notifications on outgoing text";
    private static final String SENDING_MESSAGE = "sending message...";
    private static final int NOTIFICATION_ID = 1;
    private static final int SEND_ID = 12;
    private static final int DEV_ID = 13;
    private static final String SENT_TEXT = "message sent successfully!";
    private static final String DEV_TEXT = "message received successfully!";
    private static final String ACTION_SENT = "SENT";
    private static final String ACTION_DEL = "DELIVERED";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() ==  Intent.ACTION_NEW_OUTGOING_CALL)
        {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String number = prefs.getString(PHONE_FIELD, null);
            String message = prefs.getString(PREDEFINED_FIELD, null);
            if(number != null && message != null)
            {
                String callingTo = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Intent sent = new Intent("SENT");
                PendingIntent sentPending = PendingIntent.getBroadcast(context, SEND_ID, sent, PendingIntent.FLAG_UPDATE_CURRENT);
                Intent delivered = new Intent("DELIVERED");
                PendingIntent deliveredPending = PendingIntent.getBroadcast(context, DEV_ID, delivered, PendingIntent.FLAG_UPDATE_CURRENT);
                setNotification(context, SENDING_MESSAGE);
                BroadcastReceiver sentBC = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent1) {
                        setNotification(context, SENT_TEXT);
                        context.getApplicationContext().unregisterReceiver(this);
                    }
                };
                BroadcastReceiver devBC = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent1) {
                        setNotification(context, DEV_TEXT);
                        context.getApplicationContext().unregisterReceiver(this);
                    }
                };
                context.getApplicationContext().registerReceiver(sentBC, new IntentFilter(ACTION_SENT));
                context.getApplicationContext().registerReceiver(devBC, new IntentFilter(ACTION_DEL));
                SmsManager.getDefault().sendTextMessage(number, null, message + callingTo, sentPending, deliveredPending);
            }
        }
    }
    private void setNotification(Context context, String not) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            channel.setDescription(channel_description);
            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(not)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

}
