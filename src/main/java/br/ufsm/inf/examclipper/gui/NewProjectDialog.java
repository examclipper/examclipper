package br.ufsm.inf.examclipper.gui;

import br.ufsm.inf.examclipper.model.Project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class NewProjectDialog extends JDialog implements ActionListener {

   private static final int DEFAULT_WIDTH  = 670;
   private static final int DEFAULT_HEIGHT = 490;

   private Project project;

   private File selectedFile;
   private File selectedFolder;
   
   public NewProjectDialog(Frame parent) {
      super(parent, "New Project", true);

      project        = null;
      selectedFile   = null;
      selectedFolder = new File(System.getProperty("user.home"));

      Point parentLocation = parent.getLocation();
      setLocation(parentLocation.x, parentLocation.y);

      JLabel projectNameLabel     = new JLabel();
      JLabel projectFileLabel     = new JLabel();
      JLabel projectLocationLabel = new JLabel();
      JLabel projectFolderLabel   = new JLabel();

      Dimension size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      JPanel panel = new JPanel();
      panel.setMaximumSize(size);
      panel.setMinimumSize(size);
      panel.setPreferredSize(size);
      panel.setLayout(null);

      Font font = new Font("Meiryo", Font.PLAIN, 12);

      projectNameLabel.setText("Nome do Projeto");
      projectNameLabel.setFont(font);
      projectNameLabel.setBounds(10, 10, 650, 20);

      projectFileLabel.setText("Arquivo do Projeto");
      projectFileLabel.setFont(font);
      projectFileLabel.setBounds(10, 60, 650, 20);

      projectLocationLabel.setText("Localização do Projeto");
      projectLocationLabel.setFont(font);
      projectLocationLabel.setBounds(10, 110, 650, 20);
      
      projectFolderLabel.setText("Pasta do Projeto");
      projectFolderLabel.setFont(font);
      projectFolderLabel.setBounds(10, 160, 650, 20);

      projectNameInput = new JTextField();
      projectNameInput.setFont(font);
      projectNameInput.setBounds(10, 30, 650, 30);
      projectNameInput.addKeyListener(new KeyListener() {

         @Override
         public void keyTyped(KeyEvent e) {
            // Empty
         }

         @Override
         public void keyPressed(KeyEvent e) {
            // Empty
         }

         @Override
         public void keyReleased(KeyEvent e) {
            String projectName = projectNameInput.getText();
            if(!projectName.isEmpty()) {
               projectName = projectName.replaceAll("\\s+", "");
               File file = new File(selectedFolder.getAbsolutePath() + File.separator + projectName);

               projectFolderInput.setText(file.getAbsolutePath());

               attCreateProjectButton();
               verifyProjectFolder(file);
            }            
         }
      });

      ImageIcon icOpenPDF   = new ImageIcon(getClass().getClassLoader().getResource("ic_pdf_16.png"));
      findFileProjectButton = new JButton(icOpenPDF);
      findFileProjectButton.addActionListener(this);
      findFileProjectButton.setBounds(610, 80, 50, 30);

      projectFileInput = new JTextField();
      projectFileInput.setFont(font);
      projectFileInput.setEnabled(false);
      projectFileInput.setBounds(10, 80, 590, 30);

      projectLocationInput = new JTextField(selectedFolder.getAbsolutePath());
      projectLocationInput.setFont(font);
      projectLocationInput.setEnabled(false);
      projectLocationInput.setBounds(10, 130, 590, 30);

      ImageIcon icNewProject  = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_plus_solid_16.png"));
      findFolderProjectButton = new JButton(icNewProject);
      findFolderProjectButton.addActionListener(this);
      findFolderProjectButton.setBounds(610, 130, 50, 30);

      projectFolderInput = new JTextField(selectedFolder.getAbsolutePath());
      projectFolderInput.setFont(font);
      projectFolderInput.setEnabled(false);
      projectFolderInput.setBounds(10, 180, 650, 30);
      
      createProjectButton = new JButton("Criar");
      createProjectButton.setEnabled(false);
      createProjectButton.setFont(font);
      createProjectButton.addActionListener(this);
      createProjectButton.setBounds(410, 450, 120, 30);

      cancelProjectButton = new JButton("Cancelar");
      cancelProjectButton.setFont(font);
      cancelProjectButton.addActionListener(this);
      cancelProjectButton.setBounds(540, 450, 120, 30);

      ImageIcon icWarning = new ImageIcon(getClass().getClassLoader().getResource("ic_warning_16.png"));
      errorLabel = new JLabel("A pasta do projeto já existe e não está vazia!");
      errorLabel.setVisible(false);
      errorLabel.setFont(font);
      errorLabel.setForeground(Color.RED);
      errorLabel.setIcon(icWarning);
      errorLabel.setBounds(10, 210, 650, 30);
      
      panel.add(projectNameLabel);
      panel.add(projectFileLabel);
      panel.add(projectLocationLabel);
      panel.add(projectFolderLabel);
      panel.add(projectNameInput);
      panel.add(projectFileInput);
      panel.add(projectLocationInput);
      panel.add(projectFolderInput);
      panel.add(findFileProjectButton);
      panel.add(findFolderProjectButton);
      panel.add(errorLabel);
      panel.add(createProjectButton);
      panel.add(cancelProjectButton);

      getContentPane().add(panel);
      setResizable(false);
      pack();
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();
      if (source == createProjectButton) {
         String projectName = projectNameInput.getText();
         
         project = new Project();
         project.setName(projectName);
         project.setPdf(selectedFile);
         project.setLocation(selectedFolder);
         project.setFolder(projectName.replaceAll("\\s+", ""));
                  
         dispose();
      }
      else if(source == cancelProjectButton) {
         project = null;
         dispose();
      }
      else if(source == findFileProjectButton) {
         showOpenPDFDialog();
      }
      else if(source == findFolderProjectButton) {
         showSelectFolderDialog();
      }
   }

   public Project run() {
      this.setVisible(true);

      return project;
   }

   private JTextField projectNameInput;
   private JTextField projectFileInput;
   private JTextField projectLocationInput;
   private JTextField projectFolderInput;

   private JButton findFileProjectButton;
   private JButton findFolderProjectButton;

   private JButton createProjectButton;
   private JButton cancelProjectButton;
   
   private JLabel errorLabel;

   private void showOpenPDFDialog() {
      Platform.runLater(() -> {
         FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");

         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Open PDF");
         fileChooser.setInitialDirectory(selectedFile != null ? selectedFile : new File(System.getProperty("user.home")));
         fileChooser.getExtensionFilters().add(extentionFilter);

         selectedFile = fileChooser.showOpenDialog(null);
         if(selectedFile != null) {
            String projectName = projectNameInput.getText().replaceAll("\\s+", "");
            File file = new File(selectedFolder.getAbsolutePath() + File.separator + projectName);

            projectFileInput.setText(selectedFile.getAbsolutePath());
            projectFolderInput.setText(file.getAbsolutePath());

            attCreateProjectButton();
            verifyProjectFolder(file);
         }
      });
   }

   private void showSelectFolderDialog() {
      Platform.runLater(() -> {
         DirectoryChooser directoryChooser = new DirectoryChooser();
         directoryChooser.setTitle("Save Clippings");
         directoryChooser.setInitialDirectory(selectedFolder);

         selectedFolder = directoryChooser.showDialog(null);
         if(selectedFolder != null) {
            projectLocationInput.setText(selectedFolder.getAbsolutePath());
            projectFolderInput.setText(selectedFolder.getAbsolutePath() + File.separator + projectNameInput.getText());
         }
      });
   }
   
   private void attCreateProjectButton() {
      boolean isNameProjectEmpty = projectNameInput.getText().isEmpty();
      boolean isFileProjectEmpty = projectFileInput.getText().isEmpty();
      
      createProjectButton.setEnabled(!isNameProjectEmpty && !isFileProjectEmpty);
   }
   
   private void verifyProjectFolder(File folder) {
      String[] files = folder.list();
      if(files != null) {
         if(files.length > 0) {
            errorLabel.setVisible(true);
            createProjectButton.setEnabled(false);
         }
         else {
            errorLabel.setVisible(false);
         }
      }
      else {
         errorLabel.setVisible(false);
      }
   }
}
