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
	
		<div class="container">
			<div class="row">
				<div class="col-md-3">
					<template:area path="left-column" />
				</div>
				<div class="col-md-9">
					<template:area path="pagecontent" />
				</div>
			</div>
		</div>
	
		<c:if test="${renderContext.editMode}">
			<template:addResources type="css" resources="edit.css" />
		</c:if>
		
		<%@include file="footer.jspf" %>
		
		<template:theme />
	</body>
</html>
