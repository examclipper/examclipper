package Model;

import java.awt.Point;
import java.awt.Rectangle;

public class CropRectangle extends Rectangle {
   
   public static final int CORNER_DIMENSION = 10;

   public static final int NOTHING_TYPE     = 0;
   public static final int STATEMENT_TYPE   = 1;
   public static final int IMAGE_TYPE       = 2;
   public static final int ALTERNATIVE_TYPE = 3;
   
   public static final int UL_CORNER = 0;
   public static final int UR_CORNER = 1;
   public static final int LL_CORNER = 2;
   public static final int LR_CORNER = 3;   
   
   // Type
   private int type;
   // isSelected
   private boolean selected;
   
   public CropRectangle() {
      this(0, 0, 0, 0);
   }
   
   public CropRectangle(CropRectangle rect) {
      this(rect.x, rect.y, rect.width, rect.height);
   }
   
   public CropRectangle(int x, int y, int width, int height) {
      super();
      
      this.x        = x;
      this.y        = y;
      this.height   = height;
      this.width    = width;
      this.type     = NOTHING_TYPE;
      this.selected = false;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }
   
   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public int getCornerClicked(Point point) {
      int x = (int) getX() - (CORNER_DIMENSION / 2);
      int y = (int) getY() - (CORNER_DIMENSION / 2);
      // UL
      if((point.x > x && point.x < x + CORNER_DIMENSION) && 
         (point.y > y && point.y < y + CORNER_DIMENSION)) {
         return UL_CORNER;
      }
      // UR
      if((point.x > x + width && point.x < x + width + CORNER_DIMENSION) && 
         (point.y > y         && point.y < y + CORNER_DIMENSION)) {
         return UR_CORNER;
      }
      if((point.x > x          && point.x < x + CORNER_DIMENSION) && 
         (point.y > y + height && point.y < y + height + CORNER_DIMENSION)) {
         return LL_CORNER;
      }
      if((point.x > x + width  && point.x < x + width  + CORNER_DIMENSION) && 
         (point.y > y + height && point.y < y + height + CORNER_DIMENSION)) {
         return LR_CORNER;
      }
      
      return -1;
   }
   
   public void setNewULCorner(Point point) {
      width  = x + width  - point.x;
      height = y + height - point.y;
      x = point.x;
      y = point.y;
   }

   public void setNewURCorner(Point point) {
      width  = point.x - x;
      height = y + height - point.y;
      y = point.y;
   }

   public void setNewLLCorner(Point point) {
      width  = x + width - point.x;
      height = point.y - y;
      x = point.x;
   }

   public void setNewLRCorner(Point point) {
      width  = point.x - x;
      height = point.y - y;
   }   
}
