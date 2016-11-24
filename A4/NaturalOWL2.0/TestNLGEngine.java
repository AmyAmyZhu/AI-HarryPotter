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

import gr.aueb.cs.nlg.NLGEngine.NLGEngine;

import gr.aueb.cs.nlg.Languages.Languages;
import gr.aueb.cs.nlg.Utils.NLGUser;
import org.semanticweb.owlapi.model.IRI;

public class TestNLGEngine {

    public static void main(String args[]) {
        //Set the paths of the domain ontology and the NL resources ontology
        String owlPath = "MPIRO Ontologies\\mpiro.owl";
        String NLResourcePath = "MPIRO Ontologies\\NLresources.owl";

        //Initializing the NLGEngine
        //You can set the language to either Languages.ENGLISH or Languages.GREEK
        NLGEngine myEngine = new NLGEngine(owlPath, NLResourcePath, Languages.ENGLISH);

        //IRI specifying the user type, as set in the NL resources.
        IRI userModelIRI = IRI.create("http://www.aueb.gr/users/ion/nlowl/dgr#ExpertAdult");
        //String specifying the userID of the user.
        String userID = "123";

        //Create a new user
        NLGUser user = new NLGUser(userID, myEngine.getUserModel(userModelIRI));

        //Set whether or not comparisons to previously generated texts should also be included in the generated text
        myEngine.setAllowComparisons(true);
        //If comparisons are to be generated, the generation tree must be build
        myEngine.buildTree();

        /* The depth in the graph of the ontology we are allowed to go in content selection 
         * when describing instances. A depth of 1 will produce a text conveying only properties
         * of the instance being described. Larger depth values will produce texts conveying 
         * also properties of other related instances (e.g., “This lekythos was created in the 
         * classical period. The classical period was…”).
         */
        int depth = 2;

        //IRI specifying the individual or class (of the domain ontology) whose description we want to generate.
        IRI indivIRI = IRI.create("http://www.aueb.gr/users/ion/mpiro.owl#exhibit6");

        //Generate the text
        String result[] = myEngine.generateDescription(indivIRI, user, depth);

        System.out.println("-------------------PIPELINE------------------------");
        /* The result array consists of 3 strings
         * The first string contains the produced text along with
         * the outputs of the intermediate stages of the NLG engine
         */
        System.out.println(result[0]);

        System.out.println("---------------------------------------------------");
        //The second string contains only the produced text 
        System.out.println(result[1]);

        System.out.println("---------------------------------------------------");
        //The third string contains semantic-linguistic annotations of the produced text
        System.out.println(result[2]);

        //You can choose a generation engine amongst: NLGEngine.PIPELINE_MODEL, NLGEngine.ENGINE_ILP_MODEL, NLGEngine.ENGINE_ILP_APPROXIMATION_MODEL
        //By default the engine used is the NLGEngine.PIPELINE_MODEL
        myEngine.setUseEngine(NLGEngine.ENGINE_ILP_MODEL);

        //ILP model parameter, weighing the number of selected facts versus the length of the resulting text
        double lamda = 0.5;

        /* ILP model parameters specifying the text length (in sentences) and the sentence length (in slots)
         * Unlike the pipeline model, the ILP models do not take these values from the user model
         * 
         * The ENGINE_ILP_MODEL's solving speed grows exponentially to the text length, for values greater 
         * than 3 we advise you use the ENGINE_ILP_APPROXIMATION_MODEL 
         */
        int textLength = 3;
        int slotLength = 10;

        try {
            result = myEngine.generateDescription(indivIRI, user, 1, lamda, textLength, slotLength);
        } catch (java.lang.UnsatisfiedLinkError e) {
            result = new String[3];

            for (int i = 0; i < 3; i++) {
                result[i] = "*NULL*";
            }
            System.out.println("The GNU Linear Programming Kit is not installed properly. See the README file for details.");
        } catch (java.lang.NoClassDefFoundError e) {
            result = new String[3];

            for (int i = 0; i < 3; i++) {
                result[i] = "*NULL*";
            }
            System.out.println("The GNU Linear Programming Kit is not installed properly. See the README file for details.");
        }

        System.out.println("-------------------ILPMODEL------------------------");
        /* The result array consists of 3 strings
         * The first string contains the produced text along with
         * the outputs of the intermediate stages of the NLG engine
         */
        System.out.println(result[0]);

        System.out.println("---------------------------------------------------");
        //The second string contains only the produced text 
        System.out.println(result[1]);

        System.out.println("---------------------------------------------------");
        //The third string contains semantic-linguistic annotations of the produced text
        System.out.println(result[2]);
    }
}
