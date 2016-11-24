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

import gr.aueb.cs.nlg.Languages.Languages;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLNameQueryManager;
import gr.aueb.cs.nlg.NLFiles.OrderingQueryManager;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationPropertySlot;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlan;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;
import gr.aueb.cs.nlg.NLFiles.UserModel;
import gr.aueb.cs.nlg.NLFiles.UserModelQueryManager;

import gr.aueb.cs.nlg.Utils.Fact;
import gr.aueb.cs.nlg.Utils.InterestComparatorImpl;
import gr.aueb.cs.nlg.Utils.NLGUser;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public class ContentSelection extends NLGEngineComponent {

    Set<OWLOntology> mainModels;
    private NLNameQueryManager NLNQM;
    private SentencePlanQueryManager SPQM;
    private OrderingQueryManager OQM;
    private UserModelQueryManager UMQM;
    private MappingQueryManager MQM;
    private HashSet<IRI> mentionedEntities;
    private HashSet<Fact> conveyedFacts;
    private HashSet<IRI> usedSentencePlans;
    private HashSet<IRI> usedNLNames;
    private HashMap<IRI, IRI> usedSentencePlanInDescr; //Property -> SentencePlan
    private HashMap<IRI, IRI> usedNLNamesInDescr; //Entity -> NLName
    private HashMap<IRI, IRI> anonymousSuperClasses; //Entity -> SuperClass
    private boolean assimilationON = true;
    private boolean interestON = true;
    private boolean allFactsAreAssimilated = false;

    public ContentSelection(Set<OWLOntology> ontologies, SentencePlanQueryManager SPQM, NLNameQueryManager NLNQM, UserModelQueryManager UMQM, MappingQueryManager MQM, OrderingQueryManager OQM, String Language) {
        super(Language);

        this.mainModels = ontologies;
        this.SPQM = SPQM;
        this.NLNQM = NLNQM;
        this.UMQM = UMQM;
        this.MQM = MQM;
        this.OQM = OQM;
    }

    public boolean isAssimilationON() {
        return assimilationON;
    }

    public void setAssimilationON(boolean AssimilationON) {
        this.assimilationON = AssimilationON;
    }

    public boolean isInterestON() {
        return interestON;
    }

    public void setInterestON(boolean InterestON) {
        this.interestON = InterestON;
    }

    public void addModel(OWLOntology m) {
        this.mainModels.add(m);
    }

    public void setNamespaces(XmlMsgs MyXmlMsgs) {
        for (OWLOntology ontology : mainModels) {
            Map<String, String> prefixesToNSMap = ontology.getOWLOntologyManager().getOntologyFormat(ontology).asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();

            Iterator<String> iter = prefixesToNSMap.keySet().iterator();

            while (iter.hasNext()) {
                String pref = iter.next().toString();
                String uriForPref = prefixesToNSMap.get(pref).toString();

                MyXmlMsgs.setNamespace(uriForPref, pref);
            }
        }
    }

    public void clearBuffers() {
        mentionedEntities = new HashSet<IRI>();
        conveyedFacts = new HashSet<Fact>();
        usedSentencePlans = new HashSet<IRI>();
        usedNLNames = new HashSet<IRI>();

        usedSentencePlanInDescr = new HashMap<IRI, IRI>();
        usedNLNamesInDescr = new HashMap<IRI, IRI>();
        anonymousSuperClasses = new HashMap<IRI, IRI>();
    }

    public void setMentionedEntity(IRI entityIRI) {
        mentionedEntities.add(entityIRI);
    }

    public void updateUser(NLGUser user) {
        for (IRI mentionedEntity : mentionedEntities) {
            user.increaseEntityMentionedCount(mentionedEntity);
        }

        for (IRI usedSentencePlan : usedSentencePlans) {
            user.increaseSentencePlanUseCount(usedSentencePlan);
        }

        for (IRI usedNLName : usedNLNames) {
            user.increaseNLNameUseCount(usedNLName);
        }

        for (Fact conveyedFact : conveyedFacts) {
            ArrayList<String> values = new ArrayList<String>();
            if (conveyedFact.getObject().startsWith("and(")) {
                values = Fact.parseModifier(conveyedFact.getObject());
            } else if (conveyedFact.getObject().startsWith("or(")) {
                values = Fact.parseModifier(conveyedFact.getObject());
            } else {
                values.add(conveyedFact.getObject());
            }

            for (String value : values) {
                Fact conveyed = new Fact(conveyedFact.getSubject(), conveyedFact.getPredicate(), value);
                for (String modifier : conveyedFact.getModifiers()) {
                    conveyed.addModifier(modifier);
                }
                conveyed.setCardinality(conveyedFact.getCardinality());
                conveyed.setPolarity(conveyedFact.isPolarity());

                user.increaseFactMentionedCount(conveyed);
            }

            //If a concatenation property already includes a fact, it should count as conveyed
            if (usedSentencePlanInDescr.get(conveyedFact.getPredicate()) != null) {
                SentencePlan plan = SPQM.getSentencePlan(usedSentencePlanInDescr.get(conveyedFact.getPredicate()));

                for (SPSlot slot : plan.getSlotsList()) {
                    if (slot instanceof SPConcatenationSlot) {
                        for (SPConcatenationPropertySlot propertySlot : ((SPConcatenationSlot) slot).getPropertySlots()) {
                            HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>> objPropertyExprMaps = new HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>>();
                            HashMap<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyExprMaps = new HashMap<OWLDataPropertyExpression, Set<OWLLiteral>>();
                            for (OWLOntology ontology : mainModels) {
                                for (String value : values) {
                                    for (OWLEntity entity : ontology.getEntitiesInSignature(IRI.create(value))) {
                                        if (entity.isOWLNamedIndividual()) {
                                            for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : entity.asOWLNamedIndividual().getObjectPropertyValues(ontology).entrySet()) {
                                                if (propertyExprMap.getKey().asOWLObjectProperty().getIRI().equals(propertySlot.getPropertyIRI())) {
                                                    objPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                                                }
                                            }
                                            for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : entity.asOWLNamedIndividual().getDataPropertyValues(ontology).entrySet()) {
                                                if (propertyExprMap.getKey().asOWLDataProperty().getIRI().equals(propertySlot.getPropertyIRI())) {
                                                    dataPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : objPropertyExprMaps.entrySet()) {
                                for (OWLIndividual object : propertyExprMap.getValue()) {
                                    if (object.isNamed()) {
                                        Fact secondLevelFact = new Fact(IRI.create(conveyedFact.getObject()), propertySlot.getPropertyIRI(), object.asOWLNamedIndividual().getIRI().toString());
                                        user.increaseFactMentionedCount(secondLevelFact);
                                    }
                                }
                            }
                            for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : dataPropertyExprMaps.entrySet()) {
                                for (OWLLiteral object : propertyExprMap.getValue()) {
                                    Fact secondLevelFact = new Fact(IRI.create(conveyedFact.getObject()), propertySlot.getPropertyIRI(), object.getLiteral());
                                    user.increaseFactMentionedCount(secondLevelFact);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public XmlMsgs getMessages(IRI entityOrClassIRI, XmlMsgs messages, Node messageRoot, int depth, NLGUser user) {
        if (NLGEngine.isClass(mainModels, entityOrClassIRI)) {
            return getClassMessages(entityOrClassIRI, messages, messages.getRoot(), depth, 1, user);
        } else {
            return getIndividualMessages(entityOrClassIRI, messages, messages.getRoot(), depth, 1, user);
        }
    }

    //returns the messages of a Class
    public XmlMsgs getClassMessages(IRI classIRI, XmlMsgs messages, Node messageRoot, int depth, int level, NLGUser user) {
        Set<OWLEntity> classes = new HashSet<OWLEntity>();
        for (OWLOntology ontology : mainModels) {
            classes.addAll(ontology.getEntitiesInSignature(classIRI, true));
        }

        for (OWLEntity owlClass : classes) {
            if (owlClass.isOWLClass()) {
                OWLClass subject = owlClass.asOWLClass();

                if (!subject.isAnonymous()) {
                    ArrayList<Fact> isAFacts = new ArrayList<Fact>();
                    ArrayList<Fact> notIsAFacts = new ArrayList<Fact>();
                    ArrayList<Fact> oneOfFacts = new ArrayList<Fact>();
                    ArrayList<Fact> disjointFacts = new ArrayList<Fact>();

                    //ClassAssertion
                    Set<OWLClassExpression> classExprs = new HashSet<OWLClassExpression>();
                    for (OWLOntology ontology : mainModels) {
                        classExprs.addAll(subject.getSuperClasses(ontology));
                    }
                    for (OWLClassExpression classExpr : classExprs) {
                        addClassExpressionMessages(classExpr, classIRI, messages, messageRoot, isAFacts, notIsAFacts, oneOfFacts, depth, level, user);
                    }//ClassAssertion

                    classExprs = new HashSet<OWLClassExpression>();
                    for (OWLOntology ontology : mainModels) {
                        classExprs.addAll(subject.getEquivalentClasses(ontology));
                    }
                    for (OWLClassExpression classExpr : classExprs) {
                        addClassExpressionMessages(classExpr, classIRI, messages, messageRoot, isAFacts, notIsAFacts, oneOfFacts, depth, level, user);
                    }//ClassAssertion

                    if (!isAFacts.isEmpty()) {
                        addDomainIndependentMessage(isAFacts, messages, messageRoot, true, depth, level, user);
                    }
                    if (!notIsAFacts.isEmpty()) {
                        addDomainIndependentMessage(notIsAFacts, messages, messageRoot, true, depth, level, user);
                    }
                    if (!oneOfFacts.isEmpty()) {
                        addDomainIndependentMessage(oneOfFacts, messages, messageRoot, false, depth, level, user);
                    }

                    classExprs = new HashSet<OWLClassExpression>();
                    for (OWLOntology ontology : mainModels) {
                        classExprs.addAll(subject.getDisjointClasses(ontology));
                    }
                    for (OWLClassExpression classExpr : classExprs) {
                        Set<OWLClass> disjointClasses = classExpr.getClassesInSignature();
                        for (OWLClass disjointClass : disjointClasses) {
                            if (!disjointClass.isAnonymous()) {
                                IRI disjointClassIRI = disjointClass.getIRI();

                                Fact fact = new Fact(classIRI, NLResourceManager.isA.getIRI(), disjointClassIRI.toString());
                                fact.addModifier(XmlMsgs.NOT_MODIFIER);

                                disjointFacts.add(fact);
                            }
                        }
                    }//ClassAssertion
                    if (!disjointFacts.isEmpty()) {
                        addDomainIndependentMessage(disjointFacts, messages, messageRoot, true, depth, level, user);
                    }
                }
            }
        }
        return messages;
    }//getMsgsOfAClass

    //returns the messages of an individual
    public XmlMsgs getIndividualMessages(IRI individualIRI, XmlMsgs messages, Node messageRoot, int depth, int level, NLGUser user) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology ontology : mainModels) {
            entities.addAll(ontology.getEntitiesInSignature(individualIRI, true));
        }
        for (OWLEntity entity : entities) {
            if (entity.isOWLNamedIndividual()) {
                OWLNamedIndividual subject = entity.asOWLNamedIndividual();

                if (subject == null) { // In case of datatype properties
                    return messages;
                }

                ArrayList<Fact> isAFacts = new ArrayList<Fact>();
                ArrayList<Fact> notIsAFacts = new ArrayList<Fact>();
                ArrayList<Fact> oneOfFacts = new ArrayList<Fact>();

                //ClassAssertion
                Set<OWLClassExpression> classExprs = new HashSet<OWLClassExpression>();
                for (OWLOntology ontology : mainModels) {
                    classExprs.addAll(subject.getTypes(ontology));
                }
                for (OWLClassExpression classExpr : classExprs) {
                    addClassExpressionMessages(classExpr, individualIRI, messages, messageRoot, isAFacts, notIsAFacts, oneOfFacts, depth, level, user);
                }//ClassAssertion

                if (!isAFacts.isEmpty()) {
                    addDomainIndependentMessage(isAFacts, messages, messageRoot, true, depth, level, user);
                }
                if (!notIsAFacts.isEmpty()) {
                    addDomainIndependentMessage(notIsAFacts, messages, messageRoot, true, depth, level, user);
                }
                if (!oneOfFacts.isEmpty()) {
                    addDomainIndependentMessage(oneOfFacts, messages, messageRoot, false, depth, level, user);
                }

                //SameIndividual
                Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
                for (OWLOntology ontology : mainModels) {
                    individuals.addAll(subject.getSameIndividuals(ontology));
                }
                ArrayList<Fact> facts = new ArrayList<Fact>();
                for (OWLIndividual indiv : individuals) {
                    if (indiv.isNamed()) {
                        IRI indivIRI = indiv.asOWLNamedIndividual().getIRI();

                        if (!indivIRI.equals(individualIRI)) {
                            Fact fact = new Fact(individualIRI, NLResourceManager.sameIndividuals.getIRI(), indivIRI.toString());
                            facts.add(fact);
                        }
                    }
                }//SameIndividual
                if (!facts.isEmpty()) {
                    addDomainIndependentMessage(facts, messages, messageRoot, true, depth, level, user);
                }

                //DifferentIndividual
                facts = new ArrayList<Fact>();
                individuals = new HashSet<OWLIndividual>();
                for (OWLOntology ontology : mainModels) {
                    individuals.addAll(subject.getDifferentIndividuals(ontology));
                }
                for (OWLIndividual indiv : individuals) {
                    if (indiv.isNamed()) {
                        IRI indivIRI = indiv.asOWLNamedIndividual().getIRI();

                        Fact fact = new Fact(individualIRI, NLResourceManager.differentIndividuals.getIRI(), indivIRI.toString());
                        facts.add(fact);
                    }
                }//DifferentIndividual
                if (!facts.isEmpty()) {
                    addDomainIndependentMessage(facts, messages, messageRoot, true, depth, level, user);
                }

                //Object Properties
                facts = new ArrayList<Fact>();
                HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>> objPropertyExprMaps = new HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : subject.getObjectPropertyValues(ontology).entrySet()) {
                        objPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : objPropertyExprMaps.entrySet()) {
                    OWLObjectPropertyExpression propertyExpr = propertyExprMap.getKey();

                    facts = new ArrayList<Fact>();
                    for (OWLIndividual object : propertyExprMap.getValue()) {
                        Fact fact = new Fact(individualIRI, propertyExpr.asOWLObjectProperty().getIRI(), object.asOWLNamedIndividual().getIRI().toString());
                        facts.add(fact);
                    }
                    addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
                }

                //Negative object Properties
                facts = new ArrayList<Fact>();
                HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>> negObjPropertyExprMaps = new HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : subject.getNegativeObjectPropertyValues(ontology).entrySet()) {
                        negObjPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : negObjPropertyExprMaps.entrySet()) {
                    OWLObjectPropertyExpression propertyExpr = propertyExprMap.getKey();

                    facts = new ArrayList<Fact>();
                    for (OWLIndividual object : propertyExprMap.getValue()) {
                        if (object.isNamed()) {
                            Fact fact = new Fact(individualIRI, propertyExpr.asOWLObjectProperty().getIRI(), object.asOWLNamedIndividual().getIRI().toString());
                            fact.addModifier(XmlMsgs.NOT_MODIFIER);
                            facts.add(fact);
                        }
                    }
                    addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
                }

                //Data Properties
                HashMap<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyExprMaps = new HashMap<OWLDataPropertyExpression, Set<OWLLiteral>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : subject.getDataPropertyValues(ontology).entrySet()) {
                        dataPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : dataPropertyExprMaps.entrySet()) {
                    OWLDataPropertyExpression propertyExpr = propertyExprMap.getKey();

                    facts = new ArrayList<Fact>();
                    for (OWLLiteral object : propertyExprMap.getValue()) {
                        Fact fact = new Fact(individualIRI, propertyExpr.asOWLDataProperty().getIRI(), object.getLiteral());
                        facts.add(fact);
                    }
                    addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
                }

                //Negative Data Properties
                HashMap<OWLDataPropertyExpression, Set<OWLLiteral>> negDataPropertyExprMaps = new HashMap<OWLDataPropertyExpression, Set<OWLLiteral>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : subject.getNegativeDataPropertyValues(ontology).entrySet()) {
                        negDataPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : negDataPropertyExprMaps.entrySet()) {
                    OWLDataPropertyExpression propertyExpr = propertyExprMap.getKey();

                    facts = new ArrayList<Fact>();
                    for (OWLLiteral object : propertyExprMap.getValue()) {
                        Fact fact = new Fact(individualIRI, propertyExpr.asOWLDataProperty().getIRI(), object.getLiteral());
                        fact.addModifier(XmlMsgs.NOT_MODIFIER);
                        facts.add(fact);
                    }
                    addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
                }
            }
            return messages;
        }
        return messages;
    }//getIndividualMessages

    //returns the messages of an individual
    public XmlMsgs getSingleObjectMessages(IRI individualIRI, IRI propertyIRI, IRI fillerIRI, XmlMsgs messages, Node messageRoot, NLGUser user) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology ontology : mainModels) {
            entities.addAll(ontology.getEntitiesInSignature(individualIRI, true));
        }
        for (OWLEntity entity : entities) {
            if (entity.isOWLNamedIndividual()) {
                OWLNamedIndividual subject = entity.asOWLNamedIndividual();

                HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>> objPropertyExprMaps = new HashMap<OWLObjectPropertyExpression, Set<OWLIndividual>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : subject.getObjectPropertyValues(ontology).entrySet()) {
                        objPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLObjectPropertyExpression, Set<OWLIndividual>> propertyExprMap : objPropertyExprMaps.entrySet()) {
                    OWLObjectPropertyExpression propertyExpr = propertyExprMap.getKey();

                    ArrayList<Fact> facts = new ArrayList<Fact>();
                    if (propertyExpr.asOWLObjectProperty().getIRI().equals(propertyIRI)) {
                        for (OWLIndividual object : propertyExprMap.getValue()) {
                            if (fillerIRI != null) {
                                if (object.asOWLNamedIndividual().getIRI().equals(fillerIRI)) {
                                    Fact fact = new Fact(individualIRI, propertyExpr.asOWLObjectProperty().getIRI(), object.asOWLNamedIndividual().getIRI().toString());
                                    facts.add(fact);
                                }
                            } else {
                                Fact fact = new Fact(individualIRI, propertyExpr.asOWLObjectProperty().getIRI(), object.asOWLNamedIndividual().getIRI().toString());
                                facts.add(fact);
                            }
                        }
                        addPropertyMessage(facts, messages, messageRoot, true, 1, 1, user);
                    }
                }
            }
            return messages;
        }
        return messages;
    }//getSingleIndividualObjectMessages

    //returns the messages of an individual
    public XmlMsgs getSingleDataMessages(IRI individualIRI, IRI propertyIRI, OWLLiteral filler, XmlMsgs messages, Node messageRoot, NLGUser user) {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for (OWLOntology ontology : mainModels) {
            entities.addAll(ontology.getEntitiesInSignature(individualIRI, true));
        }
        for (OWLEntity entity : entities) {
            if (entity.isOWLNamedIndividual()) {
                OWLNamedIndividual subject = entity.asOWLNamedIndividual();

                HashMap<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyExprMaps = new HashMap<OWLDataPropertyExpression, Set<OWLLiteral>>();
                for (OWLOntology ontology : mainModels) {
                    for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : subject.getDataPropertyValues(ontology).entrySet()) {
                        dataPropertyExprMaps.put(propertyExprMap.getKey(), propertyExprMap.getValue());
                    }
                }
                for (Map.Entry<OWLDataPropertyExpression, Set<OWLLiteral>> propertyExprMap : dataPropertyExprMaps.entrySet()) {
                    OWLDataPropertyExpression propertyExpr = propertyExprMap.getKey();

                    ArrayList<Fact> facts = new ArrayList<Fact>();
                    if (propertyExpr.asOWLDataProperty().getIRI().equals(propertyIRI)) {
                        for (OWLLiteral object : propertyExprMap.getValue()) {
                            if (filler != null) {
                                if (object.getLiteral().equals(filler)) {
                                    Fact fact = new Fact(individualIRI, propertyExpr.asOWLDataProperty().getIRI(), object.getLiteral());
                                    facts.add(fact);
                                }
                            } else {
                                Fact fact = new Fact(individualIRI, propertyExpr.asOWLDataProperty().getIRI(), object.getLiteral());
                                facts.add(fact);
                            }
                        }
                        addPropertyMessage(facts, messages, messageRoot, true, 1, 1, user);
                    }
                }
            }
            return messages;
        }
        return messages;
    }//getSingleIndividualDataMessages

    //returns the messages of an individual
    public XmlMsgs getSpecificMessage(IRI subjectIRI, IRI subjectNLNameIRI, IRI propertyIRI, IRI sentencePlanIRI, IRI objectIRI, IRI objectNLNameIRI, XmlMsgs messages, Node messageRoot, NLGUser user) {
        boolean isObjectProperty = false;
        for (OWLOntology ontology : mainModels) {
            if (ontology.containsObjectPropertyInSignature(propertyIRI, true)) {
                isObjectProperty = true;
            }
        }

        Element message = messages.addNewElement((Element) messageRoot, "", "", propertyIRI.getFragment());

        //Property attributes
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI.toString());
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, sentencePlanIRI.toString());

        //Subject attributes
        if (subjectNLNameIRI.equals(NLResourceManager.anonymous.getIRI())) {
            subjectNLNameIRI = chooseAnonymousNLName(subjectIRI, user);
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, "anonymous(" + subjectNLNameIRI.toString() + ')');
        } else {
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, subjectNLNameIRI.toString());
        }
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.REF, subjectIRI.toString());

        //Object attributes
        if (isObjectProperty) {
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG, objectNLNameIRI.toString());
        }
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.VALUE, objectIRI.toString());

        //Other attributes
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.LEVEL, "" + 1);

        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.isConnective, "false");
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED, "false");
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST, "false");

        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, "1");
        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, "0");

        if (isObjectProperty) {
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.prpType, XmlMsgs.ObjectProperty);
        } else {
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.prpType, XmlMsgs.DatatypeProperty);
        }

        return messages;
    }//getSpecificMessage

    private void addClassExpressionMessages(OWLClassExpression classExpr, IRI individualIRI, XmlMsgs messages, Node messageRoot, ArrayList<Fact> isAFacts, ArrayList<Fact> notIsAFacts, ArrayList<Fact> oneOfFacts, int depth, int level, NLGUser user) {
        if (classExpr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
            IRI classIRI = classExpr.asOWLClass().getIRI();

            Fact fact;
            if (NLGEngine.isClass(mainModels, individualIRI)) {
                fact = new Fact(individualIRI, NLResourceManager.isA.getIRI(), classIRI.toString());
            } else {
                fact = new Fact(individualIRI, NLResourceManager.instanceOf.getIRI(), classIRI.toString());
            }

            isAFacts.add(fact);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
            OWLObjectComplementOf complementExpr = (OWLObjectComplementOf) classExpr;

            if (!complementExpr.getOperand().isAnonymous()) {
                Fact fact;
                if (NLGEngine.isClass(mainModels, individualIRI)) {
                    fact = new Fact(individualIRI, NLResourceManager.isA.getIRI(), complementExpr.getOperand().asOWLClass().getIRI().toString());
                } else {
                    fact = new Fact(individualIRI, NLResourceManager.instanceOf.getIRI(), complementExpr.getOperand().asOWLClass().getIRI().toString());
                }

                fact.addModifier(XmlMsgs.NOT_MODIFIER);

                notIsAFacts.add(fact);
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
            OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) classExpr;

            for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                if (indiv.isNamed()) {
                    Fact fact = new Fact(individualIRI, NLResourceManager.oneOf.getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                    oneOfFacts.add(fact);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
            OWLObjectHasValue hasValueExpr = (OWLObjectHasValue) classExpr;
            ArrayList<Fact> facts = new ArrayList<Fact>();
            if (hasValueExpr.getValue().isNamed()) {
                Fact fact = new Fact(individualIRI, hasValueExpr.getProperty().asOWLObjectProperty().getIRI(), hasValueExpr.getValue().asOWLNamedIndividual().getIRI().toString());
                facts.add(fact);
            }
            addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.DATA_HAS_VALUE)) {
            OWLDataHasValue hasValueExpr = (OWLDataHasValue) classExpr;

            ArrayList<Fact> facts = new ArrayList<Fact>();
            Fact fact = new Fact(individualIRI, hasValueExpr.getProperty().asOWLDataProperty().getIRI(), hasValueExpr.getValue().getLiteral().toString());
            facts.add(fact);

            addPropertyMessage(facts, messages, messageRoot, true, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_HAS_SELF)) {
            OWLObjectHasSelf hasSelfExpr = (OWLObjectHasSelf) classExpr;

            Fact fact = new Fact(individualIRI, hasSelfExpr.getProperty().asOWLObjectProperty().getIRI(), individualIRI.toString());

            addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
            OWLObjectCardinalityRestriction maxExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!maxExpr.getFiller().isAnonymous()) {
                if (maxExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (maxExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(maxExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = maxExpr.getFiller().asOWLClass().getIRI().toString();
                    }
                    if (filler.isEmpty()) {
                        for (OWLOntology ontology : mainModels) {
                            filler = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().toString();
                        }
                    }

                    Fact fact = new Fact(individualIRI, maxExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(maxExpr.getCardinality());

                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
            OWLObjectCardinalityRestriction minExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!minExpr.getFiller().isAnonymous()) {
                if (minExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (minExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(minExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = minExpr.getFiller().asOWLClass().getIRI().toString();
                    }
                    if (filler.isEmpty()) {
                        for (OWLOntology ontology : mainModels) {
                            filler = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().toString();
                        }
                    }

                    Fact fact = new Fact(individualIRI, minExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(minExpr.getCardinality());
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
            OWLObjectCardinalityRestriction exactExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!exactExpr.getFiller().isAnonymous()) {
                if (exactExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (exactExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(exactExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = exactExpr.getFiller().asOWLClass().getIRI().toString();
                    }
                    if (filler.isEmpty()) {
                        for (OWLOntology ontology : mainModels) {
                            filler = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().toString();
                        }
                    }

                    Fact fact = new Fact(individualIRI, exactExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(exactExpr.getCardinality());
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
            OWLObjectSomeValuesFrom someValuesExpr = (OWLObjectSomeValuesFrom) classExpr;

            if (!someValuesExpr.getFiller().isAnonymous()) {
                if (someValuesExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (someValuesExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(someValuesExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = someValuesExpr.getFiller().asOWLClass().getIRI().toString();
                    }
                    if (filler.isEmpty()) {
                        for (OWLOntology ontology : mainModels) {
                            filler = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().toString();
                        }
                    }

                    Fact fact = new Fact(individualIRI, someValuesExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG);
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            } else if (someValuesExpr.getFiller().getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
                OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) someValuesExpr.getFiller();

                for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                    if (indiv.isNamed()) {
                        Fact fact = new Fact(individualIRI, someValuesExpr.getProperty().asOWLObjectProperty().getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                        fact.addModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG);
                        addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                    }
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
            OWLObjectAllValuesFrom allValuesExpr = (OWLObjectAllValuesFrom) classExpr;

            if (!allValuesExpr.getFiller().isAnonymous()) {
                if (allValuesExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (allValuesExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(allValuesExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = allValuesExpr.getFiller().asOWLClass().getIRI().toString();
                    }
                    if (filler.isEmpty()) {
                        for (OWLOntology ontology : mainModels) {
                            filler = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLThing().toString();
                        }
                    }

                    Fact fact = new Fact(individualIRI, allValuesExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG);
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            } else if (allValuesExpr.getFiller().getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
                OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) allValuesExpr.getFiller();

                for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                    if (indiv.isNamed()) {
                        Fact fact = new Fact(individualIRI, allValuesExpr.getProperty().asOWLObjectProperty().getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                        fact.addModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG);
                        addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                    }
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_UNION_OF)) {
            OWLObjectUnionOf unionOfExpr = (OWLObjectUnionOf) classExpr;

            boolean containsMultiClassOperators = false;
            for (OWLClassExpression disjunctExpr : unionOfExpr.asDisjunctSet()) {
                if ((disjunctExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_UNION_OF)) || (disjunctExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF))) {
                    containsMultiClassOperators = true;
                }
            }
            if (!containsMultiClassOperators) {
                for (OWLClassExpression disjunctExpr : unionOfExpr.asDisjunctSet()) {
                    addDisjunctedClassExpressionMessages(disjunctExpr, individualIRI, messages, messageRoot, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
            OWLObjectIntersectionOf intersectionOfExpr = (OWLObjectIntersectionOf) classExpr;

            boolean containsMultiClassOperators = false;
            for (OWLClassExpression conjunctExpr : intersectionOfExpr.asConjunctSet()) {
                if ((conjunctExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_UNION_OF)) || (conjunctExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF))) {
                    containsMultiClassOperators = true;
                }
            }
            if (!containsMultiClassOperators) {
                for (OWLClassExpression conjunctExpr : intersectionOfExpr.asConjunctSet()) {
                    addClassExpressionMessages(conjunctExpr, individualIRI, messages, messageRoot, isAFacts, notIsAFacts, oneOfFacts, depth, level, user);
                }
            }
        }
    }

    private void addDisjunctedClassExpressionMessages(OWLClassExpression classExpr, IRI individualIRI, XmlMsgs messages, Node messageRoot, int depth, int level, NLGUser user) {
        if (classExpr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
            IRI classIRI = classExpr.asOWLClass().getIRI();

            Fact fact = new Fact(individualIRI, NLResourceManager.isA.getIRI(), classIRI.toString());
            addDomainIndependentMessage(fact, messages, messageRoot, false, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
            OWLObjectComplementOf complementExpr = (OWLObjectComplementOf) classExpr;

            if (!complementExpr.getOperand().isAnonymous()) {
                Fact fact = new Fact(individualIRI, NLResourceManager.isA.getIRI(), complementExpr.getOperand().asOWLClass().getIRI().toString());
                fact.addModifier(XmlMsgs.NOT_MODIFIER);

                addDomainIndependentMessage(fact, messages, messageRoot, false, depth, level, user);
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
            OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) classExpr;

            for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                if (indiv.isNamed()) {
                    Fact fact = new Fact(individualIRI, NLResourceManager.oneOf.getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                    addDomainIndependentMessage(fact, messages, messageRoot, false, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_HAS_VALUE)) {
            OWLObjectHasValue hasValueExpr = (OWLObjectHasValue) classExpr;

            ArrayList<Fact> facts = new ArrayList<Fact>();
            if (hasValueExpr.getValue().isNamed()) {
                Fact fact = new Fact(individualIRI, hasValueExpr.getProperty().asOWLObjectProperty().getIRI(), hasValueExpr.getValue().asOWLNamedIndividual().getIRI().toString());
                facts.add(fact);
            }
            addPropertyMessage(facts, messages, messageRoot, false, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_HAS_SELF)) {
            OWLObjectHasSelf hasSelfExpr = (OWLObjectHasSelf) classExpr;

            Fact fact = new Fact(individualIRI, hasSelfExpr.getProperty().asOWLObjectProperty().getIRI(), individualIRI.toString());

            addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
            OWLObjectCardinalityRestriction maxExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!maxExpr.getFiller().isAnonymous()) {
                if (maxExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (maxExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(maxExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = maxExpr.getFiller().asOWLClass().getIRI().toString();
                    }

                    Fact fact = new Fact(individualIRI, maxExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(maxExpr.getCardinality());
                    addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
            OWLObjectCardinalityRestriction minExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!minExpr.getFiller().isAnonymous()) {
                if (minExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (minExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(minExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = minExpr.getFiller().asOWLClass().getIRI().toString();
                    }

                    Fact fact = new Fact(individualIRI, minExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(minExpr.getCardinality());
                    addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
            OWLObjectCardinalityRestriction exactExpr = (OWLObjectCardinalityRestriction) classExpr;

            if (!exactExpr.getFiller().isAnonymous()) {
                if (exactExpr.getProperty().isObjectPropertyExpression()) {
                    String filler = "";

                    if (exactExpr.getFiller().asOWLClass().isOWLThing()) {
                        Set<OWLObjectProperty> properties = new HashSet<OWLObjectProperty>();
                        for (OWLOntology ontology : mainModels) {
                            properties.addAll(ontology.getObjectPropertiesInSignature(true));
                        }
                        for (OWLObjectProperty property : properties) {
                            if (property.getIRI().equals(exactExpr.getProperty().asOWLObjectProperty().getIRI())) {
                                Set<OWLClassExpression> ranges = new HashSet<OWLClassExpression>();
                                for (OWLOntology ontology : mainModels) {
                                    ranges.addAll(property.getRanges(ontology));
                                }
                                for (OWLClassExpression range : ranges) {
                                    if (!range.isAnonymous()) {
                                        filler = range.asOWLClass().getIRI().toString();
                                    }
                                }
                            }
                        }
                    } else {
                        filler = exactExpr.getFiller().asOWLClass().getIRI().toString();
                    }

                    Fact fact = new Fact(individualIRI, exactExpr.getProperty().asOWLObjectProperty().getIRI(), filler);
                    fact.addModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG);
                    fact.setCardinality(exactExpr.getCardinality());
                    addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
            OWLObjectSomeValuesFrom someValuesExpr = (OWLObjectSomeValuesFrom) classExpr;

            if (!someValuesExpr.getFiller().isAnonymous()) {
                if (someValuesExpr.getProperty().isObjectPropertyExpression()) {
                    Fact fact = new Fact(individualIRI, someValuesExpr.getProperty().asOWLObjectProperty().getIRI(), someValuesExpr.getFiller().asOWLClass().getIRI().toString());
                    fact.addModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG);
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            } else if (someValuesExpr.getFiller().getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
                OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) someValuesExpr.getFiller();

                for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                    if (indiv.isNamed()) {
                        Fact fact = new Fact(individualIRI, someValuesExpr.getProperty().asOWLObjectProperty().getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                        fact.addModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG);
                        addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                    }
                }
            }
        } else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
            OWLObjectAllValuesFrom allValuesExpr = (OWLObjectAllValuesFrom) classExpr;

            if (!allValuesExpr.getFiller().isAnonymous()) {
                if (allValuesExpr.getProperty().isObjectPropertyExpression()) {
                    Fact fact = new Fact(individualIRI, allValuesExpr.getProperty().asOWLObjectProperty().getIRI(), allValuesExpr.getFiller().asOWLClass().getIRI().toString());
                    fact.addModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG);
                    addPropertyMessage(fact, messages, messageRoot, true, depth, level, user);
                }
            } else if (allValuesExpr.getFiller().getClassExpressionType().equals(ClassExpressionType.OBJECT_ONE_OF)) {
                OWLObjectOneOf oneOfExpr = (OWLObjectOneOf) allValuesExpr.getFiller();

                for (OWLIndividual indiv : oneOfExpr.getIndividuals()) {
                    if (indiv.isNamed()) {
                        Fact fact = new Fact(individualIRI, allValuesExpr.getProperty().asOWLObjectProperty().getIRI(), indiv.asOWLNamedIndividual().getIRI().toString());
                        fact.addModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG);
                        addPropertyMessage(fact, messages, messageRoot, false, depth, level, user);
                    }
                }
            }
        }
    }

    private void addDomainIndependentMessage(Fact fact, XmlMsgs messages, Node messageRoot, boolean isConnective, int depth, int level, NLGUser user) {
        ArrayList<Fact> facts = new ArrayList<Fact>();
        facts.add(fact);

        addDomainIndependentMessage(facts, messages, messageRoot, isConnective, depth, level, user);
    }

    private void addDomainIndependentMessage(ArrayList<Fact> facts, XmlMsgs messages, Node messageRoot, boolean isConnective, int depth, int level, NLGUser user) {
        if (!facts.isEmpty()) {
            String interestValue = "";
            String assimilationScore = "";

            Fact fact = facts.get(0);

            Element message = messages.addNewElement((Element) messageRoot, NLResourceManager.nlowlNS, XmlMsgs.prefix, fact.getPredicate().getFragment());

            if (fact.getPredicate().equals(NLResourceManager.isA.getIRI()) || fact.getPredicate().equals(NLResourceManager.instanceOf.getIRI())) {

                if (fact.getPredicate().equals(NLResourceManager.isA.getIRI())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty, NLResourceManager.isA.getIRI().toString());
                } else {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty, NLResourceManager.instanceOf.getIRI().toString());
                }

                if (Languages.isEnglish(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.isASPEN_IRI.toString());
                } else if (Languages.isGreek(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.isASPGR_IRI.toString());
                }

                if (fact.hasModifier(XmlMsgs.NOT_MODIFIER)) {
                    if (XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.polarity).equals("false")) {
                        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
                    }
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");
                } else {
                    if (XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.polarity).equals("true")) {
                        messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");
                    }
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
                }

                IRI forModifier = null;
                if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.maxCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.minCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.exactCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.allValuesFrom.getIRI();
                } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.someValuesFrom.getIRI();
                }

                if (user != null) {
                    if (fact.getPredicate().equals(NLResourceManager.isA.getIRI())) {
                        interestValue = "" + UMQM.getInterest(NLResourceManager.isA.getIRI(), fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                    } else {
                        interestValue = "" + UMQM.getInterest(NLResourceManager.instanceOf.getIRI(), fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                    }

                    assimilationScore = "" + calculateAssimilationScore(fact, user);
                } else {
                    interestValue = "1";
                    assimilationScore = "0";
                }
            } else if (fact.getPredicate().equals(NLResourceManager.sameIndividuals.getIRI())) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty, NLResourceManager.sameIndividuals.getIRI().toString());
                if (Languages.isEnglish(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.sameIndividualSPEN_IRI.toString());
                } else if (Languages.isGreek(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.sameIndividualSPGR_IRI.toString());
                }

                IRI forModifier = null;
                if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.maxCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.minCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.exactCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.allValuesFrom.getIRI();
                } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.someValuesFrom.getIRI();
                }

                if (user != null) {
                    interestValue = "" + UMQM.getInterest(NLResourceManager.sameIndividuals.getIRI(), fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                    assimilationScore = "" + calculateAssimilationScore(fact, user);
                } else {
                    interestValue = "1";
                    assimilationScore = "0";
                }
            } else if (fact.getPredicate().equals(NLResourceManager.differentIndividuals.getIRI())) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty, NLResourceManager.differentIndividuals.getIRI().toString());
                if (Languages.isEnglish(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.sameIndividualSPEN_IRI.toString());
                } else if (Languages.isGreek(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.sameIndividualSPGR_IRI.toString());
                }
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");

                IRI forModifier = null;
                if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.maxCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.minCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.exactCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.allValuesFrom.getIRI();
                } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.someValuesFrom.getIRI();
                }

                if (user != null) {
                    interestValue = "" + UMQM.getInterest(NLResourceManager.differentIndividuals.getIRI(), fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                    assimilationScore = "" + calculateAssimilationScore(fact, user);
                } else {
                    interestValue = "1";
                    assimilationScore = "0";
                }
            } else if (fact.getPredicate().equals(NLResourceManager.oneOf.getIRI())) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty, NLResourceManager.oneOf.getIRI().toString());
                if (Languages.isEnglish(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.isASPEN_IRI.toString());
                } else if (Languages.isGreek(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.isASPGR_IRI.toString());
                }

                IRI forModifier = null;
                if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.maxCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.minCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.exactCardinality.getIRI();
                } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.allValuesFrom.getIRI();
                } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                    forModifier = NLResourceManager.someValuesFrom.getIRI();
                }
                if (user != null) {
                    interestValue = "" + UMQM.getInterest(NLResourceManager.oneOf.getIRI(), fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                    assimilationScore = "" + calculateAssimilationScore(fact, user);
                } else {
                    interestValue = "1";
                    assimilationScore = "0";
                }
            }

            IRI propertyIRI = fact.getPredicate();
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI.toString());

            StringBuffer valueMessage;
            StringBuffer NLNameMessage;
            if (isConnective) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.isConnective, "true");
                valueMessage = new StringBuffer("and(");
                NLNameMessage = new StringBuffer("and(");
            } else {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.isConnective, "false");
                valueMessage = new StringBuffer("or(");
                NLNameMessage = new StringBuffer("or(");
            }

            boolean useKindOf = false;
            ArrayList<IRI> objectNLNames = new ArrayList<IRI>();
            for (Fact f : facts) {
                IRI objectNLName = null;

                boolean contains = false;
                for (OWLOntology ontology : mainModels) {
                    if (ontology.containsEntityInSignature(IRI.create(fact.getObject()), true)) {
                        contains = true;
                    }
                }
                if (contains) {
                    if (!f.getObject().isEmpty()) {
                        objectNLName = chooseNLName(IRI.create(f.getObject()), user);
                        if (objectNLName.equals(NLResourceManager.anonymous.getIRI())) {
                            objectNLName = null;
                        }
                    } else {
                        if (Languages.isEnglish(getLanguage())) {
                            objectNLName = DefaultResourcesManager.entityNLNEN_IRI;
                        } else if (Languages.isGreek(getLanguage())) {
                            objectNLName = DefaultResourcesManager.entityNLNGR_IRI;
                        }
                    }
                    if (objectNLName != null) {
                        NLNameMessage.append(objectNLName.toString()).append(", ");
                        objectNLNames.add(objectNLName);
                    }
                }
                //KIND OF
                if (objectNLName != null) {
                    valueMessage.append(f.getObject().toString()).append(", ");
                    if (NLGEngine.isClass(mainModels, f.getSubject())) {
                        if (f.getPredicate().equals(NLResourceManager.isA.getIRI())) {
                            if (!(NLNQM.getNLName(objectNLName).getHeadSlot() instanceof NLNAdjectiveSlot)) {
                                useKindOf = true;
                            }
                        }
                    }
                }
            }
            valueMessage = new StringBuffer(valueMessage.substring(0, valueMessage.length() - 2));
            valueMessage.append(")");
            NLNameMessage = new StringBuffer(NLNameMessage.substring(0, NLNameMessage.length() - 2));
            NLNameMessage.append(")");

            IRI subjectNLName = chooseNLName(fact.getSubject(), user);
            if (subjectNLName != null) {
                if (subjectNLName.equals(NLResourceManager.anonymous.getIRI())) {
                    subjectNLName = chooseAnonymousNLName(fact.getSubject(), user);
                	messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, "anonymous(" + subjectNLName.toString() + ')');
                } else {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, subjectNLName.toString());
                }
            }

            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.VALUE, valueMessage.toString());

            boolean contains = false;
            for (OWLOntology ontology : mainModels) {
                if (ontology.containsEntityInSignature(IRI.create(fact.getObject()), true)) {
                    contains = true;
                }
            }
            if (contains) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG, NLNameMessage.toString());
            }
            if (useKindOf) {
                if (Languages.isEnglish(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.kindOfSPEN_IRI.toString());
                } else if (Languages.isGreek(getLanguage())) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, DefaultResourcesManager.kindOfSPGR_IRI.toString());
                }
            }

            if (fact.getPredicate().equals(NLResourceManager.isA.getIRI()) || fact.getPredicate().equals(NLResourceManager.instanceOf.getIRI())) {
                String aggAllowedSubjectNLN = NLNQM.getIsAggAllowed(subjectNLName, getLanguage()) + "";

                boolean aggAllowedObjectNLN = true;
                for (IRI objectNLName : objectNLNames) {
                    if (!NLNQM.getIsAggAllowed(objectNLName, getLanguage())) {
                        aggAllowedObjectNLN = false;
                    }
                }

                if (aggAllowedSubjectNLN.equals("true") && aggAllowedObjectNLN) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED, "true");
                } else {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED, "false");
                }
            }

            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.REF, fact.getSubject().toString());
            if (OQM.getPropertySection(propertyIRI).equals(NLResourceManager.defaultSection.getIRI())) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ORDER_TAG, 0 + "");
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_TAG, 0 + "");
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_NAME, NLResourceManager.defaultSection.getIRI().getFragment());
            } else {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ORDER_TAG, "" + OQM.getPropertyOrder(propertyIRI));
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_TAG, "" + OQM.getPropertySectionOrder(propertyIRI));
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_NAME, OQM.getPropertySection(propertyIRI).getFragment());
            }
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.LEVEL, level + "");

            //if (level == 1) {
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, interestValue);
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, assimilationScore);
            //} else {
            //    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, 0 + "");
            //    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, "0");
            //}
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.prpType, XmlMsgs.ObjectProperty);

            // recursively call getClassMessages or getIndividualMessagesboolean
            contains = false;
            for (OWLOntology ontology : mainModels) {
                if (ontology.containsClassInSignature(fact.getSubject(), true)) {
                    contains = true;
                }
            }
            if (!contains) {
                contains = false;
                for (OWLOntology ontology : mainModels) {
                    if ((ontology.containsClassInSignature(IRI.create(fact.getObject()), true)) && (level < depth)) {
                        contains = true;
                    }
                }
                boolean contains2 = false;
                for (OWLOntology ontology : mainModels) {
                    if ((ontology.containsIndividualInSignature(IRI.create(fact.getObject()), true)) && (level < depth)) {
                        contains2 = true;
                    }
                }
                if (contains) {
                    getClassMessages(IRI.create(fact.getObject()), messages, message, depth, ++level, user);
                } else if (contains2) {
                    getIndividualMessages(IRI.create(fact.getObject()), messages, message, depth, ++level, user);
                }
            }
        }
    }

    private void addPropertyMessage(Fact fact, XmlMsgs messages, Node messageRoot, boolean isConnective, int depth, int level, NLGUser user) {
        ArrayList<Fact> facts = new ArrayList<Fact>();
        facts.add(fact);

        addPropertyMessage(facts, messages, messageRoot, isConnective, depth, level, user);
    }

    private void addPropertyMessage(ArrayList<Fact> facts, XmlMsgs messages, Node messageRoot, boolean isConnective, int depth, int level, NLGUser user) {
        if (!facts.isEmpty()) {
            boolean isObjectProperty = false;
            Fact fact = facts.get(0);

            //choose sentence plan and NLNames
            IRI propertyIRI = fact.getPredicate();

            for (OWLOntology ontology : mainModels) {
                if (ontology.containsObjectPropertyInSignature(propertyIRI, true)) {
                    isObjectProperty = true;
                }
            }

            IRI sentencePlanSelected = chooseSentencePlan(propertyIRI, user);

            Element message = messages.addNewElement((Element) messageRoot, "", "", propertyIRI.getFragment());
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.forProperty, propertyIRI.toString());
            if (sentencePlanSelected != null) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, sentencePlanSelected.toString());
            }

            IRI subjectNLName = chooseNLName(fact.getSubject(), user);
            if (subjectNLName != null) {
                if (subjectNLName.equals(NLResourceManager.anonymous.getIRI())) {
                    subjectNLName = chooseAnonymousNLName(fact.getSubject(), user);
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, "anonymous(" + subjectNLName.toString() + ')');
                } else {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG, subjectNLName.toString());
                }
            }
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.REF, fact.getSubject().toString());

            StringBuffer valueMessage;
            StringBuffer NLNameMessage;
            if (isConnective) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.isConnective, "true");
                valueMessage = new StringBuffer("and(");
                NLNameMessage = new StringBuffer("and(");
            } else {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.isConnective, "false");
                valueMessage = new StringBuffer("or(");
                NLNameMessage = new StringBuffer("or(");
            }
            ArrayList<IRI> objectNLNames = new ArrayList<IRI>();
            for (Fact f : facts) {
                IRI objectNLName = null;
                if (isObjectProperty) {
                    if (!f.getObject().isEmpty()) {
                        objectNLName = chooseNLName(IRI.create(f.getObject()), user);
                        if (objectNLName.equals(NLResourceManager.anonymous.getIRI())) {
                            objectNLName = null;
                        }
                    } else {
                        if (Languages.isEnglish(getLanguage())) {
                            objectNLName = DefaultResourcesManager.entityNLNEN_IRI;
                        } else if (Languages.isGreek(getLanguage())) {
                            objectNLName = DefaultResourcesManager.entityNLNGR_IRI;
                        }
                    }
                    if (objectNLName != null) {
                        valueMessage.append(f.getObject().toString()).append(", ");
                        if (objectNLName.equals(NLResourceManager.anonymous.getIRI())) {
                            objectNLName = MQM.getDefaultNLName(IRI.create(f.getObject()), NLNQM, getLanguage());
                        }
                        NLNameMessage.append(objectNLName.toString()).append(", ");
                        objectNLNames.add(objectNLName);
                    }
                } else {
                    valueMessage.append(f.getObject().toString()).append(", ");
                }
            }
            if (valueMessage.substring(valueMessage.length() - 2).equals(", ")) {
                valueMessage = new StringBuffer(valueMessage.substring(0, valueMessage.length() - 2));
            }
            valueMessage.append(")");
            if (NLNameMessage.substring(NLNameMessage.length() - 2).equals(", ")) {
                NLNameMessage = new StringBuffer(NLNameMessage.substring(0, NLNameMessage.length() - 2));
            }
            NLNameMessage.append(")");

            if (isObjectProperty) {
                if (subjectNLName != null) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG, NLNameMessage.toString());
                }
            }

            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.VALUE, valueMessage.toString());

            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.LEVEL, "" + level);
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ORDER_TAG, "" + OQM.getPropertyOrder(propertyIRI));
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_TAG, "" + OQM.getPropertySectionOrder(propertyIRI));
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SECTION_NAME, OQM.getPropertySection(propertyIRI).getFragment());

            if (fact.hasModifier(XmlMsgs.NOT_MODIFIER)) {
                if (XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.polarity).equals("false")) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
                }
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");
            } else {
                if (XmlMsgs.getAttribute(message, XmlMsgs.prefix, XmlMsgs.polarity).equals("true")) {
                    messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "false");
                }
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.polarity, "true");
            }

            if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG);
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.cardinality, fact.getCardinality() + "");
            } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG);
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.cardinality, fact.getCardinality() + "");
            } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG);
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.cardinality, fact.getCardinality() + "");
            } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG);
            } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG);
            }

            String aggAllowedSP = SPQM.getIsAggAllowed(sentencePlanSelected, getLanguage()) + "";
            String aggAllowedSubjectNLN = NLNQM.getIsAggAllowed(subjectNLName, getLanguage()) + "";

            boolean aggAllowedObjectNLN = true;
            boolean focusLost = false;
            for (IRI objectNLName : objectNLNames) {
                if (!NLNQM.getIsAggAllowed(objectNLName, getLanguage())) {
                    aggAllowedObjectNLN = false;
                }
                if (NLNQM.getFocusLost(objectNLName, getLanguage())) {
                    focusLost = true;
                }
            }

            if (aggAllowedSP.equals("true") && aggAllowedSubjectNLN.equals("true") && aggAllowedObjectNLN) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED, "true");
            } else {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED, "false");
            }
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST, focusLost + "");

            String interestValue = "";
            String assimilationScore = "";

            IRI forModifier = null;
            if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                forModifier = NLResourceManager.maxCardinality.getIRI();
            } else if (fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                forModifier = NLResourceManager.minCardinality.getIRI();
            } else if (fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                forModifier = NLResourceManager.exactCardinality.getIRI();
            } else if (fact.hasModifier(XmlMsgs.ALL_VALUES_FROM_RESTRICTION_TAG)) {
                forModifier = NLResourceManager.allValuesFrom.getIRI();
            } else if (fact.hasModifier(XmlMsgs.SOME_VALUES_FROM_RESTRICTION_TAG)) {
                forModifier = NLResourceManager.someValuesFrom.getIRI();
            }

            if (user != null) {
                interestValue = "" + UMQM.getInterest(propertyIRI, fact.getSubject(), forModifier, user.getUserModel().getUMIRI());
                assimilationScore = "" + calculateAssimilationScore(fact, user);
            } else {
                interestValue = "1";
                assimilationScore = "0";
            }

            //If a concatenation property already includes this fact, there is no interest in repeating it
            if (depth == 2) {
                if (!XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG).isEmpty()) {
                    SentencePlan plan = SPQM.getSentencePlan(IRI.create(XmlMsgs.getAttribute(messageRoot, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));

                    for (SPSlot slot : plan.getSlotsList()) {
                        if (slot instanceof SPConcatenationSlot) {
                            for (SPConcatenationPropertySlot propertySlot : ((SPConcatenationSlot) slot).getPropertySlots()) {
                                if (propertySlot.getPropertyIRI().equals(fact.getPredicate())) {
                                    interestValue = "0";
                                }
                            }
                        }
                    }
                }
            }

            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.INTEREST, interestValue);
            messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE, assimilationScore);

            if (isObjectProperty) {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.prpType, XmlMsgs.ObjectProperty);

                // recursively call getMsgsOfAnInstance
                boolean contains = false;
                for (OWLOntology ontology : mainModels) {
                    if (ontology.containsClassInSignature(fact.getSubject())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    if (level < depth) {
                        getIndividualMessages(IRI.create(fact.getObject()), messages, message, depth, ++level, user);
                    }
                }
            } else {
                messages.setAttr(message, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.prpType, XmlMsgs.DatatypeProperty);
            }
        }
    }

    /*  choose sentence plan for the specified property.
     *  if this property has already appeared in the description
     *  choose the same sentence plan as before.
     */
    public IRI chooseSentencePlan(IRI propertyIRI, NLGUser user) {
        IRI sentencePlanSelected = null;
        if (!usedSentencePlanInDescr.containsKey(propertyIRI)) {
            sentencePlanSelected = MQM.chooseSentencePlan(propertyIRI, SPQM, getLanguage(), user);

            usedSentencePlanInDescr.put(propertyIRI, sentencePlanSelected);
        } else {
            sentencePlanSelected = usedSentencePlanInDescr.get(propertyIRI);
        }
        return sentencePlanSelected;
    }

    /*  choose NLName for the specified individual/class.
     *  if this individual/class has already appeared in the description
     *  choose the same NLName as before.
     */
    public IRI chooseNLName(IRI indivOrClassIRI, NLGUser user) {
        IRI NLNameSelected = null;

        if (!usedNLNamesInDescr.containsKey(indivOrClassIRI)) {
            NLNameSelected = MQM.chooseNLName(indivOrClassIRI, NLNQM, getLanguage(), user);
            usedNLNamesInDescr.put(indivOrClassIRI, NLNameSelected);
        } else {
            NLNameSelected = usedNLNamesInDescr.get(indivOrClassIRI);
        }
        return NLNameSelected;
    }

    private IRI chooseAnonymousNLName(IRI indivOrClassIRI, NLGUser user) {
        if (!anonymousSuperClasses.containsKey(indivOrClassIRI)) {
            Set<OWLClassAssertionAxiom> superClassAssertions;
            Set<OWLClassExpression> equivalentClasses;

            ArrayList<IRI> superClasses = new ArrayList<IRI>();

            Set<OWLEntity> entities = new HashSet<OWLEntity>();
            for (OWLOntology ontology : mainModels) {
                entities.addAll(ontology.getEntitiesInSignature(indivOrClassIRI, true));
            }
            for (OWLEntity entity : entities) {
                superClassAssertions = null;
                equivalentClasses = null;
                if (entity.isOWLClass()) {
                    superClassAssertions = new HashSet<OWLClassAssertionAxiom>();
                    for (OWLOntology ontology : mainModels) {
                        superClassAssertions.addAll(ontology.getClassAssertionAxioms(entity.asOWLClass()));
                    }
                    equivalentClasses = new HashSet<OWLClassExpression>();
                    for (OWLOntology ontology : mainModels) {
                        equivalentClasses.addAll(entity.asOWLClass().getEquivalentClasses(ontology));
                    }
                } else if (entity.isOWLNamedIndividual()) {
                    superClassAssertions = new HashSet<OWLClassAssertionAxiom>();
                    for (OWLOntology ontology : mainModels) {
                        superClassAssertions.addAll(ontology.getClassAssertionAxioms(entity.asOWLNamedIndividual()));
                    }
                }
                if (superClassAssertions != null) {
                    for (OWLClassAssertionAxiom superClass : superClassAssertions) {
                        superClasses.addAll(NLGEngine.getSuperClasses(indivOrClassIRI, superClass.getClassExpression()));
                    }
                }
                if (equivalentClasses != null) {
                    for (OWLClassExpression equivalentClass : equivalentClasses) {
                        superClasses.addAll(NLGEngine.getSuperClasses(indivOrClassIRI, equivalentClass));
                    }
                }
                for (int i = superClasses.size() - 1; i >= 0; i--) {
                    IRI superName = chooseNLName(superClasses.get(i), user);

                    if ((superName != null) && (!superName.equals(NLResourceManager.anonymous.getIRI()))) {
                        anonymousSuperClasses.put(indivOrClassIRI, superName);
                        return superName;
                    }
                }
                for (IRI superClass : superClasses) {
                    IRI superName = chooseAnonymousNLName(superClass, user);
                    if (superName != null) {
                        anonymousSuperClasses.put(indivOrClassIRI, superName);
                        return superName;
                    }
                }
                return MQM.chooseDefaultNLName(indivOrClassIRI, NLNQM, getLanguage());
            }
        } else {
            return anonymousSuperClasses.get(indivOrClassIRI);
        }
        return null;
    }

    public XmlMsgs visitRecursivelyForInverseFacts(Node n, XmlMsgs messages, ArrayList<String> array) {
        if (n.hasChildNodes()) {
            NodeList list = n.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null) {
                    if (!node.getNodeName().equals(XmlMsgs.prefix + ":type")) {
                        if ((node.getAttributes().getNamedItem(XmlMsgs.prefix + ":" + XmlMsgs.prpType).getNodeValue()).equals(XmlMsgs.ObjectProperty)) {
                            String property1 = node.getNodeName();// trexon kombos
                            String namespace = property1.substring(0, property1.indexOf(':'));

                            Set<OWLEntity> entities = new HashSet<OWLEntity>();
                            for (OWLOntology ontology : mainModels) {
                                entities.addAll(ontology.getEntitiesInSignature(IRI.create(messages.getRoot().getAttributes().getNamedItem("xmlns:" + namespace).getNodeValue() + property1.substring(3)), true));
                            }

                            for (OWLEntity curProp : entities) {
                                if (curProp.isOWLObjectProperty()) {
                                    String val = node.getAttributes().getNamedItem("nlowl:Val").getNodeValue();
                                    String ref = node.getAttributes().getNamedItem("nlowl:ref").getNodeValue();
                                    array.add(list.item(i).getNodeName());
                                    for (int k = 0; k < array.size(); k++) {

                                        Set<OWLEntity> entities2 = new HashSet<OWLEntity>();
                                        for (OWLOntology ontology : mainModels) {
                                            entities2.addAll(ontology.getEntitiesInSignature(IRI.create(messages.getRoot().getAttributes().getNamedItem("xmlns:p1").getNodeValue() + array.get(k).substring(3)), true));
                                        }

                                        for (OWLEntity curProp2 : entities2) {
                                            if (curProp2.isOWLObjectProperty()) {
                                                //Makis 2 - specific transformation into OWLAPI could result in error
                                                //What we need is to check whether a property is the inverse of another property
                                                boolean contains = false;
                                                for (OWLOntology ontology : mainModels) {
                                                    if (curProp.asOWLObjectProperty().getInverses(ontology).contains(curProp2)) {
                                                        contains = true;
                                                    }
                                                }

                                                if (contains) {
                                                    if (val.equals(array.get(k).substring(array.get(k).lastIndexOf(' ') + 1))) {
                                                        if (ref.equals(array.get(k).substring(array.get(k).indexOf(' ') + 1, array.get(k).lastIndexOf(' ')))) {
                                                            messages.removeNode(node);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < list.getLength(); i++) {
                if (list.item(i).hasChildNodes()) {
                    messages = visitRecursivelyForInverseFacts(list.item(i), messages, array);
                }
            }
        }

        return messages;
    }

    public XmlMsgs removeInverseFacts(XmlMsgs messages, int depth) {

        ArrayList<String> array = new ArrayList<String>();
        if (depth == 1) {
            return messages;
        }
        NodeList list = messages.getRoot().getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).hasChildNodes() && !(list.item(i).getNodeName().equals("nlowl:type"))) {
                String val = list.item(i).getAttributes().getNamedItem("nlowl:Val").getNodeValue();
                String ref = list.item(i).getAttributes().getNamedItem("nlowl:ref").getNodeValue();
                array.add(list.item(i).getNodeName() + " " + val + " " + ref);

                messages = visitRecursivelyForInverseFacts(list.item(i), messages, array);
            }
        }
        return messages;
    }

    public float calculateAssimilationScore(Fact factID, NLGUser user) {
        IRI forModifier = null;
        if (factID.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
            forModifier = NLResourceManager.maxCardinality.getIRI();
        } else if (factID.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
            forModifier = NLResourceManager.minCardinality.getIRI();
        } else if (factID.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
            forModifier = NLResourceManager.exactCardinality.getIRI();
        }

        int factMentionedCount = user.getFactMentionedCount(factID);
        int maxRepetitions = UMQM.getRepetitions(factID.getPredicate(), factID.getSubject(), forModifier, user.getUserModel().getUMIRI());
        if (maxRepetitions == 0) {
            return 0;
        }
        return factMentionedCount / maxRepetitions;
    }

    public boolean allFactsAreAssimilated() {
        return allFactsAreAssimilated;
    }

    public XmlMsgs getTheMostInterestingUnassimilatedFacts(XmlMsgs messages, int depth, NLGUser user) {
        if (user != null) {
            int levelsSizes[] = new int[depth];

            for (int i = 0; i < levelsSizes.length; i++) {
                levelsSizes[i] = 0;
            }

            Node messageRoot = messages.getRoot();
            ArrayList<Node> factNodes = XmlMsgs.returnChildNodes(messageRoot); // add first level's facts

            levelsSizes[0] = factNodes.size();

            int levelBegin = 0;

            // get all facts from xml tree and put them into an array
            int d = 1;
            while (d < depth) // while there is a level to explore
            {
                int level_size = 0;
                for (int i = levelBegin; i < levelsSizes[d - 1]; i++) // for each node of the d-1 level
                {
                    Node currNode = factNodes.get(i);
                    ArrayList<Node> currNode_NextLevelFacts = XmlMsgs.returnChildNodes(currNode);

                    level_size += currNode_NextLevelFacts.size();
                    factNodes.addAll(currNode_NextLevelFacts); // add their childs to facts
                }

                levelsSizes[d] = level_size;
                levelBegin += level_size;
                d++;
            }

            //Remove inconsistent and redundantmessages on cardinality
            for (Node checkNode : factNodes) {
                if (XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                    String forPropertyMax = "";
                    if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                    } else if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                    }
                    String forOwnerMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.REF);
                    int checkCardinality = Integer.parseInt(XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                    for (Node secondaryNode : factNodes) {
                        if (!secondaryNode.equals(checkNode)) {
                            if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                int secondaryCardinality = Integer.parseInt(XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (checkCardinality < secondaryCardinality) {
                                        if (messages.getMessages().contains(secondaryNode)) {
                                            messages.removeNode(secondaryNode);
                                        }
                                        if (messages.getMessages().contains(checkNode)) {
                                            messages.removeNode(checkNode);
                                        }
                                    }
                                    if (checkCardinality == secondaryCardinality) {
                                        messages.setAttr((Element) secondaryNode, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.modifier, XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG);

                                        if (messages.getMessages().contains(checkNode)) {
                                            messages.removeNode(checkNode);
                                        }
                                    }
                                }
                            } else if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                int secondaryCardinality = Integer.parseInt(XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (checkCardinality <= secondaryCardinality) {
                                        if (messages.getMessages().contains(secondaryNode)) {
                                            messages.removeNode(secondaryNode);
                                        }
                                    } else {
                                        if (messages.getMessages().contains(checkNode)) {
                                            messages.removeNode(checkNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                    String forPropertyMax = "";
                    if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                    } else if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                    }
                    String forOwnerMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.REF);
                    int currentCardinality = Integer.parseInt(XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                    for (Node secondaryNode : factNodes) {
                        if (!secondaryNode.equals(checkNode)) {
                            if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (messages.getMessages().contains(secondaryNode)) {
                                        messages.removeNode(secondaryNode);
                                    }
                                }
                            } else if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (messages.getMessages().contains(secondaryNode)) {
                                        messages.removeNode(secondaryNode);
                                    }
                                }
                            } else if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                int secondaryCardinality = Integer.parseInt(XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (currentCardinality == secondaryCardinality) {
                                        if (messages.getMessages().contains(secondaryNode)) {
                                            messages.removeNode(secondaryNode);
                                        }
                                    } else {
                                        if (messages.getMessages().contains(checkNode)) {
                                            messages.removeNode(checkNode);
                                        }
                                        if (messages.getMessages().contains(secondaryNode)) {
                                            messages.removeNode(secondaryNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                    String forPropertyMax = "";
                    if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                    } else if (!XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                        forPropertyMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                    }
                    String forOwnerMax = XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.REF);
                    int checkCardinality = Integer.parseInt(XmlMsgs.getAttribute(checkNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                    for (Node secondaryNode : factNodes) {
                        if (!secondaryNode.equals(checkNode)) {
                            if (XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.modifier).equals(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG)) {
                                String forPropertyMin = "";
                                if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forProperty);
                                } else if (!XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty).isEmpty()) {
                                    forPropertyMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.forDomainIndependentProperty);
                                }
                                String forOwnerMin = XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.REF);

                                int secondaryCardinality = Integer.parseInt(XmlMsgs.getAttribute(secondaryNode, XmlMsgs.prefix, XmlMsgs.cardinality));

                                if (forPropertyMax.equals(forPropertyMin) && forOwnerMax.equals(forOwnerMin)) {
                                    if (checkCardinality >= secondaryCardinality) {
                                        if (messages.getMessages().contains(secondaryNode)) {
                                            messages.removeNode(secondaryNode);
                                        }
                                    } else {
                                        if (messages.getMessages().contains(checkNode)) {
                                            messages.removeNode(checkNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //logger.debug("Facts size:" + Facts.size());
            Object[] factNodesArray = factNodes.toArray();

            // sort each level by interest
            levelBegin = 0;
            for (int i = 0; i < depth; i++) {
                if (i == 0) {
                    Arrays.sort(factNodesArray, levelBegin, levelBegin + levelsSizes[i], new InterestComparatorImpl(false));
                } else {
                    levelBegin += levelsSizes[i - 1];
                    int LevelEnd = levelBegin + levelsSizes[i];
                    //logger.debug("Levelbegin:" + Levelbegin + " LevelEnd" + LevelEnd);
                    Arrays.sort(factNodesArray, levelBegin, LevelEnd, new InterestComparatorImpl(false));
                }
            }

            //logger.debug("Facts array size:" + Facts_Array.length);

            int maxMessages = user.getUserModel().getMaxMessagesPerPage();

            // check if all first level facts are assimilated
            boolean allFirstLevelFactsAssimilated = true;
            for (int i = 0; i < levelsSizes[0]; i++) {
                Node currNode = (Node) factNodesArray[i];
                if (Float.parseFloat(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE)) < 1.0F
                        && Integer.parseInt(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.INTEREST)) != 0) {
                    allFirstLevelFactsAssimilated = false;
                }
            }

            allFactsAreAssimilated = allFirstLevelFactsAssimilated;

            if (!allFirstLevelFactsAssimilated) {
                int countMessages = 0;

                for (int i = 0; i < factNodesArray.length; i++) {
                    Node currNode = (Node) factNodesArray[i];

                    //keep the facts which are not assimilated and have positive interest 
                    if ((countMessages < maxMessages || maxMessages == UserModel.USER_MODEL_ALL_MESSAGES_PER_PAGE)
                            && Integer.parseInt(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.INTEREST)) != 0
                            && Float.parseFloat(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE)) < 1.0F) {
                        // keep it and
                        // increase its assimilation score!!!!    
                        countMessages++;

                        Fact fact = new Fact("[" + XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.REF) + ", " + XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.forProperty) + ", " + XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.VALUE) + "]");
                        if (!XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.modifier).trim().isEmpty()) {
                            fact.addModifier(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.modifier));
                        }
                        if (fact.hasModifier(XmlMsgs.MAX_CARDINALITY_RESTRICTION_TAG) || fact.hasModifier(XmlMsgs.MIN_CARDINALITY_RESTRICTION_TAG) || fact.hasModifier(XmlMsgs.EXACT_CARDINALITY_RESTRICTION_TAG)) {
                            if (!XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.cardinality).trim().isEmpty()) {
                                fact.setCardinality(Integer.parseInt(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.cardinality)));
                            }
                        }
                        if (XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.polarity).trim().equals("false")) {
                            fact.setPolarity(false);
                        }
                        if (AxiomType.getAxiomType(fact.getPredicate().getFragment()) == null) {
                            conveyedFacts.add(fact);

                            usedSentencePlans.add(IRI.create(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG)));
                            usedNLNames.add(IRI.create(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG)));
                            if (!XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG).isEmpty()) {
                                usedNLNames.add(IRI.create(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG)));
                            }
                        } else {
                            conveyedFacts.add(fact);

                            usedNLNames.add(IRI.create(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.SUB_NLNAME_TAG)));
                            if (!XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG).isEmpty()) {
                                usedNLNames.add(IRI.create(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG)));
                            }
                            //mentionedEntities.add( XmlMsgs.getAttribute(currNode,XmlMsgs.prefix, XmlMsgs.VALUE));
                        }
                    } else if (assimilationON && interestON) {
                        //logger.debug("deleting from the xml tree");
                        // delete from the xml tree
                        currNode.getParentNode().removeChild(currNode);
                    }
                }
            } else {//AllFirstLevelFactsAssimilated
                for (int i = 0; i < factNodesArray.length; i++) {
                    Node currNode = (Node) factNodesArray[i];
                    if (Integer.parseInt(XmlMsgs.getAttribute(currNode, XmlMsgs.prefix, XmlMsgs.INTEREST)) != 0) {// interest != 0
                    } else {
                        if (assimilationON && interestON) {
                            currNode.getParentNode().removeChild(currNode);
                        }
                    }
                }
            }//AllFirstLevelFactsAssimilated

            return messages;
        }
        return messages;
    }
}