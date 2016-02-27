<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>
<jcr:nodeProperty var="title" name="jcr:title" node="${currentNode}" />

<c:set var="tabCssClass" value="OngletInactif" />

<c:if
	test="${(empty currentResource.moduleParams.displayTab and currentResource.moduleParams.stat) 
					or (currentResource.moduleParams.displayTab eq currentNode.name)}">
	<c:set var="tabCssClass" value="OngletActif" />
</c:if>
<a
	href="<c:url value='${url.mainResource}?displayTab=${currentNode.name}${currentResource.moduleParams.ps}'/>"
	class="ongletArticle ${tabCssClass}"> <span
	class="conteneurLibelleOnglet"> <span>${title.string}</span>
</span>
</a>
