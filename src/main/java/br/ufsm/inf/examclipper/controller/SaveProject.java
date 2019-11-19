package br.ufsm.inf.examclipper.controller;

import br.ufsm.inf.examclipper.model.Clip;
import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.model.Project;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SaveProject extends Thread {

   private Project project;

   // Current
   public int currentStep;
   // Total
   public int numbersOfSteps;

   public SaveProject(Project project) {
      this.project = project;

      currentStep    = 0;
      numbersOfSteps = 2;
   }

   @Override
   public final void run() {
      System.out.println(" > [ProjectCreator] Saving project [" + project.getName() + "]");

      // Create JSON
      JSONObject json = createJSON();
      currentStep++;

      // Save JSON
      saveJSON(json);
      currentStep++;
   }

   private JSONObject createJSON() {
      List<Page> lPages = project.getPages();

      numbersOfSteps += lPages.size();
      
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

            clips.add(clip);
         }

         pages.add(clips);
         currentStep++;
      }

      // Project File
      JSONObject pf = new JSONObject();

      pf.put("ProjectName",      project.getName());
      pf.put("ProjectLocation",  project.getLocation().getAbsolutePath());
      pf.put("PDF",              project.getPDF().getName());
      pf.put("Pages",            pages);

      return pf;
   }

   private void saveJSON(JSONObject json) {
      String path = project.getLocation().getAbsolutePath();
      String file = project.getName().toLowerCase().replaceAll("\\s+", "");
            
      try {
         FileWriter fw = new FileWriter(path + File.separator + file + ".examclipper-project");
         fw.write(json.toJSONString());
         fw.close();
      }
      catch (Exception e) {
         System.out.println(" > [ProjectCreator] Error to create project file.");
      }
   }
}
