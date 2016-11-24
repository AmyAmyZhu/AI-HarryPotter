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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class ComparisonsAllowedDialog extends JFrame {
	private static final long serialVersionUID = -7027686970940568147L;
	
	ComparisonsAllowedDialogPanel panel;
	String title = "";
	
	ComparisonsAllowedDialog(NaturalOWLTab tab) {
		super("Set properties that allow comparisons");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new ComparisonsAllowedDialogPanel(tab);
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

class ComparisonsAllowedDialogPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -4833225818678494874L;

	NaturalOWLTab tab;
	
	ArrayList<JComponent> options = new ArrayList<JComponent>();
	
	DefaultComboBoxModel connectComboModel;
	DefaultListModel<ListIRI> connectListModel;
	
	JLabel connectLabel;
	JLabel connectListLabel;	
	JComboBox<ListIRI> connectCombo;
	JButton connectAdd = new JButton("+");
	JButton connectMinus = new JButton("-");
	JList<ListIRI> connectList;

	ComparisonsAllowedDialogPanel(NaturalOWLTab tab) {
		super();
		
		this.tab = tab;
				
		JPanel connectionCard = new JPanel();
		JPanel connectionCardSub1 = new JPanel();
		JPanel connectionCardSub2 = new JPanel();
		connectionCard.setLayout(new BorderLayout());
		connectionCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		connectionCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));

		connectLabel = new JLabel("Add properties that allow comparisons");
			
		HashSet<ListIRI> entries = new HashSet<ListIRI>();		
        entries.add(new ListIRI((NLResourceManager.isA.getIRI())));
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
        
        connectListLabel = new JLabel("Properties that allow comparisons");
        
        connectListModel = new DefaultListModel<ListIRI>();
        connectList = new JList<ListIRI>(connectListModel);
        connectList.setCellRenderer(new ListRenderer());
        connectList.setVisibleRowCount(10);

        ArrayList<IRI> sortedList2 = new ArrayList<IRI>(SentencePlanTab.MQM.getPropertiesThatAllowComparisons());
        Collections.sort(sortedList2);
        for (IRI entryIRI : sortedList2) {
        	connectListModel.addElement(new ListIRI(entryIRI));
        	connectComboModel.removeElement(new ListIRI(entryIRI));
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
			((SentencePlanTab)tab).MQM.setComparisonsAllowed(((ListIRI)connectCombo.getSelectedItem()).getEntryIRI(), true);			
			connectListModel.addElement((ListIRI)connectCombo.getSelectedItem());
        	connectComboModel.removeElement(connectCombo.getSelectedItem());
			
			((SentencePlanTab)tab).dirtenOntologies();
		} else if (e.getSource() == connectMinus) {
			if (connectList.getSelectedIndex() != -1) {
				((SentencePlanTab)tab).MQM.setComparisonsAllowed(connectListModel.getElementAt(connectList.getSelectedIndex()).getEntryIRI(), false);
				connectListModel.removeElementAt(connectList.getSelectedIndex());
	
				HashSet<ListIRI> entries = new HashSet<ListIRI>();

		        entries.add(new ListIRI((NLResourceManager.isA.getIRI())));
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
		        
		        connectComboModel = new DefaultComboBoxModel((ListIRI[]) sortedList.toArray());
		        connectCombo.setModel(connectComboModel);
		        
		        if (connectComboModel.getIndexOf(selected) != -1) {
		        	connectCombo.setSelectedItem(selected);
		        }			
			
				((SentencePlanTab)tab).dirtenOntologies();
			}
		}
	}
}