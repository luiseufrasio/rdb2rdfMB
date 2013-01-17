/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class DbConnection {

    public static Connection connSQLite;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, e);
        }

        Statement stm;
        try {
            connSQLite = DriverManager.getConnection("jdbc:sqlite:db2rdf.db");

            stm = connSQLite.createStatement();
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS MappingConfiguration ("
                    + "id integer PRIMARY KEY NOT NULL,"
                    + "ontologyAlias text NOT NULL,"
                    + "databaseAlias text NOT NULL,"
                    + "creationDate text NOT NULL,"
                    + "ontologyFilePath text,"
                    + "ontologyURL text,"
                    + "ontologyContent NOT NULL,"
                    + "databaseDriver text NOT NULL,"
                    + "databaseUrl text NOT NULL,"
                    + "databaseUser text NOT NULL,"
                    + "databasePassword text NOT NULL,"
                    + "UNIQUE(ontologyAlias,databaseAlias));");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createSQLiteDB() throws SQLException, ClassNotFoundException,
            IOException {
        try (Statement stm = connSQLite.createStatement()) {
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS MappingConfiguration ("
                    + "id integer PRIMARY KEY NOT NULL,"
                    + "ontologyAlias text NOT NULL,"
                    + "databaseAlias text NOT NULL,"
                    + "ontologyFilePath varchar(255),"
                    + "ontologyURL varchar(255),"
                    + "ontologyContent none NOT NULL,"
                    + "databaseDriver text NOT NULL,"
                    + "databaseUrl text NOT NULL,"
                    + "databaseUser text NOT NULL,"
                    + "databasePassword text NOT NULL,"
                    + "UNIQUE(ontologyAlias,databaseAlias));");

            /*
             stm.executeUpdate("CREATE TABLE IF NOT EXISTS RdfClass ("
             + "id integer PRIMARY KEY NOT NULL,"
             + "prefix varchar(20),"
             + "name varchar(50),"
             + "FOREIGN KEY(prefix) REFERENCES Ontology(prefix) ON DELETE CASCADE,"
             + "UNIQUE(prefix,name));");

             stm.executeUpdate("CREATE TABLE IF NOT EXISTS DataProperty ("
             + "id integer PRIMARY KEY NOT NULL,"
             + "prefix varchar(20),"
             + "name varchar(50),"
             + "FOREIGN KEY(prefix) REFERENCES Ontology(prefix) ON DELETE CASCADE,"
             + "UNIQUE(prefix,name));");

             stm.executeUpdate("CREATE TABLE IF NOT EXISTS ObjectProperty ("
             + "id integer PRIMARY KEY NOT NULL,"
             + "prefix varchar(20),"
             + "name varchar(50),"
             + "FOREIGN KEY(prefix) REFERENCES Ontology(prefix) ON DELETE CASCADE,"
             + "UNIQUE(prefix,name));");

             stm.executeUpdate("CREATE TABLE IF NOT EXISTS DomainLiteral ("
             + "id integer PRIMARY KEY NOT NULL,"
             + "idDataProperty integer,"
             + "class integer,"
             + "literalType varchar(20),"
             + "FOREIGN KEY(class) REFERENCES RdfClass(id) ON DELETE CASCADE,"
             + "FOREIGN KEY(idDataProperty) REFERENCES DataProperty(id) ON DELETE CASCADE);");

             stm.executeUpdate("CREATE TABLE IF NOT EXISTS DomainRange ("
             + "id integer PRIMARY KEY NOT NULL,"
             + "idObjectProperty integer,"
             + "classDomain integer,"
             + "classRange integer,"
             + "FOREIGN KEY(classDomain) REFERENCES RdfClass(id) ON DELETE CASCADE,"
             + "FOREIGN KEY(classRange) REFERENCES RdfClass(id) ON DELETE CASCADE,"
             + "FOREIGN KEY(idObjectProperty) REFERENCES ObjectProperty(id) ON DELETE CASCADE);");
             */
        }
    }

    public static Connection connect(String driverName, String url, String user, String passwd) throws Exception {
        try {
            Class.forName(DbConnection.getDriverClass(driverName));
            Connection c = DriverManager.getConnection(url, user, passwd);
            return c;
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        }
    }

    public static String getDriverClass(String v) {
        switch (v) {
            case "MySQL Connector":
                return "com.mysql.jdbc.Driver";
            case "PostgreSQL":
                return "org.postgresql.Driver";
            default:
                return "";
        }
    }
}