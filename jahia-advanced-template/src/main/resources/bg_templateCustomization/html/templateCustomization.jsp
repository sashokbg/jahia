<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr"%>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search"%>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib"%>
<fmt:message key="jahiajahiaAdvancedTemplates.settings.changeSaved"
	var="i18nSaved" />
<c:set var="i18nSaved" value="${functions:escapeJavaScript(i18nSaved)}" />
<template:addResources type="css" resources="jquery.fileupload.css" />
<template:addResources type="javascript"
	resources="jquery.min.js,jquery.form.min.js,jquery.fileupload-with-ui.min.js" />
<template:addResources>
	<script type="text/javascript">
		function updateSiteFooterLinks() {
			$('#updateSiteForm').ajaxSubmit({
				data : {
					'displayFooterLinks' : $('#footerLinks').val()
				},
				dataType : "json",
				success : function(response) {
					if (response.warn != undefined) {
						alert(response.warn);
					} else {
						alert('${i18nSaved}');
					}
				},
				error : function() {
					//do nothing;
				}
			});
		}
	</script>
</template:addResources>
<c:set var="site" value="${renderContext.mainResource.node.resolveSite}" />

<c:set var="displayFooterLinks"
	value="${site.properties['displayFooterLinks'].string}" />

<h2>
	<fmt:message
		key="bgmix_templateCustomization.displayFooterLinks" />
</h2>
<div class="box-1">
	<form id="updateSiteForm"
		action="${url.base}${renderContext.mainResource.node.resolveSite.path}"
		method="post">
		<input type="hidden" name="jcrMethodToCall" value="put" /> <input
			type="hidden" name="jcr:mixinTypes"
			value="bgmix:templateCustomization" /> <label
			for="footerLinks" class="checkbox"> <select id="footerLinks"
			name="footerLinks" onchange="updateSiteFooterLinks()">
				<option value="all" ${displayFooterLinks eq 'all' ? 'selected' : ''}><fmt:message
						key="bgmix_templateCustomization.displayFooterLinks.all" /></option>
				<option value="none"
					${displayFooterLinks eq 'none' ? 'selected' : ''}><fmt:message
						key="bgmix_templateCustomization.displayFooterLinks.none" /></option>
				<option value="home"
					${displayFooterLinks eq 'home' ? 'selected' : ''}><fmt:message
						key="bgmix_templateCustomization.displayFooterLinks.home" /></option>
		</select>
		</label>
	</form>
</div>
<h2>
	<fmt:message key="jahiaAdvancedTemplates.settings.logo" />
</h2>
<div class="box-1">
	<jcr:node var="siteLogo"
		path="${site.path}/files/bootstrap/img/logo.png" />
	<c:if test="${not empty siteLogo}">
		<img id="siteLogo-${currentNode.identifier}"
			src="${not empty siteLogo.url ? siteLogo.url : ''}"
			alt="<fmt:message key="jahiaAdvancedTemplates.logo"/>" />
	</c:if>
	<form class="file_upload" id="file_upload_${currentNode.identifier}"
		action="<c:url value='${url.base}${renderContext.mainResource.node.resolveSite.path}.updateLogo.do'/>"
		method="POST" enctype="multipart/form-data" accept="application/json">
		<div id="file_upload_container-${currentNode.identifier}"
			class="btn btn-primary" style="width: 200px">
			<input type="file" name="file" multiple>
			<button>
				<fmt:message key="jahiaAdvancedTemplates.uploadLogo" />
			</button>
			<div id="drop-box-file-upload-${currentNode.identifier}">
				<fmt:message key="jahiaAdvancedTemplates.uploadLogo" />
			</div>
		</div>
	</form>
	<table id="files${currentNode.identifier}" class="table"></table>
</div>
<script>
	/*global $ */
	$(function() {
		$('#file_upload_${currentNode.identifier}')
				.fileUploadUI(
						{
							namespace : 'file_upload_${currentNode.identifier}',
							onComplete : function(event, files, index, xhr,
									handler) {
								var response = JSON.parse(xhr.response);
								if (response.logoUpdate) {
									d = new Date();
									$("#siteLogo-${currentNode.identifier}")
											.attr(
													"src",
													response.logoUrl + "?"
															+ d.getTime());
								} else {
									alert(response.errorMessage);
								}
							},
							acceptFileTypes : /(\.|\/)(png)$/i,
							uploadTable : $('#files${currentNode.identifier}'),
							dropZone : $('#file_upload_container-${currentNode.identifier}'),
							beforeSend : function(event, files, index, xhr,
									handler, callBack) {
								handler.formData = {
									'jcrNodeType' : "jnt:file",
									'jcrNodeName' : "logo.png",
									'jcrTargetName' : "logo.png",
									'jcrReturnContentType' : "json",
									'jcrReturnContentTypeOverride' : 'application/json; charset=UTF-8'
								};
								callBack();
							},
							buildUploadRow : function(files, index) {
								return $('<tr><td>'
										+ files[index].name
										+ '<\/td>'
										+ '<td class="file_upload_progress"><div><\/div><\/td>'
										+ '<td class="file_upload_cancel">'
										+ '<button class="ui-state-default ui-corner-all" title="Cancel">'
										+ '<span class="ui-icon ui-icon-cancel">Cancel<\/span>'
										+ '<\/button><\/td><\/tr>');
							}
						});
	});
</script>