package br.ufsm.inf.examclipper.controller;

import br.ufsm.inf.examclipper.model.Clip;
import br.ufsm.inf.examclipper.model.Page;

import java.io.File;

import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

public class ClipExtractor {
   
   public ClipExtractor() {
      // Empty Constructor
   }
   
   public void extractClippings(String path, List<Page> lPages) {
      System.out.println(" > [OpenCVController] Extracting clippings");
      verifyPath(path);
      int totalPages = lPages.size();
      for(int i = 0; i < totalPages; i++) {
         Page page  = lPages.get(i);
         Mat  image = page.getImage();
         System.out.print("\t > [OpenCVController] Page [" + (i + 1) + "] " + page.getFilename());
         
         String folderPath = path + File.separator + "page-" + (i + 1) + File.separator;  
         verifyPath(folderPath);

         List<Clip> lClippings  = sortList(page.getClippings());
         int numbersOfClippings = lClippings.size();
         int successClippings   = 0;

         for(int j = 0; j < numbersOfClippings; j++) {
            Clip r = lClippings.get(j);
            
            Rect ocvRect  = new Rect(r.x, r.y, r.width, r.height);
            Mat  clipping = new Mat(image.clone(), ocvRect);
            
            String filePath = folderPath + (j + 1);
            switch(r.getType()) {
               case Clip.NOTHING_TYPE:
                  filePath += "_nenhum";
                  break;
               case Clip.STATEMENT_TYPE:
                  filePath += "_enunciado";
                  break;
               case Clip.IMAGE_TYPE:
                  filePath += "_imagem";
                  break;
               case Clip.ALTERNATIVE_TYPE:
                  filePath += "_alternativa";
                  break;
            }
            filePath += ".tif";
            
            boolean result = Imgcodecs.imwrite(filePath, clipping);
            if(result) successClippings++;
         }
         System.out.println(" -> (" + successClippings + "/" + numbersOfClippings + ") clippings");
      }
      System.out.println(" > [OpenCVController] (" + totalPages + ") pages");
   }

   private List<Clip> sortList(List<Clip> lClippings) {
      Collections.sort(lClippings, (rect1, rect2) -> {         
         return rect1.y - rect2.y;
      });
           
      return lClippings;
   }

   private void verifyPath(String path) {
      File folder = new File(path); 
      if(!folder.exists()) {
         folder.mkdir();
      }
   }
}
