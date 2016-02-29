<%--
	@author : el-aarko
	@created : 12 oct. 2012
	@Id : ciSiteMap
	@description : module du plan du site

 --%>

<%@include file="../../common/declarations.jspf" %>

<%-- <c:set var="homePath" value="${renderContext.site.home.path }" /> --%>
<%-- <jcr:sql var="rubricQuery" sql="select * from [jnt:page] as object where ISCHILDNODE(object, ['${homePath}']) and object.isRubric = 'true'" /> --%>
<%-- <c:set var="rubricQuerySize">${rubricQuery.nodes.size }</c:set> --%>

<div id="planSite">
	<h1>Plan du site</h1>
	<%-- Rubriques --%>
	<c:forEach items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}" var="rubric">
	<template:addCacheDependency node="${rubric}" />
		<c:if test="${rubric.properties['isRubric'].boolean}">
			<h2>${cia:pageTitle(rubric)}</h2>
			<div class="groupeLiens">
				<c:forEach items="${jcr:getChildrenOfType(rubric,'jnt:page')}" var="thematic" varStatus="status">
				<template:addCacheDependency node="${thematic}" />
					<c:if test="${thematic.properties['isThematic'].boolean}">
						<c:if test="${status.index % 3 eq 0}">
							${status.index % 3 eq 0 ? '<div class="ligneLiens">' : ''}
						</c:if>
							<div class="colonneLiens">
								<h3>${cia:pageTitle(thematic)}</h3>
								<ul>
									<c:forEach items="${jcr:getChildrenOfType(thematic,'jnt:page')}" var="articleforumPage">
									<template:addCacheDependency node="${articleforumPage}" />
										<li><a href="${articleforumPage.url}">${cia:pageTitle(articleforumPage)}</a></li>
									</c:forEach>
								</ul>
							</div>
						${status.index % 3 eq 2 or status.last ? '<div class="breaker"></div></div>' : ''}
					</c:if>
				</c:forEach>
			</div>
		</c:if>
	</c:forEach>
</div>
	