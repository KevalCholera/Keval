package com.example.keval.keval.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        JSONObject reminderList, mainJson;
        try {
            reminderList = new JSONObject(intent.getStringExtra("NOTIFICATION_ID"));
            mainJson = new JSONObject(intent.getStringExtra("mainJson"));
            NotificationUtil.createNotification(context, reminderList, mainJson);

            // Update lists in tab fragments
            updateLists(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateLists(Context context) {
        Intent intent = new Intent("BROADCAST_REFRESH");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
