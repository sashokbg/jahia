//onChange=onStateChangeYT;onClose=onCloseYT;onFinish=onFinishedYT;onOpen=onOpenYT

var _pauseFlag = false;
function onStateChangeYT(event) {
	videoarraynum = event.target.id - 1;
	if (event.data == YT.PlayerState.PLAYING) {
		trackEvent([ 'Carrousel', 'Play video', videoArray[videoarraynum] ]);
		_pauseFlag = false;
	}
	
	if (event.data == YT.PlayerState.ENDED) {
		trackEvent([ 'Carrousel', 'Video lue en entier', videoArray[videoarraynum] ]);
	}
	
	if (event.data == YT.PlayerState.PAUSED && _pauseFlag == false) {
		trackEvent([ 'Carrousel', 'Video en pause', videoArray[videoarraynum] ]);
		_pauseFlag = true;
	}
	
	if (event.data == YT.PlayerState.BUFFERING) {
		trackEvent([ 'Carrousel', 'Video en chargement', videoArray[videoarraynum] ]);
	}
	
	if (event.data == YT.PlayerState.CUED) {
		trackEvent([ 'Carrousel', 'Video en rep\u00E9rage', videoArray[videoarraynum] ]);
	}
}

var onCloseYT = function(){
	alert('close');
};

var onFinishedYT = function(){
	alert('finish');
};

var onOpenYT = function() {
	alert('opened');
};