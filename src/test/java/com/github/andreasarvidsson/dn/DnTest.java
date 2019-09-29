package com.github.andreasarvidsson.dn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class DnTest {

    final String dnString = "cn=Andreas\\, Arvidsson,ou=Karlstad,o=Repill Linpro,c=Sverige";
    final DN dn = new DN(dnString);

    @Test
    public void length() throws Exception {
        Assert.assertEquals(4, dn.length());
    }

    @Test
    public void equals() throws Exception {
        Assert.assertEquals(true, dn.equals(new DN(dnString)));
        Assert.assertEquals(true, dn.equals(dnString));
    }

    @Test
    public void getRDN() throws Exception {
        Assert.assertEquals("c=Sverige", dn.getRDN(0).toString());
        Assert.assertEquals("o=Repill Linpro", dn.getRDN(1).toString());
        Assert.assertEquals("ou=Karlstad", dn.getRDN(2).toString());
    }

    @Test
    public void getLastRDN() throws Exception {
        Assert.assertEquals("cn=Andreas\\, Arvidsson", dn.getLastRDN().toString());
    }

    @Test
    public void getFirstRDN() throws Exception {
        Assert.assertEquals("o=Repill Linpro", dn.getFirstRDN("o").toString());
    }

    @Test
    public void getFirstIndex() throws Exception {
        Assert.assertEquals(1, dn.getFirstIndex("o"));
    }

    @Test
    public void getParent() throws Exception {
        Assert.assertEquals("ou=Karlstad,o=Repill Linpro,c=Sverige", dn.getParent().toString());
    }

    @Test
    public void getParents() throws Exception {
        final List<DN> parents = dn.getParents();
        Assert.assertEquals(3, parents.size());
        Assert.assertEquals("ou=Karlstad,o=Repill Linpro,c=Sverige", parents.get(0).toString());
        Assert.assertEquals("o=Repill Linpro,c=Sverige", parents.get(1).toString());
        Assert.assertEquals("c=Sverige", parents.get(2).toString());
    }

    @Test
    public void append() throws Exception {
        final DN dn2 = dn.append("ou", " # , # + \" \\ < > ; u0x0Ah u0x0Di = / ");
        Assert.assertEquals(5, dn2.length());
        rdn(dn2.getLastRDN());
    }

    private void rdn(final RDN rdn1) {
        final RDN rdn2 = new RDN(rdn1.attribute, rdn1.value);
        final RDN rdn3 = new RDN(rdn1.attribute, rdn1.valueEscaped, true);
        Assert.assertEquals("\\# \\, # \\+ \\\" \\\\ \\< \\> \\; \\u0x0Ah \\u0x0Di \\= \\/", rdn1.valueEscaped);
        Assert.assertEquals("rdn2.attribute", rdn1.attribute, rdn2.attribute);
        Assert.assertEquals("rdn2.value", rdn1.value, rdn2.value);
        Assert.assertEquals("rdn2.valueEscaped", rdn1.valueEscaped, rdn2.valueEscaped);
        Assert.assertEquals("rdn3.attribute", rdn1.attribute, rdn3.attribute);
        Assert.assertEquals("rdn3.value", rdn1.value, rdn3.value);
        Assert.assertEquals("rdn3.valueEscaped", rdn1.valueEscaped, rdn3.valueEscaped);
    }

    @Test
    public void toStringTest() throws Exception {
        Assert.assertEquals(dn.value, dn.toString());
    }

    @Test
    public void compareTo() throws Exception {
        final List<DN> dns = Arrays.asList(
                new DN("c=D,b=A,a=C"),
                new DN("c=A,b=D,a=B"),
                new DN("c=G,b=H,a=A"),
                new DN("d=F,c=C,b=C,a=D"),
                new DN("c=C,b=C,a=D")
        );
        Collections.sort(dns);
        Assert.assertEquals("A,H,G", getStr(dns.get(0)));
        Assert.assertEquals("B,D,A", getStr(dns.get(1)));
        Assert.assertEquals("C,A,D", getStr(dns.get(2)));
        Assert.assertEquals("D,C,C", getStr(dns.get(3)));
        Assert.assertEquals("D,C,C,F", getStr(dns.get(4)));
    }

    private String getStr(final DN dn) {
        final List<String> res = new ArrayList();
        for (final RDN rdn : dn) {
            res.add(rdn.value);
        }
        return String.join(",", res);
    }

}
