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

public class SPSlot implements Comparable<SPSlot> {

    public static final String NONE_AGREE = "none";
    public static final String anonymousIndivPattern = "slot";
    public static final String concatAnonymousIndivPattern = "conslot";
    public static final int PROPERTY_OWNER = 0;
    public static final int PROPERTY_FILLER = 1;
    public static final int STRING = 2;
    public static final int LEXICON_ENTRY = 3;
    public static final int PREPOSITION = 4;
    public static final int CONCATENATION = 5;
    public static final int CLASS_DSCRPT = 6;
    private NodeID id;
    private int order;

    public SPSlot(NodeID id, int o) {
        this.setId(id);
        this.setOrder(o);
    }

    public SPSlot(SPSlot o) {
        this.setId(o.getId());
        this.setOrder(o.getOrder());
    }

    public final void setOrder(int o) {
        this.order = o;
    }

    public int getOrder() {
        return order;
    }

    public int compareTo(SPSlot o) {
        if (o != null) {
            if (this.order < o.order) {
                return -1;
            }
            if (this.order > o.order) {
                return 1;
            }
        }
        return 0;
    }

    public NodeID getId() {
        return id;
    }

    public final void setId(NodeID id) {
        this.id = id;
    }

    public void increaseOrder() {
        this.order++;
    }

    public void decreaseOrder() {
        this.order--;
    }
}