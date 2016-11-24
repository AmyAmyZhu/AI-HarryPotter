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

import org.semanticweb.owlapi.model.NodeID;

public class NLNPrepositionSlot extends NLNSlot {

    public static final String PREPOSITION_EN_ABOARD = "aboard";
    public static final String PREPOSITION_EN_ABOUT = "about";
    public static final String PREPOSITION_EN_ABOVE = "above";
    public static final String PREPOSITION_EN_ACROSS = "across";
    public static final String PREPOSITION_EN_AFTER = "after";
    public static final String PREPOSITION_EN_AGAINST = "against";
    public static final String PREPOSITION_EN_ALONG = "along";
    public static final String PREPOSITION_EN_AMID = "amid";
    public static final String PREPOSITION_EN_AMONG = "among";
    public static final String PREPOSITION_EN_ANTI = "anti";
    public static final String PREPOSITION_EN_AROUND = "around";
    public static final String PREPOSITION_EN_AS = "as";
    public static final String PREPOSITION_EN_AT = "at";
    public static final String PREPOSITION_EN_BEFORE = "before";
    public static final String PREPOSITION_EN_BEHIND = "behind";
    public static final String PREPOSITION_EN_BELOW = "below";
    public static final String PREPOSITION_EN_BENEATH = "beneath";
    public static final String PREPOSITION_EN_BESIDE = "beside";
    public static final String PREPOSITION_EN_BETWEEN = "between";
    public static final String PREPOSITION_EN_BEYOND = "beyond";
    public static final String PREPOSITION_EN_BUT = "but";
    public static final String PREPOSITION_EN_BY = "by";
    public static final String PREPOSITION_EN_DESPITE = "despite";
    public static final String PREPOSITION_EN_DOWN = "down";
    public static final String PREPOSITION_EN_DURING = "during";
    public static final String PREPOSITION_EN_EXCEPT = "except";
    public static final String PREPOSITION_EN_FOR = "for";
    public static final String PREPOSITION_EN_FROM = "from";
    public static final String PREPOSITION_EN_IN = "in";
    public static final String PREPOSITION_EN_INSIDE = "inside";
    public static final String PREPOSITION_EN_INTO = "into";
    public static final String PREPOSITION_EN_LIKE = "like";
    public static final String PREPOSITION_EN_MINUS = "minus";
    public static final String PREPOSITION_EN_NEAR = "near";
    public static final String PREPOSITION_EN_OF = "of";
    public static final String PREPOSITION_EN_OFF = "off";
    public static final String PREPOSITION_EN_ON = "on";
    public static final String PREPOSITION_EN_ONTO = "onto";
    public static final String PREPOSITION_EN_OPPOSITE = "opposite";
    public static final String PREPOSITION_EN_OUTSIDE = "outside";
    public static final String PREPOSITION_EN_OVER = "over";
    public static final String PREPOSITION_EN_PAST = "past";
    public static final String PREPOSITION_EN_PER = "per";
    public static final String PREPOSITION_EN_PLUS = "plus";
    public static final String PREPOSITION_EN_ROUND = "round";
    public static final String PREPOSITION_EN_SAVE = "save";
    public static final String PREPOSITION_EN_SINCE = "since";
    public static final String PREPOSITION_EN_THAN = "than";
    public static final String PREPOSITION_EN_THROUGH = "through";
    public static final String PREPOSITION_EN_TO = "to";
    public static final String PREPOSITION_EN_TOWARD = "toward";
    public static final String PREPOSITION_EN_TOWARDS = "towards";
    public static final String PREPOSITION_EN_UNDER = "under";
    public static final String PREPOSITION_EN_UNLIKE = "unlike";
    public static final String PREPOSITION_EN_UNTIL = "until";
    public static final String PREPOSITION_EN_UP = "up";
    public static final String PREPOSITION_EN_UPON = "upon";
    public static final String PREPOSITION_EN_VIA = "via";
    public static final String PREPOSITION_EN_WITH = "with";
    public static final String PREPOSITION_EN_WITHIN = "within";
    public static final String PREPOSITION_EN_WITHOUT = "without";
    public static final String PREPOSITION_GR_ANEU = "άνευ";
    public static final String PREPOSITION_GR_ANTI = "αντί";
    public static final String PREPOSITION_GR_APO = "από";
    public static final String PREPOSITION_GR_GIA = "για";
    public static final String PREPOSITION_GR_DIXWS = "δίχως";
    public static final String PREPOSITION_GR_EKTOS = "εκτός";
    public static final String PREPOSITION_GR_EXAITIAS = "εξαιτίας";
    public static final String PREPOSITION_GR_EWS = "έως";
    public static final String PREPOSITION_GR_KATA = "κατά";
    public static final String PREPOSITION_GR_ME = "με";
    public static final String PREPOSITION_GR_METAXY = "μεταξύ";
    public static final String PREPOSITION_GR_META = "μετά";
    public static final String PREPOSITION_GR_MEXRI = "μέχρι";
    public static final String PREPOSITION_GR_PRIN = "πριν";
    public static final String PREPOSITION_GR_PARA = "παρά";
    public static final String PREPOSITION_GR_PRO = "προ";
    public static final String PREPOSITION_GR_PROS = "προς";
    public static final String PREPOSITION_GR_SAN = "σαν";
    public static final String PREPOSITION_GR_SE = "σε";
    public static final String PREPOSITION_GR_YPER = "υπέρ";
    public static final String PREPOSITION_GR_WS = "ως";
    private String prep;

    public NLNPrepositionSlot(String prep, NodeID id, int o) {
        super(id, o);
        this.prep = prep;
    }

    public NLNPrepositionSlot(NLNPrepositionSlot o) {
        super(o.getId(), o.getOrder());
        this.prep = o.getPrep();
    }

