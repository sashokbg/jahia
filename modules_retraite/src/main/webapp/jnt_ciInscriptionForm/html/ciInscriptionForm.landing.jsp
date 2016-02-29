<%--
	@author : p.lakreb
	@Id : forumlaire d'inscription
	@description : En tant qu'Internaute, je souhaite m'inscrire
				   à la communauté / US-001b
 --%>


<%@ include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="redirectPage" var="redirectPage" />
<jcr:nodeProperty node="${currentNode}" name="cguPage" var="cguPage" />

<template:addResources  type="css" resources="jCrop/loader/fileuploader.css" />
<template:addResources  type="css" resources="jCrop/jquery.Jcrop.min.css" />
<template:addResources>
	<style>
		.frmInscription .conteneurPaysCP {
			width : auto;
		}
		.frmInscription .conteneurPays {
			width: 158px;
			float: left;
		}
		.frmInscription .conteneurCP {
			float: left;
			width: auto;
		}
		.frmInscription select{
			margin-bottom: 16px;
		}
		.loadingScreenImg{
			padding: 10px;
		}
		.frmInscription input[type='text'], .frmInscription input[type='password']{
			margin-right: 2px !important;
			padding-right: 0 !important;
			width: 276px !important;
		}
		.header{
			padding-bottom: 10px;
		}
		.btValideInscription {
			float: left;
			margin-top: 5px;
		}
		.messageErreur{
			margin-left: 10px;
		}
		.messageGenerique{
			margin-left: 0px;
		}
		.pseudoValideMessage{
			margin-left: 6px;
		}
	</style>
</template:addResources>
<%@ include file="ciInscriptionForm.landing.js.jspf" %>

<%--
	Intégration de la maquette de l'inscription :
 --%>

<div class="inscriptionFormLanding" style="padding: 10px 0 5px 0">
<h3 class="band">Inscription</h2>


<%-- Fancy box --%>
<div id="popLoadingWrapper" style="display: none">
	<div id="popLoading">
		<h3 class="band">INSCRIPTION EN COURS</h3>
		<div id="conteneurLogin3">
			<center>
				<div id="frmLogin3">
					<p>
						Votre page va se recharger automatiquement.<br />
						Merci de patienter, nous enregistrons votre inscription.
					</p>
				</div>
			</center>
			<center>
				<img class="loadingScreenImg" src="${currentModule.rootFolderPath}/img/jCrop/loader/loading.gif">
			</center>
		</div>
	</div>
</div>

<a id="lienPopLoding" href="#popLoading" style="display: none;"><!-- empty --></a>

				
<span class="messageErreur messageGenerique"><fmt:message key="registration.error.global" /></span>
<c:if test="${isMaintenance}">
	<div class="dataSection" style="font-weight: bold;">
		<img
			style="width: 50px; display: inline-block; float: left; margin: -7px 0 0 -12px;"
			src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
			alt="maintenance_warn" /> <span>Notre site est en cours de
			maintenance, nous vous invitons &agrave; r&eacuteit&eacute;rer votre
			inscription ult&eacute;rieurement. Veuillez nous excuser.</span>
	</div>
	<c:if test="${not renderContext.editMode}">
		<template:addResources insert="true">
			<script type="text/javascript">
				$(document).ready(function() {
					$('#ciInscriptionForm select, #ciInscriptionForm input').attr(
						'disabled', 'disabled');
				});
			</script>
		</template:addResources>
	</c:if>
