<%--
	@author : lakreb
	@created : 14 sept. 2012
	@Id : US 500
	@description : remonte les dernieres questions de la rubrique courante ou du site

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>

<c:if test="${!renderContext.editMode}">
	<c:set var="rubricNode" value="${cia:getRubricParent(urlResolver.node)}" />
</c:if>

<c:set var="queryPath" value="${not empty rubricNode ? rubricNode.path : renderContext.site.home.path}" />
<utility:logger level="debug" value=" [ciLastQuestionsBox] - looking for ciquestion in path ${queryPath}" />

<jcr:nodeProperty name="boxTitle" node="${currentNode}" var="boxTitle" />
<jcr:nodeProperty name="nbHit" node="${currentNode}" var="nbHit" />

<jcr:jqom var="numberOfPostsQuery" limit="${nbHit.long}">
	<query:selector nodeTypeName="jnt:ciQuestion" selectorName="post" />
	<query:descendantNode path="${queryPath}" selectorName="post" />
	<query:sortBy propertyName="jcr:created" selectorName="post" order="desc" />
</jcr:jqom>

<c:set var="numberOfPosts" value="${numberOfPostsQuery.nodes.size}" />

<%-- @TODO css refactoring --%>
<div id="gaLastQuestionsBox" class="questionsMemeSujet" style="height:auto;display:block;margin-bottom:10px;">
	<h3 class="band statutPro">${boxTitle.string}</h3>
	<c:choose>
		<c:when test="${numberOfPosts > 0}">
			<c:forEach items="${numberOfPostsQuery.nodes}" var="subchild" varStatus="status">
				<c:set var="questionsStatus" value="${status}" scope="request"/>
				<template:module node="${ subchild }" view="hidden.summary.box" />
			</c:forEach>
		</c:when>
		<c:otherwise>		
			<div class="conteneurQuestionMemeSujet item">
				<span class="aucuneQuestion">
					Aucune question n'a encore &eacute;t&eacute; pos&eacute;e.<br/><br/>
					<a href="${renderContext.site.properties['addQuestionLink'].node.url}<c:if test='${not empty rubricNode}'>?rubric=${queryPath}</c:if>" title="Posez votre question" class="mbLink">Posez votre question</a>
				</span>
			</div>	
		</c:otherwise>
	</c:choose>
	<div class="breaker"></div>
</div>
