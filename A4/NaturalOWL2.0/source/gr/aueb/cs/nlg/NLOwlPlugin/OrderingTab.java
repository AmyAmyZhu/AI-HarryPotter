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
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.NLFiles.OrderingQueryManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class OrderingTab extends NaturalOWLTab {
	private OWLModelManagerListener listener;

	private JPanel orderingPanel = new JPanel();

	static MyJPanel sectionsPanel = new MyJPanel();
	static MyJPanel unsortedPanels = new MyJPanel();	
	public static ArrayList<SectionPanel> sections = new ArrayList();
	public static ArrayList<UnsortedPanel> unsorted = new ArrayList();
	static JScrollPane sectionScroll;
	static JScrollPane unsortedScroll;
	
	@Override
	protected void initialiseOWLView() throws Exception {
		initialiseNaturalOWL();
		
		refresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
		        refresh();
			}
		});
		
		setLayout(new BorderLayout(10, 10));

		JPanel sectionSuperPanel = new JPanel();
		JPanel unsortedSuperPanel = new JPanel();
		orderingPanel = new JPanel();
		
		sectionsPanel = new MyJPanel(450, 0);

		sectionScroll = new JScrollPane();
		sectionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sectionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sectionScroll.setViewportView(sectionsPanel);
		
		unsortedPanels = new MyJPanel(580, 0);

		unsortedScroll = new JScrollPane();
		unsortedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		unsortedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		unsortedScroll.setViewportView(unsortedPanels);

		loadSectionsAndOrders();
		loadUnsortedProperties();

		JLabel sectionTitle = new JLabel("Sections and Order");
		sectionTitle.setFont(new Font("Serif", Font.BOLD, 24));
		
		JButton newSectionButton = new JButton("Add new section");
		newSectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SectionPanel sectionPnl = new SectionPanel(getThis());
				OrderingTab.addToSections(sectionPnl);

				IRI sectionIRI = IRI.create(NLResourceManager.resourcesNS + "NewSection");

				int n = 1;
				while (!isUniqueIRI(sectionIRI)) {
					n++;
					sectionIRI = IRI.create(NLResourceManager.resourcesNS + "NewSection" + n);
				}
				
				sectionPnl.createSectionPanel(new ListIRI(sectionIRI), OrderingQueryManager.defaultOrder, "", "", null);
				
				NaturalOWLTab.OQM.addSection(sectionIRI);
				getThis().dirtenOntologies();

				OrderingTab.getSectionsPanel().validate();
				OrderingTab.updateSectionScroll();
				
				updateSectionComboBoxes();
			}
		});
		
		sectionSuperPanel.add(sectionTitle);
		sectionSuperPanel.add(newSectionButton);
		sectionSuperPanel.add(Box.createRigidArea(new Dimension(0,20)));
		sectionSuperPanel.add(sectionScroll);
		sectionSuperPanel.setLayout(new BoxLayout(sectionSuperPanel, BoxLayout.PAGE_AXIS));


		JLabel unsortedTitle = new JLabel("Unsorted properties");
		unsortedTitle.setFont(new Font("Serif", Font.BOLD, 24));
		
		//JLabel unsortedExplanation = new JLabel("The following properties have not been assigned a section or order value. They will appear at the end of the generated text, after the ordered sentences.");
		
		JButton refreshUnsortedButton = new JButton("Refresh unsorted properties");
		refreshUnsortedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshUnsorted();
			}
		});
		
		unsortedSuperPanel.add(unsortedTitle);
		//unsortedSuperPanel.add(unsortedExplanation);
		unsortedSuperPanel.add(refreshUnsortedButton);
		unsortedSuperPanel.add(Box.createRigidArea(new Dimension(0,20)));
		unsortedSuperPanel.add(unsortedScroll);
		unsortedSuperPanel.setLayout(new BoxLayout(unsortedSuperPanel, BoxLayout.PAGE_AXIS));
		
		orderingPanel.add(sectionSuperPanel);
		orderingPanel.add(unsortedSuperPanel);
		orderingPanel.setLayout(new BoxLayout(orderingPanel, BoxLayout.LINE_AXIS));
		add("West", orderingPanel);
		
		setFocusCycleRoot(true);
		setFocusTraversalPolicyProvider(true);
		setFocusTraversalPolicy(new MyOwnFocusTraversalPolicy());
		
		repaint();
	}
	
	private void refresh() {
		if (orderingPanel != null)
			remove(orderingPanel);
		
		setLayout(new BorderLayout(10, 10));

		JPanel sectionSuperPanel = new JPanel();
		JPanel unsortedSuperPanel = new JPanel();
		orderingPanel = new JPanel();
		
		sectionsPanel = new MyJPanel(450, 0);

		sectionScroll = new JScrollPane();
		sectionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sectionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sectionScroll.setViewportView(sectionsPanel);
		
		unsortedPanels = new MyJPanel(580, 0);

		unsortedScroll = new JScrollPane();
		unsortedScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		unsortedScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		unsortedScroll.setViewportView(unsortedPanels);

		loadSectionsAndOrders();
		loadUnsortedProperties();

		JLabel sectionTitle = new JLabel("Sections and Orders");
		sectionTitle.setFont(new Font("Serif", Font.BOLD, 24));
		
		JButton newSectionButton = new JButton("Add new section");
		newSectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SectionPanel sectionPnl = new SectionPanel(getThis());
				OrderingTab.addToSections(sectionPnl);

				IRI sectionIRI = IRI.create(NLResourceManager.resourcesNS + "NewSection");

				int n = 1;
				while (!isUniqueIRI(sectionIRI)) {
					n++;
					sectionIRI = IRI.create(NLResourceManager.resourcesNS + "NewSection" + n);
				}
				
				sectionPnl.createSectionPanel(new ListIRI(sectionIRI), OrderingQueryManager.defaultOrder, "", "", null);
				
				NaturalOWLTab.OQM.addSection(sectionIRI);
				getThis().dirtenOntologies();

				OrderingTab.getSectionsPanel().validate();
				OrderingTab.updateSectionScroll();
				
				updateSectionComboBoxes();
			}
		});
		
		sectionSuperPanel.add(sectionTitle);
		sectionSuperPanel.add(newSectionButton);
		sectionSuperPanel.add(Box.createRigidArea(new Dimension(0,20)));
		sectionSuperPanel.add(sectionScroll);
		sectionSuperPanel.setLayout(new BoxLayout(sectionSuperPanel, BoxLayout.PAGE_AXIS));


		JLabel unsortedTitle = new JLabel("Unsorted properties");
		unsortedTitle.setFont(new Font("Serif", Font.BOLD, 24));
		
		JButton refreshUnsortedButton = new JButton("Refresh unsorted properties");
		refreshUnsortedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshUnsorted();
			}
		});
		
		unsortedSuperPanel.add(unsortedTitle);
		//unsortedSuperPanel.add(unsortedExplanation);
		unsortedSuperPanel.add(refreshUnsortedButton);
		unsortedSuperPanel.add(Box.createRigidArea(new Dimension(0,20)));
		unsortedSuperPanel.add(unsortedScroll);
		unsortedSuperPanel.setLayout(new BoxLayout(unsortedSuperPanel, BoxLayout.PAGE_AXIS));
		
		orderingPanel.add(sectionSuperPanel);
		orderingPanel.add(unsortedSuperPanel);
		orderingPanel.setLayout(new BoxLayout(orderingPanel, BoxLayout.LINE_AXIS));
		add("West", orderingPanel);
		
		setFocusCycleRoot(true);
		setFocusTraversalPolicyProvider(true);
		setFocusTraversalPolicy(new MyOwnFocusTraversalPolicy());
		
		repaint();
	}
	
	public void updateSectionComboBoxes() {
		HashSet<ListIRI> availableSections = new HashSet();
	    for (IRI sectionIRI : NaturalOWLTab.OQM.getOrderedSections()) {
	    	availableSections.add(new ListIRI(sectionIRI));
	    }
	    
	    ArrayList<ListIRI> sortList = new ArrayList<ListIRI>(availableSections);
	    Collections.sort(sortList);
	    
		for (UnsortedPanel uPanel : OrderingTab.getUnsorted()) {
			uPanel.setSectionsComboModel(new DefaultComboBoxModel(sortList.toArray()));
	        
			Object selected = uPanel.getSectionCombo().getSelectedItem();
			uPanel.getSectionCombo().setModel(uPanel.getSectionsComboModel());
	        
	        if (uPanel.getSectionsComboModel().getIndexOf(selected) != -1) {
	        	uPanel.getSectionCombo().setSelectedItem(selected);
	        }
		}
	}

	public void loadSectionsAndOrders() {
		// den borei na ginei kateutheian ena Section a = new Section
		// ("Section name")
		// epeidi xreiazetai ws antikeimeno Section na exei idi topothetithei
		// mesa sto arraylist sections
		// dimiourgoume loipon ousiastika ena keno antikeimeno, to opoio
		// prosthetoume sto arraylist
		// kai sti synexeia to "kataskeuazoume", me ti methodo createSection,
		// opou pername kai to onoma kai ta properties tou
		ArrayList<IRI> sectionIRIs = NaturalOWLTab.OQM.getOrderedSections();
		for (int i = 0; i < sectionIRIs.size(); i++) {
			SectionPanel sectionPnl = new SectionPanel(this);
			addToSections(sectionPnl);
			
			ArrayList<IRI> propertyIRIs = NaturalOWLTab.OQM.getPropertiesInSection(sectionIRIs.get(i));
			ArrayList<ListIRI> propertyListIRIs = new ArrayList<ListIRI>();
			for (int j = 0; j < propertyIRIs.size(); j++) {
				propertyListIRIs.add(new ListIRI(propertyIRIs.get(j)));
			}
			sectionPnl.createSectionPanel(new ListIRI(sectionIRIs.get(i)), NaturalOWLTab.OQM.getSectionOrder(sectionIRIs.get(i)), NaturalOWLTab.OQM.getSectionLabel(sectionIRIs.get(i), Languages.ENGLISH), NaturalOWLTab.OQM.getSectionLabel(sectionIRIs.get(i), Languages.GREEK), propertyListIRIs);
		}
	}

	public void loadUnsortedProperties() {
		//+++ for all properties in the ontologies
		HashSet<ListIRI> propertyEntries = new HashSet();
        for (OWLOntology owl : getOWLModelManager().getActiveOntologies()) {
        	for (OWLObjectProperty prop : owl.getObjectPropertiesInSignature()) {
        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
        			if (NaturalOWLTab.OQM.getPropertySection(prop.getIRI()).equals(NLResourceManager.defaultSection.getIRI())) {
        				propertyEntries.add(new ListIRI(prop.getIRI()));
        			}
        		}
        	}
        	for (OWLDataProperty prop : owl.getDataPropertiesInSignature()) {
        		if (!prop.getIRI().getStart().equals(NLResourceManager.nlowlNS)) {
        			if (NaturalOWLTab.OQM.getPropertySection(prop.getIRI()).equals(NLResourceManager.defaultSection.getIRI())) {
        				propertyEntries.add(new ListIRI(prop.getIRI()));
        			}
        		}
        	}
        }

        for (OWLNamedIndividual domainIndependentProperty : NLResourceManager.domainIndependentProperties) {
			if (NaturalOWLTab.OQM.getPropertySection(domainIndependentProperty.getIRI()).equals(NLResourceManager.defaultSection.getIRI())) {
				propertyEntries.add(new ListIRI(domainIndependentProperty.getIRI()));
			}
        }

        ArrayList<ListIRI> sortList = new ArrayList(propertyEntries);
        Collections.sort(sortList);
        
		for (int i = 0; i < sortList.size(); i++) {
			UnsortedPanel panel = new UnsortedPanel(getThis(), unsortedPanels);
			
			panel.createUnsortedPanel(sortList.get(i));
			unsorted.add(panel);
		}
		unsortedPanels.validate();
		unsortedPanels.repaint();
	}
	
	public void refreshUnsorted() {
		unsortedPanels = new MyJPanel(580, 0);
		unsortedScroll.setViewportView(unsortedPanels);
		
		loadUnsortedProperties();
	}

	@Override
	protected void disposeOWLView() {
		super.disposeOWLView();
		getOWLModelManager().removeListener(listener);
	}

	public static MyJPanel getSectionsPanel() {
		return sectionsPanel;
	}

	public static MyJPanel getUnsortedPanels() {
		return unsortedPanels;
	}

	public static ArrayList<UnsortedPanel> getUnsorted() {
		return unsorted;
	}

	public static ArrayList<SectionPanel> getSections() {
		return sections;
	}

	public static void addToUnsorted(UnsortedPanel b) {
		unsorted.add(b);
	}

	public static void addToSections(SectionPanel b) {
		sections.add(b);
	}

	public static void addToSections(int i, SectionPanel b) {
		sections.add(i, b);
	}

	public static int getSectionsSize() {
		return sections.size();
	}

	public static void removeFromSections(SectionPanel b) {
		for (int i = 0; i < sections.size(); i++) {
			if (sections.get(i).equals(b)) {
				sections.remove(i);
			}
		}
	}


	static void removeFromUnsorted(UnsortedPanel u) {
		for (int i = 0; i < unsorted.size(); i++) {
			if (unsorted.get(i).equals(u)) {
				unsorted.remove(i);
			}
		}
	}

	public static int getPositionInSections(SectionPanel b) {
		boolean flag = false;
		int i = 0;
		while (!flag && i < sections.size()) {
			if (sections.get(i).equals(b)) {
				flag = true;
			} else
				i++;
		}
		return i;
	}
	
    public static boolean isValidOrder(String num){
	    try{
	    	int i = Integer.parseInt(num);
	    	
	    	if (i < 1) {
	    		return false;
	    	}
	    	if (i > 100) {
	    		return false;
	    	}
	    } catch(NumberFormatException nfe) {
	    	return false;
	    }
	    return true;
    }

	public static void updateSectionScroll() {
		sectionScroll.updateUI();
	}

	public static void updateUnsortedScroll() {
		unsortedScroll.updateUI();
	}

	OrderingTab getThis() {
		return this;
	}

	public void actionPerformed(ActionEvent e) {}

	public void itemStateChanged(ItemEvent arg0) {}
}

