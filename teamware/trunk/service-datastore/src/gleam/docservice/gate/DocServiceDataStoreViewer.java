/*
 *  DocServiceDataStoreViewer.java
 *  
 *  Copyright (c) 1998-2006, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Andrey Shafirin 23/05/2006
 *
 *  $Id$
 *
 */

package gleam.docservice.gate;

import gate.CreoleRegister;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.VisualResource;
import gate.creole.AbstractResource;
import gate.creole.ResourceData;
import gate.creole.ResourceInstantiationException;
import gate.event.DatastoreEvent;
import gate.event.DatastoreListener;
import gate.gui.Handle;
import gate.gui.MainFrame;
import gate.gui.NameBearerHandle;
import gate.persist.PersistenceException;
import gate.security.SecurityException;
import gate.util.Err;
import gate.util.GateRuntimeException;
import gate.util.Strings;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

/**
 * This is a GATE visual resource for the DataStore implemented as GLEM service
 * data store.
 * </p>
 */
public class DocServiceDataStoreViewer extends JScrollPane implements VisualResource, DatastoreListener {

	DefaultMutableTreeNode treeRoot;

	DefaultTreeModel treeModel;

	JTree mainTree;

	DocServiceDataStore datastore;

	NameBearerHandle myHandle;

	protected FeatureMap features;

	public DocServiceDataStoreViewer() {
	}

	public void cleanup() {
		myHandle = null;
		datastore = null;
	}

	/** Accessor for features. */
	public FeatureMap getFeatures() {
		return features;
	}

	/**
	 * Gets the value of a parameter of this resource.
	 * 
	 * @param paramaterName
	 *          the name of the parameter
	 * @return the current value of the parameter
	 */
	public Object getParameterValue(String paramaterName) throws ResourceInstantiationException {
		return AbstractResource.getParameterValue(this, paramaterName);
	}

	/**
	 * Sets the value for a specified parameter.
	 * 
	 * @param paramaterName
	 *          the name for the parameteer
	 * @param parameterValue
	 *          the value the parameter will receive
	 */
	public void setParameterValue(String paramaterName, Object parameterValue) throws ResourceInstantiationException {
		// get the beaninfo for the resource bean, excluding data about Object
		BeanInfo resBeanInf = null;
		try {
			resBeanInf = Introspector.getBeanInfo(this.getClass(), Object.class);
		} catch (Exception e) {
			throw new ResourceInstantiationException("Couldn't get bean info for resource " + this.getClass().getName()
					+ Strings.getNl() + "Introspector exception was: " + e);
		}
		AbstractResource.setParameterValue(this, resBeanInf, paramaterName, parameterValue);
	}

	/**
	 * Sets the values for more parameters in one step.
	 * 
	 * @param parameters
	 *          a feature map that has paramete names as keys and parameter values
	 *          as values.
	 */
	public void setParameterValues(FeatureMap parameters) throws ResourceInstantiationException {
		AbstractResource.setParameterValues(this, parameters);
	}

	/** Initialise this resource, and return it. */
	public Resource init() throws ResourceInstantiationException {
		return this;
	}

	public void setTarget(Object target) {
		if (target == null) {
			datastore = null;
			return;
		}
		if (target instanceof DocServiceDataStore) {
			datastore = (DocServiceDataStore) target;
			initGuiComponents();
			initListeners();
		} else {
			throw new IllegalArgumentException("DocServiceDatastoreViewers can only be used with DocServiceDataStore!\n"
					+ target.getClass().toString() + " is not a DocServiceDataStore!");
		}
	}

	public void setHandle(Handle handle) {
		if (handle instanceof NameBearerHandle) {
			myHandle = (NameBearerHandle) handle;
		}
	}

	public void resourceAdopted(DatastoreEvent evt) {
		// do nothing
	}

