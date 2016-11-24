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

public class LexEntryVerbEN extends LexEntryVerb {

    private String baseForm;
    private String simplePres3rdSing;
    private String presParticiple;
    private String simplePast;
    private String pastParticiple;

    public LexEntryVerbEN(String bf, String s3rd, String prp, String sp, String pap) {
        baseForm = bf;
        simplePres3rdSing = s3rd;
        presParticiple = prp;
        simplePast = sp;
        pastParticiple = pap;
    }

    public LexEntryVerbEN(LexEntryVerbEN o) {
        baseForm = o.getBaseForm();
        simplePres3rdSing = o.getSimplePresent3rdSingular();
        presParticiple = o.getPresentParticiple();
        simplePast = o.getSimplePast();
        pastParticiple = o.getPastParticiple();
    }

    public LexEntryVerbEN() {
        baseForm = "";
        simplePres3rdSing = "";
        presParticiple = "";
        simplePast = "";
        pastParticiple = "";
    }

    public void setPresentForms(String bf, String s3rd, String prp) {
        this.baseForm = bf;
        this.simplePres3rdSing = s3rd;
        this.presParticiple = prp;
    }

    public void setBaseForm(String bf) {
        this.baseForm = bf;
    }

    public void setSimplePresent3rdSingular(String s3rd) {
        this.simplePres3rdSing = s3rd;
    }

    public void setPresentParticiple(String prp) {
        this.presParticiple = prp;
    }

    public void setPastForms(String sp, String pap) {
        simplePast = sp;
        pastParticiple = pap;
    }

    public void setSimplePast(String sp) {
        this.simplePast = sp;
    }

    public void setPastParticiple(String pap) {
        this.pastParticiple = pap;
    }

    public String getBaseForm() {
        return this.baseForm;
    }

    public String getSimplePresent3rdSingular() {
        return this.simplePres3rdSing;
    }

    public String getPresentParticiple() {
        return this.presParticiple;
    }

    public String getSimplePast() {
        return this.simplePast;
    }

    public String getPastParticiple() {
        return this.pastParticiple;
    }

    public boolean isVowel(char letter) {
        if ((letter == 'a') || (letter == 'e') || (letter == 'i') || (letter == 'o') || (letter == 'u')) {
            return true;
        }
        return false;
    }

    public String addPrefixED(String verb) {
        if (verb.endsWith("e")) {
            return verb + "d";
        } else if ((verb.endsWith("l")) && (isVowel(verb.charAt(verb.length() - 2)))) {
            return verb + "l" + "ed";
        } else if ((verb.endsWith("y")) && (!isVowel(verb.charAt(verb.length() - 2)))) {
            return verb.substring(0, verb.length() - 2) + "i" + "ed";
        } else {
            return verb + "ed";
        }
    }

    public String addPrefixING(String verb) {
        if (verb.endsWith("ee")) {
            return verb + "ing";
        } else if (verb.endsWith("e")) {
            return verb.substring(0, verb.length() - 2) + "ing";
        } else if ((verb.endsWith("l")) && (isVowel(verb.charAt(verb.length() - 2)))) {
            return verb + "l" + "ing";
        } else if (verb.endsWith("ie")) {
            return verb.substring(0, verb.length() - 3) + "y" + "ing";
        } else {
            return verb + "ing";
        }
    }

    public String get(String voice, String tense, String person, String number) {
        if (voice.equals(XmlMsgs.ACTIVE_VOICE)) {
            if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
                if (number.equals(XmlMsgs.SINGULAR)) {
                    if (person.equals(XmlMsgs.PERSON_3RD)) {
                        return getSimplePresent3rdSingular();
                    }
                    return getBaseForm();
                } else if (number.equals(XmlMsgs.PLURAL)) {
                    return getBaseForm();
                }
            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
                return getPresentParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
                return getPastParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
                return getPastParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
                return getPresentParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
                return getPresentParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
                return getBaseForm();
            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
                return getPresentParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
                return getPastParticiple();
            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
                return getPresentParticiple();
            }
        } else if (voice.equals(XmlMsgs.PASSIVE_VOICE)) {
            return getPastParticiple();
        }
        return "";
    }
}