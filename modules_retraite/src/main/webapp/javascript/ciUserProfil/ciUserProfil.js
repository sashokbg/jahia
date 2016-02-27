var thematicHtmlBloc ="<div id=\"div_${identifier}\" class=\"conteneurThematiqueChoisie fl\">"+
"<span class=\"libelleThematique fl\">${title}</span><a onclick=\"removeThematic('${identifier}','${title}')\" class=\"lienRemoveThematique fl\"></a>"+
"<div class=\"clear\"></div>"+
"</div>";

var thematicTemplate = $.template(null, thematicHtmlBloc);

var selectThematic = function(identifier, isInit) {
	if (identifier == "") {
		return;
	}
	var selectedThematics = $("#selectedThematics").val();
	if (selectedThematics == "") {
		$("#selectedThematics").val(identifier);
	} else if (!isInit && selectedThematics.indexOf(identifier) != -1) {
		return;
	} else {
		$("#selectedThematics").val(selectedThematics+","+identifier);
	}
	$("#selectedThematics").change();
	var title= $("#selectThematics").children("option:selected").html();
	var element = {"title" : title, "identifier" : identifier};
	$.tmpl( thematicTemplate, element ).appendTo( $("#sousThematiquesChoisies") );
};

var selectRubric = function(identifier, isInit) {
	if (identifier == "") {
		return;
	}
	var selectedRubrics = $("#selectedRubrics").val();
	if (selectedRubrics == "") {
		$("#selectedRubrics").val(identifier);
	} else if (!isInit && selectedRubrics.indexOf(identifier) != -1) {
		return;
	} else {
		$("#selectedRubrics").val(selectedRubrics+","+identifier);
	}
	$("#selectedRubrics").change();	
	var title= $("#selectRubrics").children("option:selected").html();
	var element = {"title" : title, "identifier" : identifier};
	$.tmpl( thematicTemplate, element ).appendTo( $("#thematiquesChoisies") );
};

var loadThematicsAction = null;

var loadThematics = function(pagePath) {
	if (pagePath == "") {
		return;
	}
	$("#selectThematics").html("");
	var queryString = "rubricPath="+pagePath;
	jQuery.ajax({
		type : "post",
		url : loadThematicsAction,
		data : queryString,
		dataType  :"json",
		success : function(obj) {
			if (obj == null)
				return;
			if (obj.thematics) {
				$("#selectThematics").append("<option value=\"\">Selectionner</option>");
				for (var i = 0; i < obj.thematics.length; i++) {
					var thematic = obj.thematics[i];
					$("#selectThematics").append('<option value="'+thematic.identifier+'">'+thematic.name+'</option>');
				}
			} else {
				alert("Erreur lors de la r\u00E9cup\u00E9ration des pages th\u00E9matiques.");
			}
		}
	});
};

var switchUserClassAction = null;

var switchUserClass = function(username, targetClass, redirect) {
	jQuery.ajax({
		type : "post",
		url : switchUserClassAction,
		data : "name="+username+"&targetClass="+targetClass,
		dataType  :"json",
		success : function(obj) {
			if (obj == null)
				return;
			if (obj.ok) {
				if (obj.redirect)
					document.location = obj.redirect;
			} else {
				alert("Vous avez pas les droits : redirection 404");
			}
		}
	});
};

var removeThematic = function(identifier,title) {
	var selectedThematics = $("#selectedThematics").val();
	if (selectedThematics.indexOf(identifier) != -1) {
		selectedThematics = selectedThematics.replace(identifier+",", "");
		selectedThematics = selectedThematics.replace(","+identifier, "");
		selectedThematics = selectedThematics.replace(identifier, "");
		if (selectedThematics == ",") {
			selectedThematics = "";
		}
		$("#selectedThematics").val(selectedThematics);
		$("#selectedThematics").change();
		$("#div_"+identifier).remove();
		events.push(['Espace membre','Supprimer thematique expert',title]);
		return;
	}
	var selectedRubrics = $("#selectedRubrics").val();
	if (selectedRubrics.indexOf(identifier) != -1) {
		selectedRubrics = selectedRubrics.replace(identifier+",", "");
		selectedRubrics = selectedRubrics.replace(","+identifier, "");
		selectedRubrics = selectedRubrics.replace(identifier, "");
		if (selectedRubrics == ",") {
			selectedRubrics = "";
		}
		$("#selectedRubrics").val(selectedRubrics);
		$("#selectedRubrics").change();	
		$("#div_"+identifier).remove();
		events.push(['Espace membre','Supprimer theme prefere',title]);
	}
};

var initSituationRadioButtons = function() {
	$("#radFuturRetraite").click(function() {
		$(".rjNode").hide();
		$(".frNode").show();
	});
	
	$("#radRetJunior").click(function() {
		$(".rjNode").show();
		$(".frNode").hide();
	});
	if ($("#radRetJunior")[0].checked) {
		$("#radRetJunior").click();
	}
	if ($("#radFuturRetraite")[0].checked) {
		$("#radFuturRetraite").click();
	}
};

var initSelectedCountry = function() {
	$("#country").val($("#selectedCountry").val());
	$("#country").change();
};

function alphaNumOnly(e) {
	var key;
	var keychar;

	if (window.event)
	   key = window.event.keyCode;
	else if (e)
	   key = e.which;
	else
	   return true;
	keychar = String.fromCharCode(key);
	keychar = keychar.toLowerCase();

	// control keys
	if ((key==null) || (key==0) || (key==8) || 
	    (key==9) || (key==13) || (key==27) )
	   return true;
	
	// alphas and numbers
	else if ((("abcdefghijklmnopqrstuvwxyz0123456789").indexOf(keychar) > -1))
	   return true;
	else
	   return false;

}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}