package br.ufsm.inf.examclipper.controller;

import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.model.Project;

import java.io.File;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
      verifyFolder(project.getLocation().getAbsolutePath());
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

   private void verifyFolder(String path) {
      File folder = new File(path);
      if (!folder.exists()) {
         folder.mkdir();
      }
   }

   private void movePDFFile() {
      verifyFolder(project.getPDFFolder());

      File source = project.getPDF();
      File destination = new File(project.getPDFFolder() + source.getName());
      try {
         Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
         System.out.println(" > [ProjectCreator] Failed to move PDF file to project folder");
         e.printStackTrace();
      }
   }

   private JSONObject createJSON() {
      List<Page> lPages = project.getPages();

      JSONArray pages = new JSONArray();
      for (int i = 0; i < lPages.size(); i++) {
         pages.add(new JSONArray());
      }

      // Project File
      JSONObject pf = new JSONObject();

      pf.put("ProjectName", project.getName());
      pf.put("ProjectLocation", project.getLocation().getAbsolutePath());
      pf.put("PDF", project.getPDF().getName());
      pf.put("Pages", pages);

      return pf;
   }

   private void createProjectConfigFile(JSONObject json) {
      String path = project.getLocation().getAbsolutePath();
      String file = project.getName().toLowerCase().replaceAll("\\s+", "");

      try {
         FileWriter fw = new FileWriter(path + File.separator + file + ".examclipper-project");
         fw.write(json.toJSONString());
         fw.close();
      } catch (Exception e) {
         System.out.println(" > [ProjectCreator] Error to create project file.");
      }
   }
}
