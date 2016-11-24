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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public class NLNamesTree extends NaturalOWLTab  {
	private static final long serialVersionUID = -5897999950683770116L;

	protected void initialiseOWLView() throws Exception {
		initialiseNaturalOWL();
		
		refresh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
		        refresh();
			}
		});
		
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		
        NLNameTreeComponent tree = new NLNameTreeComponent(this);

        removeAll();
        add(tree.TP(), BorderLayout.CENTER);
	}
	
	private void refresh() {
		NLNameTreeComponent tree = new NLNameTreeComponent(this);
		
        removeAll();
        add(tree.TP(), BorderLayout.CENTER);		
	}

	public void actionPerformed(ActionEvent e) {}

	public void itemStateChanged(ItemEvent e) {}		
}