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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Luis
 */
public class MappingConfigurationDAO {

    public int add(MappingConfiguration mc) throws SQLException {
        Statement stm = DbConnection.connSQLite.createStatement();

        stm.executeUpdate("INSERT INTO MappingConfiguration(ontologyAlias,databaseAlias,creationDate,ontologyFilePath,ontologyURL,ontologyContent,databaseDriver,databaseUrl,databaseUser,databasePassword ) VALUES ('"
                + mc.getOntologyAlias() + "','" + mc.getDatabaseAlias() + "','" + mc.getCreationDate() + "','" + mc.getOntologyFilePath() + "','" + mc.getOntologyURL() + "','" + mc.getOntologyContent() + "','" + mc.getDatabaseDriver() + "','" + mc.getDatabaseUrl() + "','" + mc.getDatabaseUser() + "','" + mc.getDatabasePassword() + "')");

        ResultSet rs = stm.executeQuery("SELECT id FROM MappingConfiguration WHERE ontologyAlias='"
                + mc.getOntologyAlias() + "' AND databaseAlias='" + mc.getDatabaseAlias() + "'");
        rs.next();

        int id = rs.getInt("id");

        rs.close();
        stm.close();

        return id;
    }

    public void remove(int id) throws SQLException {
        Statement stm = DbConnection.connSQLite.createStatement();

        stm.executeUpdate("DELETE FROM MappingConfiguration WHERE id=\"" + id
                + "\"");
        stm.close();
    }

    public List<MappingConfiguration> findAll() throws SQLException {
        Statement stm = DbConnection.connSQLite.createStatement();
        List<MappingConfiguration> cList = new ArrayList<>();
        ResultSet rs;
        rs = stm.executeQuery("SELECT * FROM MappingConfiguration "
                + "ORDER BY creationDate DESC ");
        while (rs.next()) {
            cList.add(new MappingConfiguration(rs.getInt("id"), rs.getString("ontologyAlias"), rs.getString("databaseAlias"), rs.getString("creationDate")));
        }
        rs.close();
        stm.close();
        return cList;
    }

    public MappingConfiguration findById(int id) throws SQLException {
        Statement stm = DbConnection.connSQLite.createStatement();

        ResultSet rs = stm.executeQuery("SELECT * FROM MappingConfiguration Where id =" + id);
        rs.next();
        MappingConfiguration mappingConfiguration = new MappingConfiguration(rs.getInt("id"), rs.getString("ontologyAlias"), rs.getString("databaseAlias"), rs.getString("creationDate"));

        rs.close();
        stm.close();

        return mappingConfiguration;
    }
}
