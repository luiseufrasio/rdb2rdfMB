package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.ArrayList;
import java.util.List;

public class CCA extends CA {

    private Class_ class_;
    private List<String> attributes = new ArrayList<String>();
    private String selCondition;

    public CCA() {
    }

    public CCA(Class_ class_, List<String> attributes, String selCondition) {
        this.class_ = class_;
        this.attributes = attributes;
        this.selCondition = selCondition;
    }

    @Override
    public String toString() {
        String strCCA = prefixName + " : " + class_ + " ≡ " + relationName + "[";
        int i = 0;
        
        for (String att : attributes) {
            if (i++ > 0) {
                strCCA += ", " + att;
            } else {
                strCCA += att;
            }
        }
        strCCA += "]";

        if (selCondition != null) {
            strCCA += ", " + selCondition;
        }

        return strCCA;
    }

    public Class_ getClass_() {
        return class_;
    }

    public void setClass_(Class_ class_) {
        this.class_ = class_;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getSelCondition() {
        return selCondition;
    }

    public void setSelCondition(String selCondition) {
        this.selCondition = selCondition;
    }

    @Override
    public CA build(String toString, MappingConfiguration mc) {
        CCA ca = new CCA();
        //Format prefixName : class ≡ relationName [attList]
        String v1[] = toString.split(":");
        ca.setPrefixName(v1[0].trim());
        String v2[] = v1[1].split(" ");
        ca.setClass_(new Class_(v2[1]));
        ca.setRelationName(v2[3]);

        List<String> atts = new ArrayList<>();
        ca.setAttributes(atts);
        for (int i = 4; i < v2.length; i++) {
            String att = v2[i].replaceAll(",", "");
            if (att.indexOf('[') != -1) {
                att = att.substring(1);
            }   
            if (att.indexOf(']') != -1) {
                att = att.substring(0, att.length() - 1);
            }
            atts.add(att);
        }

        return ca;
    }
}