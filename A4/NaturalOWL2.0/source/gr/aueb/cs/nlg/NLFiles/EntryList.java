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

import gr.aueb.cs.nlg.Languages.Languages;

public class EntryList {

    private LexEntry LE_EN;
    private LexEntry LE_GR;

    public EntryList(LexEntry LE_EN, LexEntry LE_GR) {
        this.LE_EN = LE_EN;
        this.LE_GR = LE_GR;
    }

    public EntryList() {
        this.LE_EN = null;
        this.LE_GR = null;
    }

    public void setEntry(String lang, Object b) {
        if (Languages.isEnglish(lang)) {
            this.LE_EN = (LexEntry) b;

        } else if (Languages.isGreek(lang)) {
            this.LE_GR = (LexEntry) b;
        }
    }

    public LexEntry getEntry(String lang) {
        if (Languages.isEnglish(lang)) {
            return this.LE_EN;
        } else if (Languages.isGreek(lang)) {
            return this.LE_GR;
        }
        return null;
    }
}