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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ComparisonNode {

	private String name;
	private int cardinality;
	private ComparisonNode parent;
	private ArrayList<String> properties;
	private ArrayList<ComparisonNode> children;
	private ArrayList<HashMap<String, Integer>> values_cardinality;
	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setChildren(ArrayList<ComparisonNode> children) {
		this.children = children;
	}

	public ArrayList<HashMap<String, Integer>> getValues_cardinality() {
		return values_cardinality;
	}

	public void setProperties(ArrayList<String> properties) {
		this.properties = properties;
	}

	public void setValues_cardinality(ArrayList<HashMap<String, Integer>> values_cardinality) {
		this.values_cardinality = values_cardinality;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	public ComparisonNode(String uri) {
		age = 1;
		name = uri;
		cardinality = 1;
		parent = null;
		properties = new ArrayList<String>();
		children = new ArrayList<ComparisonNode>();
		values_cardinality = new ArrayList<HashMap<String, Integer>>();
	}

	public ComparisonNode(ComparisonNode oldNode) {
		age = oldNode.getAge();
		name = oldNode.getType().toString();
		cardinality = oldNode.getCardinality();
		parent = null;
		properties = new ArrayList<String>();
		children = new ArrayList<ComparisonNode>();
		values_cardinality = new ArrayList<HashMap<String, Integer>>();
		for (String property : oldNode.getProperties()) {
			properties.add(property);
		}
		for (int i = 0; i < properties.size(); i++) {
			values_cardinality.add(new HashMap<String, Integer>());
			for (String value : oldNode.values_cardinality.get(i).keySet()) {
				values_cardinality.get(i).put(value, oldNode.values_cardinality.get(i).get(value));
			}
		}
	}

	public void increaseCardinality() {
		age = 1;
		cardinality++;
	}

	public ArrayList<ComparisonNode> getChildren() {
		return children;
	}

	public String getValue(String property) {
		this.clear();
		String value = "";
		int index = properties.indexOf(property);
		if (index >= 0) {
			Set<String> values = values_cardinality.get(index).keySet();
			for (String val : values) {
				value = val;
			}
		}
		return value;
	}

	private void addChild(ComparisonNode child) {
		children.add(child);
	}

	public ComparisonNode getParent() {
		return parent;
	}

	public void setParent(ComparisonNode parent) {
		if (parent != null) {
			parent.addChild(this);
			this.parent = parent;
		}
	}

	public String getType() {
		return name;
	}

	public int getCardinality() {
		return cardinality;
	}

	public void setType(String name) {
		this.name = name;
	}

	public ArrayList<String> getProperties() {
		return properties;
	}

	public HashMap<String, Integer> getValueCardinality(int index) {
		return values_cardinality.get(index);
	}

	public void removeProperty(String property) {
		int index = properties.indexOf(property);
		if (index >= 0) {
			properties.remove(index);
			values_cardinality.remove(index);
		}
	}

	public void decreaceCardinality(String property, String value) {
		int index = properties.indexOf(property);
		if (index >= 0) {
			int valueCardinality = values_cardinality.get(index).get(value);
			if (valueCardinality > 1) {
				values_cardinality.get(index).put(value, valueCardinality - 1);
			} else {
				removeValue(property, value);
			}
		}
	}

	public void removeValue(String property, String value) {
		int index = properties.indexOf(property);
		if (index >= 0) {
			values_cardinality.get(index).remove(value);
		}
	}

	public void addProperty(String pname, String pvalue) {
		if (properties.contains(pname)) {
			int index = properties.indexOf(pname);
			if (values_cardinality.get(index).keySet().contains(pvalue)) {
				values_cardinality.get(index).put(pvalue, values_cardinality.get(index).get(pvalue) + 1);
			} else {
				values_cardinality.get(index).put(pvalue, 1);
			}
		} else {
			properties.add(pname);
			int index = properties.indexOf(pname);
			values_cardinality.add(new HashMap<String, Integer>());
			values_cardinality.get(index).put(pvalue, 1);
		}
	}

	public String toString() {
		return name + " " + cardinality + "\n" + values_cardinality;
	}

	public boolean equals(ComparisonNode node) {
		if (this.getType().equals(node.getType())) {
			return true;
		}
		return false;
	}

	public void clear() {
		for (int i = properties.size() - 1; i >= 0; i--) {
			if (values_cardinality.get(i).isEmpty()) {
				removeProperty(properties.get(i));
			}
		}
	}
}
