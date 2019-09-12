package br.ufsm.inf.examclipper.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
   // Project Name
   private String name;
   // PDF File
   private File pdf;
   // Location
   private File location;
   // Folder inside location
   private String folder;
   // 
   private List<Page> lPages;
   
   public Project() {
      lPages = new ArrayList<>();
   }
   
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public File getPdf() {
      return pdf;
   }

   public void setPdf(File pdf) {
      this.pdf = pdf;
   }

   public File getLocation() {
      return location;
   }

   public void setLocation(File location) {
      this.location = location;
   }

   public String getFolder() {
      return folder;
   }

   public void setFolder(String folder) {
      this.folder = folder;
   }

   public List<Page> getPages() {
      return lPages;
   }

   public void setPages(List<Page> lPages) {
      this.lPages = lPages;
   }

   @Override
   public String toString() {
      return "Project {" + "\n\tName: " + name + "\n\tPDF: " + pdf + "\n\tLocation: " + location + File.separator + folder + "\n}";
   }
}
