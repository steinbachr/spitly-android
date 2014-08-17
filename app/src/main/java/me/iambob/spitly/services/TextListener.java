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

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();

            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Database db = new Database(context);
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (int i = 0 ; i < pdus.length ; i++) {
                        SmsMessage message = SmsMessage.createFromPdu((byte[])pdus[i]);
                        String msgFrom = message.getOriginatingAddress();
                        Contact receivedFrom = db.getContactByName(msgFrom);
                        if (receivedFrom != null && receivedFrom.isStarred()) {
                            MessagingUtils.createReceivedTextNotification(context, receivedFrom);
                        }
                    }
                }catch(Exception e){
                }
            }
        }
    }
}