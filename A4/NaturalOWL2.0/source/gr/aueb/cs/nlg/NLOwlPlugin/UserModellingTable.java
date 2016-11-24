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

import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.UserModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class UserModellingTable extends NaturalOWLTab implements
		TableModelListener {

	Logger log = Logger.getLogger(LexiconTab.class);

	private OWLModelManagerListener modelListener;

	private JPanel globalPanel;
	private JPanel userTypePanel;
	private JTable globalTable;
	private JTable table;
	private JScrollPane scrollTable;
	private JPanel panelButtons;
	private JPanel panelExtra;
	private JLabel previewLabel;

	private JButton addUT;
	private JButton removeUT;
	private JButton duplicateUT;

	private ListIRI selectedProperty = null;
	private ListIRI selectedClass = null;
	private ListIRI selectedIndiv = null;
	private ListIRI selectedModifier = null;

	private final String userTypeColumn = "User Type";
	private final String maxMessagesPerPageColumn = "Max Messages per Page";
	private final String maxMessagesPerSentenceColumn = "Max Messages per Sentence";
	private final String globalInterestColumn = "Global Interest";
	private final String globalRepetitionsColumn = "Global Repetitions";

	private final String setInterestColumn = "Set Interest";
	private final String setRepetitionsColumn = "Set Repetitions";
	private final String effectiveInterestColumn = "Effective Interest";
	private final String effectiveRepetitionsColumn = "Effective Repetitions";

	private ArrayList<ListIRI> userTypes;

	@Override
	protected void initialiseOWLView() throws Exception {
		initialiseNaturalOWL();

		refresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				refresh();
			}
		});

		userModelTableModifierSelectionModel.addListener(getOWLModifierSelectionModelListener());

		userModelTablePropertySelectionModel.addListener(getOWLPropertySelectionModelListener());

		userModelTableClassSelectionModel.addListener(getOWLClassSelectionModelListener());

		userModelTableIndivSelectionModel.addListener(getOWLIndividualSelectionModelListener());

		// setLayout(new FlowLayout());

		globalPanel = new JPanel();
		globalPanel.setLayout(new BorderLayout());

		userTypePanel = new JPanel();
		userTypePanel.setLayout(new BorderLayout());
		
		globalTable = fillGlobalTable();
		table = fillUserTypeTable();
		
		setLayout(new BorderLayout());

		globalPanel.add(globalTable.getTableHeader(), BorderLayout.PAGE_START);
		globalPanel.add(globalTable, BorderLayout.CENTER);		
		
		userTypePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		scrollTable = new JScrollPane(table);
		userTypePanel.add(scrollTable, BorderLayout.CENTER);
		
		add(globalPanel, BorderLayout.PAGE_START);
		add(userTypePanel, BorderLayout.CENTER);

		panelExtra = new JPanel();
		panelExtra.setLayout(new BoxLayout(panelExtra, BoxLayout.PAGE_AXIS));

		panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout());

		addUT = new JButton("Add user type");
		removeUT = new JButton("Remove user type");
		duplicateUT = new JButton("Duplicate user type");

		addUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = addRow();
			}
		});

		removeUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = removeRow();
			}
		});

		duplicateUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = duplicateRow();
			}
		});

		panelButtons.add(addUT);
		panelButtons.add(removeUT);
		panelButtons.add(duplicateUT);
		panelExtra.add(panelButtons);

		JPanel panelLabel = new JPanel();
		previewLabel = new JLabel("Set User Type properties.");

		panelLabel.add(previewLabel);
		panelExtra.add(panelLabel);

		add(panelExtra, BorderLayout.SOUTH);
	}

	private void refresh() {
		if (table.getTableHeader() != null)
			remove(table.getTableHeader());
		if (scrollTable != null)
			remove(scrollTable);
		if (panelExtra != null)
			remove(panelExtra);

		globalPanel = new JPanel();
		globalPanel.setLayout(new BorderLayout());

		userTypePanel = new JPanel();
		userTypePanel.setLayout(new BorderLayout());

		globalTable = fillGlobalTable();
		table = fillUserTypeTable();
		setLayout(new BorderLayout());

		globalPanel.add(globalTable.getTableHeader(), BorderLayout.PAGE_START);
		globalPanel.add(globalTable, BorderLayout.CENTER);		
		
		userTypePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		scrollTable = new JScrollPane(table);
		userTypePanel.add(scrollTable, BorderLayout.CENTER);
		
		add(globalPanel, BorderLayout.PAGE_START);
		add(userTypePanel, BorderLayout.CENTER);

		panelExtra = new JPanel();
		panelExtra.setLayout(new BoxLayout(panelExtra, BoxLayout.PAGE_AXIS));

		panelButtons = new JPanel();
		panelButtons.setLayout(new FlowLayout());

		addUT = new JButton("Add user type");
		removeUT = new JButton("Remove user type");
		duplicateUT = new JButton("Duplicate user type");

		addUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = addRow();
			}
		});

		removeUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = removeRow();
			}
		});

		duplicateUT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table = duplicateRow();
			}
		});

		panelButtons.add(addUT);
		panelButtons.add(removeUT);
		panelButtons.add(duplicateUT);
		panelExtra.add(panelButtons);

		JPanel panelLabel = new JPanel();
		previewLabel = new JLabel("Set User Type properties.");

		panelLabel.add(previewLabel);
		panelExtra.add(panelLabel);

		add(panelExtra, BorderLayout.SOUTH);
	}

	public JTable addRow() {
		DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();

		IRI userTypeIRI = IRI.create(NLResourceManager.resourcesNS + "NewUser");

		int n = 1;
		while (!isUniqueIRI(userTypeIRI)) {
			n++;
			userTypeIRI = IRI.create(NLResourceManager.resourcesNS + "NewUser" + n);
		}

		UMQM.addUserModel(userTypeIRI);
		dirtenOntologies();

		UserModel UT = UMQM.getUserModel(userTypeIRI);
		defaultModel.addRow(new Object[] { new ListIRI(UT.getUMIRI()),
				UT.getMaxMessagesPerPage(), UT.getMaxMessagesPerSentence(),
				UT.getGlobalInterest(), UT.getGlobalRepetitions() });
		table.setModel(defaultModel);

		if ((selectedProperty == null) && (selectedModifier == null)) {
			updateUserTypeTable();
		} else {
			updateInterestTable();
		}

		return table;
	}

	public JTable removeRow() {
		DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
		int rowIndex = table.getSelectedRow();
		// if a row is selected, get confirmation dialog box
		if (rowIndex != -1) {
			ListIRI userTypeIRI = (ListIRI) defaultModel
					.getValueAt(rowIndex, 0);

			if (!userTypeIRI.getEntryIRI().equals(
					NLResourceManager.globalUserModel.getIRI())) {
				int selection = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete "
								+ userTypeIRI.getEntryIRI().getFragment()
								+ " user type?");
				if (selection == JOptionPane.OK_OPTION) {
					UMQM.deleteUserModel(userTypeIRI.getEntryIRI());
					dirtenOntologies();

					defaultModel.removeRow(rowIndex);
					table.setModel(defaultModel);

					if ((selectedProperty == null)
							&& (selectedModifier == null)) {
						updateUserTypeTable();
					} else {
						updateInterestTable();
					}
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"You cannot delete the global user type.");
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Please select a row in the user type table.");
		}

		return table;
	}

	public JTable duplicateRow() {
		DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
		int rowIndex = table.getSelectedRow();

		// if a row is selected, get confirmation dialog box
		if (rowIndex != -1) {
			ListIRI fromIRI = (ListIRI) defaultModel.getValueAt(rowIndex, 0);

			if (!fromIRI.getEntryIRI().equals(
					NLResourceManager.globalUserModel.getIRI())) {
				IRI toIRI = IRI.create(fromIRI.getEntryIRI().toString()
						+ "_copy");

				int n = 1;
				while (!isUniqueIRI(toIRI)) {
					n++;
					toIRI = IRI.create(fromIRI.getEntryIRI().toString()
							+ "_copy" + n);
				}

				UMQM.duplicateUserModel(fromIRI.getEntryIRI(), toIRI);
				dirtenOntologies();

				UserModel UT = UMQM.getUserModel(toIRI);
				defaultModel.addRow(new Object[] { new ListIRI(UT.getUMIRI()),
						UT.getMaxMessagesPerPage(),
						UT.getMaxMessagesPerSentence(), UT.getGlobalInterest(),
						UT.getGlobalRepetitions() });
				table.setModel(defaultModel);

				if ((selectedProperty == null) && (selectedModifier == null)) {
					updateUserTypeTable();
				} else {
					updateInterestTable();
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"You cannot duplicate the global user type.");
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Please select a row in the user type table.");
		}

		return table;
	}

	public void updateUserTypeTable() {
		globalPanel.remove(globalTable.getTableHeader());
		globalPanel.remove(globalTable);
		globalTable = fillGlobalTable();
		globalPanel.add(globalTable.getTableHeader(), BorderLayout.PAGE_START);
		globalPanel.add(globalTable, BorderLayout.CENTER);

		globalTable.validate();
		globalTable.repaint();
		
		userTypePanel.remove(table.getTableHeader());
		userTypePanel.remove(scrollTable);
		table = fillUserTypeTable();
		userTypePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		scrollTable = new JScrollPane(table);
		userTypePanel.add(scrollTable, BorderLayout.CENTER);

		table.validate();
		table.repaint();
		
		validate();
		repaint();
	}

	public void updateInterestTable() {
		globalPanel.remove(globalTable.getTableHeader());
		globalPanel.remove(globalTable);
		globalTable = fillGlobalInterestTable();
		globalPanel.add(globalTable.getTableHeader(), BorderLayout.PAGE_START);
		globalPanel.add(globalTable, BorderLayout.CENTER);

		globalTable.validate();
		globalTable.repaint();
		
		userTypePanel.remove(table.getTableHeader());
		userTypePanel.remove(scrollTable);
		table = fillInterestTable();
		userTypePanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
		scrollTable = new JScrollPane(table);
		userTypePanel.add(scrollTable, BorderLayout.CENTER);

		table.validate();
		table.repaint();
		
		validate();
		repaint();
	}
	
	public JTable fillGlobalTable() {
		String[] columnNames = { userTypeColumn, globalInterestColumn, globalRepetitionsColumn, "", ""};

		Object[][] data = new Object[1][3];

		UserModel UModel = NaturalOWLTab.UMQM.getUserModel(NLResourceManager.globalUserModel.getIRI());

		data[0][0] = new ListIRI(NLResourceManager.globalUserModel.getIRI());
		data[0][1] = UModel.getGlobalInterest();
		data[0][2] = UModel.getGlobalRepetitions();

		if (data[0][2].toString().equals("0")) {
			data[0][2] = "INFINITE";
		}
		
		globalTable = new JTable(new DefaultTableModel(data, columnNames)) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					if (column == 0) {
						JComponent jc = (JComponent) c;
						jc.setToolTipText("<html><b>This is a default resource of the system.</b> <br>" + ((ListIRI) getValueAt(row, column)).getToolTipText() + "</html>");
					} else {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(null);
					}
				}
				return c;
			}

            public boolean isCellEditable(int row, int col) {
                if(col == 0 || col == 3 || col == 4){
                    return false;
                }
                return true;
            }
		};

		TableColumn column = null;
		for (int i = 0; i < globalTable.getColumnCount(); i++) {
			column = globalTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		globalTable.getTableHeader().setReorderingAllowed(false);
		// allows rows selection, used in removeRow
		globalTable.setCellSelectionEnabled(true);
		globalTable.getTableHeader().setResizingAllowed(false);
		globalTable.getModel().addTableModelListener(this);

		return globalTable;
	}
	
	public JTable fillGlobalInterestTable() {
		String[] columnNames = { userTypeColumn, setInterestColumn,
				setRepetitionsColumn, "", ""};

		// to be filled with meaningful data
		Object[][] data = new Object[1][5];

		UserModel UModel = NaturalOWLTab.UMQM.getUserModel(NLResourceManager.globalUserModel.getIRI());

		data[0][0] = new ListIRI(NLResourceManager.globalUserModel.getIRI());
	
		if ((selectedIndiv != null)) {
			if (selectedModifier != null) {
				data[0][1] = NaturalOWLTab.UMQM.getInstanceLevelInterest(
						selectedProperty.getEntryIRI(),
						selectedIndiv.getEntryIRI(),
						selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM
						.getInstanceLevelRepetitions(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(),
								selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
			} else {
				data[0][1] = NaturalOWLTab.UMQM.getInstanceLevelInterest(
						selectedProperty.getEntryIRI(),
						selectedIndiv.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM
						.getInstanceLevelRepetitions(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
			}
		} else if (selectedClass != null) {
			if (selectedModifier != null) {
				data[0][1] = NaturalOWLTab.UMQM.getClassLevelInterest(
						selectedProperty.getEntryIRI(),
						selectedClass.getEntryIRI(),
						selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM.getClassLevelRepetitions(
						selectedProperty.getEntryIRI(),
						selectedClass.getEntryIRI(),
						selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
			} else {
				data[0][1] = NaturalOWLTab.UMQM.getClassLevelInterest(
						selectedProperty.getEntryIRI(),
						selectedClass.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM.getClassLevelRepetitions(
						selectedProperty.getEntryIRI(),
						selectedClass.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
			}
		} else if (selectedProperty != null) {
			if (selectedModifier != null) {
				data[0][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
						selectedProperty.getEntryIRI(),
						selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM
						.getPropertyLevelRepetitions(
								selectedProperty.getEntryIRI(),
								selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
			} else {
				data[0][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
						selectedProperty.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
				data[0][2] = NaturalOWLTab.UMQM
						.getPropertyLevelRepetitions(
								selectedProperty.getEntryIRI(), null, NLResourceManager.globalUserModel.getIRI());
			}
		} else if (selectedModifier != null) {
			data[0][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
					null, selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
			data[0][2] = NaturalOWLTab.UMQM.getPropertyLevelRepetitions(
					null, selectedModifier.getEntryIRI(), NLResourceManager.globalUserModel.getIRI());
		}

		if (data[0][2].toString().equals("0")) {
			data[0][2] = "INFINITE";
		}

		globalTable = new JTable(new DefaultTableModel(data, columnNames)) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					if (column == 0) {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(((ListIRI) getValueAt(row, column)).getToolTipText());
					} else {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(null);
					}
				}
				return c;
			}

            public boolean isCellEditable(int row, int col) {
                if(col == 0 || col == 3 || col == 4){
                    return false;
                }
                return true;
            }
		};

		TableColumn column = null;

		for (int i = 0; i < globalTable.getColumnCount(); i++) {
			column = globalTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		globalTable.getTableHeader().setReorderingAllowed(false);
		// allows rows selection, used in removeRow
		globalTable.setCellSelectionEnabled(true);
		globalTable.getTableHeader().setResizingAllowed(false);
		globalTable.getModel().addTableModelListener(this);

		return globalTable;
	}

	public JTable fillUserTypeTable() {
		String[] columnNames = { userTypeColumn,  globalInterestColumn,
				globalRepetitionsColumn, maxMessagesPerPageColumn,
				maxMessagesPerSentenceColumn};
		userTypes = new ArrayList<ListIRI>();

		// to be filled with meaningful data

		Object[][] data = new Object[NaturalOWLTab.UMQM.getUserModels().size() - 1][5];

		int row = 0;
		for (IRI UMIRI : NaturalOWLTab.UMQM.getUserModels()) {
			if (!UMIRI.equals(NLResourceManager.globalUserModel.getIRI())) {
				UserModel UModel = NaturalOWLTab.UMQM.getUserModel(UMIRI);
	
				userTypes.add(new ListIRI(UMIRI));
				data[row][0] = new ListIRI(UMIRI);
				data[row][1] = UModel.getGlobalInterest();
				data[row][2] = UModel.getGlobalRepetitions();
				data[row][3] = UModel.getMaxMessagesPerPage();
				data[row][4] = UModel.getMaxMessagesPerSentence();
				
				if (data[row][2].toString().equals("0")) {
					data[row][2] = "INFINITE";
				}
				if (data[row][3].toString().equals("-1")) {
					data[row][3] = "ALL";
				}
				
				row++;
			}
		}

		table = new JTable(new DefaultTableModel(data, columnNames)) {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					if (column == 0) {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(((ListIRI) getValueAt(row, column)).getToolTipText());
					} else {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(null);
					}
				}
				return c;
			}
		};

		TableColumn column = null;
		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		table.getTableHeader().setReorderingAllowed(false);
		// allows rows selection, used in removeRow
		table.setCellSelectionEnabled(true);
		table.getTableHeader().setResizingAllowed(false);
		table.getModel().addTableModelListener(this);

		return table;
	}

	public JTable fillInterestTable() {
		String[] columnNames = { userTypeColumn, setInterestColumn,
				setRepetitionsColumn, effectiveInterestColumn,
				effectiveRepetitionsColumn };
		userTypes = new ArrayList<ListIRI>();

		// to be filled with meaningful data
		Object[][] data = new Object[NaturalOWLTab.UMQM.getUserModels().size() - 1][5];

		int row = 0;
		for (IRI UMIRI : NaturalOWLTab.UMQM.getUserModels()) {
			if (!UMIRI.equals(NLResourceManager.globalUserModel.getIRI())) {
				userTypes.add(new ListIRI(UMIRI));
				data[row][0] = new ListIRI(UMIRI);
	
				if ((selectedIndiv != null)) {
					if (selectedModifier != null) {
						data[row][1] = NaturalOWLTab.UMQM.getInstanceLevelInterest(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][2] = NaturalOWLTab.UMQM
								.getInstanceLevelRepetitions(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(),
										selectedModifier.getEntryIRI(), UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
					} else {
						data[row][1] = NaturalOWLTab.UMQM.getInstanceLevelInterest(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(), null, UMIRI);
						data[row][2] = NaturalOWLTab.UMQM
								.getInstanceLevelRepetitions(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(), null, UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(), null, UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								selectedIndiv.getEntryIRI(), null, UMIRI);
					}
				} else if (selectedClass != null) {
					if (selectedModifier != null) {
						data[row][1] = NaturalOWLTab.UMQM.getClassLevelInterest(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][2] = NaturalOWLTab.UMQM.getClassLevelRepetitions(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
					} else {
						data[row][1] = NaturalOWLTab.UMQM.getClassLevelInterest(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(), null, UMIRI);
						data[row][2] = NaturalOWLTab.UMQM.getClassLevelRepetitions(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(), null, UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(), null, UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								selectedClass.getEntryIRI(), null, UMIRI);
					}
				} else if (selectedProperty != null) {
					if (selectedModifier != null) {
						data[row][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
								selectedProperty.getEntryIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][2] = NaturalOWLTab.UMQM
								.getPropertyLevelRepetitions(
										selectedProperty.getEntryIRI(),
										selectedModifier.getEntryIRI(), UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								getOWLModelManager().getOWLDataFactory()
										.getOWLThing().getIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								getOWLModelManager().getOWLDataFactory()
										.getOWLThing().getIRI(),
								selectedModifier.getEntryIRI(), UMIRI);
					} else {
						data[row][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
								selectedProperty.getEntryIRI(), null, UMIRI);
						data[row][2] = NaturalOWLTab.UMQM
								.getPropertyLevelRepetitions(
										selectedProperty.getEntryIRI(), null, UMIRI);
	
						data[row][3] = NaturalOWLTab.UMQM.getInterest(
								selectedProperty.getEntryIRI(),
								getOWLModelManager().getOWLDataFactory()
										.getOWLThing().getIRI(), null, UMIRI);
						data[row][4] = NaturalOWLTab.UMQM.getRepetitions(
								selectedProperty.getEntryIRI(),
								getOWLModelManager().getOWLDataFactory()
										.getOWLThing().getIRI(), null, UMIRI);
					}
				} else if (selectedModifier != null) {
					data[row][1] = NaturalOWLTab.UMQM.getPropertyLevelInterest(
							null, selectedModifier.getEntryIRI(), UMIRI);
					data[row][2] = NaturalOWLTab.UMQM.getPropertyLevelRepetitions(
							null, selectedModifier.getEntryIRI(), UMIRI);
	
					data[row][3] = NaturalOWLTab.UMQM.getInterest(null,
							getOWLModelManager().getOWLDataFactory().getOWLThing()
									.getIRI(), selectedModifier.getEntryIRI(),
							UMIRI);
					data[row][4] = NaturalOWLTab.UMQM.getRepetitions(null,
							getOWLModelManager().getOWLDataFactory().getOWLThing()
									.getIRI(), selectedModifier.getEntryIRI(),
							UMIRI);
				}
				
				if (data[row][2].toString().equals("0")) {
					data[row][2] = "INFINITE";
				}				
				if (data[row][4].toString().equals("0")) {
					data[row][4] = "INFINITE";
				}
	
				row++;
			}
		}

		table = new JTable(new DefaultTableModel(data, columnNames)) {
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					if (column == 0) {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(((ListIRI) getValueAt(row, column)).getToolTipText());
					} else {
						JComponent jc = (JComponent) c;
						jc.setToolTipText(null);
					}
				}
				return c;
			}

			public boolean isCellEditable(int row, int col) {
				if ((col == 3) || (col == 4)) {
					return false;
				} else {
					return true;
				}
			}
		};

		TableColumn column = null;

		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(25);
		}

		table.getTableHeader().setReorderingAllowed(false);
		// allows rows selection, used in removeRow
		table.setCellSelectionEnabled(true);
		table.getTableHeader().setResizingAllowed(false);
		table.getModel().addTableModelListener(this);

		return table;
	}

	private void updatePreviewLabel() {
		if ((selectedProperty == null) && (selectedClass == null)
				&& (selectedIndiv == null) && (selectedModifier == null)) {
			previewLabel.setText("Set User Type properties.");
		} else if ((selectedProperty != null) && (selectedClass == null)
				&& (selectedIndiv == null) && (selectedModifier == null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment() + ".");
		} else if ((selectedProperty != null) && (selectedClass == null)
				&& (selectedIndiv == null) && (selectedModifier != null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment() + " ("
					+ selectedModifier.getEntryIRI().getFragment() + ")" + ".");
		} else if ((selectedProperty != null) && (selectedClass != null)
				&& (selectedIndiv == null) && (selectedModifier == null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment()
					+ " and class " + selectedClass.getEntryIRI().getFragment()
					+ ".");
		} else if ((selectedProperty != null) && (selectedClass != null)
				&& (selectedIndiv == null) && (selectedModifier != null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment() + " ("
					+ selectedModifier.getEntryIRI().getFragment() + ")"
					+ " and class " + selectedClass.getEntryIRI().getFragment()
					+ ".");
		} else if ((selectedProperty != null) && (selectedClass != null)
				&& (selectedIndiv != null) && (selectedModifier == null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment()
					+ " and individual "
					+ selectedIndiv.getEntryIRI().getFragment() + ".");
		} else if ((selectedProperty != null) && (selectedClass != null)
				&& (selectedIndiv != null) && (selectedModifier != null)) {
			previewLabel.setText("Set interest/repetitions for property "
					+ selectedProperty.getEntryIRI().getFragment() + " ("
					+ selectedModifier.getEntryIRI().getFragment() + ")"
					+ " and individual "
					+ selectedIndiv.getEntryIRI().getFragment() + ".");
		} else if ((selectedProperty == null) && (selectedClass == null)
				&& (selectedIndiv == null) && (selectedModifier != null)) {
			previewLabel
					.setText("Set interest/repetitions for property modifier "
							+ selectedModifier.getEntryIRI().getFragment()
							+ ".");
		}
	}

	@Override
	protected void disposeOWLView() {
		super.disposeOWLView();
		getOWLModelManager().removeListener(modelListener);
	}

	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();

		TableModel model = (TableModel) e.getSource();
		TableModel userTypeModel = table.getModel();
		String columnName = model.getColumnName(column);

		if (columnName.equals(userTypeColumn)) {
			ListIRI oldUserTypeIRI = userTypes.get(row);

			if (model.getValueAt(row, column) instanceof String) {
				String newName = (String) (model.getValueAt(row, column));

				IRI newUserTypeIRI = IRI.create(oldUserTypeIRI.getEntryIRI().getStart() + newName);
				if (!newName.equals(oldUserTypeIRI.getEntryIRI().getFragment())) {
					if (!newName.equals("") && isLegalIRI(newUserTypeIRI) && isUniqueIRI(newUserTypeIRI)) {
						UMQM.renameUserModel(oldUserTypeIRI.getEntryIRI(), newUserTypeIRI);
						model.setValueAt(new ListIRI(newUserTypeIRI), row, column);
						dirtenOntologies();
					} else {
						JOptionPane.showMessageDialog(null, "The name you input is invalid or already exists. Please choose a different name.");
						model.setValueAt(oldUserTypeIRI, row, column);
					}
				} else {
					model.setValueAt(oldUserTypeIRI, row, column);
				}
			}
		} else if (columnName.equals(maxMessagesPerPageColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");
			int oldNumber = UMQM.getMaxMessagesPerPage(userTypeIRI
					.getEntryIRI());
			
			if (number.equalsIgnoreCase("ALL")) {
				int newNumber = UserModel.USER_MODEL_ALL_MESSAGES_PER_PAGE;
				if (newNumber != oldNumber) {
					UMQM.setMaxMessagesPerPage(userTypeIRI.getEntryIRI(), newNumber);
					model.setValueAt("ALL", row, column);
					dirtenOntologies();
				}				
			} else {
				try {
					if (Integer.parseInt(number) == UserModel.USER_MODEL_ALL_MESSAGES_PER_PAGE) {
						int newNumber = UserModel.USER_MODEL_ALL_MESSAGES_PER_PAGE;
						if (newNumber != oldNumber) {
							UMQM.setMaxMessagesPerPage(userTypeIRI.getEntryIRI(), newNumber);
							model.setValueAt("ALL", row, column);
							dirtenOntologies();
						}
				    } else if (isValidSize(number)) {
						int newNumber = Integer.parseInt(number);
						if (newNumber != oldNumber) {
							UMQM.setMaxMessagesPerPage(userTypeIRI.getEntryIRI(), newNumber);
							dirtenOntologies();
						}
					} else {
						model.setValueAt(oldNumber, row, column);
					}
	            }
	            catch (NumberFormatException nfe) {
	            	model.setValueAt(oldNumber, row, column);
	            }
			}
		} else if (columnName.equals(maxMessagesPerSentenceColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");
			int oldNumber = UMQM.getMaxMessagesPerSentence(userTypeIRI.getEntryIRI());
			
			try {
				if (isValidSize(number)) {
					int newNumber = Integer.parseInt(number);
					if (newNumber != oldNumber) {
						UMQM.setMaxMessagesPerSentence(userTypeIRI.getEntryIRI(), newNumber);
						dirtenOntologies();
					}
				} else if (Integer.parseInt(number) == -1) {
					int newNumber = Integer.parseInt(number);
					if (newNumber != oldNumber) {
						UMQM.setMaxMessagesPerSentence(userTypeIRI.getEntryIRI(), newNumber);
						model.setValueAt(oldNumber, row, column);
						dirtenOntologies();
					}
			    } else {
					model.setValueAt(oldNumber, row, column);
				}
            }
            catch (NumberFormatException nfe) {
            	model.setValueAt(oldNumber, row, column);
            }
		} else if (columnName.equals(globalInterestColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");
			int oldNumber = UMQM.getGlobalInterest(userTypeIRI.getEntryIRI());
			try {
				if (isValidInterest(number)) {
					int newNumber = Integer.parseInt(number);
					if (newNumber != oldNumber) {
						UMQM.setGlobalInterest(userTypeIRI.getEntryIRI(), newNumber);
						dirtenOntologies();
					}
				} else {
					model.setValueAt(oldNumber, row, column);
				}
            }
            catch (NumberFormatException nfe) {
            	model.setValueAt(oldNumber, row, column);
            }
		} else if (columnName.equals(globalRepetitionsColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");
			int oldNumber = UMQM.getGlobalRepetitions(userTypeIRI.getEntryIRI());
			
			if (number.equalsIgnoreCase("INFINITE")) {
				int newNumber = UserModel.USER_MODEL_INFINITE_REPETITIONS;
				if (newNumber != oldNumber) {
					UMQM.setGlobalRepetitions(userTypeIRI.getEntryIRI(), newNumber);
					model.setValueAt("INFINITE", row, column);
					dirtenOntologies();
				}				
			} else {
				try {
					if (Integer.parseInt(number) == UserModel.USER_MODEL_INFINITE_REPETITIONS) {
						int newNumber = UserModel.USER_MODEL_INFINITE_REPETITIONS;
						if (newNumber != oldNumber) {
							UMQM.setGlobalRepetitions(userTypeIRI.getEntryIRI(), newNumber);
							model.setValueAt("INFINITE", row, column);
							dirtenOntologies();
						}
					} else if (isValidSize(number)) {
						int newNumber = Integer.parseInt(number);
						if (newNumber != oldNumber) {
							UMQM.setGlobalRepetitions(userTypeIRI.getEntryIRI(), newNumber);
							dirtenOntologies();
						}
					} else {
						model.setValueAt(oldNumber, row, column);
					}
	            }
	            catch (NumberFormatException nfe) {
	            	model.setValueAt(oldNumber, row, column);
	            }
			}
		}

		else if (columnName.equals(setInterestColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");

			int oldNumber = -1;
			if ((selectedIndiv != null)) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getInstanceLevelInterest(
							selectedProperty.getEntryIRI(),
							selectedIndiv.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getInstanceLevelInterest(
							selectedProperty.getEntryIRI(),
							selectedIndiv.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedClass != null) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getClassLevelInterest(
							selectedProperty.getEntryIRI(),
							selectedClass.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getClassLevelInterest(
							selectedProperty.getEntryIRI(),
							selectedClass.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedProperty != null) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getPropertyLevelInterest(
							selectedProperty.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getPropertyLevelInterest(
							selectedProperty.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedModifier != null) {
				oldNumber = UMQM.getPropertyLevelInterest(null,
						selectedModifier.getEntryIRI(),
						userTypeIRI.getEntryIRI());
			}

			if (isValidInterest(number)) {
				try {
					int newNumber = Integer.parseInt(number);
					if (newNumber != oldNumber) {
											
						if ((selectedIndiv != null)) {
							if (selectedModifier != null) {
								UMQM.setInstanceLevelInterest(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI()), row, 3);
							} else {
								UMQM.setInstanceLevelInterest(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(), null,
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										selectedIndiv.getEntryIRI(), null,
										userTypeIRI.getEntryIRI()), row, 3);
							}
						} else if (selectedClass != null) {
							if (selectedModifier != null) {
								UMQM.setClassLevelInterest(
										selectedProperty.getEntryIRI(),
										selectedClass.getEntryIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										selectedClass.getEntryIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI()), row, 3);
							} else {
								UMQM.setClassLevelInterest(
										selectedProperty.getEntryIRI(),
										selectedClass.getEntryIRI(), null,
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										selectedClass.getEntryIRI(), null,
										userTypeIRI.getEntryIRI()), row, 3);
							}
						} else if (selectedProperty != null) {
							if (selectedModifier != null) {
								UMQM.setPropertyLevelInterest(
										selectedProperty.getEntryIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										getOWLModelManager().getOWLDataFactory()
												.getOWLThing().getIRI(),
										selectedModifier.getEntryIRI(),
										userTypeIRI.getEntryIRI()), row, 3);
							} else {
								UMQM.setPropertyLevelInterest(
										selectedProperty.getEntryIRI(), null,
										userTypeIRI.getEntryIRI(), newNumber);
								userTypeModel.setValueAt(UMQM.getInterest(
										selectedProperty.getEntryIRI(),
										getOWLModelManager().getOWLDataFactory()
												.getOWLThing().getIRI(), null,
										userTypeIRI.getEntryIRI()), row, 3);
							}
						} else if (selectedModifier != null) {
							UMQM.setPropertyLevelInterest(null,
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getInterest(null,
									getOWLModelManager().getOWLDataFactory()
											.getOWLThing().getIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI()), row, 3);
						}
						dirtenOntologies();
					}
	            }
	            catch (NumberFormatException nfe) {
	            	model.setValueAt(oldNumber, row, column);
	            }
			} else {
				model.setValueAt(oldNumber, row, column);
			}
		} else if (columnName.equals(setRepetitionsColumn)) {
			ListIRI userTypeIRI = (ListIRI) model.getValueAt(row, 0);

			String number = (String) (model.getValueAt(row, column) + "");

			int oldNumber = -1;
			if ((selectedIndiv != null)) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getInstanceLevelRepetitions(
							selectedProperty.getEntryIRI(),
							selectedIndiv.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getInstanceLevelRepetitions(
							selectedProperty.getEntryIRI(),
							selectedIndiv.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedClass != null) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getClassLevelRepetitions(
							selectedProperty.getEntryIRI(),
							selectedClass.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getClassLevelRepetitions(
							selectedProperty.getEntryIRI(),
							selectedClass.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedProperty != null) {
				if (selectedModifier != null) {
					oldNumber = UMQM.getPropertyLevelRepetitions(
							selectedProperty.getEntryIRI(),
							selectedModifier.getEntryIRI(),
							userTypeIRI.getEntryIRI());
				} else {
					oldNumber = UMQM.getPropertyLevelRepetitions(
							selectedProperty.getEntryIRI(), null,
							userTypeIRI.getEntryIRI());
				}
			} else if (selectedModifier != null) {
				oldNumber = UMQM.getPropertyLevelRepetitions(null,
						selectedModifier.getEntryIRI(),
						userTypeIRI.getEntryIRI());
			}

			if (number.equalsIgnoreCase("INFINITE") || isValidInterest(number)) {
				int newNumber;
				if (number.equalsIgnoreCase("INFINITE")) {
					newNumber = UserModel.USER_MODEL_INFINITE_REPETITIONS;
				} else {
					try {					
						newNumber = Integer.parseInt(number);
		            }
		            catch (NumberFormatException nfe) {
		            	newNumber = oldNumber;
		            	model.setValueAt(oldNumber, row, column);
		            }
				}
				
				if (newNumber != oldNumber) {
					if ((selectedIndiv != null)) {
						if (selectedModifier != null) {
							UMQM.setInstanceLevelRepetitions(
									selectedProperty.getEntryIRI(),
									selectedIndiv.getEntryIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									selectedIndiv.getEntryIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI()), row, 4);
						} else {
							UMQM.setInstanceLevelRepetitions(
									selectedProperty.getEntryIRI(),
									selectedIndiv.getEntryIRI(), null,
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									selectedIndiv.getEntryIRI(), null,
									userTypeIRI.getEntryIRI()), row, 4);
						}
					} else if (selectedClass != null) {
						if (selectedModifier != null) {
							UMQM.setClassLevelRepetitions(
									selectedProperty.getEntryIRI(),
									selectedClass.getEntryIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									selectedClass.getEntryIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI()), row, 4);
						} else {
							UMQM.setClassLevelRepetitions(
									selectedProperty.getEntryIRI(),
									selectedClass.getEntryIRI(), null,
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									selectedClass.getEntryIRI(), null,
									userTypeIRI.getEntryIRI()), row, 4);
						}
					} else if (selectedProperty != null) {
						if (selectedModifier != null) {
							UMQM.setPropertyLevelRepetitions(
									selectedProperty.getEntryIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									getOWLModelManager().getOWLDataFactory()
											.getOWLThing().getIRI(),
									selectedModifier.getEntryIRI(),
									userTypeIRI.getEntryIRI()), row, 4);
						} else {
							UMQM.setPropertyLevelRepetitions(
									selectedProperty.getEntryIRI(), null,
									userTypeIRI.getEntryIRI(), newNumber);
							userTypeModel.setValueAt(UMQM.getRepetitions(
									selectedProperty.getEntryIRI(),
									getOWLModelManager().getOWLDataFactory()
											.getOWLThing().getIRI(), null,
									userTypeIRI.getEntryIRI()), row, 4);
						}
					} else if (selectedModifier != null) {
						UMQM.setPropertyLevelRepetitions(null,
								selectedModifier.getEntryIRI(),
								userTypeIRI.getEntryIRI(), newNumber);
						userTypeModel.setValueAt(UMQM.getRepetitions(null,
								getOWLModelManager().getOWLDataFactory()
										.getOWLThing().getIRI(),
								selectedModifier.getEntryIRI(),
								userTypeIRI.getEntryIRI()), row, 4);
					}
					
					if (newNumber == UserModel.USER_MODEL_INFINITE_REPETITIONS) {
						model.setValueAt("INFINITE", row, column);
						userTypeModel.setValueAt("INFINITE", row, 4);
					}					
					
					dirtenOntologies();
				}
			} else {
				model.setValueAt(oldNumber, row, column);
			}
		}
	}

	private OWLSelectionModelListener getOWLModifierSelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTableModifierSelectionModel
						.getSelectedEntity();

				if (selected != null) {
					selectedModifier = new ListIRI(selected.getIRI());
				} else {
					selectedModifier = null;
					userModelTableModifierSelectionModel = new OWLSelectionModelImpl();
					userModelTableModifierSelectionModel
							.addListener(getOWLModifierSelectionModelListener());
				}

				if ((selectedProperty == null) && (selectedModifier == null)) {
					updateUserTypeTable();
				} else {
					updateInterestTable();
				}
				updatePreviewLabel();
			}
		};
	}

	private OWLSelectionModelListener getOWLPropertySelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTablePropertySelectionModel
						.getSelectedEntity();

				if (selected != null) {
					selectedProperty = new ListIRI(selected.getIRI());
				} else {
					selectedProperty = null;
					userModelTablePropertySelectionModel = new OWLSelectionModelImpl();
					userModelTablePropertySelectionModel
							.addListener(getOWLPropertySelectionModelListener());
				}

				if ((selectedProperty == null) && (selectedModifier == null)) {
					updateUserTypeTable();
				} else {
					updateInterestTable();
				}
				updatePreviewLabel();
			}
		};
	}

	private OWLSelectionModelListener getOWLClassSelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTableClassSelectionModel
						.getSelectedEntity();

				if (selected != null) {
					selectedClass = new ListIRI(selected.getIRI());
				} else {
					selectedClass = null;
					userModelTableClassSelectionModel = new OWLSelectionModelImpl();
					userModelTableClassSelectionModel
							.addListener(getOWLClassSelectionModelListener());
				}

				if (selectedProperty != null) {
					updateInterestTable();
				}

				updatePreviewLabel();
			}
		};
	}

	private OWLSelectionModelListener getOWLIndividualSelectionModelListener() {
		return new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = userModelTableIndivSelectionModel
						.getSelectedEntity();

				if (selected != null) {
					selectedIndiv = new ListIRI(selected.getIRI());
				} else {
					selectedIndiv = null;
					userModelTableIndivSelectionModel = new OWLSelectionModelImpl();
					userModelTableIndivSelectionModel
							.addListener(getOWLIndividualSelectionModelListener());
				}

				if (selectedProperty != null) {
					updateInterestTable();
				}

				updatePreviewLabel();
			}
		};
	}

	public static boolean isValidInterest(String num) {
		try {
			int i = Integer.parseInt(num);

			if (i < -1) {
				return false;
			}
			if (i > 3) {
				return false;
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isValidSize(String num) {
		try {
			int i = Integer.parseInt(num);

			if (i < 1) {
				return false;
			}
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public void actionPerformed(ActionEvent arg0) {
	}

	public void itemStateChanged(ItemEvent arg0) {
	}
}