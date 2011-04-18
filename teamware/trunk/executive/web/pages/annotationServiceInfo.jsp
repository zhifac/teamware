<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="annotationService.info.title"/></title>
<content tag="heading"><fmt:message key="annotationService.info.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<div class="separator"></div>

<table class="detail" cellpadding="5">

   
    <tr>
<td></td>
        <td class="buttonBar">
            <c:choose> 
            <c:when test="${not empty param.popup}">
             <input type="button" class="button" name="close" value="Close"
                onclick="closeWindowAndRefreshParent()" />
            </c:when>
            <c:otherwise> 
            <input type="button" class="button" name="done" value="<fmt:message key='annotationService.info.button'/>"
                onclick="location.href='<html:rewrite forward="viewAnnotationServices"/>'" />
            </c:otherwise> 
</c:choose>  
            
        </td>
    </tr>
</table>
</form>
