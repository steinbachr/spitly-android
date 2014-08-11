package me.iambob.spitly.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import me.iambob.spitly.R;
import me.iambob.spitly.fragments.FragmentSendText;
import me.iambob.spitly.utils.ContactsUtils;
import me.iambob.spitly.utils.MessagingUtils;
import me.iambob.spitly.models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class SendTextActivity extends WaitForContactsActivity {
    Contact selectedContact;

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
        MessagingUtils.sendMessage(selectedContact.getNumber(), message);
    }


    /**-- WaitForContactsActivity Overrides --**/

    public void onContactsLoaded(ArrayList<Contact> loadedContacts) {
        Collections.sort(loadedContacts, new ContactComparator());

        Spinner contactsSpinner = (Spinner)findViewById(R.id.contacts_spinner);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, loadedContacts);
        contactsSpinner.setAdapter(adapter);
        contactsSpinner.setOnItemSelectedListener(new ContactSelectedListener());

        try {
            selectedContact = loadedContacts.get(0);
        } catch (IndexOutOfBoundsException exc) {
            //no contacts loaded for some reason..what a loser
        }
    }

    /**-- Comparator for Comparing Contacts --**/
    class ContactComparator implements Comparator<Contact> {
        /**-- Comparator Overrides --**/
        @Override
        public int compare(Contact o1, Contact o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    /**-- Contact Selected Listener --**/
    class ContactSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            selectedContact = (Contact)parent.getItemAtPosition(pos);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }
}
