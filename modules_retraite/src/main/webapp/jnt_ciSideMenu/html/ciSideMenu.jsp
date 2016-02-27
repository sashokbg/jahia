<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : SideMenu
	@description : Menu vertical (colonne de gauche)

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>

<c:set var="currentPage" value="${renderContext.mainResource.node }" />
<c:set var="rubricPage" value="${cia:getRubricParent(currentPage)}" />

<c:if test="${not empty rubricPage }">

	<h2 id="titreMenuGauche">${cia:pageTitle(rubricPage) }</h2>
	<ul id="menuGauche">
		<c:forEach items="${jcr:getChildrenOfType(rubricPage,'jnt:page')}" var="child" varStatus="step">
			<c:if test="${child.properties['isThematic'].boolean}">
				<c:choose>
					<c:when test="${fn:contains(currentPage.path,child.path)}">
						<li><a class="lienPrincipal lienPrincipalActif" href="${child.url }">${cia:pageTitle(child)}</a>
							<ul class="sousMenuGauche">
								<c:set var="forumPage" value="" />
								<c:forEach items="${jcr:getChildrenOfType(child,'jnt:page')}" var="subchild" varStatus="step">
									<c:choose>
										<c:when test="${subchild.properties['isForum'].boolean}">
											<c:set var="forumPage" value="${subchild}" />
										</c:when>
										<c:otherwise>
											<c:set var="lienActif" value="${currentPage eq subchild ? 'lienActif' : ''}" />
											<li><a class="lienSecondaire ${lienActif}" href="${subchild.url }">${cia:pageTitle(subchild)}</a></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								<c:if test="${not empty forumPage }">
									<c:set var="lienActif" value="${fn:contains(currentPage.path,forumPage.path) ? 'lienActif' : ''}" />
									<li><a class="lienSecondaire lienVersForum ${lienActif }" href="${forumPage.url }">${cia:pageTitle(forumPage)}</a></li>
								</c:if>
							</ul></li>
					</c:when>
					<c:otherwise>
						<li><a class="lienPrincipal" href="${child.url }">${cia:pageTitle(child)}</a></li>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</ul>
</c:if>
