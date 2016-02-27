
var profileQuestionHtml = "<div class=\"Question bloc ${paireCss}\">"+
"<div class=\"avatar\">"+
"<a class=\"blocQuestionAvatar\" href=\"#\" title=\"Acceder au profil\">"+
"<img alt=\"Photo Avatar\"  height=\"40px\" width=\"40px\" src=\"${avatar}\" />"+
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
var profileQuestionHtmlTemplate = $.template(null, profileQuestionHtml);


//Question avec reponse
var profileQuestionWithReplyHtml = "<div class=\"globalQuestionReponse bloc ${paireCss}\" >"+
"<div class=\"uneQuestion\">"+
"<div class=\"avatar\">"+
"<a class=\"blocQuestionAvatar\" href=\"#\" title=\"Acceder au profil\">"+
"<img alt=\"Photo Avatar\"  height=\"40px\" width=\"40px\" src=\"${avatar}\" />"+
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
var profileQuestionWithReplyHtmlTemplate = $.template(null, profileQuestionWithReplyHtml);
