<%--
	@author : el-aarko
	@created : 31 juil. 2012
	@Id : VisualiserQuestion
	@description : permet de visualiser le detail d'une question
					 / US-100

 --%>


<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>

<template:addCacheDependency flushOnPathMatchingRegexp="${currentNode.path}/.*/ciReply" />

<template:addResources type="javascript" resources="jquery.min.js,jquery.jeditable.mini.js"/>
<template:addResources type="javascript" resources="ciQuestion/ciQuestion.js"/>

<%@include file="../../common/ciFlagmessage.jspf" %>
<%@include file="../../common/ciSendFriendlyMessage.jspf" %>
<%@include file="../../common/increment_viewcount.jspf"	%>

<c:if test="${jcr:hasPermission(currentNode, 'moveQuestion')}">
	<%@include file="ciQuestion_move.jspf"	%>
</c:if>
<%-- Veuillez commenter les blocs importants --%>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="body" var="body" />
<jcr:nodeProperty node="${currentNode}" name="user" var="userInfo" />
<jcr:nodeProperty node="${currentNode}" name="pageTitle" var="pageTitle" />

<c:set var="postType">
	<fmt:message key="user.post.question" />
</c:set>
<c:set var="currentQuestionDate" value="${cia:formatDate(currentNode.properties['jcr:created'].date.time, postDatePattern, true)}" />
<%-- Si on n'est pas connecte --%>
<c:set var="onclick">
	<c:choose>
		<c:when test="${not isMaintenance}">
			openConnectionBox();
		</c:when>
		<c:otherwise>
			$('.wAccess.btEnvoyer').click();
		</c:otherwise>
	</c:choose>
</c:set> 

<c:set var="userNode" value="${userInfo.node}" />
<%@include file="../../common/ciSetUserInfo.jspf"%>

<template:addCacheDependency node="${userNode}" />

