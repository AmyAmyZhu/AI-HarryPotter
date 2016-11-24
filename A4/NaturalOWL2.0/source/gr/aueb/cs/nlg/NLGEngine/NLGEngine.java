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

import gr.aueb.cs.nlg.Comparisons.Comparison;
import gr.aueb.cs.nlg.Comparisons.ComparisonTree;
import gr.aueb.cs.nlg.Comparisons.MelegkoglouAlgorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import gr.aueb.cs.nlg.Languages.Languages;

import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.LexiconQueryManager;
import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLNArticleSlot;
import gr.aueb.cs.nlg.NLFiles.NLNNounSlot;
import gr.aueb.cs.nlg.NLFiles.NLNSlot;
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLNameQueryManager;
import gr.aueb.cs.nlg.NLFiles.OrderingQueryManager;
import gr.aueb.cs.nlg.NLFiles.SentencePlanQueryManager;
import gr.aueb.cs.nlg.NLFiles.UserModel;
import gr.aueb.cs.nlg.NLFiles.UserModelQueryManager;

import gr.aueb.cs.nlg.Utils.NLGUser;
import gr.aueb.cs.nlg.Utils.XmlDocumentCreator;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NLGEngine {

    public final static int ENGINE_PIPELINE_MODEL = 0;
    public final static int ENGINE_ILP_MODEL = 1;
    public final static int ENGINE_ILP_APPROXIMATION_MODEL = 2;
    private OWLOntologyManager manager = null;
    private File domainOwlFile = null;
    private File NLOwlFile = null;
    private String lang;
    private int mode;
    private int useEngine = ENGINE_PIPELINE_MODEL;
    private boolean useSlotLimit = false;
    private boolean allowComparisons = false;
    private XmlDocumentCreator XmlDocCreator;
    private SentencePlanQueryManager SPQM;
    private LexiconQueryManager LQM;
    private NLNameQueryManager NLNQM;
    private UserModelQueryManager UMQM;
    private MappingQueryManager MQM;
    private OrderingQueryManager OQM;
    private ContentSelection CS;
    private Lexicalisation LEX;
    private Aggregation AGGRGT;
    private ReferringExpressionsGenerator genRefExpr;
    private SurfaceRealization SR;
    private ILPEngine ILPEn;
    private XmlMsgs messagesAfterContentSelection;
    private ArrayList<String> stagesOutputs;
    private double lamda = 0.5;
    private int maxSentences = 3;
    private int maxSlotsPerSentence = 10;
    public List<?> charComparison;
    private AnnotatedDescription AD;
    private ComparisonTree comparisonTree;
    private ComparisonTree fullTree;

    public NLGEngine() {
    }

    public void setGenerateReferringExpressions(boolean b) {
        SR.setGenerateReferringExpressions(b);
    }

    public void setAnnotateGeneratedResources(boolean b) {
        SR.setAnnotateGeneratedResources(b);
    }

    public void setUseNLNames(boolean b) {
        MQM.setUseNLNames(b);
    }

    public void setUseSentencePlans(boolean b) {
        MQM.setUseSentencePlans(b);
    }

    public ContentSelection getCS() {
        return CS;
    }

    public void setCS(ContentSelection CS) {
        this.CS = CS;
    }

    public SurfaceRealization getSR() {
        return this.SR;
    }

    public void reset() {
        comparisonTree = new ComparisonTree(manager, MQM);
    }

    public LexiconQueryManager getLexicon() {
        return this.LQM;
    }

    // initialize NLG engine
    public NLGEngine(String domainOwlPath, String NLResourcesOwlPath, String lang) {
        XmlDocCreator = new XmlDocumentCreator();
        this.lang = lang;

        //Load Ontology
        domainOwlFile = new File(domainOwlPath);
        if (domainOwlFile.exists()) {
            manager = OWLManager.createOWLOntologyManager();

            try {
                OWLOntology ontology = manager.loadOntologyFromOntologyDocument(domainOwlFile);
                System.err.println("Loaded ontology: " + ontology);

                Set<OWLOntology> imports = ontology.getImportsClosure();

                for (OWLOntology importOnt : imports) {
                    IRI importOntIRI = manager.getOntologyDocumentIRI(importOnt);

                    if (importOntIRI.getScheme().equals("http")) {
                        try {
                            manager.loadOntology(importOntIRI);
                        } catch (Exception e) {
                        }
                    } else if (importOntIRI.getScheme().equals("file")) {
                        try {
                            manager.removeOntology(manager.getOntology(importOnt.getOntologyID()));
                            manager.loadOntologyFromOntologyDocument(importOntIRI);
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (OWLOntologyCreationException e) {
            } catch (UnknownOWLOntologyException e) {
            }
        }
        //Load Natural Language Resources
        NLOwlFile = new File(NLResourcesOwlPath);
        if (NLOwlFile.exists()) {//
            NLResourceManager resourcesManager = new NLResourceManager();
            resourcesManager.loadNLResourcesModel(NLOwlFile);

            // load lexicon
            LQM = new LexiconQueryManager(resourcesManager);
            LQM.importLexiconEntries(resourcesManager.getNLResourcesModel());
            for (OWLOntology model : manager.getOntologies()) {
                LQM.importLexiconEntries(model);
            }

            //load sentencePlans info
            SPQM = new SentencePlanQueryManager(resourcesManager);
            SPQM.importSentencePlans(resourcesManager.getNLResourcesModel());
            for (OWLOntology model : manager.getOntologies()) {
                SPQM.importSentencePlans(model);
            }

            //load NLNames info
            NLNQM = new NLNameQueryManager(resourcesManager);
            NLNQM.importNLNames(resourcesManager.getNLResourcesModel());
            for (OWLOntology model : manager.getOntologies()) {
                NLNQM.importNLNames(model);
            }

            //load User Modelling info
            UMQM = new UserModelQueryManager(resourcesManager);
            UMQM.importUserModels(resourcesManager.getNLResourcesModel());
            for (OWLOntology model : manager.getOntologies()) {
                UMQM.importUserModels(model);
                UMQM.importAnnotationEvents(model);
            }

            MQM = new MappingQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                MQM.importMappings(model);
            }

            OQM = new OrderingQueryManager(resourcesManager);
            OQM.importSections(resourcesManager.getNLResourcesModel());
            OQM.importOrdering(resourcesManager.getNLResourcesModel());
            for (OWLOntology model : manager.getOntologies()) {
                OQM.importSections(model);
                OQM.importOrdering(model);
            }

            initializeNLGModules();
        } else {
            NLResourceManager resourcesManager = new NLResourceManager();

            LQM = new LexiconQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                LQM.importLexiconEntries(model);
            }

            SPQM = new SentencePlanQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                SPQM.importSentencePlans(model);
            }

            NLNQM = new NLNameQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                NLNQM.importNLNames(model);
            }

            UMQM = new UserModelQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                UMQM.importUserModels(model);
                UMQM.importAnnotationEvents(model);
            }

            MQM = new MappingQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                MQM.importMappings(model);
            }

            OQM = new OrderingQueryManager(resourcesManager);
            for (OWLOntology model : manager.getOntologies()) {
                OQM.importSections(model);
                OQM.importOrdering(model);
            }

            initializeNLGModules();
        }
    }

    public NLGEngine(OWLOntologyManager ontologyManager, // ontology
            LexiconQueryManager LQMan, // lexicon
            SentencePlanQueryManager SPQMan, // sentence plans
            NLNameQueryManager NLNQMan, // sentence plans
            UserModelQueryManager UMQMan, // user modelling
            MappingQueryManager MQMan, // mapping
            OrderingQueryManager OQMan, // ordering
            String lang // 
            ) {
        XmlDocCreator = new XmlDocumentCreator();

        this.manager = ontologyManager;
        this.lang = lang;

        this.LQM = LQMan;
        this.SPQM = SPQMan;
        this.NLNQM = NLNQMan;
        this.UMQM = UMQMan;
        this.MQM = MQMan;
        this.OQM = OQMan;

        initializeNLGModules();
    }

    //Giorgos
    public void buildTree() {
        if (manager == null) {
            return;
        }

        for (OWLOntology model : manager.getOntologies()) {
            Set<OWLNamedIndividual> entities = model.getIndividualsInSignature();
            for (OWLNamedIndividual entity : entities) {
                //GEORGE check for double entities in tree, ok? Ok! :)
                if (!model.containsClassInSignature(entity.getIRI())) {
                    fullTree.add(entity.getIRI());
                }
            }
        }
    }

    public boolean areAllFactsAssimilated() {
        return CS.allFactsAreAssimilated();
    }

    private void initializeNLGModules() {
        CS = new ContentSelection(manager.getOntologies(), SPQM, NLNQM, UMQM, MQM, OQM, this.lang);
        LEX = new Lexicalisation(manager.getOntologies(), SPQM, this.lang);
        AGGRGT = new Aggregation(SPQM, NLNQM, LQM, this.lang);
        genRefExpr = new ReferringExpressionsGenerator(this.lang);
        SR = new SurfaceRealization(manager.getOntologies(), NLNQM, LQM, MQM, OQM, this.lang);

        ILPEn = new ILPEngine(SPQM, MQM, this.lang);
        comparisonTree = new ComparisonTree(manager, MQM);
        fullTree = new ComparisonTree(manager, MQM);
    }

    public void refreshNLGModules(Set<OWLOntology> new_models, String mylang) {
        CS = new ContentSelection(new_models, SPQM, NLNQM, UMQM, MQM, OQM, mylang);
        LEX = new Lexicalisation(new_models, SPQM, mylang);
        AGGRGT = new Aggregation(SPQM, NLNQM, LQM, mylang);
        genRefExpr = new ReferringExpressionsGenerator(mylang);
        SR = new SurfaceRealization(new_models, NLNQM, LQM, MQM, OQM, mylang);
        ILPEn = new ILPEngine(SPQM, MQM, mylang);
    }

    public String[] generateDescription(IRI entityIRI, NLGUser user, int depth, double lamda, int maxSentences, int maxSlotsPerSentence) {
        setLamda(lamda);
        setMaxSentences(maxSentences);
        setMaxSlotsPerSentence(maxSlotsPerSentence);

        return generateDescription(entityIRI, user, depth);
    }

    public String[] generateDescription(IRI entityIRI, NLGUser user, int depth) {
        stagesOutputs = new ArrayList<String>();
        String result[] = new String[3];
        result[0] = "";
        result[1] = "";
        result[2] = "";

        if (user != null && user.getUserModel() != null) {
            if (user.getUserModel().getMaxMessagesPerSentence() != -1) {
                AGGRGT.setMaxMessagesPerSentence(user.getUserModel().getMaxMessagesPerSentence());
            }
        } else {
            user = new NLGUser("default", UMQM.getGlobalUserModel());
            AGGRGT.setMaxMessagesPerSentence(1);
        }

        String textResult = "";

        XmlMsgs messages;
        if (NLGEngine.isClass(manager.getOntologies(), entityIRI)) {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.CLS_TYPE, XmlDocCreator.getNewDocument());
        } else {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.INST_TYPE, XmlDocCreator.getNewDocument());
        }

        //*****CONTENT SELECTION*****CONTENT SELECTION*****
        CS.clearBuffers();
        CS.setNamespaces(messages);

        messages = CS.getMessages(entityIRI, messages, messages.getRoot(), depth, user);

        textResult = textResult + "\n" + "---Content Selection---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        if (messages.getMessages().isEmpty()) {
            System.err.println("No facts could be retrieved!");
        }

        // switch on the assimilation & interest
        // if needed it will switched off later...
        CS.setAssimilationON(true);
        CS.setInterestON(true);

        // call here a function that clear the "dirty" msgs
        /*
        if (mode == 0) {
        messages = CS.removeUselessFacts(messages, messages.getRoot()); //Makis
        
        textResult = textResult + "\n" + "---Remove Dirty---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);
        }*/

        //messages = CS.removeInverseFacts(messages, depth);//Nantia
        //logger.info(messages.getStringDescription(true));

        messages = CS.getTheMostInterestingUnassimilatedFacts(messages, depth, user);

        textResult = textResult + "\n" + "---Content Selection/Most Interesting Not Assimilated Facts---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        boolean enforceILPDecisions = false;
        boolean enforceSlotLimit = false;
        if (messages.getMessages().size() > 0) {
            if (useEngine == NLGEngine.ENGINE_ILP_MODEL) {
                messages = ILPEn.createAndSolveILPNLG(messages, lamda, maxSentences, maxSlotsPerSentence);
                enforceILPDecisions = true;
                enforceSlotLimit = true;
            } else if (useEngine == NLGEngine.ENGINE_ILP_APPROXIMATION_MODEL) {
                messages = ILPEn.createAndApproximateILPNLG(messages, lamda, maxSentences, maxSlotsPerSentence);
                enforceILPDecisions = true;
                enforceSlotLimit = true;
            }
        }

        if (user != null) {
            CS.setMentionedEntity(entityIRI);
            CS.updateUser(user);
        }

        //*****COMPARISONS*****COMPARISONS*****
        if (allowComparisons) {
            findComparisons(entityIRI, user, messages);
        }

        //*****ORDERING*****ORDERING*****
        messages.sortByOrder();

        textResult = textResult + "\n" + "---Ordering---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //logger.info(messages.getStringDescription(true));
        messagesAfterContentSelection = messages;  // store the messages after Content Selection

        //*****LEXICALIZATION*****LEXICALIZATION*****
        messages = LEX.lexicalizeInstances(messages);

        textResult = textResult + "\n" + "---Lexicalisation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****AGGREGATION*****AGGREGATION*****
        if (enforceSlotLimit) {
            messages = AGGRGT.aggregate(messages, enforceILPDecisions, enforceSlotLimit);
        } else {
            messages = AGGRGT.aggregate(messages, enforceILPDecisions, useSlotLimit);
        }

        textResult = textResult + "\n" + "---Aggregation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);
        //logger.info(textResult);

        //*****REFERRING EXPRESSIONS*****REFERRING EXPRESSIONS*****
        messages = genRefExpr.generateReferringExpressions(messages);

        textResult = textResult + "\n" + "---Referring expressions---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****SURFACE REALIZATION*****SURFACE REALIZATION*****
        String RT = SR.realizeMessages(messages, user);

        textResult = textResult + "\n" + "---Surface Realization---" + "\n";
        stagesOutputs.add(RT);
        textResult = textResult + "\n" + RT + "\n";

        //logger.info(textResult);

        result[0] = textResult;
        result[1] = RT;

        SR.getAnnotatedDescription().removeLastPeriod();
        this.AD = SR.getAnnotatedDescription();

        result[2] = AD.getAnnotatedXml();

        return result;
    }

    public String[] generateSingleObjectDescription(IRI entityIRI, IRI propertyIRI, IRI fillerObject, NLGUser user) {
        stagesOutputs = new ArrayList<String>();
        String result[] = new String[3];
        result[0] = "";
        result[1] = "";
        result[2] = "";

        if (user != null && user.getUserModel() != null) {
            if (user.getUserModel().getMaxMessagesPerSentence() != -1) {
                AGGRGT.setMaxMessagesPerSentence(user.getUserModel().getMaxMessagesPerSentence());
            }
        } else {
            user = new NLGUser("default", UMQM.getGlobalUserModel());
            AGGRGT.setMaxMessagesPerSentence(1);
        }

        String textResult = "";

        XmlMsgs messages;
        if (NLGEngine.isClass(manager.getOntologies(), entityIRI)) {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.CLS_TYPE, XmlDocCreator.getNewDocument());
        } else {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.INST_TYPE, XmlDocCreator.getNewDocument());
        }

        //*****CONTENT SELECTION*****CONTENT SELECTION*****
        CS.clearBuffers();
        CS.setNamespaces(messages);

        messages = CS.getSingleObjectMessages(entityIRI, propertyIRI, fillerObject, messages, messages.getRoot(), user);

        textResult = textResult + "\n" + "---Content Selection---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        if (messages.getMessages().isEmpty()) {
            System.err.println("No facts could be retrieved!");
        }

        // switch on the assimilation & interest
        // if needed it will switched off later...
        CS.setAssimilationON(false);
        CS.setInterestON(false);

        textResult = textResult + "\n" + "---Content Selection/Most Interesting Not Assimilated Facts---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //logger.info(messages.getStringDescription(true));
        messagesAfterContentSelection = messages;  // store the messages after Content Selection

        //*****LEXICALIZATION*****LEXICALIZATION*****
        messages = LEX.lexicalizeInstances(messages);

        textResult = textResult + "\n" + "---Lexicalisation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****AGGREGATION*****AGGREGATION*****
        messages = AGGRGT.aggregate(messages, false, false);

        textResult = textResult + "\n" + "---Aggregation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);
        //logger.info(textResult);

        //*****REFERRING EXPRESSIONS*****REFERRING EXPRESSIONS*****
        messages = genRefExpr.generateReferringExpressions(messages);

        textResult = textResult + "\n" + "---Referring expressions---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****SURFACE REALIZATION*****SURFACE REALIZATION*****
        String RT = SR.realizeMessages(messages, user);

        textResult = textResult + "\n" + "---Surface Realization---" + "\n";
        stagesOutputs.add(RT);
        textResult = textResult + "\n" + RT + "\n";

        //logger.info(textResult);

        result[0] = textResult;
        result[1] = RT;

        SR.getAnnotatedDescription().removeLastPeriod();
        this.AD = SR.getAnnotatedDescription();

        result[2] = AD.getAnnotatedXml();

        return result;
    }

    public String[] generateSingleDataDescription(IRI entityIRI, IRI propertyIRI, OWLLiteral filler, NLGUser user) {
        stagesOutputs = new ArrayList<String>();
        String result[] = new String[3];
        result[0] = "";
        result[1] = "";
        result[2] = "";

        if (user != null && user.getUserModel() != null) {
            if (user.getUserModel().getMaxMessagesPerSentence() != -1) {
                AGGRGT.setMaxMessagesPerSentence(user.getUserModel().getMaxMessagesPerSentence());
            }
        } else {
            user = new NLGUser("default", UMQM.getGlobalUserModel());
            AGGRGT.setMaxMessagesPerSentence(1);
        }

        String textResult = "";

        XmlMsgs messages;
        if (NLGEngine.isClass(manager.getOntologies(), entityIRI)) {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.CLS_TYPE, XmlDocCreator.getNewDocument());
        } else {
            messages = new XmlMsgs(entityIRI.toString(), XmlMsgs.INST_TYPE, XmlDocCreator.getNewDocument());
        }

        //*****CONTENT SELECTION*****CONTENT SELECTION*****
        CS.clearBuffers();
        CS.setNamespaces(messages);

        messages = CS.getSingleDataMessages(entityIRI, propertyIRI, filler, messages, messages.getRoot(), user);

        textResult = textResult + "\n" + "---Content Selection---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        if (messages.getMessages().isEmpty()) {
            System.err.println("No facts could be retrieved!");
        }

        // switch on the assimilation & interest
        // if needed it will switched off later...
        CS.setAssimilationON(false);
        CS.setInterestON(false);

        textResult = textResult + "\n" + "---Content Selection/Most Interesting Not Assimilated Facts---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //logger.info(messages.getStringDescription(true));
        messagesAfterContentSelection = messages;  // store the messages after Content Selection

        //*****LEXICALIZATION*****LEXICALIZATION*****
        messages = LEX.lexicalizeInstances(messages);

        textResult = textResult + "\n" + "---Lexicalisation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****AGGREGATION*****AGGREGATION*****
        messages = AGGRGT.aggregate(messages, false, false);

        textResult = textResult + "\n" + "---Aggregation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);
        //logger.info(textResult);

        //*****REFERRING EXPRESSIONS*****REFERRING EXPRESSIONS*****
        messages = genRefExpr.generateReferringExpressions(messages);

        textResult = textResult + "\n" + "---Referring expressions---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****SURFACE REALIZATION*****SURFACE REALIZATION*****
        String RT = SR.realizeMessages(messages, user);

        textResult = textResult + "\n" + "---Surface Realization---" + "\n";
        stagesOutputs.add(RT);
        textResult = textResult + "\n" + RT + "\n";

        //logger.info(textResult);

        result[0] = textResult;
        result[1] = RT;

        SR.getAnnotatedDescription().removeLastPeriod();
        this.AD = SR.getAnnotatedDescription();

        result[2] = AD.getAnnotatedXml();

        return result;
    }

    public String[] generateSpecificDescription(IRI subjectIRI, IRI subjectNLNameIRI, IRI propertyIRI, IRI sentencePlanIRI, IRI objectIRI, IRI objectNLNameIRI, NLGUser user) {
        stagesOutputs = new ArrayList<String>();
        String result[] = new String[3];
        result[0] = "";
        result[1] = "";
        result[2] = "";

        String textResult = "";

        XmlMsgs messages;
        if (NLGEngine.isClass(manager.getOntologies(), subjectIRI)) {
            messages = new XmlMsgs(subjectIRI.toString(), XmlMsgs.CLS_TYPE, XmlDocCreator.getNewDocument());
        } else {
            messages = new XmlMsgs(subjectIRI.toString(), XmlMsgs.INST_TYPE, XmlDocCreator.getNewDocument());
        }

        //*****CONTENT SELECTION*****CONTENT SELECTION*****
        CS.clearBuffers();
        CS.setNamespaces(messages);

        messages = CS.getSpecificMessage(subjectIRI, subjectNLNameIRI, propertyIRI, sentencePlanIRI, objectIRI, objectNLNameIRI, messages, messages.getRoot(), user);

        textResult = textResult + "\n" + "---Content Selection---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        if (messages.getMessages().isEmpty()) {
            System.err.println("No facts could be retrieved!");
        }

        // switch on the assimilation & interest
        // if needed it will switched off later...
        CS.setAssimilationON(false);
        CS.setInterestON(false);

        textResult = textResult + "\n" + "---Content Selection/Most Interesting Not Assimilated Facts---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //logger.info(messages.getStringDescription(true));
        messagesAfterContentSelection = messages;  // store the messages after Content Selection

        //*****LEXICALIZATION*****LEXICALIZATION*****
        messages = LEX.lexicalizeInstances(messages);

        textResult = textResult + "\n" + "---Lexicalisation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****AGGREGATION*****AGGREGATION*****
        messages = AGGRGT.aggregate(messages, false, false);

        textResult = textResult + "\n" + "---Aggregation---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);
        //logger.info(textResult);

        //*****REFERRING EXPRESSIONS*****REFERRING EXPRESSIONS*****
        messages = genRefExpr.generateReferringExpressions(messages);

        textResult = textResult + "\n" + "---Referring expressions---" + "\n";
        stagesOutputs.add(messages.getStringDescription(true));
        textResult = textResult + " " + messages.getStringDescription(true);

        //*****SURFACE REALIZATION*****SURFACE REALIZATION*****
        String RT = SR.realizeMessages(messages, user);

        textResult = textResult + "\n" + "---Surface Realization---" + "\n";
        stagesOutputs.add(RT);
        textResult = textResult + "\n" + RT + "\n";

        //logger.info(textResult);

        result[0] = textResult;
        result[1] = RT;

        SR.getAnnotatedDescription().removeLastPeriod();
        this.AD = SR.getAnnotatedDescription();

        result[2] = AD.getAnnotatedXml();

        return result;
    }

    private void findComparisons(IRI entityIRI, NLGUser user, XmlMsgs messages) {
        if (!isClass(manager.getOntologies(), entityIRI)) {
            ComparisonTree tempTree = new ComparisonTree(comparisonTree);
            ComparisonTree fulltreetemp = new ComparisonTree(fullTree);
            comparisonTree.add(entityIRI);
            boolean entityMentioned = false;

            if (tempTree.contains(entityIRI.toString())) {
                tempTree.removeEntity(entityIRI);
                entityMentioned = true;
            }

            MelegkoglouAlgorithm full = new MelegkoglouAlgorithm(fulltreetemp);
            Comparison comparison = full.detectUnique(comparisonTree.create(entityIRI));

            boolean isASelected = false;
            for (Node property : messages.getMessages()) {
                if (property.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG) || property.getNodeName().equals(XmlMsgs.prefix + ":" + NLResourceManager.instanceOfIndiv)) {
                    isASelected = true;
                }
            }

            if (!isASelected && comparison != null) {
                comparison = null;
            }

            if (comparison == null) {
                fulltreetemp = new ComparisonTree(fullTree);
                full = new MelegkoglouAlgorithm(fulltreetemp);
                comparison = full.compareFullCollection(comparisonTree.create(entityIRI));
            }
            if (comparison == null) {
                fulltreetemp = new ComparisonTree(fullTree);
                full = new MelegkoglouAlgorithm(fulltreetemp);
                comparison = full.compareFullCollectionBlur(comparisonTree.create(entityIRI));
            }

            MelegkoglouAlgorithm mentioned = new MelegkoglouAlgorithm(tempTree);
            if (comparison == null) {
                comparison = mentioned.compare(comparisonTree.create(entityIRI));
            }
            if (comparison == null) {
                comparison = mentioned.compareBlur(comparisonTree.create(entityIRI));
            }

            ArrayList<Node> properties = messages.getMessages();
            if (comparison != null) {
                comparison.setLang(lang);
                for (Node property : properties) {
                    if (comparison.getProperty().equals(XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG)) {
                        if (property.getNodeName().equals(XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG) || property.getNodeName().equals(XmlMsgs.prefix + ":" + NLResourceManager.instanceOfIndiv)) {
                            property.getAttributes().item(0).setNodeValue("false");
                            IRI sentencePlanIRI = IRI.create(XmlMsgs.getAttribute(property, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));
                            IRI sentencePlanUniqueIRI = MQM.chooseUniqueSentencePlan(sentencePlanIRI, SPQM, lang);

                            messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, sentencePlanUniqueIRI.toString());

                            ArrayList<IRI> NLNamesIRIs = new ArrayList<IRI>();
                            IRI NLNameIRI;

                            String NLNameIRIStrings = XmlMsgs.getAttribute(property, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG);
                            boolean and = NLNameIRIStrings.toLowerCase().startsWith("and(");
                            for (int i = 0; i < NLNameIRIStrings.split(",").length; i++) {
                                if (and) {
                                    NLNameIRI = IRI.create(NLNameIRIStrings.substring(4, NLNameIRIStrings.length()).split(",")[i]);
                                    NLNamesIRIs.add(NLNameIRI);
                                } else {
                                    NLNameIRI = IRI.create(NLNameIRIStrings.substring(3, NLNameIRIStrings.length()).split(",")[i]);
                                    NLNamesIRIs.add(NLNameIRI);
                                }
                            }

                            //remove ")" from last NLName
                            IRI newIRI = IRI.create(NLNamesIRIs.get(NLNamesIRIs.size() - 1).toString().substring(0, NLNamesIRIs.get(NLNamesIRIs.size() - 1).toString().length() - 1));
                            NLNamesIRIs.set(NLNamesIRIs.size() - 1, newIRI);

                            ArrayList<IRI> NLNameUniqueIRIs = MQM.chooseUniqueNLName(NLNamesIRIs, NLNQM, lang);
                            String names = "";
                            if (!NLNameUniqueIRIs.isEmpty()) {
                                if (NLNameUniqueIRIs.size() == 1) {
                                    names = NLNameUniqueIRIs.get(0).toString();
                                } else {
                                    if (and) {
                                        names = "and(";
                                    } else {
                                        names = "or(";
                                    }
                                    for (IRI name : NLNameUniqueIRIs) {
                                        names += name.toString() + ",";
                                    }
                                    //delete last "," and close parenthesis
                                    names = names.substring(0, names.length() - 1) + ")";
                                }
                                messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.OBJ_NLNAME_TAG, names);
                            }
                        }
                    } else if (property.getNodeName().equals(comparison.getProperty())) {
                        //dont allow aggregations for comparisons
                        property.getAttributes().item(0).setNodeValue("false");
                        IRI NLNameIRI = MQM.chooseNLName(IRI.create(comparison.getComparator()), NLNQM, lang, user);
                        if (entityMentioned) {
                            if (comparisonTree.comparatorIncludes(entityIRI.toString(), comparison.getComparator())) {
                                entityMentioned = false;
                            }
                        }
                        IRI NLNameCompIRI = MQM.chooseComparisonNLName(NLNameIRI, NLNQM, lang, comparison, entityMentioned);

                        String gen = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                        String number = XmlMsgs.SINGULAR;
                        NLName valueNLName = null;

                        if (!comparison.isSame()) {
                            if (!comparison.getValueIRI().toString().isEmpty()) {
                                IRI value = MQM.chooseNLName(comparison.getValueIRI(), NLNQM, lang, user);
                                valueNLName = NLNQM.getNLName(value);
                                for (NLNSlot slot : valueNLName.getSlotsList()) {
                                    if (slot instanceof NLNArticleSlot) {
                                        if (!((NLNArticleSlot) slot).getGender().isEmpty()) {
                                            gen = ((NLNArticleSlot) slot).getGender();
                                        }
                                        if (!((NLNArticleSlot) slot).getNumber().isEmpty()) {
                                            number = ((NLNArticleSlot) slot).getNumber();
                                        }
                                    } else if (slot instanceof NLNNounSlot) {
                                        if (((NLNNounSlot) slot).isHead()) {
                                            gen = LQM.getNounEntry(((NLNNounSlot) slot).getLexiconEntryIRI(), lang).getGender();
                                            number = LQM.getNounEntry(((NLNNounSlot) slot).getLexiconEntryIRI(), lang).getNumber();
                                        }
                                    }
                                }
                                if (number.equals("both")) {
                                    number = XmlMsgs.SINGULAR;
                                }
                            }
                        }

                        IRI sentencePlanIRI = IRI.create(XmlMsgs.getAttribute(property, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG));
                        IRI sentencePlanCompIRI = MQM.chooseComparisonSentencePlan(sentencePlanIRI, SPQM, lang, comparison, gen, number, SR, NLNameCompIRI);

                        messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.SENTENCE_PLAN_TAG, sentencePlanCompIRI.toString());

                        messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARE_TO, comparison.getComparator());
                        messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARATOR_NLNAME_TAG, NLNameCompIRI.toString());
                        messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARE_TO_FILLER, comparison.getValueIRI().toString());
                        if (valueNLName != null) {
                            messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARATOR_FILLER_NLNAME_TAG, valueNLName.getNLNameIRI().toString());
                        }

                        messages.addStringSlot(property, property.getNodeName(), comparison.getText());

                        gen = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;

                        if (comparison.isMany()) {
                            for (NLNSlot slot : NLNQM.getNLName(NLNameCompIRI).getSlotsList()) {
                                if (slot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) slot).isHead()) {
                                        gen = LQM.getNounEntry(((NLNNounSlot) slot).getLexiconEntryIRI(), lang).getGender();
                                    }
                                }
                            }

                            for (NLNSlot slot : NLNQM.getNLName(NLNameCompIRI).getSlotsList()) {
                                if (slot instanceof NLNArticleSlot) {
                                    ((NLNArticleSlot) slot).setGender(gen);
                                    ((NLNArticleSlot) slot).setNumber(XmlMsgs.PLURAL);
                                    ((NLNArticleSlot) slot).setAgreesWithID(null);
                                    if (comparison.isSame()) {
                                        ((NLNArticleSlot) slot).setCase(XmlMsgs.NOMINATIVE_TAG);
                                    } else {
                                        ((NLNArticleSlot) slot).setCase(XmlMsgs.ACCUSATIVE_TAG);
                                    }
                                    ((NLNArticleSlot) slot).setDefinite(true);
                                }
                            }
                        } else {
                            for (NLNSlot slot : NLNQM.getNLName(NLNameCompIRI).getSlotsList()) {
                                if (slot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) slot).isHead()) {
                                        gen = LQM.getNounEntry(((NLNNounSlot) slot).getLexiconEntryIRI(), lang).getGender();
                                    }
                                } else if (slot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) slot).isHead()) {
                                        gen = LQM.getNounEntry(((NLNNounSlot) slot).getLexiconEntryIRI(), lang).getGender();
                                    }
                                }

                            }
                            for (NLNSlot slot : NLNQM.getNLName(NLNameCompIRI).getSlotsList()) {
                                if (slot instanceof NLNArticleSlot) {
                                    ((NLNArticleSlot) slot).setGender(gen);
                                    ((NLNArticleSlot) slot).setNumber(XmlMsgs.SINGULAR);
                                    ((NLNArticleSlot) slot).setAgreesWithID(null);
                                    ((NLNArticleSlot) slot).setCase(XmlMsgs.NOMINATIVE_TAG);
                                    ((NLNArticleSlot) slot).setDefinite(true);
                                }
                            }
                            messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARE_TO, comparison.getComparator());
                            messages.setAttr(((Element) property), NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.COMPARATOR_NLNAME_TAG, NLNameCompIRI.toString());
                        }
                    }
                }
            }
        }
    }

    public void setLanguage(String lang) {
        this.lang = lang;
        Languages.updateLanguages(CS, LEX, genRefExpr, SR, AGGRGT, lang);
    }

    public String getLang() {
        return this.lang;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    public Set<OWLOntology> getDomainOntologies() {
        return this.manager.getOntologies();
    }

    public Set<IRI> getUserModels() {
        return UMQM.getUserModels();
    }

    public UserModel getUserModel(IRI userModelIRI) {
        return UMQM.getUserModel(userModelIRI);
    }

    public static boolean isClass(Set<OWLOntology> models, IRI entityIRI) {
        for (OWLOntology model : models) {
            if (model.containsClassInSignature(entityIRI, true)) {
                return true;
            }
        }
        return false;
    }

    //Returns the class of an instance
    public static IRI getClassType(OWLOntology model, IRI instanceIRI) {
        if (model == null) {
            return null;
        }

        Set<OWLEntity> entities = model.getEntitiesInSignature(instanceIRI, true);

        for (OWLEntity entity : entities) {
            if (entity.isOWLClass()) {
                OWLClass cls = entity.asOWLClass();

                Set<OWLClassExpression> superCs = cls.getSuperClasses(model);

                for (OWLClassExpression expr : superCs) {
                    if (expr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
                        OWLClass superC = expr.asOWLClass();
                        if (!superC.isAnonymous()) {
                            return superC.getIRI();
                        }
                    }
                }
            } else if (entity.isOWLNamedIndividual()) {
                OWLNamedIndividual ind = entity.asOWLNamedIndividual();

                Set<OWLClassExpression> superCs = ind.getTypes(model);

                for (OWLClassExpression expr : superCs) {
                    if (expr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
                        OWLClass superC = expr.asOWLClass();
                        if (!superC.isAnonymous()) {
                            return superC.getIRI();
                        }
                    }
                }
            } else {
                return null;
            }
        }

        return null;
    }

    public static ArrayList<IRI> getSuperClasses(IRI indivOrClassIRI, OWLClassExpression expression) {
        ArrayList<IRI> superClasses = new ArrayList<IRI>();

        if (expression.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
            if (!expression.asOWLClass().getIRI().equals(indivOrClassIRI)) {
                superClasses.add(expression.asOWLClass().getIRI());
            }
        } else if (expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
            OWLObjectIntersectionOf intersectionOfExpr = (OWLObjectIntersectionOf) expression;

            for (OWLClassExpression conjunctExpr : intersectionOfExpr.asConjunctSet()) {
                superClasses.addAll(getSuperClasses(indivOrClassIRI, conjunctExpr));
            }
        } else if (expression.getClassExpressionType().equals(ClassExpressionType.OBJECT_UNION_OF)) {
            OWLObjectUnionOf unionOfExpr = (OWLObjectUnionOf) expression;

            for (OWLClassExpression disjunctExpr : unionOfExpr.asDisjunctSet()) {
                superClasses.addAll(getSuperClasses(indivOrClassIRI, disjunctExpr));
            }
        }

        return superClasses;
    }

    public String[][] getFactsAndAssimilations() {

        ArrayList<?> msgs = messagesAfterContentSelection.getMessages();
        String[][] array = new String[msgs.size()][2];

        for (int i = 0; i < msgs.size(); i++) {

            Node msg = (Node) msgs.get(i);

            String sub = XmlMsgs.getAttribute(msg, XmlMsgs.prefix, XmlMsgs.REF);
            String obj = XmlMsgs.getAttribute(msg, XmlMsgs.prefix, XmlMsgs.VALUE);
            String pred = "";
            String score = XmlMsgs.getAttribute(msg, XmlMsgs.prefix, XmlMsgs.ASSIMIL_SCORE);

            if (msg.getNodeName().equals("nlowl:type")) {
                pred = org.semanticweb.owlapi.rdf.util.RDFConstants.RDF_TYPE;
            } else {
                pred = msg.getNamespaceURI() + msg.getLocalName();
            }

            array[i][0] = "[" + sub + ", " + pred + ", " + obj + "]";
            array[i][1] = score;
        }

        return array;
    }

    public AnnotatedDescription getAnnotatedText() {
        return this.AD;
    }

    public SentencePlanQueryManager getSentencePlanQueryManager() {
        return this.SPQM;
    }

    public NLNameQueryManager getNLNameQueryManager() {
        return this.NLNQM;
    }

    public MappingQueryManager getMappingQueryManager() {
        return this.MQM;
    }

    public LexiconQueryManager getLexiconQueryManager() {
        return this.LQM;
    }

    public void setUseEngine(int engine) {
        this.useEngine = engine;
    }

    public void setUseSlotLimit(boolean use) {
        this.useSlotLimit = use;
    }

    public void setMaxSentences(int max) {
        this.maxSentences = max;
    }

    public void setMaxSlotsPerSentence(int max) {
        this.maxSlotsPerSentence = max;
        this.AGGRGT.setMaxSlotsPerSentence(max);
    }

    public void setLamda(double l) {
        this.lamda = l;
    }

    public void setSectionParagraphs(boolean sp) {
        SR.setSectionParagraphs(sp);
    }

    public void setOverrideMaxMessagesPerSentence(int value) {
        this.AGGRGT.setOverrideMaxMessagesPerSentence(value);
    }

    public void setAllowComparisons(boolean value) {
        allowComparisons = value;
    }

    public boolean isUniqueIRI(IRI entryIRI) {
        if (DefaultResourcesManager.isDefaultResource(entryIRI)) {
            return false;
        }
        if (LQM.isNoun(entryIRI)) {
            return false;
        }
        if (LQM.isAdjective(entryIRI)) {
            return false;
        }
        if (LQM.isVerb(entryIRI)) {
            return false;
        }
        if (SPQM.getSentencePlansList(Languages.ENGLISH).containsSentencePlan(entryIRI)) {
            return false;
        }
        if (SPQM.getSentencePlansList(Languages.GREEK).containsSentencePlan(entryIRI)) {
            return false;
        }
        if (NLNQM.getNLNamesList(Languages.ENGLISH).containsNLName(entryIRI)) {
            return false;
        }
        if (NLNQM.getNLNamesList(Languages.GREEK).containsNLName(entryIRI)) {
            return false;
        }
        if (OQM.getOrderedSections().contains(entryIRI)) {
            return false;
        }
        if (UMQM.getUserModels().contains(entryIRI)) {
            return false;
        }

        return true;
    }
}
