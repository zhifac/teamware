/*
 * This file is part of SAFE, licenced under the GNU Lesser General Public
 * Licence.
 *
 * (c) 2007 The University of Sheffield
 *
 * $Id$
 */
package gleam.gateservice.definition.tool;

import gate.Gate;
import gate.creole.Parameter;
import gate.creole.ResourceData;
import gate.util.GateRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class encapsulating information about a saved GATE application. It
 * holds information about the PRs in the application, and the set of
 * annotation set names that appear to be used for input and output
 * (i.e. taken from the value of any parameters called "inputASName" and
 * "outputASName" in the saved application state).
 */
public class ApplicationInfo {
  private List<PRInfo> prInfos;

  private Set<String> inputAnnotationSetNames;

  private Set<String> outputAnnotationSetNames;

  public ApplicationInfo(List<PRInfo> prInfos,
          Set<String> inputAnnotationSetNames,
          Set<String> outputAnnotationSetNames) {
    this.prInfos = prInfos;
    this.inputAnnotationSetNames = inputAnnotationSetNames;
    this.outputAnnotationSetNames = outputAnnotationSetNames;
  }

  public List<PRInfo> getPrInfos() {
    return prInfos;
  }

  public Set<String> getInputAnnotationSetNames() {
    return inputAnnotationSetNames;
  }

  public Set<String> getOutputAnnotationSetNames() {
    return outputAnnotationSetNames;
  }

  private DefaultMutableTreeNode treeRoot = null;

  /**
   * Returns a tree which represents this application. The root node has
   * one child for each PR in the application, whose userObject is the
   * PRInfo object. Each PR node has one child for each runtime
   * parameter, whose userObject is the parameter name.
   */
  public DefaultMutableTreeNode getParamsTreeRoot() {
    if(treeRoot == null) {
      buildTree();
    }

    return treeRoot;
  }

  private void buildTree() throws GateRuntimeException {
    treeRoot = new DefaultMutableTreeNode();
    // add a node for each PR in the application
    for(PRInfo pr : prInfos) {
      DefaultMutableTreeNode prNode = new DefaultMutableTreeNode(pr, true);
      treeRoot.add(prNode);

      // fetch parameter information from the creole register
      ResourceData rd = Gate.getCreoleRegister().get(pr.getClassName());
      if(rd == null) {
        throw new GateRuntimeException("Couldn't get resource data for PR "
                + pr.getName() + " of class " + pr.getClassName());
      }
      List<List<Parameter>> runtimeParams = rd.getParameterList()
              .getRuntimeParameters();

      List<DefaultMutableTreeNode> paramNodes = new ArrayList<DefaultMutableTreeNode>();

      // add a child node for each parameter name
      for(List<Parameter> disjunction : runtimeParams) {
        for(Parameter param : disjunction) {
          DefaultMutableTreeNode paramNode = new DefaultMutableTreeNode(param
                  .getName(), false);
          paramNodes.add(paramNode);
        }
      }

      // sort param nodes into alphabetical order by parameter name
      Collections.sort(paramNodes, new Comparator<DefaultMutableTreeNode>() {
        public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
          return ((String)a.getUserObject()).compareTo((String)b
                  .getUserObject());
        }
      });

      // insert nodes into the tree
      for(DefaultMutableTreeNode node : paramNodes) {
        prNode.add(node);
      }
    }
  }
}