<%--${jcr:getParentOfType(currentNode,'jmix:theme').properties['j:theme'].node}--%>
<div class="questionContainer ${userTypeCss}">
	<div class="colonneAvatar">
		<a class="blocQuestionAvatar" title="Acc&eacute;der au profil de ${pseudo}" href="${publicProfileUrl}">
			<img src="${avatarSrc40}" width="40px" height="40px" alt="${fn:escapeXml(userInfo.node.properties['j:nodename'].string)}" /></a>
		<c:if test="${cia:isMember(userNode)}" >
			<div>
				<template:addResources type="css" resources="ciProfil/ratingStarsQuestionReponse.css"/>
				<%@include file="../../common/ratingStars.jspf"%>
			</div>
		</c:if>
	</div>
	<div class="colonneContenu">
		<div class="questionEntete">
			<%--  --%>
			<fmt:message key="user.postIntroduction">
				<fmt:param>${postType}</fmt:param>
				<fmt:param>${pseudo}</fmt:param>
				<fmt:param>${userTypeLabel}</fmt:param>
				<fmt:param>${publicProfileUrl}</fmt:param>
			</fmt:message>
		</div>

		<c:set var="loggedUserPseudo" value="${currentUser.userProperties.properties.pseudoname}"/>
		<c:set var="canProfessionalEditOwnPost"
			value="${cia:isInProfessionalGroup(renderContext,displayedUser) && loggedUserPseudo eq pseudo}"/>
		<c:set var="canEditPost" value="${jcr:hasPermission(currentNode, 'editReply') && (
			(cia:isModerator(renderContext,currentUser)) || (canProfessionalEditOwnPost eq 'true')) }"/>

		<c:if test="${canEditPost}">
			<c:set var="ondblclick">ondblclick="editPost('edit_title${currentNode.identifier}'); return false;"</c:set>
		</c:if>
		<h1 id="content-to-edit_title${currentNode.identifier}" class="questionTitre" <c:out value="${ondblclick}" escapeXml="false"/>>${title.string}</h1>
		<c:if test="${canEditPost}">
		    <div class="editablePost" <c:out value="jcr:id='title' jcr:url='${url.base}${currentNode.path}.ciSeoUpdateAction.do'" escapeXml="false"/>
			    id="edit_title${currentNode.identifier}" >${fn:escapeXml(title.string)}</div>
		</c:if>

		<c:if test="${jcr:hasPermission(currentNode, 'moveQuestion')}">
			<div class="hidden" style="display: none">
				<fmt:message key="question.title"/>
				<div class="questionDetail postBody" id="content-to-rename${currentNode.UUID}"></div>
			</div>
		</c:if>
		<div class="questionDetail postBody" id="content-to-edit${currentNode.UUID}">${fn:replace(body.string, newLineChar, "<br />")}</div>
		<div class="questionDetail" id="content-to-blockquote${currentNode.UUID}" style="display: none;">${cia:encodePostUrls(body, renderContext.response) }</div>
		<%-- Modification --%>
		<c:if test="${canEditPost}">
			<c:set var="qUrl">
				<c:url value='${url.base}${currentNode.path}'/>
			</c:set>
		    <div class="content editablePost" <c:out value="jcr:id='pageTitle' jcr:url='${qUrl}' " escapeXml="false"/>
		         id="rename${currentNode.identifier}" >${pageTitle.string}</div>
		    <div class="content editablePost" <c:out value="jcr:id='body' jcr:url='${qUrl}' " escapeXml="false"/>
		         id="edit${currentNode.identifier}" >${cia:encodePostUrls(body, renderContext.response)}</div>
		</c:if>
		<div class="questionFooter ">
			<!-- Signaler un abus -->
			<a class="signalerAbus" title="Signaler un abus"  href="#popSignaler" 
				onclick="return ciFlagMessage('${currentNode.path}','${jcr:getParentOfType(currentNode,'jnt:page').url}');">
				<fmt:message key="ciFlagMessage.link.label"/>
			</a>	
				
			<div class="questionFooterRight">
				<span>
					<c:if test="${canEditPost}">
						<a title="<fmt:message key='action.rename.help'/>" onclick="editPost('rename${currentNode.UUID}'); return false;"><fmt:message key="action.rename"/></a>
					</c:if>
					<c:if test="${jcr:hasPermission(currentNode, 'moveQuestion')}">
					 | <a title="D&eacute;placer" onclick="openMoveQuestionPopin();"><fmt:message key="action.move"/></a>
					</c:if>
					<c:if test="${canEditPost}">
					 | <a title="Modifier"	href="#edit" onclick="editPost('edit${currentNode.UUID}'); return false;"><fmt:message key="action.edit"/></a>
					</c:if>
					<%-- Remontee --%>
					<c:if test="${jcr:hasPermission(currentNode, 'setPickableNode') && renderContext.liveMode}">
					| 
					<a href="${url.base}${currentNode.path}.setPickableNode.do?nodePath=${currentNode.path}">
						${currentNode.properties['isNotPickable'].boolean ? 'Remonter' : 'Ne pas remonter'}
					</a>
					</c:if> 
					<c:if test="${jcr:hasPermission(currentNode, 'deleteQuestion')}">
						<fmt:message var="confirmMsg" key="question.delete.confirm"/>
					 | <a title="Supprimer" href="#delete" onclick='if (window.confirm("${functions:escapeJavaScript(confirmMsg)}"))
		                        { document.getElementById("delete-question${currentNode.UUID}").submit(); } return false;'><fmt:message key="action.delete"/></a>	| 
					</c:if>
					${currentQuestionDate}
				</span> 

				<c:if test="${connected && not isMaintenance}"><c:set var="onclick" >reply();</c:set></c:if>
				<span title="R&eacute;pondre" class="btRepondre fr wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="${onclick}"><!-- ne pas enlever --></span>

			</div>
			<div class="bulleRepondre">
				<c:if test="${connected && not isMaintenance}"><c:set var="onclick">reply();</c:set></c:if>
				<a title="R&eacute;pondre" onclick="${onclick}">R&eacute;pondre</a> |
				<c:if test="${connected && not isMaintenance}"><c:set
						var="onclick">replyWithBlockote('${pseudo}', 'content-to-blockquote${currentNode.identifier}')</c:set></c:if>
				<a title="R&eacute;pondre en citant" onclick="${onclick}">R&eacute;pondre en citant</a>
			</div>
		</div>
	</div>
	<%-- Supprimer --%>
	<c:if test="${jcr:hasPermission(currentNode, 'deleteQuestion')}">
	    <template:tokenizedForm>
	    	<c:url var="deleteQuestionUrl" value="${url.base}${currentNode.path}.deleteQuestion.do"/>
	        <form action="${deleteQuestionUrl}" method="post" id="delete-question${currentNode.UUID}">
	        	<c:url var="redirectTo" value="${url.base}${jcr:getParentOfType(jcr:getParentOfType(currentNode,'jnt:page'),'jnt:page').path}" />
	            <input type="hidden" name="jcrRedirectTo" value="${redirectTo}"/>
                <%-- Define the output format for the newly created node by default html or by redirectTo--%>
	        </form>
	    </template:tokenizedForm>
	</c:if>
