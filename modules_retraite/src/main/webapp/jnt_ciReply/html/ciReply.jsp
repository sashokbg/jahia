<%--
	@author : el-aarko
	@created : 31 juil. 2012
	@Id : visualiser une r�ponse
	@description : permet de visualiser le d�tail d'une r�ponse
					 / US-100

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>
<% pageContext.setAttribute("newLineChar", "\\n\\r"); %>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="body" var="body" />
<jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" />
<jcr:nodeProperty node="${currentNode}" name="score" var="score" />


<c:set var="userNode" value="${userInfo.node}" />
<%@include file="../../common/ciSetUserInfo.jspf"%>


<%-- Si on n'est pas connecte --%>
<c:if test="${not isMaintenance}"><c:set var="onclick">openConnectionBox();</c:set></c:if>

<script>
$(document).ready(function() {
	setupRateReplyButton("reply-vote-${currentNode.identifier}");
	//html.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
});

</script>

<template:addResources type="css" resources="signature.css" />
<template:addResources type="javascript" resources="signature.js"/>

<template:addResources>
<style type="text/css">
	pre.postBodyPre {
	    white-space: pre-wrap;       /* CSS 3 */
	    white-space: -moz-pre-wrap;  /* Mozilla, since 1999 */
	    white-space: -pre-wrap;      /* Opera 4-6 */
	    white-space: -o-pre-wrap;    /* Opera 7 */
	    word-wrap: break-word;       /* Internet Explorer 5.5+ */
	}
</style>	
</template:addResources>

<template:addCacheDependency node="${userNode}" />

<%-- adding cache deps on other reply being modified as sort will always be properly displayed --%>
<c:set var="parentQ" value="${jcr:getParentOfType(currentNode,'jnt:ciQuestion')}" />
<c:if test="${not empty parentQ}">
	<template:addCacheDependency flushOnPathMatchingRegexp="${parentQ.path}/.*/ciReply" />
</c:if>

