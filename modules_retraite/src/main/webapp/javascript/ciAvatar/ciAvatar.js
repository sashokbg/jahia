/**
 * @author : lakreb
 * @created : 20 juil. 2012
 * @description : user load and crop avatar capabilities.
 * 
 */

var debug = false;
var jCropZoneMaxWidth = 618;
var imgFrameSize = 52;
var imgFrameSelector = '.avatarFrame';
var imgSelector = '.avatarFrame img';
 
$(document).ready(function() {
	var template = '<div class="qq-uploader">' + 
				        '<ul class="qq-upload-list"></ul>' + 
				      '</div>';
	var inputFuName = 'croppedImage';
	
	new qq.FileUploader({
	    element: $('#file-uploader')[0],
	    action: urlAction,
	    params: {
	    	avatarAction: "set"
	    },
	    button: $('#file-uploader-button')[0],
	    multiple: false, 
	    sizeLimit: avatarMaxSize,
	    allowedExtensions: ['jpg', 'jpeg', 'png', 'gif', 'tif'],
	    onComplete: function(id, fileName, responseJSON){
	    	var json = eval(responseJSON);
	    	if(json.success){
	    		$('.qq-upload-delete').show();
	    		setImageToCroppingZone();
	    	}
	    },
	    errorInHtmlBody: true,
	    template: template,
	    // template for one item in file list
	    fileTemplate: '<li>' +
		                '<span class="qq-upload-spinner"></span>' +
		                '<span class="qq-upload-finished"></span>' +
		                '<span class="qq-upload-failed-icon"></span>' +
		                '<span class="qq-upload-file"></span>' +
		                '<span class="qq-upload-size"></span>' +
		                '<a class="qq-upload-cancel" href="#">Annuler</a>' +
		                '<a class="qq-upload-delete" href="#">Annuler</a>' +
		                '<span class="qq-upload-failed-text">\u00C9chec</span>' +
			          '</li>',
        activeDnd: false,
	    onSubmit: function(id, fileName){
	    	$('.qq-upload-list').empty();
	    },
	    onUpload: function(id, fileName, xhr){
	    	$('.qq-upload-delete').hide();
	    },
	    onValidate: function(id, fileName){
	    	$('#cropppingZone').empty();
	    	$('.qq-upload-list').empty();
	    	$('.messageCrop').hide();
    		$('#cropppingZone').hide();
	    },
	    inputName: inputFuName,
	    name: inputFuName,
	    messages: {
            typeError: "{file} n'est pas valide. Seuls les fichiers {extensions} sont autoris\u00E9s.",
            sizeError: "{file} est trop volumineux, la taille maximum du fichier est de {sizeLimit}.",
            minSizeError: "{file} n'est pas assez grand, la taille minimum du fichier est {minSizeLimit}.",
            emptyError: "{file} est vide, refaites une s\u00E9lection sans cette derni\u00E8re.",
            onLeave: "Le fichier est en cours de t\u00E9l\u00E9chargement, si vous quittez maintenant, leur chargement sera annul\u00E9."            
        }
	});
	
	initDeleteButton();
	
});

function setImageToCroppingZone() {
	$('#cropppingZone').empty();
	var urlGetAvatar = urlAction + "?avatarAction=get&timestamp=" + new Date().getTime();
	$('.messageCrop').show();
	$('#cropppingZone').show();
	$('#cropppingZone').append('<img style="max-width: ' + jCropZoneMaxWidth + 'px;" id="jcrop_target" alt="Avatar" src="' + urlGetAvatar + '" />');
	$('#cropppingZone').append('<input type="hidden" name="cropFrameMaxSize" value="' + jCropZoneMaxWidth + '" />');
	$('#cropppingZone').append('<input type="hidden" name="avatarRequested" value="true" />');
	$('#cropppingZone').append('<input id="cropCoords" type="hidden" value="" name="cropCoords" />');
	$('#jcrop_target')[0].onload = attachJcrop;
	$('.qq-upload-delete').click(initAvatar);
}

