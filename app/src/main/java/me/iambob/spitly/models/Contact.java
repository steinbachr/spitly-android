package me.iambob.spitly.models;

import android.content.Context;

import java.util.Comparator;

import me.iambob.spitly.database.Database;


public class Contact {
    /**-- Intent Keys --**/
    public static final String CONTACT_NAME = "contact name";

    private String contactId;
    private boolean isStarred;
    private String name;
    private String number;

    /**-- Constructors --**/
    public Contact(String contactId, String name, String number) {
        this.contactId = contactId;
        this.isStarred = false;
        this.name = name;
        this.number = number;
    }

    public Contact(String contactId, boolean isStarred, String name, String number) {
        this.contactId = contactId;
        this.isStarred = isStarred;
        this.name = name;
        this.number = number;
    }


    /**-- Getters / Setters --**/

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    /**-- Overrides --**/
    public String toString() {
        return this.getName();
    }

    /**-- Public Methods --**/
    /**
     * toggle the star on this contact on/off
     * @return true if the star has been toggled on, false if its been turned off
     */
    public boolean toggleStar(Context ctx) {
        Database db = new Database(ctx);
        boolean isStarred = this.isStarred();

        this.setStarred(!isStarred);

        /* persist the star value to the db */
        if (isStarred) {
            db.unstarContact(this);
        } else {
            db.starContact(this);
        }

        return !isStarred;
    }
}
