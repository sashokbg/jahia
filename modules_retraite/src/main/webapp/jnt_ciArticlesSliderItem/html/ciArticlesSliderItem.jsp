<%--
	@author : el-aarko
	@created : 3 oct. 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf" %>

<%-- Veuillez commenter les blocs importants --%>
<jcr:nodeProperty var="pageArticle" name="pageArticle" node="${currentNode}" />

<template:addCacheDependency node="${pageArticle.node}" />

<jcr:sql var="tmpQuery" limit="1"
	sql="select * from [jnt:ciArticle] as object where isdescendantnode(object, ['${pageArticle.node.path}'])" />
<c:forEach items="${tmpQuery.nodes }" var="article">
	<jcr:nodeProperty var="title" name="title" node="${article}" />
	<jcr:nodeProperty var="intro" name="intro" node="${article}" />
	<jcr:nodeProperty var="publishDate" name="j:lastPublished" node="${article}" />
	<jcr:nodeProperty var="sliderImage" name="sliderImage" node="${article}" />
	<jcr:nodeProperty node="${article}" name="nbOfViews" var="nbOfViews" />
	
	<c:set var="articleNode" value="${article}" />
	<%@include file="../../common/articles/articleDateOverlay.jspf" %>
	
	<c:set var="nbOfViews">${(empty nbOfViews) ? 0 : nbOfViews.long }</c:set>
	
	<fmt:message var="viewsMsg" key="questions.viewed">
	<fmt:param>
		<c:if test="${nbOfViews gt 1}">s</c:if>
	</fmt:param>
	</fmt:message>
	
	<li class="carouselElement carouselRubrique">
		<div class="carouselElementGauche">
			<span class="dateCarousel"><fmt:formatDate value="${publishDate.date.time }" pattern="dd/MM/yyyy" /></span>
		<h1 class="titreCarousel">${cia:cutString(functions:removeHtmlTags(title.string), 50)}</h1>
		<div class="chapeauCarousel">${cia:cutString(functions:removeHtmlTags(intro.string), 140)}</div>
		<a data-href="" class="lireArticleCarousel" href="${jcr:getParentOfType(article,'jnt:page').url}">Lire l'article</a>
	</div>
	<img class="carouselElementImage" src="${sliderImage.node.url}" alt="${title.string}"/>
		<div class="breaker"></div>
	</li>
</c:forEach>