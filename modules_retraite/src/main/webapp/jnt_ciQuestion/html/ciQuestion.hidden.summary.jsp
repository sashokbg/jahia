<%--
	@author : el-aarko
	@created : 14 aout 2012
	@Id : QuestionSummary
	@description : Vue r&eacute;duite d'une question utilis&eacute;e pour les listes

 --%>

<%@include file="../../common/declarations.jspf" %>

<template:addCacheDependency flushOnPathMatchingRegexp="${currentNode.path}/.*/ciReply" />

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="nbOfReplies" var="nbOfReplies" />
<jcr:nodeProperty node="${currentNode}" name="nbOfProfReplies" var="nbOfProfReplies" />
<jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long}</c:set>
<c:set var="nbOfReplies">${(empty nbOfReplies) ? 0 : nbOfReplies.long}</c:set>
<c:set var="nbOfProfReplies">${(empty nbOfProfReplies) ? 0 : nbOfProfReplies.long}</c:set>
<c:set var="nbOfMemberReplies">${nbOfReplies - nbOfProfReplies}</c:set>

<c:set var="userNode" value="${userInfo.node}" />
<%@include file="../../common/ciSetUserInfo.jspf"%>

<template:addCacheDependency node="${userNode}" />

<c:if test="${questionsStatus.index % 2 eq 0}">
	<c:set var="paire" value=" Paire" />
</c:if>
<c:if test="${questionsStatus.last}">
	<c:set var="last" value=" last" />
</c:if>

<div class="Question ${userTypeCss} ${paire } ${last}">
	<div class="avatar">
		<a class="blocQuestionAvatar" href="${publicProfileUrl }" title="Acc&eacute;der au profil de ${pseudo}">
			<img alt="Photo Avatar" height="40px" width="40px"  src="${avatarSrc40}">
		</a>
	</div>
	<div class="colonneContenuListeQ">
		<h2><a href="${jcr:getParentOfType(currentNode,'jnt:page').url}" title="Question">${title.string}</a></h2>
		Par <strong class="userLink"><a href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}">${pseudo}</a></strong>
		<span class="functionOrStatus">${userTypeLabel}</span>
		<div class="infosQuestion">
		<fmt:message var="profMsg" key="questions.messages.of.professional" >
			<fmt:param><c:if test="${nbOfProfReplies gt 1}">s</c:if></fmt:param>
		</fmt:message>
		<fmt:message var="memMsg" key="questions.messages.of.members" >
			<fmt:param><c:if test="${nbOfMemberReplies gt 1}">s</c:if></fmt:param>
		</fmt:message>
		<fmt:message var="viewsMsg" key="questions.viewed" >
			<fmt:param><c:if test="${nbOfViews gt 1}">s</c:if></fmt:param>
		</fmt:message>
		<ul>
			<li>${nbOfMemberReplies}&nbsp;${memMsg}</li>
			<li>${nbOfProfReplies }&nbsp;${profMsg}</li>
			<li class="noir">${nbOfViews}&nbsp;${viewsMsg}</li>
			<li class="last"><fmt:message key="questions.last.message" />&nbsp;${cia:formatDate(currentNode.properties['modifiedDate'].date.time, questionDatePattern, true)}</li>
		</ul>
		</div>
	</div>	
</div>