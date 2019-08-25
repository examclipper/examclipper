package Controller;

import Model.Page;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class PDFConversor extends Thread {
   
   private static final String IMAGE_FORMAT = "jpg";
   private static final int    DPI          = 96;
   private static final float  QUALITY      = 1.0f;

   // PDF Document
   private PDDocument pdf;
   // List
   private List<Page> lPages;
   // Current Page
   public int currentPage;
   
   public PDFConversor(File file, final List<Page> lPages) {
      System.out.println(" > [Conversor] " + file);
  
      try {
         pdf         = PDDocument.load(file);
         this.lPages = lPages;
         currentPage = 1;
         
         nu.pattern.OpenCV.loadShared();
         //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
         System.out.println(" > [ConversorController] OpenCV Processor Loaded");
      }
      catch(Exception e) {
         System.out.println(" > [Conversor] Failed to load PDF!");
         pdf = null;
      }
   }
   
   @Override
   public final void run() {
      System.out.println(" > [ConversorController] Converting PDF to Mat...");
      try {         
         // Number of pages
         int numberOfPages = getNumberOfPages();

         // Renderer
         PDFRenderer renderer = new PDFRenderer(pdf);
         renderer.setSubsamplingAllowed(false);

         // ImageType
         ImageType imageType = ImageType.RGB;

         // Iterate all pages
         for(int page = 0; page < numberOfPages; page++) {    
            System.out.println(" > [ConversorController] Converting page " + (page + 1));
            
            BufferedImage buffImage = renderer.renderImageWithDPI(page, DPI, imageType);
            Mat           matImage  = bufferedImage2Mat(buffImage, 0);
            
            Page mPage = new Page("Page " + (page + 1), "");
            mPage.setImage(matImage);
            mPage.setModified(matImage);
            mPage.setBufferedImage(buffImage);
            
            lPages.add(mPage);
            currentPage++;
         }

         //Closing the document  
         pdf.close();
      }
      catch(IOException ioe) {
         System.out.println(" > [Conversor] Error: " + ioe);
         ioe.printStackTrace();
      }
   }
   
   public int getNumberOfPages() {
      return pdf.getNumberOfPages();
   }
   
   private Mat inputStream2Mat(InputStream is, int flag) throws IOException {
      byte[] byteBuffer = IOUtils.toByteArray(is);
      
      return Imgcodecs.imdecode(new MatOfByte(byteBuffer), flag);
   }
   
   private InputStream bufferedImage2InputStream(BufferedImage bi) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(bi, "jpg", baos);
      
      return new ByteArrayInputStream(baos.toByteArray());
   }
   
   private Mat bufferedImage2Mat(BufferedImage bi, int flag) throws IOException {
      return inputStream2Mat(bufferedImage2InputStream(bi), flag);
   }
}
