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

public class ComparisonFullCollection extends Comparison {

	public ComparisonFullCollection(boolean same, String comparator, String property, boolean all, boolean many, String value) {
		super(same, comparator, property, all, many, value);
		if (all) {
			type = ComparisonTypes.FULL_COLECTION_ALL;
		} else {
			type = ComparisonTypes.FULL_COLECTION_MOST;
		}
	}

	public boolean isAll() {
		return all;
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

	public String getSuffix() {
		if (Languages.isEnglish(lang)) {
			return ComparisonTexts.SUFIX_FULL_COLECTION_EN;
		}
		return ComparisonTexts.SUFIX_FULL_COLECTION_GR;
	}
}
