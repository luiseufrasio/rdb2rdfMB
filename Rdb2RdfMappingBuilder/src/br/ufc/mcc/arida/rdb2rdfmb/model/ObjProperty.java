package br.ufc.mcc.arida.rdb2rdfmb.model;

public class ObjProperty extends Property {

    private Class_ range;

    public ObjProperty(String name, Class_ domain, Class_ range) {
        super();
        this.name = name;
        this.domain = domain;
        this.range = range;
    }

    public ObjProperty(String name) {
        this.name = name;
    }

    public Class_ getRange() {
        return range;
    }

    public void setRange(Class_ range) {
        this.range = range;
    }

    @Override
    public String getRangeName() {
        return range.getName();
    }
}