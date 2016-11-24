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

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;

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

public class LexiconQueryManager {

    private HashMap<IRI, NounEntryList> lexiconNouns; //[NPuri => NP]
    private HashMap<IRI, VerbEntryList> lexiconVerbs; //[NPuri => NP]
    private HashMap<IRI, AdjectiveEntryList> lexiconAdjectives; //[NPuri => NP]
    private NLResourceManager NLResourcesManager;

    public NLResourceManager getNLResourcesManager() {
        return NLResourcesManager;
    }

    public LexiconQueryManager(NLResourceManager NLResourcesManager) {
        this.NLResourcesManager = NLResourcesManager;
        init();
    }

    private void init() {
        lexiconNouns = new HashMap<IRI, NounEntryList>();               // NP lexicon
        lexiconVerbs = new HashMap<IRI, VerbEntryList>();               // NP lexicon
        lexiconAdjectives = new HashMap<IRI, AdjectiveEntryList>();	// NP lexicon

        HashMap<IRI, NounEntryList> defaultLexiconNouns = DefaultResourcesManager.generateDefaultNouns();
        for (IRI nounIRI : defaultLexiconNouns.keySet()) {
            lexiconNouns.put(nounIRI, defaultLexiconNouns.get(nounIRI));
        }

        HashMap<IRI, VerbEntryList> defaultLexiconVerbs = DefaultResourcesManager.generateDefaultVerbs();
        for (IRI verbIRI : defaultLexiconVerbs.keySet()) {
            lexiconVerbs.put(verbIRI, defaultLexiconVerbs.get(verbIRI));
        }

        HashMap<IRI, AdjectiveEntryList> defaultLexiconAdjectives = DefaultResourcesManager.generateDefaultAdjectives();
        for (IRI adjectiveIRI : defaultLexiconAdjectives.keySet()) {
            lexiconAdjectives.put(adjectiveIRI, defaultLexiconAdjectives.get(adjectiveIRI));
        }
    }

    //return a HashSet that contains the lexicon
    public HashSet<IRI> getEntries() {
        HashSet<IRI> v = new HashSet<IRI>();
        for (IRI nounIRI : lexiconNouns.keySet()) {
            v.add(nounIRI);
        }

        for (IRI adjectiveIRI : lexiconAdjectives.keySet()) {
            v.add(adjectiveIRI);
        }

        for (IRI verbIRI : lexiconVerbs.keySet()) {
            v.add(verbIRI);
        }

        return v;
    }

    //return a HashSet that contains  the nouns in the lexicon
    public Set<IRI> getNounEntries() {
        return lexiconNouns.keySet();
    }

    //return a HashSet that contains  the verbs in the lexicon
    public Set<IRI> getVerbEntries() {
        return lexiconVerbs.keySet();
    }

    //return a HashSet that contains  the adjectives in the lexicon
    public Set<IRI> getAdjectiveEntries() {
        return lexiconAdjectives.keySet();
    }

    public LexEntryNoun getNounEntry(IRI nounIRI, String language) {
        return (LexEntryNoun) lexiconNouns.get(nounIRI).getEntry(language);
    }

    public LexEntryVerb getVerbEntry(IRI verbIRI, String language) {
        return (LexEntryVerb) lexiconVerbs.get(verbIRI).getEntry(language);
    }

    public LexEntryAdjective getAdjectiveEntry(IRI adjectiveIRI, String language) {
        return (LexEntryAdjective) lexiconAdjectives.get(adjectiveIRI).getEntry(language);
    }

    public ArrayList<HashSet<IRI>> importLexiconEntries(OWLOntology NLResourcesModel) {
        ArrayList<HashSet<IRI>> loadedResources = new ArrayList<HashSet<IRI>>();
        HashSet<IRI> NLResourcesLoaded = new HashSet<IRI>();

        //NOUN Entries
        Set<OWLClassAssertionAxiom> nounEntryAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.NounLexiconEntry);
        Set<OWLLiteral> literals;
        Set<OWLIndividual> individuals;

