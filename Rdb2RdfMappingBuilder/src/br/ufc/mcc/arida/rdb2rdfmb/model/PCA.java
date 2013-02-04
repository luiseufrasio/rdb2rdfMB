package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.ArrayList;
import java.util.List;

public abstract class PCA extends CA {

    protected List<String> fks = new ArrayList<String>();

    public PCA(List<String> fks) {
        this.fks = fks;
    }

    public PCA() {
    }

    public List<String> getFks() {
        return fks;
    }

    public void setFks(List<String> fks) {
        this.fks = fks;
    }
}