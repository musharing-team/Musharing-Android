package com.mine.musharing.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mine.musharing.R;
import com.mine.musharing.activities.MusicChatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

public class NotifyService extends Service {

    public NotifyService() {
    }

    TimerTask refreshNotifications = new TimerTask() {
        @Override
        public void run() {
            // TODO: get notifications
            Random random = new Random();
            showNotification("" + random.nextInt(), "" + random.nextInt(), "" + random.nextInt());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        showNotification("Test", "test", "test");

        Timer timer = new Timer();
        timer.schedule(refreshNotifications, 0, 2000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification(String title, String content, String detail) {

        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // 通知渠道的处理，android8.0+ 适用
        String channel_id = "musharing_notify_channel_01";
        CharSequence channel_name = "Musharing";
        String channel_description = "Notifications from musharing.";
        int channel_importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            channel_importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(channel_id, channel_name, channel_importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel.setDescription(channel_description);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNM.createNotificationChannel(mChannel);
        }

        // 点击通知时的 PendingIntent
        Intent intent = new Intent(this, MusicChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 构建通知
        Notification notification = new NotificationCompat.Builder(this, channel_id)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(detail))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // 发送通知
        mNM.notify(1, notification);
    }
}
