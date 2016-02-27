package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.commons.util.CiConstants;
import org.commons.util.Formatter;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class AgeOfMembers extends AbstractStatCallback {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AgeOfMembers.class);

	enum Ages {
		NULL("Age non renseign" + Formatter._Character.EACUTE), //
		PLAGE_18_29("De 18 " + Formatter._Character.AGRAVE + " 29 ans"), //
		PLAGE_30_39("De 30 " + Formatter._Character.AGRAVE + " 39 ans"), //
		PLAGE_40_49("De 40 " + Formatter._Character.AGRAVE + " 49 ans"), //
		PLAGE_50_59("De 50 " + Formatter._Character.AGRAVE + " 59 ans"), //
		PLAGE_60_69("De 60 " + Formatter._Character.AGRAVE + " 69 ans"), //
		PLAGE_OVER_70("Plus de 70 ans");

		private String label;

		private Ages(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	};

	@Override
	public Object getData(JCRSessionWrapper session, RenderContext context) {
		Map<Ages, Integer> memberAgeEndMap = new HashMap<Ages, Integer>();
		Map<String, Integer> memberAgeEndMap2 = new HashMap<String, Integer>();
		NodeIterator nodes = getMembersNodes(session, context, null, MEMBER_WHATEVER_THE_LOCK);

		if (nodes != null) {

			// initialisation des tableaux
			for (Ages age1 : Ages.values()) {
				memberAgeEndMap.put(age1, 0);
			}

			while (nodes.hasNext()) {
				JCRNodeWrapper node = (JCRNodeWrapper) nodes.next();

				String userAge = null;

				String blockMember = null;
				try {
					blockMember = node.getProperty(CiConstants.PROPERTIES_USER_ACCOUNT_LOCKED).getString();
				} catch (RepositoryException e1) {
					LOGGER.error("Problem when accessing " + CiConstants.PROPERTIES_USER_ACCOUNT_LOCKED + " property on user " + node);
				}

				if (blockMember == "false") {

					try {
						if (node.hasProperty(CiConstants.PARAM_USER_BIRTH_DATE)) {
							userAge = node.getProperty(CiConstants.PARAM_USER_BIRTH_DATE).getString();

							if (userAge == null)
								continue;
							
							SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
							Date birthDate = null;
							
							if (LOGGER.isDebugEnabled())
								LOGGER.debug("Added new age in list : " + node.getName() + "[" + userAge + "]");
							
							try {
								birthDate = sdf.parse(userAge);
							} catch (ParseException e) {
								continue;
							}

							Calendar birthEndDate = Calendar.getInstance();
							birthEndDate.setTime(birthDate);
							
							// recherche de l'âge du membre
							int daysdiff = CalendarUtil.getDaysBetween(birthEndDate.getTime(), new Date());
							int ageMember = daysdiff / 365;

							switch (ageMember) {
							case 0:
								memberAgeEndMap.put(Ages.NULL, memberAgeEndMap.get(Ages.NULL) + 1);
								break;
							case 18:
							case 19:
							case 20:
							case 21:
							case 22:
							case 23:
							case 24:
							case 25:
							case 26:
							case 27:
							case 28:
							case 29:
								memberAgeEndMap.put(Ages.PLAGE_18_29, memberAgeEndMap.get(Ages.PLAGE_18_29) + 1);
								break;
							case 30:
							case 31:
							case 32:
							case 33:
							case 34:
							case 35:
							case 36:
							case 37:
							case 38:
							case 39:
								memberAgeEndMap.put(Ages.PLAGE_30_39, memberAgeEndMap.get(Ages.PLAGE_30_39) + 1);
								break;
							case 40:
							case 41:
							case 42:
							case 43:
							case 44:
							case 45:
							case 46:
							case 47:
							case 48:
							case 49:
								memberAgeEndMap.put(Ages.PLAGE_40_49, memberAgeEndMap.get(Ages.PLAGE_40_49) + 1);
								break;
							case 50:
							case 51:
							case 52:
							case 53:
							case 54:
							case 55:
							case 56:
							case 57:
							case 58:
							case 59:
								memberAgeEndMap.put(Ages.PLAGE_50_59, memberAgeEndMap.get(Ages.PLAGE_50_59) + 1);
								break;
							case 60:
							case 61:
							case 62:
							case 63:
							case 64:
							case 65:
							case 66:
							case 67:
							case 68:
							case 69:
								memberAgeEndMap.put(Ages.PLAGE_60_69, memberAgeEndMap.get(Ages.PLAGE_60_69) + 1);
								break;
							default:
								memberAgeEndMap.put(Ages.PLAGE_OVER_70, memberAgeEndMap.get(Ages.PLAGE_OVER_70) + 1);
								break;
							}
						} else {
							memberAgeEndMap.put(Ages.NULL, memberAgeEndMap.get(Ages.NULL) + 1);
						}

					} catch (RepositoryException e) {
						LOGGER.error("Error on getting age for member : " + node);
					}
				}
			}
		}

		// ordonne la map
		Set<Ages> cpKeySet = new TreeSet<Ages>(Collections.reverseOrder(new ValueComparator(memberAgeEndMap)));
		cpKeySet.addAll(memberAgeEndMap.keySet());
		// créer la liste
		for (Ages age1 : cpKeySet) {
			String key = age1.toString();
			memberAgeEndMap2.put(key, memberAgeEndMap.get(age1));
		}

		// retour de la map
		return memberAgeEndMap2;
	}

	// ordonne une map
	static class ValueComparator implements Comparator<Object> {
		private Map<? extends Object, Integer> map;

		public ValueComparator(Map<? extends Object, Integer> base) {
			this.map = base;
		}

		public int compare(Object a, Object b) {
			if (map.get(a).equals(map.get(b))) {
				if (a.equals(b)) {
					return 0;
				} else {
					return String.valueOf(b).compareTo(String.valueOf(a));
				}
			}
			return (Integer) map.get(a).compareTo((Integer) map.get(b));
		}

		@Override
		public boolean equals(Object arg0) {
			return super.equals(arg0);
		}
	}

}
