package me.iambob.spitly.models;

public class Contact {
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
}
