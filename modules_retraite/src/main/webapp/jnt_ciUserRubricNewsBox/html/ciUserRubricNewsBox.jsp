<%--
	@author : el-aarko
	@created : 19 sept. 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp
 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<c:if test="${connected }">
	
	<template:addCacheDependency uuid="${currentUser.identifier}" />
	
	<jcr:nodeProperty node="${currentNode}" name="boxTitle" var="boxTitle" />
	<jcr:nodeProperty node="${currentNode}" name="nbOfArticles" var="nbOfArticlesSelected" />
	<jcr:nodeProperty node="${currentNode}" name="nbOfQuestions" var="nbOfQuestionsSelected" />
	<jcr:node var="userNode" uuid="${currentUser.identifier }"/>
	
	<%@include file="../../common/ciSetUserInfo.jspf"%>
	
	<c:choose>
		<c:when test="${empty selectedRubrics || empty cia:userRubrics(userNode)[0]}">
			<div class="conteneurSujet actuThematiques actuThematiquesVides">
	        <h3 class="band">${boxTitle.boolean ? 'Non contribu&eacute;' : boxTitle.string }</h3>			
				<div class="listeSujets">
					<ul class="fondListeSujets">
						<li class="blocSujet question questionLast">
							<p class="questionBox">
								<fmt:message key="ciUserRubricNewsBox.empty.message.part1"/>
								<a href="${renderContext.site.properties['userProfilLink'].node.url }#thematicsAnchor" class="">profil</a>
								<fmt:message key="ciUserRubricNewsBox.empty.message.part2"/> 
							</p>	
						</li>							
					</ul>
				</div>			
			</div>
		</c:when>
		<c:otherwise>
			<%-- @TODO css refactoring --%>
			<div class="boxActuMesThematiques">						
				<h3 class="band" style="background-color: #019934;">${boxTitle.boolean ? 'Non contribu&eacute;' : boxTitle.string }</h3>
				<%-- les articles --%>
				<c:set var="sql" >select * from [jnt:ciArticle] as article where </c:set>
				<c:set var="isFirstItem" value="${true}" />
				<c:forTokens items="${selectedRubrics}" delims="," var="item" varStatus="status">
					<jcr:node var="itemNode" uuid="${item}"/>
					<c:if test="${not empty itemNode}">
						<c:if test="${not status.first and not isFirstItem}"><c:set var="sql">${sql } or </c:set></c:if>
						<c:set var="sql">${sql} isdescendantnode(article, ['${itemNode.path}']) </c:set>
						<c:set var="isFirstItem" value="${false}" />
					</c:if>
				</c:forTokens>
				<c:set var="sql">${sql} order by [jcr:created] desc</c:set>	
				<jcr:sql var="articlesQuery" limit="${empty nbOfArticlesSelected ? 2 : nbOfArticlesSelected.long}" sql="${sql }" />
				<c:forEach items="${articlesQuery.nodes}" var="article" varStatus="status">
					<template:module node="${article}" view="hidden.summary.sideBox" />
				</c:forEach>
						 	
				<%-- les questions --%>
				<c:set var="sql" >select * from [jnt:ciQuestion] as question where </c:set>
				<c:set var="isFirstItem" value="${true}" />
				<c:forTokens items="${selectedRubrics}" delims="," var="item" varStatus="status">
					<jcr:node var="itemNode" uuid="${item}"/>
					<c:if test="${not empty itemNode}">
						<c:if test="${not status.first and not isFirstItem}"><c:set var="sql">${sql } or </c:set></c:if>
						<c:set var="sql">${sql} isdescendantnode(question, ['${itemNode.path}']) </c:set>
						<c:set var="isFirstItem" value="${false}" />
					</c:if>
				</c:forTokens>
				<c:set var="sql">${sql} order by [jcr:created] desc</c:set>
				<jcr:sql var="questionsQuery" limit="${empty nbOfQuestionsSelected ? 2 : nbOfQuestionsSelected.long}" sql="${sql }" />
				<c:forEach items="${questionsQuery.nodes}" var="question" varStatus="status">
					<template:module node="${ question }" view="hidden.summary.sideBox" />
				</c:forEach>
				
				<%-- Rappel des thématiques --%>
				<div class="lienComplementaireBoite">
					<a id="seeAllNewsSidebox" href="${myActivityUrl}" title="Voir toute l'actualit&eacute; de mes th&eacute;matiques">Voir toute l'actualit&eacute; de mes th&eacute;matiques</a>
				</div>
						
			</div>
		</c:otherwise>
	</c:choose>
</c:if>