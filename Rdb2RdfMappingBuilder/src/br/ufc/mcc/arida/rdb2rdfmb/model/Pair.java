/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

/**
 *
 * @author Luis
 */
public class Pair {

    private String child;
    private String parent;

    public Pair(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
