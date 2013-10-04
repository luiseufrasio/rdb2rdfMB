package rdb2rdfmappingbuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Rdb2RdfMappingBuilder extends Application {

    public static Stage pStage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Rdb2RdfMappingBuilder.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("main.fxml"));
            Scene scene = new Scene(page);
            pStage = primaryStage;
            primaryStage.setScene(scene);
            primaryStage.setTitle("RBA - R2RML By Assertions");
            primaryStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("img/icon.png")));

            primaryStage.show();
            
            pStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    System.exit(1);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(Rdb2RdfMappingBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}