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
package gr.aueb.cs.nlg.Utils;

import java.util.HashMap;

import gr.aueb.cs.nlg.NLFiles.UserModel;

import org.semanticweb.owlapi.model.IRI;

public class NLGUser {

    String id;
    UserModel UM;
    private HashMap<IRI, Integer> entitiesMentionedCount;           // [resourceID  --- > counter]
    private HashMap<Fact, Integer> factMentionedCount;              // [factID  --- > counter]
    private HashMap<IRI, Integer> sentencePlansUseCount;            // [planID  --- > counter]
    private HashMap<IRI, Integer> NLNameUseCount;                   // [nameID  --- > counter]

    public NLGUser(String id, UserModel UM) {
        this.id = id;
        this.UM = UM;

        entitiesMentionedCount = new HashMap<IRI, Integer>();
        factMentionedCount = new HashMap<Fact, Integer>();
        sentencePlansUseCount = new HashMap<IRI, Integer>();
        NLNameUseCount = new HashMap<IRI, Integer>();
    }

    public int getEntityMentionedCount(IRI entityIRI) {
        if (entitiesMentionedCount.containsKey(entityIRI)) {
            return entitiesMentionedCount.get(entityIRI);
        }
        return 0;
    }

    public int getFactMentionedCount(Fact factID) {
        if (factMentionedCount.keySet().contains(factID)) {
            return factMentionedCount.get(factID);
        }
        return 0;
    }

    public int getSentencePlanUseCount(IRI sentencePlanIRI) {
        if (sentencePlansUseCount.containsKey(sentencePlanIRI)) {
            return sentencePlansUseCount.get(sentencePlanIRI);
        }
        return 0;
    }

    public int getNLNameUseCount(IRI NLNameIRI) {
        if (NLNameUseCount.containsKey(NLNameIRI)) {
            return NLNameUseCount.get(NLNameIRI);
        }
        return 0;
    }

    public void increaseEntityMentionedCount(IRI entityIRI) {
        if (entitiesMentionedCount.containsKey(entityIRI)) {
            int value = entitiesMentionedCount.get(entityIRI);
            entitiesMentionedCount.put(entityIRI, value + 1);
        } else {
            entitiesMentionedCount.put(entityIRI, 1);
        }
    }

    public void increaseFactMentionedCount(Fact factID) {
        if (factMentionedCount.containsKey(factID)) {
            int value = factMentionedCount.get(factID);
            factMentionedCount.put(factID, value + 1);
        } else {
            factMentionedCount.put(factID, 1);
        }
    }

    public void increaseSentencePlanUseCount(IRI sentencePlanIRI) {
        if (sentencePlansUseCount.containsKey(sentencePlanIRI)) {
            int value = sentencePlansUseCount.get(sentencePlanIRI);
            sentencePlansUseCount.put(sentencePlanIRI, value + 1);
        } else {
            sentencePlansUseCount.put(sentencePlanIRI, 1);
        }
    }

    public void increaseNLNameUseCount(IRI NLNameIRI) {
        if (NLNameUseCount.containsKey(NLNameIRI)) {
            int value = NLNameUseCount.get(NLNameIRI);
            NLNameUseCount.put(NLNameIRI, value + 1);
        } else {
            NLNameUseCount.put(NLNameIRI, 1);
        }
    }

    public void resetCounters() {
        entitiesMentionedCount = new HashMap<IRI, Integer>();
        factMentionedCount = new HashMap<Fact, Integer>();
        sentencePlansUseCount = new HashMap<IRI, Integer>();
        NLNameUseCount = new HashMap<IRI, Integer>();
    }

    public UserModel getUserModel() {
        return UM;
    }

    public void setUserModel(UserModel UM) {
        this.UM = UM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
