/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCControlMatrixEditor extends JFrame {
	
	/*
	 * Attempt to mimic the table header renderer
	 */
	private class RowNumberRenderer extends DefaultTableCellRenderer {
		public RowNumberRenderer() {
			setHorizontalAlignment(JLabel.LEFT);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					if(row == _myOverRow) {
						setBackground(Color.RED);
					}else {

						setBackground(Color.LIGHT_GRAY);
					}
					setFont(CCUIConstants.ARIAL_BOLD_10);
				}
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}
	
	/*
	 *	Use a JTable as a renderer for row numbers of a given main table.
	 *  This table must be added to the row header of the scrollpane that
	 *  contains the main table.
	 */
	public class CCControlMatrixInputs extends JTable {

		public CCControlMatrixInputs() {
			_myTable.addPropertyChangeListener(e ->{
				switch(e.getPropertyName()) {
				case "selectionModel":
					setSelectionModel(_myTable.getSelectionModel());
					break;
				case "rowHeight":
					repaint();
					break;
				case "model":
					_myTable.getModel().addTableModelListener(this);
					revalidate();
					break;
				}
			});
			_myTable.getModel().addTableModelListener(e -> {
				revalidate();
			});
			
			setFocusable(false);
			setAutoCreateColumnsFromModel(false);
			setSelectionModel(_myTable.getSelectionModel());

			TableColumn column = new TableColumn();
			column.setHeaderValue(" ");
			addColumn(column);
			column.setCellRenderer(new RowNumberRenderer());

			getColumnModel().getColumn(0).setPreferredWidth(150);
			setPreferredScrollableViewportSize(getPreferredSize());
		}

		@Override
		public void addNotify() {
			super.addNotify();

			Component c = getParent();

			// Keep scrolling of the row table in sync with the main table.

			if (c instanceof JViewport) {
				JViewport viewport = (JViewport) c;
				viewport.addChangeListener(e -> {
					// Keep the scrolling of the row table in sync with main table
					JScrollPane scrollPane = (JScrollPane) viewport.getParent();
					scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
				});
			}
		}

		/*
		 * Delegate method to main table
		 */
		@Override
		public int getRowCount() {
			return _myTable.getRowCount();
		}

		@Override
		public int getRowHeight(int row) {
			int rowHeight = _myTable.getRowHeight(row);

			if (rowHeight != super.getRowHeight(row)) {
				super.setRowHeight(row, rowHeight);
			}

			return rowHeight;
		}

		/*
		 * No model is being used for this table so just use the row number as the
		 * value of the cell.
		 */
		@Override
		public Object getValueAt(int row, int column) {
			if(_myControlMatrix == null)return row;
			return _myControlMatrix.inputs()[row];
		}

		/*
		 * Don't edit data in the main TableModel by mistake
		 */
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		/*
		 * Do nothing since the table ignores the model
		 */
		@Override
		public void setValueAt(Object value, int row, int column) {}	
	}
	
	private class CCControlMatrixTableModel implements TableModel{

		@Override
		public int getRowCount() {
			return _myControlMatrix.rows();
		}

		@Override
		public int getColumnCount() {
			return _myControlMatrix.columns();
		}

		@Override
		public String getColumnName(int columnIndex) {
			return _myControlMatrix.outputs()[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Double.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _myControlMatrix.get(columnIndex, rowIndex)[0];
		}

		@Override
		public void setValueAt(Object theValue, int rowIndex, int columnIndex) {
			_myControlMatrix.set(columnIndex, rowIndex, (Double)theValue);
		}

		@Override
		public void addTableModelListener(TableModelListener l) {}

		@Override
		public void removeTableModelListener(TableModelListener l) {}
		
	}
	
	private JTable _myTable;
	private CCControlMatrix _myControlMatrix;
	
	private int _myOverRow = -1;
	private int _myOverColumn = -1;

	public CCControlMatrixEditor(String theTitle, CCControlMatrix theMatrix) {
		super(theTitle);
		_myControlMatrix = theMatrix;
		
		if(theMatrix == null) {
			_myTable = new JTable(4,4);
			for(int c = 0; c < 4;c++) {
				for(int r = 0; r < 4;r++) {
					_myTable.setValueAt(CCMath.random(), r, c);
				}
			}
		}else {
			_myTable = new JTable(new CCControlMatrixTableModel());
		}
		
		JScrollPane scrollPane = new JScrollPane(_myTable);
		JTable rowTable = new CCControlMatrixInputs();
		scrollPane.setRowHeaderView(rowTable);
		scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());
		
		_myTable.setEnabled(false);
		_myTable.setGridColor( Color.LIGHT_GRAY );
		_myTable.setColumnSelectionAllowed( false );
		_myTable.setRowSelectionAllowed( false );
		_myTable.setIntercellSpacing( new Dimension(2, 2) );
		_myTable.getTableHeader().setFont(CCUIConstants.ARIAL_BOLD_10);
		_myTable.setFont(CCUIConstants.ARIAL_9);
		_myTable.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				_myOverRow = _myTable.rowAtPoint(e.getPoint());
				_myOverColumn = _myTable.columnAtPoint(e.getPoint());
				CCLog.info(_myOverRow + ":" + _myOverColumn);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	
		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		containerPanel.setLayout(new BorderLayout());
		// panel to test
		JPanel testPanel = new JPanel();
		testPanel.setBackground(Color.blue);
		containerPanel.add(scrollPane, BorderLayout.CENTER);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(containerPanel, BorderLayout.CENTER);

		pack();
	}
	
	public void render(){
//		_myCurvePanel.render();
	}
	
	public void update(){
//		_myCurvePanel.update();
	}

	public double value(double theIn) {
		return 0;
//		return _myCurvePanel.value(theIn);
	}

//	public SwingCurvePanel panel() {
////		return _myCurvePanel;
//	}

	public static void main(String[] args) {
		CCControlMatrixEditor myFrame = new CCControlMatrixEditor("check it", null);
		myFrame.setVisible(true);
	}
}
