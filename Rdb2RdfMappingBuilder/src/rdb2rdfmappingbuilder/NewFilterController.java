/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Luis
 */
public class NewFilterController implements Initializable {
    @FXML
    Label lblTable;
    @FXML
    ComboBox<String> cbxColumn;
    @FXML
    ComboBox<String> cbxOperator;
    @FXML
    TextField txtValue;
    @FXML
    Button cancel;
    @FXML
    Button save;
    @FXML
    TextArea txtFilter;
    
    public static NewFilterController nm;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'NewMapping.fxml'.";
        nm = this;
        
        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }

    /**
     * Called when the Cancel button is fired.
     *
     * @param event the action event.
     */
    public void cancelFired(ActionEvent event) throws IOException {
        MainController.newFilterStage.close();
    }

    /**
     * Called when the Save button is fired.
     *
     * @param event the action event.
     */
    public void saveFired(ActionEvent event) throws IOException {
        
    }
}