</c:if>
<form id="ciInscriptionForm" name="ciInscriptionForm" action="${url.base}${currentNode.path}.ciRegister.do" method="post">
	<div class="frmInscription landing" style="width: auto; padding: 15px 2px 0 10px;">
		<input type="hidden" name="jcrRedirectTo" value="${url.base}${redirectPage.node.path}"/>
		<input type="hidden" name="jcrErrorRedirectTo" value="${renderContext.mainResource.node.path}"/>
		
		<span class="introFrm">Bonjour, merci de renseigner les informations suivantes :</span>
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.pseudo" /></span>
		<span class="complementLabelChamp"><fmt:message key="registration.label.pseudo.comment" /></span>
		<div class="clear"></div>
		<input name="pseudoname" id="pseudoname" type="text" value="" class="champFormInscription" maxlength="20" onKeyPress="return alphaNumOnly(event);"/>
		<span id="pseudonameValid" class="messageErreur" style="display:none;"></span>
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.mail" /></span>
		<span class="complementLabelChamp"><fmt:message key="registration.label.mail.comment" /></span>
		<div class="clear"></div>
		<input name="emailAddress" id="emailAddress" type="text" value="" class="champFormInscription" />
		<span id="emailAddressValid" class="messageErreur" style="display:none;"></span>
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.password" /></span>
		<span class="complementLabelChamp"><fmt:message key="registration.label.password.comment" /></span>
		<div class="clear"></div>
		<input name="password" id="password" type="password" value="" class="champFormInscription" maxlength="20"/>
		<div class="clear"></div>
		
		<div class="clear"></div>
		<span class="labelFormInscription"><fmt:message key="registration.label.type" /></span>
		<div class="clear"></div>
		<div class="choixJeSuis">
			<input class="unRadio" name="userType" type="radio" id="radFuturRetraite" value="radFuturRetraite"/><label for="radFuturRetraite"><fmt:message key="registration.label.future.retraite" /></label>
			<span class="complementLabelChamp"><fmt:message key="registration.label.future.retraite.complement.simple"/></span>
			<div class="clear" style="margin-bottom: 10px;"></div>
			<input class="unRadio" name="userType" type="radio" id="radRetJunior" value="radRetJunior"/><label for="radRetJunior"><fmt:message key="registration.label.junior.retraite" /></label>						
			<span class="complementLabelChamp"><fmt:message key="registration.label.junior.retraite.complement.simple"/></span>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
		<div class="conteneurChoixSexe">
			<fmt:message key="registration.label.gender" />&nbsp;
			<input class="unRadio" name="gender" checked="checked" type="radio" id="chkHomme" value="male"/><label for="chkHomme"><fmt:message key="registration.label.gender.male" /></label>
			<input class="unRadio" name="gender" type="radio" id="chkFemme" value="female"/><label for="chkFemme" ><fmt:message key="registration.label.gender.female" /></label>
			<div class="clear"></div>
		</div>
		
		<div class="clear"></div>
	</div>
	
	<div class="frmInscriptionSecondaire" style="width: auto">
		<div class="offreParMail" style="margin-bottom:15px; margin-left: 0px;">
			<input style="margin-right: 12px;" type="checkbox" class="uneCheckbox fl" name="acceptNewsletterGroup" value="true"/>
			<div><fmt:message key="registration.label.receive.news.line1" /></div>
			<div><fmt:message key="registration.label.receive.news.line2" /></div>						
		</div>
		<div class="clear"></div>
		<div class="offreParMail" style="margin-left: 0px;">
			<input style="margin-right: 12px;" type="checkbox" class="uneCheckbox fl" name="acceptNewsletter" value="true" />
			<div><fmt:message key="registration.label.receive.newsletter"/></div>
		</div>
		<div class="clear" ></div>
	</div>
	<div class="frmInscriptionSecondaire" style="width: auto">
		<div class="fl">
			<input id="cguAccepted" name="cguAccepted" type="checkbox" class="uneCheckbox fl" />
			<div class="cgu">
				<fmt:message key="registration.label.cg.read" /> <a target="_blank" title="Conditions G&eacute;n&eacute;rales d'utilisation" href="${cguPage.node.url}" class="lienCgu"><fmt:message key="registration.label.cg" /> </a>*						
			</div>
			<div class="clear"></div>
			<div id="cguMessage"></div>
		</div>
		<c:choose>
			<c:when test="${not isMaintenance}">
				<input type="button" class="btValideInscription" value=""
					id="submitButton" />
					<a href="#popLoading" class="hidden" />
			</c:when>
			<c:otherwise>
				<a class="maintenance btValideInscription" href="#popMaintenance"></a>
			</c:otherwise>
		</c:choose>
		<div class="clear"></div>
	</div>
</form>
</div>