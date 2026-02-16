package com.photofusion.holi.videomaker.photo.slideshow.util.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.photofusion.holi.videomaker.photo.slideshow.MainActivity;
import com.photofusion.holi.videomaker.photo.slideshow.R;

import java.util.Random;


public class AlarmReceiver extends BroadcastReceiver {

    // Java
    Resources res = MainActivity.activity.getResources();
    String[] myArray = res.getStringArray(R.array.daily_quotes);
    Random random = new Random();
    int randomIndex = random.nextInt(myArray.length);

    // Get the random quote
    String randomQuote = myArray[randomIndex];


    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the alarm trigger action
        showNotification(context, "Daily Quotes", randomQuote, R.mipmap.ic_launcher_foreground);


    }


    @SuppressLint("MissingPermission")
    private void showNotification(Context context, String title, String message, int icon) {
        createNotificationChannel(context);

//        Drawable drawable = MainActivity.activity.getResources().getDrawable(R.mipmap.ic_launcher_foreground);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra("NOTIFICATION_CLICKED", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
//                .setLargeIcon(drawableToBitmap(drawable))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Clear the notification when clicked

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

