/*
NaturalOWL version 2.0 
Copyright (C) 2013 Gerasimos Lampouras
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
package gr.aueb.cs.nlg.NLFiles;

import org.semanticweb.owlapi.model.IRI;

public class UMPrpInstanceLevelKey {

    private IRI forProperty;
    private IRI forInstance;
    private IRI forModifier;

    public UMPrpInstanceLevelKey(IRI forProperty, IRI forInstance, IRI forModifier) {
        this.forProperty = forProperty;
        this.forInstance = forInstance;
        this.forModifier = forModifier;
    }

    public IRI getForInstance() {
        return forInstance;
    }

    public void setForInstance(IRI forInstance) {
        this.forInstance = forInstance;
    }

    public IRI getForProperty() {
        return forProperty;
    }

    public void setForProperty(IRI forProperty) {
        this.forProperty = forProperty;
    }

    public IRI getForModifier() {
        return forModifier;
    }

    public void setForModifier(IRI forModifier) {
        this.forModifier = forModifier;
    }

    public int hashCode() {
        if (forModifier != null) {
            if (forProperty == null) {
                return forInstance.hashCode() + forModifier.hashCode();
            }
            return forProperty.hashCode() + forInstance.hashCode() + forModifier.hashCode();
        }
        return forProperty.hashCode() + forInstance.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof UMPrpInstanceLevelKey) {
            UMPrpInstanceLevelKey temp = (UMPrpInstanceLevelKey) o;

            if (temp.forProperty.toString().compareTo(forProperty.toString()) == 0 && temp.forInstance.toString().compareTo(forInstance.toString()) == 0) {
                if (temp.forModifier == null && forModifier == null) {
                    return true;
                }
                if (temp.forModifier == null) {
                    return false;
                }
                if (forModifier == null) {
                    return false;
                }
                if (temp.forModifier.toString().compareTo(forModifier.toString()) == 0) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }
}