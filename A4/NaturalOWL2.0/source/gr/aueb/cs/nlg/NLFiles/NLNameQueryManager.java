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

import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClassExpression;

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class NLNameQueryManager {

    private NLNamesList GreekNLNameList;
    private NLNamesList EnglishNLNameList;
    NLResourceManager NLResourcesManager;

    //constructor
    public NLNameQueryManager(NLResourceManager NLResourcesManager) {
        this.NLResourcesManager = NLResourcesManager;
        init();
    }

    private void init() {
        GreekNLNameList = new NLNamesList(Languages.GREEK);
        EnglishNLNameList = new NLNamesList(Languages.ENGLISH);

        for (NLName plan : DefaultResourcesManager.generateDefaultNLNames(Languages.GREEK)) {
            GreekNLNameList.add(plan);
        }
        for (NLName plan : DefaultResourcesManager.generateDefaultNLNames(Languages.ENGLISH)) {
            EnglishNLNameList.add(plan);
        }
    }

    public void importNLNames(OWLOntology NLResourcesModel) {
        Set<OWLClassAssertionAxiom> nlNameAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.NLName);

        for (OWLClassAssertionAxiom nlNameAssertation : nlNameAssertations) {
            OWLIndividual nlNameEntry = nlNameAssertation.getIndividual();

            if (EnglishNLNameList.containsNLName(nlNameEntry.asOWLNamedIndividual().getIRI())) {
                EnglishNLNameList.removeNLName(nlNameEntry.asOWLNamedIndividual().getIRI());
            }
            if (GreekNLNameList.containsNLName(nlNameEntry.asOWLNamedIndividual().getIRI())) {
                GreekNLNameList.removeNLName(nlNameEntry.asOWLNamedIndividual().getIRI());
            }

            Set<OWLIndividual> forLanguages = nlNameEntry.getObjectPropertyValues(NLResourceManager.forLanguage, NLResourcesModel);

            for (OWLIndividual language : forLanguages) {
                if (language.equals(NLResourceManager.greekLanguage)) {
                    GreekNLNameList.add(createNLName(nlNameEntry, Languages.GREEK, NLResourcesModel));
                } else if (language.equals(NLResourceManager.englishLanguage)) {
                    EnglishNLNameList.add(createNLName(nlNameEntry, Languages.ENGLISH, NLResourcesModel));
                }
            }
        }
    }//LoadNLNames

    private NLName createNLName(OWLIndividual nlName, String lang, OWLOntology NLResourcesModel) {
        String aggAllowed = "true";
        Set<OWLLiteral> literals = nlName.getDataPropertyValues(NLResourceManager.aggregationAllowed, NLResourcesModel);

        for (OWLLiteral literal : literals) {
            aggAllowed = literal.getLiteral();
        }

        String focusLost = "false";
        literals = nlName.getDataPropertyValues(NLResourceManager.focusLost, NLResourcesModel);

        for (OWLLiteral literal : literals) {
            focusLost = literal.getLiteral();
        }

        ArrayList<NLNSlot> slots = createSlots(nlName, NLResourcesModel);

        return new NLName(slots, nlName.asOWLNamedIndividual().getIRI(), (aggAllowed.compareTo("true") == 0) ? true : false, (focusLost.compareTo("true") == 0) ? true : false, lang);
    }

    public ArrayList<NLNSlot> createSlots(OWLIndividual nlName, OWLOntology NLResourcesModel) {
        ArrayList<NLNSlot> nlNameSlots = new ArrayList<NLNSlot>();

        Set<OWLLiteral> literals;
        Set<OWLIndividual> individuals;

        int order = -1;
        String caseType = "";
        boolean definite = false;
        String text = "";
        IRI entryIRI = null;
        boolean isHead = false;
        boolean isCapitalized = false;
        String gender = "";
        String number = "";
        NodeID agreesWithID = null;

        Set<OWLIndividual> slots = nlName.getObjectPropertyValues(NLResourceManager.hasSlot, NLResourcesModel);

        for (OWLIndividual slot : slots) {
            //logger.debug("adding Slot");
            Set<OWLClassExpression> slotTypes = slot.getTypes(NLResourcesModel);

            for (OWLClassExpression slotType : slotTypes) {
                if (slotType.asOWLClass().getIRI().equals(NLResourceManager.ArticleSlot.getIRI())) {
                    caseType = "";
                    gender = "";
                    number = "";
                    definite = false;
                    agreesWithID = null;

                    literals = slot.getDataPropertyValues(NLResourceManager.isDefinite, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        definite = literal.parseBoolean();
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.agreeWith, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        agreesWithID = individual.asOWLAnonymousIndividual().getID();
                    }

                    if (agreesWithID == null) {
                        individuals = slot.getObjectPropertyValues(NLResourceManager.useCase, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.nominativeCase)) {
                                caseType = XmlMsgs.NOMINATIVE_TAG;
                            } else if (individual.equals(NLResourceManager.genitiveCase)) {
                                caseType = XmlMsgs.GENITIVE_TAG;
                            } else if (individual.equals(NLResourceManager.accusativeCase)) {
                                caseType = XmlMsgs.ACCUSATIVE_TAG;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useGender, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.masculineGender)) {
                                gender = XmlMsgs.GENDER_MASCULINE;
                            } else if (individual.equals(NLResourceManager.feminineGender)) {
                                gender = XmlMsgs.GENDER_FEMININE;
                            } else if (individual.equals(NLResourceManager.neuterGender)) {
                                gender = XmlMsgs.GENDER_NEUTER;
                            } else if (individual.equals(NLResourceManager.masculineOrFeminineGender)) {
                                gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useNumber, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.singularNumber)) {
                                number = XmlMsgs.SINGULAR;
                            } else if (individual.equals(NLResourceManager.pluralNumber)) {
                                number = XmlMsgs.PLURAL;
                            } else if (individual.equals(NLResourceManager.bothNumbers)) {
                                number = LexEntry.NUMBER_BOTH;
                            }
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    NLNArticleSlot AS = new NLNArticleSlot(definite, caseType, gender, number, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);
                    nlNameSlots.add(AS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.AdjectiveSlot.getIRI())) {
                    entryIRI = null;
                    caseType = "";
                    gender = "";
                    number = "";
                    isHead = false;
                    isCapitalized = false;
                    agreesWithID = null;

                    individuals = slot.getObjectPropertyValues(NLResourceManager.useLexiconEntry, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        entryIRI = individual.asOWLNamedIndividual().getIRI();
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.agreeWith, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        agreesWithID = individual.asOWLAnonymousIndividual().getID();
                    }

                    if (agreesWithID == null) {
                        individuals = slot.getObjectPropertyValues(NLResourceManager.useCase, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.nominativeCase)) {
                                caseType = XmlMsgs.NOMINATIVE_TAG;
                            } else if (individual.equals(NLResourceManager.genitiveCase)) {
                                caseType = XmlMsgs.GENITIVE_TAG;
                            } else if (individual.equals(NLResourceManager.accusativeCase)) {
                                caseType = XmlMsgs.ACCUSATIVE_TAG;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useGender, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.masculineGender)) {
                                gender = XmlMsgs.GENDER_MASCULINE;
                            } else if (individual.equals(NLResourceManager.feminineGender)) {
                                gender = XmlMsgs.GENDER_FEMININE;
                            } else if (individual.equals(NLResourceManager.neuterGender)) {
                                gender = XmlMsgs.GENDER_NEUTER;
                            } else if (individual.equals(NLResourceManager.masculineOrFeminineGender)) {
                                gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useNumber, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.singularNumber)) {
                                number = XmlMsgs.SINGULAR;
                            } else if (individual.equals(NLResourceManager.pluralNumber)) {
                                number = XmlMsgs.PLURAL;
                            } else if (individual.equals(NLResourceManager.bothNumbers)) {
                                number = LexEntry.NUMBER_BOTH;
                            }
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.isHead, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        isHead = literal.parseBoolean();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.isCapitalized, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        isCapitalized = literal.parseBoolean();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    NLNAdjectiveSlot ES = new NLNAdjectiveSlot(entryIRI, caseType, gender, number, isHead, isCapitalized, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);
                    nlNameSlots.add(ES);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.NounSlot.getIRI())) {
                    entryIRI = null;
                    caseType = "";
                    number = "";
                    isHead = false;
                    isCapitalized = false;
                    agreesWithID = null;

                    individuals = slot.getObjectPropertyValues(NLResourceManager.useLexiconEntry, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        entryIRI = individual.asOWLNamedIndividual().getIRI();
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.agreeWith, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        agreesWithID = individual.asOWLAnonymousIndividual().getID();
                    }

                    if (agreesWithID == null) {
                        individuals = slot.getObjectPropertyValues(NLResourceManager.useCase, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.nominativeCase)) {
                                caseType = XmlMsgs.NOMINATIVE_TAG;
                            } else if (individual.equals(NLResourceManager.genitiveCase)) {
                                caseType = XmlMsgs.GENITIVE_TAG;
                            } else if (individual.equals(NLResourceManager.accusativeCase)) {
                                caseType = XmlMsgs.ACCUSATIVE_TAG;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useNumber, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.singularNumber)) {
                                number = XmlMsgs.SINGULAR;
                            } else if (individual.equals(NLResourceManager.pluralNumber)) {
                                number = XmlMsgs.PLURAL;
                            } else if (individual.equals(NLResourceManager.bothNumbers)) {
                                number = LexEntry.NUMBER_BOTH;
                            }
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.isHead, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        isHead = literal.parseBoolean();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.isCapitalized, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        isCapitalized = literal.parseBoolean();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    NLNNounSlot NS = new NLNNounSlot(entryIRI, caseType, number, isHead, isCapitalized, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);
                    nlNameSlots.add(NS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.StringSlot.getIRI())) {
                    text = "";

                    literals = slot.getDataPropertyValues(NLResourceManager.hasString, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        text = literal.getLiteral();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    NLNStringSlot SS = new NLNStringSlot(text, slot.asOWLAnonymousIndividual().getID(), order);
                    nlNameSlots.add(SS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.PrepositionSlot.getIRI())) {
                    text = "";

                    literals = slot.getDataPropertyValues(NLResourceManager.hasString, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        text = literal.getLiteral();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    NLNPrepositionSlot PS = new NLNPrepositionSlot(text, slot.asOWLAnonymousIndividual().getID(), order);
                    nlNameSlots.add(PS);
                }
            }
        }

        return nlNameSlots;
    }

    public boolean getIsAggAllowed(IRI NLNameIRI, String language) {
        if (Languages.isEnglish(language)) {
            NLName name = EnglishNLNameList.getNLName(NLNameIRI);

            if (name != null) {
                return name.getAggAllowed();
            }
        } else if (Languages.isGreek(language)) {
            NLName name = GreekNLNameList.getNLName(NLNameIRI);

            if (name != null) {
                return name.getAggAllowed();
            }
        }
        return false;
    }

    public boolean getFocusLost(IRI NLNameIRI, String language) {
        if (Languages.isEnglish(language)) {
            NLName name = EnglishNLNameList.getNLName(NLNameIRI);

            if (name != null) {
                return name.getFocusLost();
            }
        } else if (Languages.isGreek(language)) {
            NLName name = GreekNLNameList.getNLName(NLNameIRI);

            if (name != null) {
                return name.getFocusLost();
            }
        }
        return false;
    }

    public NLResourceManager getNLResourcesManager() {
        return NLResourcesManager;
    }

    // get a list of all sentence plans for a specific language
    public NLNamesList getNLNamesList(String language) {
        if (Languages.isEnglish(language)) {
            return EnglishNLNameList;
        } else if (Languages.isGreek(language)) {
            return GreekNLNameList;
        }
        return null;
    }

    /* toIRI - IRI to be copied TO
     * fromIRI - IRI to be copied FROM
     */
    public void duplicateNLNameInLists(IRI fromIRI, IRI toIRI, String toLanguage) {
        NLName fromName = null;
        if (EnglishNLNameList.containsNLName(fromIRI)) {
            fromName = EnglishNLNameList.getNLName(fromIRI);
        } else if (GreekNLNameList.containsNLName(fromIRI)) {
            fromName = GreekNLNameList.getNLName(fromIRI);
        }

        if (fromName != null) {
            ArrayList<NLNSlot> slots = new ArrayList<NLNSlot>();
            for (NLNSlot slot : fromName.getSlotsList()) {
                if (slot instanceof NLNArticleSlot) {
                    NLNArticleSlot slotCopy = new NLNArticleSlot((NLNArticleSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNArticleSlot) slot).getOrder()).getID());
                    if (((NLNArticleSlot) slot).getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNArticleSlot) slot).getAgreesWithID().toString().substring(((NLNArticleSlot) slot).getAgreesWithID().toString().indexOf(NLNSlot.anonymousIndivPattern) + NLNSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromName.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setCase(XmlMsgs.NOMINATIVE_TAG);
                        slotCopy.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                    } else if (fromName.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setCase("");
                        slotCopy.setGender("");
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof NLNAdjectiveSlot) {
                    NLNAdjectiveSlot slotCopy = new NLNAdjectiveSlot((NLNAdjectiveSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNAdjectiveSlot) slot).getOrder()).getID());
                    if (((NLNAdjectiveSlot) slot).getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNAdjectiveSlot) slot).getAgreesWithID().toString().substring(((NLNAdjectiveSlot) slot).getAgreesWithID().toString().indexOf(NLNSlot.anonymousIndivPattern) + NLNSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromName.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setCase(XmlMsgs.NOMINATIVE_TAG);
                        slotCopy.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                    } else if (fromName.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setCase("");
                        slotCopy.setGender("");
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof NLNNounSlot) {
                    NLNNounSlot slotCopy = new NLNNounSlot((NLNNounSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNNounSlot) slot).getOrder()).getID());
                    if (((NLNNounSlot) slot).getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNNounSlot) slot).getAgreesWithID().toString().substring(((NLNNounSlot) slot).getAgreesWithID().toString().indexOf(NLNSlot.anonymousIndivPattern) + NLNSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromName.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setCase(XmlMsgs.NOMINATIVE_TAG);
                    } else if (fromName.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setCase("");
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof NLNPrepositionSlot) {
                    NLNPrepositionSlot slotCopy = new NLNPrepositionSlot((NLNPrepositionSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNPrepositionSlot) slot).getOrder()).getID());

                    if (fromName.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setPrep(NLNPrepositionSlot.PREPOSITION_GR_ANEU);
                    } else if (fromName.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setPrep(NLNPrepositionSlot.PREPOSITION_EN_ABOARD);
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof NLNStringSlot) {
                    NLNStringSlot slotCopy = new NLNStringSlot((NLNStringSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNStringSlot) slot).getOrder()).getID());

                    slots.add(slotCopy);
                }
            }

            if (toLanguage.equals(Languages.ENGLISH)) {
                EnglishNLNameList.add(new NLName(slots, toIRI, fromName.getAggAllowed(), fromName.getFocusLost(), Languages.ENGLISH));
            } else if (toLanguage.equals(Languages.GREEK)) {
                GreekNLNameList.add(new NLName(slots, toIRI, fromName.getAggAllowed(), fromName.getFocusLost(), Languages.GREEK));
            }
        }
    }

    public NLName getNLName(IRI NLNameIRI, String language) {
        if (Languages.isEnglish(language)) {
            return EnglishNLNameList.getNLName(NLNameIRI);
        } else if (Languages.isGreek(language)) {
            return GreekNLNameList.getNLName(NLNameIRI);
        }
        return null;
    }

    public NLName getNLName(IRI NLNameIRI) {
        if (EnglishNLNameList.containsNLName(NLNameIRI)) {
            return EnglishNLNameList.getNLName(NLNameIRI);
        } else if (GreekNLNameList.containsNLName(NLNameIRI)) {
            return GreekNLNameList.getNLName(NLNameIRI);
        }
        return null;
    }

    public ArrayList<NLNSlot> getSlots(IRI NLNameIRI) {
        if (EnglishNLNameList.containsNLName(NLNameIRI)) {
            return EnglishNLNameList.getNLName(NLNameIRI).getSlotsList();
        } else if (GreekNLNameList.containsNLName(NLNameIRI)) {
            return GreekNLNameList.getNLName(NLNameIRI).getSlotsList();
        }
        return null;
    }

    public void addNLName(IRI entryIRI, String lang) {
        ArrayList<NLNSlot> slots = new ArrayList<NLNSlot>();

        NLNArticleSlot AS = new NLNArticleSlot(false, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, null, getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(entryIRI.getFragment() + "_" + NLNSlot.anonymousIndivPattern + 1).getID(), 1);
        slots.add(AS);

        if (Languages.isEnglish(lang)) {
            EnglishNLNameList.add(new NLName(slots, entryIRI, true, false, Languages.ENGLISH));
        } else if (Languages.isGreek(lang)) {
            GreekNLNameList.add(new NLName(slots, entryIRI, true, false, Languages.GREEK));
        }
    }

    public void addNLName(NLName name, String lang) {
        if (Languages.isEnglish(lang)) {
            EnglishNLNameList.add(name);
        } else if (Languages.isGreek(lang)) {
            GreekNLNameList.add(name);
        }
    }

    public void removeNLName(IRI entryIRI) {
        if (EnglishNLNameList.containsNLName(entryIRI)) {
            EnglishNLNameList.removeNLName(entryIRI);
        }
        if (GreekNLNameList.containsNLName(entryIRI)) {
            GreekNLNameList.removeNLName(entryIRI);
        }
    }

    public void exportNLNames(OWLOntology resourceOntology) {
        OWLDataFactory factory = NLResourcesManager.getDataFactory();

        ArrayList<NLName> nlNameMasterList = new ArrayList<NLName>();

        nlNameMasterList.addAll(EnglishNLNameList.getNLNamesList());
        nlNameMasterList.addAll(GreekNLNameList.getNLNamesList());

        for (NLName name : nlNameMasterList) {
            if (DefaultResourcesManager.isDefaultResource(name.getNLNameIRI()) || !name.getNLNameIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
                IRI nlNameIRI = name.getNLNameIRI();

                OWLClassAssertionAxiom classAssertion;

                OWLClass cls = NLResourceManager.NLName;
                OWLNamedIndividual indivPlan = factory.getOWLNamedIndividual(nlNameIRI);
                classAssertion = factory.getOWLClassAssertionAxiom(cls, indivPlan);
                NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                OWLObjectProperty objectProperty = NLResourceManager.forLanguage;
                OWLObjectPropertyAssertionAxiom objectAssertion = null;
                if (Languages.isEnglish(name.getLanguage())) {
                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, NLResourceManager.englishLanguage);
                } else if (Languages.isGreek(name.getLanguage())) {
                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, NLResourceManager.greekLanguage);
                }
                AddAxiom addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                OWLDataProperty dataProperty = NLResourceManager.aggregationAllowed;
                OWLDataPropertyAssertionAxiom dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, indivPlan, name.getAggAllowed());
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                dataProperty = NLResourceManager.focusLost;
                dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, indivPlan, name.getFocusLost());
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                ArrayList<NLNSlot> slots = name.getSlotsList();

                OWLNamedIndividual indiv;
                OWLAnonymousIndividual anIndiv;

                for (NLNSlot slot : slots) {
                    if (slot instanceof NLNArticleSlot) {
                        cls = NLResourceManager.ArticleSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.isDefinite;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNArticleSlot) slot).isDefinite());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((NLNArticleSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((NLNArticleSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useCase;
                            if (((NLNArticleSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                            } else if (((NLNArticleSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                            } else if (((NLNArticleSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            objectProperty = NLResourceManager.useGender;
                            if (((NLNArticleSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.masculineGender);
                            } else if (((NLNArticleSlot) slot).getGender().equals(XmlMsgs.GENDER_FEMININE)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.feminineGender);
                            } else if (((NLNArticleSlot) slot).getGender().equals(XmlMsgs.GENDER_NEUTER)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.neuterGender);
                            } else if (((NLNArticleSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE_OR_FEMININE)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.masculineOrFeminineGender);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            objectProperty = NLResourceManager.useNumber;
                            if (((NLNArticleSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((NLNArticleSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((NLNArticleSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }
                        }

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof NLNAdjectiveSlot) {
                        cls = NLResourceManager.AdjectiveSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useLexiconEntry;
                        indiv = factory.getOWLNamedIndividual(((NLNAdjectiveSlot) slot).getLexiconEntryIRI());
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.isHead;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNAdjectiveSlot) slot).isHead());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.isCapitalized;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNAdjectiveSlot) slot).isCapitalized());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((NLNAdjectiveSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((NLNAdjectiveSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useNumber;
                            if (((NLNAdjectiveSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((NLNAdjectiveSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((NLNAdjectiveSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            if (Languages.isGreek(name.getLanguage())) {
                                objectProperty = NLResourceManager.useCase;
                                if (((NLNAdjectiveSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                                } else if (((NLNAdjectiveSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                                } else if (((NLNAdjectiveSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                                } else {
                                    objectAssertion = null;
                                }
                                if (objectAssertion != null) {
                                    addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                                }

                                objectProperty = NLResourceManager.useGender;
                                if (((NLNAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.masculineGender);
                                } else if (((NLNAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_FEMININE)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.feminineGender);
                                } else if (((NLNAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_NEUTER)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.neuterGender);
                                } else if (((NLNAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE_OR_FEMININE)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.masculineOrFeminineGender);
                                } else {
                                    objectAssertion = null;
                                }
                                if (objectAssertion != null) {
                                    addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                                }
                            }
                        }

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof NLNNounSlot) {
                        cls = NLResourceManager.NounSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useLexiconEntry;
                        indiv = factory.getOWLNamedIndividual(((NLNNounSlot) slot).getLexiconEntryIRI());
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.isHead;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNNounSlot) slot).isHead());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.isCapitalized;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNNounSlot) slot).isCapitalized());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((NLNNounSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((NLNNounSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useNumber;
                            if (((NLNNounSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((NLNNounSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((NLNNounSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            if (Languages.isGreek(name.getLanguage())) {
                                objectProperty = NLResourceManager.useCase;
                                if (((NLNNounSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                                } else if (((NLNNounSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                                } else if (((NLNNounSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                                } else {
                                    objectAssertion = null;
                                }
                                if (objectAssertion != null) {
                                    addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                                }
                            }
                        }

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof NLNStringSlot) {
                        cls = NLResourceManager.StringSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasString;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNStringSlot) slot).getText());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof NLNPrepositionSlot) {
                        cls = NLResourceManager.PrepositionSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasString;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((NLNPrepositionSlot) slot).getPrep());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    }
                }
            }
        }
    }
}