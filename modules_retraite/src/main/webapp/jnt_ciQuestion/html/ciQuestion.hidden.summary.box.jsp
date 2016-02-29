<%--
	@author : el-aarko
	@created : 14 aout 2012
	@Id : QuestionSummary
	@description : Vue reduite d'une question utilisee pour les listes

 --%>

<%@include file="../../common/declarations.jspf"%>

<template:addCacheDependency flushOnPathMatchingRegexp="${currentNode.path}/.*/ciReply" />

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="jcr:created" var="created" />
<jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" />
<jcr:nodeProperty node="${currentNode}" name="nbOfReplies" var="nbOfPostReplies" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />

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
<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<div class="conteneurQuestionMemeSujet item">
	<a class="blocQuestionAvatar" href="${publicProfileUrl}">
		<img src="${avatarSrc40}" height="40px" width="40px"  alt="avatar" title="Acc&eacute;der au profil de ${pseudo}"  /></a>
	<div class="conteneurQuestionEnRapport">
		<div class="libelleQuestion">
			<a href="${jcr:getParentOfType(currentNode,'jnt:page').url}">
				<strong>${cia:cutString(title.string, 50)}</strong>
			</a>
		</div>
		<span class="questionDe">Question de <strong class="userLink"><a href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}" >${pseudo}</a></strong></span>
		<span class="infosComplementaires">
			<span class="dateInfoCompl">${cia:formatDate(created.date.time, 'dd/MM/yy', true)}</span>
			<span class="nbVuesInfoCompl">${nbOfViews}&nbsp;${viewsMsg}</span>
			<span class="nbMessagesInfoCompl">${nbOfPostReplies}&nbsp;${memMsg}</span>
		</span>
	</div>
	<div class="breaker"></div>
</div>
