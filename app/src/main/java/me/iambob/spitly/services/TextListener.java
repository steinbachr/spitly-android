package me.iambob.spitly.services;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.telephony.gsm.SmsMessage;
import android.os.Bundle;

import java.util.HashMap;

import me.iambob.spitly.database.Database;
import me.iambob.spitly.utils.MessagingUtils;
import me.iambob.spitly.models.Contact;

public class TextListener extends BroadcastReceiver {

    /**-- Helpers --**/
    private void launchSpitly(Context context) {
        Intent i = new Intent();
        i.setClassName("me.iambob.spitly", "me.iambob.spitly.SendTextActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
//            Bundle bundle = intent.getExtras();
//
//            if (bundle != null){
//                //---retrieve the SMS message received---
//                try{
//                    Object[] pdus = (Object[]) bundle.get("pdus");
//                    for (int i = 0 ; i < pdus.length ; i++) {
//                        SmsMessage message = SmsMessage.createFromPdu((byte[])pdus[i]);
//                        String msg_from = message.getOriginatingAddress();
//                        /* TODO: check if the msg_from is from a starred contact and - if so - show notifation */
//                    }
//
//                    Database db = new Database(context);
//                    Contact receivedFrom = db.getContactByName("Me");
//                    MessagingUtils.createReceivedTextNotification(context, receivedFrom);
//                }catch(Exception e){
//                }
//            }
//        }
    }
}