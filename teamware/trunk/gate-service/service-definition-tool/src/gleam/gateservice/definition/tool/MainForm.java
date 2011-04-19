/*
 *  MainForm.java
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
package gleam.gateservice.definition.tool;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class MainForm extends JPanel
{
   JTabbedPane jtabbedpane1 = new JTabbedPane();
   JTabbedPane jtabbedpane2 = new JTabbedPane();
   JTree applicationTree = new JTree();
   JButton attachParamButton = new JButton();
   JButton detachParamButton = new JButton();
   JButton addGasParameterButton = new JButton();
   JButton removeGasParameterButton = new JButton();
   JTable gasParametersTable = new JTable();
   JList paramMappingsList = new JList();
   JButton addParamMappingButton = new JButton();
   JButton removeParamMappingButton = new JButton();
   JList featureMappingsList = new JList();
   JButton addFeatureMappingButton = new JButton();
   JButton removeFeatureMappingButton = new JButton();
   JList inputSetsList = new JList();
   JButton addInputSetButton = new JButton();
   JButton removeInputSetButton = new JButton();
   JList outputSetsList = new JList();
   JButton addOutputSetButton = new JButton();
   JButton removeOutputSetButton = new JButton();

   /**
    * Default constructor
    */
   public MainForm()
   {
      initializePanel();
   }

   /**
    * Adds fill components to empty cells in the first row and first column of the grid.
    * This ensures that the grid spacing will be the same as shown in the designer.
    * @param cols an array of column indices in the first row where fill components should be added.
    * @param rows an array of row indices in the first column where fill components should be added.
    */
   void addFillComponents( Container panel, int[] cols, int[] rows )
   {
      Dimension filler = new Dimension(10,10);

      boolean filled_cell_11 = false;
      CellConstraints cc = new CellConstraints();
      if ( cols.length > 0 && rows.length > 0 )
      {
         if ( cols[0] == 1 && rows[0] == 1 )
         {
            /** add a rigid area  */
            panel.add( Box.createRigidArea( filler ), cc.xy(1,1) );
            filled_cell_11 = true;
         }
      }

      for( int index = 0; index < cols.length; index++ )
      {
         if ( cols[index] == 1 && filled_cell_11 )
         {
            continue;
         }
         panel.add( Box.createRigidArea( filler ), cc.xy(cols[index],1) );
      }

      for( int index = 0; index < rows.length; index++ )
      {
         if ( rows[index] == 1 && filled_cell_11 )
         {
            continue;
         }
         panel.add( Box.createRigidArea( filler ), cc.xy(1,rows[index]) );
      }

   }

   /**
    * Helper method to load an image file from the CLASSPATH
    * @param imageName the package and name of the file to load relative to the CLASSPATH
    * @return an ImageIcon instance with the specified image file
    * @throws IllegalArgumentException if the image resource cannot be loaded.
    */
   public ImageIcon loadImage( String imageName )
   {
      try
      {
         ClassLoader classloader = getClass().getClassLoader();
         java.net.URL url = classloader.getResource( imageName );
         if ( url != null )
         {
            ImageIcon icon = new ImageIcon( url );
            return icon;
         }
      }
      catch( Exception e )
      {
         e.printStackTrace();
      }
      throw new IllegalArgumentException( "Unable to load image: " + imageName );
   }

   public JPanel createPanel()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)","FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      jtabbedpane1.addTab("Parameters",null,createPanel1());
      jtabbedpane1.addTab("Annotation Sets",null,createPanel8());
      jpanel1.add(jtabbedpane1,cc.xy(1,1));

      addFillComponents(jpanel1,new int[0],new int[0]);
      return jpanel1;
   }

   public JPanel createPanel1()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)","FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      jtabbedpane2.addTab("Application view",null,createPanel2());
      jtabbedpane2.addTab("Service view",null,createPanel3());
      jpanel1.add(jtabbedpane2,cc.xy(1,1));

      addFillComponents(jpanel1,new int[0],new int[0]);
      return jpanel1;
   }

   public JPanel createPanel2()
   {
      JPanel jpanel1 = new JPanel();
      EmptyBorder emptyborder1 = new EmptyBorder(4,4,4,4);
      jpanel1.setBorder(emptyborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      applicationTree.setName("applicationTree");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(applicationTree);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      attachParamButton.setActionCommand("Attach to parameter");
      attachParamButton.setEnabled(false);
      attachParamButton.setName("attachParamButton");
      attachParamButton.setText("Attach to parameter");
      jpanel1.add(attachParamButton,cc.xy(3,1));

      detachParamButton.setActionCommand("Detach");
      detachParamButton.setEnabled(false);
      detachParamButton.setName("detachParamButton");
      detachParamButton.setText("Detach");
      jpanel1.add(detachParamButton,cc.xy(3,3));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   public JPanel createPanel3()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)","FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      jpanel1.add(createPanel4(),cc.xy(1,1));
      addFillComponents(jpanel1,new int[]{ 1 },new int[]{ 1 });
      return jpanel1;
   }

   public JPanel createPanel4()
   {
      JPanel jpanel1 = new JPanel();
      EmptyBorder emptyborder1 = new EmptyBorder(4,4,4,4);
      TitledBorder titledborder1 = new TitledBorder(null,"GaS parameter",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(49,106,196));
      Border border1 = BorderFactory.createCompoundBorder(emptyborder1,titledborder1);
      jpanel1.setBorder(border1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:8DLU:NONE,FILL:DEFAULT:GROW(1.0)","FILL:DEFAULT:GROW(1.0),CENTER:4DLU:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      jpanel1.add(createPanel5(),cc.xywh(1,1,3,1));
      jpanel1.add(createPanel6(),cc.xy(1,3));
      jpanel1.add(createPanel7(),cc.xy(3,3));
      addFillComponents(jpanel1,new int[]{ 1,2,3 },new int[]{ 1,2,3 });
      return jpanel1;
   }

   public JPanel createPanel5()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      addGasParameterButton.setActionCommand("Add");
      addGasParameterButton.setName("addGasParameterButton");
      addGasParameterButton.setRolloverEnabled(true);
      addGasParameterButton.setText("Add");
      jpanel1.add(addGasParameterButton,cc.xy(3,1));

      removeGasParameterButton.setActionCommand("Remove");
      removeGasParameterButton.setEnabled(false);
      removeGasParameterButton.setName("removeGasParameterButton");
      removeGasParameterButton.setRolloverEnabled(true);
      removeGasParameterButton.setText("Remove");
      jpanel1.add(removeGasParameterButton,cc.xy(3,3));

      gasParametersTable.setName("gasParametersTable");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(gasParametersTable);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   public JPanel createPanel6()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"PR parameters",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(49,106,196));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      paramMappingsList.setName("paramMappingsList");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(paramMappingsList);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      addParamMappingButton.setActionCommand("Add");
      addParamMappingButton.setEnabled(false);
      addParamMappingButton.setName("addParamMappingButton");
      addParamMappingButton.setRolloverEnabled(true);
      addParamMappingButton.setText("Add");
      jpanel1.add(addParamMappingButton,cc.xy(3,1));

      removeParamMappingButton.setActionCommand("Remove");
      removeParamMappingButton.setEnabled(false);
      removeParamMappingButton.setName("removeParamMappingButton");
      removeParamMappingButton.setRolloverEnabled(true);
      removeParamMappingButton.setText("Remove");
      jpanel1.add(removeParamMappingButton,cc.xy(3,3));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   public JPanel createPanel7()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"Document features",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(49,106,196));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      featureMappingsList.setName("featureMappingsList");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(featureMappingsList);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      addFeatureMappingButton.setActionCommand("Add");
      addFeatureMappingButton.setEnabled(false);
      addFeatureMappingButton.setName("addFeatureMappingButton");
      addFeatureMappingButton.setRolloverEnabled(true);
      addFeatureMappingButton.setText("Add");
      jpanel1.add(addFeatureMappingButton,cc.xy(3,1));

      removeFeatureMappingButton.setActionCommand("Remove");
      removeFeatureMappingButton.setEnabled(false);
      removeFeatureMappingButton.setName("removeFeatureMappingButton");
      removeFeatureMappingButton.setRolloverEnabled(true);
      removeFeatureMappingButton.setText("Remove");
      jpanel1.add(removeFeatureMappingButton,cc.xy(3,3));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   public JPanel createPanel8()
   {
      JPanel jpanel1 = new JPanel();
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0)","FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      jpanel1.add(createPanel9(),cc.xy(1,1));
      jpanel1.add(createPanel10(),cc.xy(1,2));
      addFillComponents(jpanel1,new int[]{ 1 },new int[]{ 1,2 });
      return jpanel1;
   }

   public JPanel createPanel9()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"Input sets",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(49,106,196));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      inputSetsList.setName("inputSetsList");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(inputSetsList);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      addInputSetButton.setActionCommand("Add");
      addInputSetButton.setName("addInputSetButton");
      addInputSetButton.setRolloverEnabled(true);
      addInputSetButton.setText("Add");
      jpanel1.add(addInputSetButton,cc.xy(3,1));

      removeInputSetButton.setActionCommand("Remove");
      removeInputSetButton.setEnabled(false);
      removeInputSetButton.setName("removeInputSetButton");
      removeInputSetButton.setRolloverEnabled(true);
      removeInputSetButton.setText("Remove");
      jpanel1.add(removeInputSetButton,cc.xy(3,3));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   public JPanel createPanel10()
   {
      JPanel jpanel1 = new JPanel();
      TitledBorder titledborder1 = new TitledBorder(null,"Output sets",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,null,new Color(49,106,196));
      jpanel1.setBorder(titledborder1);
      FormLayout formlayout1 = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE","CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
      CellConstraints cc = new CellConstraints();
      jpanel1.setLayout(formlayout1);

      outputSetsList.setName("outputSetsList");
      JScrollPane jscrollpane1 = new JScrollPane();
      jscrollpane1.setViewportView(outputSetsList);
      jscrollpane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      jscrollpane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jpanel1.add(jscrollpane1,cc.xywh(1,1,1,4));

      addOutputSetButton.setActionCommand("Add");
      addOutputSetButton.setName("addOutputSetButton");
      addOutputSetButton.setRolloverEnabled(true);
      addOutputSetButton.setText("Add");
      jpanel1.add(addOutputSetButton,cc.xy(3,1));

      removeOutputSetButton.setActionCommand("Remove");
      removeOutputSetButton.setEnabled(false);
      removeOutputSetButton.setName("removeOutputSetButton");
      removeOutputSetButton.setRolloverEnabled(true);
      removeOutputSetButton.setText("Remove");
      jpanel1.add(removeOutputSetButton,cc.xy(3,3));

      addFillComponents(jpanel1,new int[]{ 2 },new int[]{ 2,3,4 });
      return jpanel1;
   }

   /**
    * Initializer
    */
   protected void initializePanel()
   {
      setLayout(new BorderLayout());
      add(createPanel(), BorderLayout.CENTER);
   }


}
