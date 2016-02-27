<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="../../jnt_ciProList/html/ciProList.js.jspf" %>
<%@include file="ga.jspf" %>

<template:addCacheDependency flushOnPathMatchingRegexp="/users/.*" />

<h2 class="titrePageVert">Les membres</h2>

<c:set var="filterSection">
	<c:if test="${not empty param.thematicPath}">and user.selectedThematics like '%${param.thematicPath}%'</c:if>
	<c:if test="${not empty param.rubricPath and empty param.thematicPath}">
		<jcr:node var="rubric" path="${param.rubricPath}"/>
		<c:forEach items="${jcr:getChildrenOfType(rubric, 'jnt:page')}" var="thematic" varStatus="step">
			<c:if test="${step.first}">
			and ((user.selectedThematics like '%${thematic.identifier}%' 
			</c:if>
			<c:if test="${not step.first}">
			or user.selectedThematics like '%${thematic.identifier}%' 
			</c:if>
			<c:if test="${step.last}">
			) or user.selectedRubrics like '%${rubric.identifier}%') 
			</c:if>
		</c:forEach>
	</c:if>
</c:set>

<c:if test="${not empty param.ordre and param.ordre eq 'pseudo'}">
	<c:set var="orderByParam">lower(pseudoname)</c:set>
	<c:set var="order">asc</c:set>
</c:if>

<c:if test="${empty param.ordre or (not empty param.ordre and param.ordre eq 'inscription')}">
	<c:set var="orderByParam">[jcr:created]</c:set>
	<c:set var="order">desc</c:set>
</c:if>

