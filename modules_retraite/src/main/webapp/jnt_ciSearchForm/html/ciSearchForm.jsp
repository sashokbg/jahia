<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : US-800
	@description : permet d'envoyer un formulaire de recherche sur les articles et questions du forum

 --%>

<%@include file="../../common/declarations.jspf"%>
<template:addResources type="css" resources="ci.autocomplete.css" />
<template:addResources type="css" resources="thickbox.css" />
<template:addResources type="javascript" resources="jquery.autocomplete.js" />
<template:addResources type="javascript" resources="jquery.bgiframe.min.js" />
<template:addResources type="javascript" resources="thickbox-compressed.js" />
<template:addResources>
	<style>
		.zoneQuestionMenu .spinnerWait {
			background-position: right;
		}
	</style>
</template:addResources>
<template:addResources>
	<script>
	 $(document).ready(function() {
	        $("#champQuestion").autocomplete("${url.base}${currentNode.path}.autoComplete.do", {
		        	dataType: "json",
		            cacheLength: 1,
		            parse: function parse(data) {
		        		var parsed = [];
		        		var rows = data.result;
		        		for (var i=0; i < rows.length; i++) {
		        			var row = $.trim(rows[i]);
		        			if (row) {
		        				parsed[parsed.length] = {
		        					data: row,
		        					value: row,
		        					result: row
		        				};
		        			}
		        		}
		        		return parsed;
		        	},
		        	formatItem: function(row) {
						return row;
					},
	             	minChars: 3,
	                loadingClass: "spinnerWait"
	        });
	    });
	var highlightSearchField = function(row) {
		var speed = 110;
		var wd = row.width();
		row.stop().animate({width: wd + wd/20 + "px"}, speed).animate({width: wd + "px"}, speed);
	};
	var validateSearchForm = function(){
		var term = $('#champQuestion').val();
		if (term != null && $.trim(term) != '' && term != 'J\'ai une question sur...') {
			return true;
			} else {
				highlightSearchField($('#champQuestion'));
				$('#champQuestion').focus().select().val('');
				return false;
			}
		};
	</script>
</template:addResources>

<%-- Veuillez commenter les blocs importants --%>

<div class="zoneQuestionMenu">
	<s:form name="questionForm"
		action="${renderContext.site.properties['searchPageLink'].node.url}"
		method="get"
		onSubmit="return validateSearchForm();">
		<c:if
			test="${empty renderContext.site.properties['searchPageLink'].node && renderContext.editMode}">
			<span style="color: red;">Le lien d'affichage des
				r&eacute;sultats n'est pas renseign&eacute; !</span>
		</c:if>
		<s:nodeType display="false" value="jmix:ciSearchable" />
		<c:set var="termSearch"
			value="${fn:escapeXml(param['src_terms[0].term'])}" />
		<s:term match="all_words" searchInSelectionOptions="true"
			id="champQuestion" searchIn="siteContent,files"
			value="${not empty termSearch ? termSearch : 'J\\'ai une question sur...'}" class="champQuestion" />
		<input type="button" value="" onclick="if(validateSearchForm())this.form.submit();" class="btQuestion" id="searchSubmit" />
		<s:site value="${renderContext.site.name}" display="false" />
		<s:language value="${renderContext.mainResource.locale}"
			display="false" />
	</s:form>
</div>
<div class="breaker">
	<!-- ne pas enlever -->
</div>
