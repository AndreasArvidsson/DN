package com.github.andreasarvidsson.dn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andreas Arvidsson https://github.com/AndreasArvidsson/DN
 * https://docs.microsoft.com/en-us/previous-versions/windows/desktop/ldap/distinguished-names
 */
public class DN implements Comparable<DN>, Iterable<RDN> {

    //Split on , but not \,
    private final static Pattern DN_REGEXP = Pattern.compile("(\\\\.|[^,])+");
    //Split on = but not \=
    private final static Pattern RDN_REGEXP = Pattern.compile("(\\\\.|[^=])+");

    public final String value;
    private List<RDN> rdns = null;

    @JsonCreator
    public DN(final String dnString) {
        this.value = dnString;
    }

    public DN(final List<RDN> rdns) {
        this.value = rdnsToString(rdns);
        this.rdns = rdns;
    }

    public DN() {
        this.value = "";
    }

    public int length() {
        return getRdns().size();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof DN && value.equals(((DN) o).value);
    }

    public boolean equals(final String dnString) {
        return this.value.equals(dnString);
    }

    public RDN getRDN(final int index) {
        return getRdns().get(index);
    }

    public RDN getLastRDN() {
        return getRDN(length() - 1);
    }

    public String getLastValue() {
        return getLastRDN().value;
    }

    public RDN getFirstRDN(final String attribute) {
        for (final RDN rdn : getRdns()) {
            if (rdn.attribute.equals(attribute)) {
                return rdn;
            }
        }
        return null;
    }

    public int getFirstIndex(final String attribute) {
        for (int i = 0; i < length(); ++i) {
            if (rdns.get(i).attribute.equals(attribute)) {
                return i;
            }
        }
        return -1;
    }

    public DN getParent() {
        if (this.length() < 2) {
            return null;
        }
        return subDn(length() - 1);
    }

    public List<DN> getParents() {
        final List<DN> res = new ArrayList();
        for (int i = length() - 1; i > 0; --i) {
            res.add(subDn(i));
        }
        return res;
    }

    public DN append(final String attribute, final String value) {
        final List<RDN> newRdns = new ArrayList(getRdns());
        newRdns.add(new RDN(attribute, value));
        return new DN(newRdns);
    }

    public DN subDn(final int length) {
        return new DN(getRdns().subList(0, length));
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(final DN dn) {
        final List<RDN> otherRdns = dn.getRdns();
        final int size = Math.min(length(), otherRdns.size());
        for (int i = 0; i < size; ++i) {
            final int compare = rdns.get(i).compareTo(otherRdns.get(i));
            if (compare > 0) {
                return 1;
            }
            if (compare < 0) {
                return -1;
            }
        }
        if (length() > otherRdns.size()) {
            return 1;
        }
        if (length() < otherRdns.size()) {
            return -1;
        }
        return 0;
    }

    @Override
    public Iterator<RDN> iterator() {
        return new RdnIterator();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    private List<RDN> getRdns() {
        if (rdns == null) {
            rdns = stringToRdns(value);
        }
        return rdns;
    }

    private static List<RDN> stringToRdns(final String dnString) {
        final List<RDN> res = new ArrayList();
        final Matcher m = DN_REGEXP.matcher(dnString);
        while (m.find()) {
            final Matcher m2 = RDN_REGEXP.matcher(m.group());
            if (!m2.find()) {
                throw new RuntimeException(String.format("Malformed DN string '%s'", dnString));
            }
            final String attribute = m2.group();
            if (!m2.find()) {
                throw new RuntimeException(String.format("Malformed DN string '%s'", dnString));
            }
            res.add(new RDN(attribute, m2.group(), true));
        }
        Collections.reverse(res);
        return res;
    }

    private static String rdnsToString(final List<RDN> rdns) {
        final StringBuilder sb = new StringBuilder();
        for (int i = rdns.size() - 1; i > -1; --i) {
            sb.append(rdns.get(i));
            if (i > 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private class RdnIterator implements Iterator<RDN> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < rdns.size();
        }

        @Override
        public RDN next() {
            return rdns.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("not supported yet");
        }
    }

}
