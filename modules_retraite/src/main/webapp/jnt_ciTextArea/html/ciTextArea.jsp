<%@include file="../../common/declarations.jspf" %>

<jcr:nodeProperty node="${currentNode}" name="title" var="title" />
<jcr:nodeProperty node="${currentNode}" name="imageContent" var="imageContent" />
<jcr:nodeProperty node="${currentNode}" name="content" var="content" />

<div class="textBloc" style="padding: 10px 0 0 0;">
	<h3 class="band" style="background-color: #009fcc;">${title.string}</h3>
	<div class="imageContent">
		${imageContent.string}
	</div>
	<div style="background: whiteSmoke; border: 1px solid #BFBFBF; padding: 10px;">
		<style type="text/css">
			ul{
				list-style-type: disc !important;
				padding-left: 30px;
			}
		</style>
		${content.string}
	</div>
</div>