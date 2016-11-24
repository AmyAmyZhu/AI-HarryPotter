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

import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class LexEntryNounEN extends LexEntryNoun {

    private String singular;
    private String plural;

    public LexEntryNounEN(String singular, String plural, String g, String num) {
        this.singular = singular;
        this.plural = plural;
        this.setGender(g);
        this.setNumber(num);
    }

    public LexEntryNounEN(String g, String num) {
        this.singular = "";
        this.plural = "";
        this.setGender(g);
        this.setNumber(num);
    }

    public LexEntryNounEN(LexEntryNounEN o) {
        this.singular = o.getSingular();
        this.plural = o.getPlural();
        this.setGender(o.getGender());
        this.setNumber(o.getNumber());
    }

    public LexEntryNounEN() {
        this.singular = "";
        this.plural = "";
        this.setGender(GENDER_MASCULINE_OR_FEMININE);
        this.setNumber(NUMBER_BOTH);
    }

    public void setSingular(String s) {
        singular = s;
    }

    public void setPlural(String p) {
        plural = p;
    }

    public void setSingPlural(String s, String p) {
        singular = s;
        plural = p;
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

    public String get(String caseType, String number) {
        String ret = "";
        if (this.getNumber().equals(LexEntry.NUMBER_ONLY_SINGLE)) {
            ret = this.singular;
        } else if (this.getNumber().equals(LexEntry.NUMBER_ONLY_PLURAL)) {
            ret = this.plural;
        } else if (number.equals(XmlMsgs.SINGULAR)) {
            ret = this.singular;
        } else if (number.equals(XmlMsgs.PLURAL)) {
            ret = this.plural;
        }

        if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
            if (ret.endsWith("s")) {
                ret += "'";
                return ret;
            }
            ret += "'s";
            return ret;
        }
        return ret;
    }
}
