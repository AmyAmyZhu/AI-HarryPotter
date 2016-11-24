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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.*;

public class TreeComponent implements ActionListener, TreeSelectionListener {
	JTree tree;
	NaturalOWLTab father;
	TreeComponent treeCmp;
	
	public TreeComponent(NaturalOWLTab fatherTab) {
		father = fatherTab;
		treeCmp = this;
	}
	    
	public void createTree() {
	}

	public JPanel TP() {
		// JFrame frame = new JFrame("Creating a JTree Component!");
		JPanel complete = new JPanel();
		JPanel jp = new JPanel();
		JPanel treepanel = new JPanel();

		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		complete.setLayout(new BoxLayout(complete, BoxLayout.Y_AXIS));

		createTree();
		jp.setBackground(new Color(255,255,255));
		treepanel.add(tree);
		treepanel.setBackground(new Color(255,255,255));
		
		complete.add(jp);
		complete.add(treepanel);
		complete.setBackground(new Color(255,255,255));
		
		return complete;

	}

	public void addNode() {
	}

	public void deleteNode(DefaultMutableTreeNode node) {
	}

	public void duplicateNode(DefaultMutableTreeNode node, String fromLanguage, String toLanguage) {
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.startsWith("New")) {
			addNode();
		}
	}
	
	public DefaultMutableTreeNode getMatchingNode(DefaultMutableTreeNode node, ListIRI selectedIRI) {	
		if (selectedIRI != null) {
			for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
				if (((ListIRI)(((DefaultMutableTreeNode)tree.getModel().getChild(node, i)).getUserObject())).equals(selectedIRI)) {
					return (DefaultMutableTreeNode)tree.getModel().getChild(node, i);
				}
				if (!((DefaultMutableTreeNode)tree.getModel().getChild(node, i)).isLeaf()) {
					DefaultMutableTreeNode selectedNode = getMatchingNode((DefaultMutableTreeNode)tree.getModel().getChild(node, i), selectedIRI);
					if (selectedNode != null) {
						return selectedNode;
					}
				}
			}
		}
		return null;
	}

	public void valueChanged(TreeSelectionEvent arg0) {}
}