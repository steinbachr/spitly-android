package me.iambob.spitly.database;

import android.content.Context;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import me.iambob.spitly.models.Contact;
import me.iambob.spitly.database.DatabaseContract.DatabaseEntry;

import java.util.ArrayList;
import java.util.HashMap;


public class Database {
    DatabaseHelper databaseHelper;
    HashMap<String, Contact> dbContacts;

    public Database(Context ctx) {
        this.databaseHelper = new DatabaseHelper(ctx);
        this.dbContacts = this.getDbContacts();
    }


    /**-- Helpers --**/
    /**
     * get all contacts from the Database as a hashmap where the key is the contact id and the value
     * the contact itself
     * @return HashMap of contactId -> Contact
     */
    private HashMap<String, Contact> getDbContacts() {
        HashMap<String, Contact> result = new HashMap<String, Contact>();

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String[] projection = {DatabaseEntry.COLUMN_NAME_CONTACT_ID, DatabaseEntry.COLUMN_NAME_NAME,
                DatabaseEntry.COLUMN_NAME_IS_STARRED, DatabaseEntry.COLUMN_NAME_NAME, DatabaseEntry.COLUMN_NAME_NUMBER};
        Cursor c = db.query(DatabaseEntry.TABLE_NAME, projection, null, null, null, null, null);

        int idIndex = c.getColumnIndex(DatabaseEntry.COLUMN_NAME_CONTACT_ID);
        int starredIndex = c.getColumnIndex(DatabaseEntry.COLUMN_NAME_IS_STARRED);
        int nameIndex = c.getColumnIndex(DatabaseEntry.COLUMN_NAME_NAME);
        int phoneIndex = c.getColumnIndex(DatabaseEntry.COLUMN_NAME_NUMBER);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Contact dbContact = new Contact(c.getString(idIndex), c.getInt(starredIndex) == 1, c.getString(nameIndex), c.getString(phoneIndex));
            result.put(dbContact.getContactId(), dbContact);
            c.moveToNext();
        }

        return result;
    }

    /**
     * check if the passed contact exists in the DB
     * @param contact the contact to check existence for
     * @return true if contact is already stored, false otherwise
     */
    private boolean contactInDb(Contact contact) {
        return this.dbContacts.containsKey(contact.getContactId());
    }

    /**
     * update the given contact in the database with given vals. Important note: we select contacts
     * in the database on their name rather then phone since if we have multiple numbers for a user
     * (i.e. Eric Jaugey -> 5192932939 and 3943932323) we want to star both those numbers.
     * @param contact the contact to update in the database
     * @param updateVals the vals to update the contact with
     */
    private void updateContactInDb(Contact contact, ContentValues updateVals) {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        String selection = DatabaseEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { contact.getName() };
        new UpdateTask(db, updateVals, selection, selectionArgs).execute();
    }

    /**
     * add the passed contact to the database
     * @param contact the contact to add to the database
     */
    private void addContactToDb(Contact contact) {
        SQLiteDatabase db = this.databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_CONTACT_ID, contact.getContactId());
        values.put(DatabaseEntry.COLUMN_NAME_IS_STARRED, 0);
        values.put(DatabaseEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseEntry.COLUMN_NAME_NUMBER, contact.getNumber());

        new InsertTask(db, values).execute();
    }

    /**
     * return whether the contact had previously been starred by the user
     * @param contact the contact to get the isStarred value for
     * @return true if the contact was starred, false otherwise
     */
    private boolean getContactStarred(Contact contact) {
        Contact dbContact = this.dbContacts.get(contact.getContactId());
        return dbContact.isStarred();
    }


    /**-- Public API --**/
    /**
     * for each passed contact, check whether that contact already exists in the database. If so,
     * augment the contact's isStarred value with the stored value. If not, add the contact to the
     * database.
     * @param contacts the contacts to augment / insert
     */
    public void processContacts(ArrayList<Contact> contacts) {
        for (Contact cont : contacts) {
            if (this.contactInDb(cont)) {
                cont.setStarred(this.getContactStarred(cont));
            } else {
                this.addContactToDb(cont);
            }
        }
    }

    /**
     * get the Contact from the Database having the given name, or null if none found
     * @param name the name of the contact to get from the database
     * @return the Contact if found, null otherwise
     */
    public Contact getContactByName(String name) {
        HashMap<String, Contact> dbContacts = getDbContacts();

        for (HashMap.Entry<String, Contact> contact : dbContacts.entrySet()) {
            if (contact.getValue().getName().equalsIgnoreCase(name)) {
                return contact.getValue();
            }
        }

        return null;
    }

    /**
     * get the Contact from the Database having the given number, or null if none found
     * @param number the number of the contact to get from the database
     * @return the Contact if found, null otherwise
     */
    public Contact getContactByNumber(String number) {
        HashMap<String, Contact> dbContacts = getDbContacts();

        for (HashMap.Entry<String, Contact> contact : dbContacts.entrySet()) {
            if (contact.getValue().getNumber().equals(number)) {
                return contact.getValue();
            }
        }

        return null;
    }

    /**
     * star the given contact
     * @param contact the contact to star
     */
    public void starContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_IS_STARRED, 1);
        this.updateContactInDb(contact, values);
    }

    /**
     * unstar the given contact
     * @param contact the contact to unstar
     */
    public void unstarContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseEntry.COLUMN_NAME_IS_STARRED, 0);
        this.updateContactInDb(contact, values);
    }

    /**-- TASKS --**/
    /**
     * The AsyncTask in charge of actually performing the insert into the DB
     */
    class InsertTask extends AsyncTask<Void, Void, Void> {
        SQLiteDatabase db;
        ContentValues data;

        public InsertTask(SQLiteDatabase db, ContentValues data) {
            this.db = db;
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... noop) {
            this.db.insert(DatabaseEntry.TABLE_NAME, null, this.data);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    /**
     * The AsyncTask in charge of updating data in the database
     */
    class UpdateTask extends AsyncTask<Void, Void, Void> {
        SQLiteDatabase db;
        ContentValues data;
        String selection;
        String[] selectionArgs;

        public UpdateTask(SQLiteDatabase db, ContentValues data, String selection, String[] selectionArgs) {
            this.db = db;
            this.data = data;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        @Override
        protected Void doInBackground(Void... noop) {
            this.db.update(DatabaseEntry.TABLE_NAME, this.data, this.selection, this.selectionArgs);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
