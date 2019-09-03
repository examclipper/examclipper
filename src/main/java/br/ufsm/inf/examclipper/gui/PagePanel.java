package br.ufsm.inf.examclipper.gui;

import br.ufsm.inf.examclipper.ExamClipperGUI;
import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.model.Clip;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;

public class PagePanel extends JPanel {

   private ExamClipperGUI gui;
   // Current Page
   private Page page;
   
   private Clip  clip;
   private Point auxPoint;
   private Point auxCornerPoint;
   
   public PagePanel(ExamClipperGUI gui) {
      this(gui, null);
   }
   
   public PagePanel(ExamClipperGUI gui, Page page) {
      super();
      
      this.gui  = gui;
      this.page = page;
      
      clip     = null;
      auxPoint = auxCornerPoint = null;
      
      setSettings();
   }
   
   private void setSettings() {
      PagePanelMouseAdapter mouseAdapter = new PagePanelMouseAdapter();
      addMouseMotionListener(mouseAdapter);
      addMouseListener(mouseAdapter);
      
      PagePanelKeyAdapter keyboardAdapter = new PagePanelKeyAdapter();
      addKeyListener(keyboardAdapter);
   }
   
   public void setPage(Page page) {
      this.page = page;
      setDimensions();
      repaint();
   }
   
   private void setDimensions() {
      BufferedImage image = page.getBufferedImage();
      
      Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
      setPreferredSize(dimension);
      setSize(dimension);
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
      if(page != null) {
         g2.drawImage(page.getBufferedImage(), null, 0, 0);
         
         if(clip != null) {
            drawClipRegion(g2, clip);
         }
         
         for (Clip rect : page.getClippings()) {
            drawClipRegion(g2, rect);
            if (rect.isSelected()) {
               drawSelectionOverlay(g2, rect);
            }
         }
      }

      g2.dispose();
   }

   private void drawClipRegion(Graphics2D g2, Clip rect) {
      Color color = Color.BLACK;
      String type = "Nenhum";
      switch(rect.getType()) {
         case Clip.STATEMENT_TYPE:
            color = Color.BLUE;
            type  = "Enunciado";
            break;
         case Clip.IMAGE_TYPE:
            color = Color.GREEN;
            type  = "Imagem";
            break;
         case Clip.ALTERNATIVE_TYPE:
            color = Color.RED;
            type  = "Alternativas";
            break;
      }
      Stroke oldStroke = g2.getStroke();

      g2.setComposite(type.equals("Nenhum") ? SMOOTH_NORMAL : FULL_COMPOSITE);
      g2.setColor(color);
      g2.setStroke(new BasicStroke(NORMAL_BORDER_WIDTH));
      g2.draw(rect);
      
      int strWidth = g2.getFontMetrics().stringWidth(type);
      g2.setComposite(FULL_COMPOSITE);
      g2.setColor(Color.BLACK);
      g2.drawString(type, rect.x + rect.width - NORMAL_BORDER_WIDTH - strWidth, rect.y + rect.height - NORMAL_BORDER_WIDTH);
      
      g2.setStroke(oldStroke);
   }
   
   private void drawSelectionOverlay(Graphics2D g2, Clip clip) {
      g2.setComposite(XOR_COMPOSITE);
      g2.setColor(Color.BLACK);
      g2.setStroke(SELECTED_STROKE);
      g2.drawRect(clip.x + SELECT_BORDER_WIDTH / 2, clip.y + SELECT_BORDER_WIDTH / 2, clip.width - SELECT_BORDER_WIDTH / 2, clip.height - SELECT_BORDER_WIDTH / 2);

      // Rect size in pixels
      String size = Integer.toString(clip.width) + "x" + Integer.toString(clip.height);
      // g2.setFont(scaleFont(size, clip));
      g2.setComposite(FULL_COMPOSITE);
      g2.setColor(Color.BLACK);
      g2.drawString(size, clip.x + NORMAL_BORDER_WIDTH, clip.y + clip.height - NORMAL_BORDER_WIDTH);
            
      // Corners
      g2.setColor(Color.RED);
      int corner = Clip.CORNER_DIMENSION;
      int x = clip.x - (corner / 2);
      int y = clip.y - (corner / 2);
      g2.fillRect(x,              y,               corner, corner);
      g2.fillRect(x,              y + clip.height, corner, corner);
      g2.fillRect(x + clip.width, y,               corner, corner);
      g2.fillRect(x + clip.width, y + clip.height, corner, corner);
   }
   
