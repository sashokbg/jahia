<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>


<jcr:nodeProperty var="title" name="title" node="${currentNode}" />
<jcr:nodeProperty var="body" name="body" node="${currentNode}" />
<jcr:nodeProperty var="marker" name="linkmarker" node="${currentNode}" />
<jcr:nodeProperty var="displayStyle" name="displayStyle" node="${currentNode}" />

<c:if test="${not empty marker}">
	<c:if test="${markerValue eq marker.string }">
		<div class="${displayStyle.string}">
			<c:if test="${not empty title }">
				<h3>${title.string}</h3>
			</c:if>
			<c:if test="${not empty body }">
				${body.string}
			</c:if>
		</div>
	</c:if>
</c:if>
<c:if test="${ empty marker}">
	<div class="${displayStyle.string}">
		<c:if test="${not empty title }">
			<h3 class="customStyleFCK_H3">${title.string}</h3>
		</c:if>
		<c:if test="${not empty body }">
			${body.string}
		</c:if>
		
	</div>
</c:if>
