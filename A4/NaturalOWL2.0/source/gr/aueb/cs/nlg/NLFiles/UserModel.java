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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

public final class UserModel {

    static public final int USER_MODEL_UNDEFINED = -1;
    static public final int USER_MODEL_INTEREST_NO_SHOW = 0;
    static public final int USER_MODEL_MAX_INTEREST = 3;
    static public final int USER_MODEL_DEFAULT_INTEREST = 1;
    static public final int USER_MODEL_DEFAULT_REPETITIONS = 1;
    static public final int USER_MODEL_DEFAULT_APPROPRIATENESS = 1;
    static public final int USER_MODEL_ALL_MESSAGES_PER_PAGE = -1;
    static public final int USER_MODEL_INFINITE_REPETITIONS = 0;
    private IRI UMIRI;
    private int maxMessagesPerSentence;
    private int maxMessagesPerPage;
    //private String Lang;
    //Sentence Plan and NLName Appropriateness
    private HashMap<IRI, Integer> sentencePlanAppropriateness;
    private HashMap<IRI, Integer> NLNameAppropriateness;
    //Interest and Repetitions
    //Global Level
    private int globalInterest;
    private int globalRepetitions;
    //Property Level
    private HashMap<UMPrpLevelKey, Integer> propertyLevelInterests;
    private HashMap<UMPrpLevelKey, Integer> propertyLevelRepetitions;
    //Class Level
    private HashMap<UMPrpClassLevelKey, Integer> classLevelInterests;
    private HashMap<UMPrpClassLevelKey, Integer> classLevelRepetitions;
    //Instance Level
    private HashMap<UMPrpInstanceLevelKey, Integer> instanceLevelInterests;
    private HashMap<UMPrpInstanceLevelKey, Integer> instanceLevelRepetitions;

    public UserModel(IRI UMIRI, int maxMessagesPerSentence, int maxMessagesPerPage) {
        this.UMIRI = UMIRI;

        this.maxMessagesPerSentence = maxMessagesPerSentence;
        this.maxMessagesPerPage = maxMessagesPerPage;
        this.globalInterest = USER_MODEL_DEFAULT_INTEREST;
        this.globalRepetitions = USER_MODEL_DEFAULT_REPETITIONS;

        sentencePlanAppropriateness = new HashMap<IRI, Integer>();
        NLNameAppropriateness = new HashMap<IRI, Integer>();

        propertyLevelInterests = new HashMap<UMPrpLevelKey, Integer>();
        propertyLevelRepetitions = new HashMap<UMPrpLevelKey, Integer>();

        classLevelInterests = new HashMap<UMPrpClassLevelKey, Integer>();
        classLevelRepetitions = new HashMap<UMPrpClassLevelKey, Integer>();

        instanceLevelInterests = new HashMap<UMPrpInstanceLevelKey, Integer>();
        instanceLevelRepetitions = new HashMap<UMPrpInstanceLevelKey, Integer>();
    }

    public UserModel(IRI UMIRI) {
        this.UMIRI = UMIRI;

        this.maxMessagesPerSentence = 3;
        this.maxMessagesPerPage = USER_MODEL_ALL_MESSAGES_PER_PAGE;
        this.globalInterest = USER_MODEL_DEFAULT_INTEREST;
        this.globalRepetitions = USER_MODEL_DEFAULT_REPETITIONS;

        sentencePlanAppropriateness = new HashMap<IRI, Integer>();
        NLNameAppropriateness = new HashMap<IRI, Integer>();

        propertyLevelInterests = new HashMap<UMPrpLevelKey, Integer>();
        propertyLevelRepetitions = new HashMap<UMPrpLevelKey, Integer>();

        classLevelInterests = new HashMap<UMPrpClassLevelKey, Integer>();
        classLevelRepetitions = new HashMap<UMPrpClassLevelKey, Integer>();

        instanceLevelInterests = new HashMap<UMPrpInstanceLevelKey, Integer>();
        instanceLevelRepetitions = new HashMap<UMPrpInstanceLevelKey, Integer>();
    }

