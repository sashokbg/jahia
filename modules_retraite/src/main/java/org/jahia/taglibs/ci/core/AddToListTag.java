package org.jahia.taglibs.ci.core;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.jahia.taglibs.AbstractJahiaTag;

/**
 * Add a new value (object) to an array list. The name of the attribute must be given.
 * scope can be set, default is page.
 * 
 * @author foo
 * 
 */
public class AddToListTag extends AbstractJahiaTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 926875373673423082L;
	private String var;
	private Object value;
	private Integer scope = PageContext.PAGE_SCOPE;

	@SuppressWarnings("unchecked")
	@Override
	public int doStartTag() throws JspException {
		List<Object> list = null;
		if (pageContext.findAttribute(var) != null) {
			list = (List<Object>) pageContext.getAttribute(var, scope);
		} else
			list = new ArrayList<Object>();

		list.add(value);
		pageContext.setAttribute(var, list, scope);
		return SKIP_BODY;
	}

	/** @jsp The name of the attribute to store the list. */
	public void setVar(String var) {
		this.var = var;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setScope(String scope) {
		if ("request".equals(scope))
			this.scope = PageContext.REQUEST_SCOPE;
		if ("session".equals(scope))
			this.scope = PageContext.SESSION_SCOPE;
		if ("application".equals(scope))
			this.scope = PageContext.APPLICATION_SCOPE;
	}

}