</div>
<div class="breaker"><!-- ne pas enlever --></div>

				
<%-- Voir les reponses les mieux notees / les plus recentes   --%>

<c:set var="orderByParam">
<c:if test="${not empty param.orderby }">${param.orderby }</c:if>
<c:if test="${empty param.orderby and not empty renderContext.site.properties['repliesListOrder']}">${renderContext.site.properties['repliesListOrder'].string}</c:if>
<c:if test="${empty param.orderby and empty renderContext.site.properties['repliesListOrder']}">score</c:if>
</c:set>

<div class="voirLes">
	<span><fmt:message key="replies.action.see"/></span>
	<form method="get" name="voirLes">
		<select class="listeVoirLes" name="orderby" onchange="this.form.submit();">
			<option value="score" 
			<c:if test="${orderByParam=='score'}"> selected="selected"</c:if>
			
			><fmt:message key="replies.sort.most.rated"/></option>
			<option value="lastmod"
			<c:if test="${orderByParam=='lastmod'}"> selected="selected"</c:if>
			><fmt:message key="replies.sort.most.recent"/></option>
		</select>						
	</form>							
</div>

<%-- les r�ponses --%>
<div>
<c:set var="orderBy" value="${orderByParam}"/>
<c:if test="${orderByParam=='lastmod'}">
<c:set var="orderBy" value="[jcr:created]"/>
</c:if>
<jcr:sql var="numberOfPostsQuery"
        sql="select * from [jnt:ciReply] as post  where isdescendantnode(post, ['${currentNode.path}']) order by post.${orderBy} desc"/>
<c:set target="${moduleMap}" property="commentsList" value="${numberOfPostsQuery.nodes}"/>
<c:set target="${moduleMap}" property="listTotalSize" value="${numberOfPostsQuery.nodes.size}"/>        

<c:set var="pageSize" value="${not empty renderContext.site.properties['paginationmaxItems'].long ? renderContext.site.properties['paginationmaxItems'].long : 6}" />

<template:initPager totalSize="${moduleMap.listTotalSize}" id="${currentNode.identifier}" pageSize="${pageSize}"/>
<c:forEach items="${moduleMap.commentsList}" var="subchild" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}">
	<c:set var="repliesStatus" value="${status}" scope="request"/>
	<template:module node="${ subchild }" editable="true" /><br/>
</c:forEach>
<%@include file="../../common/ciPagination.jspf"%>
<template:removePager id="${currentNode.identifier}"/>
</div>



