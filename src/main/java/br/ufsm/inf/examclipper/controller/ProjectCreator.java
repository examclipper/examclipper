package br.ufsm.inf.examclipper.controller;

import br.ufsm.inf.examclipper.model.Clip;
import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.model.Project;

import java.io.File;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectCreator extends Thread {

   private Project project;

   // Current
   public int currentStep;
   // Total
   public int numbersOfSteps;

   public ProjectCreator(Project project) {
      this.project = project;

      currentStep = 0;
      numbersOfSteps = 4;
   }

   @Override
   public final void run() {
      System.out.println(" > [ProjectCreator] Creating project [" + project.getName() + "]");

      // Verify and create project folder
      verifyFolder();
      currentStep++;

      // Copy PDF file to project folder
      movePDFFile();
      currentStep++;

      // Create JSON
      JSONObject json = createJSON();
      currentStep++;

      // Create Project Config File
      createProjectConfigFile(json);
      currentStep++;
   }

   private void verifyFolder() {
      File folder = new File(project.getLocation() + File.separator + project.getFolder());
      if (!folder.exists()) {
         folder.mkdir();
      }
   }

   private void movePDFFile() {
      File pdf = project.getPdf();
      File folder = new File(project.getLocation() + File.separator + project.getFolder() + File.separator + pdf.getName());
      try {
         Files.copy(pdf.toPath(), folder.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
         System.out.println(" > [ProjectCreator] Failed to move PDF file to project folder");
      }
   }

   private JSONObject createJSON() {
      List<Page> lPages = project.getPages();

      JSONArray pages = new JSONArray();
      for (int i = 0; i < lPages.size(); i++) {
         List<Clip> clippings = lPages.get(i).getClippings();

         JSONArray clips = new JSONArray();
         for (Clip c : clippings) {
            JSONObject clip = new JSONObject();

            clip.put("x", c.x);
            clip.put("y", c.y);
            clip.put("width", c.width);
            clip.put("height", c.height);

            clips.put(clip);
         }

         pages.put(clips);
      }

      // Project File
      JSONObject pf = new JSONObject();

      pf.put("PDF", project.getPdf().getName());
      pf.put("NumberOfPages", lPages.size());
      pf.put("Pages", pages);

      return pf;
   }

   private void createProjectConfigFile(JSONObject json) {
      String path = project.getLocation() + File.separator + project.getFolder();
      
      try {
         FileWriter fw = new FileWriter(path + File.separator + project.getFolder().toLowerCase() + ".examclipper-project");
         fw.write(json.toString(4));
         fw.close();
      }
      catch (Exception e) {
         System.out.println(" > [ProjectCreator] Error to create project file.");
      }
   }
}
