package org.newdawn.slick.tools.peditor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ThreeCascadeJComboBox {

private JComboBox combo1;

private JComboBox combo2;

private JComboBox combo3;

public static void main(String[] args) {
    new ThreeCascadeJComboBox();
}

public ThreeCascadeJComboBox() {
    JFrame v = new JFrame();
    v.getContentPane().setLayout(new FlowLayout());
    combo1 = new JComboBox();
    loadCombo1();
    combo1.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent arg0) {
            loadCombo2((String) combo1.getSelectedItem());
        }

    });

    combo2 = new JComboBox();
    loadCombo2((String) combo1.getSelectedItem());
    combo2.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent arg0) {
            loadCombo3((String) combo2.getSelectedItem());
        }

    });


    combo3 = new JComboBox();
    loadCombo3((String) combo2.getSelectedItem());

    v.getContentPane().add(combo1);
    v.getContentPane().add(combo2);
    v.getContentPane().add(combo3);
    v.pack();
    v.setVisible(true);
    v.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
}

private void loadCombo1() {
    combo1.addItem("letters");
    combo1.addItem("numbers");
}

private void loadCombo2(String seleccionEnCombo1) {
    combo2.removeAllItems();
    if (seleccionEnCombo1.equals("letters")) {
        combo2.addItem("A");
        combo2.addItem("B");
        combo2.addItem("C");
    } else if (seleccionEnCombo1.equals("numbers")) {
        combo2.addItem("1");
        combo2.addItem("2");
        combo2.addItem("3");
    }

}

private void loadCombo3(String seleccionEnCombo2) {
    combo3.removeAllItems();
    if (seleccionEnCombo2.equals("A")) {
        combo3.addItem("A-1");
        combo3.addItem("A-2");
        combo3.addItem("A-3");
    } else if (seleccionEnCombo2.equals("B")) {
        combo3.addItem("B-1");
        combo3.addItem("B-2");
        combo3.addItem("B-3");
    } else if (seleccionEnCombo2.equals("C")) {
        combo3.addItem("C-1");
        combo3.addItem("C-2");
        combo3.addItem("C-3");
    } else if (seleccionEnCombo2.equals("1")) {
        combo3.addItem("1-a");
        combo3.addItem("1-b");
        combo3.addItem("1-c");
    } else if (seleccionEnCombo2.equals("2")) {
        combo3.addItem("2-a");
        combo3.addItem("2-b");
        combo3.addItem("2-c");
    } else if (seleccionEnCombo2.equals("3")) {
        combo3.addItem("3-a");
        combo3.addItem("3-b");
        combo3.addItem("3-c");
    }   
}
}