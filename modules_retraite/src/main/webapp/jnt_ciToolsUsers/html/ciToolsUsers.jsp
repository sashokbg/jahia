<%--
	@author : lakreb
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<template:addResources resources="moderator.css" type="css" />

<template:addResources>
	<script>
		/*
		 * on future version extraction job could be parametised from client side using this template or not
		 */

		var userExtractorHtml = "<div id='thematiquesChoisies'>"
				+ "<div id='${extractUuid}' class='conteneurThematiqueChoisie fl'>"
				+ "<a><span class='libelleThematique fl'>${extractTitle}</span></a>"
				+ "<a onclick='removeUserExtractor('${extractUuid}')' class='lienRemoveThematique fl'></a>"
				+ "<div class='clear'></div> </div> </div>";

		$(document).ready(function() {
			$('.userLocked').click(
				function() {
					var that = $(this);
					var callableUrl = "${url.base}${currentNode.path}.ciUpdateUserPropertyAction.do";
					var userName =that.attr('jcr:userName');
					var locked = eval(that.attr('jcr:locked'));
					var parentCell = that.parent();
					parentCell.addClass('spinnerWait');
					var datas = {
						'userName' : userName
					};
					datas['propertyKey'] = 'j:accountLocked';
					datas['propertyValue'] = !locked;

					$.ajax({
						url : callableUrl, 
						data : datas,
						dataType  :"json",
						type : "post",
						success : function(json) {
							parentCell.removeClass('spinnerWait');
							if(json.update == 'ok'){
								var src = '/icons/' + (!locked ? '' : 'un') + 'lock.png';
								that.html((locked ? 'B' : 'D\u00E9b') + 'loquer <img src=\'' + src + '\'/>');
								that.attr('jcr:locked', !locked);
							} else {
								alert('Une erreur s\'est produite');
								that.children('img').attr('src', '/icons/delete.png');
							}
						},
						error : function() {
							alert('Une erreur s\'est produite');
							that.children('img').attr('src', '/icons/delete.png');
						}
					});
			});
		});
	</script>
</template:addResources>

<jcr:nodeProperty name="itemsPerPage" node="${currentNode}"
	var="itemsPerPage" />

<c:set var="itemsPerPage"
	value="${not empty itemsPerPage && !renderContext.editMode ? itemsPerPage.long : '3'}" />

