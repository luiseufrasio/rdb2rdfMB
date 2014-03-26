/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

import de.fuberlin.wiwiss.d2rq.algebra.Join;

/**
 *
 * @author Luis
 */
public class Fk {

    private boolean inverse;
    private Join join;

    public Fk(boolean inverse, Join join) {
        this.inverse = inverse;
        this.join = join;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }
}
