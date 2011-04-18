<%@ include file="/common/taglibs.jsp"%>
<head>
<title><fmt:message key="ontologyRepositoryList.title"/></title>
<content tag="heading"><fmt:message key="ontologyRepositoryList.heading"/></content>
<meta name="menu" content="ResourceMenu"/>
</head>
<c:set var="buttons">
    <input type="button" style="margin-right: 5px"
        onclick="location.href='<html:rewrite forward="addOntologyRepository"/>'"
        value="<fmt:message key="button.uploadOntology"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/addOntologyBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />

    <input type="button" onclick="location.href='<html:rewrite forward="mainMenu"/>'"
        value="<fmt:message key="button.backToMainPage"/>"
        onmouseover="ajax_showTooltip('ajaxtooltip/info/doneBtn.jsp',this);return false" onmouseout="ajax_hideTooltip()" 
        />
</c:set>

<c:out value="${buttons}" escapeXml="false"/>

<display:table name="ontologyRepositoryList" cellspacing="0" cellpadding="0"
    id="ontologyRepositoryList" pagesize="25" class="table ontologyRepositoryList"
    export="true" requestURI="">
    
    <display:column property="name" sortable="true" headerClass="sortable" titleKey="ontologyRepositoryForm.name"/>
    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="${ontologyRepositoryList.name}"/>" href="<c:url value="/ontologyDisplay.html?method=view&name=${ontologyRepositoryList.name}"/>" >
    <img src="<c:url value="/images/open.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/viewOntology.jsp',this);return false" onmouseout="ajax_hideTooltip()" class="icon" />
    </a>
    </display:column>

    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="${ontologyRepositoryList.name}"/>" href="<c:url value="/addOntologyRepository.html?method=edit&name=${ontologyRepositoryList.name}"/>">
    <img src="<c:url value="/images/edit.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/updateOntologyRepository.jsp',this);return false" onmouseout="ajax_hideTooltip()" />
    </a>
    </display:column>

    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="${ontologyRepositoryList.name}"/>" href="<c:url value="/ontologyDisplay.html?method=download&name=${ontologyRepositoryList.name}"/>">
    <img src="<c:url value="/images/download.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/downloadOntology.jsp',this);return false" onmouseout="ajax_hideTooltip()" />
    </a>
    </display:column>
    
    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="clear${ontologyRepositoryList.name}"/>" href="<c:url value="/ontologyRepositoryList.html?method=clear&name=${ontologyRepositoryList.name}"/>">
    <img src="<c:url value="/images/clear.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/clearOntologyRepository.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('Are you sure to empty the selected ontology repository?')" />
    </a>
    </display:column>
    
    <display:column style="width: 5%" media="html csv excel xml">
    <a id="<c:out value="delete${ontologyRepositoryList.name}"/>" href="<c:url value="/ontologyRepositoryList.html?method=delete&name=${ontologyRepositoryList.name}"/>">
    <img src="<c:url value="/images/delete.gif"/>" onmouseover="ajax_showTooltip('ajaxtooltip/info/deleteOntologyRepository.jsp',this);return false" onmouseout="ajax_hideTooltip()" onclick="bCancel=true; return confirmDialog('Are you sure to delete the selected ontology repository?')" />
    </a>
    </display:column>
    <display:setProperty name="paging.banner.item_name" value="ontology repository"/>
    <display:setProperty name="paging.banner.items_name" value="ontology repositories"/>
</display:table>

<c:out value="${buttons}" escapeXml="false"/>

<script type="text/javascript">
    highlightTableRows("ontologyRepositoryList");
</script>
