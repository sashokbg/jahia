/*
	@author : Sylvain Pichard
	@created : 22 octobre 2012
	@Id : desinscription validator
	@description : script javascript qui permet de valider le formulaire de desinscription et de l'envoyer
*/


$(document).ready(function() {

	$('#ciUnsubscribeNewsletterForm').submit(function() {
		var email = $('#emailSubscriber').val();
		if (! isValidEmailAddress(email)) {
			$('.champMailDesinscNews').addClass('messageErreur');
			$(".messageId-KO").show();	
			return false;
		}		
	});
});