package org.jahia.modules.ci.tools.moderator.extraction.stats.localities;

import org.commons.util.Formatter;

public enum Region {

	ALSACE						(1, "FR-A", "Alsace"), //
	AQUITAINE					(2, "FR-B", "Aquitaine"), //
	AUVERGNE					(3, "FR-C", "Auvergne"), //
	BASSE_NORMANDIE				(4, "FR-P", "Basse-Normandie"), //
	BOURGOGNE					(5, "FR-D", "Bourgogne"), //
	BRETAGNE					(6, "FR-E", "Bretagne"), //
	CENTRE						(7, "FR-F", "Centre"), //
	CHAMPAGNE_ARDENNE			(8, "FR-G", "Champagne-Ardenne"), //
	CORSE						(9, "FR-H", "Corse"), //
	FRANCHE_COMPTE				(10, "FR-I", "Franche-Comt" + Formatter._Character.EACUTE), //
	HAUTE_NORMANDIE				(11, "FR-Q", "Haute-Normandie"), //
	IDF							(12, "FR-J", Formatter._Character.ICIRC + "le-de-France"), //
	LANGUEDOC_ROUSSILLON		(13, "FR-K", "Languedoc-Roussillon"), //
	LIMOUSIN					(14, "FR-L", "Limousin"), //
	LORRAINE					(15, "FR-M", "Lorraine"), //
	MIDI_PYRENEES				(16, "FR-N", "Midi-Pyr" + Formatter._Character.EACUTE + "n" + Formatter._Character.EACUTE + "es"), //
	NORD_PAS_DE_CALAIS			(17, "FR-O", "Nord-pas-de-Calais"), //
	PAYS_DE_LOIRE				(18, "FR-R", "Pays de la Loire"), //
	PICARDIE					(19, "FR-S", "Picardie"), //
	POITOU_CHARENTE				(20, "FR-T", "Poitou-Charentes"), //
	PACA						(21, "FR-U", "Provence-Alpes-C" + Formatter._Character.OCIRC + "te-d'Azur"), //
	RHONE_ALPES					(22, "FR-V", "Rh" + Formatter._Character.OCIRC + "ne-Alpes"), //

	/* DOM */
	GUADELOUPE					(23, "FR-GP", "Guadeloupe"), //
	GUYANE						(24, "FR-GF", "Guyane"), //
	MARTINIQUE					(24, "FR-MQ", "Martinique"), //
	MAYOTTE						(25, "FR-YT", "Mayotte"), //
	REUNION						(26, "FR-RE", "La R" + Formatter._Character.EACUTE + "union"),

	/* TOM */
	SAINT_BARTHELEMY			(27, "FR-BL", "Saint-Barth" + Formatter._Character.EACUTE + "lemy"), //
	SAINT_MARTIN				(28, "FR-MF", "Saint-Martin"), //
	NEW_CALEDONIA				(30, "FR-NC", "Nouvelle-Cal" + Formatter._Character.EACUTE + "donie"), //
	FRENCH_POLYNESIA			(31, "FR-PF", "Polyn" + Formatter._Character.EACUTE + "sie française"), //
	SAINT_PIERRE_AND_MIQUELON	(32, "FR-PM", "Saint-Pierre-et-Miquelon"), //
	FRENCH_SOUTHERN_TERRITORIES	(33, "FR-TF", "Terres australes françaises"), //
	WALLIS_AND_FUTUNA			(34, "FR-WF", "Wallis-et-Futuna"), //
	CLIPPERTON					(35, "FR-CP", "Clipperton");

	private int index;
	private String label;
	private String frCode;

	private Region(int index, String frCode, String label) {
		this.index = index;
		this.frCode = frCode;
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Code from ISO 3166-2 French codes.
	 * 
	 * @see http://en.wikipedia.org/wiki/ISO_3166-2:FR
	 */
	public String getFrCode() {
		return frCode;
	}

}