<%--
	@author : el-aarko
	@created : 19 oct. 2012
	@Id :ciErrorPage
	@description : page d'erreur (404)

 --%>

<%@include file="../../common/declarations.jspf" %>

<jcr:nodeProperty name="errorText" node="${currentNode }" var="errorText"/>
<%-- <jcr:nodeProperty name="errorNumber" node="${currentNode }" var="errorNumber"/> --%>

<template:addResources>
<script type="text/javascript">
	$(document).ready(function() {
		$("#valideRecherche404").click(function() {
			$("#champQuestion").val($("#champRecherche404").val());
			$("#searchSubmit").click();
		})
	});
</script>

</template:addResources>

<div id="page404">
	<div id="messageInformatif">
		<c:if test="${empty errorText }">
			<p>Nous sommes d&eacute;sol&eacute;:</p><p>Le lien que vous avez demand&eacute; n'existe pas</p><p>ou n'est plus disponible</p>
		</c:if>
		${errorText.string}
	</div>
	<span class="alternative404">vous pouvez n&eacute;anmoins:</span>
	<div id="rechercheLienAccueil404">
		<div id="recherche404">
			<div class="titreAlternative">Faire une recherche sur le site</div>
			<input id="champRecherche404" value="tapez votre recherche"/>
			<input type="button" value="" id="valideRecherche404" />
			<div class="breaker"></div>
		</div>
		<div id="retourAccueil404">
			<div class="titreAlternative">Retourner vers notre page d'accueil</div>
			<a href="${renderContext.site.home.url}"><input type="button" value="" id="retourAcceuil404" /></a>
		</div>
		<div class="breaker"></div>
	</div>
</div>