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

import org.semanticweb.owlapi.model.IRI;

public class ListIRI implements Comparable<ListIRI> {
    private IRI entryIRI;  
      
    public ListIRI(IRI e) {  
    	entryIRI = e;
    }  
      
    public String toString() {  
        if (DefaultResourcesManager.isDefaultResource(entryIRI)) {
        	return "<html><b>" + entryIRI.getFragment() + "</b></html>";
        }
		return entryIRI.getFragment();
    }  
      
    public String getToolTipText() {  
        return entryIRI.toString();
    }
    
    public IRI getEntryIRI() {
    	return entryIRI;
    }

	public int compareTo(ListIRI o) {
		return this.getEntryIRI().toString().compareTo(o.getEntryIRI().toString());
	}
	
	public boolean equals(Object o) {
		if (o instanceof ListIRI) {
			return this.getEntryIRI().equals(((ListIRI)o).getEntryIRI());
		}
		return false;
	}
	
	public int hashCode() {
		return this.getEntryIRI().hashCode();
	}
}