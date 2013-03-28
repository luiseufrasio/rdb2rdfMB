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
    private String att;

    public TableAtt(String table, String att) {
        this.table = table;
        this.att = att;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }
}
