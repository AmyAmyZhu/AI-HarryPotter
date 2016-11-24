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

import gr.aueb.cs.nlg.NLFiles.NLResourceManager;

import gr.aueb.cs.nlg.Utils.XmlDocumentCreator;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AnnotatedDescription {

    private XmlDocumentCreator XmlDocCreator;
    private Document AnnotatedText;
    private Element AnnTExt;
    private Element currentPeriod;
    private Element currentSentence;
    public static String AnnotText = "AnnotatedText";
    public static String RE = "RE";
    public static String CANNEDTEXT = "CANNEDTEXT";
    public static String RELATIVE_PRONOUN = "RelativePronoun";
    public static String forProperty = "forProperty";
    public static String text = "TEXT";
    public static String VERB = "VERB";
    public static String PERIOD = "Period";
    public static String SENTENCE = "Sentence";
    public static String PUNCTUATION = "Punct";
    public static String REF = "ref";
    public static String ROLE = "role";
    public static String Interest = "Interest";
    public static String Assim = "Assim";
    public static String RE_TYPE = "RE_TYPE";
    public static String WSPACE = "WSPACE";
    public static String EmptyRef = "EmptyRef";
    public static String Comparator = "Comparator";
    public static String TURN = "Turn";

    /** Creates a new instance of AnnotatedDescription */
    public AnnotatedDescription() {
        XmlDocCreator = new XmlDocumentCreator();
    }

    public Document getXmlDoc() {
        NodeList periods = this.AnnTExt.getChildNodes();

        // for each period
        for (int i = 0; i < periods.getLength(); i++) {
            Node Period = periods.item(i);

            NodeList PeriodSlots = Period.getChildNodes();

            for (int k = 0; k < PeriodSlots.getLength(); k++) {
                Node PeriodSlot = PeriodSlots.item(k);

                // the first sentence of this period
                if ((PeriodSlot.getNodeName().equals("Sentence") || PeriodSlot.getNodeName().equals("Comparator"))
                        && k == 0) {
                    Node sent = PeriodSlot;
                    NodeList SentSlots = sent.getChildNodes();


                    for (int j = 0; j < SentSlots.getLength(); j++) {
                        Node Slot = SentSlots.item(j);

                        //the first slot of this sentence
                        // that is not null must be capitalized
                        if (Slot.getTextContent() != null && !Slot.getTextContent().isEmpty()) {
                            Slot.setTextContent(SurfaceRealization.capitalizeText(Slot.getTextContent()));
                            j = SentSlots.getLength();
                        } else {
                            Slot.setTextContent(Slot.getTextContent());
                        }
                    }
                }
            }
        }

        return AnnotatedText;
    }

    public Node getRoot() {
        getXmlDoc();
        return this.AnnTExt;
    }
    private Node ComparatorSlot;
    private Element ComparatorSlotInAnnotationsTree;
    private String StringCompEntities = "";
    private String TurnToSee = "";
    private boolean InComparator = false;

    public void setStringCompEntities(String str) {
        StringCompEntities = str;
    }

    public void setTurnToSee(String str) {
        TurnToSee = str;
    }

    public void startComparator(Node Slot) {
        ComparatorSlot = Slot;
        InComparator = true;

        String turn = XmlMsgs.getAttribute(Slot, XmlMsgs.prefix, "ComparatorEntitiesTurn");

        if (turn != null && !turn.isEmpty()) {
            this.setTurnToSee(turn);
        }
    }

    public void finishComparator() {
        reformComparator();
        ComparatorSlot = null;
        InComparator = false;

    }

    public void print() {
        getXmlDoc();
    }

    public String getAnnotatedXml() {
        getXmlDoc();
        return XmlMsgs.getStringDescription(AnnTExt, true);
    }

    public void generateAnnotatedDescription() {
        AnnotatedText = XmlDocCreator.getNewDocument();
        AnnTExt = AnnotatedText.createElement(AnnotText);
        AnnotatedText.appendChild(AnnTExt);

        addStartPeriod();
        addStartSentence();
    }

    public void setEntityId(String id) {
        AnnTExt.setAttribute("ref", id);
    }

    public void addRE(String text, String ontID, String prop, String produced_re, String role, String interest, String assimScore) {
        if (currentSentence != null) {
            makeItComparator();

            String re_tag = RE;

            if (produced_re.equals(SurfaceRealization.PROD_RE_NULL)) {
                re_tag = EmptyRef;
            } else {
                re_tag = produced_re;
            }

            Element ELEM = AnnotatedText.createElement(re_tag);
            currentSentence.appendChild(ELEM);

            ELEM.setAttribute(REF, ontID);
            ELEM.setAttribute(ROLE, role);

            addForProperty(ELEM, prop);
            addInterest(ELEM, interest);
            addAssim(ELEM, assimScore);

            ELEM.setTextContent(text);

            slotInComparator(ELEM, prop);
        }
    }

    public void addRE(String ontID, String role) {
        if (currentSentence != null) {
            makeItComparator();

            String re_tag = EmptyRef;

            Element ELEM = AnnotatedText.createElement(re_tag);
            currentSentence.appendChild(ELEM);

            ELEM.setAttribute(REF, ontID);
            ELEM.setAttribute(ROLE, role);

            ELEM.setTextContent("");
        }

    }

    public void addCannedText(String text, String ontID, String prop, String role, String interest, String assimScore) {
        if (currentSentence != null) {
            makeItComparator();

            Element ELEM = AnnotatedText.createElement(CANNEDTEXT);
            currentSentence.appendChild(ELEM);

            ELEM.setAttribute(REF, ontID);
            ELEM.setAttribute(ROLE, role);
            ELEM.setTextContent(text);

            addForProperty(ELEM, prop);
            addInterest(ELEM, interest);
            addAssim(ELEM, assimScore);

            slotInComparator(ELEM, prop);
        }
    }

    public void addText(String text) {
        addText(text, "", "", "", "", "", "false", false);
    }

    public void addText(String text, String prop, String role, String ref, String interest, String assimScore, String isPrep) {
        addText(text, prop, role, ref, interest, assimScore, isPrep, true);
    }

    public void addText(String text, String prop, String role, String ref, String interest, String assimScore, String isPrep, boolean b) {

        if (currentSentence != null) {
            makeItComparator();

            boolean endOfSentence = false;
            boolean endOfPeriod = false;

            if (text.equals(".")) {
                Element punct = AnnotatedText.createElement(PUNCTUATION);
                punct.setTextContent(text);

                endOfSentence = true;
                endOfPeriod = true;

                addStartPeriod(endOfPeriod, punct);
                addStartSentence(endOfSentence);

                slotInComparator(punct, prop);

            } else if (text.equals("and") || text.equals("και")) {
                Element textEl = AnnotatedText.createElement("Text");
                textEl.setTextContent(text);

                endOfSentence = true;
                addStartSentence(endOfSentence, textEl);

                slotInComparator(textEl, prop);
            } else if (text.equals(";")) {
                Element Punct = AnnotatedText.createElement(PUNCTUATION);
                Punct.setTextContent(text);

                endOfSentence = true;
                addStartSentence(endOfSentence, Punct);

                slotInComparator(Punct, prop);
            } else if (text.equals(",")) {
                Element Punct = AnnotatedText.createElement(PUNCTUATION);
                Punct.setTextContent(text);

                endOfSentence = true;
                addStartSentence(endOfSentence, Punct);

                slotInComparator(Punct, prop);

            } else if (text.equals(Aggregation.GREEK_FEMININE_RELATIVE_PRONOUN)
                    || text.equals(Aggregation.GREEK_MASCULINE_RELATIVE_PRONOUN)
                    || text.equals(Aggregation.GREEK_NEUTER_RELATIVE_PRONOUN)
                    || text.equals(Aggregation.GREEK_GENDER_INSENSITIVE_PRONOUN)) {
                Element textEl = AnnotatedText.createElement(RELATIVE_PRONOUN);
                textEl.setTextContent(text);

                textEl.setAttribute(REF, ref);
                textEl.setAttribute(ROLE, role);

                endOfSentence = true;

                if (currentSentence.getLastChild() != null && currentSentence.getLastChild().getTextContent().compareTo(",") == 0) {
                } else {
                    addStartSentence(endOfSentence);
                }

                currentSentence.appendChild(textEl);

                slotInComparator(textEl, prop);
            } else if (text.equals("which")) {
                Element textEl = AnnotatedText.createElement(RELATIVE_PRONOUN);
                textEl.setTextContent(text);

                textEl.setAttribute(REF, ref);
                textEl.setAttribute(ROLE, role);

                currentSentence.appendChild(textEl);

                slotInComparator(textEl, prop);
            } else if (text.equals("that")) {
                Element textEl = AnnotatedText.createElement(AnnotatedDescription.text);
                textEl.setTextContent(text);

                currentSentence.appendChild(textEl);

                slotInComparator(textEl, prop);
            } else {
                Element elem = AnnotatedText.createElement(AnnotatedDescription.text);

                if (isPrep.equals("true")) {
                    elem = AnnotatedText.createElement("Prep");
                } else {
                    elem = AnnotatedText.createElement(AnnotatedDescription.text);
                }

                currentSentence.appendChild(elem);
                elem.setTextContent(text);

                if (b) {
                    addForProperty(elem, prop);
                    addInterest(elem, interest);
                    addAssim(elem, assimScore);
                }

                slotInComparator(elem, prop);
            }
        }
    }

    public void addVerb(String text, String prop, String interest, String assimScore) {
        if (currentSentence != null) {
            makeItComparator();
            Element ELEM = AnnotatedText.createElement(VERB);
            currentSentence.appendChild(ELEM);

            ELEM.setTextContent(text);

            addForProperty(ELEM, prop);
            addInterest(ELEM, interest);
            addAssim(ELEM, assimScore);

            slotInComparator(ELEM, prop);
        }
    }

    public void addWhitespace() {
        if (currentSentence != null) {
            Element elem = AnnotatedText.createElement(WSPACE);
            currentSentence.appendChild(elem);
        }
    }

    public void addStartPeriod() {
        Element elem = AnnotatedText.createElement(AnnotatedDescription.PERIOD);
        currentPeriod = elem;
        AnnTExt.appendChild(elem);
    }

    private void addStartPeriod(boolean endOfPeriod, Element punct) {
        if (endOfPeriod) {
            // create a new period
            Element elem = AnnotatedText.createElement(AnnotatedDescription.PERIOD);

            currentPeriod.appendChild(punct);
            currentPeriod = elem;

            AnnTExt.appendChild(elem);
        }
    }

    public void addStartSentence() {
        //makeitComparator();

        if (!InComparator) {
            // create a new sentence tag
            Element sentenceElem = AnnotatedText.createElement(AnnotatedDescription.SENTENCE);
            // set the new sentence tag as the current sentence
            this.currentSentence = sentenceElem;

            // add it
            if (currentPeriod != null) {
                currentPeriod.appendChild(sentenceElem);
            }
        }

    }

    private void addStartSentence(boolean end_of_sentence) {
        if (end_of_sentence && !InComparator) {

            Element sentenceElem = AnnotatedText.createElement(AnnotatedDescription.SENTENCE);
            this.currentSentence = sentenceElem;

            if (currentPeriod != null) {
                currentPeriod.appendChild(sentenceElem);
            }
        }
    }

    private void addStartSentence(boolean end_of_sentence, Element connector) {
        if (end_of_sentence && !InComparator) {
            //boolean isComp = makeitComparator();

            Element sentenceElem = AnnotatedText.createElement(AnnotatedDescription.SENTENCE);

            this.currentSentence = sentenceElem;

            if (currentPeriod != null) {
                currentPeriod.appendChild(connector);

                currentPeriod.appendChild(sentenceElem);
            }
        } else if (end_of_sentence && InComparator) {
            this.currentSentence.appendChild(connector);
        }
    }

    public void makeItComparator() {
        if (InComparator && currentSentence != null
                && currentSentence.getNodeName().compareTo(AnnotatedDescription.SENTENCE) == 0) {
            Element comparator = AnnotatedText.createElement(AnnotatedDescription.Comparator);
            currentSentence.getParentNode().replaceChild(comparator, currentSentence);

            currentSentence = comparator;
            ComparatorSlotInAnnotationsTree = comparator;
        }
    }

    private void slotInComparator(Node createdSlot, String prop) {
        // marks some of the comparator slots with
        // the property they are coming from
        if (InComparator
                && prop != null
                && prop.compareTo("") != 0
                && prop.compareTo(NLResourceManager.nlowlNS + "Comparator") != 0) {
            ((Element) createdSlot).setAttribute(AnnotatedDescription.forProperty, prop);
        }

        // if there is a "," immediatelly
        // after comparator put it
        // as last slot inside comparator
        if (!InComparator && createdSlot.getTextContent().compareTo(",") == 0) {
            if (createdSlot.getPreviousSibling() != null && createdSlot.getPreviousSibling().getNodeName().compareTo("Comparator") == 0) {
                createdSlot.getPreviousSibling().appendChild(createdSlot);
            }
        }
    }

    // reform comparator
    // when we are done with it.
    public void reformComparator() {

        if (ComparatorSlotInAnnotationsTree != null) {
            // remove unnecessary attributes from comparator tag
            ComparatorSlotInAnnotationsTree.removeAttribute(AnnotatedDescription.Interest);
            ComparatorSlotInAnnotationsTree.removeAttribute(AnnotatedDescription.forProperty);
            ComparatorSlotInAnnotationsTree.removeAttribute(AnnotatedDescription.Assim);

            ComparatorSlotInAnnotationsTree.setAttribute(AnnotatedDescription.REF, StringCompEntities);
            ComparatorSlotInAnnotationsTree.setAttribute(AnnotatedDescription.TURN, this.TurnToSee);

            // determine if it is a positive or negative comparison
            Node feat = ComparatorSlot.getAttributes().getNamedItem("nlowl:Feature");

            if (feat != null) {
                if (!feat.getTextContent().isEmpty()) {
                    ComparatorSlotInAnnotationsTree.setAttribute("type", "negative");
                } else {
                    ComparatorSlotInAnnotationsTree.setAttribute("type", "positive");
                }
            } else {
                ComparatorSlotInAnnotationsTree.setAttribute("type", "positive");
            }


            // clear roles in some of the comparator slots
            // 

            NodeList CompSlots = ComparatorSlotInAnnotationsTree.getChildNodes();
            for (int i = 0; i < CompSlots.getLength(); i++) {
                Node slot = CompSlots.item(i);

                if (slot.getAttributes().getNamedItem(AnnotatedDescription.ROLE) != null
                        && slot.getAttributes().getNamedItem(AnnotatedDescription.forProperty) == null) {
                    slot.getAttributes().removeNamedItem(AnnotatedDescription.ROLE);
                }
            }


            // group two or more NPs to 1

            CompSlots = ComparatorSlotInAnnotationsTree.getChildNodes();
            ArrayList<Node> NPs_Delete = new ArrayList<Node>();
            ArrayList<Node> CompSlots_VEC = new ArrayList<Node>();

            int NP_count = 0;
            String NPs_Content = "";
            int i = 0;

            for (i = 0; i < CompSlots.getLength(); i++) {
                CompSlots_VEC.add(CompSlots.item(i));
            }

            i = 0;
            while (i < CompSlots_VEC.size()) {
                Node slot = CompSlots_VEC.get(i);

                //found the first NP
                if (slot.getNodeName().compareTo("NP") == 0) {
                    NP_count = 1;
                    NPs_Content = slot.getTextContent();
                    NPs_Delete.clear();
                    NPs_Delete.add(slot);

                    if (i + 1 < CompSlots.getLength()) {
                        i++;
                        slot = CompSlots_VEC.get(i);

                        // find the next NPs
                        while (i < CompSlots.getLength() && CompSlots_VEC.get(i).getNodeName().compareTo("NP") == 0) {
                            NP_count++;
                            NPs_Content = NPs_Content + " " + CompSlots_VEC.get(i).getTextContent();
                            NPs_Delete.add(CompSlots_VEC.get(i));

                            // go to next  slot
                            i++;

                        }
                    }

                    if (NP_count > 1) {
                        for (int j = 0; j < NPs_Delete.size() - 1; j++) {
                            NPs_Delete.get(j).getParentNode().removeChild(NPs_Delete.get(j));
                        }

                        NPs_Delete.get(NPs_Delete.size() - 1).setTextContent(NPs_Content);
                    }
                }

                i++;
            }// while  

            // create a new sentence inside 
            // comparator if necessary
            // there will be at most one sentence in the comparator

            CompSlots = ComparatorSlotInAnnotationsTree.getChildNodes();
            CompSlots_VEC = null;
            CompSlots_VEC = new ArrayList<Node>();

            for (i = 0; i < CompSlots.getLength(); i++) {
                CompSlots_VEC.add(CompSlots.item(i));
            }

            boolean found_owner = false;
            boolean sentence_created = false;

            Node the_comp_sent_slot = null;

            i = 0;
            while (i < CompSlots_VEC.size()) {
                Node slot = CompSlots_VEC.get(i);
                //String pre_Prop = "";        
                //String current_Prop = "";

                if (slot.getAttributes() != null && slot.getAttributes().getNamedItem(AnnotatedDescription.forProperty) == null || slot.getAttributes().getNamedItem(AnnotatedDescription.forProperty).getNodeName().compareTo("") == 0) {
                    // do nothing
                } else {   // create a sentence inside comparator

                    String PropURI = ((Element) slot).getAttribute(AnnotatedDescription.forProperty);
                    sentence_created = true;
                    // create a new sentence tag
                    Element SENTENCE_ELEM = AnnotatedText.createElement(AnnotatedDescription.SENTENCE);
                    // set the new sentence tag as the current sentence
                    this.currentSentence = SENTENCE_ELEM;
                    this.currentSentence.setAttribute(AnnotatedDescription.forProperty, PropURI);

                    while (i < CompSlots_VEC.size()) {
                        slot = CompSlots_VEC.get(i);
                        ((Element) slot).removeAttribute(AnnotatedDescription.forProperty);
                        this.currentSentence.appendChild(slot);
                        i++;
                    }

                    ComparatorSlotInAnnotationsTree.appendChild(SENTENCE_ELEM);

                    // we have just added a sentence inside comparator
                    // if this sentence does not contain
                    // an owner then carry the owner 
                    // from the comparator.
                    // the sentence should always have an owner

                    NodeList sentence_slots = SENTENCE_ELEM.getChildNodes();


                    for (int k = 0; k < sentence_slots.getLength(); k++) {
                        Element sent_slot = ((Element) sentence_slots.item(k));

                        if (sent_slot.getAttribute(AnnotatedDescription.ROLE) != null) {
                            if (sent_slot.getAttribute(AnnotatedDescription.ROLE).compareTo(XmlMsgs.OWNER_TAG) == 0) {
                                found_owner = true;

                            }
                        }
                    }

                    the_comp_sent_slot = SENTENCE_ELEM;

                }

                i++;
            }// while

            if (found_owner) {
                // nothing has to be done
            } else if (sentence_created) {
                CompSlots = ComparatorSlotInAnnotationsTree.getChildNodes();
                CompSlots_VEC = null;
                CompSlots_VEC = new ArrayList<Node>();

                for (i = 0; i < CompSlots.getLength(); i++) {
                    CompSlots_VEC.add(CompSlots.item(i));
                }

                i = 0;

                boolean found_NP = false;
                Node node_after_new = null;

                while (i < CompSlots_VEC.size()) {
                    Node slot = CompSlots_VEC.get(i);

                    if (slot.getNodeName().compareTo("NP") == 0) {
                        found_NP = true;
                        ((Element) slot).setAttribute(AnnotatedDescription.ROLE, XmlMsgs.OWNER_TAG);
                        the_comp_sent_slot.insertBefore(slot, the_comp_sent_slot.getFirstChild());
                        node_after_new = slot;
                    } else {
                        if (slot.getNodeName().compareTo(AnnotatedDescription.SENTENCE) != 0 && found_NP) {
                            the_comp_sent_slot.insertBefore(slot, node_after_new);
                        }
                    }

                    i++;
                }
            }
        }
    }

    private void addInterest(Element SentenceSlot, String interest) {
        Element SentenceNode = (Element) SentenceSlot.getParentNode();

        if (SentenceNode.getAttributes() != null) {
            if (SentenceNode.getAttributes().getNamedItem(Interest) == null) {
                SentenceNode.setAttribute(Interest, interest);
            } else {
                if (SentenceNode.getAttributes().getNamedItem(Interest).getTextContent().isEmpty()) {
                    SentenceNode.setAttribute(Interest, interest);
                }
            }
        }

    }

    private void addAssim(Element SentenceSlot, String assim) {
        Element SentenceNode = (Element) SentenceSlot.getParentNode();

        if (SentenceNode.getAttributes() != null) {
            if (SentenceNode.getAttributes().getNamedItem(Assim) == null) {
                SentenceNode.setAttribute(Assim, assim);
            } else {
                if (SentenceNode.getAttributes().getNamedItem(Assim).getTextContent().isEmpty()) {
                    SentenceNode.setAttribute(Assim, assim);
                }
            }
        }

    }

    private void addForProperty(Element SentenceSlot, String property) {
        Element SentenceNode = (Element) SentenceSlot.getParentNode();

        if (SentenceNode.getAttributes() != null) {
            if (SentenceNode.getAttributes().getNamedItem(forProperty) == null) {
                SentenceNode.setAttribute(forProperty, property);
            } else {
                if (SentenceNode.getAttributes().getNamedItem(forProperty).getTextContent().isEmpty()) {
                    SentenceNode.setAttribute(forProperty, property);
                }
            }
        }

    }

    public void removeLastPeriod() {
        if (this.currentPeriod != null) {
            currentPeriod.getParentNode().removeChild(currentPeriod);
        }
    }
}