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
   // Project File
   private String file;
   // List w/ Pages
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

   public File getPDF() {
      return pdf;
   }

   public void setPDF(File pdf) {
      this.pdf = pdf;
   }

   public File getLocation() {
      return location;
   }

   public void setLocation(File location) {
      this.location = location;
   }

   public File getFile() {
      return new File(getLocation() + File.separator + file);
   }

   public void setFile(String file) {
      this.file = file;
   }

   public List<Page> getPages() {
      return lPages;
   }

   public void setPages(List<Page> lPages) {
      this.lPages = lPages;
   }

   public String getPDFFolder() {
      return getLocation() + File.separator + "pdf" + File.separator;
   }

   @Override
   public String toString() {
      return "Project {" + "\n\tName: " + name + "\n\tPDF: " + pdf + "\n\tLocation: " + location + "\n}";
   }
}
