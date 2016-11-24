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
package gr.aueb.cs.nlg.NLGEngine;

import java.util.ArrayList;
import java.util.HashSet;

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.NLFiles.NLResourceManager;
import gr.aueb.cs.nlg.Utils.XmlMsgs;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

//GRE: Generate Referring Expressions
public class ReferringExpressionsGenerator extends NLGEngineComponent {

    private HashSet<String> introducedEntities;

    public ReferringExpressionsGenerator(String lang) {
        super(lang);
        introducedEntities = new HashSet<String>();
    }

    public XmlMsgs generateReferringExpressions(XmlMsgs messages) {
        introducedEntities = new HashSet<String>();
        Document doc = messages.getXMLTree();

        if (Languages.isEnglish(getLanguage())) {// english
            //System.out.println("GRE....English");     

            Node previousOwnerMessage = null;

            ArrayList<Node> owners = messages.returnMatchedNodes(XmlMsgs.prefix, XmlMsgs.OWNER_TAG);
            boolean previousAggAllowed = false;

            for (int i = 0; i < owners.size(); i++) {// for each owner
                Node currentOwner = owners.get(i);
                String ownerRef = XmlMsgs.getAttribute(currentOwner.getParentNode(), XmlMsgs.prefix, XmlMsgs.REF);
                String fillerRef = XmlMsgs.getAttribute(currentOwner.getParentNode(), XmlMsgs.prefix, XmlMsgs.VALUE);

                Node currentOwnerMessage = currentOwner.getParentNode();
                if (currentOwnerMessage.equals(previousOwnerMessage)) {
                    previousAggAllowed = true;
                } else {
                    Node previousMessage = currentOwner.getParentNode().getPreviousSibling();

                    if (previousMessage == null) {
                        previousAggAllowed = true;
                    } else if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED).compareTo("true") == 0) {
                        previousAggAllowed = true;
                    } else {
                        previousAggAllowed = false;
                    }
                }

                if ((!introducedEntities.contains(ownerRef))) {
                    messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                }//!visited
                //else if(!previousAggAllowed)
                //{
                //    MyXmlMsgs.SetAttr((Element)current_owner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel2);                            
                //}
                else {//visited
                    Node previousMessage = currentOwner.getParentNode().getPreviousSibling();
                    int prevLevel = 0;

                    if (currentOwnerMessage.equals(previousOwnerMessage)) {
                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                        //MyXmlMsgs.replaceWithPronoun(current_owner, XmlMsgs.getAttribute(current_owner,XmlMsgs.prefix,XmlMsgs.CASE_TAG));                                
                    } else {
                        if (previousMessage != null) {
                            prevLevel = Integer.parseInt(XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.LEVEL));
                        }

                        int level = -1;

                        // 15/4/2008 changed
                        if (XmlMsgs.getAttribute(currentOwnerMessage, XmlMsgs.prefix, XmlMsgs.LEVEL) == null || XmlMsgs.getAttribute(currentOwnerMessage, XmlMsgs.prefix, XmlMsgs.LEVEL).isEmpty()) {
                            level = 1;
                        } else {
                            level = Integer.parseInt(XmlMsgs.getAttribute(currentOwnerMessage, XmlMsgs.prefix, XmlMsgs.LEVEL));
                        }

                        if (prevLevel == 0 && level == 1) {
                            messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel4);
                        } else if (prevLevel > 0) {
                            if (level < prevLevel) {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                            }
                            if (level > prevLevel) {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                            } else if (level == prevLevel) {
                                if (!previousAggAllowed) {
                                    if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST).compareTo("true") == 0) {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                                    } else {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    }
                                } else {
                                    if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST).compareTo("true") == 0) {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    } else {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    }
                                }
                            }
                        }
                    }
                }

                introducedEntities.add(ownerRef);
                introducedEntities.add(fillerRef);

                previousOwnerMessage = currentOwnerMessage;
            }// for each owner

            messages.setXMLTree(doc);
        }// english
        else if (Languages.isGreek(getLanguage())) {// greek		
            ArrayList<?> owners = messages.returnMatchedNodes(XmlMsgs.prefix, XmlMsgs.OWNER_TAG);

            boolean previousAggAllowed = false;
            Node previousOwnerMessage = null;

            for (int i = 0; i < owners.size(); i++) {

                Node currentOwner = (Node) owners.get(i);

                String ownerRef = XmlMsgs.getAttribute(currentOwner.getParentNode(), XmlMsgs.prefix, XmlMsgs.REF);
                String fillerRef = XmlMsgs.getAttribute(currentOwner.getParentNode(), XmlMsgs.prefix, XmlMsgs.VALUE);

                Node currentOwnerMessage = currentOwner.getParentNode();

                if (currentOwnerMessage.equals(previousOwnerMessage)) {
                    previousAggAllowed = true;
                } else {
                    Node previousMessage = currentOwner.getParentNode().getPreviousSibling();

                    if (previousMessage == null) {
                        previousAggAllowed = true;
                    } else if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.AGGREG_ALLOWED).compareTo("true") == 0) {
                        previousAggAllowed = true;
                    } else {
                        previousAggAllowed = false;
                    }
                }

                if ((!introducedEntities.contains(ownerRef))) {
                    messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                }//!visited
                else {//visited
                    Node previousMessage = currentOwner.getParentNode().getPreviousSibling();
                    int preLevel = 0;

                    if (currentOwnerMessage.equals(previousOwnerMessage)) {
                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                    } else// current_Owner_Msg!=Previous_Owner_Msg
                    {
                        if (previousMessage != null) {
                            preLevel = Integer.parseInt(XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.LEVEL));
                        }

                        int level = Integer.parseInt(XmlMsgs.getAttribute(currentOwnerMessage, XmlMsgs.prefix, XmlMsgs.LEVEL));

                        if (preLevel == 0) {
                            if (level == 1)// epistrofh apo ton geniko typo
                            {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel4);
                            } else // synexizoume ston geniko typo
                            {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                            }
                        } else if (preLevel > 0) {
                            if (level < preLevel)// epistrofh apo katwtero level 
                            {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                            } else if (level > preLevel) // paw se katwtero level
                            {
                                messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel2);
                            } else if (level == preLevel) {
                                if (!previousAggAllowed) {
                                    if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST).compareTo("true") == 0) {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel3);
                                    } else {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    }
                                } else {
                                    if (XmlMsgs.getAttribute(previousMessage, XmlMsgs.prefix, XmlMsgs.FOCUS_LOST).compareTo("true") == 0) {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    } else {
                                        messages.setAttr((Element) currentOwner, NLResourceManager.nlowlNS, XmlMsgs.prefix, XmlMsgs.RE_FOCUS, XmlMsgs.FOCUSLevel1);
                                    }
                                }
                            }
                        }
                    }

                }

                introducedEntities.add(ownerRef);
                introducedEntities.add(fillerRef);

                previousOwnerMessage = currentOwnerMessage;
            }//for
        }// greek
        return messages;
    }//GenerateReferringExpressions
}//GRE
