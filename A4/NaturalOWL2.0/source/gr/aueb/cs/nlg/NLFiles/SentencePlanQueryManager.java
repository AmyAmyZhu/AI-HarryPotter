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

import java.util.List;
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

public class SentencePlanQueryManager {

    public List<String> PropertiesUsedForComparisons;
    private SentencePlansList GreekSentencePlanList;
    private SentencePlansList EnglishSentencePlanList;
    private NLResourceManager NLResourcesManager;

    //constructor
    public SentencePlanQueryManager(NLResourceManager NLResourcesManager) {
        this.NLResourcesManager = NLResourcesManager;
        init();
    }

    //Load microplans info
    private void init() {
        GreekSentencePlanList = new SentencePlansList(Languages.GREEK); //!!!
        EnglishSentencePlanList = new SentencePlansList(Languages.ENGLISH); //!!!

        for (SentencePlan plan : DefaultResourcesManager.generateDefaultSentencePlans(Languages.GREEK)) {
            GreekSentencePlanList.add(plan);
        }
        for (SentencePlan plan : DefaultResourcesManager.generateDefaultSentencePlans(Languages.ENGLISH)) {
            EnglishSentencePlanList.add(plan);
        }
    }

    public void importSentencePlans(OWLOntology NLResourcesModel) {
        //LoadSentencePlans
        Set<OWLClassAssertionAxiom> sentencePlanAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.SentencePlan);

