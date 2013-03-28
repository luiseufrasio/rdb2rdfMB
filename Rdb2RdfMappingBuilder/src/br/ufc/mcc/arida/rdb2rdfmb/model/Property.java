package br.ufc.mcc.arida.rdb2rdfmb.model;

public abstract class Property {

    protected String prefix;
    protected String name;
    protected Class_ domain;
    protected int maxCardinality = 1;

    public Property() {
        super();
    }

    public Property(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    @Override
    public String toString() {
        return (prefix == null ? "" : prefix) + " : " + name.toLowerCase();
    }

    public abstract String getRangeName();

    public int getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }

    public String getPrefix() {
        return (prefix == null ? "" : prefix);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class_ getDomain() {
        return domain;
    }

    public void setDomain(Class_ domain) {
        this.domain = domain;
    }
}