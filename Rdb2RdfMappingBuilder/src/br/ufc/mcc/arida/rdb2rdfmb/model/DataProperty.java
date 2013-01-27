package br.ufc.mcc.arida.rdb2rdfmb.model;

public class DataProperty extends Property {

    private String range;

    public DataProperty(String name, Class_ domain, String range) {
        super();
        this.name = name;
        this.domain = domain;
        this.range = range;
    }

    public DataProperty(String name) {
        super(name);
    }
    
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public String getRangeName() {
        return getRange();
    }
}
