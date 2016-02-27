<%--
	@author : lakreb
	@created : 17 oct. 2012
	@Id : US-1200
	@description : Remontee des derniers posts avec en priorite les nons-repondus

 --%>

<%@include file="../../common/declarations.jspf"%>

<div id="patchTools">
	<h2>Lancer un patch JCR manuellement:</h2>
	<br/>
	<b style="color: red;">A utiliser avec pr&eacute;caution !</b>
	<br/>
	<form action="${url.base}${currentNode.path}.patcherAction.do" method="GET">
		<input type="hidden" name="jcrRedirectTo"  value="<c:url value='${url.base}${renderContext.mainResource.node.path}?displayTab=citoolspatches'/>" />
		<select name="number" id="patchesList" onchange="updateDescription(this)">
			<option value="null">Select a patch</option>
		</select>
		<br/>
		<b>Description:</b>
		<div style="margin-top: 5px;" id="patchDescription"></div>
		<br/>
		<b>Patch Version:</b>
		<div style="margin-top: 5px; color:red" id="patchVersion"> </div>
		<br/>
		<b>Execute in:</b>
		<br/>
		<select name="workspace" id="workspace">
			<option value="default">default</option>
			<option value="live">Live</option>
		</select>
		<br/>
		<input type="button" value="Lancer" onclick="executePatch()"/>
		<br/>
		<br/>
		<div id="executing" style="display:none;">
			Execution en cours
		</div>
		<div id="done" style="display:none;">
			DONE
		</div>
	</form>
</div>

<script type="text/javascript">
	function updateDescription(element){
		$('#patchDescription').text($(element).find('option:selected').data("desc"));
		$('#patchVersion').text($(element).find('option:selected').data("version"));
	}

	function executePatch(){
		$('#executing').show();
		$.ajax({
			dataType: "json",
			url: "${url.base}${currentNode.path}.patcherAction.do",
			data: { number: $('select#patchesList').val(),
					workspace: $('select#workspace').val()
			}
		}).done(function(result) {
			if(result.result==true){
				$('#done').css("color","green");
				$('#done').text("DONE - OK");
			}
			else{
				$('#done').css("color","red");
				$('#done').text("DONE - PROBLEM");
			}
			$('#done').show();
			$('#executing').hide();
		});
	}
	
	$(document).ready(function(){
		$.ajax({
			dataType: "json",
			url: "${url.base}${currentNode.path}.patcherAction.do",
			data: { listPatches: "true"}
		}).done(function(result) {
			console.log(result.patches);
			$.each(result.patches, function(i, obj) {
				console.log(obj);
			     $('#patchesList')
			         .append($("<option></option>")
			         .attr("value",i)
			         .attr("data-desc",obj.description)
			         .attr("data-version",obj.version)
			         .text(obj.name));
			});
		});
	});
</script>