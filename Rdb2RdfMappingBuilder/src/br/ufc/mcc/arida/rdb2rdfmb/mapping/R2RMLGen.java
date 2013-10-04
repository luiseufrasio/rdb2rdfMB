package br.ufc.mcc.arida.rdb2rdfmb.mapping;

import br.ufc.mcc.arida.rdb2rdfmb.model.CA;
import br.ufc.mcc.arida.rdb2rdfmb.model.CCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.Class_;
import br.ufc.mcc.arida.rdb2rdfmb.model.DCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.DataProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.Fk;
import br.ufc.mcc.arida.rdb2rdfmb.model.OCA;
import br.ufc.mcc.arida.rdb2rdfmb.model.ObjProperty;
import br.ufc.mcc.arida.rdb2rdfmb.model.Pair;
import br.ufc.mcc.arida.rdb2rdfmb.model.Property;
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
public class R2RMLGen {

    public static String buildR2RML(ListView<CA> assertionsList, HashMap<String, Fk> mapFks, HashMap<String, String> mapPrefixes) throws Exception {
        StringBuilder r2rml = new StringBuilder("");
        r2rml.append("@prefix rr: &lt;http://www.w3.org/ns/r2rml#&gt; .\n");
        r2rml.append("@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .\n");
        r2rml.append("@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .\n");
        r2rml.append("@prefix xsd: &lt;http://www.w3.org/2001/XMLSchema#&gt; .");

        List<String> prefixes = new ArrayList<>();
        for (CA ca : assertionsList.getItems()) {
            if (ca instanceof CCA) {
                CCA cca = (CCA) ca;
                List<String> specialTriplesMap = new ArrayList<>();
                String prefixClass = cca.getClass_().getPrefix();
                if (!prefixes.contains(prefixClass)) {
                    prefixes.add(prefixClass);
                }
                String viewName = cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + "_view";
                List<String> atts = new ArrayList<>();

                Map<String, Object> param = new HashMap<>();
                param.put("mapName", cca.getClass_().getName());
                param.put("className", cca.getClass_().getName());
                param.put("prefixClass", prefixClass);
                param.put("table", viewName);
                param.put("atts", atts);

                int i = 1;
                while (i <= cca.getAttributes().size()) {
                    String index = (i == 1 ? "" : "" + i);
                    atts.add("ID" + index);
                    i++;
                }

                r2rml.append(TemplateUtil.applyTemplate("r2rml/subjectMap", param));

                for (DCA dca : cca.getDcaList()) {
                    if (assertionsList.getItems().contains(dca)) {
                        DataProperty dp = dca.getdProperty();
                        Map<String, Object> param2 = new HashMap<>();
                        String prefix = dp.getPrefix();
                        if (!prefixes.contains(prefix)) {
                            prefixes.add(prefix);
                        }
                        param2.put("prefix", prefix);
                        param2.put("propertyName", dp.getName());
                        if (dca.getAttributes().size() == 1) {
                            param2.put("type", 1);
                            param2.put("columnName", dp.getPrefix() + "_" + dp.getName());
                        } else {
                            param2.put("type", 3);
                            List<String> cols = new ArrayList<>();
                            i = 1;
                            while (i <= dca.getAttributes().size()) {
                                String index = (i == 1 ? "" : "" + i);
                                cols.add(dp.getPrefix() + "_" + dp.getName() + index);
                                i++;
                            }
                            param2.put("cols", cols);
                        }

                        if (dp.getMaxCardinality() == 1) {
                            r2rml.append(TemplateUtil.applyTemplate("r2rml/predicateObjectMap", param2));
                        } else {
                            // Create another triples map
                            specialTriplesMap.add(createSpecialTriplesMap(param2, cca, dp));
                        }
                    }
                }

                for (OCA oca : cca.getOcaList()) {
                    if (assertionsList.getItems().contains(oca)) {
                        ObjProperty op = oca.getoProperty();
                        Map<String, Object> param2 = new HashMap<>();
                        String prefix = op.getPrefix();
                        if (!prefixes.contains(prefix)) {
                            prefixes.add(prefix);
                        }
                        param2.put("prefix", prefix);
                        param2.put("propertyName", op.getName());
                        param2.put("type", 2);
                        String rangeClass = oca.getoProperty().getRange().getName();
                        param2.put("rangeClass", rangeClass);
                        List<Pair> pairs = new ArrayList<>();
                        param2.put("pairs", pairs);

                        int size = oca.getFks().size();
                        if (size > 0) {
                            String fkStr = oca.getFks().get(size - 1);
                            Fk fk = mapFks.get(fkStr);
                            int qtdeAtts = fk.getJoin().attributes1().size();

                            i = 1;
                            while (i <= qtdeAtts) {
                                String index = (i == 1 ? "" : "" + i);
                                pairs.add(new Pair(op.getPrefix() + "_" + op.getName() + index, "ID" + index));
                                i++;
                            }
                        } else {
                            CCA ccaRange = CCA.getCcaFromClass(assertionsList, op.getRange());
                            int q = ccaRange.getAttributes().size();
                            
                            i = 1;
                            while (i <= q) {
                                String index = (i == 1 ? "" : "" + i);
                                pairs.add(new Pair(op.getPrefix() + "_" + op.getName() + index, "ID" + index));
                                i++;
                            }
                        }

                        if (op.getMaxCardinality() == 1) {
                            r2rml.append(TemplateUtil.applyTemplate("r2rml/predicateObjectMap", param2));
                        } else {
                            // Create another triples map
                            specialTriplesMap.add(createSpecialTriplesMap(param2, cca, op));
                        }
                    }
                }

                int idx = r2rml.lastIndexOf(";");
                r2rml.replace(idx, idx + 1, ".");

                for (String tm : specialTriplesMap) {
                    r2rml.append(tm);
                    idx = r2rml.lastIndexOf(";");
                    r2rml.replace(idx, idx + 1, ".");
                }
            }
        }

        for (String prefix : prefixes) {
            r2rml.insert(0, "@prefix " + prefix + ": &lt;" + mapPrefixes.get(prefix) + "&gt; .\n");
        }

        return r2rml.toString();
    }

    private static String createSpecialTriplesMap(Map<String, Object> param2, CCA cca, Property p) throws Exception {
        param2.put("mapName", cca.getClass_().getName() + "_" + p.getName());
        param2.put("className", cca.getClass_().getName());
        param2.put("prefixClass", cca.getClass_().getPrefix());
        param2.put("table", cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + "_"
                + p.getPrefix() + "_" + p.getName() + "_view");
        List<String> atts2 = new ArrayList<>();
        param2.put("atts", atts2);

        int i = 1;
        while (i <= cca.getAttributes().size()) {
            String index = (i == 1 ? "" : "" + i);
            atts2.add("ID_" + cca.getClass_().getPrefix() + "_" + cca.getClass_().getName() + index);
            i++;
        }

        StringBuilder r2rml2 = new StringBuilder("");
        r2rml2.append(TemplateUtil.applyTemplate("r2rml/subjectMap", param2));
        r2rml2.append(TemplateUtil.applyTemplate("r2rml/predicateObjectMap", param2));

        return r2rml2.toString();
    }
}