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

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

      
public class ListRenderer extends DefaultListCellRenderer {  
	private static final long serialVersionUID = 4460531137475818281L;

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) { 
        JComponent c = (JComponent)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);  
        
        if (value instanceof ListIRI) {  
        	if (!DefaultResourcesManager.isDefaultResource(((ListIRI)value).getEntryIRI())) {
        		list.setToolTipText(((ListIRI)value).getToolTipText());  
        	} else {
        		list.setToolTipText("<html><b>This is a default resource of the system.</b> <br>" + ((ListIRI)value).getToolTipText() + "</html>");
        	}     
        }  
        else {  
            list.setToolTipText("");  
        }  
        return c;  
    }  
}