<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf" %>

<template:addCacheDependency node="${renderContext.mainResource.node}" />

<div class="menu">									
	<!-- boutons menu -->
	<div class="barreMenu">
		<ul>
			<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:ciHeaderMenuItem')}">
				<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciHeaderMenuItem')}" var="subchild" varStatus="status">
					<template:module node="${subchild}" editable="true" />
				</c:forEach>
			</c:if>
			<c:if test="${renderContext.editMode}">
				<template:module path="*" nodeTypes="jnt:ciHeaderMenuItem" editable="true" />
			</c:if>
		</ul>
	</div>
</div>



