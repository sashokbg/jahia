<%--
	@author : el-aarko
	@created : 25 juil. 2012
	@Id : Inscription confirmation
	@description : page de confirmation suite à l'inscription

 --%>

<%@include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="contentRJUser" var="contentRJUser" />
<jcr:nodeProperty node="${currentNode}" name="contentFRUser" var="contentFRUser" />

<jcr:node var="userNode" uuid="${currentUser.identifier}"/>
<%@include file="../../common/ciSetUserInfo.jspf"%>

<c:if test="${cia:removeSessionEventFlag(renderContext, 'JUST_CREATED')}">
	<template:addResources>
		<!-- Debut code AdPerf v2.0 http://weborama.com -->
		<script>var webo_performance = false;</script>
		<script type="text/javascript"
			src="http://cstatic.weborama.fr/js/advertiser/wbo_performance.js"></script>
		<script type="text/javascript">
			WBO_AMOUNT = "0.0"; /* <== set here the command amount */
			WBO_CLIENT = "${email}"; /* <== set here your client id */
			WBO_INVOICE = ""; /* <== set here your bill id */
			WBO_NUM_ITEMS = 1; /* <== set here the number of item */
			WBO_POST_VALIDATION = 0; /* <== set to 1 if need backoffice validation */
		
			if (webo_performance) {
				transfo = new performanceTransfo(SITE = 434432, WBO_ID_TRANSFO = 20802);
				transfo.setAmount(WBO_AMOUNT);
				transfo.setClient(WBO_CLIENT);
				transfo.setId(WBO_INVOICE);
				transfo.setQuantity(WBO_NUM_ITEMS);
				transfo.setPostValidation(WBO_POST_VALIDATION);
				transfo.setHost("ag2rlamondiale.solution.weborama.fr");
		
				/* == Optional parameters == */
				//~optional parameters~
				/* == Type parameters == */
				/* == Free parameters == */
				transfo.execute();
				//alert('OK traceur');
			}
		</script>
		<!-- Fin code AdPerf -->
	</template:addResources>
</c:if>

<div class="conteneurSimple degrade">
	<span class="messageBienvenueTitre"><fmt:message key="registration.confirm.welcome"/>
			<span></span> <!-- blank space... &nbsp; adds 2 whitespaces... -->
			<c:if test="${empty currentUser.properties['j:firstName']
							|| empty currentUser.properties['j:lastName']}">
				${currentUser.properties['pseudoname']},
			</c:if>
			<c:if test="${not empty currentUser.properties['j:firstName'] 
							&& not empty currentUser.properties['j:lastName']}">
				<span class="vert">${currentUser.properties['j:firstName']}</span>
				<span class="vert">${currentUser.properties['j:lastName']},</span>
			</c:if>
	</span>
	
	<span class="messageBienvenue" style="padding-bottom: 10px;">
		<fmt:message key="registration.confirm.description.mail"/>
			&nbsp;<span class="lienBleu">${currentUser.properties['j:email']}</span>
	</span>
	
	<span class="messageBienvenue" style="padding-bottom: 10px;">
		<c:choose>
			<c:when test="${'radRetJunior' eq userType}">
				${contentRJUser.string}
			</c:when>
			<c:otherwise>
				${contentFRUser.string}
			</c:otherwise>
		</c:choose>
	</span>
	
	<a href="${renderContext.site.properties['userActivitiesLink'].node.url}" class="lienAccesEspacePrive fl"></a>
	<a href="${renderContext.site.home.url}" class="lienRetourSite fl" ></a>
	<div class="clear"></div>
</div>