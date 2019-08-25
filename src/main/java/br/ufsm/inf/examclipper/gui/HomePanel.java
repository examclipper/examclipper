package GUI;

import View.ExtractorGUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

public class HomePanel extends JPanel {

   private ExtractorGUI gui;
   
   private JButton loadButton;

   public HomePanel(ImageIcon icon, ActionListener actionListener) {
      initComponents(icon, actionListener);
   }
   
   private void initComponents(ImageIcon icon, ActionListener actionListener) {
      
      BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
      setLayout(boxLayout);
      
      JLabel label = new JLabel(icon);
      label.setAlignmentX(Component.CENTER_ALIGNMENT);
      
      loadButton = new JButton("Load file");
      loadButton.setBackground(null);
      loadButton.setOpaque(false);
      loadButton.setBorder(new EmptyBorder(0, 20, 0, 20));
      loadButton.setIcon(icon);
      loadButton.addActionListener(actionListener);
      loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
      loadButton.setPreferredSize(new Dimension(100, 50));
      
      add(Box.createVerticalGlue());
      add(label);
      add(Box.createRigidArea(new Dimension(0, 40)));
      add(loadButton);
      add(Box.createVerticalGlue());
   }
}
