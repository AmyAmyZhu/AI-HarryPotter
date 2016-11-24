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
import java.awt.Font;


import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeRenderer extends DefaultTreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {	
		JComponent c = (JComponent)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (((DefaultMutableTreeNode)value).getUserObject() instanceof ListIRI) {  
        	if (!DefaultResourcesManager.isDefaultResource(((ListIRI)((DefaultMutableTreeNode)value).getUserObject()).getEntryIRI())) {
        		tree.setToolTipText(((ListIRI)((DefaultMutableTreeNode)value).getUserObject()).getToolTipText());        		
        	} else {
        		tree.setToolTipText("<html><b>This is a default resource of the system.</b> <br>" + ((ListIRI)((DefaultMutableTreeNode)value).getUserObject()).getToolTipText() + "</html>");
        	}              
        }
        else {
        	tree.setToolTipText((String)((DefaultMutableTreeNode)value).getUserObject());  
        }
        return c;  
	}
}