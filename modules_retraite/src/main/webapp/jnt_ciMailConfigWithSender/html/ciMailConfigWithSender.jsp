<%--
	@author : lakreb
	@created : 30 oct. 2012
	@Id : N/A
	@description : template du mail avec expediteur

 --%>

<%@include file="../../common/declarations.jspf"%>
<style>
span.mailLegend {
	font-size: 8px; font-weight: bold; color: gray;
}
</style>
<fieldset style="padding: 10px; border: solid 2px lightgreen;">
	<legend style="color: lightgreen;">
		<jcr:nodeProperty name="jcr:title" node="${currentNode}" />
	</legend>
	<span class="mailLegend"> Sujet du mail </span>
	<p>
		<jcr:nodeProperty name="subject" node="${currentNode}" />
	</p>
	<br /> <span class="mailLegend"> Corps du message du destinaire
	</span>
	<p>
		<jcr:nodeProperty name="recipientMailBody" node="${currentNode}" />
	</p>
	<br /> <span class="mailLegend"> Corps du message de
		l'exp&eacute;diteur </span>
	<p>
		<jcr:nodeProperty name="senderMailBody" node="${currentNode}" />
	</p>
</fieldset>
