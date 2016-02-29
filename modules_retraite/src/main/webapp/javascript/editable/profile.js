var editableActionUrl;
var editableLiveActionUrl;
var pseudo;
var userName;
var initEditable = function() {
	$(".editableField").editable(function (value, settings) {
        var submitId = $(this).attr('jcr:id');
        var data = "userName="+userName;
        data += "&propertyKey="+submitId;
        data += "&propertyValue="+value;
        
        if (submitId == "pseudoname") {
        	$.post(editableActionUrl, data, function(response){
            	document.location = document.location.href.replace("="+pseudo, "="+value);
            }, "json");
        } else {
        	$.post(editableActionUrl, data, null, "json");
        }
        return(value);
    }, {
        type    : 'textarea',
        onblur  : 'ignore',
        height : '40px',
        width  : '100%',
        submit  : '<div class="clear"></div><input type="button" style="float:right" value="" class="btEnvoyer" />',
        cancel  : '<input type="button" style="float:right" value="" class="btAnnuler" /><div class="clear"></div>',
        tooltip : 'Cliquer pour \u00E9diter',
        event     : "dblclick"
    });
	
	$("#selectedRubrics, #selectedThematics, #userType").change(function() {
        var submitId = $(this).attr('name');
        var data = "userName="+userName;
        data += "&propertyKey="+submitId;
        data += "&propertyValue="+$(this).val();
        $.post(editableActionUrl, data, null, "json");
	});
	
	$("#radFuturRetraite, #radRetJunior").click(function() {
		$("#userType").val($(this).val()).change();
	});
};

$(document).ready(function() {
	initEditable();
});