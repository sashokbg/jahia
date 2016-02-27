<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<c:set var="displayTab" value="${not empty renderContext.mainResource.moduleParams.displayTab ? renderContext.mainResource.moduleParams.displayTab : param.displayTab}"/>
<c:set var="ps" value=""/>
<c:forEach items="${param}" var="p">
    <c:if test="${p.key ne 'displayTab'}">
        <c:set var="ps" value="${ps}&${p.key}=${p.value}" />
    </c:if>
</c:forEach>
<div class="barreOnglets">
	<c:forEach items="${currentNode.nodes}" var="subList" varStatus="status">
	    <c:if test="${status.first || displayTab eq subList.name}">
	        <c:set var="displayList" value="${subList}"/>
	    </c:if>
	    
	    <template:module node="${subList}" view="tabularList" editable="true" >
	        <template:param name="stat" value="${status.first}"/>
	        <template:param name="displayTab" value="${displayTab}"/>
	        <template:param name="ps" value="${ps}"/>
	    </template:module>
	</c:forEach>
	<div class="breaker"></div>
</div>
<c:if test="${not empty displayList}">
	<template:module path="${displayList.path}" />
</c:if>


<c:if test="${renderContext.editMode}">
    <template:module path="*"/>
</c:if>