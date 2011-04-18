package gleam.executive.workflow.util;

import gleam.executive.workflow.model.AnnotationStatusInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gleam.executive.workflow.util.JPDLConstants;

/**
 * The Class AnnotationUtil.
 */
public class AnnotationUtil {

	private static Log log = LogFactory.getLog(AnnotationUtil.class);

	private static boolean isDocumentStatusOpen(AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_NOT_STARTED) ||
		   annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_IN_PROGRESS);
	}

	private static boolean isUniqueAnnotator(AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.isUniqueAnnotator();
	}

	private static boolean hasAnnotatorRejectedDocument(String username, AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.getRejectedByList().contains(username);
	}

	private static boolean hasAnnotatorAnnotatedDocument(String username, AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.getAnnotatedByList().contains(username);
	}

	private static boolean hasAnnotatorTakenDocument(String username, AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.getTakenByList().contains(username);
	}

	public static boolean hasDocumentAnnotationFailed(AnnotationStatusInfo annotationStatusInfo){
		return annotationStatusInfo.getStatus().startsWith(AnnotationStatusInfo.STATUS_FAILED);
	}

	public static boolean hasEverybodyRejected(AnnotationStatusInfo annotationStatusInfo, int poolSize){
		return annotationStatusInfo.getRejectedByList().size()==poolSize;
	}

	private static boolean hasAnnotatorWorkedOnDocument(String username, AnnotationStatusInfo annotationStatusInfo){
		return hasAnnotatorRejectedDocument(username, annotationStatusInfo)
			  || hasAnnotatorTakenDocument(username, annotationStatusInfo)
			  || (hasAnnotatorAnnotatedDocument(username, annotationStatusInfo) && isUniqueAnnotator(annotationStatusInfo));
	}

	public static boolean isDocumentAvailableToAnnotator(String username, AnnotationStatusInfo annotationStatusInfo){
		boolean flag = false;
		if(annotationStatusInfo==null || isDocumentStatusOpen(annotationStatusInfo) &&
		   !hasAnnotatorWorkedOnDocument(username, annotationStatusInfo)) {
			flag = true;
			log.debug("document: "+annotationStatusInfo.getDocumentId() + " is available to annotator: "+username);
		}
		return flag;

	}

	public static AnnotationStatusInfo markDocumentAsTaken(String username, AnnotationStatusInfo annotationStatusInfo){
		if(isDocumentStatusOpen(annotationStatusInfo)){
			annotationStatusInfo.getTakenByList().add(username);
			if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_NOT_STARTED)){
	        	annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_IN_PROGRESS);
	        	annotationStatusInfo.setStartDate(new Date());
	        	log.debug("document: "+annotationStatusInfo.getDocumentId() + " is taken by annotator: "+username);
	        }
		}
        else {
			log.error("document: "+annotationStatusInfo.getDocumentId() + " is not in OPEN STATUS. Cannot mark it as TAKEN by annotator: "+username);
		}
        return annotationStatusInfo;
	}

	public static AnnotationStatusInfo markDocumentAsAnnotated(String username, AnnotationStatusInfo annotationStatusInfo){
		if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_IN_PROGRESS)){
		annotationStatusInfo.getAnnotatedByList().add(username);
        annotationStatusInfo.getTakenByList().remove(username);
        log.debug("document: "+annotationStatusInfo.getDocumentId() + " is annotated by annotator: "+username);
        // if this was the last needed annotation
        if(annotationStatusInfo.getAnnotatedByList().size() == annotationStatusInfo.getNumberOfIterations()){
        	annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_ANNOTATED);
        	annotationStatusInfo.setEndDate(new Date());
        	log.debug("document: "+annotationStatusInfo.getDocumentId() + " is completely annotated!!!!!");
        }
		}
		else {
			log.error("document: "+annotationStatusInfo.getDocumentId() + " is not in STATUS_IN_PROGRESS. Cannot mark it as ANNOTATED by annotator: "+username);
		}
        return annotationStatusInfo;
	}
	
	public static AnnotationStatusInfo resetDocument(AnnotationStatusInfo annotationStatusInfo){
		if(annotationStatusInfo!=null){ 
		log.debug("reset document: "+annotationStatusInfo.getDocumentId());
        // if this was the last needed annotation
       	annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_IN_PROGRESS);
        annotationStatusInfo.setEndDate(null);
		}
        return annotationStatusInfo;
	}
	
	public static AnnotationStatusInfo markDocumentAsAborted(AnnotationStatusInfo annotationStatusInfo){
		 log.debug("abort task for document: "+annotationStatusInfo.getDocumentId());
       // if this was the last needed annotation
      	annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_ABORTED);
       annotationStatusInfo.setEndDate(new Date());
       return annotationStatusInfo;
	}

	public static AnnotationStatusInfo markDocumentAsRejected(String username, AnnotationStatusInfo annotationStatusInfo, int poolSize){
		if(annotationStatusInfo.getStatus().equals(AnnotationStatusInfo.STATUS_IN_PROGRESS)){
		annotationStatusInfo.getRejectedByList().add(username);
        annotationStatusInfo.getTakenByList().remove(username);
        if(hasEverybodyRejected(annotationStatusInfo, poolSize)){
          log.debug("document: "+annotationStatusInfo.getDocumentId() + " mark as canceled ");
          annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_CANCELED);
        }
        log.debug("document: "+annotationStatusInfo.getDocumentId() + " is rejected by annotator: "+username);
        // do not change status and dates
		}
		else {
			log.error("document: "+annotationStatusInfo.getDocumentId() + " is not in STATUS_IN_PROGRESS. Cannot mark it as REJECTED by annotator: "+username);
	          
		}
        return annotationStatusInfo;
	}

	public static AnnotationStatusInfo markDocumentAsFailed(String error, AnnotationStatusInfo annotationStatusInfo){
		annotationStatusInfo.setStatus(AnnotationStatusInfo.STATUS_FAILED + ": " + error);
		log.debug("document: "+annotationStatusInfo.getDocumentId() + " is failed due to: "+error);
		return annotationStatusInfo;
	}

	public static String resolveNextAvailableAnnotationSetName(
			Collection<String> allAnnotationSetNames, String username) {
		List<Integer> filteredList = new ArrayList<Integer>();

		Iterator<String> it = allAnnotationSetNames.iterator();
		int max = 0;
		int suffixValue = 0;
		String prefix;
		if (username == null || username.startsWith(JPDLConstants.ANONYMOUS_ANNOTATION_SET_NAME_PREFIX)) {
			prefix = JPDLConstants.ANONYMOUS_ANNOTATION_SET_NAME_PREFIX;
		} else {
			prefix = username;
		}

		while (it.hasNext()) {
			String asName = it.next();
			if (asName != null && asName.startsWith(prefix)) {
				String suffix = asName.substring(prefix.length());
				try {
					if (!"".equals(suffix)) {
						suffixValue = Integer.valueOf(suffix);
						if (suffixValue > max) {
							max = suffixValue;
						}
					}
					filteredList.add(suffixValue);
				} catch (NumberFormatException nfe) {
					// ignore since annotatorXXX, where XXX is not integer, is
					// not relevant
				}

			}
		}

		String generatedSuffix = "";

		if (username == null || filteredList.size() > 0) {
			List<Integer> sequenceList = initializeSequence(max + 1);
			Collection<Integer> result = CollectionUtils.subtract(sequenceList,
					filteredList);
			// now find minimal not used integer
			Integer minValue = Collections.min(result);
			log.debug("Found first free suffix: " + minValue);
			generatedSuffix = minValue.toString();
		} else {
			// in case that AS will be named after username, we want in initial
			// case to
			// have username without any suffix
			log.debug("There is no AS: " + username
					+ " Do not generate suffix in initial case!");
		}

		return prefix + generatedSuffix;
	}

	public static List<Integer> initializeSequence(int length) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			list.add(i, i + 1);
		}
		return list;
	}

	public static List<String> generateRandomAnnotationSets(){
		 List<String> annotationSetNameList = new ArrayList<String>();
		 Random randomGenerator = new Random();
		    for (int idx = 0; idx < 2; idx++){
		      int randomInt = randomGenerator.nextInt(5);
		      randomInt++;
		      log.debug("Generated : " + randomInt);
		      annotationSetNameList.add(JPDLConstants.ANONYMOUS_ANNOTATION_SET_NAME_PREFIX + randomInt);
		    }

		 return annotationSetNameList;
	}

}
