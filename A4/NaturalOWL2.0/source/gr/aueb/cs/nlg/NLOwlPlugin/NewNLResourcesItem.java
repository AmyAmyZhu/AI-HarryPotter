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

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;
import org.semanticweb.owlapi.model.IRI;

public class NewNLResourcesItem extends ProtegeOWLAction {
	private static final long serialVersionUID = -7502958196553167913L;

	public void initialise() throws Exception {}

	public void dispose() throws Exception {}

	public void actionPerformed(ActionEvent event) {
		while(true) {
			String s = (String)JOptionPane.showInputDialog(getOWLWorkspace(), "NL Resources IRI", "NLResources Ontology Name", JOptionPane.QUESTION_MESSAGE, null, null, NLResourceManager.resourcesNS.substring(0, NLResourceManager.resourcesNS.length()-1));
	
			if (s != null) {
				IRI ontologyIRI = IRI.create(s);
				if (!s.equals("") && NaturalOWLTab.isLegalIRI(ontologyIRI)) {
					NaturalOWLTab.masterLoadedOntologyID = getOWLModelManager().getActiveOntology().getOntologyID().getOntologyIRI().toString();
					NaturalOWLTab.createNewNLResourcesOntology(ontologyIRI, getOWLWorkspace().getOWLModelManager().getOntologies());
					NaturalOWLTab.refresh.setSelected(!NaturalOWLTab.refresh.isSelected());
				    return;
				}
			} else {
				return;
			}
		}
	}

}