    public void addSentencePlan(IRI sentencePlanIRI) {
        sentencePlanAppropriateness.put(sentencePlanIRI, USER_MODEL_DEFAULT_APPROPRIATENESS);
    }

    public void addNLName(IRI NLNameIRI) {
        NLNameAppropriateness.put(NLNameIRI, USER_MODEL_DEFAULT_APPROPRIATENESS);
    }

    public void renameClass(IRI oldClassIRI, IRI newClassIRI) {
        for (UMPrpClassLevelKey key : classLevelInterests.keySet()) {
            if (key.getForClass().equals(oldClassIRI)) {
                int value = classLevelInterests.remove(key);
                key.setForClass(newClassIRI);

                classLevelInterests.put(key, value);
            }
        }

        for (UMPrpClassLevelKey key : classLevelRepetitions.keySet()) {
            if (key.getForClass().equals(oldClassIRI)) {
                int value = classLevelRepetitions.remove(key);
                key.setForClass(newClassIRI);

                classLevelRepetitions.put(key, value);
            }
        }
    }

    public void renameInstance(IRI oldInstanceIRI, IRI newInstanceIRI) {
        for (UMPrpInstanceLevelKey key : instanceLevelInterests.keySet()) {
            if (key.getForInstance().equals(oldInstanceIRI)) {
                int value = instanceLevelInterests.remove(key);
                key.setForInstance(newInstanceIRI);

                instanceLevelInterests.put(key, value);
            }
        }

        for (UMPrpInstanceLevelKey key : instanceLevelRepetitions.keySet()) {
            if (key.getForInstance().equals(oldInstanceIRI)) {
                int value = instanceLevelRepetitions.remove(key);
                key.setForInstance(newInstanceIRI);

                instanceLevelRepetitions.put(key, value);
            }
        }
    }

    public void renameProperty(IRI oldPropertyIRI, IRI newPropertyIRI) {
        //Property Level
        for (UMPrpLevelKey key : propertyLevelInterests.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = propertyLevelInterests.remove(key);

                key.setForProperty(newPropertyIRI);
                propertyLevelInterests.put(key, value);
            }
        }

