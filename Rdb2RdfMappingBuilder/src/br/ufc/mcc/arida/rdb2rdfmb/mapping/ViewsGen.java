/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufc.mcc.arida.rdb2rdfmb.mapping;

import br.ufc.mcc.arida.rdb2rdfmb.model.AttAlias;
import br.ufc.mcc.arida.rdb2rdfmb.model.CA;
import br.ufc.mcc.arida.rdb2rdfmb.model.CCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.DCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.DataProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.Fk;
import br.ufc.mcc.arida.rdb2rdfmb.model.MappingConfiguration;
import br.ufc.mcc.arida.rdb2rdfmb.model.OCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.ObjProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.Pair;
import br.ufc.mcc.arida.rdb2rdfmb.model.Property;
import br.ufc.mcc.arida.rdb2rdfmb.model.TableAtt;
import de.fuberlin.wiwiss.d2rq.algebra.Attribute;
import de.fuberlin.wiwiss.d2rq.algebra.Join;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.ListView;
import rdb2rdfmappingbuilder.TemplateUtil;

/**
 *
 * @author Luis
 */
public class ViewsGen {

    public static List<String> buildViews(ListView<CA> assertionsList, HashMap<String, Fk> mapFks, MappingConfiguration mc) throws Exception {
        List<String> listViews = new ArrayList<>();

        for (CA ca : assertionsList.getItems()) {
            if (ca instanceof CCA) {
                CCA cca = (CCA) ca;
                String viewName = cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + "_view";
                List<String> tables = new ArrayList<>();
                List<AttAlias> atts = new ArrayList<>();
                List<TableAtt> parentAtts = new ArrayList<>();
                List<Pair> pairs = new ArrayList<>();

                Map<String, Object> param = new HashMap<>();
                param.put("viewName", viewName);
                param.put("childTable", cca.getRelationName());
                param.put("tables", tables);
                param.put("atts", atts);
                param.put("parentAtts", parentAtts);
                param.put("pairs", pairs);
                param.put("filter", cca.getSelCondition());
                param.put("db", mc.getDatabaseDriver());

                tables.add(cca.getRelationName());
                int i = 1;
                for (String att : cca.getAttributes()) {
                    String index = (i == 1 ? "" : "" + i);
                    atts.add(new AttAlias(att, "ID" + index));
                    i++;
                }

                for (DCA dca : cca.getDcaList()) {
                    if (assertionsList.getItems().contains(dca)) {
                        DataProperty dp = dca.getdProperty();
                        if (dp.getMaxCardinality() == 1) {
                            if (dca.getFks().isEmpty()) {
                                i = 1;
                                for (String att : dca.getAttributes()) {
                                    String index = (i == 1 ? "" : "" + i);
                                    atts.add(new AttAlias(att, dp.getPrefix() + "_" + dp.getName() + index));
                                    i++;
                                }
                            } else {
                                addTablesJoinsDP(dca, mapFks, tables, pairs, parentAtts, dp);
                            }
                        } else {
                            // Create another view
                            List<String> tables2 = new ArrayList<>();
                            List<AttAlias> atts2 = new ArrayList<>();
                            List<TableAtt> parentAtts2 = new ArrayList<>();
                            List<Pair> pairs2 = new ArrayList<>();

                            Map<String, Object> param2 = new HashMap<>();
                            setParamsUri(param2, cca, dp, dca, tables2, atts2, parentAtts2, pairs2, mc);

                            addTablesJoinsDP(dca, mapFks, tables2, pairs2, parentAtts2, dp);

                            if (cca.getSelCondition() == null) {
                                listViews.add(TemplateUtil.applyTemplate("views/pathView", param2));
                            } else {
                                listViews.add(TemplateUtil.applyTemplate("views/pathFilterView", param2));
                            }
                        }
                    }
                }

                for (OCA oca : cca.getOcaList()) {
                    if (assertionsList.getItems().contains(oca)) {
                        ObjProperty op = oca.getoProperty();
                        String alias = op.getPrefix() + "_" + op.getName();
                        if (op.getMaxCardinality() == 1) {
                            if (oca.getFks().size() == 1) {
                                String fkStr = oca.getFks().get(0);
                                Fk fk = mapFks.get(fkStr);
                                if (!fk.isInverse()) {
                                    Join j = fk.getJoin();
                                    i = 0;
                                    while (i < j.attributes1().size()) {
                                        Attribute a1 = (Attribute) j.attributes1().get(i);
                                        atts.add(new AttAlias(a1.attributeName(), alias));
                                        alias += (i + 2);
                                        i++;
                                    }
                                }
                            } else {
                                // Add tables and joins
                                addTablesJoinsOP(oca, mapFks, tables, pairs, parentAtts, op);
                            }
                        } else {
                            // Create another view
                            List<String> tables2 = new ArrayList<>();
                            List<AttAlias> atts2 = new ArrayList<>();
                            List<TableAtt> parentAtts2 = new ArrayList<>();
                            List<Pair> pairs2 = new ArrayList<>();

                            Map<String, Object> param2 = new HashMap<>();
                            setParamsUri(param2, cca, op, oca, tables2, atts2, parentAtts2, pairs2, mc);
                            addTablesJoinsOP(oca, mapFks, tables2, pairs2, parentAtts2, op);

                            if (cca.getSelCondition() == null) {
                                listViews.add(TemplateUtil.applyTemplate("views/pathView", param2));
                            } else {
                                listViews.add(TemplateUtil.applyTemplate("views/pathFilterView", param2));
                            }
                        }
                    }
                }

                if (parentAtts.isEmpty()) {
                    if (cca.getSelCondition() == null) {
                        listViews.add(TemplateUtil.applyTemplate("views/simpleView", param));
                    } else {
                        listViews.add(TemplateUtil.applyTemplate("views/filterView", param));
                    }
                } else {
                    if (cca.getSelCondition() == null) {
                        listViews.add(TemplateUtil.applyTemplate("views/pathView", param));
                    } else {
                        listViews.add(TemplateUtil.applyTemplate("views/pathFilterView", param));
                    }
                }
            }
        }

        return listViews;
    }

