<%--
	@author : lakreb
	@created : 17 oct. 2012
	@Id : US-1200
	@description : Remontee des derniers posts avec en priorite les nons-repondus

 --%>

<%@include file="../../common/declarations.jspf"%>

<c:set
	value="${jcr:getParentOfType(currentNode, 'jnt:virtualsite').path}"
	var="queryPath" />

<div id="surLesForums">
	<h2 class="titrePage titreThematiquePage">SUR LES FORUMS</h2>
	<h3 class="sousTitreBlocHome">Derni&egrave;res Questions sans
		r&eacute;ponses</h3>
	<jcr:jqom var="listOfPost">
		<query:selector nodeTypeName="jnt:ciQuestion" selectorName="post" />
		<query:descendantNode path="${queryPath}" selectorName="post" />
		<query:and>
			<query:equalTo value="0" propertyName="nbOfReplies" />
			<query:equalTo value="0" propertyName="nbOfProfReplies" />
			<query:or>
				<query:not>
					<query:propertyExistence selectorName="post"
						propertyName="isNotToolsDisplayed" />
				</query:not>
				<query:equalTo value="false" propertyName="isNotToolsDisplayed" />
			</query:or>
		</query:and>
		<query:sortBy propertyName="jcr:created" selectorName="post"
			order="desc" />
	</jcr:jqom>
	<c:choose>
		<c:when test="${listOfPost.nodes.size > 0}">
			<c:forEach items="${listOfPost.nodes}" var="post" varStatus="status">
				<c:set var="userNode" value="${post.properties['user'].node}" />
				<%@include file="../../common/ciSetUserInfo.jspf"%>
				<div
					class="ligneQuestionHome ${status.count % 2 == 0 ? 'pair' : 'impair'}">
					<div class="avatar">
						<a target="_blank" title="Acc&eacute;der au profil de ${pseudo}"
							href="${publicProfileUrl}" class="blocQuestionAvatar">
							<img src="${avatarSrc40}" height="40px" width="40px"  alt="Photo Avatar" />
						</a>
					</div>
					<div class="colonneContenuListeQ">
						<div class="userLink">
							<strong><a target="_blank"
								title="Acc&eacute;der au profil de ${pseudo}"
								href="${publicProfileUrl}">${pseudo}</a></strong><span class="pipeFell">|</span><span>${userTypeLabel}</span>
						</div>
						<h3>
							<a target="_blank"
								href="${jcr:getParentOfType(post,'jnt:page').url}">${cia:cutString(functions:removeHtmlTags(post.properties['title'].string),
								80)}</a>
						</h3>
						<div class="infosQuestion">
							<ul>
								<li class="typeForum">${cia:getThematicParent(post).properties['shortTitle'].string}</li>
								<li>${cia:formatDate(post.properties['jcr:created'].date.time,
									questionDatePattern, true)}</li>
								<c:set var="nbOfViews"
									value="${not empty post.properties['nbOfViews'] ? post.properties['nbOfViews'].long : '0'}" />
								<li class="last">${nbOfViews} vue${nbOfViews gt 1 ? 's' :
									''}</li>
							</ul>
						</div>
						<c:if
							test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
							<form
								action="${url.base}${currentNode.path}.setPickableNode.do?displayTab=${currentNode.name}${currentResource.moduleParams.ps}">
								<input type="hidden" name="nodePath" value="${post.path}" /> <input
									type="hidden" name="moderatorTools" value="true" /> <a
									href="javascript:void(0)" OnClick="$(this).parent().submit();"
									class="supprimerQuestion">Supprimer du tableau de bord</a>
							</form>
						</c:if>
					</div>
					<div class="breaker"></div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="dataSection bandUp last impair">Aucune question
				sans r&eacute;ponses pour le moment pour le moment.</div>
		</c:otherwise>
	</c:choose>

	<h3 class="sousTitreBlocHome">Derni&egrave;res r&eacute;ponses</h3>

	<jcr:jqom var="listOfPost">
		<query:selector nodeTypeName="jnt:ciReply" selectorName="post" />
		<query:descendantNode path="${queryPath}" selectorName="post" />
		<query:or>
			<query:not>
				<query:propertyExistence selectorName="post"
					propertyName="isNotToolsDisplayed" />
			</query:not>
			<query:equalTo value="false" propertyName="isNotToolsDisplayed" />
		</query:or>
		<query:sortBy propertyName="jcr:created" selectorName="post"
			order="desc" />
	</jcr:jqom>
	<c:choose>
		<c:when test="${listOfPost.nodes.size > 0}">
			<c:set target="${moduleMap}" property="postsList"
				value="${listOfPost.nodes}" />
			<c:set target="${moduleMap}" property="postsListTotalSize"
				value="${listOfPost.nodes.size}" />

			<c:set var="paginationOn" value="${false}" />
			<c:if test="${moduleMap.postsListTotalSize > 6}">
				<c:set var="paginationOn" value="${true}" />
			</c:if>
			<template:initPager totalSize="${moduleMap.postsListTotalSize}"
				id="${currentNode.identifier}" pageSize="6" />

			<c:forEach items="${moduleMap.postsList}" begin="${moduleMap.begin}"
				end="${moduleMap.end}" var="post" varStatus="status">
				<c:set var="userNode" value="${post.properties['user'].node}" />
				<%@include file="../../common/ciSetUserInfo.jspf"%>
				<div
					class="${userTypeCss} ligneQuestionHome reponse ${status.count % 2 == 0 ? 'pair' : 'impair'}">
					<div class="avatar">
						<a target="_blank" title="Acc&eacute;der au profil de ${pseudo}"
							href="${publicProfileUrl}" class="blocQuestionAvatar">
							<img src="${avatarSrc40}"  height="40px" width="40px" alt="Photo Avatar" />
						</a>
					</div>
					<div class="colonneContenuListeQ">
						<div class="headReponse">
							<h3>
								<a target="_blank"
									href="${jcr:getParentOfType(post,'jnt:page').url}?orderby=lastmod">${cia:cutString(functions:removeHtmlTags(cia:pageTitle(jcr:getParentOfType(post,'jnt:page'))),
									30)}</a>
							</h3>
							<div class="infosQuestion">
								<ul>
									<li class="typeForum">${cia:getThematicParent(post).properties['shortTitle'].string}</li>
									<li>${cia:formatDate(post.properties['jcr:created'].date.time,
										replyDatePattern, true)}</li>
									<c:set var="postQuestion"
										value="${jcr:getParentOfType(post, 'jnt:ciQuestion')}" />
									<c:set var="nbOfViews"
										value="${not empty postQuestion.properties['nbOfViews'] ? postQuestion.properties['nbOfViews'].long : '0'}" />
									<li class="last">${nbOfViews} vue${nbOfViews gt 1 ? 's' :
										''}</li>
								</ul>
							</div>
							<div class="breaker"></div>
						</div>
						<div class="userLink">
							<div class="headReponseDetail">
								<strong><a target="_blank"
									title="Acc&eacute;der au profil de ${pseudo}"
									href="${publicProfileUrl}">${isProfessional ? fullname :
										pseudo}</a></strong> <span class="pipeFell">|</span>
								<c:set var="displayedUser"
									value="${cia:getUserByPseudoname(pseudo)}" />
								<c:choose>
									<c:when test="${cia:isModerator(renderContext,displayedUser)}">
										<span class="functionOrStatus">Mod&eacute;rateur</span>
									</c:when>
									<c:otherwise>
										<span class="functionOrStatus">${userTypeLabel}</span>
									</c:otherwise>
								</c:choose>
								<span class="aRepondu"> a r&eacute;pondu :</span>
							</div>
						</div>
						<a target="_blank" class="debutReponseForumHome"
							href="${jcr:getParentOfType(post,'jnt:page').url}?orderby=lastmod">
							${cia:cutString(functions:removeHtmlTags(post.properties['body'].string),
							85)} </a>
						<c:if
							test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
							<form
								action="${url.base}${currentNode.path}.setPickableNode.do?displayTab=${currentNode.name}${currentResource.moduleParams.ps}">
								<input type="hidden" name="nodePath" value="${post.path}" /> <input
									type="hidden" name="moderatorTools" value="true" /> <input
									name="displayTab" type="hidden" value="${param.displayTab}" />
								<a href="javascript:void(0)"
									OnClick="$(this).parent().submit();" class="supprimerQuestion">Supprimer
									du tableau de bord</a>
							</form>
						</c:if>
					</div>
					<div class="breaker"></div>
				</div>
			</c:forEach>
			<c:if test="${paginationOn}">
				<br />
				<br />
				<%@include file="../../common/ciPagination.jspf"%>
			</c:if>
			<template:removePager id="${currentNode.identifier}" />
		</c:when>
		<c:otherwise>
			<div class="dataSection bandUp last impair">Aucune
				r&eacute;ponse pour le moment.</div>
		</c:otherwise>
	</c:choose>

</div>