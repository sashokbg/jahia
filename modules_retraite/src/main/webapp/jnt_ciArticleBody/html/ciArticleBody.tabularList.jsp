<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty var="tabTitle1" name="tabTitle1" node="${currentNode}" />
<jcr:nodeProperty var="tabTitle2" name="tabTitle2" node="${currentNode}" />
<jcr:nodeProperty var="tabTitle3" name="tabTitle3" node="${currentNode}" />




<c:set var="tabCssClass" value="OngletInactif"/>

<c:if test="${(empty currentResource.moduleParams.displayTab and currentResource.moduleParams.stat) or (currentResource.moduleParams.displayTab eq currentNode.name)}">
    <c:set var="tabCssClass" value="OngletActif"/>
</c:if>

<a href="<c:url value='${url.mainResource}?displayTab=${currentNode.name}${currentResource.moduleParams.ps}'/>" class="ongletArticle ${tabCssClass }">
	<span class="conteneurLibelleOnglet">
		<c:if test="${not empty tabTitle1 }"><span>${tabTitle1.string}</span></c:if>
		<c:if test="${not empty tabTitle2 }"><span>${tabTitle2.string}</span></c:if>
		<c:if test="${not empty tabTitle3 }"><span>${tabTitle3.string}</span></c:if>
	</span>
</a>
