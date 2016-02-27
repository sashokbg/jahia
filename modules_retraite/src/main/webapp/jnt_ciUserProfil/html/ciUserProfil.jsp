<%--
	@author : el-aarko
	@created : 27 aout 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>
<c:if test="${connected }">
<jcr:node var="currentUserNode" uuid="${currentUser.identifier}" />
<c:set var="userNode" value="${currentUserNode}" />

<template:addCacheDependency uuid="${currentUser.identifier}" />

<%@ include file="../../common/ciSetUserInfo.jspf"%>

<%-- Veuillez commenter les blocs importants --%>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />

<template:addResources type="css"
	resources="jCrop/loader/fileuploader.css" />
<template:addResources type="css" resources="jCrop/jquery.Jcrop.min.css" />

<%@include file="ciUserProfil.js.jspf"%>

<a
	href="${renderContext.site.properties['userActivitiesLink'].node.url}"
	id="ongletMonActivite" class="ongletActiviteInactif"></a>
<a href="#" id="ongletMonProfil" class="ongletProfilActif"></a>

<div class="breaker"></div>

<div id="userProfil" class="myProfile">
	<span class="messageErreur messageGenerique" style="display:none;"><fmt:message
			key="profile.error.global" />
	</span>
	<c:if test="${isMaintenance}">
		<div class="dataSection" style="font-weight: bold;">
		<img
			style="width: 55px; display: inline-block; float: left; margin: -7px 0 0 -12px;"
			src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
			alt="maintenance_warn" /> 
		<span >Le site est en cours de maintenance, nous vous invitons &agrave; vous reconnecter ult&eacute;rieurement pour modifier votre profil.
		Merci de nous excuser pour la g&ecirc;ne occasionn&eacute;e.</span>
		</div>
	</c:if>
	<form action="${url.base}${currentNode.path}.ciUserProfile.do"
		method="post" id="profilForm">
		<div id="userIdentity" class="dataSection">
			<c:if test="${not empty title }">
				<span id="monProfilTitle" class="userInfoLine titreSection"><fmt:message
						key="profile.info.member" /></span>
			</c:if>
				<c:if test="${cia:removeSessionEventFlag(renderContext, 'profileUpdated')}">
				<div style="margin-bottom: 15px">
					<span style="padding-left: 25px" class="correct"><fmt:message
							key="profile.updated.successful" /></span>
				</div>
				<div class="breaker"></div>
			</c:if>
			<div id="avatarSection" class="userInfoLine">
				<div class="conteneurAvatar" id="conteneurAvatar">
					<span class="avatarFrame">
						<img id="user_avatar" src="${avatarSrc51}" alt="mon avatar" width="51px" height="51px" />
					</span>
				</div>
				<div id="file-upload-container" class="fl">
					<span class="labelUpload"><fmt:message
							key="registration.label.avatar" /></span> <span
						class="complementLabelChamp">(Poids max :
						${cia:formattedSize(avatarMaxSize.long)})</span>
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
					
					<div id="file-uploader" class="avatarJpg">
						<!-- dnr -->
					</div>
					<noscript>
						<p>Si votre navigateur n'accepte pas le javascript, vous devez
							charger une image au bon format (ratio 1:1), elle sera
							retaill&eacute;e en 60x60.</p>
						<input name="croppedImage" type="file" id="photoToCrop"
							value="T&eacute;l&eacute;charger" />
						<div class="clear"></div>
					</noscript>
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
				<span class="messageCrop" style="display: none;">D&eacute;placer
					la zone en surbrillance pour recadrer votre photo aux bonnes
					dimensions</span>
				<div id="cropppingZone" style="display: none;">
					<!-- dnr -->
				</div>
			</div>
			<div class="clear"></div>

			<input id="pseudoname" name="pseudoname" onKeyPress="return alphaNumOnly(event)"
				class="champFormInscription " type="text" value="${pseudo}" /> <span
				class="infoChamp"><fmt:message key='profile.form.pseudo' /></span> <span
				id="pseudonameValid" class="messageErreur" style="display: none;"></span>

			<input id="firstname" name="firstname" class="champFormInscription "
				type="text" value="${firstname}" /> <span class="infoChamp"><fmt:message
					key='profile.form.firstname' /></span> <input name="lastname"
				id="lastname" class="champFormInscription " type="text"
				value="${lastname}" /> <span class="infoChamp"><fmt:message
					key='profile.form.lastname' /></span>
			<div class="clear"></div>
			<input id="birthDate" class="champFormInscription champCP"
				type="text" name="birthDate" value="${birthDate}" /> <span
				class="infoChamp"><fmt:message key='profile.form.birthdate' /></span>
			<div class="clear"></div>

			<div class="conteneurPaysCP" style="width: 100%">
				<div class="conteneurPays">
					<div class="clear"></div>
					<select id="country" name="country" class="listePays">
						<%@ include file="../../common/countries.jspf"%>
					</select> <input type="hidden" id="selectedCountry"
						value="${userNode.properties['country'].string}" />
					<div class="clear"></div>
				</div>

				<div class="conteneurZip">
					<div class="clear"></div>
					<input type="text" class="champFormInscription champCP"
						value="${zipCode}" name="zipCode" id="zipCode" /> <span
						class="infoChamp"><fmt:message key='profile.form.zipcode' /></span>
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>

			<input id="emailAddress" class="champFormInscription " type="text"
				name="emailAddress" value="${email}" /> <span id="emailAddressValid"
				class="messageErreur" style="display: none;"></span>

			<div class="clear"></div>
			<div id="modifPwd">
				<span id="libPwdOrigine" class="inputTitle"><fmt:message
						key='profile.form.password' /></span>
				<div class="clear"></div>
				<input class="champFormInscription fl" id="password" type="password"
					value="" disabled="disabled" />
					
				<c:choose>
					<c:when test="${not isMaintenance}">
						<input type="button" value="" class="btModifMDP" />
					</c:when>
					<c:otherwise>
						<a style="margin-top: 10px;" class="maintenance btModifMDP" href="#popMaintenance"></a>
					</c:otherwise>
				</c:choose>
						
				<span id="passwordValid"
					class="messageErreur" style="display: none;"></span>
				<div id="blocNewPwd">
					<div class="clear"></div>
					<span class="inputTitle"><fmt:message
							key='profile.form.newpassword' /></span>
					<div class="clear"></div>

					<input name="password" id="newPwd" class="champFormInscription"
						type="password" value="" /> <span id="newPasswordError"
						class="messageErreur" style="display: none;"></span>
					<div class="clear"></div>
					<span class="inputTitle"><fmt:message
							key='profile.form.confirmnewpassword' /></span>
					<div class="clear"></div>
					<input id="confNewPwd" class="champFormInscription fl"
						name="confirmPassword" type="password" value="" /> <input
						type="button" value="" class="btValideModifMDP fl"
						id="passwordChange" /> <input type="button" value=""
						class="btAnnuleModifMDP fl" />
				</div>
				<div class="clear"></div>
				<div style="margin-bottom: 10px; display: bloc;">
					<span id="passwordChangedOk"
						class="messageErreur pseudoValideMessage" style="display: none;"><fmt:message
							key="profil.change.password.success"></fmt:message></span> <span
						id="passwordChangedKo" class="messageErreur"
						style="display: none;"><fmt:message
							key="profil.change.password.error"></fmt:message></span>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<%-- situation --%>
		<c:set var="fcheck">checked="checked"</c:set>
		<c:set var="jcheck" value="" />
		<c:if
			test="${userNode.properties['userType'].string eq 'radRetJunior'}">
			<c:set var="fcheck" value="" />
			<c:set var="jcheck">checked="checked"</c:set>
		</c:if>
		<input type="hidden" id="checkedUserType"
			value="${userNode.properties['userType'].string}" />
		<div id="userStatus" class="dataSection">
			<div class="choixJeSuis">
				<span class="userInfoLine titreSection"><fmt:message
						key='profile.form.mysituation' /></span> <input class="unRadio"
					name="userType" type="radio"
					<c:out value="${fcheck}" escapeXml="false" /> id="radFuturRetraite"
					value="radFuturRetraite" /> <label for="radFuturRetraite"><fmt:message
						key="registration.label.future.retraite" /></label> <input
					class="unRadio" name="userType" type="radio"
					<c:out value="${jcheck}" escapeXml="false" /> id="radRetJunior"
					value="radRetJunior" /> <label for="radRetJunior"><fmt:message
						key="registration.label.junior.retraite" /></label>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
			<%-- futur retraite --%>
			<c:set var="scheck" value="" />
			<c:set var="rcheck" value="" />
			<c:set var="focheck" value="" />
			<c:if
				test="${fn:contains(userNode.properties['choixActivite'].string, 'chkNonSalarie')}">
				<c:set var="rcheck">checked="checked"</c:set>
			</c:if>
			<c:if
				test="${fn:contains(userNode.properties['choixActivite'].string, 'chkSalarie')}">
				<c:set var="scheck">checked="checked"</c:set>
			</c:if>
			<c:if
				test="${fn:contains(userNode.properties['choixActivite'].string, 'chkFonctionnaire')}">
				<c:set var="focheck">checked="checked"</c:set>
			</c:if>
			<span class="userInfoLine titreSection frNode"><fmt:message
					key="registration.label.future.retraite" /></span> <span
				class="userInfoLine titreSection rjNode"><fmt:message
					key="registration.label.junior.retraite" /></span> <span
				class="inputTitle"><fmt:message key='profile.form.ihavebeen' /></span>
			<span class="inputTitle complementLabelChamp"><fmt:message
					key='profile.form.ihavebeen.complement' /></span> <span class="inputTitle">:</span>
			<div class="clear"></div>
			<div class="choixActivite">
				<input class="uneCheckbox" name="choixActivite" type="checkbox"
					<c:out value="${scheck}" escapeXml="false" /> id="chkSalarie"
					value="chkSalarie" /> <label for="chkSalarie"><fmt:message
						key="registration.label.salarie" /></label> <input class="uneCheckbox"
					name="choixActivite" type="checkbox"
					<c:out value="${rcheck}" escapeXml="false" /> id="chkNonSalarie"
					value="chkNonSalarie" /> <label for="chkNonSalarie"><fmt:message
						key="registration.label.travailleur" /></label> <input
					class="uneCheckbox" name="choixActivite" type="checkbox"
					<c:out value="${focheck}" escapeXml="false" />
					id="chkFonctionnaire" value="chkFonctionnaire" /> <label
					for="chkFonctionnaire"><fmt:message
						key="registration.label.fonctionnaire" /></label>
				<div class="clear"></div>
			</div>
			<div class="conteneurDebutTravail">
				<div class="conteneurDateDepart">
					<span class="labelFormInscription frNode"><fmt:message
							key="registration.label.stop.working.at" /></span> <span
						class="labelFormInscription rjNode"><fmt:message
							key="registration.label.retraite.since" /></span>
					<div class="clear"></div>
					<input id="retreatYear" type="text" value="${retreatYear}"
						name="retreatYear" class="champFormInscription champDateDepart" />
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>

		<a name="thematicsAnchor"></a>
		<div id="userThematic" class="dataSection">
			<span class="userInfoLine titreSection"><fmt:message
					key='profile.form.favthemes' /></span> <span><fmt:message
					key='profile.form.favthemes.complement' /></span>
			<div class="conteneurPrincipalThematiques">
				<div class="fl">
					<span class="inputTitle"><fmt:message
							key='profile.form.theme' /></span>
					<div class="clear"></div>
					<select id="selectRubrics" onchange="selectRubric(this.value);">
						<option value="">
							<fmt:message key='profile.form.select' />
						</option>
						<c:forEach
							items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}"
							var="subchild" varStatus="step">
							<c:if test="${subchild.properties['isRubric'].boolean}">
								<option value="${subchild.identifier }">${subchild.properties['jcr:title'].string
									}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
				<div class="clear"></div>
			</div>
			<input id="selectedRubrics" name="selectedRubrics"
				value="${userNode.properties['selectedRubrics'].string }"
				type="hidden" />
			<div class="" id="thematiquesChoisies">
				<c:forTokens
					items="${userNode.properties['selectedRubrics'].string}" delims=","
					var="item">
					<jcr:node var="itemNode" uuid="${item }" />
					<div id="div_${item }" class="conteneurThematiqueChoisie fl removeTheme">
						<span class="libelleThematique fl">
							${itemNode.properties['jcr:title'].string}
						</span> <a onclick="removeThematic('${item}','${functions:escapeJavaScript(itemNode.properties['jcr:title'].string)}')"
							class="lienRemoveThematique fl"></a>
						<div class="clear"></div>
					</div>
				</c:forTokens>
			</div>
			<div class="clear"></div>
		</div>
		<div id="userExpertDomain" class="dataSection">
			<span class="userInfoLine titreSection"><fmt:message
					key='profile.form.imanexpert' /></span> <span><fmt:message
					key='profile.form.everyquestion' /></span>
			<div class="conteneurPrincipalThematiques">
				<div class="fl">
					<span class="inputTitle"><fmt:message
							key='profile.form.theme' /></span>
					<div class="clear"></div>
					<select id="selectTheme" onchange="loadThematics(this.value);">
						<option value="">Selectionner</option>
						<c:forEach
							items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}"
							var="subchild" varStatus="step">
							<c:if test="${subchild.properties['isRubric'].boolean}">
								<option value="${subchild.path }">${subchild.properties['jcr:title'].string
									}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
				<div class="fr">
					<div class="conteneurSousThematique">
						<span class="inputTitle"><fmt:message
								key='profile.form.subtheme' /></span>
						<div class="clear"></div>
						<select id="selectThematics" onchange="selectThematic(this.value)"></select>
					</div>
				</div>
				<div class="clear"></div>
			</div>
			<input id="selectedThematics" name="selectedThematics"
				value="${selectedThematics}" type="hidden" />
			<div class="" id="sousThematiquesChoisies">
				<c:forTokens items="${selectedThematics}" delims="," var="item">
					<jcr:node var="itemNode" uuid="${item }" />
					<div id="div_${item }" class="conteneurThematiqueChoisie fl">
						<span class="libelleThematique fl">
							${itemNode.properties['jcr:title'].string}
						</span> <a onclick="removeThematic('${item}','${functions:escapeJavaScript(itemNode.properties['jcr:title'].string)}')"
							class="lienRemoveThematique fl"></a>
						<div class="clear"></div>
					</div>
				</c:forTokens>
			</div>
			<div class="clear"></div>
		</div>
		<div id="userNotifications" class="dataSection last">
			<span class="userInfoLine titreSection">Mes abonnements</span>
			<div class="userNotification">
				<c:set var="newscheck" value="" />
				<c:if
					test="${not empty userNode.properties['acceptNotification'] && userNode.properties['acceptNotification'].boolean == 'true' }">
					<c:set var="notifcheck">checked="checked"</c:set>
				</c:if>
				<input id="acceptNotification" name="acceptNotification"
					type="checkbox" value="true"
					<c:out value='${notifcheck}' escapeXml="false" /> /> <label
					for="acceptNotification"><fmt:message
						key="profile.form.notifications" /></label>
			</div>
			<div class="userNotification">
				<c:set var="newscheck" value="" />
				<c:if
					test="${not empty userNode.properties['acceptNewsletter'] && userNode.properties['acceptNewsletter'].boolean == 'true' }">
					<c:set var="newscheck">checked="checked"</c:set>
				</c:if>
				<input id="acceptNewsletter" name="acceptNewsletter" value="true"
					type="checkbox" <c:out value='${newscheck}' escapeXml="false" /> />
				<label for="acceptNewsletter"><fmt:message
						key='profile.form.newsletter' /></label>
			</div>
			<div class="userNotification last">
				<c:set var="newsgroupcheck" value="" />
				<c:if
					test="${not empty userNode.properties['acceptNewsletterGroup'] && userNode.properties['acceptNewsletterGroup'].boolean == 'true' }">
					<c:set var="newsgroupcheck">checked="checked"</c:set>
				</c:if>
				<input id="acceptNewsletterGroup" name="acceptNewsletterGroup"
					value="true" type="checkbox"
					<c:out value='${newsgroupcheck}' escapeXml="false" /> /> <label
					for="acceptNewsletterGroup"><fmt:message
						key='profile.form.offers' /></label>
			</div>
			<div class="userUnsubscribe">
				<a class="lienDesabonner fl wAccess" href="${isMaintenance ? '#popMaintenance' : renderContext.site.properties['userUnsubscribeLink'].node.url}"><fmt:message
						key='profile.form.unsubscribe.link' /></a> 
						
						<c:choose>
							<c:when test="${not isMaintenance}">
								<input type="button" class="btValideModifProfil fr" value="" id="submitButton" />
							</c:when>
							<c:otherwise>
								<a class="maintenance btValideModifProfil fr" href="#popMaintenance"></a>
							</c:otherwise>
						</c:choose>
						
				<div class="clear"></div>
			</div>

			<input type="hidden" name="jcrRedirectTo"
				value="${url.base}${renderContext.mainResource.node.path}" />

			<div class="breaker">
				<!-- ne pas enlever -->
			</div>
		</div>

	</form>
</div>
</c:if>