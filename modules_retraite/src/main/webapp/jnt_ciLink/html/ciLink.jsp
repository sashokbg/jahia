<%--
	@author : Sylvain Pichard
	@created : 27 sept. 2012
	@Id : lien
	@description : Affiche un lien href (interne ou externe)

 --%>

<%@include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="linkLabel" var="linkLabel" />
<jcr:nodeProperty node="${currentNode}" name="link" var="link" />
<jcr:nodeProperty node="${currentNode}" name="externalLink" var="externalLink" />
<jcr:nodeProperty node="${currentNode}" name="target" var="target" />

<template:addCacheDependency node="${renderContext.mainResource.node}" />

<c:choose>
	<%-- lien interne --%>
	<c:when test="${not empty link}">	
		<a class="${currentResource.moduleParams.linkClass}" href="${link.node.url}" title="${linkLabel.string}" target="${empty target.string ? '_self' : target.string}">
			${linkLabel.string}		 
		</a>
	</c:when>
	<%-- lien externe --%>
	<c:otherwise>
		<c:choose>
		    <c:when test="${!fn:startsWith(externalLink.string, 'http')}">
		    	<a class="${currentResource.moduleParams.linkClass}" href="http://${externalLink.string}" title="${linkLabel.string}" target="${empty target.string ? '_blank' : target.string}">
		    		${linkLabel.string}	
		    	</a>
		    </c:when>
		    <c:otherwise>
		    	<a class="${currentResource.moduleParams.linkClass}" href="${externalLink.string}" title="${linkLabel.string}" target="${empty target.string ? '_blank' : target.string}">
		    		${linkLabel.string}	
		    	</a>
		    </c:otherwise>
		</c:choose>	
	
	</c:otherwise>
</c:choose>