function trim (myString)
{
	if (myString == null) {return "";}
	return myString.replace(/^\s+/g,'').replace(/\s+$/g,'');
} 

function openPreview() {
	var text = trim($("#answerBody").val());
	if (text == "Je r\u00E9ponds" || text == "" || text == "Veuillez saisir une r\u00E9ponse") {
		$("#answerBody").addClass("enErreur");
		$("#answerBody").val("Veuillez saisir une r\u00E9ponse");
	} else {
		$("#answerBody").removeClass("enErreur");
		$("#previewAnswerBody").html(formatHtmlPost($("#answerBody").val()));
		$(".answerToQuestion").hide();
		$(".PreviwAnswer").show();
	}
}

function closePreview() {
	$(".answerToQuestion").show();
	$(".PreviwAnswer").hide();
}

function formatHtmlPost(text) {
	text = text.replace(/\n/g, "<br>");
	text = text.replace(" \t ", '<div class="citationContainer">');
	text = text.replace(" \t\t ", '</div>');
	return text;
}

//pas besoin d'utiliser
//utilizer la balise html <pre>...</pre>
function setupPostBr() {
	$(".postBody").each(function(item) {
		var text = $(this).html();
		$(this).html(formatHtmlPost(text));
	});
}


function fitToContent(id)
{
   var text = id && id.style ? id : document.getElementById(id);
   if ( !text )
      return;

   var adjustedHeight = text.clientHeight;
   adjustedHeight = Math.max(text.scrollHeight, adjustedHeight);
   if ( adjustedHeight > text.clientHeight ) {
      text.style.height = adjustedHeight + "px";
   } else {
	   text.style.height = text.scrollHeight;
   }
}
 
function editPost(edit) {
	$("#"+edit).dblclick();
	var textarea = $("#"+edit).children().children(":input[name='value']");
	textarea.keyup(function() {fitToContent(this);});
	setTimeout (function (){textarea.keyup();textarea.focus();}, 150);
	$("#content-to-"+edit).hide();
	$("#"+edit).show();
}

function reply() {
	$("#answerBody").focus();
	$("#answerBody").click();
}

function replyWithBlockote(pseudo, postId) {
	var post = $("#"+postId);
	post.children('.citationContainer').remove();
	post.children('a').each(function() {
		var url = $(this).attr('href'); 
		$(this).replaceWith(url);
	});
	post.children('br').replaceWith('\r\n');
	var index = post.html().indexOf(' \t\t');
	if (index != -1) {
		post.html(post.html().substring(index).replace(' \t\t', '').replace('\r\n', ''));
	}
	$("#answerBody").val(" \t " +pseudo + " a \u00E9crit :"+ "\r\n" + post.text() + " \t\t \r\n");
	setTimeout (function (){
		$("#answerBody").keyup();
		$("#answerBody").focus();
		var val = $("#answerBody").val();
		$("#answerBody").val('');
		$("#answerBody").val(val);
	}, 300);
}

function escapeAnswerBody() {
	 return true;
}

function cancelEditPost() {
	$(".postBody, .questionTitre").show();
	$(".editablePost").hide();
}

function saveReply() {
	var text = trim($("#answerBody").val());
	if (text == "Je r\u00E9ponds" || text == "" || text == "Veuillez saisir une r\u00E9ponse") {
		$("#answerBody").addClass("enErreur");
		$("#answerBody").val("Veuillez saisir une r\u00E9ponse");
	} else {
		$("#answerBody").removeClass("enErreur");
		$(".btEnvoyer").attr('disabled', 'disabled');
		$("#answerForm").submit();
	}
}

function setupRateReplyButton(cookiName) {
	var node = $("#"+cookiName);
	if ($.cookie(cookiName)) {
		node.addClass("plusUnContainerValide");
		node.removeClass("plusUnContainer");
		node.children(".bulleRepondre").remove();
		node.children("form").children("input:button").attr("onclick", "");
	}
}

function rateReply(cookieName) {
	$.cookie(cookieName, "rated");
}

var initQuestionPage = function (){
	$("#answerBody").keyup(function() {fitToContent(this);});
	closePreview();
//    setupPostBr();
    $(".editablePost").hide();
    $(".hidden").hide();

    $(".editablePost").editable(function (value, settings) {
        var data = {'jcrMethodToCall':'put'};
        var submitId = $(this).attr('jcr:id');
        data[submitId] = value;
        if (submitId == 'title') {
        	$.post($(this).attr('jcr:url'), submitId+"="+value, function(data){
        		if(data.reload)
        			location.reload(true);
        	}, "json");
        } else {
        	$.post($(this).attr('jcr:url'), data, null, "json");
        }
        var id = $(this).attr('id');
        $("#content-to-"+id).show();
        $("#content-to-"+id).html(value);
        $("#content-to-"+id).html(formatHtmlPost($("#content-to-"+id).html()));
        $("#"+id).hide();
        $(".hidden").hide();

        return(value);
    }, {
        type    : 'textarea',
        cssclass : 'maReponseSaisie',
        width	: '436px',
        onblur  : 'ignore',
        height : 'auto',
        submit  : '<input type="submit" style="float:right" value="" class="btEnvoyer" />',
        cancel  : '<input type="button" style="float:right" value="" class="btAnnuler" onclick="cancelEditPost();" /><div class="clear"></div>',
        tooltip : 'Cliquer pour \u00E9diter',
        event     : "dblclick"
    });
    
};

$(document).ready(function() {
	initQuestionPage();
});