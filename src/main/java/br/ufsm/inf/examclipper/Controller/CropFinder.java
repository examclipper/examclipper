package Controller;

import Model.CropRectangle;
import Model.Page;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CropFinder extends Thread {

   private Page page;
   private int  horizontalSize;
   private int  verticalSize;

   public CropFinder(Page page, int horizontalSize, int verticalSize) {
      this.page           = page;
      this.horizontalSize = horizontalSize;
      this.verticalSize   = verticalSize;
   }

   @Override
   public void run() {
      // Clear previous rectangles
      page.clearRectangles();

      // Original Image
      Mat image = page.getImage().clone();
      // Gray Image
      Mat gray = new Mat(image.size(), CvType.CV_8UC1);
      switch(image.channels()) {
         case 3:
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
            break;
         case 1:
            image.copyTo(gray);
            break;
         default:
            System.out.println("Invalid image type:" + image.type());
            break;
      }

      // Threshold 
      Imgproc.threshold(gray, gray, 127, 255, Imgproc.THRESH_BINARY);

      // Element -> Rectangle
      int elementType = Imgproc.CV_SHAPE_RECT;
      // Size
      Size size = new Size(2 * horizontalSize + 1, 2 * verticalSize + 1);
      // Point 
      Point point = new Point(horizontalSize, verticalSize);
      // Element
      Mat element = Imgproc.getStructuringElement(elementType, size, point);

      // Erode Process
      Imgproc.erode(gray, gray, element);

      // Contours list
      List<MatOfPoint> contours = new ArrayList<>();
      // Mat hierarchy
      Mat hierarchy = new Mat();
      // Find contours in gray image
      Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

      // Iterate all contours
      System.out.println(" > [OpenCVController] " + contours.size() + " contours founded!");
      for(MatOfPoint mop : contours) {
         Rect rect = Imgproc.boundingRect(mop);

         if((rect.width * rect.height > 2500) && (rect.width != image.cols()) && (rect.height != image.rows())) {
            page.addCropRectangle(new CropRectangle(rect.x, rect.y, rect.width, rect.height));
         }
      }

      // Set Modified Image
      page.setModified(gray);
   }

   public Page getPage() {
      return page;
   }
}
