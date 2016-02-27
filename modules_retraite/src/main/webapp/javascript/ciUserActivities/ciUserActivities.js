/*
 * Templates d'affichage des blocs
 */
// Question sans reponse
var questionHtml = "<div class=\"Question bloc ${paireCss} ${last}\">"+
"<div class=\"avatar\">"+
"<a class=\"blocQuestionAvatar\" href=\"${publicProfileUrl}\" title=\"Acc\u00E9der au profil\">"+
"<img alt=\"Photo Avatar\"  height=\"40px\" width=\"40px\"  src=\"${avatar}\" />"+
"</a>"+
"</div>"+
"<div class=\"colonneContenuListeQ\">"+
"<h2><a href=\"${url}\" title=\"\">${title}</a></h2>"+
"<div class=\"infosQuestion\">"+
"<ul>"+
"<li> ${nbOfMemberReplies} message${memberRepliesPlurial} de membre${memberRepliesPlurial}</li>"+
"<li> ${nbOfProfReplies} message${profRepliesPlurial} de professionnel${profRepliesPlurial}</li>"+
"<li class=\"noir\"> ${nbOfViews} vue${viewsPlurial}</li>"+
"<li class=\"last\"> Dernier message ${lastModified}</li>"+
"</ul>"+
"</div>"+
"</div>"+
"</div>";
var questionHtmlTemplate = $.template(null, questionHtml);
var questionHtmlTemplateNoLast = $.template(null, questionHtml);
questionHtmlTemplateNoLast.noLast = true;

//Bloc Questions du badge user
var questionHtmlBadge = "<div class=\"Question bloc ${paireCss} ${last}\">"+
"<div class=\"colonneContenuListeQ\">"+
"<h2><a href=\"${url}\" title=\"\">${shortTitle}</a></h2>"+
"<div class=\"infosQuestion\">"+ 
"<ul>"+
"<li class=\"last\">${createdDateWithHours}</li>"+
"<li class=\"noir\"> ${nbOfViews} vue${viewsPlurial}</li>"+
"<li> ${nbOfReplies} message${repliesPlurial}</li>"+
"</ul>"+
"</div>"+
"</div>"+
"</div>";
var questionHtmlBadgeTemplate = $.template(null, questionHtmlBadge);

// Article
var articleHtml = "<div class=\"dataSection paddingMedium\"><a href=\"${url}\">${title}</a></div>";
var articleHtmlTemplate = $.template(null, articleHtml);

// Breaker
var breakerHtml = "<div class=\"breaker\"></div>";

// Question avec reponse
var questionWithReplyHtml = "<div class=\"globalQuestionReponse bloc ${paireCss} ${last}\" >"+
"<div class=\"uneQuestion\">"+
"<div class=\"avatar\">"+
"<a class=\"blocQuestionAvatar\" href=\"${questionProfileUrl }\" title=\"Acc\u00E9der au profil\">"+
"<img alt=\"Photo Avatar\" height=\"40px\" width=\"40px\"  src=\"${avatar}\" />"+
"</a>"+
"</div>"+
"<div class=\"colonneContenuListeQ blocQuestion\">"+
"<div class=\"questionEntete\">"+
"Question de <span class=\"pseudo\">${pseudo}</span><span class=\"statut\">${status}</span>"+
"</div>"+
"<h2><a href=\"${url}\" title=\"\">${questionTitle}</a></h2>"+
"<div class=\"questionDetail\">"+
"<p class=\"detail\">${questionBody}</p> "+
"</div>"+
"<div class=\"infosQuestion blocQR\">"+
"<ul>"+
"<li class=\"gris heureQ\">${questionDate}</li>"+
"<li class=\"gris nbVue\"> ${nbOfViews} vue${viewsPlurial}</li>"+
"<li class=\"nbMessages\"> ${nbOfMemberReplies} message${memberRepliesPlurial} de membre${memberRepliesPlurial}</li>"+
"<li class=\"nbMessages last\"> ${nbOfProfReplies} message${profRepliesPlurial} de professionnel${profRepliesPlurial}</li>"+
"</ul>"+
"</div>"+
"</div>	"+
"</div>"+
"<div class=\"colonneContenuListeQ blocReponse\">"+
"<h5>Ma reponse</h5>"+
"<p class=\"detail\">${replyBody}</p>"+
"<div class=\"dateHeureReponse\">${replyDate}</div>"+
"</div>"+
"<div class=\"breaker\"></div>"+
"</div>";
var questionWithReplyHtmlTemplate = $.template(null, questionWithReplyHtml);
var questionWithReplyHtmlTemplateNoLast = $.template(null, questionWithReplyHtml);
questionWithReplyHtmlTemplateNoLast.noLast = true;
/*
 * Offset des listes
 */
