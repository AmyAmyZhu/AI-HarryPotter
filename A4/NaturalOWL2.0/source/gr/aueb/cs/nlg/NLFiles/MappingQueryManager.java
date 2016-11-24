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

import gr.aueb.cs.nlg.Comparisons.Comparison;
import gr.aueb.cs.nlg.Comparisons.ComparisonTypes;
import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.NLGEngine.SurfaceRealization;
import gr.aueb.cs.nlg.Utils.NLGUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

import org.semanticweb.owlapi.model.OWLLiteral;

public class MappingQueryManager {

    private boolean useNLNames = true;
    private boolean useSentencePlans = true;
    //Mappings
    private HashMap<IRI, HashSet<IRI>> sentencePlanMappings;
    private HashMap<IRI, HashSet<IRI>> NLNameMappings;
    private HashMap<IRI, Boolean> propertyComparisonsAllowed;
    //main ontologies
    private HashSet<OWLOntology> mainModels;
    NLResourceManager NLResourcesManager;

    public MappingQueryManager(NLResourceManager NLResourcesManager) {
        mainModels = new HashSet<OWLOntology>();
        this.NLResourcesManager = NLResourcesManager;

        init();
    }

    // initialize all hashtables
    public final void init() {
        sentencePlanMappings = new HashMap<IRI, HashSet<IRI>>();
        NLNameMappings = new HashMap<IRI, HashSet<IRI>>();
        propertyComparisonsAllowed = new HashMap<IRI, Boolean>();
    }

