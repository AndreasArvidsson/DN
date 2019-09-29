# DN

**Working with Distinguished Name(DN) strings**    

## Usage
```java
DN dn = new DN("cn=Andreas Arvidsson,ou=Karlstad,o=Repill Linpro,c=Sweden");
```

### DN Class 
Represents the entire DN string.

**Members**
* value: String containing the entire DN.
* rdns: List containing instances of RDN in reverse order from the DN string.
    - rdns is private. Use 'getRND' method to access RNDs.

```java
DN = new DN("cn=Andreas,o=Repill Linpro,c=Sweden");
dn.value -> "cn=Andreas,o=Repill Linpro,c=Sweden"
dn.rdns -> [ RDN("c=Sweden"), RDN("o=Repill Linpro"), RDN("cn=Andreas") ]  
```

### RDN Class
Represents a single Relative DN.

**Members**
* attribute: String containing the entire DN.
* value: String containing the value in raw/unescaped format. 
* valueEscaped: String containing the value in escaped format. 

```java
RDN rdn = new RDN("cn", "Arvidsson, Andreas");
rdn.attribute -> "cn"
rdn.value -> "Arvidsson, Andreas"
rdn.valueEscaped -> "Arvidsson\, Andreas"
```

### length
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro,c=Sweden");
dn.length() -> 3
```

### equals
```java
DN dn = new DN("cn=Andreas");
dn.equals(new DN("cn=Andreas")) -> true
dn.equals("cn=Andreas") -> true
```

### getRDN
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.getRDN(1) -> RDN("cn=Andreas")
```

### getLastRDN
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.getLastRDN() -> RDN("cn=Andreas")
```

### getFirstRDN
Find first RDN with the given attribute
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.getFirstRDN("cn") -> RDN("cn=Andreas")
```

### getFirstIndex
Find index of first RDN with the given attribute
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.getFirstIndex("cn") -> 1
```

### getParent
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.getParent() -> DN("o=Repill Linpro")
```

### getParents
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro,c=Sweden");
dn.getParents() -> [ DN("o=Repill Linpro,c=Sweden"), DN("c=Sweden") ]
```

### append
```java
DN dn = new DN("o=Repill Linpro,c=Sweden");
dn.append("cn", "Andreas") -> DN("cn=Andreas,o=Repill Linpro,c=Sweden")
```

### toString
```java
DN dn = new DN("cn=Andreas,o=Repill Linpro");
dn.toString() -> "cn=Andreas,o=Repill Linpro"
```

### compareTo
* Used to sort a list of DNs by RDN.value
* Returns -1, 0, +1 
```java
List<DN> dns = Arrays.asList(
    new DN("cn=Andreas,c=Sweden"),
    new DN("cn=Andreas,o=Repill Linpro"),
    new DN("cn=Arvidsson,o=Repill Linpro")
);
Collections.sort(dns);
dns -> [
    DN("cn=Andreas,o=Repill Linpro"),
    DN("cn=Arvidsson,o=Repill Linpro"),
    DN("cn=Andreas,c=Sweden")
]
```
