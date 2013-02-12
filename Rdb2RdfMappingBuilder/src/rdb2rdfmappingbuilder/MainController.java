/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.db.DbConnection;
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
import br.ufc.mcc.arida.rdb2rdfmb.model.Pair;
import d2rq.server;
import de.fuberlin.wiwiss.d2rq.algebra.Join;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
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
    private TreeView<String> ontoTree;
    @FXML
    TextField txtAssertion;
    @FXML
    Button newMapping;
    @FXML
    Button edit;
    @FXML
    Button delete;
    @FXML
    Button createR2rml;
    @FXML
    TableView<MappingConfigurationEntry> mcTable;
    @FXML
    TableColumn itemOntoName;
    @FXML
    TableColumn itemDbName;
    @FXML
    TableColumn itemDateCreation;
    @FXML
    ListView<CA> assertionsList;
    @FXML
    TabPane tabPane;
    @FXML
    WebView r2rmlContent;
    public static final Stage secondaryStage = new Stage(StageStyle.UTILITY);
    public static MainController m;
    ObservableList<MappingConfigurationEntry> dataMc = FXCollections.observableArrayList();
    ObservableList<CA> dataAssertions = FXCollections.observableArrayList();
    MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();
    HashMap<TreeItem, CA> assertions = new HashMap<TreeItem, CA>();
    HashMap<Class_, CCA> mapClassAssertion = new HashMap<Class_, CCA>();
    HashMap<String, Class_> classes = new HashMap<String, Class_>();
    HashMap<TreeItem, Object> dbMap = new HashMap<TreeItem, Object>();
    HashMap<String, List<Attribute>> mapTableCols = new HashMap<String, List<Attribute>>();
    HashMap<String, List<Join>> mapTableFks = new HashMap<String, List<Join>>();
    HashMap<String, List<Join>> mapTableFksInv = new HashMap<String, List<Join>>();
    HashMap<String, Fk> mapFks = new HashMap<String, Fk>();
    StringBuilder r2rml = new StringBuilder("");
    MappingConfiguration mc = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";
        m = this;

        itemDateCreation.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("creationDate"));
        itemDbName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("databaseAlias"));
        itemOntoName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("ontologyAlias"));

        mcTable.setItems(dataMc);
        assertionsList.setItems(dataAssertions);

        if (dataAssertions.size() == 0) {
            createR2rml.setDisable(true);
        } else {
            createR2rml.setDisable(false);
        }

        final ObservableList<MappingConfigurationEntry> tableSelection = mcTable.getSelectionModel().getSelectedItems();

        tratarEventoMcTable();
        tratarEventoOntoTree();
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

    public void buildOntoTree(MappingConfiguration mc) {
        try {
            loadOntology(mc);

            // Crio o nó pai que será o nome da ontologia
            TreeItem<String> ontoRoot = new TreeItem<>(mc.getOntologyAlias());

            // Crio nós filhos com os nomes das classes
            Collection<Class_> cClasses = classes.values();
            for (Class_ class_ : cClasses) {
                Node classIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/ontology/class.gif")));
                TreeItem<String> item = new TreeItem<>(class_.getName(), classIcon);
                CCA cca = new CCA();
                cca.setPrefixName(mc.getOntologyAlias().toLowerCase());
                cca.setClass_(class_);
                assertions.put(item, cca);
                mapClassAssertion.put(class_, cca);

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
            TreeItem<String> treeItem = new TreeItem<>(relationName.tableName(), tableIcon);
            dataBase.getChildren().add(treeItem);
            dbMap.put(treeItem, relationName);

            List<Attribute> listCols = schema.listColumns(relationName);
            for (Attribute attribute : listCols) {
                TreeItem<String> colItem = colItem = new TreeItem<>(attribute.attributeName());
                List<Attribute> listPk = schema.primaryKeyColumns(relationName);
                if (listPk.contains(attribute)) {
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
            mapTableCols.put(relationName.tableName(), listCols);

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
                String fkName = "";
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

            if (datatypeProperty.getDomain() != null && datatypeProperty.getRange() != null) {
                String dClassName = datatypeProperty.getDomain().getLocalName();
                Class_ dClass = classes.get(dClassName);
                String dpName = datatypeProperty.getLocalName();
                String rangeName = datatypeProperty.getRange().getLocalName();

                DataProperty dp = new DataProperty(dpName, dClass, rangeName);
                if (dClass != null) {
                    dClass.getdProperties().add(dp);
                }
            }
        }

        ExtendedIterator<com.hp.hpl.jena.ontology.ObjectProperty> i3 = ontModel.listObjectProperties();
        while (i3.hasNext()) {
            ObjectProperty objectProperty = (ObjectProperty) i3.next();

            if (objectProperty.getDomain() != null && objectProperty.getRange() != null) {
                String dClassName = objectProperty.getDomain().getLocalName();
                String rClassName = objectProperty.getRange().getLocalName();
                String opName = objectProperty.getLocalName();

                Class_ dClass = classes.get(dClassName);
                Class_ rClass = classes.get(rClassName);

                ObjProperty op = new ObjProperty(opName, dClass, rClass);
                if (dClass != null) {
                    dClass.getoProperties().add(op);
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

        tabPane.getSelectionModel().select(1);
        assertionsList.getSelectionModel().select(ca);

        createR2rml.setDisable(false);
    }

    /**
     * Called when the PublishR2RML button is fired.
     *
     * @param event the action event.
     */
    public void publishR2rmlFired(ActionEvent event) throws IOException, Exception {
        File f = new File("r2rml.ttl");
        FileUtils.writeStringToFile(f, r2rml.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">"));

        server s = new server();
        try {
            s.process(new String[]{"-u", mc.getDatabaseUser(), "-pass", mc.getDatabasePassword(), "-d", DbConnection.getDriverClass(mc.getDatabaseDriver()), "-j", mc.getDatabaseUrl(), "r2rml.ttl"});
        } catch (Exception e) {
            e.printStackTrace();
        }

        URI u = new URI("http://localhost:2020");
        Desktop.getDesktop().browse(u);
    }

    /**
     * Called when the CreateR2RML button is fired.
     *
     * @param event the action event.
     */
    public void createR2rmlFired(ActionEvent event) throws IOException, Exception {
        tabPane.getTabs().get(2).setDisable(false);
        tabPane.getSelectionModel().select(2);
        String baseUri = "".equals(mc.getOntologyURL()) ? "http://www.example.com#" : mc.getOntologyURL();

        r2rml = new StringBuilder("");
        r2rml.append("@prefix rr: &lt;http://www.w3.org/ns/r2rml#&gt; .\n");
        r2rml.append("@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .\n");
        r2rml.append("@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .\n");
        r2rml.append("@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .\n");
        r2rml.append("@prefix "
                + mc.getOntologyAlias().toLowerCase()
                + ": &lt;" + (baseUri)
                + "&gt; .");

        for (CA ca : assertionsList.getItems()) {
            if (ca instanceof CCA) {
                CCA cca = (CCA) ca;
                List<DCA> dcaViews = new ArrayList<DCA>();
                List<OCA> ocaViews = new ArrayList<OCA>();

                Map<String, Object> param = new HashMap<String, Object>();
                param.put("className", cca.getClass_());
                param.put("prefixClass", cca.getPrefixName());
                param.put("classUri", cca.getClass_().toString().toLowerCase());
                param.put("table", cca.getRelationName());
                param.put("atts", cca.getAttributes());

                r2rml.append(TemplateUtil.applyTemplate("subjectMap", param));

                for (DCA dca : cca.getDcaList()) {
                    if (assertionsList.getItems().contains(dca)) {
                        if (dca.getFks().size() > 0) {
                            dcaViews.add(dca);
                            continue;
                        }
                        param.clear();
                        param.put("prefix", dca.getPrefixName());
                        param.put("propertyName", dca.getdProperty().getName());
                        param.put("type", 1);
                        param.put("columnName", dca.getAttributes().get(0));

                        r2rml.append(TemplateUtil.applyTemplate("predicateObjectMap", param));
                    }
                }

                for (OCA oca : cca.getOcaList()) {
                    if (assertionsList.getItems().contains(oca)) {
                        if (oca.getFks().size() == 1) {
                            param.clear();
                            param.put("prefix", oca.getPrefixName());
                            param.put("propertyName", oca.getoProperty().getName());
                            param.put("type", 2);
                            param.put("rangeClass", oca.getoProperty().getRange());
                            List<Pair> pairs = new ArrayList<Pair>();
                            String fkStr = oca.getFks().get(0);
                            Fk fk = mapFks.get(fkStr);
                            Join j = fk.getJoin();
                            int i = 0;
                            while (i < j.attributes1().size()) {
                                Pair p = null;
                                Attribute a1 = (Attribute) j.attributes1().get(i);
                                Attribute a2 = (Attribute) j.attributes2().get(i);
                                if (!fk.isInverse()) {
                                    p = new Pair(a1.attributeName(), a2.attributeName());
                                } else {
                                    p = new Pair(a2.attributeName(), a1.attributeName());
                                }

                                pairs.add(p);
                                i++;
                            }
                            param.put("pairs", pairs);
                            r2rml.append(TemplateUtil.applyTemplate("predicateObjectMap", param));
                        } else if (oca.getFks().size() > 1) {
                            ocaViews.add(oca);
                        }
                    }
                }
                int idx = r2rml.lastIndexOf(";");
                r2rml.replace(idx, idx + 1, ".");

                // Criando as Datatype Properties que necessitam de VISÕES
                for (DCA dca : dcaViews) {
                    param.clear();
                    String pName = dca.getdProperty().getName();
                    param.put("propertyName", pName.substring(0, 1).toUpperCase() + pName.substring(1));
                    param.put("subjectAtts", cca.getAttributes());
                    param.put("atts", dca.getAttributes());

                    List<String> tables = new ArrayList<String>();
                    List<Pair> pairs = new ArrayList<Pair>();
                    for (String fkStr : dca.getFks()) {
                        Fk fk = mapFks.get(fkStr);
                        Join j = fk.getJoin();
                        int i = 0;
                        while (i < j.attributes1().size()) {
                            Pair p = null;
                            Attribute a1 = (Attribute) j.attributes1().get(i);
                            Attribute a2 = (Attribute) j.attributes2().get(i);
                            if (!fk.isInverse()) {
                                p = new Pair(a1.attributeName(), a1.tableName(), a2.attributeName(), a2.tableName());
                                if (!tables.contains(a1.tableName())) {
                                    tables.add(a1.tableName());
                                }
                                if (!tables.contains(a2.tableName())) {
                                    tables.add(a2.tableName());
                                }
                            } else {
                                p = new Pair(a2.attributeName(), a2.tableName(), a1.attributeName(), a1.tableName());
                                if (!tables.contains(a2.tableName())) {
                                    tables.add(a2.tableName());
                                }
                                if (!tables.contains(a1.tableName())) {
                                    tables.add(a1.tableName());
                                }
                            }

                            pairs.add(p);
                            i++;
                        }
                    }
                    param.put("pairs", pairs);
                    param.put("tables", tables);
                    param.put("childTable", tables.get(0));
                    param.put("parentTable", tables.get(tables.size() - 1));
                    param.put("domainClassUri", cca.getClass_().getName().toLowerCase());
                    param.put("prefix", dca.getPrefixName());

                    r2rml.append(TemplateUtil.applyTemplate("datatypeKeyPathMap", param));
                }

                // Criando as Object Properties que necessitam de VISÕES
                for (OCA oca : ocaViews) {
                    param.clear();
                    String pName = oca.getoProperty().getName();
                    param.put("propertyName", pName.substring(0, 1).toUpperCase() + pName.substring(1));
                    param.put("subjectAtts", cca.getAttributes());
                    Class_ rangeClass = oca.getoProperty().getRange();
                    CCA rangeCca = mapClassAssertion.get(rangeClass);
                    param.put("objectAtts", rangeCca.getAttributes());

                    List<String> tables = new ArrayList<String>();
                    List<Pair> pairs = new ArrayList<Pair>();
                    for (String fkStr : oca.getFks()) {
                        Fk fk = mapFks.get(fkStr);
                        Join j = fk.getJoin();
                        int i = 0;
                        while (i < j.attributes1().size()) {
                            Pair p = null;
                            Attribute a1 = (Attribute) j.attributes1().get(i);
                            Attribute a2 = (Attribute) j.attributes2().get(i);
                            if (!fk.isInverse()) {
                                p = new Pair(a1.attributeName(), a1.tableName(), a2.attributeName(), a2.tableName());
                                if (!tables.contains(a1.tableName())) {
                                    tables.add(a1.tableName());
                                }
                                if (!tables.contains(a2.tableName())) {
                                    tables.add(a2.tableName());
                                }
                            } else {
                                p = new Pair(a2.attributeName(), a2.tableName(), a1.attributeName(), a1.tableName());
                                if (!tables.contains(a2.tableName())) {
                                    tables.add(a2.tableName());
                                }
                                if (!tables.contains(a1.tableName())) {
                                    tables.add(a1.tableName());
                                }
                            }

                            pairs.add(p);
                            i++;
                        }
                    }
                    param.put("pairs", pairs);
                    param.put("tables", tables);
                    param.put("childTable", tables.get(0));
                    param.put("parentTable", tables.get(tables.size() - 1));
                    param.put("domainClassUri", cca.getClass_().getName().toLowerCase());
                    param.put("prefix", oca.getPrefixName());
                    param.put("rangeClassUri", rangeCca.getClass_().getName().toLowerCase());

                    r2rml.append(TemplateUtil.applyTemplate("objectKeyPathMap", param));
                }
            }
        }

        r2rmlContent.getEngine()
                .loadContent("<pre>" + r2rml.toString() + "</pre>");
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
                            try {
                                mc = mcDAO.findById(selectedItem.getId());

                                dataAssertions.clear();
                                createR2rml.setDisable(true);
                                tabPane.getTabs().get(2).setDisable(true);
                                dbTree.setRoot(null);
                                ontoTree.setRoot(null);
                                assertions.clear();
                                mapClassAssertion.clear();
                                mapTableFks.clear();
                                mapTableFksInv.clear();
                                mapFks.clear();
                                txtAssertion.setText("");
                                assertions.clear();
                                classes.clear();
                                dbMap.clear();
                                mapTableCols.clear();
                                buildDBTree(mc);
                                buildOntoTree(mc);
                                tabPane.getSelectionModel().select(0);
                            } catch (SQLException ex) {
                                Logger.getLogger(MainController.class
                                        .getName()).log(Level.SEVERE, null, ex);
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
                        txtAssertion.setText(assertions.get(selectedItem).toString());
                    } else {
                        txtAssertion.setText("");
                    }
                    if (mouseEvent.getClickCount() == 2) {
                        //TODO
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

                                    List<Attribute> cols = mapTableCols.get(refTable.tableName());
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
                                        String tName = joinInv.table2().tableName();
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
                        createR2rml.setDisable(true);
                        tabPane.getTabs().get(2).setDisable(true);
                    }
                }
            }
        });
    }

    private void createDpItem(TreeItem<String> item, DataProperty dp, CCA cca) {
        Node datatypePIcon = new ImageView(
                new Image(getClass().getResourceAsStream("img/ontology/datatypeP.gif")));
        TreeItem<String> subItem = new TreeItem<>(dp.getName(), datatypePIcon);
        item.getChildren().add(subItem);

        DCA dca = new DCA();
        dca.setPrefixName(mc.getOntologyAlias().toLowerCase());
        dca.setdProperty(dp);
        assertions.put(subItem, dca);
        cca.getDcaList().add(dca);
    }

    private void createOpItem(TreeItem<String> item, ObjProperty op, CCA cca) {
        Node objectPIcon = new ImageView(
                new Image(getClass().getResourceAsStream("img/ontology/objectP.gif")));
        TreeItem<String> subItem = new TreeItem<>(op.getName(), objectPIcon);
        item.getChildren().add(subItem);

        OCA oca = new OCA();
        oca.setPrefixName(mc.getOntologyAlias().toLowerCase());
        oca.setoProperty(op);
        assertions.put(subItem, oca);
        cca.getOcaList().add(oca);
    }
}
