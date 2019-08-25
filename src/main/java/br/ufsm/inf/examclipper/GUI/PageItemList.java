package GUI;

import Model.Page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PageItemList extends JPanel implements ListCellRenderer<Page> {

   private static final int HORIZONTAL_PADDING = 7;
   private static final int VERTICAL_PADDING   = 5;

   private JPanel panel;
   private JLabel pageNumberLabel;
   private ImagePageItemList imagePanel;

   public PageItemList() {
      super(new BorderLayout());

      initComponents();
   }

   private void initComponents() {
      panel = new JPanel(new BorderLayout());
      panel.setBackground(Color.LIGHT_GRAY);

      pageNumberLabel = new JLabel("X");
      pageNumberLabel.setFont(new Font("Meiryo", Font.BOLD, 14));
      pageNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
      pageNumberLabel.setVerticalAlignment(SwingConstants.TOP);
      
      imagePanel = new ImagePageItemList();

      panel.add(imagePanel,      BorderLayout.CENTER);
      panel.add(pageNumberLabel, BorderLayout.WEST);

      setBackground(Color.LIGHT_GRAY);
      add(panel, BorderLayout.CENTER);
   }

   public void setDimensions() {
      int width  = getWidth();
      int height = getHeight();

      pageNumberLabel.setPreferredSize(new Dimension(30, height));
      imagePanel.setPreferredSize(new Dimension(width - 30, 150));
   }

   @Override
   public Component getListCellRendererComponent(JList<? extends Page> jlist, Page e, int i, boolean bln, boolean bln1) {
      int width  = getWidth();
      int height = getHeight();
      
      pageNumberLabel.setText(Integer.toString(i + 1));
      pageNumberLabel.setForeground(bln ? Color.RED : Color.BLACK);
      pageNumberLabel.setPreferredSize(new Dimension(30, height));

      imagePanel.setSelected(bln);
      imagePanel.setBufferedImage(e.getBufferedImage());
      imagePanel.setPreferredSize(new Dimension(width - 30, 150));

      int total = jlist.getModel().getSize();
      setBorder(new EmptyBorder(VERTICAL_PADDING, 0, (i == (total - 1)) ? VERTICAL_PADDING : 0, HORIZONTAL_PADDING));

      return this;
   }
}
