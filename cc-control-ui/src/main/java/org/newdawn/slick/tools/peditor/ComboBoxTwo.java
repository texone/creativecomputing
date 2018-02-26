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
package org.newdawn.slick.tools.peditor;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
 
public class ComboBoxTwo extends JPanel implements ActionListener
{
    private JComboBox<String> mainComboBox;
    private JComboBox<String> subComboBox;
    private Hashtable<String, String[]> subItems = new Hashtable<String, String[]>();
 
    public ComboBoxTwo()
    {
        String[] items = { "Select Item", "Color", "Shape", "Fruit" };
        mainComboBox = new JComboBox<String>( items );
        mainComboBox.addActionListener( this );
 
        //  prevent action events from being fired when the up/down arrow keys are used
        mainComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        add( mainComboBox );
 
        //  Create sub combo box with multiple models
 
        subComboBox = new JComboBox<String>();
        subComboBox.setPrototypeDisplayValue("XXXXXXXXXX"); // JDK1.4
        add( subComboBox );
 
        String[] subItems1 = { "Select Color", "Red", "Blue", "Green" };
        subItems.put(items[1], subItems1);
 
        String[] subItems2 = { "Select Shape", "Circle", "Square", "Triangle" };
        subItems.put(items[2], subItems2);
 
        String[] subItems3 = { "Select Fruit", "Apple", "Orange", "Banana" };
        subItems.put(items[3], subItems3);
    }
 
    public void actionPerformed(ActionEvent e)
    {
        String item = (String)mainComboBox.getSelectedItem();
        Object o = subItems.get( item );
 
        if (o == null)
        {
            subComboBox.setModel( new DefaultComboBoxModel() );
        }
        else
        {
            subComboBox.setModel( new DefaultComboBoxModel( (String[])o ) );
        }
    }
 
    private static void createAndShowUI()
    {
        JFrame frame = new JFrame("SSCCE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add( new ComboBoxTwo() );
        frame.setLocationByPlatform( true );
        frame.pack();
        frame.setVisible( true );
    }
 
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowUI();
            }
        });
    }
}
