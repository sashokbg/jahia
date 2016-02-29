<%--
	@author : annosse
	@created : 11 septembre 2012
	@Id : idDeLaPage
	@description : queFaitLaJsp

 --%>

<%@page import="org.jahia.registries.ServicesRegistry"%>
<%@page import="org.jahia.services.usermanager.JahiaUser"%>
<%@include file="../../common/declarations.jspf"%>

<c:set var="orderByParam">
<c:if test="${not empty param.orderby }">${param.orderby }</c:if>
<c:if test="${empty param.orderby }">[jcr:created]</c:if>
</c:set>

<jcr:sql var="membersListQuery"
        sql="select * from [jnt:user] "/>
as user  where user.isMember='true' order by user.${orderByParam} desc HAHA
<c:set target="${moduleMap}" property="membersList" value="${membersListQuery.nodes}"/>
<c:set target="${moduleMap}" property="membersListTotalSize" value="${membersListQuery.nodes.size}"/>


<jcr:sql var="futurRetiredMembersListQuery"
        sql="select * from [jnt:user] as user  where (user.isMember='true' and user.userType ='radFuturRetraite' ) order by user.${orderByParam}   desc"/>

<c:set target="${moduleMap}" property="membersListfuturRetiredSize" value="${futurRetiredMembersListQuery.nodes.size}"/>

Il y a ${moduleMap.membersListTotalSize} membres
dont ${moduleMap.membersListfuturRetiredSize} futurs retraités et ${moduleMap.membersListTotalSize - moduleMap.membersListfuturRetiredSize} retraités juniors

${renderContext.site.properties['memberListLineperPage'].long}
<template:initPager totalSize="${membersListTotalSize.listTotalSize}" id="${currentNode.identifier}" pageSize="150"/>
<c:forEach items="${moduleMap.membersList}" var="member" varStatus="status" begin="${moduleMap.begin}" end="${moduleMap.end}" >
	${member.properties['pseudoname'].string}AA
	${member.properties['jcr:created'].date}AA
	${member.properties['userType'].string}AA
	${member.properties['j:picture'].node.url}AA
	<c:set var="usersss" scope="request" value="${member.name}"/>
	
	<%
	try{
		
	JahiaUser usss=ServicesRegistry.getInstance().getJahiaUserManagerService().lookupUser(""+request.getAttribute("usersss"));
	usss.setProperty("isMember", "true");
	}catch(Exception e){
		//rien
	}
	%>
</c:forEach>

<%@include file="../../common/ciPagination.jspf"%>

<template:removePager id="${currentNode.identifier}"/>