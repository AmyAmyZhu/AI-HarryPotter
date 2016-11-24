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
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class UserModellingTrees extends NaturalOWLTab implements
		ActionListener, ItemListener, DocumentListener {

	protected static final String DefaultMutableTreeNode = null;

	Logger log = Logger.getLogger(LexiconTab.class);

	private OWLModelManagerListener modelListener;
	
	JTabbedPane tabbedProperties;
	JPanel objectPropertiesPanel;
	JPanel dataPropertiesPanel;
	JPanel independentPropertiesPanel;
	JPanel modifiersPanel;
	JPanel classesPanel;
	JPanel individualsPanel;
	JPanel panelMain;
	
	JPanel objectPropertiesTreePanel;
	JPanel dataPropertiesTreePanel;
	JPanel independentPropertiesTreePanel;
	JPanel modifiersTreePanel;
	JPanel classesTreePanel;
	JPanel individualsTreePanel;
	
	JButton objectPropertyClearButton;
	JButton dataPropertyClearButton;
	JButton independentPropertyClearButton;
	JButton modifierClearButton;
	JButton classClearButton;
	JButton individualClearButton;
	
	JButton objectPropertyRefreshButton;
	JButton dataPropertyRefreshButton;
	JButton classRefreshButton;
	JButton individualRefreshButton;
	
	JScrollPane scrollObjectProperties;
	JScrollPane scrollDataProperties;
	JScrollPane scrollIndependentProperties;
	JScrollPane scrollModifiers;
	JScrollPane scrollClasses;
	JScrollPane scrollIndividuals;
	
	ObjectPropertiesTreeComponent objectPropertiesTree;
	DataPropertiesTreeComponent dataPropertiesTree;
	IndependentPropertiesTreeComponent independentPropertiesTree;
	ModifiersTreeComponent modifiersTree;
	ClassesTreeComponent classesTree;
	IndividualsTreeComponent individualsTree;

	@Override
	protected void initialiseOWLView() throws Exception {
		userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
		userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());

		refresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
		        refresh();
			}
		});
		
		createTrees();
	}
	
	private void refresh() {
		removeAll();
		createTrees();
	}
	
	private void createTrees() {
		setLayout(new BorderLayout(10, 10));

		panelMain = new JPanel();
		objectPropertiesPanel = new JPanel();
		dataPropertiesPanel = new JPanel();
		independentPropertiesPanel = new JPanel();
		modifiersPanel = new JPanel();
		classesPanel = new JPanel();
		individualsPanel = new JPanel();

		panelMain.setLayout(new GridBagLayout());
		objectPropertiesPanel.setLayout(new BorderLayout());
		dataPropertiesPanel.setLayout(new BorderLayout());
		independentPropertiesPanel.setLayout(new BorderLayout());
		modifiersPanel.setLayout(new BorderLayout());
		classesPanel.setLayout(new BorderLayout());
		individualsPanel.setLayout(new BorderLayout());
		
		objectPropertyClearButton = new JButton("Clear property selection");
		dataPropertyClearButton = new JButton("Clear property selection");
		independentPropertyClearButton = new JButton("Clear domain-independent property selection");
		modifierClearButton = new JButton("Clear modifier selection");
		classClearButton = new JButton("Clear class selection");
		individualClearButton = new JButton("Clear individual selection");
		
		objectPropertyClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				objectPropertiesTree.clearSelection();
				userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
				userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				
				classesTree.clearSelection();
				userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
				userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
				
				individualsTree.clearSelection();
				
				classesTree.setPropertyIRI(null);
				
				classesTreePanel = classesTree.TP();
				scrollClasses.setViewportView(classesTreePanel);
				
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);				
			}
		});
		
		dataPropertyClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				dataPropertiesTree.clearSelection();
				userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
				userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				
				classesTree.clearSelection();
				userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
				userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
				
				individualsTree.clearSelection();				
				
				classesTree.setPropertyIRI(null);
				
				classesTreePanel = classesTree.TP();
				scrollClasses.setViewportView(classesTreePanel);
				
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);				
			}
		});
		
		independentPropertyClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				independentPropertiesTree.clearSelection();
				userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
				userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				
				classesTree.clearSelection();
				userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
				userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
				
				individualsTree.clearSelection();
				
				classesTree.setPropertyIRI(null);
				
				classesTreePanel = classesTree.TP();
				scrollClasses.setViewportView(classesTreePanel);
				
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);				
			}
		});
		
		modifierClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				modifiersTree.clearSelection();
			}
		});
		
		classClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				classesTree.clearSelection();
				userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
				userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
				
				individualsTree.clearSelection();
				
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);
			}
		});
		
		individualClearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				individualsTree.clearSelection();
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);
			}
		});		

		objectPropertyRefreshButton = new JButton("Refresh");
		dataPropertyRefreshButton = new JButton("Refresh");
		classRefreshButton = new JButton("Refresh");
		individualRefreshButton = new JButton("Refresh");
		
		objectPropertyRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        ListIRI selectedPropertyIRI = null;
		        ListIRI selectedClassIRI = null;
		        ListIRI selectedIndividualIRI = null;
		        
		        if (objectPropertiesTree.tree.getLastSelectedPathComponent() != null)
		        	selectedPropertyIRI = (ListIRI)((DefaultMutableTreeNode)objectPropertiesTree.tree.getLastSelectedPathComponent()).getUserObject();
		        if (classesTree.tree.getLastSelectedPathComponent() != null)
		        	selectedClassIRI = (ListIRI)((DefaultMutableTreeNode)classesTree.tree.getLastSelectedPathComponent()).getUserObject();
		        if (individualsTree.tree.getLastSelectedPathComponent() != null)
		        	selectedIndividualIRI = (ListIRI)((DefaultMutableTreeNode)individualsTree.tree.getLastSelectedPathComponent()).getUserObject();
				
				objectPropertiesTreePanel = objectPropertiesTree.TP();
				scrollObjectProperties.setViewportView(objectPropertiesTreePanel);

				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)objectPropertiesTree.tree.getModel().getRoot();				
				DefaultMutableTreeNode selectedNode = objectPropertiesTree.getMatchingNode(parent, selectedPropertyIRI);
				
				if (selectedNode != null) {
					TreePath selectedPath = new TreePath(((DefaultTreeModel)objectPropertiesTree.tree.getModel()).getPathToRoot(selectedNode));
					objectPropertiesTree.tree.setSelectionPath(selectedPath);
					
					classesTree.setPropertyIRI(selectedPropertyIRI.getEntryIRI());
					
					parent = (DefaultMutableTreeNode)classesTree.tree.getModel().getRoot();				
					selectedNode = classesTree.getMatchingNode(parent, selectedClassIRI);
					
					if (selectedNode != null) {
						selectedPath = new TreePath(((DefaultTreeModel)classesTree.tree.getModel()).getPathToRoot(selectedNode));
						classesTree.tree.setSelectionPath(selectedPath);
						
						individualsTree.setClassIRI(selectedClassIRI.getEntryIRI());
						
						parent = (DefaultMutableTreeNode)individualsTree.tree.getModel().getRoot();				
						selectedNode = individualsTree.getMatchingNode(parent, selectedIndividualIRI);
						if (selectedNode != null) {
							selectedPath = new TreePath(((DefaultTreeModel)individualsTree.tree.getModel()).getPathToRoot(selectedNode));
							individualsTree.tree.setSelectionPath(selectedPath);
						}
					}
				} else {					
					individualsTree.setClassIRI(null);
					
					individualsTreePanel = individualsTree.TP();
					scrollIndividuals.setViewportView(individualsTreePanel);
				}
			}
		});
		
		dataPropertyRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        ListIRI selectedPropertyIRI = null;
		        ListIRI selectedClassIRI = null;
		        ListIRI selectedIndividualIRI = null;
		        
		        if (dataPropertiesTree.tree.getLastSelectedPathComponent() != null)
		        	selectedPropertyIRI = (ListIRI)((DefaultMutableTreeNode)dataPropertiesTree.tree.getLastSelectedPathComponent()).getUserObject();
		        if (classesTree.tree.getLastSelectedPathComponent() != null)
		        	selectedClassIRI = (ListIRI)((DefaultMutableTreeNode)classesTree.tree.getLastSelectedPathComponent()).getUserObject();
		        if (individualsTree.tree.getLastSelectedPathComponent() != null)
		        	selectedIndividualIRI = (ListIRI)((DefaultMutableTreeNode)individualsTree.tree.getLastSelectedPathComponent()).getUserObject();
				
				dataPropertiesTreePanel = dataPropertiesTree.TP();
				scrollDataProperties.setViewportView(dataPropertiesTreePanel);

				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)dataPropertiesTree.tree.getModel().getRoot();				
				DefaultMutableTreeNode selectedNode = dataPropertiesTree.getMatchingNode(parent, selectedPropertyIRI);
				
				if (selectedNode != null) {
					TreePath selectedPath = new TreePath(((DefaultTreeModel)dataPropertiesTree.tree.getModel()).getPathToRoot(selectedNode));
					dataPropertiesTree.tree.setSelectionPath(selectedPath);
					
					classesTree.setPropertyIRI(selectedPropertyIRI.getEntryIRI());
					
					parent = (DefaultMutableTreeNode)classesTree.tree.getModel().getRoot();				
					selectedNode = classesTree.getMatchingNode(parent, selectedClassIRI);
					
					if (selectedNode != null) {
						selectedPath = new TreePath(((DefaultTreeModel)classesTree.tree.getModel()).getPathToRoot(selectedNode));
						classesTree.tree.setSelectionPath(selectedPath);
						
						individualsTree.setClassIRI(selectedClassIRI.getEntryIRI());
						
						parent = (DefaultMutableTreeNode)individualsTree.tree.getModel().getRoot();				
						selectedNode = individualsTree.getMatchingNode(parent, selectedIndividualIRI);
						if (selectedNode != null) {
							selectedPath = new TreePath(((DefaultTreeModel)individualsTree.tree.getModel()).getPathToRoot(selectedNode));
							individualsTree.tree.setSelectionPath(selectedPath);
						}
					}
				} else {					
					individualsTree.setClassIRI(null);
					
					individualsTreePanel = individualsTree.TP();
					scrollIndividuals.setViewportView(individualsTreePanel);
				}
			}
		});
			
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
		
		objectPropertiesPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		dataPropertiesPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		independentPropertiesPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		modifiersPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		classesPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		individualsPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		
		objectPropertiesTree = new ObjectPropertiesTreeComponent(this);
		dataPropertiesTree = new DataPropertiesTreeComponent(this);
		independentPropertiesTree = new IndependentPropertiesTreeComponent(this);
		modifiersTree = new ModifiersTreeComponent(this);
		classesTree = new ClassesTreeComponent(this);
		individualsTree = new IndividualsTreeComponent(this);
		
		//JLabel objectPropertiesLabel = new JLabel("Object Properties");
		//JLabel dataPropertiesLabel = new JLabel("Data Properties");
		JLabel modifiersLabel = new JLabel("Property Modifiers");
		JLabel classesLabel = new JLabel("Classes");
		JLabel individualsLabel = new JLabel("Individuals");
		
		objectPropertiesTreePanel = objectPropertiesTree.TP();
		scrollObjectProperties = new JScrollPane(objectPropertiesTreePanel);
		scrollObjectProperties.setViewportView(objectPropertiesTreePanel);
		
		dataPropertiesTreePanel = dataPropertiesTree.TP();
		scrollDataProperties = new JScrollPane(dataPropertiesTreePanel);
		scrollDataProperties.setViewportView(dataPropertiesTreePanel);
		
		independentPropertiesTreePanel = independentPropertiesTree.TP();
		scrollIndependentProperties = new JScrollPane(independentPropertiesTreePanel);
		scrollIndependentProperties.setViewportView(independentPropertiesTreePanel);
		
		modifiersTreePanel = modifiersTree.TP();
		scrollModifiers = new JScrollPane(modifiersTreePanel);
		scrollModifiers.setViewportView(modifiersTreePanel);

		classesTreePanel = classesTree.TP();
		scrollClasses = new JScrollPane(classesTreePanel);
		scrollClasses.setViewportView(classesTreePanel);

		individualsTreePanel = individualsTree.TP();
		scrollIndividuals = new JScrollPane(individualsTreePanel);
		scrollIndividuals.setViewportView(individualsTreePanel);
		
		JPanel objectPropertiesButtonsPanel = new JPanel();
		JPanel dataPropertiesButtonsPanel = new JPanel();
		JPanel independentPropertiesButtonsPanel = new JPanel();
		JPanel modifiersButtonsPanel = new JPanel();
		JPanel classesButtonsPanel = new JPanel();
		JPanel individualsButtonsPanel = new JPanel();

		objectPropertiesButtonsPanel.add(objectPropertyClearButton);
		objectPropertiesButtonsPanel.add(objectPropertyRefreshButton);
		dataPropertiesButtonsPanel.add(dataPropertyClearButton);
		dataPropertiesButtonsPanel.add(dataPropertyRefreshButton);
		independentPropertiesButtonsPanel.add(independentPropertyClearButton);
		modifiersButtonsPanel.add(modifierClearButton);
		classesButtonsPanel.add(classClearButton);
		classesButtonsPanel.add(classRefreshButton);
		individualsButtonsPanel.add(individualClearButton);
		individualsButtonsPanel.add(individualRefreshButton);
		
		//objectPropertiesPanel.add(objectPropertiesLabel, BorderLayout.PAGE_START);
		objectPropertiesPanel.add(scrollObjectProperties, BorderLayout.CENTER);
		objectPropertiesPanel.add(objectPropertiesButtonsPanel, BorderLayout.PAGE_END);
		//dataPropertiesPanel.add(dataPropertiesLabel, BorderLayout.PAGE_START);
		dataPropertiesPanel.add(scrollDataProperties, BorderLayout.CENTER);
		dataPropertiesPanel.add(dataPropertiesButtonsPanel, BorderLayout.PAGE_END);
		independentPropertiesPanel.add(scrollIndependentProperties, BorderLayout.CENTER);
		independentPropertiesPanel.add(independentPropertiesButtonsPanel, BorderLayout.PAGE_END);
		modifiersPanel.add(modifiersLabel, BorderLayout.PAGE_START);
		modifiersPanel.add(scrollModifiers, BorderLayout.CENTER);
		modifiersPanel.add(modifiersButtonsPanel, BorderLayout.PAGE_END);		
		classesPanel.add(classesLabel, BorderLayout.PAGE_START);	
		classesPanel.add(scrollClasses, BorderLayout.CENTER);	
		classesPanel.add(classesButtonsPanel, BorderLayout.PAGE_END);
		individualsPanel.add(individualsLabel, BorderLayout.PAGE_START);
		individualsPanel.add(scrollIndividuals, BorderLayout.CENTER);
		individualsPanel.add(individualsButtonsPanel, BorderLayout.PAGE_END);
		
		tabbedProperties = new JTabbedPane();
		tabbedProperties.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (tabbedProperties.getSelectedIndex() == 0) {
					objectPropertiesTree.clearSelection();
					userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
					userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				}
				if (tabbedProperties.getSelectedIndex() == 1) {
					dataPropertiesTree.clearSelection();
					userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
					userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				}
				if (tabbedProperties.getSelectedIndex() == 2) {
					independentPropertiesTree.clearSelection();
					userModelTreePropertySelectionModel = new OWLSelectionModelImpl();
					userModelTreePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());
				}
				classesTree.clearSelection();
				userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
				userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
				
				individualsTree.clearSelection();
				
				classesTree.setPropertyIRI(null);
				
				classesTreePanel = classesTree.TP();
				scrollClasses.setViewportView(classesTreePanel);
				
				individualsTree.setClassIRI(null);
				
				individualsTreePanel = individualsTree.TP();
				scrollIndividuals.setViewportView(individualsTreePanel);
			}
		});
		
		tabbedProperties.addTab("Object Properties", null, objectPropertiesPanel, null);
		tabbedProperties.addTab("Data Properties", null, dataPropertiesPanel, null);
		tabbedProperties.addTab("Independent-domain Properties", null, independentPropertiesPanel, null);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.25;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		panelMain.add(modifiersPanel, c);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,10,0,0);
		c.anchor = GridBagConstraints.LINE_START;
		panelMain.add(tabbedProperties, c);
		//panelMain.add(Box.createRigidArea(new Dimension(10,0)));
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0,10,0,10);
		panelMain.add(classesPanel, c);
		//panelMain.add(Box.createRigidArea(new Dimension(10,0)));
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.LINE_END;
		panelMain.add(individualsPanel, c);

		add(panelMain);
	}

	private OWLSelectionModelListener getOWLPropertySelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTreePropertySelectionModel.getSelectedEntity();
				if (selected != null) {
					classesTree.clearSelection();
					userModelTreeClassSelectionModel = new OWLSelectionModelImpl();
					userModelTreeClassSelectionModel.addListener(getOWLClassSelectionModelListener());
					
					individualsTree.clearSelection();	
					showDomainClasses(selected.getIRI());
	
					validate();
					repaint();
				}
			}
		};
	}
	
	private OWLSelectionModelListener getOWLClassSelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTreeClassSelectionModel.getSelectedEntity();
				if (selected != null) {
					individualsTree.clearSelection();
					
					showDomainIndividuals(selected.getIRI());

					validate();
					repaint();
				}
			}
		};
	}
	
	private void showDomainClasses(IRI propertyIRI) {
		classesTree.setPropertyIRI(propertyIRI);
		
		classesTreePanel = classesTree.TP();
		scrollClasses.setViewportView(classesTreePanel);
		
		individualsTree.setClassIRI(null);
		
		individualsTreePanel = individualsTree.TP();
		scrollIndividuals.setViewportView(individualsTreePanel);
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
	
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}
}