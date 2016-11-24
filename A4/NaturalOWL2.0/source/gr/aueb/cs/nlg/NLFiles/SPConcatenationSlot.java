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

import java.util.ArrayList;

import java.util.Collections;
import org.semanticweb.owlapi.model.NodeID;

public class SPConcatenationSlot extends SPSlot {

    private ArrayList<SPConcatenationPropertySlot> propertySlots;

    public SPConcatenationSlot(NodeID id, int o) {
        super(id, o);

        this.propertySlots = new ArrayList<SPConcatenationPropertySlot>();
    }

    public SPConcatenationSlot(SPConcatenationSlot o) {
        super(o.getId(), o.getOrder());

        this.propertySlots = new ArrayList<SPConcatenationPropertySlot>();
        for (SPConcatenationPropertySlot concat : o.getPropertySlots()) {
            this.propertySlots.add(new SPConcatenationPropertySlot(concat));
        }
    }

    public void concatenateProperty(SPConcatenationPropertySlot cpSlot) {
        this.propertySlots.add(cpSlot);
    }

    public void swapPropertyOrder(SPConcatenationPropertySlot cpSlot1, SPConcatenationPropertySlot cpSlot2) {
        int order1 = cpSlot1.getOrder();
        int order2 = cpSlot2.getOrder();

        cpSlot1.setOrder(order2);
        cpSlot2.setOrder(order1);
    }

    public void removeProperty(int index) {
        this.propertySlots.remove(index);
    }

    public ArrayList<SPConcatenationPropertySlot> getPropertySlots() {
        return this.propertySlots;
    }

    public ArrayList<SPConcatenationPropertySlot> getSortedPropertySlots() {
        ArrayList<SPConcatenationPropertySlot> sortedPropertySlots = new ArrayList<SPConcatenationPropertySlot>(this.propertySlots);
        Collections.sort(sortedPropertySlots);
        return sortedPropertySlots;
    }

    public String toString() {
        return "CONCATENATION SLOT: " + propertySlots.toString();
    }
}