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
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.NLNAdjectiveSlot;
import gr.aueb.cs.nlg.NLFiles.NLNArticleSlot;
import gr.aueb.cs.nlg.NLFiles.NLNNounSlot;
import gr.aueb.cs.nlg.NLFiles.NLNPrepositionSlot;
import gr.aueb.cs.nlg.NLFiles.NLNSlot;
import gr.aueb.cs.nlg.NLFiles.NLNStringSlot;
import gr.aueb.cs.nlg.NLFiles.NLName;
import gr.aueb.cs.nlg.NLFiles.SPSlot;
import gr.aueb.cs.nlg.NLGEngine.EnglishArticles;
import gr.aueb.cs.nlg.NLGEngine.GreekArticles;
import gr.aueb.cs.nlg.NLGEngine.SurfaceRealization;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

public class NLNamesTab extends NaturalOWLTab {
	private static final long serialVersionUID = 8268241587271333587L;
	private ArrayList<NLNameBox> boxes = new ArrayList<NLNameBox>();
	private ArrayList<JPanel> pluses = new ArrayList<JPanel>();
	private MyJP mainPanel;
	private JScrollPane scroll;
	private JCheckBox aggr;
	private JCheckBox focus;
	private JButton anonymousButton;
	private JButton connectButton;
	private JButton appropButton;
	private static JPanel preview;
	private static JPanel error;
	
	private NLName loadedName;
	
	Logger log = Logger.getLogger(NLNamesTab.class);

	private OWLModelManagerListener modelListener;

	@Override
	protected void initialiseOWLView() throws Exception {

		NLNameSelectionModel.addListener(new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {

                OWLEntity selected = NLNameSelectionModel.getSelectedEntity();

                loadedName = NLNQM.getNLName(selected.getIRI());
                
                if (loadedName != null) {                    
                    checkOrderConsistency(loadedName);
                	showNLName(loadedName);
                	
                	if (DefaultResourcesManager.isDefaultResource(loadedName.getNLNameIRI())) {
                		connectButton.setVisible(false);
                		appropButton.setVisible(false);
            			aggr.setVisible(false);
            			focus.setVisible(false);
                	} else {                	
                		connectButton.setVisible(true);
                		appropButton.setVisible(true);
            			aggr.setVisible(true);
            			focus.setVisible(true);
                	}
                }
                validate();
                repaint();
            }
        });
		
		setLayout(new BorderLayout(10, 10));	
		
