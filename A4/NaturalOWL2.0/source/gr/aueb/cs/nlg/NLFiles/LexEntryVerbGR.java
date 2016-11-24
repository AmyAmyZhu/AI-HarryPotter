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

public class LexEntryVerbGR extends LexEntryVerb {

    private String activeSimplePresent1stSingular;
    private String activeSimplePresent2ndSingular;
    private String activeSimplePresent3rdSingular;
    private String activeSimplePresent1stPlural;
    private String activeSimplePresent2ndPlural;
    private String activeSimplePresent3rdPlural;
    private String activeSimplePast1stSingular;
    private String activeSimplePast2ndSingular;
    private String activeSimplePast3rdSingular;
    private String activeSimplePast1stPlural;
    private String activeSimplePast2ndPlural;
    private String activeSimplePast3rdPlural;
    private String activePastContinuous1stSingular;
    private String activePastContinuous2ndSingular;
    private String activePastContinuous3rdSingular;
    private String activePastContinuous1stPlural;
    private String activePastContinuous2ndPlural;
    private String activePastContinuous3rdPlural;
    private String activeSimpleFuture1stSingular;
    private String activeSimpleFuture2ndSingular;
    private String activeSimpleFuture3rdSingular;
    private String activeSimpleFuture1stPlural;
    private String activeSimpleFuture2ndPlural;
    private String activeSimpleFuture3rdPlural;
    private String activeInfinitive;
    private String activeParticiple;
    private String passiveSimplePresent1stSingular;
    private String passiveSimplePresent2ndSingular;
    private String passiveSimplePresent3rdSingular;
    private String passiveSimplePresent1stPlural;
    private String passiveSimplePresent2ndPlural;
    private String passiveSimplePresent3rdPlural;
    private String passiveSimplePast1stSingular;
    private String passiveSimplePast2ndSingular;
    private String passiveSimplePast3rdSingular;
    private String passiveSimplePast1stPlural;
    private String passiveSimplePast2ndPlural;
    private String passiveSimplePast3rdPlural;
    private String passivePastContinuous1stSingular;
    private String passivePastContinuous2ndSingular;
    private String passivePastContinuous3rdSingular;
    private String passivePastContinuous1stPlural;
    private String passivePastContinuous2ndPlural;
    private String passivePastContinuous3rdPlural;
    private String passiveSimpleFuture1stSingular;
    private String passiveSimpleFuture2ndSingular;
    private String passiveSimpleFuture3rdSingular;
    private String passiveSimpleFuture1stPlural;
    private String passiveSimpleFuture2ndPlural;
    private String passiveSimpleFuture3rdPlural;
    private String passiveInfinitive;
    private String passiveParticiple;

    public LexEntryVerbGR() {
        activeSimplePresent1stSingular = "";
        activeSimplePresent2ndSingular = "";
        activeSimplePresent3rdSingular = "";
        activeSimplePresent1stPlural = "";
        activeSimplePresent2ndPlural = "";
        activeSimplePresent3rdPlural = "";

        activeSimplePast1stSingular = "";
        activeSimplePast2ndSingular = "";
        activeSimplePast3rdSingular = "";
        activeSimplePast1stPlural = "";
        activeSimplePast2ndPlural = "";
        activeSimplePast3rdPlural = "";

        activePastContinuous1stSingular = "";
        activePastContinuous2ndSingular = "";
        activePastContinuous3rdSingular = "";
        activePastContinuous1stPlural = "";
        activePastContinuous2ndPlural = "";
        activePastContinuous3rdPlural = "";

        activeSimpleFuture1stSingular = "";
        activeSimpleFuture2ndSingular = "";
        activeSimpleFuture3rdSingular = "";
        activeSimpleFuture1stPlural = "";
        activeSimpleFuture2ndPlural = "";
        activeSimpleFuture3rdPlural = "";

        activeInfinitive = "";
        activeParticiple = "";

        passiveSimplePresent1stSingular = "";
        passiveSimplePresent2ndSingular = "";
        passiveSimplePresent3rdSingular = "";
        passiveSimplePresent1stPlural = "";
        passiveSimplePresent2ndPlural = "";
        passiveSimplePresent3rdPlural = "";

        passiveSimplePast1stSingular = "";
        passiveSimplePast2ndSingular = "";
        passiveSimplePast3rdSingular = "";
        passiveSimplePast1stPlural = "";
        passiveSimplePast2ndPlural = "";
        passiveSimplePast3rdPlural = "";

        passivePastContinuous1stSingular = "";
        passivePastContinuous2ndSingular = "";
        passivePastContinuous3rdSingular = "";
        passivePastContinuous1stPlural = "";
        passivePastContinuous2ndPlural = "";
        passivePastContinuous3rdPlural = "";

        passiveSimpleFuture1stSingular = "";
        passiveSimpleFuture2ndSingular = "";
        passiveSimpleFuture3rdSingular = "";
        passiveSimpleFuture1stPlural = "";
        passiveSimpleFuture2ndPlural = "";
        passiveSimpleFuture3rdPlural = "";

        passiveInfinitive = "";
        passiveParticiple = "";
    }

