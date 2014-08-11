package me.iambob.spitly.utils;

import android.telephony.SmsManager;

/**
 * provides methods for sending / receiving messages
 */
public class MessagingUtils {
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
}
