package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.Objects;

public abstract class CA {

    protected String relationName;
    protected MappingConfiguration mc;

    public abstract CA build(String toString, MappingConfiguration mc);

    public abstract String key();

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public MappingConfiguration getMc() {
        return mc;
    }

    public void setMc(MappingConfiguration mc) {
        this.mc = mc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CA) {
            CA ca = (CA) obj;
            return this.key().equals(ca.key());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.key());
        return hash;
    }
}