class SectionPanel {
	static boolean first = true;

	private OrderingTab father;
	
	private boolean firstInProp = true;
	private MyJPanel sectionPanel = new MyJPanel(430, 0);
	private MyJPanel buttonsPanel = new MyJPanel();
	private MyJPanel labelPanelEN = new MyJPanel();
	private MyJPanel labelPanelGR = new MyJPanel();
	private MyJPanel orderPanel;
	private ArrayList<OrderPanel> orderPanels = new ArrayList<OrderPanel>();
	private int sectionOrder;
	private String sectionLabelEN;
	private String sectionLabelGR;
	private JTextField sectionOrderTextField;
	private JTextField sectionLabelENTextField;
	private JTextField sectionLabelGRTextField;
	private JTextField sectionNameTextField;
	private JButton buttonNew;
	private JButton up;
	private JButton down;
	
	private ListIRI sectionIRI;

	SectionPanel(OrderingTab f) {
		father = f;
	}

	void createSectionTitle(ListIRI n) {
		this.sectionNameTextField = new JTextField(23);
		this.sectionNameTextField.setText(n.toString());
		this.sectionNameTextField.setToolTipText(n.getEntryIRI().toString());
		this.sectionNameTextField.setEditable(false);
		
		this.sectionNameTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == KeyEvent.VK_ENTER) {
					getThis().sectionNameTextField.setEditable(false);
					
					IRI newSectionIRI = IRI.create(sectionIRI.getEntryIRI().getStart() + getThis().sectionNameTextField.getText());
					if (!getThis().sectionNameTextField.getText().equals("") && father.isLegalIRI(newSectionIRI) && father.isUniqueIRI(newSectionIRI)) {
						NaturalOWLTab.OQM.renameSection(sectionIRI.getEntryIRI(), newSectionIRI);
						
						father.updateSectionComboBoxes();
						
						sectionIRI = new ListIRI(newSectionIRI);
						father.dirtenOntologies();
					} else {
						getThis().sectionNameTextField.setText(sectionIRI.toString());
					}
				}
			}

			public void keyReleased(KeyEvent key) {}
			public void keyTyped(KeyEvent key) {}
		});
		
		this.buttonsPanel.add(sectionNameTextField, 1);
	}
	
	void createSectionLabelEN(String sectionLabel) {
		this.sectionLabelEN = sectionLabel;
		
		this.sectionLabelENTextField = new JTextField(35);
		this.sectionLabelENTextField.setText(sectionLabel);
		this.sectionLabelENTextField.setEditable(false);
		
		this.sectionLabelENTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == KeyEvent.VK_ENTER) {
					getThis().sectionLabelENTextField.setEditable(false);
				
					NaturalOWLTab.OQM.setSectionLabel(getThis().sectionIRI.getEntryIRI(), sectionLabelENTextField.getText(), Languages.ENGLISH);
					father.dirtenOntologies();
				}
			}

			public void keyReleased(KeyEvent key) {}
			public void keyTyped(KeyEvent key) {}
		});
		
		this.labelPanelEN.add(sectionLabelENTextField, 1);
	}
	
	void createSectionLabelGR(String sectionLabel) {
		this.sectionLabelGR = sectionLabel;
		
		this.sectionLabelGRTextField = new JTextField(35);
		this.sectionLabelGRTextField.setText(sectionLabel);
		this.sectionLabelGRTextField.setEditable(false);
		
		this.sectionLabelGRTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == KeyEvent.VK_ENTER) {
					getThis().sectionLabelGRTextField.setEditable(false);
				
					NaturalOWLTab.OQM.setSectionLabel(getThis().sectionIRI.getEntryIRI(), sectionLabelGRTextField.getText(), Languages.GREEK);
					father.dirtenOntologies();
				}
			}

			public void keyReleased(KeyEvent key) {}
			public void keyTyped(KeyEvent key) {}
		});
		
		this.labelPanelGR.add(sectionLabelGRTextField, 1);
	}
	
	void createSectionOrder(int n) {
		sectionOrder = n;
		
		this.sectionOrderTextField = new JTextField(3);
		this.sectionOrderTextField.setText(sectionOrder + "");
		this.sectionOrderTextField.setEditable(false);
		//this.sectionOrderTextField.setBorder(null);
		
		this.sectionOrderTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == KeyEvent.VK_ENTER) {
					if (OrderingTab.isValidOrder(getThis().sectionOrderTextField.getText())) {
						updateOrder(Integer.parseInt(getThis().sectionOrderTextField.getText()));
						getThis().sectionOrderTextField.setEditable(false);
					
						NaturalOWLTab.OQM.setSectionOrder(getThis().sectionIRI.getEntryIRI(), Integer.parseInt(getThis().sectionOrderTextField.getText()));
						father.dirtenOntologies();
					} else {
						getThis().sectionOrderTextField.setText(sectionOrder + "");
					}
				}
			}

			public void keyReleased(KeyEvent key) {}
			public void keyTyped(KeyEvent key) {}
		});
		
		this.buttonsPanel.add(sectionOrderTextField, 2);
	}

	void loadOrderPanels(ArrayList<ListIRI> list) {
		int size = list.size();
		OrderPanel propertyArray[] = new OrderPanel[size];
		for (int i = 0; i < propertyArray.length; i++) {
			propertyArray[i] = new OrderPanel(this);
			this.addToOrderPanels(propertyArray[i]);
		}
		for (int i = 0; i < propertyArray.length; i++) {
			propertyArray[i].createOrderPanel(list.get(i), NaturalOWLTab.OQM.getPropertyOrder(list.get(i).getEntryIRI()));
		}

		this.sectionPanel.validate();
		this.sectionPanel.repaint();
	}
	
	void movePanel(int start, int finish) {
		if (start <= finish) {
			for (int i = start + 1; i <= finish; i++) {
				OrderingTab.getSectionsPanel().switchPanelOrder(i);
				switchSectionPanels(i);
	
				SectionPanel upperSectionPanel = (SectionPanel) OrderingTab.getSections().get(i - 1);
				SectionPanel thisSectionPanel = (SectionPanel) OrderingTab.getSections().get(i);
				
				updateUpDownArrows(upperSectionPanel);
				updateUpDownArrows(thisSectionPanel);
	
				upperSectionPanel.buttonsPanel.repaint();
				thisSectionPanel.buttonsPanel.repaint();
			}
		} else {
			for (int i = start; i > finish + 1; i--) {
				OrderingTab.getSectionsPanel().switchPanelOrder(i);
				switchSectionPanels(i);
	
				SectionPanel upperSectionPanel = (SectionPanel) OrderingTab.getSections().get(i - 1);
				SectionPanel thisSectionPanel = (SectionPanel) OrderingTab.getSections().get(i);
				
				updateUpDownArrows(upperSectionPanel);
				updateUpDownArrows(thisSectionPanel);
	
				upperSectionPanel.buttonsPanel.repaint();
				thisSectionPanel.buttonsPanel.repaint();
			}
		}
		OrderingTab.getSectionsPanel().repaint();
	}

	void switchSectionPanels(int i) {
		SectionPanel a = (SectionPanel) OrderingTab.getSections().get(i - 1);
		SectionPanel b = (SectionPanel) OrderingTab.getSections().get(i);
		OrderingTab.removeFromSections(a);
		OrderingTab.removeFromSections(b);
		OrderingTab.addToSections(i - 1, b);
		OrderingTab.addToSections(i, a);
	}
	
	void updateOrder(int newOrder) {
		int position = OrderingTab.getPositionInSections(getThis());
		SectionPanel thisSectionPanel = (SectionPanel) OrderingTab.getSections().get(position);

		int thisOrder = thisSectionPanel.getSectionOrder();
		for (int i = 0; i < OrderingTab.getSections().size(); i++) {
			SectionPanel checkOrderPanel = (SectionPanel) OrderingTab.getSections().get(i);
			int checkOrder = checkOrderPanel.getSectionOrder();
			
			if (checkOrder > newOrder) {
				thisSectionPanel.setSectionOrder(newOrder);
				movePanel(position, i-1);
				return;
			}
		}

		thisSectionPanel.setSectionOrder(newOrder);
		movePanel(position, OrderingTab.getSections().size()-1);
		return;
	}

	void updateUpDownArrows(SectionPanel sectionPanel) {
		// if existing, hide them
		if (sectionPanel.up == null) {
			ClassLoader loader = OrderingTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/upImg.png");		
			ImageIcon upIcon = new ImageIcon(imageURL);		
			Image img = upIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			upIcon = new ImageIcon(newimg);  
			sectionPanel.up = new JButton(upIcon);
			sectionPanel.up.setOpaque(false);
			sectionPanel.up.setContentAreaFilled(false);
			sectionPanel.up.setBorderPainted(false);	

			sectionPanel.up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int position = OrderingTab.getPositionInSections(getThis());
					
					SectionPanel upperSectionPanel = (SectionPanel) OrderingTab.getSections().get(position - 1);
					SectionPanel thisSectionPanel = (SectionPanel) OrderingTab.getSections().get(position);
					OrderingTab.getSectionsPanel().switchPanelOrder(position);
					switchSectionPanels(position);
					
					int upperSectionOrder = upperSectionPanel.getSectionOrder();
					int thisSectionOrder = thisSectionPanel.getSectionOrder();
					if (upperSectionOrder != thisSectionOrder) {
						upperSectionPanel.setSectionOrder(thisSectionOrder);
						thisSectionPanel.setSectionOrder(upperSectionOrder);
					} else {
						if (upperSectionOrder > 1) {
							if (position - 2 >= 0) {
								int upperUpperSectionOrder = ((SectionPanel) OrderingTab.getSections().get(position - 2)).getSectionOrder();
								if (upperUpperSectionOrder != upperSectionOrder) {
									thisSectionPanel.setSectionOrder(upperSectionOrder - 1);
								}
							} else {
								thisSectionPanel.setSectionOrder(upperSectionOrder - 1);
							}
						}
					}

					updateUpDownArrows(upperSectionPanel);
					updateUpDownArrows(thisSectionPanel);

					upperSectionPanel.buttonsPanel.repaint();
					thisSectionPanel.buttonsPanel.repaint();
					OrderingTab.getSectionsPanel().repaint();
				}
			});
			sectionPanel.up.setVisible(true);
			sectionPanel.buttonsPanel.add(sectionPanel.up);
		}
		
		if (sectionPanel.down == null) {
			ClassLoader loader = OrderingTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/downImg.png");		
			ImageIcon downIcon = new ImageIcon(imageURL);		
			Image img = downIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			downIcon = new ImageIcon(newimg);  
			sectionPanel.down = new JButton(downIcon);
			sectionPanel.down.setOpaque(false);
			sectionPanel.down.setContentAreaFilled(false);
			sectionPanel.down.setBorderPainted(false);
			
			sectionPanel.down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int position = OrderingTab.getPositionInSections(getThis());
					
					SectionPanel thisSectionPanel = (SectionPanel) OrderingTab.getSections().get(position);
					SectionPanel lowerSectionPanel = (SectionPanel) OrderingTab.getSections().get(position + 1);

					OrderingTab.getSectionsPanel().switchPanelOrder(position + 1);
					switchSectionPanels(position + 1);
					
					int thisSectionOrder = thisSectionPanel.getSectionOrder();
					int lowerSectionOrder = lowerSectionPanel.getSectionOrder();
					if (lowerSectionOrder != thisSectionOrder) {
						thisSectionPanel.setSectionOrder(lowerSectionOrder);
						lowerSectionPanel.setSectionOrder(thisSectionOrder);
					} else {
						if (lowerSectionOrder < OrderingQueryManager.defaultOrder) {
							if (position + 2 < OrderingTab.getSections().size()) {
								int lowerLowerSectionOrder = ((SectionPanel) OrderingTab.getSections().get(position + 2)).getSectionOrder();
								if (lowerLowerSectionOrder != lowerSectionOrder) {
									thisSectionPanel.setSectionOrder(lowerSectionOrder + 1);
								}
							} else {
								thisSectionPanel.setSectionOrder(lowerSectionOrder + 1);
							}
						}
					}

					updateUpDownArrows(thisSectionPanel);
					updateUpDownArrows(lowerSectionPanel);

					thisSectionPanel.buttonsPanel.repaint();
					lowerSectionPanel.buttonsPanel.repaint();
					OrderingTab.getSectionsPanel().repaint();
				}
			});
			sectionPanel.down.setVisible(true);
			sectionPanel.buttonsPanel.add(sectionPanel.down);
		}

		int position = OrderingTab.getPositionInSections(sectionPanel);
		
		// recheck and recreate if needed
		if ((position == 0) && (OrderingTab.getSectionsSize() == 1)) {
			sectionPanel.up.setEnabled(false);
			sectionPanel.down.setEnabled(false);
		} else if (position == 0) {
			sectionPanel.up.setEnabled(false);
			sectionPanel.down.setEnabled(true);
		} else if ((position > 0)&&(position < OrderingTab.getSectionsSize() - 1)) {
			sectionPanel.down.setEnabled(true);
			sectionPanel.up.setEnabled(true);
		} else if (position == OrderingTab.getSectionsSize() - 1) {
			sectionPanel.up.setEnabled(true);
			sectionPanel.down.setEnabled(false);
		}
		sectionPanel.sectionPanel.repaint();
		sectionPanel.sectionPanel.validate();
	}

	void createSectionPanel(ListIRI sectionIRI, int sectionOrder, String sectionLabelEN, String sectionLabelGR, ArrayList<ListIRI> orderIRIs) {
		getThis().sectionIRI = sectionIRI;
		
		this.sectionPanel.add(this.buttonsPanel);
		this.buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.buttonsPanel.setPreferredSize(new Dimension(400, 130));/***/
		this.buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		
		this.sectionPanel.add(this.labelPanelEN);
		this.labelPanelEN.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.labelPanelEN.setPreferredSize(new Dimension(390, 40));/***/
		
		this.sectionPanel.add(this.labelPanelGR);
		this.labelPanelGR.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.labelPanelGR.setPreferredSize(new Dimension(390, 40));/***/
		
		JLabel orderTitle = new JLabel("Included properties in this section:");
		this.sectionPanel.add(orderTitle);
		
		this.orderPanel = new MyJPanel(330, 0);
		this.orderPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		this.sectionPanel.add(this.orderPanel);

		this.sectionPanel.increaseHeight(160);/***/
		OrderingTab.getSectionsPanel().increaseHeight(170);/***/
		OrderingTab.updateSectionScroll();
		if (buttonNew != null) {
			buttonNew.setVisible(false);
		}
		// title
		
		JLabel titleLabel = new JLabel("Section:");
		titleLabel.setPreferredSize(new Dimension(40, titleLabel.getPreferredSize().height));
		this.buttonsPanel.add(titleLabel);
		
		createSectionTitle(sectionIRI);
		
		this.sectionNameTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) { 
				if (!getThis().sectionNameTextField.isEditable()) {
					getThis().sectionNameTextField.setEditable(true);
				}}
			public void focusLost(FocusEvent arg0) {
				if (getThis().sectionNameTextField.isEditable()) {
					getThis().sectionNameTextField.setEditable(false);
					
					IRI newSectionIRI = IRI.create(getThis().sectionIRI.getEntryIRI().getStart() + getThis().sectionNameTextField.getText());
					if (!getThis().sectionNameTextField.getText().equals("") && father.isLegalIRI(newSectionIRI) && father.isUniqueIRI(newSectionIRI)) {
						NaturalOWLTab.OQM.renameSection(getThis().sectionIRI.getEntryIRI(), newSectionIRI);
						
						father.updateSectionComboBoxes();
						
						getThis().sectionIRI = new ListIRI(newSectionIRI);
						father.dirtenOntologies();
					} else {
						getThis().sectionNameTextField.setText(getThis().sectionIRI.toString());
					}
				}
			}
		});
		
		//Section order
		createSectionOrder(sectionOrder);
		
		this.sectionOrderTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) { 
				if (!getThis().sectionOrderTextField.isEditable()) {
					getThis().sectionOrderTextField.setEditable(true);
				}}
			public void focusLost(FocusEvent arg0) {
				if (getThis().sectionOrderTextField.isEditable()) {
					if (OrderingTab.isValidOrder(getThis().sectionOrderTextField.getText())) {
						updateOrder(Integer.parseInt(getThis().sectionOrderTextField.getText()));
						getThis().sectionOrderTextField.setEditable(false);
					
						NaturalOWLTab.OQM.setSectionOrder(getThis().sectionIRI.getEntryIRI(), Integer.parseInt(getThis().sectionOrderTextField.getText()));
						father.dirtenOntologies();
					} else {
						getThis().sectionOrderTextField.setText(getThis().getSectionOrder() + "");
					}
				}
			}
		});
		
		updateUpDownArrows(this);

		// delete button
		ClassLoader loader = OrderingTab.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/deleteImg.png");		
		ImageIcon deleteIcon = new ImageIcon(imageURL);		
		Image img = deleteIcon.getImage(); 
		Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		deleteIcon = new ImageIcon(newimg);  
		JButton deleteSectionBut = new JButton(deleteIcon);
		deleteSectionBut.setOpaque(false);
		deleteSectionBut.setContentAreaFilled(false);
		deleteSectionBut.setBorderPainted(false);	
		String t = "Are you sure you want to delete this Section?";
		final Object title = new String(t);
		deleteSectionBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = JOptionPane.showConfirmDialog(null, title, "Delete?",
						JOptionPane.OK_CANCEL_OPTION);
				if (ans == JOptionPane.OK_OPTION) {
					delete(getThis());
				}
			}
		});
		this.buttonsPanel.add(deleteSectionBut);
		// end delete button

		// Label		
		JLabel labelLabelEN = new JLabel("English label:");
		labelLabelEN.setPreferredSize(new Dimension(65, labelLabelEN.getPreferredSize().height));
		this.labelPanelEN.add(labelLabelEN);
		
		createSectionLabelEN(sectionLabelEN);
		
		this.sectionLabelENTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) { 
				if (!getThis().sectionLabelENTextField.isEditable()) {
					getThis().sectionLabelENTextField.setEditable(true);
				}}
			public void focusLost(FocusEvent arg0) {
				if (getThis().sectionLabelENTextField.isEditable()) {
					getThis().sectionLabelENTextField.setEditable(false);
				
					NaturalOWLTab.OQM.setSectionLabel(getThis().sectionIRI.getEntryIRI(), getThis().sectionLabelENTextField.getText(), Languages.ENGLISH);
					father.dirtenOntologies();
				}
			}
		});

		this.buttonsPanel.add(labelPanelEN);

		// Label		
		JLabel labelLabelGR = new JLabel("Greek label:");
		labelLabelGR.setPreferredSize(new Dimension(65, labelLabelGR.getPreferredSize().height));
		this.labelPanelGR.add(labelLabelGR);
		
		createSectionLabelGR(sectionLabelGR);
		
		this.sectionLabelGRTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) { 
				if (!getThis().sectionLabelGRTextField.isEditable()) {
					getThis().sectionLabelGRTextField.setEditable(true);
				}}
			public void focusLost(FocusEvent arg0) {
				if (getThis().sectionLabelGRTextField.isEditable()) {
					getThis().sectionLabelGRTextField.setEditable(false);
				
					NaturalOWLTab.OQM.setSectionLabel(getThis().sectionIRI.getEntryIRI(), getThis().sectionLabelGRTextField.getText(), Languages.GREEK);
					father.dirtenOntologies();
				}
			}
		});

		this.buttonsPanel.add(labelPanelGR);

		// addProperties();
		if(orderIRIs != null)
			loadOrderPanels(orderIRIs);
		
		OrderingTab.getSectionsPanel().add(this.sectionPanel);

		for (int i = 0; i < OrderingTab.getSectionsSize(); i++) {
			updateUpDownArrows((SectionPanel) OrderingTab.getSections().get(i));
		}
	}

	void delete(SectionPanel section) {
		// delete apo to UI
		OrderingTab.getSectionsPanel().decreaseHeight(this.sectionPanel.height + 30);
		OrderingTab.getSectionsPanel().remove(section.sectionPanel);
		OrderingTab.getSectionsPanel().validate();
		OrderingTab.getSectionsPanel().repaint();
		OrderingTab.updateSectionScroll();

		// delete from sections list
		for (int i = 0; i < OrderingTab.getSectionsSize(); i++) {
			if (OrderingTab.getSections().get(i).equals(section)) {
				OrderingTab.removeFromSections(section);
			}
		}
		for (int i = 0; i < OrderingTab.getSectionsSize(); i++) {
			updateUpDownArrows((SectionPanel) OrderingTab.getSections().get(i));
		}

		NaturalOWLTab.OQM.deleteSection(sectionIRI.getEntryIRI());
		father.dirtenOntologies();
		
		father.refreshUnsorted();
	}

	SectionPanel getThis() {
		return this;
	}

	public MyJPanel getOrderPanel() {
		return this.orderPanel;
	}

	public MyJPanel getSectionPanel() {
		return this.sectionPanel;
	}
	
	public JTextField getSectionNameTextField() {
		return sectionNameTextField;
	}

	public ArrayList<OrderPanel> getOrderPanels() {
		return this.orderPanels;
	}

	public void addToOrderPanels(OrderPanel p) {
		this.orderPanels.add(p);
	}

	public void addToOrderPanels(int i, OrderPanel p) {
		this.orderPanels.add(i, p);
	}

	public int getOrderPanelsSize() {
		return this.orderPanels.size();
	}

	public void removeFromOrderPanels(OrderPanel p) {
		for (int i = 0; i < this.orderPanels.size(); i++) {
			if (this.orderPanels.get(i).equals(p)) {
				this.orderPanels.remove(i);
			}
		}
	}

	public int getPositionInOrderPanels(OrderPanel p) {
		boolean flag = false;
		int i = 0;
		while (!flag && i < this.orderPanels.size()) {
			if (this.orderPanels.get(i).equals(p)) {
				flag = true;
			} else
				i++;
		}
		return i;
	}

	public void updatePropertiesPanel() {
		this.orderPanel.updateUI();
	}

	public static boolean isFirst() {
		return first;
	}

	public static void setFirst(boolean first) {
		SectionPanel.first = first;
	}

	public boolean isFirstInProp() {
		return firstInProp;
	}

	public void setFirstInProp(boolean firstInProp) {
		this.firstInProp = firstInProp;
	}

	public MyJPanel getButtonsPanel() {
		return buttonsPanel;
	}

	public int getSectionOrder() {
		return sectionOrder;
	}

	public void setSectionOrder(int section) {
		this.sectionOrder = section;
		
		sectionOrderTextField.setText(sectionOrder + "");
	}

	public JTextField getSectionTextField() {
		return sectionNameTextField;
	}

	public ListIRI getSectionIRI() {
		return sectionIRI;
	}	
	
	public OrderingTab getFather() {
		return father;
	}
}

