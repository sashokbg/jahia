<%--
	@author : el-aarko
	@created : 15 oct. 2012
	@Id : jnt_ciHomeSliderItem
	@description : Element du carrousel de la home

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="pageArticle" var="pageArticle" />
<jcr:nodeProperty node="${currentNode}" name="pageThematic" var="pageThematic" />
<jcr:nodeProperty node="${currentNode}" name="videoLink" var="videoLink" />
<jcr:nodeProperty node="${currentNode}" name="videoTitle" var="videoTitle" />
<jcr:nodeProperty node="${currentNode}" name="text" var="text" />
<jcr:nodeProperty node="${currentNode}" name="image" var="image" />
<jcr:nodeProperty node="${currentNode}" name="link" var="link" />

<c:choose>
	<c:when test="${not empty videoLink}">
		<c:set var="titre_video" value="${empty videoTitle ? 'Vid&eacute;o de pr&eacute;sentation' : videoTitle.string}" />
		<li class="carouselElement carouselRubrique">
			<div class="carouselElementGauche">
				${text.string}
			</div>
			<a class="lienVideo" data-titre="${fn:escapeXml(titre_video)}" href="${videoLink.string}" rel="shadowbox;height=530;width=720;handleOversize=none;" title="${titre_video}">
				<img height="232px" width="319px" class="carouselElementImage" src="${image.node.url}" alt="${videoTitle.string }"/>
			</a>
		</li>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${not empty  pageThematic}">
				<c:set var="rubricPage" value="${cia:getRubricParent(pageThematic.node)}" />
				<li class="carouselElement carouselRubrique">
					<div class="carouselElementGauche">
						<span class="dateCarousel">${cia:pageShortTitle(rubricPage) }</span>
						<h1 class="titreCarousel">${cia:pageTitle(pageThematic.node) }</h1>
						<p class="chapeauCarousel">${cia:cutString(pageThematic.node.properties['description'].string, 140)}</p>
						<a data-titre="${fn:escapeXml(cia:pageTitle(pageThematic.node))}" class="lireArticleCarousel thematic" href="${pageThematic.node.url}">
							Lire la suite
						</a>
					</div>
					<img height="232px" width="319px" class="carouselElementImage" src="${pageThematic.node.properties['sliderImage'].node.url}" alt="${cia:pageTitle(pageThematic.node) }"/>
				</li>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty pageArticle}">
					<jcr:sql var="tmpQuery" limit="1"
						sql="select * from [jnt:ciArticle] as object where isdescendantnode(object, ['${pageArticle.node.path}'])" />
					<c:forEach items="${tmpQuery.nodes }" var="article">
						<template:addCacheDependency node="${article}" />
						<li class="carouselElement carouselRubrique">
							<div class="carouselElementGauche">
								<c:set var="titre_article" value="${cia:cutString(functions:removeHtmlTags(article.properties['title'].string), 50)}" />
								<c:set var="publishDate" value="${article.properties['j:lastPublished']}" />
								
								<c:set var="articleNode" value="${article}" />
								<%@include file="../../common/articles/articleDateOverlay.jspf" %>
								
								<span class="dateCarousel">article&nbsp;<fmt:formatDate value="${publishDate.date.time}" pattern="dd/MM/yyyy" /></span>
								<h1 class="titreCarousel">${titre_article}</h1>
								<div class="chapeauCarousel">${cia:cutString(functions:removeHtmlTags(article.properties['intro'].string), 140)}</div>
								<a data-titre="${not empty titre_article ? fn:escapeXml(titre_article) : 'N/A'}" class="lireArticleCarousel article" href="${jcr:getParentOfType(article,'jnt:page').url}">
									Lire l'article
								</a>
							</div>
							<img height="232px" width="319px" class="carouselElementImage" src="${article.properties['homeSliderImage'].node.url }" alt="${title.string}"/>
						</li>
					</c:forEach>
				</c:if>
				<c:if test="${empty videoLink and empty pageThematic and empty pageArticle}">
					<li class="carouselElement carouselRubrique">
						<div class="carouselElementGauche">
							${text.string }
						</div>
						<a data-titre="${not empty link ? fn:escapeXml(link.node.displayableName) : '#'}" href="${not empty link ? link.node.url : '#'}">
							<img height="232px" width="319px" class="carouselElementImage" src="${image.node.url}" alt="${videoTitle.string }"/>
						</a>
					</li>
				</c:if>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>