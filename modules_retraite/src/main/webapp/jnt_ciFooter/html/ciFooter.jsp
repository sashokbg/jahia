<%--
	@author : Sylvain Pichard
	@created : 11 octobre 2012
	@Id : Footer 
	@description : Footer qui contient des onglets
 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>
								
								
<div id="barreFooterTop">
	<ul id="mainLinks">
		<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:ciFooterOnglet')}">
			<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciFooterOnglet')}" var="subchild" varStatus="status">
				<template:module node="${subchild}" editable="true" />
			</c:forEach>
		</c:if>
		<c:if test="${renderContext.editMode}">
			<template:module path="*" nodeTypes="jnt:ciFooterOnglet" editable="true" />
		</c:if>		
	</ul>
	<div class="breaker"><!-- ne pas effacer--></div>
</div>
