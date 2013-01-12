/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

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
    TextField filePath;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert cancel != null : "fx:id=\"cancel\" was not injected: check your FXML file 'NewMapping.fxml'.";
        assert search != null : "fx:id=\"search\" was not injected: check your FXML file 'NewMapping.fxml'.";

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
}
