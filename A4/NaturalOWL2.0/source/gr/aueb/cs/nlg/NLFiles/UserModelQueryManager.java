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
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

public class UserModelQueryManager {

    private HashMap<IRI, UserModel> userModels;
    private NLResourceManager NLResourcesManager;
    public HashSet<OWLOntology> mainModels;

    public UserModelQueryManager(NLResourceManager NLResourcesManager) {
        this.NLResourcesManager = NLResourcesManager;

        mainModels = new HashSet<OWLOntology>();
        init();
    }

    // initialize all hashtables
    private void init() {
        userModels = new HashMap<IRI, UserModel>();

        UserModel UM = new UserModel(NLResourceManager.globalUserModel.getIRI());

        UM.setMaxMessagesPerSentence(1);
        UM.setMaxMessagesPerPage(-1);
        UM.setGlobalInterest(UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setGlobalRepetitions(UserModel.USER_MODEL_DEFAULT_REPETITIONS);

        UM.setPropertyLevelInterest(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);

        userModels.put(UM.getUMIRI(), UM);
    }

    /* toIRI - IRI to be copied TO
     * fromIRI - IRI to be copied FROM
     */
    public void duplicateUserModel(IRI fromIRI, IRI toIRI) {
        UserModel fromModel = userModels.get(fromIRI); // remove ut
        UserModel toModel = new UserModel(toIRI, fromModel.getMaxMessagesPerSentence(), fromModel.getMaxMessagesPerPage());

        userModels.put(toIRI, toModel); // add ut with new name

        setGlobalInterest(toIRI, fromModel.getGlobalInterest());
        setGlobalRepetitions(toIRI, fromModel.getGlobalRepetitions());

        for (IRI sentencePlanIRI : fromModel.getSentencePlansIRIs()) {
            setSentencePlanAppropriateness(sentencePlanIRI, toIRI, fromModel.getSentencePlanAppropriateness(sentencePlanIRI));
        }
        for (IRI NLNameIRI : fromModel.getNLNamesIRIs()) {
            setNLNameAppropriateness(NLNameIRI, toIRI, fromModel.getNLNameAppropriateness(NLNameIRI));
        }

        for (UMPrpLevelKey key : fromModel.getPropertyLevelKeys()) {
            setPropertyLevelInterest(key.getForProperty(), key.getForModifier(), toIRI, fromModel.getPropertyLevelInterest(key.getForProperty(), key.getForModifier()));
            setPropertyLevelRepetitions(key.getForProperty(), key.getForModifier(), toIRI, fromModel.getPropertyLevelRepetitions(key.getForProperty(), key.getForModifier()));
        }

        for (UMPrpClassLevelKey key : fromModel.getClassLevelKeys()) {
            setClassLevelInterest(key.getForProperty(), key.getForClass(), key.getForModifier(), toIRI, fromModel.getClassLevelInterest(key.getForProperty(), key.getForClass(), key.getForModifier()));
            setClassLevelRepetitions(key.getForProperty(), key.getForClass(), key.getForModifier(), toIRI, fromModel.getClassLevelRepetitions(key.getForProperty(), key.getForClass(), key.getForModifier()));
        }

        for (UMPrpInstanceLevelKey key : fromModel.getInstanceLevelKeys()) {
            setInstanceLevelInterest(key.getForProperty(), key.getForInstance(), key.getForModifier(), toIRI, fromModel.getInstanceLevelInterest(key.getForProperty(), key.getForInstance(), key.getForModifier()));
            setInstanceLevelRepetitions(key.getForProperty(), key.getForInstance(), key.getForModifier(), toIRI, fromModel.getInstanceLevelRepetitions(key.getForProperty(), key.getForInstance(), key.getForModifier()));
        }
    }

    // delete a user type
    public void deleteUserModel(IRI UserModelIRI) {
        // delete user type and its parameters
        UserModel UT = userModels.remove(UserModelIRI);

        //Ontology side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UserModelIRI);
        for (OWLOntology mainModel : mainModels) {
            for (UMPrpLevelKey key : UT.getPropertyLevelKeys()) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), null, null, key.getForModifier(), null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    if (key.getForProperty() != null) {
                        OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                        OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForProperty());
                        removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                    if (key.getForModifier() != null) {
                        objectProperty = NLResourceManager.forModifier;
                        objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(key.getForModifier()));
                        removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    int oldValue = UT.getPropertyLevelInterest(key.getForProperty(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    oldValue = UT.getPropertyLevelRepetitions(key.getForProperty(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                }
            }
            for (UMPrpClassLevelKey key : UT.getClassLevelKeys()) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), key.getForClass(), null, key.getForModifier(), null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForProperty());
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    annotationProperty = NLResourceManager.forOwner;
                    annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForClass());
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    if (key.getForModifier() != null) {
                        objectProperty = NLResourceManager.forModifier;
                        objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(key.getForModifier()));
                        removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    int oldValue = UT.getClassLevelInterest(key.getForProperty(), key.getForClass(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    oldValue = UT.getClassLevelRepetitions(key.getForProperty(), key.getForClass(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                }
            }
            for (UMPrpInstanceLevelKey key : UT.getInstanceLevelKeys()) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), null, key.getForInstance(), key.getForModifier(), null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForProperty());
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    annotationProperty = NLResourceManager.forOwner;
                    annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForInstance());
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    if (key.getForModifier() != null) {
                        objectProperty = NLResourceManager.forModifier;
                        objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(key.getForModifier()));
                        removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    int oldValue = UT.getInstanceLevelInterest(key.getForProperty(), key.getForInstance(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }

                    oldValue = UT.getInstanceLevelRepetitions(key.getForProperty(), key.getForInstance(), key.getForModifier());

                    if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                        OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                        OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                        removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                }
            }
            for (IRI sentencePlanIRI : UT.getSentencePlansIRIs()) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, sentencePlanIRI, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forSentencePlan;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(sentencePlanIRI));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    int oldValue = UT.getSentencePlanAppropriateness(sentencePlanIRI);

                    OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
            for (IRI NLNameIRI : UT.getNLNamesIRIs()) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, null, NLNameIRI);

                if (anIndiv != null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forNLName;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(NLNameIRI));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    int oldValue = UT.getNLNameAppropriateness(NLNameIRI);

                    OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }
    }

    // add a new user type
    public void addUserModel(IRI UserModelIRI) {
        UserModel UM = new UserModel(UserModelIRI, 1, 5);

        UM.setPropertyLevelInterest(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
        UM.setPropertyLevelInterest(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
        UM.setPropertyLevelRepetitions(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);

        userModels.put(UserModelIRI, UM);
    }

    public void addSentencePlan(IRI sentencePlanIRI) {
        for (UserModel UT : userModels.values()) {
            UT.addSentencePlan(sentencePlanIRI);
        }
    }

    public void addNLName(IRI NLNameIRI) {
        for (UserModel UT : userModels.values()) {
            UT.addNLName(NLNameIRI);
        }
    }

    // rename user type
    public void renameUserModel(IRI oldUTIRI, IRI newUTIRI) {
        UserModel UT = userModels.remove(oldUTIRI); // remove ut

        //Ontology side        
        OWLEntityRenamer renamer = new OWLEntityRenamer(NLResourcesManager.getOntologyManager(), mainModels);

        for (OWLOntologyChange renameChange : renamer.changeIRI(oldUTIRI, newUTIRI)) {
            NLResourcesManager.getOntologyManager().applyChange(renameChange);
        }

        userModels.put(newUTIRI, UT); // add ut with new name
    }

    public void renameProperty(IRI oldPropertyIRI, IRI newPropertyIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpLevelKey key : UT.getPropertyLevelKeys()) {
                    if (key.getForProperty().equals(oldPropertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), oldPropertyIRI, null, null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, oldPropertyIRI);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, newPropertyIRI);
                            AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
                for (UMPrpClassLevelKey key : UT.getClassLevelKeys()) {
                    if (key.getForProperty().equals(oldPropertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), oldPropertyIRI, key.getForClass(), null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, oldPropertyIRI);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, newPropertyIRI);
                            AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
                for (UMPrpInstanceLevelKey key : UT.getInstanceLevelKeys()) {
                    if (key.getForProperty().equals(oldPropertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), oldPropertyIRI, null, key.getForInstance(), key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, oldPropertyIRI);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, newPropertyIRI);
                            AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
            }
            UT.renameProperty(oldPropertyIRI, newPropertyIRI);
        }
    }

    public void renameClass(IRI oldClassIRI, IRI newClassIRI) {
        for (UserModel UT : userModels.values()) {
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpClassLevelKey key : UT.getClassLevelKeys()) {
                    if (key.getForClass().equals(oldClassIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), oldClassIRI, null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forOwner;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, oldClassIRI);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, newClassIRI);
                            AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
            }
            UT.renameClass(oldClassIRI, newClassIRI);
        }
    }

    public void renameInstance(IRI oldInstanceIRI, IRI newInstanceIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpInstanceLevelKey key : UT.getInstanceLevelKeys()) {
                    if (key.getForInstance().equals(oldInstanceIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), null, oldInstanceIRI, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forOwner;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, oldInstanceIRI);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, newInstanceIRI);
                            AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                        }
                    }
                }
            }
            UT.renameInstance(oldInstanceIRI, newInstanceIRI);
        }
    }

    public void renameSentencePlan(IRI oldSentencePlanIRI, IRI newSentencePlanIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, oldSentencePlanIRI, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forSentencePlan;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(oldSentencePlanIRI));
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(newSentencePlanIRI));
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            }
            UT.renameSentencePlan(oldSentencePlanIRI, newSentencePlanIRI);
        }
    }

    public void renameNLName(IRI oldNLNameIRI, IRI newNLNameIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, null, oldNLNameIRI);

                if (anIndiv != null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forNLName;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(oldNLNameIRI));
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(newNLNameIRI));
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            }
            UT.renameNLName(oldNLNameIRI, newNLNameIRI);
        }
    }

    public void deleteProperty(IRI propertyIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UT.getUMIRI());
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpLevelKey key : UT.getPropertyLevelKeys()) {
                    if (key.getForProperty().equals(propertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), propertyIRI, null, null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                            OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, propertyIRI);
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            int oldValue = UT.getPropertyLevelInterest(propertyIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }

                            oldValue = UT.getPropertyLevelRepetitions(propertyIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }
                        }
                    }
                }
                for (UMPrpClassLevelKey key : UT.getClassLevelKeys()) {
                    if (key.getForProperty().equals(propertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), propertyIRI, key.getForClass(), null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                            OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, propertyIRI);
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationProperty = NLResourceManager.forOwner;
                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForClass());
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            int oldValue = UT.getClassLevelInterest(propertyIRI, key.getForClass(), key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }

                            oldValue = UT.getClassLevelRepetitions(propertyIRI, key.getForClass(), key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }
                        }
                    }
                }
                for (UMPrpInstanceLevelKey key : UT.getInstanceLevelKeys()) {
                    if (key.getForProperty().equals(propertyIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), propertyIRI, null, key.getForInstance(), key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                            OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, propertyIRI);
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationProperty = NLResourceManager.forOwner;
                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForInstance());
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            int oldValue = UT.getInstanceLevelInterest(propertyIRI, key.getForInstance(), key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }

                            oldValue = UT.getInstanceLevelRepetitions(propertyIRI, key.getForInstance(), key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }
                        }
                    }
                }
            }

            UT.deleteProperty(propertyIRI);
        }
    }

    public void deleteClass(IRI classIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UT.getUMIRI());
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpClassLevelKey key : UT.getClassLevelKeys()) {
                    if (key.getForClass().equals(classIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), classIRI, null, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                            OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForProperty());
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationProperty = NLResourceManager.forOwner;
                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, classIRI);
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            int oldValue = UT.getClassLevelInterest(key.getForProperty(), classIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }

                            oldValue = UT.getClassLevelRepetitions(key.getForProperty(), classIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }
                        }
                    }
                }
            }
            UT.deleteClass(classIRI);
        }
    }

    public void deleteInstance(IRI instanceIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UT.getUMIRI());
            for (OWLOntology mainModel : mainModels) {
                for (UMPrpInstanceLevelKey key : UT.getInstanceLevelKeys()) {
                    if (key.getForInstance().equals(instanceIRI)) {
                        OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), key.getForProperty(), null, instanceIRI, key.getForModifier(), null, null);

                        if (anIndiv == null) {
                            anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                            }

                            OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                            OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                            OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, key.getForProperty());
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            annotationProperty = NLResourceManager.forOwner;
                            annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, instanceIRI);
                            removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                            int oldValue = UT.getInstanceLevelInterest(key.getForProperty(), instanceIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }

                            oldValue = UT.getInstanceLevelRepetitions(key.getForProperty(), instanceIRI, key.getForModifier());

                            if (oldValue != UserModel.USER_MODEL_UNDEFINED) {
                                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                                removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                            }
                        }
                    }
                }
            }

            UT.deleteInstance(instanceIRI);
        }
    }

    public void deleteSentencePlan(IRI sentencePlanIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UT.getUMIRI());
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, sentencePlanIRI, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forSentencePlan;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(sentencePlanIRI));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    int oldValue = UT.getSentencePlanAppropriateness(sentencePlanIRI);

                    OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }

            UT.deleteSentencePlan(sentencePlanIRI);
        }
    }

    public void deleteNLName(IRI NLNameIRI) {
        for (UserModel UT : userModels.values()) {
            //Ontology Side
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(UT.getUMIRI());
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, UT.getUMIRI(), null, null, null, null, null, NLNameIRI);

                if (anIndiv != null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forNLName;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(NLNameIRI));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    int oldValue = UT.getNLNameAppropriateness(NLNameIRI);

                    OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }

            UT.deleteNLName(NLNameIRI);
        }
    }

    public void setMaxMessagesPerPage(IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        //Ontology side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        int oldValue = UT.getMaxMessagesPerPage();
        for (OWLOntology mainModel : mainModels) {
            OWLDataProperty dataProperty = NLResourceManager.maxMessagesPerPage;
            OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, oldValue);
            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

            if (value != UserModel.USER_MODEL_ALL_MESSAGES_PER_PAGE) {
                dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            }
        }

        UT.setMaxMessagesPerPage(value);
    }

    public void setMaxMessagesPerSentence(IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        //Ontology side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        int oldValue = UT.getMaxMessagesPerSentence();
        for (OWLOntology mainModel : mainModels) {
            OWLDataProperty dataProperty = NLResourceManager.maxMessagesPerSentence;
            OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, oldValue);
            RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

            dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, value);
            AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
        }

        UT.setMaxMessagesPerSentence(value);
    }

    public void setGlobalInterest(IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value > UserModel.USER_MODEL_MAX_INTEREST) {
            value = UserModel.USER_MODEL_MAX_INTEREST;
        } else if (value < UserModel.USER_MODEL_UNDEFINED) {
            value = UserModel.USER_MODEL_DEFAULT_INTEREST;
        }

        //Ontology side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        int oldValue = UT.getGlobalInterest();
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, null, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getGlobalInterest() == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }
        }

        UT.setGlobalInterest(value);
    }

    public void setGlobalRepetitions(IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value < 0) {
            value = UserModel.USER_MODEL_DEFAULT_REPETITIONS;
        }

        //Ontology side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        int oldValue = UT.getGlobalRepetitions();
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, null, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getGlobalRepetitions() == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }
        }

        UT.setGlobalRepetitions(value);
    }

    public void setPropertyLevelInterest(IRI forProperty, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value > UserModel.USER_MODEL_MAX_INTEREST) {
            value = UserModel.USER_MODEL_MAX_INTEREST;
        } else if (value < UserModel.USER_MODEL_UNDEFINED) {
            value = UserModel.USER_MODEL_UNDEFINED;
        }

        //Ontology Side
        if (forProperty != null) {
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, null, null, forModifier, null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                        OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                        OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                        AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    }

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                    if (forModifier != null) {
                        OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                        OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                        addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    }
                } else {
                    int oldValue = UT.getPropertyLevelInterest(forProperty, forModifier);

                    OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }

                if (value != UserModel.USER_MODEL_UNDEFINED) {
                    OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else if (UT.getPropertyLevelRepetitions(forProperty, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    if (forModifier != null) {
                        objectProperty = NLResourceManager.forModifier;
                        objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                        removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                }
            }
        } else if (forModifier != null) {
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, forModifier, null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else {
                    int oldValue = UT.getPropertyLevelInterest(forProperty, forModifier);

                    OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }

                if (value != UserModel.USER_MODEL_UNDEFINED) {
                    OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else if (UT.getPropertyLevelRepetitions(forProperty, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }

        UT.setPropertyLevelInterest(forProperty, forModifier, value);
    }

    public void setPropertyLevelRepetitions(IRI forProperty, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value < 0) {
            value = UserModel.USER_MODEL_DEFAULT_REPETITIONS;
        }

        //Ontology Side
        if (forProperty != null) {
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, null, null, forModifier, null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                        OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                        OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                        AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    }

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                    if (forModifier != null) {
                        OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                        OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                        addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                    }
                } else {
                    int oldValue = UT.getPropertyLevelRepetitions(forProperty, forModifier);

                    OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }

                if (value != UserModel.USER_MODEL_UNDEFINED) {
                    OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else if (UT.getPropertyLevelInterest(forProperty, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                    OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                    removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    if (forModifier != null) {
                        objectProperty = NLResourceManager.forModifier;
                        objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                        removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                        NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                    }
                }
            }
        } else if (forModifier != null) {
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
            for (OWLOntology mainModel : mainModels) {
                OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, forModifier, null, null);

                if (anIndiv == null) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                        anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                    }

                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else {
                    int oldValue = UT.getPropertyLevelRepetitions(forProperty, forModifier);

                    OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }

                if (value != UserModel.USER_MODEL_UNDEFINED) {
                    OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                    OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                } else if (UT.getPropertyLevelInterest(forProperty, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }

        UT.setPropertyLevelRepetitions(forProperty, forModifier, value);
    }

    public void setClassLevelInterest(IRI forProperty, IRI forClass, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value > UserModel.USER_MODEL_MAX_INTEREST) {
            value = UserModel.USER_MODEL_MAX_INTEREST;
        } else if (value < UserModel.USER_MODEL_UNDEFINED) {
            value = UserModel.USER_MODEL_UNDEFINED;
        }

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, forClass, null, forModifier, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forClass);
                addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                if (forModifier != null) {
                    OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                int oldValue = UT.getClassLevelInterest(forProperty, forClass, forModifier);

                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getClassLevelRepetitions(forProperty, forClass, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forClass);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                if (forModifier != null) {
                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }

        UT.setClassLevelInterest(forProperty, forClass, forModifier, value);
    }

    public void setClassLevelRepetitions(IRI forProperty, IRI forClass, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value < 0) {
            value = UserModel.USER_MODEL_DEFAULT_REPETITIONS;
        }

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, forClass, null, forModifier, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forClass);
                addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                if (forModifier != null) {
                    OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                int oldValue = UT.getClassLevelRepetitions(forProperty, forClass, forModifier);

                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getClassLevelInterest(forProperty, forClass, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forClass);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                if (forModifier != null) {
                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }
        UT.setClassLevelRepetitions(forProperty, forClass, forModifier, value);
    }

    public void setInstanceLevelInterest(IRI forProperty, IRI forInstance, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value > UserModel.USER_MODEL_MAX_INTEREST) {
            value = UserModel.USER_MODEL_MAX_INTEREST;
        } else if (value < UserModel.USER_MODEL_UNDEFINED) {
            value = UserModel.USER_MODEL_UNDEFINED;
        }

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, null, forInstance, forModifier, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forInstance);
                addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                if (forModifier != null) {
                    OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                int oldValue = UT.getInstanceLevelInterest(forProperty, forInstance, forModifier);

                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.hasInterest;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getInstanceLevelRepetitions(forProperty, forInstance, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forInstance);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                if (forModifier != null) {
                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }
        UT.setInstanceLevelInterest(forProperty, forInstance, forModifier, value);
    }

    public void setInstanceLevelRepetitions(IRI forProperty, IRI forInstance, IRI forModifier, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        if (value < 0) {
            value = UserModel.USER_MODEL_DEFAULT_REPETITIONS;
        }

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, forProperty, null, forInstance, forModifier, null, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                if (!userType.equals(NLResourceManager.globalUserModel.getIRI())) {
                    OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                    AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forInstance);
                addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                if (forModifier != null) {
                    OWLObjectProperty objectProperty = NLResourceManager.forModifier;
                    OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
                }
            } else {
                int oldValue = UT.getInstanceLevelRepetitions(forProperty, forInstance, forModifier);

                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_UNDEFINED) {
                OWLDataProperty dataProperty = NLResourceManager.maxRepetitions;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else if (UT.getInstanceLevelInterest(forProperty, forInstance, forModifier) == UserModel.USER_MODEL_UNDEFINED) {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                OWLAnnotationProperty annotationProperty = NLResourceManager.forProperty;
                OWLAnnotationAssertionAxiom annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forProperty);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                annotationProperty = NLResourceManager.forOwner;
                annotationAssertion = NLResourcesManager.getDataFactory().getOWLAnnotationAssertionAxiom(annotationProperty, anIndiv, forInstance);
                removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                if (forModifier != null) {
                    objectProperty = NLResourceManager.forModifier;
                    objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(forModifier));
                    removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                    NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
                }
            }
        }
        UT.setInstanceLevelRepetitions(forProperty, forInstance, forModifier, value);
    }

    public void setSentencePlanAppropriateness(IRI sentencePlanIRI, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, null, sentencePlanIRI, null);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                objectProperty = NLResourceManager.forSentencePlan;
                objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(sentencePlanIRI));
                addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else {
                int oldValue = UT.getSentencePlanAppropriateness(sentencePlanIRI);

                OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS) {
                OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                objectProperty = NLResourceManager.forSentencePlan;
                objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(sentencePlanIRI));
                removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }
        }
        UT.setSentencePlanAppropriateness(sentencePlanIRI, value);
    }

    public void setNLNameAppropriateness(IRI NLNameIRI, IRI userType, int value) {
        UserModel UT = userModels.get(userType);

        //Ontology Side
        OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userType);
        for (OWLOntology mainModel : mainModels) {
            OWLAnonymousIndividual anIndiv = getUMNode(mainModel, userType, null, null, null, null, null, NLNameIRI);

            if (anIndiv == null) {
                anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                while (mainModel.getReferencedAnonymousIndividuals().contains(anIndiv)) {
                    anIndiv = NLResourcesManager.getDataFactory().getOWLAnonymousIndividual();
                }

                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

                objectProperty = NLResourceManager.forNLName;
                objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(NLNameIRI));
                addAxiomChange = new AddAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else {
                int oldValue = UT.getNLNameAppropriateness(NLNameIRI);

                OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, oldValue);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }

            if (value != UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS) {
                OWLDataProperty dataProperty = NLResourceManager.hasAppropriateness;
                OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, anIndiv, value);
                AddAxiom addAxiomChange = new AddAxiom(mainModel, dataAssertion);
                NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
            } else {
                OWLObjectProperty objectProperty = NLResourceManager.forUserType;
                OWLObjectPropertyAssertionAxiom objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, indivUM);
                RemoveAxiom removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);

                objectProperty = NLResourceManager.forNLName;
                objectAssertion = NLResourcesManager.getDataFactory().getOWLObjectPropertyAssertionAxiom(objectProperty, anIndiv, NLResourcesManager.getDataFactory().getOWLNamedIndividual(NLNameIRI));
                removeAxiomChange = new RemoveAxiom(mainModel, objectAssertion);
                NLResourcesManager.getOntologyManager().applyChange(removeAxiomChange);
            }
        }
        UT.setNLNameAppropriateness(NLNameIRI, value);
    }

    public void importUserModels(OWLOntology NLResourcesModel) {
        Set<OWLLiteral> literals;

        //User Models
        Set<OWLClassAssertionAxiom> userModelAssertations = NLResourcesModel.getClassAssertionAxioms(NLResourceManager.UserType);

        for (OWLClassAssertionAxiom userModelAssertation : userModelAssertations) {
            OWLIndividual userModelEntry = userModelAssertation.getIndividual();

            if (!userModels.containsKey(userModelEntry.asOWLNamedIndividual().getIRI())) {
                UserModel UM = new UserModel(userModelEntry.asOWLNamedIndividual().getIRI());

                literals = userModelEntry.getDataPropertyValues(NLResourceManager.maxMessagesPerSentence, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    UM.setMaxMessagesPerSentence(literal.parseInteger());
                }

                literals = userModelEntry.getDataPropertyValues(NLResourceManager.maxMessagesPerPage, NLResourcesModel);
                for (OWLLiteral literal : literals) {
                    UM.setMaxMessagesPerPage(literal.parseInteger());
                }

                UM.setPropertyLevelInterest(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
                UM.setPropertyLevelRepetitions(NLResourceManager.instanceOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
                UM.setPropertyLevelInterest(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
                UM.setPropertyLevelRepetitions(NLResourceManager.oneOf.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
                UM.setPropertyLevelInterest(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
                UM.setPropertyLevelRepetitions(NLResourceManager.differentIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
                UM.setPropertyLevelInterest(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
                UM.setPropertyLevelRepetitions(NLResourceManager.sameIndividuals.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
                UM.setPropertyLevelInterest(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_INTEREST);
                UM.setPropertyLevelRepetitions(NLResourceManager.isA.getIRI(), null, UserModel.USER_MODEL_DEFAULT_REPETITIONS);
                userModels.put(UM.getUMIRI(), UM);
            }
        }
    }

    public void exportUserModels(OWLOntology NLResourcesModel) {
        for (IRI userModelIRI : getUserModels()) {
            UserModel UM = userModels.get(userModelIRI);
            OWLNamedIndividual indivUM = NLResourcesManager.getDataFactory().getOWLNamedIndividual(userModelIRI);

            OWLClass cls = NLResourceManager.UserType;
            OWLClassAssertionAxiom classAssertion = NLResourcesManager.getDataFactory().getOWLClassAssertionAxiom(cls, indivUM);
            NLResourcesManager.getOntologyManager().addAxiom(NLResourcesModel, classAssertion);

            OWLDataProperty dataProperty = NLResourceManager.maxMessagesPerSentence;
            OWLDataPropertyAssertionAxiom dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, UM.getMaxMessagesPerSentence());
            AddAxiom addAxiomChange = new AddAxiom(NLResourcesModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            dataProperty = NLResourceManager.maxMessagesPerPage;
            dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, UM.getMaxMessagesPerPage());
            addAxiomChange = new AddAxiom(NLResourcesModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            dataProperty = NLResourceManager.hasInterest;
            dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, UM.getGlobalInterest());
            addAxiomChange = new AddAxiom(NLResourcesModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);

            dataProperty = NLResourceManager.maxRepetitions;
            dataAssertion = NLResourcesManager.getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, indivUM, UM.getGlobalRepetitions());
            addAxiomChange = new AddAxiom(NLResourcesModel, dataAssertion);
            NLResourcesManager.getOntologyManager().applyChange(addAxiomChange);
        }
    }

    public void importAnnotationEvents(OWLOntology mainModel) {
        mainModels.add(mainModel);

        //Interest/Repetitions
        Set<OWLIndividual> individuals;
        Set<OWLLiteral> literals;

        Set<OWLAnonymousIndividual> UMNodes = mainModel.getReferencedAnonymousIndividuals();

        for (OWLAnonymousIndividual node : UMNodes) {
            OWLIndividual forUserModel = null;
            IRI forProperty = null;
            IRI forClass = null;
            IRI forInstance = null;
            IRI forModifier = null;
            OWLLiteral hasInterest = null;
            OWLLiteral maxRepetitions = null;

            OWLIndividual forSentencePlan = null;
            OWLIndividual forNLName = null;
            OWLLiteral hasAppropriateness = null;

            individuals = node.getObjectPropertyValues(NLResourceManager.forUserType, mainModel);
            for (OWLIndividual individual : individuals) {
                forUserModel = individual;
            }

            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(node)) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.forProperty)) {
                    if ((annotation.getAnnotation().getValue() instanceof IRI)) {
                        forProperty = (IRI) annotation.getAnnotation().getValue();
                    } else if (annotation.getAnnotation().getValue() instanceof OWLLiteral) {
                        forProperty = IRI.create(((OWLLiteral) annotation.getAnnotation().getValue()).getLiteral());
                    }
                }
            }

            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(node)) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.forOwner)) {
                    if ((annotation.getAnnotation().getValue() instanceof IRI)) {
                        if (mainModel.containsClassInSignature((IRI) annotation.getAnnotation().getValue(), true)) {
                            forClass = (IRI) annotation.getAnnotation().getValue();
                        } else if (mainModel.containsIndividualInSignature((IRI) annotation.getAnnotation().getValue(), true)) {
                            forInstance = (IRI) annotation.getAnnotation().getValue();
                        }
                    }
                }
            }

            individuals = node.getObjectPropertyValues(NLResourceManager.forModifier, mainModel);
            for (OWLIndividual individual : individuals) {
                forModifier = individual.asOWLNamedIndividual().getIRI();
            }

            literals = node.getDataPropertyValues(NLResourceManager.hasInterest, mainModel);
            for (OWLLiteral literal : literals) {
                hasInterest = literal;
            }

            literals = node.getDataPropertyValues(NLResourceManager.maxRepetitions, mainModel);
            for (OWLLiteral literal : literals) {
                maxRepetitions = literal;
            }

            individuals = node.getObjectPropertyValues(NLResourceManager.forSentencePlan, mainModel);
            for (OWLIndividual individual : individuals) {
                forSentencePlan = individual;
            }

            individuals = node.getObjectPropertyValues(NLResourceManager.forNLName, mainModel);
            for (OWLIndividual individual : individuals) {
                forNLName = individual;
            }

            literals = node.getDataPropertyValues(NLResourceManager.hasAppropriateness, mainModel);
            for (OWLLiteral literal : literals) {
                hasAppropriateness = literal;
            }

            HashSet<UserModel> UMs = new HashSet<UserModel>();
            //If no user model is defined, the configurations are to the global UM
            if (forUserModel != null) {
                if (checkUserTypeExists(forUserModel.asOWLNamedIndividual().getIRI())) {
                    UMs.add(userModels.get(forUserModel.asOWLNamedIndividual().getIRI()));
                }
            } else {
                UMs.add(userModels.get(NLResourceManager.globalUserModel.getIRI()));
            }

            for (UserModel UM : UMs) {
                //Interest
                //Instance Level
                if ((forProperty != null) && (forInstance != null) && (hasInterest != null)) {
                    UM.setInstanceLevelInterest(forProperty, forInstance, forModifier, hasInterest.parseInteger());
                } //Class Level
                else if ((forProperty != null) && (forClass != null) && (hasInterest != null)) {
                    UM.setClassLevelInterest(forProperty, forClass, forModifier, hasInterest.parseInteger());
                } //Property Level
                else if ((forProperty != null) && (hasInterest != null)) {
                    UM.setPropertyLevelInterest(forProperty, forModifier, hasInterest.parseInteger());
                } else if ((forModifier != null) && (hasInterest != null)) {
                    UM.setPropertyLevelInterest(null, forModifier, hasInterest.parseInteger());
                } else if (hasInterest != null) {
                    UM.setGlobalInterest(hasInterest.parseInteger());
                }

                //Repetitions
                //Instance Level
                if ((forProperty != null) && (forInstance != null) && (maxRepetitions != null)) {
                    UM.setInstanceLevelRepetitions(forProperty, forInstance, forModifier, maxRepetitions.parseInteger());
                } //Class Level
                else if ((forProperty != null) && (forClass != null) && (maxRepetitions != null)) {
                    UM.setClassLevelRepetitions(forProperty, forClass, forModifier, maxRepetitions.parseInteger());
                } //Property Level
                else if ((forProperty != null) && (maxRepetitions != null)) {
                    UM.setPropertyLevelRepetitions(forProperty, forModifier, maxRepetitions.parseInteger());
                } else if ((forModifier != null) && (maxRepetitions != null)) {
                    UM.setPropertyLevelRepetitions(null, forModifier, maxRepetitions.parseInteger());
                } else if (maxRepetitions != null) {
                    UM.setGlobalRepetitions(maxRepetitions.parseInteger());
                }

                //SentencePlan/NLName Appropriateness
                if ((forSentencePlan != null) && (hasAppropriateness != null)) {
                    UM.setSentencePlanAppropriateness(forSentencePlan.asOWLNamedIndividual().getIRI(), hasAppropriateness.parseInteger());
                } else if ((forNLName != null) && (hasAppropriateness != null)) {
                    UM.setNLNameAppropriateness(forNLName.asOWLNamedIndividual().getIRI(), hasAppropriateness.parseInteger());
                }
            }
        }
    }//loadUserModels

    public OWLAnonymousIndividual getUMNode(OWLOntology mainModel, IRI userModel, IRI property, IRI cls, IRI instance, IRI modifier, IRI sentencePlan, IRI NLName) {
        OWLAnonymousIndividual UMNode = null;
        Set<OWLAnonymousIndividual> UMNodes = mainModel.getReferencedAnonymousIndividuals();

        for (OWLAnonymousIndividual node : UMNodes) {
            if (!userModel.equals(NLResourceManager.globalUserModel.getIRI())) {
                UMNode = checkUMNode(mainModel, node, userModel, property, cls, instance, modifier, sentencePlan, NLName);
            } else {
                UMNode = checkUMNode(mainModel, node, null, property, cls, instance, modifier, sentencePlan, NLName);
            }
            if (UMNode != null) {
                return UMNode;
            }
        }

        return null;
    }

    public OWLAnonymousIndividual checkUMNode(OWLOntology mainModel, OWLAnonymousIndividual node, IRI userModel, IRI property, IRI cls, IRI instance, IRI modifier, IRI sentencePlan, IRI NLName) {
        Set<OWLIndividual> individuals;

        IRI forUserModel = null;
        IRI forProperty = null;
        IRI forClass = null;
        IRI forInstance = null;
        IRI forModifier = null;
        IRI forSentencePlan = null;
        IRI forNLName = null;

        individuals = node.getObjectPropertyValues(NLResourceManager.forUserType, mainModel);
        for (OWLIndividual individual : individuals) {
            forUserModel = individual.asOWLNamedIndividual().getIRI();
        }

        for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(node)) {
            if (annotation.getAnnotation().getProperty().equals(NLResourceManager.forProperty)) {
                if ((annotation.getAnnotation().getValue() instanceof IRI)) {
                    forProperty = (IRI) annotation.getAnnotation().getValue();
                } else if (annotation.getAnnotation().getValue() instanceof OWLLiteral) {
                    forProperty = IRI.create(((OWLLiteral) annotation.getAnnotation().getValue()).getLiteral());
                }
            }
        }

        for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(node)) {
            if (annotation.getAnnotation().getProperty().equals(NLResourceManager.forOwner)) {
                if ((annotation.getAnnotation().getValue() instanceof IRI)) {
                    if (mainModel.containsClassInSignature((IRI) annotation.getAnnotation().getValue(), true)) {
                        forClass = (IRI) annotation.getAnnotation().getValue();
                    } else if (mainModel.containsIndividualInSignature((IRI) annotation.getAnnotation().getValue(), true)) {
                        forInstance = (IRI) annotation.getAnnotation().getValue();
                    }
                }
            }
        }

        individuals = node.getObjectPropertyValues(NLResourceManager.forModifier, mainModel);
        for (OWLIndividual individual : individuals) {
            forModifier = individual.asOWLNamedIndividual().getIRI();
        }

        individuals = node.getObjectPropertyValues(NLResourceManager.forSentencePlan, mainModel);
        for (OWLIndividual individual : individuals) {
            forSentencePlan = individual.asOWLNamedIndividual().getIRI();
        }

        individuals = node.getObjectPropertyValues(NLResourceManager.forNLName, mainModel);
        for (OWLIndividual individual : individuals) {
            forNLName = individual.asOWLNamedIndividual().getIRI();
        }

        if (forUserModel != null) {
            if (userModel != null) {
                if (!forUserModel.equals(userModel)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (userModel != null) {
            return null;
        }
        if (forProperty != null) {
            if (property != null) {
                if (!forProperty.equals(property)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (property != null) {
            return null;
        }
        if (forClass != null) {
            if (cls != null) {
                if (!forClass.equals(cls)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (cls != null) {
            return null;
        }
        if (forInstance != null) {
            if (instance != null) {
                if (!forInstance.equals(instance)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (instance != null) {
            return null;
        }
        if (forModifier != null) {
            if (modifier != null) {
                if (!forModifier.equals(modifier)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (modifier != null) {
            return null;
        }
        if (forSentencePlan != null) {
            if (sentencePlan != null) {
                if (!forSentencePlan.equals(sentencePlan)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (sentencePlan != null) {
            return null;
        }
        if (forNLName != null) {
            if (NLName != null) {
                if (!forNLName.equals(NLName)) {
                    return null;
                }
            } else {
                return null;
            }
        } else if (NLName != null) {
            return null;
        }

        return node;
    }

    public int getMaxMessagesPerPage(IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getMaxMessagesPerPage();
    }

    public int getMaxMessagesPerSentence(IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getMaxMessagesPerSentence();
    }

    public int getGlobalInterest(IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getGlobalInterest();
    }

    public int getGlobalRepetitions(IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getGlobalRepetitions();
    }

    public int getPropertyLevelInterest(IRI forProperty, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getPropertyLevelInterest(forProperty, forModifier);
    }

    public int getPropertyLevelRepetitions(IRI forProperty, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getPropertyLevelRepetitions(forProperty, forModifier);
    }

    public int getClassLevelInterest(IRI forProperty, IRI forClass, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getClassLevelInterest(forProperty, forClass, forModifier);
    }

    public int getClassLevelRepetitions(IRI forProperty, IRI forClass, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getClassLevelRepetitions(forProperty, forClass, forModifier);
    }

    public int getInstanceLevelInterest(IRI forProperty, IRI forInstance, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getInstanceLevelInterest(forProperty, forInstance, forModifier);
    }

    public int getInstanceLevelRepetitions(IRI forProperty, IRI forInstance, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);

        return UT.getInstanceLevelRepetitions(forProperty, forInstance, forModifier);
    }

    public int getInterest(IRI forProperty, IRI forOwner, IRI forModifier, IRI userType) {
        UserModel UM = userModels.get(userType);
        UserModel global = userModels.get(NLResourceManager.globalUserModel.getIRI());

        HashSet<Integer> modelInterests = new HashSet<Integer>();
        int interest = -1;
        for (OWLOntology mainModel : mainModels) {
            for (OWLEntity entity : mainModel.getEntitiesInSignature(forOwner, false)) {
                if (entity.isOWLNamedIndividual()) {
                    interest = UM.getInstanceLevelInterest(forProperty, forOwner, forModifier);

                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = global.getInstanceLevelInterest(forProperty, forOwner, forModifier);
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        for (OWLClassAssertionAxiom superClassAxiom : mainModel.getClassAssertionAxioms(entity.asOWLNamedIndividual())) {
                            if (!superClassAxiom.getClassExpression().isAnonymous()) {
                                if (interest == UserModel.USER_MODEL_UNDEFINED) {
                                    interest = getInterest(forProperty, superClassAxiom.getClassExpression().asOWLClass().getIRI(), forModifier, userType);
                                }
                            }
                        }
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = UM.getPropertyLevelInterest(forProperty, forModifier);
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = global.getPropertyLevelInterest(forProperty, forModifier);
                    }
                    if ((interest == UserModel.USER_MODEL_UNDEFINED) && (forModifier != null)) {
                        interest = UM.getPropertyLevelInterest(null, forModifier);
                    }
                    if ((interest == UserModel.USER_MODEL_UNDEFINED) && (forModifier != null)) {
                        interest = global.getPropertyLevelInterest(null, forModifier);
                    }
                } else if (entity.isOWLClass()) {
                    interest = UM.getClassLevelInterest(forProperty, forOwner, forModifier);

                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = global.getClassLevelInterest(forProperty, forOwner, forModifier);
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        for (OWLClassExpression superClassAxiom : entity.asOWLClass().getSuperClasses(mainModel)) {
                            if (!superClassAxiom.isAnonymous()) {
                                if (interest == UserModel.USER_MODEL_UNDEFINED) {
                                    interest = getInterest(forProperty, superClassAxiom.asOWLClass().getIRI(), forModifier, userType);
                                }
                            }
                        }
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = UM.getPropertyLevelInterest(forProperty, forModifier);
                    }
                    if (interest == UserModel.USER_MODEL_UNDEFINED) {
                        interest = global.getPropertyLevelInterest(forProperty, forModifier);
                    }
                    if ((interest == UserModel.USER_MODEL_UNDEFINED) && (forModifier != null)) {
                        interest = UM.getPropertyLevelInterest(null, forModifier);
                    }
                    if ((interest == UserModel.USER_MODEL_UNDEFINED) && (forModifier != null)) {
                        interest = global.getPropertyLevelInterest(null, forModifier);
                    }
                }
            }
            modelInterests.add(interest);
        }

        interest = UserModel.USER_MODEL_UNDEFINED;
        for (Integer modelInterest : modelInterests) {
            if (modelInterest.intValue() > interest) {
                interest = modelInterest.intValue();
            }
        }

        if (interest == UserModel.USER_MODEL_UNDEFINED) {
            interest = UM.getGlobalInterest();
        }
        if (interest == UserModel.USER_MODEL_UNDEFINED) {
            interest = global.getGlobalInterest();
        }

        return interest;
    }

    public int getRepetitions(IRI forProperty, IRI forIRI, IRI forModifier, IRI userType) {
        UserModel UT = userModels.get(userType);
        UserModel global = userModels.get(NLResourceManager.globalUserModel.getIRI());

        HashSet<Integer> modelRepetitions = new HashSet<Integer>();
        int repetitions = -1;
        for (OWLOntology mainModel : mainModels) {
            for (OWLEntity entity : mainModel.getEntitiesInSignature(forIRI, false)) {
                if (entity instanceof OWLNamedIndividual) {
                    repetitions = UT.getInstanceLevelRepetitions(forProperty, forIRI, forModifier);
                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = global.getPropertyLevelRepetitions(forProperty, forModifier);
                    }

                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        for (OWLClassAssertionAxiom superClassAxiom : mainModel.getClassAssertionAxioms((OWLNamedIndividual) entity)) {
                            if (!superClassAxiom.getClassExpression().isAnonymous()) {
                                if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                                    repetitions = UT.getClassLevelRepetitions(forProperty, superClassAxiom.getClassExpression().asOWLClass().getIRI(), forModifier);
                                }
                            }
                        }
                    }
                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = UT.getPropertyLevelRepetitions(forProperty, forModifier);
                    }
                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = global.getPropertyLevelRepetitions(forProperty, forModifier);
                    }
                } else if (entity instanceof OWLClass) {
                    repetitions = UT.getClassLevelRepetitions(forProperty, forIRI, forModifier);
                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = global.getClassLevelRepetitions(forProperty, forIRI, forModifier);
                    }

                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = UT.getPropertyLevelRepetitions(forProperty, forModifier);
                    }
                    if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
                        repetitions = global.getPropertyLevelRepetitions(forProperty, forModifier);
                    }
                }
            }
            modelRepetitions.add(repetitions);
        }

        repetitions = UserModel.USER_MODEL_UNDEFINED;
        for (Integer modelInterest : modelRepetitions) {
            if (modelInterest.intValue() > repetitions) {
                repetitions = modelInterest.intValue();
            }
        }

        if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
            repetitions = UT.getGlobalRepetitions();
        }
        if (repetitions == UserModel.USER_MODEL_UNDEFINED) {
            repetitions = global.getGlobalRepetitions();
        }

        return repetitions;
    }

    public UserModel getGlobalUserModel() {
        return userModels.get(NLResourceManager.globalUserModel.getIRI());
    }

    public int getSentencePlanAppropriateness(IRI sentencePlanIRI, IRI userType) {
        UserModel UT = userModels.get(userType);
        return UT.getSentencePlanAppropriateness(sentencePlanIRI);
    }

    public int getNLNameAppropriateness(IRI NLNameIRI, IRI userType) {
        UserModel UT = userModels.get(userType);
        return UT.getNLNameAppropriateness(NLNameIRI);
    }

    public UserModel getUserModel(IRI userType) {
        return this.userModels.get(userType);
    }

    //  get a list of all user types
    public Set<IRI> getUserModels() {
        return this.userModels.keySet();
    }

    // check if the specified user type exists
    public boolean checkUserTypeExists(IRI UserType) {
        return this.userModels.containsKey(UserType);
    }
}//class UserModellingQueryManager