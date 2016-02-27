<%--
	@author : el-aarko
	@created : 10 Aout 2012
	@Id : AddQuestion
	@description : permet de poser une question / US-103

 --%>

<%@include file="../../common/declarations.jspf" %>
<%@include file="ga.jspf" %>

<%@include file="ciQuestionForm.js.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title" 		var="title" />
<jcr:nodeProperty node="${currentNode}" name="summary" 		var="summary" />
<jcr:nodeProperty node="${currentNode}" name="underTitle1" 	var="underTitle1" />
<jcr:nodeProperty node="${currentNode}" name="underTitle2" 	var="underTitle2" />
<jcr:nodeProperty node="${currentNode}" name="body" 		var="body" />


<fmt:message var="titleDefault" 		key="jnt_ciQuestionForm.title.default"/>
<fmt:message var="summaryDefault" 		key="jnt_ciQuestionForm.summary.default"/>
<fmt:message var="underTitle1Default" 	key="jnt_ciQuestionForm.underTitle1.default"/>
<fmt:message var="underTitle2Default" 	key="jnt_ciQuestionForm.underTitle2.default"/>

<c:if test="${isMaintenance and not renderContext.editMode}">
	<template:addResources insert="true">
		<script type="text/javascript">
			$(document).ready(function(){
				$('#questionEdition select, #questionEdition input, #questionEdition textarea').attr('disabled', 'disabled');
			});
		</script>
		
	</template:addResources>
</c:if>

<div class="titrePage">
	<h1>${empty title ? titleDefault : title.string}</h1>
</div>
<%-- Edition de la question --%>
<div id="questionEdition" class="boxQuestion">	
	<c:if test="${isMaintenance}">
		<div style="font-weight: bold; padding: 15px;">
		<img
		
			style="width: 55px; display: inline-block; float: left; margin: -7px 0 0 -12px;"
			src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
			alt="maintenance_warn" /> 
		<span>Le site est en cours de maintenance, nous vous invitons &agrave; vous reconnecter ult&eacute;rieurement pour poser votre question.
		Veuillez nous excuser.</span>
		</div>
	</c:if>
	<div class="outils">
		<h2>${empty underTitle1 ? underTitle1Default : underTitle1.string}</h2>
		${not empty body ? body.string : ''}		
	</div>
	<div class="separation"></div>
	<template:tokenizedForm>
		<form action="${url.base}${currentNode.path}.ciAddQuestionAction.do" name="askQuestionForm" id="askQuestionForm" method="post">
		<div class="formulaireQuestion">
			<h2>${empty underTitle2 ? underTitle2Default : underTitle2.string}</h2>
			<table>
			<tr>
				<th><fmt:message key="questionform.thematics"/></th>
				<th><fmt:message key="questionform.under.thematics"/></th>
			</tr>
			<tr>
				<td>
					<select id="selectRubrics"  class="champFormSignalerAbus" name="rubricPath">
						<option value="">S&eacute;lectionner</option>
						<c:forEach items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}" var="subchild" varStatus="step">
							<c:if test="${subchild.properties['isRubric'].boolean}">
								<option value="${subchild.path}">${subchild.properties['jcr:title'].string}</option>
							</c:if>
						</c:forEach>
					</select>
				</td>
				<td>
					<select id="selectThematics" class="champFormSignalerAbus" name="thematicPath">
						<option value="">S&eacute;lectionner</option>
					</select>
				</td>
			</tr>
			</table>
			<div class="erreurFormulaireQuestion" id="thematicError"></div>
			Sujet de ma question ou de mon t&eacute;moignage <span>(100 caract&egrave;res max)</span>
			<input id="questionTitle" name="title" type=text maxlength="100"/>
			<div class="erreurFormulaireQuestion" id="titleError"></div>
			Ma question ou mon t&eacute;moignage <span>(2000 caract&egrave;res max)</span>
			<textarea id="questionBody" name="body" maxlength="2000"></textarea>
		</div>
		<c:choose>
			<c:when test="${not isMaintenance}">
				<input class="btEnvoyerQuestion btEnvoyer2" type="button" value="">
				<input title="Pr&eacute;visualiser ma r&eacute;ponse" type="button" value="" class="btPrevisualiserQuestion wAccess" ${isMaintenance ? 'href="#popMaintenance"' : ''} onclick="openPreview();"/>
			</c:when>
			<c:otherwise>
				<a style="margin-right: 12px;" class="maintenance btEnvoyerQuestion btEnvoyer2"
					href="#popMaintenance"></a>
				<a class="maintenance btModifierQuestion" href="#popMaintenance"></a>
			</c:otherwise>
		</c:choose>
	</form>
	</template:tokenizedForm>

	<div class="breaker"><!-- ne pas enlever --></div>
</div>

<%-- Pr&eacute;visualisation de la question --%>
<div id="questionPreview">
	<jcr:node var="currentUserNode" uuid="${currentUser.identifier}"/>
	<c:set var="userNode" value="${currentUserNode}" />
	<%@include file="../../common/ciSetUserInfo.jspf"%>
	<div class="questionContainer statutRetraite">
		<div class="colonneAvatar">
			<a class="blocQuestionAvatar" title="Acc&eacute;der au profil de ${pseudo}"  href="#">
				<img src="${avatarSrc40}"  height="40px" width="40px"  alt="Photo Avatar" /></a>
		</div>
		<div class="colonneContenu">
			<div class="questionEntete">
				Question de <span class="pseudo">
					<a href="#" title="Acc&eacute;der au profil de ${pseudo}" >${pseudo}</a>
				</span> | <span class="statut">${userTypeLabel}</span>
			</div>
			<h2 id="questionTitlePreview" class="questionTitre"></h2>
			<div id="questionBodyPreview" class="questionDetail"></div>
			
			<div class="questionFooter "> 
				<div class="questionFooterRight" > 
					<c:set var="nowDate" value="<%=new java.util.Date()%>" />
					${cia:formatDate(nowDate, postDatePattern, true)}
				</div>			
			</div>						
		</div>					
	</div>
	
	<c:choose>
		<c:when test="${not isMaintenance}">
			<input class="btEnvoyerQuestion btEnvoyer2" type="button" value="">
			<input class="btModifierQuestion" type="button" value="" onclick="closePreview();">
		</c:when>
		<c:otherwise>
			<a style="margin-right: 12px;"
				class="maintenance btEnvoyerQuestion btEnvoyer2"
				href="#popMaintenance"></a>
			<a class="maintenance btModifierQuestion" href="#popMaintenance"></a>
		</c:otherwise>
	</c:choose>
		
	
	<div class="breaker"><!-- ne pas enlever --></div>
</div>