		JPanel previewPanel = new JPanel();
		previewPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		previewPanel.setPreferredSize(new Dimension(600, 120));
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.PAGE_AXIS));

		JPanel previewLabelPanel = new JPanel(new FlowLayout());
		JLabel previewLabel = new JLabel("NL Name Preview");
		previewLabelPanel.add(previewLabel);
		
		preview = new JPanel();
		
		JLabel prevLabel = new JLabel(" ");
		preview.add(prevLabel);
		
		error = new JPanel();
		
		JLabel errorLabel = new JLabel(" ");
		error.add(errorLabel);
		
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
		previewPanel.add(error);
		previewPanel.add(previewButtonPanel);

		mainPanel = new MyJP(0, 250);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));		

		scroll = new JScrollPane(mainPanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setViewportView(mainPanel);
		scroll.setSize(new Dimension(800, 400));		
		
		JPanel subPanel = new JPanel();
		subPanel.setPreferredSize(new Dimension(600, 40));
		subPanel.setLayout(new BorderLayout(50, 50));
		
		anonymousButton = new JButton("Set classes and/or individuals as anonymous...");
		anonymousButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {			
				ConnectionDialog dlg = new ConnectionDialog(thisTab, NLResourceManager.anonymous.getIRI());	
				dlg.getResponse();
			}
			
		});
		
		connectButton = new JButton("Connect current NL Name with classes and/or individuals...");
		connectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {			
				ConnectionDialog dlg = new ConnectionDialog(thisTab, loadedName.getNLNameIRI());	
				dlg.getResponse();
			}
			
		});
		
		appropButton = new JButton("Set NL Name appropriateness...");
		appropButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {			
				AppropriatenessDialog dlg = new AppropriatenessDialog(thisTab, loadedName.getNLNameIRI());
				dlg.getResponse();
			}
		});
		
		aggr = new JCheckBox("Allow the containing sentence to be aggregated", true);
		aggr.addItemListener(this);
		
		focus = new JCheckBox("Causes the focus of the sentence to shift", true);
		focus.addItemListener(this);

		connectButton.setVisible(false);
		appropButton.setVisible(false);
		aggr.setVisible(false);
		focus.setVisible(false);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(anonymousButton);
		buttonsPanel.add(connectButton);
		buttonsPanel.add(appropButton);

		subPanel.add(buttonsPanel, BorderLayout.LINE_START);

		JPanel checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.PAGE_AXIS));
		checkboxPanel.add(aggr);
		checkboxPanel.add(focus);

		subPanel.add(checkboxPanel, BorderLayout.LINE_END);

		add(previewPanel, "North");
		add(scroll, "Center");
		add(subPanel, "South");

		validate();
		repaint();
	}		
	
	public MyJP getMainPanel(){
		return mainPanel;
	}
	
	public static JPanel getPreview(){
		return preview;
	}
	
	public static JPanel getError(){
		return error;
	}
	
	public ArrayList<NLNameBox> getBoxes(){
		return boxes;
	}
	
	public ArrayList<NLNSlot> getSlots(){
		return loadedName.getSlotsList();
	}
	
	public void addToBoxes(NLNameBox b){
		boxes.add(b);
		repaintPreview();
	}
	
	public void addToBoxes(int pos, NLNameBox b){
		boxes.add(pos, b);
		repaintPreview();
	}	

	public void addToSlots(int pos, NLNSlot slot){
		loadedName.getSlotsList().add(pos, slot);
	}
	
	public void removeFromBoxes(NLNameBox b) {
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get(i).equals(b)) {
				boxes.remove(i);
			}
		}
		repaintPreview();
	}
	
	public void removeFromSlots(NLNSlot slot) {
		loadedName.getSlotsList().remove(slot);
	}
	
	public void setSlot(NLNSlot slot, int pos) {
		loadedName.getSlotsList().set(pos, slot);
	}
	
	public NLNamesTab getThis() {
		return this;
	}
	
	public int getPositionInBoxes(NLNameBox b) {
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
		getError().removeAll();
		
		JLabel prevLabel = new JLabel(previewNLName());
		getPreview().add(prevLabel);

		if (!isHeadSlotDefined()) {
			JLabel errorLabel = new JLabel("No head slot defined!");
			errorLabel.setForeground(new Color(255, 0, 0));
			getError().add(errorLabel);
		}

		getError().validate();
		getError().repaint();	
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

	private void showNLName(NLName name) {
		int i = 0;
		
		ArrayList<NLNSlot> slots = name.getSlotsList();
		Collections.sort(slots);

		clearAllBoxesFromUI();
		for (NLNSlot slot: slots) {
			new NLNameBox(this, slot, name.getLanguage(), i++);
		}
		
		if (loadedName.getAggAllowed())
			aggr.setSelected(true);
		else
			aggr.setSelected(false);
		
		if (loadedName.getFocusLost())
			focus.setSelected(true);
		else
			focus.setSelected(false);
	}

	private void checkOrderConsistency(NLName name) {
		ArrayList<NLNSlot> slots = name.getSlotsList();
		Collections.sort(slots);

		HashSet<Integer> orders = new HashSet<Integer>();
		for (NLNSlot slot: slots) {
			while (orders.contains(slot.getOrder())) {
				slot.setOrder(slot.getOrder() + 1);
			}
			
			orders.add(slot.getOrder());
		}
	}
	
	
	public void clearAllBoxesFromUI(){
		ArrayList<NLNameBox> toBeRemoved = new ArrayList<NLNameBox>(boxes);
		
		for (NLNameBox box : toBeRemoved) {
			box.delete(box);
		}
		mainPanel.removeAll();
	}
	
	public NLName getLoadedName() {
		return loadedName;
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
				loadedName.setAggAllowed(true);
			else
				loadedName.setAggAllowed(false);
			
			dirtenOntologies();
		} else if (evt.getSource() == focus) {
			if (focus.isSelected())
				loadedName.setFocusLost(true);
			else
				loadedName.setFocusLost(false);
			
			dirtenOntologies();
		}
	}
	
	public String previewNLName() {
        ArrayList<NLNSlot> slots = new ArrayList<NLNSlot>();
		for (int i = 0; i < boxes.size(); i++) {
			if(boxes.get(i).select.getSelectedIndex()!=-1){
				slots.add(boxes.get(i).getSlot());
			}
		}
        Collections.sort(slots);
        
        ArrayList<String> realizedSlots = new ArrayList<String>();
        ArrayList<String> slotTypes = new ArrayList<String>();
        
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNAdjectiveSlot) {
            	slotTypes.add("ADJECTIVE");
            	
                NLNAdjectiveSlot adjective = (NLNAdjectiveSlot) slots.get(i);
                IRI lexiconEntryIRI = adjective.getLexiconEntryIRI();

                if (lexiconEntryIRI != null) {
	                String number = "";
	                String caseType = "";
	                String gender = "";
	
	                if (Languages.isGreek(loadedName.getLanguage())) {
	                    if (adjective.isHead()) {
	                        number = adjective.getNumber();
	                        caseType = adjective.getCase();
	                        gender = adjective.getGender();
	                    } else if (adjective.getAgreesWithID() != null) {
	                        NLNSlot headSlot = adjective;
	
	                        boolean foundHead = false;
	                        NodeID agreeID = adjective.getAgreesWithID();
	
	                        boolean shift = false;
	                        while (!foundHead) {
	                            for (int j = 0; j < slots.size(); j++) {
	                                if (slots.get(j).getId().equals(agreeID)) {
	                                    headSlot = slots.get(j);
	
	                                    if (headSlot instanceof NLNNounSlot) {
	                                        if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
	                                            agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
	                                            shift = true;
	                                        } else {
	                                            foundHead = true;
	                                        }
	                                    } else if (headSlot instanceof NLNAdjectiveSlot) {
	                                        if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
	                                            agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
	                                            shift = true;
	                                        } else {
	                                            foundHead = true;
	                                        }
	                                    }
	                                }
	                            }
	                            if (shift == false) {
	                            	break;
	                            }
								shift = false;
	                        }
	
	                        if (headSlot != null) {
	                            if (headSlot instanceof NLNNounSlot) {
	                                number = ((NLNNounSlot) headSlot).getNumber();
	                                caseType = ((NLNNounSlot) headSlot).getCase();
	                                gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), loadedName.getLanguage()).getGender();
	                            } else if (headSlot instanceof NLNAdjectiveSlot) {
	                                number = ((NLNAdjectiveSlot) headSlot).getNumber();
	                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
	                                gender = ((NLNAdjectiveSlot) headSlot).getGender();
	                            }
	                        }
	                    } else {
	                        number = adjective.getNumber();
	                        caseType = adjective.getCase();
	                        gender = adjective.getGender();
	                    }
	                }

	                if (Languages.isEnglish(loadedName.getLanguage())) {
	                    LexEntryAdjectiveEN adjectiveEntry = (LexEntryAdjectiveEN) LQM.getAdjectiveEntry(lexiconEntryIRI, loadedName.getLanguage());
	                    if (adjective.isCapitalized()) {
	                    	realizedSlots.add(SurfaceRealization.capitalizeText(adjectiveEntry.get_form()));
	                    } else {
	                    	realizedSlots.add(adjectiveEntry.get_form());
	                    }
	                } else if (Languages.isGreek(loadedName.getLanguage())) {
	                    LexEntryAdjectiveGR adjectiveEntry = (LexEntryAdjectiveGR) LQM.getAdjectiveEntry(lexiconEntryIRI, loadedName.getLanguage());
	                    if (adjective.isCapitalized()) {
	                    	realizedSlots.add(SurfaceRealization.capitalizeText(adjectiveEntry.get(gender, number, caseType)));
	                    } else {
	                    	realizedSlots.add(adjectiveEntry.get(gender, number, caseType));
	                    }
	                }
                } else {
                	realizedSlots.add("NULL");
                }
            } else if (slots.get(i) instanceof NLNNounSlot) {
            	slotTypes.add("NOUN");
            	
                NLNNounSlot noun = (NLNNounSlot) slots.get(i);
                IRI lexiconEntryIRI = noun.getLexiconEntryIRI();

                if (lexiconEntryIRI != null) {
	                String number = "";
	                String caseType = "";
	
	                if (noun.isHead()) {
	                    number = noun.getNumber();
	                    if (Languages.isGreek(loadedName.getLanguage())) {
	                        caseType = noun.getCase();
	                    }
	                } else if (noun.getAgreesWithID() != null) {
	                    NLNSlot headSlot = noun;
	
	                    boolean foundHead = false;
	                    NodeID agreeID = noun.getAgreesWithID();
	
	                    boolean shift = false;
	                    while (!foundHead) {
	                        for (int j = 0; j < slots.size(); j++) {
	                            if (slots.get(j).getId().equals(agreeID)) {
	                                headSlot = slots.get(j);
	
	                                if (headSlot instanceof NLNNounSlot) {
	                                    if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
	                                        agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
	                                        shift = true;
	                                    } else {
	                                        foundHead = true;
	                                    }
	                                } else if (headSlot instanceof NLNAdjectiveSlot) {
	                                    if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
	                                        agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
	                                        shift = true;
	                                    } else {
	                                        foundHead = true;
	                                    }
	                                }
	                            }
	                        }
	                        if (shift == false) {
	                        	break;
	                        }
							shift = false;
	                    }
	
	                    if (headSlot != null) {
	                        if (headSlot instanceof NLNNounSlot) {
	                            number = ((NLNNounSlot) headSlot).getNumber();
	                            if (Languages.isGreek(loadedName.getLanguage())) {
	                                caseType = ((NLNNounSlot) headSlot).getCase();
	                            }
	                        } else if (headSlot instanceof NLNAdjectiveSlot) {
	                            number = ((NLNAdjectiveSlot) headSlot).getNumber();
	                            if (Languages.isGreek(loadedName.getLanguage())) {
	                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
	                            }
	                        }
	                    }
	                } else {
	                    number = noun.getNumber();
	                    if (Languages.isGreek(loadedName.getLanguage())) {
	                        caseType = noun.getCase();
	                    }
	                }
	
	                if (Languages.isEnglish(loadedName.getLanguage())) {
	                    LexEntryNounEN nounEntry = (LexEntryNounEN) LQM.getNounEntry(lexiconEntryIRI, loadedName.getLanguage());
	                    if (noun.isCapitalized()) {
	                    	realizedSlots.add(SurfaceRealization.capitalizeText(nounEntry.get("", number)));
	                    } else {
	                    	realizedSlots.add(nounEntry.get("", number));
	                    }
	                } else if (Languages.isGreek(loadedName.getLanguage())) {
	                    LexEntryNounGR nounEntry = (LexEntryNounGR) LQM.getNounEntry(lexiconEntryIRI, loadedName.getLanguage());
	                    if (noun.isCapitalized()) {
	                    	realizedSlots.add(SurfaceRealization.capitalizeText(nounEntry.get(caseType, number)));
	                    } else {
	                    	realizedSlots.add(nounEntry.get(caseType, number));
	                    }
	                }
                } else {
                	realizedSlots.add("NULL");
                }
            } else if (slots.get(i) instanceof NLNPrepositionSlot) {
            	slotTypes.add("PREP");
            	
                NLNPrepositionSlot preposition = (NLNPrepositionSlot) slots.get(i);
                realizedSlots.add(preposition.getPrep());
            } else if (slots.get(i) instanceof NLNStringSlot) {
            	slotTypes.add("STRING");
            	
                NLNStringSlot string = (NLNStringSlot) slots.get(i);
                realizedSlots.add(string.getText());
            }
        }

        //ARTICLE PASS
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNArticleSlot) {
            	slotTypes.add(i, "ARTICLE");
            	
                NLNArticleSlot article = (NLNArticleSlot) slots.get(i);
                String realizedArticle = "";
                String number = XmlMsgs.SINGULAR;
                String gender = XmlMsgs.GENDER_MASCULINE_OR_FEMININE;
                String caseType = XmlMsgs.NOMINATIVE_TAG;

                if (article.getAgreesWithID() != null) {
                    NLNSlot headSlot = article;

                    boolean foundHead = false;
                    NodeID agreeID = article.getAgreesWithID();

                    boolean shift = false;
                    while (!foundHead) {
                        for (int j = 0; j < slots.size(); j++) {
                            if (slots.get(j).getId().equals(agreeID)) {
                                headSlot = slots.get(j);

                                if (headSlot instanceof NLNNounSlot) {
                                    if (((NLNNounSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNNounSlot) headSlot).getAgreesWithID();
                                        shift = true;
                                        j = 0;
                                    } else {
                                        foundHead = true;
                                    }
                                } else if (headSlot instanceof NLNAdjectiveSlot) {
                                    if (((NLNAdjectiveSlot) headSlot).getAgreesWithID() != null) {
                                        agreeID = ((NLNAdjectiveSlot) headSlot).getAgreesWithID();
                                        shift = true;
                                        j = 0;
                                    } else {
                                        foundHead = true;
                                    }
                                }
                            }
                        }
                        if (shift == false) {
                        	break;
                        }
						shift = false;
                    }

                    if (headSlot != null) {
                        if (headSlot instanceof NLNNounSlot) {
                            number = ((NLNNounSlot) headSlot).getNumber();
                            if (Languages.isGreek(loadedName.getLanguage())) {
                                gender = LQM.getNounEntry(((NLNNounSlot) headSlot).getLexiconEntryIRI(), loadedName.getLanguage()).getGender();
                                caseType = ((NLNNounSlot) headSlot).getCase();
                            }
                        } else if (headSlot instanceof NLNAdjectiveSlot) {
                            number = ((NLNAdjectiveSlot) headSlot).getNumber();
                            if (Languages.isGreek(loadedName.getLanguage())) {
                                gender = ((NLNAdjectiveSlot) headSlot).getGender();
                                caseType = ((NLNAdjectiveSlot) headSlot).getCase();
                            }
                        }
                    }
                } else {
                    number = article.getNumber();
                    gender = article.getGender();
                    caseType = article.getCase();
                }

                if (Languages.isEnglish(loadedName.getLanguage())) {
                	if (article.isDefinite()) {
                        realizedArticle = EnglishArticles.getDefiniteArticle();
                    } else {
                        if (number.equals(XmlMsgs.SINGULAR)) {
                            if (i < realizedSlots.size()) {
                                realizedArticle = EnglishArticles.getIndefiniteArticle(realizedSlots.get(i));
                            } else {
                                realizedArticle = EnglishArticles.getIndefiniteArticle("");
                            }
                        }
                    }
                } else if (Languages.isGreek(loadedName.getLanguage())) {
                    if (article.isDefinite()) {
                        if (i < realizedSlots.size()) {
                            realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, realizedSlots.get(i));
                        } else {
                            realizedArticle = GreekArticles.getDefiniteArticle(gender, number, caseType, "");
                        }
                    } else {
                        realizedArticle = GreekArticles.getIndefiniteArticle(gender, number, caseType);
                    }
                }
                realizedSlots.add(i, realizedArticle);
            }
        }

        String realizedName = "<html>";
        for (int i = 0; i < realizedSlots.size(); i++) {
        	realizedName = realizedName + " [ " + realizedSlots.get(i) + " <sub>" + slotTypes.get(i) + "</sub>" + " ] ";
        }
        realizedName = realizedName + "</html>";

        return realizedName.trim();
    }
	
	public boolean isHeadSlotDefined() {
        ArrayList<NLNSlot> slots = new ArrayList<NLNSlot>();
		for (int i = 0; i < boxes.size(); i++) {
			if(boxes.get(i).select.getSelectedIndex()!=-1){
				slots.add(boxes.get(i).getSlot());
			}
		}
        
		boolean headSlotDefined = false;
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof NLNAdjectiveSlot) {
                if (((NLNAdjectiveSlot)slots.get(i)).isHead()) {
                	headSlotDefined = true;
                }
            } else if (slots.get(i) instanceof NLNNounSlot) {
                if (((NLNNounSlot)slots.get(i)).isHead()) {
                	headSlotDefined = true;
                }
            }
        }

        return headSlotDefined;
    }
}

