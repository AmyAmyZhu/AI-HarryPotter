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

import org.semanticweb.owlapi.model.IRI;

public class SentencePlansList {

    private String language;
    private ArrayList<SentencePlan> SentencePlanList;

    SentencePlansList(String language, ArrayList<SentencePlan> ML) {
        this.language = language;
        this.SentencePlanList = ML;
    }

    SentencePlansList(String language) {
        this.language = language;
        this.SentencePlanList = new ArrayList<SentencePlan>();
    }

    public int size() {
        return SentencePlanList.size();
    }

    public void add(SentencePlan m) {
        this.SentencePlanList.add(m);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setSentencePlansList(ArrayList<SentencePlan> ML) {
        this.SentencePlanList = ML;
    }

    public ArrayList<SentencePlan> getSentencePlansList() {
        return this.SentencePlanList;
    }

    public SentencePlan getSentencePlan(int i) {
        return this.SentencePlanList.get(i);
    }

    public boolean containsSentencePlan(IRI iri) {
        for (SentencePlan plan : SentencePlanList) {
            if (plan.getSentencePlanIRI().equals(iri)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeSentencePlan(IRI iri) {
        for (int i = 0; i < SentencePlanList.size(); i++) {
            if (SentencePlanList.get(i).getSentencePlanIRI().equals(iri)) {
                SentencePlanList.remove(i);
                return true;
            }
        }
        return false;
    }

    public SentencePlan getSentencePlan(IRI iri) {
        for (SentencePlan plan : SentencePlanList) {
            if (plan.getSentencePlanIRI().equals(iri)) {
                return plan;
            }
        }
        return null;
    }

    public void setSentencePlan(int i, SentencePlan M) {
        this.SentencePlanList.set(i, M);
    }
}//SentencePlansList