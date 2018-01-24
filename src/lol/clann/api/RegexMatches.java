
package lol.clann.api;

import java.util.regex.Pattern;


public class RegexMatches {
    public static Boolean has(String s, String regex) {
        return Pattern.compile(regex).matcher(s).find();
    }

    public static String replaceAll(String input, String regex, String replace) {
        return Pattern.compile(regex).matcher(input).replaceAll(replace);
    }

    public static String replace(String input, String regex, String replace) {
        return Pattern.compile(regex).matcher(input).replaceFirst(replace);
    }
}
