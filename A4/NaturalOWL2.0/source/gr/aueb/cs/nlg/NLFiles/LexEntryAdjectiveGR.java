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
package gr.aueb.cs.nlg.NLFiles;

import gr.aueb.cs.nlg.Utils.XmlMsgs;

public class LexEntryAdjectiveGR extends LexEntryAdjective {

    private String singularNomMasc;
    private String singularNomFem;
    private String singularNomNeut;
    private String pluralNomMasc;
    private String pluralNomFem;
    private String pluralNomNeut;
    private String singularGenMasc;
    private String singularGenFem;
    private String singularGenNeut;
    private String pluralGenMasc;
    private String pluralGenFem;
    private String pluralGenNeut;
    private String singularAccMasc;
    private String singularAccFem;
    private String singularAccNeut;
    private String pluralAccMasc;
    private String pluralAccFem;
    private String pluralAccNeut;

    public LexEntryAdjectiveGR(String sm, String sf, String sn, String pm, String pf, String pn) {
        singularNomMasc = sm;
        singularNomFem = sf;
        singularNomNeut = sn;
        pluralNomMasc = pm;
        pluralNomFem = pf;
        pluralNomNeut = pn;

        singularGenMasc = "";
        singularGenFem = "";
        singularGenNeut = "";
        pluralGenMasc = "";
        pluralGenFem = "";
        pluralGenNeut = "";

        singularAccMasc = "";
        singularAccFem = "";
        singularAccNeut = "";
        pluralAccMasc = "";
        pluralAccFem = "";
        pluralAccNeut = "";
    }

