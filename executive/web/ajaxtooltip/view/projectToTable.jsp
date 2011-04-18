<%@ include file="/common/taglibs.jsp"%>

<%String projectId = request.getParameter("projectId"); 
%>
<executive:projectToTable projectId="<%=projectId%>" />
			    


