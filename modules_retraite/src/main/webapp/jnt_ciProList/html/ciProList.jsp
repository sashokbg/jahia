<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<%@include file="ciProList.js.jspf" %>
<%@include file="ga.jspf" %>

<h2 class="titrePage">Les professionnels</h2>
<div id="lesPro">
	<c:set var="filterSection">
		<c:if test="${not empty param.thematicPath }">and user.selectedThematics like '%${param.thematicPath}%'
		</c:if>
		<c:if test="${empty param.rubricPath and empty param.thematicPath}"></c:if>
		<c:if test="${not empty param.rubricPath and empty param.thematicPath}">
			<jcr:node var="rubric" path="${param.rubricPath }"/>
			<c:forEach items="${jcr:getChildrenOfType(rubric, 'jnt:page')}" var="thematic" varStatus="step">
				<c:if test="${step.first}">
				and (user.selectedThematics like '%${thematic.identifier}%' 
				</c:if>
				<c:if test="${not step.first}">
				or user.selectedThematics like '%${thematic.identifier}%' 
				</c:if>
				<c:if test="${step.last}">
				) 
				</c:if>
			</c:forEach>
		</c:if>
	</c:set>
	 
	<c:set var="userAlias" value="user" />
	<c:set var="clauses" value="${clauseSiteUsers} user.isProfessional='true' and user.[j:accountLocked]<>'true' ${filterSection} "/>
	<c:set var="usersList" value="${cia:getUserList(userAlias, clauses,'[j:lastName]', order, 0)}" />
	
	<c:set target="${moduleMap}" property="membersList" value="${usersList}"/>	
	<c:set target="${moduleMap}" property="membersListTotalSize" value="${usersList.size}"/>

	<c:set var="paginationOn" value="${false}"/>	
	<c:if test="${moduleMap.membersListTotalSize > renderContext.site.properties['proListLineperPage'].long}">
		<c:set var="paginationOn" value="${true}"/>	
	</c:if>
	
	<div class="dataSection">
	<c:if test="${moduleMap.membersListTotalSize > 1}">
			<div class="detailTitre"><span class="bleu">${moduleMap.membersListTotalSize}</span> professionnels sont disponibles pour r&eacute;pondre &agrave; vos questions les plus pointues</div>
			<div>et vous accompagner dans votre pr&eacute;paration</div>	
	</c:if>
	<c:if test="${moduleMap.membersListTotalSize eq 1}">
		<div class="detailTitre"><span class="bleu">${moduleMap.membersListTotalSize }</span> professionnel est disponible pour r&eacute;pondre &agrave; vos questions les plus pointues</div>
		<div>et vous accompagner dans votre pr&eacute;paration</div>
	</c:if>
	<c:if test="${moduleMap.membersListTotalSize eq 0}">
		<div class="detailTitre"><span class="bleu">0</span> professionnel est disponible pour r&eacute;pondre &agrave; cette sous-th&eacute;matique, veuillez en s&eacute;lectionner une autre. Merci</div>
		<c:set var="zeroResultCss" value="last"/> 
	</c:if>
	</div>
	<div class="dataSection ${zeroResultCss }">
		<form name="" action="" method="get">						
		<div class="selectMembresContainer">
			<span>Trier par th&eacute;matique</span>
											
			<select id="selectRubrics"  class="champFormSignalerAbus" name="rubricPath" onChange="$('#selectThematics').val('');this.form.submit();">
				<option value="">S&eacute;lectionner</option>
				<c:forEach items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}" var="subchild" varStatus="step">
					<c:if test="${subchild.properties['isRubric'].boolean}">
						<c:set var="isselected"></c:set>
						<c:if test="${param.rubricPath eq subchild.path}">
							<c:set var="isselected">selected="selected" </c:set>
						</c:if>
						<option value="${subchild.path}" <c:out value="${isselected}" escapeXml="false" />>${subchild.properties['jcr:title'].string }</option>
					</c:if>
				</c:forEach>
			</select>
		</div>
		<div class="selectMembresContainer">									
			<span>Sous-th&eacute;matique</span>								
			<select id="selectThematics" class="champFormSignalerAbus" name="thematicPath" onChange="this.form.submit();">
				<option value="">S&eacute;lectionner</option>
			</select>
		</div>
		</form>
		<div class="clear"><!-- ne pas effacer --></div>							
	</div>

	<template:initPager totalSize="${moduleMap.membersListTotalSize}" id="${currentNode.identifier}" pageSize="${renderContext.site.properties['proListLineperPage'].long}"/>
	<c:forEach items="${moduleMap.membersList}" var="myuser" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}" >
		<jcr:node var="userNode" uuid="${myuser.identifier }"/>
		<%@include file="../../common/ciSetUserInfo.jspf"%>
		
		<c:set var="impair" value="${status.count % 2 eq 0 ? '' : ' impair' }"/>
		<c:set var="last" value="${status.last && !paginationOn ? ' last' : '' }"/>
			
		<div class="dataSection proContainer ${impair} ${last}">							
			<a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}" ><!-- ne pas effacer --></a>
			<div class="colonneAvatarPro">
				<img src="${avatarSrc}" alt="Photo Avatar" />
			</div>							
			<div class="colonneContenuPro">
				<p class="pseudo">${firstname}&nbsp;${lastname}</p>				
				<c:set var="displayedUser" value="${cia:getUserByPseudoname(pseudo)}"/>
				<c:choose>					
					<c:when test="${cia:isModerator(renderContext,displayedUser)}">
						<p class="bleu">Mod&eacute;rateur</p>
					</c:when>
					<c:otherwise>					
						<p class="bleu">${userTypeLabel}</p>
						<p>Domaines d'expertise : 
							<c:choose>
								<c:when test="${not empty selectedThematics}">							 
								 	<c:forTokens items="${selectedThematics}" delims="," var="item" varStatus="status">
										<jcr:node var="itemNode" uuid="${item}" />
										<c:if test="${not empty itemNode}">
											${itemNode.properties['jcr:title'].string}${status.last ? '' : ', '}
										</c:if>
									</c:forTokens>
									
								</c:when>
								<c:otherwise>
									Non renseign&eacute;
								</c:otherwise>
							</c:choose>
						</p>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${nbOfReplies > 1}">
						<p>${nbOfReplies} r&eacute;ponses sur le site</p>					
					</c:when>
					<c:otherwise>
						<p>${nbOfReplies} r&eacute;ponse sur le site</p>
					</c:otherwise>					
				</c:choose>			
			</div>							
			<div class="clear"><!-- ne pas effacer --></div>									
		</div>
	</c:forEach>
	<c:if test="${paginationOn}">
		<div class="dataSection last">
			<%@include file="../../common/ciPagination.jspf"%>
		</div>
	</c:if>
	<template:removePager id="${currentNode.identifier}"/>

</div>