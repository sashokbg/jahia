package org.jahia.modules.ci.tools.moderator.extraction.stats.localities;

import org.commons.util.Formatter;

public enum Department {
	AIN								("01", "Ain", Region.RHONE_ALPES), //
	AISNE							("02", "Aisne", Region.PICARDIE), //
	ALLIER							("03", "Allier", Region.AUVERGNE), //
	ALPES_DE_HAUTE_PROVENCE			("04", "Alpes-de-Haute-Provence", Region.PACA), //
	HAUTES_ALPES					("05", "Hautes-Alpes", Region.PACA), //
	ALPES_MARITIMES					("06", "Alpes-Maritimes", Region.PACA), //
	ARDECHE							("07", "Ard" + Formatter._Character.EGRAVE + "che", Region.RHONE_ALPES), //
	ARDENNES						("08", "Ardennes", Region.CHAMPAGNE_ARDENNE), //
	ARIEGE							("09", "Ari" + Formatter._Character.EGRAVE + "ge", Region.MIDI_PYRENEES), //
	AUBE							("10", "Aube", Region.CHAMPAGNE_ARDENNE), //
	AUDE							("11", "Aude", Region.LANGUEDOC_ROUSSILLON), //
	AVEYRON							("12", "Aveyron", Region.MIDI_PYRENEES), //
	BOUCHES_DU_RHONE				("13", "Bouches-du-Rh" + Formatter._Character.OCIRC + "ne", Region.PACA), //
	CALVADOS						("14", "Calvados", Region.BASSE_NORMANDIE), //
	CANTAL							("15", "Cantal", Region.AUVERGNE), //
	CHARENTE						("16", "Charente", Region.POITOU_CHARENTE), //
	CHARENTE_MARITIME				("17", "Charente-Maritime", Region.POITOU_CHARENTE), //
	CHER							("18", "Cher", Region.CENTRE), //
	CORREZE							("19", "Corr" + Formatter._Character.EGRAVE + "ze", Region.LIMOUSIN), //

	/*
	 * les codes postaux commencent par 20 mais le numrero de departement
	 * est 2A
	 */
	CORSE_DU_SUD					("20", "Corse-du-Sud", Region.CORSE), //
	/*
	 * les codes postaux commencent par 20 mais le numrero de departement
	 * est 2B
	 */
	HAUTE_CORSE						("20", "Haute-Corse", Region.CORSE), //