var offsets = new Array();
var offsetQuestionsIndex = 0;
var offsetArticlesIndex = 1;
var offsetThematicQuestionsIndex = 2;
var offsetUserQuestionsIndex = 3;
var offsetUserRepliesIndex = 4;
var offsetThematicQuestionsIndexBadge = 5;
var offsetUserQuestionsIndexBadge = 6;

offsets[offsetQuestionsIndex] = 0;
offsets[offsetArticlesIndex] = 0;
offsets[offsetThematicQuestionsIndex] = 0;
offsets[offsetUserQuestionsIndex] = 0;
offsets[offsetUserRepliesIndex] = 0;
offsets[offsetThematicQuestionsIndexBadge] = 0;
offsets[offsetUserQuestionsIndexBadge] = 0;

/**
 * Fonctions qui permet d'afficher les elements (questions, articles, reponses...) en mode ajax
 * en se basant sur le flux json en reponse et la template d'affichage
 * action : l'action ajax
 * offsetIndex : index du tableau qui regroupe les offsets
 * parentDiv : div qui contient les elements
 * moreElementLink : lien (voir plus d'élements)
 * template : template d'affichage de l'element
 */
var displayMoreElements = function(action, queryString, offsetIndex, parentDiv, moreElementLink, template, callbackFunction) {
	var params =  "offset="+offsets[offsetIndex];
	if (params) {
		params += "&" + queryString;
	}
	jQuery.ajax({
		type : "post",
		url : action,
		data : params,
		dataType  :"json",
		success : function(response) {
			if (response == null){
				$(parentDiv).prev('.emptyContent').show();
				$(parentDiv).prev('.emptyContent .messageMonProfilVide').show();
				return;
			}
			
			if (! response.elements || response.elements.length == 0){
				$(parentDiv).prev('.emptyContent').show();
				$(parentDiv).prev('.emptyContent .messageMonProfilVide').show();
			}
			
			if (response.elements) {
				$(parentDiv).prev('.emptyContent').hide();
				$(parentDiv).prev('.emptyContent .messageMonProfilVide').hide();
				if(response.elements.length > 0)
					$(parentDiv).children(".bloc:last").removeClass('last');
				for (var i = 0; i < response.elements.length; i++) {
					var element = response.elements[i];
					if (offsets[offsetIndex] != 0 || i != 0) {
						var paireCss = $(parentDiv).children(".bloc:last").hasClass("Paire");
						if (!paireCss) {
							element.paireCss = "Paire";
						}
					}
					
					if((i + 1) == response.elements.length && !template.noLast){
						element.last = "last";
					}
					
					$.tmpl( template, element ).appendTo( parentDiv );
				}
				$(parentDiv).append(breakerHtml);
			}
			
			if (response.offset) {
				offsets[offsetIndex] = response.offset;
			} else {
				$(moreElementLink).parent().hide();
			}
			
			if (response.nbOfElments) {
				$(moreElementLink).children(".nbOfElements").html(response.nbOfElments);
			}
			
			if (callbackFunction) {
				callbackFunction(response);
			}
		}
	});
};
