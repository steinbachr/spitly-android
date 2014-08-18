package me.iambob.spitly.utils;

import android.os.SystemClock;

import me.iambob.spitly.models.Contact;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GeneralUtils {

    public static boolean containsNumber(int[] numbers, int number) {
        for (int i = 0 ; i < numbers.length ; i++) {
            if (numbers[i] == number) {
                return false;
            }
        }

        return true;
    }

    /**
     * search through a list of contacts to try and find the one having the given name
     * @param contacts list of contacts to search through
     * @param contactName the name of the contact to find
     * @return the found Contact, or null if not found
     */
    public static Contact findContactByName(ArrayList<Contact> contacts, String contactName) {
        for (Contact c : contacts) {
            if (c.getName().toLowerCase().equals(contactName.toLowerCase())) {
                return c;
            }
        }

        return null;
    }

    /**
     * get the time in the future given by the time unit * the amount of time to wait + the current system time
     * @param unit the unit of measurement to use for calculating the future time (hours, minutes, seconds)
     * @param time the amount to go into the future
     * @return the time in milliseconds in the future as calculated
     */
    public static long getFutureTimeInMilliseconds(TimeUnit unit, int time) {
        final int MILLISECOND = 1000;
        long futureTime = SystemClock.elapsedRealtime();

        if (unit == TimeUnit.SECONDS) {
            futureTime += time * MILLISECOND;
        } else if (unit == TimeUnit.MINUTES) {
            futureTime += time * (MILLISECOND * 60);
        } else if (unit == TimeUnit.HOURS) {
            futureTime += time * (MILLISECOND * 60 * 60);
        }

        return futureTime;
    }
}
