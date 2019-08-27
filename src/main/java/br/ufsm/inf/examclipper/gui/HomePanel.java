package br.ufsm.inf.examclipper.gui;

import br.ufsm.inf.examclipper.ExamClipperGUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

public class HomePanel extends JPanel {

   private ExamClipperGUI gui;

   private JButton loadButton;

   public HomePanel(ImageIcon icon, ImageIcon logo, ActionListener actionListener) {
      initComponents(icon, logo, actionListener);
   }

   private void initComponents(ImageIcon icon, ImageIcon logo, ActionListener actionListener) {

      BorderLayout borderLayout = new BorderLayout();
      setLayout(borderLayout);

      JLabel logoLabel = new JLabel(logo);
      logoLabel.setBorder(new EmptyBorder(10, 0, 25, 0));

      loadButton = new JButton("Load file");
      loadButton.setBackground(null);
      loadButton.setBorder(new EmptyBorder(0, 20, 0, 20));
      loadButton.setIcon(icon);
      loadButton.addActionListener(actionListener);
      loadButton.setPreferredSize(new Dimension(200, 50));
      loadButton.addMouseListener(new MouseAdapter() {
         Color oldcolor = loadButton.getForeground();

         public void mouseEntered(MouseEvent me) {
            loadButton.setForeground(Color.red);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
         }

         public void mouseExited(MouseEvent me) {
            loadButton.setForeground(oldcolor);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
      });

      JLabel newProject = new JLabel("Novo Projeto");
      newProject.setBorder(new EmptyBorder(0, 20, 0, 20));
      newProject.setIcon(icon);
      newProject.setOpaque(true);
      newProject.setPreferredSize(new Dimension(200, 50));
      newProject.addMouseListener(new MouseAdapter() {
         Color oldcolor   = newProject.getForeground();
         Color oldBgColor = newProject.getBackground();

         public void mouseEntered(MouseEvent me) {
            newProject.setForeground(Color.RED);
            newProject.setBackground(Color.BLACK);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
         }

         public void mouseExited(MouseEvent me) {
            newProject.setForeground(oldcolor);
            newProject.setBackground(oldBgColor);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
      });
      
      JPanel buttonsPanel = new JPanel();
      buttonsPanel.add(loadButton);
      buttonsPanel.add(newProject);

      add(logoLabel, BorderLayout.NORTH);
      add(buttonsPanel, BorderLayout.CENTER);
   }
}
