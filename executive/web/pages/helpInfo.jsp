<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="help.title"/></title>
    <content tag="heading"><fmt:message key="help.heading"/></content>
    <meta name="menu" content="SupportMenu"/>
</head>

<div class="separator"></div>
<SCRIPT LANGUAGE="JavaScript">
	function popupBox(url){
		newWindow = window.open(url,"Demo", "location=0,status=1,width=900,height=740,scrollbars=1,menubar=0,toolbar=0,resizable=1");
		var winl=(screen.width-800)/2;
		var wint=(screen.height-400)/2;
		newWindow.moveTo(winl,wint);
	}
	</SCRIPT>

<ul class="glassList">
<strong>Documents:</strong>
    <li>
        <a href="download.html?type=help&id=user-guide.pdf">User Guide</a>
    </li>
       <li>
        <a href="download.html?type=help&id=annotation-guidelines.pdf">SAM Patents - Annotation Guidelines</a>
    </li>
    </ul>
    <ul class="glassList">
     <strong>Movies (for Annotators):</strong>
       <li>
        <a href="#" onClick='popupBox("docs/annoplayer/Anno.swf")'>Annotation Editor Demo Movie (Flash video)</a>
       </li>
     </ul>
     <ul class="glassList">
      <strong>Movies (for Curators):</strong>
      <li>
        <a href="#" onClick='popupBox("docs/annicplayer/Annic.swf")'>ANNIC Demo Movie (Flash video)</a>
      </li>
     </ul>
     <ul class="glassList">
      <strong>Movies (for Managers):</strong>
      <li>
        <a href="docs/automaticplayer/automatic-annotation-mode.htm" target="_blank">Automatic Annotation Demo Movie (Flash video with audio)</a>
      </li>
      <li>
        <a href="docs/manualplayer/manual-annotation-mode.htm" target="_blank">Manual Annotation Demo Movie (Flash video with audio)</a>
      </li>
     </ul>