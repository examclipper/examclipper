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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class NewProjectDialog extends JDialog implements ActionListener {

   private static final int DEFAULT_WIDTH = 670;
   private static final int DEFAULT_HEIGHT = 490;

   private static final int LABEL_WIDTH = 650;
   private static final int INPUT_WIDTH = 650;
   private static final int IC_BTN_WIDTH = 50;
   private static final int LABEL_HEIGHT = 20;
   private static final int INPUT_HEIGHT = 35;
   private static final int IC_BTN_HEIGHT = 35;

   private static final int BUTTON_WIDTH = 120;
   private static final int BUTTON_HEIGHT = 30;

   private Project project;

   private File selectedFile;
   private File selectedFolder;

   public NewProjectDialog(Frame parent) {
      super(parent, "Novo Projeto", true);

      project = null;
      selectedFile = null;
      selectedFolder = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());

      Point parentLocation = parent.getLocation();
      setLocation(parentLocation.x, parentLocation.y);

      JLabel projectNameLabel = new JLabel();
      JLabel projectFileLabel = new JLabel();
      JLabel projectLocationLabel = new JLabel();
      JLabel projectFolderLabel = new JLabel();

      Dimension size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      JPanel panel = new JPanel();
      panel.setMaximumSize(size);
      panel.setMinimumSize(size);
      panel.setPreferredSize(size);
      panel.setBackground(Color.WHITE);
      panel.setLayout(null);

      Font font = new Font("Meiryo", Font.PLAIN, 12);

      projectNameLabel.setText("Nome do Projeto");
      projectNameLabel.setFont(font);
      projectNameLabel.setBounds(10, 10, LABEL_WIDTH, LABEL_HEIGHT);

      projectFileLabel.setText("Arquivo do Projeto");
      projectFileLabel.setFont(font);
      projectFileLabel.setBounds(10, 80, LABEL_WIDTH, LABEL_HEIGHT);

      projectLocationLabel.setText("Localização do Projeto");
      projectLocationLabel.setFont(font);
      projectLocationLabel.setBounds(10, 150, LABEL_WIDTH, LABEL_HEIGHT);

      projectFolderLabel.setText("Pasta do Projeto");
      projectFolderLabel.setFont(font);
      projectFolderLabel.setBounds(10, 220, LABEL_WIDTH, LABEL_HEIGHT);

      projectNameInput = new JTextField();
      projectNameInput.setFont(font);
      projectNameInput.setBorder(BorderFactory.createCompoundBorder(projectNameInput.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
      projectNameInput.setBounds(10, 30, INPUT_WIDTH, INPUT_HEIGHT);
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
            if (!projectName.isEmpty()) {
               projectName = projectName.replaceAll("\\s+", "");
               File file = new File(selectedFolder.getAbsolutePath() + File.separator + projectName);

               projectFolderInput.setText(file.getAbsolutePath());

               attCreateProjectButton();
               verifyProjectFolder(file);
            }
         }
      });

      projectFileInput = new JTextField();
      projectFileInput.setFont(font);
      projectFileInput.setBorder(BorderFactory.createCompoundBorder(projectFileInput.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
      projectFileInput.setEnabled(false);
      projectFileInput.setBounds(10, 100, INPUT_WIDTH - IC_BTN_WIDTH - 10, INPUT_HEIGHT);

      ImageIcon icOpenPDF = new ImageIcon(getClass().getClassLoader().getResource("ic_pdf_16.png"));
      findFileProjectButton = new JButton(icOpenPDF);
      findFileProjectButton.addActionListener(this);
      findFileProjectButton.setBounds(610, 100, IC_BTN_WIDTH, IC_BTN_HEIGHT);

      projectLocationInput = new JTextField(selectedFolder.getAbsolutePath());
      projectLocationInput.setFont(font);
      projectLocationInput.setBorder(BorderFactory.createCompoundBorder(projectLocationInput.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
      projectLocationInput.setEnabled(false);
      projectLocationInput.setBounds(10, 170, INPUT_WIDTH - IC_BTN_WIDTH - 10, INPUT_HEIGHT);

      ImageIcon icNewProject = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_plus_solid_16.png"));
      findFolderProjectButton = new JButton(icNewProject);
      findFolderProjectButton.addActionListener(this);
      findFolderProjectButton.setBounds(610, 170, IC_BTN_WIDTH, IC_BTN_HEIGHT);

      projectFolderInput = new JTextField(selectedFolder.getAbsolutePath());
      projectFolderInput.setFont(font);
      projectFolderInput.setBorder(BorderFactory.createCompoundBorder(projectFolderInput.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
      projectFolderInput.setEnabled(false);
      projectFolderInput.setBounds(10, 240, INPUT_WIDTH, INPUT_HEIGHT);

      createProjectButton = new JButton("Criar");
      createProjectButton.setEnabled(false);
      createProjectButton.setFont(font);
      createProjectButton.addActionListener(this);
      createProjectButton.setBounds(410, DEFAULT_HEIGHT - BUTTON_HEIGHT - 10, BUTTON_WIDTH, BUTTON_HEIGHT);

      cancelProjectButton = new JButton("Cancelar");
      cancelProjectButton.setFont(font);
      cancelProjectButton.addActionListener(this);
      cancelProjectButton.setBounds(540, DEFAULT_HEIGHT - BUTTON_HEIGHT - 10, BUTTON_WIDTH, BUTTON_HEIGHT);

      ImageIcon icWarning = new ImageIcon(getClass().getClassLoader().getResource("ic_warning_16.png"));
      errorLabel = new JLabel("A pasta do projeto já existe e não está vazia!");
      errorLabel.setVisible(false);
      errorLabel.setFont(font);
      errorLabel.setForeground(Color.RED);
      errorLabel.setIcon(icWarning);
      errorLabel.setBounds(10, 280, LABEL_WIDTH, LABEL_HEIGHT);

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
         project.setPDF(selectedFile);
         project.setLocation(new File(selectedFolder + File.separator + projectName.replaceAll("\\s+", "")));

         dispose();
      } else if (source == cancelProjectButton) {
         project = null;
         dispose();
      } else if (source == findFileProjectButton) {
         showOpenPDFDialog();
      } else if (source == findFolderProjectButton) {
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
         fileChooser.setTitle("Abrir PDF");
         fileChooser.setInitialDirectory(selectedFile != null ? selectedFile : new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()));
         fileChooser.getExtensionFilters().add(extentionFilter);

         selectedFile = fileChooser.showOpenDialog(null);
         if (selectedFile != null) {
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
         directoryChooser.setTitle("Selecione Pasta");
         directoryChooser.setInitialDirectory(selectedFolder);

         selectedFolder = directoryChooser.showDialog(null);
         if (selectedFolder != null) {
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
      if (files != null) {
         if (files.length > 0) {
            errorLabel.setVisible(true);
            createProjectButton.setEnabled(false);
         } else {
            errorLabel.setVisible(false);
         }
      } else {
         errorLabel.setVisible(false);
      }
   }
}
