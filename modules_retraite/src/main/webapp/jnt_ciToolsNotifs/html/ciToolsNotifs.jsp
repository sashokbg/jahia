<%--
	@author : lakreb
	@created : 17 oct. 2012
	@Id : US-1200
	@description : Remontee des derniers posts avec en priorite les nons-repondus

 --%>

<%@include file="../../common/declarations.jspf"%>

<div id="moderatorTools">
	<h3>Lancer le processus de notifications manuellement:</h3>
	<form action="${url.base}${currentNode.path}.manualNotificationRunAction.do" method="GET">
		<input type="hidden" name="jcrRedirectTo"  value="<c:url value='${url.base}${renderContext.mainResource.node.path}'/>" />
		Daily
		<input type="radio" name="job" value="daily" />
		<br />
		2Days
		<input type="radio" name="job" value="2days" />
		<br />
		4Days
		<input type="radio" name="job" value="4days" />
		<br />
		<input type="submit" value="Lancer"/>
	</form>
</div>