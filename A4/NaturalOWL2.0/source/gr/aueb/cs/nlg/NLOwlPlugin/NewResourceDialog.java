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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class NewResourceDialog extends JFrame {
	private static final long serialVersionUID = 2479651880224511587L;
	
	NewResourceDialogPanel panel;
	String title;

	NewResourceDialog(String title, TreeComponent tab, boolean simple, String text) {
		super(title);
		this.title = title;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new NewResourceDialogPanel(tab, simple, text);
	}

	public boolean getResponse() {
		int selection = JOptionPane.showConfirmDialog(this, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (selection == JOptionPane.OK_OPTION)
			return true;
		return false;
	}

	public String getName() {
		return this.panel.getName();
	}

	public String getFirstSelected() {
		return this.panel.getFirstSelected();
	}
	
	public String getSecondSelected() {
		return this.panel.getSecondSelected();
	}
}

class NewResourceDialogPanel extends JPanel {
	private static final long serialVersionUID = 1799975683407897340L;
	
	GridBagConstraints gbc = new GridBagConstraints();
	GridBagLayout gbl = new GridBagLayout();
	ArrayList<JComponent> options = new ArrayList<JComponent>();

	NewResourceDialogPanel(TreeComponent tab, boolean simple, String text) {
		super();

		if(simple){
			String name = "Please enter a new name:";
			JLabel nameLabel = new JLabel(name);
			JTextField nametf = new JTextField(text, 20);
			addComponent(nameLabel, 0, 0, GridBagConstraints.WEST);
			addComponent(nametf, 0, 1, GridBagConstraints.WEST);
			options.add(nametf);
		}
		else{			
			String name = "";
			String parentType = "Please choose a language:";
			String[] choices = new String[] { "English", "Greek" };
			if (tab instanceof LexiconTreeComponent) {
				name = "Please enter a name for the new Lexicon Entry:";
				parentType = "Please choose a Part of Speech:";
				choices = new String[] { "Adjective", "Noun", "Verb" };
			} else if (tab instanceof NLNameTreeComponent) {
				name = "Please enter a name for the new NL Name:";
			} else if (tab instanceof SentencePlanTreeComponent) {
				name = "Please enter a name for the new Sentence Plan:";
			}

			JLabel nameLabel = new JLabel(name);
			JTextField nametf = new JTextField(text, 20);
			JLabel parentLabel = new JLabel(parentType);
			JComboBox<String> parentcb = new JComboBox<String>(choices);

			addComponent(nameLabel, 0, 0, GridBagConstraints.WEST);
			addComponent(nametf, 0, 1, GridBagConstraints.WEST);
			addComponent(parentLabel, 0, 2, GridBagConstraints.WEST);
			addComponent(parentcb, 0, 3, GridBagConstraints.WEST);
			options.add(nametf);
			options.add(parentcb);
		}

		this.setLayout(gbl);
	}

	public String getName() {
		return ((JTextField) (options.get(0))).getText();
	}
	
	public String getFirstSelected() {
		return ((JComboBox<String>) options.get(0)).getSelectedItem().toString();
	}
	
	public String getSecondSelected() {
		return ((JComboBox<String>) options.get(1)).getSelectedItem().toString();
	}

	public void addComponent(Component component, int xpos, int ypos) {
		gbc.gridx = xpos;
		gbc.gridy = ypos;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbl.setConstraints(component, gbc);
		this.add(component);
	}

	public void addComponent(Component component, int xpos, int ypos, int anchor) {
		gbc.anchor = anchor;
		addComponent(component, xpos, ypos);
	}
}