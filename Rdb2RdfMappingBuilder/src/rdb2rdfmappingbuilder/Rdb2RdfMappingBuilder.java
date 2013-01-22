package rdb2rdfmappingbuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Rdb2RdfMappingBuilder extends Application {

    public static Stage pStage;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Rdb2RdfMappingBuilder.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("main.fxml"));
            Scene scene = new Scene(page);
            pStage = primaryStage;
            primaryStage.setScene(scene);
            primaryStage.setTitle("rdb2RDF Mapping Builder");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Rdb2RdfMappingBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}