	COTE_DOR						("21", "C" + Formatter._Character.OCIRC + "te-d'Or", Region.BOURGOGNE), //
	COTES_DARMOR					("22", "C" + Formatter._Character.OCIRC + "tes-d'Armor", Region.BRETAGNE), //
	CREUSE							("23", "Creuse", Region.LIMOUSIN), //
	DORDOGNE						("24", "Dordogne", Region.AQUITAINE), //
	DOUBS							("25", "Doubs", Region.FRANCHE_COMPTE), //
	DROME							("26", "Dr" + Formatter._Character.OCIRC + "me", Region.RHONE_ALPES), //
	EURE							("27", "Eure", Region.HAUTE_NORMANDIE), //
	EURE_ET_LOIR					("28", "Eure-et-Loir", Region.CENTRE), //
	FINISTERE						("29", "Finist" + Formatter._Character.EGRAVE + "re", Region.BRETAGNE), //
	GARD							("30", "Gard", Region.LANGUEDOC_ROUSSILLON), //
	HAUTE_GARONNE					("31", "Haute-Garonne", Region.MIDI_PYRENEES), //
	GERS							("32", "Gers", Region.MIDI_PYRENEES), //
	GIRONDE							("33", "Gironde", Region.AQUITAINE), //
	HERAULT							("34", "H" + Formatter._Character.EACUTE + "rault", Region.LANGUEDOC_ROUSSILLON), //
	ILLE_ET_VILAINE					("35", "Ille-et-Vilaine", Region.BRETAGNE), //
	INDRE							("36", "Indre", Region.CENTRE), //
	INDRE_ET_LOIRE					("37", "Indre-et-Loire", Region.CENTRE), //
	ISERE							("38", "Is" + Formatter._Character.EGRAVE + "re", Region.RHONE_ALPES), //
	JURA							("39", "Jura", Region.FRANCHE_COMPTE), //
	LANDES							("40", "Landes", Region.AQUITAINE), //
	LOIR_ET_CHER					("41", "Loir-et-Cher", Region.CENTRE), //
	LOIRE							("42", "Loire", Region.RHONE_ALPES), //
	HAUTE_LOIRE						("43", "Haute-Loire", Region.AUVERGNE), //
	LOIRE_ATLANTIQUE				("44", "Loire-Atlantique", Region.PAYS_DE_LOIRE), //
	LOIRET							("45", "Loiret", Region.CENTRE), //
	LOT								("46", "Lot", Region.MIDI_PYRENEES), //
	LOT_ET_GARONNE					("47", "Lot-et-Garonne", Region.AQUITAINE), //
	LOZERE							("48", "Loz" + Formatter._Character.EGRAVE + "re", Region.LANGUEDOC_ROUSSILLON), //
	MAINE_ET_LOIRE					("49", "Maine-et-Loire", Region.PAYS_DE_LOIRE), //
	MANCHE							("50", "Manche", Region.BASSE_NORMANDIE), //
	MARNE							("51", "Marne", Region.CHAMPAGNE_ARDENNE), //
	HAUTE_MARNE						("52", "Haute-Marne", Region.CHAMPAGNE_ARDENNE), //
	MAYENNE							("53", "Mayenne", Region.PAYS_DE_LOIRE), //
	MEURTHE_ET_MOSELLE				("54", "Meurthe-et-Moselle", Region.LORRAINE), //
	MEUSE							("55", "Meuse", Region.LORRAINE), //
	MORBIHAN						("56", "Morbihan", Region.BRETAGNE), //
	MOSELLE							("57", "Moselle", Region.LORRAINE), //
	NIEVRE							("58", "Ni" + Formatter._Character.EGRAVE + "vre", Region.BOURGOGNE), //
	NORD							("59", "Nord", Region.NORD_PAS_DE_CALAIS), //
	OISE							("60", "Oise", Region.PICARDIE), //
	ORNE							("61", "Orne", Region.BASSE_NORMANDIE), //
	PAS_DE_CALAIS					("62", "Pas-de-Calais", Region.NORD_PAS_DE_CALAIS), //
	PUY_DE_DOME						("63", "Puy-de-D" + Formatter._Character.OCIRC + "me", Region.AUVERGNE), //
	PYRENEES_ATLANTIQUES			("64", "Pyr" + Formatter._Character.EACUTE + "n" + Formatter._Character.EACUTE + "es-Atlantiques", Region.AQUITAINE), //
	HAUTES_PYRENEES					("65", "Hautes-Pyr" + Formatter._Character.EACUTE + "n" + Formatter._Character.EACUTE + "es", Region.MIDI_PYRENEES), //
	PYRENEES_ORIENTALES				("66", "Pyr" + Formatter._Character.EACUTE + "n" + Formatter._Character.EACUTE + "es-Orientales", Region.LANGUEDOC_ROUSSILLON), //
	BAS_RHIN						("67", "Bas-Rhin", Region.ALSACE), //
	HAUT_RHIN						("68", "Haut-Rhin", Region.ALSACE), //
	RHONE							("69", "Rh" + Formatter._Character.OCIRC + "ne", Region.RHONE_ALPES), //
	HAUTE_SAONE						("70", "Haute-Sa" + Formatter._Character.OCIRC + "ne", Region.FRANCHE_COMPTE), //
	SAONE_ET_LOIRE					("71", "Sa" + Formatter._Character.OCIRC + "ne-et-Loire", Region.BOURGOGNE), //
	SARTHE							("72", "Sarthe", Region.PAYS_DE_LOIRE), //
	SAVOIE							("73", "Savoie", Region.RHONE_ALPES), //
	HAUTE_SAVOIE					("74", "Haute-Savoie", Region.RHONE_ALPES), //
	PARIS							("75", "Paris", Region.IDF), //
	SEINE_MARITIME					("76", "Seine-Maritime", Region.HAUTE_NORMANDIE), //
	SEINE_ET_MARNE					("77", "Seine-et-Marne", Region.IDF), //
	YVELINES						("78", "Yvelines", Region.IDF), //
	DEUX_SEVRES						("79", "Deux-S" + Formatter._Character.EGRAVE + "vres", Region.POITOU_CHARENTE), //
	SOMME							("80", "Somme", Region.PICARDIE), //
	TARN							("81", "Tarn", Region.MIDI_PYRENEES), //
	TARN_ET_GARONNE					("82", "Tarn-et-Garonne", Region.MIDI_PYRENEES), //
	VAR								("83", "Var", Region.PACA), //
	VAUCLUSE						("84", "Vaucluse", Region.PACA), //
	VENDEE							("85", "Vend" + Formatter._Character.EACUTE + "e", Region.PAYS_DE_LOIRE), //
	VIENNE							("86", "Vienne", Region.POITOU_CHARENTE), //
	HAUTE_VIENNE					("87", "Haute-Vienne", Region.LIMOUSIN), //
	VOSGES							("88", "Vosges", Region.LORRAINE), //
	YONNE							("89", "Yonne", Region.BOURGOGNE), //
	TERRITOIRE_DE_BELFORT			("90", "Territoire de Belfort", Region.FRANCHE_COMPTE), //
	ESSONNE							("91", "Essonne", Region.IDF), //
	HAUTS_DE_SEINE					("92", "Hauts-de-Seine", Region.IDF), //
	SEINE_SAINT_DENIS				("93", "Seine-Saint-Denis", Region.IDF), //
	VAL_DE_MARNE					("94", "Val-de-Marne", Region.IDF), //
	GUADELOUPE						("971", "Guadeloupe", Region.GUADELOUPE), //
	MARTINIQUE						("972", "Martinique", Region.MARTINIQUE), //
	GUYANNE							("973", "Guyanne", Region.GUYANE), //
	REUNION							("974", "R" + Formatter._Character.EACUTE + "union", Region.REUNION), //
	SAINT_PIERRE_AND_MIQUELON		("975", "Saint-Pierre-et-Miquelon", Region.SAINT_PIERRE_AND_MIQUELON), //
	MAYOTTE							("976", "Mayotte", Region.MAYOTTE), //
	SAINT_BARTHELEMY				("977", "Saint-Barth" + Formatter._Character.EACUTE + "l" + Formatter._Character.EACUTE + "my", Region.SAINT_BARTHELEMY), //
	SAINT_MARTIN					("978", "Saint-Martin", Region.SAINT_MARTIN), //
	TERRES_AUSTRALES_ANTARCTIQUES	("984", "Terres-Australes et Antarctiques", Region.FRENCH_SOUTHERN_TERRITORIES),
	WALLIS_AND_FUTUNA				("986", "Wallis-et-Futuna", Region.WALLIS_AND_FUTUNA),
	POLYNESIE_FRANCAISE				("987", "Polyn" + Formatter._Character.EACUTE + "sie Fran" + Formatter._Character.CCDILLE + "aise", Region.FRENCH_POLYNESIA),
	NOUVELLE_CALEDONIE				("988", "Nouvelle-Cal" + Formatter._Character.EACUTE + "donie", Region.NEW_CALEDONIA);

	private Region region;
	private String departmentNumber;
	private String label;

	private Department(String departmentNumber, String label, Region region) {
		this.region = region;
		this.departmentNumber = departmentNumber;
		this.label = label;
	}

	public static Department getFromDepartmentNumber(String departmentNumber) {
		for (int i = 0; i < Department.values().length; i++) {
			Department dept = Department.values()[i];
			if (dept.departmentNumber.equals(departmentNumber))
				return dept;
		}
		return null;
	}

	public Region getRegion() {
		return region;
	}

	public String getDepartmentNumber() {
		return departmentNumber;
	}

	public String getLabel() {
		return label;
	}

}