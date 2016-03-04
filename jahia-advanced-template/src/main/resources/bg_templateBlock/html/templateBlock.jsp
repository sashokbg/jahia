<!DOCTYPE html>

<%@include file="../../incl/common.jspf"%>

<jcr:nodeProperty name="size" node="${currentNode}" var="size"/>
<jcr:nodeProperty name="mix:title" node="${currentNode}" var="title"/>

<c:if test="${renderContext.editMode}">
	${title}
	<c:set var="editClass" value="edit"></c:set>
</c:if>

<div class="col-md-${size.string} header-zone ${editClass}">
	<template:area path="${currentNode.name}" areaAsSubNode="true" />
</div>
