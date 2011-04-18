<%@ include file="/common/taglibs.jsp"%>

<head>
<title><fmt:message key="display.title"/></title>
<content tag="heading"><fmt:message key="display.heading"/></content>
<meta name="menu" content="ProjectMenu"/>
</head>

<table class="detail" cellpadding="5">

    <tr>
        <th>Filename:</th>
        <td><c:out value="${fileName}"/></td>
    </tr>

    <tr>
        <th>File size:</th>
        <td><c:out value="${size}"/></td>
    </tr>

    <tr>
        <td></td>
        <td class="buttonBar">
            <input type="button" class="button" name="done" value="Done"
                onclick="location.href='<html:rewrite forward="processDefinitionList"/>'" />
            <input type="button" class="button" style="width: 120px" value="Upload Another"
                onclick="location.href='<html:rewrite forward="processDefinitionUpload"/>'" />
        </td>
    </tr>
</table>
</form>
