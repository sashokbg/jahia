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

<%-- @TODO css refactoring --%>
<div class="conteneurSujet forumRelation" style="float: right;">	
	<h3 class="band statutPro">${empty boxTitle ? 'forums en relation' : boxTitle.string }</h3>
	<div class="listeSujets">
		<ul class="fondListeSujets">
		<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:ciRelatedForumItem')}">
			<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciRelatedForumItem')}" var="subchild" varStatus="status">
				<c:set var="itemStatus" scope="request" value="${status }" />
				<template:module node="${subchild}" editable="true" view="hidden.summary.box" />
			</c:forEach>
		</c:if>
	
		</ul>
		<c:if test="${renderContext.editMode}">
			<template:module path="*" nodeTypes="jnt:ciRelatedForumItem" editable="true" view="hidden.summary.box" />
		</c:if>
	</div>
</div>

