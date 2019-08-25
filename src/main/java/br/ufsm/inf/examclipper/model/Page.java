package Model;

import java.util.List;
import java.util.ArrayList;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

public class Page {
   // File Name
   private String filename;
   // Path
   private String path;
   // Original Image
   private Mat image;
   // Modified Image
   private Mat modified;
   // BufferedImage
   private BufferedImage buffImage;
   // List of marks in the image
   private List<CropRectangle> lRectangles;

   public Page() {
      this("", "");
   }

   public Page(String filename, String path) {
      this.filename    = filename;
      this.path        = path;
      this.image       = null;
      this.modified    = null;
      this.buffImage   = null;
      this.lRectangles = new ArrayList<>();
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public Mat getImage() {
      return image;
   }

   public void setImage(Mat image) {
      this.image = image;
   }

   public Mat getModified() {
      return modified;
   }

   public void setModified(Mat modified) {
      this.modified = modified;
   }

   public BufferedImage getBufferedImage() {
      return buffImage;
   }

   public void setBufferedImage(BufferedImage bufferedImage) {
      this.buffImage = bufferedImage;
   }

   public List<CropRectangle> getRectangles() {
      return lRectangles;
   }

   public void setRectangles(List<CropRectangle> lRectangles) {
      this.lRectangles = lRectangles;
   }

   public void addCropRectangle(CropRectangle crop) {
      this.lRectangles.add(crop);
   }

   public void removeRectangles(List<CropRectangle> removeList) {
      lRectangles.removeAll(removeList);
   }

   public void clearRectangles() {
      this.lRectangles.clear();
   }

   public int getNumbersOfRectangles() {
      return lRectangles.size();
   }
}
