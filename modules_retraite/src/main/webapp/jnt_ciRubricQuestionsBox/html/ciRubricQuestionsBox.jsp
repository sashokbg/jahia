<%--
	@author : el-aarko
	@created : 18 sept. 2012
	@Id : jnt:ciThematicQuestionsBox
	@description : affiche les questions de la meme thematique
				   box de la coeur de page question ou article

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="boxTitle" var="boxTitle" />

<c:set var="thematicPage" value="${cia:getThematicParent(currentNode)}"></c:set>

<c:if test="${empty thematicPage}">
	<c:set var="thematicPage" value="${cia:getRubricParent(currentNode)}"></c:set>
</c:if>

<c:if test="${empty thematicPage}">
	<c:set var="thematicPage" value="${renderContext.site.home}"></c:set>
</c:if>

<c:catch>
<c:set var="currentQuestion" value="${jcr:getChildrenOfType(jcr:getChildrenOfType(urlResolver.node, 'jnt:contentList')[0], 'jnt:ciQuestion')[0]}"/>
</c:catch>
<c:if test="${not empty currentQuestion }">
	<c:set var="clause" value=" and not issamenode(['${currentQuestion.path}'])"/>
</c:if>

<c:if test="${not empty thematicPage}">
	<jcr:sql var="questionsQuery" limit="4"
		sql="select * from [jnt:ciQuestion] as post where isdescendantnode(post, ['${thematicPage.path}']) ${clause } order by [jcr:created] desc" />
</c:if>
<c:set var="questionsQuerySize">${questionsQuery.nodes.size }</c:set>

<c:if test="${questionsQuerySize gt 0 }">
	</c:if>

<div id="gaRubricQuestionsBox" class="grandConteneurQuestionsMemeSujet" >
	<div class="questionsMemeSujet">
		<h3 class="band brown">Questions ${cia:pageShortTitle(thematicPage)}</h3>
		
		<%-- Cas : plusieurs questions --%>
		<c:if test="${questionsQuerySize gt 0 }">
			<c:forEach items="${questionsQuery.nodes}" var="question" varStatus="status">
				<template:module node="${ question }" view="hidden.summary.box" editable="false" />
			</c:forEach>
		</c:if>
	
		<%-- Le cas : aucune quesiton --%>
		<c:if test="${empty questionsQuerySize or questionsQuerySize eq 0 }">
			<jcr:sql var="tempQuestionsQuery" limit="4"
				sql="select * from [jnt:ciQuestion] as post where isdescendantnode(post, ['${thematicPage.path}']) order by [jcr:created] desc" />
			<c:set var="questionsQuerySize">${tempQuestionsQuery.nodes.size }</c:set>
			<%-- Cas : une seule question --%>
			<c:if test="${questionsQuerySize eq 1}">
				<div class="conteneurAucuneQuestion">
					<span class="aucuneQuestion">Aucune autre question pour le moment</span>
				</div>
			</c:if>
			
			<c:if test="${empty questionsQuerySize or questionsQuerySize eq 0 }">
				<div class="conteneurAucuneQuestion">
					<span class="aucuneQuestion">
						Aucune question n'a encore &eacute;t&eacute; pos&eacute;e.<br/><br/>
						<a class="mbLink" href="${renderContext.site.properties['addQuestionLink'].node.url}?rubric=${cia:getRubricParent(currentNode).path}&thematic=${cia:getThematicParent(currentNode).path}" title="Posez votre question">Posez votre question</a>
					</span>
				</div>
			</c:if>
		</c:if>
	</div>
	<c:forEach items="${jcr:getChildrenOfType(thematicPage, 'jnt:page') }" var="item">
		<c:if test="${not empty item.properties['isForum'] and item.properties['isForum'].boolean}">
			<c:set var="forumPageUrl" value="${item.url }" />
		</c:if>
	</c:forEach>
	<div class="conteneurQuestionMemeSujet lienVoirForum">
		<a href="${forumPageUrl}">Voir le forum</a>
	</div>
</div>