<%--
	@author : el-aarko
	@created : 27 aoÃ»t 2012
	@Id : idDeLaPage
	@description : Affichage Article (vue liste)

 --%>

<%@include file="../../common/declarations.jspf" %>

<%-- Déclarations des propriétés du module --%>
<jcr:nodeProperty var="title" name="title" node="${currentNode}" />
<jcr:nodeProperty var="publishDate" name="j:lastPublished" node="${currentNode}" />
<jcr:nodeProperty var="intro" name="intro" node="${currentNode}" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />

<c:set var="articleNode" value="${currentNode}" />
<%@include file="../../common/articles/articleDateOverlay.jspf" %>

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long }</c:set>

<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<div class="ligneUnArticleThematique">
	<a href="${jcr:getParentOfType(currentNode,'jnt:page').url}">
		<span class="nbVuesDate">${nbOfViews }&nbsp;${viewsMsg} | <fmt:formatDate value="${publishDate.date.time }" pattern="dd/MM/yyyy" /></span>
		<span class="titreArticle">${title.string}</span>
		<span class="chapeauArticleThematique">${cia:cutString(functions:removeHtmlTags(intro.string), 140)}</span>
	</a>
</div>