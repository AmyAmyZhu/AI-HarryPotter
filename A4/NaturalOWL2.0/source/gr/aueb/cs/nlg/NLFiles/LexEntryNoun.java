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

public abstract class LexEntryNoun extends LexEntry {

    private String gender;
    private String number;

    public LexEntryNoun() {
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String g) {
        if (g.equals(GENDER_MASCULINE) || g.equals(GENDER_FEMININE) || g.equals(GENDER_NEUTER) || g.equals(GENDER_MASCULINE_OR_FEMININE)) {
            gender = g;
        } else {
            System.err.println("Error in defining gender: unexpected value found " + g);
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String n) {
        if (n.equals(NUMBER_ONLY_SINGLE) || n.equals(NUMBER_ONLY_PLURAL) || n.equals(NUMBER_BOTH)) {
            number = n;
        } else {
            System.err.println("Error in defining number: unexpected value found " + n);
        }
    }

    public abstract String get(String Case, String numb);
}
