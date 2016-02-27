
<%--
	@author : Sylvain Pichard
	@created : 8 octobre 2012
	@Id : ReplySummary
	@description : Vue r&eacute;duite d'une réponse utilis&eacute;e pour les listes

 --%>

<%@include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="body" var="body" />
<%-- <jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" /> --%>
<jcr:nodeProperty node="${currentNode}" name="score" var="score" />

<c:set var="pair" value="${replyStatus.count % 2 eq 0 ? '' : ' pair' }"/>
<c:set var="last" value="${replyStatus.last ? ' last' : '' }"/>

<div class="ligneReponsePro ${pair} ${last}">
	<a href="${jcr:getParentOfType(currentNode,'jnt:page').url}" title="Acc&eacute;der &agrave; la discussion"><!-- ne pas effacer --></a>
	<span class="dateLigneReponsePro">${cia:formatDate(currentNode.properties['jcr:created'].date.time, replyDatePattern, true)}</span>
	<div class="uneReponse">${cia:cutString(functions:removeHtmlTags(body.string), 295)}</div>
</div>


