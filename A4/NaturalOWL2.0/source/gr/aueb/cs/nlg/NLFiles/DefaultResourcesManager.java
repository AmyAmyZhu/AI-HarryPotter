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
package gr.aueb.cs.nlg.NLFiles;

import gr.aueb.cs.nlg.Comparisons.Comparison;
import gr.aueb.cs.nlg.Comparisons.ComparisonFullCollection;
import gr.aueb.cs.nlg.Comparisons.ComparisonTexts;
import gr.aueb.cs.nlg.Comparisons.ComparisonTypes;
import gr.aueb.cs.nlg.Languages.Languages;

import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

public class DefaultResourcesManager {

    //Nouns
    public static IRI entityNLE_IRI = IRI.create(NLResourceManager.nlowlNS + "entityNLE");
    //Verbs
    public static IRI toBeVLE_IRI = IRI.create(NLResourceManager.nlowlNS + "toBeVLE");
    public static IRI toHaveVLE_IRI = IRI.create(NLResourceManager.nlowlNS + "toHaveVLE");
    //Adjectives
    public static IRI identicalALE_IRI = IRI.create(NLResourceManager.nlowlNS + "identicalALE");
    public static IRI otherALE_IRI = IRI.create(NLResourceManager.nlowlNS + "otherALE");
    public static IRI onlyALE_IRI = IRI.create(NLResourceManager.nlowlNS + "onlyALE");
    public static IRI prevALE_IRI = IRI.create(NLResourceManager.nlowlNS + "previousALE");
    public static IRI mostALE_IRI = IRI.create(NLResourceManager.nlowlNS + "mostALE");
    public static IRI allALE_IRI = IRI.create(NLResourceManager.nlowlNS + "allALE");
    //NL Names
    public static IRI entityNLNEN_IRI = IRI.create(NLResourceManager.nlowlNS + "entityNLNEN");
    public static IRI entityNLNGR_IRI = IRI.create(NLResourceManager.nlowlNS + "entityNLNGR");
    //Sentence Plans
    public static IRI isASPEN_IRI = IRI.create(NLResourceManager.nlowlNS + "isASPEN");
    public static IRI isASPGR_IRI = IRI.create(NLResourceManager.nlowlNS + "isASPGR");
    public static IRI sameIndividualSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + "sameIndividualSPEN");
    public static IRI sameIndividualSPGR_IRI = IRI.create(NLResourceManager.nlowlNS + "sameIndividualSPGR");
    public static IRI kindOfSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + "kindOfSPEN");
    public static IRI kindOfSPGR_IRI = IRI.create(NLResourceManager.nlowlNS + "kindOfSPGR");
    public static IRI oneOfSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + "oneOfSPEN");
    public static IRI oneOfSPGR_IRI = IRI.create(NLResourceManager.nlowlNS + "oneOfSPGR");

    public static boolean isDefaultResource(IRI resourceIRI) {
        if (resourceIRI.equals(entityNLE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(toBeVLE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(toHaveVLE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(identicalALE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(onlyALE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(prevALE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(mostALE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(allALE_IRI)) {
            return true;
        }
        if (resourceIRI.equals(entityNLNEN_IRI)) {
            return true;
        }
        if (resourceIRI.equals(entityNLNGR_IRI)) {
            return true;
        }
        if (resourceIRI.equals(isASPEN_IRI)) {
            return true;
        }
        if (resourceIRI.equals(isASPGR_IRI)) {
            return true;
        }
        if (resourceIRI.equals(sameIndividualSPEN_IRI)) {
            return true;
        }
        if (resourceIRI.equals(sameIndividualSPGR_IRI)) {
            return true;
        }
        if (resourceIRI.equals(kindOfSPEN_IRI)) {
            return true;
        }
        if (resourceIRI.equals(kindOfSPGR_IRI)) {
            return true;
        }
        if (resourceIRI.equals(oneOfSPEN_IRI)) {
            return true;
        }
        if (resourceIRI.equals(oneOfSPGR_IRI)) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.anonymous.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.exactCardinality.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.minCardinality.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.maxCardinality.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.allValuesFrom.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.someValuesFrom.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.instanceOf.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.isA.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.oneOf.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.differentIndividuals.getIRI())) {
            return true;
        }
        if (resourceIRI.equals(NLResourceManager.sameIndividuals.getIRI())) {
            return true;
        }
        return false;
    }

    public static boolean isModifier(IRI modifier) {
        if (modifier.equals(NLResourceManager.exactCardinality.getIRI())) {
            return true;
        }
        if (modifier.equals(NLResourceManager.minCardinality.getIRI())) {
            return true;
        }
        if (modifier.equals(NLResourceManager.maxCardinality.getIRI())) {
            return true;
        }
        return false;
    }

    public DefaultResourcesManager() {
        super();
    }

    public static SentencePlan generateSentencePlanForComparison(SentencePlan sentencePlan, Comparison comparison) {
        String lang = "";
        if (Languages.isEnglish(sentencePlan.getLanguage())) {
            lang = "EN";
        } else if (Languages.isGreek(sentencePlan.getLanguage())) {
            lang = "GR";
        }

        int order = 0;
        ArrayList<SPSlot> slotsList = new ArrayList<SPSlot>();
        if (!comparison.getValueIRI().toString().isEmpty()) {
            boolean startAppending = false;
            Collections.sort(sentencePlan.getSlotsList());
            for (int i = 0; i < sentencePlan.getSlotsList().size(); i++) {
                SPSlot slot = sentencePlan.getSlotsList().get(i);
                if (slot instanceof SPOwnerSlot) {
                    if (!comparison.isSame()) {
                        startAppending = true;
                    }

                    if (comparison.isSame()) {
                        SPComparatorSlot comparator = new SPComparatorSlot(XmlMsgs.NOMINATIVE_TAG, comparison.isMany(), NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_SLOT), order);

                        slotsList.add(comparator);
                        order++;
                    } else {
                        SPComparatorSlot comparator = new SPComparatorSlot(XmlMsgs.ACCUSATIVE_TAG, comparison.isMany(), NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_SLOT), order);

                        slotsList.add(comparator);
                        order++;
                    }

                    SPStringSlot stringSlot = new SPStringSlot(comparison.getSuffix(), NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COLLECTION_SLOT), order);
                    slotsList.add(stringSlot);
                    order++;

                    stringSlot = new SPStringSlot(",", NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMMA_SLOT + "_" + 1), order);
                    slotsList.add(stringSlot);
                    order++;

                    if (!comparison.isSame()) {
                        stringSlot = new SPStringSlot(comparison.getReminderConnector(), NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.CONNECTOR_REMINDER_SLOT), order);
                        slotsList.add(stringSlot);
                        order++;
                    }
                } else if (slot instanceof SPFillerSlot && !comparison.isSame()) {
                    startAppending = false;

                    SPComparatorFillerSlot comparatorFiller = new SPComparatorFillerSlot(XmlMsgs.ACCUSATIVE_TAG, false, NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_FILLER_SLOT), order);
                    slotsList.add(comparatorFiller);
                    order++;

                    SPStringSlot stringSlot = new SPStringSlot(",", NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMMA_SLOT + "_" + 2), order);
                    slotsList.add(stringSlot);
                    order++;
                } else if (startAppending) {
                    if (slot instanceof SPVerbSlot) {
                        SPVerbSlot verbSlot = new SPVerbSlot((SPVerbSlot) slot);
                        verbSlot.setId(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + verbSlot.getId().toString().substring(verbSlot.getId().toString().indexOf(':') + 1)));
                        verbSlot.setOrder(order);

                        if (verbSlot.getAgreesWithID() != null) {
                            for (SPSlot agreeWith : sentencePlan.getSlotsList()) {
                                if (verbSlot.getAgreesWithID().equals(agreeWith.getId())) {
                                    if (agreeWith instanceof SPOwnerSlot) {
                                        verbSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_SLOT));
                                        break;
                                    } else if (agreeWith instanceof SPFillerSlot) {
                                        verbSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_FILLER_SLOT));
                                        break;
                                    } else {
                                        verbSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + agreeWith.getId().toString().substring(agreeWith.getId().toString().indexOf(':') + 1)));
                                        break;
                                    }
                                }
                            }
                        }

                        slotsList.add(verbSlot);
                        order++;
                    } else if (slot instanceof SPNounSlot) {
                        SPNounSlot nounSlot = new SPNounSlot((SPNounSlot) slot);
                        nounSlot.setId(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + nounSlot.getId().toString().substring(nounSlot.getId().toString().indexOf(':') + 1)));
                        nounSlot.setOrder(order);

                        if (nounSlot.getAgreesWithID() != null) {
                            for (SPSlot agreeWith : sentencePlan.getSlotsList()) {
                                if (nounSlot.getAgreesWithID().equals(agreeWith.getId())) {
                                    if (agreeWith instanceof SPOwnerSlot) {
                                        nounSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_SLOT));
                                        break;
                                    } else if (agreeWith instanceof SPFillerSlot) {
                                        nounSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_FILLER_SLOT));
                                        break;
                                    } else {
                                        nounSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + agreeWith.getId().toString().substring(agreeWith.getId().toString().indexOf(':') + 1)));
                                        break;
                                    }
                                }
                            }
                        }

                        slotsList.add(nounSlot);
                        order++;
                    } else if (slot instanceof SPAdjectiveSlot) {
                        SPAdjectiveSlot adjectiveSlot = new SPAdjectiveSlot((SPAdjectiveSlot) slot);
                        adjectiveSlot.setId(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + adjectiveSlot.getId().toString().substring(adjectiveSlot.getId().toString().indexOf(':') + 1)));
                        adjectiveSlot.setOrder(order);

                        if (adjectiveSlot.getAgreesWithID() != null) {
                            for (SPSlot agreeWith : sentencePlan.getSlotsList()) {
                                if (adjectiveSlot.getAgreesWithID().equals(agreeWith.getId())) {
                                    if (agreeWith instanceof SPOwnerSlot) {
                                        adjectiveSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_SLOT));
                                        break;
                                    } else if (agreeWith instanceof SPFillerSlot) {
                                        adjectiveSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.COMPARATOR_FILLER_SLOT));
                                        break;
                                    } else {
                                        adjectiveSlot.setAgreesWithID(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + agreeWith.getId().toString().substring(agreeWith.getId().toString().indexOf(':') + 1)));
                                        break;
                                    }
                                }
                            }
                        }

                        slotsList.add(adjectiveSlot);
                        order++;
                    } else if (slot instanceof SPPrepositionSlot) {
                        SPPrepositionSlot prepSlot = new SPPrepositionSlot((SPPrepositionSlot) slot);
                        prepSlot.setId(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + prepSlot.getId().toString().substring(prepSlot.getId().toString().indexOf(':') + 1)));
                        prepSlot.setOrder(order);

                        slotsList.add(prepSlot);
                        order++;
                    } else if (slot instanceof SPStringSlot) {
                        SPStringSlot stringSlot = new SPStringSlot((SPStringSlot) slot);
                        stringSlot.setId(NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + lang + "_" + ComparisonTypes.REMINDER_SLOT + "_" + stringSlot.getId().toString().substring(stringSlot.getId().toString().indexOf(':') + 1)));
                        stringSlot.setOrder(order);

                        slotsList.add(stringSlot);
                        order++;
                    }
                }
            }
        }

        Collections.sort(sentencePlan.getSlotsList());
        for (SPSlot slot : sentencePlan.getSlotsList()) {
            slot.setOrder(order);
            slotsList.add(slot);
            order++;
        }

        IRI compSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + sentencePlan.getSentencePlanIRI().getFragment() + "-" + comparison.getType() + "_" + comparison.getValueIRI().getFragment() + "_" + lang);
        SentencePlan compSP = new SentencePlan(slotsList, compSPEN_IRI, false, sentencePlan.getLanguage(), true);

        return compSP;
    }

    public static SentencePlan generateSentencePlanForUniqueComparison(SentencePlan sentencePlan) {
        String lang = "";
        if (Languages.isEnglish(sentencePlan.getLanguage())) {
            lang = "EN";
        } else if (Languages.isGreek(sentencePlan.getLanguage())) {
            lang = "GR";
        }

        ArrayList<SPSlot> slotsList = new ArrayList<SPSlot>(sentencePlan.getSlotsList());

        SPStringSlot stringSlot;
        if (Languages.isEnglish(sentencePlan.getLanguage())) {
            stringSlot = new SPStringSlot(ComparisonTexts.SUFIX_FULL_COLECTION_EN, NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "_" + ComparisonTypes.UNIQUE + "_" + lang + "_" + ComparisonTypes.COLLECTION_SLOT), slotsList.size());
        } else {
            stringSlot = new SPStringSlot(ComparisonTexts.SUFIX_FULL_COLECTION_GR, NodeID.getNodeID(sentencePlan.getSentencePlanIRI().getFragment() + "_" + ComparisonTypes.UNIQUE + "_" + lang + "_" + ComparisonTypes.COLLECTION_SLOT), slotsList.size());
        }
        slotsList.add(stringSlot);

        IRI uniqueSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + sentencePlan.getSentencePlanIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "SP" + lang);
        SentencePlan uniqueSP = new SentencePlan(slotsList, uniqueSPEN_IRI, false, sentencePlan.getLanguage(), true);

        return uniqueSP;
    }

    public static NLName generateNLNameForComparison(NLName name, Comparison comparison, boolean entityMentioned) {
        String lang = "";
        if (Languages.isEnglish(name.getLanguage())) {
            lang = "EN";
        } else if (Languages.isGreek(name.getLanguage())) {
            lang = "GR";
        }

        ArrayList<NLNSlot> slotsList = new ArrayList<NLNSlot>();

        for (NLNSlot slot : name.getSlotsList()) {
            if (slot instanceof NLNNounSlot) {
                slotsList.add(new NLNNounSlot((NLNNounSlot) slot));
            } else if (slot instanceof NLNPrepositionSlot) {
                slotsList.add(new NLNPrepositionSlot((NLNPrepositionSlot) slot));
            } else if (slot instanceof NLNStringSlot) {
                slotsList.add(new NLNStringSlot((NLNStringSlot) slot));
            } else if (slot instanceof NLNArticleSlot) {
                slotsList.add(new NLNArticleSlot((NLNArticleSlot) slot));
            } else if (slot instanceof NLNAdjectiveSlot) {
                slotsList.add(new NLNAdjectiveSlot((NLNAdjectiveSlot) slot));
            } else {
                slotsList.add(new NLNSlot(slot));
            }
        }

        Collections.sort(slotsList);

        String cas = XmlMsgs.ACCUSATIVE_TAG;
        String gen = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
        String num = XmlMsgs.SINGULAR;
        NodeID agreeID = null;

        for (NLNSlot slot : slotsList) {
            if (slot instanceof NLNArticleSlot) {
                gen = ((NLNArticleSlot) slotsList.get(0)).getGender();
            }
            if (slot instanceof NLNNounSlot) {
                if (((NLNNounSlot) slot).isHead()) {
                    cas = ((NLNNounSlot) slot).getCase();
                    num = ((NLNNounSlot) slot).getNumber();
                    agreeID = ((NLNNounSlot) slot).getId();
                }
            } else if (slot instanceof NLNAdjectiveSlot) {
                if (((NLNAdjectiveSlot) slot).isHead()) {
                    cas = ((NLNNounSlot) slot).getCase();
                    num = ((NLNNounSlot) slot).getNumber();
                    agreeID = ((NLNAdjectiveSlot) slot).getId();
                }
            }
        }

        int slotsAdded = 0;
        if (!(comparison instanceof ComparisonFullCollection)) {//Comparison with mentioned Instances
            if (comparison.isMany()) {//Many Instance Comparator
                if (slotsList.get(0) instanceof NLNArticleSlot) {
                    ((NLNArticleSlot) slotsList.get(0)).setDefinite(true);
                    if (!comparison.isSame()) {
                        ((NLNArticleSlot) slotsList.get(0)).setCase(XmlMsgs.ACCUSATIVE_TAG);
                        if (entityMentioned) {
                            for (NLNSlot slot : slotsList) {
                                if (slot != slotsList.get(0)) {
                                    slot.setOrder(slot.getOrder() + 1);
                                }
                            }
                            if (agreeID == null) {
                                slotsList.add(new NLNAdjectiveSlot(otherALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder() + 1));
                                slotsAdded++;
                            } else {
                                slotsList.add(new NLNAdjectiveSlot(otherALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder() + 1));
                                slotsAdded++;
                            }
                        }
                    }
                } else {
                    NLNArticleSlot articleSlot;
                    if (!comparison.isSame()) {
                        cas = XmlMsgs.ACCUSATIVE_TAG;
                        if (entityMentioned) {
                            if (agreeID == null) {
                                slotsList.add(new NLNAdjectiveSlot(otherALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder() + 1));
                                slotsAdded++;
                            } else {
                                slotsList.add(new NLNAdjectiveSlot(otherALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder() + 1));
                                slotsAdded++;
                            }
                        }
                    }
                    if (agreeID == null) {
                        articleSlot = new NLNArticleSlot(true, cas, gen, num, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                    } else {
                        articleSlot = new NLNArticleSlot(true, "", "", "", agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                    }
                    for (NLNSlot slot : slotsList) {
                        slot.setOrder(slot.getOrder() + 2);
                    }
                    slotsList.add(articleSlot);
                    slotsAdded++;
                }
            } else {//Single Instance Comparator
                if (slotsList.get(0) instanceof NLNArticleSlot) {
                    ((NLNArticleSlot) slotsList.get(0)).setDefinite(true);
                } else {
                    NLNArticleSlot articleSlot;
                    if (agreeID == null) {
                        articleSlot = new NLNArticleSlot(true, cas, gen, num, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                    } else {
                        articleSlot = new NLNArticleSlot(true, "", "", "", agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                    }
                    for (NLNSlot slot : slotsList) {
                        slot.setOrder(slot.getOrder() + 1);
                    }
                    slotsList.add(articleSlot);
                    slotsAdded++;
                }
            }
        } else {//Comparison with Full Collection
            NLNAdjectiveSlot adjectiveSlot;
            ComparisonFullCollection fullComparison = (ComparisonFullCollection) comparison;
            if (fullComparison.isAll()) {
                if (agreeID == null) {
                    adjectiveSlot = new NLNAdjectiveSlot(allALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                } else {
                    adjectiveSlot = new NLNAdjectiveSlot(allALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                }
                for (NLNSlot slot : slotsList) {
                    slot.setOrder(slot.getOrder() + 1);
                }
            } else {
                if (agreeID == null) {
                    adjectiveSlot = new NLNAdjectiveSlot(mostALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                } else {
                    adjectiveSlot = new NLNAdjectiveSlot(mostALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + lang + "_" + comparison.getType() + "_" + slotsAdded), slotsList.get(0).getOrder());
                }
                if (lang.equals("EN")) {
                    for (NLNSlot slot : slotsList) {
                        slot.setOrder(slot.getOrder() + 1);
                    }
                } else {
                    for (NLNSlot slot : slotsList) {
                        if (slot.getOrder() != slotsList.get(0).getOrder()) {
                            slot.setOrder(slot.getOrder() + 1);
                        }
                    }
                }
            }
            slotsList.add(adjectiveSlot);
            slotsAdded++;
        }

        IRI compSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + name.getNLNameIRI().getFragment() + "-CompNLN" + "_" + comparison.getType() + lang);
        NLName compNLN = new NLName(slotsList, compSPEN_IRI, false, false, name.getLanguage());
        return compNLN;
    }

    public static NLName generateNLNameForUniqueComparison(NLName name) {
        String lang = "";
        if (Languages.isEnglish(name.getLanguage())) {
            lang = "EN";
        } else if (Languages.isGreek(name.getLanguage())) {
            lang = "GR";
        }

        ArrayList<NLNSlot> slotsList = new ArrayList<NLNSlot>(name.getSlotsList());

        Collections.sort(slotsList);

        String cas = XmlMsgs.NOMINATIVE_TAG;
        String gen = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
        String num = XmlMsgs.SINGULAR;
        NodeID agreeID = null;

        for (NLNSlot slot : slotsList) {
            if (slot instanceof NLNNounSlot) {
                if (((NLNNounSlot) slot).isHead()) {
                    agreeID = ((NLNNounSlot) slot).getId();
                }
            } else if (slot instanceof NLNAdjectiveSlot) {
                if (((NLNAdjectiveSlot) slot).isHead()) {
                    agreeID = ((NLNAdjectiveSlot) slot).getId();
                }
            }
        }

        int slotsAdded = 0;
        if (slotsList.get(0) instanceof NLNArticleSlot) {
            ((NLNArticleSlot) slotsList.get(0)).setDefinite(true);

            NLNAdjectiveSlot adjectiveSlot;
            if (agreeID == null) {
                adjectiveSlot = new NLNAdjectiveSlot(onlyALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder() + 1);
            } else {
                adjectiveSlot = new NLNAdjectiveSlot(onlyALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder() + 1);
            }

            for (NLNSlot slot : slotsList) {
                if (slot.getOrder() != slotsList.get(0).getOrder()) {
                    slot.setOrder(slot.getOrder() + 1);
                }
            }
            slotsList.add(adjectiveSlot);
            slotsAdded++;
        } else {
            for (NLNSlot slot : slotsList) {
                slot.setOrder(slot.getOrder() + 2);
            }

            NLNArticleSlot articleSlot;
            if (agreeID == null) {
                articleSlot = new NLNArticleSlot(true, cas, gen, num, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder());
            } else {
                articleSlot = new NLNArticleSlot(true, "", "", "", agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder());
            }
            slotsList.add(articleSlot);
            slotsAdded++;

            NLNAdjectiveSlot adjectiveSlot;
            if (agreeID == null) {
                adjectiveSlot = new NLNAdjectiveSlot(onlyALE_IRI, cas, gen, num, false, false, null, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder() + 1);
            } else {
                adjectiveSlot = new NLNAdjectiveSlot(onlyALE_IRI, "", "", "", false, false, agreeID, NodeID.getNodeID(name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + "_" + lang + "_" + slotsAdded), slotsList.get(0).getOrder() + 1);
            }
            slotsList.add(adjectiveSlot);
            slotsAdded++;
        }

        IRI uniqueSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + name.getNLNameIRI().getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + lang);
        NLName uniqueNLN = new NLName(slotsList, uniqueSPEN_IRI, false, false, name.getLanguage());
        return uniqueNLN;
    }

    public static SentencePlan generateDefaultSentencePlanForProperty(IRI propertyIRI, OWLOntology ontology, String language) {
        String plan = "";

        for (OWLAnnotationAssertionAxiom annotation : ontology.getAnnotationAssertionAxioms(propertyIRI)) {
            if (annotation.getProperty().isLabel()) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    if (Languages.isEnglish(language)) {
                        if (((OWLLiteral) annotation.getValue()).hasLang("en")) {
                            plan = ((OWLLiteral) annotation.getValue()).getLiteral();
                        } else if (!((OWLLiteral) annotation.getValue()).hasLang()) {
                            plan = ((OWLLiteral) annotation.getValue()).getLiteral();
                        }
                    } else if (Languages.isGreek(language)) {
                        if (((OWLLiteral) annotation.getValue()).hasLang("gr") || ((OWLLiteral) annotation.getValue()).hasLang("el")) {
                            plan = ((OWLLiteral) annotation.getValue()).getLiteral();
                        }
                    }
                }
            }
        }

        if (plan.isEmpty()) {
            plan = spaceString(propertyIRI.getFragment(), true);
        }

        String lang = "";
        if (Languages.isEnglish(language)) {
            lang = "EN";
        } else if (Languages.isGreek(language)) {
            lang = "GR";
        }

        SPOwnerSlot ownerSlot;
        SPStringSlot stringSlot;
        SPFillerSlot fillerSlot;
        ArrayList<SPSlot> planSlots;

        ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID(propertyIRI.getFragment() + "-DefaultSP" + lang + "1"), 1);
        stringSlot = new SPStringSlot(plan, NodeID.getNodeID(propertyIRI.getFragment() + "-DefaultSP" + lang + "2"), 2);
        fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID(propertyIRI.getFragment() + "-DefaultSP" + lang + "3"), 3);

        planSlots = new ArrayList<SPSlot>();
        planSlots.add(ownerSlot);
        planSlots.add(stringSlot);
        planSlots.add(fillerSlot);

        IRI DefaultSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + propertyIRI.getFragment() + "-DefaultSP" + lang);
        if (Languages.isEnglish(language)) {
            return new SentencePlan(planSlots, DefaultSPEN_IRI, true, Languages.ENGLISH, true);
        } else if (Languages.isGreek(language)) {
            return new SentencePlan(planSlots, DefaultSPEN_IRI, true, Languages.GREEK, true);
        }

        return null;
    }

    public static NLName generateDefaultNLNameForIndividual(IRI indivIRI, OWLOntology ontology, String language) {
        String name = "";

        for (OWLAnnotationAssertionAxiom annotation : ontology.getAnnotationAssertionAxioms(indivIRI)) {
            if (annotation.getProperty().isLabel()) {
                if (annotation.getValue() instanceof OWLLiteral) {
                    if (Languages.isGreek(language)) {
                        if (((OWLLiteral) annotation.getValue()).hasLang("gr") || ((OWLLiteral) annotation.getValue()).hasLang("el")) {
                            name = ((OWLLiteral) annotation.getValue()).getLiteral();
                        }
                    }
                    if (Languages.isEnglish(language) || (name.isEmpty())) {
                        if (((OWLLiteral) annotation.getValue()).hasLang("en")) {
                            name = ((OWLLiteral) annotation.getValue()).getLiteral();
                        } else if (!((OWLLiteral) annotation.getValue()).hasLang()) {
                            name = ((OWLLiteral) annotation.getValue()).getLiteral();
                        }
                    }
                }
            }
        }

        String lang = "";
        if (Languages.isEnglish(language)) {
            lang = "EN";
        } else if (Languages.isGreek(language)) {
            lang = "GR";
        }

        if (name.isEmpty()) {
            if (indivIRI.getFragment() != null) {
                name = spaceString(indivIRI.getFragment(), false);
            } else {
                name = spaceString(indivIRI.toString().substring(indivIRI.toString().lastIndexOf('/') + 1), false);
            }
        }

        NLNStringSlot stringSlot;
        ArrayList<NLNSlot> nameSlots;

        stringSlot = new NLNStringSlot(name, NodeID.getNodeID(indivIRI.getFragment() + "-DefaultNLN" + lang + "1"), 1);

        nameSlots = new ArrayList<NLNSlot>();
        nameSlots.add(stringSlot);

        IRI DefaultNLNEN_IRI = IRI.create(NLResourceManager.nlowlNS + indivIRI.getFragment() + "-DefaultNLN" + lang);
        if (Languages.isEnglish(language)) {
            return new NLName(nameSlots, DefaultNLNEN_IRI, true, false, Languages.ENGLISH, true);
        } else if (Languages.isGreek(language)) {
            return new NLName(nameSlots, DefaultNLNEN_IRI, true, false, Languages.GREEK, true);
        }

        return null;
    }

    public static String spaceString(String str, boolean toLowerCase) {
        StringBuilder ret = new StringBuilder(str.replaceAll("_", " ").replaceAll("-", " "));

        boolean previousDigit = false;
        boolean previousUpperCase = false;
        for (int i = 0; i < ret.length(); i++) {
            if (Character.isUpperCase(ret.charAt(i))) {
                if (!previousUpperCase) {
                    ret.insert(i, " ");
                    i++;
                }
                previousDigit = false;
                previousUpperCase = true;
            } else if ((Character.isDigit(ret.charAt(i)))) {
                if (!previousDigit) {
                    ret.insert(i, " ");
                    i++;
                }
                previousDigit = true;
                previousUpperCase = false;
            } else if (ret.charAt(i) == '.') {
                if (!previousDigit && !previousUpperCase) {
                    ret.insert(i, " ");
                    i++;
                }
            } else {
                if (previousDigit) {
                    ret.insert(i, " ");
                    i++;
                }
                previousDigit = false;
                previousUpperCase = false;
            }
        }
        if (toLowerCase) {
            return ret.toString().toLowerCase().replaceAll("\\s+", " ").trim();
        }
        return ret.toString().replaceAll("\\s+", " ").trim();
    }

    public static HashMap<IRI, VerbEntryList> generateDefaultVerbs() {
        HashMap<IRI, VerbEntryList> list = new HashMap<IRI, VerbEntryList>();

        LexEntryVerbEN verbEN;
        LexEntryVerbGR verbGR;
        VerbEntryList entryList;

        //to be
        verbEN = new LexEntryVerbEN("be", "is", "are", "was", "were");
        verbGR = new LexEntryVerbGR();
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "είμαι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "είσαι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "είναι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "είμαστε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "είσαστε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "είναι");

        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "ήμουν");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "ήσουν");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "ήταν");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "ήμασταν");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "ήσασταν");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "ήταν");

        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "είμαι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "είσαι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "είναι");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "είμαστε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "είσαστε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "είναι");

        entryList = new VerbEntryList(verbEN, verbGR);
        list.put(toBeVLE_IRI, entryList);

        //to have
        verbEN = new LexEntryVerbEN("have", "has", "having", "had", "had");
        verbGR = new LexEntryVerbGR();
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "έχω");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "έχεις");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "έχει");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "έχουμε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "έχετε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "έχουν");

        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "είχα");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "είχες");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "είχε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "είχαμε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "είχατε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "είχαν");

        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, "έχω");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, "έχεις");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, "έχει");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, "έχουμε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, "έχετε");
        verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, "έχουν");

        entryList = new VerbEntryList(verbEN, verbGR);
        list.put(toHaveVLE_IRI, entryList);

        return list;
    }

    public static HashMap<IRI, AdjectiveEntryList> generateDefaultAdjectives() {
        HashMap<IRI, AdjectiveEntryList> list = new HashMap<IRI, AdjectiveEntryList>();

        LexEntryAdjectiveEN adjectiveEN;
        LexEntryAdjectiveGR adjectiveGR;
        AdjectiveEntryList entryList;

        //identical
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("identical");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setSingularNominativeMasculine("πανομοιότυπος");
        adjectiveGR.setSingularNominativeFeminine("πανομοιότυπη");
        adjectiveGR.setSingularNominativeNeuter("πανομοιότυπο");
        adjectiveGR.setPluralNominativeMasculine("πανομοιότυποι");
        adjectiveGR.setPluralNominativeFeminine("πανομοιότυπες");
        adjectiveGR.setPluralNominativeNeuter("πανομοιότυπα");

        adjectiveGR.setSingularGenitiveMasculine("πανομοιότυπου");
        adjectiveGR.setSingularGenitiveFeminine("πανομοιότυπης");
        adjectiveGR.setSingularGenitiveNeuter("πανομοιότυπου");
        adjectiveGR.setPluralGenitiveMasculine("πανομοιότυπων");
        adjectiveGR.setPluralGenitiveFeminine("πανομοιότυπων");
        adjectiveGR.setPluralGenitiveNeuter("πανομοιότυπων");

        adjectiveGR.setSingularAccusativeMasculine("πανομοιότυπο");
        adjectiveGR.setSingularAccusativeFeminine("πανομοιότυπη");
        adjectiveGR.setSingularAccusativeNeuter("πανομοιότυπο");
        adjectiveGR.setPluralAccusativeMasculine("πανομοιότυπους");
        adjectiveGR.setPluralAccusativeFeminine("πανομοιότυπες");
        adjectiveGR.setPluralAccusativeNeuter("πανομοιότυπα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(identicalALE_IRI, entryList);

        //most
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("most");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setSingularNominativeMasculine("περισσότερος");
        adjectiveGR.setSingularNominativeFeminine("περισσότερη");
        adjectiveGR.setSingularNominativeNeuter("περισσότερο");
        adjectiveGR.setPluralNominativeMasculine("περισσότεροι");
        adjectiveGR.setPluralNominativeFeminine("περισσότερες");
        adjectiveGR.setPluralNominativeNeuter("περισσότερα");

        adjectiveGR.setSingularGenitiveMasculine("περισσότερου");
        adjectiveGR.setSingularGenitiveFeminine("περισσότερης");
        adjectiveGR.setSingularGenitiveNeuter("περισσότερου");
        adjectiveGR.setPluralGenitiveMasculine("περισσότερων");
        adjectiveGR.setPluralGenitiveFeminine("προηγούμενων");
        adjectiveGR.setPluralGenitiveNeuter("προηγούμενων");

        adjectiveGR.setSingularAccusativeMasculine("περισσότερο");
        adjectiveGR.setSingularAccusativeFeminine("περισσότερη");
        adjectiveGR.setSingularAccusativeNeuter("περισσότερο");
        adjectiveGR.setPluralAccusativeMasculine("περισσότερους");
        adjectiveGR.setPluralAccusativeFeminine("περισσότερες");
        adjectiveGR.setPluralAccusativeNeuter("περισσότερα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(mostALE_IRI, entryList);

        //all
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("all");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setPluralNominativeMasculine("όλοι");
        adjectiveGR.setPluralNominativeFeminine("όλες");
        adjectiveGR.setPluralNominativeNeuter("όλα");

        adjectiveGR.setPluralGenitiveMasculine("όλων");
        adjectiveGR.setPluralGenitiveFeminine("όλων");
        adjectiveGR.setPluralGenitiveNeuter("όλων");

        adjectiveGR.setPluralAccusativeMasculine("όλους");
        adjectiveGR.setPluralAccusativeFeminine("όλες");
        adjectiveGR.setPluralAccusativeNeuter("όλα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(allALE_IRI, entryList);

        //previous
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("previous");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setSingularNominativeMasculine("προηγούμενος");
        adjectiveGR.setSingularNominativeFeminine("προηγούμενη");
        adjectiveGR.setSingularNominativeNeuter("προηγούμενο");
        adjectiveGR.setPluralNominativeMasculine("προηγούμενοι");
        adjectiveGR.setPluralNominativeFeminine("προηγούμενες");
        adjectiveGR.setPluralNominativeNeuter("προηγούμενα");

        adjectiveGR.setSingularGenitiveMasculine("προηγούμενου");
        adjectiveGR.setSingularGenitiveFeminine("προηγούμενης");
        adjectiveGR.setSingularGenitiveNeuter("προηγούμενου");
        adjectiveGR.setPluralGenitiveMasculine("προηγούμενων");
        adjectiveGR.setPluralGenitiveFeminine("προηγούμενων");
        adjectiveGR.setPluralGenitiveNeuter("προηγούμενων");

        adjectiveGR.setSingularAccusativeMasculine("προηγούμενο");
        adjectiveGR.setSingularAccusativeFeminine("προηγούμενη");
        adjectiveGR.setSingularAccusativeNeuter("προηγούμενο");
        adjectiveGR.setPluralAccusativeMasculine("προηγούμενους");
        adjectiveGR.setPluralAccusativeFeminine("προηγούμενες");
        adjectiveGR.setPluralAccusativeNeuter("προηγούμενα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(prevALE_IRI, entryList);

        //other
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("other");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setSingularNominativeMasculine("άλλος");
        adjectiveGR.setSingularNominativeFeminine("άλλη");
        adjectiveGR.setSingularNominativeNeuter("άλλο");
        adjectiveGR.setPluralNominativeMasculine("άλλοι");
        adjectiveGR.setPluralNominativeFeminine("άλλεςες");
        adjectiveGR.setPluralNominativeNeuter("άλλα");

        adjectiveGR.setSingularGenitiveMasculine("άλλου");
        adjectiveGR.setSingularGenitiveFeminine("άλλης");
        adjectiveGR.setSingularGenitiveNeuter("άλλου");
        adjectiveGR.setPluralGenitiveMasculine("άλλων");
        adjectiveGR.setPluralGenitiveFeminine("άλλων");
        adjectiveGR.setPluralGenitiveNeuter("άλλων");

        adjectiveGR.setSingularAccusativeMasculine("άλλο");
        adjectiveGR.setSingularAccusativeFeminine("άλλη");
        adjectiveGR.setSingularAccusativeNeuter("άλλο");
        adjectiveGR.setPluralAccusativeMasculine("άλλους");
        adjectiveGR.setPluralAccusativeFeminine("άλλες");
        adjectiveGR.setPluralAccusativeNeuter("άλλα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(otherALE_IRI, entryList);

        //only
        adjectiveEN = new LexEntryAdjectiveEN();
        adjectiveEN.set_form("only");

        adjectiveGR = new LexEntryAdjectiveGR();
        adjectiveGR.setSingularNominativeMasculine("μόνος");
        adjectiveGR.setSingularNominativeFeminine("μόνη");
        adjectiveGR.setSingularNominativeNeuter("μόνο");
        adjectiveGR.setPluralNominativeMasculine("μόνοι");
        adjectiveGR.setPluralNominativeFeminine("μόνες");
        adjectiveGR.setPluralNominativeNeuter("μόνα");

        adjectiveGR.setSingularGenitiveMasculine("μόνου");
        adjectiveGR.setSingularGenitiveFeminine("μόνης");
        adjectiveGR.setSingularGenitiveNeuter("μόνου");
        adjectiveGR.setPluralGenitiveMasculine("μόνων");
        adjectiveGR.setPluralGenitiveFeminine("μόνων");
        adjectiveGR.setPluralGenitiveNeuter("μόνων");

        adjectiveGR.setSingularAccusativeMasculine("μόνο");
        adjectiveGR.setSingularAccusativeFeminine("μόνη");
        adjectiveGR.setSingularAccusativeNeuter("μόνο");
        adjectiveGR.setPluralAccusativeMasculine("μόνους");
        adjectiveGR.setPluralAccusativeFeminine("μόνες");
        adjectiveGR.setPluralAccusativeNeuter("μόνα");

        entryList = new AdjectiveEntryList(adjectiveEN, adjectiveGR);
        list.put(onlyALE_IRI, entryList);

        return list;
    }

    public static HashMap<IRI, NounEntryList> generateDefaultNouns() {
        HashMap<IRI, NounEntryList> list = new HashMap<IRI, NounEntryList>();

        LexEntryNounEN nounEN;
        LexEntryNounGR nounGR;
        NounEntryList entryList;

        //entity
        nounEN = new LexEntryNounEN();

        nounEN.setGender(LexEntry.GENDER_NEUTER);
        nounEN.setNumber(LexEntry.NUMBER_BOTH);
        nounEN.setSingular("entity");
        nounEN.setPlural("entities");

        nounGR = new LexEntryNounGR();
        nounGR.setGender(LexEntry.GENDER_FEMININE);
        nounGR.setNumber(LexEntry.NUMBER_BOTH);
        nounGR.setSingularNominative("οντότητα");
        nounGR.setSingularGenitive("οντότητας");
        nounGR.setSingularAccusative("οντότητα");
        nounGR.setPluralNominative("οντότητες");
        nounGR.setPluralGenitive("οντοτήτων");
        nounGR.setPluralAccusative("οντότητες");

        entryList = new NounEntryList(nounEN, nounGR);
        list.put(entityNLE_IRI, entryList);

        return list;
    }

    public static ArrayList<NLName> generateDefaultNLNames(String language) {
        ArrayList<NLName> list = new ArrayList<NLName>();
        if (Languages.isEnglish(language)) {
            NLNNounSlot nounSlot;
            ArrayList<NLNSlot> nameSlots;
            NLName name;

            //Entity Sentence Plan
            nounSlot = new NLNNounSlot(entityNLE_IRI, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR, true, false, null, NodeID.getNodeID("entityNLNEN1"), 1);

            nameSlots = new ArrayList<NLNSlot>();
            nameSlots.add(nounSlot);

            name = new NLName(nameSlots, entityNLNEN_IRI, true, false, Languages.ENGLISH);
            list.add(name);
        } else if (Languages.isGreek(language)) {
            NLNNounSlot nounSlot;
            ArrayList<NLNSlot> nameSlots;
            NLName name;

            //Class Assertion Sentence Plan
            nounSlot = new NLNNounSlot(entityNLE_IRI, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR, true, false, null, NodeID.getNodeID("entityNLNGR1"), 1);

            nameSlots = new ArrayList<NLNSlot>();
            nameSlots.add(nounSlot);

            name = new NLName(nameSlots, entityNLNGR_IRI, true, false, Languages.GREEK);
            list.add(name);
        }

        return list;
    }

    public static ArrayList<SentencePlan> generateDefaultSentencePlans(String language) {
        ArrayList<SentencePlan> list = new ArrayList<SentencePlan>();
        if (Languages.isEnglish(language)) {
            SPOwnerSlot ownerSlot;
            SPVerbSlot verbSlot;
            SPAdjectiveSlot adjectiveSlot;
            SPStringSlot stringSlot;
            SPPrepositionSlot prepSlot;
            SPFillerSlot fillerSlot;
            ArrayList<SPSlot> planSlots;
            SentencePlan plan;

            //Class Assertion Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("isASPEN1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("isASPEN1"), NodeID.getNodeID("isASPEN2"), 2);
            fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID("isASPEN3"), 3);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, isASPEN_IRI, true, Languages.ENGLISH);
            plan.setAggAllowed(true);
            list.add(plan);

            //Same Individual Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("sameIndividualSPEN1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("sameIndividualSPEN1"), NodeID.getNodeID("sameIndividualSPEN2"), 2);
            adjectiveSlot = new SPAdjectiveSlot(identicalALE_IRI, "", "", "", NodeID.getNodeID("sameIndividualSPEN1"), NodeID.getNodeID("sameIndividualSPEN3"), 3);
            prepSlot = new SPPrepositionSlot(SPPrepositionSlot.PREPOSITION_EN_TO, NodeID.getNodeID("sameIndividualSPEN4"), 4);
            fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID("sameIndividualSPEN5"), 5);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(adjectiveSlot);
            planSlots.add(prepSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, sameIndividualSPEN_IRI, true, Languages.ENGLISH);
            plan.setAggAllowed(true);
            list.add(plan);

            //kindOf Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("kindOfSPEN1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("kindOfSPEN1"), NodeID.getNodeID("kindOfSPEN2"), 2);
            stringSlot = new SPStringSlot("a kind", NodeID.getNodeID("differentIndividualSPEN3"), 3);
            prepSlot = new SPPrepositionSlot(SPPrepositionSlot.PREPOSITION_EN_OF, NodeID.getNodeID("kindOfSPEN4"), 4);
            fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID("kindOfSPEN5"), 5);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(stringSlot);
            planSlots.add(prepSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, kindOfSPEN_IRI, true, Languages.ENGLISH);
            plan.setAggAllowed(true);
            list.add(plan);
        } else if (Languages.isGreek(language)) {
            SPOwnerSlot ownerSlot;
            SPVerbSlot verbSlot;
            SPAdjectiveSlot adjectiveSlot;
            SPStringSlot stringSlot;
            SPPrepositionSlot prepSlot;
            SPFillerSlot fillerSlot;
            ArrayList<SPSlot> planSlots;
            SentencePlan plan;

            //Class Assertion Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("isASPGR1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("isASPGR1"), NodeID.getNodeID("isASPGR2"), 2);
            fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID("isASPGR3"), 3);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, isASPGR_IRI, true, Languages.GREEK);
            plan.setAggAllowed(true);
            list.add(plan);

            //Same Individual Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("sameIndividualSPGR1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("sameIndividualSPGR1"), NodeID.getNodeID("sameIndividualSPGR2"), 2);
            adjectiveSlot = new SPAdjectiveSlot(identicalALE_IRI, "", "", "", NodeID.getNodeID("sameIndividualSPEN1"), NodeID.getNodeID("sameIndividualSPEN3"), 3);
            prepSlot = new SPPrepositionSlot(SPPrepositionSlot.PREPOSITION_GR_ME, NodeID.getNodeID("sameIndividualSPGR4"), 4);
            fillerSlot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, NodeID.getNodeID("sameIndividualSPGR5"), 5);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(adjectiveSlot);
            planSlots.add(prepSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, sameIndividualSPGR_IRI, true, Languages.GREEK);
            plan.setAggAllowed(true);
            list.add(plan);

            //kindOf Sentence Plan
            ownerSlot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.REF_AUTO, NodeID.getNodeID("kindOfSPGR1"), 1);
            verbSlot = new SPVerbSlot(toBeVLE_IRI, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, "true", "", "", NodeID.getNodeID("kindOfSPGR1"), NodeID.getNodeID("kindOfSPGR2"), 2);
            stringSlot = new SPStringSlot("ένα είδος", NodeID.getNodeID("kindOfSPGR3"), 3);
            fillerSlot = new SPFillerSlot(XmlMsgs.GENITIVE_TAG, false, NodeID.getNodeID("kindOfSPGR4"), 4);

            planSlots = new ArrayList<SPSlot>();
            planSlots.add(ownerSlot);
            planSlots.add(verbSlot);
            planSlots.add(stringSlot);
            planSlots.add(fillerSlot);

            plan = new SentencePlan(planSlots, kindOfSPGR_IRI, true, Languages.GREEK);
            plan.setAggAllowed(true);
            list.add(plan);
        }

        return list;
    }
}
