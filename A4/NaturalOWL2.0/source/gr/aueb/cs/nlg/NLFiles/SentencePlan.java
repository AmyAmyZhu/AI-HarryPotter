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

public class SentencePlan implements Comparable<SentencePlan> {

    private ArrayList<SPSlot> SlotsList;
    private IRI SentencePlanIRI;
    private boolean AggAllowed;
    private String language;
    boolean isGenerated;

    public SentencePlan(ArrayList<SPSlot> SL, IRI SIRI, boolean AggAllowed, String language) {
        this.SlotsList = SL;
        this.SentencePlanIRI = SIRI;
        this.AggAllowed = AggAllowed;
        this.language = language;

        this.isGenerated = false;
    }

    public SentencePlan(ArrayList<SPSlot> SL, IRI SIRI, boolean AggAllowed, String language, boolean isGenerated) {
        this.SlotsList = SL;
        this.SentencePlanIRI = SIRI;
        this.AggAllowed = AggAllowed;
        this.language = language;

        this.isGenerated = isGenerated;
    }

    public SentencePlan() {
        SlotsList = null;
    }

    public void setLanguage(String lang) {
        language = lang;
    }

    public String getLanguage() {
        return language;
    }

    public void setSlotslist(ArrayList<SPSlot> SL) {
        this.SlotsList = SL;
    }

    public ArrayList<SPSlot> getSlotsList() {
        return this.SlotsList;
    }

    public void setAggAllowed(boolean AggAllowed) {
        this.AggAllowed = AggAllowed;
    }

    public boolean getAggAllowed() {
        return this.AggAllowed;
    }

    public IRI getSentencePlanIRI() {
        return this.SentencePlanIRI;
    }

    public void setSentencePlanIRI(IRI sURi) {
        this.SentencePlanIRI = sURi;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public int compareTo(SentencePlan sp) {
        return this.getSentencePlanIRI().toString().compareTo(sp.getSentencePlanIRI().toString());
    }
}