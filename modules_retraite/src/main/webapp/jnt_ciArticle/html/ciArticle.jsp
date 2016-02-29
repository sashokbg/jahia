<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>


<jcr:nodeProperty var="articleTitle" name="title" node="${currentNode}" />
<jcr:nodeProperty var="articleIntro" name="intro" node="${currentNode}" />
<jcr:nodeProperty var="articleIntroImage" name="introImage" node="${currentNode}" />

<%@include file="../../common/increment_viewcount.jspf"	%>
<%@include file="../../common/increment_pageThematic_viewcount.jspf" %>
<%@ include file="ga.jspf"%>

<div id="conteneurArticle">
	<div class="headerArticle">
		<c:if test="${not empty articleIntroImage}">
			<img src="${articleIntroImage.node.url}" alt="${articleIntroImage.node.displayableName}" />
		</c:if>
		<c:if test="${not empty articleTitle }">
			<h1 ${empty articleIntroImage ? 'style="margin-top:0"' : ''}>${articleTitle.string}</h1>
		</c:if>
		<c:if test="${not empty articleIntro }">
			<div class="chapeauArticle">
				<span>${articleIntro.string}</span>
			</div>
		</c:if>
	</div>
	 
	<c:if test="${jcr:hasChildrenOfType(currentNode,'jmix:ciArticleComponents')}">
		<c:forEach items="${jcr:getChildrenOfType(currentNode,'jmix:ciArticleComponents')}" var="subchild" varStatus="status">
			<template:module node="${subchild}" editable="true"/>
		</c:forEach>
	</c:if>
	
	<c:if test="${renderContext.editMode}">
		<template:module path="*" editable="true" view="hidden.summary.box" />
	</c:if>
	<div class="clear"></div>
</div>