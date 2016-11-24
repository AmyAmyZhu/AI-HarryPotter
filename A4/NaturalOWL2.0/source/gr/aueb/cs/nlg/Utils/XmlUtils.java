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

import java.io.ByteArrayOutputStream;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlUtils {

    public XmlUtils() {
    }

    // return a string representation of the xml document 
    public static String getStringDescription(Node nd, boolean indent) {
        return getStringDescription(nd, indent, "UTF-8");
    }//getStringDescription	

    public static String getStringDescription(Node nd, boolean indent, String encoding) {
        try {
            OutputFormat OutFrmt = null;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XmlDocumentCreator xml_doc_crtr = new XmlDocumentCreator();
            Document doc = xml_doc_crtr.getNewDocument();
            Node copy_of_nd = doc.importNode(nd, true);
            doc.appendChild(copy_of_nd);

            OutFrmt = new OutputFormat(doc, encoding, indent);

            OutFrmt.setIndenting(indent);

            OutFrmt.setEncoding(encoding);
            XMLSerializer xmlsrz = new XMLSerializer(os, OutFrmt);
            xmlsrz.serialize(doc);
            return new String(os.toByteArray(), Charset.forName(encoding));
        }//try
        catch (UnsupportedCharsetException e) {
            return "Error: UnsupportedCharsetException";
        } catch (Exception e) {
            return "Error";
        }//catch
    }//getStringDescription
}
