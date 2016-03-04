<!DOCTYPE html>

<%@include file="../../incl/common.jspf"%>

<jcr:nodeProperty name="size" node="${currentNode}" var="size"/>
<jcr:nodeProperty name="mix:title" node="${currentNode}" var="title"/>

<c:if test="${renderContext.editMode}">
	${title}
	<c:set var="editClass" value="edit"></c:set>
</c:if>

<div class="col-md-${size.string} header-zone ${editClass}">
<%-- 	<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:content')}"> --%>
<%-- 		<c:forEach --%>
<%--  			items="${jcr:getChildrenOfType(currentNode,'jnt:content')}" --%>
<%-- 			var="subchild" varStatus="status"> --%>
<%--  			<template:module node="${subchild}" editable="true" /> --%>
<%--  		</c:forEach> - --%>
<%-- 	</c:if>  --%>
	
	<template:area path="${currentNode.name}" areaAsSubNode="true" />
	
<%-- 	<c:if test="${renderContext.editMode}"> --%>
<%-- 		<template:module path="*" nodeTypes="acme:headerZone"></template:module> --%>
<%-- 	</c:if> --%>
</div>