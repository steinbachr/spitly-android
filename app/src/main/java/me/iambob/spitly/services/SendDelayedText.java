package me.iambob.spitly.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import me.iambob.spitly.models.Contact;
import me.iambob.spitly.utils.MessagingUtils;

public class SendDelayedText extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String contactName = intent.getStringExtra(Contact.CONTACT_NAME);
        String contactNumber = intent.getStringExtra(Contact.CONTACT_NUMBER);
        String contactMessage = intent.getStringExtra(Contact.CONTACT_MESSAGE);

        try {
            System.out.println("***text execution started***");
            boolean messageSent = MessagingUtils.sendMessage(contactNumber, contactMessage);
            if (messageSent) {
                MessagingUtils.createTextSentNotification(context, contactName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Hit exception running text sender");
            Toast.makeText(context, "Oops we hit a problem. Text failed to send.", Toast.LENGTH_LONG).show();
        }
    }
}
