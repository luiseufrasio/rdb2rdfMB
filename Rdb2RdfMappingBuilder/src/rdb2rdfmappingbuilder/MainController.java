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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Luis
 */
public class MainController implements Initializable {

    @FXML
    Button newMapping;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";

        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }

    /**
     * Called when the New button is fired.
     *
     * @param event the action event.
     */
    public void newMappingFired(ActionEvent event) throws IOException {
        System.out.println("chamou");
        AnchorPane page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("NewMapping.fxml"));

        final Stage secondaryStage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(page);
        secondaryStage.setScene(scene);
        secondaryStage.setTitle("Config a new Mapping");

        secondaryStage.show();
    }
}
