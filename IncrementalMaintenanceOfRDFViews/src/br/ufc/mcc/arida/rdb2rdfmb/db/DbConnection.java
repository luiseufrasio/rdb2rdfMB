/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.db;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import de.fuberlin.wiwiss.d2rq.dbschema.DatabaseSchemaInspector;
import de.fuberlin.wiwiss.d2rq.map.Database;
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

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, e);
        }

        Statement stm;
        try {
            Connection connSQLite = getConnSQLite();
            
            stm = connSQLite.createStatement();
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS MappingConfiguration ("
                    + "id integer PRIMARY KEY NOT NULL,"
                    + "ontologyAlias text NOT NULL,"
                    + "databaseAlias text NOT NULL,"
                    + "creationDate text NOT NULL,"
                    + "ontologyFilePath text,"
                    + "ontologyURL text,"
                    + "ontologyLang text NOT NULL,"
                    + "databaseDriver text NOT NULL,"
                    + "databaseUrl text NOT NULL,"
                    + "databaseUser text NOT NULL,"
                    + "databasePassword text NOT NULL,"
                    + "UNIQUE(ontologyAlias,databaseAlias));");
            
            stm.executeUpdate("CREATE TABLE IF NOT EXISTS CorrespondenceAssertion ("
                    + "id integer PRIMARY KEY NOT NULL,"
                    + "toString text NOT NULL,"
                    + "type text NOT NULL,"
                    + "idMappingConfiguration integer NOT NULL,"
                    + "FOREIGN KEY(idMappingConfiguration) REFERENCES MappingConfiguration(id) ON DELETE CASCADE);");
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
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

    public static DatabaseSchemaInspector getSchemaInspector(String driverName, String url, String user, String passwd) {
        Database database = new Database(ResourceFactory.createResource());
        database.setJdbcURL(url);
        database.setJDBCDriver(getDriverClass(driverName));
        database.setUsername(user);
        database.setPassword(passwd);
        
        return database.connectedDB().schemaInspector();
    }

    public static String getDriverClass(String v) {
        switch (v) {
            case "MySQL Connector":
                return "com.mysql.jdbc.Driver";
            case "PostgreSQL":
                return "org.postgresql.Driver";
            case "SQLite":
                return "org.sqlite.JDBC";
            default:
                return "";
        }
    }

    public static Connection getConnSQLite() throws SQLException {
        Connection connSQLite = DriverManager.getConnection("jdbc:sqlite:db2rdf.db");
        connSQLite.setAutoCommit(true);
        
        return connSQLite;
    }
}