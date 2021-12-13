package com.newrelic.logging.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser
{
    private static Pattern  PARAMETERS_PATTERN = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

    public static Map<String,String> getMessageParameters(String message)
    {
        HashMap <String,String> messageParameters = new HashMap<>();
        Matcher parametersMatcher = PARAMETERS_PATTERN.matcher(message);
        while (parametersMatcher.find())
        {
            messageParameters.put(parametersMatcher.group(1),parametersMatcher.group(2));
        }
        return messageParameters;
    }
}