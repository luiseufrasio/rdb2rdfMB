package br.ufc.mcc.arida.rdb2rdfmb.model;

public abstract class Property {

    protected String name;
    protected Class_ domain;

    public Property() {
        super();
    }

    public Property(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract String getRangeName();

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