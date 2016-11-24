/*
NaturalOWL version 2.0
Copyright (C) 2013 Gerasimos Lampouras and Ioanna Giannopoulou
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

package gr.aueb.cs.nlg.NLOwlPlugin;

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.LexiconQueryManager;
import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLNameQueryManager;
import gr.aueb.cs.nlg.NLFiles.OrderingQueryManager;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;
import gr.aueb.cs.nlg.NLFiles.UserModelQueryManager;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.editor.owl.model.selection.OWLSelectionModel;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyImpl;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class NaturalOWLTab extends AbstractOWLViewComponent implements ActionListener, ItemListener {
	private static final long serialVersionUID = 2933019629486971027L;
	
	static Logger log = Logger.getLogger(LexiconTab.class);
	public NaturalOWLTab thisTab;
	static File loadedNLResourcesFile = null;
		
	static LexiconQueryManager LQM = null;
	static SentencePlanQueryManager SPQM = null; 
	static NLNameQueryManager NLNQM = null; 
	static MappingQueryManager MQM = null; 
	static OrderingQueryManager OQM = null; 
	static UserModelQueryManager UMQM = null; 
	
	static OWLSelectionModel lexiconSelectionModel;
	static OWLSelectionModel sentencePlanSelectionModel;
	static OWLSelectionModel NLNameSelectionModel;
	static OWLSelectionModel userModelTreePropertySelectionModel;
	static OWLSelectionModel userModelTreeClassSelectionModel;
	static OWLSelectionModel userModelTableModifierSelectionModel;
	static OWLSelectionModel userModelTablePropertySelectionModel;
	static OWLSelectionModel userModelTableClassSelectionModel;
	static OWLSelectionModel userModelTableIndivSelectionModel;
	static OWLSelectionModel generationClassSelectionModel;
	static OWLSelectionModel generationIndivSelectionModel;
	
	static String NLFilePath = "";
	static String masterLoadedOntologyID = "";
	static JCheckBox refresh = new JCheckBox();
	static final JFileChooser fc = new JFileChooser();
	
	static boolean areNLResourcesDirty = false;
	
	static NLResourceManager resourcesManager;	
	
	private IOListener ioListener;
	private OWLOntologyChangeListener changeListener;
    	
    NaturalOWLTab() {
    	super();
		thisTab = this;
    }

	public void initialiseNaturalOWL() throws Exception {
    	ioListener = new IOListener(){
            public void beforeSave(IOListenerEvent event) {
        		if (areNLResourcesDirty) {
        			Object[] options = {"Yes","No"};
        			int n = JOptionPane.showOptionDialog(null, "<html><b>Do you also want to save changes in the current NL resource ontology of the NaturalOWL plugin?</b><br>Your changes will be lost if you don't save them.</html>", "NaturalOWL: Save NL resources ontology?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        			if (n == 0) {
	        			if (loadedNLResourcesFile != null) {       				
	        				saveNLResourcesOntology(loadedNLResourcesFile);
	        			} else {
	        				saveAsNaturalOWLFiles(thisTab.getOWLWorkspace());
	        			}
        			}
        		}
            }

            public void afterSave(IOListenerEvent event) {}
            public void beforeLoad(IOListenerEvent event) {}
            public void afterLoad(IOListenerEvent event) {}
        };
        
		getOWLModelManager().addIOListener(ioListener);
		
		/*
        changeListener = new OWLOntologyChangeListener() {
    		public void ontologiesChanged(List<? extends OWLOntologyChange> arg0) throws OWLException {
    			IRI addedObjectProperty = null;
    			IRI addedDataProperty = null;
    			IRI addedIndividual = null;
    			IRI addedClass = null;
    			IRI deletedObjectProperty = null;
    			IRI deletedDataProperty = null;
    			IRI deletedIndividual = null;
    			IRI deletedClass = null;
    			
    			for (OWLOntologyChange change : arg0) {
    				System.out.println("CHANGE " + arg0);
					if (change instanceof AddAxiom) {
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getObjectPropertiesInSignature().size() == 1) {
								for (OWLObjectProperty changed : change.getAxiom().getObjectPropertiesInSignature()) {
									addedObjectProperty = changed.getIRI();
								}
			    				System.out.println("CHANGE " + addedObjectProperty);
								refreshActiveOntologyProperties = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getDataPropertiesInSignature().size() == 1) {
								for (OWLDataProperty changed : change.getAxiom().getDataPropertiesInSignature()) {
									addedDataProperty = changed.getIRI();
								}
								refreshActiveOntologyProperties = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getIndividualsInSignature().size() == 1) {
								for (OWLNamedIndividual changed : change.getAxiom().getIndividualsInSignature()) {
									addedIndividual = changed.getIRI();
								}
								refreshActiveOntologyClassOrIndivs = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getClassesInSignature().size() == 1) {
								for (OWLClass changed : change.getAxiom().getClassesInSignature()) {
									deletedClass = changed.getIRI();
								}
								refreshActiveOntologyClassOrIndivs = true;
							}
						}
					}
					if (change instanceof RemoveAxiom) {
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getObjectPropertiesInSignature().size() == 1) {
								for (OWLObjectProperty changed : change.getAxiom().getObjectPropertiesInSignature()) {
									deletedObjectProperty = changed.getIRI();
								}
								refreshActiveOntologyProperties = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getDataPropertiesInSignature().size() == 1) {
								for (OWLDataProperty changed : change.getAxiom().getDataPropertiesInSignature()) {
									deletedDataProperty = changed.getIRI();
								}
								refreshActiveOntologyProperties = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getIndividualsInSignature().size() == 1) {
								for (OWLNamedIndividual changed : change.getAxiom().getIndividualsInSignature()) {
									deletedIndividual = changed.getIRI();
								}
								refreshActiveOntologyClassOrIndivs = true;
							}
						}
						if (change.getAxiom().equals(AxiomType.DECLARATION)) {
							if (change.getAxiom().getClassesInSignature().size() == 1) {
								for (OWLClass changed : change.getAxiom().getClassesInSignature()) {
									deletedClass = changed.getIRI();
								}
								refreshActiveOntologyClassOrIndivs = true;
							}
						}
					}
    			}
    			
    			if (addedObjectProperty != null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("ADDED OBJECT PROPERTY" + addedObjectProperty);
    			} else if (addedObjectProperty == null && addedDataProperty != null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("ADDED DATA PROPERTY" + addedDataProperty);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual != null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("ADDED INDIVIDUAL" + addedIndividual);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass != null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("ADDED CLASS" + addedClass);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty != null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("DELETED PROPERTY " + deletedObjectProperty);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty != null && deletedIndividual == null && deletedClass == null) {
					System.out.println("DELETED DATA PROPERTY " + deletedDataProperty);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual != null && deletedClass == null) {
					System.out.println("DELETED INDIVIDUAL" + deletedIndividual);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass != null) {
					System.out.println("DELETED CLASS" + deletedClass);
    			} else if (addedObjectProperty != null && addedDataProperty == null && addedIndividual == null && addedClass == null && deletedObjectProperty != null && deletedDataProperty == null && deletedIndividual == null && deletedClass == null) {
					System.out.println("RENAMED OBJECT PROPERTY" + deletedObjectProperty + " -> "+ addedObjectProperty);
    			} else if (addedObjectProperty == null && addedDataProperty != null && addedIndividual == null && addedClass == null && deletedObjectProperty == null && deletedDataProperty != null && deletedIndividual == null && deletedClass == null) {
					System.out.println("RENAMED DATA PROPERTY" + deletedDataProperty + " -> "+ addedDataProperty);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual != null && addedClass == null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual != null && deletedClass == null) {
					System.out.println("RENAMED INDIVIDUAL" + deletedIndividual + " -> "+ addedIndividual);
    			} else if (addedObjectProperty == null && addedDataProperty == null && addedIndividual == null && addedClass != null && deletedObjectProperty == null && deletedDataProperty == null && deletedIndividual == null && deletedClass != null) {
					System.out.println("RENAMED CLASS" + deletedClass + " -> "+ addedClass);
    			}
    		}
        };
		getOWLModelManager().addOntologyChangeListener(changeListener);*/
		
		loadNaturalOWLFiles();
	}
		
	public void loadNaturalOWLFiles(){
		boolean reload = false;
        
    	String ontPath = getOWLModelManager().getOWLOntologyManager().getOntologyDocumentIRI(getOWLModelManager().getActiveOntology()).toString();
        if (ontPath.startsWith("file")) {
        	NLFilePath = ontPath.substring(6, ontPath.lastIndexOf("/") + 1).replaceAll("%20", " ");
        } else {
        	NLFilePath = "";
        }
        if (!getOWLModelManager().getActiveOntology().getOntologyID().getOntologyIRI().toString().equals(masterLoadedOntologyID)) {
        	System.out.println(masterLoadedOntologyID);
    		masterLoadedOntologyID = getOWLModelManager().getActiveOntology().getOntologyID().getOntologyIRI().toString();
    		reload = true;
    	}
		
        if (reload) {
            File NLOwlFile = new File(NLFilePath + "NLresources.owl");
            
            if (NLOwlFile.exists()) {
        		openNLResourcesOntology(NLOwlFile, getOWLModelManager().getOntologies(), false);
            } else {
        		createNewNLResourcesOntology(IRI.create(NLResourceManager.resourcesNS.substring(0, NLResourceManager.resourcesNS.length()-1)), getOWLModelManager().getOntologies());
            }
        }
	}
	
	static void createNewNLResourcesOntology(IRI ontologyIRI, Set<OWLOntology> ontologies) {
        resourcesManager = new NLResourceManager();
        
		OWLOntology resourcesOntology = new OWLOntologyImpl(resourcesManager.getOntologyManager(), new OWLOntologyID(ontologyIRI));
	
        resourcesManager.loadNLResourcesModel(resourcesOntology);
        
        LQM = new LexiconQueryManager(resourcesManager);
        for (OWLOntology model : ontologies) {
        	LQM.importLexiconEntries(model);
        }
        
        SPQM = new SentencePlanQueryManager(resourcesManager);
        for (OWLOntology model : ontologies) {
        	SPQM.importSentencePlans(model);
        }
        
        NLNQM = new NLNameQueryManager(resourcesManager);
        for (OWLOntology model : ontologies) {
        	NLNQM.importNLNames(model);
        }

        UMQM = new UserModelQueryManager(resourcesManager);
        for (OWLOntology owl : ontologies) {
        	UMQM.importUserModels(owl);
        	UMQM.importAnnotationEvents(owl);
        }

        MQM = new MappingQueryManager(resourcesManager);
        for (OWLOntology model : ontologies) {
            MQM.importMappings(model);
        }

        OQM = new OrderingQueryManager(resourcesManager);
        for (OWLOntology model : ontologies) {
            OQM.importSections(model);
            OQM.importOrdering(model);
        }
        
        refreshListeners();
	}
	
	static void openNLResourcesOntology(File NLOwlFile, Set<OWLOntology> ontologies, boolean replaceOld) {
		loadedNLResourcesFile = NLOwlFile;
		
		if (replaceOld || resourcesManager == null || resourcesManager.getNLResourcesModel() == null) {
			resourcesManager = new NLResourceManager();
	        resourcesManager.loadNLResourcesModel(NLOwlFile);
		} else {
			importNLResourcesOntology(NLOwlFile, ontologies);
		}
        
        // load lexicon
        LQM = new LexiconQueryManager(resourcesManager);
        LQM.importLexiconEntries(resourcesManager.getNLResourcesModel());
        for (OWLOntology owl : ontologies) {
        	LQM.importLexiconEntries(owl);
        }

        //load sentencePlans info
        SPQM = new SentencePlanQueryManager(resourcesManager);
        SPQM.importSentencePlans(resourcesManager.getNLResourcesModel());
        for (OWLOntology owl : ontologies) {
        	SPQM.importSentencePlans(owl);
        }

        //load NLNames info
        NLNQM = new NLNameQueryManager(resourcesManager);
        NLNQM.importNLNames(resourcesManager.getNLResourcesModel());
        for (OWLOntology owl : ontologies) {
        	NLNQM.importNLNames(owl);
        }

        //load User Modelling info
        UMQM = new UserModelQueryManager(resourcesManager);
    	UMQM.importUserModels(resourcesManager.getNLResourcesModel());
        for (OWLOntology owl : ontologies) {
        	UMQM.importUserModels(owl);
        	UMQM.importAnnotationEvents(owl);
        }

        MQM = new MappingQueryManager(resourcesManager);
        for (OWLOntology owl : ontologies) {
        	MQM.importMappings(owl);
        }

        OQM = new OrderingQueryManager(resourcesManager);
    	OQM.importSections(resourcesManager.getNLResourcesModel());
    	OQM.importOrdering(resourcesManager.getNLResourcesModel());
        for (OWLOntology owl : ontologies) {
        	OQM.importSections(owl);
        	OQM.importOrdering(owl);
        }
        
        refreshListeners();
    	log.info("Loading NaturalOWL resources from " + resourcesManager.getNLResourcesModel());
	}
	
	static void refreshListeners() {
		if (lexiconSelectionModel == null)
			lexiconSelectionModel = new OWLSelectionModelImpl();
		if (sentencePlanSelectionModel == null)
			sentencePlanSelectionModel = new OWLSelectionModelImpl();
		if (NLNameSelectionModel == null)
			NLNameSelectionModel = new OWLSelectionModelImpl();

		if (userModelTreePropertySelectionModel == null)
			userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
		if (userModelTreeClassSelectionModel == null)
			userModelTreeClassSelectionModel = new OWLSelectionModelImpl();

		if (userModelTableModifierSelectionModel == null)
			userModelTableModifierSelectionModel = new OWLSelectionModelImpl();
		if (userModelTablePropertySelectionModel == null)
			userModelTablePropertySelectionModel = new OWLSelectionModelImpl();
		if (userModelTableClassSelectionModel == null)
			userModelTableClassSelectionModel = new OWLSelectionModelImpl();
		if (userModelTableIndivSelectionModel == null)
			userModelTableIndivSelectionModel = new OWLSelectionModelImpl();

		if (generationClassSelectionModel == null)
			generationClassSelectionModel = new OWLSelectionModelImpl();
		if (generationIndivSelectionModel == null)
			generationIndivSelectionModel = new OWLSelectionModelImpl();
	}
	
	static void importNLResourcesOntology(File NLOwlFile, Set<OWLOntology> ontologies) throws org.semanticweb.owlapi.model.OWLOntologyRenameException {
        try {            
        	OWLOntology NLResourcesModel;
        	if (resourcesManager == null) {
                resourcesManager = new NLResourceManager();
                
                LQM = new LexiconQueryManager(resourcesManager);
                SPQM = new SentencePlanQueryManager(resourcesManager);
                NLNQM = new NLNameQueryManager(resourcesManager);
                UMQM = new UserModelQueryManager(resourcesManager);
                MQM = new MappingQueryManager(resourcesManager);
                OQM = new OrderingQueryManager(resourcesManager);

    	        NLResourcesModel = resourcesManager.getOntologyManager().loadOntologyFromOntologyDocument(NLOwlFile);
        	} else {
            	NLResourceManager tempManager =  new NLResourceManager();
    	        NLResourcesModel = tempManager.getOntologyManager().loadOntologyFromOntologyDocument(NLOwlFile);
        	}
	
	        LQM.importLexiconEntries(NLResourcesModel);	
	        for (OWLOntology owl : ontologies) {
	        	LQM.importLexiconEntries(owl);
	        }
	        
	        SPQM.importSentencePlans(NLResourcesModel);	
	        for (OWLOntology owl : ontologies) {
	        	SPQM.importSentencePlans(owl);
	        }
	        
	        NLNQM.importNLNames(NLResourcesModel);
	        for (OWLOntology owl : ontologies) {
	        	NLNQM.importNLNames(owl);
	        }
	        
	    	UMQM.importUserModels(NLResourcesModel);	
	        for (OWLOntology owl : ontologies) {
	        	UMQM.importUserModels(owl);
	        	UMQM.importAnnotationEvents(owl);
	        }
	        
        	MQM.importMappings(NLResourcesModel);    
	        for (OWLOntology owl : ontologies) {
	        	MQM.importMappings(owl);
	        }
	        
	    	OQM.importSections(NLResourcesModel);
	    	OQM.importOrdering(NLResourcesModel);
	        for (OWLOntology owl : ontologies) {
	        	OQM.importSections(owl);
	        	OQM.importOrdering(owl);
	        }	    	
	    	
	        refreshListeners();
	    	log.info("Importing NaturalOWL resources from " + NLResourcesModel);
        } catch (org.semanticweb.owlapi.model.OWLOntologyCreationException e) {
        }
	}
	
	static void saveAsNaturalOWLFiles(OWLWorkspace component) {
		fc.setFileFilter(new OWLFilter());
		int returnVal = NaturalOWLTab.fc.showSaveDialog(component);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (NaturalOWLTab.fc.getSelectedFile().exists()) {
				int confirm = JOptionPane.showConfirmDialog(null, "Replace existing file?");
				if (confirm == JOptionPane.YES_OPTION) {
					loadedNLResourcesFile = NaturalOWLTab.fc.getSelectedFile();
					saveNLResourcesOntology(loadedNLResourcesFile);
				}
				return;
			}
			try {
				if (NaturalOWLTab.fc.getSelectedFile().createNewFile()) {
					loadedNLResourcesFile = NaturalOWLTab.fc.getSelectedFile();
					saveNLResourcesOntology(loadedNLResourcesFile);
				}
			} catch (IOException e) {
			}
	    	return;
		}
		return;
	}

	static void saveNLResourcesOntology(File NLOwlFile) {	
		if (resourcesManager == null) {
			resourcesManager = new NLResourceManager();
		}
				
		OWLOntology resourcesOntology = new OWLOntologyImpl(resourcesManager.getNLResourcesModel().getOWLOntologyManager(), resourcesManager.getNLResourcesModel().getOntologyID());
		
		if (LQM == null) {
	        LQM = new LexiconQueryManager(resourcesManager);
		}

		if (SPQM == null) {
	        SPQM = new SentencePlanQueryManager(resourcesManager);
		}

		if (NLNQM == null) {
	        NLNQM = new NLNameQueryManager(resourcesManager);
		}

		if (OQM == null) {
	        OQM = new OrderingQueryManager(resourcesManager);
		}

		if (UMQM == null) {
			UMQM = new UserModelQueryManager(resourcesManager);
		}

		resourcesManager.exportResourcesOntologyTBox(resourcesOntology);
        LQM.exportLexiconEntries(resourcesOntology);
        SPQM.exportSentencePlans(resourcesOntology);
        NLNQM.exportNLNames(resourcesOntology);
        OQM.exportSections(resourcesOntology);
        OQM.exportOrders(resourcesOntology);
        UMQM.exportUserModels(resourcesOntology);
        
        try {
            if (NLOwlFile.exists()) {
    	        FileDocumentTarget fileTarget = new FileDocumentTarget(NLOwlFile);
    	        OWLFunctionalSyntaxOntologyFormat funSyntaxFormat = new OWLFunctionalSyntaxOntologyFormat();
    	        funSyntaxFormat.setPrefix(XmlMsgs.prefix, NLResourceManager.nlowlNS);

            	resourcesManager.getOntologyManager().saveOntology(resourcesOntology, funSyntaxFormat, fileTarget);
            	areNLResourcesDirty = false;
            	
            	log.info("Saving NaturalOWL resources to " + NLOwlFile.toURI());
            } else if (NLOwlFile.createNewFile()){
    	        FileDocumentTarget fileTarget = new FileDocumentTarget(NLOwlFile);
    	        OWLFunctionalSyntaxOntologyFormat funSyntaxFormat = new OWLFunctionalSyntaxOntologyFormat();
    	        
            	resourcesManager.getOntologyManager().saveOntology(resourcesOntology, funSyntaxFormat, fileTarget);
            	areNLResourcesDirty = false;
            	
            	log.info("Saving NaturalOWL resources to " + NLOwlFile.toURI());
            }
        } catch (OWLOntologyStorageException e) {
            System.out.println("Could not save ontology: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not save ontology: " + e.getMessage());
		}        
	}
	
	public void dirtenOntologies() {
		getOWLModelManager().setDirty(getOWLModelManager().getActiveOntology());
		areNLResourcesDirty = true;
	}
	
	static boolean isLegalIRI(IRI entryIRI) {
		try {
			new java.net.URI(entryIRI.toString());
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}
	
	public boolean isUniqueIRI(IRI entryIRI) {
		if (DefaultResourcesManager.isDefaultResource(entryIRI))
			return false;
		if(LQM.isNoun(entryIRI))
			return false;
		if(LQM.isAdjective(entryIRI))
			return false;
		if(LQM.isVerb(entryIRI))
			return false;
		if(SPQM.getSentencePlansList(Languages.ENGLISH).containsSentencePlan(entryIRI))
			return false;
		if(SPQM.getSentencePlansList(Languages.GREEK).containsSentencePlan(entryIRI))
			return false;
		if(NLNQM.getNLNamesList(Languages.ENGLISH).containsNLName(entryIRI))
			return false;
		if(NLNQM.getNLNamesList(Languages.GREEK).containsNLName(entryIRI))
			return false;
		if(OQM.getOrderedSections().contains(entryIRI))
			return false;
		if(UMQM.getUserModels().contains(entryIRI))
			return false;
		 
		return true;
	}

	protected void disposeOWLView() {
		getOWLModelManager().removeOntologyChangeListener(changeListener);
		getOWLModelManager().removeIOListener(ioListener);
	}
}

class OWLFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
 
        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("owl")) {
                return true;
            }
			return false;
        }
 
        return false;
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
 
    public String getDescription() {
        return "OWL file";
    }
}