        for (OWLClassAssertionAxiom sentencePlanAssertation : sentencePlanAssertations) {
            OWLIndividual sentencePlanEntry = sentencePlanAssertation.getIndividual();

            if (EnglishSentencePlanList.containsSentencePlan(sentencePlanEntry.asOWLNamedIndividual().getIRI())) {
                EnglishSentencePlanList.removeSentencePlan(sentencePlanEntry.asOWLNamedIndividual().getIRI());
            }
            if (GreekSentencePlanList.containsSentencePlan(sentencePlanEntry.asOWLNamedIndividual().getIRI())) {
                GreekSentencePlanList.removeSentencePlan(sentencePlanEntry.asOWLNamedIndividual().getIRI());
            }

            Set<OWLIndividual> forLanguages = sentencePlanEntry.getObjectPropertyValues(NLResourceManager.forLanguage, NLResourcesModel);

            for (OWLIndividual language : forLanguages) {
                if (language.equals(NLResourceManager.greekLanguage)) {
                    GreekSentencePlanList.add(createSentencePlan(sentencePlanEntry, Languages.GREEK, NLResourcesModel));
                } else if (language.equals(NLResourceManager.englishLanguage)) {
                    EnglishSentencePlanList.add(createSentencePlan(sentencePlanEntry, Languages.ENGLISH, NLResourcesModel));
                }
            }
        }
    }//LoadSentencePlans

    private SentencePlan createSentencePlan(OWLIndividual sentencePlan, String lang, OWLOntology NLResourcesModel) {
        String aggAllowed = "true";
        Set<OWLLiteral> literals = sentencePlan.getDataPropertyValues(NLResourceManager.aggregationAllowed, NLResourcesModel);

        for (OWLLiteral literal : literals) {
            aggAllowed = literal.getLiteral();
        }

        ArrayList<SPSlot> slots = createSlots(sentencePlan, NLResourcesModel);

        return new SentencePlan(slots, sentencePlan.asOWLNamedIndividual().getIRI(), (aggAllowed.compareTo("true") == 0) ? true : false, lang);
    }

    public ArrayList<SPSlot> createSlots(OWLIndividual sentencePlan, OWLOntology NLResourcesModel) {
        ArrayList<SPSlot> sentencePlanSlots = new ArrayList<SPSlot>();

        Set<OWLLiteral> literals;
        Set<OWLIndividual> individuals;

        int order = -1;
        String refType = "";
        String caseType = "";
        boolean bullets = false;
        String text = "";
        IRI entryIRI = null;
        String tense = "";
        String voice = "";
        String polarity = "";
        String gender = "";
        String number = "";
        String person = "";
        NodeID agreesWithID = null;

        Set<OWLIndividual> slots = sentencePlan.getObjectPropertyValues(NLResourceManager.hasSlot, NLResourcesModel);

        for (OWLIndividual slot : slots) {
            //logger.debug("adding Slot");
            Set<OWLClassExpression> slotTypes = slot.getTypes(NLResourcesModel);

            for (OWLClassExpression slotType : slotTypes) {
                if (slotType.asOWLClass().getIRI().equals(NLResourceManager.OwnerSlot.getIRI())) {
                    //logger.debug("adding OwnerSlot");
                    refType = "";
                    caseType = "";

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

                    individuals = slot.getObjectPropertyValues(NLResourceManager.refExpressionType, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        if (individual.equals(NLResourceManager.autoRefExpression)) {
                            refType = XmlMsgs.REF_AUTO;
                        } else if (individual.equals(NLResourceManager.pronounRefExpression)) {
                            refType = XmlMsgs.REF_PRONOUN;
                        } else if (individual.equals(NLResourceManager.demonstrativeRefExpression)) {
                            refType = XmlMsgs.REF_DEMONSTRATIVE;
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPOwnerSlot OS = new SPOwnerSlot(caseType, refType, slot.asOWLAnonymousIndividual().getID(), order);
                    sentencePlanSlots.add(OS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.FillerSlot.getIRI())) {
                    //logger.debug("adding FillerSlot"); 
                    caseType = "";
                    bullets = false;

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

                    literals = slot.getDataPropertyValues(NLResourceManager.useBullets, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        bullets = literal.parseBoolean();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPFillerSlot FS = new SPFillerSlot(caseType, bullets, slot.asOWLAnonymousIndividual().getID(), order);
                    sentencePlanSlots.add(FS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.VerbSlot.getIRI())) {
                    //logger.debug("adding LexiconEntrySlot");
                    entryIRI = null;
                    tense = "";
                    voice = "";
                    polarity = "";
                    number = "";
                    person = "";
                    agreesWithID = null;

                    individuals = slot.getObjectPropertyValues(NLResourceManager.useLexiconEntry, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        entryIRI = individual.asOWLNamedIndividual().getIRI();
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.agreeWith, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        agreesWithID = individual.asOWLAnonymousIndividual().getID();
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.useTense, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        if (individual.equals(NLResourceManager.simplePresentTense)) {
                            tense = XmlMsgs.TENSE_SIMPLE_PRESENT;
                        } else if (individual.equals(NLResourceManager.presentContinuousTense)) {
                            tense = XmlMsgs.TENSE_PRESENT_CONTINUOUS;
                        } else if (individual.equals(NLResourceManager.presentPerfectTense)) {
                            tense = XmlMsgs.TENSE_PRESENT_PERFECT;
                        } else if (individual.equals(NLResourceManager.simplePastTense)) {
                            tense = XmlMsgs.TENSE_SIMPLE_PAST;
                        } else if (individual.equals(NLResourceManager.pastContinuousTense)) {
                            tense = XmlMsgs.TENSE_PAST_CONTINUOUS;
                        } else if (individual.equals(NLResourceManager.pastPerfectTense)) {
                            tense = XmlMsgs.TENSE_PAST_PERFECT;
                        } else if (individual.equals(NLResourceManager.pastPerfectContinuousTense)) {
                            tense = XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS;
                        } else if (individual.equals(NLResourceManager.simpleFutureTense)) {
                            tense = XmlMsgs.TENSE_SIMPLE_FUTURE;
                        } else if (individual.equals(NLResourceManager.futureContinuousTense)) {
                            tense = XmlMsgs.TENSE_FUTURE_CONTINUOUS;
                        } else if (individual.equals(NLResourceManager.futurePerfectTense)) {
                            tense = XmlMsgs.TENSE_FUTURE_PERFECT;
                        } else if (individual.equals(NLResourceManager.futurePerfectContinuousTense)) {
                            tense = XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS;
                        } else if (individual.equals(NLResourceManager.infinitiveTense)) {
                            tense = XmlMsgs.TENSE_INFINITIVE;
                        } else if (individual.equals(NLResourceManager.participleTense)) {
                            tense = XmlMsgs.TENSE_PARTICIPLE;
                        }
                    }

                    individuals = slot.getObjectPropertyValues(NLResourceManager.useVoice, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        if (individual.equals(NLResourceManager.activeVoice)) {
                            voice = XmlMsgs.ACTIVE_VOICE;
                        } else if (individual.equals(NLResourceManager.passiveVoice)) {
                            voice = XmlMsgs.PASSIVE_VOICE;
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.usePolarity, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        polarity = literal.getLiteral();
                    }

                    if (agreesWithID == null) {
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

                        individuals = slot.getObjectPropertyValues(NLResourceManager.usePerson, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.firstPerson)) {
                                person = XmlMsgs.PERSON_1ST;
                            } else if (individual.equals(NLResourceManager.secondPerson)) {
                                person = XmlMsgs.PERSON_2ND;
                            } else if (individual.equals(NLResourceManager.thirdPerson)) {
                                person = XmlMsgs.PERSON_3RD;
                            }
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPVerbSlot VLES = new SPVerbSlot(entryIRI, tense, voice, polarity, number, person, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);

                    sentencePlanSlots.add(VLES);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.AdjectiveSlot.getIRI())) {
                    //logger.debug("adding LexiconEntrySlot");
                    entryIRI = null;
                    caseType = "";
                    gender = "";
                    number = "";
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

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPAdjectiveSlot ALES = new SPAdjectiveSlot(entryIRI, caseType, gender, number, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);

                    sentencePlanSlots.add(ALES);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.NounSlot.getIRI())) {
                    //logger.debug("adding LexiconEntrySlot");
                    entryIRI = null;
                    caseType = "";
                    number = "";
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
                            } else if (individual.equals(NLResourceManager.bothNumbers)) {
                                number = LexEntry.NUMBER_BOTH;
                            }
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useNumber, NLResourcesModel);

                        for (OWLIndividual individual : individuals) {
                            if (individual.equals(NLResourceManager.singularNumber)) {
                                number = XmlMsgs.SINGULAR;
                            } else if (individual.equals(NLResourceManager.pluralNumber)) {
                                number = XmlMsgs.PLURAL;
                            }
                        }
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPNounSlot NLES = new SPNounSlot(entryIRI, caseType, number, agreesWithID, slot.asOWLAnonymousIndividual().getID(), order);

                    sentencePlanSlots.add(NLES);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.StringSlot.getIRI())) {
                    //logger.debug("adding StringSlot");
                    text = "";

                    literals = slot.getDataPropertyValues(NLResourceManager.hasString, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        text = literal.getLiteral();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPStringSlot SS = new SPStringSlot(text, slot.asOWLAnonymousIndividual().getID(), order);
                    sentencePlanSlots.add(SS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.PrepositionSlot.getIRI())) {
                    //logger.debug("adding PrepositionSlot");
                    text = "";

                    literals = slot.getDataPropertyValues(NLResourceManager.hasString, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        text = literal.getLiteral();
                    }

                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPPrepositionSlot PS = new SPPrepositionSlot(text, slot.asOWLAnonymousIndividual().getID(), order);
                    sentencePlanSlots.add(PS);
                } else if (slotType.asOWLClass().getIRI().equals(NLResourceManager.ConcatenationSlot.getIRI())) {
                    //logger.debug("adding ConcatenationSlot");
                    literals = slot.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                    for (OWLLiteral literal : literals) {
                        order = literal.parseInteger();
                    }

                    SPConcatenationSlot CS = new SPConcatenationSlot(slot.asOWLAnonymousIndividual().getID(), order);

                    individuals = slot.getObjectPropertyValues(NLResourceManager.concatenates, NLResourcesModel);

                    for (OWLIndividual individual : individuals) {
                        entryIRI = null;
                        caseType = "";

                        literals = individual.asOWLAnonymousIndividual().getDataPropertyValues(NLResourceManager.hasProperty, NLResourcesModel);

                        for (OWLLiteral literal : literals) {
                            entryIRI = IRI.create(literal.getLiteral());
                        }

                        individuals = slot.getObjectPropertyValues(NLResourceManager.useCase, NLResourcesModel);

                        for (OWLIndividual indiv : individuals) {
                            if (indiv.equals(NLResourceManager.nominativeCase)) {
                                caseType = XmlMsgs.NOMINATIVE_TAG;
                            } else if (indiv.equals(NLResourceManager.genitiveCase)) {
                                caseType = XmlMsgs.GENITIVE_TAG;
                            } else if (indiv.equals(NLResourceManager.accusativeCase)) {
                                caseType = XmlMsgs.ACCUSATIVE_TAG;
                            }
                        }

                        literals = individual.getDataPropertyValues(NLResourceManager.hasOrder, NLResourcesModel);

                        for (OWLLiteral literal : literals) {
                            order = literal.parseInteger();
                        }

                        SPConcatenationPropertySlot CPS = new SPConcatenationPropertySlot(entryIRI, caseType, individual.asOWLAnonymousIndividual().getID(), order);
                        CS.concatenateProperty(CPS);
                    }

                    sentencePlanSlots.add(CS);
                }
            }
        }

        return sentencePlanSlots;
    }

    public boolean getIsAggAllowed(IRI SentencePlanIRI, String language) {
        if (Languages.isEnglish(language)) {
            SentencePlan plan = EnglishSentencePlanList.getSentencePlan(SentencePlanIRI);

            if (plan != null) {
                return plan.getAggAllowed();
            }
        } else if (Languages.isGreek(language)) {
            SentencePlan plan = GreekSentencePlanList.getSentencePlan(SentencePlanIRI);

            if (plan != null) {
                return plan.getAggAllowed();
            }
        }
        return false;
    }

    public NLResourceManager getNLResourcesManager() {
        return NLResourcesManager;
    }

    // get a list of all sentence plans for a specific language
    public SentencePlansList getSentencePlansList(String language) {
        if (Languages.isEnglish(language)) {
            return EnglishSentencePlanList;
        } else if (Languages.isGreek(language)) {
            return GreekSentencePlanList;
        }
        return null;
    }

    /* toIRI - IRI to be copied TO
     * fromIRI - IRI to be copied FROM
     */
    public void duplicateSentencePlanInLists(IRI fromIRI, IRI toIRI, String toLanguage) {
        SentencePlan fromPlan = null;
        if (EnglishSentencePlanList.containsSentencePlan(fromIRI)) {
            fromPlan = EnglishSentencePlanList.getSentencePlan(fromIRI);
        } else if (GreekSentencePlanList.containsSentencePlan(fromIRI)) {
            fromPlan = GreekSentencePlanList.getSentencePlan(fromIRI);
        }

        if (fromPlan != null) {
            ArrayList<SPSlot> slots = new ArrayList<SPSlot>();
            for (SPSlot slot : fromPlan.getSlotsList()) {
                if (slot instanceof SPOwnerSlot) {
                    SPOwnerSlot slotCopy = new SPOwnerSlot((SPOwnerSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    slots.add(slotCopy);
                } else if (slot instanceof SPFillerSlot) {
                    SPFillerSlot slotCopy = new SPFillerSlot((SPFillerSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    slots.add(slotCopy);
                } else if (slot instanceof SPVerbSlot) {
                    SPVerbSlot slotCopy = new SPVerbSlot((SPVerbSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());
                    if (slotCopy.getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getAgreesWithID().toString().substring(slotCopy.getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromPlan.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        if (!slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PRESENT_PERFECT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_PAST)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PAST_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PAST_PERFECT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_FUTURE)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_FUTURE_PERFECT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_INFINITIVE)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PARTICIPLE)) {
                            slotCopy.setTense(XmlMsgs.TENSE_SIMPLE_PRESENT);
                        }
                    } else if (fromPlan.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        if (!slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PRESENT_PERFECT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_PAST)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PAST_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_SIMPLE_FUTURE)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_FUTURE_PERFECT)
                                && !slotCopy.getTense().equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                            slotCopy.setTense(XmlMsgs.TENSE_SIMPLE_PRESENT);
                        }
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof SPAdjectiveSlot) {
                    SPAdjectiveSlot slotCopy = new SPAdjectiveSlot((SPAdjectiveSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());
                    if (slotCopy.getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getAgreesWithID().toString().substring(slotCopy.getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromPlan.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setCase(XmlMsgs.NOMINATIVE_TAG);
                        slotCopy.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                    } else if (fromPlan.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setCase("");
                        slotCopy.setGender("");
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof SPNounSlot) {
                    SPNounSlot slotCopy = new SPNounSlot((SPNounSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());
                    if (slotCopy.getAgreesWithID() != null) {
                        slotCopy.setAgreesWithID(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getAgreesWithID().toString().substring(slotCopy.getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length())).getID());
                    }

                    if (fromPlan.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setCase(XmlMsgs.NOMINATIVE_TAG);
                    } else if (fromPlan.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setCase("");
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof SPPrepositionSlot) {
                    SPPrepositionSlot slotCopy = new SPPrepositionSlot((SPPrepositionSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    if (fromPlan.getLanguage().equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
                        slotCopy.setPrep(SPPrepositionSlot.PREPOSITION_GR_ANEU);
                    } else if (fromPlan.getLanguage().equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
                        slotCopy.setPrep(SPPrepositionSlot.PREPOSITION_EN_ABOARD);
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof SPStringSlot) {
                    SPStringSlot slotCopy = new SPStringSlot((SPStringSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    slots.add(slotCopy);
                } else if (slot instanceof SPConcatenationSlot) {
                    SPConcatenationSlot slotCopy = new SPConcatenationSlot((SPConcatenationSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    for (SPConcatenationPropertySlot concatCopy : slotCopy.getPropertySlots()) {
                        concatCopy.setId((getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.concatAnonymousIndivPattern + slotCopy.getOrder() + "_" + concatCopy.getOrder())).getID());
                    }

                    slots.add(slotCopy);
                } else if (slot instanceof SPComparatorSlot) {
                    SPComparatorSlot slotCopy = new SPComparatorSlot((SPComparatorSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    slots.add(slotCopy);
                } else if (slot instanceof SPComparatorFillerSlot) {
                    SPComparatorFillerSlot slotCopy = new SPComparatorFillerSlot((SPComparatorFillerSlot) slot);
                    slotCopy.setId(getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(toIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + slotCopy.getOrder()).getID());

                    slots.add(slotCopy);
                }
            }

            if (toLanguage.equals(Languages.ENGLISH)) {
                EnglishSentencePlanList.add(new SentencePlan(slots, toIRI, fromPlan.getAggAllowed(), Languages.ENGLISH));
            } else if (toLanguage.equals(Languages.GREEK)) {
                GreekSentencePlanList.add(new SentencePlan(slots, toIRI, fromPlan.getAggAllowed(), Languages.GREEK));
            }
        }
    }

    // get a list of slots for the specified sentence plan
    public SentencePlan getSentencePlan(IRI sentencePlanIRI, String language) {
        if (Languages.isEnglish(language)) {
            return EnglishSentencePlanList.getSentencePlan(sentencePlanIRI);
        } else if (Languages.isGreek(language)) {
            return GreekSentencePlanList.getSentencePlan(sentencePlanIRI);
        }
        return null;
    }

    // get a list of slots for the specified sentence plan
    public ArrayList<SPSlot> getSlots(IRI sentencePlanIRI) {
        if (EnglishSentencePlanList.containsSentencePlan(sentencePlanIRI)) {
            return EnglishSentencePlanList.getSentencePlan(sentencePlanIRI).getSlotsList();
        } else if (GreekSentencePlanList.containsSentencePlan(sentencePlanIRI)) {
            return GreekSentencePlanList.getSentencePlan(sentencePlanIRI).getSlotsList();
        }
        return null;
    }// save NP to lexicon

    public void addSentencePlan(IRI entryIRI, String lang) {
        if (Languages.isEnglish(lang)) {
            ArrayList<SPSlot> slots = new ArrayList<SPSlot>();

            SPOwnerSlot OS = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, SPOwnerSlot.REF_AUTO, getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(entryIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + 1).getID(), 1);
            slots.add(OS);

            EnglishSentencePlanList.add(new SentencePlan(slots, entryIRI, true, Languages.ENGLISH));
        } else if (Languages.isGreek(lang)) {
            ArrayList<SPSlot> slots = new ArrayList<SPSlot>();

            SPOwnerSlot OS = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, SPOwnerSlot.REF_AUTO, getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(entryIRI.getFragment() + "_" + SPSlot.anonymousIndivPattern + 1).getID(), 1);
            slots.add(OS);

            GreekSentencePlanList.add(new SentencePlan(slots, entryIRI, true, Languages.GREEK));
        }
    }

    public void addSentencePlan(SentencePlan plan, String lang) {
        if (Languages.isEnglish(lang)) {
            EnglishSentencePlanList.add(plan);
        } else if (Languages.isGreek(lang)) {
            GreekSentencePlanList.add(plan);
        }
    }

    public void removeSentencePlan(IRI entryIRI) {
        if (EnglishSentencePlanList.containsSentencePlan(entryIRI)) {
            EnglishSentencePlanList.removeSentencePlan(entryIRI);
        }
        if (GreekSentencePlanList.containsSentencePlan(entryIRI)) {
            GreekSentencePlanList.removeSentencePlan(entryIRI);
        }
    }

    public SentencePlan getSentencePlan(IRI SentencePlanIRI) {
        if (EnglishSentencePlanList.containsSentencePlan(SentencePlanIRI)) {
            return EnglishSentencePlanList.getSentencePlan(SentencePlanIRI);
        } else if (GreekSentencePlanList.containsSentencePlan(SentencePlanIRI)) {
            return GreekSentencePlanList.getSentencePlan(SentencePlanIRI);
        }
        return null;
    }

    public void exportSentencePlans(OWLOntology resourceOntology) {
        OWLDataFactory factory = NLResourcesManager.getDataFactory();

        ArrayList<SentencePlan> sentencePlanMasterList = new ArrayList<SentencePlan>();

        sentencePlanMasterList.addAll(EnglishSentencePlanList.getSentencePlansList());
        sentencePlanMasterList.addAll(GreekSentencePlanList.getSentencePlansList());

        for (SentencePlan plan : sentencePlanMasterList) {
            if (DefaultResourcesManager.isDefaultResource(plan.getSentencePlanIRI()) || !plan.getSentencePlanIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
                IRI sentencePlanIRI = plan.getSentencePlanIRI();

                OWLClassAssertionAxiom classAssertion;

                OWLClass cls = NLResourceManager.SentencePlan;
                OWLNamedIndividual indivPlan = factory.getOWLNamedIndividual(sentencePlanIRI);
                classAssertion = factory.getOWLClassAssertionAxiom(cls, indivPlan);
                NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                OWLObjectProperty objectProperty = NLResourceManager.forLanguage;
                OWLObjectPropertyAssertionAxiom objectAssertion = null;
                if (Languages.isEnglish(plan.getLanguage())) {
                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, NLResourceManager.englishLanguage);
                } else if (Languages.isGreek(plan.getLanguage())) {
                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, NLResourceManager.greekLanguage);
                }
                AddAxiom addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                OWLDataProperty dataProperty = NLResourceManager.aggregationAllowed;
                OWLDataPropertyAssertionAxiom dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, indivPlan, plan.getAggAllowed());
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                ArrayList<SPSlot> slots = plan.getSlotsList();

                OWLNamedIndividual indiv;
                OWLAnonymousIndividual anIndiv;

                for (SPSlot slot : slots) {
                    if (slot instanceof SPOwnerSlot) {
                        cls = NLResourceManager.OwnerSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useCase;
                        if (((SPOwnerSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                        } else if (((SPOwnerSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                        } else if (((SPOwnerSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                        } else {
                            objectAssertion = null;
                        }
                        if (objectAssertion != null) {
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }

                        objectProperty = NLResourceManager.refExpressionType;
                        if (((SPOwnerSlot) slot).getRefType().equals(XmlMsgs.REF_AUTO)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.autoRefExpression);
                        } else if (((SPOwnerSlot) slot).getRefType().equals(XmlMsgs.REF_PRONOUN)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pronounRefExpression);
                        } else if (((SPOwnerSlot) slot).getRefType().equals(XmlMsgs.REF_DEMONSTRATIVE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.demonstrativeRefExpression);
                        } else {
                            objectAssertion = null;
                        }
                        if (objectAssertion != null) {
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof SPFillerSlot) {
                        cls = NLResourceManager.FillerSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useCase;
                        if (((SPFillerSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                        } else if (((SPFillerSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                        } else if (((SPFillerSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                        } else {
                            objectAssertion = null;
                        }
                        if (objectAssertion != null) {
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }

                        dataProperty = NLResourceManager.useBullets;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((SPFillerSlot) slot).getBullets());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof SPVerbSlot) {
                        cls = NLResourceManager.VerbSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useLexiconEntry;
                        indiv = factory.getOWLNamedIndividual(((SPVerbSlot) slot).getLexiconEntryIRI());
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useTense;
                        if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.simplePresentTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.presentContinuousTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.presentPerfectTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.simplePastTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pastContinuousTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PAST_PERFECT)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pastPerfectTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pastPerfectContinuousTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.simpleFutureTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.futureContinuousTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.futurePerfectTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.futurePerfectContinuousTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_INFINITIVE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.infinitiveTense);
                        } else if (((SPVerbSlot) slot).getTense().equals(XmlMsgs.TENSE_PARTICIPLE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.participleTense);
                        } else {
                            objectAssertion = null;
                        }
                        if (objectAssertion != null) {
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }

                        objectProperty = NLResourceManager.useVoice;
                        if (((SPVerbSlot) slot).getVoice().equals(XmlMsgs.ACTIVE_VOICE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.activeVoice);
                        } else if (((SPVerbSlot) slot).getVoice().equals(XmlMsgs.PASSIVE_VOICE)) {
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.passiveVoice);
                        } else {
                            objectAssertion = null;
                        }
                        if (objectAssertion != null) {
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }

                        dataProperty = NLResourceManager.usePolarity;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((SPVerbSlot) slot).getPolarity());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((SPVerbSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((SPVerbSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useNumber;
                            if (((SPVerbSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((SPVerbSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((SPVerbSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            objectProperty = NLResourceManager.usePerson;
                            if (((SPVerbSlot) slot).getPerson().equals(XmlMsgs.PERSON_1ST)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.firstPerson);
                            } else if (((SPVerbSlot) slot).getPerson().equals(XmlMsgs.PERSON_2ND)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.secondPerson);
                            } else if (((SPVerbSlot) slot).getPerson().equals(XmlMsgs.PERSON_3RD)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.thirdPerson);
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
                    } else if (slot instanceof SPAdjectiveSlot) {
                        cls = NLResourceManager.AdjectiveSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useLexiconEntry;
                        indiv = factory.getOWLNamedIndividual(((SPAdjectiveSlot) slot).getLexiconEntryIRI());
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((SPAdjectiveSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((SPAdjectiveSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useNumber;
                            if (((SPAdjectiveSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((SPAdjectiveSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((SPAdjectiveSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            if (Languages.isGreek(plan.getLanguage())) {
                                objectProperty = NLResourceManager.useCase;
                                if (((SPAdjectiveSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                                } else if (((SPAdjectiveSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                                } else if (((SPAdjectiveSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.accusativeCase);
                                } else {
                                    objectAssertion = null;
                                }
                                if (objectAssertion != null) {
                                    addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                                }

                                objectProperty = NLResourceManager.useGender;
                                if (((SPAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.masculineGender);
                                } else if (((SPAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_FEMININE)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.feminineGender);
                                } else if (((SPAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_NEUTER)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.neuterGender);
                                } else if (((SPAdjectiveSlot) slot).getGender().equals(XmlMsgs.GENDER_MASCULINE_OR_FEMININE)) {
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
                    } else if (slot instanceof SPNounSlot) {
                        cls = NLResourceManager.NounSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        objectProperty = NLResourceManager.useLexiconEntry;
                        indiv = factory.getOWLNamedIndividual(((SPNounSlot) slot).getLexiconEntryIRI());
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        if (((SPNounSlot) slot).getAgreesWithID() != null) {
                            objectProperty = NLResourceManager.agreeWith;
                            OWLAnonymousIndividual anonym = factory.getOWLAnonymousIndividual(((SPNounSlot) slot).getAgreesWithID().toString());
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, anonym);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        } else {
                            objectProperty = NLResourceManager.useNumber;
                            if (((SPNounSlot) slot).getNumber().equals(XmlMsgs.SINGULAR)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.singularNumber);
                            } else if (((SPNounSlot) slot).getNumber().equals(XmlMsgs.PLURAL)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.pluralNumber);
                            } else if (((SPNounSlot) slot).getNumber().equals(LexEntry.NUMBER_BOTH)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.bothNumbers);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            if (Languages.isGreek(plan.getLanguage())) {
                                objectProperty = NLResourceManager.useCase;
                                if (((SPNounSlot) slot).getCase().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.nominativeCase);
                                } else if (((SPNounSlot) slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                                    objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourceManager.genitiveCase);
                                } else if (((SPNounSlot) slot).getCase().equals(XmlMsgs.ACCUSATIVE_TAG)) {
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
                    } else if (slot instanceof SPStringSlot) {
                        cls = NLResourceManager.StringSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasString;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((SPStringSlot) slot).getText());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof SPPrepositionSlot) {
                        cls = NLResourceManager.PrepositionSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasString;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, ((SPPrepositionSlot) slot).getPrep());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    } else if (slot instanceof SPConcatenationSlot) {
                        cls = NLResourceManager.ConcatenationSlot;
                        anIndiv = factory.getOWLAnonymousIndividual(slot.getId().toString());
                        classAssertion = factory.getOWLClassAssertionAxiom(cls, anIndiv);
                        NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                        objectProperty = NLResourceManager.hasSlot;
                        objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indivPlan, anIndiv);
                        addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        dataProperty = NLResourceManager.hasOrder;
                        dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, slot.getOrder());
                        addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                        ArrayList<SPConcatenationPropertySlot> propertySlots = ((SPConcatenationSlot) slot).getPropertySlots();

                        for (SPConcatenationPropertySlot propertySlot : propertySlots) {
                            cls = NLResourceManager.PropertySlot;
                            OWLAnonymousIndividual propertyAnIndiv = factory.getOWLAnonymousIndividual(propertySlot.getId().toString());
                            classAssertion = factory.getOWLClassAssertionAxiom(cls, propertyAnIndiv);
                            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

                            objectProperty = NLResourceManager.concatenates;
                            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, propertyAnIndiv);
                            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                            dataProperty = NLResourceManager.hasProperty;
                            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, propertyAnIndiv, propertySlot.getPropertyIRI().toString());
                            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                            objectProperty = NLResourceManager.useCase;
                            if (propertySlot.getCaseType().equals(XmlMsgs.NOMINATIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, propertyAnIndiv, NLResourceManager.nominativeCase);
                            } else if (propertySlot.getCaseType().equals(XmlMsgs.GENITIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, propertyAnIndiv, NLResourceManager.genitiveCase);
                            } else if (propertySlot.getCaseType().equals(XmlMsgs.ACCUSATIVE_TAG)) {
                                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, propertyAnIndiv, NLResourceManager.accusativeCase);
                            } else {
                                objectAssertion = null;
                            }
                            if (objectAssertion != null) {
                                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                            }

                            dataProperty = NLResourceManager.hasOrder;
                            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, propertyAnIndiv, propertySlot.getOrder());
                            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
            }
        }
    }

    public List<String> getPropertiesUsedForComparisons() {
        return this.PropertiesUsedForComparisons;
    }
}