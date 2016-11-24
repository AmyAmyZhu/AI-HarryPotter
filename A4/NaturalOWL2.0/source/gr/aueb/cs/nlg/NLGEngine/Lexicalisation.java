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
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.SPAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.SPComparatorFillerSlot;
import gr.aueb.cs.nlg.NLFiles.SPComparatorSlot;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationPropertySlot;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationSlot;
import gr.aueb.cs.nlg.NLFiles.SPFillerSlot;
import gr.aueb.cs.nlg.NLFiles.SPNounSlot;
import gr.aueb.cs.nlg.NLFiles.SPOwnerSlot;
import gr.aueb.cs.nlg.NLFiles.SPPrepositionSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SPStringSlot;
import gr.aueb.cs.nlg.NLFiles.SPVerbSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;

import gr.aueb.cs.nlg.Utils.Fact;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import org.w3c.dom.Node;

public class Lexicalisation extends NLGEngineComponent {

    public static final String MAX_CARDINALITY_EN = "at most";
    public static final String MAX_CARDINALITY_GR = "το περισσότερο";
    public static final String MIN_CARDINALITY_EN = "at least";
    public static final String MIN_CARDINALITY_GR = "το λιγότερο";
    public static final String EXACT_CARDINALITY_EN = "exactly";
    public static final String EXACT_CARDINALITY_GR = "ακριβώς";
    public static final String ALL_VALUES_CARDINALITY_EN = "only";
    public static final String ALL_VALUES_CARDINALITY_GR = "μόνο";
    public static final String SOME_VALUES_CARDINALITY_EN = "at least 1";
    public static final String SOME_VALUES_CARDINALITY_GR = "το λιγότερο 1";
    private Set<OWLOntology> mainModels;
    private SentencePlanQueryManager SPQM;

    public Lexicalisation(Set<OWLOntology> ontologies, SentencePlanQueryManager SPQM, String Language) {
        super(Language);
        this.mainModels = ontologies;
        this.SPQM = SPQM;
    }

