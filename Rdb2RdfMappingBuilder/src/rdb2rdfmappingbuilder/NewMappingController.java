/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.db.DbConnection;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfiguration;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfigurationEntry;
import br.ufc.mcc.arida.rdb2rdfmb.sqlite.dao.MappingConfigurationDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Luis
 */
public class NewMappingController implements Initializable {

    @FXML
    Button cancel;
    @FXML
    Button search;
    @FXML
    Button testConn;
    @FXML
    Button save;
    @FXML
    TextField ontoAlias;
    @FXML
    TextField dbAlias;
    @FXML
    TextField filePath;
    @FXML
    TextField ontoUrl;
    @FXML
    TextField url;
    @FXML
    TextField user;
    @FXML
    TextField passwd;
    @FXML
    ComboBox<String> comboDrivers;
    @FXML
    ComboBox<String> comboOntoLangs;

    public static NewMappingController nm;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert search != null : "fx:id=\"search\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert filePath != null : "fx:id=\"filePath\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert comboDrivers != null : "fx:id=\"comboDrivers\" was not injected: check your FXML file 'NewMapping.fxml'.";

        comboDrivers.getSelectionModel().select(0);
        nm = this;

        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }

    /**
     * Called when the Cancel button is fired.
     *
     * @param event the action event.
     */
    public void cancelFired(ActionEvent event) throws IOException {
        MainController.secondaryStage.close();
    }

    /**
     * Called when the Search button is fired.
     *
     * @param event the action event.
     */
    public void searchFired(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();

        File f = fc.showOpenDialog(MainController.secondaryStage);

        filePath.setText(f.getAbsolutePath());
    }

    /**
     * Called when the TestConnection button is fired.
     *
     * @param event the action event.
     */
    public void testConnectionFired(ActionEvent event) throws IOException {
        String msg;
        int msgType;
        try {
            Connection c;
            c = DbConnection.connect(comboDrivers.getValue(), url.getText(), user.getText(), passwd.getText());

            if (c != null) {
                c.close();
                msg = "Connection Successful";
                msgType = JOptionPane.INFORMATION_MESSAGE;
            } else {
                msg = "Unexpected Error";
                msgType = JOptionPane.ERROR_MESSAGE;
            }
        } catch (Exception e) {
            msg = "Unexpected Error: " + e.getMessage();
            msgType = JOptionPane.ERROR_MESSAGE;
        }

        JOptionPane.showMessageDialog(null, msg, "Result", msgType);
    }

    /**
     * Called when the TestConnection button is fired.
     *
     * @param event the action event.
     */
    public void saveFired(ActionEvent event) throws IOException {
        MappingConfiguration mc;

        if (!("".equals(filePath.getText().trim()))) {
            mc = new MappingConfiguration(ontoAlias.getText(), dbAlias.getText(), filePath.getText(), 1, comboOntoLangs.getValue(), comboDrivers.getValue(), url.getText(), user.getText(), passwd.getText());
        } else {
            mc = new MappingConfiguration(ontoAlias.getText(), dbAlias.getText(), ontoUrl.getText(), 2, comboOntoLangs.getValue(), comboDrivers.getValue(), url.getText(), user.getText(), passwd.getText());
        }

        MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();
        try {
            if (ontoAlias.isDisable()) {
                mcDAO.update(mc);
            } else {
                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mc.setCreationDate(formatDate.format(new Date()));
                int id = mcDAO.add(mc);
                mc.setId(id);
                MainController.addRowMcTable(new MappingConfigurationEntry(mc));
            }

            JOptionPane.showMessageDialog(null, "Mapping Configuration Saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            MainController.secondaryStage.close();
        } catch (SQLException ex) {
            Logger.getLogger(NewMappingController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}