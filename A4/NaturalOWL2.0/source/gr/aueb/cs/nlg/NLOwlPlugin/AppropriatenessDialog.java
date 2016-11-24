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
import gr.aueb.cs.nlg.NLFiles.UserModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.semanticweb.owlapi.model.IRI;

public class AppropriatenessDialog extends JFrame {
	private static final long serialVersionUID = -4315676483993103529L;
	
	AppropriatenessDialogPanel panel;
	String title = "";
	
	AppropriatenessDialog(NaturalOWLTab tab, IRI resourceEntry) {
		super("Appropriateness for " + resourceEntry.getFragment());
		title = "Appropriateness for " + resourceEntry.getFragment();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new AppropriatenessDialogPanel(tab, resourceEntry);
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

class AppropriatenessDialogPanel extends JPanel implements TableModelListener {
	private static final long serialVersionUID = -2771247919823475133L;
	
	private final String userTypeColumn = "User Type";
	private final String appropriatenessColumn = "Appropriateness";
	
	NaturalOWLTab tab;
	IRI resourceEntry;
	
	JLabel connectLabel;
	JTable appropriatenessTable;
	

	AppropriatenessDialogPanel(NaturalOWLTab tab, IRI resourceEntry) {
		super();
		
		this.tab = tab;
		this.resourceEntry = resourceEntry;
				
		JPanel connectionCard = new JPanel();
		JPanel connectionCardSub1 = new JPanel();
		connectionCard.setLayout(new BorderLayout());
		connectionCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		if (tab instanceof NLNamesTab) {
			connectLabel = new JLabel("Set how appropriate this NL Name is for each User Type");
			fillNLNameTable();
		} else if (tab instanceof SentencePlanTab) {	
			connectLabel = new JLabel("Set how appropriate this Sentence Plan is for each User Type");
			fillSentencePlanTable();
		}        
	        
        JScrollPane scrollApropTable = new JScrollPane(appropriatenessTable);
        //scrollApropTable.setPreferredSize(new Dimension(210, 140));   

		connectionCardSub1.add(connectLabel);
        connectionCard.add(connectionCardSub1, BorderLayout.PAGE_START);
        connectionCard.add(scrollApropTable, BorderLayout.CENTER);
        
        add(connectionCard);
	}
	
	public void fillNLNameTable(){
		String[] columnNames = {userTypeColumn, appropriatenessColumn};
		
		//to be filled with meaningful data
		Object[][] data = new Object[NaturalOWLTab.UMQM.getUserModels().size() - 1][2];
		
		int row = 0;
		for (IRI UMIRI : NaturalOWLTab.UMQM.getUserModels()) {
			if (!UMIRI.equals(NLResourceManager.globalUserModel.getIRI())) {
				data[row][0] = new ListIRI(UMIRI);
				data[row][1] = NaturalOWLTab.UMQM.getNLNameAppropriateness(resourceEntry, UMIRI);
				row++;
			}
		}

		appropriatenessTable = new JTable(new AppropriatenessTableModel(data, columnNames));
		TableColumn column = null;
		
		for (int i = 0; i < appropriatenessTable.getColumnCount(); i++){
			column = appropriatenessTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		appropriatenessTable.getTableHeader().setReorderingAllowed(false);
		//allows rows selection, used in removeRow
		appropriatenessTable.setCellSelectionEnabled(true);
		appropriatenessTable.getModel().addTableModelListener(this);
	}
	
	public void fillSentencePlanTable(){
		String[] columnNames = {userTypeColumn, appropriatenessColumn};
		
		//to be filled with meaningful data
		Object[][] data = new Object[NaturalOWLTab.UMQM.getUserModels().size() - 1][2];
		
		int row = 0;
		for (IRI UMIRI : NaturalOWLTab.UMQM.getUserModels()) {
			if (!UMIRI.equals(NLResourceManager.globalUserModel.getIRI())) {
				data[row][0] = new ListIRI(UMIRI);
				data[row][1] = NaturalOWLTab.UMQM.getSentencePlanAppropriateness(resourceEntry, UMIRI);
				row++;
			}
		}

		appropriatenessTable = new JTable(new AppropriatenessTableModel(data, columnNames));
		TableColumn column = null;
		
		for (int i = 0; i < appropriatenessTable.getColumnCount(); i++){
			column = appropriatenessTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		appropriatenessTable.getTableHeader().setReorderingAllowed(false);
		//allows rows selection, used in removeRow
		appropriatenessTable.setCellSelectionEnabled(true);
		appropriatenessTable.getModel().addTableModelListener(this);
	}

	public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        
        if (columnName.equals(appropriatenessColumn)) {
            ListIRI userTypeIRI = (ListIRI)model.getValueAt(row, 0);

            String number = (model.getValueAt(row, column) + "");
            int oldNumber = UserModel.USER_MODEL_DEFAULT_APPROPRIATENESS;
            if (tab instanceof NLNamesTab) {
                oldNumber = NaturalOWLTab.UMQM.getNLNameAppropriateness(resourceEntry, userTypeIRI.getEntryIRI());
    		} else if (tab instanceof SentencePlanTab) {	
                oldNumber = NaturalOWLTab.UMQM.getSentencePlanAppropriateness(resourceEntry, userTypeIRI.getEntryIRI());
    		} 
            
            try {
            	int newNumber = Integer.parseInt(number);
            	if (newNumber != oldNumber) {
                    if (tab instanceof NLNamesTab) {
                		NaturalOWLTab.UMQM.setNLNameAppropriateness(resourceEntry, userTypeIRI.getEntryIRI(), newNumber);
            		} else if (tab instanceof SentencePlanTab) {	
                		NaturalOWLTab.UMQM.setSentencePlanAppropriateness(resourceEntry, userTypeIRI.getEntryIRI(), newNumber);
            		} 
            		tab.dirtenOntologies();
            	}
            }
            catch (NumberFormatException nfe) {
            	model.setValueAt(oldNumber, row, column);
            }
        }		
	}
}

class AppropriatenessTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1316937386079216972L;

	public AppropriatenessTableModel(Object[][] data, String[] columnNames) {
		super(data, columnNames);
	}

	public boolean isCellEditable(int row, int col) {  
	if (col == 0) {  
            return false;  
        }
	return true;         
    }  
}