<%--
	@author : el-aarko
	@created : 15 oct. 2012
	@Id : jnt_ciHomeSlider
	@description : Carrousel de la Home

 --%>

<%@include file="../../common/declarations.jspf"%>

<%-- fichier ga.jspf ajouté à la fin  --%>

<jcr:nodeProperty node="${currentNode}" name="player" var="player" />
<jcr:nodeProperty node="${currentNode}" name="nbOfElements"
	var="nbOfElements" />

<div class="carousel home">
	<ul id="carrousel">
		<c:if
			test="${jcr:hasChildrenOfType(currentNode,'jnt:ciHomeSliderItem')}">
			<c:set var="items"
				value="${jcr:getChildrenOfType(currentNode,'jnt:ciHomeSliderItem')}" />
			<c:if test="${not renderContext.editMode}">
				<template:addResources type="javascript"
					resources="jquery.bxSlider.js"></template:addResources>
				<template:addResources type="css"
					resources="bx_styles/bx_styles.css"></template:addResources>
				<template:addResources type="javascript" resources="shadowbox.js" />
				<template:addResources type="css" resources="shadowbox.css" />
				<template:addResources>
					<script type="text/javascript">
						// La vidéo	
						Shadowbox
								.init({
									flashVars : "buttoncolor=008559&amp;buttonovercolor=008559&amp;loadingcolor=008559&amp;sliderovercolor=008559",
									troubleElements : [ "select", "object",
											"embed" ]
								});
						Shadowbox.path = "${player.node.url}";
						Shadowbox.player = "";
						// [EVOL]#143 - Slider home : le slider ne defile pas lorqu'un seul slide est renseigne
						<c:if test="${not empty items[1]}">
							//Le carrousel
							var transition = 2000;
							var startSliding = function() {
								$('#carrousel').bxSlider({
									auto : true,
									autoControls : false,
									autoHover : true,
									pager : true,
									controls : false,
									/*startingSlide : (size - 1),      FGE mettre (size-1) comme valeur pour starting slide permet de faire defiler les slides a l'envers: le premier dernier deviens le premier, ne pas oublier d'inverser les liens de pagination dans le css*/
									startingSlide : 0,
									autoStart : true,
									speed : transition,
									randomStart : true
								});
							}
							jQuery(document).ready(function() {
								var size = $('.carouselElement').size();
								if (size > 0) {
									startSliding();
								}
							});
						</c:if>
					</script>
				</template:addResources>
			</c:if>
			<c:forEach items="${items }" var="subchild" varStatus="status"
				end="${nbOfElements.long eq 0 ? 99 : nbOfElements.long }">
				<template:module node="${subchild}" editable="true" />
			</c:forEach>
		</c:if>
	</ul>
</div>

<c:if test="${renderContext.editMode}">
	<template:module path="*" nodeTypes="jnt:ciHomeSliderItem"
		editable="true" />
</c:if>
<%@include file="ga.jspf"%>