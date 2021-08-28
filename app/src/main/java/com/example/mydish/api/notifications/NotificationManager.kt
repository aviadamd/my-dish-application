package com.example.mydish.api.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mydish.R
import com.example.mydish.utils.Constants
import com.example.mydish.utils.Tags.NOTIFICATIONS
import com.example.mydish.view.activities.MainActivity

/**
 * Create a new package as "notification" and a class as NotifyWorker as below.
 * Extent the Worker class with required params.
 */
class NotificationManager(
    private val context: Context,
    workerParams : WorkerParameters
) : Worker(context, workerParams) {

    /**
     * Override the doWork function.
     * This function will be executed when the work scheduler is triggered.
     */
    override fun doWork(): Result {
        //Call the trigger the notification when doWork is called
        sendNotification()
        return success()
    }

    /** function to send the notification **/
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification() {
        if(!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Log.i(NOTIFICATIONS,"notifications are disabled")
            return
        }

        // In this case the notification id is 0.
        // If you are dealing with dynamic functionality then you can have it as unique for every notification.
        val notificationId = 0
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //Pass the notification id as intent extra to handle the code when user is
        //navigate in the app with notification.
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)

        /** Notify the events that happen. this is how you can tell the user that
         * something has happened in the background**/
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Generate bit map from vector icon using extension function that i created
        //Notification logo
        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)

        //Create the style of the Notification.
        //You can create the style as you want here we will create a notification using BigPicture.
        //For Example InboxStyle() which is used for simple Text message.
        //Null passed to avoid the duplication of image when the notification is en-large from notification tray.
        val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)

        /**
         * By giving a PendingIntent to another application, in this case ListenableWorker
         * That will wait until the notification processes will be ready to execute
         */
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0,intent,0)

        /*** Set all notification property */
        val notification = buildNotification(bitmap, pendingIntent, bigPicStyle)
        //Set the priority to the notification
        notification.priority = NotificationCompat.PRIORITY_MAX

        //Set channel ID for notification if you are using the API level 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Notification channel id for give that processes the id off the notification
            notification.setChannelId(Constants.NOTIFICATION_CHANNEL)

            // Setup the Ringtone for Notification.
            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL,
                Constants.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)

            notificationManager.createNotificationChannel(channel)
        }


        //Notify the user with Notification id and Notification ->
        // builder using the NotificationManager instance that we have created.
        notificationManager.notify(notificationId, notification.build())
    }

    /*** A function that will convert the vector image to bitmap as below.*/
    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        // Get the Drawable Vector Image
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) ?: return null

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun buildNotification(
        bitmap: Bitmap?, pendingIntent: PendingIntent, bigPicStyle: NotificationCompat.BigPictureStyle): NotificationCompat.Builder {

        return NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL)
            // Set the Notification Title
            .setContentTitle("Check out new dish recipes")
            // Set the Notification SubTitle
            .setContentText("Tap for more details")
            // Set the small icon also you can say as notification icon that we have generated.
            .setSmallIcon(R.drawable.ic_stat_notification)
            // Set the Large icon
            .setLargeIcon(bitmap)
            // Set the default notification options that will be used.
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            // Supply a PendingIntent to send when the notification is clicked.
            .setContentIntent(pendingIntent)
            // Add a rich notification style to be applied at build time.
            .setStyle(bigPicStyle)
            // Setting this flag will make it so the notification is automatically canceled when the user clicks it in the panel.
            .setAutoCancel(true)
    }
}