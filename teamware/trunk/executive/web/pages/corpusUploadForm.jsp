<%@ include file="/common/taglibs.jsp" %>
<head>
<title><fmt:message key="corpusDetail.title"/></title>
<!-- dwr-upload CSS -->
<link rel="stylesheet" href="<c:url value='/styles/dwrupload/lightboxDwrUpload.css' />" media="screen,projection" type="text/css" />
<link rel="stylesheet" href="<c:url value='/styles/dwrupload/dwrUpload.css' />" media="screen,projection" type="text/css" />
<script type="text/javascript" src="<c:url value='/dwr/interface/UploadMonitor.js'/>"></script><%-- The methds is implemented directly in dwr-upload.js. Don't know why --%>
<script type="text/javascript" src="<c:url value='/dwr/interface/DSMonitor.js'/>"></script><%-- The methds is implemented directly in dwr-upload.js. Don't know why --%>
<script type="text/javascript" src="<c:url value='/scripts/dwrupload/dwrUpload.js'/>"></script>


<style type="text/css">
  #progressBar { padding-top: 5px; }
  #progressBarBox { width: 350px; height: 20px; border: 1px inset; background: #eee;}
  #progressBarBoxContent { width: 0; height: 20px; border: 1px solid #444; background: navy; }
  #progressDSBar { padding-top: 5px; }
  #progressDSBarBox { width: 350px; height: 20px; border: 1px inset; background: #eee;}
  #progressDSBarBoxContent { width: 0; height: 20px; border: 1px solid #444; background: red; }
  #theMeter {width: 350px;}
  #theDSMeter {width: 350px;}
</style>
<content tag="heading"><fmt:message key="upload.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<fmt:message key="upload.message"/>


<%--  The dynamicJavascript and staticJavascript attributes default to true, but if dynamicJavascript is set to true  and staticJavascript is set to false then only the dynamic JavaScript will be rendered. If dynamicJavascript is set to false  and staticJavascript is set to true then only the static JavaScript will be rendered which can then be put in separate JSP page so the browser can cache the static JavaScript. From: http://struts.apache.org/userGuide/struts-html.html#javascript --%>
<html:javascript formName="corpusUploadForm" cdata="false"
                 dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<html:rewrite page="/scripts/validator.jsp"/>"></script>

<script type="text/javascript">
  dwrUploadPrepareMessage ='Preparing...';
  dwrUploadTransferMessage = 'Uploading archive ...';
  dwrUploadCompletedMessage = 'Upload is finished';
  dwrPopulationPrepareMessage ='Preparing...';
  dwrPopulationProcessingMessage = 'Processing population of the corpus...';
  dwrPopulationCompletedMessage = 'Population is completed';
  

  function upload(form) {	  
    if (validateCorpusUploadForm(form)) {
      //Validation succeded or bCancel=true
      var hasfiles = ($('file').value).length >= 1;
	  var hasencoding = ($('encoding').value).length >0;
	  var filee =$('file').value;
	  var encoding=$('encoding').value;

      /*If saveUpdate button used and files exsists */
      if(hasfiles&&hasencoding) {
        //Dont show lightbox if delete button is used
        if(filee.substring(filee.length-4,filee.length)=='.zip'){
        	    //dwr.engine.setActiveReverseAjax(true);
          		startProgress(); //Start the progressbar
          		//showProgressAnimation(); //Start the progressbar animation graphic
        }else{
			alert("Please select a valid Zip File");
			return false;
	    }
      }
 
      return true;
    }
    //Validation failed
    return false;
  }
  
  function populateDS(form) {
     if(upload(form)){
          //startDSProgress(); //Start the docservice progressbar
          //showDSProgressAnimation(); //Start the docservice progressbar animation graphic
     return true;   	
     }
        	
     else {
      return false;
     }
  }
  function checkSC(form){
	var swith=document.getElementById("switch").value;
	if(swith=="true"){
		return populateDS(form);
	}else {
		return false;
	}
  }
  function uncheckAlert(){
	 
	  var markupAwareChecked=document.getElementById("markupAware").checked;
	  if(!markupAwareChecked) {
		  if(!confirmDialog("Warning: All annotations contained in your documents will be deleted. Do you want to proceed?")){
			  document.getElementById("markupAware").checked= true;
		  }	  
	  }
  }	


</script>

<p>

  <html:form action="saveUploadCorpus.html" enctype="multipart/form-data" styleId="corpusUploadForm" method="post" onsubmit="return checkSC(this)">
  
  <%-- TODO note: trick to ensure default submit action is not the first of the delete buttons. Details see: HowTo set the default submit button on a form http://raibledesigns.com/page/rd?entry=howto_set_the_default_submit --%>
  <input type="submit" style="display: none"/>
  <input type="hidden" id="switch" name="switch" value="true"/>
  <input type="hidden" id="popup" name="popup" value="<c:out value="${param.popup}"/>"/>
  <ul>
     
     
     <li>
        <executive:label styleClass="desc" key="corpusForm.corpusName"/>
        <html:errors property="corpusName"/>
        <html:text property="corpusName" styleId="corpusName" styleClass="text medium" />
    </li>

    <html:hidden property="corpusID"/>
  	<li>
 		<executive:label key="fileUploadForm.encoding" styleClass="desc"/>
 		<html:errors property="encoding"/>
  		<html:text property="encoding" styleId="encoding" styleClass="text medium" value="UTF-8"/>
		
  	</li>
  	<li>
  		<executive:label key="fileUploadForm.file1" styleClass="desc"/>
  		<html:errors property="file"/>
  		<nested:file styleId="file" property="file" styleClass="default"/><br/>
  	</li>
      <li>
 		<b><executive:label key="fileUploadForm.unpackmarkup"/></b>
  		<input type="checkbox" id="markupAware" name="markupAware" value="true" checked="checked" onClick="uncheckAlert()"/>
  	     <img src="<c:url value="/images/iconHelp.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/unpackMarkup.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
  	</li>
  </ul>
  <ul>
	<li>
  		<nested:submit property="method.save" styleId="uploadbutton">
    		<fmt:message key="button.upload"/>
  		</nested:submit>
  		<c:if test="${param.popup!='true'}">
  		<input type="button" onclick="location.href='<c:url value="/corpora.html"/>'"
        	value="<fmt:message key="button.cancel"/>"/>
        </c:if>	
    </li>
  </ul>
</p>

</html:form>
<table>
   <div id="progressBar" style="display: none;">
            <div id="theMeter">
                <div id="progressBarText"></div>

                <div id="progressBarBox">
                    <div id="progressBarBoxContent"></div>
                </div>
            </div>
        </div>
        
       <div id="progressDSBar" style="display: none;">

            <div id="theDSMeter">
                <div id="progressDSBarText"></div>

                <div id="progressDSBarBoxContent"></div>
                </div>
            </div>
        </div>
<span id="progressMsg" style="width: 350px; display:none; margin-left: 3px;">
  <img id="uploadindicator" src="<c:url value='/images/ajax/indicator.gif'/>" alt="Indicator" />
</span>
</table>