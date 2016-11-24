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
package gr.aueb.cs.nlg.Utils;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import gr.aueb.cs.nlg.NLFiles.NLResourceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;

public class XmlMsgs extends XmlUtils {

    private Document doc;
    private Element MsgsElement;
    //private String Language;
    //-----------------------------------------------------------------------------------
    //tags definition
    public final static String INTERSECTION_OF_TAG = "intersectionOf";
    public final static String SUBCLASS_OF_TAG = "subclassOf";
    public final static String UNION_OF_TAG = "unionOf";
    public final static String COMPLEMENT_OF_TAG = "complementOf";
    public final static String CLASS_TAG = "cls";
    public final static String HAS_VALUE_RESTRICTION_TAG = "hasValueRestriction";
    public final static String SOME_VALUES_FROM_RESTRICTION_TAG = "someValuesFromRestriction";
    public final static String ALL_VALUES_FROM_RESTRICTION_TAG = "allValuesFromRestriction";
    public final static String EXACT_CARDINALITY_RESTRICTION_TAG = "exactCardRestriction";
    public final static String MIN_CARDINALITY_RESTRICTION_TAG = "minCardRestriction";
    public final static String MAX_CARDINALITY_RESTRICTION_TAG = "maxCardRestriction";
    public final static String ENUMERATION_OF_TAG = "enumerationOf";
    public final static String EQUIVALENT_CLASS_TAG = "equivalentClass";
    public final static String DISJOINT_WITH_TAG = "disjointWith";
    public final static String INSTANCE_TAG = "Instance";
    public final static String DIFFERENT_FROM_TAG = "differentFrom";
    public final static String SAME_AS_TAG = "sameAs";
    public static final String NOT_MODIFIER = "not";
    //-----------------------------------------------------------------------------------
    public final static String NLNAME_TAG = "NLName";
    public final static String SUB_NLNAME_TAG = "subjectNLName";
    public final static String OBJ_NLNAME_TAG = "objectNLName";
    public final static String COMPARATOR_NLNAME_TAG = "comparatorNLName";
    public final static String COMPARATOR_FILLER_NLNAME_TAG = "comparatorFillerNLName";
    public final static String SENTENCE_PLAN_TAG = "sentencePlan";
    public final static String ORDER_TAG = "order";
    public final static String SECTION_TAG = "section";
    public final static String SECTION_NAME = "sectionName";
    //-----------------------------------------------------------------------------------
    public final static int CLS_TYPE = 0;
    public final static int INST_TYPE = 1;
    //-----------------------------------------------------------------------------------	
    //slot tags        
    public final static String VERB_TAG = "verb";
    public final static String ADJECTIVE_TAG = "adjective";
    public final static String NOUN_TAG = "noun";
    public final static String singular_VERB_TAG = "singularVerb";
    public final static String plural_VERB_TAG = "pluralVerb";
    public final static String FILLER_TAG = "filler";
    public final static String OWNER_TAG = "owner";
    public final static String PREPOSITION_TAG = "preposition";
    public final static String TEXT_TAG = "text";
    public final static String CONCAT_TAG = "concatenation";
    public final static String COMPARATOR_TAG = "comparator";
    public final static String COMPARATOR_FILLER_TAG = "comparatorFiller";
    public final static String CLSDESC_TAG = "clsDesc";
    public final static String DISJUNCTION_TAG = "disjunction";
    //from here
    public final static String RETYPE = "RE_TYPE";
    public static final String REF_AUTO = "auto";
    public static final String REF_PRONOUN = "pronoun";
    public static final String REF_DEMONSTRATIVE = "demonstrative";
    //to here
    public final static String IS_A_TAG = "isA";
    public final static String A_TAG = "aOrAn";
    //slots attributes
    public final static String CASE_TAG = "case";
    public final static String TENSE_TAG = "tense";
    public final static String VOICE_TAG = "voice";
    public final static String GENDER_TAG = "gender";
    public final static String PERSON_TAG = "person";
    public final static String NUMBER_TAG = "number";
    public final static String AGREE_TAG = "agree";
    public final static String PASSIVE_VOICE = "passive";
    public final static String ACTIVE_VOICE = "active";
    public final static String TENSE_SIMPLE_PRESENT = "simple present";
    public final static String TENSE_PRESENT_CONTINUOUS = "present continuous";
    public final static String TENSE_PRESENT_PERFECT = "present perfect";
    public final static String TENSE_SIMPLE_PAST = "simple past";
    public final static String TENSE_PAST_CONTINUOUS = "past continuous";
    public final static String TENSE_PAST_PERFECT = "past perfect";
    public final static String TENSE_PAST_PERFECT_CONTINUOUS = "past perfect continuous";
    public final static String TENSE_SIMPLE_FUTURE = "simple future";
    public final static String TENSE_FUTURE_CONTINUOUS = "future continuous";
    public final static String TENSE_FUTURE_PERFECT = "future perfect";
    public final static String TENSE_FUTURE_PERFECT_CONTINUOUS = "future perfect continuous";
    public final static String TENSE_INFINITIVE = "infinitive";
    public final static String TENSE_PARTICIPLE = "participle";
    public final static String PERSON_1ST = "1st";
    public final static String PERSON_2ND = "2nd";
    public final static String PERSON_3RD = "3rd";
    public final static String NOMINATIVE_TAG = "nominative";
    public final static String GENITIVE_TAG = "genitive";
    public final static String ACCUSATIVE_TAG = "accusative";
    public final static String GENDER_MASCULINE = "masculine";
    public final static String GENDER_FEMININE = "feminine";
    public final static String GENDER_MASCULINE_OR_FEMININE = "masculineOrFeminine";
    public final static String GENDER_NEUTER = "neuter";
    public final static String SINGULAR = "singular";
    public final static String PLURAL = "plural";
    public final static String type = "type";
    public final static String PRONOUN_TAG = "Pronoun";
    public final static String REF = "ref";
    public final static String VALUE = "value";
    public final static String COMPARE_TO = "compareTo";
    public final static String COMPARE_TO_FILLER = "compareToFiller";
    public final static String INTEREST = "interest";
    public final static String ASSIMIL_SCORE = "AssimilationScore";
    public final static String TYPE_OF = "typeOf";
    public final static String AGGREG_ALLOWED = "AGGREG_ALLOWED";
    public final static String LEVEL = "LEVEL";
    public final static String RE_FOCUS = "RE_FOCUS";
    public final static String FOCUSLevel4 = "FOCUSLevel4";
    public final static String FOCUSLevel3 = "FOCUSLevel3";
    public final static String FOCUSLevel2 = "FOCUSLevel2";
    public final static String FOCUSLevel1 = "FOCUSLevel1";
    public final static String FOCUS_LOST = "FOCUS_LOST";
    //-----------------------------------------------------------------------------------
    public final static String prpType = "prpType";
    public final static String ObjectProperty = "objPrp";
    public final static String DatatypeProperty = "dPrp";
    public final static String forProperty = "forProperty";
    public final static String forSubject = "forSubject";
    public final static String forObject = "forObject";
    public final static String forDomainIndependentProperty = "forDomainIndependentProperty";
    public final static String concatIndividual = "concatenateIndividual";
    public final static String useAuxiliaryVerb = "useAuxiliaryVerb";
    public final static String useBullets = "useBullets";
    public final static String articleUse = "articleUse";
    public final static String polarity = "polarity";
    public final static String modifier = "modifier";
    public final static String cardinality = "cardinality";
    public final static String factsContained = "factsContained";
    public final static String ID = "ID";
    public final static String lexiconEntryIRI = "lexiconEntryIRI";
    //Aggregation
    public final static String ILPSentence = "ILPSentence";
    public final static String appendAdjective = "appendAdjective";
    public final static String countAppends = "countAppends";
    public final static String isConnective = "isConnective";
    public final static String CONNECTIVE = "&&";
    public final static String CONNECTIVE_2ND_LEVEL = "&&&";
    public final static String NOT_CONNECTIVE = "!&&";
    public final static String NOT_CONNECTIVE_2ND_LEVEL = "!&&&";
    public final static String DISJUNCTIVE = "||";
    public final static String DISJUNCTIVE_2ND_LEVEL = "|||";
    public static String prefix = "nlowl";

