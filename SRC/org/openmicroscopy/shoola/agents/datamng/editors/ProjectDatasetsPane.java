/*
 * org.openmicroscopy.shoola.agents.datamng.editors.ProjectDatasetsTab
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.datamng.editors;

//Java imports
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.datamng.DataManager;
import org.openmicroscopy.shoola.env.data.model.DatasetSummary;
import org.openmicroscopy.shoola.util.ui.TableComponent;
import org.openmicroscopy.shoola.util.ui.TableComponentCellEditor;
import org.openmicroscopy.shoola.util.ui.TableComponentCellRenderer;

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
class ProjectDatasetsPane
	extends JPanel
{
	
	private static final int		POS_ONE = 0, POS_TWO = 1, POS_THREE = 2,
									POS_FOUR = 3;
	
	private static final Dimension	VP_DIM = new Dimension(200, 70);
	
	private static final Dimension	BOX_H = new Dimension(10, 0),
									BOX_V = new Dimension(0, 10);
	
	/** Reference to the manager. */
	private ProjectEditorManager	manager;

	private JButton					removeButton, cancelButton, addButton;
	
	private JPanel					tableToAddPanel, buttonsPanel, tablePanel;
	
	private DatasetsTableModel 		datasetsTM;
	
	private List					datasetsToAdd;
	
	private List					listDatasets;
	
	ProjectDatasetsPane(ProjectEditorManager manager)
	{
		this.manager = manager;
		datasetsToAdd = new ArrayList();
		buildGUI();
	}

	JButton getAddButton()
	{
		return addButton;
	}
	
	JButton getRemoveButton()
	{
		return removeButton;
	}
	
	JButton getCancelButton()
	{
		return cancelButton;
	}
	
	/** Select or not all datasets. */
	void setSelection(Object val)
	{
		int countCol = datasetsTM.getColumnCount()-1;
		for (int i = 0; i < datasetsTM.getRowCount(); i++)
			datasetsTM.setValueAt(val, i, countCol);
	}
	
	/** Rebuild the component if some datasets are marked to be added. */ 
	void buildComponent(List l)
	{
		datasetsToAdd = l;
		removeAll();
		tableToAddPanel = buildTableToAddPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tablePanel, POS_ONE);
		add(tableToAddPanel, POS_TWO);
		add(Box.createRigidArea(BOX_V), POS_THREE);
		add(buttonsPanel, POS_FOUR);
		Border b = BorderFactory.createEmptyBorder(0, 0, 10, 10);
		setBorder(b);
	}
	
	/** Build and layout the GUI. */
	private void buildGUI()
	{
		listDatasets = manager.getProjectData().getDatasets();
		tablePanel = buildTablePanel();
		buttonsPanel = buildButtonsPanel();
		tableToAddPanel = buildTableToAddPanel();
		JPanel p = new JPanel();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tablePanel, POS_ONE);
		add(tableToAddPanel, POS_TWO);
		add(Box.createRigidArea(BOX_V), POS_THREE);
		add(buttonsPanel, POS_FOUR);
		Border b = BorderFactory.createEmptyBorder(0, 0, 10, 10);
		setBorder(b);
	}

	/** Initializes and build panel containing the buttons. */
	private JPanel buildButtonsPanel()
	{
		JPanel controls = new JPanel();
		//remove button
		removeButton = new JButton("Remove All");
		removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removeButton.setToolTipText("Remove all datasets");
		
		//cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cancelButton.setToolTipText("Cancel selection");
		
		addButton = new JButton("Add");
		addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addButton.setToolTipText("Add datasets to the project");
		
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		controls.add(cancelButton);
		controls.add(Box.createRigidArea(BOX_H));
		controls.add(removeButton);
		controls.add(Box.createRigidArea(BOX_H));
		controls.add(addButton);
		controls.setOpaque(false); //make panel transparent
		
		if (listDatasets == null || listDatasets.size() == 0) {
			removeButton.setEnabled(false);
			cancelButton.setEnabled(false);
			addButton.setEnabled(false);
		}
		return controls;
	}
	
	/** Build panel with table containing the datasets to add. */
	private JPanel buildTableToAddPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		if (datasetsToAdd.size() != 0) {
			p.add(buildLabelTable());
			DatasetsAddTableModel datasetsAddTM = new DatasetsAddTableModel();
			JTable table = new JTable(datasetsAddTM);
			table.setBackground(DataManager.STEELBLUE);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setPreferredScrollableViewportSize(VP_DIM);
			//wrap table in a scroll pane and add it to the panel
			JScrollPane spAdd = new JScrollPane(table);
			p.add(spAdd);
		}
		return p;
	}
	
	/** Build panel with table containing existing datasets. */
	private JPanel buildTablePanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		//datasets table
		datasetsTM = new DatasetsTableModel();
		JTable t = new JTable(datasetsTM);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setPreferredScrollableViewportSize(VP_DIM);
		//wrap table in a scroll pane and add it to the panel
		JScrollPane sp = new JScrollPane(t);
		p.add(sp);
		return p;
	}
	
	
	private JTable buildLabelTable()
	{
		JTable table = new TableComponent(1, 3);
		table.setTableHeader(null);
		table.setOpaque(false);
		table.setShowGrid(false);
		table.setRowHeight(25);
		//First row.
		JLabel label = new JLabel(" Datasets to add");
		table.setValueAt(label, 0, 0);
		label = new JLabel("");
		table.setValueAt(label, 0, 1);
		table.setValueAt(label, 0, 2);
		table.setDefaultRenderer(JComponent.class, 
								new TableComponentCellRenderer());
		table.setDefaultEditor(JComponent.class, 
								new TableComponentCellEditor());
		return table;
	}
	
	/** 
	 * A <code>3</code>-column table model to view the summary of 
	 * datasets contained in the project.
	 * The first column contains the datasets ID and the 
	 * second column the names. Cells are not editable. 
	 */
	private class DatasetsTableModel
		extends AbstractTableModel
	{
		private final String[]	columnNames = {"ID", "Name", "Remove"};
		private final Object[]	datasets = listDatasets.toArray();
		private Object[][] 		data = new Object[datasets.length][3];
		private Map datasetSummaries;

		private DatasetsTableModel()
		{
			datasetSummaries = new HashMap();
			DatasetSummary ds;
			for (int i = 0; i < datasets.length; i++) {
				ds = (DatasetSummary) datasets[i];
				String sID = ""+ ds.getID();
				data[i][0] = sID;
				data[i][1] = ds.getName();
				data[i][2] = new Boolean(false);
				datasetSummaries.put(sID, ds);
			}
		}
	
		public int getColumnCount() { return 3; }
	
		public int getRowCount() { return datasets.length; }
	
		public String getColumnName(int col) { return columnNames[col]; }
		
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		public Object getValueAt(int row, int col) { return data[row][col]; }

		public boolean isCellEditable(int row, int col)
		{ 
			boolean isEditable = false;
			if (col == 2) isEditable = true;
			return isEditable;
		}
		
		public void setValueAt(Object value, int row, int col)
		{
			data[row][col]= value;
			fireTableCellUpdated(row, col);
			boolean b = ((Boolean) value).booleanValue();
			DatasetSummary ds = (DatasetSummary) 
								datasetSummaries.get((String) data[row][0]);
			manager.selectDataset(b, ds);
		}
	}
	
	/** 
	 * A <code>3</code>-column table model to view the summary of 
	 * datasets to be added to the project.
	 * The first column contains the datasets ID and the 
	 * second column the names. Cells are not editable. 
	 */
	private class DatasetsAddTableModel
		extends AbstractTableModel
	{
		private final String[]	columnNames = {"ID", "Name", "Remove"};
		private final Object[]	datasets = datasetsToAdd.toArray();
		private Object[][]		data = new Object[datasets.length][3];
		private Map 			datasetSummaries;

		private DatasetsAddTableModel()
		{
			datasetSummaries = new HashMap();
			DatasetSummary ds;
			for (int i = 0; i < datasets.length; i++) {
				ds = (DatasetSummary) datasets[i];
				String sID = ""+ ds.getID();
				data[i][0] = sID;
				data[i][1] = ds.getName();
				data[i][2] = new Boolean(false);
				datasetSummaries.put(sID, ds);
			}
		}

		public int getColumnCount() { return 3; }

		public int getRowCount() { return datasets.length; }

		public String getColumnName(int col) { return columnNames[col]; }
	
		public Class getColumnClass(int c)
		{ 
			return getValueAt(0, c).getClass();
		}

		public Object getValueAt(int row, int col) { return data[row][col]; }

		public boolean isCellEditable(int row, int col)
		{ 
			boolean isEditable = false;
			if (col == 2) isEditable = true;
			return isEditable;
		}
	
		public void setValueAt(Object value, int row, int col)
		{
			data[row][col]= value;
			fireTableCellUpdated(row, col);
			boolean b = ((Boolean) value).booleanValue();
			DatasetSummary ds = (DatasetSummary) 
								datasetSummaries.get((String) data[row][0]);
			manager.updateAddSelection(b, ds);
		}
	}

}
