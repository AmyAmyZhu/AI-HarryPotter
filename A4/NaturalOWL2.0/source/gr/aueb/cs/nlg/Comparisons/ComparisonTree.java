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

import gr.aueb.cs.nlg.NLFiles.MappingQueryManager;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.IRI;

import gr.aueb.cs.nlg.NLGEngine.NLGEngine;
import gr.aueb.cs.nlg.Utils.Fact;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;

public class ComparisonTree implements Cloneable {

	private static final String lt_OR_gt_regex = "[<>]";
	private static final String Thing = "owl:Thing";
	private ComparisonNode root;
	private ArrayList<ComparisonNode> nodes;
	private OWLOntologyManager manager;
	private ArrayList<String> entities;
	private MappingQueryManager MQM;

	public ComparisonTree(OWLOntologyManager manager, MappingQueryManager MQM) {
		this.MQM = MQM;
		this.manager = manager;
		entities = new ArrayList<String>();
		nodes = new ArrayList<ComparisonNode>();
		root = null;
	}

	public ComparisonTree(ComparisonTree oldTree) {
		this.MQM = oldTree.MQM;
		this.manager = oldTree.manager;
		this.root = null;
		this.nodes = new ArrayList<ComparisonNode>();
		this.entities = new ArrayList<String>(oldTree.entities);
		for (int i = 0; i < oldTree.nodes.size(); i++) {
			this.nodes.add(new ComparisonNode(oldTree.nodes.get(i)));
			if (oldTree.getRoot().getType().equals(oldTree.nodes.get(i).getType())) {
				this.root = this.nodes.get(i);
			}
		}
		for (int i = 0; i < oldTree.nodes.size(); i++) {
			if (oldTree.nodes.get(i).getParent() != null) {
				int parentIndex = oldTree.nodes.indexOf(oldTree.nodes.get(i).getParent());
				this.nodes.get(i).setParent(this.nodes.get(parentIndex));
			}
		}
	}

	public boolean contains(String entity) {
		return entities.contains(entity);
	}

	public boolean comparatorIncludes(String entity, String comparator) {
		ArrayList<String> entityParents = new ArrayList<String>();
		String parent = findParent(IRI.create(entity));
		while (!parent.equals(Thing)) {
			entityParents.add(parent);
			parent = findParent(IRI.create(parent));
		}
		entityParents.add(Thing);
		return entityParents.contains(comparator);
	}

	public boolean areComparisonsAllowed(IRI iri) {
		return MQM.areComparisonsAllowed(iri);
	}

