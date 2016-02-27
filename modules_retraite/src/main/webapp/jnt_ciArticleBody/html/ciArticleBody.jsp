<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>
<%@ include file="../../common/ciSendFriendlyMessage.jspf"%>

<c:set var="currentPage" value="${renderContext.mainResource.node}" />

<jcr:nodeProperty var="body" name="body" node="${currentNode}" />
<jcr:nodeProperty var="marker" name="marker" node="${currentNode}" />
<c:set var="markerValue" value="${marker.string}" scope="request" />
<c:if test="${not empty body }">
	<div class="detailArticle">
		${cia:encodePostUrls(body.string, renderContext.response) }
		<script>
			function trackFBFromArticle(targetUrl, action) {
				trackEvent(['Facebook - Articles', action,
						'${functions:escapeJavaScript(currentPage.displayableName)}'], '_trackSocial');
				if(logFB)
					logFB('Reporting Facebook ' + action
						+ ' Event on "${functions:escapeJavaScript(currentPage.displayableName)}"');
			}
		</script>
		<div class="btFacebook">
			<div class="fb-like" data-send="false" data-layout="button_count"
				data-width="210" data-show-faces="true" data-font="verdana"></div>
			<div class="recommandeArticle">
				<a id="lienRecommandeArticleBody" class="lienRecommande" title="Recommander &agrave; un ami"
					href="#popRecommandation"
					onclick="return ciSendFriendMessage('${currentNode.path}','${renderContext.mainResource.node.url}','${type}','${currentNode.UUID}');">
					<fmt:message key="ciSendFriendlyMessageUsers.link.label" />
				</a>
			</div>
			<div class="breaker"></div>
		</div>

	</div>
</c:if>

