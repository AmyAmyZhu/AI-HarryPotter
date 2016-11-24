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
import gr.aueb.cs.nlg.NLFiles.DefaultResourcesManager;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryNoun;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerb;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbGR;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.SPAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationPropertySlot;
import gr.aueb.cs.nlg.NLFiles.SPConcatenationSlot;
import gr.aueb.cs.nlg.NLFiles.SPFillerSlot;
import gr.aueb.cs.nlg.NLFiles.SPNounSlot;
import gr.aueb.cs.nlg.NLFiles.SPVerbSlot;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLFiles.SPOwnerSlot;
import gr.aueb.cs.nlg.NLFiles.SPPrepositionSlot;
import gr.aueb.cs.nlg.NLFiles.SentencePlan;
import gr.aueb.cs.nlg.NLFiles.SPStringSlot;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class SentencePlanTab extends NaturalOWLTab {
	private static final long serialVersionUID = -7007128314799160146L;
	
	private ArrayList<SentencePlanBox> boxes = new ArrayList<SentencePlanBox>();
	private ArrayList<JPanel> pluses = new ArrayList<JPanel>();
	private MyJPan mainPanel;
	private JScrollPane scroll;
	private JCheckBox aggr;
	private JButton comparisonsAllowedButton;
	private JButton connectButton;
	private JButton appropButton;
	private static JPanel preview;
	
	private SentencePlan loadedPlan;

	Logger log = Logger.getLogger(SentencePlanTab.class);

	// private ExpressionEditor<OWLClassExpression> owlDescriptionEditor;

	// private ResultsList resultsList;

	private OWLModelManagerListener modelListener;

	// private boolean requiresRefresh = false;

	@Override
	protected void initialiseOWLView() throws Exception {
		if(sentencePlanSelectionModel!=null){
			sentencePlanSelectionModel.addListener(new OWLSelectionModelListener() {
	            public void selectionChanged() throws Exception {
	
	                OWLEntity selected = sentencePlanSelectionModel.getSelectedEntity();
	
	                loadedPlan = SPQM.getSentencePlan(selected.getIRI());
	                
	                if (loadedPlan != null) {		                
		                checkOrderConsistency(loadedPlan);
	                	showSentencePlan(loadedPlan);
	                	
	                	if (DefaultResourcesManager.isDefaultResource(loadedPlan.getSentencePlanIRI())) {
	                		connectButton.setVisible(false);
		                	appropButton.setVisible(false);
		            		aggr.setVisible(false);
	                	} else {
	                		connectButton.setVisible(true);
		                	appropButton.setVisible(true);
		            		aggr.setVisible(true);
	                	}
	                }
	                validate();
	                repaint();
	            }
	        });
		}
		
		setLayout(new BorderLayout(10, 10));	
		
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		previewPanel.setPreferredSize(new Dimension(600, 100));
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.PAGE_AXIS));

		JPanel previewLabelPanel = new JPanel(new FlowLayout());
		JLabel previewLabel = new JLabel("Sentence Plan Preview");
		previewLabelPanel.add(previewLabel);
		
		preview = new JPanel();
		JLabel blabel = new JLabel(" ");
		preview.add(blabel);
		
		JPanel previewButtonPanel = new JPanel(new FlowLayout());
		JButton previewRefreshButton = new JButton("Refresh preview");
		previewRefreshButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getThis().repaintPreview();
			}			
		});
		previewButtonPanel.add(previewRefreshButton);

		previewPanel.add(previewLabelPanel);
		previewPanel.add(preview);
		previewPanel.add(previewButtonPanel);
		
		mainPanel = new MyJPan(0, 320);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));		

		scroll = new JScrollPane(mainPanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setViewportView(mainPanel);
		scroll.setSize(new Dimension(800, 400));

		JPanel subPanel = new JPanel();
		subPanel.setPreferredSize(new Dimension(600, 30));
		subPanel.setLayout(new BorderLayout(50, 50));

		comparisonsAllowedButton = new JButton("Set properties that allow comparisons...");
		comparisonsAllowedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {			
				ComparisonsAllowedDialog dlg = new ComparisonsAllowedDialog(thisTab);	
				dlg.getResponse();
			}
			
		});
		
		connectButton = new JButton("Connect current Sentence Plan with properties...");
		connectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ConnectionDialog dlg = new ConnectionDialog(thisTab, loadedPlan.getSentencePlanIRI());
				dlg.getResponse();
			}
		});
		
		appropButton = new JButton("Set Sentence Plan appropriateness...");
		appropButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {			
				AppropriatenessDialog dlg = new AppropriatenessDialog(thisTab, loadedPlan.getSentencePlanIRI());
				dlg.getResponse();
			}
		});
		
		aggr = new JCheckBox("Allow the resulting sentence to be aggregated", true);
		aggr.addItemListener(this);
		
		connectButton.setVisible(false);
		appropButton.setVisible(false);
		aggr.setVisible(false);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(comparisonsAllowedButton);
		buttonsPanel.add(connectButton);
		buttonsPanel.add(appropButton);
		
		subPanel.add(buttonsPanel, BorderLayout.LINE_START);
		subPanel.add(aggr, BorderLayout.LINE_END);
		
		add(previewPanel, "North");
		add(scroll, "Center");
		add(subPanel, "South");
		
		validate();
		repaint();
	}
	
	public MyJPan getMainPanel(){
		return mainPanel;
	}
	
	public static JPanel getPreview(){
		return preview;
	}
	
	public ArrayList<SentencePlanBox> getBoxes(){
		return boxes;
	}
	
	public ArrayList<SPSlot> getSlots(){
		return loadedPlan.getSlotsList();
	}
	
	public SentencePlanTab getThis() {
		return this;
	}
	
	public void addToBoxes(SentencePlanBox b){
		boxes.add(b);
		repaintPreview();
	}
	
	public void addToBoxes(int pos, SentencePlanBox b){
		boxes.add(pos, b);
		repaintPreview();
	}

	public void addToSlots(int pos, SPSlot slot){
		if (pos < loadedPlan.getSlotsList().size()) {
			loadedPlan.getSlotsList().add(pos, slot);
		} else {
			loadedPlan.getSlotsList().add(slot);
		}
	}
	
	public void removeFromBoxes(SentencePlanBox b) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).equals(b)) {
				boxes.remove(i);
			}
		}
		repaintPreview();
	}
	
	public void removeFromSlots(SPSlot slot) {
		loadedPlan.getSlotsList().remove(slot);
	}
	
	public void setSlot(SPSlot slot, int pos) {
		loadedPlan.getSlotsList().set(pos, slot);
	}
	
	public int getPositionInBoxes(SentencePlanBox b) {
		boolean flag = false;
		int i = 0;
		while (!flag && i < boxes.size()) {
			if (boxes.get(i).equals(b)) {
				flag = true;
			} else
				i++;
		}
		if (flag)
			return i;
		return boxes.size()-1;
	}
	
	public void repaintPreview(){
		getPreview().removeAll();
		
		JLabel blabel = new JLabel(previewSentencePlan());
		getPreview().add(blabel);
		
		getPreview().validate();
		getPreview().repaint();		
		
	}
	
	public void updateScroll() {
		scroll.validate();
		scroll.updateUI();
	}
	
	public void addToPluses(int pos, JPanel pane) {
		pluses.add(pos, pane);
	}
	
	public void removeFromPluses(int pos) {
		pluses.remove(pos);
	}

	
	public int getPlusPosition(JPanel pane) {
		for (int i = 0; i < pluses.size(); i++) {
			if (pluses.get(i).equals(pane)) { 
				return i;
			}
		}
		return 0;
	}	

	protected void disposeOWLView() {
		super.disposeOWLView();
		getOWLModelManager().removeListener(modelListener);
	}

	public void actionPerformed(ActionEvent arg0) {		
	}

	public void itemStateChanged(ItemEvent evt) {
		if (evt.getSource() == aggr) {
			if (aggr.isSelected())
				loadedPlan.setAggAllowed(true);
			else
				loadedPlan.setAggAllowed(false);
			dirtenOntologies();
		}
	}

	private void showSentencePlan(SentencePlan plan) {		
		int i = 0;
		
		ArrayList<SPSlot> slots = plan.getSlotsList();
		Collections.sort(slots);

		clearAllBoxesFromUI();
		for (SPSlot slot: slots) {
			new SentencePlanBox(this, slot, plan.getLanguage(), i++);
		}
		
		if (loadedPlan.getAggAllowed())
			aggr.setSelected(true);
		else
			aggr.setSelected(false);
	}

	private void checkOrderConsistency(SentencePlan plan) {
		ArrayList<SPSlot> slots = plan.getSlotsList();
		Collections.sort(slots);

		HashSet<Integer> orders = new HashSet<Integer>();
		for (SPSlot slot: slots) {
			while (orders.contains(slot.getOrder())) {
				slot.setOrder(slot.getOrder() + 1);
			}
			
			orders.add(slot.getOrder());
		}
	}
	
	public void clearAllBoxesFromUI(){
		ArrayList<SentencePlanBox> toBeRemoved = new ArrayList<SentencePlanBox>(boxes);
		
		for (SentencePlanBox box : toBeRemoved) {
			box.delete(box);
		}
		mainPanel.removeAll();
	}
	
	public SentencePlan getLoadedPlan() {
		return loadedPlan;
	}
	
	public String previewSentencePlan() {
        ArrayList<SPSlot> slots = new ArrayList<SPSlot>();
		for (int i = 0; i < boxes.size(); i++) {
			if(boxes.get(i).select.getSelectedIndex()!=-1){
				slots.add(boxes.get(i).getSlot());
			}
		}
        Collections.sort(slots);
        
        ArrayList<String> realizedSlots = new ArrayList<String>();
        ArrayList<String> slotTypes = new ArrayList<String>();
        
        for (int j = 0; j < slots.size(); j++) {
        	SPSlot slot = slots.get(j);

            //if the Slot is verb
            if (slot instanceof SPVerbSlot) {
            	slotTypes.add("VERB");
            	
                IRI lexiconEntryIRI = ((SPVerbSlot)slot).getLexiconEntryIRI();

                if (lexiconEntryIRI != null) {
	                String voice = ((SPVerbSlot)slot).getVoice();
	                String tense = ((SPVerbSlot)slot).getTense();
	
	                NodeID agreeWith = ((SPVerbSlot)slot).getAgreesWithID();
	                String number = XmlMsgs.SINGULAR;
	                String person = XmlMsgs.PERSON_3RD;
	
	                if (agreeWith == null) {
	                    number = ((SPVerbSlot)slot).getNumber();
	                    person = ((SPVerbSlot)slot).getPerson();
	                } else {
	                    for (int k = 0; k < slots.size(); k++) {
	                        if (slots.get(k).getId().equals(agreeWith)) {
	                            if (slots.get(k) instanceof SPNounSlot) {
	                                NodeID nounAgreeWith = ((SPNounSlot)slots.get(k)).getAgreesWithID();
	
	                                if (nounAgreeWith == null) {
	                                    number = ((SPNounSlot)slots.get(k)).getNumber();
	                                }
	                                person = XmlMsgs.PERSON_3RD;
	                            }
	                        }
	                    }
	                    person = XmlMsgs.PERSON_3RD;
	                }
	
	                LexEntryVerb verb = LQM.getVerbEntry(lexiconEntryIRI, loadedPlan.getLanguage());
	
	                if (lexiconEntryIRI.equals(DefaultResourcesManager.toBeVLE_IRI)) {
	                    String ret = "";
	                    if (Languages.isEnglish(loadedPlan.getLanguage())) {
	                        if (voice.equals(XmlMsgs.ACTIVE_VOICE)) {
	                            if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
	                                if (number.equals(XmlMsgs.SINGULAR)) {
	                                    if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                        ret = ret + "am";
	                                    } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                        ret = ret + "are";
	                                    } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                        ret = ret + "is";
	                                    }
	                                } else if (number.equals(XmlMsgs.PLURAL)) {
	                                    ret = ret + "are";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
	                                if (number.equals(XmlMsgs.SINGULAR)) {
	                                    if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                        ret = ret + "am";
	                                    } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                        ret = ret + "are";
	                                    } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                        ret = ret + "is";
	                                    }
	                                } else if (number.equals(XmlMsgs.PLURAL)) {
	                                    ret = ret + "are";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
	                                    ret = ret + "has";
	                                } else {
	                                    ret = ret + "have";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " been";
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
	                                    ret = ret + "was";
	                                } else {
	                                    ret = ret + "were";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
	                                    ret = ret + "was";
	                                } else {
	                                    ret = ret + "were";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "had not been";
	                                } else {
	                                    ret = ret + "had been";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
	                                ret = ret + "will";
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " be";
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not be";
	                                } else {
	                                    ret = ret + "will be";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have been";
	                                } else {
	                                    ret = ret + "will have been";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have been";
	                                } else {
	                                    ret = ret + "will have been";
	                                }
	                            }
	                        }
	                    } else if (Languages.isGreek(loadedPlan.getLanguage())) {
	                        if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                            ret = ret + "δεν ";
	                        }
	                        if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
	                            ret = ret + "θα";
	                        }
	                        ret = ret + ((LexEntryVerbGR)verb).get(voice, tense, person, number);
	                    }
	                    realizedSlots.add(ret.trim());
	                } else {
	                    String ret = "";
	                    if (Languages.isEnglish(loadedPlan.getLanguage())) {
	                        if (voice.equals(XmlMsgs.ACTIVE_VOICE)) {
	                            if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
	                                if (number.equals(XmlMsgs.SINGULAR)) {
	                                    if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                        ret = ret + "am";
	                                    } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                        ret = ret + "are";
	                                    } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                        ret = ret + "is";
	                                    }
	                                } else if (number.equals(XmlMsgs.PLURAL)) {
	                                    ret = ret + "are";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
	                                    ret = ret + "has";
	                                } else {
	                                    ret = ret + "have";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "did not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
	                                    ret = ret + "was";
	                                } else {
	                                    ret = ret + "were";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "had not been";
	                                } else {
	                                    ret = ret + "had been";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
	                                ret = ret + "will";
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not be";
	                                } else {
	                                    ret = ret + "will be";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have";
	                                } else {
	                                    ret = ret + "will have";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have been";
	                                } else {
	                                    ret = ret + "will have been";
	                                }
	                            }
	                        } else if (voice.equals(XmlMsgs.PASSIVE_VOICE)) {
	                            if (tense.equals(XmlMsgs.TENSE_SIMPLE_PRESENT)) {
	                                if (number.equals(XmlMsgs.SINGULAR)) {
	                                    if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                        ret = ret + "am";
	                                    } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                        ret = ret + "are";
	                                    } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                        ret = ret + "is";
	                                    }
	                                } else if (number.equals(XmlMsgs.PLURAL)) {
	                                    ret = ret + "are";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_CONTINUOUS)) {
	                                if (number.equals(XmlMsgs.SINGULAR)) {
	                                    if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                        ret = ret + "am";
	                                    } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                        ret = ret + "are";
	                                    } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                        ret = ret + "is";
	                                    }
	                                } else if (number.equals(XmlMsgs.PLURAL)) {
	                                    ret = ret + "are";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && (person.equals(XmlMsgs.PERSON_3RD))) {
	                                    ret = ret + "has";
	                                } else {
	                                    ret = ret + "have";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " been";
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
	                                    ret = ret + "was";
	                                } else {
	                                    ret = ret + "were";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_CONTINUOUS)) {
	                                if ((number.equals(XmlMsgs.SINGULAR)) && ((person.equals(XmlMsgs.PERSON_1ST)) || (person.equals(XmlMsgs.PERSON_3RD)))) {
	                                    ret = ret + "was";
	                                } else {
	                                    ret = ret + "were";
	                                }
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "has not been";
	                                } else {
	                                    ret = ret + "has been";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
	                                ret = ret + "will";
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + " not";
	                                }
	                                ret = ret + " be";
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not be";
	                                } else {
	                                    ret = ret + "will be";
	                                }
	                                ret = ret + " being";
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have";
	                                } else {
	                                    ret = ret + "will have";
	                                }
	                                ret = ret + " been";
	                            } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS)) {
	                                if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                                    ret = ret + "will not have been";
	                                } else {
	                                    ret = ret + "will have been";
	                                }
	                                ret = ret + " being";
	                            }
	                        }
	
	                        if (((SPVerbSlot)slot).getPolarity().equals("false") && (tense.equals(XmlMsgs.TENSE_SIMPLE_PAST))) {
	                            ret = ret + " " + ((LexEntryVerbEN) verb).get(voice, XmlMsgs.TENSE_SIMPLE_PRESENT, person, number);
	                        } else {
	                            ret = ret + " " + ((LexEntryVerbEN) verb).get(voice, tense, person, number);
	                        }
	
	                        if (((SPVerbSlot)slot).getPolarity().equals("false") && !(ret.contains("not"))) {
	                            ret = ret + " not";
	                        }
	                    } else if (Languages.isGreek(loadedPlan.getLanguage())) {
	                        if (((SPVerbSlot)slot).getPolarity().equals("false")) {
	                            ret = ret + "δεν ";
	                        }
	
	                        if (tense.equals(XmlMsgs.TENSE_PRESENT_PERFECT)) {
	                            if (number.equals(XmlMsgs.SINGULAR)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "έχω";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "έχεις";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "έχει";
	                                }
	                            } else if (number.equals(XmlMsgs.PLURAL)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "έχουμε";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "έχετε ";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "έχουν";
	                                }
	                            }
	
	                        } else if (tense.equals(XmlMsgs.TENSE_PAST_PERFECT)) {
	                            if (number.equals(XmlMsgs.SINGULAR)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "είχα";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "είχες";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "είχε";
	                                }
	                            } else if (number.equals(XmlMsgs.PLURAL)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "είχαμε";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "είχατε";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "είχαν";
	                                }
	                            }
	                        } else if (tense.equals(XmlMsgs.TENSE_SIMPLE_FUTURE)) {
	                            ret = ret + "θα";
	                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_CONTINUOUS)) {
	                            ret = ret + "θα";
	                        } else if (tense.equals(XmlMsgs.TENSE_FUTURE_PERFECT)) {
	                            ret = ret + "θα ";
	                            if (number.equals(XmlMsgs.SINGULAR)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "έχω";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "έχεις";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "έχει";
	                                }
	                            } else if (number.equals(XmlMsgs.PLURAL)) {
	                                if (person.equals(XmlMsgs.PERSON_1ST)) {
	                                    ret = ret + "έχουμε";
	                                } else if (person.equals(XmlMsgs.PERSON_2ND)) {
	                                    ret = ret + "έχετε";
	                                } else if (person.equals(XmlMsgs.PERSON_3RD)) {
	                                    ret = ret + "έχουν";
	                                }
	                            }
	                        }
	
	                        ret = ret + " " + ((LexEntryVerbGR) verb).get(voice, tense, person, number);
	                    }
	                    realizedSlots.add(ret.trim());
	                }
                } else {
                	realizedSlots.add("NULL");
                }
            } else if (slot instanceof SPAdjectiveSlot) {
            	slotTypes.add("ADJECTIVE");
            	
                IRI lexiconEntryIRI = ((SPAdjectiveSlot)slot).getLexiconEntryIRI();

                if (lexiconEntryIRI != null) {
	                NodeID agreeWith = ((SPAdjectiveSlot)slot).getAgreesWithID();
	                String number = XmlMsgs.SINGULAR;
	                String caseType = XmlMsgs.NOMINATIVE_TAG;
	                String gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
	
	                if (agreeWith == null) {
	                    number = ((SPAdjectiveSlot)slot).getNumber();
	                    if (Languages.isGreek(loadedPlan.getLanguage())) {
	                        caseType = ((SPAdjectiveSlot)slot).getCase();
	                        gender = ((SPAdjectiveSlot)slot).getGender();
	                    }
	                } else {
	                    for (int k = 0; k < slots.size(); k++) {
	                    	if (slots.get(k) instanceof SPAdjectiveSlot) {
	                    		if (((SPAdjectiveSlot)slots.get(k)).getAgreesWithID() != null) {
			                        if (((SPAdjectiveSlot)slots.get(k)).getAgreesWithID().equals(agreeWith)) {
		                                number = ((SPAdjectiveSlot) slots.get(k)).getNumber();
		                                if (Languages.isGreek(loadedPlan.getLanguage())) {
		                                    caseType = ((SPAdjectiveSlot) slots.get(k)).getCase();
		                                    gender = ((SPAdjectiveSlot) slots.get(k)).getGender();
		                                }
		                        	}
	                    		}
	                        }
	                    }
	                }
	
	                if (Languages.isEnglish(loadedPlan.getLanguage())) {
	                    LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, loadedPlan.getLanguage());
	                    realizedSlots.add(adjectiveEntry.get_form());
	                } else if (Languages.isGreek(loadedPlan.getLanguage())) {
	                    LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, loadedPlan.getLanguage());
	                    realizedSlots.add(adjectiveEntry.get(gender, number, caseType));
	                }
                } else {
                	realizedSlots.add("NULL");
                }
            } else if (slot instanceof SPNounSlot) {
            	slotTypes.add("NOUN");
            	
                IRI lexiconEntryIRI = ((SPNounSlot)slot).getLexiconEntryIRI();
                
                if (lexiconEntryIRI != null) {
	                NodeID agreeWith = ((SPNounSlot)slot).getAgreesWithID();
	                String number = XmlMsgs.SINGULAR;
	                String caseType = XmlMsgs.NOMINATIVE_TAG;
	
	                if (agreeWith == null) {
	                    number = ((SPNounSlot)slot).getNumber();
	                    if (Languages.isGreek(loadedPlan.getLanguage())) {
	                        caseType = ((SPNounSlot)slot).getCase();
	                    }
	                } else {
	                    for (int k = 0; k < slots.size(); k++) {
	                    	if (slots.get(k) instanceof SPNounSlot) {
	                    		if (((SPNounSlot)slots.get(k)).getAgreesWithID() != null) {
			                        if (((SPNounSlot)slots.get(k)).getAgreesWithID().equals(agreeWith)) {
		                                number = ((SPNounSlot) slots.get(k)).getNumber();
		                                if (Languages.isGreek(loadedPlan.getLanguage())) {
		                                    caseType = ((SPNounSlot) slots.get(k)).getCase();
		                                }
			                        }
	                    		}
	                    	}
	                    }
	                }
	
	                if (Languages.isEnglish(loadedPlan.getLanguage())) {
	                    LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, loadedPlan.getLanguage());
	                    realizedSlots.add(nounEntry.get("", number));
	                } else if (Languages.isGreek(loadedPlan.getLanguage())) {
	                    LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, loadedPlan.getLanguage());
	                    realizedSlots.add(nounEntry.get(caseType, number));
	                }
                } else {
                	realizedSlots.add("NULL");
                }
            } else if (slot instanceof SPStringSlot) {
            	slotTypes.add("STRING");
            	
                String ret = ((SPStringSlot)slot).getText();
                realizedSlots.add(ret);
            } else if (slot instanceof SPPrepositionSlot) {
            	slotTypes.add("PREP");
            	
                String ret = ((SPPrepositionSlot)slot).getPrep();
                realizedSlots.add(ret);
            } else if (slot instanceof SPOwnerSlot) {//OWNER_TAG 
            	slotTypes.add("OWNER");
            	
            	if (((SPOwnerSlot)slot).getRefType().equals(SPOwnerSlot.REF_AUTO)) {
            		if (((SPOwnerSlot)slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
            			realizedSlots.add("OWNER's");
            		} else {
            			realizedSlots.add("OWNER");
            		}
            	} else if (((SPOwnerSlot)slot).getRefType().equals(SPOwnerSlot.REF_DEMONSTRATIVE)) {
                    if (Languages.isEnglish(loadedPlan.getLanguage())) {
                		if (((SPOwnerSlot)slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                			realizedSlots.add("this OWNER_SUPERCLASS's");
                		} else {
                			realizedSlots.add("this OWNER_SUPERCLASS");
                		}
                    } else if (Languages.isGreek(loadedPlan.getLanguage())) {
                		if (((SPOwnerSlot)slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                			realizedSlots.add("αυτού του OWNER_SUPERCLASS");
                		} else {
                			realizedSlots.add("αυτός ο OWNER_SUPERCLASS");
                		}
                    }
            	} else if (((SPOwnerSlot)slot).getRefType().equals(SPOwnerSlot.REF_PRONOUN)) {
                    if (Languages.isEnglish(loadedPlan.getLanguage())) {
                		if (((SPOwnerSlot)slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
                			realizedSlots.add("his");
                		} else {
                			realizedSlots.add("he");
                		}
                    } else if (Languages.isGreek(loadedPlan.getLanguage())) {
                		realizedSlots.add("");
                    }
            	}
            } else if (slot instanceof SPFillerSlot) {
            	slotTypes.add("FILLER");

        		if (((SPFillerSlot)slot).getCase().equals(XmlMsgs.GENITIVE_TAG)) {
        			realizedSlots.add("FILLER's");
        		} else {
        			realizedSlots.add("FILLER");
        		}
            } else if (slot instanceof SPConcatenationSlot) {//filler // edw to allaxa            	
            	for (SPConcatenationPropertySlot concat :((SPConcatenationSlot)slot).getSortedPropertySlots()) {
                	slotTypes.add("CONCAT");
                	
                    realizedSlots.add(concat.getPropertyIRI().getFragment());            		
            	}
            }//filler
        }

        String realizedPlan = "<html>";
        for (int i = 0; i < realizedSlots.size(); i++) {
            realizedPlan = realizedPlan + " [ " + realizedSlots.get(i) + " <sub>" + slotTypes.get(i) + "</sub>" + " ] ";
        }
        realizedPlan = realizedPlan + "</html>";

        return realizedPlan.trim();
    }
}

class SentencePlanBox implements ItemListener, FocusListener, ActionListener, ListSelectionListener, DocumentListener {
	final static String ownerSlotStr = "Property Owner";
	final static String fillerSlotStr = "Property Filler";
	final static String adjectiveSlotStr = "Adjective";	
	final static String nounSlotStr = "Noun";	
	final static String verbSlotStr = "Verb";
	final static String concatenationSlotStr = "Concatenation";	
	final static String stringSlotStr = "String";
	final static String prepositionSlotStr = "Preposition";
	
	String language;
	JPanel buttonsPanel;
	JPanel box;
	JPanel dynamicPanel;
	JPanel orderPanel;
	JButton del;
	JComboBox select;
	JButton left;
	JButton right;
	int pos; //position in Boxes arraylist
	//the arraylist stores only the boxes
	//the panel contains a sequence of box |+| box |+| box |+| ...
	//so, for a given box b, if b.positionInBoxes = i, this means that b.positionInPanel = i*2

	DefaultComboBoxModel<Object> verbAgreeWithSlotModel;
	DefaultComboBoxModel concatComboModel;
	DefaultListModel concatListModel;
	JComboBox exprCombo;
	JComboBox prepLECombo;
	JComboBox verbLECombo;
	JComboBox verbTenseCombo;
	JComboBox verbVoiceCombo;
	JComboBox verbNumCombo;
	JComboBox verbPerCombo;
	JComboBox indCaseCombo;
	JComboBox ownCaseCombo;
	JComboBox concatCombo;
	JComboBox verbAgreeWithCombo;
	JLabel exprLabel;
	JLabel indCaseLabel;
	JLabel ownCaseLabel;
	JLabel verbLELabel;
	JLabel tenseLabel;
	JLabel polarityLabel;
	JLabel voiceLabel;
	JLabel numberLabel;
	JLabel personLabel;
	JLabel agreeWithLabel;
	JLabel prepLELabel;
	JLabel concatLabel;
	JLabel concatListLabel;	

	//ADJ
	JLabel adjCaseLabel;
	JLabel adjGenderLabel;
	JLabel adjNumLabel;
	JComboBox adjCaseCombo;
	JComboBox adjGenderCombo;
	JComboBox adjNumCombo;
	JComboBox adjLECombo;
	JComboBox adjAgreeWithCombo;
	DefaultComboBoxModel<Object> adjAgreeWithSlotModel;

	//NOUN
	JLabel nounCaseLabel;
	JLabel nounNumLabel;
	JComboBox nounCaseCombo;
	JComboBox nounNumCombo;
	JComboBox nounLECombo;
	JComboBox polarityCombo;
	JComboBox nounAgreeWithCombo;
	DefaultComboBoxModel<Object> nounAgreeWithSlotModel;
	
	JButton concatAdd = new JButton("+");
	JButton concatMinus = new JButton("-");
	JButton concatUp = new JButton("UP");
	JButton concatDown = new JButton("DOWN");
	JCheckBox bulletsCheck;
	JTextArea stringTextArea;
	JList concatList;
	JLabel orderLabel;
	
	String lastOpenSlotTab = "";
	
	SentencePlanTab father = null;
	SPSlot slot = null;
	
	SentencePlanBox(SentencePlanTab f, SPSlot slot, String lang, int pos){
		this.box = new JPanel();
		
		this.father = f;
		this.slot = slot;
		this.pos = pos;
		this.language = lang;
		
		this.buttonsPanel = new JPanel();
		this.buttonsPanel.setPreferredSize(new Dimension(220, 30));//(220, 30)
		this.buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 1));
		this.box.add(buttonsPanel);
		
		this.box.setPreferredSize(new Dimension(225, 330));//(225,250)
		this.box.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		
		String[] choices = {ownerSlotStr, fillerSlotStr, adjectiveSlotStr, nounSlotStr, verbSlotStr, prepositionSlotStr, stringSlotStr, concatenationSlotStr};        
		this.select = new JComboBox<String>(choices);
		this.select.setEditable(false);
		this.select.setVisible(true);
		this.buttonsPanel.add(select);	
		
		this.dynamicPanel = new JPanel();
		this.dynamicPanel.setLayout(new CardLayout());
		this.dynamicPanel.setPreferredSize(new Dimension(200,250));//(200,200)
		this.box.add(dynamicPanel);	
		
		this.orderPanel = new JPanel();
		this.orderPanel.setPreferredSize(new Dimension(220, 30));//(220, 30)
		this.orderPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 1));
		this.box.add(orderPanel);
		
		orderLabel = new JLabel("Slot order: " + slot.getOrder());
		orderPanel.add(orderLabel);

		JLabel slotIDLab = new JLabel(slot.getId().toString());
		this.box.add(slotIDLab);
		
		father.addToBoxes(pos, this);
		
		dynamicPanels();
				
		addMoveArrows(this);
		addDeleteButton();	
			
		father.getMainPanel().increaseWidth(225);
		father.updateScroll();
		father.getMainPanel().add(this.box, pos*2);

		loadSlotInBox(slot);
		createPlus();
		updateAll();
	}
	
	void createPlus(){
		final JPanel p = new JPanel();
		
		p.setPreferredSize(new Dimension(40,250));
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		ClassLoader loader = SentencePlanTab.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/plusImg.png");		
		ImageIcon plusIcon = new ImageIcon(imageURL);		
		Image img = plusIcon.getImage(); 
		Image newimg = img.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);  
		plusIcon = new ImageIcon(newimg);  
		JButton plus = new JButton(plusIcon);
		plus.setOpaque(false);
		plus.setContentAreaFilled(false);
		plus.setBorderPainted(false);
		p.add(plus);
		
		plus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int pos = father.getPlusPosition(p);
				
				SPOwnerSlot slot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, SPOwnerSlot.REF_AUTO, SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + (pos + 2)).getID(), pos + 2);
				father.addToSlots(pos + 1, slot);
				for (int j = pos + 1; j < father.getBoxes().size(); j++) {
					father.getBoxes().get(j).getSlot().increaseOrder();
					father.getBoxes().get(j).getSlot().setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + father.getBoxes().get(j).getSlot().getOrder()).getID());
				}
				
				//Makes sure that even if slot order changes, "agrees with" points at the correct slots
				for (int j = father.getBoxes().size() - 1; j >= pos + 1; j--) {
					SPSlot incSlot = father.getBoxes().get(j).getSlot();

					if ((incSlot instanceof SPOwnerSlot)||(incSlot instanceof SPFillerSlot)||(incSlot instanceof SPAdjectiveSlot)||(incSlot instanceof SPNounSlot)) {
				        for (int f = 0; f < father.getBoxes().size(); f++) {
				        	ArrayList<DefaultComboBoxModel> mdls = father.getBoxes().get(f).getAgreeWithSlotModels();
				        	for (DefaultComboBoxModel mdl : mdls) {
								if (mdl.getIndexOf(incSlot.getOrder() - 1) != -1) {
									mdl.addElement(incSlot.getOrder());
	
									if (!mdl.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
										if ((Integer)mdl.getSelectedItem() == incSlot.getOrder() - 1) {
											mdl.setSelectedItem(incSlot.getOrder());
										}
									}
									mdl.removeElement(incSlot.getOrder() - 1);
								}
				        	}
						}
			        }
					father.getBoxes().get(j).updateOrderLabel();
				}
				
				SentencePlanBox newBox = new SentencePlanBox(father, slot, language, pos + 1);
				updatePositions();
				for (int j = 0; j < father.getBoxes().size(); j++) {
					addMoveArrows((SentencePlanBox) father.getBoxes().get(j));
					((SentencePlanBox)father.getBoxes().get(j)).addDeleteButton();
				}
				father.getMainPanel().validate();
				father.updateScroll();
				
				father.dirtenOntologies();
			}
		});
		father.getMainPanel().increaseWidth(70);
		father.updateScroll();
		updatePositions();
		
		father.addToPluses(getThis().pos, p);
		
		int position = (father.getPositionInBoxes(getThis())*2)+1;
		father.getMainPanel().add(p, position);
		father.getMainPanel().validate();		
	}
	
	void switchBoxes(int i) {
		SentencePlanBox a = (SentencePlanBox) father.getBoxes().get(i - 1);
		SentencePlanBox b = (SentencePlanBox) father.getBoxes().get(i);
				
		father.removeFromBoxes(a);
		father.removeFromBoxes(b);
		father.addToBoxes(i - 1, b);
		father.addToBoxes(i, a);
		
		SPSlot aSlot = a.getSlot();
		SPSlot bSlot = b.getSlot();
		
		father.removeFromSlots(aSlot);
		father.removeFromSlots(bSlot);
		int aOrder = aSlot.getOrder();
		int bOrder = bSlot.getOrder();
		
		//Makes sure that even if slot order changes, "agrees with" points at the correct slots
		if ((aSlot instanceof SPOwnerSlot)||(aSlot instanceof SPFillerSlot)||(aSlot instanceof SPAdjectiveSlot)||(aSlot instanceof SPNounSlot)) {
			for (int f = 0; f < father.getBoxes().size(); f++) {
				ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
					if (mdl.getIndexOf(aSlot.getOrder()) != -1) {
						mdl.addElement(bOrder);
						if (!mdl.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
							if ((Integer)mdl.getSelectedItem() == aSlot.getOrder()) {
								mdl.setSelectedItem(bOrder);
							}
						}
						mdl.removeElement(aSlot.getOrder());
					}
	        	}
			}
		}
		//Makes sure that even if slot order changes, "agrees with" points at the correct slots
		if ((bSlot instanceof SPOwnerSlot)||(bSlot instanceof SPFillerSlot)||(bSlot instanceof SPAdjectiveSlot)||(bSlot instanceof SPNounSlot)) {
			for (int f = 0; f < father.getBoxes().size(); f++) {
				ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
					if (mdl.getIndexOf(bSlot.getOrder()) != -1) {
						mdl.addElement(aOrder);
						if (!mdl.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
							if ((Integer)mdl.getSelectedItem() == bSlot.getOrder()) {
								mdl.setSelectedItem(aOrder);
							}
						}
						mdl.removeElement(bSlot.getOrder());
					}
	        	}
			}
		}
				
		aSlot.setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + bOrder).getID());
		aSlot.setOrder(bOrder);
		if (aSlot instanceof SPConcatenationSlot) {
			for (int index = 0; index < ((SPConcatenationSlot)aSlot).getPropertySlots().size(); index++) {
				SPConcatenationPropertySlot concat = ((SPConcatenationSlot)aSlot).getPropertySlots().get(index);
				concat.setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.concatAnonymousIndivPattern + aSlot.getOrder() + "_" + (index + 1)).getID());
			}
		}
		
		bSlot.setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + aOrder).getID());
		bSlot.setOrder(aOrder);
		if (bSlot instanceof SPConcatenationSlot) {
			for (int index = 0; index < ((SPConcatenationSlot)bSlot).getPropertySlots().size(); index++) {
				SPConcatenationPropertySlot concat = ((SPConcatenationSlot)bSlot).getPropertySlots().get(index);
				concat.setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.concatAnonymousIndivPattern + bSlot.getOrder() + "_" + (index + 1)).getID());
			}
		}
		father.addToSlots(i - 1, bSlot);
		father.addToSlots(i, aSlot);

		a.updateOrderLabel();
		b.updateOrderLabel();
		
		father.repaintPreview();
		
		father.dirtenOntologies();
	}
	
	void addDeleteButton(){
		if(this.del==null){
			ClassLoader loader = SentencePlanTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/deleteImg.png");		
			ImageIcon delIcon = new ImageIcon(imageURL);	
			Image img = delIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			delIcon = new ImageIcon(newimg);  
			del = new JButton(delIcon);
			del.setOpaque(false);
			del.setContentAreaFilled(false);
			del.setBorderPainted(false);
			
			String t = "Are you sure you want to delete this?";
			final Object title = new String(t);
			del.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int ans = JOptionPane.showConfirmDialog(null, title, "Delete?",
							JOptionPane.OK_CANCEL_OPTION);
					if (ans == JOptionPane.OK_OPTION) {
						int position = (father.getPositionInBoxes(getThis()) + 1);
						father.removeFromPluses(position - 1);
						for (int j = position; j < father.getBoxes().size(); j++) {
							((SentencePlanBox)father.getBoxes().get(j)).getSlot().decreaseOrder();
							((SentencePlanBox)father.getBoxes().get(j)).getSlot().setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + ((SentencePlanBox)father.getBoxes().get(j)).getSlot().getOrder()).getID());
						}
						
						//Makes sure that even if slot gets deleted, "agrees with" doesn't point at it
						if ((getThis().slot instanceof SPOwnerSlot)||(getThis().slot instanceof SPFillerSlot)||(getThis().slot instanceof SPAdjectiveSlot)||(getThis().slot instanceof SPNounSlot)) {
					        for (int f = 0; f < father.getBoxes().size(); f++) {
					        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
					        	for (DefaultComboBoxModel mdl : mdls) {
									if (mdl.getIndexOf(getThis().getSlot().getOrder()) != -1) {
										if (!mdl.getSelectedItem().equals(SPVerbSlot.NONE_AGREE)) {
											if ((Integer)mdl.getSelectedItem() == getThis().getSlot().getOrder()) {
												mdl.setSelectedItem(SPVerbSlot.NONE_AGREE);
											}
										}
										
										mdl.removeElement(getThis().getSlot().getOrder());
									}
					        	}
					        }
						}
						
						father.removeFromSlots(getThis().slot);
						delete(getThis());

						//Makes sure that even if slot order changes, "agrees with" points at the correct slots
						for (int j = position - 1; j < father.getBoxes().size(); j++) {
							SPSlot decSlot = ((SentencePlanBox)father.getBoxes().get(j)).getSlot();
							if ((decSlot instanceof SPOwnerSlot)||(decSlot instanceof SPFillerSlot)||(decSlot instanceof SPAdjectiveSlot)||(decSlot instanceof SPNounSlot)) {
								for (int f = 0; f < father.getBoxes().size(); f++) {
									ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
						        	for (DefaultComboBoxModel mdl : mdls) {
										if (mdl.getIndexOf(decSlot.getOrder() + 1) != -1) {
											mdl.addElement(decSlot.getOrder());
	
											if (!mdl.getSelectedItem().equals(SPVerbSlot.NONE_AGREE)) {
												if ((Integer)mdl.getSelectedItem() == decSlot.getOrder() + 1) {
													mdl.setSelectedItem(decSlot.getOrder());
												}
											}
											mdl.removeElement(decSlot.getOrder() + 1);
										}
						        	}
								}
							}
							((SentencePlanBox)father.getBoxes().get(j)).updateOrderLabel();
						}
						
						father.dirtenOntologies();
					}
				}
			});
			this.buttonsPanel.add(del, 0);				
		}

		if(father.getBoxes().size()>1){
			del.setEnabled(true);
		} else {
			del.setEnabled(false);
		}
	}
	
	void addMoveArrows(SentencePlanBox b) {
	// if existing, remove them
		if (b.left != null)
			b.buttonsPanel.remove(b.left);
		if (b.right != null)
			b.buttonsPanel.remove(b.right);

		final int pos = father.getPositionInBoxes(b);
		
		if (pos > 0) {
			ClassLoader loader = SentencePlanTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/leftImg.png");		
			ImageIcon leftIcon = new ImageIcon(imageURL);	
			Image img = leftIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			leftIcon = new ImageIcon(newimg);  
			
			b.left = new JButton(leftIcon);
			b.left.setOpaque(false);
			b.left.setContentAreaFilled(false);
			b.left.setBorderPainted(false);
			b.left.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					SentencePlanBox leftBox = (SentencePlanBox) father.getBoxes().get(pos - 1);
					SentencePlanBox thisBox = (SentencePlanBox) father.getBoxes().get(pos);
					
					father.getMainPanel().switchOrder(pos*2);
					switchBoxes(pos);

					updatePositions();
					addMoveArrows(leftBox);
					addMoveArrows(thisBox);

					leftBox.box.repaint();
					thisBox.box.repaint();

					father.getMainPanel().validate();
					father.getMainPanel().repaint();											
				}
			});
			b.left.setVisible(true);
			b.buttonsPanel.add(b.left);
		}

		if (pos < father.getBoxes().size() - 1) {
			ClassLoader loader = SentencePlanTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/rightImg.png");		
			ImageIcon rightIcon = new ImageIcon(imageURL);				
			Image img = rightIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			rightIcon = new ImageIcon(newimg);  
			
			b.right = new JButton(rightIcon);
			b.right.setOpaque(false);
			b.right.setContentAreaFilled(false);
			b.right.setBorderPainted(false);
			b.right.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SentencePlanBox thisBox = (SentencePlanBox) father.getBoxes().get(pos);
					SentencePlanBox rightBox = (SentencePlanBox) father.getBoxes().get(pos+1);

					father.getMainPanel().switchOrder((pos*2)+2);
					switchBoxes(pos+1);
					
					updatePositions();	
					addMoveArrows(thisBox);
					addMoveArrows(rightBox);
					
					thisBox.box.repaint();
					rightBox.box.repaint();
					father.getMainPanel().validate();
					father.getMainPanel().repaint();
												
				}
			});
			b.right.setVisible(true);
			b.buttonsPanel.add(b.right);	
		}

		b.buttonsPanel.validate();
		b.box.validate();
		b.box.repaint();
	}
	
	
	void delete(SentencePlanBox b) {
		// delete from UI		
		father.getMainPanel().decreaseWidth(295); //225+70
		father.updateScroll();
		int pos = father.getPositionInBoxes(b);
		
		father.getMainPanel().remove(b.box);
		if(pos != 0 || father.getBoxes().size()!=1)
			father.getMainPanel().remove(father.getMainPanel().getComponent(pos*2));
		father.getMainPanel().validate();
		father.getMainPanel().repaint();			
		
		// delete from boxes list
		for (int i = 0; i < father.getBoxes().size(); i++) {
			if (father.getBoxes().get(i).equals(b)) {
				father.removeFromBoxes(b);
			}
		}
		updatePositions();
		for (int i = 0; i < father.getBoxes().size(); i++) {
			addMoveArrows((SentencePlanBox) father.getBoxes().get(i));
			((SentencePlanBox)father.getBoxes().get(i)).addDeleteButton();
		}
	}
	
	void updatePositions(){
		for(int i = 0; i < father.getBoxes().size(); i++){
			((SentencePlanBox)father.getBoxes().get(i)).setPosition(i);
		}
	}	
	
	SentencePlanBox getThis(){
		return this;
	}
	
	void setPosition(int i){
		this.pos = i;
	}
	
	void updateAll(){
		for(int i = 0; i < father.getBoxes().size(); i++){
			addMoveArrows((SentencePlanBox) father.getBoxes().get(i));
			((SentencePlanBox)father.getBoxes().get(i)).addDeleteButton();
		}
	}
	
	public void itemStateChanged(ItemEvent evt) {
		//if (evt.getItem().equals(ownerSlotStr) || evt.getItem().equals(fillerSlotStr) || evt.getItem().equals(lexiconSlotStr) || evt.getItem().equals(prepositionSlotStr) || evt.getItem().equals(stringSlotStr) || evt.getItem().equals(concatenationSlotStr)) {
		if (evt.getSource() == select) {
			String option = (String)evt.getItem();
			
			if (option.equals(ownerSlotStr)) {
				slot = new SPOwnerSlot(XmlMsgs.NOMINATIVE_TAG, SPOwnerSlot.REF_AUTO, slot.getId(), slot.getOrder());
			}
			else if (option.equals(fillerSlotStr)) {
				slot = new SPFillerSlot(XmlMsgs.NOMINATIVE_TAG, false, slot.getId(), slot.getOrder());
			}
			else if (option.equals(adjectiveSlotStr)) {
				slot = new SPAdjectiveSlot(null, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(nounSlotStr)) {
				slot = new SPNounSlot(null, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(verbSlotStr)) {
				slot = new SPVerbSlot(null, XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.ACTIVE_VOICE, SPVerbSlot.POLARITY_POSITIVE, XmlMsgs.SINGULAR, XmlMsgs.PERSON_1ST, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(prepositionSlotStr)) {
				if (language.equals(Languages.ENGLISH)) {
					slot = new SPPrepositionSlot(SPPrepositionSlot.PREPOSITION_EN_AFTER, slot.getId(), slot.getOrder());
		        }
				else if(language.equals(Languages.GREEK)) {
					slot = new SPPrepositionSlot(SPPrepositionSlot.PREPOSITION_GR_APO, slot.getId(), slot.getOrder());
				}
			}
			else if (option.equals(stringSlotStr)) {
				slot = new SPStringSlot("", slot.getId(), slot.getOrder());
			}
			else if (option.equals(concatenationSlotStr)) {
				slot = new SPConcatenationSlot(slot.getId(), slot.getOrder());
			}

			father.setSlot(slot, this.pos);
			father.dirtenOntologies();
			loadSlotInBox(slot);
		}
		//Owner Slot
		else if (evt.getSource() == exprCombo) {
			((SPOwnerSlot)slot).setRefType(exprCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == ownCaseCombo) {
			((SPOwnerSlot)slot).setCase(ownCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		//Filler Slot
		else if (evt.getSource() == indCaseCombo) {
			((SPFillerSlot)slot).setCase(indCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == bulletsCheck) {
			if (bulletsCheck.isSelected())
				((SPFillerSlot)slot).setBullets(true);
			else
				((SPFillerSlot)slot).setBullets(false);
			father.dirtenOntologies();
		}
		//Adjective Slot
		else if (evt.getSource() == adjLECombo) {
			((SPAdjectiveSlot)slot).setLexiconEntryIRI(((ListIRI)adjLECombo.getSelectedItem()).getEntryIRI());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjCaseCombo) {
			((SPAdjectiveSlot)slot).setCase(adjCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjGenderCombo) {
			((SPAdjectiveSlot)slot).setGender(adjGenderCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjNumCombo) {
			((SPAdjectiveSlot)slot).setNumber(adjNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjAgreeWithCombo) {
			if (adjAgreeWithCombo.getSelectedItem() != null ) {
				if (adjAgreeWithCombo.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
					((SPAdjectiveSlot)slot).setAgreesWithID(null);
					father.dirtenOntologies();
	
					if(language.equals(Languages.GREEK)) {
						adjCaseCombo.setEnabled(true);
						if (adjCaseCombo.getSelectedIndex() == -1)
							adjCaseCombo.setSelectedIndex(0);
						adjCaseCombo.addItemListener(this);
						adjGenderCombo.setEnabled(true);
						if (adjGenderCombo.getSelectedIndex() == -1)
							adjGenderCombo.setSelectedIndex(0);
						adjGenderCombo.addItemListener(this);
					}
					adjNumCombo.setEnabled(true);
					if (adjNumCombo.getSelectedIndex() == -1)
						adjNumCombo.setSelectedIndex(0);
					adjNumCombo.addItemListener(this);
				}
				else {
					HashSet<SPSlot> slots = new HashSet<SPSlot>();
					slots.add(slot);
					
					if (agreeCycleExists(slots, Integer.parseInt(adjAgreeWithCombo.getSelectedItem().toString()))) {
						adjAgreeWithCombo.setSelectedItem(SPSlot.NONE_AGREE);
						JOptionPane.showMessageDialog(null,
							    "Cyclic agreement between slots.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} else {
						((SPAdjectiveSlot)slot).setAgreesWithID(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + adjAgreeWithCombo.getSelectedItem().toString()).getID());
						father.dirtenOntologies();
						
						if(language.equals(Languages.GREEK)) {
							adjCaseCombo.setEnabled(false);
							adjCaseCombo.removeItemListener(this);
							adjGenderCombo.setEnabled(false);
							adjGenderCombo.removeItemListener(this);
						}
						adjNumCombo.setEnabled(false);
						adjNumCombo.removeItemListener(this);
					}
				}
			}
		}
		//Noun Slot
		else if (evt.getSource() == nounLECombo) {
			((SPNounSlot)slot).setLexiconEntryIRI(((ListIRI)nounLECombo.getSelectedItem()).getEntryIRI());
			
			LexEntryNoun entry = SentencePlanTab.LQM.getNounEntry(((SPNounSlot)slot).getLexiconEntryIRI(), language);

			nounNumCombo.setEnabled(true);
			if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
				nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
				nounNumCombo.setEnabled(false);
			} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
				nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
				nounNumCombo.setEnabled(false);
			}
			
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounCaseCombo) {
			((SPNounSlot)slot).setCase(nounCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounNumCombo) {
			((SPNounSlot)slot).setNumber(nounNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounAgreeWithCombo) {
			if (nounAgreeWithCombo.getSelectedItem() != null ) {
				if (nounAgreeWithCombo.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
					((SPNounSlot)slot).setAgreesWithID(null);
					father.dirtenOntologies();
	
					if(language.equals(Languages.GREEK)) {
						nounCaseCombo.setEnabled(true);
						if (nounCaseCombo.getSelectedIndex() == -1)
							nounCaseCombo.setSelectedIndex(0);
						nounCaseCombo.addItemListener(this);
					}
					nounNumCombo.setEnabled(true);
					if (nounNumCombo.getSelectedIndex() == -1)
						nounNumCombo.setSelectedIndex(0);
					nounNumCombo.addItemListener(this);
				} else {
					HashSet<SPSlot> slots = new HashSet<SPSlot>();
					slots.add(slot);
					
					if (agreeCycleExists(slots, Integer.parseInt(nounAgreeWithCombo.getSelectedItem().toString()))) {
						nounAgreeWithCombo.setSelectedItem(SPSlot.NONE_AGREE);
						JOptionPane.showMessageDialog(null,
							    "Cyclic agreement between slots.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} else {
						((SPNounSlot)slot).setAgreesWithID(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + nounAgreeWithCombo.getSelectedItem().toString()).getID());
						father.dirtenOntologies();
						
						if(language.equals(Languages.GREEK)) {
							nounCaseCombo.setEnabled(false);
							nounCaseCombo.removeItemListener(this);
						}
						nounNumCombo.setEnabled(false);
						nounNumCombo.removeItemListener(this);
					}
				}
			}
		}
		//Verb Entry Slot
		else if (evt.getSource() == verbLECombo) {
			((SPVerbSlot)slot).setLexiconEntryIRI(((ListIRI)verbLECombo.getSelectedItem()).getEntryIRI());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == polarityCombo) {
			((SPVerbSlot)slot).setPolarity(polarityCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == verbTenseCombo) {
			((SPVerbSlot)slot).setTense(verbTenseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == verbVoiceCombo) {
			((SPVerbSlot)slot).setVoice(verbVoiceCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == verbNumCombo) {
			((SPVerbSlot)slot).setNumber(verbNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == verbPerCombo) {
			((SPVerbSlot)slot).setPerson(verbPerCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == verbAgreeWithCombo) {
			
			if (verbAgreeWithCombo.getSelectedItem().equals(SPSlot.NONE_AGREE)) {
				((SPVerbSlot)slot).setAgreesWithID(null);
				father.dirtenOntologies();
				
				verbNumCombo.setEnabled(true);
				if (verbNumCombo.getSelectedIndex() == -1)
					verbNumCombo.setSelectedIndex(0);
				verbNumCombo.addItemListener(this);
				verbPerCombo.setEnabled(true);
				if (verbPerCombo.getSelectedIndex() == -1)
					verbPerCombo.setSelectedIndex(0);
				verbPerCombo.addItemListener(this);
			}
			else {
				HashSet<SPSlot> slots = new HashSet();
				slots.add(slot);
				
				if (agreeCycleExists(slots, Integer.parseInt(verbAgreeWithCombo.getSelectedItem().toString()))) {
					verbAgreeWithCombo.setSelectedItem(SPSlot.NONE_AGREE);
					JOptionPane.showMessageDialog(null,
					    "Cyclic agreement between slots.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				} else {
					((SPVerbSlot)slot).setAgreesWithID(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.anonymousIndivPattern + verbAgreeWithCombo.getSelectedItem().toString()).getID());
					father.dirtenOntologies();
					
					verbNumCombo.setEnabled(false);
					verbNumCombo.removeItemListener(this);
					verbPerCombo.setEnabled(false);
					verbPerCombo.removeItemListener(this);
				}
			}
		}
		//Preposition Slot
		else if (evt.getSource() == prepLECombo) {
			((SPPrepositionSlot)slot).setPrep(prepLECombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
	}
	
	//Dynamic Panels
	void dynamicPanels(){
		JPanel ownerCard = new JPanel();
		JPanel ownerCardSub1 = new JPanel();
		JPanel ownerCardSub2 = new JPanel();
		JPanel fillerCard = new JPanel();
		JPanel fillerCardSub1 = new JPanel();
		JPanel fillerCardSub2 = new JPanel();
		JPanel adjectiveCard = new JPanel();
		JPanel adjectiveCardSub2 = new JPanel();
		JPanel adjectiveCardSub3 = new JPanel();
		JPanel adjectiveCardSub4 = new JPanel();
		JPanel adjectiveCardSub5 = new JPanel();
		JPanel adjectiveCardSub6 = new JPanel();
		JPanel nounCard = new JPanel();
		JPanel nounCardSub2 = new JPanel();
		JPanel nounCardSub3 = new JPanel();
		JPanel nounCardSub4 = new JPanel();
		JPanel nounCardSub5 = new JPanel();
		JPanel verbSlotCard = new JPanel();
		JPanel verbSlotCardSub1 = new JPanel();
		JPanel verbSlotCardSub2 = new JPanel();
		JPanel verbSlotCardSub3 = new JPanel();
		JPanel verbSlotCardSub4 = new JPanel();
		JPanel verbSlotCardSub5 = new JPanel();
		JPanel verbSlotCardSub6 = new JPanel();
		JPanel verbSlotCardSub7 = new JPanel();
		JPanel prepositionCard = new JPanel();
		JPanel prepositionCardSub1 = new JPanel();
		JPanel stringCard = new JPanel();
		JPanel stringCardSub1 = new JPanel();
		JPanel concatenationCard = new JPanel();
		JPanel concatenationCardSub1 = new JPanel();
		JPanel concatenationCardSub2 = new JPanel();
		JPanel concatenationCardSub3 = new JPanel();
		JPanel concatenationCardSub4 = new JPanel();
		JPanel concatenationCardSub5 = new JPanel();
		
		ownerCard.setLayout(new GridLayout(8,0));
		ownerCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		fillerCard.setLayout(new GridLayout(8,0));
		fillerCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		fillerCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCard.setLayout(new GridLayout(8,0));
		adjectiveCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub6.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCard.setLayout(new GridLayout(8,0));
		nounCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCard.setLayout(new GridLayout(8,0));
		verbSlotCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub6.setLayout(new FlowLayout(FlowLayout.LEFT));
		verbSlotCardSub7.setLayout(new FlowLayout(FlowLayout.LEFT));
		prepositionCard.setLayout(new GridLayout(8,0));
		prepositionCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		stringCard.setLayout(new GridLayout(0,1));
		stringCardSub1.setLayout(new BorderLayout());
		concatenationCard.setLayout(new BorderLayout());
		concatenationCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		concatenationCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		concatenationCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		concatenationCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		concatenationCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		//property owner
		exprLabel = new JLabel("Expression Type"); 
		String[] choices = {SPOwnerSlot.REF_AUTO, SPOwnerSlot.REF_PRONOUN, SPOwnerSlot.REF_DEMONSTRATIVE};
		exprCombo = new JComboBox(choices);
        
		ownerCardSub1.add(exprLabel);
        ownerCardSub1.add(exprCombo);
        ownerCard.add(ownerCardSub1);
        
        Dimension labelD = exprLabel.getPreferredSize();
        Dimension comboD = exprCombo.getPreferredSize();
		int boxLabelWidth = 85;
		int boxComboWidth = 105;

		exprLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
		exprCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		
		ownCaseLabel = new JLabel("Case");
        if(language.equals(Languages.GREEK)) {
			String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
			ownCaseCombo = new JComboBox(cases);
        } else if(language.equals(Languages.ENGLISH)) {
			String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG};
			ownCaseCombo = new JComboBox(cases);
        }
        ownCaseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        ownCaseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        
        ownerCardSub2.add(ownCaseLabel);
        ownerCardSub2.add(ownCaseCombo);
        ownerCard.add(ownerCardSub2);
        
        //filler
		indCaseLabel = new JLabel("Case");
        if(language.equals(Languages.GREEK)) {
			String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
	        indCaseCombo = new JComboBox(cases);
        } else if(language.equals(Languages.ENGLISH)) {
			String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG};
	        indCaseCombo = new JComboBox(cases);
        }
        
        fillerCardSub1.add(indCaseLabel);
        fillerCardSub1.add(indCaseCombo);
        fillerCard.add(fillerCardSub1);

		bulletsCheck = new JCheckBox("Use bullets", false);
        fillerCardSub2.add(bulletsCheck);
        fillerCard.add(fillerCardSub2);       

		indCaseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
		indCaseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		
		//adjective
		JLabel adjLELabel = new JLabel("Lexicon Entry"); 

		HashSet<ListIRI> adjLexiconEntries = new HashSet();
		for (IRI iri : SentencePlanTab.LQM.getAdjectiveEntries()) {
			adjLexiconEntries.add(new ListIRI(iri));
		}
		
		ArrayList<ListIRI> sortedList = new ArrayList(adjLexiconEntries);
        Collections.sort(sortedList);
        adjLECombo = new JComboBox(sortedList.toArray());    
        adjLECombo.setRenderer(new ListRenderer());
        
        adjectiveCardSub2.add(adjLELabel);
        adjectiveCardSub2.add(adjLECombo); 
        adjectiveCard.add(adjectiveCardSub2);

        if(language.equals(Languages.GREEK)) {
            adjCaseLabel = new JLabel("Case");
            String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
            adjCaseCombo = new JComboBox(cases);
            
            adjectiveCardSub3.add(adjCaseLabel);
            adjectiveCardSub3.add(adjCaseCombo);
            adjectiveCard.add(adjectiveCardSub3);

            adjGenderLabel = new JLabel("Gender");
            String[] genders = {XmlMsgs.GENDER_MASCULINE, XmlMsgs.GENDER_FEMININE, XmlMsgs.GENDER_NEUTER};
            adjGenderCombo = new JComboBox(genders);

            adjectiveCardSub4.add(adjGenderLabel);
            adjectiveCardSub4.add(adjGenderCombo);
            adjectiveCard.add(adjectiveCardSub4);
		}
        
        adjNumLabel = new JLabel("Number");
        String[] numbers = {XmlMsgs.SINGULAR, XmlMsgs.PLURAL};
        adjNumCombo = new JComboBox(numbers);         
            
        adjectiveCardSub5.add(adjNumLabel);
        adjectiveCardSub5.add(adjNumCombo);
        adjectiveCard.add(adjectiveCardSub5);
        
		adjAgreeWithSlotModel = new DefaultComboBoxModel();
		adjAgreeWithSlotModel.addElement(SPSlot.NONE_AGREE);
		for (int j = 0; j < father.getBoxes().size(); j++) {
			SPSlot s = ((SentencePlanBox)father.getBoxes().get(j)).getSlot();
			if ((s instanceof SPOwnerSlot)||(s instanceof SPFillerSlot)||(s instanceof SPAdjectiveSlot)||(s instanceof SPNounSlot)) {
				if (s.getOrder() != getSlot().getOrder()) {
					adjAgreeWithSlotModel.addElement(s.getOrder());
				}
			}
		}
		JLabel adjAgreeWithLabel = new JLabel("Agree with slot ");
		adjAgreeWithCombo = new JComboBox(adjAgreeWithSlotModel);
		
		adjectiveCardSub6.add(adjAgreeWithLabel);
		adjectiveCardSub6.add(adjAgreeWithCombo);
        adjectiveCard.add(adjectiveCardSub6);

        adjLELabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        if(language.equals(Languages.GREEK)) {
        	adjCaseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        	adjGenderLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
		}
        adjNumLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        adjAgreeWithLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));

        adjLECombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		if(language.equals(Languages.GREEK)) {
			adjCaseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
			adjGenderCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		}
		adjNumCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		adjAgreeWithCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
			
		//noun
		JLabel nounLELabel = new JLabel("Lexicon Entry"); 
		
		HashSet<ListIRI> nounLexiconEntries = new HashSet();
		for (IRI iri : SentencePlanTab.LQM.getNounEntries()) {
			nounLexiconEntries.add(new ListIRI(iri));
		}
		
		sortedList = new ArrayList(nounLexiconEntries);
        Collections.sort(sortedList);		
		nounLECombo = new JComboBox(sortedList.toArray());
		nounLECombo.setRenderer(new ListRenderer());
        
		nounCardSub2.add(nounLELabel);
		nounCardSub2.add(nounLECombo); 
		nounCard.add(nounCardSub2);
            
        if(language.equals(Languages.GREEK)) {
            nounCaseLabel = new JLabel("Case");
            String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
            nounCaseCombo = new JComboBox(cases);
            
            nounCardSub3.add(nounCaseLabel);
            nounCardSub3.add(nounCaseCombo);
    		nounCard.add(nounCardSub3);
		}
        
        nounNumLabel = new JLabel("Number");
        nounNumCombo = new JComboBox(numbers);         
            
        nounCardSub4.add(nounNumLabel);
        nounCardSub4.add(nounNumCombo);
		nounCard.add(nounCardSub4);
        
		nounAgreeWithSlotModel = new DefaultComboBoxModel();
		nounAgreeWithSlotModel.addElement(SPSlot.NONE_AGREE);
		for (int j = 0; j < father.getBoxes().size(); j++) {
			SPSlot s = ((SentencePlanBox)father.getBoxes().get(j)).getSlot();
			if ((s instanceof SPOwnerSlot)||(s instanceof SPFillerSlot)||(s instanceof SPAdjectiveSlot)||(s instanceof SPNounSlot)) {
				if (s.getOrder() != getSlot().getOrder()) {
					nounAgreeWithSlotModel.addElement(s.getOrder());
				}
			}
		}
		JLabel nounAgreeWithLabel = new JLabel("Agree with slot ");
		nounAgreeWithCombo = new JComboBox(nounAgreeWithSlotModel);
		
		nounCardSub5.add(nounAgreeWithLabel);
		nounCardSub5.add(nounAgreeWithCombo);   
		nounCard.add(nounCardSub5);
		
		nounLELabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        if(language.equals(Languages.GREEK)) {
        	nounCaseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
		}
        nounNumLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        nounAgreeWithLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));

        nounLECombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		if(language.equals(Languages.GREEK)) {
			nounCaseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		}
		nounNumCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		nounAgreeWithCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		
        //verb
		verbLELabel = new JLabel("Lexicon Entry"); 
		
		HashSet<ListIRI> verbLexiconEntries = new HashSet();
		
		for (IRI iri : SentencePlanTab.LQM.getVerbEntries()) {
			verbLexiconEntries.add(new ListIRI(iri));
		}
		
		sortedList = new ArrayList(verbLexiconEntries);
        Collections.sort(sortedList);
        verbLECombo = new JComboBox(sortedList.toArray());      
        verbLECombo.setRenderer(new ListRenderer());    
        
        verbSlotCardSub1.add(verbLELabel);
        verbSlotCardSub1.add(verbLECombo); 
        verbSlotCard.add(verbSlotCardSub1);         
        
		if(language.equals(Languages.ENGLISH)) {
			tenseLabel = new JLabel("Tense/Aspect");
            String[] tenses = {XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.TENSE_PRESENT_CONTINUOUS, XmlMsgs.TENSE_PRESENT_PERFECT, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.TENSE_PAST_PERFECT_CONTINUOUS, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.TENSE_FUTURE_CONTINUOUS, XmlMsgs.TENSE_FUTURE_PERFECT, XmlMsgs.TENSE_FUTURE_PERFECT_CONTINUOUS, XmlMsgs.TENSE_PARTICIPLE}; 
            verbTenseCombo = new JComboBox(tenses);
		}
		else if(language.equals(Languages.GREEK)) {
            //verb
            tenseLabel = new JLabel("Tense/Aspect");
            String[] tenses = {XmlMsgs.TENSE_SIMPLE_PRESENT, XmlMsgs.TENSE_PRESENT_PERFECT, XmlMsgs.TENSE_SIMPLE_PAST, XmlMsgs.TENSE_PAST_CONTINUOUS, XmlMsgs.TENSE_PAST_PERFECT, XmlMsgs.TENSE_SIMPLE_FUTURE, XmlMsgs.TENSE_FUTURE_CONTINUOUS, XmlMsgs.TENSE_FUTURE_PERFECT, XmlMsgs.TENSE_PARTICIPLE};
            verbTenseCombo = new JComboBox(tenses);
		}
        verbSlotCardSub2.add(tenseLabel);
        verbSlotCardSub2.add(verbTenseCombo);
        verbSlotCard.add(verbSlotCardSub2);
        
        polarityLabel = new JLabel("Polarity");
        String[] polarities = {SPVerbSlot.POLARITY_POSITIVE, SPVerbSlot.POLARITY_NEGATIVE, SPVerbSlot.POLARITY_FILLER};
        polarityCombo = new JComboBox(polarities);
        
        verbSlotCardSub3.add(polarityLabel);
        verbSlotCardSub3.add(polarityCombo);
        verbSlotCard.add(verbSlotCardSub3);
        
        voiceLabel = new JLabel("Voice");
        String[] voices = {XmlMsgs.ACTIVE_VOICE, XmlMsgs.PASSIVE_VOICE}; 
        verbVoiceCombo = new JComboBox(voices);
        
        verbSlotCardSub4.add(voiceLabel);
        verbSlotCardSub4.add(verbVoiceCombo);
        verbSlotCard.add(verbSlotCardSub4);
        
        numberLabel = new JLabel("Number");
        verbNumCombo = new JComboBox(numbers); //to be disabled when agree with is chosen
        
        verbSlotCardSub5.add(numberLabel);
        verbSlotCardSub5.add(verbNumCombo);
        verbSlotCard.add(verbSlotCardSub5);
        
        personLabel = new JLabel("Person");
        String[] persons = {XmlMsgs.PERSON_1ST, XmlMsgs.PERSON_2ND, XmlMsgs.PERSON_3RD};
        verbPerCombo = new JComboBox(persons); //to be disabled when agree with is chosen

        verbSlotCardSub6.add(personLabel);
        verbSlotCardSub6.add(verbPerCombo);
        verbSlotCard.add(verbSlotCardSub6);
        
        verbAgreeWithSlotModel = new DefaultComboBoxModel();
        verbAgreeWithSlotModel.addElement(SPSlot.NONE_AGREE);
        for (int j = 0; j < father.getBoxes().size(); j++) {
        	SPSlot s = ((SentencePlanBox)father.getBoxes().get(j)).getSlot();
        	if ((s instanceof SPOwnerSlot)||(s instanceof SPFillerSlot)||(s instanceof SPNounSlot)) {
        		verbAgreeWithSlotModel.addElement(s.getOrder());
        	}
		}
        JLabel agreeWithLabel = new JLabel("Agree with slot ");
        verbAgreeWithCombo = new JComboBox(verbAgreeWithSlotModel);

        verbSlotCardSub7.add(agreeWithLabel);
        verbSlotCardSub7.add(verbAgreeWithCombo);
        verbSlotCard.add(verbSlotCardSub7);        

        verbLELabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        tenseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        polarityLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        voiceLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        numberLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        personLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        agreeWithLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));

        verbLECombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        verbTenseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        polarityCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        verbVoiceCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        verbNumCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		verbPerCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		verbAgreeWithCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        
        //preposition
        prepLELabel = new JLabel("Preposition");
        String[] preps = new String[0];
        if(language.equals(Languages.ENGLISH)) {
        	preps = SPPrepositionSlot.getEnglishPrepositionList().toArray(preps);
        }
		else if(language.equals(Languages.GREEK)) {
        	preps = SPPrepositionSlot.getGreekPrepositionList().toArray(preps);
		}
        prepLECombo = new JComboBox(preps);            

        prepositionCardSub1.add(prepLELabel);
        prepositionCardSub1.add(prepLECombo);
        prepositionCard.add(prepositionCardSub1);
        
        prepLELabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        prepLECombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));

        //string slot
        JLabel stringLabel = new JLabel("String");
        stringTextArea = new JTextArea(8, 8);
        stringTextArea.setLineWrap(true);
        stringTextArea.getDocument().putProperty("name", "stringText");
        JScrollPane scroll = new JScrollPane(stringTextArea);
        
        stringCardSub1.add(BorderLayout.NORTH, stringLabel);
        stringCardSub1.add(BorderLayout.CENTER, scroll);
        stringCard.add(stringCardSub1);
        
        //concatenation slot
        concatLabel = new JLabel("Add filler properties to concatenate");

        HashSet<ListIRI> propertyEntries = new HashSet();
        for (OWLOntology owl : father.getOWLModelManager().getActiveOntologies()) {
        	for (OWLObjectProperty prop : owl.getObjectPropertiesInSignature()) {
        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
        			propertyEntries.add(new ListIRI(prop.getIRI()));
        		}
        	}
        	for (OWLDataProperty prop : owl.getDataPropertiesInSignature()) {
        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
        			propertyEntries.add(new ListIRI(prop.getIRI()));
        		}
        	}
        }
        
        ArrayList<ListIRI> sortList = new ArrayList(propertyEntries);
        Collections.sort(sortList);
        
        concatComboModel = new DefaultComboBoxModel(sortList.toArray());
        concatCombo = new JComboBox(concatComboModel);
        concatCombo.setRenderer(new ListRenderer());
        
        concatAdd = new JButton("+");
        
        concatenationCardSub1.add(concatLabel);
        concatenationCardSub1.add(concatCombo);
        concatenationCardSub1.add(concatAdd);
        concatenationCardSub1.setPreferredSize(new Dimension(190, 50));
        concatenationCard.add(BorderLayout.NORTH, concatenationCardSub1);
        
        concatListLabel = new JLabel("Filler properties to concatenate");
        
        concatListModel = new DefaultListModel();
        concatList = new JList(concatListModel);
        concatList.setCellRenderer(new ListRenderer());
        concatList.setVisibleRowCount(10);
        JScrollPane concatListPane = new JScrollPane(concatList);
        concatListPane.setPreferredSize(new Dimension(190, 140));

        concatenationCardSub2.add(concatListLabel);
        concatenationCardSub2.add(concatListPane);
        concatenationCard.add(BorderLayout.CENTER, concatenationCardSub2);
        
        concatMinus = new JButton("-");
        concatUp = new JButton("UP");
        concatDown = new JButton("DOWN");
        
        concatenationCardSub3.add(concatMinus);
        concatenationCardSub3.add(concatUp);
        concatenationCardSub3.add(concatDown);
        concatenationCard.add(BorderLayout.SOUTH, concatenationCardSub3);
                
		dynamicPanel.add(ownerCard, ownerSlotStr);
		dynamicPanel.add(fillerCard, fillerSlotStr);
		dynamicPanel.add(adjectiveCard, adjectiveSlotStr);
		dynamicPanel.add(nounCard, nounSlotStr);
		dynamicPanel.add(verbSlotCard, verbSlotStr);
		dynamicPanel.add(prepositionCard, prepositionSlotStr);
		dynamicPanel.add(stringCard, stringSlotStr);
		dynamicPanel.add(concatenationCard, concatenationSlotStr);
	}
	
	void loadSlotInBox(SPSlot slot) {
		this.select.removeItemListener(this);
		removeListenersOfLastOpenSlotTab();
		
		CardLayout cl = (CardLayout)(dynamicPanel.getLayout());
		
		if (slot instanceof SPOwnerSlot) {
			lastOpenSlotTab = ownerSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
		        	if (mdl.getIndexOf(slot.getOrder()) == -1)
		        		mdl.addElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(ownerSlotStr);
			
			exprCombo.setSelectedItem(((SPOwnerSlot)slot).getRefType());
			exprCombo.addItemListener(this);
			
			ownCaseCombo.setSelectedItem(((SPOwnerSlot)slot).getCase());
			ownCaseCombo.addItemListener(this);
		}
		else if (slot instanceof SPFillerSlot) {
			lastOpenSlotTab = fillerSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) == -1)
	        			mdl.addElement(slot.getOrder());
	        	}
			}
	        
			select.setSelectedItem(fillerSlotStr);
			
			indCaseCombo.setSelectedItem(((SPFillerSlot)slot).getCase());
			indCaseCombo.addItemListener(this);
			
			if (((SPFillerSlot)slot).getBullets()) {
				bulletsCheck.setSelected(true);
			}
			else {
				bulletsCheck.setSelected(false);
			}
			bulletsCheck.addItemListener(this);
		}
		else if (slot instanceof SPAdjectiveSlot) {
			lastOpenSlotTab = adjectiveSlotStr;
			
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if ((mdl.getIndexOf(slot.getOrder()) == -1)&&((SentencePlanBox)father.getBoxes().get(j)).getSlot().getOrder() != slot.getOrder())
		        		mdl.addElement(slot.getOrder());
	        	}
			}
	        
			select.setSelectedItem(adjectiveSlotStr);
			
			if (((SPAdjectiveSlot)slot).getLexiconEntryIRI() != null)
				adjLECombo.setSelectedItem(new ListIRI(((SPAdjectiveSlot)slot).getLexiconEntryIRI()));
			else if (adjLECombo.getSelectedItem() != null)
				((SPAdjectiveSlot)slot).setLexiconEntryIRI(((ListIRI)adjLECombo.getSelectedItem()).getEntryIRI());

			adjLECombo.addItemListener(this);
			adjLECombo.addFocusListener(this);
							
			if (language.equals(Languages.GREEK)) {
				adjCaseLabel.setText("Case");
				adjGenderLabel.setText("Gender");
			}
			adjNumLabel.setText("Number");
			
			if (((SPAdjectiveSlot)slot).getAgreesWithID() == null) {
				if (language.equals(Languages.GREEK)) {
					adjCaseCombo.setEnabled(true);
					adjCaseCombo.setSelectedItem(((SPAdjectiveSlot)slot).getCase());
					adjCaseCombo.addItemListener(this);
					adjGenderCombo.setEnabled(true);
					adjGenderCombo.setSelectedItem(((SPAdjectiveSlot)slot).getGender());
					adjGenderCombo.addItemListener(this);
				}
				adjNumCombo.setEnabled(true);
				adjNumCombo.setSelectedItem(((SPAdjectiveSlot)slot).getNumber());
				adjNumCombo.addItemListener(this);
			}
			else {
				String agreeID = ((SPAdjectiveSlot)slot).getAgreesWithID().toString();

				int agreeSlot = -1;
				for (int i = 0; i < father.getSlots().size(); i++) {
					if (father.getSlots().get(i).getId().toString().equals(agreeID)) {
						agreeSlot = i + 1;
					}
				}

				if (agreeSlot != -1) {
					if (adjAgreeWithSlotModel.getIndexOf(agreeSlot) == -1) {
						adjAgreeWithSlotModel.addElement(agreeSlot);
					}
					adjAgreeWithCombo.setSelectedItem(agreeSlot);
					
					if (language.equals(Languages.GREEK)) {
						adjCaseCombo.setEnabled(false);
						adjCaseCombo.setSelectedItem(null);
						adjCaseCombo.removeItemListener(this);
						adjGenderCombo.setEnabled(false);
						adjGenderCombo.setSelectedItem(null);
						adjGenderCombo.removeItemListener(this);
					}
					adjNumCombo.setEnabled(false);
					adjNumCombo.setSelectedItem(null);
					adjNumCombo.removeItemListener(this);
				} else {
					System.err.println("Cannot find slot " + agreeID);
					
					if (language.equals(Languages.GREEK)) {
						adjCaseCombo.setEnabled(true);
						adjCaseCombo.setSelectedItem(((SPAdjectiveSlot)slot).getCase());
						adjCaseCombo.addItemListener(this);
						adjGenderCombo.setEnabled(true);
						adjGenderCombo.setSelectedItem(((SPAdjectiveSlot)slot).getGender());
						adjGenderCombo.addItemListener(this);
					}
					adjNumCombo.setEnabled(true);
					adjNumCombo.setSelectedItem(((SPAdjectiveSlot)slot).getNumber());
					adjNumCombo.addItemListener(this);
				}
			}
			
			adjAgreeWithCombo.addFocusListener(this);
			adjAgreeWithCombo.addItemListener(this);
		}
		else if (slot instanceof SPNounSlot) {
			lastOpenSlotTab = nounSlotStr;
			
			for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
		        	if ((mdl.getIndexOf(slot.getOrder()) == -1)&&((SentencePlanBox)father.getBoxes().get(j)).getSlot().getOrder() != slot.getOrder())
		        		mdl.addElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(nounSlotStr);

			if (((SPNounSlot)slot).getLexiconEntryIRI() != null)
				nounLECombo.setSelectedItem(new ListIRI(((SPNounSlot)slot).getLexiconEntryIRI()));
			else if (nounLECombo.getSelectedItem() != null)
				((SPNounSlot)slot).setLexiconEntryIRI(((ListIRI)nounLECombo.getSelectedItem()).getEntryIRI());

			nounLECombo.addItemListener(this);
			nounLECombo.addFocusListener(this);
				
			if (language.equals(Languages.GREEK)) {
				nounCaseLabel.setText("Case");
			}
			nounNumLabel.setText("Number");
			
			if (((SPNounSlot)slot).getAgreesWithID() == null) {
				if (language.equals(Languages.GREEK)) {
					nounCaseCombo.setEnabled(true);
					nounCaseCombo.setSelectedItem(((SPNounSlot)slot).getCase());
					nounCaseCombo.addItemListener(this);
				}
				nounNumCombo.setEnabled(true);
				nounNumCombo.setSelectedItem(((SPNounSlot)slot).getNumber());
				
				if (((SPNounSlot)slot).getLexiconEntryIRI() != null) {
					LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((SPNounSlot)slot).getLexiconEntryIRI(), language);
	
					if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
						nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
						nounNumCombo.setEnabled(false);
						
						if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
							((SPNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
						}
					} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
						nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
						nounNumCombo.setEnabled(false);
						
						if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
							((SPNounSlot)slot).setNumber(XmlMsgs.PLURAL);
						}
					}
				}
				
				nounNumCombo.addItemListener(this);
			}
			else {
				String agreeID = ((SPNounSlot)slot).getAgreesWithID().toString();
				
				int agreeSlot = -1;
				for (int i = 0; i < father.getSlots().size(); i++) {
					if (father.getSlots().get(i).getId().toString().equals(agreeID)) {
						agreeSlot = i + 1;
					}
				}

				if (agreeSlot != -1) {
					if (nounAgreeWithSlotModel.getIndexOf(agreeSlot) == -1) {
						System.err.println("Slot " + agreeID + " is neither an adjective nor a noun.");
						nounAgreeWithSlotModel.addElement(agreeSlot);
					}
					nounAgreeWithCombo.setSelectedItem(agreeSlot);
					
					if (language.equals(Languages.GREEK)) {
						nounCaseCombo.setEnabled(false);
						nounCaseCombo.setSelectedItem(null);
						nounCaseCombo.removeItemListener(this);
					}
					nounNumCombo.setEnabled(false);
					nounNumCombo.setSelectedItem(null);
				
					LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((SPNounSlot)slot).getLexiconEntryIRI(), language);		
	
					if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
						nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
						nounNumCombo.setEnabled(false);
						
						if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
							((SPNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
						}
					} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
						nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
						nounNumCombo.setEnabled(false);
						
						if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
							((SPNounSlot)slot).setNumber(XmlMsgs.PLURAL);
						}
					}
					
					nounNumCombo.removeItemListener(this);	
				} else {
					System.err.println("Cannot find slot " + agreeID);

					if (language.equals(Languages.GREEK)) {
						nounCaseCombo.setEnabled(true);
						nounCaseCombo.setSelectedItem(((SPNounSlot)slot).getCase());
						nounCaseCombo.addItemListener(this);
					}
					nounNumCombo.setEnabled(true);
					nounNumCombo.setSelectedItem(((SPNounSlot)slot).getNumber());
					
					if (((SPNounSlot)slot).getLexiconEntryIRI() != null) {
						LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((SPNounSlot)slot).getLexiconEntryIRI(), language);
		
						if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
							nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
							nounNumCombo.setEnabled(false);
							
							if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
								((SPNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
							}
						} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
							nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
							nounNumCombo.setEnabled(false);
							
							if (!((SPNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
								((SPNounSlot)slot).setNumber(XmlMsgs.PLURAL);
							}
						}
					}
				}
			}
			
			nounAgreeWithCombo.addFocusListener(this);
			nounAgreeWithCombo.addItemListener(this);
		}
		else if (slot instanceof SPVerbSlot) {
			lastOpenSlotTab = verbSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
	        			mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(verbSlotStr);
			
			if (((SPVerbSlot)slot).getLexiconEntryIRI() != null)
				verbLECombo.setSelectedItem(new ListIRI(((SPVerbSlot)slot).getLexiconEntryIRI()));
			else
				((SPVerbSlot)slot).setLexiconEntryIRI(((ListIRI)verbLECombo.getSelectedItem()).getEntryIRI());
			
			verbLECombo.addItemListener(this);
			verbLECombo.addFocusListener(this);
			verbTenseCombo.setSelectedItem(((SPVerbSlot)slot).getTense());
			verbTenseCombo.addItemListener(this);
			polarityCombo.setSelectedItem(((SPVerbSlot)slot).getPolarity());
			polarityCombo.addItemListener(this);
			verbVoiceCombo.setSelectedItem(((SPVerbSlot)slot).getVoice());
			verbVoiceCombo.addItemListener(this);
			
			if (((SPVerbSlot)slot).getAgreesWithID() == null) {
				verbNumCombo.setEnabled(true);
				verbNumCombo.setSelectedItem(((SPVerbSlot)slot).getNumber());
				verbNumCombo.addItemListener(this);
				verbPerCombo.setEnabled(true);
				verbPerCombo.setSelectedItem(((SPVerbSlot)slot).getPerson());
				verbPerCombo.addItemListener(this);
			}
			else {
				String agreeID = ((SPVerbSlot)slot).getAgreesWithID().toString();

				int agreeSlot = -1;
				for (int i = 0; i < father.getSlots().size(); i++) {
					if (father.getSlots().get(i).getId().toString().equals(agreeID)) {
						agreeSlot = i + 1;
					}
				}

				if (agreeSlot != -1) {
					if (verbAgreeWithSlotModel.getIndexOf(agreeSlot) == -1) {
						System.err.println("Slot " + agreeID + " is neither an owner, a filler nor a noun.");
						verbAgreeWithSlotModel.addElement(agreeSlot);
					}
					verbAgreeWithCombo.setSelectedItem(agreeSlot);
					
					verbNumCombo.setEnabled(false);
					verbNumCombo.setSelectedItem(null);
					verbNumCombo.removeItemListener(this);
					verbPerCombo.setEnabled(false);
					verbPerCombo.setSelectedItem(null);
					verbPerCombo.removeItemListener(this);
				} else {
					System.err.println("Cannot find slot " + agreeID);

					verbNumCombo.setEnabled(true);
					verbNumCombo.setSelectedItem(((SPVerbSlot)slot).getNumber());
					verbNumCombo.addItemListener(this);
					verbPerCombo.setEnabled(true);
					verbPerCombo.setSelectedItem(((SPVerbSlot)slot).getPerson());
					verbPerCombo.addItemListener(this);
				}
			}
			
	        verbAgreeWithCombo.addFocusListener(this);
	        verbAgreeWithCombo.addItemListener(this);
		}
		else if (slot instanceof SPPrepositionSlot) {
			lastOpenSlotTab = prepositionSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
	        			mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(prepositionSlotStr);

			prepLECombo.setSelectedItem(((SPPrepositionSlot)slot).getPrep());
			prepLECombo.addItemListener(this);
		}
		else if (slot instanceof SPStringSlot) {
			lastOpenSlotTab = stringSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
	        			mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(stringSlotStr);
			
			stringTextArea.setText(((SPStringSlot)slot).getText());
			stringTextArea.getDocument().addDocumentListener(this);
		}
		else if (slot instanceof SPConcatenationSlot) {
			lastOpenSlotTab = concatenationSlotStr;
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((SentencePlanBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
	        			mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(concatenationSlotStr);
			
			ArrayList<SPConcatenationPropertySlot> propertySlots = ((SPConcatenationSlot)slot).getPropertySlots();
			Collections.sort(propertySlots);
			
			for (SPConcatenationPropertySlot propSlot: propertySlots) {
				concatListModel.addElement(new ListIRI(propSlot.getPropertyIRI()));
			}
	        concatMinus.setEnabled(false);
	        concatUp.setEnabled(false);
	        concatDown.setEnabled(false);

			concatList.addListSelectionListener(this);
	        concatCombo.addFocusListener(this);

	        concatAdd.addActionListener(this);
	        concatMinus.addActionListener(this);
	        concatUp.addActionListener(this);
	        concatDown.addActionListener(this);
		}
		cl.show(dynamicPanel, lastOpenSlotTab);
		dynamicPanel.validate();
		this.select.addItemListener(this);
	}
	
	public void removeListenersOfLastOpenSlotTab() {
		if (lastOpenSlotTab == ownerSlotStr) {
			exprCombo.removeItemListener(this);
			ownCaseCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == fillerSlotStr) {
			indCaseCombo.removeItemListener(this);
			bulletsCheck.removeItemListener(this);
		}
		else if (lastOpenSlotTab == adjectiveSlotStr) {
			adjLECombo.removeItemListener(this);
			adjLECombo.removeFocusListener(this);
			if (language.equals(Languages.GREEK)) {
				adjCaseCombo.removeItemListener(this);
				adjGenderCombo.removeItemListener(this);
			}
			adjNumCombo.removeItemListener(this);
			adjAgreeWithCombo.removeFocusListener(this);
			adjAgreeWithCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == nounSlotStr) {
			nounLECombo.removeItemListener(this);
			nounLECombo.removeFocusListener(this);
			if (language.equals(Languages.GREEK)) {
				nounCaseCombo.removeItemListener(this);
			}
			nounNumCombo.removeItemListener(this);
			nounAgreeWithCombo.removeFocusListener(this);
			nounAgreeWithCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == verbSlotStr) {
			verbLECombo.removeItemListener(this);
			verbLECombo.removeFocusListener(this);
			verbTenseCombo.removeItemListener(this);
			polarityCombo.removeItemListener(this);
			verbVoiceCombo.removeItemListener(this);
			verbNumCombo.removeItemListener(this);
			verbPerCombo.removeItemListener(this);
			verbAgreeWithCombo.removeFocusListener(this);
	        verbAgreeWithCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == prepositionSlotStr) {
			prepLECombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == stringSlotStr) {
			stringTextArea.getDocument().removeDocumentListener(this);
		}
		else if (lastOpenSlotTab == concatenationSlotStr) {
			concatCombo.removeFocusListener(this);
			concatAdd.removeActionListener(this);
	        concatMinus.removeActionListener(this);
	        concatUp.removeActionListener(this);
	        concatDown.removeActionListener(this);
		}
	}

	public void focusGained(FocusEvent e) {
		if (e.getSource() == concatCombo) {		
			Object selected = concatCombo.getSelectedItem();
			
			HashSet<ListIRI> propertyEntries = new HashSet();
	        for (OWLOntology owl : father.getOWLModelManager().getOntologies()) {
	        	for (OWLObjectProperty prop : owl.getObjectPropertiesInSignature()) {
	        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
	        			propertyEntries.add(new ListIRI(prop.getIRI()));
	        		}
	        	}
	        	for (OWLDataProperty prop : owl.getDataPropertiesInSignature()) {
	        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
	        			propertyEntries.add(new ListIRI(prop.getIRI()));
	        		}
	        	}
	        }

	        if (!areIdentical((DefaultComboBoxModel)concatCombo.getModel(), propertyEntries)) {
		        ArrayList<ListIRI> sortList = new ArrayList(propertyEntries);
		        Collections.sort(sortList);
	        
	        	concatComboModel = new DefaultComboBoxModel(sortList.toArray());
		        
		        concatCombo.setModel(concatComboModel);
		        
		        if (concatComboModel.getIndexOf(selected) != -1) {
		        	concatCombo.setSelectedItem(selected);
		        }
	        }
		} else if (e.getSource() == adjLECombo) {		
			Object selected = adjLECombo.getSelectedItem();
			
			HashSet<ListIRI> adjLexiconEntries = new HashSet();
			for (IRI iri : SentencePlanTab.LQM.getAdjectiveEntries()) {
				adjLexiconEntries.add(new ListIRI(iri));
			}

	        if (!areIdentical((DefaultComboBoxModel)adjLECombo.getModel(), adjLexiconEntries)) {	        
		        ArrayList<ListIRI> sortedList = new ArrayList(adjLexiconEntries);
		        Collections.sort(sortedList);
		        DefaultComboBoxModel adjComboModel = new DefaultComboBoxModel(sortedList.toArray());
	        	adjLECombo.setModel(adjComboModel);
		        
		        if (adjComboModel.getIndexOf(selected) != -1) {
		        	adjLECombo.setSelectedItem(selected);
		        }
	        }
		} else if (e.getSource() == nounLECombo) {		
			Object selected = nounLECombo.getSelectedItem();
			
			HashSet<ListIRI> nounLexiconEntries = new HashSet();
			for (IRI iri : SentencePlanTab.LQM.getNounEntries()) {
				nounLexiconEntries.add(new ListIRI(iri));
			}

	        if (!areIdentical((DefaultComboBoxModel)nounLECombo.getModel(), nounLexiconEntries)) {	        
		        ArrayList<ListIRI> sortedList = new ArrayList(nounLexiconEntries);
		        Collections.sort(sortedList);
		        DefaultComboBoxModel nounComboModel = new DefaultComboBoxModel(sortedList.toArray());
		        nounLECombo.setModel(nounComboModel);
		        
		        if (nounComboModel.getIndexOf(selected) != -1) {
		        	nounLECombo.setSelectedItem(selected);
		        }
	        }
		} else if (e.getSource() == verbLECombo) {		
			Object selected = verbLECombo.getSelectedItem();
			
			HashSet<ListIRI> verbLexiconEntries = new HashSet();
			for (IRI iri : SentencePlanTab.LQM.getVerbEntries()) {
				verbLexiconEntries.add(new ListIRI(iri));
			}

	        if (!areIdentical((DefaultComboBoxModel)verbLECombo.getModel(), verbLexiconEntries)) {	        
		        ArrayList<ListIRI> sortedList = new ArrayList(verbLexiconEntries);
		        Collections.sort(sortedList);
		        DefaultComboBoxModel verbComboModel = new DefaultComboBoxModel(sortedList.toArray());
		        verbLECombo.setModel(verbComboModel);
		        
		        if (verbComboModel.getIndexOf(selected) != -1) {
		        	verbLECombo.setSelectedItem(selected);
		        }
	        }
		} else if (e.getSource() == adjAgreeWithCombo) {
			Object selected = adjAgreeWithCombo.getSelectedItem();
			
			DefaultComboBoxModel tempModel = adjAgreeWithSlotModel;	
			tempModel.removeElement(this.getSlot().getOrder());
			
			adjAgreeWithCombo.setModel(tempModel);
			
	        if (tempModel.getIndexOf(selected) != -1) {
	        	adjAgreeWithCombo.setSelectedItem(selected);
	        }
		} else if (e.getSource() == nounAgreeWithCombo) {
			Object selected = nounAgreeWithCombo.getSelectedItem();
			
			DefaultComboBoxModel tempModel = nounAgreeWithSlotModel;	
			tempModel.removeElement(this.getSlot().getOrder());
			
			nounAgreeWithCombo.setModel(tempModel);		
			
	        if (tempModel.getIndexOf(selected) != -1) {
	        	nounAgreeWithCombo.setSelectedItem(selected);
	        }
		} else if (e.getSource() == verbAgreeWithCombo) {
			Object selected = verbAgreeWithCombo.getSelectedItem();
			
			verbAgreeWithCombo.setModel(verbAgreeWithSlotModel);
			
	        if (verbAgreeWithSlotModel.getIndexOf(selected) != -1) {
	        	verbAgreeWithCombo.setSelectedItem(selected);
	        }
		}
	}

	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {	
		//Concatenation Slot
		if (e.getSource() == concatAdd) {
			SPConcatenationPropertySlot prSlot = new SPConcatenationPropertySlot(((ListIRI)concatCombo.getSelectedItem()).getEntryIRI(), XmlMsgs.NOMINATIVE_TAG, SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.concatAnonymousIndivPattern + slot.getOrder() + "_" + (((SPConcatenationSlot)slot).getPropertySlots().size() + 1)).getID(), ((SPConcatenationSlot)slot).getPropertySlots().size() + 1);
			((SPConcatenationSlot)slot).concatenateProperty(prSlot);
			
			concatListModel.addElement((ListIRI)concatCombo.getSelectedItem());
			
			father.dirtenOntologies();
		}
		else if (e.getSource() == concatMinus) {
			((SPConcatenationSlot)slot).removeProperty(concatList.getSelectedIndex());
			
			for (int i = concatList.getSelectedIndex(); i < ((SPConcatenationSlot)slot).getPropertySlots().size(); i++) {
				SPConcatenationPropertySlot concat = ((SPConcatenationSlot)slot).getPropertySlots().get(i);
				concat.setOrder(concat.getOrder() - 1);
				concat.setId(SentencePlanTab.SPQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedPlan().getSentencePlanIRI().getFragment() + "_" + SPSlot.concatAnonymousIndivPattern + slot.getOrder() + "_" + concat.getOrder()).getID());
	        }
			
			concatListModel.removeElementAt(concatList.getSelectedIndex());
			
			father.dirtenOntologies();
		}
		else if (e.getSource() == concatUp) {
			int index = concatList.getSelectedIndex();
			if (index != 0) {
				ListIRI moveUp = (ListIRI)concatListModel.get(index);
				ListIRI moveDown = (ListIRI)concatListModel.get(index - 1); 
				concatListModel.setElementAt(moveUp, index - 1);
				concatListModel.setElementAt(moveDown, index);

				((SPConcatenationSlot)slot).swapPropertyOrder(((SPConcatenationSlot)slot).getPropertySlots().get(index - 1), ((SPConcatenationSlot)slot).getPropertySlots().get(index));
			}
			concatList.setSelectedIndex(index - 1);
			father.dirtenOntologies();
		}
		else if (e.getSource() == concatDown) {
			int index = concatList.getSelectedIndex();
			if (index != concatListModel.getSize() - 1) {
				ListIRI moveDown = (ListIRI)concatListModel.get(index);
				ListIRI moveUp = (ListIRI)concatListModel.get(index + 1); 
				concatListModel.setElementAt(moveUp, index);
				concatListModel.setElementAt(moveDown, index + 1);
				
				((SPConcatenationSlot)slot).swapPropertyOrder(((SPConcatenationSlot)slot).getPropertySlots().get(index), ((SPConcatenationSlot)slot).getPropertySlots().get(index + 1));
			}
			concatList.setSelectedIndex(index + 1);
			father.dirtenOntologies();
		}	
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == concatList) {
			int index = concatList.getSelectedIndex();
			if (index == -1) {
		        concatMinus.setEnabled(false);
		        concatUp.setEnabled(false);
		        concatDown.setEnabled(false);
			}
			else if (index == 0) {
		        concatMinus.setEnabled(true);
		        concatUp.setEnabled(false);
		        concatDown.setEnabled(true);
			}
			else if (index == concatListModel.getSize() - 1) {
		        concatMinus.setEnabled(true);
		        concatUp.setEnabled(true);
		        concatDown.setEnabled(false);
			}
			else {
		        concatMinus.setEnabled(true);
		        concatUp.setEnabled(true);
		        concatDown.setEnabled(true);
			}
		}
	}

	public void changedUpdate(DocumentEvent event) {
		updateEntry(event);
	}

	public void insertUpdate(DocumentEvent event) {
		updateEntry(event);
	}

	public void removeUpdate(DocumentEvent event) {
		updateEntry(event);
	}
	
	public void updateOrderLabel() {
		orderLabel.setText("Slot order: " + slot.getOrder());
	}
	
	public void updateEntry(DocumentEvent event) {
    	String name = (String)event.getDocument().getProperty("name");
		
		if (name.equals("stringText")) {
			((SPStringSlot)slot).setText(stringTextArea.getText());
			father.dirtenOntologies();
		}
	}
	
	public SPSlot getSlot() {
		return slot;
	}

	public ArrayList<DefaultComboBoxModel> getAgreeWithSlotModels() {
		ArrayList<DefaultComboBoxModel> agreeModels = new ArrayList();
		
		agreeModels.add(verbAgreeWithSlotModel);
		agreeModels.add(adjAgreeWithSlotModel);
		agreeModels.add(nounAgreeWithSlotModel);
		
		return agreeModels;
	}
	
	private boolean agreeCycleExists(HashSet<SPSlot> slots, int agreeWithOrder) {
		for (int j = 0; j < father.getBoxes().size(); j++) {
			SPSlot agreeSlot = ((SentencePlanBox)father.getBoxes().get(j)).getSlot();
			if (agreeSlot.getOrder() == agreeWithOrder) {
				if ((agreeSlot instanceof SPOwnerSlot)||(agreeSlot instanceof SPFillerSlot)) {
					return false;
				} else if (agreeSlot instanceof SPAdjectiveSlot) {
					if (slots.contains(agreeSlot)) {
						return true;
					} else if (((SPAdjectiveSlot)agreeSlot).getAgreesWithID() == null) {
						return false;
					} else {
						slots.add(agreeSlot);
						agreeWithOrder = Integer.parseInt(((SPAdjectiveSlot) agreeSlot).getAgreesWithID().toString().substring(((SPAdjectiveSlot) agreeSlot).getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length()));
						return agreeCycleExists(slots, agreeWithOrder);
					}
				} else if (agreeSlot instanceof SPNounSlot) {
					if (slots.contains(agreeSlot)) {
						return true;
					} else if (((SPNounSlot)agreeSlot).getAgreesWithID() == null) {
						return false;
					} else {
						slots.add(agreeSlot);
						agreeWithOrder = Integer.parseInt(((SPNounSlot) agreeSlot).getAgreesWithID().toString().substring(((SPNounSlot) agreeSlot).getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length()));
						return agreeCycleExists(slots, agreeWithOrder);
					}
				}
			}
		}
		return false;
	}
	
	public boolean areIdentical(DefaultComboBoxModel a, HashSet b) {
        if (a.getSize() == b.size()) {
	        HashSet checkSet = new HashSet(b);
	        for (int i = 0; i < a.getSize(); i++) {
				checkSet.add(a.getElementAt(i));
	        }
	        if (checkSet.size() == b.size()) {
	        	return true;
	        }
	        return false;
        } else {
			return false;
        }
	}
}

class MyJPan extends JPanel {
	int width;
	int height;

	public MyJPan() {
		super();
	}

	public MyJPan(int wide, int high) {
		width = wide;
		height = high;
		setPreferredSize(new Dimension(width, height));
	}

	public void increaseWidth(int wide) {
		width += wide;
		setPreferredSize(new Dimension(width, height));
	}

	public void decreaseWidth(int wide) {
		width -= wide;
		setPreferredSize(new Dimension(width, height));

	}
	public void switchOrder(int i) {	
		JPanel p1 = (JPanel) this.getComponent(i);
		this.remove(this.getComponent(i));
		this.add(p1, i - 2);
		JPanel p2 = (JPanel) this.getComponent(i-1);
		this.remove(this.getComponent(i-1));
		this.add(p2, i);
		this.validate();
		this.repaint();
	}
}
