<%--
	@author : Sylvain Pichard
	@created : 11 octobre 2012
	@Id : Footer Colonne
	@description : Une colonne du footer. Elle contient une liste de liens 
 --%>

<%@ include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title" var="title"/>

<li class="colonneFooter">
	<ul>
		<li class="titreColonneFooter">${title.string}</li>
					
		<c:if test="${jcr:hasChildrenOfType(currentNode,'jnt:ciLink')}">
			<c:forEach items="${jcr:getChildrenOfType(currentNode,'jnt:ciLink')}" var="subchild" varStatus="status">
				<c:set var="itemStatus" scope="request" value="${status}" />
				<li>
					<template:module node="${subchild}" editable="true">
						<template:param name="linkClass" value="lienColonne"/>
					</template:module>
				</li>
			</c:forEach>
		</c:if>

		<c:if test="${renderContext.editMode}">
			<template:module path="*" nodeTypes="jnt:ciLink" editable="true" />			
		</c:if>
	</ul>
</li>
