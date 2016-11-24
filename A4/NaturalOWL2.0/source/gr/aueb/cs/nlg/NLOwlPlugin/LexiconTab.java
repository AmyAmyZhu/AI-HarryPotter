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

import gr.aueb.cs.nlg.NLFiles.LexEntry;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryNounGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryAdjectiveGR;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbEN;
import gr.aueb.cs.nlg.NLFiles.LexEntryVerbGR;
import gr.aueb.cs.nlg.NLFiles.EntryList;
import gr.aueb.cs.nlg.NLFiles.AdjectiveEntryList;
import gr.aueb.cs.nlg.NLFiles.NounEntryList;
import gr.aueb.cs.nlg.NLFiles.VerbEntryList;

import gr.aueb.cs.nlg.Utils.XmlMsgs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.semanticweb.owlapi.model.OWLEntity;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LexiconTab extends NaturalOWLTab implements ActionListener,
		ItemListener, DocumentListener {
	private static final long serialVersionUID = 8268241587271333587L;

	private OWLModelManagerListener modelListener;

	JPanel panelSelect;
	JPanel panelTree;
	JPanel panelMain;
	JPanel panelVerb;
	JPanel panelNoun;
	JPanel panelAdjective;
	JPanel panelTense;
	JPanel panelVerbCard;
	JPanel panelVerbVoiceCard;
	JPanel panelVerbActiveTenseCard;
	JPanel panelVerbPassiveTenseCard;
	JPanel panelNounCard;
	JPanel panelAdjectiveCard;
	JPanel panelAdjectiveGenderCard;
	JPanel nounCardEn;
	JPanel nounCardGr;
	JPanel nounCardEnA;
	JPanel nounCardEnB;
	JPanel nounCardEnTop;
	JPanel nounCardGrA;
	JPanel nounCardGrB;
	JPanel nounCardGrTop;
	JPanel nounCardsEn;
	JPanel nounCardsGr;

	JComboBox<String> type;
	JComboBox<String> verbLanguage;
	JComboBox<String> voice;
	JComboBox<String> activeTense;
	JComboBox<String> passiveTense;
	JComboBox<String> nounLanguage;
	JComboBox<String> adjectiveLanguage;
	JComboBox<String> adjectiveGender;
	JLabel typeLabel;
	JLabel tenseLabel;
	JLabel languageLabel;
	JLabel voiceLabel;
	JLabel numberLabel;
	JLabel genderLabel;

	JButton nounGrGenButton;

	String selectedLanguage = "English";
	String selectedGender = "Masculine";
	String selectedVoice = "Active";
	String selectedActiveTense = "Simple Present";
	String selectedPassiveTense = "Simple Present";

	// ADJECTIVE VARIABLES

	LexEntryAdjectiveEN adjectiveEN = null;
	LexEntryAdjectiveGR adjectiveGR = null;

	JTextField adjEnTextfield;

	JTextField singMascNomAdjGrTextfield, singMascGenAdjGrTextfield,
			singMascAccAdjGrTextfield, plurMascNomAdjGrTextfield,
			plurMascGenAdjGrTextfield, plurMascAccAdjGrTextfield;
	JTextField singFemNomAdjGrTextfield, singFemGenAdjGrTextfield,
			singFemAccAdjGrTextfield, plurFemNomAdjGrTextfield,
			plurFemGenAdjGrTextfield, plurFemAccAdjGrTextfield;
	JTextField singNeutNomAdjGrTextfield, singNeutGenAdjGrTextfield,
			singNeutAccAdjGrTextfield, plurNeutNomAdjGrTextfield,
			plurNeutGenAdjGrTextfield, plurNeutAccAdjGrTextfield;

	// NOUN VARIABLES

	LexEntryNounEN nounEN = null;
	LexEntryNounGR nounGR = null;

	JRadioButton mascEnNounBut, femEnNounBut, mascFemEnNounBut, neutEnNounBut;
	JRadioButton bothEnNounBut, singEnNounBut, plEnNounBut;

	JTextField singEnNounTextfield, plurEnNounTextfield;

	JRadioButton mascGrNounBut, femGrNounBut, mascFemGrNounBut, neutGrNounBut;
	JRadioButton bothGrNounBut, singGrNounBut, plGrNounBut;

	JTextField singNomGrNounTextfield, singGenGrNounTextfield,
			singAccGrNounTextfield, plurNomGrNounTextfield,
			plurGenGrNounTextfield, plurAccGrNounTextfield;

	// VERB VARIABLES

	LexEntryVerbEN verbEN = null;
	LexEntryVerbGR verbGR = null;

	JTextField baseFormEnVerbTextfield, simPres3rdSingEnVerbTextfield,
			presPartEnVerbTextfield, simPastEnVerbTextfield,
			pastPartEnVerbTextfield;

	JTextField actSimPres1stSingGrVerbTextfield,
			actSimPres2ndSingGrVerbTextfield, actSimPres3rdSingGrVerbTextfield,
			actSimPres1stPlurGrVerbTextfield, actSimPres2ndPlurGrVerbTextfield,
			actSimPres3rdPlurGrVerbTextfield;
	JTextField actSimPast1stSingGrVerbTextfield,
			actSimPast2ndSingGrVerbTextfield, actSimPast3rdSingGrVerbTextfield,
			actSimPast1stPlurGrVerbTextfield, actSimPast2ndPlurGrVerbTextfield,
			actSimPast3rdPlurGrVerbTextfield;
	JTextField actPastCont1stSingGrVerbTextfield,
			actPastCont2ndSingGrVerbTextfield,
			actPastCont3rdSingGrVerbTextfield,
			actPastCont1stPlurGrVerbTextfield,
			actPastCont2ndPlurGrVerbTextfield,
			actPastCont3rdPlurGrVerbTextfield;
	JTextField actSimpFut1stSingGrVerbTextfield,
			actSimpFut2ndSingGrVerbTextfield, actSimpFut3rdSingGrVerbTextfield,
			actSimpFut1stPlurGrVerbTextfield, actSimpFut2ndPlurGrVerbTextfield,
			actSimpFut3rdPlurGrVerbTextfield;
	JTextField actInfGrVerbTextfield, actPartGrVerbTextfield;

	JTextField pasSimPres1stSingGrVerbTextfield,
			pasSimPres2ndSingGrVerbTextfield, pasSimPres3rdSingGrVerbTextfield,
			pasSimPres1stPlurGrVerbTextfield, pasSimPres2ndPlurGrVerbTextfield,
			pasSimPres3rdPlurGrVerbTextfield;
	JTextField pasSimPast1stSingGrVerbTextfield,
			pasSimPast2ndSingGrVerbTextfield, pasSimPast3rdSingGrVerbTextfield,
			pasSimPast1stPlurGrVerbTextfield, pasSimPast2ndPlurGrVerbTextfield,
			pasSimPast3rdPlurGrVerbTextfield;
	JTextField pasPastCont1stSingGrVerbTextfield,
			pasPastCont2ndSingGrVerbTextfield,
			pasPastCont3rdSingGrVerbTextfield,
			pasPastCont1stPlurGrVerbTextfield,
			pasPastCont2ndPlurGrVerbTextfield,
			pasPastCont3rdPlurGrVerbTextfield;
	JTextField pasSimpFut1stSingGrVerbTextfield,
			pasSimpFut2ndSingGrVerbTextfield, pasSimpFut3rdSingGrVerbTextfield,
			pasSimpFut1stPlurGrVerbTextfield, pasSimpFut2ndPlurGrVerbTextfield,
			pasSimpFut3rdPlurGrVerbTextfield;
	JTextField pasInfGrVerbTextfield, pasPartGrVerbTextfield;

	@Override
	protected void initialiseOWLView() throws Exception {
		lexiconSelectionModel.addListener(new OWLSelectionModelListener() {
			public void selectionChanged() throws Exception {
				OWLEntity selected = lexiconSelectionModel.getSelectedEntity();

				EntryList entryList = LQM.getEntryList(selected.getIRI());

				if (entryList != null) {
					showLexiconEntry(entryList);
				}
				validate();
				repaint();
			}
		});

		setLayout(new BorderLayout(10, 10));

		panelMain = new JPanel();
		panelMain.setLayout(new FlowLayout(FlowLayout.LEFT));

		JScrollPane scroll = new JScrollPane(panelMain);
		scroll.setViewportView(panelMain);

		add("Center", scroll);
	}

	public void viewMainSubpanel(String type) {
		if (type.compareTo("Verb") == 0) {
			panelMain.removeAll();
			showVerbPanel();
			repaint();
		} else if (type.compareTo("Noun") == 0) {
			panelMain.removeAll();
			showNounPanel();
			repaint();
		} else if (type.compareTo("Adjective") == 0) {
			panelMain.removeAll();
			showAdjectivePanel();
			repaint();
		}
	}

	public void showAdjectivePanel() {
		panelSelect = new JPanel();
		panelSelect.setLayout(new FlowLayout(FlowLayout.LEADING));
		String[] languages = { "English", "Greek" };
		adjectiveLanguage = new JComboBox<String>(languages);
		adjectiveLanguage.setSelectedItem(selectedLanguage);
		adjectiveLanguage.addItemListener(this);
		languageLabel = new JLabel("Language: ");

		panelAdjective = new JPanel();
		panelAdjective
				.setLayout(new BoxLayout(panelAdjective, BoxLayout.Y_AXIS));
		panelAdjectiveCard = new JPanel();
		panelAdjectiveCard.setLayout(new CardLayout());

		adjectiveDynamicPanels();

		panelSelect.add(languageLabel);
		panelSelect.add(adjectiveLanguage);

		panelAdjective.add(panelSelect);
		panelAdjective.add(panelAdjectiveCard);

		panelMain.add(panelAdjective);

		validate();
		repaint();
	}

	public void adjectiveDynamicPanels() {

		// JPanel
		JPanel card1 = new JPanel();
		JPanel card2 = new JPanel();

		card1.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label1e = new JLabel("Adjective");
		adjEnTextfield = new JTextField(12);
		adjEnTextfield.getDocument().putProperty("name", "adjEn");

		card1.add(label1e);
		card1.add(adjEnTextfield);

		adjEnTextfield.setText(adjectiveEN.get_form());

		// /////////////////////////////
		card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));
		JPanel card2top = new JPanel();
		card2top.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelAdjectiveGenderCard = new JPanel();
		panelAdjectiveGenderCard.setLayout(new CardLayout());

		String[] gend = { "Masculine", "Feminine", "Neuter" };
		adjectiveGender = new JComboBox<String>(gend);
		adjectiveGender.setSelectedItem(selectedGender);
		adjectiveGender.addItemListener(this);
		genderLabel = new JLabel("Gender: ");

		card2top.add(genderLabel);
		card2top.add(adjectiveGender);

		// masculine
		JPanel card2masc = new JPanel();
		JPanel mascLeft = new JPanel();
		JPanel mascRight = new JPanel();

		mascLeft.setLayout(new GridLayout(3, 1));
		mascRight.setLayout(new GridLayout(3, 1));

		JLabel label1m = new JLabel("Singular Nominative");
		singMascNomAdjGrTextfield = new JTextField(12);
		singMascNomAdjGrTextfield.getDocument().putProperty("name",
				"singMascNomAdjGr");
		JLabel label2m = new JLabel("Singular Genitive");
		singMascGenAdjGrTextfield = new JTextField(12);
		singMascGenAdjGrTextfield.getDocument().putProperty("name",
				"singMascGenAdjGr");
		JLabel label3m = new JLabel("Singular Accusative");
		singMascAccAdjGrTextfield = new JTextField(12);
		singMascAccAdjGrTextfield.getDocument().putProperty("name",
				"singMascAccAdjGr");
		JLabel label4m = new JLabel("Plural Nominative");
		plurMascNomAdjGrTextfield = new JTextField(12);
		plurMascNomAdjGrTextfield.getDocument().putProperty("name",
				"plurMascNomAdjGr");
		JLabel label5m = new JLabel("Plural Genitive");
		plurMascGenAdjGrTextfield = new JTextField(12);
		plurMascGenAdjGrTextfield.getDocument().putProperty("name",
				"plurMascGenAdjGr");
		JLabel label6m = new JLabel("Plural Accusative");
		plurMascAccAdjGrTextfield = new JTextField(12);
		plurMascAccAdjGrTextfield.getDocument().putProperty("name",
				"plurMascAccAdjGr");

		mascLeft.add(label1m);
		mascLeft.add(singMascNomAdjGrTextfield);
		mascLeft.add(label2m);
		mascLeft.add(singMascGenAdjGrTextfield);
		mascLeft.add(label3m);
		mascLeft.add(singMascAccAdjGrTextfield);
		mascRight.add(label4m);
		mascRight.add(plurMascNomAdjGrTextfield);
		mascRight.add(label5m);
		mascRight.add(plurMascGenAdjGrTextfield);
		mascRight.add(label6m);
		mascRight.add(plurMascAccAdjGrTextfield);

		card2masc.setLayout(new FlowLayout(FlowLayout.LEFT));
		card2masc.add(mascLeft);
		card2masc.add(mascRight);

		singMascNomAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR,
				XmlMsgs.NOMINATIVE_TAG));
		singMascGenAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR,
				XmlMsgs.GENITIVE_TAG));
		singMascAccAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR,
				XmlMsgs.ACCUSATIVE_TAG));
		plurMascNomAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL,
				XmlMsgs.NOMINATIVE_TAG));
		plurMascGenAdjGrTextfield
				.setText(adjectiveGR.get(XmlMsgs.GENDER_MASCULINE,
						XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
		plurMascAccAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL,
				XmlMsgs.ACCUSATIVE_TAG));

		// feminine
		JPanel card2fem = new JPanel();
		JPanel femLeft = new JPanel();
		JPanel femRight = new JPanel();

		femLeft.setLayout(new GridLayout(3, 2));
		femRight.setLayout(new GridLayout(3, 2));

		JLabel label1f = new JLabel("Singular Nominative");
		singFemNomAdjGrTextfield = new JTextField(12);
		singFemNomAdjGrTextfield.getDocument().putProperty("name",
				"singFemNomAdjGr");
		JLabel label2f = new JLabel("Singular Genitive");
		singFemGenAdjGrTextfield = new JTextField(12);
		singFemGenAdjGrTextfield.getDocument().putProperty("name",
				"singFemGenAdjGr");
		JLabel label3f = new JLabel("Singular Accusative");
		singFemAccAdjGrTextfield = new JTextField(12);
		singFemAccAdjGrTextfield.getDocument().putProperty("name",
				"singFemAccAdjGr");
		JLabel label4f = new JLabel("Plural Nominative");
		plurFemNomAdjGrTextfield = new JTextField(12);
		plurFemNomAdjGrTextfield.getDocument().putProperty("name",
				"plurFemNomAdjGr");
		JLabel label5f = new JLabel("Plural Genitive");
		plurFemGenAdjGrTextfield = new JTextField(12);
		plurFemGenAdjGrTextfield.getDocument().putProperty("name",
				"plurFemGenAdjGr");
		JLabel label6f = new JLabel("Plural Accusative");
		plurFemAccAdjGrTextfield = new JTextField(12);
		plurFemAccAdjGrTextfield.getDocument().putProperty("name",
				"plurFemAccAdjGr");

		femLeft.add(label1f);
		femLeft.add(singFemNomAdjGrTextfield);
		femLeft.add(label2f);
		femLeft.add(singFemGenAdjGrTextfield);
		femLeft.add(label3f);
		femLeft.add(singFemAccAdjGrTextfield);
		femRight.add(label4f);
		femRight.add(plurFemNomAdjGrTextfield);
		femRight.add(label5f);
		femRight.add(plurFemGenAdjGrTextfield);
		femRight.add(label6f);
		femRight.add(plurFemAccAdjGrTextfield);

		card2fem.setLayout(new FlowLayout(FlowLayout.LEFT));
		card2fem.add(femLeft);
		card2fem.add(femRight);

		singFemNomAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR,
				XmlMsgs.NOMINATIVE_TAG));
		singFemGenAdjGrTextfield.setText(adjectiveGR
				.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR,
						XmlMsgs.GENITIVE_TAG));
		singFemAccAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR,
				XmlMsgs.ACCUSATIVE_TAG));
		plurFemNomAdjGrTextfield.setText(adjectiveGR
				.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL,
						XmlMsgs.NOMINATIVE_TAG));
		plurFemGenAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
		plurFemAccAdjGrTextfield.setText(adjectiveGR
				.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL,
						XmlMsgs.ACCUSATIVE_TAG));

		// neuter
		JPanel card2neut = new JPanel();
		JPanel neutLeft = new JPanel();
		JPanel neutRight = new JPanel();

		neutLeft.setLayout(new GridLayout(3, 1));
		neutRight.setLayout(new GridLayout(3, 1));

		JLabel label1n = new JLabel("Singular Nominative");
		singNeutNomAdjGrTextfield = new JTextField(12);
		singNeutNomAdjGrTextfield.getDocument().putProperty("name",
				"singNeutNomAdjGr");
		JLabel label2n = new JLabel("Singular Genitive");
		singNeutGenAdjGrTextfield = new JTextField(12);
		singNeutGenAdjGrTextfield.getDocument().putProperty("name",
				"singNeutGenAdjGr");
		JLabel label3n = new JLabel("Singular Accusative");
		singNeutAccAdjGrTextfield = new JTextField(12);
		singNeutAccAdjGrTextfield.getDocument().putProperty("name",
				"singNeutAccAdjGr");
		JLabel label4n = new JLabel("Plural Nominative");
		plurNeutNomAdjGrTextfield = new JTextField(12);
		plurNeutNomAdjGrTextfield.getDocument().putProperty("name",
				"plurNeutNomAdjGr");
		JLabel label5n = new JLabel("Plural Genitive");
		plurNeutGenAdjGrTextfield = new JTextField(12);
		plurNeutGenAdjGrTextfield.getDocument().putProperty("name",
				"plurNeutGenAdjGr");
		JLabel label6n = new JLabel("Plural Accusative");
		plurNeutAccAdjGrTextfield = new JTextField(12);
		plurNeutAccAdjGrTextfield.getDocument().putProperty("name",
				"plurNeutAccAdjGr");

		neutLeft.add(label1n);
		neutLeft.add(singNeutNomAdjGrTextfield);
		neutLeft.add(label2n);
		neutLeft.add(singNeutGenAdjGrTextfield);
		neutLeft.add(label3n);
		neutLeft.add(singNeutAccAdjGrTextfield);
		neutRight.add(label4n);
		neutRight.add(plurNeutNomAdjGrTextfield);
		neutRight.add(label5n);
		neutRight.add(plurNeutGenAdjGrTextfield);
		neutRight.add(label6n);
		neutRight.add(plurNeutAccAdjGrTextfield);

		card2neut.setLayout(new FlowLayout(FlowLayout.LEFT));
		card2neut.add(neutLeft);
		card2neut.add(neutRight);

		singNeutNomAdjGrTextfield.setText(adjectiveGR
				.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR,
						XmlMsgs.NOMINATIVE_TAG));
		singNeutGenAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG));
		singNeutAccAdjGrTextfield.setText(adjectiveGR
				.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR,
						XmlMsgs.ACCUSATIVE_TAG));
		plurNeutNomAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG));
		plurNeutGenAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG));
		plurNeutAccAdjGrTextfield.setText(adjectiveGR.get(
				XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG));

		panelAdjectiveGenderCard.add(card2masc, "Masculine");
		panelAdjectiveGenderCard.add(card2fem, "Feminine");
		panelAdjectiveGenderCard.add(card2neut, "Neuter");

		card2.add(card2top);// , "North");
		card2.add(panelAdjectiveGenderCard);// , "Center");

		panelAdjectiveCard.add(card1, "English");
		panelAdjectiveCard.add(card2, "Greek");

		CardLayout cl = (CardLayout) (panelAdjectiveGenderCard.getLayout());
		cl.show(panelAdjectiveGenderCard, selectedGender);
		panelAdjectiveGenderCard.validate();

		cl = (CardLayout) (panelAdjectiveCard.getLayout());
		cl.show(panelAdjectiveCard, selectedLanguage);
		panelAdjectiveCard.validate();
	}

	public void showNounPanel() {
		panelSelect = new JPanel();
		panelSelect.setLayout(new FlowLayout(FlowLayout.LEADING));

		String[] languages = { "English", "Greek" };
		nounLanguage = new JComboBox<String>(languages);
		nounLanguage.setSelectedItem(selectedLanguage);
		nounLanguage.addItemListener(this);
		languageLabel = new JLabel("Language: ");

		panelSelect.add(languageLabel);
		panelSelect.add(nounLanguage);

		panelNoun = new JPanel();
		panelNoun.setLayout(new BoxLayout(panelNoun, BoxLayout.Y_AXIS));

		panelNounCard = new JPanel();
		panelNounCard.setLayout(new CardLayout());

		nounDynamicPanels();

		panelNoun.add(panelSelect);
		panelNoun.add(panelNounCard);

		panelMain.add(panelNoun);

		validate();
		repaint();
	}

	public void nounDynamicPanels() {
		// english
		nounCardEn = new JPanel();
		nounCardEn.setLayout(new BoxLayout(nounCardEn, BoxLayout.Y_AXIS));

		JPanel panel1Gender = new JPanel();
		panel1Gender.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panelEnNumber = new JPanel();
		panelEnNumber.setLayout(new FlowLayout(FlowLayout.LEFT));

		genderLabel = new JLabel("Gender: ");
		mascEnNounBut = new JRadioButton("Masculine");
		mascEnNounBut.setActionCommand("Masculine");
		mascEnNounBut.setSelected(true);
		mascEnNounBut.addActionListener(this);

		femEnNounBut = new JRadioButton("Feminine");
		femEnNounBut.setActionCommand("Feminine");
		femEnNounBut.addActionListener(this);

		mascFemEnNounBut = new JRadioButton("Masculine/Feminine");
		mascFemEnNounBut.setActionCommand("Masculine/Feminine");
		mascFemEnNounBut.addActionListener(this);

		neutEnNounBut = new JRadioButton("Neuter");
		neutEnNounBut.setActionCommand("Neuter");
		neutEnNounBut.addActionListener(this);

		ButtonGroup genderGroup1 = new ButtonGroup();
		// add to group
		genderGroup1.add(mascEnNounBut);
		genderGroup1.add(femEnNounBut);
		genderGroup1.add(mascFemEnNounBut);
		genderGroup1.add(neutEnNounBut);
		// add to panel
		panel1Gender.add(genderLabel);
		panel1Gender.add(mascEnNounBut);
		panel1Gender.add(femEnNounBut);
		panel1Gender.add(mascFemEnNounBut);
		panel1Gender.add(neutEnNounBut);

		numberLabel = new JLabel("Number: ");

		bothEnNounBut = new JRadioButton("Both");
		bothEnNounBut.setActionCommand("Both");
		bothEnNounBut.setSelected(true);
		bothEnNounBut.addActionListener(this);

		singEnNounBut = new JRadioButton("Only Singular");
		singEnNounBut.setActionCommand("Only Singular");
		singEnNounBut.addActionListener(this);

		plEnNounBut = new JRadioButton("Only Plural");
		plEnNounBut.setActionCommand("Only Plural");
		plEnNounBut.addActionListener(this);

		// add to group
		ButtonGroup numberGroupEn = new ButtonGroup();
		numberGroupEn.add(bothEnNounBut);
		numberGroupEn.add(singEnNounBut);
		numberGroupEn.add(plEnNounBut);
		// add to panel
		panelEnNumber.add(numberLabel);
		panelEnNumber.add(bothEnNounBut);
		panelEnNumber.add(singEnNounBut);
		panelEnNumber.add(plEnNounBut);

		nounCardEnA = new JPanel();
		nounCardEnB = new JPanel();
		nounCardEnTop = new JPanel();
		nounCardEnA.setLayout(new GridLayout(6, 1));
		nounCardEnB.setLayout(new GridLayout(6, 1));
		nounCardEnTop.setLayout(new BoxLayout(nounCardEnTop, BoxLayout.Y_AXIS));
		nounCardEnTop.add(panel1Gender);
		nounCardEnTop.add(panelEnNumber);

		JLabel label1 = new JLabel("Singular");
		singEnNounTextfield = new JTextField(12);
		singEnNounTextfield.getDocument().putProperty("name", "singEnNoun");

		JLabel label2 = new JLabel("Plural");
		plurEnNounTextfield = new JTextField(12);
		plurEnNounTextfield.getDocument().putProperty("name", "plurEnNoun");

		nounCardEnA.add(label1);
		nounCardEnA.add(singEnNounTextfield);
		nounCardEnB.add(label2);
		nounCardEnB.add(plurEnNounTextfield);

		nounCardsEn = new JPanel();
		nounCardsEn.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardsEn.add(nounCardEnA);
		nounCardsEn.add(nounCardEnB);

		nounCardEn.add(nounCardEnTop);// , "North");
		nounCardEn.add(nounCardsEn);
		// nounCard1.add(nounCard1a, "West");
		// nounCard1.add(nounCard1b, "East");

		if (nounEN.getGender().equals(LexEntry.GENDER_MASCULINE)) {
			mascEnNounBut.setSelected(true);
		} else if (nounEN.getGender().equals(LexEntry.GENDER_FEMININE)) {
			femEnNounBut.setSelected(true);
		} else if (nounEN.getGender().equals(LexEntry.GENDER_NEUTER)) {
			neutEnNounBut.setSelected(true);
		} else if (nounEN.getGender().equals(
				LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
			mascFemEnNounBut.setSelected(true);
		}

		if (nounEN.getNumber().equals(LexEntry.NUMBER_ONLY_SINGLE)) {
			singEnNounBut.setSelected(true);

			nounCardEnA.setVisible(true);
			nounCardEnB.setVisible(false);
		} else if (nounEN.getNumber().equals(LexEntry.NUMBER_ONLY_PLURAL)) {
			plEnNounBut.setSelected(true);

			nounCardEnA.setVisible(false);
			nounCardEnB.setVisible(true);
		} else if (nounEN.getNumber().equals(LexEntry.NUMBER_BOTH)) {
			bothEnNounBut.setSelected(true);

			nounCardEnA.setVisible(true);
			nounCardEnB.setVisible(true);
		}

		singEnNounTextfield.setText(nounEN.getSingular());
		plurEnNounTextfield.setText(nounEN.getPlural());

		// //////////////////////
		// greek
		nounCardGr = new JPanel();
		// nounCard2.setLayout(new BorderLayout(5,5));
		nounCardGr.setLayout(new BoxLayout(nounCardGr, BoxLayout.Y_AXIS));

		JPanel panel2Gender = new JPanel();
		panel2Gender.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel panelGrNumber = new JPanel();
		panelGrNumber.setLayout(new FlowLayout(FlowLayout.LEFT));

		genderLabel = new JLabel("Gender: ");
		mascGrNounBut = new JRadioButton("Masculine");
		mascGrNounBut.setActionCommand("Masculine");
		mascGrNounBut.setSelected(true);
		mascGrNounBut.addActionListener(this);

		femGrNounBut = new JRadioButton("Feminine");
		femGrNounBut.setActionCommand("Feminine");
		femGrNounBut.addActionListener(this);

		mascFemGrNounBut = new JRadioButton("Masculine/Feminine");
		mascFemGrNounBut.setActionCommand("Masculine/Feminine");
		mascFemGrNounBut.addActionListener(this);

		neutGrNounBut = new JRadioButton("Neuter");
		neutGrNounBut.setActionCommand("Neuter");
		neutGrNounBut.addActionListener(this);

		ButtonGroup genderGroup2 = new ButtonGroup();
		// add to group
		genderGroup2.add(mascGrNounBut);
		genderGroup2.add(femGrNounBut);
		genderGroup2.add(mascFemGrNounBut);
		genderGroup2.add(neutGrNounBut);
		// add to panel
		panel2Gender.add(genderLabel);
		panel2Gender.add(mascGrNounBut);
		panel2Gender.add(femGrNounBut);
		panel2Gender.add(mascFemGrNounBut);
		panel2Gender.add(neutGrNounBut);

		numberLabel = new JLabel("Number: ");

		bothGrNounBut = new JRadioButton("Both");
		bothGrNounBut.setActionCommand("Both");
		bothGrNounBut.setSelected(true);
		bothGrNounBut.addActionListener(this);

		singGrNounBut = new JRadioButton("Only Singular");
		singGrNounBut.setActionCommand("Only Singular");
		singGrNounBut.addActionListener(this);

		plGrNounBut = new JRadioButton("Only Plural");
		plGrNounBut.setActionCommand("Only Plural");
		plGrNounBut.addActionListener(this);

		// add to group
		ButtonGroup numberGroupGr = new ButtonGroup();
		numberGroupGr.add(bothGrNounBut);
		numberGroupGr.add(singGrNounBut);
		numberGroupGr.add(plGrNounBut);
		// add to panel
		panelGrNumber.add(numberLabel);
		panelGrNumber.add(bothGrNounBut);
		panelGrNumber.add(singGrNounBut);
		panelGrNumber.add(plGrNounBut);

		nounCardGrA = new JPanel();
		nounCardGrB = new JPanel();
		nounCardGrTop = new JPanel();
		nounCardGrA.setLayout(new GridLayout(6, 1));
		nounCardGrB.setLayout(new GridLayout(6, 1));
		nounCardGrTop.setLayout(new BoxLayout(nounCardGrTop, BoxLayout.Y_AXIS));
		nounCardGrTop.add(panel2Gender);
		nounCardGrTop.add(panelGrNumber);

		JLabel label1g = new JLabel("Singular Nominative");
		singNomGrNounTextfield = new JTextField(12);
		singNomGrNounTextfield.getDocument().putProperty("name", "singNomGr");

		JLabel label2g = new JLabel("Singular Genitive");
		singGenGrNounTextfield = new JTextField(12);
		singGenGrNounTextfield.getDocument().putProperty("name", "singGenGr");

		JLabel label3 = new JLabel("Singular Accusative");
		singAccGrNounTextfield = new JTextField(12);
		singAccGrNounTextfield.getDocument().putProperty("name", "singAccGr");

		JLabel label4 = new JLabel("Plural Nominative");
		plurNomGrNounTextfield = new JTextField(12);
		plurNomGrNounTextfield.getDocument().putProperty("name", "plurNomGr");

		JLabel label5 = new JLabel("Plural Genitive");
		plurGenGrNounTextfield = new JTextField(12);
		plurGenGrNounTextfield.getDocument().putProperty("name", "plurGenGr");

		JLabel label6 = new JLabel("Plural Accusative");
		plurAccGrNounTextfield = new JTextField(12);
		plurAccGrNounTextfield.getDocument().putProperty("name", "plurAccGr");

		nounCardGrA.add(label1g);
		nounCardGrA.add(singNomGrNounTextfield);
		nounCardGrA.add(label2g);
		nounCardGrA.add(singGenGrNounTextfield);
		nounCardGrA.add(label3);
		nounCardGrA.add(singAccGrNounTextfield);
		nounCardGrB.add(label4);
		nounCardGrB.add(plurNomGrNounTextfield);
		nounCardGrB.add(label5);
		nounCardGrB.add(plurGenGrNounTextfield);
		nounCardGrB.add(label6);
		nounCardGrB.add(plurAccGrNounTextfield);

		nounCardGr.add(nounCardGrTop);
		nounCardsGr = new JPanel();
		nounCardsGr.setLayout(new FlowLayout(FlowLayout.LEFT));
		nounCardsGr.add(nounCardGrA);
		nounCardsGr.add(nounCardGrB);
		nounCardGr.add(nounCardsGr);

		if (nounGR.getGender().equals(LexEntry.GENDER_MASCULINE)) {
			mascGrNounBut.setSelected(true);
		} else if (nounGR.getGender().equals(LexEntry.GENDER_FEMININE)) {
			femGrNounBut.setSelected(true);
		} else if (nounGR.getGender().equals(LexEntry.GENDER_NEUTER)) {
			neutGrNounBut.setSelected(true);
		} else if (nounGR.getGender().equals(
				LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
			mascFemGrNounBut.setSelected(true);
		}

		if (nounGR.getNumber().equals(LexEntry.NUMBER_ONLY_SINGLE)) {
			singGrNounBut.setSelected(true);

			nounCardGrA.setVisible(true);
			nounCardGrB.setVisible(false);
		} else if (nounGR.getNumber().equals(LexEntry.NUMBER_ONLY_PLURAL)) {
			plGrNounBut.setSelected(true);

			nounCardGrA.setVisible(false);
			nounCardGrB.setVisible(true);
		} else if (nounGR.getNumber().equals(LexEntry.NUMBER_BOTH)) {
			bothGrNounBut.setSelected(true);

			nounCardGrA.setVisible(true);
			nounCardGrB.setVisible(true);
		}

		singNomGrNounTextfield.setText(nounGR.get(XmlMsgs.NOMINATIVE_TAG,
				XmlMsgs.SINGULAR));
		singGenGrNounTextfield.setText(nounGR.get(XmlMsgs.GENITIVE_TAG,
				XmlMsgs.SINGULAR));
		singAccGrNounTextfield.setText(nounGR.get(XmlMsgs.ACCUSATIVE_TAG,
				XmlMsgs.SINGULAR));
		plurNomGrNounTextfield.setText(nounGR.get(XmlMsgs.NOMINATIVE_TAG,
				XmlMsgs.PLURAL));
		plurGenGrNounTextfield.setText(nounGR.get(XmlMsgs.GENITIVE_TAG,
				XmlMsgs.PLURAL));
		plurAccGrNounTextfield.setText(nounGR.get(XmlMsgs.ACCUSATIVE_TAG,
				XmlMsgs.PLURAL));

		panelNounCard.add(nounCardEn, "English");
		panelNounCard.add(nounCardGr, "Greek");

		CardLayout cl = (CardLayout) (panelNounCard.getLayout());
		cl.show(panelNounCard, selectedLanguage);
		panelNounCard.validate();
	}

	public void showVerbPanel() {

		String[] choices = { "English", "Greek" };
		verbLanguage = new JComboBox<String>(choices);
		verbLanguage.setSelectedItem(selectedLanguage);
		verbLanguage.addItemListener(this);
		languageLabel = new JLabel("Language: ");
		panelSelect = new JPanel();
		panelSelect.setLayout(new FlowLayout(FlowLayout.LEADING));
		panelSelect.add(languageLabel);
		panelSelect.add(verbLanguage);
		panelVerb = new JPanel();
		panelVerb.setLayout(new BoxLayout(panelVerb, BoxLayout.Y_AXIS));

		panelVerbCard = new JPanel();
		panelVerbCard.setLayout(new CardLayout());

		verbDynamicPanels();
		panelVerb.add(panelSelect);
		panelVerb.add(panelVerbCard);

		panelMain.add(panelVerb);

		validate();
		repaint();
	}

	public void verbDynamicPanels() {
		// english
		JPanel verbCard1 = new JPanel();
		verbCard1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JPanel verbCard1a = new JPanel();
		JPanel verbCard1b = new JPanel();
		verbCard1a.setLayout(new GridLayout(10, 1));
		verbCard1b.setLayout(new GridLayout(10, 1));

		JLabel label1 = new JLabel("Base Form");
		baseFormEnVerbTextfield = new JTextField(12);
		baseFormEnVerbTextfield.getDocument().putProperty("name",
				"baseFormEnVerb");
		baseFormEnVerbTextfield.setToolTipText("ex.: give");
		JLabel label2 = new JLabel("Simple Present 3rd Singular");
		simPres3rdSingEnVerbTextfield = new JTextField(12);
		simPres3rdSingEnVerbTextfield.getDocument().putProperty("name",
				"simPres3rdSingEnVerb");
		simPres3rdSingEnVerbTextfield.setToolTipText("ex.: gives");
		JLabel label3 = new JLabel("Present Participle");
		presPartEnVerbTextfield = new JTextField(12);
		presPartEnVerbTextfield.getDocument().putProperty("name",
				"presPartEnVerb");
		presPartEnVerbTextfield.setToolTipText("ex.: giving");
		JLabel label4 = new JLabel("Simple Past");
		simPastEnVerbTextfield = new JTextField(12);
		simPastEnVerbTextfield.getDocument().putProperty("name",
				"simPastEnVerb");
		simPastEnVerbTextfield.setToolTipText("ex.: gave");
		JLabel label5 = new JLabel("Past Participle");
		pastPartEnVerbTextfield = new JTextField(12);
		pastPartEnVerbTextfield.getDocument().putProperty("name",
				"pastPartEnVerb");
		pastPartEnVerbTextfield.setToolTipText("ex.: given");

		verbCard1a.add(label1);
		verbCard1a.add(baseFormEnVerbTextfield);
		verbCard1a.add(label2);
		verbCard1a.add(simPres3rdSingEnVerbTextfield);
		verbCard1a.add(label3);
		verbCard1a.add(presPartEnVerbTextfield);
		verbCard1b.add(label4);
		verbCard1b.add(simPastEnVerbTextfield);
		verbCard1b.add(label5);
		verbCard1b.add(pastPartEnVerbTextfield);

		verbCard1.add(verbCard1a);// , "West");
		verbCard1.add(verbCard1b);// , "East");

		baseFormEnVerbTextfield.setText(verbEN.getBaseForm());
		simPres3rdSingEnVerbTextfield.setText(verbEN
				.getSimplePresent3rdSingular());
		presPartEnVerbTextfield.setText(verbEN.getPresentParticiple());
		simPastEnVerbTextfield.setText(verbEN.getSimplePast());
		pastPartEnVerbTextfield.setText(verbEN.getPastParticiple());

		// //////////////////////
		// greek
		JPanel verbCard2 = new JPanel();
		JPanel panelVoice = new JPanel();
		panelVoice.setLayout(new FlowLayout(FlowLayout.LEADING));
		verbCard2.setLayout(new BorderLayout(5, 5));

		String[] voices = { "Active", "Passive" };
		voice = new JComboBox<String>(voices);
		voice.setSelectedItem(selectedVoice);
		voice.addItemListener(this);
		voiceLabel = new JLabel("Voice: ");
		panelVoice.add(voiceLabel);
		panelVoice.add(voice);
		verbCard2.add(panelVoice, "North");

		panelVerbVoiceCard = new JPanel();
		panelVerbVoiceCard.setLayout(new CardLayout());
		verbCard2.add(panelVerbVoiceCard);
		dynamicVoicePanels();
		// handleGreekVoices(voice.getSelectedItem().toString(),
		// tense.getSelectedItem().toString());

		panelVerbCard.add(verbCard1, "English");
		panelVerbCard.add(verbCard2, "Greek");

		CardLayout cl = (CardLayout) (panelVerbVoiceCard.getLayout());
		cl.show(panelVerbVoiceCard, selectedVoice);
		panelVerbVoiceCard.validate();

		cl = (CardLayout) (panelVerbCard.getLayout());
		cl.show(panelVerbCard, selectedLanguage);
		panelVerbCard.validate();
	}

	public void dynamicVoicePanels() {
		JPanel panelActive = new JPanel();
		JPanel panelPassive = new JPanel();
		panelActive.setLayout(new BorderLayout(5, 5));
		panelPassive.setLayout(new BorderLayout(5, 5));

		String[] actTenses = { "Simple Present", "Simple Past",
				"Past Continuous", "Simple Future", "Infinitive", "Participle" };
		activeTense = new JComboBox<String>(actTenses);
		activeTense.setSelectedItem(selectedActiveTense);
		activeTense.addItemListener(this);
		tenseLabel = new JLabel("Tense: ");
		JPanel panelTense = new JPanel();
		panelTense.setLayout(new FlowLayout(FlowLayout.LEADING));
		panelTense.add(tenseLabel);
		panelTense.add(activeTense);
		panelActive.add(panelTense, "North");

		panelVerbActiveTenseCard = new JPanel();
		panelVerbActiveTenseCard.setLayout(new CardLayout());

		JPanel aspr = new JPanel();
		JPanel aspr1 = new JPanel();
		JPanel aspr2 = new JPanel();
		JPanel aspr3 = new JPanel();
		JPanel aspr4 = new JPanel();
		JPanel aspr5 = new JPanel();
		JPanel aspr6 = new JPanel();
		JPanel asp = new JPanel();
		JPanel asp1 = new JPanel();
		JPanel asp2 = new JPanel();
		JPanel asp3 = new JPanel();
		JPanel asp4 = new JPanel();
		JPanel asp5 = new JPanel();
		JPanel asp6 = new JPanel();
		JPanel apc = new JPanel();
		JPanel apc1 = new JPanel();
		JPanel apc2 = new JPanel();
		JPanel apc3 = new JPanel();
		JPanel apc4 = new JPanel();
		JPanel apc5 = new JPanel();
		JPanel apc6 = new JPanel();
		JPanel asf = new JPanel();
		JPanel asf1 = new JPanel();
		JPanel asf2 = new JPanel();
		JPanel asf3 = new JPanel();
		JPanel asf4 = new JPanel();
		JPanel asf5 = new JPanel();
		JPanel asf6 = new JPanel();
		JPanel ai = new JPanel();
		JPanel ai1 = new JPanel();
		JPanel ap = new JPanel();
		JPanel ap1 = new JPanel();
		aspr.setLayout(new GridLayout(8, 0));
		aspr1.setLayout(new FlowLayout(FlowLayout.LEADING));
		aspr2.setLayout(new FlowLayout(FlowLayout.LEADING));
		aspr3.setLayout(new FlowLayout(FlowLayout.LEADING));
		aspr4.setLayout(new FlowLayout(FlowLayout.LEADING));
		aspr5.setLayout(new FlowLayout(FlowLayout.LEADING));
		aspr6.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp.setLayout(new GridLayout(8, 0));
		asp1.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp2.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp3.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp4.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp5.setLayout(new FlowLayout(FlowLayout.LEADING));
		asp6.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc.setLayout(new GridLayout(8, 0));
		apc1.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc2.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc3.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc4.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc5.setLayout(new FlowLayout(FlowLayout.LEADING));
		apc6.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf.setLayout(new GridLayout(8, 0));
		asf1.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf2.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf3.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf4.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf5.setLayout(new FlowLayout(FlowLayout.LEADING));
		asf6.setLayout(new FlowLayout(FlowLayout.LEADING));
		ai.setLayout(new GridLayout(8, 0));
		ai1.setLayout(new FlowLayout(FlowLayout.LEADING));
		ap.setLayout(new GridLayout(8, 0));
		ap1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel labelpr1s = new JLabel("Active Simple Present 1st Singular");
		actSimPres1stSingGrVerbTextfield = new JTextField(12);
		actSimPres1stSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres1stSingGrVerb");
		actSimPres1stSingGrVerbTextfield.setToolTipText("ex.: βάφω");
		JLabel labelpr2s = new JLabel("Active Simple Present 2nd Singular");
		actSimPres2ndSingGrVerbTextfield = new JTextField(12);
		actSimPres2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres2ndSingGrVerb");
		actSimPres2ndSingGrVerbTextfield.setToolTipText("ex.: βάφεις");
		JLabel labelpr3s = new JLabel("Active Simple Present 3rd Singular");
		actSimPres3rdSingGrVerbTextfield = new JTextField(12);
		actSimPres3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres3rdSingGrVerb");
		actSimPres3rdSingGrVerbTextfield.setToolTipText("ex.: βάφει");
		JLabel labelpr1p = new JLabel("Active Simple Present 1st Plural");
		actSimPres1stPlurGrVerbTextfield = new JTextField(12);
		actSimPres1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres1stPlurGrVerb");
		actSimPres1stPlurGrVerbTextfield.setToolTipText("ex.: βάφουμε");
		JLabel labelpr2p = new JLabel("Active Simple Present 2nd Plural");
		actSimPres2ndPlurGrVerbTextfield = new JTextField(12);
		actSimPres2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres2ndPlurGrVerb");
		actSimPres2ndPlurGrVerbTextfield.setToolTipText("ex.: βάφετε");
		JLabel labelpr3p = new JLabel("Active Simple Present 3rd Plural");
		actSimPres3rdPlurGrVerbTextfield = new JTextField(12);
		actSimPres3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPres3rdPlurGrVerb");
		actSimPres3rdPlurGrVerbTextfield.setToolTipText("ex.: βάφουν");
		// //////
		JLabel labelp1s = new JLabel("Active Simple Past 1st Singular");
		actSimPast1stSingGrVerbTextfield = new JTextField(12);
		actSimPast1stSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast1stSingGrVerb");
		actSimPast1stSingGrVerbTextfield.setToolTipText("ex.: έβαφα");
		JLabel labelp2s = new JLabel("Active Simple Past 2nd Singular");
		actSimPast2ndSingGrVerbTextfield = new JTextField(12);
		actSimPast2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast2ndSingGrVerb");
		actSimPast2ndSingGrVerbTextfield.setToolTipText("ex.: έβαφες");
		JLabel labelp3s = new JLabel("Active Simple Past 3rd Singular");
		actSimPast3rdSingGrVerbTextfield = new JTextField(12);
		actSimPast3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast3rdSingGrVerb");
		actSimPast3rdSingGrVerbTextfield.setToolTipText("ex.: έβαφε");
		JLabel labelp1p = new JLabel("Active Simple Past 1st Plural");
		actSimPast1stPlurGrVerbTextfield = new JTextField(12);
		actSimPast1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast1stPlurGrVerb");
		actSimPast1stPlurGrVerbTextfield.setToolTipText("ex.: βάφαμε");
		JLabel labelp2p = new JLabel("Active Simple Past 2nd Plural");
		actSimPast2ndPlurGrVerbTextfield = new JTextField(12);
		actSimPast2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast2ndPlurGrVerb");
		actSimPast2ndPlurGrVerbTextfield.setToolTipText("ex.: βάφατε");
		JLabel labelp3p = new JLabel("Active Simple Past 3rd Plural");
		actSimPast3rdPlurGrVerbTextfield = new JTextField(12);
		actSimPast3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimPast3rdPlurGrVerb");
		actSimPast3rdPlurGrVerbTextfield.setToolTipText("ex.: έβαφαν");
		// /////
		JLabel labelpc1s = new JLabel("Active Past Continuous 1st Singular");
		actPastCont1stSingGrVerbTextfield = new JTextField(12);
		actPastCont1stSingGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont1stSingGrVerb");
		actPastCont1stSingGrVerbTextfield.setToolTipText("ex.: έβαφα");
		JLabel labelpc2s = new JLabel("Active Past Continuous 2nd Singular");
		actPastCont2ndSingGrVerbTextfield = new JTextField(12);
		actPastCont2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont2ndSingGrVerb");
		actPastCont2ndSingGrVerbTextfield.setToolTipText("ex.: έβαφες");
		JLabel labelpc3s = new JLabel("Active Past Continuous 3rd Singular");
		actPastCont3rdSingGrVerbTextfield = new JTextField(12);
		actPastCont3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont3rdSingGrVerb");
		actPastCont3rdSingGrVerbTextfield.setToolTipText("ex.: έβαφε");
		JLabel labelpc1p = new JLabel("Active Past Continuous 1st Plural");
		actPastCont1stPlurGrVerbTextfield = new JTextField(12);
		actPastCont1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont1stPlurGrVerb");
		actPastCont1stPlurGrVerbTextfield.setToolTipText("ex.: βάφαμε");
		JLabel labelpc2p = new JLabel("Active Past Continuous 2nd Plural");
		actPastCont2ndPlurGrVerbTextfield = new JTextField(12);
		actPastCont2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont2ndPlurGrVerb");
		actPastCont2ndPlurGrVerbTextfield.setToolTipText("ex.: βάφατε");
		JLabel labelpc3p = new JLabel("Active Past Continuous 3rd Plural");
		actPastCont3rdPlurGrVerbTextfield = new JTextField(12);
		actPastCont3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"actPastCont3rdPlurGrVerb");
		actPastCont3rdPlurGrVerbTextfield.setToolTipText("ex.: έβαφαν");
		// /////
		JLabel labelf1s = new JLabel("Active Simple Future 1st Singular");
		JLabel labelf1sPlus = new JLabel("θα ");
		actSimpFut1stSingGrVerbTextfield = new JTextField(12);
		actSimpFut1stSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut1stSingGrVerb");
		actSimpFut1stSingGrVerbTextfield.setToolTipText("ex.: βάφω");
		JLabel labelf2s = new JLabel("Active Simple Future 2nd Singular");
		JLabel labelf2sPlus = new JLabel("θα ");
		actSimpFut2ndSingGrVerbTextfield = new JTextField(12);
		actSimpFut2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut2ndSingGrVerb");
		actSimpFut2ndSingGrVerbTextfield.setToolTipText("ex.: βάφεις");
		JLabel labelf3s = new JLabel("Active Simple Future 3rd Singular");
		JLabel labelf3sPlus = new JLabel("θα ");
		actSimpFut3rdSingGrVerbTextfield = new JTextField(12);
		actSimpFut3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut3rdSingGrVerb");
		actSimpFut3rdSingGrVerbTextfield.setToolTipText("ex.: βάφει");
		JLabel labelf1p = new JLabel("Active Simple Future 1st Plural");
		JLabel labelf1pPlus = new JLabel("θα ");
		actSimpFut1stPlurGrVerbTextfield = new JTextField(12);
		actSimpFut1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut1stPlurGrVerb");
		actSimpFut1stPlurGrVerbTextfield.setToolTipText("ex.: βάφουμε");
		JLabel labelf2p = new JLabel("Active Simple Future 2nd Plural");
		JLabel labelf2pPlus = new JLabel("θα ");
		actSimpFut2ndPlurGrVerbTextfield = new JTextField(12);
		actSimpFut2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut2ndPlurGrVerb");
		actSimpFut2ndPlurGrVerbTextfield.setToolTipText("ex.: βάφετε");
		JLabel labelf3p = new JLabel("Active Simple Future 3rd Plural");
		JLabel labelf3pPlus = new JLabel("θα ");
		actSimpFut3rdPlurGrVerbTextfield = new JTextField(12);
		actSimpFut3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"actSimpFut3rdPlurGrVerb");
		actSimpFut3rdPlurGrVerbTextfield.setToolTipText("ex.: βάφουν");

		JLabel labelai = new JLabel("Active Infinitive");
		actInfGrVerbTextfield = new JTextField(12);
		actInfGrVerbTextfield.getDocument().putProperty("name", "actInfGrVerb");
		actInfGrVerbTextfield.setToolTipText("ex.: βάφει");
		JLabel labelap = new JLabel("Active Participle");
		actPartGrVerbTextfield = new JTextField(12);
		actPartGrVerbTextfield.getDocument().putProperty("name",
				"actPartGrVerb");
		actPartGrVerbTextfield.setToolTipText("ex.: βάφοντας");

		Dimension d = labelpr1s.getPreferredSize();
		int tenseLabelWidth = 200;

		labelpr1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpr2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpr3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpr1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpr2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpr3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		labelp1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelp2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelp3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelp1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelp2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelp3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		labelpc1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpc2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpc3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpc1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpc2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelpc3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		labelf1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelf2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelf3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelf1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelf2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelf3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		labelai.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		labelap.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		aspr1.add(labelpr1s);
		aspr1.add(actSimPres1stSingGrVerbTextfield);

		aspr2.add(labelpr2s);
		aspr2.add(actSimPres2ndSingGrVerbTextfield);

		aspr3.add(labelpr3s);
		aspr3.add(actSimPres3rdSingGrVerbTextfield);

		aspr4.add(labelpr1p);
		aspr4.add(actSimPres1stPlurGrVerbTextfield);

		aspr5.add(labelpr2p);
		aspr5.add(actSimPres2ndPlurGrVerbTextfield);

		aspr6.add(labelpr3p);
		aspr6.add(actSimPres3rdPlurGrVerbTextfield);

		aspr.add(aspr1);
		aspr.add(aspr2);
		aspr.add(aspr3);
		aspr.add(aspr4);
		aspr.add(aspr5);
		aspr.add(aspr6);
		// /////////////////////////////////
		asp1.add(labelp1s);
		asp1.add(actSimPast1stSingGrVerbTextfield);

		asp2.add(labelp2s);
		asp2.add(actSimPast2ndSingGrVerbTextfield);

		asp3.add(labelp3s);
		asp3.add(actSimPast3rdSingGrVerbTextfield);

		asp4.add(labelp1p);
		asp4.add(actSimPast1stPlurGrVerbTextfield);

		asp5.add(labelp2p);
		asp5.add(actSimPast2ndPlurGrVerbTextfield);

		asp6.add(labelp3p);
		asp6.add(actSimPast3rdPlurGrVerbTextfield);

		asp.add(asp1);
		asp.add(asp2);
		asp.add(asp3);
		asp.add(asp4);
		asp.add(asp5);
		asp.add(asp6);
		// ////////////////////////////////////
		apc1.add(labelpc1s);
		apc1.add(actPastCont1stSingGrVerbTextfield);

		apc2.add(labelpc2s);
		apc2.add(actPastCont2ndSingGrVerbTextfield);

		apc3.add(labelpc3s);
		apc3.add(actPastCont3rdSingGrVerbTextfield);

		apc4.add(labelpc1p);
		apc4.add(actPastCont1stPlurGrVerbTextfield);

		apc5.add(labelpc2p);
		apc5.add(actPastCont2ndPlurGrVerbTextfield);

		apc6.add(labelpc3p);
		apc6.add(actPastCont3rdPlurGrVerbTextfield);

		apc.add(apc1);
		apc.add(apc2);
		apc.add(apc3);
		apc.add(apc4);
		apc.add(apc5);
		apc.add(apc6);
		// ///////////////////////////////////////
		asf1.add(labelf1s);
		asf1.add(labelf1sPlus);
		asf1.add(actSimpFut1stSingGrVerbTextfield);

		asf2.add(labelf2s);
		asf2.add(labelf2sPlus);
		asf2.add(actSimpFut2ndSingGrVerbTextfield);

		asf3.add(labelf3s);
		asf3.add(labelf3sPlus);
		asf3.add(actSimpFut3rdSingGrVerbTextfield);

		asf4.add(labelf1p);
		asf4.add(labelf1pPlus);
		asf4.add(actSimpFut1stPlurGrVerbTextfield);

		asf5.add(labelf2p);
		asf5.add(labelf2pPlus);
		asf5.add(actSimpFut2ndPlurGrVerbTextfield);

		asf6.add(labelf3p);
		asf6.add(labelf3pPlus);
		asf6.add(actSimpFut3rdPlurGrVerbTextfield);

		asf.add(asf1);
		asf.add(asf2);
		asf.add(asf3);
		asf.add(asf4);
		asf.add(asf5);
		asf.add(asf6);
		// ///////////////////////////////////
		ai1.add(labelai);
		ai1.add(actInfGrVerbTextfield);

		ai.add(ai1);

		ap1.add(labelap);
		ap1.add(actPartGrVerbTextfield);

		ap.add(ap1);

		panelVerbActiveTenseCard.add(aspr, "Simple Present");
		panelVerbActiveTenseCard.add(asp, "Simple Past");
		panelVerbActiveTenseCard.add(apc, "Past Continuous");
		panelVerbActiveTenseCard.add(asf, "Simple Future");
		panelVerbActiveTenseCard.add(ai, "Infinitive");
		panelVerbActiveTenseCard.add(ap, "Participle");

		panelActive.add(panelVerbActiveTenseCard, "Center");

		actSimPres1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		actSimPres2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		actSimPres3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		actSimPres1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		actSimPres2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		actSimPres3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		actSimPast1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		actSimPast2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		actSimPast3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		actSimPast1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		actSimPast2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		actSimPast3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		actPastCont1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		actPastCont2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		actPastCont3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		actPastCont1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		actPastCont2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		actPastCont3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		actSimpFut1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		actSimpFut2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		actSimpFut3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		actSimpFut1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		actSimpFut2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		actSimpFut3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		actInfGrVerbTextfield.setText(verbGR.get(XmlMsgs.ACTIVE_VOICE,
				XmlMsgs.TENSE_INFINITIVE, "", ""));
		actPartGrVerbTextfield.setText(verbGR.get(XmlMsgs.ACTIVE_VOICE,
				XmlMsgs.TENSE_PARTICIPLE, "", ""));

		String[] pasTenses = { "Simple Present", "Simple Past",
				"Past Continuous", "Simple Future", "Infinitive", "Participle" };
		passiveTense = new JComboBox<String>(pasTenses);
		passiveTense.setSelectedItem(selectedPassiveTense);
		passiveTense.addItemListener(this);
		tenseLabel = new JLabel("Tense: ");
		JPanel panelTense1 = new JPanel();
		panelTense1.setLayout(new FlowLayout(FlowLayout.LEADING));
		panelTense1.add(tenseLabel);
		panelTense1.add(passiveTense);
		panelPassive.add(panelTense1, "North");

		panelVerbPassiveTenseCard = new JPanel();
		panelVerbPassiveTenseCard.setLayout(new CardLayout());

		JPanel pspr = new JPanel();
		JPanel pspr1 = new JPanel();
		JPanel pspr2 = new JPanel();
		JPanel pspr3 = new JPanel();
		JPanel pspr4 = new JPanel();
		JPanel pspr5 = new JPanel();
		JPanel pspr6 = new JPanel();
		JPanel psp = new JPanel();
		JPanel psp1 = new JPanel();
		JPanel psp2 = new JPanel();
		JPanel psp3 = new JPanel();
		JPanel psp4 = new JPanel();
		JPanel psp5 = new JPanel();
		JPanel psp6 = new JPanel();
		JPanel ppc = new JPanel();
		JPanel ppc1 = new JPanel();
		JPanel ppc2 = new JPanel();
		JPanel ppc3 = new JPanel();
		JPanel ppc4 = new JPanel();
		JPanel ppc5 = new JPanel();
		JPanel ppc6 = new JPanel();
		JPanel psf = new JPanel();
		JPanel psf1 = new JPanel();
		JPanel psf2 = new JPanel();
		JPanel psf3 = new JPanel();
		JPanel psf4 = new JPanel();
		JPanel psf5 = new JPanel();
		JPanel psf6 = new JPanel();
		JPanel pi = new JPanel();
		JPanel pi1 = new JPanel();
		JPanel pp = new JPanel();
		JPanel pp1 = new JPanel();
		pspr.setLayout(new GridLayout(8, 0));
		pspr1.setLayout(new FlowLayout(FlowLayout.LEADING));
		pspr2.setLayout(new FlowLayout(FlowLayout.LEADING));
		pspr3.setLayout(new FlowLayout(FlowLayout.LEADING));
		pspr4.setLayout(new FlowLayout(FlowLayout.LEADING));
		pspr5.setLayout(new FlowLayout(FlowLayout.LEADING));
		pspr6.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp.setLayout(new GridLayout(8, 0));
		psp1.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp2.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp3.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp4.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp5.setLayout(new FlowLayout(FlowLayout.LEADING));
		psp6.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc.setLayout(new GridLayout(8, 0));
		ppc1.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc2.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc3.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc4.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc5.setLayout(new FlowLayout(FlowLayout.LEADING));
		ppc6.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf.setLayout(new GridLayout(8, 0));
		psf1.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf2.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf3.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf4.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf5.setLayout(new FlowLayout(FlowLayout.LEADING));
		psf6.setLayout(new FlowLayout(FlowLayout.LEADING));
		pi.setLayout(new GridLayout(8, 0));
		pi1.setLayout(new FlowLayout(FlowLayout.LEADING));
		pp.setLayout(new GridLayout(8, 0));
		pp1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel plabelpr1s = new JLabel("Passive Simple Present 1st Singular");
		pasSimPres1stSingGrVerbTextfield = new JTextField(12);
		pasSimPres1stSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres1stSingGrVerb");
		pasSimPres1stSingGrVerbTextfield.setToolTipText("ex.: βάφομαι");
		JLabel plabelpr2s = new JLabel("Passive Simple Present 2nd Singular");
		pasSimPres2ndSingGrVerbTextfield = new JTextField(12);
		pasSimPres2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres2ndSingGrVerb");
		pasSimPres2ndSingGrVerbTextfield.setToolTipText("ex.: βάφεσαι");
		JLabel plabelpr3s = new JLabel("Passive Simple Present 3rd Singular");
		pasSimPres3rdSingGrVerbTextfield = new JTextField(12);
		pasSimPres3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres3rdSingGrVerb");
		pasSimPres3rdSingGrVerbTextfield.setToolTipText("ex.: βάφεται");
		JLabel plabelpr1p = new JLabel("Passive Simple Present 1st Plural");
		pasSimPres1stPlurGrVerbTextfield = new JTextField(12);
		pasSimPres1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres1stPlurGrVerb");
		pasSimPres1stPlurGrVerbTextfield.setToolTipText("ex.: βαφόμαστε");
		JLabel plabelpr2p = new JLabel("Passive Simple Present 2nd Plural");
		pasSimPres2ndPlurGrVerbTextfield = new JTextField(12);
		pasSimPres2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres2ndPlurGrVerb");
		pasSimPres2ndPlurGrVerbTextfield.setToolTipText("ex.: βάφεστε");
		JLabel plabelpr3p = new JLabel("Passive Simple Present 3rd Plural");
		pasSimPres3rdPlurGrVerbTextfield = new JTextField(12);
		pasSimPres3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPres3rdPlurGrVerb");
		pasSimPres3rdPlurGrVerbTextfield.setToolTipText("ex.: βάφονται");
		// //////
		JLabel plabelp1s = new JLabel("Passive Simple Past 1st Singular");
		pasSimPast1stSingGrVerbTextfield = new JTextField(12);
		pasSimPast1stSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast1stSingGrVerb");
		pasSimPast1stSingGrVerbTextfield.setToolTipText("ex.: βάφτηκα");
		JLabel plabelp2s = new JLabel("Passive Simple Past 2nd Singular");
		pasSimPast2ndSingGrVerbTextfield = new JTextField(12);
		pasSimPast2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast2ndSingGrVerb");
		pasSimPast2ndSingGrVerbTextfield.setToolTipText("ex.: βάφτηκες");
		JLabel plabelp3s = new JLabel("Passive Simple Past 3rd Singular");
		pasSimPast3rdSingGrVerbTextfield = new JTextField(12);
		pasSimPast3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast3rdSingGrVerb");
		pasSimPast3rdSingGrVerbTextfield.setToolTipText("ex.: βάφτηκε");
		JLabel plabelp1p = new JLabel("Passive Simple Past 1st Plural");
		pasSimPast1stPlurGrVerbTextfield = new JTextField(12);
		pasSimPast1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast1stPlurGrVerb");
		pasSimPast1stPlurGrVerbTextfield.setToolTipText("ex.: βαφτήκαμε");
		JLabel plabelp2p = new JLabel("Passive Simple Past 2nd Plural");
		pasSimPast2ndPlurGrVerbTextfield = new JTextField(12);
		pasSimPast2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast2ndPlurGrVerb");
		pasSimPast2ndPlurGrVerbTextfield.setToolTipText("ex.: βαφτήκατε");
		JLabel plabelp3p = new JLabel("Passive Simple Past 3rd Plural");
		pasSimPast3rdPlurGrVerbTextfield = new JTextField(12);
		pasSimPast3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimPast3rdPlurGrVerb");
		pasSimPast3rdPlurGrVerbTextfield.setToolTipText("ex.: βάφτηκαν");
		// /////
		JLabel plabelpc1s = new JLabel("Passive Past Continuous 1st Singular");
		pasPastCont1stSingGrVerbTextfield = new JTextField(12);
		pasPastCont1stSingGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont1stSingGrVerb");
		pasPastCont1stSingGrVerbTextfield.setToolTipText("ex.: βαφόμουν");
		JLabel plabelpc2s = new JLabel("Passive Past Continuous 2nd Singular");
		pasPastCont2ndSingGrVerbTextfield = new JTextField(12);
		pasPastCont2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont2ndSingGrVerb");
		pasPastCont2ndSingGrVerbTextfield.setToolTipText("ex.: βαφόσουν");
		JLabel plabelpc3s = new JLabel("Passive Past Continuous 3rd Singular");
		pasPastCont3rdSingGrVerbTextfield = new JTextField(12);
		pasPastCont3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont3rdSingGrVerb");
		pasPastCont3rdSingGrVerbTextfield.setToolTipText("ex.: βαφόταν");
		JLabel plabelpc1p = new JLabel("Passive Past Continuous 1st Plural");
		pasPastCont1stPlurGrVerbTextfield = new JTextField(12);
		pasPastCont1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont1stPlurGrVerb");
		pasPastCont1stPlurGrVerbTextfield.setToolTipText("ex.: βαφόμασταν");
		JLabel plabelpc2p = new JLabel("Passive Past Continuous 2nd Plural");
		pasPastCont2ndPlurGrVerbTextfield = new JTextField(12);
		pasPastCont2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont2ndPlurGrVerb");
		pasPastCont2ndPlurGrVerbTextfield.setToolTipText("ex.: βαφόσασταν");
		JLabel plabelpc3p = new JLabel("Passive Past Continuous 3rd Plural");
		pasPastCont3rdPlurGrVerbTextfield = new JTextField(12);
		pasPastCont3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasPastCont3rdPlurGrVerb");
		pasPastCont3rdPlurGrVerbTextfield.setToolTipText("ex.: βάφονταν");
		// /////
		JLabel plabelf1s = new JLabel("Passive Simple Future 1st Singular");
		JLabel plabelf1sPlus = new JLabel("θα ");
		pasSimpFut1stSingGrVerbTextfield = new JTextField(12);
		pasSimpFut1stSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut1stSingGrVerb");
		pasSimpFut1stSingGrVerbTextfield.setToolTipText("ex.: βαφτώ");
		JLabel plabelf2s = new JLabel("Passive Simple Future 2nd Singular");
		JLabel plabelf2sPlus = new JLabel("θα ");
		pasSimpFut2ndSingGrVerbTextfield = new JTextField(12);
		pasSimpFut2ndSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut2ndSingGrVerb");
		pasSimpFut2ndSingGrVerbTextfield.setToolTipText("ex.: βαφτείς");
		JLabel plabelf3s = new JLabel("Passive Simple Future 3rd Singular");
		JLabel plabelf3sPlus = new JLabel("θα ");
		pasSimpFut3rdSingGrVerbTextfield = new JTextField(12);
		pasSimpFut3rdSingGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut3rdSingGrVerb");
		pasSimpFut3rdSingGrVerbTextfield.setToolTipText("ex.: βαφτεί");
		JLabel plabelf1p = new JLabel("Passive Simple Future 1st Plural");
		JLabel plabelf1pPlus = new JLabel("θα ");
		pasSimpFut1stPlurGrVerbTextfield = new JTextField(12);
		pasSimpFut1stPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut1stPlurGrVerb");
		pasSimpFut1stPlurGrVerbTextfield.setToolTipText("ex.: βαφτούμε");
		JLabel plabelf2p = new JLabel("Passive Simple Future 2nd Plural");
		JLabel plabelf2pPlus = new JLabel("θα ");
		pasSimpFut2ndPlurGrVerbTextfield = new JTextField(12);
		pasSimpFut2ndPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut2ndPlurGrVerb");
		pasSimpFut2ndPlurGrVerbTextfield.setToolTipText("ex.: βαφτείτε");
		JLabel plabelf3p = new JLabel("Passive Simple Future 3rd Plural");
		JLabel plabelf3pPlus = new JLabel("θα ");
		pasSimpFut3rdPlurGrVerbTextfield = new JTextField(12);
		pasSimpFut3rdPlurGrVerbTextfield.getDocument().putProperty("name",
				"pasSimpFut3rdPlurGrVerb");
		pasSimpFut3rdPlurGrVerbTextfield.setToolTipText("ex.: βαφτούν");

		JLabel plabelai = new JLabel("Passive Infinitive");
		pasInfGrVerbTextfield = new JTextField(12);
		pasInfGrVerbTextfield.getDocument().putProperty("name", "pasInfGrVerb");
		pasInfGrVerbTextfield.setToolTipText("ex.: βαφτεί");
		JLabel plabelap = new JLabel("Passive Participle");
		pasPartGrVerbTextfield = new JTextField(12);
		pasPartGrVerbTextfield.getDocument().putProperty("name",
				"pasPartGrVerb");
		pasPartGrVerbTextfield.setToolTipText("ex.: βαμμένος");

		d = plabelpr1s.getPreferredSize();
		plabelpr1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpr2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpr3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpr1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpr2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpr3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		plabelp1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelp2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelp3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelp1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelp2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelp3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		plabelpc1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpc2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpc3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpc1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpc2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelpc3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		plabelf1s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelf2s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelf3s.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelf1p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelf2p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelf3p.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		plabelai.setPreferredSize(new Dimension(tenseLabelWidth, d.height));
		plabelap.setPreferredSize(new Dimension(tenseLabelWidth, d.height));

		pspr1.add(plabelpr1s);
		pspr1.add(pasSimPres1stSingGrVerbTextfield);

		pspr2.add(plabelpr2s);
		pspr2.add(pasSimPres2ndSingGrVerbTextfield);

		pspr3.add(plabelpr3s);
		pspr3.add(pasSimPres3rdSingGrVerbTextfield);

		pspr4.add(plabelpr1p);
		pspr4.add(pasSimPres1stPlurGrVerbTextfield);

		pspr5.add(plabelpr2p);
		pspr5.add(pasSimPres2ndPlurGrVerbTextfield);

		pspr6.add(plabelpr3p);
		pspr6.add(pasSimPres3rdPlurGrVerbTextfield);

		pspr.add(pspr1);
		pspr.add(pspr2);
		pspr.add(pspr3);
		pspr.add(pspr4);
		pspr.add(pspr5);
		pspr.add(pspr6);
		// /////////////////////////////////
		psp1.add(plabelp1s);
		psp1.add(pasSimPast1stSingGrVerbTextfield);

		psp2.add(plabelp2s);
		psp2.add(pasSimPast2ndSingGrVerbTextfield);

		psp3.add(plabelp3s);
		psp3.add(pasSimPast3rdSingGrVerbTextfield);

		psp4.add(plabelp1p);
		psp4.add(pasSimPast1stPlurGrVerbTextfield);

		psp5.add(plabelp2p);
		psp5.add(pasSimPast2ndPlurGrVerbTextfield);

		psp6.add(plabelp3p);
		psp6.add(pasSimPast3rdPlurGrVerbTextfield);

		psp.add(psp1);
		psp.add(psp2);
		psp.add(psp3);
		psp.add(psp4);
		psp.add(psp5);
		psp.add(psp6);
		// ////////////////////////////////////
		ppc1.add(plabelpc1s);
		ppc1.add(pasPastCont1stSingGrVerbTextfield);

		ppc2.add(plabelpc2s);
		ppc2.add(pasPastCont2ndSingGrVerbTextfield);

		ppc3.add(plabelpc3s);
		ppc3.add(pasPastCont3rdSingGrVerbTextfield);

		ppc4.add(plabelpc1p);
		ppc4.add(pasPastCont1stPlurGrVerbTextfield);

		ppc5.add(plabelpc2p);
		ppc5.add(pasPastCont2ndPlurGrVerbTextfield);

		ppc6.add(plabelpc3p);
		ppc6.add(pasPastCont3rdPlurGrVerbTextfield);

		ppc.add(ppc1);
		ppc.add(ppc2);
		ppc.add(ppc3);
		ppc.add(ppc4);
		ppc.add(ppc5);
		ppc.add(ppc6);
		// ///////////////////////////////////////
		psf1.add(plabelf1s);
		psf1.add(plabelf1sPlus);
		psf1.add(pasSimpFut1stSingGrVerbTextfield);

		psf2.add(plabelf2s);
		psf2.add(plabelf2sPlus);
		psf2.add(pasSimpFut2ndSingGrVerbTextfield);

		psf3.add(plabelf3s);
		psf3.add(plabelf3sPlus);
		psf3.add(pasSimpFut3rdSingGrVerbTextfield);

		psf4.add(plabelf1p);
		psf4.add(plabelf1pPlus);
		psf4.add(pasSimpFut1stPlurGrVerbTextfield);

		psf5.add(plabelf2p);
		psf5.add(plabelf2pPlus);
		psf5.add(pasSimpFut2ndPlurGrVerbTextfield);

		psf6.add(plabelf3p);
		psf6.add(plabelf3pPlus);
		psf6.add(pasSimpFut3rdPlurGrVerbTextfield);

		psf.add(psf1);
		psf.add(psf2);
		psf.add(psf3);
		psf.add(psf4);
		psf.add(psf5);
		psf.add(psf6);
		// ///////////////////////////////////
		pi1.add(plabelai);
		pi1.add(pasInfGrVerbTextfield);

		pi.add(pi1);

		pp1.add(plabelap);
		pp1.add(pasPartGrVerbTextfield);

		pp.add(pp1);

		panelVerbPassiveTenseCard.add(pspr, "Simple Present");
		panelVerbPassiveTenseCard.add(psp, "Simple Past");
		panelVerbPassiveTenseCard.add(ppc, "Past Continuous");
		panelVerbPassiveTenseCard.add(psf, "Simple Future");
		panelVerbPassiveTenseCard.add(pi, "Infinitive");
		panelVerbPassiveTenseCard.add(pp, "Participle");

		panelPassive.add(panelVerbPassiveTenseCard, "Center");

		pasSimPres1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		pasSimPres2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		pasSimPres3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		pasSimPres1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		pasSimPres2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		pasSimPres3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		pasSimPast1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		pasSimPast2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		pasSimPast3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		pasSimPast1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		pasSimPast2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		pasSimPast3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		pasPastCont1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		pasPastCont2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		pasPastCont3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		pasPastCont1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		pasPastCont2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		pasPastCont3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		pasSimpFut1stSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR));
		pasSimpFut2ndSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR));
		pasSimpFut3rdSingGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR));
		pasSimpFut1stPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL));
		pasSimpFut2ndPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL));
		pasSimpFut3rdPlurGrVerbTextfield.setText(verbGR.get(
				XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
				XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL));

		pasInfGrVerbTextfield.setText(verbGR.get(XmlMsgs.PASSIVE_VOICE,
				XmlMsgs.TENSE_INFINITIVE, "", ""));
		pasPartGrVerbTextfield.setText(verbGR.get(XmlMsgs.PASSIVE_VOICE,
				XmlMsgs.TENSE_PARTICIPLE, "", ""));

		panelVerbVoiceCard.add(panelActive, "Active");
		panelVerbVoiceCard.add(panelPassive, "Passive");

		CardLayout cl = (CardLayout) (panelVerbActiveTenseCard.getLayout());
		cl.show(panelVerbActiveTenseCard, selectedActiveTense);
		panelVerbActiveTenseCard.validate();

		cl = (CardLayout) (panelVerbPassiveTenseCard.getLayout());
		cl.show(panelVerbPassiveTenseCard, selectedPassiveTense);
		panelVerbPassiveTenseCard.validate();
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// NOUN NUMBER
		if (source == singEnNounBut) {
			nounCardEnA.setVisible(true);
			nounCardEnB.setVisible(false);

			nounEN.setNumber(LexEntry.NUMBER_ONLY_SINGLE);
			dirtenOntologies();
		} else if (source == singGrNounBut) {
			nounCardGrA.setVisible(true);
			nounCardGrB.setVisible(false);

			nounGR.setNumber(LexEntry.NUMBER_ONLY_SINGLE);
			dirtenOntologies();
		} else if (source == plEnNounBut) {
			nounCardEnA.setVisible(false);
			nounCardEnB.setVisible(true);

			nounEN.setNumber(LexEntry.NUMBER_ONLY_PLURAL);
			dirtenOntologies();
		} else if (source == plGrNounBut) {
			nounCardGrA.setVisible(false);
			nounCardGrB.setVisible(true);

			nounGR.setNumber(LexEntry.NUMBER_ONLY_PLURAL);
			dirtenOntologies();
		} else if (source == bothEnNounBut) {
			nounCardEnA.setVisible(true);
			nounCardEnB.setVisible(true);

			nounEN.setNumber(LexEntry.NUMBER_BOTH);
			dirtenOntologies();
		} else if (source == bothGrNounBut) {
			nounCardGrA.setVisible(true);
			nounCardGrB.setVisible(true);

			nounGR.setNumber(LexEntry.NUMBER_BOTH);
			dirtenOntologies();
		}

		// NOUN GENDER
		else if (source == mascEnNounBut) {
			nounEN.setGender(LexEntry.GENDER_MASCULINE);
			dirtenOntologies();
		} else if (source == mascGrNounBut) {
			nounGR.setGender(LexEntry.GENDER_MASCULINE);
			dirtenOntologies();
		} else if (source == femEnNounBut) {
			nounEN.setGender(LexEntry.GENDER_FEMININE);
			dirtenOntologies();
		} else if (source == femGrNounBut) {
			nounGR.setGender(LexEntry.GENDER_FEMININE);
			dirtenOntologies();
		} else if (source == mascFemEnNounBut) {
			nounEN.setGender(LexEntry.GENDER_MASCULINE_OR_FEMININE);
			dirtenOntologies();
		} else if (source == mascFemGrNounBut) {
			nounGR.setGender(LexEntry.GENDER_MASCULINE_OR_FEMININE);
			dirtenOntologies();
		} else if (source == neutEnNounBut) {
			nounEN.setGender(LexEntry.GENDER_NEUTER);
			dirtenOntologies();
		} else if (source == neutGrNounBut) {
			nounGR.setGender(LexEntry.GENDER_NEUTER);
			dirtenOntologies();
		}
		repaint();
	}

	public void insertUpdate(DocumentEvent event) {
		updateEntry(event);
	}

	public void removeUpdate(DocumentEvent event) {
		updateEntry(event);
	}

	public void changedUpdate(DocumentEvent event) {
	}

	public void updateEntry(DocumentEvent event) {
		String name = (String) event.getDocument().getProperty("name");

		// System.out.println("!!! " + name);

		// NOUN ENGLISH
		if (name.equals("singEnNoun")) {
			nounEN.setSingular(singEnNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("plurEnNoun")) {
			nounEN.setPlural(plurEnNounTextfield.getText());
			dirtenOntologies();
		}

		// NOUN GREEK
		else if (name.equals("singNomGr")) {
			nounGR.setSingularNominative(singNomGrNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("singGenGr")) {
			nounGR.setSingularGenitive(singGenGrNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("singAccGr")) {
			nounGR.setSingularAccusative(singAccGrNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("plurNomGr")) {
			nounGR.setPluralNominative(plurNomGrNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("plurGenGr")) {
			nounGR.setPluralGenitive(plurGenGrNounTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("plurAccGr")) {
			nounGR.setPluralAccusative(plurAccGrNounTextfield.getText());
			dirtenOntologies();
		}

		// ADJECTIVE ENGLISH
		else if (name.equals("adjEn")) {
			adjectiveEN.set_form(adjEnTextfield.getText());
			dirtenOntologies();
		}

		// ADJECTIVE GREEK
		else if (name.equals("singMascNomAdjGr")) {
			adjectiveGR
					.setSingularNominativeMasculine(singMascNomAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("singMascGenAdjGr")) {
			adjectiveGR
					.setSingularGenitiveMasculine(singMascGenAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("singMascAccAdjGr")) {
			adjectiveGR
					.setSingularAccusativeMasculine(singMascAccAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("plurMascNomAdjGr")) {
			adjectiveGR
					.setPluralNominativeMasculine(plurMascNomAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("plurMascGenAdjGr")) {
			adjectiveGR.setPluralGenitiveMasculine(plurMascGenAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("plurMascAccAdjGr")) {
			adjectiveGR
					.setPluralAccusativeMasculine(plurMascAccAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("singFemNomAdjGr")) {
			adjectiveGR
					.setSingularNominativeFeminine(singFemNomAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("singFemGenAdjGr")) {
			adjectiveGR.setSingularGenitiveFeminine(singFemGenAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("singFemAccAdjGr")) {
			adjectiveGR
					.setSingularAccusativeFeminine(singFemAccAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("plurFemNomAdjGr")) {
			adjectiveGR.setPluralNominativeFeminine(plurFemNomAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("plurFemGenAdjGr")) {
			adjectiveGR.setPluralGenitiveFeminine(plurFemGenAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("plurFemAccAdjGr")) {
			adjectiveGR.setPluralAccusativeFeminine(plurFemAccAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("singNeutNomAdjGr")) {
			adjectiveGR
					.setSingularNominativeNeuter(singNeutNomAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("singNeutGenAdjGr")) {
			adjectiveGR.setSingularGenitiveNeuter(singNeutGenAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("singNeutAccAdjGr")) {
			adjectiveGR
					.setSingularAccusativeNeuter(singNeutAccAdjGrTextfield
							.getText());
			dirtenOntologies();
		} else if (name.equals("plurNeutNomAdjGr")) {
			adjectiveGR.setPluralNominativeNeuter(plurNeutNomAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("plurNeutGenAdjGr")) {
			adjectiveGR.setPluralGenitiveNeuter(plurNeutGenAdjGrTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("plurNeutAccAdjGr")) {
			adjectiveGR.setPluralAccusativeNeuter(plurNeutAccAdjGrTextfield
					.getText());
			dirtenOntologies();
		}

		// VERB ENGLISH
		else if (name.equals("baseFormEnVerb")) {
			verbEN.setBaseForm(baseFormEnVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("simPres3rdSingEnVerb")) {
			verbEN.setSimplePresent3rdSingular(simPres3rdSingEnVerbTextfield
					.getText());
			dirtenOntologies();
		} else if (name.equals("presPartEnVerb")) {
			verbEN.setPresentParticiple(presPartEnVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("simPastEnVerb")) {
			verbEN.setSimplePast(simPastEnVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pastPartEnVerb")) {
			verbEN.setPastParticiple(pastPartEnVerbTextfield.getText());
			dirtenOntologies();
		}

		// VERB GREEK
		else if (name.equals("actSimPres1stSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					actSimPres1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPres2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					actSimPres2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPres3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					actSimPres3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPres1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					actSimPres1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPres2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					actSimPres2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPres3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					actSimPres3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast1stSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					actSimPast1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					actSimPast2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					actSimPast3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					actSimPast1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					actSimPast2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimPast3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					actSimPast3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont1stSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					actPastCont1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					actPastCont2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					actPastCont3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					actPastCont1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					actPastCont2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPastCont3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					actPastCont3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut1stSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					actSimpFut1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					actSimpFut2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					actSimpFut3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					actSimpFut1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					actSimpFut2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actSimpFut3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.ACTIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					actSimpFut3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actInfGrVerb")) {
			verbGR.setActiveInfinitive(actInfGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("actPartGrVerb")) {
			verbGR.setActiveParticiple(actPartGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres1stSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					pasSimPres1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					pasSimPres2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					pasSimPres3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					pasSimPres1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					pasSimPres2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPres3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PRESENT,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					pasSimPres3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast1stSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					pasSimPast1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					pasSimPast2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					pasSimPast3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					pasSimPast1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					pasSimPast2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimPast3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_PAST,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					pasSimPast3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont1stSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					pasPastCont1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					pasPastCont2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					pasPastCont3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					pasPastCont1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					pasPastCont2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPastCont3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_PAST_CONTINUOUS,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					pasPastCont3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut1stSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_1ST, XmlMsgs.SINGULAR,
					pasSimpFut1stSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut2ndSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_2ND, XmlMsgs.SINGULAR,
					pasSimpFut2ndSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut3rdSingGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_3RD, XmlMsgs.SINGULAR,
					pasSimpFut3rdSingGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut1stPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_1ST, XmlMsgs.PLURAL,
					pasSimpFut1stPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut2ndPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_2ND, XmlMsgs.PLURAL,
					pasSimpFut2ndPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasSimpFut3rdPlurGrVerb")) {
			verbGR.set(XmlMsgs.PASSIVE_VOICE, XmlMsgs.TENSE_SIMPLE_FUTURE,
					XmlMsgs.PERSON_3RD, XmlMsgs.PLURAL,
					pasSimpFut3rdPlurGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasInfGrVerb")) {
			verbGR.setPassiveInfinitive(pasInfGrVerbTextfield.getText());
			dirtenOntologies();
		} else if (name.equals("pasPartGrVerb")) {
			verbGR.setPassiveParticiple(pasPartGrVerbTextfield.getText());
			dirtenOntologies();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (nounLanguage != null) {
				if (e.getSource().hashCode() == nounLanguage.hashCode()) {
					selectedLanguage = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelNounCard.getLayout());
					cl.show(panelNounCard, selectedLanguage);
					panelNounCard.validate();
				}
			}
			if (adjectiveLanguage != null) {
				if (e.getSource().hashCode() == adjectiveLanguage.hashCode()) {
					selectedLanguage = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelAdjectiveCard
							.getLayout());
					cl.show(panelAdjectiveCard, selectedLanguage);
					panelAdjectiveCard.validate();
				}
			}
			if (adjectiveGender != null) {
				if (e.getSource().hashCode() == adjectiveGender.hashCode()) {
					selectedGender = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelAdjectiveGenderCard
							.getLayout());
					cl.show(panelAdjectiveGenderCard, selectedGender);
					panelAdjectiveGenderCard.validate();
				}
			}
			if (verbLanguage != null) {
				if (e.getSource().hashCode() == verbLanguage.hashCode()) {
					selectedLanguage = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelVerbCard.getLayout());
					cl.show(panelVerbCard, selectedLanguage);
					panelVerbCard.validate();
				}
			}
			if (voice != null) {
				if (e.getSource().hashCode() == voice.hashCode()) {
					selectedVoice = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelVerbVoiceCard
							.getLayout());
					cl.show(panelVerbVoiceCard, selectedVoice);
					panelVerbVoiceCard.validate();
				}
			}
			if (activeTense != null) {
				if (e.getSource().hashCode() == activeTense.hashCode()) {
					selectedActiveTense = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelVerbActiveTenseCard
							.getLayout());
					cl.show(panelVerbActiveTenseCard, selectedActiveTense);
					panelVerbActiveTenseCard.validate();
				}
			}
			if (passiveTense != null) {
				if (e.getSource().hashCode() == passiveTense.hashCode()) {
					selectedPassiveTense = (String) e.getItem();

					CardLayout cl = (CardLayout) (panelVerbPassiveTenseCard
							.getLayout());
					cl.show(panelVerbPassiveTenseCard, selectedPassiveTense);
					panelVerbPassiveTenseCard.validate();
				}
			}
			if (e.getSource().equals(type)) {
				viewMainSubpanel(type.getSelectedItem().toString());
			}
		}
	}

	private void showLexiconEntry(EntryList entry) {
		if (entry instanceof NounEntryList) {
			adjectiveEN = null;
			adjectiveGR = null;
			verbEN = null;
			verbGR = null;
			nounEN = (LexEntryNounEN) ((NounEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.ENGLISH);
			nounGR = (LexEntryNounGR) ((NounEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.GREEK);

			panelMain.removeAll();
			showNounPanel();
			repaint();

			removeListeners();

			singEnNounTextfield.getDocument().addDocumentListener(this);
			plurEnNounTextfield.getDocument().addDocumentListener(this);
			singNomGrNounTextfield.getDocument().addDocumentListener(this);
			singGenGrNounTextfield.getDocument().addDocumentListener(this);
			singAccGrNounTextfield.getDocument().addDocumentListener(this);
			plurNomGrNounTextfield.getDocument().addDocumentListener(this);
			plurGenGrNounTextfield.getDocument().addDocumentListener(this);
			plurAccGrNounTextfield.getDocument().addDocumentListener(this);
		} else if (entry instanceof AdjectiveEntryList) {
			nounEN = null;
			nounGR = null;
			verbEN = null;
			verbGR = null;
			adjectiveEN = (LexEntryAdjectiveEN) ((AdjectiveEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.ENGLISH);
			adjectiveGR = (LexEntryAdjectiveGR) ((AdjectiveEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.GREEK);

			panelMain.removeAll();
			showAdjectivePanel();
			repaint();

			removeListeners();

			adjEnTextfield.getDocument().addDocumentListener(this);
			singMascNomAdjGrTextfield.getDocument().addDocumentListener(this);
			singMascGenAdjGrTextfield.getDocument().addDocumentListener(this);
			singMascAccAdjGrTextfield.getDocument().addDocumentListener(this);
			plurMascNomAdjGrTextfield.getDocument().addDocumentListener(this);
			plurMascGenAdjGrTextfield.getDocument().addDocumentListener(this);
			plurMascAccAdjGrTextfield.getDocument().addDocumentListener(this);
			singFemNomAdjGrTextfield.getDocument().addDocumentListener(this);
			singFemGenAdjGrTextfield.getDocument().addDocumentListener(this);
			singFemAccAdjGrTextfield.getDocument().addDocumentListener(this);
			plurFemNomAdjGrTextfield.getDocument().addDocumentListener(this);
			plurFemGenAdjGrTextfield.getDocument().addDocumentListener(this);
			plurFemAccAdjGrTextfield.getDocument().addDocumentListener(this);
			singNeutNomAdjGrTextfield.getDocument().addDocumentListener(this);
			singNeutGenAdjGrTextfield.getDocument().addDocumentListener(this);
			singNeutAccAdjGrTextfield.getDocument().addDocumentListener(this);
			plurNeutNomAdjGrTextfield.getDocument().addDocumentListener(this);
			plurNeutGenAdjGrTextfield.getDocument().addDocumentListener(this);
			plurNeutAccAdjGrTextfield.getDocument().addDocumentListener(this);
		}

		else if (entry instanceof VerbEntryList) {
			nounEN = null;
			nounGR = null;
			adjectiveEN = null;
			adjectiveGR = null;
			verbEN = (LexEntryVerbEN) ((VerbEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.ENGLISH);
			verbGR = (LexEntryVerbGR) ((VerbEntryList) entry)
					.getEntry(gr.aueb.cs.nlg.Languages.Languages.GREEK);

			panelMain.removeAll();
			showVerbPanel();
			repaint();

			removeListeners();

			baseFormEnVerbTextfield.getDocument().addDocumentListener(this);
			simPres3rdSingEnVerbTextfield.getDocument().addDocumentListener(
					this);
			presPartEnVerbTextfield.getDocument().addDocumentListener(this);
			simPastEnVerbTextfield.getDocument().addDocumentListener(this);
			pastPartEnVerbTextfield.getDocument().addDocumentListener(this);

			actSimPres1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPres2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPres3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPres1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPres2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPres3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			actSimPast1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPast2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPast3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPast1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPast2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimPast3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			actPastCont1stSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			actPastCont2ndSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			actPastCont3rdSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			actPastCont1stPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			actPastCont2ndPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			actPastCont3rdPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);

			actSimpFut1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimpFut2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimpFut3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimpFut1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimpFut2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			actSimpFut3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			actInfGrVerbTextfield.getDocument().addDocumentListener(this);
			actPartGrVerbTextfield.getDocument().addDocumentListener(this);

			pasSimPres1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPres2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPres3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPres1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPres2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPres3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			pasSimPast1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPast2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPast3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPast1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPast2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimPast3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			pasPastCont1stSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			pasPastCont2ndSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			pasPastCont3rdSingGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			pasPastCont1stPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			pasPastCont2ndPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);
			pasPastCont3rdPlurGrVerbTextfield.getDocument()
					.addDocumentListener(this);

			pasSimpFut1stSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimpFut2ndSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimpFut3rdSingGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimpFut1stPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimpFut2ndPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);
			pasSimpFut3rdPlurGrVerbTextfield.getDocument().addDocumentListener(
					this);

			pasInfGrVerbTextfield.getDocument().addDocumentListener(this);
			pasPartGrVerbTextfield.getDocument().addDocumentListener(this);
		}
	}

	private void removeListeners() {
		if (singEnNounTextfield != null) {
			singEnNounTextfield.getDocument().removeDocumentListener(this);
			plurEnNounTextfield.getDocument().removeDocumentListener(this);
			singNomGrNounTextfield.getDocument().removeDocumentListener(this);
			singGenGrNounTextfield.getDocument().removeDocumentListener(this);
			singAccGrNounTextfield.getDocument().removeDocumentListener(this);
			plurNomGrNounTextfield.getDocument().removeDocumentListener(this);
			plurGenGrNounTextfield.getDocument().removeDocumentListener(this);
			plurAccGrNounTextfield.getDocument().removeDocumentListener(this);
		}

		if (adjEnTextfield != null) {
			adjEnTextfield.getDocument().removeDocumentListener(this);
			singMascNomAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			singMascGenAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			singMascAccAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurMascNomAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurMascGenAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurMascAccAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			singFemNomAdjGrTextfield.getDocument().removeDocumentListener(this);
			singFemGenAdjGrTextfield.getDocument().removeDocumentListener(this);
			singFemAccAdjGrTextfield.getDocument().removeDocumentListener(this);
			plurFemNomAdjGrTextfield.getDocument().removeDocumentListener(this);
			plurFemGenAdjGrTextfield.getDocument().removeDocumentListener(this);
			plurFemAccAdjGrTextfield.getDocument().removeDocumentListener(this);
			singNeutNomAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			singNeutGenAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			singNeutAccAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurNeutNomAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurNeutGenAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
			plurNeutAccAdjGrTextfield.getDocument()
					.removeDocumentListener(this);
		}

		if (baseFormEnVerbTextfield != null) {
			baseFormEnVerbTextfield.getDocument().removeDocumentListener(this);
			simPres3rdSingEnVerbTextfield.getDocument().removeDocumentListener(
					this);
			presPartEnVerbTextfield.getDocument().removeDocumentListener(this);
			simPastEnVerbTextfield.getDocument().removeDocumentListener(this);
			pastPartEnVerbTextfield.getDocument().removeDocumentListener(this);
		}

		if (actSimPres1stSingGrVerbTextfield != null) {
			actSimPres1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPres2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPres3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPres1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPres2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPres3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			actSimPast1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPast2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPast3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPast1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPast2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimPast3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			actPastCont1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actPastCont2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actPastCont3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actPastCont1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actPastCont2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actPastCont3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			actSimpFut1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimpFut2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimpFut3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimpFut1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimpFut2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			actSimpFut3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			actInfGrVerbTextfield.getDocument().removeDocumentListener(this);
			actPartGrVerbTextfield.getDocument().removeDocumentListener(this);

			pasSimPres1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPres2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPres3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPres1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPres2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPres3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			pasSimPast1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPast2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPast3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPast1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPast2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimPast3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			pasPastCont1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasPastCont2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasPastCont3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasPastCont1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasPastCont2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasPastCont3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			pasSimpFut1stSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimpFut2ndSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimpFut3rdSingGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimpFut1stPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimpFut2ndPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);
			pasSimpFut3rdPlurGrVerbTextfield.getDocument()
					.removeDocumentListener(this);

			pasInfGrVerbTextfield.getDocument().removeDocumentListener(this);
			pasPartGrVerbTextfield.getDocument().removeDocumentListener(this);
		}
	}

	protected void disposeOWLView() {
		super.disposeOWLView();
		removeListeners();
		getOWLModelManager().removeListener(modelListener);
	}
}