    public LexEntryVerbGR(LexEntryVerbGR o) {
        activeSimplePresent1stSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        activeSimplePresent2ndSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        activeSimplePresent3rdSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        activeSimplePresent1stPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        activeSimplePresent2ndPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        activeSimplePresent3rdPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        activeSimplePast1stSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        activeSimplePast2ndSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        activeSimplePast3rdSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        activeSimplePast1stPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        activeSimplePast2ndPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        activeSimplePast3rdPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        activePastContinuous1stSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        activePastContinuous2ndSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        activePastContinuous3rdSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        activePastContinuous1stPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        activePastContinuous2ndPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        activePastContinuous3rdPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        activeSimpleFuture1stSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        activeSimpleFuture2ndSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        activeSimpleFuture3rdSingular = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        activeSimpleFuture1stPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        activeSimpleFuture2ndPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        activeSimpleFuture3rdPlural = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        activeInfinitive = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", "");
        activeParticiple = o.get(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", "");

        passiveSimplePresent1stSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        passiveSimplePresent2ndSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        passiveSimplePresent3rdSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        passiveSimplePresent1stPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        passiveSimplePresent2ndPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        passiveSimplePresent3rdPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        passiveSimplePast1stSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        passiveSimplePast2ndSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        passiveSimplePast3rdSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        passiveSimplePast1stPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        passiveSimplePast2ndPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        passiveSimplePast3rdPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        passivePastContinuous1stSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        passivePastContinuous2ndSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        passivePastContinuous3rdSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        passivePastContinuous1stPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        passivePastContinuous2ndPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        passivePastContinuous3rdPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        passiveSimpleFuture1stSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR);
        passiveSimpleFuture2ndSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR);
        passiveSimpleFuture3rdSingular = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR);
        passiveSimpleFuture1stPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL);
        passiveSimpleFuture2ndPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL);
        passiveSimpleFuture3rdPlural = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL);

        passiveInfinitive = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_INFINITIVE, "", "");
        passiveParticiple = o.get(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PARTICIPLE, "", "");
    }

    public void setActiveSimplePresentForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.activeSimplePresent1stSingular = sing1st;
        this.activeSimplePresent2ndSingular = sing2nd;
        this.activeSimplePresent3rdSingular = sing3rd;
        this.activeSimplePresent1stPlural = pl1st;
        this.activeSimplePresent2ndPlural = pl2nd;
        this.activeSimplePresent3rdPlural = pl3rd;
    }

    public void setActiveSimplePastForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.activeSimplePast1stSingular = sing1st;
        this.activeSimplePast2ndSingular = sing2nd;
        this.activeSimplePast3rdSingular = sing3rd;
        this.activeSimplePast1stPlural = pl1st;
        this.activeSimplePast2ndPlural = pl2nd;
        this.activeSimplePast3rdPlural = pl3rd;
    }

    public void setActivePastContinuousForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.activePastContinuous1stSingular = sing1st;
        this.activePastContinuous2ndSingular = sing2nd;
        this.activePastContinuous3rdSingular = sing3rd;
        this.activePastContinuous1stPlural = pl1st;
        this.activePastContinuous2ndPlural = pl2nd;
        this.activePastContinuous3rdPlural = pl3rd;
    }

    public void setActiveSimpleFutureForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.activeSimpleFuture1stSingular = sing1st;
        this.activeSimpleFuture2ndSingular = sing2nd;
        this.activeSimpleFuture3rdSingular = sing3rd;
        this.activeSimpleFuture1stPlural = pl1st;
        this.activeSimpleFuture2ndPlural = pl2nd;
        this.activeSimpleFuture3rdPlural = pl3rd;
    }

    public void setActiveInfinitive(String inf) {
        this.activeInfinitive = inf;
    }

    public void setActiveParticiple(String par) {
        this.activeParticiple = par;
    }

    public void setPassiveSimplePresentForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.passiveSimplePresent1stSingular = sing1st;
        this.passiveSimplePresent2ndSingular = sing2nd;
        this.passiveSimplePresent3rdSingular = sing3rd;
        this.passiveSimplePresent1stPlural = pl1st;
        this.passiveSimplePresent2ndPlural = pl2nd;
        this.passiveSimplePresent3rdPlural = pl3rd;
    }

    public void setPassiveSimplePastForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.passiveSimplePast1stSingular = sing1st;
        this.passiveSimplePast2ndSingular = sing2nd;
        this.passiveSimplePast3rdSingular = sing3rd;
        this.passiveSimplePast1stPlural = pl1st;
        this.passiveSimplePast2ndPlural = pl2nd;
        this.passiveSimplePast3rdPlural = pl3rd;
    }

    public void setPassivePastContinuousForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.passivePastContinuous1stSingular = sing1st;
        this.passivePastContinuous2ndSingular = sing2nd;
        this.passivePastContinuous3rdSingular = sing3rd;
        this.passivePastContinuous1stPlural = pl1st;
        this.passivePastContinuous2ndPlural = pl2nd;
        this.passivePastContinuous3rdPlural = pl3rd;
    }

    public void setPassiveSimpleFutureForms(String sing1st, String sing2nd, String sing3rd, String pl1st, String pl2nd, String pl3rd) {
        this.passiveSimpleFuture1stSingular = sing1st;
        this.passiveSimpleFuture2ndSingular = sing2nd;
        this.passiveSimpleFuture3rdSingular = sing3rd;
        this.passiveSimpleFuture1stPlural = pl1st;
        this.passiveSimpleFuture2ndPlural = pl2nd;
        this.passiveSimpleFuture3rdPlural = pl3rd;
    }

    public void setPassiveInfinitive(String inf) {
        this.passiveInfinitive = inf;
    }

    public void setPassiveParticiple(String par) {
        this.passiveParticiple = par;
    }

    public void set(String voice, String tense, String person, String number, String form) {
        if (voice.compareTo(XmlMsgs.ACTIVE_VOICE) == 0) {
            if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PRESENT) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimplePresent1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimplePresent2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimplePresent3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimplePresent1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimplePresent2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimplePresent3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PAST) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimplePast1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimplePast2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimplePast3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimplePast1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimplePast2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimplePast3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_CONTINUOUS) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activePastContinuous1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activePastContinuous2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activePastContinuous3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activePastContinuous1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activePastContinuous2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activePastContinuous3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_FUTURE) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimpleFuture1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimpleFuture2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimpleFuture3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.activeSimpleFuture1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.activeSimpleFuture2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.activeSimpleFuture3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_INFINITIVE) == 0) {
                this.activeInfinitive = form;
            } else if (tense.compareTo(XmlMsgs.TENSE_PARTICIPLE) == 0) {
                this.activeParticiple = form;
            }
        } else if (voice.compareTo(XmlMsgs.PASSIVE_VOICE) == 0) {
            if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PRESENT) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimplePresent1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimplePresent2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimplePresent3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimplePresent1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimplePresent2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimplePresent3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PAST) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimplePast1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimplePast2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimplePast3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimplePast1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimplePast2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimplePast3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_CONTINUOUS) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passivePastContinuous1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passivePastContinuous2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passivePastContinuous3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passivePastContinuous1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passivePastContinuous2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passivePastContinuous3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_FUTURE) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimpleFuture1stSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimpleFuture2ndSingular = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimpleFuture3rdSingular = form;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        this.passiveSimpleFuture1stPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        this.passiveSimpleFuture2ndPlural = form;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        this.passiveSimpleFuture3rdPlural = form;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_INFINITIVE) == 0) {
                this.passiveInfinitive = form;
            } else if (tense.compareTo(XmlMsgs.TENSE_PARTICIPLE) == 0) {
                this.passiveParticiple = form;
            }
        }
    }

    public String get(String voice, String tense, String person, String number) {
        if (voice.compareTo(XmlMsgs.ACTIVE_VOICE) == 0) {
            if ((tense.compareTo(XmlMsgs.TENSE_SIMPLE_PRESENT) == 0) || tense.compareTo(XmlMsgs.TENSE_FUTURE_CONTINUOUS) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimplePresent1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimplePresent2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimplePresent3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimplePresent1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimplePresent2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimplePresent3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PRESENT_PERFECT) == 0) {
                return this.activeInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PAST) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimplePast1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimplePast2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimplePast3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimplePast1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimplePast2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimplePast3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_CONTINUOUS) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activePastContinuous1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activePastContinuous2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activePastContinuous3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activePastContinuous1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activePastContinuous2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activePastContinuous3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_PERFECT) == 0) {
                return this.activeInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_FUTURE) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimpleFuture1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimpleFuture2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimpleFuture3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.activeSimpleFuture1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.activeSimpleFuture2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.activeSimpleFuture3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_FUTURE_PERFECT) == 0) {
                return this.activeInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_INFINITIVE) == 0) {
                return this.activeInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_PARTICIPLE) == 0) {
                return this.activeParticiple;
            }
        } else if (voice.compareTo(XmlMsgs.PASSIVE_VOICE) == 0) {
            if ((tense.compareTo(XmlMsgs.TENSE_SIMPLE_PRESENT) == 0) || (tense.compareTo(XmlMsgs.TENSE_FUTURE_CONTINUOUS) == 0)) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimplePresent1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimplePresent2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimplePresent3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimplePresent1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimplePresent2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimplePresent3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PRESENT_PERFECT) == 0) {
                return this.passiveInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_PAST) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimplePast1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimplePast2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimplePast3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimplePast1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimplePast2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimplePast3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_CONTINUOUS) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passivePastContinuous1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passivePastContinuous2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passivePastContinuous3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passivePastContinuous1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passivePastContinuous2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passivePastContinuous3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_PAST_PERFECT) == 0) {
                return this.passiveInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_SIMPLE_FUTURE) == 0) {
                if (number.compareTo(XmlMsgs.SINGULAR) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimpleFuture1stSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimpleFuture2ndSingular;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimpleFuture3rdSingular;
                    }
                } else if (number.compareTo(XmlMsgs.PLURAL) == 0) {
                    if (person.compareTo(XmlMsgs.PERSON_1ST) == 0) {
                        return this.passiveSimpleFuture1stPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_2ND) == 0) {
                        return this.passiveSimpleFuture2ndPlural;
                    } else if (person.compareTo(XmlMsgs.PERSON_3RD) == 0) {
                        return this.passiveSimpleFuture3rdPlural;
                    }
                }
            } else if (tense.compareTo(XmlMsgs.TENSE_FUTURE_PERFECT) == 0) {
                return this.passiveInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_INFINITIVE) == 0) {
                return this.passiveInfinitive;
            } else if (tense.compareTo(XmlMsgs.TENSE_PARTICIPLE) == 0) {
                return this.passiveParticiple;
            }
        }
        return "";
    }
}