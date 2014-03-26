/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.model;

/**
 *
 * @author Luis
 */
public class MappingConfiguration {

    private int id;
    private String ontologyAlias;
    private String databaseAlias;
    private String creationDate;
    private String ontologyFilePath;
    private String ontologyURL;
    private String ontologyLang;
    private String databaseDriver;
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    public MappingConfiguration(int id) {
        this.id = id;
    }
    
    public MappingConfiguration(int id, String ontologyAlias, String databaseAlias, String creationDate) {
        this.id = id;
        this.ontologyAlias = ontologyAlias;
        this.databaseAlias = databaseAlias;
        this.creationDate = creationDate;
    }

    public MappingConfiguration(String ontologyAlias, String databaseAlias, String ontologyLocation, int locationType, String ontologyLang, String databaseDriver, String databaseUrl, String databaseUser, String databasePassword) {
        this.ontologyAlias = ontologyAlias;
        this.databaseAlias = databaseAlias;
        if (locationType == 1) {
            this.ontologyFilePath = ontologyLocation;
        } else {
            this.ontologyURL = ontologyLocation;
        }
        this.databaseDriver = databaseDriver;
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
        this.ontologyLang = ontologyLang;
    }

    public String getOntologyLang() {
        return ontologyLang;
    }

    public void setOntologyLang(String ontologyLang) {
        this.ontologyLang = ontologyLang;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOntologyAlias() {
        return ontologyAlias;
    }

    public void setOntologyAlias(String ontologyAlias) {
        this.ontologyAlias = ontologyAlias;
    }

    public String getDatabaseAlias() {
        return databaseAlias;
    }

    public void setDatabaseAlias(String databaseAlias) {
        this.databaseAlias = databaseAlias;
    }

    public String getOntologyFilePath() {
        return ontologyFilePath;
    }

    public void setOntologyFilePath(String ontologyFilePath) {
        this.ontologyFilePath = ontologyFilePath;
    }

    public String getOntologyURL() {
        return ontologyURL;
    }

    public void setOntologyURL(String ontologyURL) {
        this.ontologyURL = ontologyURL;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}
