
<%--
	@author : el-aarko
	@created : 17 sept. 2012
	@Id : ciUsersBox
	@description : box des utilisateurs (membres ou professionnels)

 --%>

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
<%-- <c:set var="userClass">isMember</c:set> --%>
<c:set var="orderby">[jcr:created]</c:set>
<c:set var="order">desc</c:set>
<c:set var="limit">15</c:set>
<c:set var="increment">one</c:set>
<c:set var="seeAllUsersLinkLabel">Voir tous les membres</c:set>
<c:set var="seeAllUsersLinkUrl">${cia:clearUrl(siteAddress, renderContext.site.properties['membersListLink'].node)}</c:set>

<%-- Cas de la box membre avec les membres les plus actifs inscrits en premier --%>
<c:if test="${not boxType.boolean and boxType.string eq 'members' and not empty renderContext.site.properties['usersBoxListOrder'] and (renderContext.site.properties['usersBoxListOrder'].string == 'mostActive')}">
	<c:set var="orderby">nbOfReplies desc, nbOfQuestions desc</c:set>
	<c:set var="order" value=""/>	
	<c:set var="boxTitleVar">${empty boxMostActiveTitle ? 'Les membres les plus actifs' : boxMostActiveTitle.string}</c:set>	
</c:if>


<%-- Cas de la box professionnelle --%>
<c:if test="${not boxType.boolean and boxType.string eq 'professionals'}">
	<c:set var="conteanerDivClass"> statutPro</c:set>
	<c:set var="userClass" value="${clauseSiteUsers} user.isProfessional='true' "/>
	<c:set var="orderby">[jcr:created]</c:set>
	<c:set var="order">desc</c:set>
	<c:set var="limit" value=""/>
	<c:set var="increment">two</c:set>
	<c:set var="seeAllUsersLinkLabel">Voir tous les professionnels</c:set>
	<c:set var="seeAllUsersLinkUrl">${cia:clearUrl(siteAddress, renderContext.site.properties['professionalsListLink'].node)}</c:set>
</c:if>
<c:set var="userAlias" value="user" />
<%-- <c:set var="sqlQuery">select * from [jnt:user] as user where ${clauseSiteUsers} user.${userClass } = 'true' order by ${orderby }</c:set> --%>
<c:set var="usersListQuery" value="${cia:getUserList(userAlias, userClass, orderby, order, limit)}" />
<c:set var="usersCount" value="${usersListQuery.size}" />

<c:if test="${not renderContext.editMode }">
	<template:addResources type="javascript" resources="jquery.movingboxes.js" />
	<template:addResources insert="true">  <%-- laisser le insert="true" afin que ca script soit dans le head. Sinon ga.jspf ne fonctionne pas correctement ! --%>
		<script type="text/javascript">
		$(document).ready(function(){
	
			$('#slider-${increment}').movingBoxes({
				startPanel   : 3,      // start with this panel
				reducedSize  : 0.89,    // non-current panel size: 80% of panel size
				fixedHeight  : true,
				hashTags     : false, 
				buildNav     : true,   // if true, navigation links will be added
				/*navFormatter : function(index, panel){ return panel.find('h2 span').text(); }*/ // function which gets nav text from span inside the panel header
				navFormatter : function(index, panel){ return panel.find('span').html(); } // function which gets nav text from span inside the panel header
	
				// width and panelWidth options removed in v2.2.2, but still backwards compatible
				//width        : 500    // overall width of movingBoxes (not including navigation arrows)
				// panelWidth   : 0.7,    // current panel width
	
			});
			$(".div${currentNode.identifier} a.lienVoirTousMembres").html("${seeAllUsersLinkLabel}").attr("href", "${seeAllUsersLinkUrl}").attr("title","${seeAllUsersLinkLabel}");
		});
		</script>
	</template:addResources>
</c:if>
<div class="blocSlideMembres ${conteanerDivClass } div${currentNode.identifier}" style="display:block;margin-bottom:10px;">
	<h3 class="band">${boxTitleVar}</h3>
	<c:if test="${usersCount eq 0 || renderContext.editMode}">
		Aucun membre jusqu'&agrave; pr&eacute;sent.
	</c:if>
	<c:if test="${usersCount gt 0 && !renderContext.editMode}">
		<div id="slider-${increment }">
			<c:forEach items="${usersListQuery}" var="user" >
				<jcr:node var="userNode" uuid="${user.identifier}" />
				<%@include file="../../common/ciSetUserInfo.jspf"%>
				<div> 
					<a href="${publicProfileUrl}" title="Acc&eacute;der au profil de ${pseudo}">
						<img src="${avatarSrc51}" title="Acc&eacute;der au profil de ${pseudo}" alt="avatar de ${pseudo}" />
					</a>
					<div class="legende">
						<span> 
							<c:choose>
								<c:when test="${cia:isProfessional(userNode)}">
									<span class="pseudoSlider">${firstname}&nbsp;${lastname}</span>
									<span class="rolePro">${function}</span>
								</c:when>
								<c:otherwise>
									<span class="pseudoSlider">${pseudo }</span>
									<span class="typeRetraite">${userTypeLabel}</span>
									<c:if test="${(renderContext.site.properties['usersBoxListOrder'].string == 'mostActive')}">
										<c:if test="${(nbOfReplies + nbOfQuestions eq 0) ||(nbOfReplies + nbOfQuestions eq 1)}">
											Nombre de message :  ${nbOfReplies+nbOfQuestions}<br/>
										</c:if>
										<c:if test="${(nbOfReplies + nbOfQuestions gt 1)}">
											Nombre de messages :  ${nbOfReplies+nbOfQuestions}<br/>
										</c:if>
									</c:if>
									<c:if test="${(renderContext.site.properties['usersBoxListOrder'].string == 'createdDate')}">
										<span><fmt:message key="member.since" />&nbsp;${cia:memberSince(subscribeDate)}</span>
									</c:if>

								</c:otherwise>
							</c:choose>
						</span>
					</div>
				</div>
			</c:forEach>
		</div>
	</c:if>
</div>
