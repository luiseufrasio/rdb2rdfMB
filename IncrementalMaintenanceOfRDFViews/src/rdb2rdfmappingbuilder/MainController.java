/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.db.DbConnection;
import br.ufc.mcc.arida.rdb2rdfmb.mapping.R2RMLGen;
import br.ufc.mcc.arida.rdb2rdfmb.mapping.ViewsGen;
import br.ufc.mcc.arida.rdb2rdfmb.model.CA;
import br.ufc.mcc.arida.rdb2rdfmb.model.CCA;
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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.JOptionPane;
import br.ufc.mcc.arida.rdb2rdfmb.model.Class_;
import br.ufc.mcc.arida.rdb2rdfmb.model.DCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.DataProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.Fk;
import br.ufc.mcc.arida.rdb2rdfmb.model.OCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.ObjProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.PCA;
import com.hp.hpl.jena.ontology.MaxCardinalityRestriction;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import d2rq.server;
import de.fuberlin.wiwiss.d2rq.algebra.Join;
import java.awt.Desktop;
import java.io.PrintStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Luis
 */
public class MainController implements Initializable {

    @FXML //  fx:id="dbTree"
    private TreeView<String> dbTree;
    @FXML //  fx:id="ontoTree"
    protected TreeView<String> ontoTree;
    @FXML
    private TreeView<String> expOntoTree;
    @FXML
    protected TextField txtAssertion;
    StringBuilder views = new StringBuilder("");
    List<String> listViews = new ArrayList<>();
    final ToggleGroup group = new ToggleGroup();
    @FXML
    RadioButton rbtRelViews;
    @FXML
    RadioButton rbtR2rmlViews;
    @FXML
    Label lblAssertion;
    @FXML
    TextField txtAssertionExp;
    @FXML
    Button newMapping;
    @FXML
    Button edit;
    @FXML
    Button delete;
    @FXML
    Button createR2rml;
    @FXML
    Button genExpOntoTree;
    @FXML
    Button btnPublishData;
    @FXML
    TableView<MappingConfigurationEntry> mcTable;
    @FXML
    TableColumn itemOntoName;
    @FXML
    TableColumn itemDbName;
    @FXML
    ListView<CA> assertionsList;
    @FXML
    TabPane tabPane;
    @FXML
    WebView r2rmlContent;
    @FXML
    WebView sqlViews;
    public static final Stage secondaryStage = new Stage(StageStyle.UTILITY);
    public static final Stage newFilterStage = new Stage(StageStyle.UTILITY);
    public static MainController m;
    ObservableList<MappingConfigurationEntry> dataMc = FXCollections.observableArrayList();
    ObservableList<CA> dataAssertions = FXCollections.observableArrayList();
    MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();
    protected HashMap<TreeItem, CA> assertions = new HashMap<>();
    HashMap<TreeItem, CA> assertionsExp = new HashMap<>();
    HashMap<String, Class_> classes = new HashMap<>();
    HashMap<TreeItem, Object> dbMap = new HashMap<>();
    HashMap<String, List<Attribute>> mapTableCols = new HashMap<>();
    HashMap<String, List<Join>> mapTableFks = new HashMap<>();
    HashMap<String, List<Join>> mapTableFksInv = new HashMap<>();
    HashMap<String, Fk> mapFks = new HashMap<>();
    HashMap<String, String> mapPrefixes = new HashMap<>();
    String r2rml;
    MappingConfiguration mc = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";
        m = this;

//        rbtR2rmlViews.setToggleGroup(group);
//        rbtRelViews.setToggleGroup(group);

