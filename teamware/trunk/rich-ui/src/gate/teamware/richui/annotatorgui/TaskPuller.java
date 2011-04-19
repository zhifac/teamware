/*
 *  TaskPuller.java
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
 *  $Id$
 */
package gate.teamware.richui.annotatorgui;

import gate.teamware.richui.annotatorgui.actions.ActionCheckNewTasks;
import gate.teamware.richui.annotatorgui.gui.*;
import gate.teamware.richui.common.RichUIException;

import java.net.URI;
import java.security.InvalidParameterException;

/**
@deprecated
*/
public class TaskPuller extends Thread {
  /** Debug flag */
  private static final boolean DEBUG = true;
  private static final boolean DEBUG_DETAILS = true;

  // public static final int STATUS_STOPPED = 0;
  public static final int STATUS_ACTIVE = 1;
  public static final int STATUS_PASSIVE = 2;

  private static long activeInterval = 3000;
  private static long activePeriod = 30000;
  private static long activeSince = 0;

  private static TaskPuller thisInstance;

  private static boolean isNewTaskAvailable = false;

  private int status = STATUS_PASSIVE;

  private int passiveCounter = 0;
  private static final int PASSIVE_COUNTER_THRESHOLD = 20;

  private TaskPuller() {
    super();
  }

  private TaskPuller(String threadName) {
    super(threadName);
  }

  public static TaskPuller getInstance() {

	  if (thisInstance == null) {
      thisInstance = new TaskPuller("AnnGUI task puller");

      if (DEBUG)
        System.out.println("DEBUG: TaskPuller: Instance created ");
      thisInstance.start();
      if (DEBUG)
          System.out.println("DEBUG: TaskPuller: Instance started ");

	}
    return thisInstance;
  }

  private void setStatus(int newStatus) {
    if (DEBUG)
      System.out.println("DEBUG: TaskPuller.setStatus(newStatus=" + newStatus
          + ")");
    if (newStatus != STATUS_ACTIVE && newStatus != STATUS_PASSIVE) {
      throw new InvalidParameterException("Invalid status: " + newStatus);
    }
    if (newStatus == STATUS_ACTIVE) {
      activeSince = System.currentTimeMillis();
    }
    if (newStatus == STATUS_PASSIVE) {
      passiveCounter = 0;
    }
    status = newStatus;
  }

  public void activate() {
	  if (DEBUG)
     System.out.println("DEBUG: TaskPuller: Instance activated ");
    setStatus(STATUS_ACTIVE);
  }

  public void passivate() {
	  setStatus(STATUS_PASSIVE);
	  /*
	  Thread moribund = thisInstance;
	  thisInstance = null;
      moribund.interrupt();
      */
	//thisInstance.interrupt();

  }


  public int getStatus() {
    return status;
  }

  public void run() {
	    while (!interrupted()) {
	      if (DEBUG_DETAILS) {
	        System.out.println("DEBUG: TaskPuller.run(): next turn...");
	      }
	      try {
	        sleep(activeInterval);
	        if (AnnotatorGUI.getConnection() == null) {
	          if (DEBUG_DETAILS) {
	            System.out
	                .println("DEBUG: TaskPuller: No any connection. Do nothing.");
	          }
	          continue;
	        }
	        if (!(AnnotatorGUI.getConnection() instanceof ExecutiveConnection)) {
	          if (DEBUG_DETAILS) {
	            System.out
	                .println("DEBUG: TaskPuller: Connection is not an ExecutiveConnecton. Do nothing.");
	          }
	          continue;
	        }
	        if (ConnectToExecutiveDialog.getExistingInstance() != null) {
	          if (DEBUG_DETAILS) {
	            System.out
	                .println("DEBUG: TaskPuller: Existing ConnectToExecutiveDialog instance detected. Do nothing.");
	          }
	          continue;
	        }
	        if (((ExecutiveConnection) AnnotatorGUI.getConnection())
	            .getCurrentAnnotatorTask() != null) {
	          setStatus(STATUS_PASSIVE);
	          if (DEBUG_DETAILS) {
	            System.out
	                .println("DEBUG: TaskPuller: There is a task opened. Switched to passive mode and do nothing.");
	          }
	          continue;
	        }

	        if (isNewTaskAvailable) {
		          setStatus(STATUS_PASSIVE);
		          if (DEBUG_DETAILS) {
		            System.out
		                .println("DEBUG: TaskPuller: There is a task opened. Switched to passive mode and do nothing.");
		          }
		          continue;
		        }
	        if (getStatus() == STATUS_PASSIVE) {
	          /*
	        	if (passiveCounter++ > PASSIVE_COUNTER_THRESHOLD) {
	            passiveCounter = 0;
	            if (DEBUG) {
	              System.out
	                  .println("DEBUG: TaskPuller: Passive mode. Checking new tasks.");
	            }
	            //ActionCheckNewTasks.getInstance().actionPerformed(null);
	          }
	          */
	          continue;
	        }
	        if (getStatus() == STATUS_ACTIVE) {
	          if (DEBUG) {
	            System.out.println("DEBUG: TaskPuller: Active mode since: "
	                + activeSince + ", current millis: "
	                + System.currentTimeMillis() + ", diff="
	                + (System.currentTimeMillis() - activeSince) + ".");
	          }
	          if ((System.currentTimeMillis() - activeSince) > activePeriod) {
	            setStatus(STATUS_PASSIVE);
	            if (DEBUG) {
	              System.out
	                  .println("DEBUG: TaskPuller: Active period is over. Switched to passive mode.");
	            }
	            MessageDialog m = new MessageDialog(MainFrame.getInstance(),
	                "No tasks available", "No tasks available.", 7000);
	            m.setVisible(true);
	            continue;
	          }
	          if (DEBUG) {
	            System.out
	                .println("DEBUG: TaskPuller: Active mode. Checking new tasks.");
	          }
	          //ActionCheckNewTasks.getInstance().actionPerformed(null);


	          acceptTask();
	          //setStatus(STATUS_PASSIVE);
	          continue;
	        }
	        System.out
	            .println("Annotator GUI: TaskPuller: Internal error. Invalid status: "
	                + getStatus());
	        interrupt();
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      }

	    }
	    if (DEBUG) {
	      System.out.println("DEBUG: TaskPuller.run(): method finished");
	    }
	  }




