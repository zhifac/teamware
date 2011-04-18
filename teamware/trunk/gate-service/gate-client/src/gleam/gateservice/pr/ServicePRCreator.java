/*
 * This file is part of GLEAM, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2006 The University of Sheffield
 * 
 * $Id$
 */
package gleam.gateservice.pr;

import gleam.gateservice.endpoint.GateWebService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * Contacts a GaS to determine its exported parameters, and creates a
 * GATE plugin tailored to that service.
 */
public class ServicePRCreator {

  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args.length < 4) {
      usage();
      return;
    }

    URL serviceURL = null;
    try {
      serviceURL = new URL(args[0]);
    }
    catch(MalformedURLException e) {
      usage();
      System.out.println("<serviceURL> must be a valid URL");
    }

    String prName = args[1];
    String className = args[2];
    File outputDir = new File(args[3]);

    // any extra parameters are additional JAR files to add to the
    // creole.xml
    String[] extraJars = new String[args.length - 4]; // may be 0 length
    if(extraJars.length > 0) {
      System.arraycopy(args, 4, extraJars, 0, extraJars.length);
    }

    createPR(serviceURL, prName, className, outputDir, extraJars);
  }

  /**
   * Creates the Java file and creole.xml for a plugin tailored to the
   * specific GaS provided.
   * 
   * @param serviceURL the location of the GaS to use
   * @param prName the &lt;NAME&gt; of the generated PR
   * @param className the class name for the implementation class
   * @param outputDir the directory to write the creole and java files
   *          into.
   * @param extraJars any extra JAR entries to add to creole.xml
   */
  public static void createPR(URL serviceURL, String prName, String className,
          File outputDir, String... extraJars) {
    try {
      // contact the service and extract the parameter lists
      JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
      factory.setServiceClass(GateWebService.class);
      factory.setAddress(serviceURL.toExternalForm());
      GateWebService service = (GateWebService)factory.create();

      List<String> requiredParameters = service.getRequiredParameterNames();
      List<String> optionalParameters = service.getOptionalParameterNames();

      // start the output files
      File creoleXml = new File(outputDir, "creole.xml");
      FileOutputStream fos = new FileOutputStream(creoleXml);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
      PrintWriter creoleXmlWriter = new PrintWriter(osw);

      writeCreolePreamble(serviceURL, prName, className, outputDir, extraJars,
              creoleXmlWriter);

      File javaFile = new File(outputDir, className.substring(className
              .lastIndexOf('.') + 1)
              + ".java");
      fos = new FileOutputStream(javaFile);
      osw = new OutputStreamWriter(fos, "UTF-8");
      PrintWriter javaWriter = new PrintWriter(osw);

      writeJavaPreamble(className, javaWriter);

      // write the necessary code for each parameter
      if(requiredParameters != null) {
        for(String paramName : requiredParameters) {
          writeGetterAndSetter(paramName, javaWriter);
          writeCreoleParam(paramName, false, creoleXmlWriter);
        }
      }

      if(optionalParameters != null) {
        for(String paramName : optionalParameters) {
          writeGetterAndSetter(paramName, javaWriter);
          writeCreoleParam(paramName, true, creoleXmlWriter);
        }
      }

      // and round the files off properly
      writeCreoleEnd(creoleXmlWriter);
      writeJavaEnd(javaWriter);

      creoleXmlWriter.close();
      javaWriter.close();
    }
    catch(FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch(UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Writes the &lt;PARAMETER&gt; element in creole.xml for the given
   * parameter name.
   * 
   * @param paramName
   * @param optional
   * @param w
   */
  private static void writeCreoleParam(String paramName, boolean optional,
          PrintWriter w) {
    w.println("      <PARAMETER NAME=\"" + paramName + "\" RUNTIME=\"true\"");
    w.print("        ");
    if(optional) {
      w.print("OPTIONAL=\"true\"");
    }
    w.println(">java.lang.String</PARAMETER>");
  }

  /**
   * Writes a Java getter/setter method pair for the given parameter
   * name.
   * 
   * @param paramName
   * @param w
   */
  private static void writeGetterAndSetter(String paramName, PrintWriter w) {
    String beanParamName = Character.toUpperCase(paramName.charAt(0))
            + paramName.substring(1);
    w.println("  public String get" + beanParamName + "() {");
    w.println("    return (String)gasParameterValues.get(\"" + paramName
            + "\");");
    w.println("  }");
    w.println();
    w.println("  public void set" + beanParamName + "(String newValue) {");
    w.println("    gasParameterValues.put(\"" + paramName + "\", newValue);");
    w.println("  }");
    w.println();
  }

  /**
   * Writes the common parts of the creole.xml file. The JAR files are
   * represented by a single empty &lt;JARS /&gt; element, which is
   * intended to be replaced with the necessary &lt;JAR&gt; entries.
   * 
   * @param serviceURL
   * 
   * @param prName
   * @param className
   * @param extraJars
   * @param outputDir
   * @param w
   */
  private static void writeCreolePreamble(URL serviceURL, String prName,
          String className, File outputDir, String[] extraJars, PrintWriter w) {
    w.println("<CREOLE-DIRECTORY>");
    w.println("  <CREOLE>");
    w.println("    <RESOURCE>");
    w.println("      <NAME>" + prName + "</NAME>");
    w.println("      <CLASS>" + className + "</CLASS>");
    for(String jarName : extraJars) {
      w.println("      <JAR>" + jarName + "</JAR>");
    }
    // add all existing JAR files in the output dir
    FilenameFilter jarsOnlyFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
      }
    };
    for(String jarName : outputDir.list(jarsOnlyFilter)) {
      w.println("      <JAR>" + jarName + "</JAR>");
    }
    w.println("      <PARAMETER NAME=\"document\" "
            + "COMMENT=\"The document to process\"");
    w.println("        RUNTIME=\"true\">gate.Document</PARAMETER>");
    w.println("      <PARAMETER NAME=\"serviceLocation\" "
            + "COMMENT=\"URL of the service\"");
    w.println("        DEFAULT=\"" + serviceURL + "\"");
    w.println("        >java.net.URL</PARAMETER>");
    w.println("      <PARAMETER NAME=\"httpTimeout\" "
            + "COMMENT=\"Timeout to use when contacting the service\"");
    w.println("        OPTIONAL=\"true\"");
    w.println("        >java.lang.Integer</PARAMETER>");
    w.println("      <PARAMETER NAME=\"useChunkedEncoding\" DEFAULT=\"false\" "
            + "COMMENT=\"Use chunked HTTP encoding (not recommended)\"");
    w.println("        >java.lang.Boolean</PARAMETER>");
  }

  private static void writeJavaPreamble(String className, PrintWriter w) {
    w.println("/* This file was auto-generated by ServicePRCreator */");
    int indexOfDot = className.lastIndexOf('.');
    if(indexOfDot >= 0) {
      w.println("package " + className.substring(0, indexOfDot) + ";");
      w.println();
    }

    w.println("import gleam.gateservice.pr.GateServicePR;");
    w.println();
    w.println("public class " + className.substring(indexOfDot + 1)
            + " extends GateServicePR {");
  }

  /**
   * Writes the tail of the creole.xml file.
   * 
   * @param w
   */
  private static void writeCreoleEnd(PrintWriter w) {
    w.println("    </RESOURCE>");
    w.println("  </CREOLE>");
    w.println("</CREOLE-DIRECTORY>");
  }

  /**
   * Writes the tail of the Java file.
   * 
   * @param w
   */
  private static void writeJavaEnd(PrintWriter w) {
    w.println("}");
  }

  /**
   * Print usage message.
   */
  private static void usage() {
    System.out.println("Usage:");
    System.out.println();
    System.out
            .println("ServicePRCreator <serviceURL> <PRname> <classname> <outputdir>");
    System.out.println();
    System.out.println("<serviceURL> - location of the service");
    System.out.println("<PRname> - name of the PR to create");
    System.out.println("<classname> - class name for the generated PR");
    System.out
            .println("<outputdir> - directory in which to write the generated .java file");
  }

}
