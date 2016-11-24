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

import gr.aueb.cs.nlg.Languages.Languages;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class OrderingQueryManager {

    public static final int defaultOrder = 100;
    //Ordering
    private HashMap<IRI, Integer> propertyOrders;
    private HashMap<IRI, IRI> propertySections;
    private HashMap<IRI, Integer> sectionOrders;
    private HashMap<IRI, String> sectionLabelsEN;
    private HashMap<IRI, String> sectionLabelsGR;
    public HashSet<OWLOntology> mainModels;
    NLResourceManager NLResourceManager;

    public OrderingQueryManager(NLResourceManager NLResourceManager) {
        mainModels = new HashSet<OWLOntology>();
        this.NLResourceManager = NLResourceManager;

        init();
    }

    // initialize all hashtables
    private void init() {
        propertyOrders = new HashMap<IRI, Integer>();
        propertySections = new HashMap<IRI, IRI>();
        sectionOrders = new HashMap<IRI, Integer>();
        sectionLabelsEN = new HashMap<IRI, String>();
        sectionLabelsGR = new HashMap<IRI, String>();

        sectionOrders.put(NLResourceManager.defaultSection.getIRI(), defaultOrder);

        for (OWLNamedIndividual domainIndependentProperty : NLResourceManager.domainIndependentProperties) {
            propertySections.put(domainIndependentProperty.getIRI(), NLResourceManager.defaultSection.getIRI());
            propertyOrders.put(domainIndependentProperty.getIRI(), 0);
        }
    }

    public void setPropertySection(IRI propertyIRI, IRI sectionIRI) {
        propertySections.put(propertyIRI, sectionIRI);

        if (!DefaultResourcesManager.isDefaultResource(propertyIRI)) {
            for (OWLOntology model : getContainingOntologies(propertyIRI)) {
                //Delete old section assertion
                OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, propertyIRI, sectionIRI);
                OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(axiomChange);

                //Add new section assertion 
                annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, propertyIRI, sectionIRI);
                axiomChange = new AddAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public void setPropertyOrder(IRI propertyIRI, int order) {
        int oldOrder = propertyOrders.get(propertyIRI);
        propertyOrders.put(propertyIRI, order);

        if (!DefaultResourcesManager.isDefaultResource(propertyIRI)) {
            for (OWLOntology model : getContainingOntologies(propertyIRI)) {
                //Delete old section assertion
                OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, propertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(oldOrder));
                OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(axiomChange);

                //Add new section assertion 
                annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, propertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(order));
                axiomChange = new AddAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public void setSectionOrder(IRI sectionIRI, int order) {
        sectionOrders.put(sectionIRI, order);
    }

    public void addProperty(IRI propertyIRI) {
        propertySections.put(propertyIRI, NLResourceManager.defaultSection.getIRI());
        propertyOrders.put(propertyIRI, defaultOrder);

        for (OWLOntology model : getContainingOntologies(propertyIRI)) {
            if (!propertyOrders.containsKey(propertyIRI)) {
                OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, propertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(defaultOrder));
                OWLAxiomChange addAxiomChange = new AddAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(addAxiomChange);
            }
            if (!propertySections.containsKey(propertyIRI)) {
                OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, propertyIRI, NLResourceManager.defaultSection.getIRI());
                OWLAxiomChange addAxiomChange = new AddAxiom(model, annotationAssertion);
                model.getOWLOntologyManager().applyChange(addAxiomChange);
            }
        }
    }

    public void addSection(IRI sectionIRI) {
        sectionOrders.put(sectionIRI, defaultOrder);
    }

    public void renameProperty(IRI oldPropertyIRI, IRI newPropertyIRI) {
        int order = propertyOrders.remove(oldPropertyIRI);
        IRI sectionIRI = propertySections.remove(oldPropertyIRI);

        propertyOrders.put(newPropertyIRI, order);
        propertySections.put(newPropertyIRI, sectionIRI);
        for (OWLOntology model : getContainingOntologies(oldPropertyIRI)) {
            //Ontology Side
            OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, oldPropertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(order));
            OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);

            annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, newPropertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(order));
            axiomChange = new AddAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);


            annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, oldPropertyIRI, sectionIRI);
            axiomChange = new RemoveAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);

            annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, newPropertyIRI, sectionIRI);
            axiomChange = new AddAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);
        }
    }

    public void renameSection(IRI oldSectionIRI, IRI newSectionIRI) {
        int order = sectionOrders.remove(oldSectionIRI);
        sectionOrders.put(newSectionIRI, order);

        if (sectionLabelsEN.containsKey(oldSectionIRI)) {
            String label = sectionLabelsEN.remove(oldSectionIRI);
            sectionLabelsEN.put(newSectionIRI, label);
        }
        if (sectionLabelsGR.containsKey(oldSectionIRI)) {
            String label = sectionLabelsGR.remove(oldSectionIRI);
            sectionLabelsGR.put(newSectionIRI, label);
        }

        for (OWLOntology model : mainModels) {
            //Ontology Side
            for (IRI property : propertySections.keySet()) {
                if (propertySections.get(property).equals(oldSectionIRI)) {
                    propertySections.put(property, newSectionIRI);

                    if (!DefaultResourcesManager.isDefaultResource(property)) {
                        //Ontology Side
                        OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, property, oldSectionIRI);
                        OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
                        model.getOWLOntologyManager().applyChange(axiomChange);

                        annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, property, newSectionIRI);
                        axiomChange = new AddAxiom(model, annotationAssertion);
                        model.getOWLOntologyManager().applyChange(axiomChange);
                    }
                }
            }
        }
    }

    public void deleteProperty(IRI propertyIRI) {
        int order = propertyOrders.remove(propertyIRI);
        IRI sectionIRI = propertySections.remove(propertyIRI);
        for (OWLOntology model : getContainingOntologies(propertyIRI)) {
            //Ontology Side
            OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, propertyIRI, model.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(order));
            OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);

            annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, propertyIRI, sectionIRI);
            axiomChange = new RemoveAxiom(model, annotationAssertion);
            model.getOWLOntologyManager().applyChange(axiomChange);
        }
    }

    public void deleteSection(IRI sectionIRI) {
        sectionOrders.remove(sectionIRI);
        if (sectionLabelsEN.containsKey(sectionIRI)) {
            sectionLabelsEN.remove(sectionIRI);
        }
        if (sectionLabelsGR.containsKey(sectionIRI)) {
            sectionLabelsGR.remove(sectionIRI);
        }

        for (OWLOntology model : mainModels) {
            //Ontology Side
            for (IRI property : propertySections.keySet()) {
                if (propertySections.get(property).equals(sectionIRI)) {
                    propertySections.put(property, NLResourceManager.defaultSection.getIRI());

                    if (!DefaultResourcesManager.isDefaultResource(property)) {
                        //Delete old section assertion
                        OWLAnnotationAssertionAxiom annotationAssertion = model.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, property, sectionIRI);
                        OWLAxiomChange axiomChange = new RemoveAxiom(model, annotationAssertion);
                        model.getOWLOntologyManager().applyChange(axiomChange);
                    }
                }
            }
        }
    }

    public void importSections(OWLOntology NLResourcesModel) {
        HashSet<IRI> sectionIRIs = new HashSet<IRI>();
        for (OWLClassAssertionAxiom sectionAssertion : NLResourcesModel.getClassAssertionAxioms(NLResourceManager.Section)) {
            OWLIndividual sectionEntry = sectionAssertion.getIndividual();
            if (sectionEntry.isNamed()) {
                sectionIRIs.add(sectionEntry.asOWLNamedIndividual().getIRI());
            }
        }

        for (IRI sectionIRI : sectionIRIs) {
            for (OWLAnnotationAssertionAxiom annotation : NLResourcesModel.getAnnotationAssertionAxioms(sectionIRI)) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasOrderAnn)) {
                    if (annotation.getAnnotation().getValue() instanceof OWLLiteral) {
                        int order = ((OWLLiteral) annotation.getAnnotation().getValue()).parseInteger();

                        sectionOrders.put(sectionIRI, order);
                    }
                }
                if (annotation.getProperty().isLabel()) {
                    if (annotation.getValue() instanceof OWLLiteral) {
                        if (((OWLLiteral) annotation.getValue()).hasLang("gr") || ((OWLLiteral) annotation.getValue()).hasLang("el")) {
                            sectionLabelsGR.put(sectionIRI, ((OWLLiteral) annotation.getValue()).getLiteral());
                        }
                        if (((OWLLiteral) annotation.getValue()).hasLang("en")) {
                            sectionLabelsEN.put(sectionIRI, ((OWLLiteral) annotation.getValue()).getLiteral());
                        } else if (!((OWLLiteral) annotation.getValue()).hasLang()) {
                            sectionLabelsEN.put(sectionIRI, ((OWLLiteral) annotation.getValue()).getLiteral());
                        }
                    }
                }
            }

            if (!sectionOrders.containsKey(sectionIRI)) {
                addSection(sectionIRI);
            }
        }
    }

    // load mapping info from ontology
    public void importOrdering(OWLOntology model) {
        mainModels.add(model);

        //Property orders and sections
        for (OWLDataProperty dataProperty : model.getDataPropertiesInSignature(true)) {
            if (!dataProperty.getIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
                for (OWLAnnotationAssertionAxiom annotation : model.getAnnotationAssertionAxioms(dataProperty.getIRI())) {
                    if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasOrderAnn)) {
                        if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            int order = ((OWLLiteral) annotation.getAnnotation().getValue()).parseInteger();

                            propertyOrders.put(propertyIRI, order);
                        }
                    } else if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasSection)) {
                        if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            IRI sectionIRI = (IRI) annotation.getAnnotation().getValue();

                            propertySections.put(propertyIRI, sectionIRI);
                        }
                    }
                }

                if ((!propertyOrders.containsKey(dataProperty.getIRI())) || (!propertySections.containsKey(dataProperty.getIRI()))) {
                    addProperty(dataProperty.getIRI());
                }
            }
        }
        for (OWLObjectProperty objectProperty : model.getObjectPropertiesInSignature(true)) {
            if (!objectProperty.getIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
                for (OWLAnnotationAssertionAxiom annotation : model.getAnnotationAssertionAxioms(objectProperty.getIRI())) {
                    if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasOrderAnn)) {
                        if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            int order = ((OWLLiteral) annotation.getAnnotation().getValue()).parseInteger();

                            propertyOrders.put(propertyIRI, order);
                        }
                    } else if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasSection)) {
                        if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            IRI sectionIRI = (IRI) annotation.getAnnotation().getValue();

                            propertySections.put(propertyIRI, sectionIRI);
                        }
                    }
                }

                if ((!propertyOrders.containsKey(objectProperty.getIRI())) || (!propertySections.containsKey(objectProperty.getIRI()))) {
                    addProperty(objectProperty.getIRI());
                }
            }
        }
        for (OWLNamedIndividual domainIndependentProperty : NLResourceManager.domainIndependentProperties) {
            for (OWLAnnotationAssertionAxiom annotation : model.getAnnotationAssertionAxioms(domainIndependentProperty.getIRI())) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasOrderAnn)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                        IRI propertyIRI = (IRI) annotation.getSubject();
                        int order = ((OWLLiteral) annotation.getAnnotation().getValue()).parseInteger();

                        propertyOrders.put(propertyIRI, order);
                    }
                } else if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasSection)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                        IRI propertyIRI = (IRI) annotation.getSubject();
                        IRI sectionIRI = (IRI) annotation.getAnnotation().getValue();

                        propertySections.put(propertyIRI, sectionIRI);
                    }
                }
            }

            if ((!propertyOrders.containsKey(domainIndependentProperty.getIRI())) || (!propertySections.containsKey(domainIndependentProperty.getIRI()))) {
                addProperty(domainIndependentProperty.getIRI());
            }
        }

        for (IRI sectionIRI : propertySections.values()) {
            if (!sectionOrders.containsKey(sectionIRI)) {
                for (OWLAnnotationAssertionAxiom annotation : model.getAnnotationAssertionAxioms(sectionIRI)) {
                    if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasOrderAnn)) {
                        if (annotation.getAnnotation().getValue() instanceof OWLLiteral) {
                            int order = ((OWLLiteral) annotation.getAnnotation().getValue()).parseInteger();

                            sectionOrders.put(sectionIRI, order);
                        }
                    }
                }

                if (!sectionOrders.containsKey(sectionIRI)) {
                    addSection(sectionIRI);
                }
            }
        }
    }//loadOrdering

    public void exportSections(OWLOntology resourceOntology) {
        OWLDataFactory factory = resourceOntology.getOWLOntologyManager().getOWLDataFactory();

        OWLClass cls = NLResourceManager.Section;
        for (IRI sectionIRI : sectionOrders.keySet()) {
            if (!sectionIRI.equals(NLResourceManager.defaultSection.getIRI())) {
                OWLNamedIndividual indiv = factory.getOWLNamedIndividual(sectionIRI);
                OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
                resourceOntology.getOWLOntologyManager().addAxiom(resourceOntology, classAssertion);

                //Add new section assertion 
                OWLAnnotationAssertionAxiom annotationAssertion = factory.getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, sectionIRI, factory.getOWLLiteral(getSectionOrder(sectionIRI)));
                OWLAxiomChange axiomChange = new AddAxiom(resourceOntology, annotationAssertion);
                resourceOntology.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
        for (IRI sectionIRI : sectionLabelsEN.keySet()) {
            if (!sectionIRI.equals(NLResourceManager.defaultSection.getIRI())) {
                OWLNamedIndividual indiv = factory.getOWLNamedIndividual(sectionIRI);
                OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
                resourceOntology.getOWLOntologyManager().addAxiom(resourceOntology, classAssertion);

                //Add new section assertion 
                OWLAnnotationAssertionAxiom annotationAssertion = factory.getOWLAnnotationAssertionAxiom(factory.getRDFSLabel(), sectionIRI, factory.getOWLLiteral(sectionLabelsEN.get(sectionIRI), "en"));
                OWLAxiomChange axiomChange = new AddAxiom(resourceOntology, annotationAssertion);
                resourceOntology.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
        for (IRI sectionIRI : sectionLabelsGR.keySet()) {
            if (!sectionIRI.equals(NLResourceManager.defaultSection.getIRI())) {
                OWLNamedIndividual indiv = factory.getOWLNamedIndividual(sectionIRI);
                OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(cls, indiv);
                resourceOntology.getOWLOntologyManager().addAxiom(resourceOntology, classAssertion);

                //Add new section assertion 
                OWLAnnotationAssertionAxiom annotationAssertion = factory.getOWLAnnotationAssertionAxiom(factory.getRDFSLabel(), sectionIRI, factory.getOWLLiteral(sectionLabelsGR.get(sectionIRI), "gr"));
                OWLAxiomChange axiomChange = new AddAxiom(resourceOntology, annotationAssertion);
                resourceOntology.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public void exportOrders(OWLOntology resourceOntology) {
        OWLDataFactory factory = resourceOntology.getOWLOntologyManager().getOWLDataFactory();

        for (OWLNamedIndividual domainIndependentProperty : NLResourceManager.domainIndependentProperties) {
            if (!getPropertySection(domainIndependentProperty.getIRI()).equals(NLResourceManager.defaultSection.getIRI())) {
                OWLAnnotationAssertionAxiom annotationAssertion = factory.getOWLAnnotationAssertionAxiom(NLResourceManager.hasSection, domainIndependentProperty.getIRI(), getPropertySection(domainIndependentProperty.getIRI()));
                OWLAxiomChange axiomChange = new AddAxiom(resourceOntology, annotationAssertion);
                resourceOntology.getOWLOntologyManager().applyChange(axiomChange);

                annotationAssertion = factory.getOWLAnnotationAssertionAxiom(NLResourceManager.hasOrderAnn, domainIndependentProperty.getIRI(), factory.getOWLLiteral(getPropertyOrder(domainIndependentProperty.getIRI())));
                axiomChange = new AddAxiom(resourceOntology, annotationAssertion);
                resourceOntology.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public ArrayList<IRI> getOrderedSections() {
        HashSet<IRI> sectionIRIs = new HashSet<IRI>();
        for (IRI sectionIRI : sectionOrders.keySet()) {
            sectionIRIs.add(sectionIRI);
        }
        sectionIRIs.remove(NLResourceManager.defaultSection.getIRI());

        ArrayList<IRI> orderedSectionIRIs = new ArrayList<IRI>();
        IRI minIRI;
        int minOrder;
        while (!sectionIRIs.isEmpty()) {
            minIRI = null;
            minOrder = Integer.MAX_VALUE;
            for (IRI sectionIRI : sectionIRIs) {
                if (sectionOrders.get(sectionIRI) <= minOrder) {
                    minIRI = sectionIRI;
                    minOrder = sectionOrders.get(sectionIRI);
                }
            }

            orderedSectionIRIs.add(minIRI);
            sectionIRIs.remove(minIRI);
        }
        return orderedSectionIRIs;
    }

    public ArrayList<IRI> getPropertiesInSection(IRI sectionIRI) {
        HashSet<IRI> propertyIRIs = new HashSet<IRI>();
        for (IRI propertyIRI : propertySections.keySet()) {
            if (propertySections.get(propertyIRI).equals(sectionIRI)) {
                propertyIRIs.add(propertyIRI);
            }
        }

        ArrayList<IRI> orderedPropertyIRIs = new ArrayList<IRI>();

        IRI minIRI;
        int minOrder;
        while (!propertyIRIs.isEmpty()) {
            minIRI = null;
            minOrder = Integer.MAX_VALUE;
            for (IRI propertyIRI : propertyIRIs) {
                if (propertyOrders.get(propertyIRI) <= minOrder) {
                    minIRI = propertyIRI;
                    minOrder = propertyOrders.get(propertyIRI);
                }
            }

            orderedPropertyIRIs.add(minIRI);
            propertyIRIs.remove(minIRI);
        }
        return orderedPropertyIRIs;
    }

    public int getPropertyOrder(IRI propertyIRI) {
        if (propertyOrders.containsKey(propertyIRI)) {
            return propertyOrders.get(propertyIRI);
        }
        return defaultOrder;
    }

    public IRI getPropertySection(IRI propertyIRI) {
        if (propertySections.containsKey(propertyIRI)) {
            return propertySections.get(propertyIRI);
        }
        return NLResourceManager.defaultSection.getIRI();
    }

    public int getSectionOrder(IRI section) {
        if (sectionOrders.containsKey(section)) {
            return sectionOrders.get(section);
        }
        return defaultOrder;
    }

    public int getPropertySectionOrder(IRI propertyIRI) {
        if (propertySections.containsKey(propertyIRI)) {
            return sectionOrders.get(propertySections.get(propertyIRI));
        }
        return defaultOrder;
    }

    public String getSectionLabel(IRI sectionIRI, String language) {
        if (Languages.isEnglish(language)) {
            if (sectionLabelsEN.containsKey(sectionIRI)) {
                return sectionLabelsEN.get(sectionIRI);
            }
        } else if (Languages.isGreek(language)) {
            if (sectionLabelsGR.containsKey(sectionIRI)) {
                return sectionLabelsGR.get(sectionIRI);
            }
        }
        return "";
    }

    public void setSectionLabel(IRI sectionIRI, String label, String language) {
        if (label.isEmpty()) {
            if (Languages.isEnglish(language)) {
                sectionLabelsEN.remove(sectionIRI);
            } else if (Languages.isGreek(language)) {
                sectionLabelsGR.remove(sectionIRI);
            }
        } else {
            if (Languages.isEnglish(language)) {
                sectionLabelsEN.put(sectionIRI, label);
            } else if (Languages.isGreek(language)) {
                sectionLabelsGR.put(sectionIRI, label);
            }
        }
    }

    public HashSet<OWLOntology> getContainingOntologies(IRI entryIRI) {
        HashSet<OWLOntology> containingOntologies = new HashSet<OWLOntology>();
        for (OWLOntology model : mainModels) {
            if (model.containsObjectPropertyInSignature(entryIRI, true)) {
                containingOntologies.add(model);
            } else if (model.containsDataPropertyInSignature(entryIRI, true)) {
                containingOntologies.add(model);
            } else if (model.containsClassInSignature(entryIRI, true)) {
                containingOntologies.add(model);
            } else if (model.containsEntityInSignature(entryIRI, true)) {
                containingOntologies.add(model);
            } else if (model.containsIndividualInSignature(entryIRI, true)) {
                containingOntologies.add(model);
            }
        }
        return containingOntologies;
    }
}