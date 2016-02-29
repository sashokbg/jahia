<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : breadcrum
	@description : fil d'ariane

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf" %>

<c:if
	test="${renderContext.editMode && (renderContext.mainResource.node.properties['j:isHomePage'].boolean)}">
	Module Fil d'Ariane
</c:if>


<c:set var="currentPage" value="${renderContext.mainResource.node}" />
<c:set var="parentList" value="${cia:getParentPagesList(currentPage)}" />

<jcr:nodeProperty name="width" 		var="width" 		node="${currentNode}" />
<jcr:nodeProperty name="floatRight" var="floatRight" 	node="${currentNode}" />

<c:if test="${not empty width}">
	<c:set var="width" value="width: ${width.double}px;" />
</c:if>
<c:if test="${floatRight.boolean}">
	<c:set var="floatRight" value="float: right;" />
</c:if>
<c:if test="${not empty parentList}">
	<div id="filAriane" style="${width}">
		<ul style="${floatRight}">
			<c:forEach items="${parentList}" var="parent" varStatus="status">
				<c:set var="gaTitle">${functions:escapeJavaScript(cia:pageTitle(parent))}</c:set>
				<li>
					<a href="${parent.url}" title="${gaTitle }">
						${cia:pageTitle(parent)}
					</a>&nbsp;&gt;
				</li>
			</c:forEach>
			<c:set var="gaTitle">${functions:escapeJavaScript(cia:pageTitle(currentPage))}</c:set>
			<li><a class="arianeActive" href="${currentPage.url}"
				title="${gaTitle}">${cia:pageTitle(currentPage)}</a></li>
		</ul>
	</div>
</c:if>
