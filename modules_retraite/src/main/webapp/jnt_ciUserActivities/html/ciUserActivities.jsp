<%--
	@author : el-aarko
	@created : 27 aout 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>
<%@include file="../../common/declarations.jspf"%>

<template:addCacheDependency uuid="${currentUser.identifier}" />

<template:addResources type="javascript" resources="jquery.tmpl.min.js"/>
<template:addResources type="javascript" resources="ciUserActivities/ciUserActivities.js"/>
<c:if test="${connected }">
<template:addResources>
	<script type="text/javascript">
		$(document).ready(function() {
			var getUserRubricQuestions = function() {
				displayMoreElements("${url.base}${currentNode.path}.ciGetUserRubricQuestions.do", "",
						offsetQuestionsIndex, "#userThematicQuestions", "#moreQuestionsLink", questionHtmlTemplateNoLast);
			};
			var getUserRubricArticles = function() {
				displayMoreElements("${url.base}${currentNode.path}.ciGetUserRubricArticles.do", "",
						offsetArticlesIndex, "#derniersArticles", "#moreArticlesLink", articleHtmlTemplate);
			};
			var getUserThematicsQuestions = function() {
				displayMoreElements("${url.base}${currentNode.path}.ciGetUserThematicQuestions.do", "",
						offsetThematicQuestionsIndex, "#userQuestionsNoAnswer", "#getAllQuestionsLink", 
						questionHtmlTemplate);
			};
			var getUserMyQuestions = function() {
				displayMoreElements("${url.base}${currentNode.path}.ciGetUserQuestions.do", "",
						offsetUserQuestionsIndex, "#userQuestionsWithAnswer", "#getAllMyQuestionsLink", 
						questionHtmlTemplate);
			};
			var userRepliesCallback = function(response) {
				$("p.detail").each(function() {
					$(this).html(unescape($(this).text()));
					$(this).removeClass("detail");
				});
			};
			var getUserMyReplies = function() {
				displayMoreElements("${url.base}${currentNode.path}.ciGetUserReplies.do", "",
						offsetUserRepliesIndex, "#userAnswers", "#getAllMyRepliesLink", 
						questionWithReplyHtmlTemplate, userRepliesCallback);
			};
			getUserRubricQuestions();
			getUserRubricArticles();
			getUserThematicsQuestions();
			getUserMyQuestions();
			getUserMyReplies();
			$("#moreQuestionsLink").click(getUserRubricQuestions);
			$("#moreArticlesLink").click(getUserRubricArticles);
			$("#getAllQuestionsLink").click(getUserThematicsQuestions);
			$("#getAllMyQuestionsLink").click(getUserMyQuestions);
			$("#getAllMyRepliesLink").click(getUserMyReplies);
			$(".fold").hide();
			$(".clickToFold")[0].click();
			$(".clickToFold")[1].click();
		});
	</script>
</template:addResources>
<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<c:set var="myProfileUrl" value="${renderContext.site.properties['userProfilLink'].node.url}" />
<jcr:node var="userNode" uuid="${currentUser.identifier}" />
<%@include file="../../common/ciSetUserInfo.jspf" %>
<c:set var="myQuestionsBloc">
	<%-- Bloc :  Mes questions --%>
	<h3 class="band clickToFold ferme">Mes questions</h3>
	<div class="fold">
		<div class="dataSection bandSurrounded emptyContent" style="display: none;">
			<span class="messageMonProfilVide">Vous n'avez pas encore
				pos&eacute; de questions, <a id="poserQuestion"
				href="${renderContext.site.properties['addQuestionLink'].node.url}">cliquez-ici</a>
				pour en poser une.
			</span>
		</div>
		<div id="userQuestionsWithAnswer" class="dataSection bandSurrounded userQuestions">
		</div>
		<div class="voirToutes dataSection bandSurrounded">
			<a id="getAllMyQuestionsLink" class="lienBleu voirToutesQuestions">Voir
				plus de questions (<span class="nbOfElements"></span>)
			</a>
		</div>
	</div>