<%-- Ma reponse (saisie) --%>
<div class="maReponseContainer saisie answerToQuestion">
	<div class="titreBlocMaReponse"><fmt:message key="my.reply"/></div>
	<form action="${url.base}${currentNode.path}.ciAddReplyAction.do" method="post"
		id="answerForm">
		<input type="hidden" name="title" value="" />
		<input type="hidden" name="jcrRedirectTo" value="<c:url value='${renderContext.mainResource.node.url}'/>" />
		<div class="maReponseSaisie">
			<c:if test="${not connected || isMaintenance}"><c:set var="clickToAnswer" >onclick="${onclick};${isMaintenance ? '' : '$(this).blur();'}"</c:set></c:if>
			<textarea id="answerBody" class="champMaReponseSaisie" rows="3" cols="75" name="body" <c:out value="${clickToAnswer}" escapeXml="false" />${isMaintenance ? 'readonly' : ''}><fmt:message key="reply.action.i.reply"/></textarea>
		</div>
		<div class="boutonsMaReponse">
			<c:if test="${connected && not isMaintenance}"><c:set var="onclick" >openPreview();</c:set></c:if>
			<input title="Pr&eacute;visualiser ma r&eacute;ponse" type="button" value="" class="btPrevisualiserMaReponse wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="${onclick}"/>
			<c:if test="${connected && not isMaintenance}"><c:set var="onclick" >saveReply();</c:set></c:if>
			<input title="Envoyer" type="button" value="" class="btEnvoyer wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="${isMaintenance ? '' : onclick}"/>
		</div>
	</form>
</div>

<%-- Ma reponse (previsualisation) --%>
<div class="maReponseContainer PreviwAnswer">
	<jcr:node var="currentUserNode" uuid="${currentUser.identifier}"/>
	<c:set var="userNode" value="${currentUserNode}" />
	<%@ include file="../../common/ciSetUserInfo.jspf" %>
	
	<div class="titreBlocMaReponse"><fmt:message key="my.reply"/></div>
	<div class="questionContainer">
		<div class="questionEntete">
			<fmt:message key="reply.by"/> <span class="pseudo">
			<a href="#" title="Acc&eacute;der au profil de ${pseudo}" >
			${isProfessional ? fullname : pseudo}</a></span> |
			<c:set var="displayedUser" value="${cia:getUserByPseudoname(pseudo)}"/>
				<c:choose>					
					<c:when test="${cia:isModerator(renderContext,displayedUser)}">
					 <span class="statut">Mod&eacute;rateur</span>
					</c:when>
				 <c:otherwise>
					<span class="statut">${userTypeLabel}</span>
				 </c:otherwise>
			   </c:choose>	
		</div>
		<div class="colonneAvatar">
			<a class="blocQuestionAvatar" title="Acc&eacute;der au profil de ${pseudo}" >
				<img src="${avatarSrc40}" width="40px" height="40px"  alt="Photo Avatar" /></a>
		</div>
		<div class="colonneContenu">
			<div class="questionDetail" id="previewAnswerBody">
			</div>
			<div class="questionFooter "> 
				<a class="signalerAbus" title="Signaler un abus"  href="#"><fmt:message key="post.flag"/></a>
				<div class="questionFooterRight" >		
					<c:set var="nowDate" value="<%=new java.util.Date()%>" />
					${cia:formatDate(nowDate, postDatePattern, true)}
				</div>
				<div class="bulleRepondre">
					<fmt:message var="reply" key="reply.action.reply"/>
					<a title="${reply}">${reply}</a> |
					<fmt:message var="replywith" key="reply.action.reply.withquote"/>
					<a title="${replywith}">${replywith}</a>
				</div>
			</div>
		</div>
	</div>
	<div class="boutonsMaReponse">
		<input title="Modifier ma r&eacute;ponse" type="button" value="" class="btModifierMaReponse" onclick="closePreview();"/>
		<c:if test="${connected}"><c:set var="onclick" >saveReply();</c:set></c:if>
		<input title="Envoyer" type="button" value="" class="btEnvoyer" onclick="${onclick}"/>	
	</div>
</div>