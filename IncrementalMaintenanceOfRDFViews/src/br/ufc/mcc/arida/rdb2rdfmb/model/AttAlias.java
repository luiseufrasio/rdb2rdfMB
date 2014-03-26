/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

/**
 *
 * @author Luis
 */
public class AttAlias {

    private String att;
    private String alias;

    public AttAlias(String att, String alias) {
        this.att = att;
        this.alias = alias;
    }

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