    private static void addTablesJoinsDP(DCA dca, HashMap<String, Fk> mapFks, List<String> tables,
            List<Pair> pairs, List<TableAtt> parentAtts, DataProperty dp) {
        // Add tables, join pairs
        for (String fkStr : dca.getFks()) {
            Fk fk = mapFks.get(fkStr);
            Join j = fk.getJoin();
            int i = 0;
            while (i < j.attributes1().size()) {
                Pair p;
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

        // Add atts from last table
        String alias = dp.getPrefix() + "_" + dp.getName();
        int i = 1;
        for (String att : dca.getAttributes()) {
            TableAtt ta = new TableAtt(tables.get(tables.size() - 1), new AttAlias(att, alias));
            parentAtts.add(ta);
            alias += ++i;
        }
    }

    private static void addTablesJoinsOP(OCA oca, HashMap<String, Fk> mapFks, List<String> tables,
            List<Pair> pairs, List<TableAtt> parentAtts, ObjProperty op) {
        String alias = "ID_" + op.getRangeName();
        int k = 0;
        for (String fkStr : oca.getFks()) {
            k++;
            boolean isLastFk = (k == oca.getFks().size());
            Fk fk = mapFks.get(fkStr);
            Join j = fk.getJoin();
            int i = 0;
            while (i < j.attributes1().size()) {
                Pair p;
                Attribute a1 = (Attribute) j.attributes1().get(i);
                Attribute a2 = (Attribute) j.attributes2().get(i);

                if (isLastFk) {
                    TableAtt ta = new TableAtt(a1.tableName(), new AttAlias(a1.attributeName(), alias));
                    parentAtts.add(ta);
                    alias += (i + 2);
                }
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
    }

    private static void setParamsUri(Map<String, Object> param2, 
            CCA cca, Property p, CA ca, List<String> tables2, 
            List<AttAlias> atts2, List<TableAtt> parentAtts2, 
            List<Pair> pairs2, MappingConfiguration mc) {
        param2.put("viewName", cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + "_"
                + p.getPrefix() + "_" + p.getName() + "_view");
        param2.put("childTable", ca.getRelationName());
        param2.put("tables", tables2);
        param2.put("atts", atts2);
        param2.put("parentAtts", parentAtts2);
        param2.put("pairs", pairs2);
        param2.put("filter", cca.getSelCondition());
        param2.put("db", mc.getDatabaseDriver());

        tables2.add(cca.getRelationName());
        int i = 1;
        for (String att : cca.getAttributes()) {
            String index = (i == 1 ? "" : "" + i);
            atts2.add(new AttAlias(att, "ID_" + cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + index));
            i++;
        }
    }
}