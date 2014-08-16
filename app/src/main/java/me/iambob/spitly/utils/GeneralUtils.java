package me.iambob.spitly.utils;


public class GeneralUtils {

    public static boolean containsNumber(int[] numbers, int number) {
        for (int i = 0 ; i < numbers.length ; i++) {
            if (numbers[i] == number) {
                return false;
            }
        }

        return true;
    }
}
