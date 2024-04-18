package com.example.cmscreditaccountservice;

public class Utility {

    public static String generateRandomNumericString(int size) {
        String NumericString = "1234567890";

        // create StringBuffer size of NumericString
        StringBuilder sb = new StringBuilder(size);

        for (int i = 0; i < size; i++) {

            // generate a random number between
            // 0 to NumericString variable length-1
            int index = (int) ((NumericString.length()-1) * Math.random());

            // add Character one by one in end of sb
            sb.append(NumericString.charAt(index));
        }

        return sb.toString();
    }
}