<div id="moderatorTools">
	<div id="userProfil">
		<div id="thematiquesChoisies">
			<div class="conteneurThematiqueChoisie fl">
				<a
					href="${url.base}${currentNode.path}.ciUsersExtractor.do?extractor=SiteNewsletter"
					title="Exporter les abonn&eacute;s"><span
					class="libelleThematique fl">Exporter les abonn&eacute;s
						&agrave; la newsletter du site</span></a>
				<div class="clear"></div>
			</div>
		</div>
		<div id="thematiquesChoisies">
			<div class="conteneurThematiqueChoisie fl">
				<a
					href="${url.base}${currentNode.path}.ciUsersExtractor.do?extractor=GroupNewsletter"
					title="Exporter les abonn&eacute;s"><span
					class="libelleThematique fl">Exporter les abonn&eacute;s aux
						offres commerciales</span></a>
				<div class="clear"></div>
			</div>
		</div>
	</div>

	<div class="breaker"></div>

	<br />
	<hr />
	<br />

	<form action="${jcr:getParentOfType(currentNode,'jnt:page').url}">
		<input type="hidden" name="displayTab" value="${currentNode.name}${currentResource.moduleParams.ps}" />
		<fieldset title="Filtrer" style="border: gray solid 1px;">
			<legend>Filtre</legend>
			<label for="propertyValue">Ajouter un filtre : </label><input
				id="propertyValue" name="propertyValue" class="champQuestion"
				type="text" value="${param.propertyValue}"> <select
				name="propertyFilter">
				<option value="">_______________</option>
				<option
					${param.propertyFilter eq ciConstants.PROPERTIES_USER_PSEUDODENAME ? 'selected="selected"' : '' }
					value="${ciConstants.PROPERTIES_USER_PSEUDODENAME}">Pseudo</option>
				<option
					${param.propertyFilter eq ciConstants.PROPERTIES_USER_MAIL ? 'selected="selected"' : '' }
					value="${ciConstants.PROPERTIES_USER_MAIL}">E-mail</option>
			</select> <select name="typeFilter">
				<option value="">Tous</option>
				<option
					${param.typeFilter eq ciConstants.USER_TYPE_FUTUR_RETRAITE ? 'selected="selected"' : '' }
					value="${ciConstants.USER_TYPE_FUTUR_RETRAITE}">Futurs
					Retrait&eacute;s</option>
				<option
					${param.typeFilter eq ciConstants.USER_TYPE_RETRAITE_JUNIOR ? 'selected="selected"' : '' }
					value="${ciConstants.USER_TYPE_RETRAITE_JUNIOR}">Retrait&eacute;s
					Juniors</option>
			</select> <input type="submit" value="" class="btQuestion" id="searchSubmit">
		</fieldset>
		<fieldset title="Trier" style="border: gray solid 1px;">
			<legend>Tri</legend>
			<label for="sortField">Trier par : </label><select
				name="propertySorter" id="sortField">
				<option
					${param.propertySorter eq 'pseudo' ? 'selected="selected"' : '' }
					value="pseudoname">Pseudo</option>
				<option
					${param.propertySorter eq 'mostActiv' ? 'selected="selected"' : '' }
					value="mostActiv">Membres les plus actifs</option>
			</select><input type="submit" value="" class="btQuestion" id="searchSubmit">
		</fieldset>
	</form>

	<c:set var="filterSection">
		<c:if test="${not empty param.propertyFilter}">and user.[${param.propertyFilter}] like '%${fn:trim(fn:replace(param.propertyValue, ' ', ''))}%'
		</c:if>
		<c:if test="${not empty param.typeFilter}">and user.userType='${fn:trim(fn:replace(param.typeFilter, ' ', ''))}'
		</c:if>
	</c:set>

	<c:set var="userAlias" value="user" />
	<c:set var="clauses"
		value="${clauseSiteUsers} user.isMember='true' ${filterSection}" />
	<c:set var="orderBy"
		value="${not empty param.propertySorter && param.propertySorter ne 'mostActiv' ? param.propertySorter : 'pseudoname'}" />
	<c:set var="order" value="asc" />

	<c:set var="users"
		value="${cia:getUserList(userAlias, clauses, orderBy, order, 0)}" />

	<c:if
		test="${not empty param.propertySorter && param.propertySorter eq 'mostActiv'}">
		<c:set var="users" value="${cia:getMostActiveUsersList(users)}" />
	</c:if>

	<c:set target="${moduleMap}" property="usersList" value="${users}" />
	<c:set target="${moduleMap}" property="usersListTotalSize"
		value="${users.size}" />

	<c:set var="paginationOn" value="${false}" />
	<c:if test="${moduleMap.usersListTotalSize > itemsPerPage}">
		<c:set var="paginationOn" value="${true}" />
	</c:if>
	<c:if test="${moduleMap.usersListTotalSize > 0}">
		<template:initPager totalSize="${moduleMap.usersListTotalSize}"
			id="${currentNode.identifier}" pageSize="${itemsPerPage}" />
		<table class="userManager">
			<caption>Liste des utilisateurs enregistr&eacute;s depuis
				la communaut&eacute;</caption>
			<tr>
				<th>###</th>
				<th>Pseudo</th>
				<th>Nom</th>
				<th>Pr&eacute;nom</th>
				<th>Genre</th>
				<th>Type</th>
				<th>E-mail</th>
				<th>Questions</th>
				<th>R&eacute;ponses</th>
				<th>Total messages</th>
				<th>Date d'inscription</th>
			<tr>
				<c:forEach items="${moduleMap.usersList}" begin="${moduleMap.begin}"
					end="${moduleMap.end}" var="myuser" varStatus="status">
					<jcr:node var="userNode" uuid="${myuser.identifier}" />
					<%@include file="../../common/ciSetUserInfo.jspf"%>

					<c:set var="impair"
						value="${status.count % 2 eq 0 ? '' : ' impair' }" />

					<tr class="${impair}">
						<td style="width: 88px;"><a
							style="text-align: center; margin-left: 20px;" class="userLocked"
							jcr:userName="${userNode.name}" jcr:locked="${userLocked}"
							href="#"> ${userLocked ? 'D&eacute;bloquer' : 'Bloquer'} <img
								src="/icons/${userLocked ? '' : 'un'}lock.png" />
						</a></td>
						<td><a class="mbLink" target="_blank"
							title="Acc&eacute;der au profil" href="${publicProfileUrl}">${pseudo}</a></td>
						<td>${lastname}</td>
						<td>${firstname}</td>
						<td>${gender}</td>
						<td>${userTypeLabel}</td>
						<td>${email}</td>
						<td>${nbOfQuestions}</td>
						<td>${nbOfReplies}</td>
						<td>${nbOfReplies + nbOfQuestions}</td>
						<td><fmt:formatDate value="${subscribeDate.time}"
								pattern="dd/MM/yyyy HH':'mm" /></td>
					</tr>
				</c:forEach>
		</table>
		<c:if test="${paginationOn}">
			<br />
			<br />
			<%@include file="../../common/ciPagination.jspf"%>
		</c:if>
		<template:removePager id="${currentNode.identifier}" />
	</c:if>

	<div class="breaker"></div>
</div>