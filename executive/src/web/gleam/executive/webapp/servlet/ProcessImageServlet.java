package gleam.executive.webapp.servlet;

import gleam.executive.model.WebAppBean;
import gleam.executive.workflow.manager.WorkflowManager;
import gleam.executive.workflow.util.JPDLConstants;
import gleam.executive.workflow.util.WorkflowException;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lowagie.text.Image;

public class ProcessImageServlet extends HttpServlet {

	private WorkflowManager workflowManager;
	private static Log log = LogFactory.getLog(ProcessImageServlet.class);
	private static int processImageScaleoutRatio = JPDLConstants.PROCESS_IMAGE_SCALEOUT_RATIO;

	public ProcessImageServlet() {
		workflowManager = null;
	}

	public void init() throws ServletException {
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		log.debug("@@@@@ ProcessImageServlet @@@@@");
		ServletContext context = this.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);
		workflowManager = (WorkflowManager) ctx.getBean("workflowManager");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        long definitionId;
        OutputStream out;
        definitionId = Long.parseLong(request.getParameter("definitionId"));
        out = null;
        try {  
            log.debug((new StringBuilder()).append("DO GET: fetched workflowManager: pdId: ").append(definitionId).toString());
            InputStream is = workflowManager.loadProcessDefinition(definitionId).getFileDefinition().getInputStream("processimage.jpg");
            response.setContentType("image/jpeg");
            BufferedImage originalImage = ImageIO.read(is);
            ServletOutputStream os = response.getOutputStream();
            if(processImageScaleoutRatio!=1){
            int x = originalImage.getWidth();
            int y = originalImage.getHeight();
            
            int new_x = x/processImageScaleoutRatio;
            int new_y =(new_x * (y / processImageScaleoutRatio) / new_x);
  
       	    // Write image to the output stream
            
            BufferedImage newImage = createResizedCopy(originalImage,
                	new_x, new_y, true);
            ImageIO.write(newImage, "jpeg", os);
            }
            else {
            	 ImageIO.write(originalImage, "jpeg", os);
            }
            /*
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            */
        }
        catch(Exception e)
        {
            throw new IOException(e.getMessage());
        }
        /*
        finally {
        	if(out != null) {
        		out.close();
        	}
        } 
        */       
    }

	private BufferedImage createResizedCopy(BufferedImage originalImage,
			int scaledWidth, int scaledHeight, boolean preserveAlpha) {
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight,
				imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}

}