class NLNameBox implements ItemListener, FocusListener, DocumentListener{
	final static String articleSlotStr = "Article";
	final static String adjectiveSlotStr = "Adjective";	
	final static String nounSlotStr = "Noun";	
	final static String prepositionSlotStr = "Preposition";
	final static String stringSlotStr = "String";

	String language;
	JPanel buttonsPanel;
	JPanel box;
	JPanel dynamicPanel;
	JPanel orderPanel;
	JButton del;
	JComboBox<String> select;
	JButton left;
	JButton right;
	int pos; //position in Boxes arraylist
	//the arraylist stores only the boxes
	//the panel contains a sequence of box |+| box |+| box |+| box...
	//so, for a given box b, if b.positionInBoxes = i, this means that b.positionInPanel = i*2

	//ARTICLE
	JCheckBox definiteCheck;
	JLabel artCaseLabel;
	JLabel artGenderLabel;
	JLabel artNumLabel;
	JComboBox<String> artCaseCombo;
	JComboBox<String> artGenderCombo;
	JComboBox<String> artNumCombo;
	JComboBox artAgreeWithCombo;
	DefaultComboBoxModel artAgreeWithSlotModel;

	//ADJ
	JCheckBox adjHeadCheck;
	JCheckBox adjCapitalCheck;
	JLabel adjCaseLabel;
	JLabel adjGenderLabel;
	JLabel adjNumLabel;
	JComboBox adjCaseCombo;
	JComboBox adjGenderCombo;
	JComboBox adjNumCombo;
	JComboBox adjLECombo;
	JComboBox adjAgreeWithCombo;
	DefaultComboBoxModel adjAgreeWithSlotModel;

	//NOUN
	JCheckBox nounHeadCheck;
	JCheckBox nounCapitalCheck;
	JLabel nounCaseLabel;
	JLabel nounNumLabel;
	JComboBox nounCaseCombo;
	JComboBox nounNumCombo;
	JComboBox nounLECombo;
	JComboBox nounAgreeWithCombo;
	DefaultComboBoxModel nounAgreeWithSlotModel;

	//STRING
	JTextArea stringTextArea;

	//PREPOSITION
	JComboBox prepLECombo;
	
	JLabel orderLabel;

	String lastOpenSlotTab = "";
	
	NLNamesTab father = null;
	NLNSlot slot = null;
	
