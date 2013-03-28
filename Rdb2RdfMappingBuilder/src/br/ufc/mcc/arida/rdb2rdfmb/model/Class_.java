package br.ufc.mcc.arida.rdb2rdfmb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Class_ {

    private String prefix;
    private String name;
    private List<DataProperty> dProperties = new ArrayList<DataProperty>();
    private List<ObjProperty> oProperties = new ArrayList<ObjProperty>();
    private Class_ superClass;

    public Class_(String prefix, String name) {
        super();
        this.prefix = prefix;
        this.name = name;
    }

    @Override
    public String toString() {
        return (prefix == null ? "" : prefix) + " : " + ("" + name.charAt(0)).toUpperCase() + name.substring(1);
    }

    public String getPrefix() {
        return (prefix == null ? "" : prefix);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public static List<Class_> getSubClasses(Class_ c, Collection<Class_> values) {
        List<Class_> list = new ArrayList<Class_>();

        for (Class_ class_ : values) {
            if (class_.getSuperClass() == c) {
                list.add(class_);
            }
        }
        return list;
    }

    public String getName() {
        if (name == null) {
            return null;
        }
        
        return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataProperty> getdProperties() {
        return dProperties;
    }

    public void setdProperties(List<DataProperty> dProperties) {
        this.dProperties = dProperties;
    }

    public List<ObjProperty> getoProperties() {
        return oProperties;
    }

    public void setoProperties(List<ObjProperty> oProperties) {
        this.oProperties = oProperties;
    }

    public Class_ getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Class_ superClass) {
        this.superClass = superClass;
    }
}