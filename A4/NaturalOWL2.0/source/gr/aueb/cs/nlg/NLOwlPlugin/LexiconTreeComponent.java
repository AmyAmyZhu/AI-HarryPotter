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
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLNNounSlot;
import gr.aueb.cs.nlg.NLFiles.NLNSlot;
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.SPAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.SPNounSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SPVerbSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlan;

import org.semanticweb.owlapi.model.IRI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.TreeSelectionEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;

public class LexiconTreeComponent extends TreeComponent {
	
	JPanel complete;
	
	DefaultMutableTreeNode parent;
	DefaultMutableTreeNode nounParent;
	DefaultMutableTreeNode adjectiveParent;
	DefaultMutableTreeNode verbParent;
	
	public LexiconTreeComponent(NaturalOWLTab fatherTab)
	{
		super(fatherTab);
	}
	
	public void createTree() {
		parent = new DefaultMutableTreeNode("Lexicon Entries", true);
		
		adjectiveParent = new DefaultMutableTreeNode("Adjectives", true);
		parent.add(adjectiveParent);
		
		Iterator<IRI> it = NaturalOWLTab.LQM.getAdjectivesIRIs();
		ArrayList<ListIRI> toAdd = new ArrayList<ListIRI>();
		while (it.hasNext())
		{
			IRI s = it.next();
			if (!DefaultResourcesManager.isDefaultResource(s)) {
				toAdd.add(new ListIRI(s));
			}
		}
		
		Collections.sort(toAdd);
		for (ListIRI add : toAdd)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, false);
			adjectiveParent.add(node);
		}
		
		nounParent = new DefaultMutableTreeNode("Nouns", true);
		parent.add(nounParent);
		
		it = NaturalOWLTab.LQM.getNounsIRIs();

		toAdd = new ArrayList<ListIRI>();
		while (it.hasNext())
		{
			IRI s = it.next();

			if (!DefaultResourcesManager.isDefaultResource(s)) {
				toAdd.add(new ListIRI(s));
			}
		}
		
		Collections.sort(toAdd);
		for (ListIRI add : toAdd)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, false);
			nounParent.add(node);
		}
		
		verbParent = new DefaultMutableTreeNode("Verbs", true);
		parent.add(verbParent);
		
		it = NaturalOWLTab.LQM.getVerbsIRIs();

		toAdd = new ArrayList<ListIRI>();
		while (it.hasNext())
		{
			IRI s = it.next();

			if (!DefaultResourcesManager.isDefaultResource(s)) {
				toAdd.add(new ListIRI(s));
			}
		}
		
		Collections.sort(toAdd);
		for (ListIRI add : toAdd)
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, false);
			verbParent.add(node);
		}
		
		tree = new JTree(parent);
		tree.setEditable(false);
		tree.setRootVisible(false);
		
		tree.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent evt) {
	            try {
	            	//if right click
	            	if (evt.getButton() == MouseEvent.BUTTON3) {
		                treePopupMenu(evt);
		            }
	            }catch (Exception e) {}
	        }
	    });
	}
	
	public void treePopupMenu(MouseEvent evt) {
		TreePath path = tree.getPathForLocation(evt.getX(), evt.getY());
		if (path != null) {
		    tree.setSelectionPath(path);
		} else {
		    return;
		}
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		JPopupMenu popup = new JPopupMenu();
		popup.setInvoker(tree);
		if (!node.equals(parent)) {
			if(!isEntry(node))
			{
				JMenuItem newNLN = new JMenuItem("Create new " + ((String) node.getUserObject()).substring(0, ((String) node.getUserObject()).length() - 1) + " Lexicon Entry");
				newNLN.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						NewResourceDialog dlg = new NewResourceDialog("New Lexicon Entry", treeCmp, true, "");
						if(dlg.getResponse()){ //if not Cancel
							String name = dlg.getName();
							IRI entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
							
							while (name.equals("") || !father.isLegalIRI(entryIRI) || !father.isUniqueIRI(entryIRI)) {
								JOptionPane.showMessageDialog(null,
									    "The name you input is invalid or already exists. Please choose a different name.",
									    "Invalid entity name",
									    JOptionPane.ERROR_MESSAGE);

								dlg = new NewResourceDialog("New Lexicon Entry", treeCmp, true, "");
								if(dlg.getResponse()){ //if not Cancel
									name = dlg.getName();
									entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
								} else {
									return;
								}
							}
							
							createNewNode(node, entryIRI);
						}
					}
				});					
				popup.add(newNLN);
			}
			else 
			{
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						ListIRI oldName = (ListIRI)node.getUserObject();
						String startNS = oldName.getEntryIRI().getStart();
						
						NewResourceDialog dlg = new NewResourceDialog("Rename Lexicon Entry", treeCmp, true, oldName.toString());
						if(dlg.getResponse()){ //if not Cancel
							String name = dlg.getName();
							IRI toIRI = IRI.create(startNS + name);
							
							while (name.equals("") || !father.isLegalIRI(toIRI) || !father.isUniqueIRI(toIRI)) {
								JOptionPane.showMessageDialog(null,
									    "The name you input is invalid or already exists. Please choose a different name.",
									    "Invalid entity name",
									    JOptionPane.ERROR_MESSAGE);

								dlg = new NewResourceDialog("Rename Lexicon Entry", treeCmp, true, name);
								if(dlg.getResponse()){ //if not Cancel
									name = dlg.getName();
									toIRI = IRI.create(startNS + name);
								} else {
									return;
								}
							}
							
							NaturalOWLTab.LQM.duplicateEntryInLexicon(oldName.getEntryIRI(), toIRI);
							NaturalOWLTab.LQM.deleteLexiconEntry(oldName.getEntryIRI());
							
							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
							
							//Rename Lexicon Entry in Sentence Plans that use it!
							ArrayList<SentencePlan> sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).getSentencePlansList();
							for (SentencePlan plan : sentencePlansList) {
								for (SPSlot slot : plan.getSlotsList()) {
									if (nodeParent.equals(adjectiveParent) && (slot instanceof SPAdjectiveSlot)) {
										if (((SPAdjectiveSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPAdjectiveSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(nounParent) && (slot instanceof SPNounSlot)) {
										if (((SPNounSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPNounSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(verbParent) && (slot instanceof SPVerbSlot)) {
										if (((SPVerbSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPVerbSlot)slot).setLexiconEntryIRI(toIRI);							
										}
									}
								}
							}
							
							sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).getSentencePlansList();
							for (SentencePlan plan : sentencePlansList) {		
								for (SPSlot slot : plan.getSlotsList()) {
									if (nodeParent.equals(adjectiveParent) && (slot instanceof SPAdjectiveSlot)) {
										if (((SPAdjectiveSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPAdjectiveSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(nounParent) && (slot instanceof SPNounSlot)) {
										if (((SPNounSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPNounSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(verbParent) && (slot instanceof SPVerbSlot)) {
										if (((SPVerbSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((SPVerbSlot)slot).setLexiconEntryIRI(toIRI);
										}
									}
								}
							}
							
							//Rename Lexicon Entry in NLNames that use it!
							ArrayList<NLName> nlNamesList = NaturalOWLTab.NLNQM.getNLNamesList(Languages.ENGLISH).getNLNamesList();
							for (NLName nlName : nlNamesList) {	
								for (NLNSlot slot : nlName.getSlotsList()) {
									if (nodeParent.equals(adjectiveParent) && (slot instanceof NLNAdjectiveSlot)) {
										if (((NLNAdjectiveSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((NLNAdjectiveSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(nounParent) && (slot instanceof NLNNounSlot)) {
										if (((NLNNounSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((NLNNounSlot)slot).setLexiconEntryIRI(toIRI);
										}
									}
								}
							}
							
							nlNamesList = NaturalOWLTab.NLNQM.getNLNamesList(Languages.GREEK).getNLNamesList();
							for (NLName nlName : nlNamesList) {	
								for (NLNSlot slot : nlName.getSlotsList()) {
									if (nodeParent.equals(adjectiveParent) && (slot instanceof NLNAdjectiveSlot)) {
										if (((NLNAdjectiveSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((NLNAdjectiveSlot)slot).setLexiconEntryIRI(toIRI);
										}
									} else if (nodeParent.equals(nounParent) && (slot instanceof NLNNounSlot)) {
										if (((NLNNounSlot)slot).getLexiconEntryIRI().equals(oldName.getEntryIRI())) {
											((NLNNounSlot)slot).setLexiconEntryIRI(toIRI);
										}
									}
								}
							}

							DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
							model.removeNodeFromParent(node);
							
							int pos = 0;
							for (int i = 0; i < model.getChildCount(nodeParent); i++) {
								if (((ListIRI)((DefaultMutableTreeNode)model.getChild(nodeParent, i)).getUserObject()).getEntryIRI().compareTo(toIRI) < 0) {
									pos++;
								}
							}
							DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
							model.insertNodeInto(newNode, nodeParent, pos);
							father.dirtenOntologies();
						}
					}
				});					
				JMenuItem duplicate = new JMenuItem("Duplicate");
				duplicate.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {					
						duplicateNode(node, "", "");										
					}
				});
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						int selection = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this Lexicon Entry?");
						if (selection == JOptionPane.OK_OPTION)
							deleteNode(node);
					}
				});
				popup.add(rename);
				popup.add(duplicate);
				popup.add(delete);
			}
			popup.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
	
	
	public JPanel TP() {
		// JFrame frame = new JFrame("Creating a JTree Component!");
		JPanel complete = new JPanel();
		JPanel jp = new JPanel();
		JPanel treepanel = new JPanel();

		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		complete.setLayout(new BoxLayout(complete, BoxLayout.Y_AXIS));
		JButton buttonNew = new JButton("New Lexicon Entry");
		buttonNew.addActionListener(this);

		createTree();
		jp.add(buttonNew);
		jp.setBackground(new Color(255,255,255));
		treepanel.add(tree);
		treepanel.setBackground(new Color(255,255,255));
		treepanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(this);

		ClassLoader loader = LexiconTreeComponent.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/LexiconFolder.gif");
		ImageIcon folderIcon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(folderIcon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(folderIcon);
		imageURL = loader.getResource("/icons/Lexicon.png");
		ImageIcon leafIcon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(leafIcon);

		JScrollPane scrollPane = new JScrollPane(treepanel);
		
		complete.add(jp);
        complete.add(scrollPane);        
		complete.setBackground(new Color(255,255,255));
		
		return complete;
	}
	
	
	public void addNode() {
		NewResourceDialog dlg = new NewResourceDialog("New Lexicon entry", treeCmp, false, "");		
		if(dlg.getResponse()){ //if not Cancel
			String name = dlg.getName();
			String type = dlg.getSecondSelected();
			IRI entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
			
			while (name.equals("") || !father.isLegalIRI(entryIRI) || !father.isUniqueIRI(entryIRI)) {
				JOptionPane.showMessageDialog(null,
					    "The name you input is invalid or already exists. Please choose a different name.",
					    "Invalid entity name",
					    JOptionPane.ERROR_MESSAGE);
						
				dlg = new NewResourceDialog("New Lexicon entry", treeCmp, false, name);		
				if(dlg.getResponse()){ //if not Cancel
					name = dlg.getName();
					type = dlg.getSecondSelected();
					entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
				} else {
					return;
				}
			}
			
			if(type.equals("Adjective")){
				createNewNode(adjectiveParent, entryIRI);
			}
			else if(type.equals("Noun")){
				createNewNode(nounParent, entryIRI);
			}
			else if(type.equals("Verb")){
				createNewNode(verbParent, entryIRI);	
			}	
		}
	}
		
	public void createNewNode(DefaultMutableTreeNode nodeParent, IRI name){
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(name), false);
		
		if (nodeParent.equals(adjectiveParent)){
			NaturalOWLTab.LQM.addAdjectiveEntryToLexicon(name);
		}
		else if(nodeParent.equals(nounParent)){
			NaturalOWLTab.LQM.addNounEntryToLexicon(name);
		}
		else{
			NaturalOWLTab.LQM.addVerbEntryToLexicon(name);
		}

		int pos = 0;
		for (int i = 0; i < model.getChildCount(nodeParent); i++) {
			if (((ListIRI)((DefaultMutableTreeNode)model.getChild(nodeParent, i)).getUserObject()).getEntryIRI().compareTo(name) < 0) {
				pos++;
			}
		}
		model.insertNodeInto(newNode, nodeParent, pos);
		
		TreePath selectedPath = new TreePath(model.getPathToRoot(newNode));
		tree.setSelectionPath(selectedPath);
		tree.scrollPathToVisible(selectedPath);
		
		father.dirtenOntologies();
	}
	
	
	public void deleteNode(DefaultMutableTreeNode node) {
		if (isEntry(node)) {
			IRI toDeleteIRI = ((ListIRI)node.getUserObject()).getEntryIRI();
			
			//Check if Lexicon Entry is used in any Sentence Plans
			ArrayList<SentencePlan> sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).getSentencePlansList();
			for (SentencePlan plan : sentencePlansList) {
				for (SPSlot slot : plan.getSlotsList()) {
					if (node.getParent().equals(adjectiveParent) && (slot instanceof SPAdjectiveSlot)) {
						if (((SPAdjectiveSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(nounParent) && (slot instanceof SPNounSlot)) {
						if (((SPNounSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(verbParent) && (slot instanceof SPVerbSlot)) {
						if (((SPVerbSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}
			
			sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).getSentencePlansList();
			for (SentencePlan plan : sentencePlansList) {		
				for (SPSlot slot : plan.getSlotsList()) {
					if (node.getParent().equals(adjectiveParent) && (slot instanceof SPAdjectiveSlot)) {
						if (((SPAdjectiveSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(nounParent) && (slot instanceof SPNounSlot)) {
						if (((SPNounSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(verbParent) && (slot instanceof SPVerbSlot)) {
						if (((SPVerbSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in Sentence Plan " + plan.getSentencePlanIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}
			
			//Check if Lexicon Entry is used in any NLNames
			ArrayList<NLName> nlNamesList = NaturalOWLTab.NLNQM.getNLNamesList(Languages.ENGLISH).getNLNamesList();
			for (NLName nlName : nlNamesList) {	
				for (NLNSlot slot : nlName.getSlotsList()) {
					if (node.getParent().equals(adjectiveParent) && (slot instanceof NLNAdjectiveSlot)) {
						if (((NLNAdjectiveSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in NLName " + nlName.getNLNameIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(nounParent) && (slot instanceof NLNNounSlot)) {
						if (((NLNNounSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in NLName " + nlName.getNLNameIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}
			
			nlNamesList = NaturalOWLTab.NLNQM.getNLNamesList(Languages.GREEK).getNLNamesList();
			for (NLName nlName : nlNamesList) {	
				for (NLNSlot slot : nlName.getSlotsList()) {
					if (node.getParent().equals(adjectiveParent) && (slot instanceof NLNAdjectiveSlot)) {
						if (((NLNAdjectiveSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in NLName " + nlName.getNLNameIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else if (node.getParent().equals(nounParent) && (slot instanceof NLNNounSlot)) {
						if (((NLNNounSlot)slot).getLexiconEntryIRI().equals(toDeleteIRI)) {
							JOptionPane.showMessageDialog(null,
								    "Cannot delete this lexicon entry, it is used in NLName " + nlName.getNLNameIRI(),
								    "Lexicon Entry is used.",
								    JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
				}
			}
			
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			NaturalOWLTab.LQM.deleteLexiconEntry(toDeleteIRI);
			
			if (node.getParent().getIndex(node) + 1 < node.getParent().getChildCount()) {
				TreePath selectedPath = new TreePath(model.getPathToRoot(node.getParent().getChildAt(node.getParent().getIndex(node) + 1)));
				tree.setSelectionPath(selectedPath);
				tree.scrollPathToVisible(selectedPath);
			} else if (node.getParent().getIndex(node) - 1 < node.getParent().getChildCount() && node.getParent().getIndex(node) > 0) {
				TreePath selectedPath = new TreePath(model.getPathToRoot(node.getParent().getChildAt(node.getParent().getIndex(node) - 1)));
				tree.setSelectionPath(selectedPath);
				tree.scrollPathToVisible(selectedPath);
			}
			
			model.removeNodeFromParent(node);
			
			father.dirtenOntologies();
		}
	}

	public void duplicateNode(DefaultMutableTreeNode node, String fromLanguage, String toLanguage) {
		if (isEntry(node)) {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			
			IRI fromIRI = ((ListIRI)node.getUserObject()).getEntryIRI();
			
			String toIRISuffix = "_copy";
			IRI toIRI = IRI.create(fromIRI.toString() + toIRISuffix);

			int i = 2;
			while (!father.isUniqueIRI(toIRI)) {
				toIRISuffix = "_copy" + i;
				toIRI = IRI.create(fromIRI.toString() + toIRISuffix);
				i++;
			}
			
			int pos = 0;
			if (NaturalOWLTab.LQM.isNoun(fromIRI)){
				NaturalOWLTab.LQM.duplicateEntryInLexicon(fromIRI, toIRI);
				
				pos = nounParent.getIndex(node) + 1;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
				model.insertNodeInto(newNode, nounParent, pos);
			}
			else if (NaturalOWLTab.LQM.isAdjective(fromIRI)){
				NaturalOWLTab.LQM.duplicateEntryInLexicon(fromIRI, toIRI);

				pos = adjectiveParent.getIndex(node) + 1;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
				model.insertNodeInto(newNode, adjectiveParent, pos);
			}
			else if (NaturalOWLTab.LQM.isVerb(fromIRI)){
				NaturalOWLTab.LQM.duplicateEntryInLexicon(fromIRI, toIRI);

				pos = verbParent.getIndex(node) + 1;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
				model.insertNodeInto(newNode, verbParent, pos);
			}			
			father.dirtenOntologies();
		}
	}
	
	public boolean isEntry(DefaultMutableTreeNode node){		
		if (node.isLeaf() && !node.equals(nounParent) && !node.equals(adjectiveParent) && !node.equals(verbParent) && !node.equals(parent)){
			return true;
		}
		return false;
	}
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	    if (node == null)    
	    	return;

	    if (isEntry(node)) {
		    NaturalOWLTab.lexiconSelectionModel.setSelectedEntity(NaturalOWLTab.LQM.getNLResourcesManager().getDataFactory().getOWLNamedIndividual(((ListIRI)node.getUserObject()).getEntryIRI()));
	    }
	}
}