    public XmlMsgs lexicalizeInstances(XmlMsgs messages) {
        for (Node message : messages.getMessages()) { // for each msg 
            IRI sentencePlanIRI = IRI.create(XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

            if (!XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                if (sentencePlanIRI != null) {
                    applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
                }
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.HAS_VALUE_RESTRICTION_TAG)) {
                applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.DIFFERENT_FROM_TAG)) {
                //....
            } else if (XmlMsgs.compare(message, XmlMsgs.prefix, XmlMsgs.SAME_AS_TAG)) {
                //....
            } else if (!XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                if (!sentencePlanIRI.toString().isEmpty()) {
                    applySentencePlan(messages, message, SPQM.getSlots(sentencePlanIRI));
                }
            }//else
        }// for each msg 
        return messages;
    }

    public void applySentencePlan(XmlMsgs messages, Node messageRoot, ArrayList<SPSlot> slots) {
        if (slots != null) {
            String forProperty = "";
            if (!XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                forProperty = XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.forProperty);
            } else if (!XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                forProperty = XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
            }

            Collections.sort(slots);
            Node before = null;
            for (int i = 0; i < slots.size(); i++) {
                SPSlot slot = slots.get(i);
                if (slot instanceof SPOwnerSlot) {
                    SPOwnerSlot OS = (SPOwnerSlot) slot;
                    messages.addOwnerSlot(messageRoot, forProperty, OS.getCase(), OS.getRefType(), OS.getId());
                } else if (slot instanceof SPFillerSlot) {
                    SPFillerSlot FS = (SPFillerSlot) slot;
                    if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                        if (Languages.isEnglish(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "maxCardinality(" + forProperty + ")", Lexicalisation.MAX_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "maxCardinality(" + forProperty + ")", Lexicalisation.MAX_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        } else if (Languages.isGreek(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "maxCardinality(" + forProperty + ")", Lexicalisation.MAX_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "maxCardinality(" + forProperty + ")", Lexicalisation.MAX_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        }
                        if (Integer.parseInt(XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality).trim()) == 1) {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.SINGULAR, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        } else {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.PLURAL, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        }
                    } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                        if (Languages.isEnglish(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "minCardinality(" + forProperty + ")", Lexicalisation.MIN_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "minCardinality(" + forProperty + ")", Lexicalisation.MIN_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        } else if (Languages.isGreek(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "minCardinality(" + forProperty + ")", Lexicalisation.MIN_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "minCardinality(" + forProperty + ")", Lexicalisation.MIN_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        }
                        if (Integer.parseInt(XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality).trim()) == 1) {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.SINGULAR, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        } else {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.PLURAL, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        }
                    } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                        if (Languages.isEnglish(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "exactCardinality(" + forProperty + ")", Lexicalisation.EXACT_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "exactCardinality(" + forProperty + ")", Lexicalisation.EXACT_CARDINALITY_EN + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        } else if (Languages.isGreek(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "exactCardinality(" + forProperty + ")", Lexicalisation.EXACT_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality), before);
                            } else {
                                messages.addStringSlot(messageRoot, "exactCardinality(" + forProperty + ")", Lexicalisation.EXACT_CARDINALITY_GR + " " + XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality));
                            }
                        }
                        if (Integer.parseInt(XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.cardinality).trim()) == 1) {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.SINGULAR, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        } else {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.PLURAL, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        }
                    } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                        if (Languages.isEnglish(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "allValues(" + forProperty + ")", Lexicalisation.ALL_VALUES_CARDINALITY_EN, before);
                            } else {
                                messages.addStringSlot(messageRoot, "allValues(" + forProperty + ")", Lexicalisation.ALL_VALUES_CARDINALITY_EN);
                            }
                        } else if (Languages.isGreek(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "allValues(" + forProperty + ")", Lexicalisation.ALL_VALUES_CARDINALITY_GR, before);
                            } else {
                                messages.addStringSlot(messageRoot, "allValues(" + forProperty + ")", Lexicalisation.ALL_VALUES_CARDINALITY_GR);
                            }
                        }
                        messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.PLURAL, FS.getBullets(), NLName.FORCE_INDEF_ARTICLE, FS.getId());
                    } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                        if (Languages.isEnglish(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "someValues(" + forProperty + ")", Lexicalisation.SOME_VALUES_CARDINALITY_EN, before);
                            } else {
                                messages.addStringSlot(messageRoot, "someValues(" + forProperty + ")", Lexicalisation.SOME_VALUES_CARDINALITY_EN);
                            }
                        } else if (Languages.isGreek(getLanguage())) {
                            if (before != null) {
                                messages.addStringSlot(messageRoot, "someValues(" + forProperty + ")", Lexicalisation.SOME_VALUES_CARDINALITY_GR, before);
                            } else {
                                messages.addStringSlot(messageRoot, "someValues(" + forProperty + ")", Lexicalisation.SOME_VALUES_CARDINALITY_GR);
                            }
                        }
                        messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), XmlMsgs.SINGULAR, FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                    } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).equals(NLResourceManager.isA.getIRI().toString())) {
                        if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).equals(DefaultResourcesManager.kindOfSPEN_IRI.toString()) || XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).equals(DefaultResourcesManager.kindOfSPGR_IRI.toString())) {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), "", FS.getBullets(), NLName.FORCE_NO_ARTICLE, FS.getId());
                        } else {
                            messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), "", FS.getBullets(), NLName.FORCE_INDEF_ARTICLE, FS.getId());
                        }
                    } else {
                        messages.addFillerSlot(messageRoot, forProperty, FS.getCase(), "", FS.getBullets(), NLName.REGULAR_ARTICLE, FS.getId());
                    }
                } else if (slot instanceof SPVerbSlot) {
                    SPVerbSlot VS = (SPVerbSlot) slot;
                    boolean polarity = true;
                    if (VS.getPolarity().equals(SPVerbSlot.POLARITY_POSITIVE)) {
                        polarity = true;
                    } else if (VS.getPolarity().equals(SPVerbSlot.POLARITY_NEGATIVE)) {
                        polarity = false;
                    } else if (VS.getPolarity().equals(SPVerbSlot.POLARITY_FILLER)) {
                        if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.VALUE).equals("and(true)")) {
                            polarity = true;
                        } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.VALUE).equals("and(false)")) {
                            polarity = false;
                        }
                    }

                    if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.polarity).equals("false")) {
                        before = messages.addVerbSlot(messageRoot, forProperty, VS.getLexiconEntryIRI(), VS.getVoice(), VS.getTense(), VS.getNumber(), VS.getPerson(), VS.getAgreesWithID(), !polarity, VS.getId());
                    } else {
                        before = messages.addVerbSlot(messageRoot, forProperty, VS.getLexiconEntryIRI(), VS.getVoice(), VS.getTense(), VS.getNumber(), VS.getPerson(), VS.getAgreesWithID(), polarity, VS.getId());
                    }
                } else if (slot instanceof SPAdjectiveSlot) {
                    SPAdjectiveSlot AS = (SPAdjectiveSlot) slot;
                    //logger.debug("AdjectiveSlot");

                    messages.addAdjectiveSlot(messageRoot, forProperty, AS.getLexiconEntryIRI(), AS.getCase(), AS.getGender(), AS.getNumber(), AS.getAgreesWithID(), AS.getId());
                } else if (slot instanceof SPNounSlot) {
                    SPNounSlot NS = (SPNounSlot) slot;
                    messages.addNounSlot(messageRoot, forProperty, NS.getLexiconEntryIRI(), NS.getCase(), NS.getNumber(), NS.getAgreesWithID(), NS.getId());
                } else if (slot instanceof SPPrepositionSlot) {
                    SPPrepositionSlot PS = (SPPrepositionSlot) slot;

                    if (i > 0) {
                        if (slots.get(i - 1) instanceof SPVerbSlot) {
                            before = messages.addPrepositionSlot(messageRoot, forProperty, PS.getPrep());
                        } else {
                            messages.addPrepositionSlot(messageRoot, forProperty, PS.getPrep());
                        }
                    } else {
                        messages.addPrepositionSlot(messageRoot, forProperty, PS.getPrep());
                    }
                } else if (slot instanceof SPStringSlot) {
                    SPStringSlot SS = (SPStringSlot) slot;
                    messages.addStringSlot(messageRoot, forProperty, SS.getText());
                } else if (slot instanceof SPConcatenationSlot) {
                    SPConcatenationSlot CS = (SPConcatenationSlot) slot;
                    ArrayList<SPConcatenationPropertySlot> CPSs = CS.getSortedPropertySlots();

                    ArrayList<String> values = Fact.parseModifier(XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.VALUE));

                    boolean first = true;
                    for (String value : values) {
                        if (!first) {
                            if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.VALUE).startsWith("and(")) {
                                messages.addStringSlot(messageRoot, forProperty, XmlMsgs.CONNECTIVE_2ND_LEVEL);
                            } else if (XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.VALUE).startsWith("or(")) {
                                messages.addStringSlot(messageRoot, forProperty, XmlMsgs.DISJUNCTIVE_2ND_LEVEL);
                            }
                        }
                        first = false;

                        Set<OWLObjectPropertyAssertionAxiom> concatObjectProperties = new HashSet<OWLObjectPropertyAssertionAxiom>();
                        Set<OWLDataPropertyAssertionAxiom> concatDataProperties = new HashSet<OWLDataPropertyAssertionAxiom>();
                        IRI concatOwnerIRI = IRI.create(value);

                        Set<OWLEntity> entities = new HashSet<OWLEntity>();
                        for (OWLOntology ontology : mainModels) {
                            entities.addAll(ontology.getEntitiesInSignature(concatOwnerIRI, true));
                        }
                        for (OWLEntity concatOwner : entities) {
                            if (concatOwner.isOWLNamedIndividual()) {
                                for (OWLOntology ontology : mainModels) {
                                    concatObjectProperties.addAll(ontology.getObjectPropertyAssertionAxioms(concatOwner.asOWLNamedIndividual()));
                                    concatDataProperties.addAll(ontology.getDataPropertyAssertionAxioms(concatOwner.asOWLNamedIndividual()));
                                }
                            }
                        }

                        for (int j = 0; j < CPSs.size(); j++) {
                            IRI propertyIRI = CPSs.get(j).getPropertyIRI();

                            boolean containsObject = false;
                            boolean containsData = false;
                            for (OWLOntology ontology : mainModels) {
                                if (ontology.containsObjectPropertyInSignature(propertyIRI, true)) {
                                    containsObject = true;
                                }
                                if (ontology.containsDataPropertyInSignature(propertyIRI, true)) {
                                    containsData = true;
                                }
                            }
                            if (containsObject) {
                                for (OWLObjectPropertyAssertionAxiom concatProperty : concatObjectProperties) {
                                    if (concatProperty.getProperty().asOWLObjectProperty().getIRI().equals(propertyIRI)) {
                                        if (concatProperty.getObject().isNamed()) {
                                            messages.addConcatenationIndividualSlot(messageRoot, forProperty, concatProperty.getObject().asOWLNamedIndividual().getIRI(), CPSs.get(j).getCaseType());
                                        }
                                    }
                                }
                            } else if (containsData) {
                                for (OWLDataPropertyAssertionAxiom concatProperty : concatDataProperties) {
                                    if (concatProperty.getProperty().asOWLDataProperty().getIRI().equals(propertyIRI)) {
                                        messages.addStringSlot(messageRoot, forProperty, concatProperty.getObject().getLiteral());
                                    }
                                }
                            }
                        }
                    }
                } else if (slot instanceof SPComparatorSlot) {
                    SPComparatorSlot CS = (SPComparatorSlot) slot;
                    messages.addComparatorSlot(messageRoot, forProperty, CS.getCase(), CS.isMany(), NLName.REGULAR_ARTICLE, CS.getId());
                } else if (slot instanceof SPComparatorFillerSlot) {
                    SPComparatorFillerSlot CS = (SPComparatorFillerSlot) slot;
                    messages.addComparatorFillerSlot(messageRoot, forProperty, CS.getCase(), CS.isMany(), NLName.REGULAR_ARTICLE, CS.getId());
                }
            }// for each slot
        }
    }//transform
}//Lexicalisation