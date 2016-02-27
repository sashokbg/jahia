<%--
	@author : Sylvain Pichard
	@created : 18 octobre 2012
	@Id : jnt_ciThematicsListHome
	@description : Liste des thematiques remontee sur la Home

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="nbOfElements" var="nbOfElements" /> 

<template:addCacheDependency flushOnPathMatchingRegexp="${renderContext.site.path}/.*/ciArticle" />
<template:addCacheDependency flushOnPathMatchingRegexp="${renderContext.site.path}/.*/ciQuestion" />
<template:addCacheDependency flushOnPathMatchingRegexp="${renderContext.site.path}/.*/ciReply" />

<jcr:sql var="thematicQuery" limit="${empty nbOfElements ? 4 : nbOfElements.long}" sql="select * from [jnt:page] as object where object.isThematic = 'true' order by nbOfViews desc" />

<div id="thematiquesConsulteesHome">
	<h2 class="titrePage titreThematiquePage">les th&eacute;matiques les plus consult&eacute;es</h2>
 
	<c:set var="thematicsList" value="${thematicQuery.nodes}" />
	
	<c:forEach items="${thematicsList }" var="thematic" varStatus="status">
		
		<template:addCacheDependency node="${thematic}" />
		
		<c:set var="divCssClass">${status.index % 2 eq 0 ? ' fl ' : ' fr '}</c:set>
		<c:set var="breaker">${status.index % 2 eq 0 ? '' : '<div class=\"breaker\"></div>'}</c:set>
	
		<jcr:sql var="articlesQuery" sql="select * from [jnt:ciArticle] as object where isdescendantnode(object, ['${thematic.path}'])" />
		<c:set var="articlesSize">${articlesQuery.nodes.size }</c:set>
	
		<jcr:sql var="QuestionsQuery" sql="select * from [jnt:ciQuestion] as object where isdescendantnode(object, ['${thematic.path}'])" />
		<c:set var="questionsSize">${QuestionsQuery.nodes.size }</c:set>
	
		<jcr:sql var="RepliesQuery" sql="select * from [jnt:ciReply] as object where isdescendantnode(object, ['${thematic.path}'])" />
		<c:set var="repliesSize">${RepliesQuery.nodes.size }</c:set>
	
		<jcr:sql var="lastMsgQuery" limit="1"
			sql="select * from [jnt:ciQuestion] as object where isdescendantnode(object, ['${thematic.path}']) order by modifiedDate desc" />
		<c:forEach items="${lastMsgQuery.nodes}" var="node">
			<c:set var="lastMsgDate" value="${node.properties['modifiedDate'].date.time }" />
		</c:forEach>
	
		<c:set var="nbOfViews" value="${empty thematic.properties['nbOfViews'] ? 0 : thematic.properties['nbOfViews'].long}" />	
	
		<c:set var="nbOfMsg" value="${(repliesSize+questionsSize)}" />
	
		<fmt:message var="memMsg" key="thematics.messages">
			<fmt:param>
				<c:if test="${nbOfMsg gt 1}">s</c:if>
			</fmt:param>
		</fmt:message>
		<fmt:message var="viewsMsg" key="thematics.viewed">
			<fmt:param>
				<c:if test="${nbOfViews gt 1}">s</c:if>
			</fmt:param>
		</fmt:message>
		<fmt:message var="articleMsg" key="thematics.articles">
			<fmt:param>
				<c:if test="${articlesSize gt 1}">s</c:if>
			</fmt:param>
		</fmt:message>
	
		<c:set var="thematicTitle" value="${cia:pageShortTitle(thematic) }" />
		<c:set var="thematicTitle" value="${empty thematicTitle ? cia:pageTitle(thematic) : thematicTitle}" />
		<c:set var="thematicLogo" value="${cia:pageLogo(thematic) }" />
	
		<div class="boiteRubrique ${divCssClass }">
			<h3 class="titreBoiteRubrique">
				<a onclick="trackEvent(['Home page','Thematiques plus consultees','${functions:escapeJavaScript(thematicTitle)}']);"
					href="${thematic.url }">${thematicTitle }</a>
			</h3>
			<div class="contenuBoiteRubrique">
				<img width="57px" height="57px" class="fl" src="${thematicLogo}" />
				<div class="fl infosBoiteRubrique">
					<span class="nbArticlesBoiteRubrique">${articlesSize}&nbsp;${articleMsg}</span> <span class="nbVuesBoiteRubrique">${nbOfViews}&nbsp;${viewsMsg
						}</span> <span class="nbMessagesBoiteRubrique">${nbOfMsg}&nbsp;${memMsg }</span>
					<div class="breaker"></div>
					<c:if test="${nbOfMsg gt 0}">
						<span class="dernierMess"><fmt:message key="thematics.last.message" />&nbsp;${cia:formatDate(lastMsgDate, postDatePattern, true)}</span>
					</c:if>
				</div>
			</div>
		</div>
		${breaker }
	</c:forEach>
</div>

