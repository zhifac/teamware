/*
 *  SchemaCleaner.java
 *
 *  Copyright (c) 2006-2011, The University of Sheffield.
 *
 *  This file is part of GATE Teamware (see http://gate.ac.uk/teamware/), 
 *  and is free software, licenced under the GNU Affero General Public License,
 *  Version 3, November 2007 (also included with this distribution as file 
 *  LICENCE-AGPL3.html).
 *
 *  A commercial licence is also available for organisations whose business
 *  models preclude the adoption of open source and is subject to a licence
 *  fee charged by the University of Sheffield. Please contact the GATE team
 *  (see http://gate.ac.uk/g8/contact) if you require a commercial licence.
 *
 * Mark A. Greenwood, 13/08/2010
 *
 *  $Id$
 */

package gate.creole;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@CreoleResource(name = "Schema Cleaner", interfaceName = "gate.ProcessingResource", icon = "sweep.png", comment = "Produces an annotation set whose content is restricted by the specified set of schemas")
public class SchemaCleaner extends AbstractLanguageAnalyser {

  private String inputASName = null;

  private String outputASName = null;

  private List<AnnotationSchema> schemas = new ArrayList<AnnotationSchema>();

  @Override
  public void execute() throws ExecutionException {

    // If there are no schemas selected then there is nothing to do
    if(schemas.isEmpty()) return;

    // get the annotation set we are going to store the clean annotations into
    AnnotationSet outputAS = getDocument().getAnnotations(outputASName);

    // to ensure a clean set of annotations the output set must be empty before
    // we start adding annotations to it
    if(!outputAS.isEmpty())
      throw new ExecutionException("Output AnnotationSet must be empty");

    // get the set we are going to put the clean annotations into
    AnnotationSet inputAS = getDocument().getAnnotations(inputASName);

    // loop through the schemas we are cleaning against
    for(AnnotationSchema schema : schemas) {

      // get all the annotations of the same type as the current schema
      AnnotationSet annots = inputAS.get(schema.getAnnotationName());

      // if there are no annotations then move onto the next schema
      if(annots == null) continue;

      // for each of the annotations whose type matches the current schema
      for(Annotation a : annots) {

        // let's assume the annotation is valid wrt the schema
        boolean valid = true;

        // create a FeatureMap to hold any features we want to keep
        FeatureMap params = Factory.newFeatureMap();

        // if the schema specifies features then
        if(schema.getFeatureSchemaSet() != null) {

          // get the features from the existing annotation
          FeatureMap current = a.getFeatures();

          // for each of the features specified in the schema...
          for(FeatureSchema fs : schema.getFeatureSchemaSet()) {

            // get the name of the feature and...
            String fn = fs.getFeatureName();

            // the value of that feature from the existing annotation
            Object fv = current.get(fn);

            if(fv != null) {
              if(fs.getFeatureValueClass().isAssignableFrom(fv.getClass())) {
                if(!fs.isEnumeration() || fs.getPermittedValues().contains(fv)) {
                  // if the feature exists and is valid then copy it into the
                  // FeatureMap we will use to create the clean annotation
                  params.put(fn, fv);
                }
              }
            }

            if(fs.isRequired() && !params.containsKey(fn)) {
              // if the feature specified in the schema is marked as required
              // but we haven't managed to add it to the cleaned annotation for
              // some reason, then the annotation isn't valid so flag this and
              // abort
              valid = false;
              break;
            }
          }
        }

        if(valid) {
          // if we have a valid clean annotation then...
          try {
            // ... add it to the output annotation set
            outputAS.add(a.getId(), a.getStartNode().getOffset(), a
                    .getEndNode().getOffset(), schema.getAnnotationName(),
                    params);
          } catch(InvalidOffsetException e) {
            // this should be completely impossible
            throw new ExecutionException(e);
          }
        }
      }
    }
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "the annotation set used as input to this PR")
  public void setInputASName(String name) {
    inputASName = name;
  }

  public String getInputASName() {
    return inputASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "the annotation set used to store output from this PR", defaultValue = "safe.preprocessing")
  public void setOutputASName(String name) {
    outputASName = name;
  }

  public String getOutputASName() {
    return outputASName;
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "the list of schemas that define the annotations to move from the input to the output annotation set")
  public void setSchema(List<AnnotationSchema> schemas) {
    this.schemas = schemas;
  }

  public List<AnnotationSchema> getSchema() {
    return schemas;
  }
}