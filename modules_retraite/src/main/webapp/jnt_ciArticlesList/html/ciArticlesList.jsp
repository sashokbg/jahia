<%--
	@author : el-aarko
	@created : 28 sept. 2012
	@Id : jnt_ciArticlesList
	@description : Liste des articles

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<c:set var="thematicPage" value="${cia:getThematicParent(renderContext.mainResource.node)}"/>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />

<c:choose>
<c:when test="${empty thematicPage }">
	Ceci n'est pas une page de visualisation d'une sous-th\u00E9matique.
</c:when>
<c:otherwise>
	<h2 class="titrePage titreThematiquePage">${not empty title ? title.string : cia:pageTitle(thematicPage) }</h2>
	<img src="${thematicPage.properties['image'].node.url}" alt="image header thematique" />
	<div class="articlesThematique">				
		<div class="chapeauThematique">
			${thematicPage.properties['description'].string}
		</div>
		
		<jcr:sql var="articlesQuery" sql="select * from [jnt:ciArticle] as object where isdescendantnode(object, ['${thematicPage.path}']) order by nbOfViews desc" />
		<c:set var="articlesSize">${articlesQuery.nodes.size }</c:set>
				
		<c:forEach items="${articlesQuery.nodes }" var="article">
			<template:module node="${article}" editable="true" view="hidden.summary" />
		</c:forEach>
	</div>
</c:otherwise>
</c:choose>

