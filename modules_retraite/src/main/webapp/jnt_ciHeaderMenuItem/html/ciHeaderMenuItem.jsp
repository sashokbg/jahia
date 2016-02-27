<%--
	@author : el-aarko
	@created : 18 sept. 2012
	@Id : jnt:ciHeaderMenuItem
	@description : affiche un element du menu header (image+lien)

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty node="${currentNode}" name="logo" var="logo" />
<jcr:nodeProperty node="${currentNode}" name="page" var="pageNode" />

<template:addCacheDependency node="${renderContext.mainResource.node}" />

<c:set var="isActif" value=""/>
<c:if test="${fn:startsWith(renderContext.mainResource.node.path, pageNode.node.path)}">
	<c:set var="isActif" value=" actif"/>
</c:if>

<c:if test="${not empty logo }">
	<li class="menuElement ${isActif}" style="background-image:url(${logo.node.url})">
		<p><a href="${pageNode.node.url}" title="${cia:pageTitle(pageNode.node)}"></a></p>
	</li>
</c:if>
