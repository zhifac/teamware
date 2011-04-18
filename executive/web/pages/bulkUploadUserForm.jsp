<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="bulkUpload.title"/></title>
<content tag="heading"><fmt:message key="bulkUpload.heading"/></content>
<meta name="menu" content="AdminMenu"/>
</head>
<html:form action="bulkSave" method="post" styleId="bulkUploadUserForm"
    enctype="multipart/form-data" onsubmit="return validateBulkUploadForm(this)">
<input type="hidden" name="encryptPass" value="true"/>

<ul>
    <li class="info">
        <fmt:message key="bulkUpload.message"/>
        <a href="<c:url value="/download.html?type=help&id=template.xls&disableGzip=true"/>"
     onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadTemplate.jsp',this);return false" onmouseout="ajax_hideTooltip()" />
    here
    </a>
    .
    </li>
    <li>
        <executive:label key="uploadForm.file" styleClass="desc"/>
        <html:file property="file" styleClass="file large" styleId="file" />
    </li>
    <li>
        <executive:label key="bulkUploadForm.sendEmail" styleClass="desc"/><input type="checkbox" id="sendEmail" name="sendEmail" value="true" checked="checked"/>
  	</li>
  	    <li>
        <executive:label key="bulkUploadForm.receiveCopy" styleClass="desc"/><input type="checkbox" id="receiveCopy" name="receiveCopy" value="true" checked="checked"/>
  	</li>
    
    <li class="buttonBar">
        <html:submit styleClass="button" onclick="bCancel=false">
            <fmt:message key="button.upload"/>
        </html:submit>
       <input type="button" style="margin-right: 5px"
        onclick="history.go(-1)"
        value="<fmt:message key="button.cancel"/>"/>   	  
    </li>
</ul>
</html:form>

<script type="text/javascript" src="<c:url value="/scripts/validator.jsp"/>"></script>
