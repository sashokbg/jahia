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
	</style>
</template:addResources>
<%@ include file="ciInscriptionForm.js.jspf" %>

<%--
	Intégration de la maquette de l'inscription :
 --%>

<h2 class="titrePage">${title.string}</h2>


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
	<div class="frmInscription">
		<input type="hidden" name="jcrRedirectTo" value="${url.base}${redirectPage.node.path}"/>
		<input type="hidden" name="jcrErrorRedirectTo" value="${renderContext.mainResource.node.path}"/>
		
		<span class="introFrm">Bonjour, merci de renseigner les informations suivantes :</span>
		<div class="clear"></div>
					
		<span class="labelFormInscription"><fmt:message key="registration.label.firstname" /></span>
		<span class="champMandatory"><fmt:message key="registration.box.required" /></span>
		<div class="clear"></div>
		<input name="firstname" id="firstname" type="text" value="" class="champFormInscription" />
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.lastname" /></span>
		<div class="clear"></div>
		<input id="lastname" name="lastname" type="text" value="" class="champFormInscription" />
		<div class="clear"></div>
		
		<span class="sexe"><fmt:message key="registration.label.gender" /></span>
		
		<div class="clear"></div>
		
		<div class="conteneurChoixSexe">
			<input class="unRadio" name="gender" checked="checked" type="radio" id="chkHomme" value="male"/><label for="chkHomme"><fmt:message key="registration.label.gender.male" /></label>
			<input class="unRadio" name="gender" type="radio" id="chkFemme" value="female"/><label for="chkFemme" ><fmt:message key="registration.label.gender.female" /></label>
			<div class="clear"></div>
		</div>
		
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
		
		<span class="labelFormInscription"><fmt:message key="registration.label.mail.confirmation" /></span>
		<div class="clear"></div>
		<input name="confirmEmailAddress" id="confirmEmailAddress" type="text" value="" class="champFormInscription" />
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.password" /></span>
		<span class="complementLabelChamp"><fmt:message key="registration.label.password.comment" /></span>
		<div class="clear"></div>
		<input name="password" id="password" type="password" value="" class="champFormInscription" maxlength="20"/>
		<div class="clear"></div>
		
		<span class="labelFormInscription"><fmt:message key="registration.label.password.confirmation" /></span>
		<div class="clear"></div>
		<input name="confirmPassword" id="confirmPassword" type="password" value="" class="champFormInscription" />
		<div class="clear"></div>
		
		<div class="conteneurPaysCP">
			<div class="conteneurPays">
				<span class="labelFormInscription"><fmt:message key="registration.label.pays" /></span>
				<div class="clear"></div>
				<select id="country" name="country" class="listePays">								
					<%@ include file="../../common/countries.jspf"%>
				</select>
				<div class="clear"></div>
			</div>
			
			<div class="conteneurCP">
				<span class="labelFormInscription"><fmt:message key="registration.label.zipcode" /></span>
				<div class="clear"></div>
				<input id="zipCode" name="zipCode" type="text" value="" class="champFormInscription champCP"  />
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>
		
		<div class="clear"></div>
		<span class="labelFormInscription"><fmt:message key="registration.label.type" /></span>
		<div class="clear"></div>
		<div class="choixJeSuis">
			<input class="unRadio" name="userType" type="radio" id="radFuturRetraite" value="radFuturRetraite"/><label for="radFuturRetraite"><fmt:message key="registration.label.future.retraite" /></label>
			<span class="complementLabelChamp"><fmt:message key="registration.label.future.retraite.complement"/></span>
			<div class="clear" style="margin-bottom: 10px;"></div>
			<input class="unRadio" name="userType" type="radio" id="radRetJunior" value="radRetJunior"/><label for="radRetJunior"><fmt:message key="registration.label.junior.retraite" /></label>						
			<span class="complementLabelChamp"><fmt:message key="registration.label.junior.retraite.complement"/></span>
			<div class="clear"></div>
		</div>
		
		<div class="clear"></div>
		<div>
			<div id="" class="">
				<span class="labelFormInscription"><fmt:message key="registration.label.birthday" /> </span><span class="complementLabelChamp"><fmt:message key="registration.label.birthday.comment" /></span>
				<div class="clear"></div>
				<input id="birthDate" name="birthDate" type="text" class="champDateNaissance" value="JJ/MM/AAAA" />
				<div class="clear"></div>
				<span class="userInfoLine titreSection frNode"><fmt:message key="registration.label.future.retraite" /></span>
				<span class="userInfoLine titreSection rjNode"><fmt:message key="registration.label.junior.retraite" /></span>
				<span><fmt:message key="registration.label.im" /> </span><span class="complementLabelChamp"><fmt:message key="registration.label.im.choices" /></span><span>:</span>
				<div class="clear"></div>
				<div class="choixActivite">
					<input class="uneCheckbox" name="choixActivite" type="checkbox" id="chkSalarie"  value="chkSalarie"/><label for="chkSalarie"><fmt:message key="registration.label.salarie" /></label>
					<input class="uneCheckbox" name="choixActivite" type="checkbox" id="chkNonSalarie" value="chkNonSalarie"/><label for="chkNonSalarie"><fmt:message key="registration.label.travailleur" /></label>						
					<input class="uneCheckbox" name="choixActivite" type="checkbox" id="chkFonctionnaire" value="chkFonctionnaire"/><label for="chkFonctionnaire"><fmt:message key="registration.label.fonctionnaire" /></label>						
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
				<div class="conteneurDebutTravail">
					<div class="conteneurDateDepart">
						<span class="labelFormInscription frNode"><fmt:message key="registration.label.stop.working.at" /></span>
						<span class="labelFormInscription rjNode"><fmt:message key="registration.label.retraite.since" /></span>
						<div class="clear"></div>
						<input id="retreatYear" name="retreatYear" type="text" class="champFormInscription champDateDepart" value="AAAA" />
						<div class="clear"></div>
					</div>
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</div>
		</div>		
	</div>
	<div class="frmInscriptionSecondaire">
		
		<div class="conteneurAvatarFichier">
		
			<div class="conteneurAvatar">
				<span class="avatarFrame">
					<img id="user_avatar" src="/modules/modules_retraite/img/ciInscription/userMale.png" alt="mon avatar" width="52px" height="52px"/>
				</span>				
			</div>
			<div id="file-upload-container" class="fl">
				<span class="labelUpload"><fmt:message key="registration.label.avatar" /></span>
				<span class="complementLabelChamp">(Poids max : ${cia:formattedSize(avatarMaxSize.long)})</span>
				<div class="clear"></div>		
				<c:choose>
					<c:when test="${not isMaintenance}">
						<div id="file-uploader-button" class="btTelecharger"></div>
					</c:when>
					<c:otherwise>
						<div id="file-uploader-button" class="" style="display: none;"></div>
						<a style="margin-top: 10px;" class="maintenance btTelecharger" href="#popMaintenance"></a>
					</c:otherwise>
				</c:choose>
				<div id="file-uploader" class="avatarJpg"><!-- dnr --></div>
				<noscript>
					<p>Si votre navigateur n'accepte pas le javascript, vous devez charger une image au bon format (ratio 1:1), elle sera retaill&eacute;e en 60x60.</p>
					<input name="croppedImage" type="file" id="photoToCrop" value="Télécharger" />
					<div class="clear"></div>
				</noscript>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>						
			<span class="messageCrop" style="display: none;">Déplacer la zone en surbrillance pour recadrer votre photo aux bonnes dimensions</span>
			<div id="cropppingZone" style="display: none;"><!-- dnr --></div>
		</div>
		
	</div>
	
	<div class="frmInscriptionSecondaire">
		<input type="checkbox" class="uneCheckbox fl" name="acceptNewsletterGroup" value="true"/>
		<div class="offreParMail" style="margin-bottom:15px">
			<div><fmt:message key="registration.label.receive.news.line1" /></div>
			<div><fmt:message key="registration.label.receive.news.line2" /></div>						
		</div>
		<div class="clear"></div>
		<input type="checkbox" class="uneCheckbox fl" name="acceptNewsletter" value="true" />
		<div class="offreParMail" >
			<div><fmt:message key="registration.label.receive.newsletter"/></div>
		</div>
		<div class="clear" ></div>
	</div>
	<div class="frmInscriptionSecondaire">
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