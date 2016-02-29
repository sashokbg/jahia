<%--
	@author : el-aarko
	@created : 14 aout 2012
	@Id : QuestionSummary
	@description : Vue reduite d'une question utilisee pour les listes

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="jcr:created" var="created" />
<jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" />
<jcr:nodeProperty node="${currentNode}" name="nbOfReplies" var="nbOfPostReplies" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />

<template:addCacheDependency flushOnPathMatchingRegexp="${currentNode.path}/.*/ciReply" />

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long}</c:set>
<c:set var="nbOfPostReplies">${(empty nbOfPostReplies) ? 0 : nbOfPostReplies.long}</c:set>

<c:set var="userNode" value="${userInfo.node}" />
<%@include file="../../common/ciSetUserInfo.jspf"%>

<template:addCacheDependency node="${userNode}" />

<fmt:message var="memMsg" key="questions.messages">
	<fmt:param>
		<c:if test="${nbOfPostReplies gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<div class="boiteThematiques">
	<div class="ligneActuThematique">
		<a class="blocQuestionAvatar" title="Acc&eacute;der au profil" href="${publicProfileUrl}">
			<img src="${avatarSrc40}" width="40px" height="40px" alt="avatar" />
		</a>
		<span class="infoThematiquePseudo">
			<a title="Acc&eacute;der au profil" href="${publicProfileUrl}">${pseudo}</a></span>
		<a class="questionSideBox" href="${jcr:getParentOfType(currentNode,'jnt:page').url}" title="Acc&eacute;der &agrave; la question">
			<strong>${cia:cutString(title.string, 63)}</strong>
		</a>
		<span class="infoThematiqueMessage">${cia:formatDate(created.date.time, 'dd/MM/yy', true)}&nbsp;-&nbsp;${nbOfPostReplies}&nbsp;${memMsg}</span>
	</div>
</div>