    public XmlMsgs(String idName, int type, Document d) {
        doc = d;
        MsgsElement = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + "owlMsgs");

        if (type == CLS_TYPE) {
            setAttr(MsgsElement, NLResourceManager.nlowlNS, prefix, "CLS-Name", idName);
        } else if (type == INST_TYPE) {
            setAttr(MsgsElement, NLResourceManager.nlowlNS, prefix, "INST-Name", idName);
        }

        doc.appendChild(MsgsElement);

        setNamespace(NLResourceManager.nlowlNS, prefix);
    }

    public final void setNamespace(String ns, String myprefix) {
        NodeList AllNodesList = doc.getElementsByTagName(prefix + ":" + "owlMsgs");
        ((Element) AllNodesList.item(0)).setAttribute("xmlns:" + myprefix, ns);
    }

    public void setDefaultNamespace() {
        NodeList AllNodesList = doc.getElementsByTagName(prefix + ":" + "owlMsgs");
        ((Element) AllNodesList.item(0)).setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NLResourceManager.nlowlNS);
    }

    public int getType() {
        NodeList AllNodesList = doc.getElementsByTagName(prefix + ":" + "owlMsgs");

        if (getAttribute(AllNodesList.item(0), XmlMsgs.prefix, "CLS-Name").compareTo("") != 0) {
            return 1;
        } else if (getAttribute(AllNodesList.item(0), XmlMsgs.prefix, "INST-Name").compareTo("") != 0) {
            return 2;
        } else {
            return 0;
        }
    }

    public String getOwner() {
        Node root = getRoot();

        if (getType() == 1) {
            return getAttribute(root, prefix, "CLS-Name");
        } else if (getType() == 2) {
            return getAttribute(root, prefix, "INST-Name");
        } else {
            return "";
        }
    }

    public Node getRoot() {
        return doc.getElementsByTagName(XmlMsgs.prefix + ":" + "owlMsgs").item(0);
    }

    public ArrayList<Node> getMessages() {
        Node root = getRoot();
        return XmlMsgs.returnChildNodes(root);
    }

    public void removeMsgs(ArrayList<Node> msgs) {
        Node root = getRoot();

        for (int i = 0; i < msgs.size(); i++) {
            root.removeChild(msgs.get(i));
        }
    }

    public static boolean compare(Node currNode, String pref, String tag) {
        String NodeName = currNode.getNodeName();
        String prefixedName = (pref.compareTo("") == 0) ? tag : pref + ":" + tag;

        if (NodeName.compareTo(prefixedName) == 0) {
            return true;
        }
        return false;
    }

    public Document getXMLTree() {
        return doc;
    }

    public void setXMLTree(Document d) {
        doc = d;
    }

    public Element createNewMsg() {
        if (getType() == 1) {
            Element MsgElement = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + "owlMsg");
            MsgsElement.appendChild(MsgElement);

            return MsgElement;
        } else if (getType() == 2) {
            return (Element) doc.getElementsByTagNameNS(NLResourceManager.nlowlNS, "owlMsgs").item(0);
        }
        return null;
    }

    public Element createNewMsg(Element parentMsg) {
        if (getType() == 1) {
            Element MsgElement = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + "owlMsg");
            parentMsg.appendChild(MsgElement);

            return MsgElement;
        } else if (getType() == 2) {
            return (Element) doc.getElementsByTagNameNS(NLResourceManager.nlowlNS, "owlMsgs").item(0);
        }

        return null;
    }

    //add a new element with name "tag" with root Elem
    public Element addNewElement(Element Elem, String ns, String prefix, String tag) {
        if (prefix.compareTo("") != 0) {
            Element NewElement = doc.createElementNS(ns, prefix + ":" + tag);

            Elem.appendChild(NewElement);
            return NewElement;
        }
        Element NewElement = doc.createElement(tag);

        Elem.appendChild(NewElement);
        return NewElement;
    }

    // add an attribute with name "Attr" and value "value" to element Elem
    public final void setAttr(Element Elem, String ns, String pref, String Attr, String value) {
        Elem.setAttributeNS(ns, pref + ":" + Attr, value);
    }

    // set text content of an element
    public void setText(Element Elem, String text) {
        Elem.setTextContent(text);
    }

    public void addOwnerSlot(Node root, String propertyIRI, String caseType, String refType, NodeID ID) {
        Element ownerNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + OWNER_TAG);

        if (ID != null) {
            setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        }
        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG));
        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);
        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RETYPE, refType);

        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(ownerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(ownerNode);
    }

    public void addFillerSlot(Node root, String propertyIRI, String caseType, String forceNumber, boolean useBullets, int articleUse, NodeID ID) {
        boolean isSlotConnective = false;

        ArrayList<String> values = new ArrayList<String>();
        if (XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.VALUE).startsWith("and(")) {
            isSlotConnective = true;
            values = Fact.parseModifier(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.VALUE));
        } else if (XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.VALUE).startsWith("or(")) {
            isSlotConnective = false;
            values = Fact.parseModifier(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.VALUE));
        } else {
            values.add(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.VALUE));
        }
        ArrayList<String> NLNames = new ArrayList<String>();
        if (XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG).startsWith("and(")) {
            NLNames = Fact.parseModifier(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG));
        } else if (XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG).startsWith("or(")) {
            NLNames = Fact.parseModifier(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG));
        } else {
            NLNames.add(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG));
        }

        for (int i = 0; i < values.size(); i++) {
            Element fillerNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + FILLER_TAG);

            setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
            setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);

            if (XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.prpType).equals(XmlMsgs.ObjectProperty)) {
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG, NLNames.get(i));
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);
            }

            setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.articleUse, articleUse + "");

            if (forceNumber.equals(XmlMsgs.SINGULAR)) {
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.SINGULAR);
            } else if (forceNumber.equals(XmlMsgs.PLURAL)) {
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.PLURAL);
            }

            if (useBullets) {
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.useBullets, "true");
            } else {
                setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.useBullets, "false");
            }

            fillerNode.setTextContent(values.get(i));

            setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
            setAttr(fillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

            root.appendChild(fillerNode);

            if (i != values.size() - 1) {
                Element textNode = createTextNode(doc);
                if (isSlotConnective) {
                    textNode.setTextContent(XmlMsgs.CONNECTIVE_2ND_LEVEL);
                } else {
                    textNode.setTextContent(XmlMsgs.DISJUNCTIVE_2ND_LEVEL);
                }
                root.appendChild(textNode);
            }
        }

        if (values.isEmpty()) {
            root.getParentNode().removeChild(root);
        }
    }

    public Node addVerbSlot(Node root, String propertyIRI, IRI verbIRI, String voice, String tense, String number, String person, NodeID agreeID, boolean polarity, NodeID ID) {
        Element verbNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + VERB_TAG);

        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI, verbIRI.toString());
        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.VOICE_TAG, voice);
        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.TENSE_TAG, tense);

        if (agreeID == null) {
            setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, number);
            setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.PERSON_TAG, person);
        } else {
            setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGREE_TAG, agreeID.toString());
        }

        if (polarity) {
            setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
        } else {
            setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");
        }

        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(verbNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(verbNode);

        return verbNode;
    }

    public void addAdjectiveSlot(Node root, String propertyIRI, IRI adjectiveIRI, String caseType, String gender, String number, NodeID agreeID, NodeID ID) {
        Element adjectiveNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + ADJECTIVE_TAG);

        setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI, adjectiveIRI.toString());

        if (agreeID == null) {
            setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, number);
            setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);
            setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.GENDER_TAG, gender);
        } else {
            setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGREE_TAG, agreeID.toString());
        }

        setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(adjectiveNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(adjectiveNode);
    }

    public void addNounSlot(Node root, String propertyIRI, IRI nounIRI, String caseType, String number, NodeID agreeID, NodeID ID) {
        Element nounNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + NOUN_TAG);

        setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.lexiconEntryIRI, nounIRI.toString());

        if (agreeID == null) {
            setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, number);
            setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);
        } else {
            setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGREE_TAG, agreeID.toString());
        }

        setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(nounNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(nounNode);
    }

    public Node addPrepositionSlot(Node root, String propertyIRI, String text) {
        Element textNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + PREPOSITION_TAG);
        textNode.setTextContent(text);

        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(textNode);

        return textNode;
    }

    public void addStringSlot(Node root, String propertyIRI, String text) {
        Element textNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + TEXT_TAG);
        textNode.setTextContent(text);

        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(textNode);
    }

    public void addStringSlot(Node root, String propertyIRI, String text, Node before) {
        Element textNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + TEXT_TAG);
        textNode.setTextContent(text);

        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.insertBefore(textNode, before.getNextSibling());
    }

    public void addConcatenationIndividualSlot(Node root, String propertyIRI, IRI concatenationIndividual, String caseType) {
        Element textNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + CONCAT_TAG);

        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.concatIndividual, concatenationIndividual.toString());
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);

        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(textNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(textNode);
    }

    public void addComparatorSlot(Node root, String propertyIRI, String caseType, boolean isMany, int articleUse, NodeID ID) {
        Element comparatorNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + COMPARATOR_TAG);

        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);

        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.COMPARATOR_NLNAME_TAG));
        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);

        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.articleUse, articleUse + "");

        if (!isMany) {
            setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.SINGULAR);
        } else {
            setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.PLURAL);
        }

        comparatorNode.setTextContent(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.COMPARE_TO));

        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(comparatorNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(comparatorNode);
    }

    public void addComparatorFillerSlot(Node root, String propertyIRI, String caseType, boolean isMany, int articleUse, NodeID ID) {
        Element comparatorFillerNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + COMPARATOR_FILLER_TAG);

        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ID, ID.toString());
        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);

        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_NLNAME_TAG));
        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.CASE_TAG, caseType);

        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.articleUse, articleUse + "");

        if (!isMany) {
            setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.SINGULAR);
        } else {
            setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NUMBER_TAG, XmlMsgs.PLURAL);
        }

        comparatorFillerNode.setTextContent(XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.COMPARE_TO_FILLER));

        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(comparatorFillerNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(comparatorFillerNode);
    }

    public void addISASlot(Node root, String propertyIRI) {
        Element isaNode = doc.createElementNS(NLResourceManager.nlowlNS, prefix + ":" + IS_A_TAG);

        setAttr(isaNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI);

        setAttr(isaNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.NLNAME_TAG, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG));
        setAttr(isaNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.INTEREST));
        setAttr(isaNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, XmlMsgs.getAttribute(root, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE));

        root.appendChild(isaNode);
    }

    // return a string representation of the xml document 
    public String getStringDescription(boolean indent) {
        try {
            OutputFormat OutFrmt = null;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutFrmt = new OutputFormat(doc);

            OutFrmt.setIndenting(indent);
            OutFrmt.setEncoding("UTF-8");
            XMLSerializer xmlsrz = new XMLSerializer(os, OutFrmt);
            xmlsrz.serialize(doc);
            return new String(os.toByteArray(), Charset.forName("UTF-8"));
        }//try
        catch (UnsupportedCharsetException e) {
            return "ERRoR: UnsupportedCharsetException";
        } catch (Exception e) {
            return "ERRoR";
        }//catch
    }//getStringDescription

    //sort by order
    public void sortByOrder() {
        Node root = getRoot();
        ArrayList<Node> messages = getMessages();

        Object T[] = messages.toArray();
        Arrays.sort(T, new OrderComparatorImpl(true)); // sort  Nodes by the order Attribute

        for (int i = 0; i < T.length; i++) {
            Node curr = (Node) T[i];
            root.appendChild(curr); // to append child prwta afairei enan komvo 
            // pou hdh yparxei sto dentro
            sortChildMessages(curr);
        }
        groupSameProperties();
    }

    private void sortChildMessages(Node n) {
        ArrayList<Node> messageNodes = returnChildNodes(n); // get childs of n
        if (messageNodes.size() >= 1) {// if # Msg childs > 1 sort them and  recursively call sortMsgs 			
            int j = 0;
            while (j < messageNodes.size()) {
                Node curr = messageNodes.get(j);
                if (curr.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.OWNER_TAG)
                        || curr.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.FILLER_TAG)
                        || curr.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.VERB_TAG)
                        || curr.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.TEXT_TAG)) {
                    messageNodes.remove(curr);
                } else {
                    j++;
                }
            }

            Object T[] = messageNodes.toArray();
            Arrays.sort(T, new OrderComparatorImpl(true)); // sort them by order

            for (int i = 0; i < T.length; i++) {
                Node curr = (Node) T[i];
                n.getParentNode().appendChild(curr);
                sortChildMessages(curr);
            }
        }//if
    }//sortMsgs        

    private void groupSameProperties() {
        HashMap<String, Node> subjectPredicateSet = new HashMap<String, Node>();
        ArrayList<Node> Msgs = getMessages();

        for (int i = 0; i < Msgs.size(); i++) {
            Node currNode = Msgs.get(i);

            String ref = XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.REF);
            String predicate = currNode.getNodeName();

            String key = ref + "," + predicate;

            if (subjectPredicateSet.containsKey(key)) {
                Node refChild = subjectPredicateSet.get(key).getNextSibling();
                if (refChild != null) {
                    Node ParentNode = refChild.getParentNode();
                    ParentNode.insertBefore(currNode, refChild);
                } else {
                    Node ParentNode = currNode.getParentNode();
                    ParentNode.appendChild(currNode);
                }
            } else {
                subjectPredicateSet.put(key, currNode);
            }
        }
    }

    public static String getAttribute(Node node, String prefix, String AttributeName) {
        String prefixedAttrName = "";

        if (prefix.compareTo("") == 0) {
            prefixedAttrName = AttributeName;
        } else {
            prefixedAttrName = prefix + ":" + AttributeName;
        }

        String ret = "";

        NamedNodeMap NMM = node.getAttributes();

        if (NMM != null) {
            if (NMM.getNamedItem(prefixedAttrName) != null) {
                ret = NMM.getNamedItem(prefixedAttrName).getTextContent();
                return ret;
            }
            return ret;
        }
        return ret;
    }

    public static String getChild(Node node, String prefix, String ChildName) {

        String prefixedChildName = "";

        if (prefix == null) {
            prefixedChildName = ChildName;
        } else {
            prefixedChildName = prefix + ":" + ChildName;
        }


        Node n = null;
        NodeList list_of_templ_childs = node.getChildNodes();

        for (int i = 0; i < list_of_templ_childs.getLength(); i++) {
            n = list_of_templ_childs.item(i);

            if (n.getNodeName().compareTo(prefixedChildName) == 0) {

                //logger.debug("Return: " + n.getTextContent());
                return n.getTextContent();
            }
        }
        return "";
    }

    public static Node getNodeChild(Node node, String prefix, String ChildName) {

        String prefixedChildName = "";

        if (prefix == null) {
            prefixedChildName = ChildName;
        } else {
            prefixedChildName = prefix + ":" + ChildName;
        }

        Node n = null;
        NodeList list_of_templ_childs = node.getChildNodes();
        for (int i = 0; i < list_of_templ_childs.getLength(); i++) {
            n = list_of_templ_childs.item(i);
            if (n.getNodeName().compareTo(prefixedChildName) == 0) {
                //logger.debug("Return: " + n.getTextContent());
                return n;
            }
        }
        return null;
    }

    public boolean findInNodeMap(NamedNodeMap nnm, String name) {
        for (int i = 0; i < nnm.getLength(); i++) {
            if (nnm.item(i).getLocalName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Node> returnMatchedNodes(String pref, String tag) {
        NodeList Match_List = null;

        if (pref.compareTo("") == 0) {
            Match_List = doc.getElementsByTagName(tag);
        } else {
            Match_List = doc.getElementsByTagName(prefix + ":" + tag);
        }

        ArrayList<Node> Match_List_vec = new ArrayList<Node>();
        for (int i = 0; i < Match_List.getLength(); i++) {
            Match_List_vec.add(Match_List.item(i));
        }

        return Match_List_vec;
    }

    // returns a ArrayList which contains the childs
    // of current
    public static ArrayList<Node> returnChildNodes(Node current) {
        NodeList Child_List = current.getChildNodes();
        ArrayList<Node> Child_List_vec = new ArrayList<Node>();

        for (int i = 0; i < Child_List.getLength(); i++) {

            Child_List_vec.add(Child_List.item(i));
        }

        return Child_List_vec;
    }

    public static Node getFirstNonTextChild(Node parent) {
        NodeList childs = parent.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if (childs.item(i).getNodeType() != Node.TEXT_NODE) {
                return childs.item(i);
            }
        }

        return null;
    }

    public void removeNode(Node node) {
        node.getParentNode().removeChild(node);
    }

    public void replaceWithPronoun(Node node, String Case) {
        Element elmnt = doc.createElementNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.PRONOUN_TAG);
        elmnt.setAttributeNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.CASE_TAG, Case);

        Node parent = node.getParentNode();
        parent.replaceChild(elmnt, node);
    }

    public static Element createTextNode(Document doc) {
        return doc.createElementNS(NLResourceManager.nlowlNS, XmlMsgs.prefix + ":" + XmlMsgs.TEXT_TAG);
    }
}//XmlMsgs