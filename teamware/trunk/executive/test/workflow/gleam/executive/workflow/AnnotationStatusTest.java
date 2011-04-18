package gleam.executive.workflow;

import gleam.executive.workflow.model.AnnotationStatusInfo;
import gleam.executive.workflow.util.AnnotationUtil;

public class AnnotationStatusTest extends BaseWorkflowServiceTestCase  {

	private static 	AnnotationStatusInfo annotationStatusInfo  = new AnnotationStatusInfo("doc1");

	private static String[] annotators = {"annotator1", "annotator2", "annotator3", "annotator4"};

	public void testGettingNotStartedDocumentWithUniqueAnnotatorAndCheckTrueAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[0];
		assertTrue(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

	// annotator1 took the doc
	public void testGettingTakenDocumentWithUniqueAnnotatorAndCheckTrueAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[2];
        String username1 = annotators[0];
		AnnotationUtil.markDocumentAsTaken(username1, annotationStatusInfo);
		assertTrue(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
	public void testGettingAlreadyAnnotatedDocumentWithUniqueAnnotatorAndCheckTrueAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[2];
        String username1 = annotators[0];
		AnnotationUtil.markDocumentAsAnnotated(username1, annotationStatusInfo);
		assertTrue(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
//	 annotator2 canceled the doc
	public void testGettingAlreadyAnnotatedAndCanceledDocumentWithUniqueAnnotatorAndCheckTrueAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[2];
        String username2 = annotators[1];
		AnnotationUtil.markDocumentAsRejected(username2, annotationStatusInfo,annotators.length);
		assertTrue(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
//	 annotator2 canceled the doc
	public void testGettingAlreadyAnnotatedDocumentWithNonUniqueAnnotatorAndCheckTrueAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(false);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[0];
		assertTrue(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
//	 annotator2 canceled the doc
	public void testGettingAlreadyCanceledDocumentWithNonUniqueAnnotatorAndCheckFalseAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(false);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[1];
		assertFalse(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
//	 annotator2 canceled the doc
	public void testGettingAlreadyCanceledDocumentWithUniqueAnnotatorAndCheckFalseAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username = annotators[1];
		assertFalse(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

//	 annotator1 annotated the doc
//	 annotator2 canceled the doc
//	 annotatorX annotated the doc
	public void testGettingAlreadyAnnotatedDocumentWithUniqueAnnotatorAndCheckFalseAvailablity() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		String username =annotators[2];
		AnnotationUtil.markDocumentAsAnnotated(username, annotationStatusInfo);
		assertFalse(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }


	public void testGettingAlreadyAnnotatedDocumentAndCheckFalseAvailability() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
        String username = annotators[3];
		assertEquals("Check status", annotationStatusInfo.getStatus(), AnnotationStatusInfo.STATUS_ANNOTATED);
		assertFalse(AnnotationUtil.isDocumentAvailableToAnnotator(username, annotationStatusInfo));
    }

	public void testMarkingDocumentAnnotationAsFailed() throws Exception {
		String error = "Oops";
		AnnotationUtil.markDocumentAsFailed(error, annotationStatusInfo);
		assertEquals("Check status", annotationStatusInfo.getStatus(), AnnotationStatusInfo.STATUS_FAILED + ": " + error);
    }

	public void testMarkingDocumentAnnotationAsFailedAgain() throws Exception {
		String error = "Oops";
		AnnotationUtil.markDocumentAsFailed(error, annotationStatusInfo);
		assertEquals("Check status", annotationStatusInfo.getStatus(), AnnotationStatusInfo.STATUS_FAILED + ": " + error);
    }
	
	 
	public void testStatusOfDocumentNotEverybodyCanceled() throws Exception {
		annotationStatusInfo  = new AnnotationStatusInfo("doc2");
		    annotationStatusInfo.setUniqueAnnotator(true);
			annotationStatusInfo.setNumberOfIterations(2);
			AnnotationUtil.markDocumentAsTaken(annotators[0], annotationStatusInfo);
			AnnotationUtil.markDocumentAsTaken(annotators[1], annotationStatusInfo);
			AnnotationUtil.markDocumentAsRejected(annotators[0], annotationStatusInfo, annotators.length);
			AnnotationUtil.markDocumentAsRejected(annotators[1], annotationStatusInfo, annotators.length);
			assertEquals("Check status", annotationStatusInfo.getStatus(), AnnotationStatusInfo.STATUS_IN_PROGRESS);
    }
	
	public void testStatusOfDocumentEverybodyCanceled() throws Exception {
		annotationStatusInfo.setUniqueAnnotator(true);
		annotationStatusInfo.setNumberOfIterations(2);
		AnnotationUtil.markDocumentAsTaken(annotators[2], annotationStatusInfo);
		AnnotationUtil.markDocumentAsTaken(annotators[3], annotationStatusInfo);
		AnnotationUtil.markDocumentAsRejected(annotators[2], annotationStatusInfo, annotators.length);
		AnnotationUtil.markDocumentAsRejected(annotators[3], annotationStatusInfo, annotators.length);
		assertEquals("Check status", annotationStatusInfo.getStatus(), AnnotationStatusInfo.STATUS_CANCELED);
}


}