   public void deleteToSmallClippings() {
      List<Clip> rectsToTrash = new ArrayList<>();
      for(Clip rect : page.getClippings()) {
         if(rect.getWidth()  < 2 * Clip.CORNER_DIMENSION ||
            rect.getHeight() < 2 * Clip.CORNER_DIMENSION) {
             rectsToTrash.add(rect);
         }
      }
      page.removeCleppings(rectsToTrash);
  }
   
   public void deleteSelectedClippings() {
      List<Clip> removeList = new ArrayList<>();
      for(Clip rect : page.getClippings()) {
         if (rect.isSelected()) {
            removeList.add(rect);
         }
      }
      page.removeCleppings(removeList);
      repaint();
   }
   
   public void deleteAllClippings() {
      page.removeAllClippings();
      repaint();
   }
   
   private void diselectAllClippings() {
      for(Clip rect : page.getClippings()) {
         rect.setSelected(false);
      }
   }
   
   private int getNumbersOfSelectedClippings() {
      int numbersOfSelecteds = 0;
      for(Clip rect : page.getClippings()) {
         if(rect.isSelected()) {
            numbersOfSelecteds++;
         }
      }      
      return numbersOfSelecteds;
   }
   
   private int getFirstSelectedClipping() {
      for(Clip rect : page.getClippings()) {
         if(rect.isSelected()) {
            return rect.getType();
         }
      }
      return 0;
   }

   public void mergeSelectedClippings() {
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;
            
      for(Clip rect : page.getClippings()) {
         if(rect.isSelected()) {
            int x0 = rect.x;
            int y0 = rect.y;
            int x1 = x0 + rect.width;
            int y1 = y0 + rect.height;

            if(x0 < minX) minX = x0;
            if(y0 < minY) minY = y0;
            if(x1 > maxX) maxX = x1;
            if(y1 > maxY) maxY = y1;   
         }
      }
      deleteSelectedClippings();
      page.addClipping(new Clip(minX, minY, maxX - minX, maxY - minY));
   }
   
   public void setNewTypeToSelectedRectangle(int type) {
      for(Clip rect : page.getClippings()) {
         if(rect.isSelected()) {
            rect.setType(type);
         }
      }
   }
   
   // KeyBoard
   private class PagePanelKeyAdapter extends KeyAdapter {

      @Override
      public void keyPressed(KeyEvent event) {
         int keyCode = event.getKeyCode();
         switch(keyCode) {
            case KeyEvent.VK_DELETE:
               deleteSelectedClippings();
               break;
         }
      }

      @Override
      public void keyReleased(KeyEvent e) {
         // TODO
      }
   }

   // Mouse
   private class PagePanelMouseAdapter extends MouseAdapter implements ActionListener {

      private static final int NOTHING   = 0;
      private static final int NEW_RECT  = 1;
      private static final int MOVE_RECT = 2;
      private static final int MOVE_ULC  = 3; // Upper-Left  Corner
      private static final int MOVE_URC  = 4; // Upper-Right Corner
      private static final int MOVE_LLC  = 5; // Lower-Left  Corner
      private static final int MOVE_LRC  = 6; // Lower-Right Corner
      
      private int action;

      public PagePanelMouseAdapter() {
         action = NOTHING;
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         // Empty
      }
      