    public String getPrep() {
        return prep;
    }

    public void setPrep(String prep) {
        this.prep = prep;
    }

    public static ArrayList<String> getEnglishPrepositionList() {
        ArrayList<String> prepositionList = new ArrayList<String>();

        prepositionList.add(PREPOSITION_EN_ABOARD);
        prepositionList.add(PREPOSITION_EN_ABOUT);
        prepositionList.add(PREPOSITION_EN_ABOVE);
        prepositionList.add(PREPOSITION_EN_ACROSS);
        prepositionList.add(PREPOSITION_EN_AFTER);
        prepositionList.add(PREPOSITION_EN_AGAINST);
        prepositionList.add(PREPOSITION_EN_ALONG);
        prepositionList.add(PREPOSITION_EN_AMID);
        prepositionList.add(PREPOSITION_EN_AMONG);
        prepositionList.add(PREPOSITION_EN_ANTI);
        prepositionList.add(PREPOSITION_EN_AROUND);
        prepositionList.add(PREPOSITION_EN_AS);
        prepositionList.add(PREPOSITION_EN_AT);
        prepositionList.add(PREPOSITION_EN_BEFORE);
        prepositionList.add(PREPOSITION_EN_BEHIND);
        prepositionList.add(PREPOSITION_EN_BELOW);
        prepositionList.add(PREPOSITION_EN_BENEATH);
        prepositionList.add(PREPOSITION_EN_BESIDE);
        prepositionList.add(PREPOSITION_EN_BETWEEN);
        prepositionList.add(PREPOSITION_EN_BEYOND);
        prepositionList.add(PREPOSITION_EN_BUT);
        prepositionList.add(PREPOSITION_EN_BY);
        prepositionList.add(PREPOSITION_EN_DESPITE);
        prepositionList.add(PREPOSITION_EN_DOWN);
        prepositionList.add(PREPOSITION_EN_DURING);
        prepositionList.add(PREPOSITION_EN_EXCEPT);
        prepositionList.add(PREPOSITION_EN_FOR);
        prepositionList.add(PREPOSITION_EN_FROM);
        prepositionList.add(PREPOSITION_EN_IN);
        prepositionList.add(PREPOSITION_EN_INSIDE);
        prepositionList.add(PREPOSITION_EN_INTO);
        prepositionList.add(PREPOSITION_EN_LIKE);
        prepositionList.add(PREPOSITION_EN_MINUS);
        prepositionList.add(PREPOSITION_EN_NEAR);
        prepositionList.add(PREPOSITION_EN_OF);
        prepositionList.add(PREPOSITION_EN_OFF);
        prepositionList.add(PREPOSITION_EN_ON);
        prepositionList.add(PREPOSITION_EN_ONTO);
        prepositionList.add(PREPOSITION_EN_OPPOSITE);
        prepositionList.add(PREPOSITION_EN_OUTSIDE);
        prepositionList.add(PREPOSITION_EN_OVER);
        prepositionList.add(PREPOSITION_EN_PAST);
        prepositionList.add(PREPOSITION_EN_PER);
        prepositionList.add(PREPOSITION_EN_PLUS);
        prepositionList.add(PREPOSITION_EN_ROUND);
        prepositionList.add(PREPOSITION_EN_SAVE);
        prepositionList.add(PREPOSITION_EN_SINCE);
        prepositionList.add(PREPOSITION_EN_THAN);
        prepositionList.add(PREPOSITION_EN_THROUGH);
        prepositionList.add(PREPOSITION_EN_TO);
        prepositionList.add(PREPOSITION_EN_TOWARD);
        prepositionList.add(PREPOSITION_EN_TOWARDS);
        prepositionList.add(PREPOSITION_EN_UNDER);
        prepositionList.add(PREPOSITION_EN_UNLIKE);
        prepositionList.add(PREPOSITION_EN_UNTIL);
        prepositionList.add(PREPOSITION_EN_UP);
        prepositionList.add(PREPOSITION_EN_UPON);
        prepositionList.add(PREPOSITION_EN_VIA);
        prepositionList.add(PREPOSITION_EN_WITH);
        prepositionList.add(PREPOSITION_EN_WITHIN);
        prepositionList.add(PREPOSITION_EN_WITHOUT);

        return prepositionList;
    }

    public static ArrayList<String> getGreekPrepositionList() {
        ArrayList<String> prepositionList = new ArrayList<String>();

        prepositionList.add(PREPOSITION_GR_ANEU);
        prepositionList.add(PREPOSITION_GR_ANTI);
        prepositionList.add(PREPOSITION_GR_APO);
        prepositionList.add(PREPOSITION_GR_GIA);
        prepositionList.add(PREPOSITION_GR_DIXWS);
        prepositionList.add(PREPOSITION_GR_EKTOS);
        prepositionList.add(PREPOSITION_GR_EXAITIAS);
        prepositionList.add(PREPOSITION_GR_EWS);
        prepositionList.add(PREPOSITION_GR_KATA);
        prepositionList.add(PREPOSITION_GR_ME);
        prepositionList.add(PREPOSITION_GR_METAXY);
        prepositionList.add(PREPOSITION_GR_META);
        prepositionList.add(PREPOSITION_GR_MEXRI);
        prepositionList.add(PREPOSITION_GR_PRIN);
        prepositionList.add(PREPOSITION_GR_PARA);
        prepositionList.add(PREPOSITION_GR_PRO);
        prepositionList.add(PREPOSITION_GR_PROS);
        prepositionList.add(PREPOSITION_GR_SAN);
        prepositionList.add(PREPOSITION_GR_SE);
        prepositionList.add(PREPOSITION_GR_YPER);
        prepositionList.add(PREPOSITION_GR_WS);

        return prepositionList;
    }
}
