package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.ArrayList;
import java.util.List;

public class OCA extends CA {

    private ObjProperty oProperty;
    private List<String> fks;

    public OCA(ObjProperty oProperty, List<String> fks) {
        this.oProperty = oProperty;
        this.fks = fks;
    }

    public OCA() {
    }

    public ObjProperty getoProperty() {
        return oProperty;
    }

    public void setoProperty(ObjProperty oProperty) {
        this.oProperty = oProperty;
    }

    public List<String> getFks() {
        return fks;
    }

    public void setFks(List<String> fks) {
        this.fks = fks;
    }

    @Override
    public String toString() {
        String strOCA = prefixName + " : " + oProperty + " ≡ " + relationName + " / ";

        if (fks.size() == 1) {
            strOCA += fks.get(0);
        } else {
            strOCA += "[";
            int i = 0;
            for (String fk : fks) {
                if (i > 0) {
                    strOCA += ", " + fk;
                } else {
                    strOCA += fk;
                }
            }
            strOCA += "]";
        }
        
        return strOCA;
    }

    @Override
    public CA build(String toString, MappingConfiguration mc) {
        OCA ca = new OCA();
        /*
         * Formats:
         * prefixName : class ≡ relationName / [FksList]
         * prefixName : class ≡ relationName / fk
         */
        String v1[] = toString.split(":");
        ca.setPrefixName(v1[0].trim());
        String v2[] = v1[1].split(" ");
        ca.setoProperty(new ObjProperty(v2[1]));
        ca.setRelationName(v2[3]);

        List<String> fksList = new ArrayList<>();
        ca.setFks(fksList);
        int i = 5;
        if (v2[i].indexOf('[') != -1) {
            v2[i] = v2[i].substring(1);
            while (v2[i].indexOf("]") == -1) {
                v2[i] = v2[i].replaceAll(",", "");
                fksList.add(v2[i]);
                i++;
            }
            v2[i] = v2[i].substring(0, v2[i].length() - 1);
            fksList.add(v2[i]);
        } else {
            fksList.add(v2[i]);
        }

        return ca;
    }
}