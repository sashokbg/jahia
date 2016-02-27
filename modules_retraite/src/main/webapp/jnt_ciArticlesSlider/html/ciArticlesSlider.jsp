<%--
	@author : el-aarko
	@created : 1 oct. 2012
	@Id : jnt_ciArticlesSlider
	@description : Carroussel articles

 --%>

<%@include file="../../common/declarations.jspf"%>

<%-- Declarations des proprietes du module --%>
<jcr:nodeProperty var="nbOfArticles" name="nbOfArticles" node="${currentNode}" />

<c:if test="${not renderContext.editMode}">
	<template:addResources type="javascript" resources="jquery.bxSlider.js"></template:addResources>
	<template:addResources type="css" resources="bx_styles/bx_styles.css"></template:addResources>
	<template:addResources>
		<script type="text/javascript">
			var transition = 2000;
			var startSliding = function () {
				$('#slider1').bxSlider({
					auto : true,
					autoControls : false,
					autoHover : true,
					pager : true,
					controls : false,
					/*startingSlide : (size - 1),      FGE mettre (size-1) comme valeur pour starting slide permet de faire defiler les slides a l'envers: le premier dernier deviens le premier, ne pas oublier d'inverser les liens de pagination dans le css*/
					startingSlide : 0,
					autoStart : true,
					speed : transition
				});
			}	
			jQuery(document).ready(function() {
				var size = $('.carouselElement').size();
				if (size > 0) {
					startSliding();
				}
			});
		</script>
	</template:addResources>
</c:if>
<c:set var="rubricPage" value="${cia:getRubricParent(renderContext.mainResource.node)}" />

<c:choose>
	<c:when test="${empty rubricPage }">
		Ceci n'est pas un page de visualisation d'une th&eacute;matique.
	</c:when>
	<c:otherwise>
		<c:if test="${renderContext.editMode}">
			<template:module path="*" nodeTypes="jnt:ciArticlesSliderItem" editable="true" />
			<div class="breaker"></div>
		</c:if>
		<div class="carousel">
			<ul id="slider1">
				<c:choose>
					<c:when test="${jcr:hasChildrenOfType(currentNode,'jnt:ciArticlesSliderItem')}">
						<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciArticlesSliderItem')}" var="child" varStatus="status">
							<template:module node="${child }" editable="true" />
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:if test="${renderContext.editMode}">
						Remont&eacute;e automatique des ${empty nbOfArticles ? 3 : nbOfArticles.long } derniers articles publi&eacute;s
						</c:if>
						<jcr:sql var="sliderQuery" limit="${empty nbOfArticles ? 3 : nbOfArticles.long }"
							sql="select * from [jnt:ciArticle] as object where isdescendantnode(object, ['${rubricPage.path}']) order by enforcedDate desc, [j:lastPublished] desc" />
						<template:addCacheDependency flushOnPathMatchingRegexp="${rubricPage.path}/.*/ciArticle" />
						<c:forEach items="${sliderQuery.nodes }" var="node" varStatus="status">
							<jcr:nodeProperty var="title" name="title" node="${node}" />
							<jcr:nodeProperty var="intro" name="intro" node="${node}" />
							<jcr:nodeProperty var="publishDate" name="j:lastPublished" node="${node}" />
							<jcr:nodeProperty var="sliderImage" name="sliderImage" node="${node}" />
							<jcr:nodeProperty node="${currentNode}" name="nbOfViews" var="nbOfViews" />
							
							<c:set var="articleNode" value="${node}" />
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
									<c:set var="titre_article" value="${cia:cutString(functions:removeHtmlTags(title.string), 50)}" />
									<h1 class="titreCarousel">${titre_article}</h1>
									<div class="chapeauCarousel">${cia:cutString(functions:removeHtmlTags(intro.string), 140)}</div>
									<a data-titre="${not empty titre_article ? fn:escapeXml(titre_article) : 'N/A'}" class="lireArticleCarousel article" class="lireArticleCarousel" href="${jcr:getParentOfType(node,'jnt:page').url}">Lire l'article</a>
								</div>
								<img class="carouselElementImage" src="${sliderImage.node.url}" alt="${title.string}"/>
								<div class="breaker"></div>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
	</c:otherwise>
</c:choose>

<%@include file="ga.jspf" %>