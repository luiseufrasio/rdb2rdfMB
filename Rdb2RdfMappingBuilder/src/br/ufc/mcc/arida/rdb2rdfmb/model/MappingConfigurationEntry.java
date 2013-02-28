/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Luis
 */
public class MappingConfigurationEntry {

    private int id;
    private SimpleStringProperty ontologyAlias;
    private SimpleStringProperty databaseAlias;

    public ObservableValue<String> ontologyAliasProperty() {
        return ontologyAlias;
    }

    public ObservableValue<String> databaseAliasProperty() {
        return databaseAlias;
    }

    public MappingConfigurationEntry(MappingConfiguration mc) {
        id = mc.getId();
        ontologyAlias = new SimpleStringProperty(mc.getOntologyAlias());
        databaseAlias = new SimpleStringProperty(mc.getDatabaseAlias());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SimpleStringProperty getOntologyAlias() {
        return ontologyAlias;
    }

    public void setOntologyAlias(SimpleStringProperty ontologyAlias) {
        this.ontologyAlias = ontologyAlias;
    }

    public SimpleStringProperty getDatabaseAlias() {
        return databaseAlias;
    }

    public void setDatabaseAlias(SimpleStringProperty databaseAlias) {
        this.databaseAlias = databaseAlias;
    }
}