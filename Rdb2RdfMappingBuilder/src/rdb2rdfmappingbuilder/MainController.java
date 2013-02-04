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
import br.ufc.mcc.arida.rdb2rdfmb.model.OCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.ObjProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.PCA;
import de.fuberlin.wiwiss.d2rq.algebra.Join;
import java.util.ArrayList;
import java.util.Iterator;
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
    public static final Stage secondaryStage = new Stage(StageStyle.UTILITY);
    public static MainController m;
    ObservableList<MappingConfigurationEntry> dataMc = FXCollections.observableArrayList();
    ObservableList<CA> dataAssertions = FXCollections.observableArrayList();
    MappingConfigurationDAO mcDAO = new MappingConfigurationDAO();
    HashMap<TreeItem, CA> assertions = new HashMap<TreeItem, CA>();
    HashMap<String, Class_> classes = new HashMap<String, Class_>();
    HashMap<TreeItem, Object> dbMap = new HashMap<TreeItem, Object>();
    HashMap<String, List<Attribute>> mapTableCols = new HashMap<String, List<Attribute>>();
    HashMap<String, List<Join>> mapTableFks = new HashMap<String, List<Join>>();
    HashMap<String, List<Join>> mapTableFksInv = new HashMap<String, List<Join>>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert newMapping != null : "fx:id=\"newMapping\" was not injected: check your FXML file 'main.fxml'.";
        m = this;

        itemDateCreation.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("creationDate"));
        itemDbName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("databaseAlias"));
        itemOntoName.setCellValueFactory(new PropertyValueFactory<MappingConfigurationEntry, String>("ontologyAlias"));

        mcTable.setItems(dataMc);
        assertionsList.setItems(dataAssertions);

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

                /* Cria os data properties da classe */
                for (int i = 0; i < class_.getdProperties().size(); i++) {
                    Node datatypePIcon = new ImageView(
                            new Image(getClass().getResourceAsStream("img/ontology/datatypeP.gif")));
                    TreeItem<String> subItem = new TreeItem<>(class_.getdProperties().get(i).getName(), datatypePIcon);
                    item.getChildren().add(subItem);

                    DCA dca = new DCA();
                    dca.setPrefixName(mc.getOntologyAlias().toLowerCase());
                    dca.setdProperty(class_.getdProperties().get(i));
                    assertions.put(subItem, dca);
                }

                /* Cria os object properties da classe */
                for (int i = 0; i < class_.getoProperties().size(); i++) {
                    Node objectPIcon = new ImageView(
                            new Image(getClass().getResourceAsStream("img/ontology/objectP.gif")));
                    TreeItem<String> subItem = new TreeItem<>(class_.getoProperties().get(i).getName(), objectPIcon);
                    item.getChildren().add(subItem);

                    OCA oca = new OCA();
                    oca.setPrefixName(mc.getOntologyAlias().toLowerCase());
                    oca.setoProperty(class_.getoProperties().get(i));
                    assertions.put(subItem, oca);
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
        List<RelationName> tables = schema.listTableNames();
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
                String fkName = "fk_" + fk0.table1() + "2" + fk0.table2();
                Node fkIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                fkIcon.setId("FK_TO_" + fk0.table1() + "2" + fk0.table2());
                TreeItem<String> fk0Item = new TreeItem<>(fkName, fkIcon);
                treeItem.getChildren().add(fk0Item);
                dbMap.put(fk0Item, fk0);
            }
            mapTableFks.put(relationName.tableName(), listFks0);

            List<Join> listFks1 = schema.foreignKeys(relationName, 1);
            for (Join fk1 : listFks1) {
                String fkName = "";
                fkName = "fk_" + fk1.table1() + "2" + fk1.table2();
                Node fkIcon = new ImageView(
                        new Image(getClass().getResourceAsStream("img/database/fkInv.gif")));
                fkIcon.setId("FK_FROM_" + fk1.table1() + "2" + fk1.table2());
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
                            MappingConfiguration mc;
                            try {
                                mc = mcDAO.findById(selectedItem.getId());

                                txtAssertion.setText("");
                                assertions.clear();
                                classes.clear();
                                dbMap.clear();
                                mapTableCols.clear();
                                buildDBTree(mc);
                                buildOntoTree(mc);
                            } catch (SQLException ex) {
                                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
                                    String txtItem = dbItem.getGraphic().getId();
                                    RelationName refTable = txtItem.startsWith("FK_TO") ? fk.table2() : fk.table1();
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
                                            String fkName = "fk_" + join.table1() + "2" + join.table2();
                                            Node fkIcon = new ImageView(
                                                    new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                                            fkIcon.setId("FK_TO_" + join.table1() + "2" + join.table2());
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
                                            String fkName = "fk_" + joinInv.table1() + "2" + joinInv.table2();
                                            Node fkIcon = new ImageView(
                                                    new Image(getClass().getResourceAsStream("img/database/fk.gif")));
                                            fkIcon.setId("FK_TO_" + joinInv.table1() + "2" + joinInv.table2());
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
                }
            }
        });
    }
}
