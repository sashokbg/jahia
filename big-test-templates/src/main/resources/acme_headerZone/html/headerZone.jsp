<!DOCTYPE html>

<%@include file="../../incl/common.jspf"%>

<jcr:nodeProperty name="size" node="${currentNode}" var="size"/>
<jcr:nodeProperty name="mix:title" node="${currentNode}" var="title"/>

<c:if test="${renderContext.editMode}">
	${title} TITLE
</c:if>

<div class="col-md-${size.string} header-zone">
	<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:content')}">
		<c:forEach
			items="${jcr:getChildrenOfType(currentNode,'jnt:content')}"
			var="subchild" varStatus="status">
			<template:module node="${subchild}" editable="true" />
		</c:forEach>
	</c:if>
	<c:if test="${renderContext.editMode}">
		<template:module path="*"></template:module>
	</c:if>
</div>