	public void resourceDeleted(DatastoreEvent evt) {
		String resID = (String) evt.getResourceID();
		DefaultMutableTreeNode node = null;
		Enumeration nodesEnum = treeRoot.depthFirstEnumeration();
		boolean found = false;
		while (nodesEnum.hasMoreElements() && !found) {
			node = (DefaultMutableTreeNode) nodesEnum.nextElement();
			Object userObject = node.getUserObject();
			found = userObject instanceof DSEntry && ((DSEntry) userObject).id.equals(resID);
		}
		if (found) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			treeModel.removeNodeFromParent(node);
			if (parent.getChildCount() == 0) treeModel.removeNodeFromParent(parent);
		}
	}

	public void resourceWritten(DatastoreEvent evt) {
		Resource res = evt.getResource();
		String resID = (String) evt.getResourceID();
		String resType = ((ResourceData) Gate.getCreoleRegister().get(res.getClass().getName())).getName();
		DefaultMutableTreeNode parent = treeRoot;
		DefaultMutableTreeNode node = null;
		// first look for the type node
		Enumeration childrenEnum = parent.children();
		boolean found = false;
		while (childrenEnum.hasMoreElements() && !found) {
			node = (DefaultMutableTreeNode) childrenEnum.nextElement();
			found = node.getUserObject().equals(resType);
		}
		if (!found) {
			// exhausted the children without finding the node -> new type
			node = new DefaultMutableTreeNode(resType);
			treeModel.insertNodeInto(node, parent, parent.getChildCount());
		}
		mainTree.expandPath(new TreePath(new Object[] { parent, node }));

		// now look for the resource node
		parent = node;
		childrenEnum = parent.children();
		found = false;
		while (childrenEnum.hasMoreElements() && !found) {
			node = (DefaultMutableTreeNode) childrenEnum.nextElement();
			found = ((DSEntry) node.getUserObject()).id.equals(resID);
		}
		if (!found) {
			// exhausted the children without finding the node -> new resource
			DSEntry entry = new DSEntry(datastore.getLrName(resID), resID, res.getClass().getName());
			node = new DefaultMutableTreeNode(entry, false);
			treeModel.insertNodeInto(node, parent, parent.getChildCount());
		}
	}

	/** Mutator for features */
	public void setFeatures(FeatureMap features) {
		this.features = features;
	}

	protected void fireProgressChanged(int e) {
		// myHandle.fireProgressChanged(e);
	}

	protected void fireProcessFinished() {
		// myHandle.fireProcessFinished();
	}

	protected void fireStatusChanged(String e) {
		// myHandle.fireStatusChanged(e);
	}

	protected void initGuiComponents() {
		treeRoot = new DefaultMutableTreeNode(datastore.getName(), true);
		treeModel = new DefaultTreeModel(treeRoot, true);
		mainTree = new JTree();
		mainTree.setModel(treeModel);
		mainTree.setExpandsSelectedPaths(true);
		mainTree.expandPath(new TreePath(treeRoot));
		try {
			Iterator lrTypesIter = datastore.getLrTypes().iterator();
			CreoleRegister cReg = Gate.getCreoleRegister();
			while (lrTypesIter.hasNext()) {
				String type = (String) lrTypesIter.next();
				ResourceData rData = (ResourceData) cReg.get(type);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(rData.getName());
				treeModel.insertNodeInto(node, treeRoot, treeRoot.getChildCount());
				mainTree.expandPath(new TreePath(new Object[] { treeRoot, node }));
				Iterator lrIDsIter = datastore.getLrIds(type).iterator();
				while (lrIDsIter.hasNext()) {
					String id = (String) lrIDsIter.next();
					DSEntry entry = new DSEntry(datastore.getLrName(id), id, type);
					DefaultMutableTreeNode lrNode = new DefaultMutableTreeNode(entry, false);
					treeModel.insertNodeInto(lrNode, node, node.getChildCount());
					node.add(lrNode);
				}
			}
		} catch (PersistenceException pe) {
			throw new GateRuntimeException(pe.toString());
		}
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
		mainTree.setSelectionModel(selectionModel);
		getViewport().setView(mainTree);
	}

	protected void initListeners() {
		datastore.addDatastoreListener(this);
		mainTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// where inside the tree?
				TreePath path = mainTree.getPathForLocation(e.getX(), e.getY());
				Object value = null;
				if (path != null) value = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

				if (SwingUtilities.isRightMouseButton(e)) {
					// right click
					if (value != null && value instanceof DSEntry) {
						JPopupMenu popup = ((DSEntry) value).getPopup();
						popup.show(DocServiceDataStoreViewer.this, e.getX(), e.getY());
					}
				} else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					// double click -> just load the resource
					if (value != null && value instanceof DSEntry) {
						new LoadAction((DSEntry) value).actionPerformed(null);
					}
				}
			}
		});
	}

	class DSEntry {
		DSEntry(String name, String id, String type) {
			this.name = name;
			this.type = type;
			this.id = id;
			popup = new JPopupMenu();
			popup.add(new LoadAction(this));
			popup.add(new DeleteAction(this));
		}

		public String toString() {
			return name;
		}

		public JPopupMenu getPopup() {
			return popup;
		}

		String name;

		String type;

		String id;

		JPopupMenu popup;
	}

	class LoadAction extends AbstractAction {
		LoadAction(DSEntry entry) {
			super("Load");
			this.entry = entry;
		}

		public void actionPerformed(ActionEvent e) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						MainFrame.lockGUI("Loading " + entry.name);
						long start = System.currentTimeMillis();
						fireStatusChanged("Loading " + entry.name);
						fireProgressChanged(0);
						datastore.getLr(entry.type, entry.id);
						fireProgressChanged(0);
						fireProcessFinished();
						long end = System.currentTimeMillis();
						fireStatusChanged(entry.name + " loaded in "
								+ NumberFormat.getInstance().format((double) (end - start) / 1000) + " seconds");
					} catch (Exception e) {
						MainFrame.unlockGUI();
						JOptionPane.showMessageDialog(DocServiceDataStoreViewer.this, "Error!\n" + e.toString(), "GATE",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace(Err.getPrintWriter());
						fireProgressChanged(0);
						fireProcessFinished();
					} finally {
						MainFrame.unlockGUI();
					}
				}
			};
			Thread thread = new Thread(Thread.currentThread().getThreadGroup(), runnable, "Loader from DS");
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}

		DSEntry entry;
	}

	class DeleteAction extends AbstractAction {
		DeleteAction(DSEntry entry) {
			super("Delete");
			this.entry = entry;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				datastore.delete(entry.type, entry.id);
			} catch (gate.persist.PersistenceException pe) {
				JOptionPane.showMessageDialog(DocServiceDataStoreViewer.this, "Error!\n" + pe.toString(), "GATE",
						JOptionPane.ERROR_MESSAGE);
				pe.printStackTrace(Err.getPrintWriter());
			} catch (SecurityException se) {
				JOptionPane.showMessageDialog(DocServiceDataStoreViewer.this, "Error!\n" + se.toString(), "GATE",
						JOptionPane.ERROR_MESSAGE);
				se.printStackTrace(Err.getPrintWriter());
			}
		}

		DSEntry entry;
	}
}
