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
    private String childTable;
    private String parent;
    private String parentTable;

    public Pair(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    public Pair(String child, String childTable, String parent, String parentTable) {
        this.child = child;
        this.childTable = childTable;
        this.parent = parent;
        this.parentTable = parentTable;
    }

    public String getChildTable() {
        return childTable;
    }

    public void setChildTable(String childTable) {
        this.childTable = childTable;
    }

    public String getParentTable() {
        return parentTable;
    }

    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
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
