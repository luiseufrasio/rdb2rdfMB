/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfiguration;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfigurationEntry;
import br.ufc.mcc.arida.rdb2rdfmb.sqlite.dao.MappingConfigurationDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Luis
 */
public class MainController implements Initializable {

    @FXML
    Button newMapping;
    
    @FXML
    TableView<MappingConfigurationEntry> mcTable;
    
    @FXML
    TableColumn itemOntoName;
    
    @FXML
    TableColumn itemDbName;
    
    @FXML
    TableColumn itemDateCreation;
    
    public static final Stage secondaryStage = new Stage(StageStyle.UTILITY);
    public static MainController m;
    
    ObservableList<MappingConfigurationEntry> dataMc = FXCollections.observableArrayList(); ;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";
        m = this;

        itemDateCreation.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("creationDate"));
        itemDbName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("databaseAlias"));
        itemOntoName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("ontologyAlias"));
        
        mcTable.setItems(dataMc);
        
        MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();
        try {
            List<MappingConfiguration> listMc = mcDAO.findAll();
            for (MappingConfiguration mappingConfiguration : listMc) {
                dataMc.add(new MappingConfigurationEntry(mappingConfiguration));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }

    /**
     * Called when the New button is fired.
     *
     * @param event the action event.
     */
    public void newMappingFired(ActionEvent event) throws IOException {
        AnchorPane page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("NewMapping.fxml"));
        
        Scene scene = new Scene(page);
        secondaryStage.setScene(scene);
        secondaryStage.setTitle("Config a new Mapping");

        secondaryStage.show();
    }
    
    public static void addRowMcTable(MappingConfigurationEntry mc) {
        m.dataMc.add(mc);
    }
}
