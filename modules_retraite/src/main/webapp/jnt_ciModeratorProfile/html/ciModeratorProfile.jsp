<%--
	@author : pichards
	@created : 17 sept. 2012
	@Id : US402
	@description : profil d'un moderateur
 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="../../common/ciSendMessageUsers.jspf"%>

<c:choose>
	<c:when test="${renderContext.editMode}">
		Module Profil d'un Mod&eacute;rateur
	</c:when>
	<c:otherwise>	
		<c:if test="${not empty param.pseudo}">
			<c:set var="displayedUser" value="${cia:getUserByPseudoname(param.pseudo)}" scope="request" />
			<c:if test="${not empty displayedUser}">
				<jcr:node var="displayedUserNode" uuid="${displayedUser.identifier }"/>
				<c:choose>
					<c:when test="${not cia:isModerator(renderContext,displayedUser)}">				
						404
					</c:when>
					<c:otherwise>				
						<c:set var="contactLinkUrl">${renderContext.site.properties['contactPageLink'].node.url }</c:set>
						<c:set var="contactLinkLabel"><fmt:message key='modo.contact.label'/></c:set>							
						<c:choose>
							<c:when test="${jcr:hasPermission(currentNode, 'editUser') and cia:isModerator(renderContext,currentUser)}">
								<%@include file="ciModeratorProfile_moderator_view.jspf" %>
							</c:when>
							<c:otherwise>
								<%@include file="ciModeratorProfile_public_view.jspf" %>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:if>
	</c:otherwise>

</c:choose>
