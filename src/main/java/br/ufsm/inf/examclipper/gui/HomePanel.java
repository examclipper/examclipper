package br.ufsm.inf.examclipper.gui;

import br.ufsm.inf.examclipper.ExamClipperGUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;

public class HomePanel extends JPanel {

   private final ExamClipperGUI gui;

   public HomePanel(ExamClipperGUI gui) {
      this.gui = gui;

      initComponents();
   }

   private void initComponents() {
      ImageIcon icLogo        = new ImageIcon(getClass().getClassLoader().getResource("ic_logo_200.png"));
      ImageIcon icNewProject  = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_plus_solid_16.png"));
      ImageIcon icOpenProject = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_solid_16.png"));
      ImageIcon icOpenPDF     = new ImageIcon(getClass().getClassLoader().getResource("ic_pdf_16.png"));
      
      setLayout(new GridLayout(2, 1));

      Dimension size = new Dimension(220, 40);
      EmptyBorder border = new EmptyBorder(10, 20, 10, 20);

      JLabel logoLabel = new JLabel(icLogo);
      logoLabel.setBorder(new EmptyBorder(10, 0, 25, 0));

      JLabel newProject = new JLabel("  Criar um novo projeto");
      newProject.setAlignmentX(CENTER_ALIGNMENT);
      newProject.setBorder(border);
      newProject.setMaximumSize(size);
      newProject.setMinimumSize(size);
      newProject.setIcon(icNewProject);
      newProject.setOpaque(true);
      newProject.addMouseListener(new MouseAdapter() {
         Color oldFgcolor = newProject.getForeground();
         Color oldBgColor = newProject.getBackground();

         public void mouseEntered(MouseEvent me) {
            newProject.setForeground(Color.BLACK);
            newProject.setBackground(Color.LIGHT_GRAY);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
         }

         public void mouseExited(MouseEvent me) {
            newProject.setForeground(oldFgcolor);
            newProject.setBackground(oldBgColor);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         
         public void mouseClicked(MouseEvent me) {
            gui.showNewProjectDialog();
         }
      });

      JLabel openProject = new JLabel("  Abrir um projeto");
      openProject.setAlignmentX(CENTER_ALIGNMENT);
      openProject.setBorder(border);
      openProject.setMaximumSize(size);
      openProject.setMinimumSize(size);
      openProject.setIcon(icOpenProject);
      openProject.setOpaque(true);
      openProject.addMouseListener(new MouseAdapter() {
         Color oldFgcolor = newProject.getForeground();
         Color oldBgColor = newProject.getBackground();

         public void mouseEntered(MouseEvent me) {
            openProject.setForeground(Color.BLACK);
            openProject.setBackground(Color.LIGHT_GRAY);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
         }

         public void mouseExited(MouseEvent me) {
            openProject.setForeground(oldFgcolor);
            openProject.setBackground(oldBgColor);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         
         public void mouseClicked(MouseEvent me) {
            gui.showOpenProjectDialog(); 
         }
      });

      JLabel openPDF = new JLabel("  Abrir um PDF");
      openPDF.setAlignmentX(CENTER_ALIGNMENT);
      openPDF.setBorder(border);
      openPDF.setMaximumSize(size);
      openPDF.setMinimumSize(size);
      openPDF.setIcon(icOpenPDF);
      openPDF.setOpaque(true);
      openPDF.addMouseListener(new MouseAdapter() {
         Color oldFgcolor = newProject.getForeground();
         Color oldBgColor = newProject.getBackground();

         public void mouseEntered(MouseEvent me) {
            openPDF.setForeground(Color.BLACK);
            openPDF.setBackground(Color.LIGHT_GRAY);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
         }

         public void mouseExited(MouseEvent me) {
            openPDF.setForeground(oldFgcolor);
            openPDF.setBackground(oldBgColor);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         
         public void mouseClicked(MouseEvent me) {
            gui.showOpenPDFDialog();
         }
      });

      JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
      buttonsPanel.add(newProject);
      buttonsPanel.add(openProject);
      buttonsPanel.add(openPDF);

      add(logoLabel);
      add(buttonsPanel);
   }
}
