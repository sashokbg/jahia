<%--
	@author : el-aarko
	@created : 11 sept. 2012
	@Id : MemberProfile
	@description : profil d'un membre de la commuanute retraite

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="../../common/ciSendMessageUsers.jspf"%>

<template:addResources>
	<meta name="robots" content="noindex, follow">
</template:addResources>



<c:choose>
	<c:when test="${renderContext.editMode}">
		Module Profil d'un membre
	</c:when>
	<c:otherwise>	
		<c:if test="${not empty param.pseudo}">
			<c:set var="displayedUser" value="${cia:getUserByPseudoname(param.pseudo)}" scope="request" />
			<c:if test="${not empty displayedUser}">
				<template:addCacheDependency uuid="${displayedUser.identifier}" />
				<jcr:node var="displayedUserNode" uuid="${displayedUser.identifier}"/>
				<c:choose>
					<c:when test="${not cia:isMember(displayedUserNode) and displayedUserNode.properties[siteKey].boolean }">
						404
					</c:when>
					<c:otherwise>
						<jcr:node var="currentUserNode" uuid="${currentUser.identifier}"/>
						<c:choose>
							<c:when test="${cia:isProfessional(currentUserNode)}">
								<%@include file="ciMemberProfile_professional_view.jspf" %>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${jcr:hasPermission(currentNode, 'editUser') and cia:isModerator(renderContext,currentUser)}">
										<%@include file="ciMemberProfile_moderator_view.jspf" %>
									</c:when>
									<c:otherwise>
										<%@include file="ciMemberProfile_public_view.jspf" %>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:if>
	</c:otherwise>
</c:choose>