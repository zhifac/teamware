/*
Created By: Helge Tesgaard
Date: 8/7/2006

Inspired by Lightbox-gone-wild found at http://particletree.com/features/lightbox-gone-wild/
Which again is inspired by the lightbox implementation found at http://www.huddletogether.com/projects/lightbox/
*/

/*-------------------------------GLOBAL VARIABLES------------------------------------*/

var detectDwrUpl = navigator.userAgent.toLowerCase();
var OSDwrUpl,browserDwrUpl,versionDwrUpl,totalDwrUpl,thestringDwrUpl;

/*-----------------------------------------------------------------------------------------------*/

//Browser detect script origionally created by Peter Paul Koch at http://www.quirksmode.org/

function getBrowserInfoDwrUpl() {
	if (checkIt('konqueror')) {
		browserDwrUpl = "Konqueror";
		OSDwrUpl = "Linux";
	}
	else if (checkIt('safari')) browserDwrUpl 	= "Safari"
	else if (checkIt('omniweb')) browserDwrUpl 	= "OmniWeb"
	else if (checkIt('opera')) browserDwrUpl 		= "Opera"
	else if (checkIt('webtv')) browserDwrUpl 		= "WebTV";
	else if (checkIt('icab')) browserDwrUpl 		= "iCab"
	else if (checkIt('msie')) browserDwrUpl 		= "Internet Explorer"
	else if (!checkIt('compatible')) {
		browserDwrUpl = "Netscape Navigator"
		versionDwrUpl = detectDwrUpl.charAt(8);
	}
	else browserDwrUpl = "An unknown browser";

	if (!versionDwrUpl) versionDwrUpl = detectDwrUpl.charAt(place + thestringDwrUpl.length);

	if (!OSDwrUpl) {
		if (checkIt('linux')) OSDwrUpl 		= "Linux";
		else if (checkIt('x11')) OSDwrUpl 	= "Unix";
		else if (checkIt('mac')) OSDwrUpl 	= "Mac"
		else if (checkIt('win')) OSDwrUpl 	= "Windows"
		else OSDwrUpl 								= "an unknown operating system";
	}
}

function checkIt(string) {
	place = detectDwrUpl.indexOf(string) + 1;
	thestringDwrUpl = string;
	return place;
}

/*-----------------------------------------------------------------------------------------------*/
// Onload, make all links that need to trigger a LbDwrUpl active

function initializeDwrUpl(){
    addLightboxMarkup();
    /*
    lbox = document.getElementsByClassName('lbOn');
	for(i = 0; i < lbox.length; i++) {
		valid = new LbDwrUpl(lbox[i]);
	}
	*/
}


// Add in markup necessary to make this work. Basically two divs:
// Overlay holds the shadow
// LbDwrUpl is the centered square that the content is put into.
function addLightboxMarkup() {
	bod 				= document.getElementsByTagName('body')[0];
	overlay 			= document.createElement('div');
	overlay.id		= 'overlay';
	lb					= document.createElement('div');
	lb.id				= 'lightbox';
	lb.className 	= 'loading';
	lb.innerHTML	= '<div id="lbLoadMessage">' +
						  '<p>Content missing</p>' +
					  '</div>';
	bod.appendChild(overlay);
	bod.appendChild(lb);
}


Event.observe(window, 'load', initializeDwrUpl, false);
Event.observe(window, 'load', getBrowserInfoDwrUpl, false);
Event.observe(window, 'unload', Event.unloadCache, false);

/*-----------------------------------------------------------------------------------------------*/


var LightboxDwrUpl = Class.create();
LightboxDwrUpl.prototype = {

	yPos : 0,
	xPos : 0,
    initialized: false,

    //Copies the innerHTML from the lbContent argument to lightbox as content
    initialize: function(lbContent) {
        var lb = $('lightbox');
        if(lb) {
            lb.innerHTML = '<div id="lbLoadMessage">' + lbContent.innerHTML + '</div>';
            lbContent.innerHTML = ""; //Clear the seeding div
            this.initialized = true;
        }
    },

	// Turn everything on - mainly the IE fixes
	activate: function(){
		if (browserDwrUpl == 'Internet Explorer'){
			this.getScroll();
			this.prepareIE('100%', 'hidden');
			this.setScroll(0,0);
			this.hideSelects('hidden');
		}
		this.displayLightbox("block");
	},

	// Ie requires height to 100% and overflow hidden or else you can scroll down past the lightbox
	prepareIE: function(height, overflow){
		bod = document.getElementsByTagName('body')[0];
		bod.style.height = height;
		bod.style.overflow = overflow;

		htm = document.getElementsByTagName('html')[0];
		htm.style.height = height;
		htm.style.overflow = overflow;
	},

	// In IE, select elements hover on top of the lightbox
	hideSelects: function(visibility){
		selects = document.getElementsByTagName('select');
		for(i = 0; i < selects.length; i++) {
			selects[i].style.visibility = visibility;
		}
	},

	// Taken from lightbox implementation found at http://www.huddletogether.com/projects/lightbox/
	getScroll: function(){
		if (self.pageYOffset) {
			this.yPos = self.pageYOffset;
		} else if (document.documentElement && document.documentElement.scrollTop){
			this.yPos = document.documentElement.scrollTop;
		} else if (document.body) {
			this.yPos = document.body.scrollTop;
		}
	},

	setScroll: function(x, y){
		window.scrollTo(x, y);
	},


    displayLightbox: function(display){
		$('overlay').style.display = display;
		$('lightbox').style.display = display;
		//if(display != 'none') this.loadInfo();
	},

	// Example of creating your own functionality once lightbox is initiated
	deactivate: function(){
		Element.remove($('lbContent'));

		if (browserDwrUpl == "Internet Explorer"){
			this.setScroll(0,this.yPos);
			this.prepareIE("auto", "auto");
			this.hideSelects("visible");
		}

		this.displayLightbox("none");
	}
}
/*-----------------------------------------------------------------------------------------------*/
