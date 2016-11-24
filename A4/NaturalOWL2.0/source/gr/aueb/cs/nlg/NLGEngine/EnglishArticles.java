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
package gr.aueb.cs.nlg.NLGEngine;

import gr.aueb.cs.nlg.NLFiles.LexEntry;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class EnglishArticles {

    public static final String ARTICLE_THE = "the"; //definite_article
    public static final String ARTICLE_A = "a";    //indefinite article
    public static final String ARTICLE_AN = "an";    //indefinite article 
    // third person english pronouns
    public static final String singNomΜascArticle = "he";
    public static final String singΑccuΜascArticle = "him";
    public static final String sing_Gen_masc = "his";
    public static final String singNomFemArticle = "she";
    public static final String singAccuFemArticle = "her";
    public static final String singGenFemArticle = "hers";
    public static final String singNomNeuterArticle = "it";
    public static final String singAccuNeuterArticle = "it";
    public static final String singGenNeuterArticle = "its";
    public static final String singNomMascOrFemArticle = "he/she";
    public static final String singAccuMascOrFemArticle = "him/her";
    public static final String singGenMascOrFemArticle = "his/hers";
    public static final String plurNomArticle = "they";
    public static final String plurAccuArticle = "them";
    public static final String plurGenArticle = "theirs";
    //Demonstrative pronouns
    public static final String THIS = "this";
    public static final String THESE = "these";

    public static String getDefiniteArticle() {
        return ARTICLE_THE;
    }

    public static String getIndefiniteArticle(String followingText) {
        if (!followingText.trim().isEmpty()) {
            char ch = followingText.charAt(0);
            if (ch == 'a' || ch == 'e' || ch == 'o' || ch == 'i' || ch == 'u'
                    || ch == 'A' || ch == 'E' || ch == 'O' || ch == 'I' || ch == 'U') {
                return ARTICLE_AN;
            }
        }
        return ARTICLE_A;
    }

    public static String getPronoun(String Case, String number, String gender) {
        String prn = "NOT FOUND PRONOUN";

        if (number.equals(XmlMsgs.SINGULAR)) {
            if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                    prn = singNomΜascArticle;
                } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                    prn = singNomFemArticle;
                } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                    prn = singNomNeuterArticle;
                } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                    prn = singNomMascOrFemArticle;
                }
            } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                    prn = singΑccuΜascArticle;
                } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                    prn = singAccuFemArticle;
                } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                    prn = singAccuNeuterArticle;
                } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                    prn = singAccuMascOrFemArticle;
                }
            } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                    prn = sing_Gen_masc;
                } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                    prn = singGenFemArticle;
                } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                    prn = singGenNeuterArticle;
                } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                    prn = singGenMascOrFemArticle;
                }
            }
        } else if (number.equals(XmlMsgs.PLURAL)) {
            if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                prn = plurNomArticle;
            } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                prn = plurAccuArticle;
            } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                prn = plurGenArticle;
            }
        }

        return prn;
    }

    public static String getDemonstrativePronoun(String number) {
        String prn = "NOT FOUND PRONOUN";

        if (number.equals(XmlMsgs.SINGULAR)) {
            prn = THIS;

        } else if (number.equals(XmlMsgs.PLURAL)) {
            prn = THESE;
        }

        return prn;
    }
}//EnglishArticles

