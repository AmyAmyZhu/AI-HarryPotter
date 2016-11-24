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
package gr.aueb.cs.nlg.Languages;

import gr.aueb.cs.nlg.NLGEngine.Aggregation;
import gr.aueb.cs.nlg.NLGEngine.ContentSelection;
import gr.aueb.cs.nlg.NLGEngine.Lexicalisation;
import gr.aueb.cs.nlg.NLGEngine.ReferringExpressionsGenerator;
import gr.aueb.cs.nlg.NLGEngine.SurfaceRealization;

public class Languages {

    public final static String ENGLISH = "en";
    public final static String GREEK = "el";
    public final static String GREEK_ALTERNATIVE = "gr";

    public static boolean isValid(String lang) {
        if (lang.equals(ENGLISH) || lang.equals(GREEK) || lang.equals(GREEK_ALTERNATIVE)) {
            return true;
        }
        return false;
    }

    public static boolean isGreek(String str) {
        if (str.equals(GREEK) || str.equals(GREEK_ALTERNATIVE)) {
            return true;
        }

        return false;
    }

    public static boolean isEnglish(String str) {
        if (str.equals(ENGLISH)) {
            return true;
        }
        return false;
    }

    public static void updateLanguages(ContentSelection CS, Lexicalisation LEX, ReferringExpressionsGenerator GRE, SurfaceRealization SR, Aggregation AGGR, String lang) {
        if (CS != null && LEX != null && GRE != null && SR != null && AGGR != null) {
            CS.setLanguage(lang);
            LEX.setLanguage(lang);
            GRE.setLanguage(lang);
            SR.setLanguage(lang);
            AGGR.setLanguage(lang);
        }
    }
}