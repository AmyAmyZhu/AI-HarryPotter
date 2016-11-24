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

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;

import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.LexEntry;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerb;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbGR;
import gr.aueb.cs.nlg.NLFiles.LexiconQueryManager;
import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLNArticleSlot;
import gr.aueb.cs.nlg.NLFiles.NLNNounSlot;
import gr.aueb.cs.nlg.NLFiles.NLNPrepositionSlot;
import gr.aueb.cs.nlg.NLFiles.NLNSlot;
import gr.aueb.cs.nlg.NLFiles.NLNStringSlot;
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.NLNameQueryManager;

import gr.aueb.cs.nlg.Utils.NLGUser;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import gr.aueb.cs.nlg.Languages.Languages;

import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.OrderingQueryManager;
import java.util.HashSet;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SurfaceRealization extends NLGEngineComponent {

    private AnnotatedDescription annotatedDescription;
    private StringBuffer realizedText;
    private NLNameQueryManager NLNQM;
    private LexiconQueryManager LQM;
    private OrderingQueryManager OQM;
    private MappingQueryManager MQM;
    private Set<OWLOntology> mainModels;
    private NLGUser user;
    private boolean sectionParagraphs;
    private boolean generateReferringExpressions;
    private boolean annotateGeneratedResources;
    private static boolean first;
    public static String PROD_RE_BAD = "BAD_RE";
    public static String PROD_RE_NP = "NP";
    public static String PROD_RE_Pronoun = "Pronoun";
    public static String PROD_RE_Demonstrative = "Demonstrative";
    public static String PROD_RE_Article = "Article";
    public static String PROD_RE_NULL = "NullSubject";
    private HashMap<IRI, IRI> generatedSuperNames;

    SurfaceRealization(Set<OWLOntology> ontModels, NLNameQueryManager NLNQM, LexiconQueryManager LQM, MappingQueryManager MQM, OrderingQueryManager OQM, String language) {
        super(language);
        sectionParagraphs = false;
        annotatedDescription = new AnnotatedDescription();
        this.NLNQM = NLNQM;
        this.LQM = LQM;
        this.MQM = MQM;
        this.OQM = OQM;
        this.mainModels = ontModels;

        generateReferringExpressions = true;
        generatedSuperNames = new HashMap<IRI, IRI>();
    }

    public void setModel(Set<OWLOntology> m) {
        this.mainModels = m;
    }

    public String realizeMessages(XmlMsgs messages, NLGUser user) {
        // create a new annotated description
        annotatedDescription.generateAnnotatedDescription();
        annotatedDescription.setEntityId(messages.getOwner());

        this.user = user;

        realizedText = new StringBuffer();

        ArrayList<Node> messageNodes = messages.getMessages();

        String section = "";
        boolean isFirstMessage = true;
        for (int i = 0; i < messageNodes.size(); i++) {
            first = true;
            ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(messageNodes.get(i));

            if (sectionParagraphs) {
                if (!XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SECTION_NAME).equals(section)) {
                    section = XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SECTION_NAME);
                    if (!section.isEmpty()) {
                        IRI currentSectionIRI = null;
                        for (IRI sectionIRI : OQM.getOrderedSections()) {
                            if (sectionIRI.getFragment().equals(section)) {
                                currentSectionIRI = sectionIRI;
                            }
                        }

                        if (currentSectionIRI != null) {
                            if (!isFirstMessage) {
                                realizedText.append("<br><br>");
                            }

                            String name = OQM.getSectionLabel(currentSectionIRI, getLanguage());
                            if (name.isEmpty()) {
                                name = capitalize(DefaultResourcesManager.spaceString(currentSectionIRI.getFragment(), false));
                            }

                            realizedText.append("<u><b>").append(name).append("</u></b><br><br>");

                            first = true;
                        }
                    }
                }
            }

            if (messageSlots.size() > 0) {
                if (XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).startsWith(NLResourceManager.nlowlNS) && !DefaultResourcesManager.isDefaultResource(IRI.create(XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))) && annotateGeneratedResources) {
                    realizedText.append("<font color=\"#580000\"><b>");
                }
                realizeMessageSlots(realizedText, messageSlots);
                if (XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).startsWith(NLResourceManager.nlowlNS) && !DefaultResourcesManager.isDefaultResource(IRI.create(XmlMsgs.getAttribute(messageNodes.get(i), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))) && annotateGeneratedResources) {
                    realizedText.append("</b></font>");
                }
                first = false;
                isFirstMessage = false;
            }
        }

        String ret = realizedText.toString();

        ret = ret.replaceAll(" is not ", " isn't ");
        ret = ret.replaceAll(" are not ", " aren't ");
        ret = ret.replaceAll(" has not ", " hasn't ");
        ret = ret.replaceAll(" have not ", " haven't ");
        ret = ret.replaceAll(" do not ", " don't ");
        ret = ret.replaceAll(" does not ", " doesn't ");
        ret = ret.replaceAll(" did not ", " didn't ");
        ret = ret.replaceAll(" can not ", " cannot ");

        ret = ret.replaceAll(" exactly 1 ", " exactly one ");
        ret = ret.replaceAll(" exactly 2 ", " exactly two ");
        ret = ret.replaceAll(" exactly 3 ", " exactly three ");
        ret = ret.replaceAll(" exactly 4 ", " exactly four ");
        ret = ret.replaceAll(" exactly 5 ", " exactly five ");
        ret = ret.replaceAll(" exactly 6 ", " exactly six ");
        ret = ret.replaceAll(" exactly 7 ", " exactly seven ");
        ret = ret.replaceAll(" exactly 8 ", " exactly eight ");
        ret = ret.replaceAll(" exactly 9 ", " exactly nine ");

        ret = ret.replaceAll(" at most 1 ", " at most one ");
        ret = ret.replaceAll(" at most 2 ", " at most two ");
        ret = ret.replaceAll(" at most 3 ", " at most three ");
        ret = ret.replaceAll(" at most 4 ", " at most four ");
        ret = ret.replaceAll(" at most 5 ", " at most five ");
        ret = ret.replaceAll(" at most 6 ", " at most six ");
        ret = ret.replaceAll(" at most 7 ", " at most seven ");
        ret = ret.replaceAll(" at most 8 ", " at most eight ");
        ret = ret.replaceAll(" at most 9 ", " at most nine ");

        ret = ret.replaceAll(" at least 1 ", " at least one ");
        ret = ret.replaceAll(" at least 2 ", " at least two ");
        ret = ret.replaceAll(" at least 3 ", " at least three ");
        ret = ret.replaceAll(" at least 4 ", " at least four ");
        ret = ret.replaceAll(" at least 5 ", " at least five ");
        ret = ret.replaceAll(" at least 6 ", " at least six ");
        ret = ret.replaceAll(" at least 7 ", " at least seven ");
        ret = ret.replaceAll(" at least 8 ", " at least eight ");
        ret = ret.replaceAll(" at least 9 ", " at least nine ");

        ret = ret.replaceAll(" only 1 ", " only one ");
        ret = ret.replaceAll(" only 2 ", " only two ");
        ret = ret.replaceAll(" only 3 ", " only three ");
        ret = ret.replaceAll(" only 4 ", " only four ");
        ret = ret.replaceAll(" only 5 ", " only five ");
        ret = ret.replaceAll(" only 6 ", " only six ");
        ret = ret.replaceAll(" only 7 ", " only seven ");
        ret = ret.replaceAll(" only 8 ", " only eight ");
        ret = ret.replaceAll(" only 9 ", " only nine ");

        ret = ret.replaceAll(" σε το ", " στο ");
        ret = ret.replaceAll(" σε τον ", " στον ");
        ret = ret.replaceAll(" σε τη ", " στη ");
        ret = ret.replaceAll(" σε την ", " στην ");
        ret = ret.replaceAll(" σε τα ", " στα ");
        ret = ret.replaceAll(" σε τους ", " στους ");
        ret = ret.replaceAll(" σε τις ", " στις ");
        ret = ret.replaceAll(" σε των ", " στων ");

        ret = ret.replaceAll(" ακριβώς 1 ", " ακριβώς ένα ");
        ret = ret.replaceAll(" ακριβώς 2 ", " ακριβώς δύο ");
        ret = ret.replaceAll(" ακριβώς 3 ", " ακριβώς τρία ");
        ret = ret.replaceAll(" ακριβώς 4 ", " ακριβώς τέσσερα ");
        ret = ret.replaceAll(" ακριβώς 5 ", " ακριβώς πέντε ");
        ret = ret.replaceAll(" ακριβώς 6 ", " ακριβώς έξι ");
        ret = ret.replaceAll(" ακριβώς 7 ", " ακριβώς επτά ");
        ret = ret.replaceAll(" ακριβώς 8 ", " ακριβώς οχτώ ");
        ret = ret.replaceAll(" ακριβώς 9 ", " ακριβώς εννιά ");

        ret = ret.replaceAll(" μόνο 1 ", " μόνο ένα ");
        ret = ret.replaceAll(" μόνο 2 ", " μόνο δύο ");
        ret = ret.replaceAll(" μόνο 3 ", " μόνο τρία ");
        ret = ret.replaceAll(" μόνο 4 ", " μόνο τέσσερα ");
        ret = ret.replaceAll(" μόνο 5 ", " μόνο πέντε ");
        ret = ret.replaceAll(" μόνο 6 ", " μόνο έξι ");
        ret = ret.replaceAll(" μόνο 7 ", " μόνο επτά ");
        ret = ret.replaceAll(" μόνο 8 ", " μόνο οχτώ ");
        ret = ret.replaceAll(" μόνο 9 ", " μόνο εννιά ");

        ret = ret.replaceAll(" το περισσότερο 1 ", " το περισσότερο ένα ");
        ret = ret.replaceAll(" το περισσότερο 2 ", " το περισσότερο δύο ");
        ret = ret.replaceAll(" το περισσότερο 3 ", " το περισσότερο τρία ");
        ret = ret.replaceAll(" το περισσότερο 4 ", " το περισσότερο τέσσερα ");
        ret = ret.replaceAll(" το περισσότερο 5 ", " το περισσότερο πέντε ");
        ret = ret.replaceAll(" το περισσότερο 6 ", " το περισσότερο έξι ");
        ret = ret.replaceAll(" το περισσότερο 7 ", " το περισσότερο επτά ");
        ret = ret.replaceAll(" το περισσότερο 8 ", " το περισσότερο οχτώ ");
        ret = ret.replaceAll(" το περισσότερο 9 ", " το περισσότερο εννιά ");

        ret = ret.replaceAll(" το λιγότερο 1 ", " το λιγότερο ένα ");
        ret = ret.replaceAll(" το λιγότερο 2 ", " το λιγότερο δύο ");
        ret = ret.replaceAll(" το λιγότερο 3 ", " το λιγότερο τρία ");
        ret = ret.replaceAll(" το λιγότερο 4 ", " το λιγότερο τέσσερα ");
        ret = ret.replaceAll(" το λιγότερο 5 ", " το λιγότερο πέντε ");
        ret = ret.replaceAll(" το λιγότερο 6 ", " το λιγότερο έξι ");
        ret = ret.replaceAll(" το λιγότερο 7 ", " το λιγότερο επτά ");
        ret = ret.replaceAll(" το λιγότερο 8 ", " το λιγότερο οχτώ ");
        ret = ret.replaceAll(" το λιγότερο 9 ", " το λιγότερο εννιά ");

        ret = ret.replaceAll(" ;", ";");
        ret = ret.replaceAll(" ,", ",");
        ret = ret.replaceAll(" :", ":");
        ret = ret.replaceAll(" ''", "''");
        ret = ret.replaceAll("[(] ", "(");
        ret = ret.replaceAll(" [)]", ")");

        return ret;
    }//Realize
    //-----------------------------------------------------------------------------------

    private void realizeMessageSlots(StringBuffer REALIZED_TEXT, ArrayList<Node> messageSlots) {
        try {
            boolean firstFiller = true;

            for (int j = 0; j < messageSlots.size(); j++) {
                Node slot = messageSlots.get(j);

                //if the Slot is verb
                if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                    IRI lexiconEntryIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI));
                    String voice = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.VOICE_TAG);
                    String tense = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.TENSE_TAG);

                    String agreeWith = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                    String number = XmlMsgs.SINGULAR;
                    String person = XmlMsgs.PERSON_3RD;

                    if (agreeWith.isEmpty()) {
                        number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                        person = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.PERSON_TAG);
                    } else {
                        String recursiveAgreeWith = agreeWith;
                        while (!recursiveAgreeWith.isEmpty()) {
                            for (int k = 0; k < messageSlots.size(); k++) {
                                if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(recursiveAgreeWith)) {
                                    recursiveAgreeWith = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                                    if (!recursiveAgreeWith.isEmpty()) {
                                        agreeWith = recursiveAgreeWith;
                                    }
                                }
                            }
                        }
                        for (int k = 0; k < messageSlots.size(); k++) {
                            if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(agreeWith)) {
                                if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ADJECTIVE_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                                    number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                    person = XmlMsgs.PERSON_3RD;

                                    if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                                        String restriction = LQM.getNounEntry(IRI.create(XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI)), getLanguage()).getNumber();
                                        if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                                            if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                                                number = XmlMsgs.SINGULAR;
                                            } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                                                number = XmlMsgs.PLURAL;
                                            }
                                        }
                                    }
                                } else if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_TAG)) {
                                    number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                } else {
                                    String name = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                                    if (name.startsWith("anonymous(")) {
                                        name = name.substring(10, name.length() - 1);
                                    }

                                    NLNSlot nameSlot = NLNQM.getNLName(IRI.create(name)).getHeadSlot();
                                    if (nameSlot instanceof NLNNounSlot) {
                                        number = ((NLNNounSlot) nameSlot).getNumber();
                                    } else if (nameSlot instanceof NLNAdjectiveSlot) {
                                        number = ((NLNAdjectiveSlot) nameSlot).getNumber();
                                    }
                                }
                            }
                        }
                        person = XmlMsgs.PERSON_3RD;
                    }

                    boolean polarity = true;
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.polarity).equals("false")) {
                        polarity = false;
                    }
                    boolean useAuxiliaryVerb = true;
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.useAuxiliaryVerb).equals("false")) {
                        useAuxiliaryVerb = false;
                    }
                    String ret = realizeVerb(lexiconEntryIRI, tense, voice, number, person, polarity, useAuxiliaryVerb);

                    annotatedDescription.addVerb(ret.trim(), getForProperty(slot), getInterest(slot), getAssimilation(slot));
                    addText(ret.trim());
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.ADJECTIVE_TAG)) {
                    IRI lexiconEntryIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI));
                    String agreeWith = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                    String number = XmlMsgs.SINGULAR;
                    String caseType = XmlMsgs.NOMINATIVE_TAG;
                    String gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;

                    if (agreeWith.isEmpty()) {
                        number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                        if (Languages.isGreek(getLanguage())) {
                            caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                            gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                        }
                    } else {
                        String recursiveAgreeWith = agreeWith;
                        while (!recursiveAgreeWith.isEmpty()) {
                            for (int k = 0; k < messageSlots.size(); k++) {
                                if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(recursiveAgreeWith)) {
                                    recursiveAgreeWith = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                                    if (!recursiveAgreeWith.isEmpty()) {
                                        agreeWith = recursiveAgreeWith;
                                    }
                                }
                            }
                        }
                        for (int k = 0; k < messageSlots.size(); k++) {
                            if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(agreeWith)) {
                                if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ADJECTIVE_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                                    caseType = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                                    gender = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                } else if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_TAG)) {
                                    number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                } else {
                                    String name = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                                    if (name.startsWith("anonymous(")) {
                                        name = name.substring(10, name.length() - 1);
                                    }

                                    NLNSlot nameSlot = NLNQM.getNLName(IRI.create(name)).getHeadSlot();
                                    if (nameSlot instanceof NLNNounSlot) {
                                        number = ((NLNNounSlot) nameSlot).getNumber();
                                        if (Languages.isGreek(getLanguage())) {
                                            caseType = ((NLNNounSlot) nameSlot).getCase();
                                            gender = ((LexEntryNounGR) LQM.getNounEntry(((NLNNounSlot) nameSlot).getLexiconEntryIRI(), getLanguage())).getGender();
                                        }
                                    } else if (nameSlot instanceof NLNAdjectiveSlot) {
                                        number = ((NLNAdjectiveSlot) nameSlot).getNumber();
                                        if (Languages.isGreek(getLanguage())) {
                                            caseType = ((NLNAdjectiveSlot) nameSlot).getCase();
                                            gender = ((NLNAdjectiveSlot) nameSlot).getGender();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (Languages.isEnglish(getLanguage())) {
                        LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                        addText(adjectiveEntry.get_form());
                    } else if (Languages.isGreek(getLanguage())) {
                        LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                        addText(adjectiveEntry.get(gender, number, caseType));
                    }
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                    IRI lexiconEntryIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI));
                    String agreeWith = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                    String number = XmlMsgs.SINGULAR;
                    String caseType = XmlMsgs.NOMINATIVE_TAG;

                    if (agreeWith.isEmpty()) {
                        number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                        if (Languages.isGreek(getLanguage())) {
                            caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                        }
                    } else {
                        String recursiveAgreeWith = agreeWith;
                        while (!recursiveAgreeWith.isEmpty()) {
                            for (int k = 0; k < messageSlots.size(); k++) {
                                if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(recursiveAgreeWith)) {
                                    recursiveAgreeWith = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
                                    if (!recursiveAgreeWith.isEmpty()) {
                                        agreeWith = recursiveAgreeWith;
                                    }
                                }
                            }
                        }
                        for (int k = 0; k < messageSlots.size(); k++) {
                            if (XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.ID).equals(agreeWith)) {
                                if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                                    number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                    caseType = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.CASE_TAG);

                                    if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NOUN_TAG)) {
                                        String restriction = LQM.getNounEntry(IRI.create(XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI)), getLanguage()).getNumber();
                                        if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                                            if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                                                number = XmlMsgs.SINGULAR;
                                            } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                                                number = XmlMsgs.PLURAL;
                                            }
                                        }
                                    }
                                } else if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_TAG) || XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_TAG)) {
                                    number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                } else {
                                    String name = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                                    if (name.startsWith("anonymous(")) {
                                        name = name.substring(10, name.length() - 1);
                                    }

                                    NLNSlot nameSlot = NLNQM.getNLName(IRI.create(name)).getHeadSlot();
                                    if (nameSlot instanceof NLNNounSlot) {
                                        number = ((NLNNounSlot) nameSlot).getNumber();
                                        if (Languages.isGreek(getLanguage())) {
                                            caseType = ((NLNNounSlot) nameSlot).getCase();
                                        }
                                    } else if (nameSlot instanceof NLNAdjectiveSlot) {
                                        number = ((NLNAdjectiveSlot) nameSlot).getNumber();
                                        if (Languages.isGreek(getLanguage())) {
                                            caseType = ((NLNAdjectiveSlot) nameSlot).getCase();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    String restriction = LQM.getNounEntry(lexiconEntryIRI, getLanguage()).getNumber();
                    if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                        if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                            number = XmlMsgs.SINGULAR;
                        } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                            number = XmlMsgs.PLURAL;
                        }
                    }

                    if (Languages.isEnglish(getLanguage())) {
                        LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                        addText(nounEntry.get("", number));
                    } else if (Languages.isGreek(getLanguage())) {
                        LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                        addText(nounEntry.get(caseType, number));
                    }
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.TEXT_TAG) || (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.PREPOSITION_TAG))) {
                    if (XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).startsWith(NLResourceManager.nlowlNS) && !DefaultResourcesManager.isDefaultResource(IRI.create(XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))) && XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.polarity).equals("false")) {
                        if (Languages.isEnglish(getLanguage())) {
                            addText("not");
                        } else {
                            addText("δεν");
                        }
                    }
                    if (slot.getTextContent().compareTo("σε") != 0) {
                        String ret = slot.getTextContent();
                        annotatedDescription.addText(ret, getForProperty(slot), getRERole(slot), getRef(slot), getInterest(slot), getAssimilation(slot), getPrep(slot));
                        addText(ret);
                    }
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {//an to slot einai IS_A
                    if (Languages.isEnglish(getLanguage())) {
                        String ret = "is";
                        annotatedDescription.addVerb(ret, getForProperty(slot), getInterest(slot), getAssimilation(slot));
                        addText(ret);
                    } else if (Languages.isGreek(getLanguage())) {
                        String ret = "είναι";
                        annotatedDescription.addVerb(ret, getForProperty(slot), getInterest(slot), getAssimilation(slot));
                        addText(ret);
                    }
                } else if (XmlMsgs.compare(slot, "", "COMMA")) {
                    REALIZED_TEXT.deleteCharAt(REALIZED_TEXT.length() - 1);
                    REALIZED_TEXT.append(", ");
                    annotatedDescription.addText(",");
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.OWNER_TAG)) {//OWNER_TAG 
                    IRI ownerIRI = IRI.create(XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.REF));

                    String caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                    String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                    String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                    String refType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RETYPE);

                    String nameStr = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                    if (nameStr.startsWith("anonymous(")) {
                        setProducedRE(slot, SurfaceRealization.PROD_RE_NP);

                        String ret = generateRefExpressionForAnonEntity(slot, IRI.create(nameStr.substring(10, nameStr.length() - 1)), gender, number, caseType);
                        annotatedDescription.addRE(ret, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                        addText(ret);
                    } else {
                        IRI NLNameIRI = IRI.create(nameStr);
                        annotatedDescription.setStringCompEntities(ownerIRI.toString());
                        NLName name = NLNQM.getNLName(NLNameIRI);

                        IRI superNLName = null;
                        if (name.isGenerated()) {
                            superNLName = getSuperNLName(ownerIRI, user);

                            if (superNLName != null) {
                                NLNSlot superHeadSlot = NLNQM.getNLName(superNLName).getHeadSlot();

                                if (superHeadSlot instanceof NLNAdjectiveSlot) {
                                    refType = XmlMsgs.REF_AUTO;
                                    //caseType = ((NLNAdjectiveSlot) superHeadSlot).getCase();
                                    gender = ((NLNAdjectiveSlot) superHeadSlot).getGender();
                                    number = ((NLNAdjectiveSlot) superHeadSlot).getNumber();
                                } else if (superHeadSlot instanceof NLNNounSlot) {
                                    refType = XmlMsgs.REF_AUTO;
                                    //caseType = ((NLNNounSlot) superHeadSlot).getCase();
                                    gender = LQM.getNounEntry(((NLNNounSlot) superHeadSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                    number = ((NLNNounSlot) superHeadSlot).getNumber();
                                }
                            }
                        }

                        if (name.isGenerated() && annotateGeneratedResources) {
                            addText("<font color=\"#00FF00\"><b>");
                        }
                        if (name.isGenerated() && (superNLName == null)) {
                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                            String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);
                            annotatedDescription.addRE(realizedName, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                            addText(realizedName);
                        } else if (!generateReferringExpressions) {
                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                            String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);
                            annotatedDescription.addRE(realizedName, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                            addText(realizedName);
                        } else if (!refType.equals(XmlMsgs.REF_AUTO)) {
                            if (getLanguage().equals(Languages.ENGLISH)) {
                                String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);

                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                annotatedDescription.addRE(ret, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            } else if (getLanguage().equals(Languages.GREEK)) {
                                String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);

                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                annotatedDescription.addRE(ret, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        } else {
                            String ret = autoGenerateRefExpression(name, gender, number, slot);
                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                            annotatedDescription.addRE(ret, ownerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                            addText(ret);
                        }
                        if (name.isGenerated() && annotateGeneratedResources) {
                            addText("</b></font>");
                        }
                    }
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {//filler // edw to allaxa
                    IRI fillerIRI = IRI.create(slot.getTextContent());
                    IRI NLNameIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG));

                    String caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                    int articleUse = Integer.parseInt(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.articleUse));

                    ArrayList<IRI> appendAdjectives = new ArrayList<IRI>();
                    if (firstFiller) {
                        firstFiller = false;
                        if (!XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.countAppends).isEmpty()) {
                            int counter = Integer.parseInt(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.countAppends));
                            for (int c = 1; c < counter; c++) {
                                appendAdjectives.add(IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.appendAdjective + c)));
                                if ((XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.isConnective + c).equals("false")) && (c + 1 < counter)) {
                                    appendAdjectives.add(IRI.create(XmlMsgs.DISJUNCTIVE));
                                }
                            }
                        }
                    }

                    boolean useBullets = false;
                    if (j > 1) {
                        //If there is a bullet filler before me
                        if (XmlMsgs.compare(messageSlots.get(j - 2), XmlMsgs.prefix, XmlMsgs.FILLER_TAG)
                                && XmlMsgs.getAttribute(messageSlots.get(j - 2), XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")
                                && XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")) {
                            //Begin a new bullet
                            addText("</li><li>");
                            useBullets = true;
                        }
                    }
                    if (j < messageSlots.size() - 2 && !useBullets) {
                        //If there is a bullet filler after me and none before me
                        if (XmlMsgs.compare(messageSlots.get(j + 2), XmlMsgs.prefix, XmlMsgs.FILLER_TAG)
                                && XmlMsgs.getAttribute(messageSlots.get(j + 2), XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")
                                && XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")) {
                            //Begin a new list and a new bullet
                            addText("<ul><li>");
                            useBullets = true;
                        }
                    }

                    if ((NLNameIRI != null) && !(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG).trim().isEmpty())) {//object property   
                        if (Languages.isEnglish(getLanguage())) {//English 
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                String realizedName;
                                if (appendAdjectives.isEmpty()) {
                                    realizedName = realizeNLName(name, caseType, number, gender, articleUse);
                                } else {
                                    realizedName = realizeNLName(name, caseType, number, gender, articleUse, appendAdjectives);
                                }

                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("<font color=\"#00FF00\"><b>");
                                }
                                if (realizedName.isEmpty()) {
                                    setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else {
                                    if (slot.getPreviousSibling() != null
                                            && XmlMsgs.compare(slot.getPreviousSibling(), XmlMsgs.prefix, XmlMsgs.TEXT_TAG)
                                            && slot.getPreviousSibling().getTextContent().compareTo(", a kind of") == 0) {
                                        addText(realizedName);
                                        setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    } else {
                                        if (!isREAuto(slot)) {
                                            String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else {
                                            Node pre_Slot2 = slot.getPreviousSibling();

                                            if (pre_Slot2 != null && XmlMsgs.compare(pre_Slot2, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {
                                                String ret = realizedName;

                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else {
                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(realizedName);
                                            }
                                        }
                                    }
                                }
                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("</b></font>");
                                }
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                annotatedDescription.addCannedText(ret, fillerIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//English
                        else if (Languages.isGreek(getLanguage())) {//Greek
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                if (gender.isEmpty() || number.isEmpty()) {
                                    for (int k = 0; k < messageSlots.size(); k++) {
                                        if (XmlMsgs.compare(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.OWNER_TAG)) {
                                            if (gender.isEmpty()) {
                                                gender = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                            }
                                            if (number.isEmpty()) {
                                                number = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                                            }

                                            if (gender.isEmpty() || number.isEmpty()) {
                                                IRI ownerIRI = IRI.create(XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.REF));
                                                String nameStr = XmlMsgs.getAttribute(messageSlots.get(k), XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);

                                                if (!nameStr.startsWith("anonymous(")) {
                                                    IRI ownerNLNameIRI = IRI.create(nameStr);
                                                    NLName ownerName = NLNQM.getNLName(ownerNLNameIRI);

                                                    IRI superNLName = null;
                                                    if (ownerName.isGenerated()) {
                                                        superNLName = getSuperNLName(ownerIRI, user);

                                                        if (superNLName != null) {
                                                            NLNSlot superHeadSlot = NLNQM.getNLName(superNLName).getHeadSlot();

                                                            if (superHeadSlot instanceof NLNAdjectiveSlot) {
                                                                if (gender.isEmpty()) {
                                                                    gender = ((NLNAdjectiveSlot) superHeadSlot).getGender();
                                                                }
                                                                if (number.isEmpty()) {
                                                                    number = ((NLNAdjectiveSlot) superHeadSlot).getNumber();
                                                                }
                                                            } else if (superHeadSlot instanceof NLNNounSlot) {
                                                                if (gender.isEmpty()) {
                                                                    gender = LQM.getNounEntry(((NLNNounSlot) superHeadSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                                                }
                                                                if (number.isEmpty()) {
                                                                    number = ((NLNNounSlot) superHeadSlot).getNumber();
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        NLNSlot superHeadSlot = NLNQM.getNLName(ownerNLNameIRI).getHeadSlot();

                                                        if (superHeadSlot instanceof NLNAdjectiveSlot) {
                                                            if (gender.isEmpty()) {
                                                                gender = ((NLNAdjectiveSlot) superHeadSlot).getGender();
                                                            }
                                                            if (number.isEmpty()) {
                                                                number = ((NLNAdjectiveSlot) superHeadSlot).getNumber();
                                                            }
                                                        } else if (superHeadSlot instanceof NLNNounSlot) {
                                                            if (gender.isEmpty()) {
                                                                gender = LQM.getNounEntry(((NLNNounSlot) superHeadSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                                            }
                                                            if (number.isEmpty()) {
                                                                number = ((NLNNounSlot) superHeadSlot).getNumber();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                String realizedName;
                                if (appendAdjectives.isEmpty()) {
                                    realizedName = realizeNLName(name, caseType, number, gender, articleUse);
                                } else {
                                    realizedName = realizeNLName(name, caseType, number, gender, articleUse, appendAdjectives);
                                }

                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("<font color=\"#00FF00\"><b>");
                                }
                                if (realizedName.compareTo("") == 0) {
                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else if (!generateReferringExpressions) {
                                    String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                    addText(ret);
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                } else {
                                    Node pre_Slot = slot.getPreviousSibling();

                                    if (pre_Slot != null) {
                                        if (XmlMsgs.compare(pre_Slot, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {// if previous slot is an IS A
                                            if (gender.compareTo(LexEntry.GENDER_MASCULINE) == 0) {
                                                String ret = "ένας" + " " + realizedName;
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_NEUTER) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένα" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_MASCULINE_OR_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένας/μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            }
                                        }// if previous slot is an IS A
                                        else {// if previous slot is not an IS A
                                            if (!isREAuto(slot)) {
                                                String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                                addText(ret);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            } else {
                                                if (slot.getPreviousSibling().getTextContent().compareTo("σε") == 0) {
                                                    String ret = "σε" + " " + realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                } else {
                                                    String ret = realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                }
                                            }
                                        }// if previous slot is not an IS A
                                    } else {
                                        String ret = realizedName;
                                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                        addText(ret);
                                    }
                                }//
                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("</b></font>");
                                }
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                annotatedDescription.addCannedText(ret, fillerIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//Greek  
                    } else {// datatype property 
                        if (slot.getTextContent().indexOf("^^http://") > 0) {
                            String text = (slot.getTextContent()).substring(0, slot.getTextContent().indexOf("^^http://"));

                            String ret = text;
                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));

                            addText(ret);
                        } else {
                            String text = slot.getTextContent();

                            String ret = text;
                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));

                            addText(ret);
                        }
                    }
                    if (useBullets) {
                        if (j == messageSlots.size() - 1) {
                            //If we are at the end of the sentence
                            //Finish the bullet and the list
                            addText("</li></ul>");
                        } else if (j < messageSlots.size() - 2) {
                            //If there is NOT bullet filler after me
                            if (!XmlMsgs.compare(messageSlots.get(j + 2), XmlMsgs.prefix, XmlMsgs.FILLER_TAG)
                                    || !XmlMsgs.getAttribute(messageSlots.get(j + 2), XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")
                                    || !XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")) {
                                //Finish the bullet and the list
                                addText("</li></ul>");
                            }
                        }
                    }
                } else if ((XmlMsgs.compare(slot, XmlMsgs.prefix, Aggregation.AGGREGATE_NLNAME))) {
                    IRI fillerIRI = IRI.create(slot.getTextContent());

                    ArrayList<NLName> NLNames = new ArrayList<NLName>();

                    boolean useBullets = true;
                    for (Node filler : XmlMsgs.returnChildNodes(slot)) {
                        if (XmlMsgs.compare(filler, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                            IRI NLNameIRI = IRI.create(XmlMsgs.getAttribute(filler, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG));
                            NLName name = NLNQM.getNLName(NLNameIRI);

                            if (!XmlMsgs.getAttribute(filler, XmlMsgs.prefix, XmlMsgs.useBullets).equals("true")) {
                                useBullets = false;
                            }
                            if (name != null) {
                                NLNames.add(name);
                            }
                        }
                    }
                    boolean isConnective = true;
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) {
                        isConnective = false;
                    }

                    String caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);

                    if (Languages.isEnglish(getLanguage())) {//English 
                        if (!NLNames.isEmpty()) {
                            String gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                            String number = XmlMsgs.SINGULAR;

                            if (!XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG).isEmpty()) {
                                number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);
                            }

                            if (NLNames.get(0).getHeadSlot() != null) {
                                NLNSlot head = NLNames.get(0).getHeadSlot();

                                if (head instanceof NLNNounSlot) {
                                    gender = LQM.getNounEntry(((NLNNounSlot) head).getLexiconEntryIRI(), getLanguage()).getGender();
                                    if (number.isEmpty()) {
                                        number = ((NLNNounSlot) head).getNumber();
                                    }
                                } else if (head instanceof NLNAdjectiveSlot) {
                                    gender = ((NLNAdjectiveSlot) head).getGender();
                                    if (number.isEmpty()) {
                                        number = ((NLNAdjectiveSlot) head).getNumber();
                                    }
                                }
                            }

                            String realizedName = aggregateNLNameAdjectives(NLNames, caseType, number, isConnective, useBullets);

                            if (realizedName.isEmpty()) {
                                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                String ret = "NOT FOUND FILLER";
                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            } else {
                                if (slot.getPreviousSibling() != null
                                        && XmlMsgs.compare(slot.getPreviousSibling(), XmlMsgs.prefix, XmlMsgs.TEXT_TAG)
                                        && slot.getPreviousSibling().getTextContent().compareTo(", a kind of") == 0) {
                                    addText(realizedName);
                                    setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                    annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                } else {
                                    if (!isREAuto(slot)) {
                                        String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                        annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                        addText(ret);
                                    } else {
                                        Node pre_Slot2 = slot.getPreviousSibling();

                                        if (pre_Slot2 != null && XmlMsgs.compare(pre_Slot2, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {
                                            String ret = realizedName;

                                            setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else {
                                            setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(realizedName);
                                        }
                                    }
                                }
                            }
                        }
                    }//English
                    else if (Languages.isGreek(getLanguage())) {//Greek
                        if (!NLNames.isEmpty()) {
                            String gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                            String number = XmlMsgs.SINGULAR;

                            if (NLNames.get(0).getHeadSlot() != null) {
                                NLNSlot head = NLNames.get(0).getHeadSlot();

                                if (head instanceof NLNNounSlot) {
                                    gender = LQM.getNounEntry(((NLNNounSlot) head).getLexiconEntryIRI(), getLanguage()).getGender();
                                    number = ((NLNNounSlot) head).getNumber();
                                } else if (head instanceof NLNAdjectiveSlot) {
                                    gender = ((NLNAdjectiveSlot) head).getGender();
                                    number = ((NLNAdjectiveSlot) head).getNumber();
                                }
                            }

                            String realizedName = aggregateNLNameAdjectives(NLNames, caseType, number, isConnective, useBullets);

                            if (realizedName.compareTo("") == 0) {
                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                String ret = "NOT FOUND FILLER";
                                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            } else {
                                Node pre_Slot = slot.getPreviousSibling();

                                if (pre_Slot != null) {
                                    if (XmlMsgs.compare(pre_Slot, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {// if previous slot is an IS A
                                        if (gender.compareTo(LexEntry.GENDER_MASCULINE) == 0) {
                                            String ret = "ένας" + " " + realizedName;
                                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else if (gender.compareTo(LexEntry.GENDER_FEMININE) == 0) {
                                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            String ret = "μία" + " " + realizedName;
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else if (gender.compareTo(LexEntry.GENDER_NEUTER) == 0) {
                                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            String ret = "ένα" + " " + realizedName;
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else if (gender.compareTo(LexEntry.GENDER_MASCULINE_OR_FEMININE) == 0) {
                                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                            String ret = "ένας/μία" + " " + realizedName;
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        }
                                    }// if previous slot is an IS A
                                    else {// if previous slot is not an IS A
                                        if (!isREAuto(slot)) {
                                            String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                            addText(ret);
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                        } else {
                                            if (slot.getPreviousSibling().getTextContent().compareTo("σε") == 0) {
                                                String ret = "σε" + " " + realizedName;
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else {
                                                String ret = realizedName;
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            }
                                        }
                                    }// if previous slot is not an IS A
                                } else {
                                    String ret = realizedName;
                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                }
                            }//
                        } else {
                            String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                            setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                            annotatedDescription.addCannedText(ret, fillerIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                            addText(ret);
                        }
                    }//Greek      
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.CONCAT_TAG)) {//filler // edw to allaxa
                    IRI fillerIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.concatIndividual));
                    IRI NLNameIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG));

                    String caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                    int articleUse = Integer.parseInt(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.articleUse));

                    if (XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.prpType).equals(XmlMsgs.ObjectProperty)) {//object property   
                        if (Languages.isEnglish(getLanguage())) {//English 
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                String realizedName = realizeNLName(name, caseType, number, gender, articleUse);

                                if (realizedName.isEmpty()) {
                                    setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else {
                                    if (slot.getPreviousSibling() != null
                                            && XmlMsgs.compare(slot.getPreviousSibling(), XmlMsgs.prefix, XmlMsgs.TEXT_TAG)
                                            && slot.getPreviousSibling().getTextContent().compareTo(", a kind of") == 0) {
                                        addText(realizedName);
                                        setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    } else {
                                        if (!isREAuto(slot)) {
                                            String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else {
                                            Node pre_Slot2 = slot.getPreviousSibling();

                                            if (pre_Slot2 != null && XmlMsgs.compare(pre_Slot2, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {
                                                String ret = realizedName;

                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else {
                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(realizedName, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(realizedName);
                                            }
                                        }
                                    }
                                }
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                annotatedDescription.addCannedText(ret, fillerIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//English
                        else if (Languages.isGreek(getLanguage())) {//Greek
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                String realizedName = realizeNLName(name, caseType, number, gender, articleUse);

                                if (realizedName.compareTo("") == 0) {
                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else {
                                    Node pre_Slot = slot.getPreviousSibling();

                                    if (pre_Slot != null) {
                                        if (XmlMsgs.compare(pre_Slot, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {// if previous slot is an IS A
                                            if (gender.compareTo(LexEntry.GENDER_MASCULINE) == 0) {
                                                String ret = "ένας" + " " + realizedName;
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_NEUTER) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένα" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_MASCULINE_OR_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένας/μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            }
                                        }// if previous slot is an IS A
                                        else {// if previous slot is not an IS A
                                            if (!isREAuto(slot)) {
                                                String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                                addText(ret);
                                                annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            } else {
                                                if (slot.getPreviousSibling().getTextContent().compareTo("σε") == 0) {
                                                    String ret = "σε" + " " + realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                } else {
                                                    String ret = realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                }
                                            }
                                        }// if previous slot is not an IS A
                                    } else {
                                        String ret = realizedName;
                                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                        addText(ret);
                                    }
                                }//
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                annotatedDescription.addCannedText(ret, fillerIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//Greek                                 
                    } else {// datatype property 
                        if (slot.getTextContent().indexOf("^^http://") > 0) {
                            String text = (slot.getTextContent()).substring(0, slot.getTextContent().indexOf("^^http://"));

                            String ret = text;
                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));

                            addText(ret);
                        } else {
                            String text = slot.getTextContent();

                            String ret = text;
                            annotatedDescription.addRE(ret, fillerIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));

                            addText(ret);
                        }
                    }
                } else if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.COMPARATOR_TAG) || XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_TAG)) {
                    IRI comparatorIRI = IRI.create(slot.getTextContent());
                    IRI NLNameIRI = IRI.create(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG));

                    String caseType = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
                    int articleUse = Integer.parseInt(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.articleUse));

                    if ((NLNameIRI != null) && !(XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG).trim().isEmpty())) {//object property   
                        if (Languages.isEnglish(getLanguage())) {//English 
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                String realizedName = realizeNLName(name, caseType, number, gender, articleUse);

                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("<font color=\"#00FF00\"><b>");
                                }
                                if (realizedName.isEmpty()) {
                                    setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else {
                                    if (slot.getPreviousSibling() != null
                                            && XmlMsgs.compare(slot.getPreviousSibling(), XmlMsgs.prefix, XmlMsgs.TEXT_TAG)
                                            && slot.getPreviousSibling().getTextContent().compareTo(", a kind of") == 0) {
                                        addText(realizedName);
                                        setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(realizedName, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    } else {
                                        if (!isREAuto(slot)) {
                                            String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                            annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            addText(ret);
                                        } else {
                                            Node pre_Slot2 = slot.getPreviousSibling();

                                            if (pre_Slot2 != null && XmlMsgs.compare(pre_Slot2, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {
                                                String ret = realizedName;

                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else {
                                                setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(realizedName, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(realizedName);
                                            }
                                        }
                                    }
                                }
                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("</b></font>");
                                }
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                annotatedDescription.addCannedText(ret, comparatorIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//English
                        else if (Languages.isGreek(getLanguage())) {//Greek
                            NLName name = NLNQM.getNLName(NLNameIRI);
                            if (name != null) {
                                String gender = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.GENDER_TAG);
                                String number = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG);

                                String realizedName = realizeNLName(name, caseType, number, gender, articleUse);

                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("<font color=\"#00FF00\"><b>");
                                }
                                if (realizedName.compareTo("") == 0) {
                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                    String ret = "NOT FOUND FILLER";
                                    annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                    addText(ret);
                                } else if (!generateReferringExpressions) {
                                    String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                    addText(ret);
                                    annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                } else {
                                    Node pre_Slot = slot.getPreviousSibling();

                                    if (pre_Slot != null) {
                                        if (XmlMsgs.compare(pre_Slot, XmlMsgs.prefix, XmlMsgs.IS_A_TAG)) {// if previous slot is an IS A
                                            if (gender.compareTo(LexEntry.GENDER_MASCULINE) == 0) {
                                                String ret = "ένας" + " " + realizedName;
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_NEUTER) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένα" + " " + realizedName;
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            } else if (gender.compareTo(LexEntry.GENDER_MASCULINE_OR_FEMININE) == 0) {
                                                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                String ret = "ένας/μία" + " " + realizedName;
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                addText(ret);
                                            }
                                        }// if previous slot is an IS A
                                        else {// if previous slot is not an IS A

                                            if (!isREAuto(slot)) {
                                                String ret = generateRefExpressionFromUserChoice(slot, gender, number, caseType, realizedName);
                                                addText(ret);
                                                annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                            } else {
                                                if (slot.getPreviousSibling().getTextContent().compareTo("σε") == 0) {
                                                    String ret = "σε" + " " + realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                } else {
                                                    String ret = realizedName;
                                                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                                    annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                                    addText(ret);
                                                }
                                            }
                                        }// if previous slot is not an IS A
                                    } else {
                                        String ret = realizedName;
                                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                                        annotatedDescription.addRE(ret, comparatorIRI.toString(), getForProperty(slot), getProducedRE(slot), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                        addText(ret);
                                    }
                                }//
                                if (name.isGenerated() && annotateGeneratedResources) {
                                    addText("</b></font>");
                                }
                            } else {
                                String ret = "[NOUN OR CANNED TEXT NOT FOUND]";
                                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                                annotatedDescription.addCannedText(ret, comparatorIRI.toString(), slot.getParentNode().getNamespaceURI() + slot.getParentNode().getLocalName(), getRERole(slot), getInterest(slot), getAssimilation(slot));
                                addText(ret);
                            }
                        }//Greek  
                    }
                }

                //logger.debug(Slot.getTextContent() + "@");

                // if this is the last slot 
                if (j == messageSlots.size() - 1) {
                    //if not ends with '.' add a '.'
                    if (REALIZED_TEXT.charAt(REALIZED_TEXT.length() - 2) != '.') {
                        //logger.debug(Slot.getTextContent() + "@3");
                        REALIZED_TEXT.deleteCharAt(REALIZED_TEXT.length() - 1);

                        // it is the last lost so a new Period must begin
                        // by adding a '.'
                        if (!slot.getParentNode().getNodeName().equals("nlowl:Comparator")) {
                            annotatedDescription.addText(".");
                            REALIZED_TEXT.append(". ");
                        }
                    } else// 
                    {
                        // it is the last slot so a new Period must begin
                        annotatedDescription.addStartPeriod();
                        annotatedDescription.addStartSentence();
                    }

                }//if
            }//for
        }//try
        catch (DOMException e) {
        } catch (NumberFormatException e) {
        }
    }

    // adds a space to the text
    public void addSpace() {
        realizedText.append(" ");
    }

    // appends new text
    public void addText(String text) {
        if (text.compareTo("") != 0) {
            realizedText.append(capitalize(text));
            addSpace();
        }
    }

    // capitalize the first letter of a sentence
    private String capitalize(String text) {
        if (SurfaceRealization.first) {
            SurfaceRealization.first = false;
            char ch = Character.toUpperCase(text.charAt(0));
            return ch + "" + text.substring(1);
        }
        return text;
    }

    public static String capitalizeText(String text) {
        if (text.length() > 0) {
            char ch = Character.toUpperCase(text.charAt(0));
            return ch + "" + text.substring(1);
        }
        return text;
    }

    private IRI getSuperNLName(IRI indivOrClassIRI, NLGUser user) {
        if (!generatedSuperNames.containsKey(indivOrClassIRI)) {
            Set<OWLClassAssertionAxiom> superClassAssertions;

            HashSet<IRI> superClasses = new HashSet<IRI>();

            Set<OWLEntity> entities = new HashSet<OWLEntity>();
            for (OWLOntology ontology : mainModels) {
                entities.addAll(ontology.getEntitiesInSignature(indivOrClassIRI, true));
            }
            for (OWLEntity entity : entities) {
                superClassAssertions = new HashSet<OWLClassAssertionAxiom>();
                if (entity.isOWLClass()) {
                    for (OWLOntology ontology : mainModels) {
                        superClassAssertions.addAll(ontology.getClassAssertionAxioms(entity.asOWLClass()));
                    }
                } else if (entity.isOWLNamedIndividual()) {
                    for (OWLOntology ontology : mainModels) {
                        superClassAssertions.addAll(ontology.getClassAssertionAxioms(entity.asOWLNamedIndividual()));
                    }
                }
                if (!superClassAssertions.isEmpty()) {
                    for (OWLClassAssertionAxiom superClass : superClassAssertions) {
                        superClasses.addAll(NLGEngine.getSuperClasses(indivOrClassIRI, superClass.getClassExpression()));
                    }
                }
                superClasses.remove(indivOrClassIRI);
                for (IRI superClass : superClasses) {
                    IRI superName = MQM.chooseNLName(superClass, NLNQM, getLanguage(), user);

                    if ((superName != null) && (!superName.equals(NLResourceManager.anonymous.getIRI())) && (!NLNQM.getNLName(superName).isGenerated())) {
                        generatedSuperNames.put(indivOrClassIRI, superName);
                        return superName;
                    }
                }
                for (IRI superClass : superClasses) {
                    IRI superName = getSuperNLName(superClass, user);
                    if (superName != null) {
                        generatedSuperNames.put(indivOrClassIRI, superName);
                        return superName;
                    }
                }
            }
        } else {
            return generatedSuperNames.get(indivOrClassIRI);
        }
        return null;
    }

    // generate a referring expression
    // for the owner_URI
    private String autoGenerateRefExpression(NLName name, String gender, String number, Node slot) {
        String ownerCase = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.CASE_TAG);

        return generateRefExpressionForEntity(name, ownerCase, gender, number, slot);
    }

    public String realizeVerb(IRI lexiconEntryIRI, String tense, String voice, String number, String person, boolean polarity, boolean useAuxiliaryVerb) {
        LexEntryVerb verb = LQM.getVerbEntry(lexiconEntryIRI, getLanguage());

        String ret = "";
        if (lexiconEntryIRI.equals(DefaultResourcesManager.toBeVLE_IRI)) {
            if (Languages.isEnglish(getLanguage())) {
                if (voice.equals(XmlMsgs.ACTIVE_VOICE)) {
                    if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "am";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "are";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "is";
                            }
                        } else if (number.equals(XmlMsgs.PLURAL)) {
                            ret += "are";
                        }
                        if (!polarity) {
                            ret += " not";
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "am";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "are";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "is";
                            }
                        } else if (number.equals(XmlMsgs.PLURAL)) {
                            ret += "are";
                        }
                        if (!polarity) {
                            ret += " not";
                        }
                        ret += " being";
                    } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                        if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
                            ret += "has";
                        } else {
                            ret += "have";
                        }
                        if (!polarity) {
                            ret += " not";
                        }
                        ret += " been";
                    } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                        if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
                            ret += "was";
                        } else {
                            ret += "were";
                        }
                        if (!polarity) {
                            ret += " not";
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
                        if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
                            ret += "was";
                        } else {
                            ret += "were";
                        }
                        if (!polarity) {
                            ret += " not";
                        }
                        ret += " being";
                    } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
                        if (!polarity) {
                            ret += "had not been";
                        } else {
                            ret += "had been";
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                        ret += "will";
                        if (!polarity) {
                            ret += " not";
                        }
                        ret += " be";
                    } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                        if (!polarity) {
                            ret += "will not be";
                        } else {
                            ret += "will be";
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                        if (!polarity) {
                            ret += "will not have been";
                        } else {
                            ret += "will have been";
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                        if (!polarity) {
                            ret += "will not have been";
                        } else {
                            ret += "will have been";
                        }
                    }
                }
            } else if (Languages.isGreek(getLanguage())) {
                if (!polarity) {
                    ret += "δεν ";
                }
                if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                    ret += "θα";
                }
                ret += ((LexEntryVerbGR) verb).get(voice, tense, person, number);
            }
        } else {
            if (Languages.isEnglish(getLanguage())) {
                if (useAuxiliaryVerb) {
                    if (voice.equals(XmlMsgs.ACTIVE_VOICE)) {
                        if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (!polarity) {
                                    if (person.equals(XmlMsgs.PERSON_3RD)) {
                                        ret += "does not";
                                        person = XmlMsgs.PERSON_2ND;
                                    } else {
                                        ret += "do not";
                                    }
                                } else if (number.equals(XmlMsgs.PLURAL)) {
                                    ret += "do not";
                                }
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (person.equals(XmlMsgs.PERSON_1ST)) {
                                    ret += "am";
                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                    ret += "are";
                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                    ret += "is";
                                }
                            } else if (number.equals(XmlMsgs.PLURAL)) {
                                ret += "are";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                            if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
                                ret += "has";
                            } else {
                                ret += "have";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                            if (!polarity) {
                                ret += "did not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
                            if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
                                ret += "was";
                            } else {
                                ret += "were";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "had not been";
                            } else {
                                ret += "had been";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                            ret += "will";
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "will not be";
                            } else {
                                ret += "will be";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                            if (!polarity) {
                                ret += "will not have";
                            } else {
                                ret += "will have";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "will not have been";
                            } else {
                                ret += "will have been";
                            }
                        }
                    } else if (voice.equals(XmlMsgs.PASSIVE_VOICE)) {
                        if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (person.equals(XmlMsgs.PERSON_1ST)) {
                                    ret += "am";
                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                    ret += "are";
                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                    ret += "is";
                                }
                            } else if (number.equals(XmlMsgs.PLURAL)) {
                                ret += "are";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (person.equals(XmlMsgs.PERSON_1ST)) {
                                    ret += "am";
                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                    ret += "are";
                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                    ret += "is";
                                }
                            } else if (number.equals(XmlMsgs.PLURAL)) {
                                ret += "are";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                            ret += " being";
                        } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                            if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
                                ret += "has";
                            } else {
                                ret += "have";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                            ret += " been";
                        } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                            if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
                                ret += "was";
                            } else {
                                ret += "were";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                        } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
                            if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
                                ret += "was";
                            } else {
                                ret += "were";
                            }
                            if (!polarity) {
                                ret += " not";
                            }
                            ret += " being";
                        } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "has not been";
                            } else {
                                ret += "has been";
                            }
                            ret += " being";
                        } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                            ret += "will";
                            if (!polarity) {
                                ret += " not";
                            }
                            ret += " be";
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "will not be";
                            } else {
                                ret += "will be";
                            }
                            ret += " being";
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                            if (!polarity) {
                                ret += "will not have";
                            } else {
                                ret += "will have";
                            }
                            ret += " been";
                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                            if (!polarity) {
                                ret += "will not have been";
                            } else {
                                ret += "will have been";
                            }
                            ret += " being";
                        }
                    }
                } else {
                    if (!polarity) {
                        ret += "not";
                    }
                }

                if (!polarity && tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                    ret += " " + ((LexEntryVerbEN) verb).get(voice, XmlMsgs.TENSE_SIMPLE_PRESENT, person, number);
                } else {
                    ret += " " + ((LexEntryVerbEN) verb).get(voice, tense, person, number);
                }

                if (!polarity && !ret.contains("not")) {
                    ret += " not";
                }
            } else if (Languages.isGreek(getLanguage())) {
                if (!polarity) {
                    ret += "δεν";
                }

                if (useAuxiliaryVerb) {
                    if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "έχω";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "έχεις";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "έχει";
                            }
                        } else if (number.equals(XmlMsgs.PLURAL)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "έχουμε";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "έχετε ";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "έχουν";
                            }
                        }

                    } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT)) {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "είχα";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "είχες";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "είχε";
                            }
                        } else if (number.equals(XmlMsgs.PLURAL)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "είχαμε";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "είχατε";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "είχαν";
                            }
                        }
                    } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                        ret += "θα";
                    } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                        ret += "θα";
                    } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                        ret += "θα ";
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "έχω";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "έχεις";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "έχει";
                            }
                        } else if (number.equals(XmlMsgs.PLURAL)) {
                            if (person.equals(XmlMsgs.PERSON_1ST)) {
                                ret += "έχουμε";
                            } else if (person.equals(XmlMsgs.PERSON_2ND)) {
                                ret += "έχετε";
                            } else if (person.equals(XmlMsgs.PERSON_3RD)) {
                                ret += "έχουν";
                            }
                        }
                    }
                }

                ret += " " + ((LexEntryVerbGR) verb).get(voice, tense, person, number);
            }
        }
        return ret.trim();
    }

    public String realizeNLName(NLName name, String SPCaseType, String SPNumber, String SPGender, int articleUse) {
        ArrayList<NLNSlot> slots = name.getSlotsList();
        Collections.sort(slots);

        ArrayList<String> realizedSlots = new ArrayList<String>();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNAdjectiveSlot) {
                NLNAdjectiveSlot adjective = (NLNAdjectiveSlot) slots.get(i);
                IRI lexiconEntryIRI = adjective.getLexiconEntryIRI();

                String number = "";
                String caseType = "";
                String gender = "";

                if (Languages.isGreek(getLanguage())) {
                    if (adjective.isHead()) {
                        if (SPNumber.isEmpty()) {
                            number = adjective.getNumber();
                        } else {
                            number = SPNumber;
                        }
                        if (SPCaseType.isEmpty()) {
                            caseType = adjective.getCase();
                        } else {
                            caseType = SPCaseType;
                        }
                        if (SPGender.isEmpty()) {
                            gender = adjective.getGender();
                        } else {
                            gender = SPGender;
                        }
                    } else if (adjective.getAgreesWithID() != null) {
                        NLNSlot agreeSlot = adjective;

                        boolean foundAgree = false;
                        NodeID agreeID = adjective.getAgreesWithID();

                        while (!foundAgree) {
                            for (int j = 0; j < slots.size(); j++) {
                                if (slots.get(j).getId().equals(agreeID)) {
                                    agreeSlot = slots.get(j);

                                    if (agreeSlot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNNounSlot) agreeSlot).getAgreesWithID();
                                        } else {
                                            foundAgree = true;
                                        }
                                    } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                                        if (((NLNAdjectiveSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNAdjectiveSlot) agreeSlot).getAgreesWithID();
                                        } else {
                                            foundAgree = true;
                                        }
                                    } else if (agreeSlot instanceof NLNArticleSlot) {
                                        if (((NLNArticleSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNArticleSlot) agreeSlot).getAgreesWithID();
                                        } else {
                                            foundAgree = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (foundAgree && agreeSlot != null) {
                            if (agreeSlot instanceof NLNNounSlot) {
                                if (!SPNumber.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    number = SPNumber;

                                    String restriction = LQM.getNounEntry(((NLNNounSlot) agreeSlot).getLexiconEntryIRI(), getLanguage()).getNumber();
                                    if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                                        if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                                            number = XmlMsgs.SINGULAR;
                                        } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                                            number = XmlMsgs.PLURAL;
                                        }
                                    }
                                } else {
                                    number = ((NLNNounSlot) agreeSlot).getNumber();
                                }
                                if (!SPCaseType.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    caseType = SPCaseType;
                                } else {
                                    caseType = ((NLNNounSlot) agreeSlot).getCase();
                                }
                                if (!SPGender.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    gender = SPGender;
                                } else {
                                    gender = LQM.getNounEntry(((NLNNounSlot) agreeSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                }
                            } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                                if (!SPNumber.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    number = SPNumber;
                                } else {
                                    number = ((NLNAdjectiveSlot) agreeSlot).getNumber();
                                }
                                if (!SPCaseType.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    caseType = SPCaseType;
                                } else {
                                    caseType = ((NLNAdjectiveSlot) agreeSlot).getCase();
                                }
                                if (!SPGender.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                    gender = SPGender;
                                } else {
                                    gender = ((NLNAdjectiveSlot) agreeSlot).getGender();
                                }
                            }
                        }
                    } else {
                        number = adjective.getNumber();
                        caseType = adjective.getCase();
                        gender = adjective.getGender();
                    }
                }

                if (Languages.isEnglish(getLanguage())) {
                    LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                    if (adjective.isCapitalized()) {
                        realizedSlots.add(capitalizeText(adjectiveEntry.get_form()));
                    } else {
                        realizedSlots.add(adjectiveEntry.get_form());
                    }
                } else if (Languages.isGreek(getLanguage())) {
                    LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                    if (adjective.isCapitalized()) {
                        realizedSlots.add(capitalizeText(adjectiveEntry.get(gender, number, caseType)));
                    } else {
                        realizedSlots.add(adjectiveEntry.get(gender, number, caseType));
                    }
                }
            } else if (slots.get(i) instanceof NLNNounSlot) {
                NLNNounSlot noun = (NLNNounSlot) slots.get(i);
                IRI lexiconEntryIRI = noun.getLexiconEntryIRI();

                String number = "";
                String caseType = "";

                if (noun.isHead()) {
                    if (SPNumber.isEmpty()) {
                        number = noun.getNumber();
                    } else {
                        number = SPNumber;
                    }
                    if (SPCaseType.isEmpty()) {
                        caseType = noun.getCase();
                    } else {
                        caseType = SPCaseType;
                    }
                } else if (noun.getAgreesWithID() != null) {
                    NLNSlot agreeSlot = noun;

                    boolean foundAgree = false;
                    NodeID agreeID = noun.getAgreesWithID();

                    while (!foundAgree) {
                        for (int j = 0; j < slots.size(); j++) {
                            if (slots.get(j).getId().equals(agreeID)) {
                                agreeSlot = slots.get(j);

                                if (agreeSlot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) agreeSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNNounSlot) agreeSlot).getAgreesWithID();
                                    } else {
                                        foundAgree = true;
                                    }
                                } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) agreeSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNAdjectiveSlot) agreeSlot).getAgreesWithID();
                                    } else {
                                        foundAgree = true;
                                    }
                                } else if (agreeSlot instanceof NLNArticleSlot) {
                                    if (((NLNArticleSlot) agreeSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNArticleSlot) agreeSlot).getAgreesWithID();
                                    } else {
                                        foundAgree = true;
                                    }
                                }
                            }
                        }
                    }

                    if (foundAgree && agreeSlot != null) {
                        if (agreeSlot instanceof NLNNounSlot) {
                            if (!SPNumber.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                number = SPNumber;

                                String restriction = LQM.getNounEntry(((NLNNounSlot) agreeSlot).getLexiconEntryIRI(), getLanguage()).getNumber();
                                if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                                    if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                                        number = XmlMsgs.SINGULAR;
                                    } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                                        number = XmlMsgs.PLURAL;
                                    }
                                }
                            } else {
                                number = ((NLNNounSlot) agreeSlot).getNumber();
                            }
                            if (!SPCaseType.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                caseType = SPCaseType;
                            } else {
                                caseType = ((NLNNounSlot) agreeSlot).getCase();
                            }
                        } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                            if (!SPNumber.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                number = SPNumber;
                            } else {
                                number = ((NLNAdjectiveSlot) agreeSlot).getNumber();
                            }
                            if (!SPCaseType.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                caseType = SPCaseType;
                            } else {
                                caseType = ((NLNAdjectiveSlot) agreeSlot).getCase();
                            }
                        }
                    }
                } else {
                    number = noun.getNumber();
                    if (Languages.isGreek(getLanguage())) {
                        caseType = noun.getCase();
                    }
                }


                String restriction = LQM.getNounEntry(noun.getLexiconEntryIRI(), getLanguage()).getNumber();
                if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                    if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                        number = XmlMsgs.SINGULAR;
                    } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                        number = XmlMsgs.PLURAL;
                    }
                }

                if (Languages.isEnglish(getLanguage())) {
                    LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    if (noun.isCapitalized()) {
                        realizedSlots.add(capitalizeText(nounEntry.get(caseType, number)));
                    } else {
                        realizedSlots.add(nounEntry.get(caseType, number));
                    }
                } else if (Languages.isGreek(getLanguage())) {
                    LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    if (noun.isCapitalized()) {
                        realizedSlots.add(capitalizeText(nounEntry.get(caseType, number)));
                    } else {
                        realizedSlots.add(nounEntry.get(caseType, number));
                    }
                }
            } else if (slots.get(i) instanceof NLNPrepositionSlot) {
                NLNPrepositionSlot preposition = (NLNPrepositionSlot) slots.get(i);
                realizedSlots.add(preposition.getPrep());
            } else if (slots.get(i) instanceof NLNStringSlot) {
                NLNStringSlot string = (NLNStringSlot) slots.get(i);
                realizedSlots.add(string.getText());
            }
        }

        //ARTICLE PASS
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNArticleSlot) {
                if (((i == 0) && (articleUse != NLName.FORCE_NO_ARTICLE)) || (i > 0)) {
                    NLNArticleSlot article = (NLNArticleSlot) slots.get(i);
                    String realizedArticle = "";
                    String number = "";
                    String gender = "";
                    String caseType = "";

                    if (article.getAgreesWithID() != null) {
                        NLNSlot agreeSlot = article;

                        boolean foundAgree = false;
                        NodeID agreeID = article.getAgreesWithID();

                        while (!foundAgree) {
                            for (int j = 0; j < slots.size(); j++) {
                                if (slots.get(j).getId().equals(agreeID)) {
                                    agreeSlot = slots.get(j);

                                    if (agreeSlot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNNounSlot) agreeSlot).getAgreesWithID();
                                            j = 0;
                                        } else {
                                            foundAgree = true;
                                        }
                                    } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                                        if (((NLNAdjectiveSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNAdjectiveSlot) agreeSlot).getAgreesWithID();
                                            j = 0;
                                        } else {
                                            foundAgree = true;
                                        }
                                    } else if (agreeSlot instanceof NLNArticleSlot) {
                                        if (((NLNArticleSlot) agreeSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNArticleSlot) agreeSlot).getAgreesWithID();
                                            j = 0;
                                        } else {
                                            foundAgree = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (foundAgree && agreeSlot != null) {
                            if (agreeSlot instanceof NLNNounSlot) {
                                number = ((NLNNounSlot) agreeSlot).getNumber();
                                if (Languages.isGreek(getLanguage())) {
                                    if (!SPGender.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                        gender = SPGender;
                                    } else {
                                        gender = LQM.getNounEntry(((NLNNounSlot) agreeSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                    }
                                    if (!SPCaseType.isEmpty() && ((NLNNounSlot) agreeSlot).isHead()) {
                                        caseType = SPCaseType;
                                    } else {
                                        caseType = ((NLNNounSlot) agreeSlot).getCase();
                                    }
                                }
                            } else if (agreeSlot instanceof NLNAdjectiveSlot) {
                                number = ((NLNAdjectiveSlot) agreeSlot).getNumber();
                                if (Languages.isGreek(getLanguage())) {
                                    gender = ((NLNAdjectiveSlot) agreeSlot).getGender();
                                    caseType = ((NLNAdjectiveSlot) agreeSlot).getCase();
                                }
                            }
                        }
                    } else {
                        number = article.getNumber();
                        gender = article.getGender();
                        caseType = article.getCase();
                    }

                    if (Languages.isEnglish(getLanguage())) {
                        if (articleUse == NLName.FORCE_DEF_ARTICLE) {
                            realizedArticle = EnglishArticles.getDefiniteArticle();
                        } else if (articleUse == NLName.FORCE_INDEF_ARTICLE) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (i < realizedSlots.size()) {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                                } else {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle("");
                                }
                            }
                        } else if (article.isDefinite()) {
                            realizedArticle = EnglishArticles.getDefiniteArticle();
                        } else {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (i < realizedSlots.size()) {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                                } else {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle("");
                                }
                            }
                        }
                    } else if (Languages.isGreek(getLanguage())) {
                        if (articleUse == NLName.FORCE_DEF_ARTICLE) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                            } else {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                            }
                        } else if (articleUse == NLName.FORCE_INDEF_ARTICLE) {
                            realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                        } else if (article.isDefinite()) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                            } else {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                            }
                        } else {
                            realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                        }
                    }
                    realizedSlots.add(i, realizedArticle);
                }
            }
        }

        if (Languages.isEnglish(getLanguage())) {
            if ((slots.size() == 1) && (slots.get(0) instanceof NLNStringSlot) && (SPCaseType.equals(XmlMsgs.GENITIVE_TAG))) {
                if (!realizedSlots.get(realizedSlots.size() - 1).endsWith("s")) {
                    realizedSlots.set(realizedSlots.size() - 1, realizedSlots.get(realizedSlots.size() - 1) + "'s");
                } else {
                    realizedSlots.set(realizedSlots.size() - 1, realizedSlots.get(realizedSlots.size() - 1) + "'");
                }
            }
        }

        String realizedName = "";
        for (int i = 0; i < realizedSlots.size(); i++) {
            realizedName = realizedName + " " + realizedSlots.get(i);
        }

        return realizedName.trim();
    }

    private String realizeNLName(NLName name, String SPCaseType, String SPNumber, String SPGender, int articleUse, ArrayList<IRI> appendAdjectives) {
        ArrayList<NLNSlot> slots = name.getSlotsList();
        Collections.sort(slots);

        ArrayList<String> realizedSlots = new ArrayList<String>();

        int adjectiveInsertion = 0;
        boolean nameContainsAdjective = false;
        String headNumber = "";
        String headGender = "";
        String headCase = "";
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNAdjectiveSlot) {
                nameContainsAdjective = true;

                NLNAdjectiveSlot adjective = (NLNAdjectiveSlot) slots.get(i);
                IRI lexiconEntryIRI = adjective.getLexiconEntryIRI();

                String number = "";
                String caseType = "";
                String gender = "";

                if (Languages.isGreek(getLanguage())) {
                    if (adjective.isHead()) {
                        if (SPNumber.isEmpty()) {
                            number = adjective.getNumber();
                        } else {
                            number = SPNumber;
                        }
                        if (SPCaseType.isEmpty()) {
                            caseType = adjective.getCase();
                        } else {
                            caseType = SPCaseType;
                        }
                        if (SPGender.isEmpty()) {
                            gender = adjective.getGender();
                        } else {
                            gender = SPGender;
                        }
                    } else if (adjective.getAgreesWithID() != null) {
                        NLNSlot headSlot = adjective;

                        boolean foundHead = false;
                        NodeID agreeID = adjective.getAgreesWithID();

                        while (!foundHead) {
                            for (int j = 0; j < slots.size(); j++) {
                                if (slots.get(j).getId().equals(agreeID)) {
                                    headSlot = slots.get(j);

                                    if (headSlot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNAdjectiveSlot) {
                                        if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNArticleSlot) {
                                        if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (headSlot != null) {
                            if (headSlot instanceof NLNNounSlot) {
                                number = ((NLNNounSlot) headSlot).getNumber();
                                caseType = ((NLNNounSlot) headSlot).getCase();
                                gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                            } else if (headSlot instanceof NLNAdjectiveSlot) {
                                number = ((NLNAdjectiveSlot) headSlot).getNumber();
                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                                gender = ((NLNAdjectiveSlot) headSlot).getGender();
                            }
                        }
                    } else {
                        number = adjective.getNumber();
                        caseType = adjective.getCase();
                        gender = adjective.getGender();
                    }
                }

                //APPEND ADJECTIVES
                if (adjective.isHead()) {
                    boolean foundStart = false;
                    int j = i;
                    while (!foundStart && (j > 0)) {
                        if (!(slots.get(j) instanceof NLNNounSlot) && !(slots.get(j) instanceof NLNAdjectiveSlot)) {
                            foundStart = true;
                            adjectiveInsertion = i;
                        }
                        j--;
                    }

                    headNumber = number;
                    headCase = caseType;
                    headGender = gender;
                }

                if (Languages.isEnglish(getLanguage())) {
                    LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(adjectiveEntry.get_form());
                } else if (Languages.isGreek(getLanguage())) {
                    LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(adjectiveEntry.get(gender, number, caseType));
                }
            } else if (slots.get(i) instanceof NLNNounSlot) {
                NLNNounSlot noun = (NLNNounSlot) slots.get(i);
                IRI lexiconEntryIRI = noun.getLexiconEntryIRI();

                String number = "";
                String caseType = "";

                if (noun.isHead()) {
                    if (SPNumber.isEmpty()) {
                        number = noun.getNumber();
                    } else {
                        number = SPNumber;
                    }
                    if (SPCaseType.isEmpty()) {
                        caseType = noun.getCase();
                    } else {
                        caseType = SPCaseType;
                    }
                } else if (noun.getAgreesWithID() != null) {
                    NLNSlot headSlot = noun;

                    boolean foundHead = false;
                    NodeID agreeID = noun.getAgreesWithID();

                    while (!foundHead) {
                        for (int j = 0; j < slots.size(); j++) {
                            if (slots.get(j).getId().equals(agreeID)) {
                                headSlot = slots.get(j);

                                if (headSlot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNArticleSlot) {
                                    if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                }
                            }
                        }
                    }

                    if (headSlot != null) {
                        if (headSlot instanceof NLNNounSlot) {
                            number = ((NLNNounSlot) headSlot).getNumber();
                            caseType = ((NLNNounSlot) headSlot).getCase();
                        } else if (headSlot instanceof NLNAdjectiveSlot) {
                            number = ((NLNAdjectiveSlot) headSlot).getNumber();
                            caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                        }
                    }
                } else {
                    number = noun.getNumber();
                    caseType = noun.getCase();
                }

                //APPEND ADJECTIVES
                if (noun.isHead()) {
                    boolean foundStart = false;
                    int j = i;
                    while (!foundStart && (j > 0)) {
                        if (!(slots.get(j) instanceof NLNNounSlot) && !(slots.get(j) instanceof NLNAdjectiveSlot)) {
                            foundStart = true;
                            adjectiveInsertion = i;
                        }
                        j--;
                    }

                    headNumber = number;
                    headCase = caseType;
                    if (SPGender.isEmpty()) {
                        headGender = LQM.getNounEntry(noun.getLexiconEntryIRI(), getLanguage()).getGender();
                    } else {
                        headGender = SPGender;
                    }
                }

                String restriction = LQM.getNounEntry(noun.getLexiconEntryIRI(), getLanguage()).getNumber();
                if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                    if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                        number = XmlMsgs.SINGULAR;
                    } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                        number = XmlMsgs.PLURAL;
                    }
                }

                if (Languages.isEnglish(getLanguage())) {
                    LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(nounEntry.get("", number));
                } else if (Languages.isGreek(getLanguage())) {
                    LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(nounEntry.get(caseType, number));
                }
            } else if (slots.get(i) instanceof NLNPrepositionSlot) {
                NLNPrepositionSlot preposition = (NLNPrepositionSlot) slots.get(i);
                realizedSlots.add(preposition.getPrep());
            } else if (slots.get(i) instanceof NLNStringSlot) {
                NLNStringSlot string = (NLNStringSlot) slots.get(i);
                realizedSlots.add(string.getText());
            }
        }

        //APPEND ADJECTIVES
        for (int a = 0; a < appendAdjectives.size(); a++) {
            if (appendAdjectives.get(a).toString().equals(XmlMsgs.DISJUNCTIVE)) {
                realizedSlots.add(XmlMsgs.DISJUNCTIVE);
            } else if (Languages.isEnglish(getLanguage())) {
                if (a > 0 || nameContainsAdjective) {
                    realizedSlots.add(adjectiveInsertion, Aggregation.COMMA);
                }

                LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(appendAdjectives.get(a), getLanguage());
                realizedSlots.add(adjectiveInsertion, adjectiveEntry.get_form());
            } else if (Languages.isGreek(getLanguage())) {
                if (a > 0 || nameContainsAdjective) {
                    realizedSlots.add(adjectiveInsertion, Aggregation.COMMA);
                }

                LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(appendAdjectives.get(a), getLanguage());
                realizedSlots.add(adjectiveInsertion, adjectiveEntry.get(headGender, headNumber, headCase));
            }
        }

        //ARTICLE PASS
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNArticleSlot) {
                if (((i == 0) && (articleUse != NLName.FORCE_NO_ARTICLE)) || (i > 0)) {
                    NLNArticleSlot article = (NLNArticleSlot) slots.get(i);
                    String realizedArticle = "";
                    String number = "";
                    String gender = "";
                    String caseType = "";

                    if (article.getAgreesWithID() != null) {
                        NLNSlot headSlot = article;

                        boolean foundHead = false;
                        NodeID agreeID = article.getAgreesWithID();

                        while (!foundHead) {
                            for (int j = 0; j < slots.size(); j++) {
                                if (slots.get(j).getId().equals(agreeID)) {
                                    headSlot = slots.get(j);

                                    if (headSlot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNAdjectiveSlot) {
                                        if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNArticleSlot) {
                                        if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (headSlot != null) {
                            if (headSlot instanceof NLNNounSlot) {
                                number = ((NLNNounSlot) headSlot).getNumber();
                                if (Languages.isGreek(getLanguage())) {
                                    gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                    caseType = ((NLNNounSlot) headSlot).getCase();
                                }
                            } else if (headSlot instanceof NLNAdjectiveSlot) {
                                number = ((NLNAdjectiveSlot) headSlot).getNumber();
                                if (Languages.isGreek(getLanguage())) {
                                    gender = ((NLNAdjectiveSlot) headSlot).getGender();
                                    caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                                }
                            }
                        }
                    } else {
                        number = article.getNumber();
                        gender = article.getGender();
                        caseType = article.getCase();
                    }

                    if (Languages.isEnglish(getLanguage())) {
                        if (articleUse == NLName.FORCE_DEF_ARTICLE) {
                            realizedArticle = EnglishArticles.getDefiniteArticle();
                        } else if (articleUse == NLName.FORCE_INDEF_ARTICLE) {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (i < realizedSlots.size()) {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                                } else {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle("");
                                }
                            }
                        } else if (article.isDefinite()) {
                            realizedArticle = EnglishArticles.getDefiniteArticle();
                        } else {
                            if (number.equals(XmlMsgs.SINGULAR)) {
                                if (i < realizedSlots.size()) {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                                } else {
                                    realizedArticle = EnglishArticles.getIndefiniteArticle("");
                                }
                            }
                        }
                    } else if (Languages.isGreek(getLanguage())) {
                        if (articleUse == NLName.FORCE_DEF_ARTICLE) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                            } else {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                            }
                        } else if (articleUse == NLName.FORCE_INDEF_ARTICLE) {
                            realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                        } else if (article.isDefinite()) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                            } else {
                                realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                            }
                        } else {
                            realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                        }
                    }
                    realizedSlots.add(i, realizedArticle);
                }
            }
        }

        boolean isLast = true;
        boolean isBeforeLast = true;
        for (int i = realizedSlots.size() - 1; i >= 0; i--) {
            if (realizedSlots.get(i).equals(XmlMsgs.DISJUNCTIVE)) {
                if (isLast) {
                    realizedSlots.set(i, Aggregation.COMMA);
                    isLast = false;
                } else if (isBeforeLast) {
                    if (Languages.isEnglish(getLanguage())) {
                        realizedSlots.set(i, Aggregation.ENGLISH_DISJUNCTIVE);
                    } else if (Languages.isGreek(getLanguage())) {
                        realizedSlots.set(i, Aggregation.GREEK_DISJUNCTIVE);
                    }

                    isBeforeLast = false;
                } else {
                    realizedSlots.set(i, Aggregation.COMMA);
                }
            }
        }

        String realizedName = "";
        for (int i = 0; i < realizedSlots.size(); i++) {
            realizedName = realizedName + " " + realizedSlots.get(i);
        }

        return realizedName.trim();
    }

    private String aggregateNLNameAdjectives(ArrayList<NLName> names, String SPCaseType, String SPNumber, boolean isConjunction, boolean useBullets) {
        ArrayList<NLNSlot> slots = names.get(0).getSlotsList();
        Collections.sort(slots);

        ArrayList<String> realizedSlots = new ArrayList<String>();

        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNAdjectiveSlot) {
                NLNAdjectiveSlot adjective = (NLNAdjectiveSlot) slots.get(i);

                String number = "";
                String caseType = "";
                String gender = "";

                if (Languages.isGreek(getLanguage())) {
                    if (adjective.getAgreesWithID() != null) {
                        NLNSlot headSlot = adjective;

                        boolean foundHead = false;
                        NodeID agreeID = adjective.getAgreesWithID();

                        while (!foundHead) {
                            for (int j = 0; j < slots.size(); j++) {
                                if (slots.get(j).getId().equals(agreeID)) {
                                    headSlot = slots.get(j);

                                    if (headSlot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNAdjectiveSlot) {
                                        if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    } else if (headSlot instanceof NLNArticleSlot) {
                                        if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                            agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                        } else {
                                            foundHead = true;
                                        }
                                    }
                                }
                            }
                        }

                        if (headSlot != null) {
                            if (headSlot instanceof NLNNounSlot) {
                                number = ((NLNNounSlot) headSlot).getNumber();
                                caseType = ((NLNNounSlot) headSlot).getCase();
                                gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                            }
                        }
                    } else {
                        number = adjective.getNumber();
                        caseType = adjective.getCase();
                        gender = adjective.getGender();
                    }
                }

                for (NLName name : names) {
                    ArrayList<NLNSlot> nameSlots = name.getSlotsList();

                    for (int k = 0; k < nameSlots.size(); k++) {
                        if (nameSlots.get(k) instanceof NLNAdjectiveSlot) {
                            adjective = (NLNAdjectiveSlot) nameSlots.get(k);

                            IRI lexiconEntryIRI = adjective.getLexiconEntryIRI();

                            if (Languages.isEnglish(getLanguage())) {
                                LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                                realizedSlots.add(adjectiveEntry.get_form());
                            } else if (Languages.isGreek(getLanguage())) {
                                LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, getLanguage());
                                realizedSlots.add(adjectiveEntry.get(gender, number, caseType));
                            }
                            if (isConjunction) {
                                realizedSlots.add(XmlMsgs.CONNECTIVE);
                            } else {
                                realizedSlots.add(XmlMsgs.DISJUNCTIVE);
                            }

                        }
                    }
                }
            } else if (slots.get(i) instanceof NLNNounSlot) {
                NLNNounSlot noun = (NLNNounSlot) slots.get(i);
                IRI lexiconEntryIRI = noun.getLexiconEntryIRI();

                String number = "";
                String caseType = "";

                if (noun.isHead()) {
                    if (SPNumber.isEmpty()) {
                        number = noun.getNumber();
                    } else {
                        number = SPNumber;
                    }
                    if (Languages.isGreek(getLanguage())) {
                        if (SPCaseType.isEmpty()) {
                            caseType = noun.getCase();
                        } else {
                            caseType = SPCaseType;
                        }
                    }
                } else if (noun.getAgreesWithID() != null) {
                    NLNSlot headSlot = noun;

                    boolean foundHead = false;
                    NodeID agreeID = noun.getAgreesWithID();

                    while (!foundHead) {
                        for (int j = 0; j < slots.size(); j++) {
                            if (slots.get(j).getId().equals(agreeID)) {
                                headSlot = slots.get(j);

                                if (headSlot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNArticleSlot) {
                                    if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                }
                            }
                        }
                    }

                    if (headSlot != null) {
                        if (headSlot instanceof NLNNounSlot) {
                            number = ((NLNNounSlot) headSlot).getNumber();
                            if (Languages.isGreek(getLanguage())) {
                                caseType = ((NLNNounSlot) headSlot).getCase();
                            }
                        } else if (headSlot instanceof NLNAdjectiveSlot) {
                            number = ((NLNAdjectiveSlot) headSlot).getNumber();
                            if (Languages.isGreek(getLanguage())) {
                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                            }
                        }
                    }
                } else {
                    number = noun.getNumber();
                    if (Languages.isGreek(getLanguage())) {
                        caseType = noun.getCase();
                    }
                }

                String restriction = LQM.getNounEntry(noun.getLexiconEntryIRI(), getLanguage()).getNumber();
                if (!restriction.equals(LexEntry.NUMBER_BOTH)) {
                    if (restriction.equals(LexEntry.NUMBER_ONLY_SINGLE)) {
                        number = XmlMsgs.SINGULAR;
                    } else if (restriction.equals(LexEntry.NUMBER_ONLY_PLURAL)) {
                        number = XmlMsgs.PLURAL;
                    }
                }

                if (Languages.isEnglish(getLanguage())) {
                    LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(nounEntry.get("", number));
                } else if (Languages.isGreek(getLanguage())) {
                    LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, getLanguage());
                    realizedSlots.add(nounEntry.get(caseType, number));
                }
            } else if (slots.get(i) instanceof NLNPrepositionSlot) {
                NLNPrepositionSlot preposition = (NLNPrepositionSlot) slots.get(i);
                realizedSlots.add(preposition.getPrep());
            } else if (slots.get(i) instanceof NLNStringSlot) {
                NLNStringSlot string = (NLNStringSlot) slots.get(i);
                realizedSlots.add(string.getText());
            }
        }

        //ARTICLE PASS
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNArticleSlot) {
                NLNArticleSlot article = (NLNArticleSlot) slots.get(i);
                String realizedArticle = "";
                String number = "";
                String gender = "";
                String caseType = "";

                if (article.getAgreesWithID() != null) {
                    NLNSlot headSlot = article;

                    boolean foundHead = false;
                    NodeID agreeID = article.getAgreesWithID();

                    while (!foundHead) {
                        for (int j = 0; j < slots.size(); j++) {
                            if (slots.get(j).getId().equals(agreeID)) {
                                headSlot = slots.get(j);

                                if (headSlot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNArticleSlot) {
                                    if (((NLNArticleSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNArticleSlot) headSlot).getAgreesWithID();
                                    } else {
                                        foundHead = true;
                                    }
                                }
                            }
                        }
                    }

                    if (headSlot != null) {
                        if (headSlot instanceof NLNNounSlot) {
                            number = ((NLNNounSlot) headSlot).getNumber();
                            if (Languages.isGreek(getLanguage())) {
                                gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), getLanguage()).getGender();
                                caseType = ((NLNNounSlot) headSlot).getCase();
                            }
                        } else if (headSlot instanceof NLNAdjectiveSlot) {
                            number = ((NLNAdjectiveSlot) headSlot).getNumber();
                            if (Languages.isGreek(getLanguage())) {
                                gender = ((NLNAdjectiveSlot) headSlot).getGender();
                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                            }
                        }
                    }
                } else {
                    number = article.getNumber();
                    gender = article.getGender();
                    caseType = article.getCase();
                }

                if (Languages.isEnglish(getLanguage())) {
                    if (article.isDefinite()) {
                        realizedArticle = EnglishArticles.getDefiniteArticle();
                    } else {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                            } else {
                                realizedArticle = EnglishArticles.getIndefiniteArticle("");
                            }
                        }
                    }
                } else if (Languages.isGreek(getLanguage())) {
                    if (article.isDefinite()) {
                        if (i < realizedSlots.size()) {
                            realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                        } else {
                            realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                        }
                    } else {
                        realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                    }
                }
                realizedSlots.add(i, realizedArticle);
            }
        }

        boolean isLast = true;
        boolean isBeforeLast = true;
        for (int i = realizedSlots.size() - 1; i >= 0; i--) {
            if (realizedSlots.get(i).equals(XmlMsgs.CONNECTIVE)) {
                if (isLast) {
                    realizedSlots.remove(i);
                    isLast = false;
                } else if (isBeforeLast) {
                    if (Languages.isEnglish(getLanguage())) {
                        realizedSlots.set(i, Aggregation.ENGLISH_CONNECTIVE);
                    } else if (Languages.isGreek(getLanguage())) {
                        realizedSlots.set(i, Aggregation.GREEK_CONNECTIVE);
                    }

                    isBeforeLast = false;
                } else {
                    realizedSlots.set(i, Aggregation.COMMA);
                }
            } else if (realizedSlots.get(i).equals(XmlMsgs.DISJUNCTIVE)) {
                if (isLast) {
                    realizedSlots.remove(i);
                    isLast = false;
                } else if (isBeforeLast) {
                    if (Languages.isEnglish(getLanguage())) {
                        realizedSlots.set(i, Aggregation.ENGLISH_DISJUNCTIVE);
                    } else if (Languages.isGreek(getLanguage())) {
                        realizedSlots.set(i, Aggregation.GREEK_DISJUNCTIVE);
                    }

                    isBeforeLast = false;
                } else {
                    realizedSlots.set(i, Aggregation.COMMA);
                }
            }
        }

        String realizedName = "";
        for (int i = 0; i < realizedSlots.size(); i++) {
            if (useBullets) {
                if (i == 0) {
                    realizedName += "<ul><li>";
                } else if (realizedSlots.get(i - 1).equals(Aggregation.COMMA)
                        || realizedSlots.get(i - 1).equals(Aggregation.ENGLISH_CONNECTIVE)
                        || realizedSlots.get(i - 1).equals(Aggregation.GREEK_CONNECTIVE)
                        || realizedSlots.get(i - 1).equals(Aggregation.ENGLISH_DISJUNCTIVE)
                        || realizedSlots.get(i - 1).equals(Aggregation.GREEK_DISJUNCTIVE)) {
                    realizedName += "<li>";
                }
            }
            realizedName = realizedName + " " + realizedSlots.get(i);
            if (useBullets) {
                if (i == realizedSlots.size() - 1) {
                    realizedName += "</li></ul>";
                } else if (realizedSlots.get(i).equals(Aggregation.COMMA)
                        || realizedSlots.get(i).equals(Aggregation.ENGLISH_CONNECTIVE)
                        || realizedSlots.get(i).equals(Aggregation.GREEK_CONNECTIVE)
                        || realizedSlots.get(i).equals(Aggregation.ENGLISH_DISJUNCTIVE)
                        || realizedSlots.get(i).equals(Aggregation.GREEK_DISJUNCTIVE)) {
                    realizedName += "</li>";
                }
            }
        }

        return realizedName.trim();
    }

    private String generateRefExpressionForAnonEntity(Node slot, IRI superIRI, String gender, String number, String caseType) {
        if (NLNQM.getNLName(superIRI, getLanguage()) == null) {
            setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
            return "[NOT FOUND next Filler]";
        } else if (NLNQM.getNLName(superIRI, getLanguage()) != null) {
            String ret = "";

            NLNSlot head = NLNQM.getNLName(superIRI, getLanguage()).getHeadSlot();
            if (head != null) {
                if (head instanceof NLNNounSlot) {
                    if (gender.isEmpty()) {
                        gender = LQM.getNounEntry(((NLNNounSlot) head).getLexiconEntryIRI(), getLanguage()).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNNounSlot) head).getNumber();
                    }
                } else if (head instanceof NLNAdjectiveSlot) {
                    if (gender.isEmpty()) {
                        gender = ((NLNAdjectiveSlot) head).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNAdjectiveSlot) head).getNumber();
                    }
                }
            }
            //DEFAULT NLNAME CASE
            if (number.isEmpty()) {
                number = XmlMsgs.SINGULAR;
            }
            if ((XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).equals(NLResourceManager.isA.getIRI().toString())) || ((XmlMsgs.getAttribute(slot.getParentNode(), XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty))).equals(NLResourceManager.instanceOf.getIRI().toString())) {
                if (Languages.isGreek(getLanguage())) {
                    ret = realizeNLName(NLNQM.getNLName(superIRI, getLanguage()), caseType, number, gender, NLName.FORCE_NO_ARTICLE);
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).equals(XmlMsgs.FOCUSLevel1)) {
                        if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                            return GreekArticles.getDefiniteArticle(gender, number, caseType, ret);
                        }
                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_NULL);
                        return "";//null subject
                    }
                    if (!isREAuto(slot)) {
                        return generateRefExpressionFromUserChoice(slot, gender, number, caseType, ret);
                    }
                    setProducedRE(slot, SurfaceRealization.PROD_RE_Demonstrative);
                    return GreekArticles.getPronoun(gender, number, caseType, false);
                } else if (Languages.isEnglish(getLanguage())) {
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).equals(XmlMsgs.FOCUSLevel1)) {
                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_Pronoun);

                        String prn = EnglishArticles.getPronoun(caseType, number, gender);
                        return prn;
                    }
                    ret = realizeNLName(NLNQM.getNLName(superIRI, getLanguage()), caseType, number, gender, NLName.FORCE_NO_ARTICLE);
                    if (!isREAuto(slot)) {
                        return generateRefExpressionFromUserChoice(slot, gender, number, caseType, ret);
                    }
                    setProducedRE(slot, SurfaceRealization.PROD_RE_Demonstrative);
                    return EnglishArticles.getDemonstrativePronoun(number);
                }
            } else {
                if (Languages.isEnglish(getLanguage())) {
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).equals(XmlMsgs.FOCUSLevel1)) {
                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_Pronoun);

                        String prn = EnglishArticles.getPronoun(caseType, number, gender);
                        return prn;
                    }
                    ret = realizeNLName(NLNQM.getNLName(superIRI, getLanguage()), caseType, number, gender, NLName.FORCE_NO_ARTICLE);

                    if (ret.isEmpty()) {
                        setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                        return "[NOUN OR CANNED TEXT NOT FOUND]";
                    }
                    setProducedRE(slot, SurfaceRealization.PROD_RE_Demonstrative);
                    return EnglishArticles.getDemonstrativePronoun(number) + " " + ret;
                } else if (Languages.isGreek(getLanguage())) {
                    ret = realizeNLName(NLNQM.getNLName(superIRI, getLanguage()), caseType, number, gender, NLName.FORCE_NO_ARTICLE);
                    if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).equals(XmlMsgs.FOCUSLevel1)) {
                        if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                            this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                            return GreekArticles.getDefiniteArticle(gender, number, caseType, ret);
                        }
                        this.setProducedRE(slot, SurfaceRealization.PROD_RE_NULL);
                        return "";//null subject
                    }
                    if (ret.isEmpty()) {
                        setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                        return "[NOUN OR CANNED TEXT NOT FOUND]";
                    }
                    return GreekArticles.getPronoun(gender, number, caseType, true) + " " + ret;
                } else {
                    return "[NOUN OR CANNED TEXT NOT FOUND]";
                }
            }
        }

        return "ERROR";
    }

    private String generateRefExpressionForEntity(NLName name, String caseType, String gender, String number, Node slot) {
        if (Languages.isEnglish(getLanguage())) {
            String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);
            NLNSlot head = name.getHeadSlot();
            if (head != null) {
                if (head instanceof NLNNounSlot) {
                    if (gender.isEmpty()) {
                        gender = LQM.getNounEntry(((NLNNounSlot) head).getLexiconEntryIRI(), getLanguage()).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNNounSlot) head).getNumber();
                    }
                } else if (head instanceof NLNAdjectiveSlot) {
                    if (gender.isEmpty()) {
                        gender = ((NLNAdjectiveSlot) head).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNAdjectiveSlot) head).getNumber();
                    }
                }
            }

            if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel4) == 0) {
                if (exist("previous", slot.getParentNode().getAttributes())) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                return realizedName;
            }
            if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel3) == 0) {
                if (exist("previous", slot.getParentNode().getAttributes())) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                if (this.realizedText.lastIndexOf(" the") > this.realizedText.length() - 6) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                return realizedName;
            } else if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel2) == 0) {
                if (exist("previous", slot.getParentNode().getAttributes())) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                return realizedName;
            } else if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel1) == 0) {

                this.setProducedRE(slot, SurfaceRealization.PROD_RE_Pronoun);
                String prn = EnglishArticles.getPronoun(caseType, number, gender);
                return prn;
            }
        } else if (Languages.isGreek(getLanguage())) {
            String realizedName = realizeNLName(name, caseType, number, gender, NLName.REGULAR_ARTICLE);
            NLNSlot head = name.getHeadSlot();
            if (head != null) {
                if (head instanceof NLNNounSlot) {
                    if (gender.isEmpty()) {
                        gender = LQM.getNounEntry(((NLNNounSlot) head).getLexiconEntryIRI(), getLanguage()).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNNounSlot) head).getNumber();
                    }
                } else if (head instanceof NLNAdjectiveSlot) {
                    if (gender.isEmpty()) {
                        gender = ((NLNAdjectiveSlot) head).getGender();
                    }
                    if (number.isEmpty()) {
                        number = ((NLNAdjectiveSlot) head).getNumber();
                    }
                }
            }
            if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel4) == 0) {
                if (exist("previous", slot.getParentNode().getAttributes())) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                return GreekArticles.getDefiniteArticle(gender, XmlMsgs.SINGULAR, caseType, realizedName) + " " + realizedName;
            }
            if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel3) == 0) {
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                if (!realizedName.startsWith(GreekArticles.getDefiniteArticle(gender, number, caseType, realizedName))) {
                    return GreekArticles.getDefiniteArticle(gender, number, caseType, realizedName) + " " + realizedName;
                }
                return realizedName;
            } else if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel2) == 0) {
                if (exist("previous", slot.getParentNode().getAttributes())) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return realizedName;
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                if (!realizedName.startsWith(GreekArticles.getDefiniteArticle(gender, number, caseType, realizedName))) {
                    return GreekArticles.getDefiniteArticle(gender, XmlMsgs.SINGULAR, caseType, realizedName) + " " + realizedName;
                }
                return realizedName;
            } else if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RE_FOCUS).compareTo(XmlMsgs.FOCUSLevel1) == 0) {
                if (caseType.compareTo(XmlMsgs.GENITIVE_TAG) == 0) {
                    this.setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return GreekArticles.getDefiniteArticle(gender, number, caseType, realizedName);
                }
                this.setProducedRE(slot, SurfaceRealization.PROD_RE_NULL);
                return "";//null subject
            }
        }

        return "ERROR";
    }

    public boolean isREAuto(Node Slot) {
        if (XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, XmlMsgs.RETYPE).compareTo(XmlMsgs.REF_AUTO) == 0
                || XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, XmlMsgs.RETYPE).compareTo("") == 0) {
            return true;
        }
        return false;
    }

    public void removePreviousWords() {
        if (realizedText.indexOf(",") < 0 && realizedText.indexOf(".") < 0) {
            if (getLanguage().equalsIgnoreCase("El")) {
                String tmp = realizedText.substring(0, realizedText.indexOf("είναι"));
                realizedText.delete(0, realizedText.length() - 1);

                realizedText.append(tmp).append("είναι ");
            } else {
                String tmp = realizedText.substring(0, realizedText.indexOf("is"));
                realizedText.delete(0, realizedText.length() - 1);

                realizedText.append(tmp).append("is ");
            }
        }
    }

    public boolean exist(String name, NamedNodeMap map) {
        for (int i = 0; i < map.getLength(); i++) {
            if (map.item(i).getLocalName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // generate a referring expression based on the referring expression that was declared
    // for this Slot in the microplan
    public String generateRefExpressionFromUserChoice(Node slot, String gender, String number, String caseType, String text) {
        if (XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RETYPE).compareTo(XmlMsgs.REF_AUTO) != 0) {
            String retype = XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.RETYPE);

            if (retype.compareTo(XmlMsgs.REF_PRONOUN) == 0) {
                if (Languages.isEnglish(getLanguage())) {
                    setProducedRE(slot, SurfaceRealization.PROD_RE_Pronoun);
                    return EnglishArticles.getPronoun(caseType, number, gender);
                } else if (Languages.isGreek(getLanguage())) {
                    setProducedRE(slot, SurfaceRealization.PROD_RE_NULL);
                    return "";
                }
            } else if (retype.compareTo(XmlMsgs.REF_DEMONSTRATIVE) == 0) {
                if (Languages.isEnglish(getLanguage())) {
                    setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return "this" + " " + text;
                } else if (Languages.isGreek(getLanguage())) {
                    setProducedRE(slot, SurfaceRealization.PROD_RE_NP);
                    return GreekArticles.getPronoun(gender, number, caseType, true) + " " + text;
                }
            } else {
                setProducedRE(slot, SurfaceRealization.PROD_RE_BAD);
                return "ERROR";
            }
        }

        return "GREforNotAnonEntity did not procused an appropriate referring expression";
    }

    public AnnotatedDescription getAnnotatedDescription() {
        return this.annotatedDescription;
    }

    // the above functions mark the Slot
    // with infomration about the produced re from Surface Realization
    // and the role of the re (owner, filler)
    //
    private void setProducedRE(Node slot, String type) {
        ((Element) slot).setAttribute("nlowl:Produced_RE", type);

        if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.OWNER_TAG)) {
            setREROLE(slot, XmlMsgs.OWNER_TAG);
        } else {
            setREROLE(slot, XmlMsgs.FILLER_TAG);
        }
    }

    private String getProducedRE(Node Slot) {
        if (Slot.getAttributes() != null) {
            Node nd = Slot.getAttributes().getNamedItem("nlowl:Produced_RE");
            if (nd != null) {
                return nd.getTextContent();
            }
        }

        return "NOT_FOUND_RE";
    }

    private String getRERole(Node Slot) {
        if (Slot.getAttributes() != null) {
            Node nd = Slot.getAttributes().getNamedItem("nlowl:role");

            if (nd != null) {
                return nd.getTextContent();
            }
        }

        return "NOT_FOUND_RE_ROLE";
    }

    private void setREROLE(Node Slot, String type) {
        ((Element) Slot).setAttribute("nlowl:role", type);
    }

    private String getForProperty(Node Slot) {
        return XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, "forProperty");
    }

    private String getInterest(Node Slot) {
        return XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, "interest");
    }

    private String getAssimilation(Node Slot) {
        return XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE);
    }

    private String getRef(Node Slot) {
        return XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, XmlMsgs.REF);
    }

    private String getPrep(Node Slot) {
        return XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, "Prep");
    }

    public void setSectionParagraphs(boolean sectionParagraphs) {
        this.sectionParagraphs = sectionParagraphs;
    }

    public boolean isGenerateReferringExpressions() {
        return generateReferringExpressions;
    }

    public void setGenerateReferringExpressions(boolean generateReferringExpressions) {
        this.generateReferringExpressions = generateReferringExpressions;
    }

    public boolean isAnnotateGeneratedResources() {
        return annotateGeneratedResources;
    }

    public void setAnnotateGeneratedResources(boolean annotateGeneratedResources) {
        this.annotateGeneratedResources = annotateGeneratedResources;
    }
}