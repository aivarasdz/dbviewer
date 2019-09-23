package com.aivaras.dbviewer.util;

import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Check if values are of PostgreSQL identifier pattern.
     * Should be used with values that are concatenated as string to query, to prevent SQL injection.
     * @param values
     */
    public static void throwExceptionIfNotIdentifierPattern(String...values){
        Pattern pattern = Pattern.compile("[a-z][a-zA-Z0-9_]+");
        for (String value : values){
            if (!pattern.matcher(value).matches()){
                throw new IllegalArgumentException("Value is not of identifier pattern: " + value);
            }
        }
    }
}
