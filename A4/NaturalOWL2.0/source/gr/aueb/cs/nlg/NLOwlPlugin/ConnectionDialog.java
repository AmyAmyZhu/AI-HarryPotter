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
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLName;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class ConnectionDialog extends JFrame {
	private static final long serialVersionUID = -1841608893596375857L;
	
	ConnectionDialogPanel panel;
	String title = "";
	
	ConnectionDialog(NaturalOWLTab tab, IRI resourceEntry) {
		super("Connections for " + resourceEntry.getFragment());
		if (resourceEntry.equals(NLResourceManager.anonymous.getIRI())) {
			title = "Set classes/individuals as anonymous";
		} else {
			title = "Connections for " + resourceEntry.getFragment();
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new ConnectionDialogPanel(tab, resourceEntry);
	}

	public boolean getResponse() {
		String[] options = {"OK"};
		int selection = JOptionPane.showOptionDialog(this,
                panel, 
                title,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
		if (selection == JOptionPane.OK_OPTION)
			return true;
		return false;
	}
}

class ConnectionDialogPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -2270475871800382039L;
	
	NaturalOWLTab tab;
	IRI resourceEntry;
	
	ArrayList<JComponent> options = new ArrayList<JComponent>();
	
	DefaultComboBoxModel<ListIRI> connectComboModel;
	DefaultListModel<ListIRI> connectListModel;
	
	JLabel connectLabel;
	JLabel connectListLabel;	
	JComboBox<ListIRI> connectCombo;
	JButton connectAdd = new JButton("+");
	JButton connectMinus = new JButton("-");
	JList<ListIRI> connectList;

	ConnectionDialogPanel(NaturalOWLTab tab, IRI resourceEntry) {
		super();
		
		this.tab = tab;
		this.resourceEntry = resourceEntry;
				
		JPanel connectionCard = new JPanel();
		JPanel connectionCardSub1 = new JPanel();
		JPanel connectionCardSub2 = new JPanel();
		connectionCard.setLayout(new BorderLayout());
		connectionCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		connectionCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));

		if (tab instanceof NLNamesTab) {
			connectLabel = new JLabel("Add classes/individuals to realize with this NLName");
		} else {	
			connectLabel = new JLabel("Add properties to realize with this Sentence Plan");
		}

		HashSet<ListIRI> entries = new HashSet<ListIRI>();
		if (tab instanceof NLNamesTab) {
			for (OWLOntology owl : tab.getOWLModelManager().getActiveOntologies()) {
				ArrayList<IRI> NLNameIRIs = new ArrayList<IRI>();
				for (NLName name : tab.NLNQM.getNLNamesList(Languages.ENGLISH).getNLNamesList()) {
					NLNameIRIs.add(name.getNLNameIRI());
				}
				for (NLName name : tab.NLNQM.getNLNamesList(Languages.GREEK).getNLNamesList()) {
					NLNameIRIs.add(name.getNLNameIRI());
				}
				
				for (OWLIndividual indiv : owl.getIndividualsInSignature()) {
					if (!indiv.isAnonymous()) {
						if (!tab.LQM.isAdjective(indiv.asOWLNamedIndividual().getIRI()) && !tab.LQM.isNoun(indiv.asOWLNamedIndividual().getIRI()) && !tab.LQM.isVerb(indiv.asOWLNamedIndividual().getIRI()) && tab.SPQM.getSentencePlan(indiv.asOWLNamedIndividual().getIRI()) == null &&  tab.NLNQM.getNLName(indiv.asOWLNamedIndividual().getIRI()) == null && !indiv.asOWLNamedIndividual().getIRI().getStart().equals(NLResourceManager.nlowlNS) && !NLNameIRIs.contains(indiv.asOWLNamedIndividual().getIRI()) && tab.UMQM.getUserModel(indiv.asOWLNamedIndividual().getIRI()) == null) {
							if (resourceEntry.equals(NLResourceManager.anonymous.getIRI())) {
								if (!((NLNamesTab)tab).MQM.getIndividualOrClassSet().contains(indiv.asOWLNamedIndividual().getIRI())) {
									entries.add(new ListIRI(indiv.asOWLNamedIndividual().getIRI()));
								}
							} else {
								entries.add(new ListIRI(indiv.asOWLNamedIndividual().getIRI()));
							}
						}
					}
				}
				for (OWLClass cls : owl.getClassesInSignature()) {
					if (!cls.getIRI().isThing() && !tab.LQM.isAdjective(cls.getIRI()) && !tab.LQM.isNoun(cls.getIRI()) && !tab.LQM.isVerb(cls.getIRI()) && tab.SPQM.getSentencePlan(cls.getIRI()) == null &&  tab.NLNQM.getNLName(cls.getIRI()) == null && !cls.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
						if (resourceEntry.equals(NLResourceManager.anonymous.getIRI())) {
							if (!((NLNamesTab)tab).MQM.getIndividualOrClassSet().contains(cls.getIRI())) {
								entries.add(new ListIRI(cls.getIRI()));
							}
						} else {
							entries.add(new ListIRI(cls.getIRI()));
						}
	        		}
	        	}
	        }
		} else if (tab instanceof SentencePlanTab) {	
	        for (OWLOntology owl : tab.getOWLModelManager().getActiveOntologies()) {
	        	for (OWLObjectProperty prop : owl.getObjectPropertiesInSignature()) {
	        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
	        			entries.add(new ListIRI(prop.getIRI()));
	        		}
	        	}
	        	for (OWLDataProperty prop : owl.getDataPropertiesInSignature()) {
	        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
	        			entries.add(new ListIRI(prop.getIRI()));
	        		}
	        	}
	        }
		}
	        
        ArrayList<ListIRI> sortList = new ArrayList<ListIRI>(entries);
        Collections.sort(sortList);

        connectComboModel = new DefaultComboBoxModel(sortList.toArray());
        
        connectCombo = new JComboBox<ListIRI>(connectComboModel);
        connectCombo.setRenderer(new ListRenderer());
        
        connectAdd = new JButton("+");

        Dimension comboD = connectCombo.getPreferredSize();
        connectCombo.setPreferredSize(new Dimension(220, comboD.height));
        
        Dimension buttonD = connectAdd.getPreferredSize();
        connectAdd.setPreferredSize(new Dimension(buttonD.height, buttonD.height));
        
        connectionCardSub1.add(connectLabel);
        connectionCardSub1.add(connectCombo);
        connectionCardSub1.add(connectAdd);
        connectionCardSub1.setPreferredSize(new Dimension(260, 50));
        connectionCard.add(BorderLayout.NORTH, connectionCardSub1);

		if (tab instanceof NLNamesTab) {
	        connectListLabel = new JLabel("Connected classes/individuals");
		} else {	
	        connectListLabel = new JLabel("Connected properties");
		}
        
        connectListModel = new DefaultListModel<ListIRI>();
        connectList = new JList<ListIRI>(connectListModel);
        connectList.setCellRenderer(new ListRenderer());
        connectList.setVisibleRowCount(10);

		if (tab instanceof NLNamesTab) {
			if (NLNamesTab.MQM.getIndividualOrClassSet(resourceEntry) != null) {
				ArrayList<IRI> sortedList2 = new ArrayList<IRI>(NLNamesTab.MQM.getIndividualOrClassSet(resourceEntry));
		        Collections.sort(sortedList2);
		        for (IRI entryIRI : sortedList2) {
		        	connectListModel.addElement(new ListIRI(entryIRI));
		        	connectComboModel.removeElement(new ListIRI(entryIRI));
		        }
			}
		} else if (tab instanceof SentencePlanTab) {
			if (SentencePlanTab.MQM.getPropertiesSet(resourceEntry) != null) {
		        ArrayList<IRI> sortedList2 = new ArrayList<IRI>(SentencePlanTab.MQM.getPropertiesSet(resourceEntry));
		        Collections.sort(sortedList2);
		        for (IRI entryIRI : sortedList2) {
		        	connectListModel.addElement(new ListIRI(entryIRI));
		        	connectComboModel.removeElement(new ListIRI(entryIRI));
		        }
			}
		}
	        
        JScrollPane concatListPane = new JScrollPane(connectList);
        concatListPane.setPreferredSize(new Dimension(220, 140));
        connectMinus = new JButton("-");

        connectMinus.setPreferredSize(new Dimension(buttonD.height, buttonD.height));
        
        connectionCardSub2.add(connectListLabel);
        connectionCardSub2.add(concatListPane);
        connectionCardSub2.add(connectMinus);
        connectionCardSub2.setPreferredSize(new Dimension(260, 170));
        ((FlowLayout)connectionCardSub2.getLayout()).setAlignOnBaseline(true);
        connectionCard.add(BorderLayout.CENTER, connectionCardSub2);
        
        add(connectionCard);
        
        connectAdd.addActionListener(this);
        connectMinus.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectAdd) {
			if (tab instanceof NLNamesTab) {
				((NLNamesTab)tab).MQM.addNLNameMapping(((ListIRI)connectCombo.getSelectedItem()).getEntryIRI(), resourceEntry);			
				connectListModel.addElement((ListIRI)connectCombo.getSelectedItem());
	        	connectComboModel.removeElement(connectCombo.getSelectedItem());
				
				((NLNamesTab)tab).dirtenOntologies();
			}
			if (tab instanceof SentencePlanTab) {
				((SentencePlanTab)tab).MQM.addSentencePlanMapping(((ListIRI)connectCombo.getSelectedItem()).getEntryIRI(), resourceEntry);			
				connectListModel.addElement((ListIRI)connectCombo.getSelectedItem());
	        	connectComboModel.removeElement(connectCombo.getSelectedItem());
				
				((SentencePlanTab)tab).dirtenOntologies();
			}
		} else if (e.getSource() == connectMinus) {
			if (tab instanceof NLNamesTab) {
				if (connectList.getSelectedIndex() != -1) {
					((NLNamesTab)tab).MQM.removeNLNameMapping(connectListModel.getElementAt(connectList.getSelectedIndex()).getEntryIRI(), resourceEntry);			
					connectListModel.removeElementAt(connectList.getSelectedIndex());
					
					HashSet<ListIRI> entries = new HashSet<ListIRI>();
					for (OWLOntology owl : tab.getOWLModelManager().getActiveOntologies()) {
						for (OWLIndividual indiv : owl.getIndividualsInSignature()) {
							if (!indiv.isAnonymous()) {
								if (!indiv.asOWLNamedIndividual().getIRI().getStart().equals(NLResourceManager.nlowlNS) && tab.UMQM.getUserModel(indiv.asOWLNamedIndividual().getIRI()) == null) {
									entries.add(new ListIRI(indiv.asOWLNamedIndividual().getIRI()));
								}
							}
						}
						for (OWLClass cls : owl.getClassesInSignature()) {
							if (!cls.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
								entries.add(new ListIRI(cls.getIRI()));
			        		}
			        	}
			        }				
					for (int i = 0; i < connectListModel.size(); i++) {
	        			entries.remove(connectListModel.get(i));
					}
	
					Object selected = connectCombo.getSelectedItem();
			        ArrayList<ListIRI> sortedList = new ArrayList<ListIRI>(entries);
			        Collections.sort(sortedList);
			        
			        connectComboModel = new DefaultComboBoxModel<ListIRI>((ListIRI[]) sortedList.toArray());
			        connectCombo.setModel(connectComboModel);
			        
			        if (connectComboModel.getIndexOf(selected) != -1) {
			        	connectCombo.setSelectedItem(selected);
			        }
				
					((NLNamesTab)tab).dirtenOntologies();
				}
			}
			if (tab instanceof SentencePlanTab) {
				if (connectList.getSelectedIndex() != -1) {
					((SentencePlanTab)tab).MQM.removeSentencePlanMapping(connectListModel.getElementAt(connectList.getSelectedIndex()).getEntryIRI(), resourceEntry);
					connectListModel.removeElementAt(connectList.getSelectedIndex());
		
					HashSet<ListIRI> entries = new HashSet<ListIRI>();
					
			        for (OWLOntology owl : tab.getOWLModelManager().getActiveOntologies()) {
			        	for (OWLObjectProperty prop : owl.getObjectPropertiesInSignature()) {
			        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
			        			entries.add(new ListIRI(prop.getIRI()));
			        		}
			        	}
			        	for (OWLDataProperty prop : owl.getDataPropertiesInSignature()) {
			        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
			        			entries.add(new ListIRI(prop.getIRI()));
			        		}
			        	}
			        }	
					for (int i = 0; i < connectListModel.size(); i++) {
		    			entries.remove(connectListModel.get(i));
					}
		
					Object selected = connectCombo.getSelectedItem();
			        ArrayList<ListIRI> sortedList = new ArrayList<ListIRI>(entries);
			        Collections.sort(sortedList);
			        
			        connectComboModel = new DefaultComboBoxModel(sortedList.toArray());
			        connectCombo.setModel(connectComboModel);
			        
			        if (connectComboModel.getIndexOf(selected) != -1) {
			        	connectCombo.setSelectedItem(selected);
			        }			
				
					((SentencePlanTab)tab).dirtenOntologies();
				}
			}
		}
	}
}