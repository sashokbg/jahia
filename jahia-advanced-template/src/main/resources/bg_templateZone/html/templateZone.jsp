<!DOCTYPE html>

<%@include file="../../incl/common.jspf" %>
<c:set var="totalColumns" value="0" />

<c:if test="${jcr:hasChildrenOfType(currentNode,'bg:templateBlock')}">
	<c:forEach items="${jcr:getChildrenOfType(currentNode,'bg:templateBlock')}" var="subchild" varStatus="status">
		<template:module node="${subchild}" editable="true" />
		<c:set var="totalColumns" value="${totalColumns + subchild.properties.size.string}" />
	</c:forEach>
</c:if>

<c:if test="${renderContext.editMode}">
	<c:if test="${totalColumns gt 12}">
		<span class="edit-error"> Warning total number of columns > 12 ! (${totalColumns})</span>
	</c:if>
	
	<template:module nodeTypes="bg:templateBlock" path="*"></template:module>
</c:if>