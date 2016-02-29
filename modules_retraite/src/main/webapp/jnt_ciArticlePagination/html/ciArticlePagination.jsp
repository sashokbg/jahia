<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<template:initPager totalSize="${currentNode.nodes.size}" id="${currentNode.identifier}" pageSize="1"/>

<c:forEach items="${currentNode.nodes}" var="subnode" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}" >
	<template:module node="${subnode}" editable="true"/>
</c:forEach>

<%@include file="../../common/ciPaginationArticle.jspf"%>

<template:removePager id="${currentNode.identifier}"/>

<c:if test="${renderContext.editMode}">
    <template:module path="*"/>
</c:if>