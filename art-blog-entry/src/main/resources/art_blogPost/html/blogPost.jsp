<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="user" uri="http://www.jahia.org/tags/user" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<jcr:nodeProperty name="title" node="${currentNode}" var="title"/>
<jcr:nodeProperty name="postText" node="${currentNode}" var="postText"/>
<jcr:nodeProperty name="postPic" node="${currentNode}" var="postPic"/>

<jcr:nodeProperty node="${currentNode}" name="jcr:createdBy" var="createdBy"/>
<jcr:nodeProperty node="${currentNode}" name="jcr:created" var="created"/>
	
<c:set var="currentUser" value="${user:lookupUser(createdBy.string)}"/>

<fmt:formatDate value="${created.time}" type="date" pattern="dd" var="userCreatedDay"/>
<fmt:formatDate value="${created.time}" type="date" pattern="MMM" var="userCreatedMonth"/>


<div class="art-post post">
	<h1>${title.string}</h1>
	<div class="post-date"><span>Feb</span>27</div>
	
	<p class="post-info"><fmt:message key="art_blogPost.label.by"/>&nbsp;${user:fullName(currentUser)}&nbsp;-&nbsp;<fmt:formatDate value="${created.time}" type="date" dateStyle="medium"/></p>
	<hr/>
	<div class="art-pic">
		<img src="<c:url value="${postPic.node.url}" />" />
	</div>
	<pre>
		${postText.string}
	</pre>
	
</div>