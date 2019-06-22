package com.example.myspecialstalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;


public class StalkerBroadcast extends BroadcastReceiver {
    private static final String PHONE_FIELD = "number_save";
    private static final String PREDEFINED_FIELD = "text_save";
    private static final String TAG = "My Stalker Broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() ==  Intent.ACTION_NEW_OUTGOING_CALL)
        {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String number = prefs.getString(PHONE_FIELD, null);
            String message = prefs.getString(PREDEFINED_FIELD, null);
            if(number != null && message != null)
            {
                String callingTo = intent.getStringExtra(intent.EXTRA_PHONE_NUMBER);
                SmsManager.getDefault().sendTextMessage(number, null, message + callingTo, null, null);

            }
        }
    }

}