  public void acceptTask() {
		AnnotatorTask task = null;
		ExecutiveConnection connection = ((ExecutiveConnection)AnnotatorGUI.getConnection());
		if(connection!=null){
		try {
			MainFrame.getInstance().setBottomStatus(
					"Checking for Annotation Task ...");
			isNewTaskAvailable = connection.checkForNextTask();
          System.out.println("isNewTaskAvailable "+isNewTaskAvailable);
			if (isNewTaskAvailable) {

			MainFrame.getInstance().setBottomStatus(
					"Getting Annotator Task...");
			Thread.sleep(10000);
			task = connection.getNextAnnotatorTask();

			if (task != null) {
				System.out.println("task is ok ");
				MainFrame.getInstance().setBottomStatus(
				"Loading Annotator Task...");
				URI owlimServiceURL = task.getTask()
						.getOwlimServiceURL();
				String repositoryName = task.getTask()
						.getOwlimRepositoryName();

				if (owlimServiceURL != null && repositoryName != null
						&& repositoryName.trim().length() > 0) {

					MainFrame
							.getInstance()
							.setBottomStatus(
									"Loading an ontology for Annotator Task...");
					try {
						task.loadOntology();
					} catch (Exception e) {
						/*
						handleError(new AnnotatorGUIExeption(
								"Failed to load ontology. Unexpected error:\n\n"
										+ e.getClass().getName()
										+ " occured. Message:\n"
										+ e.getMessage(), e));
					   */
					}
				}
/*
				if (Thread.currentThread().isInterrupted()) {
					if (task != null)
						task.cleanup();
					//buttonConnect.setEnabled(true);
					return;
				}

*/
			} else {
				MainFrame.getInstance().setBottomStatus(
						"No tasks available.");
					MessageDialog m = new MessageDialog(MainFrame
							.getInstance(), "No tasks available",
							"No tasks available.", 7000);
					m.setVisible(true);
					// JOptionPane.showMessageDialog(null, "No tasks
					// available.",
					// "Message", JOptionPane.PLAIN_MESSAGE);}

			}
			}
			else {
				MainFrame.getInstance().setBottomStatus(
						"No tasks available.");

					MessageDialog m = new MessageDialog(MainFrame
							.getInstance(), "No tasks available",
							"No tasks available.", 7000);
					m.setVisible(true);
					// JOptionPane.showMessageDialog(null, "No tasks
					// available.",
					// "Message", JOptionPane.PLAIN_MESSAGE);}

			}

			return;
		} catch (RichUIException e) {

		} catch (Throwable e) {
			if (task != null)
				try {
					task.cleanup();
				} catch (RichUIException e1) {
				}

		}
	   }
		else {
			MainFrame.getInstance().setBottomStatus(
			"Connection lost.");

		MessageDialog m = new MessageDialog(MainFrame
				.getInstance(), "Connection lost",
				"Please reconnect.", 5000);
		m.setVisible(true);
		// JOptionPane.showMessageDialog(null, "No tasks
		// available.",
		// "Message", JOptionPane.PLAIN_MESSAGE);}

		}

	}

}


