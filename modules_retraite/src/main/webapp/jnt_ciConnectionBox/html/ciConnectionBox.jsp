<%--
	@author : m.elaarkoubi
	@Id : box de connexion, login, logout
	@description : En tant qu'Internaute, je souhaite me connecter 
				   ou m'inscrire à la communauté / US-001a
 --%>

<%@ include file="../../common/declarations.jspf" %>

<template:addResources type="javascript" resources="jquery.fancybox-1.3.4.pack.js"/>

<c:if test="${not connected || renderContext.editMode}">
	<%-- Déclarations des propriétés du module --%>
	<jcr:nodeProperty var="blocTitle" name="blocTitle" node="${renderContext.site}" />
	<jcr:nodeProperty var="blocSecondTitle" name="blocSecondTitle" node="${renderContext.site}" />
	<jcr:nodeProperty var="leftTabTitle" name="leftTabTitle" node="${renderContext.site}" />
	<jcr:nodeProperty var="leftTabContent" name="leftTabContent" node="${renderContext.site}"/>
	<jcr:nodeProperty var="rightTabTitle" name="rightTabTitle" node="${renderContext.site}"/>
	<jcr:nodeProperty var="rightTabContent" name="rightTabContent" node="${renderContext.site}"/>
	<jcr:nodeProperty var="switchTabs" name="switchTabs" node="${renderContext.site}"/>
	<jcr:nodeProperty var="tabHeight" name="tabHeight" node="${renderContext.site}"/>

	<%@ include file="ciConnectionBox.js.jspf" %>
	
	<%-- @TODO css refactoring --%>
	<div id="blockLogin" style="display:block;margin-bottom:10px">
		<%-- Formulaire d'authentification --%>
		<h3 class="band">${blocTitle.string}</h3>
		<div id="conteneurLogin">
			<div id="frmLogin">
				<form id="ciLoginForm" action="${url.base}${currentNode.path}.ciLogin.do">
					<input id="cb_username" name="username" class="champSaisie emailBackground" type="text"  value="" />
					<input id="cb_password" name="password" class="champSaisie passwordBackground" type="password"  value=""/>
					<div>
						<a href="#${isMaintenance ? 'popMaintenance' : 'popMdpOublie'}" class="forgotPwd" title="Mot de passe oubli&eacute;">
							<fmt:message key="jnt_ciConnectionBox.forgettenPassword.label"/>
						</a>
						<br/><br/>
						<a href="${renderContext.site.properties['inscriptionPageLink'].node.url}" 
							class="inscriptionLink" title="M'inscrire" >
							<fmt:message key="jnt_ciConnectionBox.inscription.label"/>
						</a>
						<div class="clear"></div>
						<input id="logonLink" class="btConnection" type="button" title="Me Connecter" value="" />
						<div class="clear"></div>
					</div>
					<div id="loginFailError" class="messageId-KO hide"></div>	
				</form>
			</div>
		</div>
		<br/>
		<%-- Onglets --%>
		<h3 class="band">${blocSecondTitle.string}</h3>
		<div id="conteneurLogin2">					
			<h5 class="futurOn">${leftTabTitle.string}</h5>
			<h5 class="retJuniorsOff">${rightTabTitle.string}</h5>
			<div class="clear"></div>
			<div class="blocRaisonInscription" style="height: ${tabHeight.long}px;">
				<div class="texteRaisonInscription" id="detailFuturRetraites">${leftTabContent.string}</div>
				<div class="texteRaisonInscription" id="detailRetraitesJuniors">${rightTabContent.string}</div>
			</div>
			<a href="${renderContext.site.properties['inscriptionPageLink'].node.url}" target="_self" title="M'inscrire">
				<span id="btnInscription" class="inscription"></span>
			</a>
		</div>
	</div>
</c:if>