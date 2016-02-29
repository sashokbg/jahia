<%--
	@author : Sylvain Pichard
	@created : 11 octobre 2012
	@Id : Footer Onglet
	@description : Un onglet du footer. Il contient un lien direct, ou une liste de colonnes 
 --%>

<%@ include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title"			var="title"/>
<jcr:nodeProperty node="${currentNode}" name="paddingLeft" 		var="paddingLeft"/>
<jcr:nodeProperty node="${currentNode}" name="paddingRight" 	var="paddingRight"/>
<jcr:nodeProperty node="${currentNode}" name="link" 			var="link"/>
<jcr:nodeProperty node="${currentNode}" name="target" 			var="target"/>


<%-- Recalcul des marges à gauche et à droite en fonction de ce qui a été renseigné ou pas --%>
<%-- 4 cas : --%>

<%-- 1) Si rien n'est renseigné : on laisse les valeurs par defaut --%>
<c:if test="${empty paddingLeft.long &&  empty paddingRight.long}">
	<c:set var="style" value="" />	
</c:if>

<%-- 2) Si les 2 sont renseignés : on reprend les 2 valeurs --%>
<c:if test="${not empty paddingLeft.long &&  not empty paddingRight.long}">
	<c:set var="style" value="padding-left:${paddingLeft.long}px; padding-right:${paddingRight.long}px;" />	
</c:if>

<%-- 3) Si seulement la marge de gauche est renseignée : on reprend sa valeur pour la marge de droite --%>
<c:if test="${not empty paddingLeft.long &&  empty paddingRight.long}">
	<c:set var="style" value="padding-left:${paddingLeft.long}px; padding-right:${paddingLeft.long}px;" />	
</c:if>

<%-- 4) Si seulement la marge de droite est renseignee : marge de gauche = defaut, et marge de droite = la valeur renseignée --%>
<c:if test="${empty paddingLeft.long &&  not empty paddingRight.long}">
	<c:set var="style" value="padding-right:${paddingRight.long}px;" />	
</c:if>

<li class="main">
	<c:choose>
		<%-- Cas d'un onglet simple --%>
		<c:when test="${not empty link}">
			<a class="lienFooterTop" style="${style}" href="${link.node.url}" title="${title.string}" target="${empty target.string ? '_self' : target.string}"	>
				${title.string}
			</a>			
		</c:when>	
	
		<%-- Cas d'un onglet avec des colonnes --%>
		<c:otherwise>
			<a class="lienFooterTop lienFooterFold" style="${style}">
				${title.string}
			</a>
			<ul class="detailFooter">
				<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:ciFooterColumn')}">
					<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciFooterColumn')}" var="subchild" varStatus="status">
						<template:module node="${subchild}" editable="true" />
					</c:forEach>
				</c:if>
				<c:if test="${renderContext.editMode}">
					<template:module path="*" nodeTypes="jnt:ciFooterColumn" editable="true" />
					<div class="breaker"><!-- ne pas effacer --></div>
				</c:if>				
			</ul>
		</c:otherwise>
	</c:choose>
</li>



