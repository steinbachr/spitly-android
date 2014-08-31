package me.iambob.spitly.utils;

import android.content.ContentResolver;
import android.telephony.SmsManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.database.Cursor;
import android.net.Uri;

import android.content.Context;
import android.content.ContentValues;
import android.content.Intent;
import android.app.PendingIntent;

import me.iambob.spitly.activities.SendTextActivity;
import me.iambob.spitly.models.Contact;
import me.iambob.spitly.R;

import java.util.ArrayList;

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
     * @param contactName optional. If given, then we pass the contact's name as an extra in the intent
     */
    private static void showNotification(String notificationTitle, String notificationMessage, int notifId, Context context, String contactName) {
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, SendTextActivity.class);
        if (contactName != null) {
            resultIntent.putExtra(Contact.CONTACT_NAME, contactName);
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
            /* maximum number of characters for a single part message is 160 */
            if (message.length() >= 160) {
                ArrayList<String> messageParts = manager.divideMessage(message);
                manager.sendMultipartTextMessage(destinationAddr, null, messageParts, null, null);
            } else {
                /* Note: the last two params probably won't stay null */
                manager.sendTextMessage(destinationAddr, null, message, null, null);
            }

            return true;
        } catch (IllegalArgumentException exc) {
            return false;
        } catch (NullPointerException exc) {
            return false;
        }
    }

    /**
     * Create the notification to be displayed to the user when there text has actually been sent
     * @param context
     */
    public static void createTextSentNotification(Context context, String recipientName) {
        showNotification("Text Sent!", String.format("Your delayed text to %s has been sent.", recipientName), 2, context, null);
    }

    /**
     * Create the notification to be displayed to the user when they receive a text from a starred contact
     * @param context
     * @param senderName the name of the sender of the text which was received by the user
     */
    public static void createReceivedTextNotification(Context context, String senderName) {
        showNotification("Received Starred Contact Text!", "click to respond with a delayed text", 1, context, senderName);
    }

    /**
     * Mark the text sent by the given number and having the given body as read
     * code from answer at:
     * http://stackoverflow.com/questions/8637271/android-how-to-mark-sms-as-read-in-onreceive
     * @param context
     * @param number the number of the text sender
     * @param body the body of the text that was received
     */
    public static void markTextAsRead(Context context, String number, String body) {
        Uri uri = Uri.parse("content://sms/inbox");
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String smsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", true);
                        values.put("seen", true);
                        cr.update(uri, values, "_id=?", new String[]{smsMessageId});
                    }
                }
                cursor.moveToNext();
            }
        } catch (Exception exc) {
            System.out.println("wasn't able to mark the text as read :(");
        }
    }
}