      @Override
      public void mouseMoved(MouseEvent e) {
         if(PagePanel.this.contains(e.getPoint())) {
            PagePanel.this.requestFocusInWindow();
         }
      }
      
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
         Point point = mouseEvent.getPoint();
         if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
            for (Clip rect : page.getClippings()) {
               int cornerClicked = rect.getCornerClicked(point);
               if(cornerClicked != -1) {
                  switch(cornerClicked) {
                     case Clip.UL_CORNER:
                        auxCornerPoint = new Point(rect.x - point.x,              rect.y - point.y);
                        break;
                     case Clip.UR_CORNER:
                        auxCornerPoint = new Point(rect.x + rect.width - point.x, rect.y - point.y);
                        break;
                     case Clip.LL_CORNER:
                        auxCornerPoint = new Point(rect.x - point.x,              rect.y + rect.height - point.y);
                        break;
                     case Clip.LR_CORNER:
                        auxCornerPoint = new Point(rect.x + rect.width - point.x, rect.y + rect.height - point.y);
                        break;
                  }
                  action = cornerClicked + 3;
                  diselectAllClippings();
                  if(!rect.isSelected()) {
                     rect.setSelected(true);  
                  }
                  clip = rect;
                  return;
               }
            }
            
            for(Clip rect : page.getClippings()) {
               if (rect.contains(point)) {
                  action = MOVE_RECT;
                  if(!mouseEvent.isControlDown()) {
                     diselectAllClippings();
                  }
                  if(!rect.isSelected()) {
                     rect.setSelected(true);  
                     gui.attRectanglePanelGUI(getNumbersOfSelectedClippings(), getFirstSelectedClipping());
                  }
                  clip = rect;
                  return;
               }
            }
            
            // Draw new Rect
            action = NEW_RECT;
            if(clip == null) {
               diselectAllClippings();
               gui.attRectanglePanelGUI(getNumbersOfSelectedClippings(), getFirstSelectedClipping());
               clip = new Clip();
               clip.setSelected(true);
               page.addClipping(clip);
               auxPoint = point;
            }
         }
      }
      
      @Override
      public void mouseReleased(MouseEvent mouseEvent) {
         Point point = mouseEvent.getPoint();
         
         action   = NOTHING;
         clip     = null;
         auxPoint = null;
         
         deleteToSmallClippings();
         repaint();
      }

      @Override
      public void mouseDragged(MouseEvent mouseEvent) {
         Point point = mouseEvent.getPoint();
         if(auxPoint == null) {
            auxPoint = point;
         }
         switch(action) {
            case NOTHING:
               break;
            case NEW_RECT:
               clip.x = (point.x < auxPoint.x) ? point.x : auxPoint.x;
               clip.y = (point.y < auxPoint.y) ? point.y : auxPoint.y;
               clip.width  = Math.abs(point.x - auxPoint.x);
               clip.height = Math.abs(point.y - auxPoint.y);
               break;
            case MOVE_RECT:
               clip.translate(point.x - auxPoint.x, point.y - auxPoint.y);
               auxPoint = point;
               break;
            case MOVE_ULC:
               point.translate(auxCornerPoint.x, auxCornerPoint.y);
               clip.setNewULCorner(point);
               auxPoint = point;
               break;
            case MOVE_URC:
               point.translate(auxCornerPoint.x, auxCornerPoint.y);
               clip.setNewURCorner(point);
               auxPoint = point;
               break;
            case MOVE_LLC:
               point.translate(auxCornerPoint.x, auxCornerPoint.y);
               clip.setNewLLCorner(point);
               auxPoint = point;
               break;
            case MOVE_LRC:
               point.translate(auxCornerPoint.x, auxCornerPoint.y);
               clip.setNewLRCorner(point);
               auxPoint = point;
               break;
         }
         repaint();
      }
   }
   
   // Constants
   private final static int         NORMAL_BORDER_WIDTH = 5;
   private final static int         SELECT_BORDER_WIDTH = 1;
   private final static Font        BASE_FONT           = new Font(null, Font.PLAIN, 10);
   private final static Composite   SMOOTH_NORMAL       = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f);
   private final static Composite   SMOOTH_SELECT       = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
   private final static Composite   XOR_COMPOSITE       = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .8f);
   private final static Composite   FULL_COMPOSITE      = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.f);
   private final static float[]     DASH_PATTERN        = { 5f, 5f };
   private final static BasicStroke SELECTED_STROKE     = new BasicStroke(SELECT_BORDER_WIDTH, BasicStroke.CAP_SQUARE,
                                                                          BasicStroke.JOIN_BEVEL, 1.0f, DASH_PATTERN, 0f);
}
