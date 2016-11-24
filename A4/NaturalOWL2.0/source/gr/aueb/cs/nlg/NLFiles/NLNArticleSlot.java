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

import org.semanticweb.owlapi.model.NodeID;

public class NLNArticleSlot extends NLNSlot {

    private boolean definite;
    private String caseType;
    private String gender;
    private String number;
    private NodeID agreesWithID;

    public NLNArticleSlot(boolean def, String cas, String gen, String num, NodeID agreeID, NodeID id, int o) {
        super(id, o);

        this.definite = def;
        this.caseType = cas;
        this.gender = gen;
        this.number = num;
        this.agreesWithID = agreeID;
    }

    public NLNArticleSlot(NLNArticleSlot o) {
        super(o.getId(), o.getOrder());

        this.definite = o.isDefinite();
        this.caseType = o.getCase();
        this.gender = o.getGender();
        this.number = o.getNumber();
        this.agreesWithID = o.getAgreesWithID();
    }

    public String getCase() {
        return caseType;
    }

    public void setCase(String cas) {
        this.caseType = cas;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isDefinite() {
        return definite;
    }

    public void setDefinite(boolean definite) {
        this.definite = definite;
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
