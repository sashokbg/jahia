<%@include file="declarations.jspf"%>
<%-- contenu html --%>
<template:addResources type="javascript" resources="jquery.fancybox-1.3.4.pack.js"/>

<template:addResources>
	<style>
	#maintenance {
		display: none;
	}
	</style>
</template:addResources>
<template:addResources>
	<script type="text/javascript">
		jQuery(document).ready(function() {
			$(".wAccess").fancybox({
				hideOnOverlayClick : true,
				padding : 0,
				overlayColor : '#FFF'
			});
		});
	</script>
</template:addResources>

<div id="maintenance">
	<div id="popMaintenance" class="degrade" style="width: 320px;">
		<h3 class="band">Maintenance du site</h3>
		<div class="contenuPopMdpOublie">
			<img style="display: inline-block; float: left; margin-left: -12px;"
				src="${currentRequest.contextPath}/modules/modules_retraite/img/maintenance/warning_100.gif"
				alt="maintenance_warn" /> <span style="text-align: justify; display: inline-block; float: left; width: 202px;">Le
				site est en cours de maintenance.<br />Certaines fonctionnalit&eacute;s sont d&eacute;sactiv&eacute;es.<br />Nous vous invitons &agrave; vous
				reconnecter ult&eacute;rieurement.<br /><br />Veuillez nous excuser.</span>
				<div class="breaker"></div>
		</div>
	</div>
</div>