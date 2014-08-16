package me.iambob.spitly.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.content.DialogInterface;

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

/** when the user chooses 'close' after sending a text, we actually want to finish the entire application */
interface ChooseNextActionDialogListener {
    public void onCloseClick(DialogInterface dialog);
}

public class SendTextActivity extends WaitForContactsActivity implements ChooseNextActionDialogListener {
    ArrayList<Contact> loadedContacts;
    Contact selectedContact;
    int selectedTime;
    String selectedTimeType;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    AutoCompleteTextView contactsAutocomplete;

    /**-- Helpers --**/
    /**
     * display the dialog which allows the user to either create a new text or to close the application
     */
    private void showChooseNextActionDialog() {
        DialogFragment newFragment = new ChooseNextActionDialog();
        newFragment.show(getFragmentManager(), "actionDialog");
    }

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

        if (selectedContact == null) {
            String autocompleteText = contactsAutocomplete.getText().toString();

            /* it's possible the user never chose a contact by clicking a contact, but rather just finished typing the contact */
            selectedContact = GeneralUtils.findContactByName(loadedContacts, autocompleteText);

            /* if the selected contact is still null, then show the user an error toast..mmmm error toast  */
           if (selectedContact == null) {
               Toast.makeText(this, String.format("%s not found", autocompleteText), Toast.LENGTH_LONG).show();
               return;
           }
        }

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
        this.showChooseNextActionDialog();
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

    public void onContactsLoaded(ArrayList<Contact> loaded) {
        loadedContacts = loaded;

        Collections.sort(loadedContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, R.layout.contact_autocomplete_item, loadedContacts);
        contactsAutocomplete = (AutoCompleteTextView)findViewById(R.id.contacts_autocomplete);

        contactsAutocomplete.setAdapter(adapter);
        contactsAutocomplete.setOnItemSelectedListener(new ContactSelectedListener());
        contactsAutocomplete.setOnItemClickListener(new ContactSelectedListener());
    }

    /**-- DialogInterface Overrides --**/
    public void onCloseClick(DialogInterface diag) {
        this.finish();
    }


    /****---- Listeners -----****/
    /**-- Contact Selected Listener --**/

    class ContactSelectedListener implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            selectedContact = (Contact)parent.getItemAtPosition(pos);
        }

        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
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

    /*****----- Dialogs ------*****/
    public static class ChooseNextActionDialog extends DialogFragment {
        ChooseNextActionDialogListener listener;

        // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            listener = (ChooseNextActionDialogListener) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(R.string.dlg_text_sent_send_another, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                            /* dont actually have to do anything here */
                }
            })
                    .setNegativeButton(R.string.dlg_text_sent_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            listener.onCloseClick(dialog);
                        }
                    });

            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_next_action, null));

            return builder.create();
        }
    }
}
