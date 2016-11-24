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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;

public class NLResourceManager {

    public OWLOntology NLResourcesModel = null;
    public OWLOntologyManager NLResourcesManager = null;
    public OWLDataFactory dataFactory = null;
    public static String nlowlNS = "http://www.aueb.gr/users/ion/nlowl/nlowl#";
    public static String resourcesNS = "http://www.aueb.gr/users/ion/nlowl/dgr#";
    public final static String TRUE = "true";
    public final static String FALSE = "false";
    //Resources class IRIs
    public static final String NLResourceCls = "NLResource";
    public static final String LanguageCls = "Language";
    public static final String CaseCls = "Case";
    public static final String NumberCls = "Number";
    public static final String GenderCls = "Gender";
    public static final String ReferringExpressionTypeCls = "ReferringExpressionType";
    public static final String TenseCls = "Tense";
    public static final String VoiceCls = "Voice";
    public static final String PersonCls = "Person";
    public static final String ModifierCls = "Modifier";
    public static final String DomainIndependentPropertyCls = "DomainIndependentProperty";
    //Resources individual IRIs
    public static final String anonymousIndiv = "anonymous";
    public static final String globalUserModelIndiv = "GlobalUserModel";
    public static final String exactCardinalityIndiv = "exactCardinality";
    public static final String minCardinalityIndiv = "minCardinality";
    public static final String maxCardinalityIndiv = "maxCardinality";
    public static final String allValuesFromIndiv = "allValuesFrom";
    public static final String someValuesFromIndiv = "someValuesFrom";
    public static final String instanceOfIndiv = "instanceOf";
    public static final String oneOfIndiv = "oneOf";
    public static final String differentIndividualsIndiv = "differentIndividuals";
    public static final String sameIndividualsIndiv = "sameIndividuals";
    public static final String isAIndiv = "isA";
    //SentencePlan/NLName class IRIs    
    public static final String NLNameCls = "NLName";
    public static final String SlotCls = "Slot";
    public static final String ArticleCls = "ArticleSlot";
    public static final String AdjectiveCls = "AdjectiveSlot";
    public static final String NounCls = "NounSlot";
    public static final String StringCls = "StringSlot";
    public static final String PrepositionCls = "PrepositionSlot";
    public static final String SentencePlanCls = "SentencePlan";
    public static final String OwnerCls = "OwnerSlot";
    public static final String FillerCls = "FillerSlot";
    public static final String VerbCls = "VerbSlot";
    public static final String ConcatenationCls = "ConcatenationSlot";
    public static final String PropertyCls = "PropertySlot";
    //SentencePlan/NLName individual IRIs
    public static final String englishLanguageIndiv = "englishLanguage";
    public static final String greekLanguageIndiv = "greekLanguage";
    public static final String nominativeCaseIndiv = "nominativeCase";
    public static final String genitiveCaseIndiv = "genitiveCase";
    public static final String accusativeCaseIndiv = "accusativeCase";
    public static final String masculineGenderIndiv = "masculineGender";
    public static final String feminineGenderIndiv = "feminineGender";
    public static final String neuterGenderIndiv = "neuterGender";
    public static final String masculineOrFeminineGenderIndiv = "masculineOrFeminineGender";
    public static final String singularNumberIndiv = "singularNumber";
    public static final String pluralNumberIndiv = "pluralNumber";
    public static final String bothNumbersIndiv = "bothNumbers";
    public static final String autoRefExpressionIndiv = "autoRefExpression";
    public static final String pronounRefExpressionIndiv = "pronounRefExpression";
    public static final String demonstrativeRefExpressionIndiv = "demonstrativeRefExpression";
    public static final String simplePresentIndiv = "simplePresent";
    public static final String presentContinuousIndiv = "presentContinuous";
    public static final String presentPerfectIndiv = "presentPerfect";
    public static final String simplePastIndiv = "simplePast";
    public static final String pastContinuousIndiv = "pastContinuous";
    public static final String pastPerfectIndiv = "pastPerfect";
    public static final String pastPerfectContinuousIndiv = "pastPerfectContinuous";
    public static final String simpleFutureIndiv = "simpleFuture";
    public static final String futureContinuousIndiv = "futureContinuous";
    public static final String futurePerfectIndiv = "futurePerfect";
    public static final String futurePerfectContinuousIndiv = "futurePerfectContinuous";
    public static final String infinitiveIndiv = "infinitive";
    public static final String participleIndiv = "participle";
    public static final String activeVoiceIndiv = "activeVoice";
    public static final String passiveVoiceIndiv = "passiveVoice";
    public static final String firstPersonIndiv = "firstPerson";
    public static final String secondPersonIndiv = "secondPerson";
    public static final String thirdPersonIndiv = "thirdPerson";
    //SentencePlan/NLName property IRIs
    public static final String hasOrderPrp = "hasOrder";
    public static final String hasSlotPrp = "hasSlot";
    public static final String forLanguagePrp = "forLanguage";
    public static final String useNumberPrp = "useNumber";
    public static final String usePersonPrp = "usePerson";
    public static final String useCasePrp = "useCase";
    public static final String useGenderPrp = "useGender";
    public static final String isHeadPrp = "isHead";
    public static final String isCapitalizedPrp = "isCapitalized";
    public static final String isDefinitePrp = "isDefinite";
    public static final String hasStringPrp = "hasString";
    public static final String useLexiconEntryPrp = "useLexiconEntry";
    public static final String agreeWithPrp = "agreeWith";
    public static final String aggregationAllowedPrp = "aggregationAllowed";
    public static final String focusLostPrp = "focusLost";
    public static final String useTensePrp = "useTense";
    public static final String useVoicePrp = "useVoice";
    public static final String usePolarityPrp = "usePolarity";
    public static final String useBulletsPrp = "useBullets";
    public static final String concatenatesPrp = "concatenates";
    public static final String hasPropertyPrp = "hasProperty";
    public static final String refExpressionTypePrp = "refExpressionType";
    //Ordering Class IRIs
    public static final String SectionCls = "Section";
    //Ordering property IRIs
    public static final String hasSectionPrp = "hasSection";
    //Ordering individual IRIs
    public static final String defaultSectionIndiv = "UNSORTED";
    //Mapping property IRIs
    public static final String hasSentencePlanPrp = "hasSentencePlan";
    public static final String hasNLNamePrp = "hasNLName";
    //Lexicon class IRIs
    public static final String LexiconEntryCls = "LexiconEntry";
    public static final String VerbLexiconEntryCls = "VerbLexiconEntry";
    public static final String NounLexiconEntryCls = "NounLexiconEntry";
    public static final String AdjectiveLexiconEntryCls = "AdjectiveLexiconEntry";
    public static final String EntryCls = "Entry";
    public static final String EnglishEntryCls = "EnglishLexEntry";
    public static final String GreekEntryCls = "GreekLexEntry";
    public static final String EnglishAdjectiveEntryCls = "EnglishAdjectiveLexEntry";
    public static final String EnglishNounEntryCls = "EnglishNounLexEntry";
    public static final String EnglishVerbEntryCls = "EnglishVerbLexEntry";
    public static final String GreekAdjectiveEntryCls = "GreekAdjectiveLexEntry";
    public static final String GreekNounEntryCls = "GreekNounLexEntry";
    public static final String GreekVerbEntryCls = "GreekVerbLexEntry";
    //Lexicon noun property IRIs
    public static final String hasGenderPrp = "hasGender";
    public static final String hasNumberPrp = "hasNumber";
    public static final String hasEnglishEntryPrp = "hasEnglishEntry";
    public static final String hasSingularEnglishPrp = "hasSingularEnglish";
    public static final String hasPluralEnglishPrp = "hasPluralEnglish";
    public static final String hasGreekEntryPrp = "hasGreekEntry";
    public static final String hasSingularNominativeGreekPrp = "hasSingularNominativeGreek";
    public static final String hasSingularGenitiveGreekPrp = "hasSingularGenitiveGreek";
    public static final String hasSingularAccusativeGreekPrp = "hasSingularAccusativeGreek";
    public static final String hasPluralNominativeGreekPrp = "hasPluralNominativeGreek";
    public static final String hasPluralGenitiveGreekPrp = "hasPluralGenitiveGreek";
    public static final String hasPluralAccusativeGreekPrp = "hasPluralAccusativeGreek";
    //Lexicon adjective property IRIs
    public static final String hasFormEnglishPrp = "hasFormEnglish";
    public static final String hasSingularNominativeMasculineGreekPrp = "hasSingularNominativeMasculineGreek";
    public static final String hasSingularNominativeFeminineGreekPrp = "hasSingularNominativeFeminineGreek";
    public static final String hasSingularNominativeNeuterGreekPrp = "hasSingularNominativeNeuterGreek";
    public static final String hasPluralNominativeMasculineGreekPrp = "hasPluralNominativeMasculineGreek";
    public static final String hasPluralNominativeFeminineGreekPrp = "hasPluralNominativeFeminineGreek";
    public static final String hasPluralNominativeNeuterGreekPrp = "hasPluralNominativeNeuterGreek";
    public static final String hasSingularGenitiveMasculineGreekPrp = "hasSingularGenitiveMasculineGreek";
    public static final String hasSingularGenitiveFeminineGreekPrp = "hasSingularGenitiveFeminineGreek";
    public static final String hasSingularGenitiveNeuterGreekPrp = "hasSingularGenitiveNeuterGreek";
    public static final String hasPluralGenitiveMasculineGreekPrp = "hasPluralGenitiveMasculineGreek";
    public static final String hasPluralGenitiveFeminineGreekPrp = "hasPluralGenitiveFeminineGreek";
    public static final String hasPluralGenitiveNeuterGreekPrp = "hasPluralGenitiveNeuterGreek";
    public static final String hasSingularAccusativeMasculineGreekPrp = "hasSingularAccusativeMasculineGreek";
    public static final String hasSingularAccusativeFeminineGreekPrp = "hasSingularAccusativeFeminineGreek";
    public static final String hasSingularAccusativeNeuterGreekPrp = "hasSingularAccusativeNeuterGreek";
    public static final String hasPluralAccusativeMasculineGreekPrp = "hasPluralAccusativeMasculineGreek";
    public static final String hasPluralAccusativeFeminineGreekPrp = "hasPluralAccusativeFeminineGreek";
    public static final String hasPluralAccusativeNeuterGreekPrp = "hasPluralAccusativeNeuterGreek";
    //Lexicon verb property IRIs
    public static final String baseFormPrp = "baseForm";
    public static final String simplePres3rdSingPrp = "simplePres3rdSing";
    public static final String presParticiplePrp = "presParticiple";
    public static final String simplePastPrp = "simplePast";
    public static final String pastParticiplePrp = "pastParticiple";
    public static final String activeSimplePresent1stSingularPrp = "activeSimplePresent1stSingular";
    public static final String activeSimplePresent2ndSingularPrp = "activeSimplePresent2ndSingular";
    public static final String activeSimplePresent3rdSingularPrp = "activeSimplePresent3rdSingular";
    public static final String activeSimplePresent1stPluralPrp = "activeSimplePresent1stPlural";
    public static final String activeSimplePresent2ndPluralPrp = "activeSimplePresent2ndPlural";
    public static final String activeSimplePresent3rdPluralPrp = "activeSimplePresent3rdPlural";
    public static final String activeSimplePast1stSingularPrp = "activeSimplePast1stSingular";
    public static final String activeSimplePast2ndSingularPrp = "activeSimplePast2ndSingular";
    public static final String activeSimplePast3rdSingularPrp = "activeSimplePast3rdSingular";
    public static final String activeSimplePast1stPluralPrp = "activeSimplePast1stPlural";
    public static final String activeSimplePast2ndPluralPrp = "activeSimplePast2ndPlural";
    public static final String activeSimplePast3rdPluralPrp = "activeSimplePast3rdPlural";
    public static final String activePastContinuous1stSingularPrp = "activePastContinuous1stSingular";
    public static final String activePastContinuous2ndSingularPrp = "activePastContinuous2ndSingular";
    public static final String activePastContinuous3rdSingularPrp = "activePastContinuous3rdSingular";
    public static final String activePastContinuous1stPluralPrp = "activePastContinuous1stPlural";
    public static final String activePastContinuous2ndPluralPrp = "activePastContinuous2ndPlural";
    public static final String activePastContinuous3rdPluralPrp = "activePastContinuous3rdPlural";
    public static final String activeSimpleFuture1stSingularPrp = "activeSimpleFuture1stSingular";
    public static final String activeSimpleFuture2ndSingularPrp = "activeSimpleFuture2ndSingular";
    public static final String activeSimpleFuture3rdSingularPrp = "activeSimpleFuture3rdSingular";
    public static final String activeSimpleFuture1stPluralPrp = "activeSimpleFuture1stPlural";
    public static final String activeSimpleFuture2ndPluralPrp = "activeSimpleFuture2ndPlural";
    public static final String activeSimpleFuture3rdPluralPrp = "activeSimpleFuture3rdPlural";
    public static final String activeInfinitivePrp = "activeInfinitive";
    public static final String activeParticiplePrp = "activeParticiple";
    public static final String passiveSimplePresent1stSingularPrp = "passiveSimplePresent1stSingular";
    public static final String passiveSimplePresent2ndSingularPrp = "passiveSimplePresent2ndSingular";
    public static final String passiveSimplePresent3rdSingularPrp = "passiveSimplePresent3rdSingular";
    public static final String passiveSimplePresent1stPluralPrp = "passiveSimplePresent1stPlural";
    public static final String passiveSimplePresent2ndPluralPrp = "passiveSimplePresent2ndPlural";
    public static final String passiveSimplePresent3rdPluralPrp = "passiveSimplePresent3rdPlural";
    public static final String passiveSimplePast1stSingularPrp = "passiveSimplePast1stSingular";
    public static final String passiveSimplePast2ndSingularPrp = "passiveSimplePast2ndSingular";
    public static final String passiveSimplePast3rdSingularPrp = "passiveSimplePast3rdSingular";
    public static final String passiveSimplePast1stPluralPrp = "passiveSimplePast1stPlural";
    public static final String passiveSimplePast2ndPluralPrp = "passiveSimplePast2ndPlural";
    public static final String passiveSimplePast3rdPluralPrp = "passiveSimplePast3rdPlural";
    public static final String passivePastContinuous1stSingularPrp = "passivePastContinuous1stSingular";
    public static final String passivePastContinuous2ndSingularPrp = "passivePastContinuous2ndSingular";
    public static final String passivePastContinuous3rdSingularPrp = "passivePastContinuous3rdSingular";
    public static final String passivePastContinuous1stPluralPrp = "passivePastContinuous1stPlural";
    public static final String passivePastContinuous2ndPluralPrp = "passivePastContinuous2ndPlural";
    public static final String passivePastContinuous3rdPluralPrp = "passivePastContinuous3rdPlural";
    public static final String passiveSimpleFuture1stSingularPrp = "passiveSimpleFuture1stSingular";
    public static final String passiveSimpleFuture2ndSingularPrp = "passiveSimpleFuture2ndSingular";
    public static final String passiveSimpleFuture3rdSingularPrp = "passiveSimpleFuture3rdSingular";
    public static final String passiveSimpleFuture1stPluralPrp = "passiveSimpleFuture1stPlural";
    public static final String passiveSimpleFuture2ndPluralPrp = "passiveSimpleFuture2ndPlural";
    public static final String passiveSimpleFuture3rdPluralPrp = "passiveSimpleFuture3rdPlural";
    public static final String passiveInfinitivePrp = "passiveInfinitive";
    public static final String passiveParticiplePrp = "passiveParticiple";
    //User Model class IRIs
    public static final String UserTypeCls = "UserType";
    public static final String AnnotationEventCls = "AnnotationEvent";
    //User Model property IRIs
    public static final String hasAppropriatenessPrp = "hasAppropriateness";
    public static final String forSentencePlanPrp = "forSentencePlan";
    public static final String forNLNamePrp = "forNLName";
    public static final String hasInterestPrp = "hasInterest";
    public static final String maxRepetitionsPrp = "maxRepetitions";
    public static final String maxMessagesPerPagePrp = "maxMessagesPerPage";
    public static final String maxMessagesPerSentencePrp = "maxMessagesPerSentence";
    public static final String forUserTypePrp = "forUserType";
    public static final String forPropertyPrp = "forProperty";
    public static final String forOwnerPrp = "forOwner";
    public static final String forModifierPrp = "forModifier";
    //Comparison property IRIs
    public static final String comparisonsAllowedPrp = "comparisonsAllowed";
    //Resource classes    
    public static OWLClass NLResource = null;
    public static OWLClass Language = null;
    public static OWLClass Case = null;
    public static OWLClass Number = null;
    public static OWLClass Gender = null;
    public static OWLClass ReferringExpressionType = null;
    public static OWLClass Tense = null;
    public static OWLClass Voice = null;
    public static OWLClass Person = null;
    public static OWLClass Modifier = null;
    public static OWLClass DomainIndependentProperty = null;
    //Resources individuals
    public static OWLNamedIndividual anonymous = null;
    public static OWLNamedIndividual globalUserModel = null;
    public static OWLNamedIndividual exactCardinality = null;
    public static OWLNamedIndividual minCardinality = null;
    public static OWLNamedIndividual maxCardinality = null;
    public static OWLNamedIndividual allValuesFrom = null;
    public static OWLNamedIndividual someValuesFrom = null;
    public static OWLNamedIndividual instanceOf = null;
    public static OWLNamedIndividual oneOf = null;
    public static OWLNamedIndividual differentIndividuals = null;
    public static OWLNamedIndividual sameIndividuals = null;
    public static OWLNamedIndividual isA = null;
    //SentencePlan/NLName classes    
    public static OWLClass NLName = null;
    public static OWLClass Slot = null;
    public static OWLClass ArticleSlot = null;
    public static OWLClass AdjectiveSlot = null;
    public static OWLClass NounSlot = null;
    public static OWLClass StringSlot = null;
    public static OWLClass PrepositionSlot = null;
    public static OWLClass SentencePlan = null;
    public static OWLClass OwnerSlot = null;
    public static OWLClass FillerSlot = null;
    public static OWLClass VerbSlot = null;
    public static OWLClass ConcatenationSlot = null;
    public static OWLClass PropertySlot = null;
    //SentencePlan/NLName individuals    
    public static OWLNamedIndividual englishLanguage = null;
    public static OWLNamedIndividual greekLanguage = null;
    public static OWLNamedIndividual nominativeCase = null;
    public static OWLNamedIndividual genitiveCase = null;
    public static OWLNamedIndividual accusativeCase = null;
    public static OWLNamedIndividual masculineGender = null;
    public static OWLNamedIndividual feminineGender = null;
    public static OWLNamedIndividual neuterGender = null;
    public static OWLNamedIndividual masculineOrFeminineGender = null;
    public static OWLNamedIndividual singularNumber = null;
    public static OWLNamedIndividual pluralNumber = null;
    public static OWLNamedIndividual bothNumbers = null;
    public static OWLNamedIndividual autoRefExpression = null;
    public static OWLNamedIndividual pronounRefExpression = null;
    public static OWLNamedIndividual demonstrativeRefExpression = null;
    public static OWLNamedIndividual simplePresentTense = null;
    public static OWLNamedIndividual presentContinuousTense = null;
    public static OWLNamedIndividual presentPerfectTense = null;
    public static OWLNamedIndividual simplePastTense = null;
    public static OWLNamedIndividual pastContinuousTense = null;
    public static OWLNamedIndividual pastPerfectTense = null;
    public static OWLNamedIndividual pastPerfectContinuousTense = null;
    public static OWLNamedIndividual simpleFutureTense = null;
    public static OWLNamedIndividual futureContinuousTense = null;
    public static OWLNamedIndividual futurePerfectTense = null;
    public static OWLNamedIndividual futurePerfectContinuousTense = null;
    public static OWLNamedIndividual infinitiveTense = null;
    public static OWLNamedIndividual participleTense = null;
    public static OWLNamedIndividual activeVoice = null;
    public static OWLNamedIndividual passiveVoice = null;
    public static OWLNamedIndividual firstPerson = null;
    public static OWLNamedIndividual secondPerson = null;
    public static OWLNamedIndividual thirdPerson = null;
    //SentencePlan/NLName properties
    public static OWLDataProperty hasOrder = null;
    public static OWLDataProperty aggregationAllowed = null;
    public static OWLDataProperty focusLost = null;
    public static OWLDataProperty isHead = null;
    public static OWLDataProperty isCapitalized = null;
    public static OWLDataProperty isDefinite = null;
    public static OWLDataProperty hasString = null;
    public static OWLDataProperty usePolarity = null;
    public static OWLDataProperty useBullets = null;
    public static OWLDataProperty hasProperty = null;
    public static OWLObjectProperty useTense = null;
    public static OWLObjectProperty useVoice = null;
    public static OWLObjectProperty refExpressionType = null;
    public static OWLObjectProperty usePerson = null;
    public static OWLObjectProperty forLanguage = null;
    public static OWLObjectProperty hasSlot = null;
    public static OWLObjectProperty useNumber = null;
    public static OWLObjectProperty useCase = null;
    public static OWLObjectProperty useGender = null;
    public static OWLObjectProperty useLexiconEntry = null;
    public static OWLObjectProperty agreeWith = null;
    public static OWLObjectProperty concatenates = null;
    //Ordering Classes
    public static OWLClass Section = null;
    //Ordering properties
    public static OWLAnnotationProperty hasOrderAnn = null;
    public static OWLAnnotationProperty hasSection = null;
    //Ordering individuals
    public static OWLNamedIndividual defaultSection = null;
    //Mapping properties
    public static OWLAnnotationProperty hasSentencePlan = null;
    public static OWLAnnotationProperty hasNLName = null;
    //Lexicon classes
    public static OWLClass LexiconEntry = null;
    public static OWLClass VerbLexiconEntry = null;
    public static OWLClass NounLexiconEntry = null;
    public static OWLClass AdjectiveLexiconEntry = null;
    public static OWLClass Entry = null;
    public static OWLClass EnglishEntry = null;
    public static OWLClass GreekEntry = null;
    public static OWLClass EnglishAdjectiveEntry = null;
    public static OWLClass EnglishNounEntry = null;
    public static OWLClass EnglishVerbEntry = null;
    public static OWLClass GreekAdjectiveEntry = null;
    public static OWLClass GreekNounEntry = null;
    public static OWLClass GreekVerbEntry = null;
    //Lexicon noun properties
    public static OWLDataProperty hasSingularEnglish = null;
    public static OWLDataProperty hasPluralEnglish = null;
    public static OWLDataProperty hasSingularNominativeGreek = null;
    public static OWLDataProperty hasSingularGenitiveGreek = null;
    public static OWLDataProperty hasSingularAccusativeGreek = null;
    public static OWLDataProperty hasPluralNominativeGreek = null;
    public static OWLDataProperty hasPluralGenitiveGreek = null;
    public static OWLDataProperty hasPluralAccusativeGreek = null;
    public static OWLObjectProperty hasGender = null;
    public static OWLObjectProperty hasNumber = null;
    public static OWLObjectProperty hasEnglishEntry = null;
    public static OWLObjectProperty hasGreekEntry = null;
    //Lexicon adjective properties
    public static OWLDataProperty hasFormEnglish = null;
    public static OWLDataProperty hasSingularNominativeMasculineGreek = null;
    public static OWLDataProperty hasSingularNominativeFeminineGreek = null;
    public static OWLDataProperty hasSingularNominativeNeuterGreek = null;
    public static OWLDataProperty hasPluralNominativeMasculineGreek = null;
    public static OWLDataProperty hasPluralNominativeFeminineGreek = null;
    public static OWLDataProperty hasPluralNominativeNeuterGreek = null;
    public static OWLDataProperty hasSingularGenitiveMasculineGreek = null;
    public static OWLDataProperty hasSingularGenitiveFeminineGreek = null;
    public static OWLDataProperty hasSingularGenitiveNeuterGreek = null;
    public static OWLDataProperty hasPluralGenitiveMasculineGreek = null;
    public static OWLDataProperty hasPluralGenitiveFeminineGreek = null;
    public static OWLDataProperty hasPluralGenitiveNeuterGreek = null;
    public static OWLDataProperty hasSingularAccusativeMasculineGreek = null;
    public static OWLDataProperty hasSingularAccusativeFeminineGreek = null;
    public static OWLDataProperty hasSingularAccusativeNeuterGreek = null;
    public static OWLDataProperty hasPluralAccusativeMasculineGreek = null;
    public static OWLDataProperty hasPluralAccusativeFeminineGreek = null;
    public static OWLDataProperty hasPluralAccusativeNeuterGreek = null;
    //Lexicon verb properties
    public static OWLDataProperty baseForm = null;
    public static OWLDataProperty simplePres3rdSing = null;
    public static OWLDataProperty presParticiple = null;
    public static OWLDataProperty simplePast = null;
    public static OWLDataProperty pastParticiple = null;
    public static OWLDataProperty activeSimplePresent1stSingular = null;
    public static OWLDataProperty activeSimplePresent2ndSingular = null;
    public static OWLDataProperty activeSimplePresent3rdSingular = null;
    public static OWLDataProperty activeSimplePresent1stPlural = null;
    public static OWLDataProperty activeSimplePresent2ndPlural = null;
    public static OWLDataProperty activeSimplePresent3rdPlural = null;
    public static OWLDataProperty activeSimplePast1stSingular = null;
    public static OWLDataProperty activeSimplePast2ndSingular = null;
    public static OWLDataProperty activeSimplePast3rdSingular = null;
    public static OWLDataProperty activeSimplePast1stPlural = null;
    public static OWLDataProperty activeSimplePast2ndPlural = null;
    public static OWLDataProperty activeSimplePast3rdPlural = null;
    public static OWLDataProperty activePastContinuous1stSingular = null;
    public static OWLDataProperty activePastContinuous2ndSingular = null;
    public static OWLDataProperty activePastContinuous3rdSingular = null;
    public static OWLDataProperty activePastContinuous1stPlural = null;
    public static OWLDataProperty activePastContinuous2ndPlural = null;
    public static OWLDataProperty activePastContinuous3rdPlural = null;
    public static OWLDataProperty activeSimpleFuture1stSingular = null;
    public static OWLDataProperty activeSimpleFuture2ndSingular = null;
    public static OWLDataProperty activeSimpleFuture3rdSingular = null;
    public static OWLDataProperty activeSimpleFuture1stPlural = null;
    public static OWLDataProperty activeSimpleFuture2ndPlural = null;
    public static OWLDataProperty activeSimpleFuture3rdPlural = null;
    public static OWLDataProperty activeInfinitive = null;
    public static OWLDataProperty activeParticiple = null;
    public static OWLDataProperty passiveSimplePresent1stSingular = null;
    public static OWLDataProperty passiveSimplePresent2ndSingular = null;
    public static OWLDataProperty passiveSimplePresent3rdSingular = null;
    public static OWLDataProperty passiveSimplePresent1stPlural = null;
    public static OWLDataProperty passiveSimplePresent2ndPlural = null;
    public static OWLDataProperty passiveSimplePresent3rdPlural = null;
    public static OWLDataProperty passiveSimplePast1stSingular = null;
    public static OWLDataProperty passiveSimplePast2ndSingular = null;
    public static OWLDataProperty passiveSimplePast3rdSingular = null;
    public static OWLDataProperty passiveSimplePast1stPlural = null;
    public static OWLDataProperty passiveSimplePast2ndPlural = null;
    public static OWLDataProperty passiveSimplePast3rdPlural = null;
    public static OWLDataProperty passivePastContinuous1stSingular = null;
    public static OWLDataProperty passivePastContinuous2ndSingular = null;
    public static OWLDataProperty passivePastContinuous3rdSingular = null;
    public static OWLDataProperty passivePastContinuous1stPlural = null;
    public static OWLDataProperty passivePastContinuous2ndPlural = null;
    public static OWLDataProperty passivePastContinuous3rdPlural = null;
    public static OWLDataProperty passiveSimpleFuture1stSingular = null;
    public static OWLDataProperty passiveSimpleFuture2ndSingular = null;
    public static OWLDataProperty passiveSimpleFuture3rdSingular = null;
    public static OWLDataProperty passiveSimpleFuture1stPlural = null;
    public static OWLDataProperty passiveSimpleFuture2ndPlural = null;
    public static OWLDataProperty passiveSimpleFuture3rdPlural = null;
    public static OWLDataProperty passiveInfinitive = null;
    public static OWLDataProperty passiveParticiple = null;
    //User Model classes
    public static OWLClass UserType = null;
    public static OWLClass AnnotationEvent = null;
    //User Model properties
    public static OWLDataProperty hasAppropriateness = null;
    public static OWLDataProperty hasInterest = null;
    public static OWLDataProperty maxRepetitions = null;
    public static OWLDataProperty maxMessagesPerPage = null;
    public static OWLDataProperty maxMessagesPerSentence = null;
    public static OWLObjectProperty forUserType = null;
    public static OWLObjectProperty forModifier = null;
    public static OWLObjectProperty forSentencePlan = null;
    public static OWLObjectProperty forNLName = null;
    public static OWLAnnotationProperty forProperty = null;
    public static OWLAnnotationProperty forOwner = null;
    //Comparison properties
    public static OWLAnnotationProperty comparisonsAllowed = null;
    public static ArrayList<OWLNamedIndividual> domainIndependentProperties = new ArrayList<OWLNamedIndividual>();