<div class="reponseContainer">
	<div class="colonneVote">												
		<div class="nbreVotes">
			<p><c:if test="${empty score.long}">0</c:if>${score.long}</p>
			<p>vote<c:if test="${not empty score.long and score.long gt 1}">s</c:if></p>
		</div>
		<div class="plusUnContainer" id="reply-vote-${currentNode.identifier}">
		<template:tokenizedForm>
	        <form action="<c:url value='${url.base}${currentNode.path}'/>.ciIncrementScore.do" method="post">
	            <input type="hidden" name="nodePath" value="${currentNode.path}" />
	            <input type="hidden" name="jcrRedirectTo" value="<c:url value='${url.base}${renderContext.mainResource.node.path}'/>"/>
	            <c:set var="onClickVote">
	            <c:choose>
	            	<c:when test="${isMaintenance}">
						
	            	</c:when>
	            	<c:otherwise>
	            		rateReply('reply-vote-${currentNode.identifier}');this.form.submit();$(this).attr('onclick', '');
	            	</c:otherwise>
	            	</c:choose> 
	            </c:set>
	            <input type="button" value="" class="btPlusUn wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="${onClickVote}"/>
	        </form>
	        <div class="bulleRepondre">
				<p><fmt:message key="reply.rate.tooltip"/></p>
			</div>
	    </template:tokenizedForm>
		</div>					
	</div>
	<c:if test="${repliesStatus.index % 2 eq 0}">
		<c:set var="paire" value=" paire" />
	</c:if>
	<c:if test="${repliesStatus.last}">
		<c:set var="last" value=" last" />
	</c:if>
	<div class="questionContainer ${userTypeCss} ${paire}${last}">	
		<div class="questionEntete">
			<fmt:message key="reply.by" />
			<span class="pseudo">
				<a href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}" >${isProfessional ? fullname : pseudo}</a>
			</span>
			| <c:set var="displayedUser" value="${cia:getUserByPseudoname(pseudo)}"/>
				<c:choose>					
					<c:when test="${cia:isModerator(renderContext,displayedUser)}">
						<span class="${userStatusCss}">Mod&eacute;rateur</span>
					</c:when>
					<c:otherwise>
						<span class="${userStatusCss}">${userTypeLabel}</span>
					</c:otherwise>
				</c:choose>	
			<div class="breaker"><!-- ne pas enlever --></div>
		</div>	
		<div class="colonneAvatar">
			<a class="blocQuestionAvatar" href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}" >
				<img src="${avatarSrc40}"  height="40px" width="40px"  alt="Photo Avatar ${pseudo}" /></a>
			<c:if test="${cia:isMember(userNode)}" >
				<div>
					<template:addResources type="css" resources="ciProfil/ratingStarsQuestionReponse.css"/>
					<%@include file="../../common/ratingStars.jspf"%>
				</div>
			</c:if>
		</div>
		<div class="colonneContenu">
			<div class="questionDetail">
				<div id="content-to-edit${currentNode.UUID}" class="postBody"><pre class="postBodyPre">${cia:encodePostUrls(body.string, renderContext.response)}</pre></div>
				<div id="content-to-blockquote${currentNode.UUID}" class="blockquoteBody" style="display: none;">${cia:encodePostUrls(body.string, renderContext.response)}</div>

				<%-- Modification --%>
				<c:set var="loggedUserPseudo" value="${currentUser.userProperties.properties.pseudoname}"/>
				<c:set var="canProfessionalEditOwnPost"
					value="${cia:isInProfessionalGroup(renderContext,displayedUser) && loggedUserPseudo eq pseudo}"/>
				<c:set var="canEditPost" value="${jcr:hasPermission(currentNode, 'editReply') && (
					(cia:isModerator(renderContext,currentUser)) || (canProfessionalEditOwnPost eq 'true')) }"/>
				
				<c:if test="${canEditPost}">
					<c:set var="rUrl">
						<c:url value='${url.base}${currentNode.path}' />
					</c:set>
					<div class="content editablePost"
						<c:out value="jcr:id='body' jcr:url='${rUrl}' " escapeXml="false"/>
						id="edit${currentNode.identifier}">${cia:encodePostUrls(body.string, renderContext.response)}</div>
				</c:if>
				
				<!-- Signature -->
				<c:if test="${cia:isInProfessionalGroup(renderContext,displayedUser)}">
					<div class="signatureContainer">
						<c:out value="${ signature }" escapeXml="false" />
					</div>
				</c:if>
			</div>
			
			<div class="questionFooter "> 
			<!-- Signaler un abus -->
				<a class="signalerAbus" title="Signaler un abus"  href="#popSignaler" 
					onclick="return ciFlagMessage('${currentNode.path}','${renderContext.mainResource.node.url}');">
						<fmt:message key="ciFlagMessage.link.label"/>
				</a>
				
				<span class="fl gris pipeRecommandeQuestion">|</span>
				<!--Recommander a un ami -->
				<c:set var="type" value="reponse"/>
				<a class="signalerAbus" title="Recommander &agrave; un ami"  href="#popRecommandation" 
					onclick="return ciSendFriendMessage('${currentNode.path}','${renderContext.mainResource.node.url}','${type}','${currentNode.UUID}');">
						<fmt:message key="ciSendFriendlyMessageUsers.link.label"/>
				</a>
				
				<div class="questionFooterRight" > 								
					<span class="replyFooterMetadata">
						<%-- Modification --%>
						<c:if test="${canEditPost}">
							<a title="Modifier"  href="#edit" onclick="<c:out value='editPost("edit${currentNode.UUID}"); return false;' />"><fmt:message key="action.edit"/></a> |
						</c:if>
						<%-- Suppression --%>
						<c:if test="${jcr:hasPermission(currentNode, 'deleteReply')}">
							<fmt:message var="confirmMsg" key="reply.delete.confirm"/>
							<a title="Supprimer"  href="#delete" onclick="<c:out value='if (window.confirm("${functions:escapeJavaScript(confirmMsg)}"))
							{ document.getElementById("delete-reply${currentNode.UUID}").submit(); } return false;' />"><fmt:message key="action.delete"/></a> |
						</c:if>
						<%-- Remontee --%>
						<c:if test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
						<a href="${url.base}${currentNode.path}.setPickableNode.do?nodePath=${currentNode.path}">
							${currentNode.properties['isNotPickable'].boolean ? 'Remonter' : 'Ne pas remonter'}
						</a> |
						</c:if> 
						${cia:formatDate(currentNode.properties['jcr:created'].date.time, postDatePattern, true)}
					</span>
					<c:if test="${connected }"><c:set var="onclick" >reply();</c:set></c:if>
					<span title="R&eacute;pondre" class="btRepondre fr wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="${onclick}" ><!-- ne pas enlever --></span>										
				</div>		
				<div class="bulleRepondre">
					<fmt:message var="reply" key="reply.action.reply"/>
					<c:if test="${connected && not isMaintenance}"><c:set var="onclick">reply();</c:set></c:if>
					<a class="wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} title="${reply }" onclick="${onclick}">${reply}</a> |
					<fmt:message var="replywith" key="reply.action.reply.withquote"/>
					<c:if test="${connected && not isMaintenance}"><c:set var="onclick">replyWithBlockote('${pseudo}', 'content-to-blockquote${currentNode.identifier}')</c:set></c:if>
					<a class="wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} title="${replywith }" onclick="${onclick}">${replywith }</a>
				</div>		
			</div>										
		</div>					
	</div>		
</div>	

<%-- Supprimer --%>
<c:if test="${jcr:hasPermission(currentNode, 'deleteReply')}">
    <template:tokenizedForm>
        <form action="<c:url value='${url.base}${currentNode.path}'/>.ciDeleteReplyAction.do" method="post" id="delete-reply${currentNode.UUID}">
            <input type="hidden" name="jcrRedirectTo" value="<c:url value='${url.base}${renderContext.mainResource.node.path}'/>"/>
        </form>
    </template:tokenizedForm>
</c:if>
