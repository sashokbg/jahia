<%--
	@author : el-aarko
	@created : 24 sept. 2012
	@Id : breadcrum
	@description : fil d'ariane

 --%>

<%@include file="../../common/declarations.jspf"%>

<jcr:nodeProperty name="fbBoxDeactivate"	var="fbBoxDeactivate"	node="${currentNode}" />
<jcr:nodeProperty name="fbFloatLeft"		var="fbFloatLeft"		node="${currentNode}" />
<jcr:nodeProperty name="fbUrl"				var="fbUrl"				node="${currentNode}" />
<jcr:nodeProperty name="fbDataSend"			var="fbDataSend"		node="${currentNode}" />
<jcr:nodeProperty name="fbLayoutStyle"		var="fbLayoutStyle"		node="${currentNode}" />
<jcr:nodeProperty name="fbWidth"			var="fbWidth"			node="${currentNode}" />
<jcr:nodeProperty name="fbShowFaces"		var="fbShowFaces"		node="${currentNode}" />
<jcr:nodeProperty name="fbFont"				var="fbFont"			node="${currentNode}" />
<jcr:nodeProperty name="fbColorScheme"		var="fbColorScheme"		node="${currentNode}" />
<jcr:nodeProperty name="fbVerbDisplay"		var="fbVerbDisplay"		node="${currentNode}" />

<c:set var="currentPage" value="${renderContext.mainResource.node}" />

<template:addCacheDependency node="${currentPage}"  />

<c:if test="${not fbBoxDeactivate.boolean && not renderContext.editMode}">
	
	<c:url var="defaultFbUrl" value="${currentPage.url}" />

	<script>
		var debugFB = false;
		$(document).ready(function(){
			logFB = function(str) {
				if (debugFB && window.console)
					console.log('[Facebook Event] ' + str);
			};
			errFB = function(str) {
				if (window.console)
					console.error('[Facebook Event] ' + str);
			};
			var chkMaxBounds = 100;
			var countChk = 0;
			var checkFbExists = setInterval(function() {
				if (!typeof FB === "undefined" && !_gaq === "undefined" || countChk <= chkMaxBounds) {
					logFB("FB present at count = " + countChk + "...");
					logFB("Trying to set FB Event callbacks...");
					try {
				    	if (FB && FB.Event && FB.Event.subscribe) {
							FB.Event.subscribe('edge.create', function(targetUrl) {
								if(targetUrl === '${fbUrl.string}') {
									trackEvent(['Facebook - Page Officielle', 'Like', '${functions:escapeJavaScript(currentPage.displayableName)}'], '_trackSocial');
									logFB('Reporting Facebook Like Event on "Facebook Page"');
								} else {
									trackFBFromArticle(targetUrl, 'Like');
								}
							});
							logFB("edge.create callback created...");
							
							FB.Event.subscribe('edge.remove', function(targetUrl) {
								if(targetUrl === '${fbUrl.string}') {
									trackEvent(['Facebook - Page Officielle', 'Unlike', '${functions:escapeJavaScript(currentPage.displayableName)}'], '_trackSocial');
									logFB('Reporting Facebook Unlike Event on "Facebook Page"');
								} else {
									trackFBFromArticle(targetUrl, 'Unlike');
								}
							});
							logFB("edge.remove callback created...");
							
							clearInterval(checkFbExists);
						}
				     } catch (e) {
				    	 if(countChk < chkMaxBounds)
				    	 	logFB("Let's try to set FB callbacks again.");
				    	 
				    	 if(countChk >= chkMaxBounds) {
				    	 	errFB("Cannot set FB callbacks after " + countChk + " tries : " + e);
				    	 	logFB("Facebook events won't be tracked !!");
				    	 }
					}
				}
				countChk++;
			}, 100); // check every 100ms
		});
	</script>
	
	<div style="${fbFloatLeft.boolean ? 'float: left;' : ''}" class="fb-like ${fbColorScheme.string eq 'dark' ? ' dark_background' : ''}"
		data-href="${functions:default(fbUrl.string, defaultFbUrl)}"
		data-send="${functions:default(fbDataSend.boolean, 'false')}"
		data-layout="${functions:default(fbLayoutStyle.string, 'button_count')}"
		data-width="${functions:default(fbWidth.double, '210')}"
		data-show-faces="${functions:default(fbShowFaces.boolean, 'false')}"
		data-font="${functions:default(fbFont.string, 'verdana')}"
		data-colorscheme="${functions:default(fbColorScheme.string, 'light')}"
		data-action="${functions:default(fbVerbDisplay.string, 'like')}"></div>
</c:if>

<c:if test="${renderContext.editMode}">
	Module Facebook - Like Button
</c:if>
