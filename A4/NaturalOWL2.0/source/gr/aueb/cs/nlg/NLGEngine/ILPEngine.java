/*
NaturalOWL version 2.0 
Copyright (C) 2013 Gerasimos Lampouras
Natural Language Processing Group, Department of Informatics, 
Athens University of Economics and Business, Greece.

NaturalOWL version 2.0 is based on the original NaturalOWL, developed by
Dimitrios Galanis and Giorgos Karakatsiotis.

This file is part of NaturalOWL version 2.0.

NaturalOWL version 2.0 is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

NaturalOWL version 2.0 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.aueb.cs.nlg.NLGEngine;

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.SPFillerSlot;
import gr.aueb.cs.nlg.NLFiles.SPOwnerSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;
import gr.aueb.cs.nlg.Utils.XmlMsgs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkException;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.semanticweb.owlapi.model.IRI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ILPEngine extends NLGEngineComponent {

    private int factCount;
    private SentencePlanQueryManager SPQM;
    private MappingQueryManager MQM;
    private ArrayList<Node> facts;
    private ArrayList<ArrayList<IRI>> sentencePlans;
    private ArrayList<String> slots;
    private HashMap<IRI, Boolean> selectedFacts;
    private HashMap<IRI, Integer> selectedSubsets;
    private HashMap<IRI, HashMap<IRI, Boolean>> selectedSentencePlans;

    public ILPEngine(SentencePlanQueryManager SPQM, MappingQueryManager MQM, String Language) {
        super(Language);

        this.SPQM = SPQM;
        this.MQM = MQM;
    }

    public XmlMsgs createAndSolveILPNLG(XmlMsgs messages, double lamda, int maxSentences, int maxSlotsPerSentence) {
        //Only take into account interesting facts
        facts = new ArrayList<Node>();
        for (int i = 0; i < messages.getMessages().size(); i++) {
            if (Integer.parseInt(XmlMsgs.getAttribute(messages.getMessages().get(i), XmlMsgs.prefix, XmlMsgs.INTEREST)) != 0) {
                facts.add(messages.getMessages().get(i));
            }
        }

        //Initialize subset selection
        selectedSubsets = new HashMap<IRI, Integer>();
        for (int i = 0; i < facts.size(); i++) {
            selectedSubsets.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), -1);
        }

        //Initialize fact selection
        selectedFacts = new HashMap<IRI, Boolean>();
        for (int i = 0; i < facts.size(); i++) {
            selectedFacts.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), Boolean.FALSE);
        }

        //Initialize sentence plan selection
        selectedSentencePlans = new HashMap<IRI, HashMap<IRI, Boolean>>();
        for (int i = 0; i < facts.size(); i++) {
            HashMap<IRI, Boolean> selectedSentencePlanIRIs = new HashMap<IRI, Boolean>();
            if (!MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).isEmpty()) {
                if (MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).size() > 0) {
                    for (IRI spIRI : MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)))) {
                        if (SPQM.getSentencePlan(spIRI).getLanguage().equals(getLanguage())) {
                            selectedSentencePlanIRIs.put(spIRI, Boolean.FALSE);
                        }
                    }
                }
            } else {
                selectedSentencePlanIRIs.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)), Boolean.FALSE);
            }
            selectedSentencePlans.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), selectedSentencePlanIRIs);
        }

        /* factCount = 0;            
        long startTime = System.currentTimeMillis(); */
        if (!facts.isEmpty()) {
            glp_prob lp = createAndSolveModel(lamda, 1.0F - lamda, maxSentences, maxSlotsPerSentence);

            if (lp != null) {
                //calculateNLGFactCount(lp, maxSentences);
                exportSelection(lp, maxSentences, -1);

                // Free memory
                GLPK.glp_delete_prob(lp);
            }
        }
        /* long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Total time elapsed: \t" + duration);
        System.out.println("@@factCount@@\t" + factCount); */

        return applyNLGSolution(messages);
    }

    public XmlMsgs createAndApproximateILPNLG(XmlMsgs messages, double lamda, int maxSentences, int maxSlotsPerSentence) {
        //Only take into account interesting facts
        facts = new ArrayList<Node>();
        for (int i = 0; i < messages.getMessages().size(); i++) {
            if (Integer.parseInt(XmlMsgs.getAttribute(messages.getMessages().get(i), XmlMsgs.prefix, XmlMsgs.INTEREST)) != 0) {
                facts.add(messages.getMessages().get(i));
            }
        }

        //Initialize subset selection
        selectedSubsets = new HashMap<IRI, Integer>();
        for (int i = 0; i < facts.size(); i++) {
            selectedSubsets.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), -1);
        }

        //Initialize fact selection
        selectedFacts = new HashMap<IRI, Boolean>();
        for (int i = 0; i < facts.size(); i++) {
            selectedFacts.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), Boolean.FALSE);
        }

        //Initialize sentence plan selection
        selectedSentencePlans = new HashMap<IRI, HashMap<IRI, Boolean>>();
        for (int i = 0; i < facts.size(); i++) {
            HashMap<IRI, Boolean> selectedSentencePlanIRIs = new HashMap<IRI, Boolean>();
            if (!MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).isEmpty()) {
                if (MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).size() > 0) {
                    for (IRI spIRI : MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)))) {
                        if (SPQM.getSentencePlan(spIRI).getLanguage().equals(getLanguage())) {
                            selectedSentencePlanIRIs.put(spIRI, Boolean.FALSE);
                        }
                    }
                }
            } else {
                selectedSentencePlanIRIs.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)), Boolean.FALSE);
            }
            selectedSentencePlans.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), selectedSentencePlanIRIs);
        }

        /* factCount = 0;
        long startTime = System.currentTimeMillis(); */
        for (int m = 0; m < maxSentences; m++) {
            if (!facts.isEmpty()) {
                glp_prob lp = createAndSolveModel(lamda, 1.0F - lamda, 1, maxSlotsPerSentence);

                if (lp != null) {
                    //calculateNLGFactCount(lp, 1);
                    exportSelection(lp, 1, m);

                    // Remove selected facts
                    ArrayList<Node> toRemove = new ArrayList<Node>();
                    for (int i = 0; i < facts.size(); i++) {
                        int column = GLPK.glp_find_col(lp, "a" + (i + 1));
                        double val = GLPK.glp_mip_col_val(lp, column);
                        if (val == 1.0) {
                            toRemove.add(facts.get(i));
                        }
                    }
                    facts.removeAll(toRemove);

                    // Free memory
                    GLPK.glp_delete_prob(lp);
                }
            }
        }
        /* long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Total time elapsed: \t" + duration);
        System.out.println("@@factCount@@\t" + factCount);*/

        return applyNLGSolution(messages);
    }

    private glp_prob createAndSolveModel(double lamda1, double lamda2, int maxSentences, int maxSlotsPerSentence) {
        glp_prob lp;
        glp_iocp iocp;
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;
        int ret;

        try {
            // Create problem
            lp = GLPK.glp_create_prob();
            GLPK.glp_set_prob_name(lp, "NLG problem");
            GLPK.glp_create_index(lp);

            // Define columns
            int columnCount = 1;
            // a_i (if fact_i is selected)
            GLPK.glp_add_cols(lp, facts.size());
            for (int i = 0; i < facts.size(); i++) {
                GLPK.glp_set_col_name(lp, columnCount, "a" + (i + 1));
                GLPK.glp_set_col_kind(lp, columnCount, GLPKConstants.GLP_BV);
                columnCount++;
            }
            // l_ikj (if sentencePlan_ik is used to realize fact_i that is in subset_j)
            int totalLikVariables = 0;
            sentencePlans = new ArrayList<ArrayList<IRI>>();
            for (int i = 0; i < facts.size(); i++) {
                ArrayList<IRI> sentencePlanIRIs = new ArrayList<IRI>();
                if (!MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).isEmpty()) {
                    if (MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).size() > 0) {
                        for (IRI spIRI : MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)))) {
                            if (SPQM.getSentencePlan(spIRI).getLanguage().equals(getLanguage())) {
                                sentencePlanIRIs.add(spIRI);
                                totalLikVariables++;
                            }
                        }
                    }
                } else {
                    sentencePlanIRIs.add(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
                    totalLikVariables++;
                }
                sentencePlans.add(sentencePlanIRIs);
            }
            GLPK.glp_add_cols(lp, totalLikVariables * maxSentences);
            for (int i = 0; i < facts.size(); i++) {
                for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                    for (int j = 0; j < maxSentences; j++) {
                        GLPK.glp_set_col_name(lp, columnCount, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                        GLPK.glp_set_col_kind(lp, columnCount, GLPKConstants.GLP_BV);
                        columnCount++;
                    }
                }
            }
            // s_o_j (if slot_o is in subset sub_j)  
            int totalSoVariables = 0;
            HashSet<String> slotStrings = new HashSet<String>();
            for (int i = 0; i < facts.size(); i++) {
                if (MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))) != null) {
                    if (MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).size() > 0) {
                        for (IRI SPIRI : MQM.getSentencePlansSet(SPQM, IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)))) {
                            if (SPQM.getSentencePlan(SPIRI).getLanguage().equals(getLanguage())) {
                                for (SPSlot slot : SPQM.getSentencePlan(SPIRI).getSlotsList()) {
                                    if (slot instanceof SPOwnerSlot) {
                                        slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
                                    } else if (slot instanceof SPFillerSlot) {
                                        slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
                                    } else {
                                        slotStrings.add(slot.toString());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (SPSlot slot : SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))).getSlotsList()) {
                        if (slot instanceof SPOwnerSlot) {
                            slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
                        } else if (slot instanceof SPFillerSlot) {
                            slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
                        } else {
                            slotStrings.add(slot.toString());
                        }
                    }
                }

                if (Languages.isEnglish(getLanguage())) {
                    //isASPEN
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.isASPEN_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                    //kindOfSPEN
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.kindOfSPEN_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                    //sameIndividualSPEN
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.sameIndividualSPEN_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                } else {
                    //isASPGR
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.isASPGR_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                    //kindOfSPEN
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.kindOfSPGR_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                    //sameIndividualSPEN
		            for (SPSlot slot : SPQM.getSentencePlan(DefaultResourcesManager.sameIndividualSPGR_IRI).getSlotsList()) {
		                if (slot instanceof SPOwnerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject));
		                } else if (slot instanceof SPFillerSlot) {
		                    slotStrings.add(slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject));
		                } else {
		                    slotStrings.add(slot.toString());
		                }
		            }
                }
            }
            slots = new ArrayList<String>(slotStrings);
            totalSoVariables = slots.size();
            GLPK.glp_add_cols(lp, totalSoVariables * maxSentences);
            for (int o = 0; o < slots.size(); o++) {
                for (int j = 0; j < maxSentences; j++) {
                    GLPK.glp_set_col_name(lp, columnCount, "s" + (o + 1) + "," + (j + 1));
                    GLPK.glp_set_col_kind(lp, columnCount, GLPKConstants.GLP_BV);
                    columnCount++;
                }
            }

            // Create constraints
            int rowCount = 1;

            // Sum(Sum(l_ikj)) - a_i = 0
            GLPK.glp_add_rows(lp, facts.size());
            for (int i = 0; i < facts.size(); i++) {
                GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_FX, 0, 0);

                columnCount = 1;
                ind = GLPK.new_intArray(sentencePlans.get(i).size() * maxSentences + 2);
                val = GLPK.new_doubleArray(sentencePlans.get(i).size() * maxSentences + 2);
                int column = GLPK.glp_find_col(lp, "a" + (i + 1));
                GLPK.intArray_setitem(ind, columnCount, column);
                GLPK.doubleArray_setitem(val, columnCount, -1);
                columnCount++;

                for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                    for (int j = 0; j < maxSentences; j++) {
                        column = GLPK.glp_find_col(lp, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                        GLPK.intArray_setitem(ind, columnCount, column);
                        GLPK.doubleArray_setitem(val, columnCount, 1);
                        columnCount++;
                    }
                }
                GLPK.glp_set_mat_row(lp, rowCount, sentencePlans.get(i).size() * maxSentences + 1, ind, val);
                rowCount++;
            }
            // Sum(s_oj) - |S_ik| l_ikj >= 0
            GLPK.glp_add_rows(lp, totalLikVariables * maxSentences);
            for (int i = 0; i < facts.size(); i++) {
                for (int j = 0; j < maxSentences; j++) {
                    for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                    	
                        GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                        GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_LO, 0, 0);

                    	HashSet<String> slotStrs = new HashSet<String>();
                        for (SPSlot slot : SPQM.getSentencePlan(sentencePlans.get(i).get(k)).getSlotsList()) {
                            String slotStr;
                            if (slot instanceof SPOwnerSlot) {
                                slotStr = slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject);
                            } else if (slot instanceof SPFillerSlot) {
                                slotStr = slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject);
                            } else {
                                slotStr = slot.toString();
                            }
                            slotStrs.add(slotStr);
                        }

                        ind = GLPK.new_intArray(slotStrs.size() + 2);
                        val = GLPK.new_doubleArray(slotStrs.size() + 2);

                        columnCount = 1;
                        for (String slotStr : slotStrs) {
                            for (int o = 0; o < slots.size(); o++) {
                                if (slots.get(o).equals(slotStr)) {
                                    int column = GLPK.glp_find_col(lp, "s" + (o + 1) + "," + (j + 1));
                                    GLPK.intArray_setitem(ind, columnCount, column);
                                    GLPK.doubleArray_setitem(val, columnCount, 1);
                                    columnCount++;
                                }
                            }
                        }

                        int column = GLPK.glp_find_col(lp, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                        GLPK.intArray_setitem(ind, columnCount, column);
                        GLPK.doubleArray_setitem(val, columnCount, -SPQM.getSentencePlan(sentencePlans.get(i).get(k)).getSlotsList().size());
                        columnCount++;
                        GLPK.glp_set_mat_row(lp, rowCount, slotStrs.size() + 1, ind, val);
                        rowCount++;
                    }
                }
            }
            // Sum(l_ikj) - s_oj >= 0
            GLPK.glp_add_rows(lp, slots.size() * maxSentences);
            for (int o = 0; o < slots.size(); o++) {
                for (int j = 0; j < maxSentences; j++) {
                    ArrayList<String> sentencePlansContainingSlot = new ArrayList<String>();
                    for (int i = 0; i < facts.size(); i++) {
                        for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                        	HashSet<String> slotStrs = new HashSet<String>();
                            for (SPSlot slot : SPQM.getSentencePlan(sentencePlans.get(i).get(k)).getSlotsList()) {
                                String slotStr;
                                if (slot instanceof SPOwnerSlot) {
                                    slotStr = slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forSubject);
                                } else if (slot instanceof SPFillerSlot) {
                                    slotStr = slot.toString() + XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forObject);
                                } else {
                                    slotStr = slot.toString();
                                }
                                slotStrs.add(slotStr);
                            }
                            for (String slotStr : slotStrs) {
                                if (slots.get(o).equals(slotStr)) {
                                    sentencePlansContainingSlot.add("l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                                }
                            }
                        }
                    }
                    GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                    GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_LO, 0, 0);

                    ind = GLPK.new_intArray(sentencePlansContainingSlot.size() + 2);
                    val = GLPK.new_doubleArray(sentencePlansContainingSlot.size() + 2);

                    columnCount = 1;

                    int column = GLPK.glp_find_col(lp, "s" + (o + 1) + "," + (j + 1));
                    GLPK.intArray_setitem(ind, columnCount, column);
                    GLPK.doubleArray_setitem(val, columnCount, -1);
                    columnCount++;

                    for (String sp : sentencePlansContainingSlot) {
                        column = GLPK.glp_find_col(lp, sp);
                        GLPK.intArray_setitem(ind, columnCount, column);
                        GLPK.doubleArray_setitem(val, columnCount, 1);
                        columnCount++;
                    }

                    GLPK.glp_set_mat_row(lp, rowCount, sentencePlansContainingSlot.size() + 1, ind, val);
                    rowCount++;
                }
            }
            // Sum(s_oj) <= maxSlotsPerSentence
            GLPK.glp_add_rows(lp, maxSentences);
            for (int j = 0; j < maxSentences; j++) {
                GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_UP, 0, maxSlotsPerSentence);

                ind = GLPK.new_intArray(slots.size() + 1);
                val = GLPK.new_doubleArray(slots.size() + 1);

                columnCount = 1;
                for (int o = 0; o < slots.size(); o++) {
                    int column = GLPK.glp_find_col(lp, "s" + (o + 1) + "," + (j + 1));
                    GLPK.intArray_setitem(ind, columnCount, column);
                    GLPK.doubleArray_setitem(val, columnCount, 1);
                    columnCount++;
                }
                GLPK.glp_set_mat_row(lp, rowCount, slots.size(), ind, val);
                rowCount++;
            }
            // Do not put different section facts in same sentence
            // Sum(l_i1_kj) + Sum(l_i2_kj) <= 1
            int firstSection = 100000000;
            for (int i = 0; i < facts.size(); i++) {
                if (Integer.parseInt(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) != 0 && Integer.parseInt(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) < firstSection) {
                    firstSection = Integer.parseInt(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.SECTION_TAG));
                }
            }
            for (int i1 = 0; i1 < facts.size(); i1++) {
                for (int i2 = i1 + 1; i2 < facts.size(); i2++) {
                    if (!XmlMsgs.getAttribute(facts.get(i1), XmlMsgs.prefix, XmlMsgs.SECTION_TAG).equals(XmlMsgs.getAttribute(facts.get(i2), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) && !(Integer.parseInt(XmlMsgs.getAttribute(facts.get(i1), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) == 0 && Integer.parseInt(XmlMsgs.getAttribute(facts.get(i2), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) == firstSection) && !(Integer.parseInt(XmlMsgs.getAttribute(facts.get(i2), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) == 0 && Integer.parseInt(XmlMsgs.getAttribute(facts.get(i1), XmlMsgs.prefix, XmlMsgs.SECTION_TAG)) == firstSection)) {
                        GLPK.glp_add_rows(lp, maxSentences);
                        for (int j = 0; j < maxSentences; j++) {
                            GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                            GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_UP, 1, 1);

                            ind = GLPK.new_intArray(sentencePlans.get(i1).size() + sentencePlans.get(i2).size());
                            val = GLPK.new_doubleArray(sentencePlans.get(i1).size() + sentencePlans.get(i2).size());

                            columnCount = 1;
                            for (int k = 0; k < sentencePlans.get(i1).size(); k++) {
                                int column = GLPK.glp_find_col(lp, "l" + (i1 + 1) + "," + (k + 1) + "," + (j + 1));
                                GLPK.intArray_setitem(ind, columnCount, column);
                                GLPK.doubleArray_setitem(val, columnCount, 1);
                                columnCount++;
                            }

                            for (int k = 0; k < sentencePlans.get(i2).size(); k++) {
                                int column = GLPK.glp_find_col(lp, "l" + (i2 + 1) + "," + (k + 1) + "," + (j + 1));
                                GLPK.intArray_setitem(ind, columnCount, column);
                                GLPK.doubleArray_setitem(val, columnCount, 1);
                                columnCount++;
                            }
                            GLPK.glp_set_mat_row(lp, rowCount, sentencePlans.get(i1).size() + sentencePlans.get(i2).size(), ind, val);
                            rowCount++;
                        }
                    }
                }
            }
            // AllValue facts should be in the same sentence
            // Sum(l_i1_kj) - Sum(l_i2_kj) = 0
            for (int i1 = 0; i1 < facts.size(); i1++) {
                for (int i2 = i1 + 1; i2 < facts.size(); i2++) {
                    if (XmlMsgs.getAttribute(facts.get(i1), XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) && XmlMsgs.getAttribute(facts.get(i2), XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) && XmlMsgs.getAttribute(facts.get(i1), XmlMsgs.prefix, XmlMsgs.forProperty).equals(XmlMsgs.getAttribute(facts.get(i2), XmlMsgs.prefix, XmlMsgs.forProperty))) {
                        GLPK.glp_add_rows(lp, maxSentences);
                        for (int j = 0; j < maxSentences; j++) {
                            GLPK.glp_set_row_name(lp, rowCount, "constr" + rowCount);
                            GLPK.glp_set_row_bnds(lp, rowCount, GLPKConstants.GLP_FX, 0, 0);

                            ind = GLPK.new_intArray(sentencePlans.get(i1).size() + sentencePlans.get(i2).size());
                            val = GLPK.new_doubleArray(sentencePlans.get(i1).size() + sentencePlans.get(i2).size());

                            columnCount = 1;
                            for (int k = 0; k < sentencePlans.get(i1).size(); k++) {
                                int column = GLPK.glp_find_col(lp, "l" + (i1 + 1) + "," + (k + 1) + "," + (j + 1));
                                GLPK.intArray_setitem(ind, columnCount, column);
                                GLPK.doubleArray_setitem(val, columnCount, 1);
                                columnCount++;
                            }

                            for (int k = 0; k < sentencePlans.get(i1).size(); k++) {
                                int column = GLPK.glp_find_col(lp, "l" + (i2 + 1) + "," + (k + 1) + "," + (j + 1));
                                GLPK.intArray_setitem(ind, columnCount, column);
                                GLPK.doubleArray_setitem(val, columnCount, -1);
                                columnCount++;
                            }
                            GLPK.glp_set_mat_row(lp, rowCount, sentencePlans.get(i1).size() + sentencePlans.get(i2).size(), ind, val);
                            rowCount++;
                        }
                    }
                }
            }
            // Facts that do not allow aggregation should be alone in sentence ************************
            // Must think of something here!!

            // Define objective
            GLPK.glp_set_obj_name(lp, "z");
            GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
            GLPK.glp_set_obj_coef(lp, 0, 1.);
            // a_i (if fact_i is selected)
            for (int i = 0; i < facts.size(); i++) {
                int column = GLPK.glp_find_col(lp, "a" + (i + 1));
                GLPK.glp_set_obj_coef(lp, column, lamda1 * normalizeInterestValue(Integer.parseInt(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.INTEREST))) / facts.size());
            }
            // s_o (if slot_o is selected)
     /*       for (int o = 0; o < totalSoVariables; o++) {
            int column = GLPK.glp_find_col(lp, "s" + (o + 1));
            GLPK.glp_set_obj_coef(lp, column, lamda3*1/totalSoVariables);
            }*/
            // s_o_j (if slot_o is in subset sub_j)
            for (int o = 0; o < slots.size(); o++) {
                for (int j = 0; j < maxSentences; j++) {
                    int column = GLPK.glp_find_col(lp, "s" + (o + 1) + "," + (j + 1));
                    GLPK.glp_set_obj_coef(lp, column, -lamda2 * (1.0F / (totalSoVariables * maxSentences)));
                }
            }

            GLPK.glp_write_lp(lp, null, "temp.lp");

            // Solve model
            iocp = new glp_iocp();
            GLPK.glp_mem_limit(10000);
            GLPK.glp_init_iocp(iocp);
            iocp.setMsg_lev(GLPKConstants.GLP_MSG_OFF);
            iocp.setPresolve(GLPKConstants.GLP_ON);
            iocp.setCb_size(256);
            iocp.setBr_tech(GLPKConstants.GLP_BR_PCH);
            iocp.setBt_tech(GLPKConstants.GLP_BT_BPH);
            iocp.setMir_cuts(GLPKConstants.GLP_ON);
            iocp.setCov_cuts(GLPKConstants.GLP_ON);
            iocp.setClq_cuts(GLPKConstants.GLP_ON);

            //long startTime = System.currentTimeMillis();
            ret = GLPK.glp_intopt(lp, iocp);
            //long endTime = System.currentTimeMillis();
            //long duration = endTime - startTime;
            //System.out.println("Time to solve: \t" + duration);

            // Retrieve solution
            if (ret != 0) {
                System.err.println("The problem could not be solved");
            }

            return lp;
        } catch (GlpkException ex) {
        }
        return null;
    }

    private void exportSelection(glp_prob lp, int maxSentences, int placeAllInThisSentence) {
        double val;

        // a_i (if fact_i is selected)
        for (int i = 0; i < facts.size(); i++) {
            int column = GLPK.glp_find_col(lp, "a" + (i + 1));
            val = GLPK.glp_mip_col_val(lp, column);
            if (val == 1.0) {
                selectedFacts.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), Boolean.TRUE);
            }
        }

        // l_ikj (if sentencePlan_ik is used to realize fact_i in subset_j)
        for (int i = 0; i < facts.size(); i++) {
            for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                for (int j = 0; j < maxSentences; j++) {
                    int column = GLPK.glp_find_col(lp, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                    val = GLPK.glp_mip_col_val(lp, column);
                    if (val == 1.0) {
                        selectedSentencePlans.get(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty))).put(sentencePlans.get(i).get(k), Boolean.TRUE);
                        if (placeAllInThisSentence == -1) {
                            selectedSubsets.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), j);
                        } else {
                            selectedSubsets.put(IRI.create(XmlMsgs.getAttribute(facts.get(i), XmlMsgs.prefix, XmlMsgs.forProperty)), placeAllInThisSentence);
                        }
                    }
                }
            }
        }
    }

    private XmlMsgs applyNLGSolution(XmlMsgs messages) {
        //Sentence Selection
        ArrayList<Node> toRemove = new ArrayList<Node>();
        toRemove.addAll(messages.getMessages());
        for (IRI fact : selectedFacts.keySet()) {
            if (selectedFacts.get(fact).equals(Boolean.TRUE)) {
                for (int i = 0; i < messages.getMessages().size(); i++) {
                    if (XmlMsgs.getAttribute(messages.getMessages().get(i), XmlMsgs.prefix, XmlMsgs.forProperty).toString().equals(fact.toString())) {
                        toRemove.remove(messages.getMessages().get(i));
                    }
                }
            }
        }
        messages.removeMsgs(toRemove);

        //SubSet Selection
        for (IRI fact : selectedSubsets.keySet()) {
            if (selectedSubsets.get(fact) > -1) {
                for (int i = 0; i < messages.getMessages().size(); i++) {
                    if (XmlMsgs.getAttribute(messages.getMessages().get(i), XmlMsgs.prefix, XmlMsgs.forProperty).toString().equals(fact.toString())) {
                        messages.setAttr((Element) messages.getMessages().get(i), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ILPSentence, selectedSubsets.get(fact) + "");
                    }
                }
            }
        }

        //Sentence Plan Selection
        for (IRI fact : selectedSentencePlans.keySet()) {
            for (IRI plan : selectedSentencePlans.get(fact).keySet()) {
                if (selectedSentencePlans.get(fact).get(plan).equals(Boolean.TRUE)) {
                    for (int i = 0; i < messages.getMessages().size(); i++) {
                        if (XmlMsgs.getAttribute(messages.getMessages().get(i), XmlMsgs.prefix, XmlMsgs.forProperty).toString().equals(fact.toString())) {
                            messages.setAttr((Element) messages.getMessages().get(i), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, plan.toString());
                        }
                    }
                }
            }
        }

        return messages;
    }

    private double normalizeInterestValue(int interest) {
        if (interest == 0) {
            return 0;
        } else {
            return interest / 3.0;
        }
    }

    private void printMIPSolution(glp_prob lp) {
        int i;
        int n;
        String name;
        double val;

        name = GLPK.glp_get_obj_name(lp);
        val = GLPK.glp_mip_obj_val(lp);
        System.out.print(name);
        System.out.print(" = ");
        System.out.println(val);
        n = GLPK.glp_get_num_cols(lp);
        for (i = 1; i <= n; i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_mip_col_val(lp, i);
            System.out.print(name);
            System.out.print(" = ");
            System.out.println(val);
        }
    }

    private void printNLGSolution(glp_prob lp, XmlMsgs msgs, int maxSentences) {
        int n;
        String name;
        double val;

        HashSet<String>[] sentences = new HashSet[maxSentences];
        for (int i = 0; i < maxSentences; i++) {
            sentences[i] = new HashSet<String>();
        }

        ArrayList<HashSet<String>> selectedSlots = new ArrayList<HashSet<String>>();
        for (int i = 0; i < maxSentences; i++) {
            selectedSlots.add(new HashSet<String>());
        }

        // l_ikj (if sentencePlan_ik is used to realize fact_i in subset_j)
        for (int i = 0; i < facts.size(); i++) {
            for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                for (int j = 0; j < maxSentences; j++) {
                    int column = GLPK.glp_find_col(lp, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                    val = GLPK.glp_mip_col_val(lp, column);
                    if (val == 1.0) {
                        sentences[j].add(GLPK.glp_get_col_name(lp, i + 1));
                    }
                }
            }
        }

        n = GLPK.glp_get_num_cols(lp);
        for (int i = 1; i <= n; i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_mip_col_val(lp, i);
            if (name.startsWith("c")) {
                if (val == 1.0) {
                }
            }
            if (name.startsWith("s") && name.indexOf(',') != -1) {
                if (val == 1.0) {
                    int sentence = Integer.parseInt(name.substring(name.indexOf(',') + 1));
                    selectedSlots.get(sentence - 1).add(name);
                }
            }
        }

        System.out.println("-----------------");
        for (int i = 0; i < maxSentences; i++) {
            System.out.println(sentences[i]);
            System.out.print("[");
            for (String sentence : sentences[i]) {
                if (XmlMsgs.getAttribute(msgs.getMessages().get(Integer.parseInt(sentence.substring(sentence.indexOf("a") + 1)) - 1), XmlMsgs.prefix, XmlMsgs.forProperty).contains("#")) {
                    System.out.print(IRI.create(XmlMsgs.getAttribute(msgs.getMessages().get(Integer.parseInt(sentence.substring(sentence.indexOf("a") + 1))), XmlMsgs.prefix, XmlMsgs.forProperty)).getFragment());
                } else {
                    System.out.print(XmlMsgs.getAttribute(msgs.getMessages().get(Integer.parseInt(sentence.substring(sentence.indexOf("a") + 1))), XmlMsgs.prefix, XmlMsgs.forProperty));
                }
                System.out.print(", ");
            }
            System.out.println("]");
            /*System.out.print("[");
            for (String slot: selectedSlots.get(i)) {
            System.out.print(slot);
            System.out.print(", ");
            }
            System.out.println("]");*/
        }
        System.out.println("-----------------");
    }

    private void calculateNLGFactCount(glp_prob lp, int maxSentences) {
        double val;

        // l_ikj (if sentencePlan_ik is used to realize fact_i in subset_j)
        for (int i = 0; i < facts.size(); i++) {
            for (int k = 0; k < sentencePlans.get(i).size(); k++) {
                for (int j = 0; j < maxSentences; j++) {
                    int column = GLPK.glp_find_col(lp, "l" + (i + 1) + "," + (k + 1) + "," + (j + 1));
                    val = GLPK.glp_mip_col_val(lp, column);
                    if (val == 1.0) {
                        factCount++;
                    }
                }
            }
        }
    }
}