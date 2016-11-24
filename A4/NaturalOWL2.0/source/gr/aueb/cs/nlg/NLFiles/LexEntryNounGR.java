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

public class LexEntryNounGR extends LexEntryNoun {

    private String singularNom;
    private String singularGen;
    private String singularAcc;
    private String pluralNom;
    private String pluralGen;
    private String pluralAcc;

    public LexEntryNounGR(String sn, String sg, String sa, String pn, String pg, String pa, String gender, String number) {
        singularNom = sn;
        singularGen = sg;
        singularAcc = sa;
        pluralNom = pn;
        pluralGen = pg;
        pluralAcc = pa;

        setGender(gender);
        setNumber(number);
    }

    public LexEntryNounGR(String gender, String number) {
        singularNom = "";
        singularGen = "";
        singularAcc = "";
        pluralNom = "";
        pluralGen = "";
        pluralAcc = "";

        setGender(gender);
        setNumber(number);
    }

    public LexEntryNounGR(LexEntryNounGR o) {
        singularNom = o.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR);
        singularGen = o.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.SINGULAR);
        singularAcc = o.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.SINGULAR);
        pluralNom = o.get(XmlMsgs.NOMINATIVE_TAG, XmlMsgs.PLURAL);
        pluralGen = o.get(XmlMsgs.GENITIVE_TAG, XmlMsgs.PLURAL);
        pluralAcc = o.get(XmlMsgs.ACCUSATIVE_TAG, XmlMsgs.PLURAL);

        setGender(o.getGender());
        setNumber(o.getNumber());
    }

    public LexEntryNounGR() {
        singularNom = "";
        singularGen = "";
        singularAcc = "";
        pluralNom = "";
        pluralGen = "";
        pluralAcc = "";

        setGender(GENDER_MASCULINE_OR_FEMININE);
        setNumber(NUMBER_BOTH);
    }

    public void setSingularCases(String nom, String gen, String acc) {
        this.singularNom = nom;
        this.singularGen = gen;
        this.singularAcc = acc;
    }

    public void setSingularNominative(String nom) {
        this.singularNom = nom;
    }

    public void setSingularGenitive(String gen) {
        this.singularGen = gen;
    }

    public void setSingularAccusative(String acc) {
        this.singularAcc = acc;
    }

    public void setPluralCases(String nom, String gen, String acc) {
        this.pluralNom = nom;
        this.pluralGen = gen;
        this.pluralAcc = acc;
    }

    public void setPluralNominative(String nom) {
        this.pluralNom = nom;
    }

    public void setPluralGenitive(String gen) {
        this.pluralGen = gen;
    }

    public void setPluralAccusative(String acc) {
        this.pluralAcc = acc;
    }

    public String get(String caseType, String numb) {
        if (numb.compareTo(XmlMsgs.SINGULAR) == 0) {
            if (caseType.compareTo(XmlMsgs.NOMINATIVE_TAG) == 0) {
                return this.singularNom;
            } else if (caseType.compareTo(XmlMsgs.GENITIVE_TAG) == 0) {
                return this.singularGen;
            } else if (caseType.compareTo(XmlMsgs.ACCUSATIVE_TAG) == 0) {
                return this.singularAcc;
            }
        } else if (numb.compareTo(XmlMsgs.PLURAL) == 0) {
            if (caseType.compareTo(XmlMsgs.NOMINATIVE_TAG) == 0) {
                return this.pluralNom;
            } else if (caseType.compareTo(XmlMsgs.GENITIVE_TAG) == 0) {
                return this.pluralGen;
            } else if (caseType.compareTo(XmlMsgs.ACCUSATIVE_TAG) == 0) {
                return this.pluralAcc;
            }
        }
        return "";
    }
}