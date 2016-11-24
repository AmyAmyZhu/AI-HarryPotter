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
import org.w3c.dom.NamedNodeMap;

public class OrderComparatorImpl extends OrderComparator {

    private boolean ascending;

    public OrderComparatorImpl(boolean asc) {
        ascending = asc;
    }

    public void setAscending(boolean asc) {
        ascending = asc;
    }

    public boolean getAscending() {
        return ascending;
    }

    public int compare(Object o1, Object o2) {
        return compare((Node) o1, (Node) o2);
    }

    public int compare(Node a, Node b) {
        NamedNodeMap NNM_a = a.getAttributes();
        NamedNodeMap NNM_b = b.getAttributes();

        Node Order_a = NNM_a.getNamedItem(XmlMsgs.prefix + ":" + XmlMsgs.ORDER_TAG);//XmlMsgs.ORDER_TAG);
        Node Order_b = NNM_b.getNamedItem(XmlMsgs.prefix + ":" + XmlMsgs.ORDER_TAG);//XmlMsgs.SECTION_TAG);

        Node Section_a = NNM_a.getNamedItem(XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG);//XmlMsgs.ORDER_TAG);
        Node Section_b = NNM_b.getNamedItem(XmlMsgs.prefix + ":" + XmlMsgs.SECTION_TAG);//XmlMsgs.SECTION_TAG);

        String property_b = b.getNodeName();

        boolean sameProperty = false;

        if (a.getNodeName() != null && property_b != null) {
            if (a.getNodeName().compareTo(property_b) == 0) {
                sameProperty = true;
            }

        }

        int a_global_order = 0;
        int b_global_order = 0;

        int a_sec = 0;
        int b_sec = 0;

        if (Section_a == null) {
            a_sec = 100000;
        } else {
            a_sec = 1000 * Integer.parseInt(Section_a.getTextContent());
        }

        if (Section_b == null) {
            b_sec = 100000;
        } else {
            b_sec = 1000 * Integer.parseInt(Section_b.getTextContent());
        }

        if (Order_a == null) {
            a_global_order = 10000;
        } else {
            a_global_order = 100 * Integer.parseInt(Order_a.getTextContent());


            if (sameProperty) {
                a_global_order += (a.getNodeName().hashCode() / Integer.MAX_VALUE);
            }
        }

        if (Order_b == null) {
            b_global_order = 10000;
        } else {
            b_global_order = 100 * Integer.parseInt(Order_b.getTextContent());

            if (sameProperty) {
                b_global_order += 1;
            }
        }
        a_global_order += a_sec;
        b_global_order += b_sec;

        int ret = a_global_order - b_global_order;

        if (ascending) {
            return ret;
        }
        return (-ret);
    }
}