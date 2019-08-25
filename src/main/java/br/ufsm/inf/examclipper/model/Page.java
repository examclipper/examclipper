package br.ufsm.inf.examclipper.model;

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
   // List of clippings
   private List<Clip> lClippings;

   public Page() {
      this("", "");
   }

   public Page(String filename, String path) {
      this.filename   = filename;
      this.path       = path;
      this.image      = null;
      this.modified   = null;
      this.buffImage  = null;
      this.lClippings = new ArrayList<>();
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

   public List<Clip> getClippings() {
      return lClippings;
   }

   public void setClippings(List<Clip> lClippings) {
      this.lClippings = lClippings;
   }

   public void addClipping(Clip clipping) {
      this.lClippings.add(clipping);
   }

   public void removeCleppings(List<Clip> removeList) {
      lClippings.removeAll(removeList);
   }

   public void removeAllClippings() {
      this.lClippings.clear();
   }

   public int getNumbersOfClippings() {
      return lClippings.size();
   }
}
