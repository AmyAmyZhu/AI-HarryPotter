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


import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class OpenNLResourcesItem extends ProtegeOWLAction {
	public void initialise() throws Exception {}

	public void dispose() throws Exception {}

	public void actionPerformed(ActionEvent event) {
		NaturalOWLTab.fc.setFileFilter(new OWLFilter());
		int returnVal = NaturalOWLTab.fc.showOpenDialog(getOWLWorkspace());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (NaturalOWLTab.fc.getSelectedFile().exists()) {
				NaturalOWLTab.masterLoadedOntologyID = getOWLModelManager().getActiveOntology().getOntologyID().getOntologyIRI().toString();
				NaturalOWLTab.openNLResourcesOntology(NaturalOWLTab.fc.getSelectedFile(), getOWLWorkspace().getOWLModelManager().getOntologies(), true);
				NaturalOWLTab.refresh.setSelected(!NaturalOWLTab.refresh.isSelected());
		    	return;
			}
			JOptionPane.showMessageDialog(getOWLWorkspace(), "File not found!");
		} else {
			return;
		}
	}

}