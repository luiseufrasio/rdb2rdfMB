package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.ArrayList;
import java.util.List;

public class DCA extends PCA {

    private DataProperty dProperty;
    private List<String> attributes = new ArrayList<String>();

    @Override
    public String toString() {
        String strDCA = dProperty + " ≡ " + relationName + " / ";

        if (fks.size() > 0) {
            strDCA += "[";
            int i = 0;
            for (String fk : fks) {
                if (i++ > 0) {
                    strDCA += ", " + fk;
                } else {
                    strDCA += fk;
                }
            }
            strDCA += "] / ";
        }

        if (attributes.size() > 1) {
            strDCA += "{";
            int i = 0;
            for (String a : attributes) {
                if (i++ > 0) {
                    strDCA += ", " + a;
                } else {
                    strDCA += a;
                }
            }
            strDCA += "}";
        } else if (attributes.size() == 1) {
            String a = attributes.get(0);
            strDCA += a;
        } else {
            strDCA += "null";
        }

        return strDCA;
    }

    @Override
    public CA build(String toString, MappingConfiguration mc) {
        DCA ca = new DCA();
        /*
         * Formats: 
         * 1. prefix : dProperty ≡ relationName / attribute
         * 2. prefix : dProperty ≡ relationName / {attList}
         * 3. prefix : dProperty ≡ relationName / [FksList] / attribute
         * 4. prefix : dProperty ≡ relationName / [FksList] / {attList}
         */
        String v1[] = toString.split(":");
        String v2[] = v1[1].split(" ");
        ca.setdProperty(new DataProperty(v1[0].trim(), v2[1]));
        ca.setRelationName(v2[3]);

        List<String> atts = new ArrayList<>();
        ca.setAttributes(atts);
        
        int i = 5;
        if (v2[i].indexOf('[') != -1) {
            List<String> fksList = new ArrayList<>();
            ca.setFks(fksList);
            
            v2[i] = v2[i].substring(1);
            while (v2[i].indexOf("]") == -1) {
                v2[i] = v2[i].replaceAll(",", "");
                fksList.add(v2[i]);
                i++;
            }

            v2[i] = v2[i].substring(0, v2[i].length() - 1);
            fksList.add(v2[i]);
        }
        
        if ("/".equals(v2[i])) {
            i++;
        }
        
        if (v2[i].indexOf('{') != -1) {
            v2[i] = v2[i].substring(1);
            while (v2[i].indexOf("}") == -1) {
                v2[i] = v2[i].replaceAll(",", "");
                atts.add(v2[i]);
                i++;
            }
            v2[i] = v2[i].substring(0, v2[i].length() - 1);
            atts.add(v2[i]);
        } else {
            atts.add(v2[i]);
        }

        return ca;
    }

    public DataProperty getdProperty() {
        return dProperty;
    }

    public void setdProperty(DataProperty dProperty) {
        this.dProperty = dProperty;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String key() {
        return dProperty.getName();
    }
}
