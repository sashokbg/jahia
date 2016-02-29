<%--
	@author : el-aarko
	@created : 27 aoÃ»t 2012
	@Id : idDeLaPage
	@description : Affichage article (vue box)

 --%>

<%@include file="../../common/declarations.jspf" %>

<%-- Déclarations des propriétés du module --%>
<jcr:nodeProperty var="title" name="title" node="${currentNode}" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />
<jcr:nodeProperty var="publishDate" name="j:lastPublished" node="${currentNode}" />

<c:set var="articleNode" value="${currentNode}" />
<%@include file="../../common/articles/articleDateOverlay.jspf" %>

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long }</c:set>

<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<div class="boiteThematiques">
	<div class="ligneActuThematique">
		<span class="infoThematique">Article - <fmt:formatDate value="${publishDate.date.time }" pattern="dd/MM/yyyy" /></span>
		<strong>${cia:cutString(title.string, 105)}</strong>
		<a class="lireSuiteThematque" href="${jcr:getParentOfType(currentNode,'jnt:page').url}" title="Lire la suite">Lire la suite</a>
	</div>
</div>