package org.ac.cst8277.akmatova.kanykei.messagesmanagementservice.util;

import java.util.regex.Pattern;

public final class EmailUtil {
    public static boolean isEmailValid(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
