package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePageItemList extends JPanel {

   private static final int BORDER_THICKNESS = 5;
   
   private BufferedImage img;
   private boolean       isSelected;
   
   public ImagePageItemList() {
      this(null);
   }
   
   public ImagePageItemList(BufferedImage img) {
      this.img        = img;
      this.isSelected = false;
   }
   
   public void setBufferedImage(BufferedImage img) {
      this.img = img;
      repaint();
   }
   
   void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
   }
   
   @Override
   public void paint(Graphics g) {
      update(g);
   }

   @Override
   public void update(Graphics g) {
      if (!isEnabled()) {
         return;
      }

      Graphics2D g2 = (Graphics2D) g;
      if(img != null) {
         g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
         
         if(isSelected) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(BORDER_THICKNESS));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
         }
      }

      g2.dispose();
   }
}