	public void removeEntity(IRI iri) {
		ArrayList<Fact> temp = getFacts(iri);
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (Fact fact : temp) {
			if (MQM.areComparisonsAllowed(fact.getPredicate())) {
				facts.add(fact);
			}
		}
		String parentName = findParent(iri);
		for (ComparisonNode node : nodes) {
			if (node.getType().equals(parentName)) {
				for (Fact fact : facts) {
					String property = fact.getPredicate().toString();
					if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
						String value = fact.getObject();
						node.decreaceCardinality(property, value);
					}
				}
				node.setCardinality(node.getCardinality() - 1);
				node.clear();
				while (node.getParent() != null) {
					node = node.getParent();
					for (Fact fact : facts) {
						String property = fact.getPredicate().toString();
						if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
							String value = fact.getObject();
							node.decreaceCardinality(property, value);
						}
					}
					node.setCardinality(node.getCardinality() - 1);
					node.clear();
				}
				return;
			}
		}
	}

	public void add(IRI iri) {
		if (entities.contains(iri.toString())) {
			String parent = findParent(iri);
			for (ComparisonNode node : nodes) {
				if (node.getType().equals(parent)) {
					node.setAge(1);
					while (node.getParent() != null) {
						node = node.getParent();
						node.setAge(1);
					}
				}
			}
			return;
		}
		entities.add(iri.toString());
		ArrayList<Fact> temp = getFacts(iri);
		ArrayList<Fact> facts = new ArrayList<Fact>();
		for (Fact fact : temp) {
			if (MQM.areComparisonsAllowed(fact.getPredicate())) {
				facts.add(fact);
			}
		}
		if (nodes.isEmpty()) {
			if (NLGEngine.isClass(manager.getOntologies(), iri)) {
				String name = iri.toString();
				String parentName = findParent(iri);
				ComparisonNode node = new ComparisonNode(name);
				if (!parentName.isEmpty()) {
					root = new ComparisonNode(parentName);
					node.setParent(root);
					nodes.add(root);
				} else {
					root = node;
				}
				nodes.add(node);

				for (Fact fact : facts) {
					String property = fact.getPredicate().toString();
					if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
						node.addProperty(property, fact.getObject());
						if (root != node) {
							root.addProperty(property, fact.getObject());
						}
					}
				}
			} else {
				String name = findParent(iri);
				if (name.isEmpty()) {
					return;
				}
				root = new ComparisonNode(name);
				for (Fact fact : facts) {
					String property = fact.getPredicate().toString();
					if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
						String value = fact.getObject();
						root.addProperty(property, value);
					}
				}
				nodes.add(root);
			}
		} else {// not empty
			for (ComparisonNode node : nodes) {
				node.setAge(node.getAge() + 1);
			}
			// Whether or not node exists in tree
			for (ComparisonNode node : nodes) {
				if (node.getType().equals(iri.toString())) {
					node.increaseCardinality();
					// foreach property
					for (Fact fact : facts) {
						String property = fact.getPredicate().toString();
						if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
							String value = fact.getObject();
							node.addProperty(property, value);
						}
					}
					// Update its parents
					while (node != root) {
						node = node.getParent();
						node.increaseCardinality();
						// foreach property
						for (Fact fact : facts) {
							String property = fact.getPredicate().toString();
							if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
								String value = fact.getObject();
								node.addProperty(property, value);
							}
						}
					}
					return;
				}
			}
			// Whether the node's father is in the tree
			// get iri's father
			String name = findParent(iri);
			for (ComparisonNode node : nodes) {
				if (node.getType().equals(name)) {
					if (NLGEngine.isClass(manager.getOntologies(), iri)) {// class
						node.increaseCardinality();
						ComparisonNode child = new ComparisonNode(iri.toString());
						// foreach property
						for (Fact fact : facts) {
							String property = fact.getPredicate().toString();
							if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
								String value = fact.getObject();
								child.addProperty(property, value);
								node.addProperty(property, value);
							}
						}// end foreach property
						child.setParent(node);
						// Update its parents
						while (node != root) {
							node = node.getParent();
							node.increaseCardinality();
							// foreach property
							for (Fact fact : facts) {
								String property = fact.getPredicate().toString();
								if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
									String value = fact.getObject();
									node.addProperty(property, value);
								}
							}// end foreach property
						}
						nodes.add(child);
						return;
					}
					// foreach property
					node.increaseCardinality();
					for (Fact fact : facts) {
						String property = fact.getPredicate().toString();
						if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
							String value = fact.getObject();
							node.addProperty(property, value);
						}
					}// end foreach property
						// Updates its parents
					while (node != root) {
						node = node.getParent();
						node.increaseCardinality();
						// foreach property
						for (Fact fact : facts) {
							String property = fact.getPredicate().toString();
							if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
								String value = fact.getObject();
								node.addProperty(property, value);
							}
						}// end foreach property
					}
					return;
				}
			}
			// If no node or its parent are found
			if (NLGEngine.isClass(manager.getOntologies(), iri)) {// class
				// get subjects fathers
				ArrayList<String> subjectSuperClasses = new ArrayList<String>();
				subjectSuperClasses.add(iri.toString());
				String parent = findParent(iri);
				while (!parent.equals(Thing)) {
					subjectSuperClasses.add(parent);
					parent = findParent(IRI.create(parent));
				}
				// TO-DO Replace owl:Thing with a final string
				subjectSuperClasses.add(Thing);
				// get root's fathers
				ArrayList<String> rootSuperClasses = new ArrayList<String>();
				rootSuperClasses.add(root.getType());
				parent = findParent(IRI.create(root.getType()));
				while (!parent.equals(Thing)) {
					rootSuperClasses.add(parent);
					parent = findParent(IRI.create(parent));
				}
				rootSuperClasses.add(Thing);
				// TO-DO Translate comments
				// an den einai oloi oi paterades tis rizas sto dentro tous
				// prosthetoume
				if (!rootSuperClasses.get(rootSuperClasses.size() - 1).equals(root.getType())) {
					for (int i = 0; i < rootSuperClasses.size(); i++) {
						if (root.getType().equals(rootSuperClasses.get(i))) {
							continue;
						}
						newRoot(new ComparisonNode(rootSuperClasses.get(i)));
					}
				}
				// an o komvos periexete stous paterades tis rizas
				// ksanakalese tin add
				if (rootSuperClasses.contains(iri.toString())) {
					add(iri);
				} else {
					// vres poia iri einai idi sto dentro
					// kai gia kathe komvo stous paterades tou subject
					// an den iparxei sto dentro prosthese ton sto dentro
					// ksekinontas apo ton progenestero
					Set<String> iris = new HashSet<String>();
					for (ComparisonNode node : nodes) {
						iris.add(node.getType());
					}
					// subjectSuperClasses.get(0)=subject
					for (int i = subjectSuperClasses.size() - 1; i > 0; i--) {
						if (!iris.contains(subjectSuperClasses.get(i))) {
							addEmptyNode(IRI.create(subjectSuperClasses.get(i)));
						}
					}
					add(iri);
				}
			} else {// individual
				String father = findParent(iri);
				// get subject's fathers
				ArrayList<String> subjectSuperClasses = new ArrayList<String>();
				subjectSuperClasses.add(iri.toString());
				String parent = findParent(iri);
				while (!parent.equals(Thing)) {
					if (!subjectSuperClasses.contains(parent)) {
						subjectSuperClasses.add(parent);
						parent = findParent(IRI.create(parent));
					} else {// exei mpei se loop
						parent = Thing;
					}
				}
				subjectSuperClasses.add(Thing);

				// get root's fathers
				ArrayList<String> rootSuperClasses = new ArrayList<String>();
				rootSuperClasses.add(root.getType());
				parent = findParent(IRI.create(root.getType()));
				while (!parent.equals(Thing)) {
					rootSuperClasses.add(parent);
					parent = findParent(IRI.create(parent));
				}
				rootSuperClasses.add(Thing);

				// an den einai oloi oi paterades tis rizas sto dentro tous
				// prosthetoume
				if (!rootSuperClasses.get(rootSuperClasses.size() - 1).equals(root.getType())) {
					for (int i = 0; i < rootSuperClasses.size(); i++) {
						if (root.getType().equals(rootSuperClasses.get(i))) {
							continue;
						}
						newRoot(new ComparisonNode(rootSuperClasses.get(i)));
					}
				}
				// vres poia iri einai idi sto dentro
				// kai gia kathe komvo stous paterades tou subject
				// an den iparxei sto dentro prosthese ton sto dentro
				// ksekinontas apo ton progenestero
				Set<String> iris = new HashSet<String>();
				for (ComparisonNode node : nodes) {
					iris.add(node.getType());
				}

				for (int i = subjectSuperClasses.size() - 1; i > 0; i--) {
					if (!iris.contains(subjectSuperClasses.get(i))) {
						// TO-DO Remove any "unused" comments
						// System.out.println("Found: " +
						// subjectSuperClasses.get(i));
						addEmptyNode(IRI.create(subjectSuperClasses.get(i)));
					}
				}
				// psaxnoume ton patera tou komvou
				for (ComparisonNode node : nodes) {
					if (node.getType().equals(father)) {
						node.increaseCardinality();
						// System.out.println(facts);
						for (Fact fact : facts) {
							String property = fact.getPredicate().toString();
							if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
								String value = fact.getObject();
								node.addProperty(property, value);
							}
						}// end foreach property
							// Enimeronei tous goneis tou
						while (node != root) {
							node = node.getParent();
							node.increaseCardinality();
							// foreach property
							for (Fact fact : facts) {
								String property = fact.getPredicate().toString();
								// TO-DO Fix this
								if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
									String value = fact.getObject();
									node.addProperty(property, value);
								}
							}// end foreach property
						}
					}
				}
			}
		}// end not empty tree
	}

	private ArrayList<Fact> getFacts(IRI iri) {
		ArrayList<Fact> facts = new ArrayList<Fact>();
		if (!NLGEngine.isClass(manager.getOntologies(), iri)) {
			Set<OWLEntity> ent = new HashSet<OWLEntity>();
			Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
			for (OWLOntology model : manager.getOntologies()) {
				ent.addAll(model.getEntitiesInSignature(iri));
				for (OWLEntity entity : ent) {
					axioms.addAll(entity.getReferencingAxioms(model));
					for (OWLAxiom axiom : axioms) {
						if (axiom.getAxiomType().equals(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
							Set<OWLObjectProperty> properties = axiom.getObjectPropertiesInSignature();
							String property = "";
							for (OWLObjectProperty prop : properties) {
								property = prop.toString().replaceAll(lt_OR_gt_regex, "");
							}
							Set<OWLNamedIndividual> objs = axiom.getIndividualsInSignature();
							String object = "";
							for (OWLNamedIndividual obj : objs) {
								if (!obj.toString().replaceAll(lt_OR_gt_regex, "").equals(iri.toString())) {
									object = obj.toString().replaceAll(lt_OR_gt_regex, "");
								}
							}
							Fact fact = new Fact(iri, IRI.create(property), object);
							if (!facts.contains(fact)) {
								facts.add(fact);
							}
						} else if (axiom.getAxiomType().equals(AxiomType.CLASS_ASSERTION)) {
							Set<OWLClass> classes = axiom.getClassesInSignature();
							String parent = "";
							for (OWLClass clas : classes) {
								parent = clas.toString().replaceAll(lt_OR_gt_regex, "");
							}
							Fact fact = new Fact(iri, IRI.create(NLResourceManager.isAIndiv), parent);
							if (!facts.contains(fact)) {
								facts.add(fact);
							}
						}
					}
				}
			}
		} else {
			for (OWLOntology model : manager.getOntologies()) {
				for (OWLEntity cls : model.getEntitiesInSignature(iri)) {
					// building an arraylist with iri's properties
					for (OWLAxiom axiom : model.getAxioms((OWLClass) cls)) {
						String property = "";
						String individual;
						if (!axiom.getObjectPropertiesInSignature().isEmpty()) {
							for (OWLObjectProperty prop : axiom.getObjectPropertiesInSignature()) {
								property = prop.toString();
								// remove 1st and last character
								property = property.substring(1, property.length() - 1);
							}
							for (OWLNamedIndividual indiv : axiom.getIndividualsInSignature()) {
								individual = indiv.toString();
								individual = individual.substring(1, individual.length() - 1);
								if (!property.isEmpty()) {
									Fact fact = new Fact(iri, IRI.create(property), individual);
									if (!facts.contains(fact)) {
										facts.add(fact);
									}
								}// end inner if
							}// end foreach individual
						}// end if
					}// end foreach axiom
				}// end foreach class signature
			}
		}
		return facts;
	}

	private void addEmptyNode(IRI iri) {
		// System.out.println("add empty");
		ComparisonNode newNode = new ComparisonNode(iri.toString());
		newNode.setCardinality(0);
		// Psaxno an o pateras tou komvou iparxei sto dentro
		// get iri's father
		String name = findParent(iri);
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getType().equals(name)) {
				nodes.add(newNode);
				newNode.setParent(nodes.get(i));
			}
		}
	}

	public String findParent(IRI iri) {
		// individual
		if (!NLGEngine.isClass(manager.getOntologies(), iri)) {
			for (OWLOntology model : manager.getOntologies()) {
				for (OWLEntity individual : model.getEntitiesInSignature(iri)) {
					Set<OWLAxiom> axioms = individual.getReferencingAxioms(model);
					for (OWLAxiom axiom : axioms) {
						if (axiom.getAxiomType().equals(AxiomType.CLASS_ASSERTION)) {
							for (OWLClass clas : axiom.getClassesInSignature()) {
								return clas.toString().replaceAll(lt_OR_gt_regex, "");
							}
						}
					}
				}
			}
		} else {// class
			Set<OWLEntity> classes = new HashSet<OWLEntity>();
			for (OWLOntology model : manager.getOntologies()) {
				classes.addAll(model.getEntitiesInSignature(iri));
			}
			for (OWLEntity owlClass : classes) {
				if (owlClass.isOWLClass()) {
					Set<OWLClassExpression> superClasses = new HashSet<OWLClassExpression>();
					for (OWLOntology model : manager.getOntologies()) {
						superClasses.addAll(owlClass.asOWLClass().getSuperClasses(model));
					}
					Set<OWLClassExpression> superClassesCopy = new HashSet<OWLClassExpression>(superClasses);
					for (OWLClassExpression expr : superClassesCopy) {
						if (expr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
							return expr.toString().replaceAll(lt_OR_gt_regex, "");
						} else if (expr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
							for (OWLEntity clas : expr.getClassesInSignature()) {
								if (clas.toString().replaceAll(lt_OR_gt_regex, "").equals(Thing)) {
									continue;
								}
								return clas.toString().replaceAll(lt_OR_gt_regex, "");
							}
						}// end if
						else {
							superClasses.remove(expr);
						}
					}// end for
					if (superClasses.isEmpty()) {
						for (OWLOntology model : manager.getOntologies()) {
							superClasses = owlClass.asOWLClass().getEquivalentClasses(model);
						}
					}
					// an einai akoma adia
					if (superClasses.isEmpty()) {
						Set<OWLAxiom> superClasses2 = new HashSet<OWLAxiom>();
						for (OWLOntology model : manager.getOntologies()) {
							superClasses2.addAll(owlClass.asOWLClass().getReferencingAxioms(model, true));
						}
						for (OWLAxiom axiom : superClasses2) {
							if (axiom.getAxiomType().equals(AxiomType.SUBCLASS_OF)) {
								String referingclasses[] = axiom.toString().substring(11, axiom.toString().length() - 1).replaceAll(lt_OR_gt_regex, "").split(" ");
								if (referingclasses[0].equals(iri.toString())) {
									if (NLGEngine.isClass(manager.getOntologies(), IRI.create(referingclasses[1]))) {
										return referingclasses[1];
									}
								}
							}
							if (axiom.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES)) {
								String referingclasses[] = axiom.toString().substring(18, axiom.toString().length() - 1).replaceAll(lt_OR_gt_regex, "").split(" ");
								if (NLGEngine.isClass(manager.getOntologies(), IRI.create(referingclasses[0]))) {
									if (iri.toString().equals(referingclasses[0])) {
										if (referingclasses[1].startsWith("ObjectIntersectionOf")) {
											return referingclasses[1].split("[(]")[1];
										}
									}
								}
							}
						}
					}
					for (OWLClassExpression expr : superClasses) {
						if (expr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
							return expr.toString().replaceAll(lt_OR_gt_regex, "");
						}
						if (expr.getClassExpressionType().equals(ClassExpressionType.OBJECT_INTERSECTION_OF)) {
							for (OWLEntity clas : expr.getClassesInSignature()) {
								if (clas.toString().replaceAll(lt_OR_gt_regex, "").equals(Thing)) {
									continue;
								}
								return clas.toString().replaceAll(lt_OR_gt_regex, "");
							}
						}// end if
					}// end for
				}
			}
		}
		return Thing;
	}

	public ComparisonNode find(String iri) {
		// an vreis komvo me to idio iri epestrepse ton
		for (ComparisonNode node : nodes) {
			if (node.getType().equals(iri)) {
				return node;
			}
		}
		// psakse gia tous progonous tou komvou
		while (!iri.equals(Thing)) {
			iri = findParent(IRI.create(iri));
			return find(iri);
		}
		return root;
	}

	public ComparisonNode getRoot() {
		return root;
	}

	public void newRoot(ComparisonNode rootParent) {
		String rootParentType = rootParent.getType();
		// check if node contained in tree
		for (ComparisonNode node : nodes) {
			if (node.getType().equals(rootParentType)) {
				return;
			}
		}
		rootParent = new ComparisonNode(root);
		rootParent.setType(rootParentType);
		rootParent.setChildren(new ArrayList<ComparisonNode>());

		root.setParent(rootParent);
		root = rootParent;
		nodes.add(root);
	}

	public void printTree(ComparisonNode node) {
		if (node == null) {
			return;
		}
		node.clear();
		System.err.println(node.getType() + "\t" + node.getCardinality());
		System.err.println("Properties");
		int index = 0;
		for (String pred : node.getProperties()) {
			System.err.println(pred + "\t" + node.getValueCardinality(index));
			index++;
		}
		if (!node.getChildren().isEmpty()) {
			for (ComparisonNode temp : node.getChildren()) {
				printTree(temp);
			}
		}
	}

	public ComparisonNode create(IRI iri) {
		ArrayList<Fact> facts = new ArrayList<Fact>();
		ComparisonNode ret = new ComparisonNode(iri.toString());
		// an iparxei komvos me name iri
		for (ComparisonNode node : nodes) {
			if (node.getType().equals(iri.toString())) {
				// is class
				if (NLGEngine.isClass(manager.getOntologies(), iri)) {
					// foreach property
					// to facts tha nai adio edo
					for (OWLOntology model : manager.getOntologies()) {
						for (OWLEntity clas : model.getEntitiesInSignature(iri)) {
							for (OWLAxiom axiom : model.getAxioms((OWLClass) clas)) {
								String property = "";
								String individual;
								if (!axiom.getObjectPropertiesInSignature().isEmpty()) {
									for (OWLObjectProperty prop : axiom.getObjectPropertiesInSignature()) {
										property = prop.toString();
										// remove 1st and last character
										property = property.substring(1, property.length() - 1);
									}
									for (OWLNamedIndividual indiv : axiom.getIndividualsInSignature()) {
										individual = indiv.toString();
										individual = individual.substring(1, individual.length() - 1);
										if (!property.isEmpty()) {
											Fact fact = new Fact(iri, IRI.create(property), individual);
											facts.add(fact);
										}// end inner if
									}// end foreach individual
								}// end if
							}// end foreach axiom
						}// end foreach class signature
						for (Fact fact : facts) {
							String property = fact.getPredicate().toString();
							if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
								ret.addProperty(property, fact.getObject());
							}
						}
						return ret;
					}
				}
			} // an einai indiv koitame an iparxei o pateras tou
			else if (NLGEngine.isClass(manager.getOntologies(), iri)) {
				OWLClass subject;
				// get subjects fathers
				Set<OWLClassExpression> superClasses;
				for (OWLOntology model : manager.getOntologies()) {
					for (OWLEntity owlClass : model.getEntitiesInSignature(iri)) {
						if (owlClass.isOWLClass()) {
							subject = owlClass.asOWLClass();
							superClasses = subject.getSuperClasses(model);
							for (OWLClassExpression expr : superClasses) {
								if (expr.getClassExpressionType().equals(ClassExpressionType.OWL_CLASS)) {
									break;
								}
							}
						}
					}
					for (OWLEntity clas : model.getEntitiesInSignature(iri)) {
						for (OWLAxiom axiom : model.getAxioms((OWLClass) clas)) {
							String property = "";
							String individual;
							if (!axiom.getObjectPropertiesInSignature().isEmpty()) {
								for (OWLObjectProperty prop : axiom.getObjectPropertiesInSignature()) {
									property = prop.toString();
									// remove 1st and last character
									property = property.substring(1, property.length() - 1);
								}
								for (OWLNamedIndividual indiv : axiom.getIndividualsInSignature()) {
									individual = indiv.toString();
									individual = individual.substring(1, individual.length() - 1);
									if (!property.isEmpty()) {
										Fact fact = new Fact(iri, IRI.create(property), individual);
										facts.add(fact);
									}// end inner if
								}// end foreach individual
							}// end if
						}// end foreach axiom
					}// end foreach class signature
					for (Fact fact : facts) {
						String property = fact.getPredicate().toString();
						if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
							String value = fact.getObject();
							ret.addProperty(property, value);
						}
					}// end foreach property
					return ret;
				}

			} else {// individual
				facts = getFacts(iri);
				for (Fact fact : facts) {
					String property = fact.getPredicate().toString();
					if (!property.equalsIgnoreCase(NLResourceManager.isAIndiv)) {
						String value = fact.getObject();
						ret.addProperty(property, value);
					}
				}
				return ret;
			}

		}
		// an den iparxei return ti riza
		return new ComparisonNode(root);
	}

	public void remove(ComparisonNode node) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).equals(node)) {
				if (nodes.get(i).getChildren().isEmpty()) {
					nodes.remove(nodes.get(i));
				} else {
					for (int j = 0; j < nodes.get(i).getChildren().size(); j++) {
						remove(nodes.get(i).getChildren().get(j));
					}
					nodes.remove(nodes.get(i));
				}
			}
		}
	}

	public ArrayList<ComparisonNode> getNodes() {
		return nodes;
	}
}