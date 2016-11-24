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
import gr.aueb.cs.nlg.NLGEngine.NLGEngine;
import gr.aueb.cs.nlg.Utils.NLGUser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelImpl;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GenerationTab extends NaturalOWLTab {
	private static final long serialVersionUID = 8268241587271333587L;
	
	private static final String PIPELINE_MODEL = "Pipeline model";
	private static final String ILP_MODEL = "ILP model";
	private static final String ILP_MODEL_APPROX = "ILP model (approx.)";
	
	private JPanel optionsPanelUnderSub2Pipeline;
	private JPanel optionsPanelUnderSub2ILP;
	
	private JLabel generationLabel;	
	private JLabel lamdaContentSection;
	private JLabel lamdaAggregation;
	private JLabel weightFactImportance;
	private JLabel weightSlotMinimization;
	private JLabel overridePipelineFactLimitFacts;
	private JLabel overridePipelineFactLimitSlots;
	private JLabel overrideILPFactLimitSlots;
	private JLabel overrideILPTextLimitSentences;
    private JButton errorButton;

	private JComboBox<String> languageCombo;
	private JComboBox<ListIRI> userTypeCombo;
	private JComboBox<String> distanceCombo;
	private JComboBox<String> engineCombo;
	
	private JCheckBox annotationCheck;
	private JCheckBox shapeCheck;
	private JCheckBox annotateGeneratedCheck;
	private JCheckBox generateComparisonsCheck;
	
	private JCheckBox overridePipelineFactLimitCheck;
	private JCheckBox overridePipelineSlotLimitCheck;
	private JCheckBox overrideILPSlotLimitCheck;
	private JCheckBox overrideILPTextLimitCheck;
	
	private JTextField overridePipelineFactLimitText;
	private JTextField overridePipelineSlotLimitText;
	private JTextField overrideILPSlotLimitText;
	private JTextField overrideILPTextLimitText;
	
	private JButton generateButton;
	private JButton resetButton;
	
	private JSlider lamdaSlider;
	private JScrollPane scroll;
	private JTextPane resultTextPane;

    private DecimalFormat df = new DecimalFormat("0.00");
	
	private OWLClass selectedClass = null;
	private OWLNamedIndividual selectedIndividual = null;
	
	private NLGUser user = null;
	private NLGEngine engine = null;

	private OWLModelManagerListener modelListener;
	
	private int pipelineFactLimit = 2;
	private int pipelineSlotLimit = 10;
	private int ILPSlotLimit = 10;
	private int ILPTextLimit = 20;

	protected void initialiseOWLView() throws Exception {
		generationClassSelectionModel.addListener(getOWLClassSelectionModelListener());		
		generationIndivSelectionModel.addListener(getOWLIndividualSelectionModelListener());

		refresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
		        refresh();
			}
		});
		
		setLayout(new BorderLayout());
		
		generateButton = new JButton("Generate text");
		resetButton = new JButton("Reset interaction history");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		
		JPanel optionsPanelOver = new JPanel();
		optionsPanelOver.setLayout(new BoxLayout(optionsPanelOver, BoxLayout.PAGE_AXIS));
		
		JPanel optionsPanelOverSub1 = new JPanel();
		optionsPanelOverSub1.setLayout(new FlowLayout(FlowLayout.LEADING));

		generationLabel = new JLabel();
		optionsPanelOverSub1.add(generationLabel);
		
		JPanel optionsPanelOverSub2 = new JPanel();
		optionsPanelOverSub2.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel languageLabel = new JLabel("For language: ");
        String[] languages = {"English", "Greek"};
		languageCombo = new JComboBox<String>(languages);
		
		JLabel userTypeLabel = new JLabel("For user type: ");		       
		userTypeCombo = new JComboBox<ListIRI>();
		updateUserTypeCombo();
		userTypeCombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				IRI userType = null;
				if (user != null) {
					userType = user.getUserModel().getUMIRI();
				}
				
				if (((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI().equals(NLResourceManager.globalUserModel.getIRI())) {
					user = new NLGUser("globalUser", UMQM.getGlobalUserModel());
				} else {
					user = new NLGUser("tempUser", UMQM.getUserModel(((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI()));
				}
				
				if (userType != null) {
					if (!userType.equals(user.getUserModel().getUMIRI())) {
						overridePipelineFactLimitCheck.setSelected(false);
						overridePipelineSlotLimitCheck.setSelected(false);
						overrideILPSlotLimitCheck.setSelected(false);
						overrideILPTextLimitCheck.setSelected(false);
						
						overridePipelineFactLimitText.setText("" + user.getUserModel().getMaxMessagesPerSentence());
						overridePipelineSlotLimitText.setText("" + 10);
						overrideILPSlotLimitText.setText("" + 10);
						overrideILPTextLimitText.setText("" + user.getUserModel().getMaxMessagesPerPage());
					}
				}
			}
		});
		
		userTypeCombo.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				updateUserTypeCombo();
			}

			public void focusLost(FocusEvent arg0) {}			
		});
				
		optionsPanelOverSub2.add(languageLabel);
		optionsPanelOverSub2.add(languageCombo);
		optionsPanelOverSub2.add(Box.createRigidArea(new Dimension(10,10)));
		optionsPanelOverSub2.add(userTypeLabel);
		optionsPanelOverSub2.add(userTypeCombo);	
		
		JPanel optionsPanelOverSub3 = new JPanel();
		optionsPanelOverSub3.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		generateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
				if (selectedIndividual != null) {
					generateText(selectedIndividual.getIRI());
				} else if (selectedClass != null) {
					generateText(selectedClass.getIRI());
				}
			}			
		});
		
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				user.resetCounters();
			}
		});
		
		optionsPanelOverSub3.add(generateButton);
		optionsPanelOverSub3.add(Box.createRigidArea(new Dimension(10,10)));
		optionsPanelOverSub3.add(resetButton);

		optionsPanelOver.add(optionsPanelOverSub1);
		optionsPanelOver.add(optionsPanelOverSub2);
		optionsPanelOver.add(optionsPanelOverSub3);
		
		//Advanced Options Panel
		
		JPanel optionsPanelUnder = new JPanel();
		optionsPanelUnder.setLayout(new BoxLayout(optionsPanelUnder, BoxLayout.LINE_AXIS));
		
		JPanel optionsPanelUnderSub1 = new JPanel();
		optionsPanelUnderSub1.setLayout(new BoxLayout(optionsPanelUnderSub1, BoxLayout.PAGE_AXIS));

		JPanel optionsPanelUnderSub1Sub1 = new JPanel();
		optionsPanelUnderSub1Sub1.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JLabel engineLabel = new JLabel("Use engine: ");
	    String[] engines = {PIPELINE_MODEL, ILP_MODEL, ILP_MODEL_APPROX};
		engineCombo = new JComboBox<String>(engines);
		
		engineCombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (engineCombo.getSelectedItem().equals(PIPELINE_MODEL)) {
					optionsPanelUnderSub2Pipeline.setVisible(true);
					optionsPanelUnderSub2ILP.setVisible(false);
				} else {
					if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
						if (isValidNumber(overrideILPTextLimitText.getText())) {
							if (Integer.parseInt(overrideILPTextLimitText.getText()) > 3 || user.getUserModel().getMaxMessagesPerPage() > 3) {
								overrideILPTextLimitText.setText("3");
								overrideILPTextLimitCheck.setSelected(true);

								errorButton.setVisible(true);
							}
						}
					} else {
						errorButton.setVisible(false);
					}
					optionsPanelUnderSub2Pipeline.setVisible(false);
					optionsPanelUnderSub2ILP.setVisible(true);
				}
			}
		});
		
		optionsPanelUnderSub1Sub1.add(engineLabel);
		optionsPanelUnderSub1Sub1.add(engineCombo);
		
		optionsPanelUnderSub1.add(optionsPanelUnderSub1Sub1);

		//Options Panel Pipeline options
		
		optionsPanelUnderSub2Pipeline = new JPanel();
		optionsPanelUnderSub2Pipeline.setLayout(new BoxLayout(optionsPanelUnderSub2Pipeline, BoxLayout.PAGE_AXIS));
       
		JPanel optionsPanelUnderSub2PipelineSub1 = new JPanel();
		optionsPanelUnderSub2PipelineSub1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel distanceLabel = new JLabel("For maximum graph distance: ");
        String[] distances = {"1", "2"};
        distanceCombo = new JComboBox<String>(distances);
        
        optionsPanelUnderSub2PipelineSub1.add(distanceLabel);
        optionsPanelUnderSub2PipelineSub1.add(distanceCombo);	
        
		JPanel optionsPanelUnderSub2PipelineSub2 = new JPanel();
		optionsPanelUnderSub2PipelineSub2.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JLabel overrideSentenceLimitLabel = new JLabel("Override user model's sentence aggregation limit:");
		optionsPanelUnderSub2PipelineSub2.add(overrideSentenceLimitLabel);

		JPanel optionsPanelUnderSub2PipelineSub3 = new JPanel();
		optionsPanelUnderSub2PipelineSub3.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		overridePipelineFactLimitCheck = new JCheckBox("Limit sentence to ");
		overridePipelineFactLimitText = new JTextField(2);
		overridePipelineFactLimitFacts = new JLabel("facts.");
		
		overridePipelineFactLimitText.setEnabled(false);				
		overridePipelineFactLimitCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (overridePipelineFactLimitCheck.isSelected()) {
					overridePipelineFactLimitText.setEnabled(true);
				} else {
					overridePipelineFactLimitText.setEnabled(false);
				}
			}
		});
		
		overridePipelineFactLimitText.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void removeUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void insertUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			
			  public void checkValue() {
				  if (isValidNumber(overridePipelineFactLimitText.getText().trim())) {
					  pipelineFactLimit = Integer.parseInt(overridePipelineFactLimitText.getText());
			      } else {
			    	  overridePipelineFactLimitText.setText("" + pipelineFactLimit);
			      }
			  }
		});

		optionsPanelUnderSub2PipelineSub3.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2PipelineSub3.add(overridePipelineFactLimitCheck);
		optionsPanelUnderSub2PipelineSub3.add(overridePipelineFactLimitText);
		optionsPanelUnderSub2PipelineSub3.add(overridePipelineFactLimitFacts);

		JPanel optionsPanelUnderSub2PipelineSub4 = new JPanel();
		optionsPanelUnderSub2PipelineSub4.setLayout(new FlowLayout(FlowLayout.LEADING));

		overridePipelineSlotLimitCheck = new JCheckBox("Limit sentence to ");
		overridePipelineSlotLimitText = new JTextField(2);
		overridePipelineFactLimitSlots = new JLabel("slots.");
		
		overridePipelineSlotLimitText.setEnabled(false);				
		overridePipelineSlotLimitCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (overridePipelineSlotLimitCheck.isSelected()) {
					overridePipelineSlotLimitText.setEnabled(true);
				} else {
					overridePipelineSlotLimitText.setEnabled(false);
				}
			}
		});	
		
		overridePipelineSlotLimitText.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void removeUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void insertUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			
			  public void checkValue() {
				  if (isValidNumber(overridePipelineSlotLimitText.getText().trim())) {
					  pipelineSlotLimit = Integer.parseInt(overridePipelineSlotLimitText.getText());
			      } else {
			    	  overridePipelineSlotLimitText.setText("" + pipelineSlotLimit);
			      }
			  }
		});

		optionsPanelUnderSub2PipelineSub4.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2PipelineSub4.add(overridePipelineSlotLimitCheck);
		optionsPanelUnderSub2PipelineSub4.add(overridePipelineSlotLimitText);
		optionsPanelUnderSub2PipelineSub4.add(overridePipelineFactLimitSlots);	

		optionsPanelUnderSub2Pipeline.add(optionsPanelUnderSub2PipelineSub1);
		optionsPanelUnderSub2Pipeline.add(new JSeparator(SwingConstants.HORIZONTAL));
		optionsPanelUnderSub2Pipeline.add(optionsPanelUnderSub2PipelineSub2);
		optionsPanelUnderSub2Pipeline.add(optionsPanelUnderSub2PipelineSub3);
		optionsPanelUnderSub2Pipeline.add(optionsPanelUnderSub2PipelineSub4);

		//Options Panel ILP options
		
		optionsPanelUnderSub2ILP = new JPanel();
		optionsPanelUnderSub2ILP.setLayout(new BoxLayout(optionsPanelUnderSub2ILP, BoxLayout.PAGE_AXIS));

		JPanel optionsPanelUnderSub2ILPSub1 = new JPanel();
		optionsPanelUnderSub2ILPSub1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel lamdaLabel = new JLabel("Set the value of lamda:");
		optionsPanelUnderSub2ILPSub1.add(lamdaLabel);
		
		JPanel optionsPanelUnderSub2ILPSub2 = new JPanel();
		optionsPanelUnderSub2ILPSub2.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		lamdaSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		lamdaSlider.setPaintLabels(false);		
		
		lamdaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lamdaContentSection.setText(df.format((double)lamdaSlider.getValue()/100));
				lamdaAggregation.setText(df.format(1.0 - (double)lamdaSlider.getValue()/100));
				
				if (((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI().equals(NLResourceManager.globalUserModel.getIRI())) {
					user = new NLGUser("globalUser", UMQM.getGlobalUserModel());
				} else {
					user = new NLGUser("tempUser", UMQM.getUserModel(((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI()));
				}
			}
		});	
        
        optionsPanelUnderSub2ILPSub2.add(lamdaSlider);
        
		JPanel optionsPanelUnderSub2ILPSub3 = new JPanel();
		optionsPanelUnderSub2ILPSub3.setLayout(new FlowLayout(FlowLayout.LEADING));

		weightFactImportance = new JLabel("Weight on maximization of fact importance: ");		
		lamdaContentSection = new JLabel(df.format((double)lamdaSlider.getValue()/100));

		optionsPanelUnderSub2ILPSub3.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2ILPSub3.add(weightFactImportance);
		optionsPanelUnderSub2ILPSub3.add(lamdaContentSection);
        
		JPanel optionsPanelUnderSub2ILPSub4 = new JPanel();
		optionsPanelUnderSub2ILPSub4.setLayout(new FlowLayout(FlowLayout.LEADING));

		weightSlotMinimization = new JLabel("Weight on minimization of distinct slots: ");	
		lamdaAggregation = new JLabel(df.format(1.0 - (double)lamdaSlider.getValue()/100));

		optionsPanelUnderSub2ILPSub4.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2ILPSub4.add(weightSlotMinimization);
		optionsPanelUnderSub2ILPSub4.add(lamdaAggregation);
        
		JPanel optionsPanelUnderSub2ILPSub5 = new JPanel();
		optionsPanelUnderSub2ILPSub5.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel overrideTextLimitLabel = new JLabel("Override user model's text length limit:");
		optionsPanelUnderSub2ILPSub5.add(overrideTextLimitLabel);

		JPanel optionsPanelUnderSub2ILPSub6 = new JPanel();
		optionsPanelUnderSub2ILPSub6.setLayout(new FlowLayout(FlowLayout.LEADING));

		overrideILPTextLimitCheck = new JCheckBox("Limit text to ");
		overrideILPTextLimitText = new JTextField(2);
		overrideILPTextLimitSentences = new JLabel("sentences.");

		ClassLoader loader = NLNamesTab.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/error.png");
		ImageIcon errorIcon = new ImageIcon(imageURL);
		Image img = errorIcon.getImage(); 
		Image newimg = img.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);  
		errorIcon = new ImageIcon(newimg);
		
		errorButton = new JButton(errorIcon);
		errorButton.setBorder(null);
		errorButton.setFocusPainted(false);
		errorButton.setContentAreaFilled(false);
		errorButton.setToolTipText("Unacceptable max messages per page value: click to view error");
		errorButton.setVisible(false);
		
		errorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (user.getUserModel().getMaxMessagesPerPage() > 3) {
					JOptionPane.showMessageDialog(null, "The selected user type has greater 'max messages per page' (" + user.getUserModel().getMaxMessagesPerPage() + ") than advisable. \nThe 'ILP model' engine requires too much time for texts larger than 3 sentences! \nUse 'ILP model (approx.)' engine instead.", "Unacceptable max messages per page value", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "The 'ILP model' engine requires too much time for texts larger than 3 sentences! \nUse 'ILP model (approx.)' engine instead.", "Unacceptable max messages per page value", JOptionPane.ERROR_MESSAGE);
					errorButton.setVisible(false);
				}				
			}
		});
		
		overrideILPTextLimitText.setEnabled(false);				
		overrideILPTextLimitCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (overrideILPTextLimitCheck.isSelected()) {
					overrideILPTextLimitText.setEnabled(true);

					errorButton.setVisible(false);
				} else {
					overrideILPTextLimitText.setEnabled(false);

					if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
						if (user.getUserModel().getMaxMessagesPerPage() > 3) {
							overrideILPTextLimitText.setText("3");
							overrideILPTextLimitCheck.setSelected(true);
	
							errorButton.setVisible(true);
						}
				    }
				}
			}
		});
		
		overrideILPTextLimitText.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void removeUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void insertUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			
			  public void checkValue() {
				  if (isValidNumber(overrideILPTextLimitText.getText().trim())) {
					  if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
						  if (Integer.parseInt(overrideILPTextLimitText.getText()) > 3) {
							  overrideILPTextLimitText.setText("3");
							  overrideILPTextLimitCheck.setSelected(true);
							  overrideILPTextLimitText.setEnabled(true);

							  errorButton.setVisible(true);
						  }
					  } else {
						  errorButton.setVisible(false);
					  }
					  ILPTextLimit = Integer.parseInt(overrideILPTextLimitText.getText());
			      } else {
			    	  overrideILPTextLimitText.setText("" + ILPTextLimit);
					  errorButton.setVisible(false);
			      }
			  }
		});

		optionsPanelUnderSub2ILPSub6.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2ILPSub6.add(overrideILPTextLimitCheck);
		optionsPanelUnderSub2ILPSub6.add(overrideILPTextLimitText);
		optionsPanelUnderSub2ILPSub6.add(overrideILPTextLimitSentences);
		optionsPanelUnderSub2ILPSub6.add(errorButton);
		
		JPanel optionsPanelUnderSub2ILPSub7 = new JPanel();
		optionsPanelUnderSub2ILPSub7.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel overrideSentenceLimitLabel2 = new JLabel("Override user model's sentence aggregation limit:");
		optionsPanelUnderSub2ILPSub7.add(overrideSentenceLimitLabel2);

		JPanel optionsPanelUnderSub2ILPSub8 = new JPanel();
		optionsPanelUnderSub2ILPSub8.setLayout(new FlowLayout(FlowLayout.LEADING));

		overrideILPSlotLimitCheck = new JCheckBox("Limit sentence to ");
		overrideILPSlotLimitText = new JTextField(2);
		overrideILPFactLimitSlots = new JLabel("slots.");	
		
		overrideILPSlotLimitText.setEnabled(false);				
		overrideILPSlotLimitCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (overrideILPSlotLimitCheck.isSelected()) {
					overrideILPSlotLimitText.setEnabled(true);
				} else {
					overrideILPSlotLimitText.setEnabled(false);
				}
			}
		});	
		
		overrideILPSlotLimitText.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void removeUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			  public void insertUpdate(DocumentEvent e) {
				  SwingUtilities.invokeLater(new Runnable() {
					  public void run(){
						  checkValue();
					  }
				  });
			  }
			
			  public void checkValue() {
				  if (isValidNumber(overrideILPSlotLimitText.getText().trim())) {
					  ILPSlotLimit = Integer.parseInt(overrideILPSlotLimitText.getText());
			      } else {
			    	  overrideILPSlotLimitText.setText("" + ILPSlotLimit);
			      }
			  }
		});
		
		optionsPanelUnderSub2ILPSub8.add(Box.createRigidArea(new Dimension(5,5)));
		optionsPanelUnderSub2ILPSub8.add(overrideILPSlotLimitCheck);
		optionsPanelUnderSub2ILPSub8.add(overrideILPSlotLimitText);
		optionsPanelUnderSub2ILPSub8.add(overrideILPFactLimitSlots);

		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub1);
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub2);
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub3);
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub4);
		optionsPanelUnderSub2ILP.add(new JSeparator(SwingConstants.HORIZONTAL));
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub5);
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub6);
		optionsPanelUnderSub2ILP.add(new JSeparator(SwingConstants.HORIZONTAL));
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub7);
		optionsPanelUnderSub2ILP.add(optionsPanelUnderSub2ILPSub8);
		
		//Options Panel misc
		
		JPanel optionsPanelUnderSub3 = new JPanel();
		optionsPanelUnderSub3.setLayout(new BoxLayout(optionsPanelUnderSub3, BoxLayout.PAGE_AXIS));
		
		JPanel optionsPanelUnderSub3Sub1 = new JPanel();
		optionsPanelUnderSub3Sub1.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		annotationCheck = new JCheckBox("Show semantic and syntactic annotations.");
		annotationCheck.setSelected(false);
		
		annotationCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (annotationCheck.isSelected()) {
					shapeCheck.setSelected(false);
					annotateGeneratedCheck.setSelected(false);
					if (engine != null) {
						engine.setSectionParagraphs(shapeCheck.isSelected());
						engine.setAnnotateGeneratedResources(annotateGeneratedCheck.isSelected());
					}
				}
			}
		});
		
		optionsPanelUnderSub3Sub1.add(annotationCheck);
		
		JPanel optionsPanelUnderSub3Sub2 = new JPanel();
		optionsPanelUnderSub3Sub2.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		shapeCheck = new JCheckBox("Shape text paragraphs.");
		shapeCheck.setSelected(false);
		
		shapeCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (shapeCheck.isSelected()) {
					annotationCheck.setSelected(false);
				}
			}
		});
		
		optionsPanelUnderSub3Sub2.add(shapeCheck);
		
		JPanel optionsPanelUnderSub3Sub3 = new JPanel();
		optionsPanelUnderSub3Sub3.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		annotateGeneratedCheck = new JCheckBox("Markup use of default resources.");
		annotateGeneratedCheck.setSelected(false);
		
		annotateGeneratedCheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (annotateGeneratedCheck.isSelected()) {
					annotationCheck.setSelected(false);

					if (engine != null)
						engine.setAnnotateGeneratedResources(annotateGeneratedCheck.isSelected());
				}
			}
		});
		
		optionsPanelUnderSub3Sub3.add(annotateGeneratedCheck);
		
		JPanel optionsPanelUnderSub3Sub4 = new JPanel();
		optionsPanelUnderSub3Sub4.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		generateComparisonsCheck = new JCheckBox("Generate comparisons.");
		generateComparisonsCheck.setSelected(false);
		
		optionsPanelUnderSub3Sub4.add(generateComparisonsCheck);
		
		optionsPanelUnderSub3.add(optionsPanelUnderSub3Sub1);
		optionsPanelUnderSub3.add(optionsPanelUnderSub3Sub2);
		optionsPanelUnderSub3.add(optionsPanelUnderSub3Sub3);
		optionsPanelUnderSub3.add(optionsPanelUnderSub3Sub4);
		
		JPanel optionsPanelUnderSub3Shell = new JPanel();
		optionsPanelUnderSub3Shell.setLayout(new FlowLayout());
		optionsPanelUnderSub3Shell.add(optionsPanelUnderSub3);	
		
		if (user == null) {
			user = new NLGUser("tempUser", UMQM.getUserModel(((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI()));
			
			overridePipelineFactLimitText.setText("" + user.getUserModel().getMaxMessagesPerSentence());
			overridePipelineSlotLimitText.setText("" + 10);
			overrideILPSlotLimitText.setText("" + 10);
			
			if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
				if (user.getUserModel().getMaxMessagesPerPage() > 3) {
					overrideILPTextLimitText.setText("3");
					overrideILPTextLimitCheck.setSelected(true);
					overrideILPTextLimitText.setEnabled(true);

				    errorButton.setVisible(true);
				} else {
					overrideILPTextLimitText.setText("" + user.getUserModel().getMaxMessagesPerPage());
					errorButton.setVisible(false);
				}
			} else {
				overrideILPTextLimitText.setText("" + user.getUserModel().getMaxMessagesPerPage());
			}
		}

		optionsPanelUnder.add(optionsPanelUnderSub1);
		optionsPanelUnder.add(optionsPanelUnderSub2Pipeline);
		optionsPanelUnder.add(optionsPanelUnderSub2ILP);
		optionsPanelUnder.add(optionsPanelUnderSub3Shell);
		
		optionsPanelUnderSub2ILP.setVisible(false);
		
		optionsPanel.add(optionsPanelOver);	
		optionsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		optionsPanel.add(optionsPanelUnder);	
		
		resultTextPane = new JTextPane();
		resultTextPane.setEditable(false);
		resultTextPane.setContentType("text/html");
		resultTextPane.setBackground(new Color(255, 255, 255));
		
		scroll = new JScrollPane(resultTextPane);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setViewportView(resultTextPane);		
	
		add(optionsPanel, BorderLayout.PAGE_START);
		add(scroll, BorderLayout.CENTER);
		
		setAllEnabled(false);
		
		validate();
		repaint();
	}
	
	private void refresh() {
		IRI userType = null;
		if (user != null) {
			userType = user.getUserModel().getUMIRI();
		}
		
		if (((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI().equals(NLResourceManager.globalUserModel.getIRI())) {
			user = new NLGUser("globalUser", UMQM.getGlobalUserModel());
		} else {
			user = new NLGUser("tempUser", UMQM.getUserModel(((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI()));
		}

		if (userType != null) {
			if (!userType.equals(user.getUserModel().getUMIRI())) {
				overridePipelineFactLimitCheck.setSelected(false);
				overridePipelineSlotLimitCheck.setSelected(false);
				overrideILPSlotLimitCheck.setSelected(false);
				overrideILPTextLimitCheck.setSelected(false);
				
				overridePipelineFactLimitText.setText("" + user.getUserModel().getMaxMessagesPerSentence());
				overridePipelineSlotLimitText.setText("" + 10);
				overrideILPSlotLimitText.setText("" + 10);

				if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
					if (user.getUserModel().getMaxMessagesPerPage() > 3) {
						overrideILPTextLimitText.setText("3");
						overrideILPTextLimitCheck.setSelected(true);
						overrideILPTextLimitText.setEnabled(true);

						errorButton.setVisible(true);
					} else {
						overrideILPTextLimitText.setText("" + user.getUserModel().getMaxMessagesPerPage());
						errorButton.setVisible(false);
					}
				} else {
					overrideILPTextLimitText.setText("" + user.getUserModel().getMaxMessagesPerPage());
				}
			}
		}
	}
	
	private void generateText(IRI target) {
		if (languageCombo.getSelectedItem().equals("English")) {
			engine = new NLGEngine(getOWLModelManager().getOWLOntologyManager(), LQM, SPQM, NLNQM, UMQM, MQM, OQM, Languages.ENGLISH);				
		} else {
			engine = new NLGEngine(getOWLModelManager().getOWLOntologyManager(), LQM, SPQM, NLNQM, UMQM, MQM, OQM, Languages.GREEK);
		}
        engine.setSectionParagraphs(shapeCheck.isSelected());
		engine.setAnnotateGeneratedResources(annotateGeneratedCheck.isSelected());
		
        engine.setAllowComparisons(generateComparisonsCheck.isSelected());
        if (generateComparisonsCheck.isSelected()) {
        	engine.buildTree();
        }
		
		if (engineCombo.getSelectedItem().equals(PIPELINE_MODEL)) {
			engine.setUseEngine(NLGEngine.ENGINE_PIPELINE_MODEL);
			
			if (overridePipelineFactLimitCheck.isSelected()) {
				engine.setOverrideMaxMessagesPerSentence(Integer.parseInt(overridePipelineFactLimitText.getText()));
			} else {
				engine.setOverrideMaxMessagesPerSentence(-1);
			}
			if (overridePipelineSlotLimitCheck.isSelected()) {
				engine.setUseSlotLimit(true);
				engine.setMaxSlotsPerSentence(Integer.parseInt(overridePipelineSlotLimitText.getText()));
			} else {
				engine.setUseSlotLimit(false);
			}
		} else if (engineCombo.getSelectedItem().equals(ILP_MODEL)) {
			engine.setUseEngine(NLGEngine.ENGINE_ILP_MODEL);
		} else if (engineCombo.getSelectedItem().equals(ILP_MODEL_APPROX)) {
			engine.setUseEngine(NLGEngine.ENGINE_ILP_APPROXIMATION_MODEL);
		}
            
		if (user == null) {
			user = new NLGUser("tempUser", UMQM.getUserModel(((ListIRI)userTypeCombo.getSelectedItem()).getEntryIRI()));
		}

        String result[];
        if (engineCombo.getSelectedItem().equals(PIPELINE_MODEL)) {
        	result = engine.generateDescription(target, user, Integer.parseInt((String)distanceCombo.getSelectedItem()));
		} else {
			try {
				result = engine.generateDescription(target, user, 1, Double.parseDouble(lamdaContentSection.getText().replace(',', '.')), Integer.parseInt(overrideILPTextLimitText.getText()), Integer.parseInt(overrideILPSlotLimitText.getText()));
			} catch (java.lang.UnsatisfiedLinkError e){
				result = new String[3];

				for (int i = 0; i < 3; i++) {
					result[i] = "*NULL*";
				}				
				JOptionPane.showMessageDialog(null, "The GNU Linear Programming Kit is not installed properly. See the README file for details.", "GLPK error", JOptionPane.ERROR_MESSAGE);
			} catch (java.lang.NoClassDefFoundError e) {
				result = new String[3];

				for (int i = 0; i < 3; i++) {
					result[i] = "*NULL*";
				}
				JOptionPane.showMessageDialog(null, "The GNU Linear Programming Kit is not installed properly. See the README file for details.", "GLPK error", JOptionPane.ERROR_MESSAGE);
			}
		}
        
        String generatedText;
		if (annotateGeneratedCheck.isSelected()) {
			generatedText = "<html>";
		}
		if (annotationCheck.isSelected()) {
			resultTextPane.setContentType("text");
		} else {
			resultTextPane.setContentType("text/html");
		}
        if (result[1].trim().equals("")) {
        	generatedText = "*NULL*";
        } else {
        	generatedText = result[1];
        }
    	if (engine.areAllFactsAssimilated()) {
    		if (annotationCheck.isSelected()) {
    			generatedText = generatedText + "\n\n";
    		} else {
    			generatedText = generatedText + "<br><br>";
    		}
    		generatedText = generatedText + "All facts are assimilated!";
    	}
    	if (annotationCheck.isSelected()) {
    		generatedText = generatedText + "\n\n";
    		generatedText = generatedText + result[2];
        }
		if (annotateGeneratedCheck.isSelected()) {
			generatedText = generatedText + "</html>";
		}
    	resultTextPane.setText(generatedText);
	}
	
	private void updateUserTypeCombo() {
		HashSet<ListIRI> userTypes = new HashSet<ListIRI>();
		for (IRI iri : UMQM.getUserModels()) {
			userTypes.add(new ListIRI(iri));
		}
		userTypes.add(new ListIRI(NLResourceManager.globalUserModel.getIRI()));

		Object selected = userTypeCombo.getSelectedItem();
        if (!areIdentical((DefaultComboBoxModel<ListIRI>)userTypeCombo.getModel(), userTypes)) {	 
	        ArrayList<ListIRI> sortedList = new ArrayList<ListIRI>(userTypes);
	        Collections.sort(sortedList);
	        
	        DefaultComboBoxModel userTypeComboModel = new DefaultComboBoxModel(sortedList.toArray());
	        userTypeCombo.setModel(userTypeComboModel);
	        
	        if (userTypeComboModel.getIndexOf(selected) != -1) {
	        	userTypeCombo.setSelectedItem(selected);
	        }
        }
	}
	
	private void setAllEnabled(boolean enabled) {		
		languageCombo.setEnabled(enabled);
		userTypeCombo.setEnabled(enabled);
		distanceCombo.setEnabled(enabled);
		
		engineCombo.setEnabled(enabled);
		
		lamdaSlider.setEnabled(enabled);
		lamdaAggregation.setEnabled(enabled);
		lamdaContentSection.setEnabled(enabled);
		
		overridePipelineFactLimitCheck.setEnabled(enabled);
		overridePipelineSlotLimitCheck.setEnabled(enabled);
		overrideILPSlotLimitCheck.setEnabled(enabled);
		overrideILPTextLimitCheck.setEnabled(enabled);

		if (enabled) {
			if (overridePipelineFactLimitCheck.isSelected()) {
				overridePipelineFactLimitText.setEnabled(true);
			}
			if (overridePipelineSlotLimitCheck.isSelected()) {
				overridePipelineSlotLimitText.setEnabled(true);
			}
			if (overrideILPSlotLimitCheck.isSelected()) {
				overrideILPSlotLimitText.setEnabled(true);
			}
			if (overrideILPTextLimitCheck.isSelected()) {
				overrideILPTextLimitText.setEnabled(true);
			}
		} else {
			overridePipelineFactLimitText.setEnabled(false);
			overridePipelineSlotLimitText.setEnabled(false);
			overrideILPSlotLimitText.setEnabled(false);
			overrideILPTextLimitText.setEnabled(false);			
		}

		weightFactImportance.setEnabled(enabled);
		weightSlotMinimization.setEnabled(enabled);
		overridePipelineFactLimitFacts.setEnabled(enabled);
		overridePipelineFactLimitSlots.setEnabled(enabled);
		overrideILPFactLimitSlots.setEnabled(enabled);
		overrideILPTextLimitSentences.setEnabled(enabled);
		
		annotationCheck.setEnabled(enabled);
		shapeCheck.setEnabled(enabled);
		annotateGeneratedCheck.setEnabled(enabled);
		generateComparisonsCheck.setEnabled(enabled);
		
		generateButton.setEnabled(enabled);
		resetButton.setEnabled(enabled);		
	}
	
	private void updateLabel() {
		if (selectedIndividual != null) {
			generationLabel.setText("Generation text for " + selectedIndividual.getIRI().getFragment() + ".");
		} else if (selectedClass != null) {
			generationLabel.setText("Generation text for " + selectedClass.getIRI().getFragment() + ".");
		} else {
			generationLabel.setText("");
		}
	}

	private OWLSelectionModelListener getOWLClassSelectionModelListener() {
		return new OWLSelectionModelListener() {
	        public void selectionChanged() throws Exception {
	            OWLEntity selected = generationClassSelectionModel.getSelectedEntity();
	
	            if (selected != null) {
	                selectedClass = getOWLModelManager().getOWLDataFactory().getOWLClass(selected.getIRI());
	            } else {
	            	selectedClass = null;
					userModelTableClassSelectionModel = new OWLSelectionModelImpl();
					userModelTableClassSelectionModel.addListener(getOWLClassSelectionModelListener());
	            }
	                
	            if (selectedClass != null) {
	        		setAllEnabled(true);
	            } else {
	        		setAllEnabled(false);
	            }
	    		updateLabel();
	            
	            validate();
	            repaint();
	        }
	    };
	}


	private OWLSelectionModelListener getOWLIndividualSelectionModelListener() {
		return new OWLSelectionModelListener() {
	        public void selectionChanged() throws Exception {
	            OWLEntity selected = generationIndivSelectionModel.getSelectedEntity();
	
	            if (selected != null) {   
	                selectedIndividual = getOWLModelManager().getOWLDataFactory().getOWLNamedIndividual(selected.getIRI());
	            } else {
	            	selectedIndividual = null;
					userModelTableIndivSelectionModel = new OWLSelectionModelImpl();
					userModelTableIndivSelectionModel.addListener(getOWLIndividualSelectionModelListener());
	            }
	                
	            if (selectedClass != null) {
	        		setAllEnabled(true);
	            } else {
	        		setAllEnabled(false);
	            }
	    		updateLabel();
	            
	            validate();
	            repaint();
	        }
	    };
	}
	
	protected void disposeOWLView() {
		super.disposeOWLView();
		getOWLModelManager().removeListener(modelListener);
	}

	public void actionPerformed(ActionEvent arg0) {}

	public void itemStateChanged(ItemEvent arg0) {}
	
	public boolean areIdentical(DefaultComboBoxModel<ListIRI> a, HashSet<ListIRI> b) {
	    if (a.getSize() == b.size()) {
	        HashSet<ListIRI> checkSet = new HashSet<ListIRI>(b);
	        for (int i = 0; i < a.getSize(); i++) {
				checkSet.add(a.getElementAt(i));
	        }
	        if (checkSet.size() == b.size()) {
	        	return true;
	        }
	        return false;
	    }
		return false;
	}

	public static boolean isValidNumber(String num) {
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
}
