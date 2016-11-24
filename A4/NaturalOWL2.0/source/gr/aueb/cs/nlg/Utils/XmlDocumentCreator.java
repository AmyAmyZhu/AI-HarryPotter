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
package gr.aueb.cs.nlg.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class XmlDocumentCreator {

    private DocumentBuilder docBuilder;

    public XmlDocumentCreator() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
        }
    }

    // return a new XML document
    public Document getNewDocument() {
        Document doc = docBuilder.newDocument();
        return doc;
    }

    // parse the file
    public Document parse(File f) {
        try {
            return docBuilder.parse(f);
        } catch (org.xml.sax.SAXException e) {
        } catch (IOException e) {
        }
        return null;
    }

    // parse the file
    public Document parse(InputStream IS) {
        try {
            return docBuilder.parse(IS);
        } catch (org.xml.sax.SAXException e) {
        } catch (IOException e) {
        }
        return null;
    }
}