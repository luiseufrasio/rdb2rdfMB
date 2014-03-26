/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.sqlite.dao;

import br.ufc.mcc.arida.rdb2rdfmb.db.DbConnection;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfiguration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import br.ufc.mcc.arida.rdb2rdfmb.model.*;
import java.sql.Connection;

/**
 *
 * @author Luis
 */
public class CorrespondenceAssertionDAO {

    public int add(CA ca) throws SQLException {
        Connection connSQLite = DbConnection.getConnSQLite();
        Statement stm = connSQLite.createStatement();

        stm.executeUpdate("INSERT INTO CorrespondenceAssertion(toString,type,idMappingConfiguration) " 
                + "VALUES ('" + ca.toString() + "','" + ca.getClass().getName() + "'," + ca.getMc().getId() + ")");

        ResultSet rs = stm.executeQuery("SELECT id FROM CorrespondenceAssertion WHERE toString='"
                + ca.toString() + "' AND type='" + ca.getClass().getName() + "' AND idMappingConfiguration = " + ca.getMc().getId());
        rs.next();

        int id = rs.getInt("id");

        rs.close();
        stm.close();
        connSQLite.close();

        return id;
    }

    public void remove(int id) throws SQLException {
        Connection connSQLite = DbConnection.getConnSQLite();
        Statement stm = connSQLite.createStatement();

        stm.executeUpdate("DELETE FROM CorrespondenceAssertion WHERE id=\"" + id
                + "\"");
        stm.close();
        connSQLite.close();
    }

    public List<CA> findAllByMC(int idMappingConfiguration) throws SQLException {
        Connection connSQLite = DbConnection.getConnSQLite();
        Statement stm = connSQLite.createStatement();
        List<CA> cList = new ArrayList<>();
        ResultSet rs;
        rs = stm.executeQuery("SELECT * FROM CorrespondenceAssertion WHERE idMappingConfiguration = " + idMappingConfiguration);
        while (rs.next()) {
            int id = rs.getInt("id");
            String toString = rs.getString("toString");
            String type = rs.getString("type");
            
            
            //cList.add(new MappingConfiguration(, , rs.getString("databaseAlias"), rs.getString("creationDate")));
        }
        rs.close();
        stm.close();
        connSQLite.close();
        return cList;
    }

    public MappingConfiguration findById(int id) throws SQLException {
        Connection connSQLite = DbConnection.getConnSQLite();
        Statement stm = connSQLite.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM MappingConfiguration Where id =" + id);
        rs.next();
        MappingConfiguration mc = 
                new MappingConfiguration(rs.getInt("id"), rs.getString("ontologyAlias"), 
                rs.getString("databaseAlias"), 
                rs.getString("creationDate"));

        mc.setDatabaseDriver(rs.getString("databaseDriver"));
        mc.setDatabasePassword(rs.getString("databasePassword"));
        mc.setDatabaseUrl(rs.getString("databaseUrl"));
        mc.setDatabaseUser(rs.getString("databaseUser"));
        mc.setOntologyLang(rs.getString("ontologyLang"));
        String oFp = rs.getString("ontologyFilePath");
        mc.setOntologyFilePath("null".equals(oFp) ? "" : oFp);
        String oURL = rs.getString("ontologyURL");
        mc.setOntologyURL("null".equals(oURL) ? "" : oURL);
        
        rs.close();
        stm.close();
        connSQLite.close();
        return mc;
    }
    
    public void delete(int id) throws SQLException {
        Connection connSQLite = DbConnection.getConnSQLite();
        Statement stm = connSQLite.createStatement();

        stm.executeUpdate("DELETE FROM MappingConfiguration " +
                " WHERE id=" + id);
        stm.close();
        connSQLite.close();
    }
}