</c:set>
<a id="ongletMonActivite" class="ongletActiviteActif"></a>
<a href="${myProfileUrl}" id="ongletMonProfil" class="ongletProfilInactif"></a>
<div class="breaker"></div>
<div id="userProfil" class="myProfile">
	<%-- Bloc :  L'actualite des thematiques --%>
	<div id="userActivityMargeUp" class="dataSection paddingMedium"><!-- dnr --></div>
	<h3 class="clickToFold band ferme">L'actualit&eacute; des
		th&eacute;matiques</h3>
	<div class="fold">
		<c:if test="${empty selectedRubrics}">
			<div class="dataSection bandSurrounded">
				<span class="messageMonProfilVide">Pour voir
					l'actualit&eacute; de vos th&eacute;matiques, pensez &agrave;
					s&eacute;lectionner les th&eacute;matiques dans l'onglet <a
					href="${myProfileUrl }#userThematic">mon profil.</a>
				</span>
			</div>
		</c:if>
		<c:if test="${not empty selectedRubrics}">
			<div class="dataSection paddingMedium bandUp">
				<c:set var="userRubricsList" value="${cia:userRubrics(userNode)}" />
				<p class="changerTheme global">
					<a href="${myProfileUrl}#userThematic">Changer mes
						th&eacute;matiques :</a>&nbsp;
					<c:set var="foundFirstItem" value="${false}" />
					<c:forEach items="${userRubricsList}" var="rubric" varStatus="status">
						<c:set var="rubricTitle" value="${cia:pageTitle(rubric)}" />
						<c:if test="${not empty rubricTitle}">
							<c:if test="${not status.first || foundFirstItem}">
								&nbsp;|&nbsp;
							</c:if>
							<c:set var="foundFirstItem" value="${true}" />
							<span><a style="text-decoration: none;" href="${rubric.url}">${rubricTitle}</a></span>
						</c:if>
					</c:forEach>
				</p>
			</div>
			<div class="dataSection bandUp sectionSubTitle">
				<span class="userInfoLine titreSection">les derni&egrave;res
					questions</span>
			</div>
		<div id="userThematicQuestions" class="dataSection bandDown userQuestions">
		</div>
		</c:if>
		<div id="voirToutesQuestionsSection" class="voirToutes paddingMedium dataSection bandSurrounded">
			<a id="moreQuestionsLink" class="lienBleu voirToutesQuestions">Voir
				toutes les questions</a>
		</div>
		<c:if test="${not empty selectedRubrics}">
			<div id="lastArticlesSection" class="dataSection bandUp sectionSubTitle">
				<span class="userInfoLine titreSection">les derniers articles</span>
			</div>
		</c:if>
		<div id="derniersArticles">
			<!-- dnr used for ajax template insert - NB : no dataSection class below, is in template -->
		</div>
		<div class="voirToutes dataSection bandDown">
			<a id="moreArticlesLink" class="lienBleu voirToutesQuestions">Voir
				plus d'articles</a>
		</div>
	</div>
	<c:if test="${userType ne ciConstants.USER_TYPE_RETRAITE_JUNIOR}">
		${myQuestionsBloc}
	</c:if>
	<%-- Bloc :  Je suis Expert sur  --%>
	<h3 class="clickToFold band ferme">je suis expert, Les questions
		qui attendent ma r&eacute;ponse</h3>
	<div class="fold">
		<c:if test="${empty selectedThematics}">
			<div class="dataSection bandSurrounded">
				<span class="messageMonProfilVide">Vous n'avez pas encore
					d&eacute;termin&eacute; vos th&eacute;matiques, <a
					href="${myProfileUrl }#userExpertDomain">cliquez-ici</a> pour les choisir.
				</span>
			</div>
		</c:if>
		<c:set var="userThematicsList" value="${cia:userThematics(userNode)}" />
		<c:if test="${not empty selectedThematics}">
			<div class="dataSection bandUp paddingMedium">
				<p class="changerTheme global">
					<a href="${myProfileUrl }#userExpertDomain">Changer mes
						sous-th&eacute;matiques:</a>&nbsp;
					<c:set var="foundFirstItem" value="${false}" />
					<c:forEach items="${userThematicsList}" var="thematic" varStatus="status">
						<c:set var="thematicTitle" value="${cia:pageTitle(thematic)}" />
						<c:if test="${not empty thematicTitle}">
							<c:if test="${not status.first || foundFirstItem}">
								&nbsp;|&nbsp;
							</c:if>
							<c:set var="foundFirstItem" value="${true}" />
							<span><a style="text-decoration: none;" href="${thematic.url}">${thematicTitle}</a></span>
						</c:if>
					</c:forEach>
				</p>
			</div>
		<div id="userQuestionsNoAnswer" class="dataSection bandDown userQuestions">
		</div>
		</c:if>
		<div class="voirToutes dataSection bandDown">
			<a id="getAllQuestionsLink" class="lienBleu voirToutesQuestions">Voir
				plus de questions (<span class="nbOfElements"></span>)
			</a>
		</div>
	</div>
	<c:if test="${userType eq ciConstants.USER_TYPE_RETRAITE_JUNIOR}">
		${myQuestionsBloc}
	</c:if>
	<%-- Bloc : Mes r&eacute;ponses --%>
	<h3 class="band clickToFold last ferme">Mes r&eacute;ponses aux autres
		membres</h3>
	<div class="fold">
		<div class="dataSection bandSurrounded emptyContent" style="display: none;">
			<span class="messageMonProfilVide">Vous n'avez pas encore
				r&eacute;pondu aux questions des autres membres.</span>
		</div>
		<div id="userAnswers" class="dataSection bandUp"></div>
		<div class="voirToutes dataSection">
			<a id="getAllMyRepliesLink" class="lienBleu voirToutesQuestions">Voir
				plus de r&eacute;ponses aux autres membres (<span
				class="nbOfElements"></span>)
			</a>
		</div>
	</div>
	<div id="userActivityMargeUp" class="dataSection paddingMedium bandUp last"><!-- dnr --></div>
</div>
</c:if>

<%@include file="ga.jspf"%>