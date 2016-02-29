<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<template:addCacheDependency uuid="${currentUser.identifier}" />

<%-- Google analytics --%>
<c:if test="${not renderContext.editMode}">
	<%@ include file="../../common/google_analytics.jspf"%>
	<c:if test="${isMaintenance}">
		<%@ include file="../../common/maintenance.jsp"%>
	</c:if>
</c:if>
<%-- Fancy Box non connecte (apparait si on essaye de repondre a une question) --%>
<%@ include file="../../common/ciNotConnected.jspf"%>
<%-- Fancy Box de mot de passe oublie --%>
<%@ include file="../../common/ciConnectionBox_forgottenPassword.jspf" %>
	
<%-- Veuillez commenter les blocs importants --%>
<jcr:nodeProperty node="${currentNode}" name="logo" 	var="logo" />
<jcr:nodeProperty node="${currentNode}" name="logoLink" var="logoLink" />
<jcr:nodeProperty node="${currentNode}" name="banner" 	var="banner" />

<c:if test="${renderContext.mainResource.node.properties['isThematic'].boolean}">
	<%@include file="../../common/increment_pageThematic_viewcount.jspf" %>
</c:if>

<!-- entete -->
<div class="headerTopContainer">
	<!-- ligne 1 de l'entete -->
	<div class="headerTop">
		<div class="proposePar">propos&eacute; par</div>
		<a class="lienPortail" title="Acc&eacute;der au portail du groupe AG2R LA MONDIALE" target="_blank" href="${logoLink.string}">
			<img class="imageHeadTop" src="${logo.node.url}" alt="AG2R La mondiale, le contraire de seul au monde"/>
		</a>	
		<c:if test="${renderContext.editMode}">
			<template:module path="*" nodeTypes="jnt:ciLink" editable="true" />
		</c:if>
			
		<div class="breaker"></div>	
	</div>
	
	<c:if test="${not empty banner}">
		<a title="Retour &agrave; l'accueil" href="${renderContext.site.home.url}" class="lienAccueil" >
			<img alt="Image de retour &agrave; l'accueil" src="${banner.node.url}" class="titleHeader"/>
		</a>
	</c:if>
		
	<div class="breaker"></div>
</div>

<hr />