        itemDbName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("databaseAlias"));
        itemOntoName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("ontologyAlias"));

        mcTable.setItems(dataMc);
        assertionsList.setItems(dataAssertions);

        if (dataAssertions.size() == 0) {
            tabPane.getTabs().get(1).setDisable(true);
            tabPane.getTabs().get(2).setDisable(true);
            tabPane.getTabs().get(3).setDisable(true);
            tabPane.getTabs().get(4).setDisable(true);
            tabPane.getSelectionModel().select(0);
        }

        final ObservableList<MappingConfigurationEntry> tableSelection = mcTable.getSelectionModel().getSelectedItems();

        tratarEventoMcTable();
        tratarEventoOntoTree();
        tratarEventoExpOntoTree();
        tratarEventoDbTree();
        tratarEventoAssertionsList();

        try {
            List<MappingConfiguration> listMc = mcDAO.findAll();
            for (MappingConfiguration mappingConfiguration : listMc) {
                dataMc.add(new MappingConfigurationEntry(mappingConfiguration));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            AnchorPane page = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("NewMapping.fxml"));
            Scene scene = new Scene(page);
            secondaryStage.setScene(scene);
            secondaryStage.initModality(Modality.APPLICATION_MODAL);
            secondaryStage.initOwner(Rdb2RdfMappingBuilder.pStage);

            AnchorPane page2 = (AnchorPane) FXMLLoader.load(Rdb2RdfMappingBuilder.class.getResource("NewFilter.fxml"));
            Scene sceneFilter = new Scene(page2);
            newFilterStage.setScene(sceneFilter);
            newFilterStage.initModality(Modality.APPLICATION_MODAL);
            newFilterStage.initOwner(Rdb2RdfMappingBuilder.pStage);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(this.getClass().getSimpleName() + ".initialize");
    }

    public void buildOntoTree(MappingConfiguration mc) {
        try {
            loadOntology(mc);

            // Crio o nó pai que será o nome da ontologia
            TreeItem<String> ontoRoot = new TreeItem<>(mc.getOntologyAlias());

            // Crio nós filhos com os nomes das classes
            Collection<Class_> cClasses = classes.values();
            for (Class_ class_ : cClasses) {
                if (class_.getName() == null) {
                    continue;
                }
                Node classIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/ontology/class.gif")));
                TreeItem<String> item = new TreeItem<>(class_.toString(), classIcon);
                CCA cca = new CCA();
                cca.setClass_(class_);
                assertions.put(item, cca);

                Class_ superClass = class_.getSuperClass();
                if (superClass != null) {
                    /* Cria os data properties da super classe */
                    for (int i = 0; i < superClass.getdProperties().size(); i++) {
                        createDpItem(item, superClass.getdProperties().get(i), cca);
                    }
                }

                /* Cria os data properties da classe */
                for (int i = 0; i < class_.getdProperties().size(); i++) {
                    createDpItem(item, class_.getdProperties().get(i), cca);
                }

                if (superClass != null) {
                    /* Cria os object properties da super classe */
                    for (int i = 0; i < superClass.getoProperties().size(); i++) {
                        createOpItem(item, superClass.getoProperties().get(i), cca);
                    }
                }

                /* Cria os object properties da classe */
                for (int i = 0; i < class_.getoProperties().size(); i++) {
                    createOpItem(item, class_.getoProperties().get(i), cca);
                }

                ontoRoot.getChildren().add(item);
            }
            // Insiro o nó raiz na TreeView
            ontoTree.setRoot(ontoRoot);
            ontoTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buildDBTree(MappingConfiguration mc) {
        /* Crio o nó pai que será o nome do banco */
        TreeItem<String> dataBase = new TreeItem<>(mc.getDatabaseAlias());
        dataBase.setExpanded(false);

        DatabaseSchemaInspector schema = DbConnection.getSchemaInspector(mc.getDatabaseDriver(), mc.getDatabaseUrl(), mc.getDatabaseUser(), mc.getDatabasePassword());
        List<RelationName> tables = schema.listTableNames(null);
        for (RelationName relationName : tables) {
            Node tableIcon = new ImageView(
                    new Image(getClass().getResourceAsStream("img/database/table.gif")));

            // Primeira maiúscula
            //String tableName = relationName.tableName().substring(0,1).toUpperCase();
            //tableName += relationName.tableName().substring(1);
            String tableName = relationName.tableName();

            TreeItem<String> treeItem = new TreeItem<>(tableName, tableIcon);
            dataBase.getChildren().add(treeItem);
            dbMap.put(treeItem, relationName);

            List<Attribute> listCols = schema.listColumns(relationName);
            for (Attribute attribute : listCols) {
                TreeItem<String> colItem = new TreeItem<>(attribute.attributeName());
                List<Attribute> listPk = schema.primaryKeyColumns(relationName);
                if (isIn(attribute, listPk)) {
                    Node pkIcon = new ImageView(
                            new Image(getClass().getResourceAsStream("img/database/pk.gif")));
                    colItem.setGraphic(pkIcon);
                } else {
                    Node attIcon = new ImageView(
                            new Image(getClass().getResourceAsStream("img/database/attribute.gif")));
                    colItem.setGraphic(attIcon);
                }
                treeItem.getChildren().add(colItem);
                dbMap.put(colItem, attribute);
            }
            mapTableCols.put(relationName.tableName().toLowerCase(), listCols);

            List<Join> listFks0 = schema.foreignKeys(relationName, 0);
            for (Join fk0 : listFks0) {
                String fkName = "fk0_" + fk0.table1() + "2" + fk0.table2();
                if (!mapFks.keySet().contains(fkName)) {
                    mapFks.put(fkName, new Fk(false, fk0));
                }
                Node fkIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                TreeItem<String> fk0Item = new TreeItem<>(fkName, fkIcon);
                treeItem.getChildren().add(fk0Item);
                dbMap.put(fk0Item, fk0);
            }
            mapTableFks.put(relationName.tableName(), listFks0);

            List<Join> listFks1 = schema.foreignKeys(relationName, 1);
            for (Join fk1 : listFks1) {
                String fkName;
                fkName = "fk1_" + fk1.table1() + "2" + fk1.table2();
                if (!mapFks.keySet().contains(fkName)) {
                    mapFks.put(fkName, new Fk(true, fk1));
                }
                Node fkIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/database/fkInv.gif")));
                TreeItem<String> fk1Item = new TreeItem<>(fkName, fkIcon);
                treeItem.getChildren().add(fk1Item);
                dbMap.put(fk1Item, fk1);
            }
            mapTableFksInv.put(relationName.tableName(), listFks1);
        }

        // Insiro o nó raiz na TreeView
        dbTree.setRoot(dataBase);
        dbTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadOntology(MappingConfiguration mc)
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
            String prefix = ontModel.getNsURIPrefix(ontClass.getNameSpace());
            String name = ontClass.getLocalName();

            if (classes.get(name + prefix) == null) {
                Class_ c = new Class_(prefix, name);
                classes.put(prefix + name, c);
                mapPrefixes.put(prefix, ontClass.getNameSpace());

                OntClass superClass = ontClass.getSuperClass();
                if (superClass != null) {
                    String superName = superClass.getLocalName();
                    String superPrefix = ontModel.getNsURIPrefix(superClass.getNameSpace());
                    Class_ superC = classes.get(superPrefix + superName);

                    if (superC == null) {
                        superC = new Class_(superPrefix, superName);
                        classes.put(superPrefix + superName, superC);
                        mapPrefixes.put(superPrefix, superClass.getNameSpace());
                    }

                    c.setSuperClass(superC);
                }
            }
        }

        ExtendedIterator<DatatypeProperty> i2 = ontModel
                .listDatatypeProperties();
        while (i2.hasNext()) {
            DatatypeProperty datatypeProperty = (DatatypeProperty) i2.next();

            if (datatypeProperty.getDomain() != null && datatypeProperty.getRange() != null) {
                ExtendedIterator iDomains = datatypeProperty.listDomain();
                while (iDomains.hasNext()) {
                    OntClass cDomain = (OntClass) iDomains.next();
                    
                    String dClassPrefix = ontModel.getNsURIPrefix(cDomain.getNameSpace());
                    String dClassName = cDomain.getLocalName();
                    Class_ dClass = classes.get(dClassPrefix + dClassName);
                    String dpName = datatypeProperty.getLocalName();
                    String dpPrefix = ontModel.getNsURIPrefix(datatypeProperty.getNameSpace());
                    String rangeName = datatypeProperty.getRange().getLocalName();

                    DataProperty dp = new DataProperty(dpPrefix, dpName, dClass, rangeName);
                    if (dClass != null) {
                        dClass.getdProperties().add(dp);
                        mapPrefixes.put(dpPrefix, datatypeProperty.getNameSpace());
                    }
                }
            }
        }

        ExtendedIterator<com.hp.hpl.jena.ontology.ObjectProperty> i3 = ontModel.listObjectProperties();
        while (i3.hasNext()) {
            ObjectProperty objectProperty = (ObjectProperty) i3.next();

            if (objectProperty.getDomain() != null && objectProperty.getRange() != null) {
                String dClassPrefix = ontModel.getNsURIPrefix(objectProperty.getDomain().getNameSpace());
                String dClassName = objectProperty.getDomain().getLocalName();
                String rClassPrefix = ontModel.getNsURIPrefix(objectProperty.getRange().getNameSpace());
                String rClassName = objectProperty.getRange().getLocalName();
                String opName = objectProperty.getLocalName();
                String opPrefix = ontModel.getNsURIPrefix(objectProperty.getNameSpace());

                Class_ dClass = classes.get(dClassPrefix + dClassName);
                Class_ rClass = classes.get(rClassPrefix + rClassName);

                ObjProperty op = new ObjProperty(opPrefix, opName, dClass, rClass);
                if (dClass != null) {
                    dClass.getoProperties().add(op);
                    mapPrefixes.put(opPrefix, objectProperty.getNameSpace());
                }
            }
        }

        ExtendedIterator<Restriction> iRest = ontModel.listRestrictions();
        while (iRest.hasNext()) {
            Restriction r = (Restriction) iRest.next();
            if (r.isMaxCardinalityRestriction()) {
                MaxCardinalityRestriction mcr = r.asMaxCardinalityRestriction();
                System.out.println(mcr.getMaxCardinality());
                OntClass ontClass = mcr.getSubClass();
                OntProperty p = mcr.getOnProperty();

                String prefix = ontModel.getNsURIPrefix(ontClass.getNameSpace());
                String name = ontClass.getLocalName();

                Class_ c = classes.get(prefix + name);
                if (p.isObjectProperty()) {
                    ObjectProperty objectProperty = p.asObjectProperty();
                    String opName = objectProperty.getLocalName();
                    String opPrefix = ontModel.getNsURIPrefix(objectProperty.getNameSpace());

                    for (ObjProperty objProperty : c.getoProperties()) {
                        if (objProperty.getPrefix().equals(opPrefix) && objProperty.getName().equals(opName)) {
                            objProperty.setMaxCardinality(mcr.getMaxCardinality());
                        }
                    }
                } else {
                    DatatypeProperty datatypeProperty = p.asDatatypeProperty();
                    String dpName = datatypeProperty.getLocalName();
                    String dpPrefix = ontModel.getNsURIPrefix(datatypeProperty.getNameSpace());

                    for (DataProperty dataProperty : c.getdProperties()) {
                        if (dataProperty.getPrefix().equals(dpPrefix) && dataProperty.getName().equals(dpName)) {
                            dataProperty.setMaxCardinality(mcr.getMaxCardinality());
                        }
                    }
                }
            }
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
     * Called when the Add Filter button is fired.
     *
     * @param event the action event.
     */
    public void addFilterFired(ActionEvent event) throws IOException {
        CCA cca = (CCA) assertions.get(ontoTree.getSelectionModel().getSelectedItem());

        NewFilterController.nm.lblTable.setText("Table: " + cca.getRelationName());
        DatabaseSchemaInspector schema = DbConnection.getSchemaInspector(mc.getDatabaseDriver(), mc.getDatabaseUrl(), mc.getDatabaseUser(), mc.getDatabasePassword());

        RelationName relationName = new RelationName(null, cca.getRelationName());

        List<Attribute> listCols = schema.listColumns(relationName);
        final ObservableList<String> items = NewFilterController.nm.cbxColumn.getItems();
        items.clear();
        for (Attribute attribute : listCols) {
            items.add(attribute.attributeName() + " [" + schema.columnType(attribute).name() + "]");
        }

        newFilterStage.setTitle("Add a Selection Filter");
        newFilterStage.show();
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
            NewMappingController.nm.comboOntoLangs.setValue(mc.getOntologyLang());
            NewMappingController.nm.passwd.setText(mc.getDatabasePassword());
            NewMappingController.nm.url.setText(mc.getDatabaseUrl());
            NewMappingController.nm.user.setText(mc.getDatabaseUser());

            NewMappingController.nm.comboDrivers.setValue(mc.getDatabaseDriver());
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class
                    .getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MainController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void addRowMcTable(MappingConfigurationEntry mc) {
        m.dataMc.add(mc);
    }

    /**
     * Called when the Save button is fired.
     *
     * @param event the action event.
     */
    public void saveAssertionFired(ActionEvent event) throws IOException {
        final TreeItem selectedItem = ontoTree.getSelectionModel().getSelectedItem();
        CA ca = assertions.get(selectedItem);

        int pos = dataAssertions.indexOf(ca);
        if (pos >= 0) {
            dataAssertions.remove(pos);
        }
        dataAssertions.add(ca);

        tabPane.getTabs().get(1).setDisable(false);
        tabPane.getSelectionModel().select(1);
        assertionsList.getSelectionModel().select(ca);

        createR2rml.setDisable(false);
        genExpOntoTree.setDisable(false);
    }

    /**
     * Called when the PublishR2RML button is fired.
     *
     * @param event the action event.
     */
    public void publishR2rmlFired(ActionEvent event) throws IOException, Exception {
        // Mostrar uma mensagem indicando a criação das procedures no banco
    }

    /**
     * Called when the genExpOnto button is fired.
     *
     * @param event the action event.
     */
    public void genExpOntoFired(ActionEvent event) throws IOException, Exception {
        tabPane.getTabs().get(2).setDisable(false);
        tabPane.getSelectionModel().select(2);

        // Crio o nó pai que será o nome da ontologia
        TreeItem<String> ontoRoot = new TreeItem<>(mc.getOntologyAlias());

        for (CA ca : assertionsList.getItems()) {
            if (ca instanceof CCA) {
                CCA cca = (CCA) ca;
                Class_ class_ = cca.getClass_();

                Node classIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/ontology/class.gif")));
                TreeItem<String> item = new TreeItem<>(class_.toString(), classIcon);
                assertionsExp.put(item, cca);

                for (DCA dca : cca.getDcaList()) {
                    if (assertionsList.getItems().contains(dca)) {
                        Node datatypePIcon = new ImageView(
                                new Image(getClass().getResourceAsStream("img/ontology/datatypeP.gif")));
                        TreeItem<String> subItem = new TreeItem<>(dca.getdProperty().toString(), datatypePIcon);
                        item.getChildren().add(subItem);
                        assertionsExp.put(subItem, dca);
                    }
                }

                for (OCA oca : cca.getOcaList()) {
                    if (assertionsList.getItems().contains(oca)) {
                        Node objectPIcon = new ImageView(
                                new Image(getClass().getResourceAsStream("img/ontology/objectP.gif")));
                        TreeItem<String> subItem = new TreeItem<>(oca.getoProperty().toString(), objectPIcon);
                        item.getChildren().add(subItem);
                        assertionsExp.put(subItem, oca);
                    }
                }

                ontoRoot.getChildren().add(item);
            }
        }

        // Insiro o nó raiz na TreeView
        expOntoTree.setRoot(ontoRoot);
        expOntoTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Called when the CreateR2RML button is fired.
     *
     * @param event the action event.
     */
    public void createSqlViewsFired(ActionEvent event) throws IOException, Exception {
        tabPane.getTabs().get(3).setDisable(false);
        tabPane.getSelectionModel().select(3);
        
        sqlViews.getEngine()
                .loadContent("<pre>" + "INCLUIR AQUI AS TRIGGERS" + "</pre>");
    }

    /**
     * Called when the CreateR2RML button is fired.
     *
     * @param event the action event.
     */
    public void createR2rmlViewsFired(ActionEvent event) throws IOException, Exception {
        tabPane.getTabs().get(4).setDisable(false);
        tabPane.getSelectionModel().select(4);

        r2rmlContent.getEngine()
                .loadContent("<pre>" + "INCLUIR AQUI AS PROCEDURES" + "</pre>");
    }

    private void tratarEventoMcTable() {
        mcTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    updateDeleteButtonState();
                    updateEditButtonState();
                    if (mouseEvent.getClickCount() == 2) {
                        final MappingConfigurationEntry selectedItem = mcTable.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) {
                            if (mc != null && selectedItem.getId() == mc.getId()) {
                                buildDBTree(mc);
                                buildOntoTree(mc);
                                tabPane.getSelectionModel().select(0);
                            } else {
                                try {
                                    mc = mcDAO.findById(selectedItem.getId());

                                    dataAssertions.clear();
                                    createR2rml.setDisable(true);
                                    tabPane.getTabs().get(2).setDisable(true);
                                    dbTree.setRoot(null);
                                    ontoTree.setRoot(null);
                                    assertions.clear();
                                    mapTableFks.clear();
                                    mapTableFksInv.clear();
                                    mapFks.clear();
                                    txtAssertion.setText("");
                                    lblAssertion.setText("Correspondence Assertion (CA):");
                                    assertions.clear();
                                    classes.clear();
                                    dbMap.clear();
                                    mapTableCols.clear();
                                    mapPrefixes.clear();
                                    buildDBTree(mc);
                                    buildOntoTree(mc);
                                    tabPane.getTabs().get(1).setDisable(true);
                                    tabPane.getTabs().get(2).setDisable(true);
                                    tabPane.getTabs().get(3).setDisable(true);
                                    tabPane.getTabs().get(4).setDisable(true);
                                    tabPane.getSelectionModel().select(0);




                                } catch (SQLException ex) {
                                    Logger.getLogger(MainController.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void tratarEventoOntoTree() {
        ontoTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    final TreeItem selectedItem = ontoTree.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        CA ca = assertions.get(selectedItem);
                        txtAssertion.setText(ca.toString());
                        if (ca instanceof CCA) {
                            lblAssertion.setText("Class Correspondence Assertion (CCA):");
                        } else if (ca instanceof DCA) {
                            lblAssertion.setText("Datatype property Correspondence Assertion (DCA):");
                        } else if (ca instanceof OCA) {
                            lblAssertion.setText("Object property Correspondence Assertion (OCA):");
                        } else {
                            lblAssertion.setText("Correspondence Assertion (CA):");
                        }
                    } else {
                        txtAssertion.setText("");
                        lblAssertion.setText("Correspondence Assertion (CA):");
                    }
                    if (mouseEvent.getClickCount() == 2) {
                        //TODO
                    }
                }
            }
        });
    }

    private void tratarEventoExpOntoTree() {
        expOntoTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    final TreeItem selectedItem = expOntoTree.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        txtAssertionExp.setText(assertionsExp.get(selectedItem).toString());
                    }
                }
            }
        });
    }

    private void tratarEventoDbTree() {
        dbTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    ObservableList<TreeItem<String>> dbItens = dbTree.getSelectionModel().getSelectedItems();
                    if (dbItens != null) {
                        TreeItem ontoItem = ontoTree.getSelectionModel().getSelectedItem();
                        CA ca = assertions.get(ontoItem);

                        int iAtt = 0;
                        for (Iterator<TreeItem<String>> it = dbItens.iterator(); it.hasNext();) {
                            TreeItem<String> dbItem = it.next();
                            Object o = dbMap.get(dbItem);

                            if (o instanceof RelationName) {
                                RelationName rn = (RelationName) o;
                                ca.setRelationName(rn.tableName());

                                if (ca instanceof PCA) {
                                    PCA pca = (PCA) ca;
                                    pca.setFks(new ArrayList<String>());
                                }
                            } else if (o instanceof Attribute) {
                                Attribute att = (Attribute) o;
                                String txtGrandParent = dbItem.getParent().getParent().getValue();
                                if (ca instanceof CCA) {
                                    if (!txtGrandParent.startsWith("fk")) {
                                        CCA cca = (CCA) ca;
                                        if (iAtt == 0) {
                                            cca.getAttributes().clear();
                                            iAtt++;
                                        }
                                        if (!cca.getAttributes().contains(att.attributeName())) {
                                            cca.getAttributes().add(att.attributeName());
                                        }
                                    }
                                } else if (ca instanceof DCA) {
                                    DCA dca = (DCA) ca;
                                    if (iAtt == 0) {
                                        dca.getAttributes().clear();
                                        iAtt++;
                                    }
                                    if (!dca.getAttributes().contains(att.attributeName())) {
                                        dca.getAttributes().add(att.attributeName());
                                    }
                                }
                                if (!txtGrandParent.startsWith("fk")) {
                                    ca.setRelationName(dbItem.getParent().getValue());
                                }
                            } else if (o instanceof Join) {
                                if (ca instanceof PCA) {
                                    PCA pca = (PCA) ca;
                                    if (!pca.getFks().contains(dbItem.getValue())) {
                                        if (!pca.getFks().contains(dbItem.getParent().getParent().getValue())) {
                                            pca.getFks().clear();
                                            if (pca instanceof DCA) {
                                                ((DCA) pca).getAttributes().clear();
                                            }

                                            // Adicionando todas as Fks da hierarquia
                                            int i = 0;
                                            List<String> fksHierarquy = new ArrayList<String>();
                                            TreeItem<String> currItem = dbItem;
                                            while (true) {
                                                if (i % 2 == 1) {
                                                    if (!currItem.getParent().getValue().startsWith("fk")) {
                                                        pca.setRelationName(currItem.getValue());
                                                        break;
                                                    }
                                                } else {
                                                    fksHierarquy.add(currItem.getValue());
                                                }
                                                i++;
                                                currItem = currItem.getParent();
                                            }

                                            i = fksHierarquy.size() - 1;
                                            while (i >= 0) {
                                                pca.getFks().add(fksHierarquy.get(i));
                                                i--;
                                            }
                                        } else {
                                            pca.getFks().add(dbItem.getValue());
                                        }
                                    }
                                }

                                if (mouseEvent.getClickCount() == 2 && dbItem.getChildren().size() == 0) {
                                    Join fk = (Join) o;
                                    RelationName refTable = dbItem.getValue().startsWith("fk0") ? fk.table2() : fk.table1();
                                    Node tableIcon = new ImageView(
                                            new Image(getClass().getResourceAsStream("img/database/table.gif")));
                                    TreeItem<String> tableRefItem = new TreeItem<>(refTable.tableName(), tableIcon);
                                    dbItem.getChildren().add(tableRefItem);

                                    List<Attribute> cols = mapTableCols.get(refTable.tableName().toLowerCase());
                                    for (Attribute column : cols) {
                                        Node attIcon = new ImageView(
                                                new Image(getClass().getResourceAsStream("img/database/attribute.gif")));
                                        TreeItem<String> colItem = new TreeItem<>(column.attributeName(), attIcon);
                                        tableRefItem.getChildren().add(colItem);
                                        dbMap.put(colItem, column);
                                    }

                                    List<Join> fks = mapTableFks.get(refTable.tableName());
                                    for (Join join : fks) {
                                        String tName = join.table2().tableName();
                                        TreeItem<String> currItem = tableRefItem;
                                        boolean include = true;
                                        while (currItem != null) {
                                            if (tName.equals(currItem.getValue())) {
                                                include = false;
                                                break;
                                            }
                                            currItem = currItem.getParent();
                                        }

                                        if (include) {
                                            String fkName = "fk0_" + join.table1() + "2" + join.table2();
                                            Node fkIcon = new ImageView(
                                                    new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                                            TreeItem<String> fk0Item = new TreeItem<>(fkName, fkIcon);
                                            tableRefItem.getChildren().add(fk0Item);
                                            dbMap.put(fk0Item, join);
                                        }
                                    }

                                    List<Join> fksInv = mapTableFksInv.get(refTable.tableName());
                                    for (Join joinInv : fksInv) {
                                        String tName = joinInv.table1().tableName();
                                        TreeItem<String> currItem = tableRefItem;
                                        boolean include = true;
                                        while (currItem != null) {
                                            if (tName.equals(currItem.getValue())) {
                                                include = false;
                                                break;
                                            }
                                            currItem = currItem.getParent();
                                        }

                                        if (include) {
                                            String fkName = "fk1_" + joinInv.table1() + "2" + joinInv.table2();
                                            Node fkIcon = new ImageView(
                                                    new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                                            TreeItem<String> fk1Item = new TreeItem<>(fkName, fkIcon);
                                            tableRefItem.getChildren().add(fk1Item);
                                            dbMap.put(fk1Item, joinInv);
                                        }
                                    }
                                }
                            }
                        }

                        if (ca != null) {
                            txtAssertion.setText(ca.toString());
                        }
                    }
                }
            }
        });
    }

    private void tratarEventoAssertionsList() {
        assertionsList.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.DELETE) {
                    dataAssertions.remove(assertionsList.getSelectionModel().getSelectedIndex());
                    if (dataAssertions.size() == 0) {
                        tabPane.getTabs().get(1).setDisable(true);
                        tabPane.getTabs().get(2).setDisable(true);
                        tabPane.getTabs().get(3).setDisable(true);
                        tabPane.getTabs().get(4).setDisable(true);
                        tabPane.getSelectionModel().select(0);
                    }
                }
            }
        });
    }

    private void createDpItem(TreeItem<String> item, DataProperty dp, CCA cca) {
        Node datatypePIcon = new ImageView(
                new Image(getClass().getResourceAsStream("img/ontology/datatypeP.gif")));
        TreeItem<String> subItem = new TreeItem<>(dp.toString(), datatypePIcon);
        item.getChildren().add(subItem);

        DCA dca = new DCA();
        dca.setdProperty(dp);
        assertions.put(subItem, dca);
        cca.getDcaList().add(dca);
    }

    private void createOpItem(TreeItem<String> item, ObjProperty op, CCA cca) {
        Node objectPIcon = new ImageView(
                new Image(getClass().getResourceAsStream("img/ontology/objectP.gif")));
        TreeItem<String> subItem = new TreeItem<>(op.toString(), objectPIcon);
        item.getChildren().add(subItem);

        OCA oca = new OCA();
        oca.setoProperty(op);
        assertions.put(subItem, oca);
        cca.getOcaList().add(oca);
    }

    private boolean isIn(Attribute attribute, List<Attribute> listPk) {
        String attName = attribute.attributeName().toLowerCase();
        for (Attribute attributePk : listPk) {
            String attPkName = attributePk.attributeName().toLowerCase();
            if (attName.equals(attPkName)) {
                return true;
            }
        }

        return false;
    }
}