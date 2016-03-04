<!DOCTYPE html>

<%@include file="../../incl/common.jspf" %>

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<template:include view="hidden.head" />

<title>${fn:escapeXml(renderContext.mainResource.node.displayableName)}</title>
</head>

<body>
	<%@include file="header.jspf" %>

	<div class="container-fluid">
		<div class="col-md-3">
			<template:area path="grid-1" />
		</div>
		<div class="col-md-3">
			<template:area path="grid-2" />
		</div>
		<div class="col-md-3">
			<template:area path="grid-3" />
		</div>
		<div class="col-md-3">
			<template:area path="grid-4" />
		</div>
	</div>

	<c:if test="${renderContext.editMode}">
		<template:addResources type="css" resources="edit.css" />
	</c:if>

	<%@include file="footer.jspf" %>

	<template:theme />

</body>
</html>
