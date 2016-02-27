<%--
	@author : p.lakreb
	@created : 31 aout 2012
	@Id : forumlaire de desinscription
	@description : En tant qu'Internaute, je souhaite me desinscrire
				   de la communaute / US-202
 --%>


<%@ include file="../../common/declarations.jspf"%>
<%@ include file="ga.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />

<template:addResources type="javascript"
	resources="jquery.fancybox-1.3.4.pack.js" />

<c:if test="${isMaintenance and not renderContext.editMode}">
	<template:addResources insert="true">
		<script type="text/javascript">
			$(document).ready(function(){
				$('#userProfil select, #userProfil input, #userProfil textarea').attr('disabled', 'disabled');
			});
		</script>
	</template:addResources>
</c:if>

<%--
	Integration de la maquette de desinscription :
 --%>

<div id="userProfil">
	<h2 class="titrePage">${title.string}</h2>
	<span class="hide messageErreur messageGenerique"
		id="messageGeneralDesinscription">Veuillez choisir une raison
		dans la liste ci-dessous</span>
	<c:if test="${isMaintenance}">
		<div class="dataSection" style="font-weight: bold;">
		<img
			style="width: 55px; display: inline-block; float: left; margin: -7px 0 0 -12px;"
			src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
			alt="maintenance_warn" /> 
		<span >Le site est en cours de maintenance, nous vous invitons &agrave; vous reconnecter ult&eacute;rieurement pour vous d&eacute;sincrire.
		Merci de nous excuser pour la g&ecirc;ne occasionn&eacute;e.</span>
		</div>
	</c:if>
	<form id="ciUnsubscribeForm" name="ciUnsubscribeForm"
		action="${url.base}${currentNode.path}.ciUnsubscribe.do" method="post">
		<div class="frmInscription desinscription">
			<c:choose>
				<c:when test="${connected}">
					<p>Vous souhaitez vous d&eacute;sinscrire de ce site.</p>
					<p>Afin de nous aider &agrave; comprendre pourquoi et comment
						am&eacute;liorer ce service,</p>
					<p>pouvez-vous nous indiquer la raison ?</p>
					<select name="reasonSelected" class="lstRaisonDesinscrire">
						<option selected="selected">S&eacute;lectionnez</option>
						<option>Trop de bugs sur le site</option>
						<option>Je ne trouve pas les r&eacute;ponses &agrave; mes
							questions</option>
						<option>Je n'ai plus besoin d'information sur le passage
							&agrave; la retraite</option>
						<option>Je me suis inscrit uniquement pour poser une
							question</option>
						<option>Je n'ai plus assez de temps &agrave; consacrer
							&agrave; cette activit&eacute;</option>
						<option>Autre</option>
					</select>
					<div class="clear"></div>
					<textarea name="reasonDetails" class="raisonDesinscription"
						rows="7" cols="43">Pr&eacute;cisez votre r&eacute;ponse ici</textarea>
					<div id="disuasion">
						<p>Attention toute d&eacute;sinscription est
							d&eacute;finitive.</p>
						<p>Si vous souhaitez seulement vous d&eacute;sinscrire des
							alertes emails,</p>
						<p>
							il vous suffit de vous rendre sur la page <a class="bleu"
								href="${renderContext.site.properties['userProfilLink'].node.url}">Mon
								Profil.</a>
						</p>
					</div>
					<input type="hidden" name="jcrRedirectTo"
						value="${url.base}${renderContext.mainResource.node.path}" />
					<c:choose>
						<c:when test="${not isMaintenance}">
							<input type="submit" class="btDesinscrire fr" value="" />
						</c:when>
						<c:otherwise>
							<a style="margin-top: 10px;" class="maintenance btDesinscrire fr" href="#popMaintenance"></a>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:if test="${cia:removeSessionEventFlag(renderContext, 'unsubscribedOk')}">
						<p>Votre d&eacute;sinscription a bien &eacute;t&eacute; prise
							en compte, merci.</p>
					</c:if>
					<a href="${renderContext.site.home.url}" class="fl retourAccueil"></a>
				</c:otherwise>
			</c:choose>
			<div class="clear"></div>
		</div>
	</form>
</div>