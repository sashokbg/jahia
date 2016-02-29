<%--
	@author : lakreb
	@created : 18 sept. 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>
<%@include file="../../common/declarations.jspf"%>

<c:set var="badEmail" value="${cia:removeSessionEventFlag(renderContext, 'badEmail')}"/>

<template:addResources type="javascript" resources="ciNewsletter/ciNewsletter.js" />
<%-- @TODO css refactoring --%>
<div class="inscriptionNewsLetter_V2 boxLeft">
	<c:choose>
		<c:when test="${cia:removeSessionEventFlag(renderContext, 'subscriptionConfirmed')}">
			<h3 class="band">Newsletter</h3>	
			<div class="conteneurBoxInscNewsletter">
				<span class="messagePrincipalNewsletter">Merci, nous avons bien enregistr&eacute; votre abonnement !</span>
				<span>Vous recevrez votre premi&egrave;re lettre d'information d'ici quelques jours ! </span><br>
				<span>Vous pourrez bien &eacute;videmment vous d&eacute;sinscrire &agrave; tout moment.</span>
			</div>
		</c:when>
		<c:otherwise>
			<h3 class="band">Newsletter</h3>			
			<div class="conteneurBoxInscNewsletter">
				<span class="messageIntroNewsletter">JE SOUHAITE &Ecirc;TRE INFORM&Eacute;(E)</span>
				<span class="messageIntroNewsletter2">DES DERNI&Egrave;RES ACTUALIT&Eacute;S DU SITE</span>
				<span>Je m'inscris &agrave; la lettre d'information :</span><br>
				<div class="breaker"></div>
				<form id="newsletterSubscription" name="ciSubscribeNewsletter" action="${url.base}${currentNode.path}.ciSubscribeNewsletter.do" method="post">
					<input name="emailSubscriber" type="text" value="Mon e-mail" class="champMailInscrNewsLetter ${badEmail ? 'champMailErreur' : ''}" />
					<input type="hidden" name="jcrRedirectTo" value="${url.base}${renderContext.mainResource.node.path}" />	

					<c:choose>
						<c:when test="${not isMaintenance}">
							<input type="submit" value="" class="btValideInscrNewsletter" />
						</c:when>
						<c:otherwise>
							<a style="margin-top: 10px;" class="maintenance btValideInscrNewsletter" href="#popMaintenance"></a>
						</c:otherwise>
					</c:choose>

				</form>
				<div class="breaker"></div>
				<div class='texte_newsletter'>
					<c:if test="${badEmail}">
						Il semble que cette adresse e-mail soit erron&eacute;e.
					</c:if>
				</div>
				<div class="breaker"></div>
			</div>			
		</c:otherwise>
	</c:choose>

</div>