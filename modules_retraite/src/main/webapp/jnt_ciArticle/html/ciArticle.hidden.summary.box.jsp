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

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long }</c:set>

<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<div class="conteneurArticlePlusVu item">
	<div class="titreArticle"><a href="${jcr:getParentOfType(currentNode,'jnt:page').url}">${cia:cutString(title.string, 80)}</a></div>
	<span class="nbVuesArticles">${nbOfViews}&nbsp;${viewsMsg}</span>
</div>