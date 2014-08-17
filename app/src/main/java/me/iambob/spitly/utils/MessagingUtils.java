package me.iambob.spitly.utils;

import android.telephony.SmsManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;

import me.iambob.spitly.activities.SendTextActivity;
import me.iambob.spitly.models.Contact;
import me.iambob.spitly.R;


/**
 * provides methods for sending / receiving messages
 */
public class MessagingUtils {
    /**-- Helpers --**/
    private static void showNotification(String notificationTitle, String notificationMessage, Context context) {
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }


    /**
     * Send a given message to the specified address
     * @param destinationAddr - the destination of the message to be sent
     * @param message - the message to be sent using the SmsManager
     * @return true if no errors were thrown
     */
    public static boolean sendMessage(String destinationAddr, String message) {
        SmsManager manager = SmsManager.getDefault();

        try {
            /* Note: the last two params probably won't stay null */
            manager.sendTextMessage(destinationAddr, null, message, null, null);
            return true;
        } catch (IllegalArgumentException exc) {
            return false;
        }
    }

    /**
     * Create the notification to be displayed to the user when there text has actually been sent
     * @param enclosing the activity to be used for context
     */
    public static void createTextSentNotification(Activity enclosing, Contact recipient) {
        Notification.Builder mBuilder = new Notification.Builder(enclosing)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Text sent!")
                        .setContentText(String.format("Your delayed text to %s has been sent.", recipient.getName()));
        Intent resultIntent = new Intent(enclosing, SendTextActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(enclosing);

        stackBuilder.addParentStack(SendTextActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)enclosing.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        int notifId = Integer.parseInt(enclosing.getResources().getString(R.string.id_send_text_notif));
        mNotificationManager.notify(notifId, mBuilder.build());
    }

    public static void createReceivedTextNotification(Context context, Contact sender) {
        showNotification("Received Starred Contact Text!", "click to respond with a delayed text", context);
    }
}
