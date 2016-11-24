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

import gr.aueb.cs.nlg.NLFiles.NLResourceManager;

import java.awt.Color;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class DataPropertiesTreeComponent extends TreeComponent {
	private DefaultMutableTreeNode parent;
	
	public DataPropertiesTreeComponent(NaturalOWLTab fatherTab) {
		super(fatherTab);
	}
	
	public void createTree() {				
		parent = new DefaultMutableTreeNode(new ListIRI(father.getOWLModelManager().getOWLDataFactory().getOWLTopDataProperty().getIRI()), true);
				
		HashSet<ListIRI> dataToAdd = new HashSet<ListIRI>();
		for (OWLDataProperty rootProperty : father.getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider().getRoots()) {
			for (OWLDataProperty rootChild : father.getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider().getChildren(rootProperty)) {
				if (!rootChild.getIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
					dataToAdd.add(new ListIRI(rootChild.getIRI()));
				}
			}
		}

		ArrayList<ListIRI> dataToAddSorted = new ArrayList<ListIRI>(dataToAdd);
		Collections.sort(dataToAddSorted);
		for (ListIRI add : dataToAddSorted) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, true);
			parent.add(node);
			
			addDataSubProperties(node, father.getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(add.getEntryIRI())));
		}
		
		tree = new JTree(parent);
		tree.setEditable(false);
		tree.setRootVisible(false);
	}
	
	private void addDataSubProperties(DefaultMutableTreeNode parent, Set<OWLDataProperty> subProperties) {
		HashSet<ListIRI> children = new HashSet<ListIRI>();
		for (OWLDataProperty dataProperty : subProperties) {
			if (!dataProperty.getIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
				children.add(new ListIRI(dataProperty.getIRI()));
			}
		}

		ArrayList<ListIRI> childrenSorted = new ArrayList<ListIRI>(children);
		Collections.sort(childrenSorted);
		for (ListIRI child : childrenSorted) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(child, true);
			parent.add(node);
			
			addDataSubProperties(node, father.getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(child.getEntryIRI())));
		}
	}
	
	public JPanel TP() {
		// JFrame frame = new JFrame("Creating a JTree Component!");
		JPanel treepanel = new JPanel();

		createTree();
		treepanel.add(tree);
		treepanel.setBackground(new Color(255,255,255));
		treepanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(this);

		ClassLoader loader = LexiconTreeComponent.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/property.data.png");
		URL imageURLOpen = loader.getResource("/icons/property.data.open.png");
		URL imageURLClose = loader.getResource("/icons/property.data.close.png");
		ImageIcon icon = new ImageIcon(imageURL);
		ImageIcon iconOpen = new ImageIcon(imageURLOpen);
		ImageIcon iconClose = new ImageIcon(imageURLClose);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(iconOpen);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(iconClose);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(icon);
		
		return treepanel;
	}
	
	public void clearSelection() {
		tree.clearSelection();
		NaturalOWLTab.userModelTreePropertySelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTreePropertySelectionModel.getSelectedEntity());
		NaturalOWLTab.userModelTablePropertySelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTablePropertySelectionModel.getSelectedEntity());
	}
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	    if (node == null) {
			NaturalOWLTab.userModelTreePropertySelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTreePropertySelectionModel.getSelectedEntity());
			NaturalOWLTab.userModelTablePropertySelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTablePropertySelectionModel.getSelectedEntity());
	    	return;
	    }

	    IRI selectedIRI = ((ListIRI)node.getUserObject()).getEntryIRI();

		for (OWLOntology model : father.getOWLModelManager().getOntologies()) {
			if (model.containsDataPropertyInSignature(selectedIRI)) {
				NaturalOWLTab.userModelTreePropertySelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(selectedIRI));
				NaturalOWLTab.userModelTablePropertySelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(selectedIRI));
				return;
			}
		}
	}
}