    public LexEntryAdjectiveGR(LexEntryAdjectiveGR o) {
        singularNomMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG);
        singularNomFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG);
        singularNomNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.NOMINATIVE_TAG);
        pluralNomMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG);
        pluralNomFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG);
        pluralNomNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.NOMINATIVE_TAG);

        singularGenMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG);
        singularGenFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG);
        singularGenNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.GENITIVE_TAG);
        pluralGenMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG);
        pluralGenFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG);
        pluralGenNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.GENITIVE_TAG);

        singularAccMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG);
        singularAccFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG);
        singularAccNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.SINGULAR, XmlMsgs.ACCUSATIVE_TAG);
        pluralAccMasc = o.get(XmlMsgs.GENDER_MASCULINE, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG);
        pluralAccFem = o.get(XmlMsgs.GENDER_FEMININE, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG);
        pluralAccNeut = o.get(XmlMsgs.GENDER_NEUTER, XmlMsgs.PLURAL, XmlMsgs.ACCUSATIVE_TAG);
    }

    public LexEntryAdjectiveGR() {
        singularNomMasc = "";
        singularNomFem = "";
        singularNomNeut = "";
        pluralNomMasc = "";
        pluralNomFem = "";
        pluralNomNeut = "";

        singularGenMasc = "";
        singularGenFem = "";
        singularGenNeut = "";
        pluralGenMasc = "";
        pluralGenFem = "";
        pluralGenNeut = "";

        singularAccMasc = "";
        singularAccFem = "";
        singularAccNeut = "";
        pluralAccMasc = "";
        pluralAccFem = "";
        pluralAccNeut = "";
    }

    public void setSingularNominativeForms(String masc, String fem, String neut) {
        this.singularNomMasc = masc;
        this.singularNomFem = fem;
        this.singularNomNeut = neut;
    }

    public void setSingularNominativeMasculine(String masc) {
        this.singularNomMasc = masc;
    }

    public void setSingularNominativeFeminine(String fem) {
        this.singularNomFem = fem;
    }

    public void setSingularNominativeNeuter(String neut) {
        this.singularNomNeut = neut;
    }

    public void setPluralNominativeCases(String masc, String fem, String neut) {
        this.pluralNomMasc = masc;
        this.pluralNomFem = fem;
        this.pluralNomNeut = neut;
    }

    public void setPluralNominativeMasculine(String masc) {
        this.pluralNomMasc = masc;
    }

    public void setPluralNominativeFeminine(String fem) {
        this.pluralNomFem = fem;
    }

    public void setPluralNominativeNeuter(String neut) {
        this.pluralNomNeut = neut;
    }

    public void setSingularGenitiveForms(String masc, String fem, String neut) {
        this.singularGenMasc = masc;
        this.singularGenFem = fem;
        this.singularGenNeut = neut;
    }

    public void setSingularGenitiveMasculine(String masc) {
        this.singularGenMasc = masc;
    }

    public void setSingularGenitiveFeminine(String fem) {
        this.singularGenFem = fem;
    }

    public void setSingularGenitiveNeuter(String neut) {
        this.singularGenNeut = neut;
    }

    public void setPluralGenitiveCases(String masc, String fem, String neut) {
        this.pluralGenMasc = masc;
        this.pluralGenFem = fem;
        this.pluralGenNeut = neut;
    }

    public void setPluralGenitiveMasculine(String masc) {
        this.pluralGenMasc = masc;
    }

    public void setPluralGenitiveFeminine(String fem) {
        this.pluralGenFem = fem;
    }

    public void setPluralGenitiveNeuter(String neut) {
        this.pluralGenNeut = neut;
    }

    public void setSingularAccusativeForms(String masc, String fem, String neut) {
        this.singularAccMasc = masc;
        this.singularAccFem = fem;
        this.singularAccNeut = neut;
    }

    public void setSingularAccusativeMasculine(String masc) {
        this.singularAccMasc = masc;
    }

    public void setSingularAccusativeFeminine(String fem) {
        this.singularAccFem = fem;
    }

    public void setSingularAccusativeNeuter(String neut) {
        this.singularAccNeut = neut;
    }

    public void setPluralAccusativeCases(String masc, String fem, String neut) {
        this.pluralAccMasc = masc;
        this.pluralAccFem = fem;
        this.pluralAccNeut = neut;
    }

    public void setPluralAccusativeMasculine(String masc) {
        this.pluralAccMasc = masc;
    }

    public void setPluralAccusativeFeminine(String fem) {
        this.pluralAccFem = fem;
    }

    public void setPluralAccusativeNeuter(String neut) {
        this.pluralAccNeut = neut;
    }

    public String generateMasculineOrFeminineForm(String masc, String fem) {
        for (int i = 1; i < masc.length(); i++) {
            String subString = masc.substring(0, i);

            if (!fem.startsWith(subString)) {
                return masc + "/" + fem.substring(i - 1);
            }
        }
        return masc + "/" + fem;
    }

    @Override
    public String get(String gender, String numb, String Case) {
        if (numb.compareTo(XmlMsgs.SINGULAR) == 0) {
            if (Case.compareTo(XmlMsgs.NOMINATIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.singularNomMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.singularNomFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.singularNomNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.singularNomMasc, this.singularNomFem);
                }
            } else if (Case.compareTo(XmlMsgs.GENITIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.singularGenMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.singularGenFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.singularGenNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.singularGenMasc, this.singularGenFem);
                }
            } else if (Case.compareTo(XmlMsgs.ACCUSATIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.singularAccMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.singularAccFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.singularAccNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.singularAccMasc, this.singularAccFem);
                }
            }
        } else if (numb.compareTo(XmlMsgs.PLURAL) == 0) {
            if (Case.compareTo(XmlMsgs.NOMINATIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.pluralNomMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.pluralNomFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.pluralNomNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.pluralNomMasc, this.pluralNomFem);
                }
            } else if (Case.compareTo(XmlMsgs.GENITIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.pluralGenMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.pluralGenFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.pluralGenNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.pluralGenMasc, this.pluralGenFem);
                }
            } else if (Case.compareTo(XmlMsgs.ACCUSATIVE_TAG) == 0) {
                if (gender.compareTo(XmlMsgs.GENDER_MASCULINE) == 0) {
                    return this.pluralAccMasc;
                } else if (gender.compareTo(XmlMsgs.GENDER_FEMININE) == 0) {
                    return this.pluralAccFem;
                } else if (gender.compareTo(XmlMsgs.GENDER_NEUTER) == 0) {
                    return this.pluralAccNeut;
                } else if (gender.compareTo(XmlMsgs.GENDER_MASCULINE_OR_FEMININE) == 0) {
                    return generateMasculineOrFeminineForm(this.pluralAccMasc, this.pluralAccFem);
                }
            }
        }
        return "";
    }
}