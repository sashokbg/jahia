<!DOCTYPE html>

<%@include file="../../incl/common.jspf" %>

<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<template:include view="hidden.head" />
	
	<title>${fn:escapeXml(renderContext.mainResource.node.displayableName)}</title>
</head>

<body>
	<template:include view="hidden.header" />
	
	<div class="container-fluid"><!--start bodywrapper-->
		<div class="row">
		    <template:area path="pagecontent"/>
		</div>
	</div>
	<!--stop bodywrapper-->
	
	<c:if test="${renderContext.editMode}">
	    <template:addResources type="css" resources="edit.css" />
	</c:if>
	
	<template:theme/>

</body>
</html>
