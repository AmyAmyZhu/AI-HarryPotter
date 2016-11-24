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

import gr.aueb.cs.nlg.Utils.XmlMsgs;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;

public class NLNAdjectiveSlot extends NLNSlot {

    private IRI lexiconEntryIRI;
    private String caseType;
    private String gender;
    private String number;
    private boolean isHead;
    private boolean capitalized;
    private NodeID agreesWithID;

    public NLNAdjectiveSlot(IRI leIRI, String cas, String gen, String num, boolean head, boolean capitalized, NodeID agreeID, NodeID id, int o) {
        super(id, o);

        this.lexiconEntryIRI = leIRI;
        this.caseType = cas;
        this.gender = gen;
        this.number = num;
        this.isHead = head;
        this.capitalized = capitalized;
        this.agreesWithID = agreeID;
    }

    public NLNAdjectiveSlot(NLNAdjectiveSlot o) {
        super(o.getId(), o.getOrder());

        this.lexiconEntryIRI = o.getLexiconEntryIRI();
        this.caseType = o.getCase();
        this.gender = o.getGender();
        this.isHead = o.isHead();
        this.capitalized = o.isCapitalized();
        this.number = o.getNumber();
        this.agreesWithID = o.getAgreesWithID();
    }

    public IRI getLexiconEntryIRI() {
        return lexiconEntryIRI;
    }

    public void setLexiconEntryIRI(IRI lexiconEntryIRI) {
        this.lexiconEntryIRI = lexiconEntryIRI;
    }

    public String getCase() {
        return caseType;
    }

    public void setCase(String cas) {
        this.caseType = cas;
    }

    public String getGender() {
        if (gender.isEmpty() && isHead) {
            return XmlMsgs.GENDER_NEUTER;
        }
        return gender;
    }

    public void setGender(String gen) {
        this.gender = gen;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        this.isHead = head;
    }

    public boolean isCapitalized() {
        return capitalized;
    }

    public void setCapitalized(boolean capitalized) {
        this.capitalized = capitalized;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public NodeID getAgreesWithID() {
        return agreesWithID;
    }

    public void setAgreesWithID(NodeID agreesWithID) {
        this.agreesWithID = agreesWithID;
    }
}
