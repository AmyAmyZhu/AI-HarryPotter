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
import java.util.Collections;

import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.NLNArticleSlot;
import gr.aueb.cs.nlg.NLFiles.NLNNounSlot;
import gr.aueb.cs.nlg.NLFiles.NLNSlot;
import gr.aueb.cs.nlg.NLFiles.NLNameQueryManager;
import gr.aueb.cs.nlg.NLFiles.SPAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.SPFillerSlot;
import gr.aueb.cs.nlg.NLFiles.SPPrepositionSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SPVerbSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;
import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounGR;
import gr.aueb.cs.nlg.NLFiles.LexiconQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.SPOwnerSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlan;
import gr.aueb.cs.nlg.Utils.Fact;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.util.HashSet;
import org.semanticweb.owlapi.model.IRI;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Aggregation extends NLGEngineComponent {

    private int maxMessagesPerSentence = 3;
    private int maxSlotsPerSentence = 10;
    private NLNameQueryManager NLNQM;
    private SentencePlanQueryManager SPQM;
    private LexiconQueryManager LQM;
    public final static String GREEK_MASCULINE_RELATIVE_PRONOUN = "ο οποίος";
    public final static String GREEK_FEMININE_RELATIVE_PRONOUN = "η οποία";
    public final static String GREEK_NEUTER_RELATIVE_PRONOUN = "το οποίο";
    public final static String GREEK_GENDER_INSENSITIVE_PRONOUN = "που";
    public final static String ENGLISH_RELATIVE_PRONOUN_WHICH = "which";
    public final static String ENGLISH_RELATIVE_PRONOUN_THAT = "that";
    public final static String AGGREGATE_NLNAME = "AGGREGATE_NLNAME";
    public final static String GREEK_CONNECTIVE = "και";
    public final static String ENGLISH_CONNECTIVE = "and";
    public final static String GREEK_DISJUNCTIVE = "ή";
    public final static String ENGLISH_DISJUNCTIVE = "or";
    public final static String GREEK_INCLUDING = "συμπεριλαμβανομένων";
    public final static String ENGLISH_INCLUDING = "including";
    public final static String GREEK_BETWEEN = "μεταξύ";
    public final static String ENGLISH_BETWEEN = "between";
    public final static String COMMA = ",";
    public final static String COLON = ":";
    private int overrideMaxMessagesPerSentence = -1;

    // constructor		
    public Aggregation(SentencePlanQueryManager SPQM, NLNameQueryManager NLNQM, LexiconQueryManager LQM, String Language) {
        super(Language);
        this.SPQM = SPQM;
        this.NLNQM = NLNQM;
        this.LQM = LQM;
    }

    public XmlMsgs aggregate(XmlMsgs messages, boolean enforceILPDecisions, boolean useSlotLimit) {
        Node root = messages.getRoot();
        ArrayList<Node> msgs = messages.getMessages();
        //remove msgs from xml tree
        messages.removeMsgs(msgs);

        //aggregate msgs using the aggregation rules
        aggregate(messages.getXMLTree(), msgs, root, enforceILPDecisions, useSlotLimit);

        return messages;
    }//Aggregate

    private void aggregate(Document doc, ArrayList<Node> messageList, Node messageRoot, boolean enforceILPDecisions, boolean useSlotLimit) {
        int i = 0;

        int limit = 0;
        if (!useSlotLimit) {
            if (overrideMaxMessagesPerSentence == -1) {
                limit = maxMessagesPerSentence;
            } else {
                limit = overrideMaxMessagesPerSentence;
            }
        } else {
            limit = maxSlotsPerSentence;
        }

        for (int j = 0; j < messageList.size(); j++) {
            if (!useSlotLimit) {
                //We set node user date for the number of facts contained in the message
                messageList.get(j).setUserData(XmlMsgs.factsContained, 1 + "", null);
            } else {
                //We set node user date for the number of slots contained in the message                
                HashSet<String> currentSlots = getUniqueSlots(messageList.get(j));
                messageList.get(j).setUserData(XmlMsgs.factsContained, currentSlots.size() + "", null);
            }
        }

        //UNION
        i = 0;
        while (i < messageList.size()) {
            Node currentMessage = messageList.get(i);

            String connectiveCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective);
            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String valueCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.VALUE);
            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if (connectiveCurrent.equals("false")) {
                int j = 0;
                while (j < messageList.size()) {
                    Node nextMessage = messageList.get(j);

                    String connectiveNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective);
                    String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);
                    String valueNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.VALUE);
                    String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                        if (connectiveNext.equals("false")) {
                            if (ownerCurrent.equals(ownerNext) && !valueCurrent.equals(valueNext)) {
                                if (XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.forProperty))) {
                                    boolean isComplete = applyUnion(doc, currentMessage, nextMessage);

                                    if (isComplete) {
                                        String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                        if (focusLostNext.equals("true")) {
                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                        }
                                        messageList.remove(nextMessage);
                                        recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                        j--;
                                        if (i >= j) {
                                            i--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    j++;
                }
            }

            i++;
        }//UNION

        //Avoid repeating a noun with multiple adjectives
        //RULE - Same O, Same P, Multiple Fs repeating a noun with same adjectives -> <O, P, and (A...A) N>
        i = 0;
        while (i < messageList.size()) {
            Node currentMessage = messageList.get(i);

            if (!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) {
                if (sharedNounWithDifferentAdjectivesInNLName(currentMessage)) {
                    applyRuleForSharedNounWithDifferentAdjectivesInNLName(doc, currentMessage, true);
                }
            }

            i++;
        }//RULE - Same O, Same P, Multiple Fs repeating a noun with same adjectives -> <O, P, and (A...A) N>

        //Remove min cardinallity if more than MIN (someValuesFrom) fillers for the corresponding property exist
        //RULE - Same O, Same P, MinCard(P), #someValuesFrom(P) > MinCard
        i = 0;
        while (i < messageList.size()) {
            Node minMessage = messageList.get(i);
            String forProperty;
            String forOwner;
            String ILPSentenceValue;
            String ILPSentenceMin;

            if (XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                forProperty = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.forProperty);
                forOwner = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.REF);
                ILPSentenceMin = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                int j = 0;
                while (j < messageList.size()) {
                    Node valueMessage = messageList.get(j);

                    ILPSentenceValue = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceMin.equals(ILPSentenceValue)) || !enforceILPDecisions) {
                        if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty)
                                && (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.REF).equals(forOwner))) {
                            if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).isEmpty() || XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                                ArrayList<Node> currentObjects = getFillers(valueMessage);

                                int cardinality = Integer.parseInt(XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.cardinality));

                                //If the fillers of the property are more than the cardinality
                                if (currentObjects.size() > cardinality) {
                                    //Remove the cardinality fact
                                    messageList.remove(minMessage);
                                    i--;
                                }
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }//RULE - Same O, Same P, MinCard(P), #someValuesFrom(P) > MinCard

        //Cardinality Restriction and Values
        //RULE - Same O, Same P, MinCard(P) - MaxCard(P), Fs 
        i = 0;
        while (i < messageList.size()) {
            Node maxMessage = messageList.get(i);
            String forProperty;
            String forOwner;
            String ILPSentenceMax;
            String ILPSentenceMin;

            if (XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                forProperty = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.forProperty);
                forOwner = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.REF);
                ILPSentenceMax = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                int j = 0;
                while (j < messageList.size()) {
                    Node minMessage = messageList.get(j);

                    ILPSentenceMin = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceMax.equals(ILPSentenceMin)) || !enforceILPDecisions) {
                        if ((XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG))
                                && (XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty))
                                && (XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.REF).equals(forOwner))) {
                            ArrayList<Node> currentObjects = new ArrayList<Node>();
                            int k = 0;
                            while (k < messageList.size()) {
                                Node valueMessage = messageList.get(k);
                                if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty)) {
                                    if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).isEmpty()) {
                                        currentObjects = getFillers(valueMessage);

                                        //ALL RULES CONTAINING MAX, MIN AND VALUES
                                        boolean completed = applyRuleMinMaxCardinality(doc, maxMessage, minMessage, currentObjects);

                                        if (completed) {
                                            //REMOVE MIN MESSAGE
                                            messageList.remove(minMessage);

                                            j--;
                                            if (i >= j) {
                                                i--;
                                            }
                                            if (k >= j) {
                                                k--;
                                            }

                                            String focusLostNext = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                            if (focusLostNext.equals("true")) {
                                                ((Element) maxMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                            }
                                            //REMOVE VALUES MESSAGE
                                            messageList.remove(valueMessage);

                                            k--;
                                            if (i >= k) {
                                                i--;
                                            }
                                            if (j >= k) {
                                                i--;
                                            }
                                        }
                                    }
                                }
                                k++;
                            }
                            if (currentObjects.isEmpty()) {
                                //RULE CONTAINING MAX, MIN IF VALUE = 0
                                boolean completed = applyRuleMinMaxCardinality(doc, maxMessage, minMessage, currentObjects);

                                if (completed) {
                                    //REMOVE MIN MESSAGE
                                    messageList.remove(minMessage);

                                    j--;
                                    if (i >= j) {
                                        i--;
                                    }
                                    if (k >= j) {
                                        k--;
                                    }
                                }
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }//RULE - Same O, Same P, MinCard(P) - MaxCard(P), Fs 

        //RULE - Same O, Same P, ExactCard(P), Fs 
        i = 0;
        while (i < messageList.size()) {
            Node exactMessage = messageList.get(i);
            String forProperty;
            String forOwner;
            String ILPSentenceExact;
            String ILPSentenceValue;

            if (XmlMsgs.getAttribute(exactMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                forProperty = XmlMsgs.getAttribute(exactMessage, XmlMsgs.prefix, XmlMsgs.forProperty);
                forOwner = XmlMsgs.getAttribute(exactMessage, XmlMsgs.prefix, XmlMsgs.REF);
                ILPSentenceExact = XmlMsgs.getAttribute(exactMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                int j = 0;
                while (j < messageList.size()) {
                    Node valueMessage = messageList.get(j);

                    ILPSentenceValue = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceExact.equals(ILPSentenceValue)) || !enforceILPDecisions) {
                        if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty)
                                && (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.REF).equals(forOwner))) {
                            if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).isEmpty()) {
                                ArrayList<Node> currentObjects = getFillers(valueMessage);

                                //ALL RULES CONTAINING EXACT AND VALUES
                                boolean completed = applyRuleExactCardinality(doc, exactMessage, currentObjects);

                                if (completed) {
                                    String focusLostNext = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                    if (focusLostNext.equals("true")) {
                                        ((Element) exactMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                    }
                                    //REMOVE VALUES MESSAGE
                                    messageList.remove(valueMessage);

                                    j--;
                                    if (i >= j) {
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }//RULE - Same O, Same P, ExactCard(P), Fs 

        //RULE - Same O, Same P, MaxCard(P), Fs 
        i = 0;
        while (i < messageList.size()) {
            Node maxMessage = messageList.get(i);
            String forProperty;
            String forOwner;
            String ILPSentenceMax;
            String ILPSentenceValue;

            if (XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                forProperty = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.forProperty);
                forOwner = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.REF);
                ILPSentenceMax = XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                int j = 0;
                while (j < messageList.size()) {
                    Node valueMessage = messageList.get(j);

                    ILPSentenceValue = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceMax.equals(ILPSentenceValue)) || !enforceILPDecisions) {
                        if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty)
                                && (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.REF).equals(forOwner))) {
                            if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).isEmpty()) {
                                ArrayList<Node> currentObjects = getFillers(valueMessage);

                                //ALL RULES CONTAINING MAX AND VALUES
                                boolean completed = applyRuleMaxCardinality(doc, maxMessage, currentObjects);

                                if (completed) {
                                    String focusLostNext = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                    if (focusLostNext.equals("true")) {
                                        ((Element) maxMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                    }
                                    //REMOVE VALUES MESSAGE
                                    messageList.remove(valueMessage);

                                    j--;
                                    if (i >= j) {
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }//RULE - Same O, Same P, MaxCard(P), Fs 

        //RULE - Same O, Same P, MinCard(P), Fs 
        i = 0;
        while (i < messageList.size()) {
            Node minMessage = messageList.get(i);
            String forProperty;
            String forOwner;
            String ILPSentenceMin;
            String ILPSentenceValue;

            if (XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                forProperty = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.forProperty);
                forOwner = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.REF);
                ILPSentenceMin = XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                int j = 0;
                while (j < messageList.size()) {
                    Node valueMessage = messageList.get(j);

                    ILPSentenceValue = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceMin.equals(ILPSentenceValue)) || !enforceILPDecisions) {
                        if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.forProperty).equals(forProperty)
                                && (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.REF).equals(forOwner))) {
                            if (XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).isEmpty() || XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) || XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                                ArrayList<Node> currentObjects = getFillers(valueMessage);

                                //ALL RULES CONTAINING MIN AND VALUES
                                boolean completed = applyRuleMinCardinality(doc, minMessage, currentObjects);

                                if (completed) {
                                    String focusLostNext = XmlMsgs.getAttribute(valueMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                    if (focusLostNext.equals("true")) {
                                        ((Element) minMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                    }
                                    //REMOVE VALUES MESSAGE
                                    messageList.remove(valueMessage);

                                    j--;
                                    if (i >= j) {
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                    j++;
                }
            }
            i++;
        }//RULE - Same O, Same P, MinCard(P), Fs 

        //Class and Second-Level Class
        //RULE - <S1, [instanceOf, isA], S2> + <S2, [instanceOf, isA], S3>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            IRI domainIndependentPropertyCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));
            IRI domainIndependentPropertyNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));

            String levelCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.LEVEL);
            String levelNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.LEVEL);

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
                if (!polarityCurrent.equals("false")) {
                    int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                    HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                    currentSlots.addAll(getUniqueSlots(nextMessage));

                    if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                        if (levelCurrent.equals("1") && levelNext.equals("2")) {
                            if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                    if (modifierCurrent.isEmpty() && modifierNext.isEmpty()) {
                                        if (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()) && domainIndependentPropertyNext.equals(NLResourceManager.isA.getIRI())) {
                                            if (currentFacts > 1) {
                                                Element textNode = XmlMsgs.createTextNode(doc);
                                                textNode.setTextContent(XmlMsgs.CONNECTIVE);
                                                currentMessage.appendChild(textNode);
                                            }

                                            applyRuleForClassAndSecondLevelClass(doc, currentMessage, nextMessage);

                                            if (!useSlotLimit) {
                                                currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                            } else {
                                                currentFacts = currentSlots.size();
                                            }
                                            currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                            String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                            if (focusLostNext.equals("true")) {
                                                ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                            }
                                            messageList.remove(i + 1);
                                            recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                            i--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S1, [instanceOf, isA], S2> + <S2, [instanceOf, isA], S3>

        //Class and Passive Sentence
        //RULE - <S, [instanceOf, isA], O1> + <S, P, O2>
        i = 0;
        int lastMessageAggregated = -1;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);

            String caseOwnerCurrent = "caseCurrent";
            SentencePlan planCurrent = SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
            for (SPSlot slot : planCurrent.getSlotsList()) {
                if (slot instanceof SPOwnerSlot) {
                    caseOwnerCurrent = ((SPOwnerSlot) slot).getCase();
                }
            }
            String caseOwnerNext = "caseNext";
            SentencePlan planNext = SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
            for (SPSlot slot : planNext.getSlotsList()) {
                if (slot instanceof SPOwnerSlot) {
                    caseOwnerNext = ((SPOwnerSlot) slot).getCase();
                }
            }

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
            String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            if (sectionCurrent.equals("0")) {
                sectionCurrent = sectionNext;
            }

            IRI domainIndependentPropertyCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));
            IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
                if (!polarityCurrent.equals("false")) {
                    int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                    HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                    currentSlots.addAll(getUniqueSlots(nextMessage));

                    if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                        if (ownerCurrent.equals(ownerNext)) {
                            if (sectionCurrent.equals(sectionNext)) {
                                if (caseOwnerCurrent.equals(caseOwnerNext)) {
                                    if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                        if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                            if (modifierCurrent.isEmpty() && modifierNext.isEmpty()) {
                                                if ((domainIndependentPropertyCurrent.equals(NLResourceManager.isA.getIRI()) || (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()))) && isPassiveSentencePlan(sentencePlanNext)) {
                                                    if (currentFacts > 1 && lastMessageAggregated == i) {
                                                        Element textNode = XmlMsgs.createTextNode(doc);
                                                        textNode.setTextContent(XmlMsgs.CONNECTIVE);
                                                        currentMessage.appendChild(textNode);
                                                    } else {
                                                        if (Languages.isEnglish(getLanguage())) {
                                                            Element textNode = XmlMsgs.createTextNode(doc);
                                                            textNode.setTextContent(Aggregation.COMMA);
                                                            currentMessage.appendChild(textNode);
                                                        }
                                                    }

                                                    applyRuleForClassAndPassiveSentence(doc, currentMessage, nextMessage);

                                                    if (!useSlotLimit) {
                                                        currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                                    } else {
                                                        currentFacts = currentSlots.size();
                                                    }
                                                    currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG, sectionCurrent + "");
                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_NAME, XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_NAME));

                                                    String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                                    if (focusLostNext.equals("true")) {
                                                        ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                                    }
                                                    messageList.remove(i + 1);
                                                    recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                                    lastMessageAggregated = i;
                                                    i--;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S, [instanceOf, isA], O1> + <S, P, O2>

        //Class and Prepositional Phrase
        //RULE - <S, [instanceOf, isA], O1> + <S, (...) toBe + Prep (...), O2>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
            String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            if (sectionCurrent.equals("0")) {
                sectionCurrent = sectionNext;
            }

            IRI domainIndependentPropertyCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));
            IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
                if (!polarityCurrent.equals("false")) {
                    int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                    HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                    currentSlots.addAll(getUniqueSlots(nextMessage));

                    if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                        if (ownerCurrent.equals(ownerNext)) {
                            if (sectionCurrent.equals(sectionNext)) {
                                if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                    if (modifierCurrent.isEmpty() && modifierNext.isEmpty()) {
                                        if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                            if ((domainIndependentPropertyCurrent.equals(NLResourceManager.isA.getIRI()) || (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()))) && isPrepositionalPhraseSentencePlan(sentencePlanNext)) {
                                                if (currentFacts > 1) {
                                                    Element textNode = XmlMsgs.createTextNode(doc);
                                                    textNode.setTextContent(XmlMsgs.CONNECTIVE);
                                                    currentMessage.appendChild(textNode);
                                                }

                                                applyRuleForClassAndPrepositionalPhrase(currentMessage, nextMessage);

                                                if (!useSlotLimit) {
                                                    currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                                } else {
                                                    currentFacts = currentSlots.size();
                                                }
                                                currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                                ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG, sectionCurrent + "");
                                                ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_NAME, XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_NAME));

                                                String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                                if (focusLostNext.equals("true")) {
                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                                }
                                                messageList.remove(i + 1);
                                                recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                                i--;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S, [instanceOf, isA], O1> + <S, (...) toBe + Prep (...), O2>

        //Class and multiple adjectives
        //RULE - <S, [instanceOf, isA], O1> + <S, toBe + Adj, O2>
        i = 0;
        while (i < messageList.size()) {
            Node currentMessage = messageList.get(i);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            IRI domainIndependentPropertyCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));

            String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            String sectionImmediateNext = "";
            String orderImmediateNext = "";
            if (i + 1 < messageList.size()) {
                sectionImmediateNext = XmlMsgs.getAttribute(messageList.get(i + 1), XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
                orderImmediateNext = XmlMsgs.getAttribute(messageList.get(i + 1), XmlMsgs.prefix, XmlMsgs.ORDER_TAG);
            }
            String sectionImmediateBefore = "";
            String orderImmediateBefore = "";
            if (i - 1 > 0) {
                sectionImmediateBefore = XmlMsgs.getAttribute(messageList.get(i - 1), XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
                orderImmediateBefore = XmlMsgs.getAttribute(messageList.get(i - 1), XmlMsgs.prefix, XmlMsgs.ORDER_TAG);
            }

            if (!polarityCurrent.equals("false")) {
                int j = 0;
                while (j < messageList.size()) {
                    Node nextMessage = messageList.get(j);

                    //MUST BE IN THE IMMEDIATELY NEXT OR PREVIOUS TO THE MESSAGE
                    //WE INTERPRET THIS AS HAVING THE SAME ORDER/SECTION WITH IMMEDIATELY PREVIOUS/NEXT MESSAGES
                    String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
                    String orderNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ORDER_TAG);

                    boolean isImmediate = false;
                    if ((sectionNext.equals(sectionImmediateNext)) && (orderNext.equals(orderImmediateNext))) {
                        isImmediate = true;
                        if (!sectionCurrent.equals("0")) {
                            if (!sectionCurrent.equals(sectionImmediateNext)) {
                                isImmediate = false;
                            }
                        } else {
                            sectionCurrent = sectionImmediateNext;
                        }
                    }
                    if ((sectionNext.equals(sectionImmediateBefore)) && (orderNext.equals(orderImmediateBefore))) {
                        isImmediate = true;
                        if (!sectionCurrent.equals("0")) {
                            if (!sectionCurrent.equals(sectionImmediateBefore)) {
                                isImmediate = false;
                            }
                        } else {
                            sectionCurrent = sectionImmediateBefore;
                        }
                    }

                    String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
                    String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

                    if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || (!enforceILPDecisions && isImmediate)) {
                        String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);
                        String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
                        String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);
                        IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

                        ArrayList<IRI> fillerNLNamesNext = new ArrayList<IRI>();
                        ArrayList<Node> nextFillers = getFillers(nextMessage);
                        if (!nextFillers.isEmpty()) {
                            for (Node object : nextFillers) {
                                fillerNLNamesNext.add(IRI.create(XmlMsgs.getAttribute(object, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG)));
                            }
                        }

                        int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                        HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                        currentSlots.addAll(getUniqueSlots(nextMessage));

                        if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                            //           if (sectionCurrent.equals(sectionNext)) {
                            if (ownerCurrent.equals(ownerNext)) {
                                if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                    if (modifierCurrent.isEmpty() && modifierNext.isEmpty()) {
                                        if ((domainIndependentPropertyCurrent.equals(NLResourceManager.isA.getIRI()) || (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()))) && isAdjectivePhraseSentencePlan(sentencePlanNext)) {

                                            applyRuleForClassAndAdjective(currentMessage, nextMessage);

                                            if (!useSlotLimit) {
                                                currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                            } else {
                                                currentFacts = currentSlots.size();
                                            }
                                            currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG, sectionCurrent + "");
                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_NAME, XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_NAME));

                                            String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                            if (focusLostNext.equals("true")) {
                                                ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                            }
                                            //REMOVE VALUES MESSAGE
                                            messageList.remove(nextMessage);
                                            recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                            j--;
                                            if (i >= j) {
                                                i--;
                                            }
                                        } else if ((domainIndependentPropertyCurrent.equals(NLResourceManager.isA.getIRI()) || (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()))) && isAdjectivePhraseSentencePlan(sentencePlanNext, fillerNLNamesNext)) {

                                            if (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) {
                                                applyRuleForClassAndAdjective(currentMessage, fillerNLNamesNext, true);
                                            } else {
                                                applyRuleForClassAndAdjective(currentMessage, fillerNLNamesNext, false);
                                            }

                                            if (!useSlotLimit) {
                                                currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                            } else {
                                                currentFacts = currentSlots.size();
                                            }
                                            currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG, sectionCurrent + "");
                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_NAME, XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_NAME));

                                            String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                            if (focusLostNext.equals("true")) {
                                                ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                            }
                                            //REMOVE VALUES MESSAGE
                                            messageList.remove(nextMessage);
                                            recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                            j--;
                                            if (i >= j) {
                                                i--;
                                            }
                                        }
                                    }
                                }
                                //    }
                            }
                        }
                    }
                    j++;
                }
            }

            i++;
        }//RULE - <S, [instanceOf, isA], O1> + <S, toBe + Adj, O2>

        //Class and Active Sentence
        //RULE - <S, [instanceOf, isA], O1> + <S, A, O2>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
            String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            if (sectionCurrent.equals("0")) {
                sectionCurrent = sectionNext;
            }

            IRI domainIndependentPropertyCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty));
            IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.FILLER_TAG);
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.OWNER_TAG);

            String caseOwnerCurrent = "caseCurrent";
            SentencePlan planCurrent = SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
            for (SPSlot slot : planCurrent.getSlotsList()) {
                if (slot instanceof SPOwnerSlot) {
                    caseOwnerCurrent = ((SPOwnerSlot) slot).getCase();
                }
            }
            String caseOwnerNext = "caseNext";
            SentencePlan planNext = SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
            for (SPSlot slot : planNext.getSlotsList()) {
                if (slot instanceof SPOwnerSlot) {
                    caseOwnerNext = ((SPOwnerSlot) slot).getCase();
                }
            }

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
                if (!polarityCurrent.equals("false")) {
                    int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                    HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                    currentSlots.addAll(getUniqueSlots(nextMessage));

                    if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                        if (sectionCurrent.equals(sectionNext)) {
                            if (ownerCurrent.equals(ownerNext)) {
                                if (caseOwnerCurrent.equals(caseOwnerNext)) {
                                    if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                        if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                            if (modifierCurrent.isEmpty() && modifierNext.isEmpty()) {
                                                if ((domainIndependentPropertyCurrent.equals(NLResourceManager.isA.getIRI()) || (domainIndependentPropertyCurrent.equals(NLResourceManager.instanceOf.getIRI()))) && isActiveSentencePlan(sentencePlanNext) && isActiveMessage(currentMessage)) {
                                                    if (currentFacts > 1) {
                                                        Element textNode = XmlMsgs.createTextNode(doc);
                                                        textNode.setTextContent(XmlMsgs.CONNECTIVE);
                                                        currentMessage.appendChild(textNode);
                                                    }

                                                    applyRuleForClassAndActiveSentence(doc, currentMessage, nextMessage);

                                                    if (!useSlotLimit) {
                                                        currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                                    } else {
                                                        currentFacts = currentSlots.size();
                                                    }
                                                    currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG, sectionCurrent + "");
                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.SECTION_NAME, XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_NAME));

                                                    String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                                    if (focusLostNext.equals("true")) {
                                                        ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                                    }
                                                    messageList.remove(i + 1);
                                                    recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                                    i--;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S, [instanceOf, isA], O1> + <S, A, O2>

        //Same verb conjunction
        //RULE - <S, P1(V), O1> + <S, P2(V), O2>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
            String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
            String polarityNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.polarity);

            if (polarityCurrent.trim().isEmpty()) {
                polarityCurrent = "true";
            }
            if (polarityNext.trim().isEmpty()) {
                polarityNext = "true";
            }

            IRI sentencePlanCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));
            IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                currentSlots.addAll(getUniqueSlots(nextMessage));

                if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                    if (sectionCurrent.equals(sectionNext)) {
                        if (ownerCurrent.equals(ownerNext)) {
                            if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                if (polarityCurrent.equals(polarityNext)) {
                                    if ((modifierCurrent.isEmpty() || modifierCurrent.equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) || modifierCurrent.equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) && (modifierNext.isEmpty() || modifierNext.equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) || modifierNext.equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG))) {
                                        if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                            if (containSameVerb(sentencePlanCurrent, sentencePlanNext)) {
                                                applyRuleForSameVerbConjunctionDisjunction(doc, currentMessage, nextMessage, true);

                                                if (!useSlotLimit) {
                                                    currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                                } else {
                                                    currentFacts = currentSlots.size();
                                                }
                                                currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                                String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                                if (focusLostNext.equals("true")) {
                                                    ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                                }
                                                messageList.remove(i + 1);
                                                recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                                i--;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S, P1(V), O1> + <S, P2(V), O2>

        //Different verb conjunction
        //RULE - <S, P1, O1> + <S, P2, O2>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String sectionCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);
            String sectionNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SECTION_TAG);

            String ownerCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.REF);
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
            String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                currentSlots.addAll(getUniqueSlots(nextMessage));

                if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                    if (sectionCurrent.equals(sectionNext)) {
                        if (ownerCurrent.equals(ownerNext)) {
                            if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                if ((modifierCurrent.isEmpty() || modifierCurrent.equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) || modifierCurrent.equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) && (modifierNext.isEmpty() || modifierNext.equals(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG) || modifierNext.equals(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG))) {
                                    if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                        applyRuleForDifferentVerbConjunction(doc, currentMessage, nextMessage);

                                        if (!useSlotLimit) {
                                            currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                        } else {
                                            currentFacts = currentSlots.size();
                                        }
                                        currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                        String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                        if (focusLostNext.equals("true")) {
                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                        }
                                        messageList.remove(i + 1);
                                        recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                        i--;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S, P1(V), O1> + <S, P2(V), O2>

        //Fact and Second-Level Fact
        //RULE - <S1, P1, S2> + <S2, P2, S3>
        i = 0;
        while (i < messageList.size() - 1) {
            Node currentMessage = messageList.get(i);
            Node nextMessage = messageList.get(i + 1);

            String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
            String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

            String valueCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.VALUE);
            ArrayList<String> valuesCurrent = Fact.parseModifier(valueCurrent);
            if (valuesCurrent.size() == 1) {
                valueCurrent = valuesCurrent.get(0);
            }
            String ownerNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.REF);

            String levelCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.LEVEL);
            String levelNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.LEVEL);

            String ILPSentenceCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);
            String ILPSentenceNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.ILPSentence);

            if ((enforceILPDecisions && ILPSentenceCurrent.equals(ILPSentenceNext)) || !enforceILPDecisions) {
                String polarityCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.polarity);
                if (!polarityCurrent.equals("false")) {
                    int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));
                    HashSet<String> currentSlots = getUniqueSlots(currentMessage);
                    currentSlots.addAll(getUniqueSlots(nextMessage));

                    if ((!useSlotLimit && (currentFacts + Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained)) <= limit)) || (useSlotLimit && (currentSlots.size() <= limit))) {
                        if (levelCurrent.equals("1") && levelNext.equals("2")) {
                            if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
                                if ((!XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false")) && (!XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.isConnective).equals("false"))) {
                                    if (valueCurrent.equals(ownerNext) && !valueCurrent.trim().isEmpty()) {
                                        applyRuleForFactAndSecondLevelFact(doc, currentMessage, nextMessage);

                                        if (!useSlotLimit) {
                                            currentFacts += Integer.parseInt((String) nextMessage.getUserData(XmlMsgs.factsContained));
                                        } else {
                                            currentFacts = currentSlots.size();
                                        }
                                        currentMessage.setUserData(XmlMsgs.factsContained, currentFacts + "", null);

                                        String focusLostNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST);
                                        if (focusLostNext.equals("true")) {
                                            ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FOCUS_LOST, "true");
                                        }
                                        ((Element) currentMessage).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.LEVEL, levelNext);

                                        messageList.remove(i + 1);
                                        recalculateAgreeWithAfterRemove(currentMessage, nextMessage);

                                        i--;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            i++;
        }//RULE - <S1, P1, S2> + <S2, P2, S3>

        //Replacing conjunction and disjunctions with appropriate words
        for (int a1 = 0; a1 < messageList.size(); a1++) {
            ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(messageList.get(a1));
            boolean isLast = true;
            boolean isLastSecondLevel = true;

            int count = 0;
            boolean connectiveNodesBetween = false;
            Node currentLastSecondLevelNode = null;
            Node currentLastNode = null;

            for (int a2 = messageSlots.size() - 1; a2 >= 0; a2--) {
                if (messageSlots.get(a2).hasChildNodes()) {
                    ArrayList<Node> currentSlotChildren = XmlMsgs.returnChildNodes(messageSlots.get(a2));
                    for (int a3 = currentSlotChildren.size() - 1; a3 >= 0; a3--) {
                        if (currentSlotChildren.get(a3).getTextContent().equals(XmlMsgs.CONNECTIVE)) {
                            if (isLast) {
                                currentLastNode = currentSlotChildren.get(a3);
                                if (Languages.isEnglish(getLanguage())) {
                                    currentSlotChildren.get(a3).setTextContent(Aggregation.ENGLISH_CONNECTIVE);
                                } else if (Languages.isGreek(getLanguage())) {
                                    currentSlotChildren.get(a3).setTextContent(Aggregation.GREEK_CONNECTIVE);
                                }

                                connectiveNodesBetween = false;
                                isLast = false;
                            } else {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.COMMA);
                                connectiveNodesBetween = true;
                            }
                            isLastSecondLevel = true;
                        } else if (currentSlotChildren.get(a3).getTextContent().equals(XmlMsgs.CONNECTIVE_2ND_LEVEL)) {
                            if (isLastSecondLevel) {
                                currentLastSecondLevelNode = currentSlotChildren.get(a3);
                                if (Languages.isEnglish(getLanguage())) {
                                    currentSlotChildren.get(a3).setTextContent(Aggregation.ENGLISH_CONNECTIVE);
                                } else if (Languages.isGreek(getLanguage())) {
                                    currentSlotChildren.get(a3).setTextContent(Aggregation.GREEK_CONNECTIVE);
                                }

                                isLastSecondLevel = false;
                            } else {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.COMMA);
                                connectiveNodesBetween = true;
                            }
                            count++;

                            if (Languages.isEnglish(getLanguage())) {
                                if (count > 2 && currentLastSecondLevelNode != null) {
                                    currentLastSecondLevelNode.setTextContent(Aggregation.COMMA + " " + currentLastSecondLevelNode.getTextContent());
                                    currentLastSecondLevelNode = null;
                                }
                            }

                            if (!connectiveNodesBetween && currentLastNode != null) {
                                currentLastNode.setTextContent(Aggregation.COMMA + " " + currentLastSecondLevelNode.getTextContent());
                                currentLastNode = null;
                            }
                        } else if (currentSlotChildren.get(a3).getTextContent().equals(XmlMsgs.DISJUNCTIVE)) {
                            if (Languages.isEnglish(getLanguage())) {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.ENGLISH_DISJUNCTIVE);
                            } else if (Languages.isGreek(getLanguage())) {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.GREEK_DISJUNCTIVE);
                            }
                            connectiveNodesBetween = true;
                            isLastSecondLevel = true;
                        } else if (currentSlotChildren.get(a3).getTextContent().equals(XmlMsgs.DISJUNCTIVE_2ND_LEVEL)) {
                            if (Languages.isEnglish(getLanguage())) {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.ENGLISH_DISJUNCTIVE);
                            } else if (Languages.isGreek(getLanguage())) {
                                currentSlotChildren.get(a3).setTextContent(Aggregation.GREEK_DISJUNCTIVE);
                            }
                            connectiveNodesBetween = true;
                        }
                    }
                }
            }
        }

        for (Node message : messageList) {
            messageRoot.appendChild(message);
        }
    }

    public HashSet<String> getUniqueSlots(Node message) {
        HashSet<String> slotStrings = new HashSet<String>();
        for (SPSlot slot : SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))).getSlotsList()) {
            if (slot instanceof SPOwnerSlot) {
                slotStrings.add(slot.toString() + XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.forSubject));
            } else if (slot instanceof SPFillerSlot) {
                slotStrings.add(slot.toString() + XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.forObject));
            } else {
                slotStrings.add(slot.toString());
            }
        }
        return slotStrings;
    }

    public boolean sharedSubjectPredicate(Node currentMessage, Node nextMessage) {
        Node currentSubject = getOwner(currentMessage);
        Node nextSubject = getOwner(nextMessage);

        if (currentSubject == null || nextSubject == null) {
            return false;
        }

        String currentCase = XmlMsgs.getAttribute(currentSubject, XmlMsgs.prefix, XmlMsgs.CASE_TAG);
        String nextCase = XmlMsgs.getAttribute(nextSubject, XmlMsgs.prefix, XmlMsgs.CASE_TAG);

        if (!currentCase.equals(nextCase)) {
            return false;
        }

        String currentPredicate = XmlMsgs.getAttribute(currentSubject, XmlMsgs.prefix, XmlMsgs.forProperty);
        String nextPredicate = XmlMsgs.getAttribute(nextSubject, XmlMsgs.prefix, XmlMsgs.forProperty);

        if (!currentPredicate.equals(nextPredicate)) {
            return false;
        }

        return true;
    }

    public boolean sharedNounWithDifferentAdjectivesInNLName(Node currentMessage) {
        ArrayList<Node> currentObjects = getFillers(currentMessage);

        if (currentObjects.isEmpty()) {
            return false;
        }

        IRI noun = null;
        if (currentObjects.size() > 1) {
            for (Node object : currentObjects) {
                NLName name = NLNQM.getNLName(IRI.create(XmlMsgs.getAttribute(object, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG)));

                boolean foundAdjective = false;
                boolean foundNoun = false;

                if (name != null) {
                    for (NLNSlot slot : name.getSlotsList()) {
                        if (slot instanceof NLNArticleSlot) {
                            if (foundAdjective || foundNoun) {
                                return false;
                            }
                        } else if (slot instanceof NLNAdjectiveSlot) {
                            foundAdjective = true;
                            if (foundNoun) {
                                return false;
                            }
                        } else if (slot instanceof NLNNounSlot) {
                            foundNoun = true;
                            if (!foundAdjective) {
                                return false;
                            } else if (!((NLNNounSlot) slot).isHead()) {
                                return false;
                            } else {
                                if (noun == null) {
                                    noun = ((NLNNounSlot) slot).getLexiconEntryIRI();
                                } else if (!noun.equals(((NLNNounSlot) slot).getLexiconEntryIRI())) {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean applyUnion(Document doc, Node currentMessage, Node nextMessage) {
        //Same verb conjunction
        //RULE - <S, P1(V), O1> + <S, P2(V), O2>
        String aggrAllowedCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);
        String aggrAllowedNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED);

        String modifierCurrent = XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.modifier);
        String modifierNext = XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.modifier);

        IRI sentencePlanCurrent = IRI.create(XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));
        IRI sentencePlanNext = IRI.create(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));

        if (aggrAllowedCurrent.equals("true") && aggrAllowedNext.equals("true")) {
            if (modifierCurrent.equals(modifierNext)) {
                if (containSameVerb(sentencePlanCurrent, sentencePlanNext)) {
                    applyRuleForSameVerbConjunctionDisjunction(doc, currentMessage, nextMessage, false);

                    return true;
                }
                applyRuleForSharedSubjectPredicate(doc, currentMessage, nextMessage, false);
                return true;
            }
        }
        //RULE - <S, P1(V), O1> + <S, P2(V), O2>
        Element textNode = XmlMsgs.createTextNode(doc);
        textNode.setTextContent(XmlMsgs.DISJUNCTIVE_2ND_LEVEL);
        currentMessage.appendChild(textNode);

        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node currentSlot = (Node) messageSlots.get(k);
            currentMessage.appendChild(currentSlot);
        }
        return true;
    }

    public boolean applyRuleMinMaxCardinality(Document doc, Node maxMessage, Node minMessage, ArrayList<Node> fillers) {
        int maxCardinality = Integer.parseInt(XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.cardinality));
        int minCardinality = Integer.parseInt(XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.cardinality));

        //RULE MAX = VALUE -> EXACTLY THESE VALUES
        if (fillers.size() == maxCardinality) {
            ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(maxMessage);
            for (int i = 0; i < messageSlots.size(); i++) {
                Node currentSlot = (Node) messageSlots.get(i);

                if (XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.forProperty).startsWith("maxCardinality(")) {
                    if (Languages.isEnglish(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.EXACT_CARDINALITY_EN + " " + maxCardinality);
                    } else if (Languages.isGreek(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.EXACT_CARDINALITY_GR + " " + maxCardinality);
                    }
                }
            }

            Element textNode = XmlMsgs.createTextNode(doc);
            textNode.setTextContent(Aggregation.COLON);
            maxMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                maxMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    maxMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE MAX > VALUE, MIN < VALUE -> MAX, MIN, INCLUDING THESE VALUES
        else if ((fillers.size() < maxCardinality) && (fillers.size() > minCardinality)) {
            ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(maxMessage);
            for (int i = 0; i < messageSlots.size(); i++) {
                Node currentSlot = (Node) messageSlots.get(i);

                if (XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.forProperty).startsWith("maxCardinality(")) {
                    if (Languages.isEnglish(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.MIN_CARDINALITY_EN + " " + minCardinality + " " + Aggregation.ENGLISH_CONNECTIVE + " " + Lexicalisation.MAX_CARDINALITY_EN + " " + maxCardinality);
                    } else if (Languages.isGreek(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.MIN_CARDINALITY_GR + " " + minCardinality + " " + Aggregation.GREEK_CONNECTIVE + " " + Lexicalisation.MAX_CARDINALITY_GR + " " + maxCardinality);
                    }
                }
            }

            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_INCLUDING);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_INCLUDING);
            }
            maxMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                maxMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    maxMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE MIN = VALUE -> MAX INCLUDING THESE VALUES
        else if (fillers.size() == minCardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_INCLUDING);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_INCLUDING);
            }
            maxMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                maxMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    maxMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE VALUE = 0 -> MAX, MIN
        else if (fillers.isEmpty()) {
            ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(maxMessage);
            for (int i = 0; i < messageSlots.size(); i++) {
                Node currentSlot = (Node) messageSlots.get(i);

                if (XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.forProperty).startsWith("maxCardinality(")) {
                    if (Languages.isEnglish(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.MIN_CARDINALITY_EN + " " + minCardinality + " " + Aggregation.ENGLISH_CONNECTIVE + " " + Lexicalisation.MAX_CARDINALITY_EN + " " + maxCardinality);
                    } else if (Languages.isGreek(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.MIN_CARDINALITY_GR + " " + minCardinality + " " + Aggregation.GREEK_CONNECTIVE + " " + Lexicalisation.MAX_CARDINALITY_GR + " " + maxCardinality);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean applyRuleExactCardinality(Document doc, Node exactMessage, ArrayList<Node> fillers) {
        int cardinality = Integer.parseInt(XmlMsgs.getAttribute(exactMessage, XmlMsgs.prefix, XmlMsgs.cardinality));

        //RULE EXACT = VALUE -> EXACTLY THESE VALUES
        if (fillers.size() == cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            textNode.setTextContent(Aggregation.COLON);
            exactMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                exactMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    exactMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE EXACT > VALUE -> EXACT INCLUDING THESE VALUES
        else if (fillers.size() < cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_INCLUDING);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_INCLUDING);
            }
            exactMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                exactMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    exactMessage.appendChild(textNode);
                }
            }
            return true;
        }
        return false;
    }

    public boolean applyRuleMaxCardinality(Document doc, Node maxMessage, ArrayList<Node> fillers) {
        int cardinality = Integer.parseInt(XmlMsgs.getAttribute(maxMessage, XmlMsgs.prefix, XmlMsgs.cardinality));

        //RULE MAX = VALUE -> EXACTLY THESE VALUES
        if (fillers.size() == cardinality) {
            ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(maxMessage);
            for (int i = 0; i < messageSlots.size(); i++) {
                Node currentSlot = (Node) messageSlots.get(i);

                if (XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.forProperty).startsWith("maxCardinality(")) {
                    if (Languages.isEnglish(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.EXACT_CARDINALITY_EN + " " + cardinality);
                    } else if (Languages.isGreek(getLanguage())) {
                        currentSlot.setTextContent(Lexicalisation.EXACT_CARDINALITY_GR + " " + cardinality);
                    }
                }
            }
            Element textNode = XmlMsgs.createTextNode(doc);
            textNode.setTextContent(Aggregation.COLON);
            maxMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                maxMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    maxMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE MAX > VALUE -> MAX INCLUDING THESE VALUES
        else if (fillers.size() < cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_INCLUDING);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_INCLUDING);
            }
            maxMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                maxMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    maxMessage.appendChild(textNode);
                }
            }
            return true;
        }
        return false;
    }

    public boolean applyRuleMinCardinality(Document doc, Node minMessage, ArrayList<Node> fillers) {
        int cardinality = Integer.parseInt(XmlMsgs.getAttribute(minMessage, XmlMsgs.prefix, XmlMsgs.cardinality));

        //RULE MIN >= VALUE -> MIN THESE VALUES
        if (fillers.size() == cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            textNode.setTextContent(Aggregation.COLON);
            minMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                minMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    minMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE MIN > VALUE -> MIN INCLUDING THESE VALUES
        else if (fillers.size() < cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_INCLUDING);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_INCLUDING);
            }
            minMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                minMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 1) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                    minMessage.appendChild(textNode);
                }
            }
            return true;
        } //RULE MIN < VALUE -> MIN AMONGST THESE VALUES
        else if (fillers.size() > cardinality) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (Languages.isEnglish(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.ENGLISH_BETWEEN);
            } else if (Languages.isGreek(getLanguage())) {
                textNode.setTextContent(Aggregation.COMMA + " " + Aggregation.GREEK_BETWEEN + " " + GreekArticles.getDefiniteArticle(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG, ""));
            }
            minMessage.appendChild(textNode);

            for (int i = 0; i < fillers.size(); i++) {
                if (Languages.isGreek(getLanguage())) {
                    ((Element) fillers.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, XmlMsgs.GENITIVE_TAG);
                }
                minMessage.appendChild(fillers.get(i));

                if (i < fillers.size() - 2) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(Aggregation.COMMA);
                    minMessage.appendChild(textNode);
                } else if (i == fillers.size() - 2) {
                    textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(XmlMsgs.DISJUNCTIVE_2ND_LEVEL);
                    minMessage.appendChild(textNode);
                }
            }
            return true;
        }
        return false;
    }

    public void applyRuleForSharedSubjectPredicate(Document doc, Node currentMessage, Node nextMessage, boolean isConjunction) {
        Element textNode = XmlMsgs.createTextNode(doc);
        if (isConjunction) {
            textNode.setTextContent(XmlMsgs.CONNECTIVE);
        } else {
            textNode.setTextContent(XmlMsgs.DISJUNCTIVE);
        }
        currentMessage.appendChild(textNode);

        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = (Node) messageSlots.get(k);

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                currentMessage.appendChild(nextSlot);
            }
        }
    }

    public void applyRuleForClassAndSecondLevelClass(Document doc, Node currentMessage, Node nextMessage) {
        boolean startAppending = false;
        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = (Node) messageSlots.get(k);

            if (startAppending) {
                currentMessage.appendChild(nextSlot);
            }

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                startAppending = true;

                Element textNode = XmlMsgs.createTextNode(doc);
                textNode.setTextContent(COMMA);
                currentMessage.appendChild(textNode);
            }
        }
    }

    public void applyRuleForFactAndSecondLevelFact(Document doc, Node currentMessage, Node nextMessage) {
        boolean startAppending = false;
        boolean alreadyHasPronoun = false;

        ArrayList<Node> messageSlots;
        String ownerGender = "";

        messageSlots = XmlMsgs.returnChildNodes(currentMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = messageSlots.get(k);
            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.TEXT_TAG)) {
                if (nextSlot.getTextContent().equals(GREEK_MASCULINE_RELATIVE_PRONOUN)
                        || nextSlot.getTextContent().equals(GREEK_FEMININE_RELATIVE_PRONOUN)
                        || nextSlot.getTextContent().equals(GREEK_NEUTER_RELATIVE_PRONOUN)
                        || nextSlot.getTextContent().equals(GREEK_GENDER_INSENSITIVE_PRONOUN)
                        || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_WHICH)
                        || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_THAT)) {
                    alreadyHasPronoun = true;
                }
            } else if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                if (Languages.isGreek(getLanguage())) {
                    String name = XmlMsgs.getAttribute(nextSlot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                    if (name.startsWith("anonymous(")) {
                        name = name.substring(10, name.length() - 1);
                    }

                    NLName nlName = NLNQM.getNLName(IRI.create(name), getLanguage());
                    if (nlName != null) {
                        for (NLNSlot slot : nlName.getSlotsList()) {
                            if (slot instanceof NLNAdjectiveSlot) {
                                if (((NLNAdjectiveSlot) slot).isHead()) {
                                    ownerGender = ((NLNAdjectiveSlot) slot).getGender();
                                }
                            } else if (slot instanceof NLNNounSlot) {
                                if (((NLNNounSlot) slot).isHead()) {
                                    LexEntryNounGR entry = (LexEntryNounGR) LQM.getEntryList(((NLNNounSlot) slot).getLexiconEntryIRI()).getEntry(getLanguage());
                                    ownerGender = entry.getGender();
                                }
                            }
                        }
                    }
                }
            }
        }

        messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = messageSlots.get(k);

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                startAppending = true;

                if (!alreadyHasPronoun) {
                    Element textNode = XmlMsgs.createTextNode(doc);
                    textNode.setTextContent(Aggregation.COMMA);
                    currentMessage.appendChild(textNode);

                    textNode = XmlMsgs.createTextNode(doc);
                    if (Languages.isGreek(getLanguage())) {
                        if (Math.random() >= 0.5) {
                            if (ownerGender.equals(XmlMsgs.GENDER_MASCULINE)) {
                                textNode.setTextContent(GREEK_MASCULINE_RELATIVE_PRONOUN);
                            } else if (ownerGender.equals(XmlMsgs.GENDER_FEMININE)) {
                                textNode.setTextContent(GREEK_FEMININE_RELATIVE_PRONOUN);
                            } else if (ownerGender.equals(XmlMsgs.GENDER_NEUTER)) {
                                textNode.setTextContent(GREEK_NEUTER_RELATIVE_PRONOUN);
                            } else {
                                textNode.setTextContent(GREEK_GENDER_INSENSITIVE_PRONOUN);
                            }
                        } else {
                            textNode.setTextContent(GREEK_GENDER_INSENSITIVE_PRONOUN);
                        }
                        currentMessage.appendChild(textNode);
                        alreadyHasPronoun = true;
                    } else if (Languages.isEnglish(getLanguage())) {
                        textNode.setTextContent(ENGLISH_RELATIVE_PRONOUN_WHICH);
                        currentMessage.appendChild(textNode);
                        alreadyHasPronoun = true;
                    }
                }
            }

            if (startAppending) {
                currentMessage.appendChild(nextSlot);
            }
        }
    }

    public void applyRuleForClassAndPassiveSentence(Document doc, Node currentMessage, Node nextMessage) {
        boolean startAppending = false;
        boolean alreadyHasPronoun = false;
        String ownerGender = "";

        ArrayList<Node> messageSlots;
        if (Languages.isGreek(getLanguage())) {
            messageSlots = XmlMsgs.returnChildNodes(currentMessage);
            for (int k = 0; k < messageSlots.size(); k++) {
                Node nextSlot = messageSlots.get(k);
                if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.TEXT_TAG)) {
                    if (nextSlot.getTextContent().equals(GREEK_MASCULINE_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_FEMININE_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_NEUTER_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_GENDER_INSENSITIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_WHICH)
                            || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_THAT)) {
                        alreadyHasPronoun = true;
                    }
                } else if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                    String name = XmlMsgs.getAttribute(nextSlot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG);
                    if (name.startsWith("anonymous(")) {
                        name = name.substring(10, name.length() - 1);
                    }

                    NLName nlName = NLNQM.getNLName(IRI.create(name), getLanguage());
                    if (nlName != null) {
                        for (NLNSlot slot : nlName.getSlotsList()) {
                            if (slot instanceof NLNAdjectiveSlot) {
                                if (((NLNAdjectiveSlot) slot).isHead()) {
                                    ownerGender = ((NLNAdjectiveSlot) slot).getGender();
                                }
                            } else if (slot instanceof NLNNounSlot) {
                                if (((NLNNounSlot) slot).isHead()) {
                                    LexEntryNounGR entry = (LexEntryNounGR) LQM.getEntryList(((NLNNounSlot) slot).getLexiconEntryIRI()).getEntry(getLanguage());
                                    ownerGender = entry.getGender();
                                }
                            }
                        }
                    }
                }
            }
        }

        messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = messageSlots.get(k);

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                ((Element) nextSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.useAuxiliaryVerb, "false");
                startAppending = true;

                if (Languages.isGreek(getLanguage())) {
                    if (!alreadyHasPronoun) {
                        Element textNode = XmlMsgs.createTextNode(doc);
                        if (Math.random() >= 0.5) {
                            if (ownerGender.equals(XmlMsgs.GENDER_MASCULINE)) {
                                textNode.setTextContent(GREEK_MASCULINE_RELATIVE_PRONOUN);
                            } else if (ownerGender.equals(XmlMsgs.GENDER_FEMININE)) {
                                textNode.setTextContent(GREEK_FEMININE_RELATIVE_PRONOUN);
                            } else if (ownerGender.equals(XmlMsgs.GENDER_NEUTER)) {
                                textNode.setTextContent(GREEK_NEUTER_RELATIVE_PRONOUN);
                            } else {
                                textNode.setTextContent(GREEK_GENDER_INSENSITIVE_PRONOUN);
                            }
                        } else {
                            textNode.setTextContent(GREEK_GENDER_INSENSITIVE_PRONOUN);
                        }
                        currentMessage.appendChild(textNode);
                    }
                }
            }

            if (startAppending) {
                currentMessage.appendChild(nextSlot);
            }
        }
    }

    public void applyRuleForClassAndActiveSentence(Document doc, Node currentMessage, Node nextMessage) {
        boolean startAppending = false;
        boolean alreadyHasPronoun = false;

        int currentFacts = Integer.parseInt((String) currentMessage.getUserData(XmlMsgs.factsContained));

        ArrayList<Node> messageSlots;
        if (Languages.isGreek(getLanguage())) {
            messageSlots = XmlMsgs.returnChildNodes(currentMessage);
            for (int k = 0; k < messageSlots.size(); k++) {
                Node nextSlot = messageSlots.get(k);
                if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.TEXT_TAG)) {
                    if (nextSlot.getTextContent().equals(GREEK_MASCULINE_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_FEMININE_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_NEUTER_RELATIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(GREEK_GENDER_INSENSITIVE_PRONOUN)
                            || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_WHICH)
                            || nextSlot.getTextContent().equals(ENGLISH_RELATIVE_PRONOUN_THAT)) {
                        alreadyHasPronoun = true;
                    }
                }
            }
        }

        messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = messageSlots.get(k);

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                startAppending = true;

                Element textNode = XmlMsgs.createTextNode(doc);
                if (!alreadyHasPronoun && currentFacts <= 1) {
                    if (Languages.isGreek(getLanguage())) {
                        textNode.setTextContent(GREEK_GENDER_INSENSITIVE_PRONOUN);
                        currentMessage.appendChild(textNode);
                    } else if (Languages.isEnglish(getLanguage())) {
                        textNode.setTextContent(ENGLISH_RELATIVE_PRONOUN_THAT);
                        currentMessage.appendChild(textNode);
                    }
                }
            }

            if (startAppending) {
                currentMessage.appendChild(nextSlot);
            }
        }
    }

    public void applyRuleForClassAndPrepositionalPhrase(Node currentMessage, Node nextMessage) {
        boolean startAppending = false;
        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node nextSlot = (Node) messageSlots.get(k);

            if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.PREPOSITION_TAG)) {
                startAppending = true;
            }
            if (startAppending) {
                currentMessage.appendChild(nextSlot);
            }
        }
    }

    //Annotate adjectives in filler, they will be filled in NLName realization
    public void applyRuleForClassAndAdjective(Node currentMessage, Node nextMessage) {
        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (Node currentSlot : XmlMsgs.returnChildNodes(currentMessage)) {
            if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                int counter = 1;
                if (!XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.countAppends).isEmpty()) {
                    counter = Integer.parseInt(XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.countAppends));
                }

                for (int k = 0; k < messageSlots.size(); k++) {
                    Node nextSlot = (Node) messageSlots.get(k);

                    if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.ADJECTIVE_TAG)) {
                        ((Element) currentSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.appendAdjective + counter, XmlMsgs.getAttribute(nextSlot, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI));
                        counter++;
                    }
                }
                ((Element) currentSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.countAppends, counter + "");
            }
        }
    }

    //Annotate adjectives in filler, they will be filled in NLName realization
    public void applyRuleForClassAndAdjective(Node currentMessage, ArrayList<IRI> nameIRIs, boolean isConnective) {
        for (Node currentSlot : XmlMsgs.returnChildNodes(currentMessage)) {
            if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                int counter = 1;
                if (!XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.countAppends).isEmpty()) {
                    counter = Integer.parseInt(XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.countAppends));
                }

                for (IRI nameIRI : nameIRIs) {
                    if (NLNQM.getNLName(nameIRI) != null) {
                        if (NLNQM.getNLName(nameIRI).getSlotsList().size() == 1) {
                            if (NLNQM.getNLName(nameIRI).getSlotsList().get(0) instanceof NLNAdjectiveSlot) {
                                ((Element) currentSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.appendAdjective + counter, ((NLNAdjectiveSlot) NLNQM.getNLName(nameIRI).getSlotsList().get(0)).getLexiconEntryIRI().toString());
                                if (!isConnective) {
                                    ((Element) currentSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.isConnective + counter, "false");
                                }
                                counter++;
                            }
                        }
                    }
                }
                ((Element) currentSlot).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.countAppends, counter + "");
            }
        }
    }

    public void applyRuleForSameVerbConjunctionDisjunction(Document doc, Node currentMessage, Node nextMessage, boolean isConjunction) {
        ArrayList<Node> messageSlotsNext = XmlMsgs.returnChildNodes(nextMessage);
        ArrayList<Node> messageSlotsCurrent = XmlMsgs.returnChildNodes(currentMessage);

        boolean prepositionFollows = false;
        for (int k = 0; k < messageSlotsNext.size(); k++) {
            if (XmlMsgs.compare(messageSlotsNext.get(k), XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {

                if (k + 1 < messageSlotsNext.size()) {
                    if (XmlMsgs.compare(messageSlotsNext.get(k + 1), XmlMsgs.prefix, XmlMsgs.PREPOSITION_TAG)) {
                        prepositionFollows = true;
                    }
                }
            }
        }

        boolean appendOnlyFillers = false;
        if (XmlMsgs.getAttribute(currentMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).equals(XmlMsgs.getAttribute(nextMessage, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG))) {
            appendOnlyFillers = true;
        }

        if (!appendOnlyFillers) {
            Element textNode = XmlMsgs.createTextNode(doc);
            if (isConjunction) {
                textNode.setTextContent(XmlMsgs.CONNECTIVE);
                if (!prepositionFollows) {
                    currentMessage.appendChild(textNode);
                }
            } else {
                textNode.setTextContent(XmlMsgs.DISJUNCTIVE);
                currentMessage.appendChild(textNode);
            }

            boolean startAppending = false;
            for (int k = 0; k < messageSlotsNext.size(); k++) {
                Node nextSlot = messageSlotsNext.get(k);

                if (startAppending) {
                    //IF NOT A MODIFIER SLOT
                    if (!XmlMsgs.getAttribute(nextSlot, XmlMsgs.prefix, XmlMsgs.forProperty).contains("(")) {
                        currentMessage.appendChild(nextSlot);
                    }
                }

                if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                    startAppending = true;
                    if (!isConjunction && prepositionFollows) {
                        k++;
                    }
                }
            }
        } else {
            for (int c = 0; c < messageSlotsCurrent.size(); c++) {
                Node currentSlot = messageSlotsCurrent.get(c);
                if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                    for (int k = 0; k < messageSlotsNext.size(); k++) {
                        Node nextSlot = messageSlotsNext.get(k);
                        if (XmlMsgs.compare(nextSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                            Element textNode = XmlMsgs.createTextNode(doc);
                            int insertsBefore = 0;
                            if (isConjunction) {
                                textNode.setTextContent(XmlMsgs.CONNECTIVE);
                                if (!prepositionFollows) {
                                    if (c + 1 < messageSlotsCurrent.size()) {
                                        currentMessage.insertBefore(textNode, messageSlotsCurrent.get(c + 1));
                                        insertsBefore++;
                                    } else {
                                        currentMessage.appendChild(textNode);
                                    }
                                }
                            } else {
                                textNode.setTextContent(XmlMsgs.DISJUNCTIVE);
                                if (c + 1 < messageSlotsCurrent.size()) {
                                    currentMessage.insertBefore(textNode, messageSlotsCurrent.get(c + 1));
                                    insertsBefore++;
                                } else {
                                    currentMessage.appendChild(textNode);
                                }
                            }

                            if (c + 1 < messageSlotsCurrent.size()) {
                                currentMessage.insertBefore(nextSlot, messageSlotsCurrent.get(c + 1));
                                insertsBefore++;
                            } else {
                                currentMessage.appendChild(nextSlot);
                            }

                            c += insertsBefore;
                        }
                    }
                }
            }
        }
    }

    public void applyRuleForDifferentVerbConjunction(Document doc, Node currentMessage, Node nextMessage) {
        Element textNode = XmlMsgs.createTextNode(doc);
        textNode.setTextContent(XmlMsgs.CONNECTIVE);
        currentMessage.appendChild(textNode);

        ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(nextMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            currentMessage.appendChild(messageSlots.get(k));
        }
    }

    public void applyRuleForSharedNounWithDifferentAdjectivesInNLName(Document doc, Node currentMessage, boolean isConjunction) {
        Element aggregateNLNameNode = doc.createElementNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + Aggregation.AGGREGATE_NLNAME);

        if (isConjunction) {
            aggregateNLNameNode.setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.isConnective, "true");
        } else {
            aggregateNLNameNode.setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.isConnective, "false");
        }

        currentMessage.appendChild(aggregateNLNameNode);

        ArrayList<?> messageSlots = XmlMsgs.returnChildNodes(currentMessage);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node currentSlot = (Node) messageSlots.get(k);

            if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                Element fillerNode = doc.createElementNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.FILLER_TAG);
                fillerNode.setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.NLNAME_TAG, XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG));
                fillerNode.setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.useBullets, XmlMsgs.getAttribute(currentSlot, XmlMsgs.prefix, XmlMsgs.useBullets));
                aggregateNLNameNode.appendChild(fillerNode);

                currentSlot.getParentNode().removeChild(currentSlot);
            } else if (currentSlot.getTextContent().equals(XmlMsgs.CONNECTIVE_2ND_LEVEL)) {
                currentSlot.getParentNode().removeChild(currentSlot);
            }
        }
    }

    public boolean isPassiveSentencePlan(IRI planIRI) {
        boolean verbFound = false;
        for (SPSlot slot : SPQM.getSlots(planIRI)) {
            if (slot instanceof SPVerbSlot) {
                verbFound = true;
                if (!((SPVerbSlot) slot).getVoice().equals(XmlMsgs.PASSIVE_VOICE)) {
                    return false;
                }
            }
        }
        if (verbFound) {
            return true;
        }
        return false;
    }

    public boolean isActiveSentencePlan(IRI planIRI) {
        boolean verbFound = false;
        for (SPSlot slot : SPQM.getSlots(planIRI)) {
            if (slot instanceof SPVerbSlot) {
                verbFound = true;
                if (!((SPVerbSlot) slot).getVoice().equals(XmlMsgs.ACTIVE_VOICE)) {
                    return false;
                }
            }
        }
        if (verbFound) {
            return true;
        }
        return false;
    }

    public boolean isActiveMessage(Node currentMessage) {
        ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(currentMessage);
        for (Node slot : messageSlots) {
            if (XmlMsgs.compare(slot, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) {
                if (!XmlMsgs.getAttribute(slot, XmlMsgs.prefix, XmlMsgs.VOICE_TAG).equals(XmlMsgs.ACTIVE_VOICE)) {
                    return false;
                }
            }
        }
        return true;
    }

    //Checks whether the sentenceplan contains the verb to be followed by a preposition
    public boolean isPrepositionalPhraseSentencePlan(IRI planIRI) {
        ArrayList<SPSlot> slots = SPQM.getSlots(planIRI);
        Collections.sort(slots);

        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof SPVerbSlot) {
                if (((SPVerbSlot) slots.get(i)).getLexiconEntryIRI().equals(DefaultResourcesManager.toBeVLE_IRI)) {
                    if (((SPVerbSlot) slots.get(i)).getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                        if (i + 1 < slots.size()) {
                            if (slots.get(i + 1) instanceof SPPrepositionSlot) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //Checks whether the sentenceplan contains the verb to be followed by an adjective and nothing else
    public boolean isAdjectivePhraseSentencePlan(IRI planIRI) {
        ArrayList<SPSlot> slots = SPQM.getSlots(planIRI);
        if (slots != null) {
            Collections.sort(slots);

            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) instanceof SPVerbSlot) {
                    if (((SPVerbSlot) slots.get(i)).getLexiconEntryIRI().equals(DefaultResourcesManager.toBeVLE_IRI)) {
                        if (((SPVerbSlot) slots.get(i)).getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                            if (i + 1 == slots.size() - 1) {
                                if (slots.get(i + 1) instanceof SPAdjectiveSlot) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //Checks whether the sentenceplan contains only a verb and the following nlname only an adjective
    public boolean isAdjectivePhraseSentencePlan(IRI planIRI, ArrayList<IRI> nameIRIs) {
        ArrayList<SPSlot> planSlots = SPQM.getSlots(planIRI);
        if (planSlots != null) {
            Collections.sort(planSlots);

            for (int i = 0; i < planSlots.size(); i++) {
                if (planSlots.get(i) instanceof SPVerbSlot) {
                    if (((SPVerbSlot) planSlots.get(i)).getLexiconEntryIRI().equals(DefaultResourcesManager.toBeVLE_IRI)) {
                        if (((SPVerbSlot) planSlots.get(i)).getTense().equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                            if (i + 1 == planSlots.size() - 1) {
                                if (planSlots.get(i + 1) instanceof SPFillerSlot) {
                                    boolean allAdjs = true;
                                    for (IRI nameIRI : nameIRIs) {

                                        ArrayList<NLNSlot> nameSlots = NLNQM.getSlots(nameIRI);
                                        if (nameSlots != null) {
                                            if (nameSlots.size() == 1) {
                                                if (!(nameSlots.get(0) instanceof NLNAdjectiveSlot)) {
                                                    allAdjs = false;
                                                }
                                            } else {
                                                allAdjs = false;
                                            }
                                        } else {
                                            allAdjs = false;
                                        }
                                    }
                                    if (allAdjs) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //Checks whether the two sentenceplans contains the same verb
    public boolean containSameVerb(IRI currentSPIRI, IRI nextSPIRI) {
        ArrayList<SPSlot> currentSlots = SPQM.getSlots(currentSPIRI);
        ArrayList<SPSlot> nextSlots = SPQM.getSlots(nextSPIRI);

        String currentPolarity = "";
        String nextPolarity = "";

        IRI currentVerbIRI = null;
        int counter = 0;
        for (SPSlot slot : currentSlots) {
            if (slot instanceof SPVerbSlot) {
                currentVerbIRI = ((SPVerbSlot) slot).getLexiconEntryIRI();
                currentPolarity = ((SPVerbSlot) slot).getPolarity();
                counter++;
            }
        }
        if (counter != 1) {
            return false;
        }

        IRI nextVerbIRI = null;
        counter = 0;
        for (SPSlot slot : nextSlots) {
            if (slot instanceof SPVerbSlot) {
                nextVerbIRI = ((SPVerbSlot) slot).getLexiconEntryIRI();
                nextPolarity = ((SPVerbSlot) slot).getPolarity();
                counter++;
            }
        }
        if (counter != 1) {
            return false;
        }

        if (currentPolarity.equals(nextPolarity)) {
            if (currentPolarity.equals(SPVerbSlot.POLARITY_POSITIVE)) {
                if (currentVerbIRI.equals(nextVerbIRI)) {
                    for (int i = 0; i < currentSlots.size(); i++) {
                        SPSlot slot = currentSlots.get(i);
                        if (slot instanceof SPVerbSlot) {
                            if (nextSlots.get(i) instanceof SPVerbSlot) {
                                return true;
                            }
                            return false;
                        }
                        if (!slot.toString().equals(nextSlots.get(i).toString())) {
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Node getOwner(Node message) {
        ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(message);
        for (int k = 0; k < messageSlots.size(); k++) {
            Node currentSlot = messageSlots.get(k);

            if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.OWNER_TAG)) {
                return currentSlot;
            }//if
        }//for

        return null;
    }//get_Owner

    private void recalculateAgreeWithAfterRemove(Node resultingMessage, Node removedMessage) {
        ArrayList<Node> resultingChildren = XmlMsgs.returnChildNodes(resultingMessage);
        ArrayList<Node> removedChildren = XmlMsgs.returnChildNodes(removedMessage);

        for (int i = 0; i < resultingChildren.size(); i++) {
            String agreeWithID = XmlMsgs.getAttribute(resultingChildren.get(i), XmlMsgs.prefix, XmlMsgs.AGREE_TAG);
            if (!agreeWithID.isEmpty()) {
                Node agreementNode = null;
                for (int s = 0; s < resultingChildren.size(); s++) {
                    if (XmlMsgs.getAttribute(resultingChildren.get(s), XmlMsgs.prefix, XmlMsgs.ID).equals(agreeWithID)) {
                        agreementNode = resultingChildren.get(s);
                    }
                }
                if (agreementNode == null) {
                    for (int r = 0; r < removedChildren.size(); r++) {
                        if (XmlMsgs.getAttribute(removedChildren.get(r), XmlMsgs.prefix, XmlMsgs.ID).equals(agreeWithID)) {
                            agreementNode = removedChildren.get(r);
                        }
                    }
                    if (agreementNode == null) {
                        ((Element) resultingChildren.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.AGREE_TAG, "");
                        System.err.println("Had to remove agreement of slot " + XmlMsgs.getAttribute(resultingChildren.get(i), XmlMsgs.prefix, XmlMsgs.ID) + " with slot " + agreeWithID + "...");
                    } else {
                        for (int s = 0; s < resultingChildren.size(); s++) {
                            if (resultingChildren.get(s).getNodeName().equals(agreementNode.getNodeName())) {
                                if ((XmlMsgs.compare(agreementNode, XmlMsgs.prefix, XmlMsgs.OWNER_TAG) || XmlMsgs.compare(agreementNode, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) || ((XmlMsgs.compare(agreementNode, XmlMsgs.prefix, XmlMsgs.ADJECTIVE_TAG) || XmlMsgs.compare(agreementNode, XmlMsgs.prefix, XmlMsgs.NOUN_TAG) || XmlMsgs.compare(agreementNode, XmlMsgs.prefix, XmlMsgs.VERB_TAG)) && XmlMsgs.getAttribute(removedChildren.get(s), XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI).equals(XmlMsgs.getAttribute(agreementNode, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI)))) {
                                    ((Element) resultingChildren.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.AGREE_TAG, XmlMsgs.getAttribute(resultingChildren.get(s), XmlMsgs.prefix, XmlMsgs.ID));
                                } else {
                                    ((Element) resultingChildren.get(i)).setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.AGREE_TAG, "");
                                    System.err.println("Had to remove agreement of slot " + XmlMsgs.getAttribute(resultingChildren.get(i), XmlMsgs.prefix, XmlMsgs.ID) + " with slot " + agreeWithID + "...");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Node> getFillers(Node message) {
        ArrayList<Node> messageSlots = XmlMsgs.returnChildNodes(message);
        ArrayList<Node> fillerSlots = new ArrayList<Node>();

        for (int k = 0; k < messageSlots.size(); k++) {
            Node currentSlot = messageSlots.get(k);
            if (XmlMsgs.compare(currentSlot, XmlMsgs.prefix, XmlMsgs.FILLER_TAG)) {
                fillerSlots.add(currentSlot);
            }//if
        }//for

        return fillerSlots;
    }

    public void setMaxMessagesPerSentence(int value) {
        maxMessagesPerSentence = value;
    }

    public void setOverrideMaxMessagesPerSentence(int value) {
        overrideMaxMessagesPerSentence = value;
    }

    public void setMaxSlotsPerSentence(int value) {
        maxSlotsPerSentence = value;
    }
}//Aggregation