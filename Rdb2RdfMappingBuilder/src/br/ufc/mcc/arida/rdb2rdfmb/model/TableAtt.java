/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

/**
 *
 * @author Luis
 */
public class TableAtt {

    private String table;
    private AttAlias attAlias;

    public TableAtt(String table, AttAlias att) {
        this.table = table;
        this.attAlias = att;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public AttAlias getAttAlias() {
        return attAlias;
    }

    public void setAttAlias(AttAlias attAlias) {
        this.attAlias = attAlias;
    }
}