// ////////////////////////////////////////////////////////////////////////////////////////////////////

class OrderPanel {
	private JPanel orderPanel = new JPanel();
	
	private int order;
	private JLabel propertyName;
	private JTextField propertyOrderTextField;
	private JButton up;
	private JButton down;
	private SectionPanel containingSectionPanel;
	
	private ListIRI propertyIRI;

	OrderPanel(SectionPanel b) {
		this.orderPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.orderPanel.setPreferredSize(new Dimension(320, 45));
		containingSectionPanel = b;
	}

	void createPropertyName(ListIRI s) {
		this.propertyName = new JLabel(s.toString());
		this.propertyName.setPreferredSize(new Dimension(160, 40));

    	if (!DefaultResourcesManager.isDefaultResource(s.getEntryIRI())) {
    		this.propertyName.setToolTipText(s.getEntryIRI().toString());
    	} else {
    		this.propertyName.setToolTipText("<html><b>This is a default resource of the system.</b> <br>" + s.getEntryIRI().toString() + "</html>");
    	}

		this.orderPanel.add(propertyName, 0);
	}
	
	void createPropertyOrder(int n) {
		order = n;
		
		this.propertyOrderTextField = new JTextField(3);
		this.propertyOrderTextField.setText(order + "");
		this.propertyOrderTextField.setEditable(false);
		//this.propertyOrderTextField.setBorder(null);
		
		this.propertyOrderTextField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == KeyEvent.VK_ENTER) {
					if (OrderingTab.isValidOrder(getThis().propertyOrderTextField.getText())) {
						updateOrder(Integer.parseInt(getThis().propertyOrderTextField.getText()));
						getThis().propertyOrderTextField.setEditable(false);
					
						NaturalOWLTab.OQM.setPropertyOrder(getThis().propertyIRI.getEntryIRI(), Integer.parseInt(getThis().propertyOrderTextField.getText()));
						containingSectionPanel.getFather().dirtenOntologies();
					} else {
						getThis().propertyOrderTextField.setText(order + "");
					}
				}
			}

			public void keyReleased(KeyEvent key) {}
			public void keyTyped(KeyEvent key) {}
		});
		
		this.orderPanel.add(propertyOrderTextField, 1);
	}
	
	void movePanel(int start, int finish) {
		if (start <= finish) {
			for (int i = start + 1; i <= finish; i++) {
				getThis().containingSectionPanel.getOrderPanel().switchPanelOrder(i);
				switchInProperties(i);
	
				OrderPanel upperOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(i - 1);
				OrderPanel thisOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(i);
				
				updateUpDownArrows(upperOrderPanel);
				updateUpDownArrows(thisOrderPanel);
	
				upperOrderPanel.orderPanel.repaint();
				thisOrderPanel.orderPanel.repaint();
			}
		} else {
			for (int i = start; i > finish + 1; i--) {
				getThis().containingSectionPanel.getOrderPanel().switchPanelOrder(i);
				switchInProperties(i);
	
				OrderPanel upperOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(i - 1);
				OrderPanel thisOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(i);
				
				updateUpDownArrows(upperOrderPanel);
				updateUpDownArrows(thisOrderPanel);
	
				upperOrderPanel.orderPanel.repaint();
				thisOrderPanel.orderPanel.repaint();
			}
		}
		getThis().containingSectionPanel.getOrderPanel().repaint();
	}

	void switchInProperties(int i) {
		OrderPanel a = (OrderPanel) containingSectionPanel.getOrderPanels().get(i - 1);
		OrderPanel b = (OrderPanel) containingSectionPanel.getOrderPanels().get(i);
		containingSectionPanel.removeFromOrderPanels(a);
		containingSectionPanel.removeFromOrderPanels(b);
		containingSectionPanel.addToOrderPanels(i - 1, b);
		containingSectionPanel.addToOrderPanels(i, a);
	}
	
	void updateOrder(int newOrder) {
		int position = containingSectionPanel.getPositionInOrderPanels(getThis());
		OrderPanel thisOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(position);

		int thisOrder = thisOrderPanel.getOrder();
		for (int i = 0; i < containingSectionPanel.getOrderPanels().size(); i++) {
			OrderPanel checkOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(i);
			int checkOrder = checkOrderPanel.getOrder();
			
			if (checkOrder > newOrder) {
				thisOrderPanel.setOrder(newOrder);
				movePanel(position, i-1);
				return;
			}
		}

		thisOrderPanel.setOrder(newOrder);
		movePanel(position, containingSectionPanel.getOrderPanels().size()-1);
		return;
	}

	void updateUpDownArrows(OrderPanel ordPanel) {
		if (ordPanel.up == null) {
			ClassLoader loader = OrderingTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/upImg.png");		
			ImageIcon upIcon = new ImageIcon(imageURL);		
			Image img = upIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			upIcon = new ImageIcon(newimg);
			ordPanel.up = new JButton(upIcon);
			ordPanel.up.setOpaque(false);
			ordPanel.up.setContentAreaFilled(false);
			ordPanel.up.setBorderPainted(false);			

			ordPanel.up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int position = containingSectionPanel.getPositionInOrderPanels(getThis());
					
					OrderPanel upperOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(position - 1);
					OrderPanel thisOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(position);

					getThis().containingSectionPanel.getOrderPanel().switchPanelOrder(position);
					switchInProperties(position);
					
					int upperOrder = upperOrderPanel.getOrder();
					int thisOrder = thisOrderPanel.getOrder();
					if (upperOrder != thisOrder) {
						upperOrderPanel.setOrder(thisOrder);
						thisOrderPanel.setOrder(upperOrder);
					} else {
						if (upperOrder > 1) {
							if (position - 2 >= 0) {
								int upperUpperOrder = ((OrderPanel) containingSectionPanel.getOrderPanels().get(position - 2)).getOrder();
								if (upperUpperOrder != upperOrder) {
									thisOrderPanel.setOrder(upperOrder - 1);
								}
							} else {
								thisOrderPanel.setOrder(upperOrder - 1);
							}
						}
					}

					updateUpDownArrows(upperOrderPanel);
					updateUpDownArrows(thisOrderPanel);

					upperOrderPanel.orderPanel.repaint();
					thisOrderPanel.orderPanel.repaint();
					getThis().containingSectionPanel.getOrderPanel().repaint();
				}
			});
			ordPanel.up.setVisible(true);
			ordPanel.orderPanel.add(ordPanel.up);
		}

		if (ordPanel.down == null) {
			ClassLoader loader = OrderingTab.class.getClassLoader();
			URL imageURL = loader.getResource("/icons/downImg.png");		
			ImageIcon downIcon = new ImageIcon(imageURL);		
			Image img = downIcon.getImage(); 
			Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
			downIcon = new ImageIcon(newimg);  
			ordPanel.down = new JButton(downIcon);
			ordPanel.down.setOpaque(false);
			ordPanel.down.setContentAreaFilled(false);
			ordPanel.down.setBorderPainted(false);			
			ordPanel.down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int position = containingSectionPanel.getPositionInOrderPanels(getThis());

					OrderPanel thisOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(position);
					OrderPanel lowerOrderPanel = (OrderPanel) containingSectionPanel.getOrderPanels().get(position + 1);
					
					getThis().containingSectionPanel.getOrderPanel().switchPanelOrder(position + 1);
					switchInProperties(position + 1);
					
					int thisOrder = thisOrderPanel.getOrder();
					int lowerOrder = lowerOrderPanel.getOrder();
					if (lowerOrder != thisOrder) {
						thisOrderPanel.setOrder(lowerOrder);
						lowerOrderPanel.setOrder(thisOrder);
					} else {
						if (lowerOrder < OrderingQueryManager.defaultOrder) {
							if (position + 2 < containingSectionPanel.getOrderPanels().size()) {
								int lowerLowerOrder = ((OrderPanel) containingSectionPanel.getOrderPanels().get(position + 2)).getOrder();
								if (lowerLowerOrder != lowerOrder) {
									thisOrderPanel.setOrder(lowerOrder + 1);
								}
							} else {
								thisOrderPanel.setOrder(lowerOrder + 1);
							}
						}
					}
					
					updateUpDownArrows(thisOrderPanel);					
					updateUpDownArrows(lowerOrderPanel);
					
					thisOrderPanel.orderPanel.repaint();
					lowerOrderPanel.orderPanel.repaint();
					getThis().containingSectionPanel.getOrderPanel().repaint();
				}
			});
			ordPanel.down.setVisible(true);
			ordPanel.orderPanel.add(ordPanel.down);
		}

		int position = containingSectionPanel.getPositionInOrderPanels(ordPanel);
		// recheck and recreate if needed
		if ((position == 0) && (containingSectionPanel.getOrderPanelsSize() == 1)) {
			ordPanel.up.setEnabled(false);
			ordPanel.down.setEnabled(false);
		} else if (position == 0) {
			ordPanel.up.setEnabled(false);
			ordPanel.down.setEnabled(true);
		} else if ((position > 0)&&(position < containingSectionPanel.getOrderPanelsSize() - 1)) {
			ordPanel.down.setEnabled(true);
			ordPanel.up.setEnabled(true);
		} else if (position == containingSectionPanel.getOrderPanelsSize() - 1) {
			ordPanel.up.setEnabled(true);
			ordPanel.down.setEnabled(false);
		}
		ordPanel.orderPanel.repaint();
		ordPanel.orderPanel.validate();
	}

	void createOrderPanel(ListIRI propertyIRI, int propertyOrder) {
		getThis().propertyIRI = propertyIRI;
		
		if (getThis().containingSectionPanel.getOrderPanel().getComponentCount() == 0) {
			getThis().containingSectionPanel.getOrderPanel().increaseHeight(10);
			getThis().containingSectionPanel.getSectionPanel().increaseHeight(10);
			OrderingTab.getSectionsPanel().increaseHeight(10);
		}
		
		getThis().containingSectionPanel.getOrderPanel().increaseHeight(50);
		getThis().containingSectionPanel.getSectionPanel().increaseHeight(50);
		getThis().containingSectionPanel.getSectionPanel().validate();
		OrderingTab.getSectionsPanel().increaseHeight(50);
		OrderingTab.getSectionsPanel().validate();
		OrderingTab.updateSectionScroll();
		
		createPropertyName(propertyIRI);
		
		createPropertyOrder(propertyOrder);
		
		this.propertyOrderTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) { 
				if (!getThis().propertyOrderTextField.isEditable()) {
					getThis().propertyOrderTextField.setEditable(true);
				}
			}
			public void focusLost(FocusEvent arg0) {
				if (getThis().propertyOrderTextField.isEditable()) {
					if (OrderingTab.isValidOrder(getThis().propertyOrderTextField.getText())) {
						updateOrder(Integer.parseInt(getThis().propertyOrderTextField.getText()));
						getThis().propertyOrderTextField.setEditable(false);
					
						NaturalOWLTab.OQM.setPropertyOrder(getThis().propertyIRI.getEntryIRI(), Integer.parseInt(getThis().propertyOrderTextField.getText()));
						containingSectionPanel.getFather().dirtenOntologies();
					} else {
						getThis().propertyOrderTextField.setText(order + "");
					}
				}
			}
		});

		updateUpDownArrows(this);

		// delete button
		ClassLoader loader = OrderingTab.class.getClassLoader();
		URL imageURL = loader.getResource("/icons/deleteImg.png");		
		ImageIcon deleteIcon = new ImageIcon(imageURL);		
		Image img = deleteIcon.getImage(); 
		Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);  
		deleteIcon = new ImageIcon(newimg);  
		JButton deleteOrderBut = new JButton(deleteIcon);
		deleteOrderBut.setOpaque(false);
		deleteOrderBut.setContentAreaFilled(false);
		deleteOrderBut.setBorderPainted(false);		
		
		String t = "Are you sure you want to remove this Property from this Section?";
		final Object title = new String(t);
		deleteOrderBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = JOptionPane.showConfirmDialog(null, title, "Delete?",
						JOptionPane.OK_CANCEL_OPTION);
				if (ans == JOptionPane.OK_OPTION) {
					delete(getThis());
				}
			}
		});
		this.orderPanel.add(deleteOrderBut);
			
		getThis().containingSectionPanel.getOrderPanel().add(this.orderPanel);
	}

	void delete(OrderPanel p) {
		// delete from UI
		getThis().containingSectionPanel.getOrderPanel().remove(p.orderPanel);

		if (getThis().containingSectionPanel.getOrderPanel().getComponentCount() == 0) {
			getThis().containingSectionPanel.getOrderPanel().decreaseHeight(10);
			getThis().containingSectionPanel.getSectionPanel().decreaseHeight(10);
			OrderingTab.getSectionsPanel().decreaseHeight(10);
		}
		
		getThis().containingSectionPanel.getOrderPanel().decreaseHeight(50);
		getThis().containingSectionPanel.getSectionPanel().decreaseHeight(50);
		getThis().containingSectionPanel.getSectionPanel().validate();
		OrderingTab.getSectionsPanel().decreaseHeight(50);
		OrderingTab.getSectionsPanel().validate();
		containingSectionPanel.getOrderPanel().validate();
		containingSectionPanel.getOrderPanel().repaint();
		containingSectionPanel.updatePropertiesPanel();
		OrderingTab.updateSectionScroll();
		
		// delete from properties list
		for (int i = 0; i < containingSectionPanel.getOrderPanelsSize(); i++) {
			if (containingSectionPanel.getOrderPanels().get(i).equals(p)) {
				containingSectionPanel.removeFromOrderPanels(p);
			}
		}
		
		// call again addUpDownArrows method
		for (int i = 0; i < containingSectionPanel.getOrderPanelsSize(); i++) {
			updateUpDownArrows((OrderPanel) containingSectionPanel.getOrderPanels().get(i));
		}
		
		//Update the resources
		NaturalOWLTab.OQM.setPropertySection(getThis().propertyIRI.getEntryIRI(), NLResourceManager.defaultSection.getIRI());
		NaturalOWLTab.OQM.setPropertyOrder(getThis().propertyIRI.getEntryIRI(), OrderingQueryManager.defaultOrder);
		containingSectionPanel.getFather().dirtenOntologies();
		
		containingSectionPanel.getFather().refreshUnsorted();
	}

	OrderPanel getThis() {
		return this;
	}	

	public JPanel getOrderPanel() {
		return orderPanel;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
		
		this.propertyOrderTextField.setText(order + "");
	}
	
	public JButton getUp() {
		return up;
	}

	public JButton getDown() {
		return down;
	}

	public SectionPanel getSection() {
		return containingSectionPanel;
	}

	public ListIRI getPropertyIRI() {
		return propertyIRI;
	}
}

