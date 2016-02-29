<%--
	@author : el-aarko
	@created : 14 aout 2012
	@Id : QuestionList
	@description : Liste des questions d'un forum

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />

<c:set var="orderByParam">
<c:if test="${not empty param.orderby }">${param.orderby }</c:if>
<c:if test="${empty param.orderby and not empty renderContext.site.properties['questionsListOrder']}">${renderContext.site.properties['questionsListOrder'].string}</c:if>
<c:if test="${empty param.orderby and empty renderContext.site.properties['questionsListOrder']}">lastmod</c:if>
</c:set>

<c:set var="thematicPage" value="${cia:getThematicParent(renderContext.mainResource.node)}" />
<c:set var="forumName" value="Forum ${cia:pageShortTitle(thematicPage)}"/>

<div class="titrePage">
	<h1>${empty title ? forumName : title.string}</h1>
</div>

<c:if test="${orderByParam=='lastmod'}">
<c:set var="orderBy" value="modifiedDate"/>
</c:if>	
<c:if test="${orderByParam=='view'}">
<c:set var="orderBy" value="nbOfViews"/>
</c:if>
<c:if test="${orderByParam=='active'}">
<c:set var="orderBy" value="nbOfReplies"/>
</c:if>
<jcr:sql var="questionsQuery"
        sql="select * from [jnt:ciQuestion] as post  where isdescendantnode(post, ['${jcr:getParentOfType(currentNode,'jnt:page').path}']) order by post.${orderBy} desc"/>
<c:set target="${moduleMap}" property="commentsList" value="${questionsQuery.nodes}"/>
<c:set target="${moduleMap}" property="listTotalSize" value="${questionsQuery.nodes.size}"/>
<c:set var="maxItem">
<c:if test="${not empty renderContext.site.properties['paginationmaxForumQuestionList']}">${renderContext.site.properties['paginationmaxForumQuestionList'].long}</c:if>
<c:if test="${empty renderContext.site.properties['paginationmaxForumQuestionList']}">6</c:if>
</c:set>

<%-- les questions --%>
<div class="listeQuestions">
	<c:choose>
		<c:when test="${moduleMap.listTotalSize > 0}">
			<%-- Voir les reponses les mieux notees / les plus recentes   --%>
			<div class="voirLes">
				<span><fmt:message key="replies.action.see" /></span>
				<form method="get" name="voirLes">
					<select class="listeVoirLes" name="orderby"
						onchange="this.form.submit();">
						<option value="lastmod"
							<c:if test="${orderByParam=='lastmod'}"> selected="selected"</c:if>>
							<fmt:message key="questions.sort.most.recent" />
						</option>

						<option value="active"
							<c:if test="${orderByParam=='active'}"> selected="selected"</c:if>>
							<fmt:message key="questions.sort.most.active" />
						</option>

						<option value="view"
							<c:if test="${orderByParam=='view'}"> selected="selected"</c:if>>
							<fmt:message key="questions.sort.most.viewed" />
						</option>

					</select>
				</form>
			</div>
			<template:initPager totalSize="${moduleMap.listTotalSize}"
				id="${currentNode.identifier}" pageSize="${maxItem }" />
			<c:forEach items="${moduleMap.commentsList}" var="subchild"
				varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}">
				<c:set var="questionsStatus" value="${status}" scope="request" />
				<template:module node="${ subchild }" editable="true"
					view="hidden.summary" />
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="dataSection">Aucune question n'a encore &eacute;t&eacute; pos&eacute;e, <a href="${renderContext.site.properties['addQuestionLink'].node.url}?rubric=${cia:getRubricParent(currentNode).path}&thematic=${cia:getThematicParent(currentNode).path}" title="Posez votre question" class="mbLink">posez votre question</a> !</div>
		</c:otherwise>
	</c:choose>
</div>
<c:choose>

	<c:when test="${moduleMap.listTotalSize > maxItem}">
		<div class="conteneurPaginationThematique">
			<%@include file="../../common/ciPagination.jspf"%>
			<div class="clear"></div>
		</div>
		<template:removePager id="${currentNode.identifier}"/>
	</c:when>
</c:choose>