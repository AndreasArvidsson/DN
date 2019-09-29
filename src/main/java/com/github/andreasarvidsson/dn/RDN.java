package com.github.andreasarvidsson.dn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andreas Arvidsson
 * https://github.com/AndreasArvidsson/DN
 */
public class RDN implements Comparable<RDN> {

    private final static Pattern MATCH_BACKSLASH = Pattern.compile("\\\\");
    private final static Pattern MATCH_OTHER = Pattern.compile("^(#)|(,|\\+|\"|<|>|;|u0x0A|u0x0D|=|\\/)");
    private final static Pattern MATCH_ESCAPED = Pattern.compile("(?:\\\\(.))");

    public final String attribute, value, valueEscaped;

    public RDN(final String attribute, final String value) {
        this(attribute, value, false);
    }

    public RDN(final String attribute, final String value, final boolean isValueEscaped) {
        this.attribute = attribute;
        if (isValueEscaped) {
            this.value = unEscapeValue(value);
            this.valueEscaped = value;
        }
        else {
            this.value = value.trim();
            this.valueEscaped = escapeValue(value.trim());
        }
    }

    @Override
    public String toString() {
        return String.format("%s=%s", attribute, valueEscaped);
    }

    @Override
    public int compareTo(final RDN rdn) {
        return value.compareTo(rdn.value);
    }

    private static String escapeValue(String value) {
        value = StringReplacer.replace(value, MATCH_BACKSLASH, (final String match) -> "\\\\\\" + match);
        return StringReplacer.replace(value, MATCH_OTHER, (final String match) -> "\\\\" + match);
    }

    private static String unEscapeValue(final String value) {
        return StringReplacer.replace(value, MATCH_ESCAPED, (final String match) -> match);
    }

    private static class StringReplacer {

        public static String replace(final String input, final Pattern regex, final StringReplacerCallback callback) {
            final StringBuffer sb = new StringBuffer();
            final Matcher regexMatcher = regex.matcher(input);
            while (regexMatcher.find()) {
                regexMatcher.appendReplacement(sb, callback.replace(regexMatcher.group()));
            }
            regexMatcher.appendTail(sb);
            return sb.toString();
        }
    }

    private interface StringReplacerCallback {

        public String replace(String match);
    }

}
