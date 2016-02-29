
<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="boxTitle" 			var="boxTitle" />
<jcr:nodeProperty node="${currentNode}" name="boxMostActiveTitle" 	var="boxMostActiveTitle" />
<jcr:nodeProperty node="${currentNode}" name="boxType" 				var="boxType" />

<template:addCacheDependency flushOnPathMatchingRegexp="/users/.*" />

<c:set var="siteAddress">${renderContext.site.properties['siteAddress'].string}</c:set>
<c:set var="boxTitleVar">${empty boxTitle ? 'A renseigner' : boxTitle.string}</c:set>

<%-- Cas de la box membre avec les derniers membres inscrits en premier --%>
<c:set var="userClass" value="${clauseSiteUsers}user.isMember='true'"/>
<c:set var="seeAllUsersLinkLabel">Voir tous les membres</c:set>
<c:set var="seeAllUsersLinkUrl">${cia:clearUrl(siteAddress, renderContext.site.properties['professionalsListLink'].node)}</c:set>

<%-- Cas de la box les plus actifs --%>
<c:set var="increment">three</c:set>
<c:set var="usersListQuery" value="${cia:getMostActiveUsersForLastMonths(3,10)}" />
<c:set var="usersCount" value="${fn:length(usersListQuery)}" />

<c:if test="${not renderContext.editMode }">
	<template:addResources type="javascript" resources="jquery.movingboxes.js" />
	<template:addResources insert="true">  <%-- laisser le insert="true" afin que ca script soit dans le head. Sinon ga.jspf ne fonctionne pas correctement ! --%>
		<script type="text/javascript">
		$(document).ready(function(){
	
			$('#slider-${increment}').movingBoxes({
				startPanel   : 1,      // start with this panel
				reducedSize  : 0.89,    // non-current panel size: 80% of panel size
				fixedHeight  : true,
				hashTags     : false,
				buildNav     : true,   // if true, navigation links will be added
				disabled     : 'not-disabled',
				navFormatter : function(index, panel){ return panel.find('span').html(); } // function which gets nav text from span inside the panel header
			});
			$(".div${currentNode.identifier} a.lienVoirTousMembres").html("${seeAllUsersLinkLabel}").attr("href", "${seeAllUsersLinkUrl}").attr("title","${seeAllUsersLinkLabel}");
			$( "#slider-three .mb-panel" ).first().addClass( "first" );
			$( "#slider-three .mb-panel" ).addClass( "activeMember" );

		});
		</script>
	</template:addResources>
</c:if>

<div class="blocSlideMembres activMembers ${conteanerDivClass } div${currentNode.identifier}" style="display:block;margin-bottom:10px;">
	<h3 class="band">${boxTitleVar}</h3>
	<c:if test="${usersCount eq 0 || renderContext.editMode}">
		Aucun membre jusqu'&agrave; pr&eacute;sent.
	</c:if>
	<c:if test="${usersCount gt 0 && !renderContext.editMode}">
		<div id="slider-${increment }">
			<c:forEach items="${usersListQuery}" var="user" >
				<jcr:node var="userNode" uuid="${user.userUUID}" />
				<%@include file="../../common/ciSetUserInfo.jspf"%>
				<div>
					<a href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}"><img src="${avatarSrc }" title="Acc&eacute;der au profil de ${pseudo}" alt="avatar de ${pseudo}" /></a>
					<div class="legende">
						<span> 
							<span class="pseudoSlider">
								<c:choose>
								    <c:when test="${empty pseudo}">${firstname}&nbsp;${lastname}</c:when>
								    <c:otherwise>${pseudo}</c:otherwise>
								</c:choose>
								<template:addResources type="css" resources="ciProfil/ratingStarsBox.css"/>
								<%@include file="../../common/ratingStars.jspf"%>
							</span>
							<span class="typeRetraite">${userTypeLabel}</span>
							<%-- <span>
								<!-- contributions -->							
								<c:out value="${nbOfReplies + nbOfQuestions}" />
								<c:choose>
  									<c:when test="${(nbOfReplies + nbOfQuestions) > 1}">
										contributions
								  	</c:when>
								  	<c:otherwise>
										contribution
									</c:otherwise>
								</c:choose>
								<!-- questions -->
								(<c:out value="${nbOfQuestions}" />
 								<c:choose>
  									<c:when test="${nbOfQuestions <= 1}">
										question
								  	</c:when>
								  	<c:otherwise>
										questions
									</c:otherwise>
								</c:choose>
								<!-- reponses -->
								et <c:out value="${nbOfReplies}" />
								<c:choose>
  									<c:when test="${nbOfReplies <= 1}">
										r&eacute;ponse)
								  	</c:when>
								  	<c:otherwise>
										r&eacute;ponses)
									</c:otherwise>
								</c:choose>
							</span> --%>
						</span>
					</div>
				</div>
			</c:forEach>
		</div>
	</c:if>
</div>
