package me.iambob.spitly.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.iambob.spitly.R;
import me.iambob.spitly.fragments.FragmentSendText;
import me.iambob.spitly.utils.ContactsUtils;
import me.iambob.spitly.utils.MessagingUtils;
import me.iambob.spitly.models.Contact;

import java.util.ArrayList;


public class SendTextActivity extends WaitForContactsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentSendText())
                    .commit();
        }

        /* load up the contact data into our database */
        ContactsUtils contactsUtils = new ContactsUtils(this);
        contactsUtils.getContacts();
    }

    public void scheduleMessage(View v) {
        String message = ((TextView)this.findViewById(R.id.message)).getText().toString();
        MessagingUtils.sendMessage("15182818509", message);
    }


    /**-- WaitForContactsActivity Overrides --**/

    public void onContactsLoaded(ArrayList<Contact> loadedContacts) {
    }
}
