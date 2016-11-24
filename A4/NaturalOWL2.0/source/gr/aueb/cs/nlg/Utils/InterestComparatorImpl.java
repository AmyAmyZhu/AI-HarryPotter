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

import org.w3c.dom.Node;

public class InterestComparatorImpl extends InterestComparator {

    private boolean ascending;

    public InterestComparatorImpl(boolean a) {
        ascending = a;
    }

    public void setAsc(boolean a) {
        ascending = a;
    }

    public boolean getAsc() {
        return ascending;
    }

    public int compare(Object o1, Object o2) {
        return compare((Node) o1, (Node) o2);
    }

    public int compare(Node a, Node b) {
        int interestA = Integer.parseInt(XmlMsgs.getAttribute(a, XmlMsgs.prefix, XmlMsgs.INTEREST));
        int interestB = Integer.parseInt(XmlMsgs.getAttribute(b, XmlMsgs.prefix, XmlMsgs.INTEREST));

        int ret = interestA - interestB;

        if (ascending) {
            return ret;
        }
        return (-ret);
    }
}//InterestComparatorImpl