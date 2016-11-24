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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class GenerationTrees extends NaturalOWLTab {
	private static final long serialVersionUID = 4013375086058766803L;

	protected static final String DefaultMutableTreeNode = null;

	Logger log = Logger.getLogger(LexiconTab.class);

	private OWLModelManagerListener modelListener;
	
	JPanel classesPanel;
	JPanel individualsPanel;
	JPanel panelMain;
	
	JPanel classesTreePanel;
	JPanel individualsTreePanel;
	
	JButton classClearButton;
	JButton individualClearButton;
	
	JButton classRefreshButton;
	JButton individualRefreshButton;
	
	JScrollPane scrollClasses;
	JScrollPane scrollIndividuals;
	
	ClassesTreeComponent classesTree;
	IndividualsTreeComponent individualsTree;

	@Override
	protected void initialiseOWLView() throws Exception {
		initialiseNaturalOWL();

		generationClassSelectionModel.addListener(getOWLClassSelectionModelListener());

		setLayout(new BorderLayout(10, 10));

		panelMain = new JPanel();
		classesPanel = new JPanel();
		individualsPanel = new JPanel();

		panelMain.setLayout(new GridBagLayout());
		classesPanel.setLayout(new BorderLayout());
		individualsPanel.setLayout(new BorderLayout());
		classClearButton = new JButton("Clear class selection");
		individualClearButton = new JButton("Clear individual selection");
		
		classClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				classesTree.clearSelection();
				
				individualsTree.clearSelection();
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);
			}
		});
		
		individualClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				individualsTree.clearSelection();
				
				//individualsTreePanel = individualsTree.TP();
				//scrollIndividuals.setViewportView(individualsTreePanel);
			}
		});		

		classRefreshButton = new JButton("Refresh");
		individualRefreshButton = new JButton("Refresh");
				
		classRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
		        ListIRI selectedClassIRI = null;
		        ListIRI selectedIndividualIRI = null;
		        
		        if (classesTree.tree.getLastSelectedPathComponent() != null)
		        	selectedClassIRI = (ListIRI)((DefaultMutableTreeNode)classesTree.tree.getLastSelectedPathComponent()).getUserObject();
		        if (individualsTree.tree.getLastSelectedPathComponent() != null)
		        	selectedIndividualIRI = (ListIRI)((DefaultMutableTreeNode)individualsTree.tree.getLastSelectedPathComponent()).getUserObject();
				
				classesTreePanel = classesTree.TP();
				scrollClasses.setViewportView(classesTreePanel);

				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)classesTree.tree.getModel().getRoot();				
				DefaultMutableTreeNode selectedNode = classesTree.getMatchingNode(parent, selectedClassIRI);
				
				if (selectedNode != null) {
					TreePath selectedPath = new TreePath(((DefaultTreeModel)classesTree.tree.getModel()).getPathToRoot(selectedNode));
					classesTree.tree.setSelectionPath(selectedPath);
					
					individualsTree.setClassIRI(selectedClassIRI.getEntryIRI());
					
					parent = (DefaultMutableTreeNode)individualsTree.tree.getModel().getRoot();				
					selectedNode = individualsTree.getMatchingNode(parent, selectedIndividualIRI);
					if (selectedNode != null) {
						selectedPath = new TreePath(((DefaultTreeModel)individualsTree.tree.getModel()).getPathToRoot(selectedNode));
						individualsTree.tree.setSelectionPath(selectedPath);
					}
				} else {
					individualsTree.setClassIRI(null);
					
					individualsTreePanel = individualsTree.TP();
					scrollIndividuals.setViewportView(individualsTreePanel);
				}
			}
		});

		individualRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        ListIRI selectedIndividualIRI = null;
		        
		        if (individualsTree.tree.getLastSelectedPathComponent() != null)
		        	selectedIndividualIRI = (ListIRI)((DefaultMutableTreeNode)individualsTree.tree.getLastSelectedPathComponent()).getUserObject();
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);
					
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)individualsTree.tree.getModel().getRoot();				
				DefaultMutableTreeNode selectedNode = individualsTree.getMatchingNode(parent, selectedIndividualIRI);
				if (selectedNode != null) {
					TreePath selectedPath = new TreePath(((DefaultTreeModel)individualsTree.tree.getModel()).getPathToRoot(selectedNode));
					individualsTree.tree.setSelectionPath(selectedPath);
				}
			}
		});
		
		classesPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		individualsPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		
		classesTree = new ClassesTreeComponent(this);
		individualsTree = new IndividualsTreeComponent(this);
		
		JLabel classesLabel = new JLabel("Classes");
		JLabel individualsLabel = new JLabel("Individuals");

		classesTreePanel = classesTree.TP();
		
		scrollClasses = new JScrollPane(classesTreePanel);
		scrollClasses.setViewportView(classesTreePanel);

		individualsTreePanel = individualsTree.TP();
		scrollIndividuals = new JScrollPane(individualsTreePanel);
		scrollIndividuals.setViewportView(individualsTreePanel);
		
		JPanel classesButtonsPanel = new JPanel();
		JPanel individualsButtonsPanel = new JPanel();

		classesButtonsPanel.add(classClearButton);
		classesButtonsPanel.add(classRefreshButton);
		individualsButtonsPanel.add(individualClearButton);
		individualsButtonsPanel.add(individualRefreshButton);
				
		classesPanel.add(classesLabel, BorderLayout.PAGE_START);	
		classesPanel.add(scrollClasses, BorderLayout.CENTER);	
		classesPanel.add(classesButtonsPanel, BorderLayout.PAGE_END);
		individualsPanel.add(individualsLabel, BorderLayout.PAGE_START);
		individualsPanel.add(scrollIndividuals, BorderLayout.CENTER);
		individualsPanel.add(individualsButtonsPanel, BorderLayout.PAGE_END);
				
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.50;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,0,0,10);
		panelMain.add(classesPanel, c);
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.LINE_END;
		panelMain.add(individualsPanel, c);

		add(panelMain);
	}
	
	private OWLSelectionModelListener getOWLClassSelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = generationClassSelectionModel.getSelectedEntity();
				if (selected != null) {
					individualsTree.clearSelection();
					showDomainIndividuals(selected.getIRI());

					validate();
					repaint();
				}
			}
		};
	}
	
	private void showDomainIndividuals(IRI classIRI) {
		individualsTree.setClassIRI(classIRI);
		
		individualsTreePanel = individualsTree.TP();
		scrollIndividuals.setViewportView(individualsTreePanel);
	}
	
	protected void disposeOWLView() {
		super.disposeOWLView();
		getOWLModelManager().removeListener(modelListener);
	}

	public void actionPerformed(ActionEvent arg0) {}

	public void itemStateChanged(ItemEvent arg0) {}
}