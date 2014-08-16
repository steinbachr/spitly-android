package me.iambob.spitly.activities;

import android.app.Activity;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import me.iambob.spitly.R;
import me.iambob.spitly.utils.ContactsUtils;
import me.iambob.spitly.utils.MessagingUtils;
import me.iambob.spitly.utils.GeneralUtils;
import me.iambob.spitly.models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SendTextActivity extends WaitForContactsActivity {
    Contact selectedContact;
    int selectedTime;
    String selectedTimeType;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**-- Helpers --**/
    /**
     * send out the message typed by the user after the chosen delay period
     * @param message the message to send after the chosen delay
     */
    private void sendMessageAfterDelay(final String message) {
        final Activity enclosing = this;

        final Runnable sendText = new Runnable() {
            public void run() {
                boolean messageSent = MessagingUtils.sendMessage(selectedContact.getNumber(), message);
                if (messageSent) {
                    MessagingUtils.createTextSentNotifation(enclosing, selectedContact);
                }
            }
        };

        TimeUnit timeUnit;
        if (selectedTimeType.equalsIgnoreCase("seconds")) {
            timeUnit = TimeUnit.SECONDS;
        } else if (selectedTimeType.equalsIgnoreCase("minutes")) {
            timeUnit = TimeUnit.MINUTES;
        } else {
            timeUnit = TimeUnit.HOURS;
        }
        scheduler.schedule(sendText, selectedTime, timeUnit);

        Toast.makeText(this, String.format("Text will go out in T-minus %d %s", selectedTime, selectedTimeType), Toast.LENGTH_LONG).show();
    }

    private void createSpinners() {
        Spinner timeSpinner = (Spinner)findViewById(R.id.time_spinner);
        Spinner timeTypeSpinner = (Spinner)findViewById(R.id.time_type_spinner);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_times, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setOnItemSelectedListener(new TimeSelectedListener());
        selectedTime = Integer.parseInt(timeSpinner.getSelectedItem().toString());

        ArrayAdapter<CharSequence> timeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_time_types, android.R.layout.simple_spinner_item);
        timeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeTypeSpinner.setAdapter(timeTypeAdapter);
        timeTypeSpinner.setOnItemSelectedListener(new TimeTypeSelectedListener());
        selectedTimeType = timeTypeSpinner.getSelectedItem().toString();
    }

    /**-- Actions --**/
    public void scheduleMessage(View v) {
        String message = ((TextView)this.findViewById(R.id.message)).getText().toString();
        sendMessageAfterDelay(message);
    }

    /**-- Activity Lifecycle Overrides --**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        this.createSpinners();

        /* load up the contact data into our database */
        ContactsUtils contactsUtils = new ContactsUtils(this);
        contactsUtils.getContacts();
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
        }
    }

    /**-- Time Selected Listener --**/

    class TimeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            selectedTime = Integer.parseInt((String)parent.getItemAtPosition(pos));
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /**-- Time Type Selected Listener --**/

    class TimeTypeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            selectedTimeType = (String)parent.getItemAtPosition(pos);
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
