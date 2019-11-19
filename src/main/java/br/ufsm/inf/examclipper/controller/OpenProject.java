package br.ufsm.inf.examclipper.controller;

import br.ufsm.inf.examclipper.model.Clip;
import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.model.Project;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OpenProject extends Thread {

   private Project project;

   // Current
   public int currentStep;
   // Total
   public int numbersOfSteps;
   // Project File
   private File projectFile;

   public OpenProject(File projectFile) {
      this.project = new Project();

      currentStep    = 0;
      numbersOfSteps = 3;

      this.projectFile = projectFile;
   }

   public Project getProject() {
      return project;
   }

   @Override
   public final void run() {
      System.out.println(" > [ProjectCreator] Opening project [" + project.getName() + "] ...");

      // File to JSON
      JSONObject json = convertFileToJSON();
      currentStep++;
      
      //
      convertJSONToProject(json);
      currentStep++;
      
      //
      getClippingsFromJSON((JSONArray) json.get("Pages"));
      currentStep++;
   }

   private JSONObject convertFileToJSON() {
      JSONObject json = null;
      
      try {
         JSONParser parser = new JSONParser();
         FileReader reader = new FileReader(projectFile);

         json = (JSONObject) parser.parse(reader);
      }
      catch (Exception e) {
         System.out.println(" > [OpenProject] Error to open project file: " + e.getMessage());
         e.printStackTrace();
      }
      
      return json;
   }

   private void convertJSONToProject(JSONObject json) {
      String projectName     = json.get("ProjectName").toString();
      String projectLocation = json.get("ProjectLocation").toString();
      String projectPDF      = json.get("PDF").toString();
      
      project = new Project();
      project.setName(projectName);
      project.setLocation(new File(projectLocation));
      project.setPDF(new File(project.getPDFFolder() + projectPDF));
      project.setFile(projectFile.getName());
      project.setPages(getPagesFromPDF());
   }

   private List<Page> getPagesFromPDF() {
      List<Page> lPages = new ArrayList<>();
      
      PDFConversor pdfConversor = new PDFConversor(project.getPDF(), lPages);
      pdfConversor.start();
      
      numbersOfSteps += pdfConversor.getNumberOfPages();
      
      int currentPage = 0;
      int lastPage    = 0;
      while(pdfConversor.isAlive()) {
         currentPage = pdfConversor.currentPage;
         if(currentPage != lastPage) {
            currentStep++;
            lastPage = currentPage;
         }
      }
      
      return lPages;
   }
   
   private List<Page> getClippingsFromJSON(JSONArray json) {
      List<Page> lPages = project.getPages();
      
      int cPage = 0;
      for(Object pObj : json) {
         JSONArray pageJSON = (JSONArray) pObj;
         for(Object cObj : pageJSON) {
            JSONObject clipJSON = (JSONObject) cObj;
            
            int x = Integer.parseInt(clipJSON.get("x").toString());
            int y = Integer.parseInt(clipJSON.get("y").toString());
            int w = Integer.parseInt(clipJSON.get("width").toString());
            int h = Integer.parseInt(clipJSON.get("height").toString());
            
            lPages.get(cPage).addClipping(new Clip(x, y, w, h));
         }
         cPage++;
      }
      
      return lPages;
   }
}
