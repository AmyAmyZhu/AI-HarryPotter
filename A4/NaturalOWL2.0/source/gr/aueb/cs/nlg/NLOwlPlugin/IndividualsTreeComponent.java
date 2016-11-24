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

import java.awt.Color;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

public class IndividualsTreeComponent extends TreeComponent {
	
	private DefaultMutableTreeNode parent;
	private IRI classIRI;
		
	public IndividualsTreeComponent(NaturalOWLTab fatherTab)
	{
		super(fatherTab);
		classIRI = null;
	}
	
	public void createTree() {	
		if (classIRI != null) {
			parent = new DefaultMutableTreeNode(new ListIRI(classIRI), true);
			
			HashSet<ListIRI> individualsToAdd = new HashSet<ListIRI>();			
			for (OWLObject indiv : father.getOWLModelManager().getOWLHierarchyManager().getOWLIndividualsByTypeHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLClass(classIRI))) {
				if (((OWLIndividual)indiv).isNamed()) {
					individualsToAdd.add(new ListIRI(((OWLIndividual)indiv).asOWLNamedIndividual().getIRI()));
				}
			}
			
			ArrayList<ListIRI> individualsToAddSorted = new ArrayList<ListIRI>(individualsToAdd);
			Collections.sort(individualsToAddSorted);
			for (ListIRI add : individualsToAddSorted) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, true);
				parent.add(node);
			}
			tree = new JTree(parent);
		} else {
			tree = new JTree();
		}		
		tree.setEditable(false);
		tree.setRootVisible(false);
	}
	
	public JPanel TP() {
		JPanel treepanel = new JPanel();

		createTree();
		
		treepanel.setBackground(new Color(255,255,255));
		treepanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(this);

		ClassLoader loader = LexiconTreeComponent.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/individual.png");
		ImageIcon icon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(icon);
		
		if (classIRI == null) {
			treepanel.remove(tree);
		} else {
			treepanel.add(tree);
		}
		
		return treepanel;
	}

	public IRI getClassIRI() {
		return classIRI;
	}

	public void setClassIRI(IRI classIRI) {
		this.classIRI = classIRI;
	}
	
	public void clearSelection() {
		tree.clearSelection();
		if (father instanceof GenerationTrees) {
			NaturalOWLTab.generationIndivSelectionModel.clearLastSelectedEntity(NaturalOWLTab.generationIndivSelectionModel.getSelectedEntity());
			NaturalOWLTab.generationIndivSelectionModel.setSelectedEntity(null);
		} else {
			NaturalOWLTab.userModelTableIndivSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableIndivSelectionModel.getSelectedEntity());
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	    if (node == null) {
			if (father instanceof GenerationTrees) {
				NaturalOWLTab.generationIndivSelectionModel.clearLastSelectedEntity(NaturalOWLTab.generationIndivSelectionModel.getSelectedEntity());
				NaturalOWLTab.generationIndivSelectionModel.setSelectedEntity(null);
			} else {
				NaturalOWLTab.userModelTableIndivSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableIndivSelectionModel.getSelectedEntity());
			}
	    	return;
	    }

	    IRI selectedIRI= ((ListIRI)node.getUserObject()).getEntryIRI();

		for (OWLOntology model : father.getOWLModelManager().getOntologies()) {
			if (model.containsIndividualInSignature(selectedIRI)) {
				if (father instanceof GenerationTrees) {
					NaturalOWLTab.generationIndivSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLClass(selectedIRI));
				} else {
					NaturalOWLTab.userModelTableIndivSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLNamedIndividual(selectedIRI));
				}
				return;
			}
		}
	}
}