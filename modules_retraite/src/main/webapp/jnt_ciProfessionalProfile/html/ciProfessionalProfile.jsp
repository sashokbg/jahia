<%--
	@author : el-aarko
	@created : 11 sept. 2012
	@Id : MemberProfile
	@description : profil d'un membre de la commuanute retraite

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="../../common/ciSendMessageUsers.jspf"%>

<template:addResources>
	<script type="text/javascript">
	function formatHtmlPost(text) {
		text = text.replace(/\n/g, "<br/>");
		return text;
	}

	function setupBr() {
		$(".editableField").each(function(item) {
			var text = $(this).html();
			$(this).html(formatHtmlPost(text));
		});
	}
	$(document).ready(function() {
		setupBr();
	});
	</script>
	<meta name="robots" content="noindex, nofollow" />
</template:addResources>


<c:choose>
	<c:when test="${renderContext.editMode}">
		Module Profil d'un Professionnel
	</c:when>
	<c:otherwise>	
		<c:if test="${not empty param.pseudo}">
			<c:set var="displayedUser" value="${cia:getUserByPseudoname(param.pseudo)}" scope="request" />
			<c:if test="${not empty displayedUser}">
				<template:addCacheDependency uuid="${displayedUser.identifier}" />
				<jcr:node var="displayedUserNode" uuid="${displayedUser.identifier}"/>
				<c:choose>
					<c:when test="${not cia:isProfessional(displayedUserNode) and displayedUserNode.properties[siteKey].boolean}">
						404
					</c:when>
					<c:otherwise>						
						<jcr:node var="currentUserNode" uuid="${currentUser.identifier}"/>
						<c:choose>
							<c:when test="${jcr:hasPermission(currentNode, 'editUser') and cia:isModerator(renderContext,currentUser)}">
								<%@include file="ciProfessionalProfile_moderator_view.jspf" %>
							</c:when>
  							<c:when test="${jcr:hasPermission(currentNode, 'editProfessional') && (currentUser.userProperties.properties.pseudoname eq param.pseudo) }">
								<%@include file="ciProfessionalProfile_professional_view.jspf" %>
							</c:when>
 							<c:otherwise>
								<%@include file="ciProfessionalProfile_public_view.jspf" %>
							</c:otherwise>
						</c:choose>					
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:if>
	</c:otherwise>

</c:choose>
