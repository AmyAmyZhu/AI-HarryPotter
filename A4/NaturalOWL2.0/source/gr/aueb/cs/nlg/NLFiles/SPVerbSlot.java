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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;

public class SPVerbSlot extends SPSlot {

    public static String POLARITY_POSITIVE = "true";
    public static String POLARITY_NEGATIVE = "false";
    public static String POLARITY_FILLER = "filler";
    private IRI lexiconEntryIRI;
    private String tense;
    private String voice;
    private String polarity;
    private String number;
    private String person;
    private NodeID agreesWithID;

    public SPVerbSlot(IRI leIRI, String ten, String voi, String pol, String num, String per, NodeID agreeID, NodeID id, int o) {
        super(id, o);

        this.lexiconEntryIRI = leIRI;
        this.tense = ten;
        this.voice = voi;
        this.polarity = pol;
        this.number = num;
        this.person = per;
        this.agreesWithID = agreeID;
    }

    public SPVerbSlot(SPVerbSlot o) {
        super(o.getId(), o.getOrder());

        this.lexiconEntryIRI = o.getLexiconEntryIRI();
        this.tense = o.getTense();
        this.voice = o.getVoice();
        this.polarity = o.getPolarity();
        this.number = o.getNumber();
        this.person = o.getPerson();
        this.agreesWithID = o.getAgreesWithID();
    }

    public IRI getLexiconEntryIRI() {
        return lexiconEntryIRI;
    }

    public void setLexiconEntryIRI(IRI lexiconEntryIRI) {
        this.lexiconEntryIRI = lexiconEntryIRI;
    }

    public String getTense() {
        return tense;
    }

    public void setTense(String tense) {
        this.tense = tense;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getPolarity() {
        return polarity;
    }

    public void setPolarity(String polarity) {
        this.polarity = polarity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public NodeID getAgreesWithID() {
        return agreesWithID;
    }

    public void setAgreesWithID(NodeID agreesWithID) {
        this.agreesWithID = agreesWithID;
    }

    public String toString() {
        return "VERB: "
                + "LexiconEntryIRI = " + lexiconEntryIRI
                + ", Tense = " + tense
                + ", Voice = " + voice
                + ", Polarity = " + polarity;
    }
}
