<%--
	@author : lakreb
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@include file="../../common/declarations.jspf"%>

<template:addResources resources="moderator.css" type="css" />

<script type='text/javascript' src='https://www.google.com/jsapi'></script>
<script type='text/javascript'>
	var debugToolsStats = false;

	google.load('visualization', '1', {
		packages : [ 'table', 'corechart', 'annotatedtimeline', 'geochart' ]
	});

	log = function(str) {
		if (this.debugToolsStats && window.console)
			console.log('[tools statistics] ' + str);
	};
	function reload() {
		jQuery.ajax({
			type : "post",
			url : "${url.base}${currentNode.path}.toolsStatsJSON.do",
			data : "typeStat=reload&param=",
			dataType : "json",
			success : function(obj) {
				location.href = location.href;
			}
		});
	}
	function addColumnsWithPercent(data, obj, debugTitle) {
		data.addColumn('number', 'Pourcentage (%)');
		var total = 0;
		for ( var i = 0; i < obj.rows.length; i++) {
			var donnees = obj.rows[i];
			total += eval(donnees.number);
		}
		for ( var i = 0; i < obj.rows.length; i++) {
			var donnees = obj.rows[i];
			var percent = (Math.round(1000 * eval(donnees.number) / total) / 10);
			data.addRow([ donnees.string, parseInt(donnees.number), percent ]);
			log(debugTitle + " : " + donnees.string + ", " + donnees.number
					+ ", " + percent);

		}
	}
</script>
<br />
<div id="moderatorTools">
	<div id="userProfil">
		<div id="thematiquesChoisies">
			<div class="conteneurThematiqueChoisie fl">
				<a href="javascript:;" onclick="reload();"> <span
					class="libelleThematique fl"> Recalculer les statistiques </span></a>
				<div class="clear"></div>
			</div>
		</div>
	</div>

	<div class="clear"></div>

	<br />
	<hr />
	<br />
	<c:if test="${!renderContext.editMode}">
		<%@include file="./tables/keyNumbers.jspf"%>
		<%@include file="./tables/mostActivesRubrics.jspf"%>
		<%@include file="./tables/mostActivesThemes.jspf"%>
		<%@include file="./graphs/preferedRubricsFR.jspf"%>
		<%@include file="./graphs/preferedRubricsRJ.jspf"%>
		<%@include file="./graphs/preferedThematicsRJ.jspf"%>
		<%@include file="./graphs/memberRegistration.jspf"%>
		<%@include file="./graphs/questionsCount.jspf"%>
		<%@include file="./graphs/zipCodeMembers.jspf"%>
		<%@include file="./maps/membersRegions.jspf"%>
		<%@include file="./graphs/memberAges.jspf"%>
		<%@include file="./graphs/informedExpertDomains.jspf"%>
		<%@include file="./graphs/membersStatus.jspf"%>
		<%@include file="./graphs/membersTypes.jspf"%>
	</c:if>
</div>