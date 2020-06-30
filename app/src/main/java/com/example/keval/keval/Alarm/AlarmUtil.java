package com.example.keval.keval.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONObject;

import java.util.Calendar;

public class AlarmUtil {

    public static void setAlarm(Context context, Intent intent, int notificationId, JSONObject reminderObj, JSONObject mainJson, Calendar calendar) {
        intent.putExtra("NOTIFICATION_ID", reminderObj.toString());
        intent.putExtra("mainJson", mainJson.toString());
//        intent.putExtra
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

//    Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//    AlarmUtil.setAlarm(getApplicationContext(), alarmIntent, reminder.getId(), mCalendar);
}
