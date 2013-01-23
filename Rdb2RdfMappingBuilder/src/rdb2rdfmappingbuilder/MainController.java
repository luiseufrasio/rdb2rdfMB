/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.db.DbConnection;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfiguration;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfigurationEntry;
import br.ufc.mcc.arida.rdb2rdfmb.sqlite.dao.MappingConfigurationDAO;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import de.fuberlin.wiwiss.d2rq.algebra.Attribute;
import de.fuberlin.wiwiss.d2rq.algebra.RelationName;
import de.fuberlin.wiwiss.d2rq.dbschema.DatabaseSchemaInspector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;
import model.Class_;
import model.DataProperty;
import model.ObjProperty;

/**
 *
 * @author Luis
 */
public class MainController implements Initializable {

    @FXML //  fx:id="dbTree"
    private TreeView<String> dbTree;
    @FXML //  fx:id="ontoTree"
    private TreeView<String> ontoTree;
    @FXML
    Button newMapping;
    @FXML
    Button edit;
    @FXML
    Button delete;
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
    ObservableList<MappingConfigurationEntry> dataMc = FXCollections.observableArrayList();
    MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";
        m = this;

        itemDateCreation.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("creationDate"));
        itemDbName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("databaseAlias"));
        itemOntoName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("ontologyAlias"));

        mcTable.setItems(dataMc);

        final ObservableList<MappingConfigurationEntry> tableSelection = mcTable.getSelectionModel().getSelectedItems();
        tableSelection.addListener(tableSelectionChanged);

        try {
            List<MappingConfiguration> listMc = mcDAO.findAll();
            for (MappingConfiguration mappingConfiguration : listMc) {
                dataMc.add(new MappingConfigurationEntry(mappingConfiguration));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        AnchorPane page;
        try {
            page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("NewMapping.fxml"));
            Scene scene = new Scene(page);
            secondaryStage.setScene(scene);
            secondaryStage.initModality(Modality.APPLICATION_MODAL);
            secondaryStage.initOwner(Rdb2RdfMappingBuilder.pStage);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }
    private final ListChangeListener<MappingConfigurationEntry> tableSelectionChanged =
            new ListChangeListener<MappingConfigurationEntry>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends MappingConfigurationEntry> c) {
                    updateDeleteButtonState();
                    updateEditButtonState();

                    final MappingConfigurationEntry selectedItem = mcTable.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        MappingConfiguration mc;
                        try {
                            mc = mcDAO.findById(selectedItem.getId());

                            buildDBTree(mc);
                            buildOntoTree(mc);
                        } catch (SQLException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };

    public void buildOntoTree(MappingConfiguration mc) {
        try {
            HashMap<String, Class_> hm = new HashMap<String, Class_>();
            loadOntology(mc, hm);

            // Crio o nó pai que será o nome da ontologia
            TreeItem<String> ontoRoot = new TreeItem<>(mc.getOntologyAlias());

            // Crio nós filhos com os nomes das classes
            Collection<Class_> cClasses = hm.values();
            for (Class_ class_ : cClasses) {
                TreeItem<String> item = new TreeItem<>(class_.getName());

                /* Cria os data properties da classe */
                for (int i = 0; i < class_.getdProperties().size(); i++) {
                    TreeItem<String> subItem = new TreeItem<>(class_.getdProperties().get(i).getName());
                    item.getChildren().add(subItem);
                }

                /* Cria os object properties da classe */
                for (int i = 0; i < class_.getoProperties().size(); i++) {
                    TreeItem<String> subItem = new TreeItem<>(class_.getoProperties().get(i).getName());
                    item.getChildren().add(subItem);
                }

                ontoRoot.getChildren().add(item);
            }
            // Insiro o nó raiz na TreeView
            ontoTree.setRoot(ontoRoot);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buildDBTree(MappingConfiguration mc) {
        /* Crio o nó pai que será o nome do banco */
        TreeItem<String> dataBase = new TreeItem<>(mc.getDatabaseAlias());
        dataBase.setExpanded(false);

        DatabaseSchemaInspector schema = DbConnection.getSchemaInspector(mc.getDatabaseDriver(), mc.getDatabaseUrl(), mc.getDatabaseUser(), mc.getDatabasePassword());
        List<RelationName> tables = schema.listTableNames();
        for (RelationName relationName : tables) {
            TreeItem<String> tableItem = new TreeItem<>(relationName.tableName());
            dataBase.getChildren().add(tableItem);

            List<Attribute> listCols = schema.listColumns(relationName);
            for (Attribute attribute : listCols) {
                TreeItem<String> colItem = new TreeItem<>(attribute.attributeName());
                tableItem.getChildren().add(colItem);
            }
        }

        // Insiro o nó raiz na TreeView
        dbTree.setRoot(dataBase);
    }

    private static void loadOntology(MappingConfiguration mc, Map<String, Class_> classes)
            throws FileNotFoundException {
        OntModel ontModel = ModelFactory
                .createOntologyModel(OntModelSpec.OWL_MEM);
        String filePath = mc.getOntologyFilePath();
        if (filePath != null && !"".equals(filePath)) {
            ontModel.read(new FileInputStream(new File(filePath)),
                    "http://ufc.br/rdb2rdfmb/", mc.getOntologyLang());
        } else {
            ontModel.read(mc.getOntologyURL(), mc.getOntologyLang());
        }

        ExtendedIterator<OntClass> i = ontModel.listClasses();
        while (i.hasNext()) {
            OntClass ontClass = (OntClass) i.next();

            String name = ontClass.getLocalName();

            if (classes.get(name) == null) {
                Class_ c = new Class_(name);
                classes.put(name, c);

                OntClass superClass = ontClass.getSuperClass();
                if (superClass != null) {
                    String superName = superClass.getLocalName();
                    Class_ superC = classes.get(superName);

                    if (superC == null) {
                        superC = new Class_(superName);
                        classes.put(superName, superC);
                    }

                    c.setSuperClass(superC);
                }
            }
        }

        ExtendedIterator<DatatypeProperty> i2 = ontModel
                .listDatatypeProperties();
        while (i2.hasNext()) {
            DatatypeProperty datatypeProperty = (DatatypeProperty) i2.next();

            String dClassName = datatypeProperty.getDomain().getLocalName();
            Class_ dClass = classes.get(dClassName);
            String dpName = datatypeProperty.getLocalName();
            String rangeName = datatypeProperty.getRange().getLocalName();

            DataProperty dp = new DataProperty(dpName, dClass, rangeName);
            dClass.getdProperties().add(dp);
        }

        ExtendedIterator<com.hp.hpl.jena.ontology.ObjectProperty> i3 = ontModel.listObjectProperties();
        while (i3.hasNext()) {
            ObjectProperty objectProperty = (ObjectProperty) i3.next();

            String dClassName = objectProperty.getDomain().getLocalName();
            String rClassName = objectProperty.getRange().getLocalName();
            String opName = objectProperty.getLocalName();

            Class_ dClass = classes.get(dClassName);
            Class_ rClass = classes.get(rClassName);

            ObjProperty op = new ObjProperty(opName, dClass, rClass);
            dClass.getoProperties().add(op);
        }
    }

    private void updateDeleteButtonState() {
        boolean disable = true;
        if (delete != null && mcTable != null) {
            final boolean nothingSelected = mcTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (delete != null) {
            delete.setDisable(disable);
        }
    }

    private void updateEditButtonState() {
        boolean disable = true;
        if (edit != null && mcTable != null) {
            final boolean nothingSelected = mcTable.getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (edit != null) {
            edit.setDisable(disable);
        }
    }

    /**
     * Called when the New button is fired.
     *
     * @param event the action event.
     */
    public void newMappingFired(ActionEvent event) throws IOException {
        NewMappingController.nm.ontoAlias.setText("");
        NewMappingController.nm.ontoAlias.setDisable(false);
        NewMappingController.nm.dbAlias.setText("");
        NewMappingController.nm.dbAlias.setDisable(false);

        NewMappingController.nm.filePath.setText("");
        NewMappingController.nm.ontoUrl.setText("");
        NewMappingController.nm.passwd.setText("");
        NewMappingController.nm.url.setText("");
        NewMappingController.nm.user.setText("");

        NewMappingController.nm.comboDrivers.setValue("");
        secondaryStage.setTitle("Config a new Mapping");
        secondaryStage.show();
    }

    /**
     * Called when the Edit button is fired.
     *
     * @param event the action event.
     */
    public void editMappingFired(ActionEvent event) throws IOException {
        final MappingConfigurationEntry selectedItem = mcTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(null, "Select a mapping configuration to be edited", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        secondaryStage.setTitle("Edit a Mapping");
        secondaryStage.show();

        try {
            MappingConfiguration mc = mcDAO.findById(selectedItem.getId());
            NewMappingController.nm.ontoAlias.setText(mc.getOntologyAlias());
            NewMappingController.nm.ontoAlias.setDisable(true);
            NewMappingController.nm.dbAlias.setText(mc.getDatabaseAlias());
            NewMappingController.nm.dbAlias.setDisable(true);

            NewMappingController.nm.filePath.setText(mc.getOntologyFilePath());
            NewMappingController.nm.ontoUrl.setText(mc.getOntologyURL());
            NewMappingController.nm.passwd.setText(mc.getDatabasePassword());
            NewMappingController.nm.url.setText(mc.getDatabaseUrl());
            NewMappingController.nm.user.setText(mc.getDatabaseUser());

            NewMappingController.nm.comboDrivers.setValue(mc.getDatabaseDriver());
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Called when the Delete button is fired.
     *
     * @param event the action event.
     */
    public void deleteMappingFired(ActionEvent event) throws IOException {
        final MappingConfigurationEntry selectedItem = mcTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(null, "Select a mapping configuration to be deleted", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(null, "The mapping selected will be DELETED. Are you sure?", "Confirmation Question", JOptionPane.OK_CANCEL_OPTION);
        if (answer == 0) {
            try {
                mcDAO.delete(selectedItem.getId());
                dataMc.remove(selectedItem);
                JOptionPane.showMessageDialog(null, "Mapping Configuration Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void addRowMcTable(MappingConfigurationEntry mc) {
        m.dataMc.add(mc);
    }
}
