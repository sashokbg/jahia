
var confirmSubcribtionTmpl ="<span class='messagePrincipalNewsletter'>Merci, nous avons bien enregistr&eacute; votre abonnement !</span>"
	+"<span>Vous recevrez votre premi&egrave;re lettre d'information d'ici quelques jours ! </span><br>"
	+"<span>Vous pourrez bien &eacute;videmment vous d&eacute;sinscrire &agrave; tout moment.</span>";

var errorSubcriptionTmpl = "Il semble que cette adresse e-mail soit erron&eacute;e.";
var onSubmitSubcriptionTmpl = "<div class='spinnerWait'></div>";

var errorServerTmpl = "Il semble qu\'une erreur se soit produite. " +
						"Merci de retenter l'op\u00E9ration ult\u00E9rieurement.";

$(document).ready(function() {
	$('#newsletterSubscription').submit(function() {
		var email = $('.champMailInscrNewsLetter').val();
		if (isValidEmailAddress(email)) {
			$.ajax({
				type : 'post',
				dataType : 'json',
				url : $('#newsletterSubscription').attr('action'),
				data : {
					emailSubscriber : email
				},
				beforeSend  : function() {
					$('.conteneurBoxInscNewsletter *').hide();
					$('.conteneurBoxInscNewsletter').append(onSubmitSubcriptionTmpl);
				},
				success : function() {
					$('.conteneurBoxInscNewsletter').html(confirmSubcribtionTmpl);
					trackEvent(['Inscription','Newsletter']);
					trackActionButton('M\'inscrire \u00E0 la Newsletter');
				},
				error : function() {
					$('.spinnerWait').remove();
					$('.texte_newsletter').html(errorServerTmpl);
					$('.conteneurBoxInscNewsletter *').show();
				}
			});
		} else {
			$('.champMailInscrNewsLetter').addClass('champMailErreur');
			$('.texte_newsletter').html(errorSubcriptionTmpl);
		}
		return false;
	});
});