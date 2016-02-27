<%--
	@author : lakreb
	@created : 14 sept. 2012
	@Id : idDeLaPage
	@description : permet de configurer un lien

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@include file="ga.jspf"%>

<%-- Veuillez commenter les blocs importants --%>

<jcr:nodeProperty name="boxTitle" node="${currentNode}" var="boxTitle" />
<jcr:nodeProperty name="boxTitle2" node="${currentNode}" var="boxTitle2" />
<jcr:nodeProperty name="boxTitle3" node="${currentNode}" var="boxTitle3" />
<jcr:nodeProperty name="link" node="${currentNode}" var="link" />

<c:set var="href" value="${empty link ? renderContext.site.properties['addQuestionLink'].node.url : link.node.url}"></c:set>

<c:set var="rubricPage"   value="${cia:getRubricParent(renderContext.mainResource.node).path}" />
<c:set var="thematicPage" value="${cia:getThematicParent(renderContext.mainResource.node).path}" />
<c:if test="${not empty rubricPage}">
	<c:set var="href" value="${href}?rubric=${rubricPage}"/>
</c:if>
<c:if test="${not empty thematicPage}">
	<c:set var="href" value="${href}&thematic=${thematicPage}"/>
</c:if>

<%-- @TODO css refactoring --%>
<div class="conteneurBlocUnique" style="float:none;display:block;">
	<h3 class="band brownL">
		<a class="wAccess" href="${isMaintenance ? '#popMaintenance' : href}" title="${functions:removeHtmlTags(boxTitle.string)}">
			${boxTitle.string}
			<br/>
			${boxTitle2.string}
			</br>
			${boxTitle3.string}
		</a>
	</h3>
</div>