class UnsortedPanel {
	private JPanel unsortedPanel = new JPanel();
	private JLabel propertyName;
	private JButton sectionAdd;
	private OrderingTab father;
	private MyJPanel containingUnsortedPanel;
	
	private JComboBox sectionCombo;
	private DefaultComboBoxModel sectionsComboModel;
	
	private ListIRI unsortedProperty;

	UnsortedPanel(OrderingTab f, MyJPanel b) {
		this.unsortedPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.unsortedPanel.setPreferredSize(new Dimension(400, 50));
		
		father = f;
		containingUnsortedPanel = b;
	}

	void createPropertyName(ListIRI s) {
		this.propertyName = new JLabel();
		this.propertyName.setPreferredSize(new Dimension(160, 40));
		this.propertyName.setText(s.toString());

    	if (!DefaultResourcesManager.isDefaultResource(s.getEntryIRI())) {
    		this.propertyName.setToolTipText(s.getEntryIRI().toString());
    	} else {
    		this.propertyName.setToolTipText("<html><b>This is a default resource of the system.</b> <br>" + s.getEntryIRI().toString() + "</html>");
    	}
    	
		this.unsortedPanel.add(propertyName, 0);
	}
	
	void createUnsortedPanel(ListIRI propertyIRI) {
		//JLabel titleLabel = new JLabel("");
		//titleLabel.setPreferredSize(new Dimension(30, titleLabel.getPreferredSize().height));
		//this.unsortedPanel.add(titleLabel);
		
		unsortedProperty = propertyIRI;
		createPropertyName(unsortedProperty);
		
		HashSet<ListIRI> availableSections = new HashSet();
        for (IRI sectionIRI : NaturalOWLTab.OQM.getOrderedSections()) {
        	availableSections.add(new ListIRI(sectionIRI));
        }
        
        ArrayList<ListIRI> sortList = new ArrayList<ListIRI>(availableSections);
        Collections.sort(sortList);
        
        sectionsComboModel = new DefaultComboBoxModel(sortList.toArray());
        sectionCombo = new JComboBox(sectionsComboModel);
        sectionCombo.setRenderer(new ListRenderer());
        
        sectionAdd = new JButton(">>");
        sectionAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListIRI selectedSection = (ListIRI)sectionCombo.getSelectedItem();
				for (SectionPanel sPanel : OrderingTab.getSections()) {
					if (sPanel.getSectionIRI().equals(selectedSection)) {
						//Add to Section
						OrderPanel oPanel = new OrderPanel(sPanel);
						sPanel.addToOrderPanels(oPanel);

						oPanel.createOrderPanel(unsortedProperty, OrderingQueryManager.defaultOrder);
						
						for (int i = 0; i < sPanel.getOrderPanelsSize(); i++) {
							oPanel.updateUpDownArrows((OrderPanel) sPanel.getOrderPanels().get(i));
						}

						OrderingTab.getSectionsPanel().validate();
						OrderingTab.getSectionsPanel().repaint();
						OrderingTab.updateSectionScroll();

						//Delete from unsorted UI
						OrderingTab.getUnsortedPanels().remove(getThis().unsortedPanel);
				        OrderingTab.getUnsortedPanels().decreaseHeight(55);
						OrderingTab.getUnsortedPanels().validate();
						OrderingTab.getUnsortedPanels().repaint();
						OrderingTab.updateUnsortedScroll();
						
						//Delete from unsorted properties
						OrderingTab.removeFromUnsorted(getThis());
						
						//Update the resources
						NaturalOWLTab.OQM.setPropertySection(unsortedProperty.getEntryIRI(), sPanel.getSectionIRI().getEntryIRI());
						NaturalOWLTab.OQM.setPropertyOrder(unsortedProperty.getEntryIRI(), OrderingQueryManager.defaultOrder);

						father.dirtenOntologies();
					}
				}
			}
		});
        
        this.unsortedPanel.add(sectionCombo);
        this.unsortedPanel.add(sectionAdd);
        
        OrderingTab.getUnsortedPanels().increaseHeight(55);
		OrderingTab.getUnsortedPanels().add(this.unsortedPanel);
	}
	
	UnsortedPanel getThis() {
		return this;
	}	

	public JPanel getUnsortedPanel() {
		return unsortedPanel;
	}

	public DefaultComboBoxModel getSectionsComboModel() {
		return sectionsComboModel;
	}

	public void setSectionsComboModel(DefaultComboBoxModel sectionsComboModel) {
		this.sectionsComboModel = sectionsComboModel;
	}

	public JComboBox getSectionCombo() {
		return sectionCombo;
	}

	public void setSectionCombo(JComboBox sectionCombo) {
		this.sectionCombo = sectionCombo;
	}
}

class MyJPanel extends JPanel {
	int width;
	int height;

	public MyJPanel() {
		super();
	}

	public MyJPanel(int wide, int high) {
		width = wide;
		height = high;
		setPreferredSize(new Dimension(width, height));
	}

	public void increaseHeight(int higher) {
		height += higher;
		setPreferredSize(new Dimension(width, height));
	}

	public void decreaseHeight(int lower) {
		height -= lower;
		setPreferredSize(new Dimension(width, height));
	}

	public void switchPanelOrder(int i) {
		// panel is erased from index i and is added in index i-1 (which is
		// enough for the switching)
		JPanel p1 = (JPanel) this.getComponent(i);
		this.remove(this.getComponent(i));
		this.add(p1, i - 1);

		this.validate();
		this.repaint();
	}
}

class MyOwnFocusTraversalPolicy extends FocusTraversalPolicy {
	public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
		return null;
	}

	public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
		return null;
	}

	public Component getDefaultComponent(Container focusCycleRoot) {
		return null;
	}

	public Component getLastComponent(Container focusCycleRoot) {
		return null;
	}

	public Component getFirstComponent(Container focusCycleRoot) {
		return null;
	}
}