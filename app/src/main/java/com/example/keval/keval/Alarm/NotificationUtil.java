package com.example.keval.keval.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.keval.keval.Activity.AddRemainder;
import com.example.keval.keval.R;

import org.json.JSONObject;

public class NotificationUtil {

    public static void createNotification(Context context, JSONObject reminder, JSONObject mainJson) {
        // Create intent for notification onClick behaviour
        Intent viewIntent = new Intent(context, AddRemainder.class).putExtra("onlyShow", mainJson.toString());
//        viewIntent.putExtra("NOTIFICATION_ID", reminder.optInt("Id"));
        PendingIntent pending = PendingIntent.getActivity(context, reminder.optInt("Id"), viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create intent for notification snooze click behaviour
//        Intent snoozeIntent = new Intent(context, SnoozeActionReceiver.class);
//        snoozeIntent.putExtra("NOTIFICATION_ID", reminder.optInt("Id"));
//        PendingIntent pendingSnooze = PendingIntent.getBroadcast(context, reminder.optInt("Id"), snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        int imageResId = context.getResources().getIdentifier(reminder.getIcon(), "drawable", context.getPackageName());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setColor(Color.parseColor(reminder.getColour()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(reminder.optString("desription")))
                .setContentTitle(reminder.optString("title"))
                .setContentText(reminder.optString("description"))
                .setSubText(context.getResources().getString(R.string.app_name))
                .setTicker(reminder.optString("title"))
                .setContentIntent(pending)
                .setAutoCancel(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String soundUri = sharedPreferences.getString("NotificationSound", "content://settings/system/notification_sound");
        if (soundUri.length() != 0) {
            builder.setSound(Uri.parse(soundUri));
        }
        if (sharedPreferences.getBoolean("checkBoxLED", true)) {
            builder.setLights(Color.BLUE, 700, 1500);
        }
        if (sharedPreferences.getBoolean("checkBoxOngoing", false)) {
            builder.setOngoing(true);
        }
        if (sharedPreferences.getBoolean("checkBoxVibrate", true)) {
            long[] pattern = {0, 300, 0};
            builder.setVibrate(pattern);
        }
//        if (sharedPreferences.getBoolean("checkBoxMarkAsDone", false)) {
//            Intent intent = new Intent(context, DismissReceiver.class);
//            intent.putExtra("NOTIFICATION_ID", reminder.getId());
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.addAction(R.drawable.ic_done_white_24dp, context.getResources().getString(R.string.mark_as_done), pendingIntent);
//        }
//        if (sharedPreferences.getBoolean("checkBoxSnooze", false)) {
//            builder.addAction(R.drawable.ic_snooze_white_24dp, context.getResources().getString(R.string.snooze), pendingSnooze);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.optInt("Id"), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}