package GUI;

import java.awt.Frame;
import java.awt.Dialog;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {

   private final Frame        frame;
   private final JLabel       label;
   private final JProgressBar progressBar;
   
   public ProgressDialog(Frame frame, String title, final Dialog.ModalityType modalityType) {
      super(frame, title, modalityType);

      this.frame       = frame;
      this.label       = new JLabel("Progress...");
      this.progressBar = new JProgressBar(0, 100);
      
      initComponents();
	}

   private void initComponents() {      
      add(label,       BorderLayout.NORTH);
      add(progressBar, BorderLayout.CENTER);

      setSize(250, 150);
      setLocationRelativeTo(frame);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
   }
   
   public void setProgressBarValue(int value, int total) {
      progressBar.setValue(value / total);
      progressBar.setString("(" + value + "/" + total + ") Loaded.");
   }
}
