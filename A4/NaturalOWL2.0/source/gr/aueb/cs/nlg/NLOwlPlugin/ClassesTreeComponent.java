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

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class ClassesTreeComponent extends TreeComponent {
	
	private DefaultMutableTreeNode parent;
	private IRI propertyIRI;
	
	public ClassesTreeComponent(NaturalOWLTab fatherTab)
	{
		super(fatherTab);
		propertyIRI = null;
	}
	
	public void createTree() {
		if (father instanceof GenerationTrees) {
			parent = new DefaultMutableTreeNode(new ListIRI(father.getOWLModelManager().getOWLDataFactory().getOWLThing().getIRI()), true);
			
			HashSet<ListIRI> classesToAdd = new HashSet<ListIRI>();			
			for (OWLClass rootClass : father.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getRoots()) {
				classesToAdd.add(new ListIRI(rootClass.getIRI()));
			}			
			
			ArrayList<ListIRI> classesToAddSorted = new ArrayList<ListIRI>(classesToAdd);
			Collections.sort(classesToAddSorted);
			for (ListIRI add : classesToAddSorted) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, true);
				parent.add(node);
				
				addSubClasses(node, father.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLClass(add.getEntryIRI())));
			}
			tree = new JTree(parent);
		} else if (propertyIRI != null) {
			parent = new DefaultMutableTreeNode(new ListIRI(father.getOWLModelManager().getOWLDataFactory().getOWLThing().getIRI()), true);
			
			HashSet<ListIRI> classesToAdd = new HashSet<ListIRI>();
			for (OWLOntology model : father.getOWLModelManager().getOntologies()) {
				if (model.containsObjectPropertyInSignature(propertyIRI)) {
					for (OWLObjectPropertyDomainAxiom domainAxiom : model.getObjectPropertyDomainAxioms(father.getOWLModelManager().getOWLDataFactory().getOWLObjectProperty(propertyIRI))) {
						classesToAdd.addAll(getClassExpressionIRIs(domainAxiom.getDomain()));
					}
				} else if (model.containsDataPropertyInSignature(propertyIRI)) {
					for (OWLDataPropertyDomainAxiom domainAxiom : model.getDataPropertyDomainAxioms(father.getOWLModelManager().getOWLDataFactory().getOWLDataProperty(propertyIRI))) {
						classesToAdd.addAll(getClassExpressionIRIs(domainAxiom.getDomain()));
					}
				}
			}
			
			if (classesToAdd.isEmpty()) {
				for (OWLClass rootClass : father.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getRoots()) {
					classesToAdd.add(new ListIRI(rootClass.getIRI()));
				}
			}
			
			
			ArrayList<ListIRI> classesToAddSorted = new ArrayList<ListIRI>(classesToAdd);
			Collections.sort(classesToAddSorted);
			for (ListIRI add : classesToAddSorted) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(add, true);
				parent.add(node);
				
				addSubClasses(node, father.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLClass(add.getEntryIRI())));
			}
			tree = new JTree(parent);
		} else {
			tree = new JTree();
		}		
		tree.setEditable(false);
		tree.setRootVisible(false);
	}
	
	private void addSubClasses(DefaultMutableTreeNode parent, Set<OWLClass> subClasses) {
		HashSet<ListIRI> children = new HashSet<ListIRI>();
		for (OWLClassExpression subClass : subClasses) {
			children.addAll(getClassExpressionIRIs(subClass));
		}

		ArrayList<ListIRI> childrenSorted = new ArrayList<ListIRI>(children);
		Collections.sort(childrenSorted);
		for (ListIRI childIRI : childrenSorted) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(childIRI, true);
			parent.add(node);
			
			addSubClasses(node, father.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(father.getOWLModelManager().getOWLDataFactory().getOWLClass(childIRI.getEntryIRI())));
		}
	}
	
	private HashSet<ListIRI> getClassExpressionIRIs(OWLClassExpression classExpr) {
		HashSet<ListIRI> classesToAdd = new HashSet<ListIRI>();
		if (classExpr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
			if (!classExpr.asOWLClass().getIRI().toString().startsWith(NLResourceManager.nlowlNS)) {
				classesToAdd.add(new ListIRI(classExpr.asOWLClass().getIRI()));
			}
		} else if (classExpr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
			for (OWLClassExpression disjExpr : classExpr.asConjunctSet()) {
				classesToAdd.addAll(getClassExpressionIRIs(disjExpr));
			}
		}
		return classesToAdd;
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
		URL imageURL = loader.getResource("/icons/Classes.gif");
		ImageIcon icon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(icon);

		if (father instanceof GenerationTrees) {
			treepanel.add(tree);
		} else {
			if (propertyIRI == null) {
				treepanel.remove(tree);
			} else {
				treepanel.add(tree);
			}
		}
		
		return treepanel;
	}

	public IRI getPropertyIRI() {
		return propertyIRI;
	}

	public void setPropertyIRI(IRI propertyIRI) {
		this.propertyIRI = propertyIRI;
	}
	
	public void clearSelection() {
		tree.clearSelection();
		if (father instanceof GenerationTrees) {
			NaturalOWLTab.generationClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.generationClassSelectionModel.getSelectedEntity());
		} else {
			NaturalOWLTab.userModelTreeClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTreeClassSelectionModel.getSelectedEntity());
			NaturalOWLTab.userModelTableClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableClassSelectionModel.getSelectedEntity());
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	    if (node == null) {
			if (father instanceof GenerationTrees) {
				NaturalOWLTab.generationClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.generationClassSelectionModel.getSelectedEntity());
			} else {
				NaturalOWLTab.userModelTreeClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTreeClassSelectionModel.getSelectedEntity());
				NaturalOWLTab.userModelTableClassSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableClassSelectionModel.getSelectedEntity());
			}
	    	return;
	    }

	    IRI selectedIRI= ((ListIRI)node.getUserObject()).getEntryIRI();

		for (OWLOntology model : father.getOWLModelManager().getOntologies()) {
			if (model.containsClassInSignature(selectedIRI)) {
				if (father instanceof GenerationTrees) {
					NaturalOWLTab.generationClassSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLClass(selectedIRI));
				} else {
					NaturalOWLTab.userModelTreeClassSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLClass(selectedIRI));
					NaturalOWLTab.userModelTableClassSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLClass(selectedIRI));
				}
				return;
			}
		}
	}
}