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

public class NLName implements Comparable<NLName> {

    public static final int REGULAR_ARTICLE = 0;
    public static final int FORCE_NO_ARTICLE = 1;
    public static final int FORCE_DEF_ARTICLE = 2;
    public static final int FORCE_INDEF_ARTICLE = 3;
    private ArrayList<NLNSlot> slotsList;
    private IRI NLNameIRI;
    boolean aggAllowed;
    boolean focusLost;
    private String language;
    boolean isGenerated;

    public NLName(ArrayList<NLNSlot> SL, IRI NIRI, boolean aggAllowed, boolean focusLost, String language) {
        this.slotsList = SL;
        this.NLNameIRI = NIRI;
        this.aggAllowed = aggAllowed;
        this.focusLost = focusLost;
        this.language = language;

        isGenerated = false;
    }

    public NLName(ArrayList<NLNSlot> SL, IRI NIRI, boolean aggAllowed, boolean focusLost, String language, boolean isGenerated) {
        this.slotsList = SL;
        this.NLNameIRI = NIRI;
        this.aggAllowed = aggAllowed;
        this.focusLost = focusLost;
        this.language = language;

        this.isGenerated = isGenerated;
    }

    public NLName() {
        slotsList = null;
    }

    public NLNSlot getHeadSlot() {
        for (NLNSlot slot : slotsList) {
            if (slot instanceof NLNNounSlot) {
                if (((NLNNounSlot) slot).isHead()) {
                    return slot;
                }
            } else if (slot instanceof NLNAdjectiveSlot) {
                if (((NLNAdjectiveSlot) slot).isHead()) {
                    return slot;
                }
            }
        }
        return null;
    }

    public NLNArticleSlot getArticle() {
        for (NLNSlot slot : slotsList) {
            if (slot instanceof NLNArticleSlot) {
                return (NLNArticleSlot) slot;
            }
        }
        return null;
    }

    public void setLanguage(String lang) {
        language = lang;
    }

    public String getLanguage() {
        return language;
    }

    public void setSlotslist(ArrayList<NLNSlot> NL) {
        this.slotsList = NL;
    }

    public ArrayList<NLNSlot> getSlotsList() {
        return this.slotsList;
    }

    public void setAggAllowed(boolean aggAllowed) {
        this.aggAllowed = aggAllowed;
    }

    public boolean getAggAllowed() {
        return this.aggAllowed;
    }

    public void setFocusLost(boolean focusLost) {
        this.focusLost = focusLost;
    }

    public boolean getFocusLost() {
        return this.focusLost;
    }

    public IRI getNLNameIRI() {
        return this.NLNameIRI;
    }

    public void setNLNameIRI(IRI nURi) {
        this.NLNameIRI = nURi;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public int compareTo(NLName name) {
        return this.getNLNameIRI().toString().compareTo(name.getNLNameIRI().toString());
    }
}