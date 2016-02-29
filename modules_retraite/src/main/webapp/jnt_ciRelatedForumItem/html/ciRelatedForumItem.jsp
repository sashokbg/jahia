<%--
	@author : el-aarko
	@created : 18 sept. 2012
	@Id : jnt:ciThematicQuestionsBox
	@description : affiche les questions de la meme thematique
				   box de la coeur de page question ou article

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="forumPage" var="forumPage" />

<c:if test="${itemStatus.index % 2 eq 0}">
	<c:set var="style" value="background-color: rgb(248, 248, 248);" />
</c:if>
<c:if test="${itemStatus.index % 2 ne 0}">
	<c:set var="style" value="background-color: rgb(235, 235, 235);" />
</c:if>
<c:if test="${itemStatus.last}">
	<c:set var="style" value="${style} border-bottom: none;" />
</c:if>

<c:if test="${not empty forumPage }">

	<template:addCacheDependency flushOnPathMatchingRegexp="${forumPage.node.path}/.*/ciQuestion" />
	
	<li class="blocSujet questionForum" style="${style}">
		<p>
			<a href="${forumPage.node.url }">${cia:pageTitle(forumPage.node) }</a>
		</p> 
		<jcr:sql var="questionsQuery" 	sql="select * from [jnt:ciQuestion] as post where isdescendantnode(post, ['${forumPage.node.path}']) order by modifiedDate desc" />
		 <c:set	var="questionSize" value="${questionsQuery.nodes.size}"></c:set>
		<div class="infosQuestion">
			<ul>
				<li>${questionSize}&nbsp;<fmt:message key="forum.nbOfQuestions">
											<fmt:param value="${questionSize gt 1 ? 's' : ''}"></fmt:param>
										</fmt:message>
				</li>
				<c:forEach items="${questionsQuery.nodes}" begin="0" end="0" var="question">
					<li class="lastLink"><fmt:message key="forum.last.message" />&nbsp;${cia:formatDate(question.properties['modifiedDate'].date.time, postDatePattern, true)}
					</li>
				</c:forEach>
			</ul>
		</div>
	</li>
</c:if>

