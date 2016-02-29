<%--
	@author : lakreb
	@created : 25 sept. 2012
	@Id : US700
	@description : affiche les resultats de recherche avec une liste de tri

 --%>
<%@include file="../../common/declarations.jspf"%>
<%
	long startTime = System.currentTimeMillis();
%>
<c:set var="hitsName" value="hits_${currentNode.identifier}" />
<c:set var="hitsCountName" value="hitsCount_${currentNode.identifier}" />

<c:set var="termSearch"
	value="${functions:removeHtmlTags(param['src_terms[0].term'])}" />
<c:if test="${not empty termSearch}">
	<s:results var="resultsHits">
		<c:set var="questionCount" value="${0}" />
		<c:set var="articleCount" value="${0}" />
		<s:resultIterator>
			<c:choose>
				<c:when test="${hit.type eq 'jnt:ciQuestion'}">
					<c:set var="questionCount" value="${questionCount + 1}" />
					<cia:addToList var="questionsList" value="${hit}" />
				</c:when>
				<c:otherwise>
					<c:set var="articleCount" value="${articleCount + 1}" />
					<cia:addToList var="articlesList" value="${hit}" />
				</c:otherwise>
			</c:choose>
		</s:resultIterator>
		<c:set target="${moduleMap}" property="listTotalSize" value="${count}" />
		<c:set target="${moduleMap}" property="resultsHits"
			value="${resultsHits}" />
		<c:set target="${moduleMap}" property="questionsList"
			value="${questionsList}" />
		<c:set target="${moduleMap}" property="questionCount"
			value="${questionCount}" />
		<c:set target="${moduleMap}" property="articlesList"
			value="${articlesList}" />
		<c:set target="${moduleMap}" property="articleCount"
			value="${articleCount}" />
	</s:results>
</c:if>

