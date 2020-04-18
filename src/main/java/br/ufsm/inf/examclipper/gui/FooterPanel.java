package br.ufsm.inf.examclipper.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class FooterPanel extends JPanel {

   private JPanel panel;

   private JLabel progressBarLabel;

   private JProgressBar progressBar;

   public FooterPanel(Font font) {
      initComponents(font);
   }

   private void initComponents(Font font) {
      panel = new JPanel();

      Dimension dimension = new Dimension(400, 100);

      progressBar = new JProgressBar(0, 100);
      progressBar.setAlignmentX(CENTER_ALIGNMENT);
      progressBar.setMaximumSize(dimension);
      progressBar.setMinimumSize(dimension);
      progressBar.setStringPainted(true);

      progressBarLabel = new JLabel();
      progressBarLabel.setAlignmentX(CENTER_ALIGNMENT);
      progressBarLabel.setMaximumSize(dimension);
      progressBarLabel.setMinimumSize(dimension);
      progressBarLabel.setForeground(Color.BLACK);
      progressBarLabel.setFont(font);

      panel.setBorder(new EmptyBorder(10, 0, 10, 0));
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBackground(Color.WHITE);
      panel.setVisible(false);
      panel.add(progressBarLabel);
      panel.add(progressBar);
   }

   public void setLabelMessage(String message) {
      progressBarLabel.setText(message);
   }

   public void setValue(int value) {
      progressBar.setValue(value);
   }

   public void setMessage(String message) {
      progressBar.setString(message);
   }

   public void show() {
      panel.setVisible(true);
   }

   public void hide() {
      panel.setVisible(false);
   }

   public JPanel getPanel() {
      return panel;
   }
}
