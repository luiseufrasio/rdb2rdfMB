package br.ufc.mcc.arida.rdb2rdfmb.model;

public abstract class CA {

    protected String prefixName;
    protected String relationName;
    protected MappingConfiguration mc;

    public abstract CA build(String toString, MappingConfiguration mc);
    
    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

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
}
