/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rdb2rdfmappingbuilder;

import br.ufc.mcc.arida.rdb2rdfmb.model.CA;
import br.ufc.mcc.arida.rdb2rdfmb.model.CCA;
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
import javafx.scene.control.TreeItem;

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

        txtFilter.setText("");

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
        TreeItem<String> nodeSel = MainController.m.ontoTree.getSelectionModel().getSelectedItem();
        CA ca = MainController.m.assertions.get(nodeSel);
        
        if (ca instanceof CCA) {
            ((CCA) ca).setSelCondition(txtFilter.getText());
        }
        
        MainController.m.txtAssertion.setText(ca.toString());
        MainController.newFilterStage.close();
    }

    /**
     * Called when the ADD button is fired.
     *
     * @param event the action event.
     */
    public void addFired(ActionEvent event) throws IOException {
        String colType[] = cbxColumn.getValue().split(" ");
        colType[0] = lblTable.getText().substring(7) + "." + colType[0];
        String value = txtValue.getText();

        if (colType[1].toUpperCase().indexOf("CHAR") > -1 || colType[1].toUpperCase().indexOf("TEXT") > -1
                || colType[1].toUpperCase().indexOf("DATE") > -1 || colType[1].toUpperCase().indexOf("TIME") > -1) {
            value = "'" + value + "'";
        }

        String filter = txtFilter.getText();

        if ("".equals(filter.trim())) {
            filter = colType[0] + " " + cbxOperator.getValue() + " " + value;
        } else {
            if (filter.trim().endsWith("AND") || filter.trim().endsWith("OR") || filter.trim().endsWith("(")) {
                filter += colType[0] + " " + cbxOperator.getValue() + " " + value;
            } else {
                filter += " AND " + colType[0] + " " + cbxOperator.getValue() + " " + value;
            }
        }

        txtFilter.setText(filter);
    }

    /**
     * Called when the ( button is fired.
     *
     * @param event the action event.
     */
    public void openParenthesisFired(ActionEvent event) throws IOException {
        String filter = txtFilter.getText();

        if ("".equals(filter.trim())) {
            txtFilter.setText("(");
        } else {
            if (filter.trim().endsWith(")")) {
                filter += " AND\n(";
            } else if (filter.trim().endsWith("AND") || filter.trim().endsWith("OR")) {
                filter += "\n(";
            }

            txtFilter.setText(filter);
        }
    }

    /**
     * Called when the ) button is fired.
     *
     * @param event the action event.
     */
    public void closeParenthesisFired(ActionEvent event) throws IOException {
        String filter = txtFilter.getText();
        if (!"".equals(filter.trim()) 
                && !filter.trim().endsWith("(")
                && !filter.trim().endsWith("AND")
                && !filter.trim().endsWith("OR") && hasOpenedParenthesis(filter)) {
            txtFilter.setText(filter + ")");
        }
    }

    /**
     * Called when the AND button is fired.
     *
     * @param event the action event.
     */
    public void andFired(ActionEvent event) throws IOException {
        String filter = txtFilter.getText();
        if (!"".equals(filter.trim()) && !filter.trim().endsWith("AND")
                && !filter.trim().endsWith("(")
                && !filter.trim().endsWith("OR")) {
            txtFilter.setText(filter + " AND ");
        }
    }

    /**
     * Called when the OR button is fired.
     *
     * @param event the action event.
     */
    public void orFired(ActionEvent event) throws IOException {
        String filter = txtFilter.getText();
        if (!"".equals(filter.trim()) && !filter.trim().endsWith("AND")
                && !filter.trim().endsWith("(")
                && !filter.trim().endsWith("OR")) {
            txtFilter.setText(filter + " OR ");
        }
    }

    private boolean hasOpenedParenthesis(String filter) {
        boolean ret = false;
        for (int i = 0; i < filter.length(); i++) {
            if (filter.charAt(i) == '(') {
                ret = true;
            } else if (filter.charAt(i) == ')' && ret) {
                ret = false;
            }
        }

        return ret;
    }
}