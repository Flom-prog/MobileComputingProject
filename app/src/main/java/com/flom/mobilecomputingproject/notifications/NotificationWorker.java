package com.flom.mobilecomputingproject.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.flom.mobilecomputingproject.LoginActivity;
import com.flom.mobilecomputingproject.R;
import com.flom.mobilecomputingproject.database.DatabaseManager;

import java.io.IOException;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] array = getTags().toArray(new String[0]);
        displayNotification(array[0], getInputData().getString("IMAGE_URI"), getInputData().getInt("ID", 0), getInputData().getBoolean("SHOW_NOTIFICATION", true));
        return Result.success();
    }

    private void displayNotification(String title, String imageUri, int reminder_id, boolean show_notification) {
        DatabaseManager myDB = new DatabaseManager(getApplicationContext());
        myDB.updateReminderSeen(reminder_id, "true");

        if (show_notification) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notification", "notification", NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(imageUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                builder = new NotificationCompat.Builder(getApplicationContext(), "notification")
                        .setContentTitle(title)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(bitmap)
                        .setContentIntent(pendingIntent)
                        .setGroup("notification")
                        .setLights(getApplicationContext().getResources().getColor(R.color.red), 300, 1000)
                        .setVibrate(new long[]{0, 100, 100, 100})
                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification))
                        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title))
                        .setAutoCancel(true);


            manager.notify(reminder_id, builder.build());
        }
    }
}