<c:choose>
	<c:when test="${moduleMap['listTotalSize'] > 0}">
		<c:choose>
			<c:when
				test="${not empty param.filterType && param.filterType ne ''}">
				<c:choose>
					<c:when test="${param.filterType eq 'jnt:ciQuestion'}">
						<c:set var="searchTotalSize" value="${moduleMap['questionCount']}" />
						<c:set var="searchHits" value="${moduleMap['questionsList']}" />
					</c:when>
					<c:otherwise>
						<c:set var="searchTotalSize" value="${moduleMap['articleCount']}" />
						<c:set var="searchHits" value="${moduleMap['articlesList']}" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:set var="searchTotalSize" value="${moduleMap['listTotalSize']}" />
				<c:set var="searchHits" value="${moduleMap['resultsHits']}" />
			</c:otherwise>
		</c:choose>

		<template:initPager totalSize="${searchTotalSize}"
			id="${currentNode.identifier}"
			pageSize="${renderContext.site.properties['paginationMaxResearchList'].long}" />

		<!-- nouvelle recherche -->

		<span>${moduleMap['listTotalSize']} r&eacute;sultats
			trouv&eacute;s pour &laquo;&nbsp;<em class="recherche">${termSearch}</em>&nbsp;&raquo;
		</span>

		<s:form id="filterTypeSearchForm" name="questionForm"
			action="${renderContext.site.properties['searchPageLink'].node.url}"
			method="get" style="display: inline; ">
			<s:nodeType display="false" value="jmix:ciSearchable" />
			<s:term display="false" match="all_words"
				searchInSelectionOptions="true" searchIn="siteContent,files"
				value="${termSearch}" />
			<input id="filterType" name="filterType" type="hidden" value="" />
			<s:site value="${renderContext.site.name}" display="false" />
			<s:language value="${renderContext.mainResource.locale}"
				display="false" />
		</s:form>
		<div class="lesOngletRecherche">
			<span
				class="fl ongletRecherche${empty param.filterType ? 'A' : 'Ina'}ctif"><a
				href="javascript:$('#filterType').val('');$('#filterTypeSearchForm').submit();"
				class="">Tous</a></span> <span
				class="fl ongletRecherche${param.filterType eq 'jnt:ciArticle' ? 'A' : 'Ina'}ctif"><a
				href="javascript:$('#filterType').val('jnt:ciArticle');$('#filterTypeSearchForm').submit();"
				class="">Articles (${articleCount})</a></span> <span
				class="fl ongletRecherche${param.filterType eq 'jnt:ciQuestion' ? 'A' : 'Ina'}ctif"><a
				href="javascript:$('#filterType').val('jnt:ciQuestion');$('#filterTypeSearchForm').submit();"
				class="">Questions (${questionCount})</a></span>
			<div class="breaker"></div>
		</div>
		<c:if test="${not empty searchHits}">
			<s:resultIterator begin="${moduleMap.begin}" end="${moduleMap.end}"
				varStatus="status" hits="${searchHits}">
				<%@ include file="searchHit.jspf"%>
			</s:resultIterator>
		</c:if>
		<c:if
			test="${searchTotalSize > renderContext.site.properties['paginationMaxResearchList'].long}">
			<div id="paginationResRech2">
				<%@include file="../../common/ciPagination.jspf"%>
				<div class="clear"></div>
			</div>
		</c:if>

		<template:removePager id="${currentNode.identifier}" />
	</c:when>








	<c:otherwise>
		<c:if test="${not empty termSearch}">
			<div id="phraseNoResult">
				Nous n'avons trouv&eacute; aucun r&eacute;sultat pour votre
				recherche <strong>${termSearch}</strong>
			</div>
		</c:if>
		<div id="suggestionRecherche">
			<p>Acc&eacute;der aux th&eacute;matiques du site:</p>
			<template:addResources>
				<script>
					$(document)
							.ready(
									function() {
										loadThematicsAction = "${url.base}${currentNode.path}.ciGetThematicNodes.do";
										$('.btValideRecherche')
												.click(
														function() {
															var url = $(
																	'#selectThematics')
																	.val();
															if (!url)
																url = $(
																		'#selectRubrics option:selected')
																		.attr(
																				'title');
															if (url)
																document.location = url;
														});
									});
					var loadThematics = function(pagePath) {
						if (pagePath == "") {
							$('#selectThematics').hide();
							return;
						} else {
							$('#selectThematics').show();
						}
						$("#selectThematics").html("");
						var queryString = "rubricPath=" + pagePath;
						jQuery
								.ajax({
									type : "post",
									url : loadThematicsAction,
									data : queryString,
									dataType : "json",
									success : function(obj) {
										if (obj == null)
											return;
										if (obj.thematics) {
											$("#selectThematics")
													.append(
															"<option value=\"\">S\u00E9lectionner</option>");
											for ( var i = 0; i < obj.thematics.length; i++) {
												var thematic = obj.thematics[i];
												$("#selectThematics").append(
														'<option value="'+thematic.url+'">'
																+ thematic.name
																+ '</option>');
											}
										} else {
											alert("Erreur lors de la r\u00E9cup\u00E9ration des pages th\u00E9matiques.");
										}
									}
								});
					};
				</script>
			</template:addResources>
			<div id="ligneListesTheme">
				<select id="selectRubrics" onchange="loadThematics(this.value);">
					<option value="">S&eacute;lectionner</option>
					<c:forEach
						items="${jcr:getChildrenOfType(renderContext.site.home,'jnt:page')}"
						var="subchild" varStatus="step">
						<c:if test="${subchild.properties['isRubric'].boolean}">
							<option title="${subchild.url}" value="${subchild.path}">${subchild.properties['jcr:title'].string}</option>
						</c:if>
					</c:forEach>
				</select> <select style="display: none;" id="selectThematics"
					class="lstSousTheme"></select>
				<div class="breaker"></div>
			</div>
			<input type="button" class="btValideRecherche" value="" />
			<p>
				Ou revenir &agrave; la <a class="lienBleu"
					href="${renderContext.site.home.url}">page d'accueil du site.</a>
			</p>
		</div>
	</c:otherwise>
</c:choose>

<%
	pageContext.setAttribute("searchTime",
			Long.valueOf(System.currentTimeMillis() - startTime));
%>

<utility:logger level="info"
	value="Search render time: ${searchTime} ms" />
