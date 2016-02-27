package org.jahia.modules.ci.tools.moderator.extraction.stats.callbacks;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.commons.util.CiConstants;
import org.jahia.modules.ci.helpers.PageHelper;
import org.jahia.modules.ci.tools.moderator.extraction.ICallback;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.jcr.JCRUser;
import org.slf4j.Logger;

public abstract class AbstractStatCallback implements ICallback, CiConstants {

	private final transient static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AbstractStatCallback.class);

	public abstract Object getData(JCRSessionWrapper session, RenderContext renderContext);

	/**
	 * Return the given date with hour time to zero.
	 * 
	 * @param date
	 *            the date you want to simplify
	 * @return the simplified date
	 */
	public static Date simplifiedDayDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		return calendar.getTime();
	}

	/**
	 * Get site members in a list as identifiers. Members have isMember and
	 * siteKey properties to true.
	 * 
	 * @since ... can specify member type
	 * @param session
	 * @param context
	 * @param memberType
	 * @param memberLock
	 * @see ICallback constant to know which value you add for memeber lock
	 * @return the list of members uuids
	 */
	protected List<String> getMembersUuids(JCRSessionWrapper session, RenderContext context, String memberType, int memberLock) {
		NodeIterator iterator = (NodeIterator) getMembersNodes(session, context, memberType, memberLock);
		List<String> uuids = new ArrayList<String>();
		while (iterator.hasNext()) {
			JCRNodeWrapper user = (JCRNodeWrapper) iterator.next();
			try {
				uuids.add(user.getIdentifier());
			} catch (RepositoryException e) {
				LOGGER.error("Problem on retrieving user uuid with " + user);
			}
		}
		return uuids;
	}

	/**
	 * Get site members in a list as nodes. Members have isMember and siteKey
	 * properties to true.
	 * 
	 * @since ... can specify member type
	 * @param session
	 * @param context
	 * @return the list of members nodes
	 * @throws RepositoryException
	 */
	protected NodeIterator getMembersNodes(JCRSessionWrapper session, RenderContext context, String memberType, int memberLock) {
		QueryResult result = null;

		String typeClause = "";
		if (StringUtils.isNotEmpty(memberType)) {
			typeClause = " and user." + PROPERTIES_USER_TYPE + "='" + memberType + "'";
		}

		String lockClause;
		switch (memberLock) {
		case -1:
			lockClause = " and user.[" + PROPERTIES_USER_ACCOUNT_LOCKED + "]='true'";
			break;
		case 1:
			lockClause = " and user.[" + PROPERTIES_USER_ACCOUNT_LOCKED + "]='false'";
			break;
		default:
			lockClause = "";
			break;
		}

		try {
			result = executeSQLQuery(session, "select * from [jnt:user] as user where user." + context.getSite().getSiteKey()
					+ "='true' and user." + PROPERTIES_USER_IS_MEMBER + "='true' " + typeClause + lockClause, 0, 0);
		} catch (RepositoryException e) {
			LOGGER.error(e.getMessage());
		}
		if (result != null) {
			try {
				return result.getNodes();
			} catch (RepositoryException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Get site professional in a list as uuids. Professional have
	 * isProfessional and siteKey properties to true.
	 * 
	 * @param session
	 * @param context
	 * @return the list of professional uuids
	 * @throws RepositoryException
	 */
	protected List<String> getProfessionalsUuids(JCRSessionWrapper session, RenderContext context) {
		NodeIterator iterator = (NodeIterator) getProfessionalsNodes(session, context);
		List<String> uuids = new ArrayList<String>();
		while (iterator.hasNext()) {
			JCRNodeWrapper user = (JCRNodeWrapper) iterator.next();
			try {
				uuids.add(user.getIdentifier());
			} catch (RepositoryException e) {
				LOGGER.error("Problem on retrieving user uuid with " + user);
			}
		}
		return uuids;
	}

	/**
	 * Get site professional in a list as nodes. Professional have
	 * isProfessional and siteKey properties to true.
	 * 
	 * @param session
	 * @param context
	 * @return the list of professional nodes
	 * @throws RepositoryException
	 */
	protected NodeIterator getProfessionalsNodes(JCRSessionWrapper session, RenderContext context) {
		QueryResult result = null;
		try {
			result = executeSQLQuery(session, "select * from [jnt:user] as user where user." + context.getSite().getSiteKey()
					+ "='true' and user." + PROPERTIES_USER_IS_PROFESSIONAL + "='true'", 0, 0);
		} catch (RepositoryException e) {
			LOGGER.error(e.getMessage());
		}
		if (result != null) {
			try {
				return result.getNodes();
			} catch (RepositoryException e) {
				LOGGER.error(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Get moderators in a list as identifiers. Moderators are in moderators
	 * group in Jahia.
	 * 
	 * @param renderContext
	 * @return the list of moderators uuids
	 */
	protected List<String> getModeratorsUuids(RenderContext renderContext) {
		List<String> moderatorsUuids = new ArrayList<String>();
		for (JCRUser user : getModeratorsNodes(renderContext)) {
			moderatorsUuids.add(user.getIdentifier());
		}
		return moderatorsUuids;
	}

	/**
	 * Get moderators in a list as nodes. Moderators are in moderators group in
	 * Jahia.
	 * 
	 * @param renderContext
	 * @return the list of moderators nodes
	 */
	protected List<JCRUser> getModeratorsNodes(RenderContext renderContext) {

		List<JCRUser> moderatorsUuids = new ArrayList<JCRUser>();
		Properties searchCriterias = new Properties();
		searchCriterias.setProperty("groupname", CiConstants.USER_GROUP_MODERATOR);
		Set<JahiaGroup> jahiaGroups = ServicesRegistry.getInstance().getJahiaGroupManagerService()
				.searchGroups(renderContext.getSite().getID(), searchCriterias);

		for (Iterator<JahiaGroup> iterator = jahiaGroups.iterator(); iterator.hasNext();) {
			JahiaGroup jahiaGroup = (JahiaGroup) iterator.next();
			Enumeration<Principal> enumeration = jahiaGroup.members();
			while (enumeration.hasMoreElements()) {
				JCRUser user = (JCRUser) enumeration.nextElement();
				moderatorsUuids.add((JCRUser) user);
			}

		}

		return moderatorsUuids;
	}

	/**
	 * 
	 * @param session
	 * @param query
	 * @param maxItems
	 * @param offset
	 * @return
	 * @throws RepositoryException
	 */
	protected static QueryResult executeSQLQuery(JCRSessionWrapper session, String query, int maxItems, int offset)
			throws RepositoryException {
		QueryManager qm = session.getWorkspace().getQueryManager();
		// query = QueryParser.escape(query);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Preparing to execute query : " + query + "\r\toffset : " + offset + "\r\tlimit : " + maxItems);
		Query q = qm.createQuery(query, Query.JCR_SQL2);
		q.setLimit(maxItems);
		q.setOffset(offset);
		return q.execute();
	}

	/**
	 * 
	 * @param uuids
	 * @param alias
	 * @param operator
	 * @return
	 */
	final protected static String extractInUuidClause(List<String> uuids, String alias, String operator) {
		StringBuilder inUuidClause = new StringBuilder();
		for (String uuid : uuids) {
			if (uuids.indexOf(uuid) > 0)
				inUuidClause.append(" and ");
			else
				inUuidClause.append(" ");
			inUuidClause.append(alias + ".[jcr:uuid] ");
			inUuidClause.append(operator + " '" + uuid + "'");
		}
		return inUuidClause.toString();
	}

	/**
	 * 
	 * @author foo
	 * 
	 */
	final protected class ValueComparator implements Comparator<String> {

		private Map<String, Integer> base;

		public void setBase(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.

		public int compare(String a, String b) {
			int ia = base.get(a);
			int ib = base.get(b);
			if (ia >= ib) {
				return -1;
			} else if (ib > ia) {
				return 1;
			} else
				return 0;
		}
	}

	/**
	 * Retrieve page title
	 * 
	 * @param rubricNode
	 * @return
	 * @throws PathNotFoundException
	 */
	protected String getPageTitle(JCRNodeWrapper pageNode) throws PathNotFoundException {
		return PageHelper.getPageTitle(pageNode);
	}

	/**
	 * Add the rubric page name in parentheses at the thematic title.
	 * 
	 * @param thematicNode
	 * @param rubricPage
	 * @return
	 * @throws PathNotFoundException
	 */
	protected String getThematicFullName(JCRNodeWrapper thematicNode, JCRNodeWrapper rubricPage) throws PathNotFoundException {
		return PageHelper.getPageTitle(thematicNode) + " (" + PageHelper.getPageTitle(rubricPage) + ")";
	}

}