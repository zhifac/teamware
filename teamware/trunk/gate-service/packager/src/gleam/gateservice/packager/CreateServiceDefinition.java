package gleam.gateservice.packager;

import gleam.gateservice.definition.GateServiceDefinition;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Ant task to create a simple service definition file with no
 * parameters, and with the input and output annotation set names
 * specified.
 */
public class CreateServiceDefinition extends Task {

  /**
   * Class representing nested inputannotationset and
   * outputannotationset elements.
   */
  public class AnnotationSets {
    private boolean input;

    public AnnotationSets(boolean input) {
      this.input = input;
    }

    /**
     * Takes a comma-separated string of annotation set names, splits
     * it, and adds the names to the relevant Set in the containing
     * class.
     */
    public void setNames(String names) {
      String[] theNames = names.trim().split("\\s*,\\s*");
      for(String name : theNames) {
        if(!"".equals(name)) {
          if(input) {
            inputAnnotationSets.add(name);
          }
          else {
            outputAnnotationSets.add(name);
          }
        }
      }
    }
  }

  /**
   * Should we take input from the default annotation set?
   */
  private boolean inputFromDefaultSet = false;

  /**
   * Set of all annotation sets from which input will be taken.
   */
  private Set<String> inputAnnotationSets = new HashSet<String>();

  /**
   * Should we send output to the default annotation set?
   */
  private boolean outputToDefaultSet = true;

  /**
   * Set of all annotation sets to which output will be sent.
   */
  private Set<String> outputAnnotationSets = new HashSet<String>();

  /**
   * Destination file to which the service definition XML will be
   * written.
   */
  private File destFile;

  public void setInputFromDefaultSet(boolean inputFromDefaultSet) {
    this.inputFromDefaultSet = inputFromDefaultSet;
  }

  public void setOutputToDefaultSet(boolean outputToDefaultSet) {
    this.outputToDefaultSet = outputToDefaultSet;
  }

  public AnnotationSets createInputAnnotationSet() {
    return new AnnotationSets(true);
  }

  public AnnotationSets createOutputAnnotationSet() {
    return new AnnotationSets(false);
  }

  public void setDestFile(File destFile) {
    this.destFile = destFile;
  }

  /**
   * Create a service definition file based on the parameters specified.
   */
  public void execute() throws BuildException {
    if(destFile == null) {
      throw new BuildException("No destination file specified", getLocation());
    }
    log("Writing service definition to " + destFile);
    if(inputFromDefaultSet) {
      log("Taking input from default annotation set", Project.MSG_VERBOSE);
      inputAnnotationSets.add("");
    }

    if(outputToDefaultSet) {
      log("Sending output to default annotation set", Project.MSG_VERBOSE);
      outputAnnotationSets.add("");
    }

    GateServiceDefinition def = new GateServiceDefinition();
    for(String setName : inputAnnotationSets) {
      log("Input annotation set name: \"" + setName + "\"", Project.MSG_VERBOSE);
      def.addInputAnnotationSet(setName);
    }
    for(String setName : outputAnnotationSets) {
      log("Output annotation set name: \"" + setName + "\"", Project.MSG_VERBOSE);
      def.addOutputAnnotationSet(setName);
    }

    // write the definition to XML
    Element xml = def.toXml();
    Document doc = new Document(xml);

    try {
      FileOutputStream fos = new FileOutputStream(destFile);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      try {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, bos);
      }
      finally {
        bos.close();
      }
    }
    catch(IOException e) {
      throw new BuildException("Error writing service definition", e,
              getLocation());
    }
  }
}
