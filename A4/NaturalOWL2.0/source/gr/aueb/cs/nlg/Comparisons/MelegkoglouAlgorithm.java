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
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Random;

import org.semanticweb.owlapi.model.IRI;

import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class MelegkoglouAlgorithm {

    private ComparisonTree tree;
    public static HashMap<String, ArrayList<String>> mentioned = new HashMap<String, ArrayList<String>>();

    public MelegkoglouAlgorithm(ComparisonTree comparisonTree) {
        tree = comparisonTree;
    }

    private void step1(ComparisonNode describing) {
        //for each node
        for (ComparisonNode node : tree.getNodes()) {
            //for each property
            for (int i = 0; i < node.getProperties().size(); i++) {
                String property = node.getProperties().get(i);
                //if a property doesnt exist in describing entity we remove it
                if (!describing.getProperties().contains(property)) {
                    node.removeProperty(property);
                    i--;
                } else {//for every property with different value
                    int index = describing.getProperties().indexOf(property);
                    //store the value of the describing entity for that property
                    String value = "";
                    for (String val : describing.getValueCardinality(index).keySet()) {
                        value = val;
                    }

                    index = node.getProperties().indexOf(property);
                    //remove the values that differ and appear once
                    Set<String> values = new HashSet<String>(node.getValueCardinality(index).keySet());
                    Iterator<String> setIter = values.iterator();
                    while (setIter.hasNext()) {
                        String val = setIter.next();
                        if ((!value.equals(val)) && node.getValueCardinality(index).get(val) == 1) {
                            node.removeValue(property, val);
                        }
                    }
                }
            }
        }
    }

    private void step2Child(ComparisonNode start, ComparisonNode describing) {
        //foreach property
        for (int index = 0; index < start.getProperties().size(); index++) {

            Set<String> values = new HashSet<String>(start.getValueCardinality(index).keySet());
            Iterator<String> setIter = values.iterator();
            //foreach value
            while (setIter.hasNext()) {
                String val = setIter.next();
                //if this value appears to every instance
                if (start.getCardinality() == start.getValueCardinality(index).get(val)) {
                    for (ComparisonNode node : start.getChildren()) {
                        //if this value appears on child with the same frequency
                        //remove the value from parent
                        int childIndex = node.getProperties().indexOf(start.getProperties().get(index));
                        if (childIndex >= 0 && node.getValueCardinality(childIndex).containsKey(val)) {
                            int valueCardinality = node.getValueCardinality(childIndex).get(val);
                            if (valueCardinality == start.getValueCardinality(index).get(val)) {
                                start.removeValue(start.getProperties().get(index), val);
                                step2Child(node, describing);
                                break;
                            }
                        }
                    }//end for each child
                    //if no child found with equal frequency of the specific value
                    //remove the value from all children
                    if (start.getValueCardinality(index).containsKey(val)) {
                        for (ComparisonNode node : start.getChildren()) {
                            node.removeValue(start.getProperties().get(index), val);
                        }
                    }

                } else {
                    //if it doesnt appear on every instance of the node
                    //check if it does for his children
                    if (!start.getProperties().isEmpty()) {
                        start.removeValue(start.getProperties().get(index), val);
                    }
                    if (!start.getChildren().isEmpty()) {
                        for (ComparisonNode node : start.getChildren()) {
                            step2Child(node, describing);
                        }
                    }
                }

            }//end foreach value
        }
    }

    private void step2(ComparisonNode start, ComparisonNode describing) {
        //foreach property
        for (int index = 0; index < start.getProperties().size(); index++) {
            Set<String> values = new HashSet<String>(start.getValueCardinality(index).keySet());
            Iterator<String> setIter = values.iterator();
            //foreach value
            while (setIter.hasNext()) {
                String val = setIter.next();
                //if this value appears to every instance
                if (start.getCardinality() == start.getValueCardinality(index).get(val)) {
                    for (ComparisonNode node : start.getChildren()) {
                        //if this value appears on child with the same frequency
                        //remove the value from parent
                        int childIndex = node.getProperties().indexOf(start.getProperties().get(index));
                        if (childIndex >= 0 && node.getValueCardinality(childIndex).containsKey(val)) {
                            int valueCardinality = node.getValueCardinality(childIndex).get(val);
                            if (valueCardinality == start.getValueCardinality(index).get(val)) {
                                start.removeValue(start.getProperties().get(index), val);
                                step2Child(node, describing);
                                break;
                            }
                        }
                    }//end for each child
                    //if no child found with equal frequency of the specific value
                    //remove the value from all children
                    if (start.getValueCardinality(index).containsKey(val)) {
                        for (ComparisonNode node : start.getChildren()) {
                            node.removeValue(start.getProperties().get(index), val);
                        }
                    }

                } else {
                    //if it doesnt appear on every instance of the node
                    //check if it does for his children
                    if (!start.getProperties().isEmpty()) {
                        start.removeValue(start.getProperties().get(index), val);
                    }
                    if (!start.getChildren().isEmpty()) {
                        for (ComparisonNode node : start.getChildren()) {
                            step2Child(node, describing);
                        }
                    }
                }

            }//end foreach value
        }

        if (start.getParent() != null) {
            step2(start.getParent(), describing);
        }
    }

    private void step2ChildBlur(ComparisonNode start, ComparisonNode describing) {
        //foreach property
        for (int index = 0; index < start.getProperties().size(); index++) {
            Set<String> values = new HashSet<String>(start.getValueCardinality(index).keySet());
            Iterator<String> setIter = values.iterator();
            //foreach value
            while (setIter.hasNext()) {
                String val = setIter.next();
                //if this value appears to every instance 
                if (start.getCardinality() == start.getValueCardinality(index).get(val)) {
                    for (ComparisonNode node : start.getChildren()) {
                        //if this value appears on child with at least 80% frequency
                        //remove the value from parent
                        int childIndex = node.getProperties().indexOf(start.getProperties().get(index));
                        if (childIndex >= 0 && node.getValueCardinality(childIndex).containsKey(val)) {
                            int valueCardinality = node.getValueCardinality(childIndex).get(val);
                            if (valueCardinality == start.getValueCardinality(index).get(val)) {
                                start.removeValue(start.getProperties().get(index), val);
                                step2Child(node, describing);
                                break;
                            }
                        }
                    }//end for each child
                    //if no child found with equal frequency of the specific value
                    //remove the value from all children
                    if (start.getValueCardinality(index).containsKey(val)) {
                        for (ComparisonNode node : start.getChildren()) {
                            node.removeValue(start.getProperties().get(index), val);
                        }
                    }

                } else {
                    //if it doesnt appear on every instance of the node
                    //check if it does for his children
                    if (!start.getProperties().isEmpty()) {
                        start.removeValue(start.getProperties().get(index), val);
                    }
                    if (!start.getChildren().isEmpty()) {
                        for (ComparisonNode node : start.getChildren()) {
                            step2Child(node, describing);
                        }
                    }
                }

            }//end foreach value
        }
    }

    private void step2Blur(ComparisonNode start, ComparisonNode describing) {
        //foreach property
        for (int index = 0; index < start.getProperties().size(); index++) {
            Set<String> values = new HashSet<String>(start.getValueCardinality(index).keySet());
            Iterator<String> setIter = values.iterator();
            //foreach value
            while (setIter.hasNext()) {
                String val = setIter.next();
                //if this value appears to every instance              
                if (start.getValueCardinality(index).get(val) >= 0.8 * start.getCardinality()) {
                    for (ComparisonNode node : start.getChildren()) {
                        //if this value appears on child with at least 80% frequency
                        //remove the value from parent

                        int childIndex = node.getProperties().indexOf(start.getProperties().get(index));
                        if (childIndex >= 0 && node.getValueCardinality(childIndex).containsKey(val)) {
                            int valueCardinality = node.getValueCardinality(childIndex).get(val);
                            if (valueCardinality == start.getValueCardinality(index).get(val)) {
                                start.removeValue(start.getProperties().get(index), val);
                                step2ChildBlur(node, describing);
                                break;
                            }
                        }
                    }//end for each child
                    //if no child found with equal frequency of the specific value
                    //remove the value from all children
                    if (start.getValueCardinality(index).containsKey(val)) {
                        for (ComparisonNode node : start.getChildren()) {
                            node.removeValue(start.getProperties().get(index), val);
                        }
                    }

                } else {
                    //if it doesnt appear on every instance of the node
                    //check if it does for his children
                    if (!start.getProperties().isEmpty()) {
                        start.removeValue(start.getProperties().get(index), val);
                    }
                    if (!start.getChildren().isEmpty()) {
                        for (ComparisonNode node : start.getChildren()) {
                            step2ChildBlur(node, describing);
                        }
                    }
                }

            }//end foreach value
        }

        if (start.getParent() != null) {
            step2(start.getParent(), describing);
        }
    }

    private ComparisonNode step3(ComparisonNode start) {
        //if comparator has mentioned long ago don't compare with him        
        if (start.getAge() > 10) {
            ArrayList<String> properties = new ArrayList<String>(start.getProperties());
            for (String property : properties) {
                start.removeProperty(property);
            }
            start.clear();
        }
        //if it has mentioned before dont mention again
        if (mentioned.containsKey(start.getType())) {
            for (String property : mentioned.get(start.getType())) {
                start.removeProperty(property);
            }
            start.clear();
        }
        if (!start.getProperties().isEmpty() && !start.getType().equals("owl:Thing")) {
            return start;
        }
		//check his brothers
		if (start.getParent() != null) {
		    for (ComparisonNode node : start.getParent().getChildren()) {
		        node.clear();
		        if (!node.getProperties().isEmpty() && !node.getType().equals("owl:Thing") && !node.getType().equals(start.getType())) {
		            return step3(node);
		        }
		    }
		}
		//check his children
		for (ComparisonNode node : start.getChildren()) {
		    node.clear();
		    if (!node.getProperties().isEmpty() && !node.getType().equals("owl:Thing")) {
		        return step3(node);
		    }
		}
		//check other nodes    
		for (ComparisonNode node : tree.getNodes()) {
		    node.clear();
		    if (!node.getProperties().isEmpty() && !node.getType().equals("owl:Thing")) {
		        return step3(node);
		    }
		}
		return null;
    }

    public Comparison compareBlur(ComparisonNode describing) {
        if (tree.getRoot() == null || tree.getNodes().size() < 5) {
            return null;
        }
        String name = describing.getType();
        ComparisonNode start = tree.find(name);
        step1(describing);
        step2Blur(start, describing);
        start.clear();
        ComparisonNode comparator = step3(start);
        if (comparator != null) {
            Random r = new Random();
            boolean same = false;
            int choice = r.nextInt(comparator.getProperties().size());
            String property = comparator.getProperties().get(choice);
            for (String value : describing.getValueCardinality(describing.getProperties().indexOf(property)).keySet()) {
                if (comparator.getValueCardinality(choice).containsKey(value)) {
                    same = true;
                }
            }
            Comparison comparison = new Comparison(same, comparator.getType(), property, false, comparator.getCardinality() > 1, start.getValue(property));
            return comparison;

        }
        return null;
    }

    public Comparison compare(ComparisonNode describing) {
        if (tree.getRoot() == null) {
            return null;
        }
        String name = describing.getType();
        ComparisonNode start = tree.find(name);
        step1(describing);
        step2(start, describing);
        start.clear();
        ComparisonNode comparator = step3(start);
        if (comparator != null) {
            Random r = new Random();
            boolean same = false;
            int choice = r.nextInt(comparator.getProperties().size());
            String property = comparator.getProperties().get(choice);
            for (String value : describing.getValueCardinality(describing.getProperties().indexOf(property)).keySet()) {
                if (comparator.getValueCardinality(choice).containsKey(value)) {
                    same = true;
                }
            }
            Comparison comparison = new Comparison(same, comparator.getType(), property, true, comparator.getCardinality() > 1, comparator.getValue(property));
            if (!comparison.isSame()) {
                ArrayList<String> propertiesMentioned = mentioned.get(comparison.getComparator());
                if (propertiesMentioned == null) {
                    propertiesMentioned = new ArrayList<String>();
                }
                propertiesMentioned.add(property);
                mentioned.put(comparison.getComparator(), propertiesMentioned);
            }
            return comparison;
        }
        return null;
    }

    public Comparison detectUnique(ComparisonNode describing) {
        String name = describing.getType();
        ComparisonNode start = tree.find(name);
        if (start.getCardinality() == 1
                && (tree.areComparisonsAllowed(IRI.create(XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG))
                || tree.areComparisonsAllowed(IRI.create(XmlMsgs.prefix + ":instanceOf")))) {
            return new Comparison(true, start.getType(), "#" + XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG, true, false, start.getValue(XmlMsgs.prefix + ":" + XmlMsgs.IS_A_TAG));
        }
        return null;
    }

    public ComparisonFullCollection compareFullCollection(ComparisonNode describing) {
        String name = describing.getType();
        ComparisonNode start = tree.find(name);
        if (start.getCardinality() < 2) {
            return null;
        }

        for (int i = 0; i < tree.getNodes().size(); i++) {
            if (tree.getNodes().get(i).getCardinality() == 1) {
                tree.remove(tree.getNodes().get(i));
            }
        }

        for (int i = 0; i < start.getProperties().size(); i++) {
            String property = start.getProperties().get(i);
            HashSet<String> values = new HashSet<String>(start.getValueCardinality(i).keySet());
            for (String value : values) {
                if (start.getValueCardinality(i).get(value) != start.getCardinality()) {
                    start.removeValue(property, value);
                }
            }
        }
        start.clear();
        if (!start.getProperties().isEmpty()) {
            Random r = new Random();
            int choice = r.nextInt(start.getProperties().size());
            String property = start.getProperties().get(choice);
            if (!mentioned.containsKey(start.getType())) {
                ArrayList<String> newList = mentioned.get(start.getType());
                if (newList == null) {
                    newList = new ArrayList<String>();
                }
                newList.add(property);
                mentioned.put(start.getType(), newList);
                return new ComparisonFullCollection(true, start.getType(), property, true, true, start.getValue(property));
            }
			if (!mentioned.get(start.getType()).contains(property)) {
			    ArrayList<String> newList = mentioned.get(start.getType());
			    if (newList == null) {
			        newList = new ArrayList<String>();
			    }
			    newList.add(property);
			    mentioned.put(start.getType(), newList);
			    return new ComparisonFullCollection(true, start.getType(), property, true, true, start.getValue(property));
			}
        }
        return null;
    }

    public ComparisonFullCollection compareFullCollectionBlur(ComparisonNode describing) {
        String name = describing.getType();
        ComparisonNode start = tree.find(name);
        if (start.getCardinality() < 5) {
            return null;
        }

        for (int i = 0; i < tree.getNodes().size(); i++) {
            if (tree.getNodes().get(i).getCardinality() == 1) {
                tree.remove(tree.getNodes().get(i));
            }
        }

        for (int i = 0; i < start.getProperties().size(); i++) {
            String property = start.getProperties().get(i);
            ArrayList<String> values = new ArrayList<String>(start.getValueCardinality(i).keySet());
            for (String value : values) {
                if (start.getValueCardinality(i).get(value) < 0.8 * start.getCardinality()) {
                    start.removeValue(property, value);
                }
            }
        }
        start.clear();
        while (!start.getProperties().isEmpty()) {
            Random r = new Random();
            int choice = r.nextInt(start.getProperties().size());
            String property = start.getProperties().get(choice);
            if (!mentioned.containsKey(start.getType())) {
                ArrayList<String> newList = mentioned.get(start.getType());
                if (newList == null) {
                    newList = new ArrayList<String>();
                }
                newList.add(property);
                mentioned.put(start.getType(), newList);

                boolean same = false;
                for (String value : describing.getValueCardinality(describing.getProperties().indexOf(property)).keySet()) {
                    if (start.getValueCardinality(choice).keySet().contains(value)) {
                        same = true;
                    }
                }
                return new ComparisonFullCollection(same, start.getType(), property, false, true, start.getValue(property));
            }
			if (!mentioned.get(start.getType()).contains(property)) {
			    ArrayList<String> newList = mentioned.get(start.getType());
			    if (newList == null) {
			        newList = new ArrayList<String>();
			    }
			    newList.add(property);
			    mentioned.put(start.getType(), newList);

			    boolean same = false;
			    for (String value : describing.getValueCardinality(describing.getProperties().indexOf(property)).keySet()) {
			        if (start.getValueCardinality(choice).keySet().contains(value)) {
			            same = true;
			        }
			    }
			    return new ComparisonFullCollection(same, start.getType(), property, false, true, start.getValue(property));
			}
            start.removeProperty(property);
            start.clear();
        }
        return null;
    }
}