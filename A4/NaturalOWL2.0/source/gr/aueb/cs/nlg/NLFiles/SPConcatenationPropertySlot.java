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

public class SPConcatenationPropertySlot extends SPSlot {

    private IRI propertyIRI;
    private String caseType;

    public SPConcatenationPropertySlot(IRI prpIRI, String cType, NodeID id, int o) {
        super(id, o);

        this.propertyIRI = prpIRI;
        this.caseType = cType;
    }

    public SPConcatenationPropertySlot(SPConcatenationPropertySlot o) {
        super(o.getId(), o.getOrder());

        this.propertyIRI = o.getPropertyIRI();
        this.caseType = o.getCaseType();
    }

    public IRI getPropertyIRI() {
        return propertyIRI;
    }

    public void setPropertyIRI(IRI propertyIRI) {
        this.propertyIRI = propertyIRI;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String toString() {
        return "{"
                + "PropertyIRI = " + propertyIRI
                + ", CaseType = " + caseType + "}";
    }
}