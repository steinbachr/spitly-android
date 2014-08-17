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
    /**
     * show the user a notification
     * @param notificationTitle the title of the notification
     * @param notificationMessage the message body for the notification
     * @param notifId the notification id
     * @param context
     * @param contact optional. If given, then we pass the contact's name as an extra in the intent
     */
    private static void showNotification(String notificationTitle, String notificationMessage, int notifId, Context context, Contact contact) {
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        Intent resultIntent = new Intent(context, SendTextActivity.class);
        if (contact != null) {
            resultIntent.putExtra(Contact.CONTACT_NAME, contact.getName());
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(SendTextActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notifId, mBuilder.build());
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
        showNotification("Text Sent!", String.format("Your delayed text to %s has been sent.", recipient.getName()), 2, enclosing, null);
    }

    /**
     * Create the notification to be displayed to the user when they receive a text from a starred contact
     * @param context
     * @param sender the sender of the text which was received by the user
     */
    public static void createReceivedTextNotification(Context context, Contact sender) {
        showNotification("Received Starred Contact Text!", "click to respond with a delayed text", 1, context, sender);
    }
}
