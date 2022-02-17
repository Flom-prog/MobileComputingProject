package com.flom.mobilecomputingproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.flom.mobilecomputingproject.database.DatabaseManager;

import java.io.IOException;

public class NotificationWorker extends Worker {

    private DatabaseManager myDB;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String[] array = getTags().toArray(new String[0]);
        displayNotification(array[1], getInputData().getString("IMAGE_URI"), getInputData().getInt("ID", 0), getInputData().getBoolean("SHOW_NOTIFICATION", true));
        return Result.success();
    }

    private void displayNotification(String title, String imageUri, int reminder_id, boolean show_notification) {
        myDB = new DatabaseManager(getApplicationContext());
        myDB.updateReminderSeen(reminder_id, "true");

        if (show_notification) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notification", "notification", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notification")
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setLargeIcon(drawableToBitmap(getApplicationContext().getDrawable(R.drawable.logo)))
                    .setLargeIcon(bitmap)
                    .setContentIntent(pendingIntent)
                    .setGroup("notification")
                    .setAutoCancel(true);

            manager.notify(1, builder.build());
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