    //chooces  a sentence plan randomly
    public IRI chooseSentencePlan(IRI propertyIRI, SentencePlanQueryManager SPQM, String language) {
        if (useSentencePlans) {        	
            HashSet<IRI> sentencePlans = new HashSet<IRI>();
            HashSet<IRI> candSentencePlans = sentencePlanMappings.get(propertyIRI);
            if (candSentencePlans != null) {
    	        for (IRI plan : candSentencePlans) {
    	        	if (SPQM.getSentencePlan(plan) != null) {
    	        		sentencePlans.add(plan);
    	        	}
    	        }
            }
            ArrayList<IRI> availableSentencePlans = new ArrayList<IRI>();

            if (!sentencePlans.isEmpty()) {
                for (IRI sentencePlanIRI : sentencePlans) {
                    if (SPQM.getSentencePlan(sentencePlanIRI, language) != null) {
                        availableSentencePlans.add(sentencePlanIRI);
                    }
                }

                if (!availableSentencePlans.isEmpty()) {
                    int index = (int) Math.floor(Math.random() * availableSentencePlans.size());
                    return availableSentencePlans.get(index);
                }
            }
        }
        for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
            SentencePlan defaultPlan = DefaultResourcesManager.generateDefaultSentencePlanForProperty(propertyIRI, mainModel, language);
            SPQM.addSentencePlan(defaultPlan, language);

            return defaultPlan.getSentencePlanIRI();
        }
        return null;
    }//chooseSentencePlan

    //chooces a sentence plan randomly
    public IRI chooseSentencePlan(IRI propertyIRI, SentencePlanQueryManager SPQM, String language, NLGUser user) {
        if (user != null) {
            if (useSentencePlans) {
                HashSet<IRI> sentencePlans = new HashSet<IRI>();
                HashSet<IRI> candSentencePlans = sentencePlanMappings.get(propertyIRI);
                if (candSentencePlans != null) {
        	        for (IRI plan : candSentencePlans) {
        	        	if (SPQM.getSentencePlan(plan) != null) {
        	        		sentencePlans.add(plan);
        	        	}
        	        }
                }
                HashMap<IRI, Float> sentencePlanScores = new HashMap<IRI, Float>();

                if (!sentencePlans.isEmpty()) {
                    for (IRI sentencePlanIRI : sentencePlans) {
                        if (SPQM.getSentencePlan(sentencePlanIRI, language) != null) {
                            float score = ((1.0F + user.getUserModel().getSentencePlanAppropriateness(sentencePlanIRI)) / (1.0F + user.getSentencePlanUseCount(sentencePlanIRI)));
                            //float score = 1.0F/SPQM.getSentencePlan(sentencePlanIRI).getSlotsList().size();
                            sentencePlanScores.put(sentencePlanIRI, score);
                        }
                    }

                    if (!sentencePlanScores.isEmpty()) {
                        IRI bestPlanIRI = null;
                        float max_score = 0.0F;

                        for (IRI sentencePlanIRI : sentencePlanScores.keySet()) {
                            if (sentencePlanScores.get(sentencePlanIRI).floatValue() >= max_score) {
                                max_score = sentencePlanScores.get(sentencePlanIRI).floatValue();
                                bestPlanIRI = sentencePlanIRI;
                            }
                        }

                        return bestPlanIRI;
                    }
                }
            }
            for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
                SentencePlan defaultPlan = DefaultResourcesManager.generateDefaultSentencePlanForProperty(propertyIRI, mainModel, language);
                SPQM.addSentencePlan(defaultPlan, language);

                return defaultPlan.getSentencePlanIRI();
            }
            return null;
        }
        return chooseSentencePlan(propertyIRI, SPQM, language);
    }//chooseSentencePlan

    public IRI chooseComparisonSentencePlan(IRI sentencePlanIRI, SentencePlanQueryManager SPQM, String language, Comparison comparison, String gen, String number, SurfaceRealization SR, IRI NLNameCompIRI) {
        IRI compSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + sentencePlanIRI.getFragment() + "-" + comparison.getType() + "_" + comparison.getValueIRI().getFragment() + "_" + language);

        SentencePlan sentencePlan = SPQM.getSentencePlan(compSPEN_IRI);

        if (sentencePlan != null) {
            return sentencePlan.getSentencePlanIRI();
        }

        sentencePlan = SPQM.getSentencePlan(sentencePlanIRI);
        if (sentencePlan != null) {
            SentencePlan compPlan = DefaultResourcesManager.generateSentencePlanForComparison(sentencePlan, comparison);
            SPQM.addSentencePlan(compPlan, language);

            return compPlan.getSentencePlanIRI();
        }

        return null;
    }//chooseComparisonSentencePlan

    //chooses a unique sentence plan for comparisons
    public IRI chooseUniqueSentencePlan(IRI sentencePlanIRI, SentencePlanQueryManager SPQM, String language) {
        IRI uniqueSPEN_IRI = IRI.create(NLResourceManager.nlowlNS + sentencePlanIRI.getFragment() + "-" + ComparisonTypes.UNIQUE + "SP" + language);

        SentencePlan sentencePlan = SPQM.getSentencePlan(uniqueSPEN_IRI);

        if (sentencePlan != null) {
            return sentencePlan.getSentencePlanIRI();
        }

        sentencePlan = SPQM.getSentencePlan(sentencePlanIRI);
        if (sentencePlan != null) {
            SentencePlan uniquePlan = DefaultResourcesManager.generateSentencePlanForUniqueComparison(sentencePlan);
            SPQM.addSentencePlan(uniquePlan, language);

            return uniquePlan.getSentencePlanIRI();
        }
        return null;
    }//chooseUniqueSentencePlan

    //chooces  a sentence plan randomly
    public IRI chooseNLName(IRI indivOrClassIRI, NLNameQueryManager NLNQM, String language) {
        if (useNLNames) {
            HashSet<IRI> NLNames = new HashSet<IRI>();
            HashSet<IRI> candNLNames = NLNameMappings.get(indivOrClassIRI);
            if (candNLNames != null) {
                for (IRI name : candNLNames) {
                	if (NLNQM.getNLName(name) != null) {
                		NLNames.add(name);
                	}
                }
            }
            ArrayList<IRI> availableNLNames = new ArrayList<IRI>();

            if (!NLNames.isEmpty()) {
                for (IRI NLNameIRI : NLNames) {
                    if (NLNQM.getNLName(NLNameIRI, language) != null) {
                        availableNLNames.add(NLNameIRI);
                    }
                }

                if (!availableNLNames.isEmpty()) {
                    int index = (int) Math.floor(1 + (Math.random() * availableNLNames.size()));
                    return availableNLNames.get(index);
                }
            }
            if (indivOrClassIRI.isThing()) {
                if (Languages.isEnglish(language)) {
                    return DefaultResourcesManager.entityNLNEN_IRI;
                }
                if (Languages.isGreek(language)) {
                    return DefaultResourcesManager.entityNLNGR_IRI;
                }
            }
        }
        for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
            NLName defaultName = DefaultResourcesManager.generateDefaultNLNameForIndividual(indivOrClassIRI, mainModel, language);
            NLNQM.addNLName(defaultName, language);

            return defaultName.getNLNameIRI();
        }
        return null;
    }//chooseSentencePlan

    //chooces a sentence plan according to user
    public IRI chooseNLName(IRI indivOrClassIRI, NLNameQueryManager NLNQM, String language, NLGUser user) {
        if (user != null) {
            if (useNLNames) {
                HashSet<IRI> NLNames = new HashSet<IRI>();
                HashSet<IRI> candNLNames = NLNameMappings.get(indivOrClassIRI);
                if (candNLNames != null) {
	                for (IRI name : candNLNames) {
	                	if (NLNQM.getNLName(name) != null || name.equals(NLResourceManager.anonymous.getIRI())) {
	                		NLNames.add(name);
	                	}
	                }
                }
                HashMap<IRI, Float> NLNameScores = new HashMap<IRI, Float>();

                if (!NLNames.isEmpty()) {
                    for (IRI NLNameIRI : NLNames) {
                        if (NLNameIRI.equals(NLResourceManager.anonymous.getIRI())) {
                            return NLNameIRI;
                        } else if (NLNQM.getNLName(NLNameIRI, language) != null) {
                            float score = ((1.0F + user.getUserModel().getNLNameAppropriateness(NLNameIRI)) / (1.0F + user.getNLNameUseCount(NLNameIRI)));
                            NLNameScores.put(NLNameIRI, score);
                        }
                    }

                    if (!NLNameScores.isEmpty()) {
                        IRI bestNameIRI = null;
                        float max_score = 0.0F;

                        for (IRI NLNameIRI : NLNameScores.keySet()) {
                            if (NLNameScores.get(NLNameIRI).floatValue() >= max_score) {
                                max_score = NLNameScores.get(NLNameIRI).floatValue();
                                bestNameIRI = NLNameIRI;
                            }
                        }

                        return bestNameIRI;
                    }

                }
                if (indivOrClassIRI.isThing()) {
                    if (Languages.isEnglish(language)) {
                        return DefaultResourcesManager.entityNLNEN_IRI;
                    }
                    if (Languages.isGreek(language)) {
                        return DefaultResourcesManager.entityNLNGR_IRI;
                    }
                }
            }
            for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
                NLName defaultName = DefaultResourcesManager.generateDefaultNLNameForIndividual(indivOrClassIRI, mainModel, language);
                NLNQM.addNLName(defaultName, language);

                return defaultName.getNLNameIRI();
            }
            return null;
        }
        return chooseNLName(indivOrClassIRI, NLNQM, language);
    }//chooseNLName
    
    public IRI chooseDefaultNLName(IRI indivOrClassIRI, NLNameQueryManager NLNQM, String language) {
        for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
            NLName defaultName = DefaultResourcesManager.generateDefaultNLNameForIndividual(indivOrClassIRI, mainModel, language);
            NLNQM.addNLName(defaultName, language);

            return defaultName.getNLNameIRI();
        }
        return null;
    }

    public IRI chooseComparisonNLName(IRI NLNameIRI, NLNameQueryManager NLNQM, String language, Comparison comparison, boolean entityMentioned) {
        IRI compIRI = IRI.create(NLResourceManager.nlowlNS + NLNameIRI.getFragment() + "-CompNLN" + "_" + comparison.getType() + language);

        NLName name = NLNQM.getNLName(compIRI);
        if (name != null) {
            return name.getNLNameIRI();
        }

        name = NLNQM.getNLName(NLNameIRI);

        if (name != null) {
            name = DefaultResourcesManager.generateNLNameForComparison(name, comparison, entityMentioned);
            NLNQM.addNLName(name, language);
            return name.getNLNameIRI();
        }

        return null;
    }

    //chooses a unique NLName for comparisons
    public ArrayList<IRI> chooseUniqueNLName(ArrayList<IRI> NLNameIRIs, NLNameQueryManager NLNQM, String language) {
        ArrayList<IRI> iris = new ArrayList<IRI>();
        for (IRI NLNameIRI : NLNameIRIs) {
            IRI uniqueNLNEN_IRI = IRI.create(NLResourceManager.nlowlNS + NLNameIRI.getFragment() + "-" + ComparisonTypes.UNIQUE + "NLN" + language);

            NLName name = NLNQM.getNLName(uniqueNLNEN_IRI);
            if (name != null) {
                iris.add(name.getNLNameIRI());
                continue;
            }

            name = NLNQM.getNLName(NLNameIRI);
            if (name != null) {
                NLName uniqueName = DefaultResourcesManager.generateNLNameForUniqueComparison(name);
                NLNQM.addNLName(uniqueName, language);

                iris.add(uniqueName.getNLNameIRI());
                continue;
            }
        }
        return iris;
    }//chooseUniqueNLName

    public IRI getDefaultNLName(IRI indivOrClassIRI, NLNameQueryManager NLNQM, String language) {
        for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
            NLName defaultName = DefaultResourcesManager.generateDefaultNLNameForIndividual(indivOrClassIRI, mainModel, language);
            NLNQM.addNLName(defaultName, language);

            return defaultName.getNLNameIRI();
        }
        return null;
    }

    public void addSentencePlanMapping(IRI propertyIRI, IRI sentencePlanIRI) {
        for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
            OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, sentencePlanIRI);
            OWLAxiomChange addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
            mainModel.getOWLOntologyManager().applyChange(addAxiomChange);
        }

        if (sentencePlanMappings.containsKey(propertyIRI)) {
            sentencePlanMappings.get(propertyIRI).add(sentencePlanIRI);
        } else {
            HashSet<IRI> sentencePlanIRIs = new HashSet<IRI>();
            sentencePlanIRIs.add(sentencePlanIRI);

            sentencePlanMappings.put(propertyIRI, sentencePlanIRIs);
        }
    }

    public void addNLNameMapping(IRI indivOrClassIRI, IRI NLNameIRI) {
        for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
            OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, indivOrClassIRI, NLNameIRI);
            OWLAxiomChange addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
            mainModel.getOWLOntologyManager().applyChange(addAxiomChange);
        }

        if (NLNameMappings.containsKey(indivOrClassIRI)) {
            NLNameMappings.get(indivOrClassIRI).add(NLNameIRI);
        } else {
            HashSet<IRI> NLNameIRIs = new HashSet<IRI>();
            NLNameIRIs.add(NLNameIRI);

            NLNameMappings.put(indivOrClassIRI, NLNameIRIs);
        }
    }

    public void setComparisonsAllowed(IRI propertyIRI, boolean allowed) {
        for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
            if (allowed) {
                OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.comparisonsAllowed, propertyIRI, mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(allowed));
                OWLAxiomChange addAxiomChange = new AddAxiom(mainModel, annotationAssertion);
                mainModel.getOWLOntologyManager().applyChange(addAxiomChange);
            } else {
                OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.comparisonsAllowed, propertyIRI, mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(!allowed));
                OWLAxiomChange removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                mainModel.getOWLOntologyManager().applyChange(removeAxiomChange);
            }
        }

        propertyComparisonsAllowed.put(propertyIRI, allowed);
    }

    public void removeSentencePlanMapping(IRI propertyIRI, IRI sentencePlanIRI) {
        for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
            OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, sentencePlanIRI);
            OWLAxiomChange removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
            mainModel.getOWLOntologyManager().applyChange(removeAxiomChange);
        }

        if (sentencePlanMappings.containsKey(propertyIRI)) {
            sentencePlanMappings.get(propertyIRI).remove(sentencePlanIRI);
        }
    }

    public void removeNLNameMapping(IRI indivOrClassIRI, IRI NLNameIRI) {
        for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
            OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, indivOrClassIRI, NLNameIRI);
            OWLAxiomChange removeAxiomChange = new RemoveAxiom(mainModel, annotationAssertion);
            mainModel.getOWLOntologyManager().applyChange(removeAxiomChange);
        }
        if (NLNameMappings.containsKey(indivOrClassIRI)) {
            NLNameMappings.get(indivOrClassIRI).remove(NLNameIRI);
        }
    }

    public void renameClass(IRI oldClassIRI, IRI newClassIRI) {
        for (OWLOntology mainModel : getContainingOntologies(oldClassIRI)) {
            if (NLNameMappings.containsKey(oldClassIRI)) {
                HashSet<IRI> NLNameIRIs = NLNameMappings.get(oldClassIRI);

                NLNameMappings.remove(oldClassIRI);
                NLNameMappings.put(newClassIRI, NLNameIRIs);

                //Ontology Side
                for (IRI NLNameIRI : NLNameIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, oldClassIRI, NLNameIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);

                    annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, newClassIRI, NLNameIRI);
                    axiomChange = new AddAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void renameInstance(IRI oldInstanceIRI, IRI newInstanceIRI) {
        for (OWLOntology mainModel : getContainingOntologies(oldInstanceIRI)) {
            if (NLNameMappings.containsKey(oldInstanceIRI)) {
                HashSet<IRI> NLNameIRIs = NLNameMappings.get(oldInstanceIRI);

                NLNameMappings.remove(oldInstanceIRI);
                NLNameMappings.put(newInstanceIRI, NLNameIRIs);

                //Ontology Side
                for (IRI NLNameIRI : NLNameIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, oldInstanceIRI, NLNameIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);

                    annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, newInstanceIRI, NLNameIRI);
                    axiomChange = new AddAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void renameProperty(IRI oldPropertyIRI, IRI newPropertyIRI) {
        for (OWLOntology mainModel : getContainingOntologies(oldPropertyIRI)) {
            if (sentencePlanMappings.containsKey(oldPropertyIRI)) {
                HashSet<IRI> sentencePlanIRIs = sentencePlanMappings.get(oldPropertyIRI);

                sentencePlanMappings.remove(oldPropertyIRI);
                sentencePlanMappings.put(newPropertyIRI, sentencePlanIRIs);

                //Ontology Side
                for (IRI sentencePlanIRI : sentencePlanIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, oldPropertyIRI, sentencePlanIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);

                    annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, newPropertyIRI, sentencePlanIRI);
                    axiomChange = new AddAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
            if (propertyComparisonsAllowed.containsKey(oldPropertyIRI)) {
                boolean allowed = propertyComparisonsAllowed.get(oldPropertyIRI);

                //Ontology Side
                OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.comparisonsAllowed, newPropertyIRI, mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(allowed));
                OWLAxiomChange axiomChange = new AddAxiom(mainModel, annotationAssertion);
                mainModel.getOWLOntologyManager().applyChange(axiomChange);

                annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.comparisonsAllowed, oldPropertyIRI, mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(allowed));
                axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                mainModel.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public void renameNLName(IRI oldNLNameIRI, IRI newNLNameIRI) {
        for (IRI indivOrClassIRI : NLNameMappings.keySet()) {
            HashSet<IRI> NLNameIRIs = NLNameMappings.get(indivOrClassIRI);
            if (NLNameIRIs.contains(oldNLNameIRI)) {
                NLNameIRIs.remove(oldNLNameIRI);
                NLNameIRIs.add(newNLNameIRI);

                //Ontology Side
                for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, indivOrClassIRI, oldNLNameIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);

                    annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, indivOrClassIRI, newNLNameIRI);
                    axiomChange = new AddAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void renameSentencePlan(IRI oldSentencePlanIRI, IRI newSentencePlanIRI) {
        for (IRI propertyIRI : sentencePlanMappings.keySet()) {
            HashSet<IRI> sentencePlanIRIs = sentencePlanMappings.get(propertyIRI);
            if (sentencePlanIRIs.contains(oldSentencePlanIRI)) {
                sentencePlanIRIs.remove(oldSentencePlanIRI);
                sentencePlanIRIs.add(newSentencePlanIRI);

                //Ontology Side
                for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, oldSentencePlanIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);

                    annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, newSentencePlanIRI);
                    axiomChange = new AddAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void deleteClass(IRI classIRI) {
        for (OWLOntology mainModel : getContainingOntologies(classIRI)) {
            if (NLNameMappings.containsKey(classIRI)) {
                HashSet<IRI> NLNamesIRIs = NLNameMappings.get(classIRI);
                NLNameMappings.remove(classIRI);

                //Ontology Side
                for (IRI NLName : NLNamesIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, classIRI, NLName);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void deleteInstance(IRI instanceIRI) {
        for (OWLOntology mainModel : getContainingOntologies(instanceIRI)) {
            if (NLNameMappings.containsKey(instanceIRI)) {
                HashSet<IRI> NLNamesIRIs = NLNameMappings.get(instanceIRI);
                NLNameMappings.remove(instanceIRI);

                //Ontology Side
                for (IRI NLName : NLNamesIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, instanceIRI, NLName);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void deleteProperty(IRI propertyIRI) {
        for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
            if (sentencePlanMappings.containsKey(propertyIRI)) {
                HashSet<IRI> sentencePlanIRIs = sentencePlanMappings.get(propertyIRI);
                sentencePlanMappings.remove(propertyIRI);

                //Ontology Side
                for (IRI sentencePlanIRI : sentencePlanIRIs) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, sentencePlanIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
            if (propertyComparisonsAllowed.containsKey(propertyIRI)) {
                boolean allowed = propertyComparisonsAllowed.get(propertyIRI);

                //Ontology Side                
                OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.comparisonsAllowed, propertyIRI, mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLLiteral(allowed));
                OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                mainModel.getOWLOntologyManager().applyChange(axiomChange);
            }
        }
    }

    public void deleteNLName(IRI NLNameIRI) {
        for (IRI indivOrClassIRI : NLNameMappings.keySet()) {
            HashSet<IRI> NLNameIRIs = NLNameMappings.get(indivOrClassIRI);
            if (NLNameIRIs.contains(NLNameIRI)) {
                NLNameIRIs.remove(NLNameIRI);

                //Ontology Side
                for (OWLOntology mainModel : getContainingOntologies(indivOrClassIRI)) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasNLName, indivOrClassIRI, NLNameIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    public void deleteSentencePlan(IRI sentencePlanIRI) {
        for (IRI propertyIRI : sentencePlanMappings.keySet()) {
            HashSet<IRI> sentencePlanIRIs = sentencePlanMappings.get(propertyIRI);
            if (sentencePlanIRIs.contains(sentencePlanIRI)) {
                sentencePlanIRIs.remove(sentencePlanIRI);

                //Ontology Side
                for (OWLOntology mainModel : getContainingOntologies(propertyIRI)) {
                    OWLAnnotationAssertionAxiom annotationAssertion = mainModel.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationAssertionAxiom(NLResourceManager.hasSentencePlan, propertyIRI, sentencePlanIRI);
                    OWLAxiomChange axiomChange = new RemoveAxiom(mainModel, annotationAssertion);
                    mainModel.getOWLOntologyManager().applyChange(axiomChange);
                }
            }
        }
    }

    // load mapping info from ontology
    public void importMappings(OWLOntology mainModel) {
        mainModels.add(mainModel);

        //Individual Mappings
        for (OWLNamedIndividual individual : mainModel.getIndividualsInSignature(true)) {
            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(individual.getIRI())) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasNLName)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                        IRI indivOrClassIRI = (IRI) annotation.getSubject();
                        IRI NLNameIRI = (IRI) annotation.getAnnotation().getValue();

                        if (NLNameMappings.containsKey(indivOrClassIRI)) {
                            NLNameMappings.get(indivOrClassIRI).add(NLNameIRI);
                        } else {
                            HashSet<IRI> NLNameIRIs = new HashSet<IRI>();
                            NLNameIRIs.add(NLNameIRI);

                            NLNameMappings.put(indivOrClassIRI, NLNameIRIs);
                        }
                    }
                }
            }
        }

        //Class Mappings
        for (OWLClass owlClass : mainModel.getClassesInSignature(true)) {
            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(owlClass.getIRI())) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasNLName)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                        IRI indivOrClassIRI = (IRI) annotation.getSubject();
                        IRI NLNameIRI = (IRI) annotation.getAnnotation().getValue();

                        if (NLNameMappings.containsKey(indivOrClassIRI)) {
                            NLNameMappings.get(indivOrClassIRI).add(NLNameIRI);
                        } else {
                            HashSet<IRI> NLNameIRIs = new HashSet<IRI>();
                            NLNameIRIs.add(NLNameIRI);

                            NLNameMappings.put(indivOrClassIRI, NLNameIRIs);
                        }
                    }
                }
            }
        }

        //Property Mappings
        for (OWLDataProperty dataProperty : mainModel.getDataPropertiesInSignature(true)) {
            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(dataProperty.getIRI())) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasSentencePlan)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                        IRI propertyIRI = (IRI) annotation.getSubject();
                        IRI sentencePlanIRI = (IRI) annotation.getAnnotation().getValue();

                        if (sentencePlanMappings.containsKey(propertyIRI)) {
                            sentencePlanMappings.get(propertyIRI).add(sentencePlanIRI);
                        } else {
                            HashSet<IRI> sentencePlanIRIs = new HashSet<IRI>();
                            sentencePlanIRIs.add(sentencePlanIRI);

                            sentencePlanMappings.put(propertyIRI, sentencePlanIRIs);
                        }
                    }
                }
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.comparisonsAllowed)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                        if (((OWLLiteral) annotation.getAnnotation().getValue()).isBoolean()) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            boolean allowed = ((OWLLiteral) annotation.getAnnotation().getValue()).parseBoolean();

                            propertyComparisonsAllowed.put(propertyIRI, allowed);
                        }
                    }
                }
            }
        }
        for (OWLObjectProperty objectProperty : mainModel.getObjectPropertiesInSignature(true)) {
            for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(objectProperty.getIRI())) {
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.hasSentencePlan)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof IRI)) {
                        IRI propertyIRI = (IRI) annotation.getSubject();
                        IRI sentencePlanIRI = (IRI) annotation.getAnnotation().getValue();

                        if (sentencePlanMappings.containsKey(propertyIRI)) {
                            sentencePlanMappings.get(propertyIRI).add(sentencePlanIRI);
                        } else {
                            HashSet<IRI> sentencePlanIRIs = new HashSet<IRI>();
                            sentencePlanIRIs.add(sentencePlanIRI);

                            sentencePlanMappings.put(propertyIRI, sentencePlanIRIs);
                        }
                    }
                }
                if (annotation.getAnnotation().getProperty().equals(NLResourceManager.comparisonsAllowed)) {
                    if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                        if (((OWLLiteral) annotation.getAnnotation().getValue()).isBoolean()) {
                            IRI propertyIRI = (IRI) annotation.getSubject();
                            boolean allowed = ((OWLLiteral) annotation.getAnnotation().getValue()).parseBoolean();

                            propertyComparisonsAllowed.put(propertyIRI, allowed);
                        }
                    }
                }
            }
        }
        for (OWLAnnotationAssertionAxiom annotation : mainModel.getAnnotationAssertionAxioms(NLResourcesManager.isA.getIRI())) {
            if (annotation.getAnnotation().getProperty().equals(NLResourceManager.comparisonsAllowed)) {
                if ((annotation.getSubject() instanceof IRI) && (annotation.getAnnotation().getValue() instanceof OWLLiteral)) {
                    if (((OWLLiteral) annotation.getAnnotation().getValue()).isBoolean()) {
                        boolean allowed = ((OWLLiteral) annotation.getAnnotation().getValue()).parseBoolean();

                        propertyComparisonsAllowed.put(NLResourcesManager.isA.getIRI(), allowed);
                    }
                }
            }
        }
    }//loadMappings

    public HashSet<IRI> getSentencePlansSet(SentencePlanQueryManager SPQM, IRI propertyIRI) {
        HashSet<IRI> sentencePlans = new HashSet<IRI>();
        HashSet<IRI> candSentencePlans = sentencePlanMappings.get(propertyIRI);
        if (candSentencePlans != null) {
	        for (IRI plan : candSentencePlans) {
	        	if (SPQM.getSentencePlan(plan) != null) {
	        		sentencePlans.add(plan);
	        	}
	        }
        }
        return sentencePlans;
    }

    public HashSet<IRI> getNLNamesSet(NLNameQueryManager NLNQM, IRI indivOrClassIRI) {
        HashSet<IRI> NLNames = new HashSet<IRI>();
        HashSet<IRI> candNLNames = NLNameMappings.get(indivOrClassIRI);
        if (candNLNames != null) {
            for (IRI name : candNLNames) {
            	if (NLNQM.getNLName(name) != null) {
            		NLNames.add(name);
            	}
            }
        }
        return NLNames;
    }

    public HashSet<IRI> getPropertiesSet(IRI sentencePlanIRI) {
        HashSet<IRI> propertiesSet = new HashSet<IRI>();
        for (IRI propertyIRI : sentencePlanMappings.keySet()) {
            if (sentencePlanMappings.get(propertyIRI).contains(sentencePlanIRI)) {
                propertiesSet.add(propertyIRI);
            }
        }
        return propertiesSet;
    }

    public HashSet<IRI> getPropertiesThatAllowComparisons() {
        HashSet<IRI> propertiesSet = new HashSet<IRI>();
        for (OWLOntology model : mainModels) {
            for (OWLDataProperty dataProperty : model.getDataPropertiesInSignature(true)) {
                if (propertyComparisonsAllowed.containsKey(dataProperty.getIRI())) {
                    propertiesSet.add(dataProperty.getIRI());
                }
            }
            for (OWLObjectProperty objectProperty : model.getObjectPropertiesInSignature(true)) {
                if (propertyComparisonsAllowed.containsKey(objectProperty.getIRI())) {
                    propertiesSet.add(objectProperty.getIRI());
                }
            }
        }
        if (propertyComparisonsAllowed.containsKey(NLResourceManager.isA.getIRI())) {
            propertiesSet.add(NLResourceManager.isA.getIRI());
        }
        return propertiesSet;
    }

    public Set<IRI> getPropertiesSet() {
        return sentencePlanMappings.keySet();
    }

    public HashSet<IRI> getIndividualOrClassSet(IRI NLNameIRI) {
        HashSet<IRI> individualOrClassSet = new HashSet<IRI>();
        for (IRI individualOrClasIRI : NLNameMappings.keySet()) {
            if (NLNameMappings.get(individualOrClasIRI).contains(NLNameIRI)) {
                individualOrClassSet.add(individualOrClasIRI);
            }
        }
        return individualOrClassSet;
    }

    public Set<IRI> getIndividualOrClassSet() {
        return NLNameMappings.keySet();
    }

    public boolean areComparisonsAllowed(IRI propertyIRI) {
        if (propertyComparisonsAllowed.containsKey(propertyIRI)) {
            return propertyComparisonsAllowed.get(propertyIRI);
        }
        return false;
    }

    public HashSet<OWLOntology> getContainingOntologies(IRI entryIRI) {
        HashSet<OWLOntology> containingOntologies = new HashSet<OWLOntology>();
        for (OWLOntology model : mainModels) {
            if (entryIRI.equals(NLResourcesManager.isA.getIRI())) {
                containingOntologies.add(model);
            } else if (model.containsObjectPropertyInSignature(entryIRI, true)) {
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

    public boolean isUseNLNames() {
        return useNLNames;
    }

    public void setUseNLNames(boolean useNLNames) {
        this.useNLNames = useNLNames;
    }

    public boolean isUseSentencePlans() {
        return useSentencePlans;
    }

    public void setUseSentencePlans(boolean useSentencePlans) {
        this.useSentencePlans = useSentencePlans;
    }
}