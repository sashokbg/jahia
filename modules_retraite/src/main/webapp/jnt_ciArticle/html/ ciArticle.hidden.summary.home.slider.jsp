<%--
	@author : el-aarko
	@created : 2 oct. 2012
	@Id : 
	@description : Affichage d'un article (vue slider)

 --%>

<%@include file="../../common/declarations.jspf" %>

<%-- Déclarations des propriétés du module --%>
<jcr:nodeProperty var="title" name="title" node="${currentNode}" />
<jcr:nodeProperty var="intro" name="intro" node="${currentNode}" />
<jcr:nodeProperty var="publishDate" name="j:lastPublished" node="${currentNode}" />
<jcr:nodeProperty var="sliderImage" name="sliderImage" node="${currentNode}" />
<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />

<c:set var="articleNode" value="${currentNode}" />
<%@include file="../../common/articles/articleDateOverlay.jspf" %>

<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long }</c:set>

<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
</fmt:message>

<li class="carouselElement carouselRubrique">
	<div class="carouselElementGauche">
		<span class="dateCarousel">article&nbsp;<fmt:formatDate value="${publishDate.date.time }" pattern="dd/MM/yyyy" /></span>
		<h1 class="titreCarousel">${cia:cutString(functions:removeHtmlTags(title.string), 50)}</h1>
		<div class="chapeauCarousel">${cia:cutString(functions:removeHtmlTags(intro.string), 140)}</div>
		<a class="lireArticleCarousel" href="${jcr:getParentOfType(currentNode,'jnt:page').url}">Lire l'article</a>
	</div>
	<img class="carouselElementImage" src="${sliderImage.node.url }" alt="${title.string}"/>
</li>