        for (OWLClassAssertionAxiom nounEntryAssertation : nounEntryAssertations) {
            OWLIndividual nounEntry = nounEntryAssertation.getIndividual();

            IRI NLResIRI = nounEntry.asOWLNamedIndividual().getIRI();
            NLResourcesLoaded.add(NLResIRI);

            Set<OWLIndividual> nounEntriesEN = nounEntry.getObjectPropertyValues(NLResourceManager.hasEnglishEntry, NLResourcesModel);

            LexEntryNounEN LE_EN = new LexEntryNounEN();

            for (OWLIndividual nounEntryEN : nounEntriesEN) {
                individuals = nounEntryEN.getObjectPropertyValues(NLResourceManager.hasGender, NLResourcesModel);

                for (OWLIndividual individual : individuals) {
                    if (individual.equals(NLResourceManager.masculineGender)) {
                        LE_EN.setGender(XmlMsgs.GENDER_MASCULINE);
                    } else if (individual.equals(NLResourceManager.feminineGender)) {
                        LE_EN.setGender(XmlMsgs.GENDER_FEMININE);
                    } else if (individual.equals(NLResourceManager.neuterGender)) {
                        LE_EN.setGender(XmlMsgs.GENDER_NEUTER);
                    } else if (individual.equals(NLResourceManager.masculineOrFeminineGender)) {
                        LE_EN.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                    }
                }
                if (individuals.isEmpty()) {
                    LE_EN.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                }

                individuals = nounEntryEN.getObjectPropertyValues(NLResourceManager.hasNumber, NLResourcesModel);

                for (OWLIndividual individual : individuals) {
                    if (individual.equals(NLResourceManager.singularNumber)) {
                        LE_EN.setNumber(XmlMsgs.SINGULAR);
                    } else if (individual.equals(NLResourceManager.pluralNumber)) {
                        LE_EN.setNumber(XmlMsgs.PLURAL);
                    } else if (individual.equals(NLResourceManager.bothNumbers)) {
                        LE_EN.setNumber(LexEntry.NUMBER_BOTH);
                    }
                }
                if (individuals.isEmpty()) {
                    LE_EN.setNumber(XmlMsgs.SINGULAR);
                }

                literals = nounEntryEN.getDataPropertyValues(NLResourceManager.hasSingularEnglish, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setSingular(literal.getLiteral());
                }
                literals = nounEntryEN.getDataPropertyValues(NLResourceManager.hasPluralEnglish, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setPlural(literal.getLiteral());
                }
            }

            Set<OWLIndividual> nounEntriesGR = nounEntry.getObjectPropertyValues(NLResourceManager.hasGreekEntry, NLResourcesModel);

            LexEntryNounGR LE_GR = new LexEntryNounGR();

            for (OWLIndividual nounEntryGR : nounEntriesGR) {
                individuals = nounEntryGR.getObjectPropertyValues(NLResourceManager.hasGender, NLResourcesModel);

                for (OWLIndividual individual : individuals) {
                    if (individual.equals(NLResourceManager.masculineGender)) {
                        LE_GR.setGender(XmlMsgs.GENDER_MASCULINE);
                    } else if (individual.equals(NLResourceManager.feminineGender)) {
                        LE_GR.setGender(XmlMsgs.GENDER_FEMININE);
                    } else if (individual.equals(NLResourceManager.neuterGender)) {
                        LE_GR.setGender(XmlMsgs.GENDER_NEUTER);
                    } else if (individual.equals(NLResourceManager.masculineOrFeminineGender)) {
                        LE_GR.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                    }
                }
                if (individuals.isEmpty()) {
                    LE_GR.setGender(XmlMsgs.GENDER_MASCULINE_OR_FEMININE);
                }

                individuals = nounEntryGR.getObjectPropertyValues(NLResourceManager.hasNumber, NLResourcesModel);

                for (OWLIndividual individual : individuals) {
                    if (individual.equals(NLResourceManager.singularNumber)) {
                        LE_GR.setNumber(XmlMsgs.SINGULAR);
                    } else if (individual.equals(NLResourceManager.pluralNumber)) {
                        LE_GR.setNumber(XmlMsgs.PLURAL);
                    } else if (individual.equals(NLResourceManager.bothNumbers)) {
                        LE_GR.setNumber(LexEntry.NUMBER_BOTH);
                    }
                }
                if (individuals.isEmpty()) {
                    LE_GR.setNumber(XmlMsgs.SINGULAR);
                }

                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasSingularNominativeGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularNominative(literal.getLiteral());
                }
                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasSingularGenitiveGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularGenitive(literal.getLiteral());
                }
                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasSingularAccusativeGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularAccusative(literal.getLiteral());
                }
                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasPluralNominativeGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralNominative(literal.getLiteral());
                }
                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasPluralGenitiveGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralGenitive(literal.getLiteral());
                }
                literals = nounEntryGR.getDataPropertyValues(NLResourceManager.hasPluralAccusativeGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralAccusative(literal.getLiteral());
                }
            }

            NounEntryList npList = new NounEntryList(LE_EN, LE_GR);

            lexiconNouns.put(NLResIRI, npList);
        }

        //ADJECTIVE Entries

        Set<OWLClassAssertionAxiom> adjectiveEntryAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.AdjectiveLexiconEntry);

        for (OWLClassAssertionAxiom adjectiveEntryAssertation : adjectiveEntryAssertations) {
            OWLIndividual adjEntry = adjectiveEntryAssertation.getIndividual();

            IRI NLResIRI = adjEntry.asOWLNamedIndividual().getIRI();
            NLResourcesLoaded.add(NLResIRI);

            Set<OWLIndividual> adjEntriesEN = adjEntry.getObjectPropertyValues(NLResourceManager.hasEnglishEntry, NLResourcesModel);

            LexEntryAdjectiveEN LE_EN = new LexEntryAdjectiveEN();

            for (OWLIndividual adjEntryEN : adjEntriesEN) {
                literals = adjEntryEN.getDataPropertyValues(NLResourceManager.hasFormEnglish, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.set_form(literal.getLiteral());
                }
            }

            Set<OWLIndividual> adjEntriesGR = adjEntry.getObjectPropertyValues(NLResourceManager.hasGreekEntry, NLResourcesModel);

            LexEntryAdjectiveGR LE_GR = new LexEntryAdjectiveGR();

            for (OWLIndividual adjEntryGR : adjEntriesGR) {
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularNominativeMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularNominativeMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularNominativeFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularNominativeFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularNominativeNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularNominativeNeuter(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralNominativeMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralNominativeMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralNominativeFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralNominativeFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralNominativeNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralNominativeNeuter(literal.getLiteral());
                }

                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularGenitiveMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularGenitiveMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularGenitiveFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularGenitiveFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularGenitiveNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularGenitiveNeuter(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralGenitiveMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralGenitiveMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralGenitiveFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralGenitiveFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralGenitiveNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralGenitiveNeuter(literal.getLiteral());
                }

                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularAccusativeMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularAccusativeMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularAccusativeFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularAccusativeFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasSingularAccusativeNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setSingularAccusativeNeuter(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralAccusativeMasculineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralAccusativeMasculine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralAccusativeFeminineGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralAccusativeFeminine(literal.getLiteral());
                }
                literals = adjEntryGR.getDataPropertyValues(NLResourceManager.hasPluralAccusativeNeuterGreek, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.setPluralAccusativeNeuter(literal.getLiteral());
                }
            }

            AdjectiveEntryList npList = new AdjectiveEntryList(LE_EN, LE_GR);
            lexiconAdjectives.put(NLResIRI, npList);
        }

        //VERB Entries

        Set<OWLClassAssertionAxiom> verbEntryAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.VerbLexiconEntry);

        for (OWLClassAssertionAxiom verbEntryAssertation : verbEntryAssertations) {
            OWLIndividual verbEntry = verbEntryAssertation.getIndividual();

            IRI NLResIRI = verbEntry.asOWLNamedIndividual().getIRI();
            NLResourcesLoaded.add(NLResIRI);

            Set<OWLIndividual> verbEntriesEN = verbEntry.getObjectPropertyValues(NLResourceManager.hasEnglishEntry, NLResourcesModel);

            LexEntryVerbEN LE_EN = new LexEntryVerbEN();

            for (OWLIndividual verbEntryEN : verbEntriesEN) {
                literals = verbEntryEN.getDataPropertyValues(NLResourceManager.baseForm, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setBaseForm(literal.getLiteral());
                }
                literals = verbEntryEN.getDataPropertyValues(NLResourceManager.simplePres3rdSing, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setSimplePresent3rdSingular(literal.getLiteral());
                }
                literals = verbEntryEN.getDataPropertyValues(NLResourceManager.presParticiple, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setPresentParticiple(literal.getLiteral());
                }
                literals = verbEntryEN.getDataPropertyValues(NLResourceManager.simplePast, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setSimplePast(literal.getLiteral());
                }
                literals = verbEntryEN.getDataPropertyValues(NLResourceManager.pastParticiple, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_EN.setPastParticiple(literal.getLiteral());
                }
            }

            Set<OWLIndividual> verbEntriesGR = verbEntry.getObjectPropertyValues(NLResourceManager.hasGreekEntry, NLResourcesModel);

            LexEntryVerbGR LE_GR = new LexEntryVerbGR();

            for (OWLIndividual verbEntryGR : verbEntriesGR) {
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePresent3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimplePast3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activePastContinuous3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeSimpleFuture3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeInfinitive, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", "", literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.activeParticiple, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", "", literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePresent3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimplePast3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passivePastContinuous3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture1stSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture2ndSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture3rdSingular, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture1stPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture2ndPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL, literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveSimpleFuture3rdPlural, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL, literal.getLiteral());
                }

                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveInfinitive, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", "", literal.getLiteral());
                }
                literals = verbEntryGR.getDataPropertyValues(NLResourceManager.passiveParticiple, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    LE_GR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", "", literal.getLiteral());
                }
            }

            VerbEntryList npList = new VerbEntryList(LE_EN, LE_GR);
            lexiconVerbs.put(NLResIRI, npList);
        }

        loadedResources.add(NLResourcesLoaded);

        return loadedResources;
    }

    public Iterator<IRI> getNounsIRIs() {
        return lexiconNouns.keySet().iterator();
    }

    public Iterator<IRI> getAdjectivesIRIs() {
        return lexiconAdjectives.keySet().iterator();
    }

    public Iterator<IRI> getVerbsIRIs() {
        return lexiconVerbs.keySet().iterator();
    }

    public EntryList getEntryList(IRI entryIRI) {
        if (lexiconNouns.containsKey(entryIRI)) {
            return lexiconNouns.get(entryIRI);
        }
        if (lexiconAdjectives.containsKey(entryIRI)) {
            return lexiconAdjectives.get(entryIRI);
        }
        if (lexiconVerbs.containsKey(entryIRI)) {
            return lexiconVerbs.get(entryIRI);
        }

        return null;
    }

    public boolean isNoun(IRI entryIRI) {
        if (lexiconNouns.containsKey(entryIRI)) {
            return true;
        }

        return false;
    }

    public boolean isAdjective(IRI entryIRI) {
        if (lexiconAdjectives.containsKey(entryIRI)) {
            return true;
        }

        return false;
    }

    public boolean isVerb(IRI entryIRI) {
        if (lexiconVerbs.containsKey(entryIRI)) {
            return true;
        }

        return false;
    }

    public void deleteLexiconEntry(IRI nlResourceIRI) {
        //remove lexicon entry
        if (lexiconNouns.containsKey(nlResourceIRI)) {
            lexiconNouns.remove(nlResourceIRI);
        }
        if (lexiconAdjectives.containsKey(nlResourceIRI)) {
            lexiconAdjectives.remove(nlResourceIRI);
        }
        if (lexiconVerbs.containsKey(nlResourceIRI)) {
            lexiconVerbs.remove(nlResourceIRI);
        }
    }

    // save NP to lexicon
    public void addNounEntryToLexicon(IRI entryIRI) {
        LexEntryNounEN LE_EN = new LexEntryNounEN();
        LexEntryNounGR LE_GR = new LexEntryNounGR();

        NounEntryList npList = new NounEntryList(LE_EN, LE_GR);

        lexiconNouns.put(entryIRI, npList);
    }

    public void addVerbEntryToLexicon(IRI entryIRI) {
        LexEntryVerbEN LE_EN = new LexEntryVerbEN();
        LexEntryVerbGR LE_GR = new LexEntryVerbGR();

        VerbEntryList npList = new VerbEntryList(LE_EN, LE_GR);

        lexiconVerbs.put(entryIRI, npList);
    }

    public void addAdjectiveEntryToLexicon(IRI entryIRI) {
        LexEntryAdjectiveEN LE_EN = new LexEntryAdjectiveEN();
        LexEntryAdjectiveGR LE_GR = new LexEntryAdjectiveGR();

        AdjectiveEntryList npList = new AdjectiveEntryList(LE_EN, LE_GR);

        lexiconAdjectives.put(entryIRI, npList);
    }

    /* toIRI - IRI to be copied TO
     * fromIRI - IRI to be copied FROM
     */
    public void duplicateEntryInLexicon(IRI fromIRI, IRI toIRI) {
        EntryList copyList = getEntryList(fromIRI);

        if (copyList instanceof NounEntryList) {
            LexEntryNounEN LE_EN = new LexEntryNounEN((LexEntryNounEN) copyList.getEntry(Languages.ENGLISH));
            LexEntryNounGR LE_GR = new LexEntryNounGR((LexEntryNounGR) copyList.getEntry(Languages.GREEK));

            NounEntryList npList = new NounEntryList(LE_EN, LE_GR);

            lexiconNouns.put(toIRI, npList);
        }
        if (copyList instanceof VerbEntryList) {
            LexEntryVerbEN LE_EN = new LexEntryVerbEN((LexEntryVerbEN) copyList.getEntry(Languages.ENGLISH));
            LexEntryVerbGR LE_GR = new LexEntryVerbGR((LexEntryVerbGR) copyList.getEntry(Languages.GREEK));

            VerbEntryList npList = new VerbEntryList(LE_EN, LE_GR);

            lexiconVerbs.put(toIRI, npList);
        }
        if (copyList instanceof AdjectiveEntryList) {
            LexEntryAdjectiveEN LE_EN = new LexEntryAdjectiveEN((LexEntryAdjectiveEN) copyList.getEntry(Languages.ENGLISH));
            LexEntryAdjectiveGR LE_GR = new LexEntryAdjectiveGR((LexEntryAdjectiveGR) copyList.getEntry(Languages.GREEK));

            AdjectiveEntryList npList = new AdjectiveEntryList(LE_EN, LE_GR);

            lexiconAdjectives.put(toIRI, npList);
        }
    }

    public void exportLexiconEntries(OWLOntology resourceOntology) {
        OWLDataFactory factory = NLResourcesManager.getDataFactory();

        OWLClassAssertionAxiom classAssertion;

        //NOUNS START
        Iterator<IRI> keysIter = lexiconNouns.keySet().iterator();
        while (keysIter.hasNext()) {
            IRI NLResourceIRI = keysIter.next();

            NounEntryList currentNPList = lexiconNouns.get(NLResourceIRI);

            OWLClass cls = NLResourceManager.NounLexiconEntry;

            OWLNamedIndividual indiv = factory.getOWLNamedIndividual(NLResourceIRI);

            classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            // greek

            cls = NLResourceManager.GreekNounEntry;
            OWLNamedIndividual entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_GreekNounEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            OWLObjectProperty objectProperty = NLResourceManager.hasGreekEntry;
            OWLObjectPropertyAssertionAxiom objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            AddAxiom addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryNounGR lex_entry_noun_gr = (LexEntryNounGR) currentNPList.getEntry(Languages.GREEK);

            if (lex_entry_noun_gr == null) {
                lex_entry_noun_gr = new LexEntryNounGR(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL);
            }

            objectProperty = NLResourceManager.hasGender;
            if (lex_entry_noun_gr.getGender().equals(XmlMsgs.GENDER_MASCULINE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.masculineGender);
            } else if (lex_entry_noun_gr.getGender().equals(XmlMsgs.GENDER_FEMININE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.feminineGender);
            } else if (lex_entry_noun_gr.getGender().equals(XmlMsgs.GENDER_NEUTER)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.neuterGender);
            } else if (lex_entry_noun_gr.getGender().equals(XmlMsgs.GENDER_MASCULINE_OR_FEMININE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.masculineOrFeminineGender);
            } else {
                objectAssertion = null;
            }
            if (objectAssertion != null) {
                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            objectProperty = NLResourceManager.hasNumber;
            if (lex_entry_noun_gr.getNumber().equals(XmlMsgs.SINGULAR)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.singularNumber);
            } else if (lex_entry_noun_gr.getNumber().equals(XmlMsgs.PLURAL)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.pluralNumber);
            } else if (lex_entry_noun_gr.getNumber().equals(LexEntry.NUMBER_BOTH)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.bothNumbers);
            } else {
                objectAssertion = null;
            }
            if (objectAssertion != null) {
                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            if (!lex_entry_noun_gr.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR).isEmpty()
                    || !lex_entry_noun_gr.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.SINGULAR).isEmpty()
                    || !lex_entry_noun_gr.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.SINGULAR).isEmpty()) {

                OWLDataProperty dataProperty = NLResourceManager.hasSingularNominativeGreek;
                OWLDataPropertyAssertionAxiom dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                dataProperty = NLResourceManager.hasSingularGenitiveGreek;
                dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.SINGULAR));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                dataProperty = NLResourceManager.hasSingularAccusativeGreek;
                dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.SINGULAR));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            if (!lex_entry_noun_gr.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.PLURAL).isEmpty()
                    || !lex_entry_noun_gr.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.PLURAL).isEmpty()
                    || !lex_entry_noun_gr.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.PLURAL).isEmpty()) {
                OWLDataProperty dataProperty = NLResourceManager.hasPluralNominativeGreek;
                OWLDataPropertyAssertionAxiom dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.PLURAL));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                dataProperty = NLResourceManager.hasPluralGenitiveGreek;
                dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.PLURAL));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                dataProperty = NLResourceManager.hasPluralAccusativeGreek;
                dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_gr.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.PLURAL));
                addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            //english NP
            cls = NLResourceManager.EnglishNounEntry;
            entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_EnglishNounEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            objectProperty = NLResourceManager.hasEnglishEntry;
            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryNounEN lex_entry_noun_en = (LexEntryNounEN) currentNPList.getEntry(Languages.ENGLISH);

            if (lex_entry_noun_en == null) {
                lex_entry_noun_en = new LexEntryNounEN(XmlMsgs.GENDER_MASCULINE_OR_FEMININE, XmlMsgs.PLURAL);
            }

            objectProperty = NLResourceManager.hasGender;
            if (lex_entry_noun_en.getGender().equals(XmlMsgs.GENDER_MASCULINE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.masculineGender);
            } else if (lex_entry_noun_en.getGender().equals(XmlMsgs.GENDER_FEMININE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.feminineGender);
            } else if (lex_entry_noun_en.getGender().equals(XmlMsgs.GENDER_NEUTER)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.neuterGender);
            } else if (lex_entry_noun_en.getGender().equals(XmlMsgs.GENDER_MASCULINE_OR_FEMININE)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.masculineOrFeminineGender);
            } else {
                objectAssertion = null;
            }
            if (objectAssertion != null) {
                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            objectProperty = NLResourceManager.hasNumber;
            if (lex_entry_noun_en.getNumber().equals(XmlMsgs.SINGULAR)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.singularNumber);
            } else if (lex_entry_noun_en.getNumber().equals(XmlMsgs.PLURAL)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.pluralNumber);
            } else if (lex_entry_noun_en.getNumber().equals(LexEntry.NUMBER_BOTH)) {
                objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, entry, NLResourceManager.bothNumbers);
            } else {
                objectAssertion = null;
            }
            if (objectAssertion != null) {
                addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }

            OWLDataProperty dataProperty = NLResourceManager.hasSingularEnglish;
            OWLDataPropertyAssertionAxiom dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_en.getSingular());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            dataProperty = NLResourceManager.hasPluralEnglish;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_noun_en.getPlural());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
        }
        //NOUNS END

        //ADJECTIVES START
        keysIter = lexiconAdjectives.keySet().iterator();

        while (keysIter.hasNext()) {
            IRI NLResourceIRI = keysIter.next();

            AdjectiveEntryList currentNPList = lexiconAdjectives.get(NLResourceIRI);

            //<localID is AdjectiveLexiconEntry>
            OWLClass cls = NLResourceManager.AdjectiveLexiconEntry;
            OWLNamedIndividual indiv = factory.getOWLNamedIndividual(NLResourceIRI);
            classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            // greek

            //<localID_GreekAdjectiveEntry is GreekAdjectiveEntry>
            cls = NLResourceManager.GreekAdjectiveEntry;
            OWLNamedIndividual entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_GreekAdjectiveEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            //<localID hasGreekEntry localID_GreekAdjectiveEntry>
            OWLObjectProperty objectProperty = NLResourceManager.hasGreekEntry;
            OWLObjectPropertyAssertionAxiom objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            AddAxiom addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryAdjectiveGR lex_entry_adjective_gr = (LexEntryAdjectiveGR) currentNPList.getEntry(Languages.GREEK);

            if (lex_entry_adjective_gr == null) {
                lex_entry_adjective_gr = new LexEntryAdjectiveGR();
            }

            OWLDataProperty dataProperty;
            OWLDataPropertyAssertionAxiom dataAssertion;

            //<localID_GreekAdjectiveEntry hasSingularMasculineGreek (...)>
            dataProperty = NLResourceManager.hasSingularNominativeMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularFeminineGreek (...)>
            dataProperty = NLResourceManager.hasSingularNominativeFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularNeuterGreek (...)>
            dataProperty = NLResourceManager.hasSingularNominativeNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);


            //<localID_GreekAdjectiveEntry hasPluralMasculineGreek (...)>
            dataProperty = NLResourceManager.hasPluralNominativeMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralFeminineGreek (...)>
            dataProperty = NLResourceManager.hasPluralNominativeFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralNeuterGreek (...)>
            dataProperty = NLResourceManager.hasPluralNominativeNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularMasculineGreek (...)>
            dataProperty = NLResourceManager.hasSingularGenitiveMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularFeminineGreek (...)>
            dataProperty = NLResourceManager.hasSingularGenitiveFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularNeuterGreek (...)>
            dataProperty = NLResourceManager.hasSingularGenitiveNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);


            //<localID_GreekAdjectiveEntry hasPluralMasculineGreek (...)>
            dataProperty = NLResourceManager.hasPluralGenitiveMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralFeminineGreek (...)>
            dataProperty = NLResourceManager.hasPluralGenitiveFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralNeuterGreek (...)>
            dataProperty = NLResourceManager.hasPluralGenitiveNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularMasculineGreek (...)>
            dataProperty = NLResourceManager.hasSingularAccusativeMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularFeminineGreek (...)>
            dataProperty = NLResourceManager.hasSingularAccusativeFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasSingularNeuterGreek (...)>
            dataProperty = NLResourceManager.hasSingularAccusativeNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralMasculineGreek (...)>
            dataProperty = NLResourceManager.hasPluralAccusativeMasculineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralFeminineGreek (...)>
            dataProperty = NLResourceManager.hasPluralAccusativeFeminineGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekAdjectiveEntry hasPluralNeuterGreek (...)>
            dataProperty = NLResourceManager.hasPluralAccusativeNeuterGreek;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_gr.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);


            //english NP
            //<localID_EnglishAdjectiveEntry is EnglishAdjectiveEntry>
            cls = NLResourceManager.EnglishAdjectiveEntry;
            entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_EnglishAdjectiveEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            //<localID hasEnglishEntry localID_EnglishAdjectiveEntry>
            objectProperty = NLResourceManager.hasEnglishEntry;
            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryAdjectiveEN lex_entry_adjective_en = (LexEntryAdjectiveEN) currentNPList.getEntry(Languages.ENGLISH);

            if (lex_entry_adjective_en == null) {
                lex_entry_adjective_en = new LexEntryAdjectiveEN();
            }

            //<localID_EnglishAdjectiveEntry hasFormEnglish (...)>
            dataProperty = NLResourceManager.hasFormEnglish;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_adjective_en.get_form());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
        }
        //ADJECTIVES END

        //VERBS START
        keysIter = lexiconVerbs.keySet().iterator();

        while (keysIter.hasNext()) {
            IRI NLResourceIRI = keysIter.next();

            VerbEntryList currentNPList = lexiconVerbs.get(NLResourceIRI);

            //<localID is VerbLexiconEntry>
            OWLClass cls = NLResourceManager.VerbLexiconEntry;
            OWLNamedIndividual indiv = factory.getOWLNamedIndividual(NLResourceIRI);
            classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            // greek

            //<localID_GreekVerbEntry is GreekVerbEntry>
            cls = NLResourceManager.GreekVerbEntry;
            OWLNamedIndividual entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_GreekVerbEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            //<localID hasGreekEntry localID_GreekVerbEntry>
            OWLObjectProperty objectProperty = NLResourceManager.hasGreekEntry;
            OWLObjectPropertyAssertionAxiom objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            AddAxiom addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryVerbGR lex_entry_verb_gr = (LexEntryVerbGR) currentNPList.getEntry(Languages.GREEK);

            if (lex_entry_verb_gr == null) {
                lex_entry_verb_gr = new LexEntryVerbGR();
            }

            OWLDataProperty dataProperty;
            OWLDataPropertyAssertionAxiom dataAssertion;

            //<localID_GreekVerbEntry activeSimplePresent1stSingular (...)>
            dataProperty = NLResourceManager.activeSimplePresent1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePresent2ndSingular (...)>
            dataProperty = NLResourceManager.activeSimplePresent2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePresent3rdSingular (...)>
            dataProperty = NLResourceManager.activeSimplePresent3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePresent1stPlural (...)>
            dataProperty = NLResourceManager.activeSimplePresent1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePresent2ndPlural (...)>
            dataProperty = NLResourceManager.activeSimplePresent2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePresent3rdPlural (...)>
            dataProperty = NLResourceManager.activeSimplePresent3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast1stSingular (...)>
            dataProperty = NLResourceManager.activeSimplePast1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast2ndSingular (...)>
            dataProperty = NLResourceManager.activeSimplePast2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast3rdSingular (...)>
            dataProperty = NLResourceManager.activeSimplePast3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast1stPlural (...)>
            dataProperty = NLResourceManager.activeSimplePast1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast2ndPlural (...)>
            dataProperty = NLResourceManager.activeSimplePast2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimplePast3rdPlural (...)>
            dataProperty = NLResourceManager.activeSimplePast3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous1stSingular (...)>
            dataProperty = NLResourceManager.activePastContinuous1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous2ndSingular (...)>
            dataProperty = NLResourceManager.activePastContinuous2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous3rdSingular (...)>
            dataProperty = NLResourceManager.activePastContinuous3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous1stPlural (...)>
            dataProperty = NLResourceManager.activePastContinuous1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous2ndPlural (...)>
            dataProperty = NLResourceManager.activePastContinuous2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activePastContinuous3rdPlural (...)>
            dataProperty = NLResourceManager.activePastContinuous3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture1stSingular (...)>
            dataProperty = NLResourceManager.activeSimpleFuture1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture2ndSingular (...)>
            dataProperty = NLResourceManager.activeSimpleFuture2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture3rdSingular (...)>
            dataProperty = NLResourceManager.activeSimpleFuture3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture1stPlural (...)>
            dataProperty = NLResourceManager.activeSimpleFuture1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture2ndPlural (...)>
            dataProperty = NLResourceManager.activeSimpleFuture2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeSimpleFuture3rdPlural (...)>
            dataProperty = NLResourceManager.activeSimpleFuture3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeInfinitive (...)>
            dataProperty = NLResourceManager.activeInfinitive;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", ""));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry activeParticiple (...)>
            dataProperty = NLResourceManager.activeParticiple;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", ""));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent1stSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePresent1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent2ndSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePresent2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent3rdSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePresent3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent1stPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePresent1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent2ndPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePresent2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePresent3rdPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePresent3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast1stSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePast1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast2ndSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePast2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast3rdSingular (...)>
            dataProperty = NLResourceManager.passiveSimplePast3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast1stPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePast1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast2ndPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePast2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimplePast3rdPlural (...)>
            dataProperty = NLResourceManager.passiveSimplePast3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous1stSingular (...)>
            dataProperty = NLResourceManager.passivePastContinuous1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous2ndSingular (...)>
            dataProperty = NLResourceManager.passivePastContinuous2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous3rdSingular (...)>
            dataProperty = NLResourceManager.passivePastContinuous3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous1stPlural (...)>
            dataProperty = NLResourceManager.passivePastContinuous1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous2ndPlural (...)>
            dataProperty = NLResourceManager.passivePastContinuous2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passivePastContinuous3rdPlural (...)>
            dataProperty = NLResourceManager.passivePastContinuous3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture1stSingular (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture1stSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture2ndSingular (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture2ndSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture3rdSingular (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture3rdSingular;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture1stPlural (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture1stPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture2ndPlural (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture2ndPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveSimpleFuture3rdPlural (...)>
            dataProperty = NLResourceManager.passiveSimpleFuture3rdPlural;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveInfinitive (...)>
            dataProperty = NLResourceManager.passiveInfinitive;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", ""));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_GreekVerbEntry passiveParticiple (...)>
            dataProperty = NLResourceManager.passiveParticiple;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_gr.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", ""));
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //english NP
            //<localID_EnglishVerbEntry is EnglishVerbEntry>
            cls = NLResourceManager.EnglishVerbEntry;
            entry = factory.getOWLNamedIndividual(IRI.create(NLResourceIRI.toString() + "_EnglishVerbEntry"));
            classAssertion = factory.getOWLClassAssertionAxiom(cls, entry);
            NLResourcesManager.getOntologyManager().addAxiom(resourceOntology, classAssertion);

            //<localID hasEnglishEntry localID_EnglishVerbEntry>
            objectProperty = NLResourceManager.hasEnglishEntry;
            objectAssertion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, indiv, entry);
            addAxiomChange = new AddAxiom(resourceOntology, objectAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            LexEntryVerbEN lex_entry_verb_en = (LexEntryVerbEN) currentNPList.getEntry(Languages.ENGLISH);

            if (lex_entry_verb_en == null) {
                lex_entry_verb_en = new LexEntryVerbEN();
            }

            //<localID_EnglishVerbEntry baseForm (...)>
            dataProperty = NLResourceManager.baseForm;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_en.getBaseForm());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_EnglishVerbEntry simplePres3rdSing (...)>
            dataProperty = NLResourceManager.simplePres3rdSing;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_en.getSimplePresent3rdSingular());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_EnglishVerbEntry presParticiple (...)>
            dataProperty = NLResourceManager.presParticiple;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_en.getPresentParticiple());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_EnglishVerbEntry simplePast (...)>
            dataProperty = NLResourceManager.simplePast;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_en.getSimplePast());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            //<localID_EnglishVerbEntry pastParticiple (...)>
            dataProperty = NLResourceManager.pastParticiple;
            dataAssertion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, entry, lex_entry_verb_en.getPastParticiple());
            addAxiomChange = new AddAxiom(resourceOntology, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
        }
        //VERBS END
    }
}