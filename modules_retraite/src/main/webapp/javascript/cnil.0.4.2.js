/*!
 * jQuery Cnil plugin 0.4.2
 *
 * Copyright 2015, Pierre Lakreb at AG2R LA MONDIALE (http://www.ag2rlamondiale.fr/)
 * 
 * This plugin was built based on the CNIL javascript provided on :
 * http://www.cnil.fr/vos-obligations/sites-web-cookies-et-autres-traceurs/outils-et-codes-sources/la-mesure-daudience/ 
 * 
 * Date : Tuesday, 19 May 14:30:27 (GMT + 1:00)
 */

(function($, undefined) {	
	
	function Cnilification() {
		this.defaults = {
			consentTemplate : '<p>Ce site utilise Google Analytics. En continuant &agrave; naviguer, vous nous autorisez &agrave; d&eacute;poser \
				des cookies &agrave; des fins de mesure d\'audience.  Pour s\'opposer &agrave; ce d&eacute;p&ocirc;t \
				vous pouvez cliquer <a id="discardCookies" href="#">ici</a>.</p>',
			optInTemplate : '<p>Vous avez accept&eacute; les cookies de mesure d\'audience.</p>',
			optOutTemplate : '<p>Vous vous &ecirc;tes oppos&eacute; au d&eacute;p&ocirc;t de cookies de mesures d\'audience dans votre navigateur.</p>',
			explicitConsentBinders : [],
			discardBinders : [],
			msgEffect : 'slide', // possible effect are "fade", "slide", "hide"
			speedMsgEffect : 'normal', // possible speed effect are "slow", "normal", "fast"
			onConsent : function(event) {}, // used when google analytics cookies are authorized. Use event to determine type of trigger ex.: "event.currentTarget.id".
			onDiscard : function(event) {}, // used when google analytics cookies are discarded. Use event to determine type of trigger ex.: "event.currentTarget.id".
			onImplicitConsent : function() {}, // used when google analytics cookies are authorized by the navigation auto-consent
			onDisplayAskConsent : function() {},
			onDisplayOptout : function() {},
			onDisplayOptin : function() {},
			alwaysDisplayOpt : false,
			retroactivTrackForTrafficSource : true, // feedback bug : traffic sources are lost before first consent cookie has been set
			retroactivTriggerSelector : 'a[href^="/"], a[href*="'+ document.location.host+'"]', // by default all anchor which contains an href attribute value that starts with "/" are considered trigger, since 0.4.2, same domain name link are too  
			pageToRetroTrack : '',
			async : true, // say if Google Analytics instance is asynchronous or not. Default is "true".
			gaqInfoToPush : null // used if google analytics is set synchronously. You can set many configuration inside such as _setDomainName, _setCustomVar, etc. with a multiple push. Note that 'pageToRetroTrack' is also used here. Please see https://developers.google.com/analytics/devguides/collection/gajs/ for explanation.
 		};
		this.cookieTimeout = 34214400000;
	};
	
	$.fn.cnilify = function(options) {
		if(typeof options == 'string')
			return $.cnilification[options].apply();
		else
			$.cnilification.cnilify(options, this);
	};
	
	$.fn.showWithEffect = function(effectType, effectSpeed, callbackFunction) {
			switch (effectType) {
			case "fade":
				this.fadeIn(effectSpeed, callbackFunction);
				break;
				
			case "slide":
				this.slideDown(effectSpeed, callbackFunction);
				break;
				
			case "hide":
				this.show(callbackFunction);
				break;

			default:
				throw new Error('Unable to execute this effect, you must choose between : "fade", "slide", "hide"');
				break;
			}
		
	};
	
	$.fn.hideWithEffect = function(effectType, effectSpeed, callbackFunction) {
		switch (effectType) {
		case "fade":
			this.fadeOut(effectSpeed, callbackFunction);
			break;
			
		case "slide":
			this.slideUp(effectSpeed, callbackFunction);
			break;
			
		case "hide":
			this.hide(callbackFunction);
			break;
			
		default:
			throw new Error('Unable to execute this effect, you must choose between : "fade", "slide", "hide"');
		break;
		}
		
	};
		
	$.extend(Cnilification.prototype, {
		cnilify : function(options, element) {
			// extends default options with parameters'
			this.opts = $.extend(this.defaults, options);
			
			var _opts = this.opts;
			
			// gaAccountID and element are mandatory. Cancel plugin if this parameter is not provided
			if(typeof _opts.gaAccountID === 'undefined' || element.length == 0)
				throw new Error("Unable to launch cnilify plugin. You must pass gaAccountID parameter as option or set to an existing css selector.");
			
			// set the cnil-wrapper tag element by append to body or tag specified in options
			var bannerInitTpl = '<div id="cnil-wrapper"><div id="cnil-content"></div><div id="close-cnil-banner" title="Fermer la banni&egrave;re"></div></div>';
			element.append(bannerInitTpl);
			
			// init disable value
			this.disableStr = 'ga-disable-' + _opts.gaAccountID;
			
			if (document.cookie.indexOf('hasConsent=false') > -1) {
				window[this.disableStr] = true;
			}
			
			this.bindAll();
			
			if (!this.getCookie('hasConsent')) {
				var referrer_host = document.referrer.split('/')[2];
				if (referrer_host != document.location.hostname) {
					window[this.disableStr] = true;
					this.displayMessage(_opts.consentTemplate, _opts.onDisplayAskConsent, true);
				} else {
					_opts.onImplicitConsent();
					this.consent();
				}
			}
		},
		getCookieExpireDate : function () {
			var date = new Date();
			date.setTime(date.getTime() + this.cookieTimeout);
			var expires = "; expires=" + date.toGMTString();
			return expires;
		},
		gaOptout : function(e) {
			var discarded = false;
			
			if(document.cookie.indexOf('hasConsent') == -1 || document.cookie.indexOf('hasConsent=true') > -1 || window[this.disableStr] == false){
				this.opts.onDiscard();
				document.cookie = 'hasConsent=false;' + this.getCookieExpireDate() + ' ; path=/';
				window[this.disableStr] = true;
				this.deleteAnalyticsCookies();
				discarded = true;
			}
			
			if(discarded && e)
				this.opts.onDiscard(e);
			
			return discarded;
		},
		deleteAnalyticsCookies : function() {
			var cookieNames = [ "__utma", "__utmb", "__utmc", "__utmz", "_ga" ];
			for (var i = 0; i < cookieNames.length; i++)
				this.delCookie(cookieNames[i]);
		},
		delCookie : function(name) {
			path = ";path=" + "/";
			domain = ";domain=" + "." + document.location.hostname;
			var expiration = "Thu, 01-Jan-1970 00:00:01 GMT";
			document.cookie = name + "=" + path + domain + ";expires=" + expiration;
		},
		consent : function(e) {
			var consentSet = false;
			
			if(window['ga-disable-' + this.opts.gaAccountID])
				consentSet = true;
				
			document.cookie = 'hasConsent=true; ' + this.getCookieExpireDate()
			+ ' ; path=/'; 
			window['ga-disable-' + this.opts.gaAccountID] = false;
	
			if(consentSet && e)
				this.opts.onConsent(e);
			
			return consentSet;
		},
		displayMessage : function(msg, displayCallback, isAskingConsent) {
			if(typeof displayCallback != "undefined")
				displayCallback.apply(this);
			
			if(isAskingConsent)
				$('#close-cnil-banner').removeClass('opt');
			else
				$('#close-cnil-banner').addClass('opt');
			
			var _opts = this.opts; 
			
			if($('#cnil-content').html() != ''){
				$('#cnil-wrapper').hideWithEffect(_opts.msgEffect, _opts.msgSpeedEffect, function() {
					$('#cnil-content').html(msg);
					$('#cnil-wrapper').showWithEffect(_opts.msgEffect, _opts.msgSpeedEffect);
				});
			} else {
				$('#cnil-wrapper').css('display', 'none');
				$('#cnil-content').html(msg);
				$('#cnil-wrapper').showWithEffect(_opts.msgEffect, _opts.msgSpeedEffect);
			}
			
			this.bindAll();
		},
		getCookie : function(NomDuCookie) {
			if (document.cookie.length > 0) {
				begin = document.cookie.indexOf(NomDuCookie + "=");
				if (begin != -1) {
					begin += NomDuCookie.length + 1;
					end = document.cookie.indexOf(";", begin);
					if (end == -1)
						end = document.cookie.length;
					return unescape(document.cookie.substring(begin, end));
				}
			}
			return null;
		},
		bindConsentCookie : function(binder) {
			var _that = this;
			$(binder).click(function(e) {
				if(!_that.getCookie('hasConsent=true')){
					var consentSet = _that.consent(e);
					if(consentSet || _that.opts.alwaysDisplayOpt) {
						_that.displayMessage(_that.opts.optInTemplate);
					}
				}
				return false;
			});
		},
		bindDiscardCookies : function(binder) {
			var _that = this;
			$(binder).click(function(e) {
				var discarded = _that.gaOptout(e);
				if(discarded || _that.opts.alwaysDisplayOpt) {
					_that.displayMessage(_that.opts.optOutTemplate, _that.opts.onDisplayOptout);
				}
				return false;
			});
		},
		bindAll : function() {
			var _opts = this.opts;
			var _that = this;

			// known binders
			$('#discardCookies').unbind('click');
			this.bindDiscardCookies('#discardCookies');			
			$('#close-cnil-banner').unbind('click');
			$('#close-cnil-banner').bind('click', function() {
				$('#cnil-wrapper').hideWithEffect(_opts.msgEffect, _opts.msgSpeedEffect);
			});
			
			// specific section for retroactiv tracking EVOL#258
			if(_opts.retroactivTrackForTrafficSource && !this.getCookie('hasConsent')){
				var retroactivBinder = function(e){
					if(_that.consent(e)) {
						if(e.target.id != "close-cnil-banner")
							_opts.onImplicitConsent();
						if(!_opts.async){
							_gaq.push(['_setAccount', _opts.gaAccountID]); 
							if(_opts.gaqInfoToPush) {
								$.each(_opts.gaqInfoToPush, function(i, val) {
									_gaq.push(val);
								});
							}
						}
						var tracker = ['_trackPageview'];
						if(_opts.pageToRetroTrack)
							tracker.push(_opts.pageToRetroTrack);
						_gaq.push(tracker);
						var $that = $(this)[0];
						setTimeout(function(){$that.click();}, 300);
						return false;
					}
				};
				if($.fn.on) {
					// new implementation of .on() binding function for jquery 1.7 and higher
					$('body').off('click',_opts.retroactivTriggerSelector + ',#close-cnil-banner:not(.opt)', retroactivBinder);
					$('body').on('click',_opts.retroactivTriggerSelector + ',#close-cnil-banner:not(.opt)', retroactivBinder);
				} else {
					$(_opts.retroactivTriggerSelector + ',#close-cnil-banner:not(.opt)').die('click', retroactivBinder);
					$(_opts.retroactivTriggerSelector + ',#close-cnil-banner:not(.opt)').live('click', retroactivBinder);
				}	
			}
			
			if(_opts.explicitConsentBinders.length > 0) {
				$.each(_opts.explicitConsentBinders, function(i, val) { 
					if($(_opts.explicitConsentBinders[i]).length > 0) {
						$(_opts.explicitConsentBinders[i]).unbind('click');
						_that.bindConsentCookie(_opts.explicitConsentBinders[i]);
					}
				});
			}
			if(_opts.discardBinders.length > 0) {
				$.each(_opts.discardBinders, function(i, val) {
					if($(_opts.discardBinders[i]).length > 0) {
						$(_opts.discardBinders[i]).unbind('click');
						_that.bindDiscardCookies(_opts.discardBinders[i]);
					}
				});
			}
		}
	});
	
	$.cnilification = new Cnilification();
	$.cnilification.version = "0.4.2";
}(jQuery));