<div id="lesMembres">
	<br/>
	<jcr:sql var="moderatorGroups"
	        sql="select * from [jnt:group] as group where isdescendantnode(group, ['${renderContext.site.path}']) and group.[j:nodename] = '${ciConstants.USER_GROUP_MODERATOR}'"/>
	
	<c:forEach var="moderatorGroup" items="${moderatorGroups.nodes}" varStatus="status"> 
		<jcr:sql var="moderatorsListQuery"
	        sql="select * from [jnt:member] as member where isdescendantnode(member, ['${moderatorGroup.path}'])"/>
 	</c:forEach>
	
	 <c:if test="${moderatorsListQuery.nodes.size > 0}">
 		<c:set var="moderateurClause" value="" />
		<c:forEach var="member" items="${moderatorsListQuery.nodes}"
			varStatus="status">
			<c:set var="moderateurClause"
				value="${moderateurClause} and user.[jcr:uuid]<>'${member.properties['j:member'].node.identifier}'" />
		</c:forEach>
	</c:if>
	<c:set var="userAlias" value="user" />
	<c:set var="clauses" value="${clauseSiteUsers} user.isMember='true' and user.[j:accountLocked]<>'true' ${moderateurClause} ${filterSection}" />
	<c:set var="futureRetiredClauses" value="${clauses} and user.userType ='radFuturRetraite'" />
	<c:set var="retiredClauses" value="${clauses} and user.userType ='radRetJunior'" />
	<c:set var="orderBy" value="${orderByParam}" />
	<c:set var="order" value="${order}" />
	
	<c:set var="usersList" value="${cia:getUserList(userAlias, clauses, orderBy, order, 0)}" />
	<c:set var="futureRetiredList" value="${cia:getUserList(userAlias, futureRetiredClauses, orderBy, order, 0)}" />
	<c:set var="retiredList" value="${cia:getUserList(userAlias, retiredClauses, orderBy, order, 0)}" />
	
	<c:set target="${moduleMap}" property="membersList" value="${usersList}"/>
	<c:set target="${moduleMap}" property="futurRetiredList" value="${futureRetiredList}"/>
	<c:set target="${moduleMap}" property="juniorRetiredList" value="${retiredList}"/>
	
	<c:set target="${moduleMap}" property="membersListTotalSize" value="${usersList.size}"/>
	<c:set target="${moduleMap}" property="membersListfuturRetiredSize" value="${futureRetiredList.size}"/>
	<c:set target="${moduleMap}" property="membersListjuniorRetiredSize" value="${retiredList.size}"/>
	
	<div class="dataSection">
	<c:if test="${moduleMap.membersListTotalSize le 1}">
		<div class="detailTitre">Il y a actuellement <span class="vert">${moduleMap.membersListTotalSize}</span> membre.</div>
		<div class="uppercase">
		<c:if test="${moduleMap.membersListfuturRetiredSize le 1}">
			<span class="vert">${moduleMap.membersListfuturRetiredSize}</span> futur retrait&eacute; et 
		</c:if>
		<c:if test="${moduleMap.membersListfuturRetiredSize gt 1}">
			<span class="vert">${moduleMap.membersListfuturRetiredSize}</span> futurs retrait&eacute;s et 
		</c:if>
		<c:if test="${moduleMap.membersListjuniorRetiredSize le 1}">
			<span class="vert">${moduleMap.membersListjuniorRetiredSize}</span> retrait&eacute; junior.
		</c:if>
		<c:if test="${moduleMap.membersListjuniorRetiredSize gt 1}">
			<span class="vert">${moduleMap.membersListjuniorRetiredSize}</span> retrait&eacute;s juniors.
		</c:if>
	</div>
	</c:if>
	<c:if test="${moduleMap.membersListTotalSize gt 1}">
		<div class="detailTitre">Il y a actuellement <span class="vert">${moduleMap.membersListTotalSize}</span> membres.</div>
		<div class="uppercase">
			<c:if test="${moduleMap.membersListfuturRetiredSize le 1}">
			<span class="vert">${moduleMap.membersListfuturRetiredSize}</span> futur retrait&eacute; et 
		</c:if>
		<c:if test="${moduleMap.membersListfuturRetiredSize gt 1}">
			<span class="vert">${moduleMap.membersListfuturRetiredSize}</span> futurs retrait&eacute;s et 
		</c:if>
		<c:if test="${moduleMap.membersListjuniorRetiredSize le 1}">
			<span class="vert">${moduleMap.membersListjuniorRetiredSize}</span> retrait&eacute; junior.
		</c:if>
		<c:if test="${moduleMap.membersListjuniorRetiredSize gt 1}">
			<span class="vert">${moduleMap.membersListjuniorRetiredSize}</span> retrait&eacute;s juniors.
		</c:if>
		</div>
	</c:if>
	<c:if test="${moduleMap.membersListTotalSize eq 0}">
			<c:set var="zeroResultCss" value="last"/>
	</c:if>						
	</div>
	
	<div class="dataSection ${zeroResultCss}">	
		<form method="get" name="voirLes">					
			<div class="selectMembresContainer">
				<span>Voir</span>
				<select name="type" onchange="this.form.submit();">
					<option value="">Tous</option>
					<option value="radFuturRetraite" ${param.type eq 'radFuturRetraite' ? 'selected="selected"' : '' }>Futurs retrait&eacute;s</option>
					<option value="radRetJunior" ${param.type eq 'radRetJunior' ? 'selected="selected"' : '' }>Retrait&eacute;s Juniors</option>
				</select>
			</div>
			<div class="selectMembresContainer">									
				<span>Trier par</span>								
				<select name="ordre" onchange="this.form.submit();">
					<option value="inscription" ${param.ordre eq 'inscription' ?  'selected="selected"' : ''}>Date d'inscription</option>
					<option value="pseudo" ${param.ordre eq 'pseudo' ?  'selected="selected"' : ''}>Pseudo</option>
				</select>									
			</div>
			<div class="clear"></div>
			<div id="thematicSort"><br />
				<div class="selectMembresContainer">
					<span>Trier par th&eacute;matique</span>
					<select style="width: 288px;" id="selectRubrics" name="rubricPath" onChange="this.form.submit();">
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
				<div class="selectMembresContainer" <c:if test="${empty param.rubricPath || param.rubricPath eq ''}">style="display: none;"</c:if>>									
					<span>Sous-th&eacute;matique</span>								
					<select style="width: 288px;" id="selectThematics" name="thematicPath" onChange="this.form.submit();">
						<option value="">S&eacute;lectionner</option>
					</select>
				</div>
			</div>
		</form>
		<div class="clear"></div>							
	</div>

	<c:if test="${not empty param.type }">
		<c:if test="${param.type eq 'radFuturRetraite' }">
			<c:set target="${moduleMap}" property="listName"
				value="${moduleMap.futurRetiredList}" />
		</c:if>
		<c:if test="${param.type eq 'radRetJunior' }">
			<c:set target="${moduleMap}" property="listName"
				value="${moduleMap.juniorRetiredList}" />
		</c:if>
	</c:if>
	<c:if test="${empty param.type }">
		<c:set target="${moduleMap}" property="listName"
			value="${moduleMap.membersList}" />
	</c:if>

	<c:set target="${moduleMap}" property="listSize">
		<c:if test="${not empty param.type }">
			<c:if test="${param.type eq 'radFuturRetraite' }">
				${moduleMap.membersListfuturRetiredSize}
			</c:if>
		<c:if test="${param.type eq 'radRetJunior' }">
				${moduleMap.membersListjuniorRetiredSize}
			</c:if>
		</c:if>
		<c:if test="${empty param.type }">
			${moduleMap.membersListTotalSize}
		</c:if>
	</c:set>
	
	<c:set var="paginationOn" value="off"/>	
	<c:if test="${moduleMap.listSize > renderContext.site.properties['memberListLineperPage'].long}">
		<c:set var="paginationOn" value="On"/>	
	</c:if>
	
	<c:if test="${moduleMap.listSize > 0}">
		<div class="dataSection ${paginationOn == 'On' ? '' : ' last' }">
		<template:initPager totalSize="${moduleMap.listSize}" id="${currentNode.identifier}" pageSize="${renderContext.site.properties['memberListLineperPage'].long}"/>
		<c:forEach items="${moduleMap.listName}" var="myuser" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}" >	
			${(status.count-1) % 4 eq 0 ? '<div>' : ''}
			<jcr:node var="userNode" uuid="${myuser.identifier}"/>
			<%@include file="../../common/ciSetUserInfo.jspf"%>
			<div class="membreContainer">
				<a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}" ><!-- ne pas effacer --></a>
				<img src="${avatarSrc65}" width="65px" height="65px" alt="Photo Avatar" />
				<div class="pseudo">${empty pseudo ? '&nbsp;' : pseudo}</div>
				<div class="vert">${userTypeLabel}</div>
				<div class="memberSince"><fmt:message key="member.since" />&nbsp;${cia:memberSince(subscribeDate)}</div>
			</div>	
			${status.count % 4 eq 0 || status.last ? '<div class="breaker"><!-- ne pas effacer --></div></div>' : ''}	
		</c:forEach> 
		<div class="clear"></div>
		</div>
	</c:if>
	<c:if test="${paginationOn == 'On'}">
		<div class="dataSection last">
			<%@include file="../../common/ciPagination.jspf"%>
		</div>
	</c:if>
	<template:removePager id="${currentNode.identifier}"/>
</div>