    public NLResourceManager() {
        NLResourcesManager = OWLManager.createOWLOntologyManager();
        dataFactory = NLResourcesManager.getOWLDataFactory();

        try {
            NLResourcesModel = NLResourcesManager.createOntology(IRI.create(resourcesNS.substring(0, resourcesNS.length() - 1)));
        } catch (OWLOntologyCreationException e) {
        }

        //Define resource classes
        NLResource = dataFactory.getOWLClass(IRI.create(nlowlNS + NLResourceCls));

        Language = dataFactory.getOWLClass(IRI.create(nlowlNS + LanguageCls));
        Case = dataFactory.getOWLClass(IRI.create(nlowlNS + CaseCls));
        Number = dataFactory.getOWLClass(IRI.create(nlowlNS + NumberCls));
        Gender = dataFactory.getOWLClass(IRI.create(nlowlNS + GenderCls));
        ReferringExpressionType = dataFactory.getOWLClass(IRI.create(nlowlNS + ReferringExpressionTypeCls));
        Tense = dataFactory.getOWLClass(IRI.create(nlowlNS + TenseCls));
        Voice = dataFactory.getOWLClass(IRI.create(nlowlNS + VoiceCls));
        Person = dataFactory.getOWLClass(IRI.create(nlowlNS + PersonCls));
        Modifier = dataFactory.getOWLClass(IRI.create(nlowlNS + ModifierCls));
        DomainIndependentProperty = dataFactory.getOWLClass(IRI.create(nlowlNS + DomainIndependentPropertyCls));

        //Define resource individuals
        anonymous = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + anonymousIndiv));
        globalUserModel = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + globalUserModelIndiv));
        exactCardinality = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + exactCardinalityIndiv));
        minCardinality = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + minCardinalityIndiv));
        maxCardinality = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + maxCardinalityIndiv));
        allValuesFrom = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + allValuesFromIndiv));
        someValuesFrom = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + someValuesFromIndiv));
        instanceOf = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + instanceOfIndiv));
        oneOf = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + oneOfIndiv));
        differentIndividuals = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + differentIndividualsIndiv));
        sameIndividuals = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + sameIndividualsIndiv));
        isA = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + isAIndiv));

        //Define SentencePlan/NLName classes
        NLName = dataFactory.getOWLClass(IRI.create(nlowlNS + NLNameCls));

        Slot = dataFactory.getOWLClass(IRI.create(nlowlNS + SlotCls));
        ArticleSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + ArticleCls));
        AdjectiveSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + AdjectiveCls));
        NounSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + NounCls));
        StringSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + StringCls));
        PrepositionSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + PrepositionCls));

        SentencePlan = dataFactory.getOWLClass(IRI.create(nlowlNS + SentencePlanCls));

        OwnerSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + OwnerCls));
        FillerSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + FillerCls));
        VerbSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + VerbCls));
        ConcatenationSlot = dataFactory.getOWLClass(IRI.create(nlowlNS + ConcatenationCls));
        PropertySlot = dataFactory.getOWLClass(IRI.create(nlowlNS + PropertyCls));

        //Define SentencePlan/NLName individuals
        englishLanguage = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + englishLanguageIndiv));
        greekLanguage = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + greekLanguageIndiv));

        nominativeCase = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + nominativeCaseIndiv));
        genitiveCase = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + genitiveCaseIndiv));
        accusativeCase = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + accusativeCaseIndiv));

        masculineGender = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + masculineGenderIndiv));
        feminineGender = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + feminineGenderIndiv));
        neuterGender = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + neuterGenderIndiv));
        masculineOrFeminineGender = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + masculineOrFeminineGenderIndiv));

        singularNumber = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + singularNumberIndiv));
        pluralNumber = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + pluralNumberIndiv));
        bothNumbers = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + bothNumbersIndiv));

        autoRefExpression = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + autoRefExpressionIndiv));
        pronounRefExpression = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + pronounRefExpressionIndiv));
        demonstrativeRefExpression = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + demonstrativeRefExpressionIndiv));

        simplePresentTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + simplePresentIndiv));
        presentContinuousTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + presentContinuousIndiv));
        presentPerfectTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + presentPerfectIndiv));
        simplePastTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + simplePastIndiv));
        pastContinuousTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + pastContinuousIndiv));
        pastPerfectTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + pastPerfectIndiv));
        pastPerfectContinuousTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + pastPerfectContinuousIndiv));
        simpleFutureTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + simpleFutureIndiv));
        futureContinuousTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + futureContinuousIndiv));
        futurePerfectTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + futurePerfectIndiv));
        futurePerfectContinuousTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + futurePerfectContinuousIndiv));
        infinitiveTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + infinitiveIndiv));
        participleTense = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + participleIndiv));

        activeVoice = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + activeVoiceIndiv));
        passiveVoice = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + passiveVoiceIndiv));

        firstPerson = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + firstPersonIndiv));
        secondPerson = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + secondPersonIndiv));
        thirdPerson = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + thirdPersonIndiv));

        //Define SentencePlan/NLName properties
        hasOrder = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasOrderPrp));
        isHead = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + isHeadPrp));
        isCapitalized = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + isCapitalizedPrp));
        isDefinite = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + isDefinitePrp));
        hasString = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasStringPrp));
        aggregationAllowed = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + aggregationAllowedPrp));
        focusLost = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + focusLostPrp));

        usePolarity = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + usePolarityPrp));
        useBullets = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + useBulletsPrp));
        hasProperty = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPropertyPrp));

        forLanguage = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + forLanguagePrp));
        hasSlot = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + hasSlotPrp));
        useNumber = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useNumberPrp));
        useCase = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useCasePrp));
        useGender = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useGenderPrp));
        useLexiconEntry = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useLexiconEntryPrp));
        agreeWith = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + agreeWithPrp));

        useTense = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useTensePrp));
        useVoice = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + useVoicePrp));
        usePerson = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + usePersonPrp));

        concatenates = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + concatenatesPrp));

        refExpressionType = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + refExpressionTypePrp));

        //Define ordering classes   
        Section = dataFactory.getOWLClass(IRI.create(nlowlNS + SectionCls));
        //Define ordering properties        
        hasOrderAnn = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + hasOrderPrp));
        hasSection = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + hasSectionPrp));
        //Define ordering individuals
        defaultSection = dataFactory.getOWLNamedIndividual(IRI.create(nlowlNS + defaultSectionIndiv));

        //Define mapping properties
        hasSentencePlan = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + hasSentencePlanPrp));
        hasNLName = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + hasNLNamePrp));

        //Define lexicon classes                
        LexiconEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + LexiconEntryCls));

        VerbLexiconEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + VerbLexiconEntryCls));
        NounLexiconEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + NounLexiconEntryCls));
        AdjectiveLexiconEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + AdjectiveLexiconEntryCls));

        Entry = dataFactory.getOWLClass(IRI.create(nlowlNS + EntryCls));
        EnglishEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + EnglishEntryCls));
        GreekEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + GreekEntryCls));

        EnglishAdjectiveEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + EnglishAdjectiveEntryCls));
        EnglishNounEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + EnglishNounEntryCls));
        EnglishVerbEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + EnglishVerbEntryCls));

        GreekAdjectiveEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + GreekAdjectiveEntryCls));
        GreekNounEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + GreekNounEntryCls));
        GreekVerbEntry = dataFactory.getOWLClass(IRI.create(nlowlNS + GreekVerbEntryCls));

        //Define the lexicon noun properties
        hasSingularEnglish = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularEnglishPrp));
        hasPluralEnglish = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralEnglishPrp));

        hasSingularNominativeGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularNominativeGreekPrp));
        hasSingularGenitiveGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularGenitiveGreekPrp));
        hasSingularAccusativeGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularAccusativeGreekPrp));
        hasPluralNominativeGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralNominativeGreekPrp));
        hasPluralGenitiveGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralGenitiveGreekPrp));
        hasPluralAccusativeGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralAccusativeGreekPrp));

        hasGender = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + hasGenderPrp));
        hasNumber = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + hasNumberPrp));

        hasEnglishEntry = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + hasEnglishEntryPrp));
        hasGreekEntry = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + hasGreekEntryPrp));

        //Define the lexicon adjective properties
        hasFormEnglish = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasFormEnglishPrp));

        hasSingularNominativeMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularNominativeMasculineGreekPrp));
        hasSingularNominativeFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularNominativeFeminineGreekPrp));
        hasSingularNominativeNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularNominativeNeuterGreekPrp));
        hasPluralNominativeMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralNominativeMasculineGreekPrp));
        hasPluralNominativeFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralNominativeFeminineGreekPrp));
        hasPluralNominativeNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralNominativeNeuterGreekPrp));

        hasSingularGenitiveMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularGenitiveMasculineGreekPrp));
        hasSingularGenitiveFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularGenitiveFeminineGreekPrp));
        hasSingularGenitiveNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularGenitiveNeuterGreekPrp));
        hasPluralGenitiveMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralGenitiveMasculineGreekPrp));
        hasPluralGenitiveFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralGenitiveFeminineGreekPrp));
        hasPluralGenitiveNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralGenitiveNeuterGreekPrp));

        hasSingularAccusativeMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularAccusativeMasculineGreekPrp));
        hasSingularAccusativeFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularAccusativeFeminineGreekPrp));
        hasSingularAccusativeNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasSingularAccusativeNeuterGreekPrp));
        hasPluralAccusativeMasculineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralAccusativeMasculineGreekPrp));
        hasPluralAccusativeFeminineGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralAccusativeFeminineGreekPrp));
        hasPluralAccusativeNeuterGreek = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasPluralAccusativeNeuterGreekPrp));

        //Define the lexicon verb properties
        baseForm = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + baseFormPrp));
        simplePres3rdSing = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + simplePres3rdSingPrp));
        presParticiple = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + presParticiplePrp));
        simplePast = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + simplePastPrp));
        pastParticiple = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + pastParticiplePrp));

        activeSimplePresent1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent1stSingularPrp));
        activeSimplePresent2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent2ndSingularPrp));
        activeSimplePresent3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent3rdSingularPrp));
        activeSimplePresent1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent1stPluralPrp));
        activeSimplePresent2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent2ndPluralPrp));
        activeSimplePresent3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePresent3rdPluralPrp));

        activeSimplePast1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast1stSingularPrp));
        activeSimplePast2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast2ndSingularPrp));
        activeSimplePast3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast3rdSingularPrp));
        activeSimplePast1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast1stPluralPrp));
        activeSimplePast2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast2ndPluralPrp));
        activeSimplePast3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimplePast3rdPluralPrp));

        activePastContinuous1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous1stSingularPrp));
        activePastContinuous2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous2ndSingularPrp));
        activePastContinuous3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous3rdSingularPrp));
        activePastContinuous1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous1stPluralPrp));
        activePastContinuous2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous2ndPluralPrp));
        activePastContinuous3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activePastContinuous3rdPluralPrp));

        activeSimpleFuture1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture1stSingularPrp));
        activeSimpleFuture2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture2ndSingularPrp));
        activeSimpleFuture3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture3rdSingularPrp));
        activeSimpleFuture1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture1stPluralPrp));
        activeSimpleFuture2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture2ndPluralPrp));
        activeSimpleFuture3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeSimpleFuture3rdPluralPrp));

        activeInfinitive = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeInfinitivePrp));
        activeParticiple = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + activeParticiplePrp));

        passiveSimplePresent1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent1stSingularPrp));
        passiveSimplePresent2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent2ndSingularPrp));
        passiveSimplePresent3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent3rdSingularPrp));
        passiveSimplePresent1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent1stPluralPrp));
        passiveSimplePresent2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent2ndPluralPrp));
        passiveSimplePresent3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePresent3rdPluralPrp));

        passiveSimplePast1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast1stSingularPrp));
        passiveSimplePast2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast2ndSingularPrp));
        passiveSimplePast3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast3rdSingularPrp));
        passiveSimplePast1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast1stPluralPrp));
        passiveSimplePast2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast2ndPluralPrp));
        passiveSimplePast3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimplePast3rdPluralPrp));

        passivePastContinuous1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous1stSingularPrp));
        passivePastContinuous2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous2ndSingularPrp));
        passivePastContinuous3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous3rdSingularPrp));
        passivePastContinuous1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous1stPluralPrp));
        passivePastContinuous2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous2ndPluralPrp));
        passivePastContinuous3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passivePastContinuous3rdPluralPrp));

        passiveSimpleFuture1stSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture1stSingularPrp));
        passiveSimpleFuture2ndSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture2ndSingularPrp));
        passiveSimpleFuture3rdSingular = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture3rdSingularPrp));
        passiveSimpleFuture1stPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture1stPluralPrp));
        passiveSimpleFuture2ndPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture2ndPluralPrp));
        passiveSimpleFuture3rdPlural = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveSimpleFuture3rdPluralPrp));

        passiveInfinitive = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveInfinitivePrp));
        passiveParticiple = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + passiveParticiplePrp));

        //Define the user model classes
        UserType = dataFactory.getOWLClass(IRI.create(nlowlNS + UserTypeCls));
        AnnotationEvent = dataFactory.getOWLClass(IRI.create(nlowlNS + AnnotationEventCls));

        //Define the user model properties
        hasAppropriateness = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasAppropriatenessPrp));
        hasInterest = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + hasInterestPrp));
        maxRepetitions = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + maxRepetitionsPrp));
        maxMessagesPerPage = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + maxMessagesPerPagePrp));
        maxMessagesPerSentence = dataFactory.getOWLDataProperty(IRI.create(nlowlNS + maxMessagesPerSentencePrp));

        forSentencePlan = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + forSentencePlanPrp));
        forNLName = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + forNLNamePrp));
        forUserType = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + forUserTypePrp));
        forModifier = dataFactory.getOWLObjectProperty(IRI.create(nlowlNS + forModifierPrp));

        forProperty = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + forPropertyPrp));
        forOwner = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + forOwnerPrp));

        //Comparison property IRIs
        comparisonsAllowed = dataFactory.getOWLAnnotationProperty(IRI.create(nlowlNS + comparisonsAllowedPrp));

        domainIndependentProperties.add(instanceOf);
        domainIndependentProperties.add(isA);
        domainIndependentProperties.add(oneOf);
        domainIndependentProperties.add(differentIndividuals);
        domainIndependentProperties.add(sameIndividuals);
    }

    public OWLDataFactory getDataFactory() {
        return this.dataFactory;
    }

    public OWLOntologyManager getOntologyManager() {
        return this.NLResourcesManager;
    }

    public OWLOntology getNLResourcesModel() {
        return this.NLResourcesModel;
    }

    public void loadNLResourcesModel(File resourcesFile) {
        try {
            if (resourcesFile.exists()) {
                NLResourcesManager.removeOntology(NLResourcesModel);

                NLResourcesModel = NLResourcesManager.loadOntologyFromOntologyDocument(resourcesFile);
                resourcesNS = NLResourcesModel.getOntologyID().getOntologyIRI().toString() + "#";
            }
        } catch (org.semanticweb.owlapi.model.OWLOntologyCreationException e) {
        }
    }

    public void loadNLResourcesModel(OWLOntology resourcesModel) {
        NLResourcesModel = resourcesModel;

        resourcesNS = NLResourcesModel.getOntologyID().getOntologyIRI().toString() + "#";
    }

    public void closeModel() {
        NLResourcesManager.removeOntology(NLResourcesModel);
    }

    public void exportResourcesOntologyTBox(OWLOntology resourceOntology) {
        //NLResource class
        OWLClass clsA = NLResource;

        //Language class + Individuals
        OWLClass clsB = Language;
        OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        AddAxiom addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLNamedIndividual indiv = englishLanguage;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = greekLanguage;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Case class + Individuals
        clsB = Case;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = nominativeCase;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = genitiveCase;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = accusativeCase;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Number class + Individuals
        clsB = Number;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = singularNumber;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = pluralNumber;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = bothNumbers;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Gender class + Individuals
        clsB = Gender;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = masculineGender;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = feminineGender;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = neuterGender;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = masculineOrFeminineGender;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //ReferringExpressionType class + Individuals
        clsB = ReferringExpressionType;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = autoRefExpression;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = pronounRefExpression;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = demonstrativeRefExpression;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Tense class + Individuals
        clsB = Tense;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = simplePresentTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = presentContinuousTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = presentPerfectTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = simplePastTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = pastContinuousTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = pastPerfectTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = pastPerfectContinuousTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = simpleFutureTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = futureContinuousTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = futurePerfectTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = futurePerfectContinuousTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = infinitiveTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = participleTense;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Voice class + Individuals
        clsB = Voice;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = activeVoice;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = passiveVoice;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Person class + Individuals
        clsB = Person;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = firstPerson;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = secondPerson;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = thirdPerson;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Modifier class + Individuals
        clsB = Modifier;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = exactCardinality;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = minCardinality;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = maxCardinality;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = allValuesFrom;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = someValuesFrom;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //DomainIndependentProperty class + Individuals
        clsB = DomainIndependentProperty;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = instanceOf;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = oneOf;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = differentIndividuals;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = sameIndividuals;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = isA;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Section class + Individuals
        clsB = Section;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = defaultSection;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //UserType class
        clsB = UserType;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Anonymous indiv        
        indiv = anonymous;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsA, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //NLName class
        clsB = NLName;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality aggregationAllowedExact1 = dataFactory.getOWLDataExactCardinality(1, aggregationAllowed, dataFactory.getBooleanOWLDatatype());
        OWLDataExactCardinality focusLostExact1 = dataFactory.getOWLDataExactCardinality(1, focusLost, dataFactory.getBooleanOWLDatatype());
        OWLObjectExactCardinality forLanguageExact1 = dataFactory.getOWLObjectExactCardinality(1, forLanguage, Language);
        OWLObjectOneOf objectOneOfLanguages = dataFactory.getOWLObjectOneOf(englishLanguage, greekLanguage);
        OWLObjectAllValuesFrom objectAllValuesFromLanguages = dataFactory.getOWLObjectAllValuesFrom(forLanguage, objectOneOfLanguages);

        OWLObjectIntersectionOf objectIntersection = dataFactory.getOWLObjectIntersectionOf(aggregationAllowedExact1, focusLostExact1, forLanguageExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //SentencePlan class
        clsB = SentencePlan;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(aggregationAllowedExact1, forLanguageExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Entry class
        clsB = Entry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //LexiconEntry class
        clsB = LexiconEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //UserType class
        clsB = UserType;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataMaxCardinality maxMessagesPerSentenceMax1 = dataFactory.getOWLDataMaxCardinality(1, maxMessagesPerSentence, dataFactory.getOWLDatatypeMinInclusiveRestriction(1));
        OWLDataMaxCardinality maxMessagesPerPageMax1 = dataFactory.getOWLDataMaxCardinality(1, maxMessagesPerPage, dataFactory.getOWLDatatypeMinInclusiveRestriction(-1));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(maxMessagesPerSentenceMax1, maxMessagesPerPageMax1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        indiv = globalUserModel;
        axiom = dataFactory.getOWLClassAssertionAxiom(clsB, indiv);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //Slot class
        clsB = Slot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasOrderExact1 = dataFactory.getOWLDataExactCardinality(1, hasOrder, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#nonNegativeInteger")));
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, hasOrderExact1);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //ArticleSlot class
        clsA = Slot;
        clsB = ArticleSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality isDefiniteExact1 = dataFactory.getOWLDataExactCardinality(1, isDefinite, dataFactory.getBooleanOWLDatatype());

        OWLObjectExactCardinality useCaseExact1 = dataFactory.getOWLObjectExactCardinality(1, useCase, Case);
        OWLObjectOneOf objectOneOfCases = dataFactory.getOWLObjectOneOf(nominativeCase, genitiveCase, accusativeCase);
        OWLObjectAllValuesFrom objectAllValuesFromCases = dataFactory.getOWLObjectAllValuesFrom(useCase, objectOneOfCases);

        OWLObjectExactCardinality useGenderExact1 = dataFactory.getOWLObjectExactCardinality(1, useGender, Gender);
        OWLObjectOneOf objectOneOfGenders = dataFactory.getOWLObjectOneOf(masculineGender, feminineGender, neuterGender, masculineOrFeminineGender);
        OWLObjectAllValuesFrom objectAllValuesFromGenders = dataFactory.getOWLObjectAllValuesFrom(useGender, objectOneOfGenders);

        OWLObjectExactCardinality useNumberExact1 = dataFactory.getOWLObjectExactCardinality(1, useNumber, Number);
        OWLObjectOneOf objectOneOfNumbers = dataFactory.getOWLObjectOneOf(singularNumber, pluralNumber, bothNumbers);
        OWLObjectAllValuesFrom objectAllValuesFromNumbers = dataFactory.getOWLObjectAllValuesFrom(useNumber, objectOneOfNumbers);

        OWLObjectExactCardinality agreeWithExact1 = dataFactory.getOWLObjectExactCardinality(1, agreeWith, Slot);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(isDefiniteExact1, useCaseExact1, objectAllValuesFromCases, useGenderExact1, objectAllValuesFromGenders, useNumberExact1, objectAllValuesFromNumbers, agreeWithExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //AdjectiveSlot class
        clsB = AdjectiveSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataMaxCardinality isHeadMax1 = dataFactory.getOWLDataMaxCardinality(1, isHead, dataFactory.getBooleanOWLDatatype());
        OWLDataMaxCardinality isCapitalizedMax1 = dataFactory.getOWLDataMaxCardinality(1, isCapitalized, dataFactory.getBooleanOWLDatatype());

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(isHeadMax1, isCapitalizedMax1, useCaseExact1, objectAllValuesFromCases, useGenderExact1, objectAllValuesFromGenders, useNumberExact1, objectAllValuesFromNumbers, agreeWithExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //NounSlot class
        clsB = NounSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(isHeadMax1, isCapitalizedMax1, useCaseExact1, objectAllValuesFromCases, useNumberExact1, objectAllValuesFromNumbers, agreeWithExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //StringSlot class
        clsB = StringSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasStringExact1 = dataFactory.getOWLDataExactCardinality(1, hasString, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, hasStringExact1);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //PrepositionSlot class
        clsB = PrepositionSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, hasStringExact1);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //OwnerSlot class
        clsB = OwnerSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLObjectExactCardinality refExpressionTypeExact1 = dataFactory.getOWLObjectExactCardinality(1, refExpressionType, ReferringExpressionType);
        OWLObjectOneOf objectOneOfRefExpressionTypes = dataFactory.getOWLObjectOneOf(autoRefExpression, pronounRefExpression, demonstrativeRefExpression);
        OWLObjectAllValuesFrom objectAllValuesFromRefExpressionTypes = dataFactory.getOWLObjectAllValuesFrom(refExpressionType, objectOneOfRefExpressionTypes);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(useCaseExact1, objectAllValuesFromCases, refExpressionTypeExact1, objectAllValuesFromRefExpressionTypes);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //FillerSlot class
        clsB = FillerSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality useBulletsExact1 = dataFactory.getOWLDataExactCardinality(1, useBullets, dataFactory.getBooleanOWLDatatype());

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(useBulletsExact1, useCaseExact1, objectAllValuesFromCases);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //VerbSlot class
        clsB = VerbSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality usePolarityExact1 = dataFactory.getOWLDataExactCardinality(1, usePolarity, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        OWLObjectExactCardinality useTenseExact1 = dataFactory.getOWLObjectExactCardinality(1, useTense, Tense);
        OWLObjectOneOf objectOneOfTenses = dataFactory.getOWLObjectOneOf(simplePresentTense, presentContinuousTense, presentPerfectTense, simplePastTense, pastContinuousTense, pastPerfectTense, pastPerfectContinuousTense, simpleFutureTense, futureContinuousTense, futurePerfectTense, futurePerfectContinuousTense, infinitiveTense, participleTense);
        OWLObjectAllValuesFrom objectAllValuesFromTenses = dataFactory.getOWLObjectAllValuesFrom(useTense, objectOneOfTenses);

        OWLObjectExactCardinality useVoiceExact1 = dataFactory.getOWLObjectExactCardinality(1, useVoice, Voice);
        OWLObjectOneOf objectOneOfVoices = dataFactory.getOWLObjectOneOf(activeVoice, passiveVoice);
        OWLObjectAllValuesFrom objectAllValuesFromVoices = dataFactory.getOWLObjectAllValuesFrom(useVoice, objectOneOfVoices);

        OWLObjectExactCardinality usePersonExact1 = dataFactory.getOWLObjectExactCardinality(1, usePerson, Person);
        OWLObjectOneOf objectOneOfPersons = dataFactory.getOWLObjectOneOf(firstPerson, secondPerson, thirdPerson);
        OWLObjectAllValuesFrom objectAllValuesFromPersons = dataFactory.getOWLObjectAllValuesFrom(usePerson, objectOneOfPersons);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(usePolarityExact1, useNumberExact1, objectAllValuesFromNumbers, useTenseExact1, objectAllValuesFromTenses, useVoiceExact1, objectAllValuesFromVoices, usePersonExact1, objectAllValuesFromPersons, agreeWithExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //ConcatenationSlot class
        clsB = ConcatenationSlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLObjectAllValuesFrom objectAllValuesFromPropertySlots = dataFactory.getOWLObjectAllValuesFrom(concatenates, PropertySlot);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectAllValuesFromPropertySlots);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //PropertySlot class
        clsB = PropertySlot;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasPropertyExact1 = dataFactory.getOWLDataExactCardinality(1, hasProperty, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, hasPropertyExact1);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //AdjectiveLexiconEntry class
        clsA = LexiconEntry;
        clsB = AdjectiveLexiconEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //NounLexiconEntry class
        clsB = NounLexiconEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //VerbLexiconEntry class
        clsB = VerbLexiconEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //EnglishEntry class
        clsA = Entry;
        clsB = EnglishEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //GreekEntry class
        clsB = GreekEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //EnglishAdjectiveEntry class
        clsA = EnglishEntry;
        clsB = EnglishAdjectiveEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasFormEnglishExact1 = dataFactory.getOWLDataExactCardinality(1, hasFormEnglish, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, hasFormEnglishExact1);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //EnglishNounEntry class
        clsB = EnglishNounEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasSingularEnglishExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularEnglish, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralEnglishExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralEnglish, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(hasSingularEnglishExact1, hasPluralEnglishExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //EnglishVerbEntry class
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality baseFormExact1 = dataFactory.getOWLDataExactCardinality(1, baseForm, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality simplePres3rdSingExact1 = dataFactory.getOWLDataExactCardinality(1, simplePres3rdSing, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality presParticipleExact1 = dataFactory.getOWLDataExactCardinality(1, presParticiple, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality simplePastExact1 = dataFactory.getOWLDataExactCardinality(1, simplePast, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality pastParticipleExact1 = dataFactory.getOWLDataExactCardinality(1, pastParticiple, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(baseFormExact1, simplePres3rdSingExact1, presParticipleExact1, simplePastExact1, pastParticipleExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //GreekAdjectiveEntry class
        clsA = GreekEntry;
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasSingularNominativeMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularNominativeMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularNominativeFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularNominativeFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularNominativeNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularNominativeNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralNominativeMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralNominativeMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralNominativeFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralNominativeFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralNominativeNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralNominativeNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularGenitiveMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularGenitiveMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularGenitiveFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularGenitiveFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularGenitiveNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularGenitiveNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralGenitiveMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralGenitiveMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralGenitiveFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralGenitiveFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralGenitiveNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralGenitiveNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularAccusativeMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularAccusativeMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularAccusativeFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularAccusativeFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularAccusativeNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularAccusativeNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralAccusativeMasculineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralAccusativeMasculineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralAccusativeFemilineGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralAccusativeFeminineGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralAccusativeNeuterGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralAccusativeNeuterGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(hasSingularNominativeMasculineGreekExact1,
                hasSingularNominativeFemilineGreekExact1,
                hasSingularNominativeNeuterGreekExact1,
                hasPluralNominativeMasculineGreekExact1,
                hasPluralNominativeFemilineGreekExact1,
                hasPluralNominativeNeuterGreekExact1,
                hasSingularGenitiveMasculineGreekExact1,
                hasSingularGenitiveFemilineGreekExact1,
                hasSingularGenitiveNeuterGreekExact1,
                hasPluralGenitiveMasculineGreekExact1,
                hasPluralGenitiveFemilineGreekExact1,
                hasPluralGenitiveNeuterGreekExact1,
                hasSingularAccusativeMasculineGreekExact1,
                hasSingularAccusativeFemilineGreekExact1,
                hasSingularAccusativeNeuterGreekExact1,
                hasPluralAccusativeMasculineGreekExact1,
                hasPluralAccusativeFemilineGreekExact1,
                hasPluralAccusativeNeuterGreekExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //GreekNounEntry class
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality hasSingularNominativeGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularNominativeGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularGenitiveGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularGenitiveGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasSingularAccusativeGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasSingularAccusativeGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralNominativeGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralNominativeGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralGenitiveGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralGenitiveGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality hasPluralAccusativeGreekExact1 = dataFactory.getOWLDataExactCardinality(1, hasPluralAccusativeGreek, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(hasSingularNominativeGreekExact1,
                hasSingularGenitiveGreekExact1,
                hasSingularAccusativeGreekExact1,
                hasPluralNominativeGreekExact1,
                hasPluralGenitiveGreekExact1,
                hasPluralAccusativeGreekExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //GreekVerbEntry class
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLDataExactCardinality activeSimplePresent1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePresent2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePresent3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePresent1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePresent2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePresent3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePresent3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimplePast3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimplePast3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activePastContinuous3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activePastContinuous3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeSimpleFuture3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, activeSimpleFuture3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeInfinitiveExact1 = dataFactory.getOWLDataExactCardinality(1, activeInfinitive, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality activeParticipleExact1 = dataFactory.getOWLDataExactCardinality(1, activeParticiple, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePresent3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePresent3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimplePast3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimplePast3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passivePastContinuous3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passivePastContinuous3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture1stSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture1stSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture2ndSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture2ndSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture3rdSingularExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture3rdSingular, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture1stPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture1stPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture2ndPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture2ndPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveSimpleFuture3rdPluralExact1 = dataFactory.getOWLDataExactCardinality(1, passiveSimpleFuture3rdPlural, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveInfinitiveExact1 = dataFactory.getOWLDataExactCardinality(1, passiveInfinitive, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));
        OWLDataExactCardinality passiveParticipleExact1 = dataFactory.getOWLDataExactCardinality(1, passiveParticiple, dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string")));

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(activeSimplePresent1stSingularExact1,
                activeSimplePresent2ndSingularExact1,
                activeSimplePresent3rdSingularExact1,
                activeSimplePresent1stPluralExact1,
                activeSimplePresent2ndPluralExact1,
                activeSimplePresent3rdPluralExact1,
                activeSimplePast1stSingularExact1,
                activeSimplePast2ndSingularExact1,
                activeSimplePast3rdSingularExact1,
                activeSimplePast1stPluralExact1,
                activeSimplePast2ndPluralExact1,
                activeSimplePast3rdPluralExact1,
                activePastContinuous1stSingularExact1,
                activePastContinuous2ndSingularExact1,
                activePastContinuous3rdSingularExact1,
                activePastContinuous1stPluralExact1,
                activePastContinuous2ndPluralExact1,
                activePastContinuous3rdPluralExact1,
                activeSimpleFuture1stSingularExact1,
                activeSimpleFuture2ndSingularExact1,
                activeSimpleFuture3rdSingularExact1,
                activeSimpleFuture1stPluralExact1,
                activeSimpleFuture2ndPluralExact1,
                activeSimpleFuture3rdPluralExact1,
                activeInfinitiveExact1,
                activeParticipleExact1,
                passiveSimplePresent1stSingularExact1,
                passiveSimplePresent2ndSingularExact1,
                passiveSimplePresent3rdSingularExact1,
                passiveSimplePresent1stPluralExact1,
                passiveSimplePresent2ndPluralExact1,
                passiveSimplePresent3rdPluralExact1,
                passiveSimplePast1stSingularExact1,
                passiveSimplePast2ndSingularExact1,
                passiveSimplePast3rdSingularExact1,
                passiveSimplePast1stPluralExact1,
                passiveSimplePast2ndPluralExact1,
                passiveSimplePast3rdPluralExact1,
                passivePastContinuous1stSingularExact1,
                passivePastContinuous2ndSingularExact1,
                passivePastContinuous3rdSingularExact1,
                passivePastContinuous1stPluralExact1,
                passivePastContinuous2ndPluralExact1,
                passivePastContinuous3rdPluralExact1,
                passiveSimpleFuture1stSingularExact1,
                passiveSimpleFuture2ndSingularExact1,
                passiveSimpleFuture3rdSingularExact1,
                passiveSimpleFuture1stPluralExact1,
                passiveSimpleFuture2ndPluralExact1,
                passiveSimpleFuture3rdPluralExact1,
                passiveInfinitiveExact1,
                passiveParticipleExact1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //AnnotationEvent class
        clsA = NLResource;
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, clsA);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        OWLObjectAllValuesFrom forUserTypeAllValues = dataFactory.getOWLObjectAllValuesFrom(forUserType, UserType);

        OWLObjectOneOf objectOneOfModifiers = dataFactory.getOWLObjectOneOf(exactCardinality, minCardinality, maxCardinality, allValuesFrom, someValuesFrom);
        OWLObjectAllValuesFrom objectAllValuesFromModifiers = dataFactory.getOWLObjectAllValuesFrom(forModifier, objectOneOfModifiers);
        OWLObjectMaxCardinality forModifierMax1 = dataFactory.getOWLObjectMaxCardinality(1, forModifier, objectAllValuesFromModifiers);

        OWLObjectMaxCardinality forSentencePlanMax1 = dataFactory.getOWLObjectMaxCardinality(1, forSentencePlan, SentencePlan);
        OWLObjectMaxCardinality forNLNameMax1 = dataFactory.getOWLObjectMaxCardinality(1, forNLName, NLName);

        objectIntersection = dataFactory.getOWLObjectIntersectionOf(forUserTypeAllValues, forModifierMax1, forSentencePlanMax1, forNLNameMax1);
        axiom = dataFactory.getOWLSubClassOfAxiom(clsB, objectIntersection);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasOrder property
        OWLDataProperty dataProperty = hasOrder;
        //Domain
        clsB = Slot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        OWLDatatype dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //isHead property
        dataProperty = isHead;
        //Domain
        clsB = Slot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //isCapitalized property
        dataProperty = isCapitalized;
        //Domain
        clsB = Slot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //isDefinite property
        dataProperty = isDefinite;
        //Domain
        clsB = ArticleSlot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasString property
        dataProperty = hasString;
        //Domain
        clsB = StringSlot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //aggregationAllowed property
        dataProperty = aggregationAllowed;
        //Domain
        HashSet<OWLClassExpression> clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(NLName);
        clsUnion.add(SentencePlan);
        OWLObjectUnionOf objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //focusLost property
        dataProperty = focusLost;
        //Domain
        clsB = NLName;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //usePolarity property
        dataProperty = usePolarity;
        //Domain
        clsB = VerbSlot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useBullets property
        dataProperty = useBullets;
        //Domain
        clsB = FillerSlot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getBooleanOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasProperty property
        dataProperty = hasProperty;
        //Domain
        clsB = PropertySlot;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularEnglish property
        dataProperty = hasSingularEnglish;
        //Domain
        clsB = EnglishNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralEnglish property
        dataProperty = hasPluralEnglish;
        //Domain
        clsB = EnglishNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularNominativeGreek property
        dataProperty = hasSingularNominativeGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularGenitiveGreek property
        dataProperty = hasSingularGenitiveGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularAccusativeGreek property
        dataProperty = hasSingularAccusativeGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralNominativeGreek property
        dataProperty = hasPluralNominativeGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralGenitiveGreek property
        dataProperty = hasPluralGenitiveGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralAccusativeGreek property
        dataProperty = hasPluralAccusativeGreek;
        //Domain
        clsB = GreekNounEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasFormEnglish property
        dataProperty = hasFormEnglish;
        //Domain
        clsB = EnglishAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularNominativeMasculineGreek property
        dataProperty = hasSingularNominativeMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularNominativeFeminineGreek property
        dataProperty = hasSingularNominativeFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularNominativeNeuterGreek property
        dataProperty = hasSingularNominativeNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //hasPluralNominativeMasculineGreek property
        dataProperty = hasPluralNominativeMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralNominativeFeminineGreek property
        dataProperty = hasPluralNominativeFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralNominativeNeuterGreek property
        dataProperty = hasPluralNominativeNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);//hasSingularGenitiveMasculineGreek property
        dataProperty = hasSingularGenitiveMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularGenitiveFeminineGreek property
        dataProperty = hasSingularGenitiveFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularGenitiveNeuterGreek property
        dataProperty = hasSingularGenitiveNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //hasPluralGenitiveMasculineGreek property
        dataProperty = hasPluralGenitiveMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralGenitiveFeminineGreek property
        dataProperty = hasPluralGenitiveFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralGenitiveNeuterGreek property
        dataProperty = hasPluralGenitiveNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);//hasSingularAccusativeMasculineGreek property
        dataProperty = hasSingularAccusativeMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularAccusativeFeminineGreek property
        dataProperty = hasSingularAccusativeFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSingularAccusativeNeuterGreek property
        dataProperty = hasSingularAccusativeNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //hasPluralAccusativeMasculineGreek property
        dataProperty = hasPluralAccusativeMasculineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralAccusativeFeminineGreek property
        dataProperty = hasPluralAccusativeFeminineGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasPluralAccusativeNeuterGreek property
        dataProperty = hasPluralAccusativeNeuterGreek;
        //Domain
        clsB = GreekAdjectiveEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //baseForm property
        dataProperty = baseForm;
        //Domain
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //simplePres3rdSing property
        dataProperty = simplePres3rdSing;
        //Domain
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //presParticiple property
        dataProperty = presParticiple;
        //Domain
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //simplePast property
        dataProperty = simplePast;
        //Domain
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //pastParticiple property
        dataProperty = pastParticiple;
        //Domain
        clsB = EnglishVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent1stSingular property
        dataProperty = activeSimplePresent1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent2ndSingular property
        dataProperty = activeSimplePresent2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent3rdSingular property
        dataProperty = activeSimplePresent3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent1stPlural property
        dataProperty = activeSimplePresent1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent2ndPlural property
        dataProperty = activeSimplePresent2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePresent3rdPlural property
        dataProperty = activeSimplePresent3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast1stSingular property
        dataProperty = activeSimplePast1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast2ndSingular property
        dataProperty = activeSimplePast2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast3rdSingular property
        dataProperty = activeSimplePast3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast1stPlural property
        dataProperty = activeSimplePast1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast2ndPlural property
        dataProperty = activeSimplePast2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimplePast3rdPlural property
        dataProperty = activeSimplePast3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous1stSingular property
        dataProperty = activePastContinuous1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous2ndSingular property
        dataProperty = activePastContinuous2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous3rdSingular property
        dataProperty = activePastContinuous3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous1stPlural property
        dataProperty = activePastContinuous1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous2ndPlural property
        dataProperty = activePastContinuous2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activePastContinuous3rdPlural property
        dataProperty = activePastContinuous3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture1stSingular property
        dataProperty = activeSimpleFuture1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture2ndSingular property
        dataProperty = activeSimpleFuture2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture3rdSingular property
        dataProperty = activeSimpleFuture3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture1stPlural property
        dataProperty = activeSimpleFuture1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture2ndPlural property
        dataProperty = activeSimpleFuture2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeSimpleFuture3rdPlural property
        dataProperty = activeSimpleFuture3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeInfinitive property
        dataProperty = activeInfinitive;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //activeParticiple property
        dataProperty = activeParticiple;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent1stSingular property
        dataProperty = passiveSimplePresent1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent2ndSingular property
        dataProperty = passiveSimplePresent2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent3rdSingular property
        dataProperty = passiveSimplePresent3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent1stPlural property
        dataProperty = passiveSimplePresent1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent2ndPlural property
        dataProperty = passiveSimplePresent2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePresent3rdPlural property
        dataProperty = passiveSimplePresent3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast1stSingular property
        dataProperty = passiveSimplePast1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast2ndSingular property
        dataProperty = passiveSimplePast2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast3rdSingular property
        dataProperty = passiveSimplePast3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast1stPlural property
        dataProperty = passiveSimplePast1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast2ndPlural property
        dataProperty = passiveSimplePast2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimplePast3rdPlural property
        dataProperty = passiveSimplePast3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous1stSingular property
        dataProperty = passivePastContinuous1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous2ndSingular property
        dataProperty = passivePastContinuous2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous3rdSingular property
        dataProperty = passivePastContinuous3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous1stPlural property
        dataProperty = passivePastContinuous1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous2ndPlural property
        dataProperty = passivePastContinuous2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passivePastContinuous3rdPlural property
        dataProperty = passivePastContinuous3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture1stSingular property
        dataProperty = passiveSimpleFuture1stSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture2ndSingular property
        dataProperty = passiveSimpleFuture2ndSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture3rdSingular property
        dataProperty = passiveSimpleFuture3rdSingular;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture1stPlural property
        dataProperty = passiveSimpleFuture1stPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture2ndPlural property
        dataProperty = passiveSimpleFuture2ndPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveSimpleFuture3rdPlural property
        dataProperty = passiveSimpleFuture3rdPlural;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveInfinitive property
        dataProperty = passiveInfinitive;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //passiveParticiple property
        dataProperty = passiveParticiple;
        //Domain
        clsB = GreekVerbEntry;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#string"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasAppropriateness property
        dataProperty = hasAppropriateness;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(NLName);
        clsUnion.add(SentencePlan);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getIntegerOWLDatatype();
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasInterest property
        dataProperty = hasInterest;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        OWLDatatypeRestriction rangeDataRestriction = dataFactory.getOWLDatatypeMinMaxInclusiveRestriction(-1, 3);
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, rangeDataRestriction);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //maxRepetitions property
        dataProperty = maxRepetitions;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        dataType = dataFactory.getOWLDatatype(IRI.create("http://www.w3.org/2001/XMLSchema#nonNegativeInteger"));
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, dataType);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //maxMessagesPerPage property
        dataProperty = maxMessagesPerPage;
        //Domain
        clsB = UserType;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        rangeDataRestriction = dataFactory.getOWLDatatypeMinInclusiveRestriction(-1);
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, rangeDataRestriction);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //maxMessagesPerSentence property
        dataProperty = maxMessagesPerSentence;
        //Domain
        clsB = UserType;
        axiom = dataFactory.getOWLDataPropertyDomainAxiom(dataProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        rangeDataRestriction = dataFactory.getOWLDatatypeMinInclusiveRestriction(1);
        axiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, rangeDataRestriction);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasSlot property
        OWLObjectProperty objectProperty = hasSlot;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(NLName);
        clsUnion.add(SentencePlan);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(ArticleSlot);
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(VerbSlot);
        clsUnion.add(StringSlot);
        clsUnion.add(PrepositionSlot);
        clsUnion.add(OwnerSlot);
        clsUnion.add(FillerSlot);
        clsUnion.add(ConcatenationSlot);
        clsUnion.add(PropertySlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useLexiconEntry property
        objectProperty = useLexiconEntry;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(VerbSlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = LexiconEntry;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //agreeWith property
        objectProperty = agreeWith;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(VerbSlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(OwnerSlot);
        clsUnion.add(FillerSlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //forLanguage property
        objectProperty = forLanguage;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(NLName);
        clsUnion.add(SentencePlan);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromLanguages);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useTense property
        objectProperty = useTense;
        //Domain
        clsB = VerbSlot;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromTenses);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useVoice property
        objectProperty = useVoice;
        //Domain
        clsB = VerbSlot;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromVoices);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //refExpressionType property
        objectProperty = refExpressionType;
        //Domain
        clsB = OwnerSlot;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromRefExpressionTypes);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //usePerson property
        objectProperty = usePerson;
        //Domain
        clsB = VerbSlot;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromPersons);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useNumber property
        objectProperty = useNumber;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(ArticleSlot);
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(VerbSlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromNumbers);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useCase property
        objectProperty = useCase;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(ArticleSlot);
        clsUnion.add(AdjectiveSlot);
        clsUnion.add(NounSlot);
        clsUnion.add(OwnerSlot);
        clsUnion.add(FillerSlot);
        clsUnion.add(PropertySlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromCases);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //useGender property
        objectProperty = useGender;
        //Domain
        clsUnion = new HashSet<OWLClassExpression>();
        clsUnion.add(ArticleSlot);
        clsUnion.add(AdjectiveSlot);
        objectUnionOf = dataFactory.getOWLObjectUnionOf(clsUnion);
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, objectUnionOf);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromGenders);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasNumber property
        objectProperty = hasNumber;
        //Domain
        clsB = NounLexiconEntry;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromNumbers);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasGender property
        objectProperty = hasGender;
        //Domain
        clsB = NounLexiconEntry;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromGenders);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //concatenates property
        objectProperty = concatenates;
        //Domain
        clsB = ConcatenationSlot;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = PropertySlot;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasEnglishEntry property
        objectProperty = hasEnglishEntry;
        //Domain
        clsB = LexiconEntry;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = EnglishEntry;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //hasGreekEntry property
        objectProperty = hasGreekEntry;
        //Domain
        clsB = LexiconEntry;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = GreekEntry;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //forUserType property
        objectProperty = forUserType;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = UserType;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //forModifier property
        objectProperty = forModifier;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, objectAllValuesFromModifiers);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //forSentencePlan property
        objectProperty = forSentencePlan;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = SentencePlan;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);

        //forNLName property
        objectProperty = forNLName;
        //Domain
        clsB = AnnotationEvent;
        axiom = dataFactory.getOWLObjectPropertyDomainAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
        //Range
        clsB = NLName;
        axiom = dataFactory.getOWLObjectPropertyRangeAxiom(objectProperty, clsB);
        addAxiom = new AddAxiom(resourceOntology, axiom);
        getOntologyManager().applyChange(addAxiom);
    }
}