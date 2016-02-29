<%--
	@author : p.lakreb
	@created : 02 Oct. 2012
	@Id : forumlaire de desinscription newsletter
	@description : US-...
 --%>


<%@ include file="../../common/declarations.jspf"%>


<template:addResources type="javascript" resources="ciUnsubscribeNewsletterForm/ciUnsubscribeNewsletterForm.js" />
	
<jcr:nodeProperty node="${currentNode}" name="title" var="title" />

<c:set var="badEmailUnsubcribe" value="${cia:removeSessionEventFlag(renderContext, 'badEmailUnsubcribe')}"/>
 
<h3 class="band">${title.string}</h3>

<c:if test="${isMaintenance and not renderContext.editMode}">
	<template:addResources insert="true">
		<script type="text/javascript">
			$(document).ready(function(){
				$('#desinscriptionNewsletter select, #desinscriptionNewsletter input,  #desinscriptionNewsletter textarea').attr('disabled', 'disabled');
			});
		</script>
	</template:addResources>
</c:if>

<form id="ciUnsubscribeNewsletterForm" name="ciUnsubscribeForm"	action="${url.base}${currentNode.path}.ciUnsubscribeNewsletter.do" method="post">
	<div style="height: auto;" id="desinscriptionNewsletter">
	<c:if test="${isMaintenance}">
			<div style="font-weight: bold; padding: 10px 0;">
				<img
					style="width: 55px; display: inline-block; float: left; margin: -7px 0 0 -12px;"
					src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
					alt="maintenance_warn" /> <span>Le site est en cours de
					maintenance, nous vous invitons &agrave; vous reconnecter
					ult&eacute;rieurement pour vous d&eacute;sincrire. Merci
					de nous excuser pour la g&ecirc;ne occasionn&eacute;e.</span>
					<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</c:if>
		<c:choose>
			<c:when	test="${cia:removeSessionEventFlag(renderContext, 'unsubscriptionConfirmed')}">
				<span id="messageAccueilNewsDesinsc">Votre d&eacute;sinscription &agrave; la newsletter sera prise en compte d'ici 24h, merci.</span>
				<a class="lienRetourAccueilNews" title="Retour &agrave; l'accueil" href="${renderContext.site.home.url}" ><!--  ne pas effacer--></a>
			</c:when>			
			<c:otherwise>
				<span id="messageAccueilNewsDesinsc">Pour vous d&eacute;sinscrire &agrave; la newsletter merci de saisir votre E-mail.</span>		
				<div class="frmDesinscNews">
					<input id="emailSubscriber" value="${empty param.emailUnsubscriber ? 'E-mail' : param.emailUnsubscriber}"
						name="emailUnsubscriber" type="text" class="champMailDesinscNews ${badEmailUnsubcribe ? ' messageErreur' : ''}" />
					<input type="hidden" name="jcrRedirectTo" value="${url.base}${renderContext.mainResource.node.path}" />
					
					<c:choose>
						<c:when test="${not isMaintenance}">
							<input type="submit" class="btDesinscNews" value="" />
						</c:when>
						<c:otherwise>
							<a class="maintenance btDesinscNews" href="#popMaintenance"></a>
						</c:otherwise>
					</c:choose>
					
					<div class="breaker"><!-- ne pas enlever --></div>
					<div id="messageGeneralDesinscription" class="messageId-KO" style="${badEmailUnsubcribe ? '' : 'display:none' }">Il semble que cet e-mail soit erron&eacute;.</div>
				</div>				
			</c:otherwise>				
		</c:choose>
		<div class="clear"></div>
	</div>
</form>