	NLNameBox(NLNamesTab f, NLNSlot slot, String lang, int pos){
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
		String[] choices = {articleSlotStr, adjectiveSlotStr, nounSlotStr, prepositionSlotStr, stringSlotStr};        
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
		ClassLoader loader = NLNamesTab.class.getClassLoader();
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
				
				NLNArticleSlot slot = new NLNArticleSlot(false, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, null, NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + (pos + 2)).getID(), pos + 2);
				father.addToSlots(pos + 1, slot);				
				for (int j = pos + 1; j < father.getBoxes().size(); j++) {
					father.getBoxes().get(j).getSlot().increaseOrder();
					father.getBoxes().get(j).getSlot().setId(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + father.getBoxes().get(j).getSlot().getOrder()).getID());
				}
				
				//Makes sure that even if slot order changes, "agrees with" points at the correct slots
				for (int j = father.getBoxes().size() - 1; j >= pos + 1; j--) {
					NLNSlot incSlot = father.getBoxes().get(j).getSlot();

					if ((incSlot instanceof NLNArticleSlot)||(incSlot instanceof NLNAdjectiveSlot)||(incSlot instanceof NLNNounSlot)) {
				        for (int f = 0; f < father.getBoxes().size(); f++) {
				        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
				        	for (DefaultComboBoxModel mdl : mdls) {
								if (mdl.getIndexOf(incSlot.getOrder() - 1) != -1) {
									mdl.addElement(incSlot.getOrder());
	
									if (!mdl.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
										if ((Integer)mdl.getSelectedItem() == incSlot.getOrder() - 1) {
											mdl.setSelectedItem(incSlot.getOrder());
										}
									}
									mdl.removeElement(incSlot.getOrder() - 1);
								}
				        	}
						}
			        }
					((NLNameBox)father.getBoxes().get(j)).updateOrderLabel();
				}

				NLNameBox newBox = new NLNameBox(father, slot, language, pos + 1);
				updatePositions();
				for (int j = 0; j < father.getBoxes().size(); j++) {
					addMoveArrows((NLNameBox) father.getBoxes().get(j));
					((NLNameBox)father.getBoxes().get(j)).addDeleteButton();
				}
				father.getMainPanel().validate();
				father.updateScroll();
				
				father.dirtenOntologies();
			}
		});
		father.getMainPanel().increaseWidth(60);
		father.updateScroll();
		updatePositions();
		
		father.addToPluses(getThis().pos, p);
		
		int position = (father.getPositionInBoxes(getThis())*2)+1;
		father.getMainPanel().add(p, position);
		father.getMainPanel().validate();		
	}
	
	
	
	void switchBoxes(int i) {
		NLNameBox a = (NLNameBox) father.getBoxes().get(i - 1);
		NLNameBox b = (NLNameBox) father.getBoxes().get(i);
				
		father.removeFromBoxes(a);
		father.removeFromBoxes(b);
		father.addToBoxes(i - 1, b);
		father.addToBoxes(i, a);
		
		NLNSlot aSlot = a.getSlot();
		NLNSlot bSlot = b.getSlot();
		
		father.removeFromSlots(aSlot);
		father.removeFromSlots(bSlot);
		int aOrder = aSlot.getOrder();
		int bOrder = bSlot.getOrder();
		
		//Makes sure that even if slot order changes, "agrees with" points at the correct slots
		if ((aSlot instanceof NLNAdjectiveSlot)||(aSlot instanceof NLNNounSlot)) {
			for (int f = 0; f < father.getBoxes().size(); f++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
					if (mdl.getIndexOf(aSlot.getOrder()) != -1) {
						mdl.addElement(bOrder);
						if (!mdl.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
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
		if ((bSlot instanceof NLNAdjectiveSlot)||(bSlot instanceof NLNNounSlot)) {
			for (int f = 0; f < father.getBoxes().size(); f++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
					if (mdl.getIndexOf(bSlot.getOrder()) != -1) {
						mdl.addElement(aOrder);
						if (!mdl.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
							if ((Integer)mdl.getSelectedItem() == bSlot.getOrder()) {
								mdl.setSelectedItem(aOrder);
							}
						}
						mdl.removeElement(bSlot.getOrder());
					}
	        	}
			}
		}
				
		aSlot.setId(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + bOrder).getID());
		aSlot.setOrder(bOrder);
		
		bSlot.setId(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + aOrder).getID());
		bSlot.setOrder(aOrder);
		
		father.addToSlots(i - 1, bSlot);
		father.addToSlots(i, aSlot);

		a.updateOrderLabel();
		b.updateOrderLabel();
		
		father.repaintPreview();
		
		father.dirtenOntologies();
	}
	
	void addDeleteButton(){
		if(this.del==null){
			ClassLoader loader = NLNamesTab.class.getClassLoader();
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
						int position = (father.getPositionInBoxes(getThis())+1);
						father.removeFromPluses(position - 1);
						for (int j = position; j < father.getBoxes().size(); j++) {
							((NLNameBox)father.getBoxes().get(j)).getSlot().decreaseOrder();
							((NLNameBox)father.getBoxes().get(j)).getSlot().setId(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + ((NLNameBox)father.getBoxes().get(j)).getSlot().getOrder()).getID());
						}
						
						//Makes sure that even if slot gets deleted, "agrees with" doesn't point at it
						if ((getThis().slot instanceof NLNAdjectiveSlot)||(getThis().slot instanceof NLNNounSlot)) {
					        for (int f = 0; f < father.getBoxes().size(); f++) {
					        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
					        	for (DefaultComboBoxModel mdl : mdls) {
									if (mdl.getIndexOf(getThis().getSlot().getOrder()) != -1) {
										if (!mdl.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
											if ((Integer)mdl.getSelectedItem() == getThis().getSlot().getOrder()) {
												mdl.setSelectedItem(NLNSlot.NONE_AGREE);
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
							NLNSlot decSlot = ((NLNameBox)father.getBoxes().get(j)).getSlot();
							if ((decSlot instanceof NLNAdjectiveSlot)||(decSlot instanceof NLNNounSlot)) {
								for (int f = 0; f < father.getBoxes().size(); f++) {
						        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(f)).getAgreeWithSlotModels();
						        	for (DefaultComboBoxModel mdl : mdls) {
										if (mdl.getIndexOf(decSlot.getOrder() + 1) != -1) {
											mdl.addElement(decSlot.getOrder());
	
											if (!mdl.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
												if ((Integer)mdl.getSelectedItem() == decSlot.getOrder() + 1) {
													mdl.setSelectedItem(decSlot.getOrder());
												}
											}
											mdl.removeElement(decSlot.getOrder() + 1);
										}
						        	}
								}
							}
							((NLNameBox)father.getBoxes().get(j)).updateOrderLabel();
						}
						
						father.dirtenOntologies();
					}
				}
			});

			this.buttonsPanel.add(del, 0);			

			this.buttonsPanel.validate();
		}

		if(father.getBoxes().size()>1){
			del.setEnabled(true);
		} else {
			del.setEnabled(false);
		}
	}

	void addMoveArrows(NLNameBox b) {
		// if they exist, remove them
		if (b.left != null)
			b.buttonsPanel.remove(b.left);
		if (b.right != null)
			b.buttonsPanel.remove(b.right);

		final int pos = father.getPositionInBoxes(b);

		if (pos > 0) {
			ClassLoader loader = NLNamesTab.class.getClassLoader();
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

					NLNameBox leftBox = (NLNameBox) father.getBoxes().get(pos - 1);
					NLNameBox thisBox = (NLNameBox) father.getBoxes().get(pos);

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
			ClassLoader loader = NLNamesTab.class.getClassLoader();
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
					NLNameBox thisBox = (NLNameBox) father.getBoxes().get(pos);
					NLNameBox rightBox = (NLNameBox) father.getBoxes().get(pos+1);

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
	
	void delete(NLNameBox b) {
		// delete from UI		
		father.getMainPanel().decreaseWidth(285); //225+60
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
		updateAll();
	}
	
	void updatePositions(){
		for(int i = 0; i < father.getBoxes().size(); i++){
			((NLNameBox)father.getBoxes().get(i)).setPosition(i);
		}
	}	
	
	NLNameBox getThis(){
		return this;
	}
	void setPosition(int i){
		this.pos = i;
	}
	void updateAll(){
		for(int i = 0; i < father.getBoxes().size(); i++){
			addMoveArrows((NLNameBox) father.getBoxes().get(i));
			((NLNameBox)father.getBoxes().get(i)).addDeleteButton();
		}
	}
	
	public void itemStateChanged(ItemEvent evt) {	
		if (evt.getSource() == select) {
			String option = (String)evt.getItem();
			
			if (option.equals(articleSlotStr)) {
				slot = new NLNArticleSlot(false, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(adjectiveSlotStr)) {
				slot = new NLNAdjectiveSlot(null, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, false, false, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(nounSlotStr)) {
				slot = new NLNNounSlot(null, XmlMsgs.NOMINATIVE_TAG, XmlMsgs.SINGULAR, false, false, null, slot.getId(), slot.getOrder());
			}
			else if (option.equals(prepositionSlotStr)) {
				if (language.equals(Languages.ENGLISH)) {
					slot = new NLNPrepositionSlot(NLNPrepositionSlot.PREPOSITION_EN_AFTER, slot.getId(), slot.getOrder());
		        }
				else if(language.equals(Languages.GREEK)) {
					slot = new NLNPrepositionSlot(NLNPrepositionSlot.PREPOSITION_GR_APO, slot.getId(), slot.getOrder());
				}
			}
			else if (option.equals(stringSlotStr)) {
				slot = new NLNStringSlot("", slot.getId(), slot.getOrder());
			}
			
			father.setSlot(slot, this.pos);
			
			loadSlotInBox(slot);
		}
		//Article Slot
		else if (evt.getSource() == definiteCheck) {
			if (definiteCheck.isSelected()) 
				((NLNArticleSlot)slot).setDefinite(true);
			else
				((NLNArticleSlot)slot).setDefinite(false);
			father.dirtenOntologies();
		}
		else if (evt.getSource() == artCaseCombo) {
			((NLNArticleSlot)slot).setCase(artCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == artGenderCombo) {
			((NLNArticleSlot)slot).setGender(artGenderCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == artNumCombo) {
			((NLNArticleSlot)slot).setNumber(artNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == artAgreeWithCombo) {
			if (artAgreeWithCombo.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
				((NLNArticleSlot)slot).setAgreesWithID(null);
				father.dirtenOntologies();

				if(language.equals(Languages.GREEK)) {
					artCaseCombo.setEnabled(true);
					if (artCaseCombo.getSelectedIndex() == -1)
						artCaseCombo.setSelectedIndex(0);
					artCaseCombo.addItemListener(this);
					artGenderCombo.setEnabled(true);
					if (artGenderCombo.getSelectedIndex() == -1)
						artGenderCombo.setSelectedIndex(0);
					artGenderCombo.addItemListener(this);
				}
				artNumCombo.setEnabled(true);
				if (artNumCombo.getSelectedIndex() == -1)
					artNumCombo.setSelectedIndex(0);
				artNumCombo.addItemListener(this);
			}
			else {
				((NLNArticleSlot)slot).setAgreesWithID(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + artAgreeWithCombo.getSelectedItem().toString()).getID());
				father.dirtenOntologies();
				
				if(language.equals(Languages.GREEK)) {
					artCaseCombo.setEnabled(false);
					artCaseCombo.removeItemListener(this);
					artGenderCombo.setEnabled(false);
					artGenderCombo.removeItemListener(this);
				}
				artNumCombo.setEnabled(false);
				artNumCombo.removeItemListener(this);
			}
		}
		//Adjective Slot
		else if (evt.getSource() == adjLECombo) {
			((NLNAdjectiveSlot)slot).setLexiconEntryIRI(((ListIRI)adjLECombo.getSelectedItem()).getEntryIRI());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjHeadCheck) {
			if (adjHeadCheck.isSelected()) {
				setAdjectiveHead(true);
				((NLNAdjectiveSlot)slot).setAgreesWithID(null);
				
				for (int j = 0; j < father.getBoxes().size(); j++) {
					NLNameBox neighborBox = (NLNameBox)father.getBoxes().get(j);
					if (neighborBox.getSlot().getOrder() != slot.getOrder()) {
			        	if ((neighborBox.getSlot() instanceof NLNAdjectiveSlot)) {
			        		neighborBox.setAdjectiveHead(false);
			        	}
			        	else if (neighborBox.getSlot() instanceof NLNNounSlot) {
			        		neighborBox.setNounHead(false);		        		
			        	}
					}
				}
			}
			else {
				setAdjectiveHead(false);
			}
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjCapitalCheck) {
			((NLNAdjectiveSlot)slot).setCapitalized(adjCapitalCheck.isSelected());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjCaseCombo) {
			((NLNAdjectiveSlot)slot).setCase(adjCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjGenderCombo) {
			((NLNAdjectiveSlot)slot).setGender(adjGenderCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjNumCombo) {
			((NLNAdjectiveSlot)slot).setNumber(adjNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == adjAgreeWithCombo) {
			if (adjAgreeWithCombo.getSelectedItem() != null ) {
				if (adjAgreeWithCombo.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
					((NLNAdjectiveSlot)slot).setAgreesWithID(null);
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
					HashSet<NLNSlot> slots = new HashSet<NLNSlot>();
					slots.add(slot);
					
					if (agreeCycleExists(slots, Integer.parseInt(adjAgreeWithCombo.getSelectedItem().toString()))) {
						adjAgreeWithCombo.setSelectedItem(SPSlot.NONE_AGREE);
						JOptionPane.showMessageDialog(null,
							    "Cyclic agreement between slots.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} else {
						((NLNAdjectiveSlot)slot).setAgreesWithID(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + adjAgreeWithCombo.getSelectedItem().toString()).getID());
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
			((NLNNounSlot)slot).setLexiconEntryIRI(((ListIRI)nounLECombo.getSelectedItem()).getEntryIRI());
			
			LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((NLNNounSlot)slot).getLexiconEntryIRI(), language);

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
		else if (evt.getSource() == nounHeadCheck) {
			if (nounHeadCheck.isSelected()) {
				setNounHead(true);
				((NLNNounSlot)slot).setAgreesWithID(null);
				
				for (int j = 0; j < father.getBoxes().size(); j++) {
					NLNameBox neighborBox = (NLNameBox)father.getBoxes().get(j);
					if (neighborBox.getSlot().getOrder() != slot.getOrder()) {
			        	if ((neighborBox.getSlot() instanceof NLNAdjectiveSlot)) {
			        		neighborBox.setAdjectiveHead(false);
			        	}
			        	else if (neighborBox.getSlot() instanceof NLNNounSlot) {
			        		neighborBox.setNounHead(false);
			        	}
					}
				}
			}
			else {
				setNounHead(false);
			}
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounCapitalCheck) {
			((NLNNounSlot)slot).setCapitalized(nounCapitalCheck.isSelected());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounCaseCombo) {
			((NLNNounSlot)slot).setCase(nounCaseCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounNumCombo) {
			((NLNNounSlot)slot).setNumber(nounNumCombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
		else if (evt.getSource() == nounAgreeWithCombo) {
			if (nounAgreeWithCombo.getSelectedItem() != null ) {
				if (nounAgreeWithCombo.getSelectedItem().equals(NLNSlot.NONE_AGREE)) {
					((NLNNounSlot)slot).setAgreesWithID(null);
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
				}
				else {
					HashSet<NLNSlot> slots = new HashSet<NLNSlot>();
					slots.add(slot);
					
					if (agreeCycleExists(slots, Integer.parseInt(nounAgreeWithCombo.getSelectedItem().toString()))) {
						nounAgreeWithCombo.setSelectedItem(SPSlot.NONE_AGREE);
						JOptionPane.showMessageDialog(null,
							    "Cyclic agreement between slots.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
					} else {
						((NLNNounSlot)slot).setAgreesWithID(NLNamesTab.NLNQM.getNLResourcesManager().getDataFactory().getOWLAnonymousIndividual(father.getLoadedName().getNLNameIRI().getFragment() + "_" + NLNSlot.anonymousIndivPattern + nounAgreeWithCombo.getSelectedItem().toString()).getID());
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
		//Preposition Slot
		else if (evt.getSource() == prepLECombo) {
			((NLNPrepositionSlot)slot).setPrep(prepLECombo.getSelectedItem().toString());
			father.dirtenOntologies();
		}
	}
	
	//Dynamic Panels
	void dynamicPanels(){
		JPanel articleCard = new JPanel();
		JPanel articleCardSub1 = new JPanel();
		JPanel articleCardSub2 = new JPanel();
		JPanel articleCardSub3 = new JPanel();
		JPanel articleCardSub4 = new JPanel();
		JPanel articleCardSub5 = new JPanel();
		JPanel adjectiveCard = new JPanel();
		JPanel adjectiveCardSub1 = new JPanel();
		JPanel adjectiveCardSub2 = new JPanel();
		JPanel adjectiveCardSub3 = new JPanel();
		JPanel adjectiveCardSub4 = new JPanel();
		JPanel adjectiveCardSub5 = new JPanel();
		JPanel adjectiveCardSub6 = new JPanel();
		JPanel adjectiveCardSub7 = new JPanel();
		JPanel nounCard = new JPanel();
		JPanel nounCardSub1 = new JPanel();
		JPanel nounCardSub2 = new JPanel();
		JPanel nounCardSub3 = new JPanel();
		JPanel nounCardSub4 = new JPanel();
		JPanel nounCardSub5 = new JPanel();
		JPanel nounCardSub6 = new JPanel();
		JPanel stringCard = new JPanel();
		JPanel stringCardSub1 = new JPanel();
		JPanel prepositionCard = new JPanel();
		JPanel prepositionCardSub1 = new JPanel();
		
		articleCard.setLayout(new GridLayout(8,0));
		articleCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		articleCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		articleCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		articleCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		articleCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCard.setLayout(new GridLayout(8,0));
		adjectiveCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub6.setLayout(new FlowLayout(FlowLayout.LEFT));
		adjectiveCardSub7.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCard.setLayout(new GridLayout(8,0));
		nounCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub2.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub3.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub4.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub5.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardSub6.setLayout(new FlowLayout(FlowLayout.LEFT));
		stringCard.setLayout(new GridLayout(0,1));
		stringCardSub1.setLayout(new BorderLayout());
		prepositionCard.setLayout(new GridLayout(8,0));
		prepositionCardSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		
        //article
		definiteCheck = new JCheckBox("Definite", false);
		articleCardSub1.add(definiteCheck);
        articleCard.add(articleCardSub1);

		if(language.equals(Languages.GREEK)) {
	        artCaseLabel = new JLabel("Case");
            String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
            artCaseCombo = new JComboBox(cases);
            
            articleCardSub2.add(artCaseLabel);
            articleCardSub2.add(artCaseCombo);
            articleCard.add(articleCardSub2);

            artGenderLabel = new JLabel("Gender");
            String[] genders = {XmlMsgs.GENDER_MASCULINE, XmlMsgs.GENDER_FEMININE, XmlMsgs.GENDER_NEUTER, XmlMsgs.GENDER_MASCULINE_OR_FEMININE};
            artGenderCombo = new JComboBox(genders);

            articleCardSub3.add(artGenderLabel);
            articleCardSub3.add(artGenderCombo);
            articleCard.add(articleCardSub3);
		}
        
        artNumLabel = new JLabel("Number");
        String[] numbers = {XmlMsgs.SINGULAR, XmlMsgs.PLURAL};
        artNumCombo = new JComboBox(numbers);
        
        articleCardSub4.add(artNumLabel);
        articleCardSub4.add(artNumCombo);
        articleCard.add(articleCardSub4);
                
        artAgreeWithSlotModel = new DefaultComboBoxModel();
        artAgreeWithSlotModel.addElement(NLNSlot.NONE_AGREE);
        for (int j = 0; j < father.getBoxes().size(); j++) {
        	NLNSlot s = ((NLNameBox)father.getBoxes().get(j)).getSlot();
        	if ((s instanceof NLNAdjectiveSlot)||(s instanceof NLNNounSlot)) {
        		artAgreeWithSlotModel.addElement(s.getOrder());
        	}
		}
        JLabel artAgreeWithLabel = new JLabel("Agree with slot ");
        artAgreeWithCombo = new JComboBox(artAgreeWithSlotModel);
        
        articleCardSub5.add(artAgreeWithLabel);
        articleCardSub5.add(artAgreeWithCombo);
        articleCard.add(articleCardSub5);
        
        Dimension labelD = artNumLabel.getPreferredSize();
        Dimension comboD = artNumCombo.getPreferredSize();
		int boxLabelWidth = 85;
		int boxComboWidth = 105;

		if(language.equals(Languages.GREEK)) {
			artCaseLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        	artGenderLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
		}
        artNumLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        artAgreeWithLabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));

		if(language.equals(Languages.GREEK)) {
			artCaseCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        	artGenderCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
		}
        artNumCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        artAgreeWithCombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
        
		//adjective
		JLabel adjLELabel = new JLabel("Lexicon Entry"); 

		adjHeadCheck = new JCheckBox("Head Adjective", false);
		adjectiveCardSub1.add(adjHeadCheck);
        adjectiveCard.add(adjectiveCardSub1);

		adjCapitalCheck = new JCheckBox("Capitalized", false);
		adjectiveCardSub2.add(adjCapitalCheck);
        adjectiveCard.add(adjectiveCardSub2);
        
        HashSet<ListIRI> adjLexiconEntries = new HashSet();
		for (IRI iri : SentencePlanTab.LQM.getAdjectiveEntries()) {
			adjLexiconEntries.add(new ListIRI(iri));
		}
		
		ArrayList<ListIRI> sortedList = new ArrayList(adjLexiconEntries);
        Collections.sort(sortedList);
        adjLECombo = new JComboBox(sortedList.toArray());    
        adjLECombo.setRenderer(new ListRenderer());        
        
        adjectiveCardSub3.add(adjLELabel);
        adjectiveCardSub3.add(adjLECombo); 
        adjectiveCard.add(adjectiveCardSub3);

        if(language.equals(Languages.GREEK)) {
            adjCaseLabel = new JLabel("Case");
            String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
            adjCaseCombo = new JComboBox(cases);
            
            adjectiveCardSub4.add(adjCaseLabel);
            adjectiveCardSub4.add(adjCaseCombo);
            adjectiveCard.add(adjectiveCardSub4);

            adjGenderLabel = new JLabel("Gender");
            String[] genders = {XmlMsgs.GENDER_MASCULINE, XmlMsgs.GENDER_FEMININE, XmlMsgs.GENDER_NEUTER};
            adjGenderCombo = new JComboBox(genders);

            adjectiveCardSub5.add(adjGenderLabel);
            adjectiveCardSub5.add(adjGenderCombo);
            adjectiveCard.add(adjectiveCardSub5);
		}
        
        adjNumLabel = new JLabel("Number");
        adjNumCombo = new JComboBox(numbers);         
            
        adjectiveCardSub6.add(adjNumLabel);
        adjectiveCardSub6.add(adjNumCombo);
        adjectiveCard.add(adjectiveCardSub6);
        
		adjAgreeWithSlotModel = new DefaultComboBoxModel();
		adjAgreeWithSlotModel.addElement(NLNSlot.NONE_AGREE);
		for (int j = 0; j < father.getBoxes().size(); j++) {
			NLNSlot s = ((NLNameBox)father.getBoxes().get(j)).getSlot();
			if ((s instanceof NLNAdjectiveSlot)||(s instanceof NLNNounSlot)) {
				if (s.getOrder() != getSlot().getOrder()) {
					adjAgreeWithSlotModel.addElement(s.getOrder());
				}
			}
		}
		JLabel adjAgreeWithLabel = new JLabel("Agree with slot ");
		adjAgreeWithCombo = new JComboBox(adjAgreeWithSlotModel);
		
		adjectiveCardSub7.add(adjAgreeWithLabel);
		adjectiveCardSub7.add(adjAgreeWithCombo);
        adjectiveCard.add(adjectiveCardSub7);

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

        nounHeadCheck = new JCheckBox("Head Noun", false);
		nounCardSub1.add(nounHeadCheck);
		nounCard.add(nounCardSub1);

        nounCapitalCheck = new JCheckBox("Capitalized", false);
		nounCardSub2.add(nounCapitalCheck);
		nounCard.add(nounCardSub2);
		
		HashSet<ListIRI> nounLexiconEntries = new HashSet();
		for (IRI iri : SentencePlanTab.LQM.getNounEntries()) {
			nounLexiconEntries.add(new ListIRI(iri));
		}
		
		sortedList = new ArrayList(nounLexiconEntries);
        Collections.sort(sortedList);		
		nounLECombo = new JComboBox(sortedList.toArray());
		nounLECombo.setRenderer(new ListRenderer());

		nounCardSub3.add(nounLELabel);
		nounCardSub3.add(nounLECombo); 
		nounCard.add(nounCardSub3);
            
        if(language.equals(Languages.GREEK)) {
            nounCaseLabel = new JLabel("Case");
            String[] cases = {XmlMsgs.NOMINATIVE_TAG, XmlMsgs.GENITIVE_TAG, XmlMsgs.ACCUSATIVE_TAG};
            nounCaseCombo = new JComboBox(cases);
            
            nounCardSub4.add(nounCaseLabel);
            nounCardSub4.add(nounCaseCombo);
    		nounCard.add(nounCardSub4);
		}
        
        nounNumLabel = new JLabel("Number");
        nounNumCombo = new JComboBox(numbers);         
            
        nounCardSub5.add(nounNumLabel);
        nounCardSub5.add(nounNumCombo);
		nounCard.add(nounCardSub5);
        
		nounAgreeWithSlotModel = new DefaultComboBoxModel();
		nounAgreeWithSlotModel.addElement(NLNSlot.NONE_AGREE);
		for (int j = 0; j < father.getBoxes().size(); j++) {
			NLNSlot s = ((NLNameBox)father.getBoxes().get(j)).getSlot();
			if ((s instanceof NLNAdjectiveSlot)||(s instanceof NLNNounSlot)) {
				if (s.getOrder() != getSlot().getOrder()) {
					nounAgreeWithSlotModel.addElement(s.getOrder());
				}
			}
		}	
		
		JLabel nounAgreeWithLabel = new JLabel("Agree with slot ");
		nounAgreeWithCombo = new JComboBox(nounAgreeWithSlotModel);
		
		nounCardSub6.add(nounAgreeWithLabel);
		nounCardSub6.add(nounAgreeWithCombo);   
		nounCard.add(nounCardSub6);
		
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
		
		//string slot
        JLabel stringLabel = new JLabel("String");
        stringTextArea = new JTextArea(8, 8);
        stringTextArea.setLineWrap(true);
        stringTextArea.getDocument().putProperty("name", "stringText");
        JScrollPane scroll = new JScrollPane(stringTextArea);
        
        stringCardSub1.add(BorderLayout.NORTH, stringLabel);
        stringCardSub1.add(BorderLayout.CENTER, scroll);
        stringCard.add(stringCardSub1);
        
        //preposition
        JLabel prepLELabel = new JLabel("Preposition");
        String[] preps = new String[0];
        if(language.equals(Languages.ENGLISH)) {
        	preps = NLNPrepositionSlot.getEnglishPrepositionList().toArray(preps);
        }
		else if(language.equals(Languages.GREEK)) {
        	preps = NLNPrepositionSlot.getGreekPrepositionList().toArray(preps);
		}
        prepLECombo = new JComboBox(preps);            

        prepositionCardSub1.add(prepLELabel);
        prepositionCardSub1.add(prepLECombo);
        prepositionCard.add(prepositionCardSub1);     
        
        prepLELabel.setPreferredSize(new Dimension(boxLabelWidth, labelD.height));
        prepLECombo.setPreferredSize(new Dimension(boxComboWidth, comboD.height));
							
		dynamicPanel.add(articleCard, articleSlotStr);
		dynamicPanel.add(adjectiveCard, adjectiveSlotStr);
		dynamicPanel.add(nounCard, nounSlotStr);
		dynamicPanel.add(stringCard, stringSlotStr);
		dynamicPanel.add(prepositionCard, prepositionSlotStr);
	}
	
	void loadSlotInBox(NLNSlot slot) {
		this.select.removeItemListener(this);
		removeListenersOfLastOpenSlotTab();
		
		CardLayout cl = (CardLayout)(dynamicPanel.getLayout());
		
		if (slot instanceof NLNArticleSlot) {
			lastOpenSlotTab = articleSlotStr;
			
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
		        		mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(articleSlotStr);
			
			if (((NLNArticleSlot)slot).isDefinite()) {
				definiteCheck.setSelected(true);
			}
			else {
				definiteCheck.setSelected(false);
			}
			definiteCheck.addItemListener(this);
			
			if (((NLNArticleSlot)slot).getAgreesWithID() == null) {
				if(language.equals(Languages.GREEK)) {
					artCaseCombo.setEnabled(true);
					artCaseCombo.setSelectedItem(((NLNArticleSlot)slot).getCase());
					artCaseCombo.addItemListener(this);
					
					artGenderCombo.setEnabled(true);
					artGenderCombo.setSelectedItem(((NLNArticleSlot)slot).getGender());
					artGenderCombo.addItemListener(this);
				}
				
				artNumCombo.setEnabled(true);
				artNumCombo.setSelectedItem(((NLNArticleSlot)slot).getNumber());
				artNumCombo.addItemListener(this);
			}
			else {
				String agreeID = ((NLNArticleSlot)slot).getAgreesWithID().toString();
				
				int agreeSlot = -1;
				for (int i = 0; i < father.getSlots().size(); i++) {
					if (father.getSlots().get(i).getId().toString().equals(agreeID)) {
						agreeSlot = i + 1;
					}
				}

				if (agreeSlot != -1) {
					if (artAgreeWithSlotModel.getIndexOf(agreeSlot) == -1) {
						artAgreeWithSlotModel.addElement(agreeSlot);
					}
					artAgreeWithCombo.setSelectedItem(agreeSlot);
					
					if(language.equals(Languages.GREEK)) {
						artCaseCombo.setEnabled(false);
						artCaseCombo.setSelectedItem(null);
						artCaseCombo.removeItemListener(this);
						artGenderCombo.setEnabled(false);
						artGenderCombo.setSelectedItem(null);
						artGenderCombo.removeItemListener(this);
					}
					artNumCombo.setEnabled(false);
					artNumCombo.setSelectedItem(null);
					artNumCombo.removeItemListener(this);
				} else {
					System.err.println("Cannot find slot " + agreeID);
					
					if(language.equals(Languages.GREEK)) {
						artCaseCombo.setEnabled(true);
						artCaseCombo.setSelectedItem(((NLNArticleSlot)slot).getCase());
						artCaseCombo.addItemListener(this);
						
						artGenderCombo.setEnabled(true);
						artGenderCombo.setSelectedItem(((NLNArticleSlot)slot).getGender());
						artGenderCombo.addItemListener(this);
					}
					
					artNumCombo.setEnabled(true);
					artNumCombo.setSelectedItem(((NLNArticleSlot)slot).getNumber());
					artNumCombo.addItemListener(this);
				}
			}
			
	        artAgreeWithCombo.addFocusListener(this);
	        artAgreeWithCombo.addItemListener(this);
		}
		else if (slot instanceof NLNAdjectiveSlot) {
			lastOpenSlotTab = adjectiveSlotStr;
			
	        for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if ((mdl.getIndexOf(slot.getOrder()) == -1)&&((NLNameBox)father.getBoxes().get(j)).getSlot().getOrder() != slot.getOrder())
		        		mdl.addElement(slot.getOrder());
	        	}
			}
	        
			select.setSelectedItem(adjectiveSlotStr);
			
			if (((NLNAdjectiveSlot)slot).getLexiconEntryIRI() != null)
				adjLECombo.setSelectedItem(new ListIRI(((NLNAdjectiveSlot)slot).getLexiconEntryIRI()));
			else if (adjLECombo.getSelectedItem() != null)
				((NLNAdjectiveSlot)slot).setLexiconEntryIRI(((ListIRI)adjLECombo.getSelectedItem()).getEntryIRI());

			adjLECombo.addItemListener(this);
			adjLECombo.addFocusListener(this);
			
			adjCapitalCheck.setSelected(((NLNAdjectiveSlot)slot).isCapitalized());
			adjCapitalCheck.addItemListener(this);
			
			if (((NLNAdjectiveSlot)slot).isHead()) {
				adjHeadCheck.setSelected(true);
				
				if (language.equals(Languages.GREEK)) {
					adjCaseLabel.setText("Default Case");
					adjGenderLabel.setText("Default Gender");
				}
				adjNumLabel.setText("Default Number");
				
				adjAgreeWithCombo.setEnabled(false);
				adjAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
				adjAgreeWithCombo.removeFocusListener(this);
				adjAgreeWithCombo.removeItemListener(this);
				
				if (language.equals(Languages.GREEK)) {
					adjCaseCombo.setEnabled(true);
					adjCaseCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getCase());
					adjCaseCombo.addItemListener(this);
					adjGenderCombo.setEnabled(true);
					adjGenderCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getGender());
					adjGenderCombo.addItemListener(this);
				}
				adjNumCombo.setEnabled(true);
				adjNumCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getNumber());
				adjNumCombo.addItemListener(this);
			}
			else {
				adjHeadCheck.setSelected(false);
				
				if (language.equals(Languages.GREEK)) {
					adjCaseLabel.setText("Case");
					adjGenderLabel.setText("Gender");
				}
				adjNumLabel.setText("Number");
				
				if (((NLNAdjectiveSlot)slot).getAgreesWithID() == null) {
					if (language.equals(Languages.GREEK)) {
						adjCaseCombo.setEnabled(true);
						adjCaseCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getCase());
						adjCaseCombo.addItemListener(this);
						adjGenderCombo.setEnabled(true);
						adjGenderCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getGender());
						adjGenderCombo.addItemListener(this);
					}
					adjNumCombo.setEnabled(true);
					adjNumCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getNumber());
					adjNumCombo.addItemListener(this);
				}
				else {
					String agreeID = ((NLNAdjectiveSlot)slot).getAgreesWithID().toString();
					
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
							adjCaseCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getCase());
							adjCaseCombo.addItemListener(this);
							adjGenderCombo.setEnabled(true);
							adjGenderCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getGender());
							adjGenderCombo.addItemListener(this);
						}
						adjNumCombo.setEnabled(true);
						adjNumCombo.setSelectedItem(((NLNAdjectiveSlot)slot).getNumber());
						adjNumCombo.addItemListener(this);
					}
				}
				
				adjAgreeWithCombo.addFocusListener(this);
				adjAgreeWithCombo.addItemListener(this);
			}
			adjHeadCheck.addItemListener(this);
		}
		else if (slot instanceof NLNNounSlot) {
			lastOpenSlotTab = nounSlotStr;
			
			for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
		        	if ((mdl.getIndexOf(slot.getOrder()) == -1)&&((NLNameBox)father.getBoxes().get(j)).getSlot().getOrder() != slot.getOrder())
		        		mdl.addElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(nounSlotStr);

			if (((NLNNounSlot)slot).getLexiconEntryIRI() != null)
				nounLECombo.setSelectedItem(new ListIRI(((NLNNounSlot)slot).getLexiconEntryIRI()));
			else if (nounLECombo.getSelectedItem() != null)
				((NLNNounSlot)slot).setLexiconEntryIRI(((ListIRI)nounLECombo.getSelectedItem()).getEntryIRI());

			nounLECombo.addItemListener(this);
			nounLECombo.addFocusListener(this);
			
			nounCapitalCheck.setSelected(((NLNNounSlot)slot).isCapitalized());
			nounCapitalCheck.addItemListener(this);
			
			if (((NLNNounSlot)slot).isHead()) {
				nounHeadCheck.setSelected(true);
				
				if (language.equals(Languages.GREEK)) {
					nounCaseLabel.setText("Default Case");
				}
				nounNumLabel.setText("Default Number");

				nounAgreeWithCombo.removeFocusListener(this);
				nounAgreeWithCombo.removeItemListener(this);
				nounAgreeWithCombo.setEnabled(false);
				nounAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
				
				if (language.equals(Languages.GREEK)) {
					nounCaseCombo.setEnabled(true);
					nounCaseCombo.setSelectedItem(((NLNNounSlot)slot).getCase());
					nounCaseCombo.addItemListener(this);
				}
				nounNumCombo.setEnabled(true);
				nounNumCombo.setSelectedItem(((NLNNounSlot)slot).getNumber());

				LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((NLNNounSlot)slot).getLexiconEntryIRI(), language);
				
				if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
					nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
					nounNumCombo.setEnabled(false);
					
					if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
						((NLNNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
					}
				} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
					nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
					nounNumCombo.setEnabled(false);
					
					if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
						((NLNNounSlot)slot).setNumber(XmlMsgs.PLURAL);
					}
				}
				
				nounNumCombo.addItemListener(this);
			} else {
				nounHeadCheck.setSelected(false);
				
				if (language.equals(Languages.GREEK)) {
					nounCaseLabel.setText("Case");
				}
				nounNumLabel.setText("Number");
				
				if (((NLNNounSlot)slot).getAgreesWithID() == null) {
					if (language.equals(Languages.GREEK)) {
						nounCaseCombo.setEnabled(true);
						nounCaseCombo.setSelectedItem(((NLNNounSlot)slot).getCase());
						nounCaseCombo.addItemListener(this);
					}
					nounNumCombo.setEnabled(true);
					nounNumCombo.setSelectedItem(((NLNNounSlot)slot).getNumber());
					
					if (((NLNNounSlot)slot).getLexiconEntryIRI() != null) {
						LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((NLNNounSlot)slot).getLexiconEntryIRI(), language);		
	
						if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
							nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
							nounNumCombo.setEnabled(false);
							
							if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
								((NLNNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
							}
						} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
							nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
							nounNumCombo.setEnabled(false);
							
							if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
								((NLNNounSlot)slot).setNumber(XmlMsgs.PLURAL);
							}
						}
					}
					
					nounNumCombo.addItemListener(this);
				}
				else {
					String agreeID = ((NLNNounSlot)slot).getAgreesWithID().toString();
					
					int agreeSlot = -1;
					for (int i = 0; i < father.getSlots().size(); i++) {
						if (father.getSlots().get(i).getId().toString().equals(agreeID)) {
							agreeSlot = i + 1;
						}
					}

					if (agreeSlot != -1) {
						if (nounAgreeWithSlotModel.getIndexOf(agreeSlot) == -1) {
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
						nounNumCombo.removeItemListener(this);
					} else {
						System.err.println("Cannot find slot " + agreeID);

						if (language.equals(Languages.GREEK)) {
							nounCaseCombo.setEnabled(true);
							nounCaseCombo.setSelectedItem(((NLNNounSlot)slot).getCase());
							nounCaseCombo.addItemListener(this);
						}
						nounNumCombo.setEnabled(true);
						nounNumCombo.setSelectedItem(((NLNNounSlot)slot).getNumber());
						
						if (((NLNNounSlot)slot).getLexiconEntryIRI() != null) {
							LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((NLNNounSlot)slot).getLexiconEntryIRI(), language);		
		
							if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
								nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
								nounNumCombo.setEnabled(false);
								
								if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.SINGULAR)) {
									((NLNNounSlot)slot).setNumber(XmlMsgs.SINGULAR);
								}
							} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
								nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
								nounNumCombo.setEnabled(false);
								
								if (!((NLNNounSlot)slot).getNumber().equals(XmlMsgs.PLURAL)) {
									((NLNNounSlot)slot).setNumber(XmlMsgs.PLURAL);
								}
							}
						}
						
						nounNumCombo.addItemListener(this);
					}
				}
				
				nounAgreeWithCombo.addFocusListener(this);
				nounAgreeWithCombo.addItemListener(this);
			}
			nounHeadCheck.addItemListener(this);
		}
		else if (slot instanceof NLNPrepositionSlot) {
			lastOpenSlotTab = prepositionSlotStr;
			
			for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
		        		mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(prepositionSlotStr);

			prepLECombo.setSelectedItem(((NLNPrepositionSlot)slot).getPrep());
			prepLECombo.addItemListener(this);
		}
		else if (slot instanceof NLNStringSlot) {
			lastOpenSlotTab = stringSlotStr;
			
			for (int j = 0; j < father.getBoxes().size(); j++) {
	        	ArrayList<DefaultComboBoxModel> mdls = ((NLNameBox)father.getBoxes().get(j)).getAgreeWithSlotModels();
	        	for (DefaultComboBoxModel mdl : mdls) {
	        		if (mdl.getIndexOf(slot.getOrder()) != -1)
		        		mdl.removeElement(slot.getOrder());
	        	}
			}
			
			select.setSelectedItem(stringSlotStr);
			
			stringTextArea.setText(((NLNStringSlot)slot).getText());
			stringTextArea.getDocument().addDocumentListener(this);
		}
		cl.show(dynamicPanel, lastOpenSlotTab);
		dynamicPanel.validate();
		this.select.addItemListener(this);
	}
	
	public void removeListenersOfLastOpenSlotTab() {
		if (lastOpenSlotTab == articleSlotStr) {
			definiteCheck.removeItemListener(this);
			if (language.equals(Languages.GREEK)) {
				artCaseCombo.removeItemListener(this);
				artGenderCombo.removeItemListener(this);
			}
			artNumCombo.removeItemListener(this);
			artAgreeWithCombo.removeFocusListener(this);
			artAgreeWithCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == adjectiveSlotStr) {
			adjLECombo.removeItemListener(this);
			adjLECombo.removeFocusListener(this);
			adjHeadCheck.removeItemListener(this);
			adjCapitalCheck.removeItemListener(this);
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
			nounHeadCheck.removeItemListener(this);
			nounCapitalCheck.removeItemListener(this);
			if (language.equals(Languages.GREEK)) {
				nounCaseCombo.removeItemListener(this);
			}
			nounNumCombo.removeItemListener(this);
			nounAgreeWithCombo.removeFocusListener(this);
			nounAgreeWithCombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == prepositionSlotStr) {
			prepLECombo.removeItemListener(this);
		}
		else if (lastOpenSlotTab == stringSlotStr) {
			stringTextArea.getDocument().removeDocumentListener(this);
		}
	}
	
	public void setAdjectiveHead(boolean isHead) {
		if (isHead && !((NLNAdjectiveSlot)slot).isHead()) {
			if (language.equals(Languages.GREEK)) {
				adjCaseLabel.setText("Default Case");
				adjGenderLabel.setText("Default Gender");
			}
			adjNumLabel.setText("Default Number");
			
			if (language.equals(Languages.GREEK)) {
				if (!adjCaseCombo.isEnabled()) {
					adjCaseCombo.setEnabled(true);
					if (adjCaseCombo.getSelectedIndex() == -1)
						adjCaseCombo.setSelectedIndex(0);
					adjCaseCombo.addItemListener(this);
				}
				if (!adjGenderCombo.isEnabled()) {
					adjGenderCombo.setEnabled(true);
					if (adjGenderCombo.getSelectedIndex() == -1)
						adjGenderCombo.setSelectedIndex(0);
					adjGenderCombo.addItemListener(this);
				}
			}
			if (!adjNumCombo.isEnabled()) {
				adjNumCombo.setEnabled(true);
				if (adjNumCombo.getSelectedIndex() == -1)
					adjNumCombo.setSelectedIndex(0);
				adjNumCombo.addItemListener(this);
			}

			adjAgreeWithCombo.removeFocusListener(this);
			adjAgreeWithCombo.removeItemListener(this);
			adjAgreeWithCombo.setEnabled(false);
			adjAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
		}
		else if (!isHead && ((NLNAdjectiveSlot)slot).isHead()) {
			if (language.equals(Languages.GREEK)) {
				adjCaseLabel.setText("Case");
				adjGenderLabel.setText("Gender");
			}
			adjNumLabel.setText("Number");

			adjAgreeWithCombo.setEnabled(true);
			adjAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
			adjAgreeWithCombo.addFocusListener(this);
			adjAgreeWithCombo.addItemListener(this);
		}
		adjHeadCheck.setSelected(isHead);
		((NLNAdjectiveSlot)slot).setHead(isHead);
	}
	
	public void setNounHead(boolean isHead) {
		if (isHead && !((NLNNounSlot)slot).isHead()) {
			if (language.equals(Languages.GREEK)) {
				nounCaseLabel.setText("Default Case");
			}
			nounNumLabel.setText("Default Number");
			
			if (language.equals(Languages.GREEK)) {
				if (!nounCaseCombo.isEnabled()) {
					nounCaseCombo.setEnabled(true);
					if (nounCaseCombo.getSelectedIndex() == -1)
						nounCaseCombo.setSelectedIndex(0);
					nounCaseCombo.addItemListener(this);
				}
			}
			if (!nounNumCombo.isEnabled()) {
				LexEntryNoun entry = NLNamesTab.LQM.getNounEntry(((NLNNounSlot)slot).getLexiconEntryIRI(), language);
				if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_SINGLE)) {
					nounNumCombo.setSelectedItem(XmlMsgs.SINGULAR);
					nounNumCombo.setEnabled(false);
				} else if (entry.getNumber().equals(LexEntryNoun.NUMBER_ONLY_PLURAL)) {
					nounNumCombo.setSelectedItem(XmlMsgs.PLURAL);
					nounNumCombo.setEnabled(false);
				} else {
					nounNumCombo.setEnabled(true);
					if (nounNumCombo.getSelectedIndex() == -1)
						nounNumCombo.setSelectedIndex(0);
					nounNumCombo.addItemListener(this);
				}
			}
			nounAgreeWithCombo.removeItemListener(this);
			nounAgreeWithCombo.removeFocusListener(this);
			nounAgreeWithCombo.setEnabled(false);
			nounAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
		}
		else if (!isHead && ((NLNNounSlot)slot).isHead()) {	
			if (language.equals(Languages.GREEK)) {
				nounCaseLabel.setText("Case");
			}
			nounNumLabel.setText("Number");

			nounAgreeWithCombo.setEnabled(true);
			nounAgreeWithCombo.setSelectedItem(NLNSlot.NONE_AGREE);
			nounAgreeWithCombo.addFocusListener(this);
			nounAgreeWithCombo.addItemListener(this);
		}
		nounHeadCheck.setSelected(isHead);
		((NLNNounSlot)slot).setHead(isHead);
	}
	
	public void updateOrderLabel() {
		orderLabel.setText("Slot order: " + slot.getOrder());
	}
	
	public void updateEntry(DocumentEvent event) {
    	String name = (String)event.getDocument().getProperty("name");
		
		if (name.equals("stringText")) {
			((NLNStringSlot)slot).setText(stringTextArea.getText());
			father.dirtenOntologies();
		}
	}
	
	public NLNSlot getSlot() {
		return slot;
	}
	
	public ArrayList<DefaultComboBoxModel> getAgreeWithSlotModels() {
		ArrayList<DefaultComboBoxModel> agreeModels = new ArrayList();
		
		agreeModels.add(artAgreeWithSlotModel);
		agreeModels.add(adjAgreeWithSlotModel);
		agreeModels.add(nounAgreeWithSlotModel);
		
		return agreeModels;
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

	public void focusGained(FocusEvent e) {
		if (e.getSource() == adjLECombo) {		
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
		} else if (e.getSource() == artAgreeWithCombo) {
			Object selected = artAgreeWithCombo.getSelectedItem();
			
			artAgreeWithCombo.setModel(artAgreeWithSlotModel);
			
	        if (artAgreeWithSlotModel.getIndexOf(selected) != -1) {
	        	artAgreeWithCombo.setSelectedItem(selected);
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
		}
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}
	
	private boolean agreeCycleExists(HashSet<NLNSlot> slots, int agreeWithOrder) {
		for (int j = 0; j < father.getBoxes().size(); j++) {
			NLNSlot agreeSlot = ((NLNameBox)father.getBoxes().get(j)).getSlot();
			if (agreeSlot.getOrder() == agreeWithOrder) {
				if (agreeSlot instanceof NLNAdjectiveSlot) {
					if (slots.contains(agreeSlot)) {
						return true;
					} else if (((NLNAdjectiveSlot)agreeSlot).getAgreesWithID() == null) {
						return false;
					} else {
						slots.add(agreeSlot);
						agreeWithOrder = Integer.parseInt(((NLNAdjectiveSlot) agreeSlot).getAgreesWithID().toString().substring(((NLNAdjectiveSlot) agreeSlot).getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length()));
						return agreeCycleExists(slots, agreeWithOrder);
					}
				} else if (agreeSlot instanceof NLNNounSlot) {
					if (slots.contains(agreeSlot)) {
						return true;
					} else if (((NLNNounSlot)agreeSlot).getAgreesWithID() == null) {
						return false;
					} else {
						slots.add(agreeSlot);
						agreeWithOrder = Integer.parseInt(((NLNNounSlot) agreeSlot).getAgreesWithID().toString().substring(((NLNNounSlot) agreeSlot).getAgreesWithID().toString().indexOf(SPSlot.anonymousIndivPattern) + SPSlot.anonymousIndivPattern.length()));
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

class MyJP extends JPanel {
	int width;
	int height;

	public MyJP() {
		super();
	}

	public MyJP(int wide, int high) {
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
