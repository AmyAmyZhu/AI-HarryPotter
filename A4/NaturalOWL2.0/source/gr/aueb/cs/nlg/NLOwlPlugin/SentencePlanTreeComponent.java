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

public class SentencePlanTreeComponent extends TreeComponent {

	JPanel complete;

	DefaultMutableTreeNode parent;
	DefaultMutableTreeNode englishParent;
	DefaultMutableTreeNode greekParent;

	public SentencePlanTreeComponent(NaturalOWLTab fatherTab) {
		super(fatherTab);
	}

	public void createTree() {
		parent = new DefaultMutableTreeNode("Sentence Plans", true);
		
		englishParent = new DefaultMutableTreeNode("English Sentence Plans", true);
		parent.add(englishParent);
		
		ArrayList<SentencePlan> sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).getSentencePlansList();
		
        Collections.sort(sentencePlansList);
		for (SentencePlan plan : sentencePlansList) {
			if (!plan.getSentencePlanIRI().toString().startsWith(NLResourceManager.nlowlNS) || (DefaultResourcesManager.isDefaultResource(plan.getSentencePlanIRI()) && plan.getSentencePlanIRI().toString().startsWith(NLResourceManager.nlowlNS))) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ListIRI(plan.getSentencePlanIRI()), false);
				englishParent.add(node);
			}
		}
		
		greekParent = new DefaultMutableTreeNode("Greek Sentence Plans", true);
		parent.add(greekParent);
		
		sentencePlansList = NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).getSentencePlansList();

        Collections.sort(sentencePlansList);
		for (SentencePlan plan : sentencePlansList) {		
			if (!plan.getSentencePlanIRI().toString().startsWith(NLResourceManager.nlowlNS) || (DefaultResourcesManager.isDefaultResource(plan.getSentencePlanIRI()) && plan.getSentencePlanIRI().toString().startsWith(NLResourceManager.nlowlNS))) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ListIRI(plan.getSentencePlanIRI()), false);
				greekParent.add(node);
			}
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
	            		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();	            		
	            		if (!((ListIRI)node.getUserObject()).toString().startsWith("<")) {
	            			treePopupMenu(evt);
	            		}
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
				JMenuItem newSP = new JMenuItem("Create new " + ((String) node.getUserObject()).substring(0, ((String) node.getUserObject()).length() - 1));
				newSP.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						NewResourceDialog dlg = new NewResourceDialog("New Sentence Plan", treeCmp, true, "");
						if(dlg.getResponse()){ //if not Cancel
							String name = dlg.getName();
							IRI entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
							
							while (name.equals("") || !father.isLegalIRI(entryIRI) || !father.isUniqueIRI(entryIRI)) {
								JOptionPane.showMessageDialog(null,
									    "The name you input is invalid or already exists. Please choose a different name.",
									    "Invalid entity name",
									    JOptionPane.ERROR_MESSAGE);

								dlg = new NewResourceDialog("New Sentence Plan", treeCmp, true, "");
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

				JMenuItem duplicateInOtherLanguage = null;
				if (node.equals(englishParent)){
					duplicateInOtherLanguage = new JMenuItem("Duplicate all in Greek");
					duplicateInOtherLanguage.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							for (int i = 0; i < englishParent.getChildCount(); i++) {
								duplicateNode((DefaultMutableTreeNode) englishParent.getChildAt(i), Languages.ENGLISH, Languages.GREEK);
							}
						}
					});
				} else if (node.equals(greekParent)){
					duplicateInOtherLanguage = new JMenuItem("Duplicate all in English");
					duplicateInOtherLanguage.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							duplicateNode(node, Languages.GREEK, Languages.ENGLISH);										
						}
					});
				}
				
				popup.add(newSP);
				if (duplicateInOtherLanguage != null) {
					popup.add(duplicateInOtherLanguage);
				}
			}
			else 
			{
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						ListIRI oldName = (ListIRI)node.getUserObject();
						String startNS = oldName.getEntryIRI().getStart();
						
						NewResourceDialog dlg = new NewResourceDialog("Rename Sentence Plan", treeCmp, true, oldName.toString());
						if(dlg.getResponse()){ //if not Cancel
							String name = dlg.getName();
							IRI toIRI = IRI.create(startNS + name);
							
							while (name.equals("") || !father.isLegalIRI(toIRI) || !father.isUniqueIRI(toIRI)) {
								JOptionPane.showMessageDialog(null,
									    "The name you input is invalid or already exists. Please choose a different name.",
									    "Invalid entity name",
									    JOptionPane.ERROR_MESSAGE);

								dlg = new NewResourceDialog("Rename Sentence Plan", treeCmp, true, name);
								if(dlg.getResponse()){ //if not Cancel
									name = dlg.getName();
									toIRI = IRI.create(startNS + name);
								} else {
									return;
								}
							}

							if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).containsSentencePlan(oldName.getEntryIRI())){
								NaturalOWLTab.SPQM.duplicateSentencePlanInLists(oldName.getEntryIRI(), toIRI, Languages.ENGLISH);
							} else if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).containsSentencePlan(oldName.getEntryIRI())){
								NaturalOWLTab.SPQM.duplicateSentencePlanInLists(oldName.getEntryIRI(), toIRI, Languages.GREEK);								
							}
							
							NaturalOWLTab.SPQM.removeSentencePlan(oldName.getEntryIRI());
							
							NaturalOWLTab.MQM.renameSentencePlan(oldName.getEntryIRI(), toIRI);
							NaturalOWLTab.UMQM.renameSentencePlan(oldName.getEntryIRI(), toIRI);

							DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
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
							
							TreePath selectedPath = new TreePath(model.getPathToRoot(newNode));
							tree.setSelectionPath(selectedPath);
							tree.scrollPathToVisible(selectedPath);
														
							father.dirtenOntologies();
						}					
					}
				});		
				
				IRI entryIRI = ((ListIRI)node.getUserObject()).getEntryIRI();

				JMenuItem duplicate = null;
				if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).containsSentencePlan(entryIRI)){
					duplicate = new JMenuItem("Duplicate");
					duplicate.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							duplicateNode(node, Languages.ENGLISH, Languages.ENGLISH);										
						}
					});
				} else if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).containsSentencePlan(entryIRI)){
					duplicate = new JMenuItem("Duplicate");
					duplicate.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							duplicateNode(node, Languages.GREEK, Languages.GREEK);										
						}
					});
				}
				
				JMenuItem duplicateInOtherLanguage = null;
				if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.ENGLISH).containsSentencePlan(entryIRI)){
					duplicateInOtherLanguage = new JMenuItem("Duplicate in Greek");
					duplicateInOtherLanguage.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							duplicateNode(node, Languages.ENGLISH, Languages.GREEK);										
						}
					});
				} else if (NaturalOWLTab.SPQM.getSentencePlansList(Languages.GREEK).containsSentencePlan(entryIRI)){
					duplicateInOtherLanguage = new JMenuItem("Duplicate in English");
					duplicateInOtherLanguage.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							duplicateNode(node, Languages.GREEK, Languages.ENGLISH);										
						}
					});
				}
				
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						int selection = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this Sentence Plan?");
						if (selection == JOptionPane.OK_OPTION) {
							deleteNode(node);
						}
					}
				});
				popup.add(rename);
				if (duplicate != null) {
					popup.add(duplicate);
				}
				if (duplicateInOtherLanguage != null) {
					popup.add(duplicateInOtherLanguage);
				}
				popup.add(delete);
			}
			popup.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	public JPanel TP() {
		JPanel complete = new JPanel();
		JPanel jp = new JPanel();
		JPanel treepanel = new JPanel();

		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		complete.setLayout(new BoxLayout(complete, BoxLayout.Y_AXIS));
		JButton buttonNew = new JButton("New Sentence Plan");
		buttonNew.addActionListener(this);

		createTree();
		jp.add(buttonNew);
		jp.setBackground(new Color(255, 255, 255));
		treepanel.add(tree);
		treepanel.setBackground(new Color(255, 255, 255));
		treepanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(this);

		ClassLoader loader = SentencePlanTreeComponent.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/SentencePlanFolder.gif");
		ImageIcon folderIcon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(folderIcon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(folderIcon);
		imageURL = loader.getResource("/icons/SentencePlan.png");
		ImageIcon leafIcon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(leafIcon);

		JScrollPane scrollPane = new JScrollPane(treepanel);
		
		complete.add(jp);
        complete.add(scrollPane);
		complete.setBackground(new Color(255, 255, 255));

		return complete;
	}

	public void addNode() {
		NewResourceDialog dlg = new NewResourceDialog("New Sentence Plan", treeCmp, false, "");
		if (dlg.getResponse()) { // if not Cancel
			String name = dlg.getName();
			String type = dlg.getSecondSelected();
			IRI entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
			
			while (name.equals("") || !father.isLegalIRI(entryIRI) || !father.isUniqueIRI(entryIRI)) {
				JOptionPane.showMessageDialog(null,
					    "The name you input is invalid or already exists. Please choose a different name.",
					    "Invalid entity name",
					    JOptionPane.ERROR_MESSAGE);
						
				dlg = new NewResourceDialog("New Sentence Plan", treeCmp, false, name);		
				if(dlg.getResponse()){ //if not Cancel
					name = dlg.getName();
					type = dlg.getSecondSelected();
					entryIRI = IRI.create(NLResourceManager.resourcesNS + name);
				} else {
					return;
				}
			}
			
			if(type.equals("English")){
				createNewNode(englishParent, entryIRI);
			}
			else if(type.equals("Greek")){
				createNewNode(greekParent, entryIRI);
			}
		}
	}
	
	public void createNewNode(DefaultMutableTreeNode nodeParent, IRI name){
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(name), false);
		if (nodeParent.equals(englishParent)){
			NaturalOWLTab.SPQM.addSentencePlan(name, Languages.ENGLISH);
		}
		else{
			NaturalOWLTab.SPQM.addSentencePlan(name, Languages.GREEK);
		}
		NaturalOWLTab.UMQM.addSentencePlan(name);
		
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
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

			NaturalOWLTab.SPQM.removeSentencePlan(((ListIRI)node.getUserObject()).getEntryIRI());
			NaturalOWLTab.MQM.deleteSentencePlan(((ListIRI)node.getUserObject()).getEntryIRI());
			NaturalOWLTab.UMQM.deleteSentencePlan(((ListIRI)node.getUserObject()).getEntryIRI());			

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

			IRI toIRI = null;

			if (fromLanguage.equals(Languages.ENGLISH) && toLanguage.equals(Languages.GREEK)) {
				if (fromIRI.toString().endsWith("_EN")) {
					toIRI =  IRI.create(fromIRI.toString().substring(0, fromIRI.toString().length() - 2) + "GR");
				}
			} else if (fromLanguage.equals(Languages.GREEK) && toLanguage.equals(Languages.ENGLISH)) {
				if (fromIRI.toString().endsWith("_GR")) {
					toIRI =  IRI.create(fromIRI.toString().substring(0, fromIRI.toString().length() - 2) + "EN");
				}
			}
			if ((toIRI == null) || !father.isUniqueIRI(toIRI)) {
				String toIRISuffix = "_copy";
				toIRI = IRI.create(fromIRI.toString() + toIRISuffix);
	
				int i = 2;
				while (!father.isUniqueIRI(toIRI)) {
					toIRISuffix = "_copy" + i;
					toIRI = IRI.create(fromIRI.toString() + toIRISuffix);
					i++;
				}
			}

			int pos = 0;
			NaturalOWLTab.SPQM.duplicateSentencePlanInLists(fromIRI, toIRI, toLanguage);
			if (toLanguage.equals(Languages.ENGLISH)) {				
				pos = englishParent.getIndex(node) + 1;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
				model.insertNodeInto(newNode, englishParent, pos);
				
				TreePath selectedPath = new TreePath(model.getPathToRoot(newNode));
				tree.setSelectionPath(selectedPath);
				tree.scrollPathToVisible(selectedPath);
			} else if (toLanguage.equals(Languages.GREEK)) {
				pos = greekParent.getIndex(node) + 1;
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ListIRI(toIRI), false);
				model.insertNodeInto(newNode, greekParent, pos);
				
				TreePath selectedPath = new TreePath(model.getPathToRoot(newNode));
				tree.setSelectionPath(selectedPath);
				tree.scrollPathToVisible(selectedPath);
			}

			if (SentencePlanTab.MQM.getIndividualOrClassSet(fromIRI) != null) {
				for (IRI entryIRI : SentencePlanTab.MQM.getPropertiesSet(fromIRI)) {
					SentencePlanTab.MQM.addSentencePlanMapping(entryIRI, toIRI);			
		        }
			}
			
			for (IRI userType : SentencePlanTab.UMQM.getUserModels()) {
				SentencePlanTab.UMQM.setSentencePlanAppropriateness(toIRI, userType, NLNamesTab.UMQM.getSentencePlanAppropriateness(fromIRI, userType));
			}

			father.dirtenOntologies();
		}
	}

	public boolean isEntry(DefaultMutableTreeNode node) {
		if (node.isLeaf() && !node.equals(englishParent) && !node.equals(greekParent) && !node.equals(parent)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("static-access")
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		if (isEntry(node)) {
			father.sentencePlanSelectionModel.setSelectedEntity(NaturalOWLTab.SPQM.getNLResourcesManager().getDataFactory().getOWLNamedIndividual(((ListIRI)node.getUserObject()).getEntryIRI()));
		}
	}
}