        for (UMPrpLevelKey key : propertyLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = propertyLevelRepetitions.remove(key);

                key.setForProperty(newPropertyIRI);
                propertyLevelRepetitions.put(key, value);
            }
        }

        //Class Level
        for (UMPrpClassLevelKey key : classLevelInterests.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = classLevelInterests.remove(key);
                key.setForProperty(newPropertyIRI);

                classLevelInterests.put(key, value);
            }
        }

        for (UMPrpClassLevelKey key : classLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = classLevelRepetitions.remove(key);
                key.setForProperty(newPropertyIRI);

                classLevelRepetitions.put(key, value);
            }
        }

        //Instance Level
        for (UMPrpInstanceLevelKey key : instanceLevelInterests.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = instanceLevelInterests.remove(key);
                key.setForProperty(newPropertyIRI);

                instanceLevelInterests.put(key, value);
            }
        }

        for (UMPrpInstanceLevelKey key : instanceLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(oldPropertyIRI)) {
                int value = instanceLevelRepetitions.remove(key);
                key.setForProperty(newPropertyIRI);

                instanceLevelRepetitions.put(key, value);
            }
        }
    }

    public void renameSentencePlan(IRI oldSentencePlanIRI, IRI newSentencePlanIRI) {
        Integer value = sentencePlanAppropriateness.remove(oldSentencePlanIRI);
        if (value != null) {
            sentencePlanAppropriateness.put(newSentencePlanIRI, value);
        } else {
            sentencePlanAppropriateness.put(newSentencePlanIRI, UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS);
        }
    }

    public void renameNLName(IRI oldNLNameIRI, IRI newNLNameIRI) {
        Integer value = NLNameAppropriateness.remove(oldNLNameIRI);
        if (value != null) {
            NLNameAppropriateness.put(newNLNameIRI, value);
        } else {
            NLNameAppropriateness.put(newNLNameIRI, UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS);
        }
    }

    public void deleteClass(IRI classIRI) {
        for (UMPrpClassLevelKey key : classLevelInterests.keySet()) {
            if (key.getForClass().equals(classIRI)) {
                classLevelInterests.remove(key);
            }
        }

        for (UMPrpClassLevelKey key : classLevelRepetitions.keySet()) {
            if (key.getForClass().equals(classIRI)) {
                classLevelRepetitions.remove(key);
            }
        }
    }

    public void deleteInstance(IRI instanceIRI) {
        for (UMPrpInstanceLevelKey key : instanceLevelInterests.keySet()) {
            if (key.getForInstance().equals(instanceIRI)) {
                instanceLevelInterests.remove(key);
            }
        }

        for (UMPrpInstanceLevelKey key : instanceLevelRepetitions.keySet()) {
            if (key.getForInstance().equals(instanceIRI)) {
                instanceLevelRepetitions.remove(key);
            }
        }
    }

    public void deleteProperty(IRI propertyIRI) {
        //Property Level
        for (UMPrpLevelKey key : propertyLevelInterests.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                propertyLevelInterests.remove(key);
            }
        }

        for (UMPrpLevelKey key : propertyLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                propertyLevelRepetitions.remove(key);
            }
        }

        //Class Level
        for (UMPrpClassLevelKey key : classLevelInterests.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                classLevelInterests.remove(key);
            }
        }

        for (UMPrpClassLevelKey key : classLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                classLevelRepetitions.remove(key);
            }
        }

        //Instance Level
        for (UMPrpInstanceLevelKey key : instanceLevelInterests.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                instanceLevelInterests.remove(key);
            }
        }

        for (UMPrpInstanceLevelKey key : instanceLevelRepetitions.keySet()) {
            if (key.getForProperty().equals(propertyIRI)) {
                instanceLevelRepetitions.remove(key);
            }
        }
    }

    public void deleteSentencePlan(IRI sentencePlanIRI) {
        sentencePlanAppropriateness.remove(sentencePlanIRI);
    }

    public void deleteNLName(IRI NLNameIRI) {
        NLNameAppropriateness.remove(NLNameIRI);
    }

    public void setPropertyLevelInterest(IRI forProperty, IRI forModifier, int value) {
        UMPrpLevelKey key = new UMPrpLevelKey(forProperty, forModifier);
        propertyLevelInterests.put(key, value);
    }

    public void setPropertyLevelRepetitions(IRI forProperty, IRI forModifier, int value) {
        UMPrpLevelKey key = new UMPrpLevelKey(forProperty, forModifier);
        propertyLevelRepetitions.put(key, value);
    }

    public void setClassLevelInterest(IRI forProperty, IRI forClass, IRI forModifier, int value) {
        UMPrpClassLevelKey key = new UMPrpClassLevelKey(forProperty, forClass, forModifier);
        classLevelInterests.put(key, value);
    }

    public void setClassLevelRepetitions(IRI forProperty, IRI forClass, IRI forModifier, int value) {
        UMPrpClassLevelKey key = new UMPrpClassLevelKey(forProperty, forClass, forModifier);
        classLevelRepetitions.put(key, value);
    }

    public void setInstanceLevelInterest(IRI forProperty, IRI forInstance, IRI forModifier, int value) {
        UMPrpInstanceLevelKey key = new UMPrpInstanceLevelKey(forProperty, forInstance, forModifier);
        instanceLevelInterests.put(key, value);
    }

    public void setInstanceLevelRepetitions(IRI forProperty, IRI forInstance, IRI forModifier, int value) {
        UMPrpInstanceLevelKey key = new UMPrpInstanceLevelKey(forProperty, forInstance, forModifier);
        instanceLevelRepetitions.put(key, value);
    }

    public void setSentencePlanAppropriateness(IRI sentencePlanIRI, int value) {
        sentencePlanAppropriateness.put(sentencePlanIRI, value);
    }

    public void setNLNameAppropriateness(IRI NLNameIRI, int value) {
        NLNameAppropriateness.put(NLNameIRI, value);
    }

    public int getPropertyLevelInterest(IRI forProperty, IRI forModifier) {
        UMPrpLevelKey key = new UMPrpLevelKey(forProperty, forModifier);
        Integer i = propertyLevelInterests.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getPropertyLevelRepetitions(IRI forProperty, IRI forModifier) {
        UMPrpLevelKey key = new UMPrpLevelKey(forProperty, forModifier);
        Integer i = propertyLevelRepetitions.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getClassLevelInterest(IRI forProperty, IRI forClass, IRI forModifier) {
        UMPrpClassLevelKey key = new UMPrpClassLevelKey(forProperty, forClass, forModifier);
        Integer i = classLevelInterests.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getClassLevelRepetitions(IRI forProperty, IRI forClass, IRI forModifier) {
        UMPrpClassLevelKey key = new UMPrpClassLevelKey(forProperty, forClass, forModifier);
        Integer i = classLevelRepetitions.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getInstanceLevelInterest(IRI forProperty, IRI forInstance, IRI forModifier) {
        UMPrpInstanceLevelKey key = new UMPrpInstanceLevelKey(forProperty, forInstance, forModifier);
        Integer i = instanceLevelInterests.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getInstanceLevelRepetitions(IRI forProperty, IRI forInstance, IRI forModifier) {
        UMPrpInstanceLevelKey key = new UMPrpInstanceLevelKey(forProperty, forInstance, forModifier);
        Integer i = instanceLevelRepetitions.get(key);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_UNDEFINED;
    }

    public int getSentencePlanAppropriateness(IRI sentencePlanIRI) {
        Integer i = sentencePlanAppropriateness.get(sentencePlanIRI);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS;
    }

    public int getNLNameAppropriateness(IRI NLNameIRI) {
        Integer i = NLNameAppropriateness.get(NLNameIRI);
        if (i != null) {
            return i;
        }
        return UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS;
    }

    public IRI getUMIRI() {
        return UMIRI;
    }

    public void setUMIRI(IRI UMIRI) {
        this.UMIRI = UMIRI;
    }

    public int getMaxMessagesPerSentence() {
        return maxMessagesPerSentence;
    }

    public void setMaxMessagesPerSentence(int MMPS) {
        maxMessagesPerSentence = MMPS;
    }

    public int getGlobalInterest() {
        return globalInterest;
    }

    public void setGlobalInterest(int globalInterest) {
        this.globalInterest = globalInterest;
    }

    public int getGlobalRepetitions() {
        return globalRepetitions;
    }

    public void setGlobalRepetitions(int globalRepetitions) {
        this.globalRepetitions = globalRepetitions;
    }

    public void setMaxMessagesPerPage(int MMPP) {
        this.maxMessagesPerPage = MMPP;
    }

    public int getMaxMessagesPerPage() {
        return this.maxMessagesPerPage;
    }

    public Set<UMPrpLevelKey> getPropertyLevelKeys() {
        Set<UMPrpLevelKey> keys = new HashSet<UMPrpLevelKey>();

        keys.addAll(propertyLevelInterests.keySet());
        keys.addAll(propertyLevelRepetitions.keySet());

        return keys;
    }

    public Set<UMPrpClassLevelKey> getClassLevelKeys() {
        Set<UMPrpClassLevelKey> keys = new HashSet<UMPrpClassLevelKey>();

        keys.addAll(classLevelInterests.keySet());
        keys.addAll(classLevelRepetitions.keySet());

        return keys;
    }

    public Set<UMPrpInstanceLevelKey> getInstanceLevelKeys() {
        Set<UMPrpInstanceLevelKey> keys = new HashSet<UMPrpInstanceLevelKey>();

        keys.addAll(instanceLevelInterests.keySet());
        keys.addAll(instanceLevelRepetitions.keySet());

        return keys;
    }

    public Set<IRI> getSentencePlansIRIs() {
        return sentencePlanAppropriateness.keySet();
    }

    public Set<IRI> getNLNamesIRIs() {
        return NLNameAppropriateness.keySet();
    }
}
