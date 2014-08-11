package me.iambob.spitly.activities;

import android.app.Activity;

import java.util.ArrayList;

import me.iambob.spitly.models.Contact;

public abstract class WaitForContactsActivity extends Activity {
    public abstract void onContactsLoaded(ArrayList<Contact> loadedContacts);
}