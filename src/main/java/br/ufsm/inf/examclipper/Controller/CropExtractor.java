package Controller;

import Model.CropRectangle;
import Model.Page;
import java.io.File;

import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class CropExtractor {
   
   public CropExtractor() {
      // Empty Constructor
   }
   
   public void extractCrops(String path, List<Page> lPages) {
      System.out.println(" > [OpenCVController] Extracting clippings");
      verifyPath(path);
      int totalPages = lPages.size();
      for(int i = 0; i < totalPages; i++) {
         Page page  = lPages.get(i);
         Mat  image = page.getImage();
         System.out.print("\t > [OpenCVController] Page [" + (i + 1) + "] " + page.getFilename());
         
         String folderPath = path + File.separator + "page-" + (i + 1) + File.separator;  
         verifyPath(folderPath);

         List<CropRectangle> lRectangles = sortList(page.getRectangles());
         int totalCrops   = lRectangles.size();
         int successCrops = 0;

         for(int j = 0; j < totalCrops; j++) {
            CropRectangle r = lRectangles.get(j);
            
            Rect ocvRect  = new Rect(r.x, r.y, r.width, r.height);
            Mat  clipping = new Mat(image.clone(), ocvRect);
            
            String filePath = folderPath + (j + 1);
            switch(r.getType()) {
               case CropRectangle.NOTHING_TYPE:
                  filePath += "_nenhum";
                  break;
               case CropRectangle.STATEMENT_TYPE:
                  filePath += "_enunciado";
                  break;
               case CropRectangle.IMAGE_TYPE:
                  filePath += "_imagem";
                  break;
               case CropRectangle.ALTERNATIVE_TYPE:
                  filePath += "_alternativa";
                  break;
            }
            filePath += ".tif";
            
            boolean result = Imgcodecs.imwrite(filePath, clipping);
            if(result) successCrops++;
         }
         System.out.println(" -> (" + successCrops + "/" + totalCrops + ") rects");
      }
      System.out.println(" > [OpenCVController] (" + totalPages + ") pages");
   }

   private List<CropRectangle> sortList(List<CropRectangle> lRectangles) {
      Collections.sort(lRectangles, (rect1, rect2) -> {         
         return rect1.y - rect2.y;
      });
           
      return lRectangles;
   }

   private void verifyPath(String path) {
      File folder = new File(path); 
      if(!folder.exists()) {
         folder.mkdir();
      }
   }
}
