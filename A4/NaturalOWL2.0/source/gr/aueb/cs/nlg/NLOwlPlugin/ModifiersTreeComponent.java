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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.semanticweb.owlapi.model.IRI;

public class ModifiersTreeComponent extends TreeComponent {
	private DefaultMutableTreeNode parent;
	
	public ModifiersTreeComponent(NaturalOWLTab fatherTab) {
		super(fatherTab);
	}
	
	public void createTree() {				
		parent = new DefaultMutableTreeNode(new ListIRI(father.getOWLModelManager().getOWLDataFactory().getOWLTopObjectProperty().getIRI()), true);
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new ListIRI(NLResourceManager.exactCardinality.getIRI()), false);
		parent.add(node);

		node = new DefaultMutableTreeNode(new ListIRI(NLResourceManager.maxCardinality.getIRI()), false);
		parent.add(node);
		
		node = new DefaultMutableTreeNode(new ListIRI(NLResourceManager.minCardinality.getIRI()), false);
		parent.add(node);
		
		node = new DefaultMutableTreeNode(new ListIRI(NLResourceManager.allValuesFrom.getIRI()), false);
		parent.add(node);
		
		node = new DefaultMutableTreeNode(new ListIRI(NLResourceManager.someValuesFrom.getIRI()), false);
		parent.add(node);
		
		tree = new JTree(parent);
		tree.setEditable(false);
		tree.setRootVisible(false);
	}
	
	public JPanel TP() {
		JPanel treepanel = new JPanel();

		createTree();
		treepanel.add(tree);
		treepanel.setBackground(new Color(255,255,255));
		treepanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new TreeRenderer());
		tree.addTreeSelectionListener(this);

		ClassLoader loader = LexiconTreeComponent.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/modifier.png");
		ImageIcon icon = new ImageIcon(imageURL);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(icon);
		((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(icon);
		
		return treepanel;
	}
	
	public void clearSelection() {
		tree.clearSelection();
		NaturalOWLTab.userModelTableModifierSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableModifierSelectionModel.getSelectedEntity());
	}
	
	public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

	    if (node == null) {
			NaturalOWLTab.userModelTableModifierSelectionModel.clearLastSelectedEntity(NaturalOWLTab.userModelTableModifierSelectionModel.getSelectedEntity());
	    	return;
	    }

	    IRI selectedIRI = ((ListIRI)node.getUserObject()).getEntryIRI();
		NaturalOWLTab.userModelTableModifierSelectionModel.setSelectedEntity(father.getOWLModelManager().getOWLDataFactory().getOWLObjectProperty(selectedIRI));
		return;
	}
}