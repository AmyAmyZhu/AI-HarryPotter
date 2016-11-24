/*
NaturalOWL version 2.0 
Copyright (C) 2013 Gerasimos Lampouras and George Papoutsakis
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

package gr.aueb.cs.nlg.Comparisons;

import gr.aueb.cs.nlg.Languages.Languages;
import org.semanticweb.owlapi.model.IRI;

public class Comparison {

	protected String lang;
	protected boolean same;
	protected boolean many;
	protected String comparator;
	protected String property;
	protected boolean all;
	protected String type;
	protected IRI valueIRI;
	protected String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAll() {
		return all;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isMany() {
		return many;
	}

	public void setMany(boolean many) {
		this.many = many;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public String getComparator() {
		return comparator;
	}

	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public String getProperty() {
		return property;
	}

	public boolean isSame() {
		return same;
	}

	public void setSame(boolean same) {
		this.same = same;
	}

	public String getText() {
		if (Languages.isEnglish(lang)) {
			return getTextEng();
		}
		return getTextGR();
	}

	public String getSuffix() {
		if (Languages.isEnglish(lang)) {
			return ComparisonTexts.SUFIX_PREVIOUS_EN;
		}
		return ComparisonTexts.SUFIX_PREVIOUS_GR;
	}

	public String getTextEng() {
		if (same) {
			return ComparisonTexts.LIKE_EN;
		}
		return ComparisonTexts.UNLIKE_EN;
	}

	public String getTextGR() {
		if (same) {
			return ComparisonTexts.LIKE_GR;
		}
		return ComparisonTexts.UNLIKE_GR;
	}

	public String getReminderConnector() {
		if (Languages.isEnglish(lang)) {
			return ComparisonTexts.WHICH;
		}
		return ComparisonTexts.POU;
	}

	public IRI getValueIRI() {
		return valueIRI;
	}

	public void setValueIRI(IRI valueIRI) {
		this.valueIRI = valueIRI;
	}

	public Comparison(boolean same, String comparator, String property,
			boolean all, boolean many, String value) {
		this.valueIRI = IRI.create(value);
		this.same = same;
		this.comparator = comparator;
		this.property = property.split("#")[1];
		this.all = all;
		this.many = many;
		this.lang = "";
		if (all) {
			if (many) {
				if (same) {
					type = ComparisonTypes.PREVIOUS_MANY_ALL_SAME;
				} else {
					type = ComparisonTypes.PREVIOUS_MANY_ALL_DIFFERENT;
				}
			} else {
				type = ComparisonTypes.PREVIOUS_ONE_ALL_SAME;
			}

		} else {
			if (many) {
				if (same) {
					type = ComparisonTypes.PREVIOUS_MANY_MOST_SAME;
				} else {
					type = ComparisonTypes.PREVIOUS_MANY_MOST_DIFFERENT;
				}
			}
		}
	}
}
