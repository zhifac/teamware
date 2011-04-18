package gleam.executive.webapp.taglib;

import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.xpath.DefaultXPath;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * 
 * @author agaton
 * 
 * @jsp.tag name="processimage" bodycontent="empty"
 * 
 */
public class ProcessImageTag extends TagSupport {

  protected final transient Log log = LogFactory.getLog(ProcessImageTag.class);
  
  private static final long serialVersionUID = 1L;
  private long taskInstanceId = -1;
  private long tokenInstanceId = -1;
  
  private byte[] gpdBytes = null;
  private byte[] imageBytes = null;
  private Token currentToken = null;
  private static long processDefinitionId = -1;
  private WorkflowManager workflowManager = null;
  //private WebAppBean webAppBean = null;
  static String currentTokenColor = "red";
  static String childTokenColor = "blue";
  static String tokenNameColor = "blue";
  static int processImageScaleoutRatio = JPDLConstants.PROCESS_IMAGE_SCALEOUT_RATIO;

  public void release() {
    taskInstanceId = -1;
    gpdBytes = null;
    imageBytes = null;
    currentToken = null;
  }

  public int doEndTag() throws JspException {
    try {
      initialize();
      retrieveByteArrays();
      if (gpdBytes != null && imageBytes != null) {
        writeTable();
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new JspException("table couldn't be displayed", e);
    } catch (DocumentException e) {
      e.printStackTrace();
      throw new JspException("table couldn't be displayed", e);
    }
    catch (WorkflowException e) {
        e.printStackTrace();
        throw new JspException("table couldn't be displayed", e);
    }
    release();
    return EVAL_PAGE;
  }

  private void retrieveByteArrays() {
    try {
      log.debug("@@@ retrieveByteArrays pID "+ processDefinitionId);
      FileDefinition fileDefinition = workflowManager.findFileDefinition(processDefinitionId);
      gpdBytes = fileDefinition.getBytes("gpd.xml");
      log.debug("@@@ retrieveByteArrays gpd "+ gpdBytes.length);
      imageBytes = fileDefinition.getBytes("processimage.jpg");
      log.debug("@@@ retrieveByteArrays gpd "+ imageBytes.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void writeTable() throws IOException, DocumentException {
	  
    int borderWidth = 4/processImageScaleoutRatio;
    Element rootDiagramElement = DocumentHelper.parseText(new String(gpdBytes)).getRootElement();
    int[] boxConstraint;
    int[] imageDimension = extractImageDimension(rootDiagramElement);
    log.debug("@@@ writeTable  imageDimension: "+ imageDimension);
    String imageLink = "processimage.img?definitionId=" + processDefinitionId;
    JspWriter jspOut = pageContext.getOut();
    if (tokenInstanceId > 0) {
        
        List allTokens = new ArrayList();
        walkTokens(currentToken, allTokens);
        
    	jspOut.println("<div style='position:relative; background-image:url(" + imageLink + "); width: " + imageDimension[0] + "px; height: " + imageDimension[1] + "px;'>");

        for (int i = 0; i < allTokens.size(); i++)
        {
            Token token = (Token) allTokens.get(i);

          //check how many tokens are on teh same level (= having the same parent)
          int offset = i;
          if(i > 0) {
            while(offset > 0 && ((Token) allTokens.get(offset - 1)).getParent().equals(token.getParent())) {
              offset--;
            }
          }
            boxConstraint = extractBoxConstraint(rootDiagramElement, token);

            //Adjust for borders
            //boxConstraint[2]-=borderWidth*2;
            //boxConstraint[3]-=borderWidth*2;

        	jspOut.println("<div style='position:absolute; left: "+ boxConstraint[0] +"px; top: "+ boxConstraint[1] +"px; ");

            if (i == (allTokens.size() - 1)) {
            	jspOut.println("border: " + currentTokenColor);
            }
            else {            	
    			jspOut.println("border: " + childTokenColor);
            }
            
            jspOut.println(" " + borderWidth + "px groove; "+
            			"width: "+ boxConstraint[2] +"px; height: "+ boxConstraint[3] +"px;'>");
			
            if(token.getName()!=null)
            {
                 jspOut.println("<span style='color:" + tokenNameColor + ";font-style:italic;position:absolute;left:"+ (boxConstraint[2] + 10) +"px;top:" +((i - offset) * 20) +";'>&nbsp;" + formatTokenName(token.getName()) +"</span>");
            }

            jspOut.println("</div>");
        }
        jspOut.println("</div>");    	
    }
    else
    {
    	boxConstraint = extractBoxConstraint(rootDiagramElement);
    	
	    jspOut.println("<table border=0 cellspacing=0 cellpadding=0 width=" + imageDimension[0] + " height=" + imageDimension[1] + ">");
	    jspOut.println("  <tr>");
	    jspOut.println("    <td width=" + imageDimension[0] + " height=" + imageDimension[1] + " style=\"background-image:url(" + imageLink + ")\" valign=top>");
	    jspOut.println("      <table border=0 cellspacing=0 cellpadding=0>");
	    jspOut.println("        <tr>");
	    jspOut.println("          <td width=" + (boxConstraint[0] - borderWidth) + " height=" + (boxConstraint[1] - borderWidth)
	            + " style=\"background-color:transparent;\"></td>");
	    jspOut.println("        </tr>");
	    jspOut.println("        <tr>");
	    jspOut.println("          <td style=\"background-color:transparent;\"></td>");
	    jspOut.println("          <td style=\"border-color:" + currentTokenColor + "; border-width:" + borderWidth + "px; border-style:groove; background-color:transparent;\" width="
	            + boxConstraint[2] + " height=" + (boxConstraint[3] + (2 * borderWidth)) + ">&nbsp;</td>");
	    jspOut.println("        </tr>");
	    jspOut.println("      </table>");
	    jspOut.println("    </td>");
	    jspOut.println("  </tr>");
	    jspOut.println("</table>");
    }
  }

  private int[] extractBoxConstraint(Element root) {
    int[] result = new int[4];
    String nodeName = currentToken.getNode().getName();
    XPath xPath = new DefaultXPath("//node[@name='" + nodeName + "']");
    Element node = (Element) xPath.selectSingleNode(root);
    result[0] = Integer.valueOf(node.attribute("x").getValue()).intValue()/processImageScaleoutRatio;
    result[1] = Integer.valueOf(node.attribute("y").getValue()).intValue()/processImageScaleoutRatio;
    result[2] = Integer.valueOf(node.attribute("width").getValue()).intValue()/processImageScaleoutRatio;
    result[3] = Integer.valueOf(node.attribute("height").getValue()).intValue()/processImageScaleoutRatio;
    return result;
  }

  private int[] extractBoxConstraint(Element root, Token token) {
	    int[] result = new int[4];
	    String nodeName = token.getNode().getName();
	    XPath xPath = new DefaultXPath("//node[@name='" + nodeName + "']");
	    Element node = (Element) xPath.selectSingleNode(root);
	    result[0] = Integer.valueOf(node.attribute("x").getValue()).intValue()/processImageScaleoutRatio;
	    result[1] = Integer.valueOf(node.attribute("y").getValue()).intValue()/processImageScaleoutRatio;
	    result[2] = Integer.valueOf(node.attribute("width").getValue()).intValue()/processImageScaleoutRatio;
	    result[3] = Integer.valueOf(node.attribute("height").getValue()).intValue()/processImageScaleoutRatio;
	    return result;
	  }
  
  private int[] extractImageDimension(Element root) {
    int[] result = new int[2];
    result[0] = Integer.valueOf(root.attribute("width").getValue()).intValue()/processImageScaleoutRatio;
    result[1] = Integer.valueOf(root.attribute("height").getValue()).intValue()/processImageScaleoutRatio;
    log.debug("x: "+result[0] + "; y:"+result[1]);
    return result;
    
  }

  private void initialize() throws WorkflowException{
	  ServletContext context = pageContext.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
	    .getRequiredWebApplicationContext(context);
	
	 workflowManager = (WorkflowManager)ctx
	    .getBean("workflowManager");
		
    if (this.taskInstanceId > 0) {
    	
    	   ProcessDefinition processDefinition = workflowManager.findProcessDefinitionByTaskInstanceId(taskInstanceId);
    	   processDefinitionId = processDefinition.getId();
    	   log.debug("@@@ initialize pID "+ processDefinitionId);
    	   currentToken = workflowManager.getTaskInstance(taskInstanceId).getToken();
    	   log.debug("@@@ found currentToken ID: "+ currentToken.getId());
    	   tokenInstanceId = currentToken.getId();
    }
    else
    {
    	
    	if (this.tokenInstanceId > 0){
    		//processDefinition = currentToken.getProcessInstance().getProcessDefinition();

            throw new WorkflowException("NOT IMPLEMENTED");		
    	}
	 }
	}
 	
    
    

  private void walkTokens(Token parent, List allTokens)
  {
      Map children = parent.getChildren();
      if(children != null && children.size() > 0)
      {
          Collection childTokens = children.values();
          for (Iterator iterator = childTokens.iterator(); iterator.hasNext();)
          {
              Token child = (Token) iterator.next();
              walkTokens(child,  allTokens);
          }
      }

      allTokens.add(parent);
  }

  
  private String formatTokenName(String tokenName){
	  String result = "";
	  if(tokenName!=null && !"".equals(tokenName)){
		  if(tokenName.startsWith(JPDLConstants.DOCUMENT_PREFIX)){
			  result = tokenName.substring(JPDLConstants.DOCUMENT_PREFIX.length());
			  int dotXml = result.indexOf(".xml");
			  if(dotXml!=-1){
				  result = result.substring(0,dotXml);
			  }
		  }
	  }
	  return result;
  }
  /**
	 * @param task
	 *            The selected option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
  public void setTask(String id) {
    this.taskInstanceId = Long.parseLong(id);
  }
  
  /**
	 * @param token
	 *            The selected option.
	 * @jsp.attribute required="false" rtexprvalue="true"
	 */
  public void setToken(long id) {
	this.tokenInstanceId = id;  
  }
  
}
