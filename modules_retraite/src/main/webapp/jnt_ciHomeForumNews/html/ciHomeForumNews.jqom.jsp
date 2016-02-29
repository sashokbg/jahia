<%--
	@author : lakreb
	@created : 17 oct. 2012
	@Id : US-1200
	@description : Remontee des derniers posts avec en priorite les nons-repondus

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf" %>

<c:set var="start" value="<%= new Date() %>" />
<jcr:nodeProperty var="pagePicker" name="pagePicker"
	node="${currentNode}" />
<jcr:nodeProperty var="nbOfQuestions" name="nbOfQuestions"
	node="${currentNode}" />
<jcr:nodeProperty var="nbOfAnswers" name="nbOfAnswers"
	node="${currentNode}" />

<c:set var="queryPath"
	value="${not empty pagePicker ? pagePicker.node.path : renderContext.site.home.path}" />

<template:addCacheDependency flushOnPathMatchingRegexp="${queryPath}/.*/ciQuestion" />

<div id="surLesForums">
	<h2 class="titrePage titreThematiquePage">
		<jcr:nodeProperty name="titreThematiquePage" node="${currentNode}" />
	</h2>
	<h3 class="sousTitreBlocHome">
		<jcr:nodeProperty name="sousTitreBlocHomeQ" node="${currentNode}" />
	</h3>

	<c:if test="${nbOfQuestions.long > 0}">
		<jcr:jqom var="listOfPost" limit="${nbOfQuestions.long}">
			<query:selector nodeTypeName="jnt:ciQuestion" selectorName="post" />
			<query:descendantNode path="${queryPath}" selectorName="post" />
			<query:and>
				<query:equalTo value="0" propertyName="nbOfReplies" />
				<query:equalTo value="0" propertyName="nbOfProfReplies" />
				<query:notEqualTo value="true" propertyName="isNotPickable" />
			</query:and>
			<query:sortBy propertyName="jcr:created" selectorName="post"
				order="desc" />
		</jcr:jqom>
		<c:if test="${listOfPost.nodes.size <= 0}">
			<jcr:jqom var="listOfPost" limit="${nbOfQuestions.long}">
				<query:selector nodeTypeName="jnt:ciQuestion" selectorName="post" />
				<query:descendantNode path="${queryPath}" selectorName="post" />
				<query:notEqualTo value="true" propertyName="isNotPickable" />
				<query:sortBy propertyName="jcr:created" selectorName="post"
					order="desc" />
			</jcr:jqom>
		</c:if>
		<c:choose>
			<c:when test="${listOfPost.nodes.size > 0}">
				<c:forEach items="${listOfPost.nodes}" var="post" varStatus="status">
					<c:set var="userNode" value="${post.properties['user'].node}" />
					<template:addCacheDependency node="${userNode}" />
					<%@include file="../../common/ciSetUserInfo.jspf"%>
					<div class="ligneQuestionHome ${status.count % 2 == 0 ? 'pair' : 'impair'} nonAnswered">
						<div class="avatar">
							<a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}"
								class="blocQuestionAvatar">
								<img src="${avatarSrc40}" height="40px" width="40px" alt="Photo Avatar" />
							</a>
						</div>
						<div class="colonneContenuListeQ">
						<div class="userLink">
							<strong><a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}">${pseudo}</a></strong><span class="pipeFell">|</span><span>${userTypeLabel}</span>
						</div>	
							<h3>
								<a class="lienRepondreForumHome"
									href="${jcr:getParentOfType(post,'jnt:page').url}">${cia:cutString(functions:removeHtmlTags(post.properties['title'].string), 80)}</a>
							</h3>
							<div class="infosQuestion">
								<ul>
									<li class="typeForum">${cia:getThematicParent(post).properties['shortTitle'].string}</li>
									<li>${cia:formatDate(post.properties['jcr:created'].date.time, replyDatePattern, true)}</li>
									<c:set var="nbOfViews" value="${not empty post.properties['nbOfViews'] ? post.properties['nbOfViews'].long : '0'}" />
									<li class="last">${nbOfViews} vue${nbOfViews gt 1 ? 's' : ''}</li>
								</ul>
							</div>
							<a href="${jcr:getParentOfType(post,'jnt:page').url}"
								class="btRepondreForumHome"> <!-- ne pas effacer -->
							</a>
							<c:if
								test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
								<form action="${url.base}${currentNode.path}.setPickableNode.do">
									<input type="hidden" name="nodePath" value="${post.path}" /> <input
										type="hidden" name="jcrRedirectTo"
										value="${url.base}${renderContext.mainResource.node.path}" /> <a
										href="javascript:void(0)" OnClick="$(this).parent().submit();"
										class="supprimerQuestion">Supprimer</a>
								</form>
							</c:if>
						</div>
						<div class="breaker"></div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="dataSection bandUp last impair">Aucune question
					pour le moment.</div>
			</c:otherwise>
		</c:choose>
	</c:if>

	<h3 class="sousTitreBlocHome">
		<jcr:nodeProperty name="sousTitreBlocHomeA" node="${currentNode}" />
	</h3>

	<c:if test="${nbOfAnswers.long > 0}">
		<jcr:jqom var="listOfPost" limit="${nbOfAnswers.long}">
			<query:selector nodeTypeName="jnt:ciReply" selectorName="post" />
			<query:descendantNode path="${queryPath}" selectorName="post" />
			<query:notEqualTo value="true" propertyName="isNotPickable" />
			<query:sortBy propertyName="jcr:created" selectorName="post"
				order="desc" />
		</jcr:jqom>
		<c:choose>
			<c:when test="${listOfPost.nodes.size > 0}">
				<c:forEach items="${listOfPost.nodes}" var="post" varStatus="status">
					<c:set var="userNode" value="${post.properties['user'].node}" />
					<template:addCacheDependency node="${userNode}" />
					<%@include file="../../common/ciSetUserInfo.jspf"%>
					<div
						class="${userTypeCss}  ligneQuestionHome reponse ${status.count % 2 == 0 ? 'pair' : 'impair'} answered">
						<div class="avatar">
							<a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}"
								class="blocQuestionAvatar">
								<img src="${avatarSrc40}" width="40px" height="40px" alt="Photo Avatar" />
							</a>
						</div>
						<div class="colonneContenuListeQ">
							<div class="headReponse">
							<h3>
								<a onclick="trackEvent(['Home page','Ont repondu','Voir la reponse']);"
									href="${jcr:getParentOfType(post,'jnt:page').url}?orderby=lastmod">${cia:cutString(functions:removeHtmlTags(cia:pageTitle(jcr:getParentOfType(post,'jnt:page'))), 30)}</a>
							</h3>
							<div class="infosQuestion">
								<ul>
									<li class="typeForum">${cia:getThematicParent(post).properties['shortTitle'].string}</li>
									<li>${cia:formatDate(post.properties['jcr:created'].date.time, replyDatePattern, true)}</li>
									<c:set var="postQuestion" value="${jcr:getParentOfType(post, 'jnt:ciQuestion')}" /> 
									<c:set var="nbOfViews" value="${not empty postQuestion.properties['nbOfViews'] ? postQuestion.properties['nbOfViews'].long : '0'}" />
									<li class="last">${nbOfViews} vue${nbOfViews gt 1 ? 's' : ''}</li>
								</ul>
							</div>
							<div class="breaker"></div>
							</div>
							<div class="userLink">
								<div class="headReponseDetail">
									<strong><a title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}">${isProfessional ? fullname : pseudo}</a></strong>
								<span class="pipeFell">|</span>
								<c:set var="displayedUser" value="${cia:getUserByPseudoname(pseudo)}"/>
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
							<a class="debutReponseForumHome"
								onclick="trackEvent(['Home page','Ont repondu','Voir la reponse']);"
								href="${jcr:getParentOfType(post,'jnt:page').url}?orderby=lastmod">
								${cia:cutString(functions:removeHtmlTags(post.properties['body'].string), 85)}
								</a>
							
							<c:if
								test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
								<form action="${url.base}${currentNode.path}.setPickableNode.do">
									<input type="hidden" name="nodePath" value="${post.path}" /> <input
										type="hidden" name="jcrRedirectTo"
										value="${url.base}${renderContext.mainResource.node.path}" /> <a
										href="javascript:void(0)" OnClick="$(this).parent().submit();"
										class="supprimerQuestion">Supprimer</a>
								</form>
							</c:if>
						</div>
						<div class="breaker"></div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="dataSection bandUp last impair">Aucune
					r&eacute;ponse pour le moment.</div>
			</c:otherwise>
		</c:choose>
	</c:if>
</div>
<c:set var="time">
<%= new Date().getTime() - ((Date)pageContext.getAttribute("start")).getTime() %>
</c:set>
<utility:logger value="search JQOM homeForumNews module executed in ${time} ms" level="info" />