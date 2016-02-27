<%--
	@author : el-aarko
	@created : 18 sept. 2012
	@Id : jnt:ciThematicQuestionsBox
	@description : affiche les articles les plus vus de la meme thematique
				   box de la coeur de page question ou article

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="boxTitle" var="boxTitle" />

<c:set var="rubricPage" value="${cia:getRubricParent(currentNode)}"></c:set>

<c:set var="currentArticle" value="${jcr:getChildrenOfType(jcr:getChildrenOfType(urlResolver.node, 'jnt:contentList')[0], 'jnt:ciArticle')[0]}"/>

<c:if test="${not empty currentArticle }">
	<c:set var="clause" value=" and not issamenode(['${currentArticle.path}'])"/>
</c:if>

<c:if test="${not empty rubricPage && renderContext.liveMode}">
	<jcr:sql var="articlesQuery" limit="5"
		sql="select * from [jnt:ciArticle] as article where isdescendantnode(article, ['${rubricPage.path}']) ${clause} order by [jcr:created] desc" />
	<c:set var="articlesQuerySize">${articlesQuery.nodes.size }</c:set>
</c:if>

<div class="articleLesPlusVus">	
	<h3 class="band brown">
		<c:choose>
			<c:when test="${boxTitle.boolean}">${boxTitle.string}</c:when>
			<c:otherwise>
				Articles&nbsp;${not empty rubricPage? cia:pageShortTitle(rubricPage) : ''}
			</c:otherwise>
		</c:choose>
	</h3>

	<%-- Cas : plusieurs questions --%>
	<c:if test="${articlesQuerySize gt 0 }">
		<c:forEach items="${articlesQuery.nodes}" var="article" varStatus="status">
			<c:set var="articleStatus" value="${status}" scope="request"/>
			<template:module node="${article}" view="hidden.summary.box" editable="false" />
		</c:forEach>
	</c:if>
	
	<%-- Cas : une seule question --%>
	<c:if test="${articlesQuerySize eq 1 }">
	
	</c:if>
	
	<%-- Le cas : aucune quesiton --%>
	<c:if test="${articlesQuerySize eq 1 }">
		
	</c:if>
</div>