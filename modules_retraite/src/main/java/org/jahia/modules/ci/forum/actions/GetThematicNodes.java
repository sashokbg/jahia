/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2012 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */

package org.jahia.modules.ci.forum.actions;

import java.util.List;
import java.util.Map;

import javax.jcr.NodeIterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commons.util.Validator;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLGenerator;
import org.jahia.services.render.URLResolver;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Retrieve thematic list from a rubric page.
 * 
 * @author : el-aarko
 */
public class GetThematicNodes extends Action {
	//private transient static Logger logger = org.slf4j.LoggerFactory.getLogger(GetThematicNodes.class);

	public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
			Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		String rubricPath = getParameter(parameters, "rubricPath");
		if (Validator.isNotEmpty(rubricPath)) {
			JCRNodeWrapper rubricNode = session.getNode(rubricPath);
			NodeIterator iterator = rubricNode.getNodes();
			while (iterator.hasNext()) {
				JCRNodeWrapper childNode = (JCRNodeWrapper) iterator.next();
				if ("jnt:page".equals(childNode.getPrimaryNodeTypeName())) {
					JSONObject json = new JSONObject();
					json.put("identifier", childNode.getIdentifier());
					URLGenerator urlGenerator = new URLGenerator(renderContext, resource);
					json.put("url", urlGenerator.buildURL(childNode, null, "html"));
					json.put("path", childNode.getPath());

					if (Validator.isNotEmpty(childNode.getPropertyAsString("jcr:title"))) {
						json.put("name", childNode.getPropertyAsString("jcr:title"));
					} else {
						json.put("name", childNode.getName());
					}
					array.put(json);
				}
			}
			result.put("thematics", array);
		}
		return new ActionResult(HttpServletResponse.SC_OK, null, result);
	}
}
