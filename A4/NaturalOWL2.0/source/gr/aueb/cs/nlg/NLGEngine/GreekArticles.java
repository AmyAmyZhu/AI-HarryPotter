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

import gr.aueb.cs.nlg.NLFiles.LexEntry;

import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class GreekArticles {

    public static final String defMasculineSingularNominative = "o";
    public static final String defMasculineSingularGenitive = "του";
    public static final String defMasculineSingularAccusative = "τον";
    public static final String defMasculinePluralNominative = "οι";
    public static final String defMasculinePluralGenitive = "των";
    public static final String defMasculinePluralAccusative = "τους";
    public static final String defFeminineSingularNominative = "η";
    public static final String defFeminineSingularGenitive = "της";
    public static final String defFeminineSingularAccusative = "την";
    public static final String defFemininePluralNominative = "οι";
    public static final String defFemininePluralGenitive = "των";
    public static final String defFemininePluralAccusative = "τις";
    public static final String defNeuterSingularNominative = "το";
    public static final String defNeuterSingularGenitive = "του";
    public static final String defNeuterSingularAccusative = "το";
    public static final String defNeuterPluralNominative = "τα";
    public static final String defNeuterPluralGenitive = "των";
    public static final String defNeuterPluralAccusative = "τα";
    public static final String defMasculineOrFeminineSingularNominative = "o/η";
    public static final String defMasculineOrFeminineSingularGenitive = "του/της";
    public static final String defMasculineOrFeminineSingularAccusative = "τον/την";
    public static final String defMasculineOrFemininePluralNominative = "οι";
    public static final String defMasculineOrFemininePluralGenitive = "των";
    public static final String defMasculineOrFemininePluralAccusative = "τους/τις";
    public static final String indefMasculineSingularNominative = "ένας";
    public static final String indefMasculineSingularGenitive = "ενός";
    public static final String indefMasculineSingularAccusative = "ένα";
    public static final String indefFeminineSingularNominative = "μία";
    public static final String indefFeminineSingularGenitive = "μίας";
    public static final String indefFeminineSingularAccusative = "μία";
    public static final String indefNeuterSingularNominative = "ένα";
    public static final String indefNeuterSingularGenitive = "ενός";
    public static final String indefNeuterSingularAccusative = "ένα";
    public static final String indefMasculineOrFeminineSingularNominative = "ένας/μία";
    public static final String indefMasculineOrFeminineSingularGenitive = "ενός/μίας";
    public static final String indefMasculineOrFeminineSingularAccusative = "ένα/μία";

    public GreekArticles() {
    }

    public static String getPrepositionalPhrase(String gender, String num, String Case, String next_text) {
        return "σ" + getDefiniteArticle(gender, num, Case, next_text);
    }

    public static String getDefiniteArticle(String gender, String num, String caseType, String nextText) {
        String ret = "";
        if (num.equals(XmlMsgs.SINGULAR)) {
            if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defMasculineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defMasculineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defMasculineSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defFeminineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defFeminineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defFeminineSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defNeuterSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defNeuterSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defNeuterSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFeminineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFeminineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFeminineSingularAccusative;
                }
            }
        } else if (num.equals(XmlMsgs.PLURAL)) {
            if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defMasculinePluralNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defMasculinePluralGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defMasculinePluralAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defFemininePluralNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defFemininePluralGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defFemininePluralAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defNeuterPluralNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defNeuterPluralGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defNeuterPluralAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFemininePluralNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFemininePluralGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.defMasculineOrFemininePluralAccusative;
                }
            }
        } else {
            System.out.println("No number defined...");
        }

        if (!ret.equals("των") && ret.endsWith("ν")) {
            if (!(nextText.startsWith("κ") || nextText.startsWith("π") || nextText.startsWith("τ")
                    || nextText.startsWith("γκ") || nextText.startsWith("μπ") || nextText.startsWith("ντ")
                    || nextText.startsWith("τσ") || nextText.startsWith("τζ") || nextText.startsWith("ξ")
                    || nextText.startsWith("ψ") || nextText.startsWith("Κ") || nextText.startsWith("Π")
                    || nextText.startsWith("Τ") || nextText.startsWith("Γκ") || nextText.startsWith("Μπ")
                    || nextText.startsWith("Ντ") || nextText.startsWith("Τσ") || nextText.startsWith("Τζ")
                    || nextText.startsWith("Ξ") || nextText.startsWith("Ψ") || nextText.startsWith("ΓΚ")
                    || nextText.startsWith("ΜΠ") || nextText.startsWith("ΝΤ") || nextText.startsWith("ΤΣ")
                    || nextText.startsWith("ΤΖ") || nextText.startsWith("Δ") || nextText.startsWith("δ")
                    || nextText.startsWith("α") || nextText.startsWith("ε")
                    || nextText.startsWith("ο") || nextText.startsWith("ω") || nextText.startsWith("ι")
                    || nextText.startsWith("η") || nextText.startsWith("υ")
                    || nextText.startsWith("Α") || nextText.startsWith("Ε")
                    || nextText.startsWith("Ο") || nextText.startsWith("Ω") || nextText.startsWith("Ι")
                    || nextText.startsWith("Η") || nextText.startsWith("Υ")
                    || nextText.startsWith("ά") || nextText.startsWith("έ")
                    || nextText.startsWith("ό") || nextText.startsWith("ώ") || nextText.startsWith("ί")
                    || nextText.startsWith("ή") || nextText.startsWith("ύ")
                    || nextText.startsWith("Ά") || nextText.startsWith("Έ")
                    || nextText.startsWith("Ό") || nextText.startsWith("Ώ") || nextText.startsWith("Ί")
                    || nextText.startsWith("Ή") || nextText.startsWith("Ύ"))) {
                ret = ret.substring(0, ret.length() - 1);
            }
        }

        return ret;
    }

    public static String getIndefiniteArticle(String gender, String num, String caseType) {
        String ret = "";
        if (num.equals(XmlMsgs.SINGULAR)) {
            if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.indefMasculineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.indefMasculineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.indefMasculineSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.indefFeminineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.indefFeminineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.indefFeminineSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.indefNeuterSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.indefNeuterSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.indefNeuterSingularAccusative;
                }
            } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    ret = GreekArticles.indefMasculineOrFeminineSingularNominative;
                } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                    ret = GreekArticles.indefMasculineOrFeminineSingularGenitive;
                } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    ret = GreekArticles.indefMasculineOrFeminineSingularAccusative;
                }
            }
        } else if (num.equals(XmlMsgs.PLURAL)) {
            ret = "";
        } else {
            System.out.println("No number defined...");
        }

        return ret;
    }

    public static String getPronoun(String gender, String number, String caseType, boolean withArticle) {
        if (number.equals(XmlMsgs.SINGULAR)) {
            if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτός";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτόν";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτός" + " " + "ο";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού" + " " + "του";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτόν" + " " + "τον";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτή";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτής";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτήν";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτή" + " " + "η";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτής" + " " + "της";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτήν" + " " + "την";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτό";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού του";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτού του";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτό" + " " + "το";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού" + " " + "του";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτό" + " " + "το";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτός/αυτή";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού/αυτής";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτόν/αυτήν";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτός ο/αυτή η";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού του/αυτής της";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτόν τον/αυτήν την";
                    }
                }
            } else {
                if (!withArticle) {
                    return "αυτός/αυτή/αυτό";
                }
                return "αυτός το/αυτή η/αυτό το";
            }
        } else if (number.equals(XmlMsgs.PLURAL)) {
            if (gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτοί";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτοί";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτοί" + " " + "οι";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών" + " " + "των";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτοί" + " " + "οι";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_FEMININE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτές";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτές";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτές" + " " + "οι";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών" + " " + "των";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτές" + " " + "οι";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_NEUTER)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτό";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτό";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτό" + " " + "το";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτού" + " " + "του";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτό" + " " + "το";
                    }
                }
            } else if (gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (!withArticle) {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτοί/αυτές";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτοί/αυτές";
                    }
                } else {
                    if (caseType.equals(XmlMsgs.NOMINATIVE_TAG)) {
                        return "αυτοί/αυτές" + " " + "οι";
                    } else if (caseType.equals(XmlMsgs.GENITIVE_TAG)) {
                        return "αυτών" + " " + "των";
                    } else if (caseType.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                        return "αυτοί/αυτές" + " " + "οι";
                    }
                }
            } else {
                if (!withArticle) {
                    return "αυτοί/αυτές/αυτά";
                }
                return "αυτοί οι/αυτές οι/αυτά τα";
            }
        } else {
            return "αυτοί/αυτές/αυτά";
        }

        return "ERROR";
    }

    public static String getPronounSpecific(String Gender, String number, String Case) {
        if (number.equals(XmlMsgs.SINGULAR)) {
            if (Gender.equals(LexEntry.GENDER_MASCULINE)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένος";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένου";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένο";
                }
            } else if (Gender.equals(LexEntry.GENDER_FEMININE)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένη";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένης";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένη";
                }
            } else if (Gender.equals(LexEntry.GENDER_NEUTER)) {

                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένο";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένου";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένο";
                }

            } else if (Gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένος/η";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένου/ης";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένο/η";
                }
            } else {
                return "αυτός/αυτή/αυτό";
            }
        } else if (number.equals(XmlMsgs.PLURAL)) {
            if (Gender.equals(LexEntry.GENDER_MASCULINE)) {

                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένοι";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένων";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένους";
                }
            } else if (Gender.equals(LexEntry.GENDER_FEMININE)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένες";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένων";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένες";
                }
            } else if (Gender.equals(LexEntry.GENDER_NEUTER)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένα";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένων";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένα";
                }

            } else if (Gender.equals(LexEntry.GENDER_MASCULINE_OR_FEMININE)) {
                if (Case.equals(XmlMsgs.NOMINATIVE_TAG)) {
                    return "συγκεκριμένοι/ες";
                } else if (Case.equals(XmlMsgs.GENITIVE_TAG)) {
                    return "συγκεκριμένων";
                } else if (Case.equals(XmlMsgs.ACCUSATIVE_TAG)) {
                    return "συγκεκριμένοι/ες";
                }
            } else {
                return "αυτοί/αυτές/αυτά";

            }
        }

        return "ERROR";
    }
}