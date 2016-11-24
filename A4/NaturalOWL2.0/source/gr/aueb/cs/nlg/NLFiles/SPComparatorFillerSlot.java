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

public class SPComparatorFillerSlot extends SPSlot {

    private String caseType;
    private boolean isMany;

    public SPComparatorFillerSlot(String cType, boolean isMany, NodeID id, int o) {
        super(id, o);

        this.caseType = cType;
        this.isMany = isMany;
    }

    public SPComparatorFillerSlot(SPComparatorFillerSlot o) {
        super(o.getId(), o.getOrder());

        this.caseType = o.getCase();
    }

    public void setCase(String Case) {
        this.caseType = Case;
    }

    public String getCase() {
        return caseType;
    }

    public boolean isMany() {
        return isMany;
    }

    public void setIsMany(boolean isMany) {
        this.isMany = isMany;
    }

    public String toString() {
        return "COMPARATORFILLER: ";
    }
}