var initDeleteButton = function() {
	if(showDeleteButton){
		$('.qq-upload-list').append('<li><a id="qq-upload-remove" class="qq-upload-cancel" href="#">Supprimer</a></li>');
		$('#qq-upload-remove').toggle(function() {
			$('#cropppingZone').append('<input type="hidden" name="removeAvatar" value="true" />');
			$('#qq-upload-remove').text('Annuler la suppression');
			$('#qq-upload-remove').before('<span class="qq-upload-failed-icon"></span>');
			$('.qq-upload-failed-icon').css('display', 'inline-block');
			$('#file-uploader-button').hide();
			$(imgSelector).attr('src', avatarRemoved);
		}, function() {
			$('#qq-upload-remove').remove();
			$('#file-uploader-button').show();
			initAvatar();
		});
	}
};

var initAvatar = function() {
	$(imgSelector).css('position','inherit');
	$(imgSelector).width(imgFrameSize + 'px');
	$(imgSelector).height(imgFrameSize + 'px');
	$(imgSelector).css('top', '0');
	$(imgSelector).css('left', '0');
	$(imgSelector).attr('src', avatarSrc);
	$('#cropppingZone').empty();
	$('.qq-upload-list').empty();
	if(showDeleteButton) {
		initDeleteButton();
	}
	$('.qq-upload-list').show();
	$('.messageCrop').hide();
	$('#cropppingZone').hide();
	return false;
};

function attachJcrop() {
	var imgWidth = $('#jcrop_target').width();
	var imgHeight = $('#jcrop_target').height();
	var imgSize = imgWidth < imgHeight ? imgWidth : imgHeight;
	var intS = imgSize;
	var intT = 0;
	var intL = 0;
	$(this).Jcrop({
		aspectRatio : 1,
		minSize : [25,25],
		onSelect:    function(c) {
			updateCoords(c);
			updateAvatar(c);
		},
		onChange:    updateAvatar,
		onRelease:   function(c) {
			updateCoords(c);
			updateAvatar(c);
		},
		setSelect:   [intT,intL,intS,intS],
		allowSelect: false
	});
}

function updateCoords(c) {
	if(c)
		$('#cropCoords').val(c.x + "," + c.y + "," + c.w + "," + c.h);
	else
		$('#cropCoords').val("");
}

function updateAvatar(c) {
	var src = $('.jcrop-holder img').attr('src');
	var srcThumb = $(imgSelector).attr('src');
	if(c){
		$(imgFrameSelector).css('position','relative');
		$(imgFrameSelector).css('overflow','hidden');
		
		if(srcThumb == avatarSrc || src != srcThumb)				
			$(imgSelector).attr('src', src);
		
		var imgW = $('.jcrop-holder img').width();
		var imgH = $('.jcrop-holder img').height();
		
		$(imgSelector).css('position','absolute');
		
		// calculating image size and position data from jCrop information
		var imgNewW = Math.round(imgFrameSize * imgW / c.w); 
		var imgNewH = Math.round(imgFrameSize * imgH / c.h);
		var imgLeft = Math.round(c.x * imgNewW / imgW);
		var imgTop 	= Math.round(c.y * imgNewH / imgH);
		
		// affecting data to image object 
		$(imgSelector).css('left', '-' + imgLeft + 'px');
		$(imgSelector).css('top', '-' + imgTop + 'px');
		$(imgSelector).css('width', imgNewW + 'px');
		$(imgSelector).css('height', imgNewH + 'px');
		
		if(debug){
			$('#file-uploader').empty();
			$('#file-uploader').append(template);
			$('.qq-upload-list').append('<li> -' + imgLeft + '; -' + imgTop + ' / ' + imgNewW + ' x ' + imgNewH + '</li>');
		}
	}else{
		$(imgSelector).css('position','inherit');
		$(imgSelector).width(imgFrameSize + 'px');
		$(imgSelector).height(imgFrameSize + 'px');
		$(imgSelector).css('top', '0');
		$(imgSelector).css('left', '0');
		$(imgSelector).attr('src', src);
	}
}