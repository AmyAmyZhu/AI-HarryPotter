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

import java.util.ArrayList;
import org.semanticweb.owlapi.model.IRI;

public class Fact {

    private IRI sub;
    private IRI predicate;
    private String obj;
    private ArrayList<String> modifiers;
    private int cardinality = -1;
    private boolean polarity = true;

    public Fact(IRI s, IRI p, String o) {
        sub = s;
        predicate = p;
        obj = o;

        modifiers = new ArrayList<String>();
    }

    public Fact(String fact) {
        int index1 = fact.indexOf(',', 0);
        int index2 = fact.indexOf(',', index1 + 2);
        //System.out.println("index1:" + index1 + " index2:" + index2);

        sub = IRI.create(fact.substring(1, index1));
        predicate = IRI.create(fact.substring(index1 + 2, index2));
        obj = fact.substring(index2 + 2, fact.length() - 1);

        modifiers = new ArrayList<String>();
    }

    public String getFact() {
        return this.toString();
    }

    public IRI getSubject() {
        return sub;
    }

    public IRI getPredicate() {
        return predicate;
    }

    public String getObject() {
        return obj;
    }

    public static IRI getSubject(String fact) {
        Fact fct = new Fact(fact);
        return fct.getSubject();
    }

    public static IRI getPredicate(String fact) {
        Fact fct = new Fact(fact);
        return fct.getPredicate();
    }

    public static String getObject(String fact) {
        Fact fct = new Fact(fact);
        return fct.getObject();
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public boolean isPolarity() {
        return polarity;
    }

    public void setPolarity(boolean polarity) {
        this.polarity = polarity;
    }

    public ArrayList<String> getModifiers() {
        return modifiers;
    }

    public void addModifier(String modifier) {
        this.modifiers.add(modifier);
    }

    public boolean hasModifier(String modifier) {
        return modifiers.contains(modifier);
    }

    public static ArrayList<String> parseModifier(String modifier) {
        ArrayList<String> parsed = new ArrayList<String>();

        int start = modifier.indexOf('(');
        int end = modifier.indexOf(',');
        if (end == -1) {
            end = modifier.length() - 1;
        }

        while ((start != -1) && (end != -1) && (start < modifier.length())) {
            parsed.add(modifier.substring(start + 1, end));
            start = end + 1;
            end = modifier.indexOf(',', end + 1);
            if (end == -1) {
                end = modifier.length() - 1;
            }
        }

        return parsed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }

        Fact f = (Fact) o;

        if (this.sub.equals(f.sub)) {
            if (this.predicate.equals(f.predicate)) {
                ArrayList<String> thisValues = new ArrayList<String>();
                if (this.obj.startsWith("and(")) {
                    thisValues = parseModifier(this.obj);
                } else if (this.obj.startsWith("or(")) {
                    thisValues = parseModifier(this.obj);
                } else {
                    thisValues.add(this.obj);
                }
                ArrayList<String> oValues = new ArrayList<String>();
                if (f.obj.startsWith("and(")) {
                    oValues = parseModifier(f.obj);
                } else if (f.obj.startsWith("or(")) {
                    oValues = parseModifier(f.obj);
                } else {
                    oValues.add(f.obj);
                }
                if (thisValues.size() == oValues.size()) {
                    for (String modifier : thisValues) {
                        if (!oValues.contains(modifier)) {
                            return false;
                        }
                    }
                    for (String modifier : oValues) {
                        if (!thisValues.contains(modifier)) {
                            return false;
                        }
                    }

                    if (this.cardinality == f.cardinality) {
                        if (this.polarity == f.polarity) {
                            for (String modifier : this.modifiers) {
                                if (!modifier.equals(XmlMsgs.NOT_MODIFIER)) {
                                    if (!f.modifiers.contains(modifier)) {
                                        return false;
                                    }
                                }
                            }
                            for (String modifier : f.modifiers) {
                                if (!modifier.equals(XmlMsgs.NOT_MODIFIER)) {
                                    if (!this.modifiers.contains(modifier)) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.sub != null ? this.sub.hashCode() : 0);
        hash = 29 * hash + (this.predicate != null ? this.predicate.hashCode() : 0);
        hash = 29 * hash + (this.obj != null ? this.obj.hashCode() : 0);
        hash = 29 * hash + (this.modifiers != null ? this.modifiers.hashCode() : 0);
        hash = 29 * hash + this.cardinality;
        hash = 29 * hash + (this.polarity ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "[" + sub.toString() + ", " + predicate.toString() + ", " + obj.toString() + "]";
    }
}
