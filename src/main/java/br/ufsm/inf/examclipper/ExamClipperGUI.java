package br.ufsm.inf.examclipper;

import br.ufsm.inf.examclipper.model.Page;
import br.ufsm.inf.examclipper.gui.HomePanel;
import br.ufsm.inf.examclipper.gui.PagePanel;
import br.ufsm.inf.examclipper.gui.PageItemList;
import br.ufsm.inf.examclipper.controller.ClipFinder;
import br.ufsm.inf.examclipper.controller.PDFConversor;
import br.ufsm.inf.examclipper.controller.ClipExtractor;
import br.ufsm.inf.examclipper.controller.OpenProject;
import br.ufsm.inf.examclipper.controller.ProjectCreator;
import br.ufsm.inf.examclipper.controller.SaveProject;
import br.ufsm.inf.examclipper.gui.NewProjectDialog;
import br.ufsm.inf.examclipper.model.Project;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import java.awt.Frame;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class ExamClipperGUI extends JFrame {

   private static final String WINDOW_TITLE = "Welcome to [ExamClipper]"; 
           
   private static final int DEFAULT_WIDTH  = 670;
   private static final int DEFAULT_HEIGHT = 490;
   private static final int MIN_WIDTH      = 400;
   private static final int MIN_HEIGHT     = 300;

   private File selectedFile;
   
   private Project project;
   
   private Page page;
   
   public ExamClipperGUI() {
      super(WINDOW_TITLE);
      
      project = null; 
      
      // JFPanel - Used for initializing javafx thread
      new JFXPanel();
      
      initComponents();
   }

   private void initComponents() {
      setLookAndFeel();
      
      setMenuBar();
      
      setHomePanel();
      
      setMainPanel();
      
      /////
      Dimension dimension = new Dimension(400, 100);
      
      progressBar = new JProgressBar(0, 100);
      progressBar.setAlignmentX(CENTER_ALIGNMENT);
      progressBar.setMaximumSize(dimension);
      progressBar.setMinimumSize(dimension);
      progressBar.setStringPainted(true);
      progressBar.setVisible(false);
      
      progressBarLabel = new JLabel();
      progressBarLabel.setAlignmentX(CENTER_ALIGNMENT);
      progressBarLabel.setMaximumSize(dimension);
      progressBarLabel.setMinimumSize(dimension);
      progressBarLabel.setFont(font);
      
      footerPanel = new JPanel();
      footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
      footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
      footerPanel.add(progressBarLabel);
      footerPanel.add(progressBar);
      
      add(wrapperPanel, BorderLayout.CENTER);
      add(footerPanel,  BorderLayout.PAGE_END);
      ////

      setWindowConfigs();
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setResizable(false);
      pack();
   }

   private void setLookAndFeel() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e) {
         System.out.println(" > [ExamClipperGUI] Error to set LookAndFeel: " + e);
         e.printStackTrace();
      } 
   }
   
   private void setMenuBar() {
      ImageIcon icNewProject  = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_plus_solid_16.png"));
      ImageIcon icOpenProject = new ImageIcon(getClass().getClassLoader().getResource("ic_folder_solid_16.png"));
      
      JMenuItem newProject = new JMenuItem("New Project", icNewProject);
      newProject.setMnemonic(KeyEvent.VK_N);
      newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
      newProject.getAccessibleContext().setAccessibleDescription("Button to create a new project");
      
      JMenuItem openProject = new JMenuItem("Open Project", icOpenProject);
      openProject.setMnemonic(KeyEvent.VK_O);
      openProject.getAccessibleContext().setAccessibleDescription("Button to open an exist project");
      
      JMenuItem exitButton = new JMenuItem("Exit", KeyEvent.VK_E);
      exitButton.addActionListener(actionListener -> System.exit(0));
      
      JMenuItem saveProjectButton = new JMenuItem("Save Project", KeyEvent.VK_S); 
      saveProjectButton.addActionListener(actionListener -> saveProject());
      
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      fileMenu.getAccessibleContext().setAccessibleDescription("File Menu");
      fileMenu.add(newProject);
      fileMenu.add(openProject);
      fileMenu.addSeparator();
      fileMenu.add(saveProjectButton); 
      fileMenu.addSeparator(); 
      fileMenu.add(exitButton);

      menuBar = new JMenuBar();
      menuBar.setVisible(false);
      menuBar.add(fileMenu);

      setJMenuBar(menuBar);
   }

   private void setMainPanel() {
      panel = new JPanel(new BorderLayout());
      panel.setMinimumSize(new Dimension(800, 600));
      panel.setVisible(false);
      
      setPagesListPanel();
      setPreviewPanel();
      setButtonsPanel();
      
      panel.add(pagesListScrollPane, BorderLayout.WEST);
      panel.add(scrollPane,          BorderLayout.CENTER);
      panel.add(buttonsPanel,        BorderLayout.EAST);
   }
   
   private void setHomePanel() {
      homePanel = new HomePanel(this);
   }
   
   private void setPagesListPanel() {     
      pagesList = new JList<Page>();
      pagesList.setBackground(Color.LIGHT_GRAY);
      pagesList.addMouseListener(new java.awt.event.MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent event) {
            pagesListMouseClicked(event);
         }
      });

      pagesListScrollPane = new JScrollPane(pagesList);
      pagesListScrollPane.setPreferredSize(new Dimension(150, panel.getHeight()));
   }
   
   private void setPreviewPanel() {
      mPagePanel = new PagePanel(this);
      
      previewPanel = new JPanel();
      previewPanel.setLayout(new FlowLayout());
      previewPanel.setEnabled(true);
      previewPanel.setBackground(new Color(186, 186, 186));
      
      scrollPane = new JScrollPane(previewPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      scrollPane.setVisible(true);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(30);
      
      cardLayout   = new CardLayout();
      wrapperPanel = new JPanel(cardLayout);
      wrapperPanel.setBackground(Color.BLUE);
      wrapperPanel.add(panel,      "main");
      wrapperPanel.add(homePanel,  "home");
      cardLayout.last(wrapperPanel);
   }
   
   private void setButtonsPanel() {
      Dimension dimension = new Dimension(200, 40);
      
      hSizeLabel  = new JLabel("Horizontal");
      hSizeLabel.setAlignmentX(CENTER_ALIGNMENT);
      hSizeLabel.setMinimumSize(dimension);
      hSizeLabel.setMaximumSize(dimension);
      
      hSizeSlider = new JSlider(1, 30, 5);
      hSizeSlider.setAlignmentX(CENTER_ALIGNMENT);
      hSizeSlider.setMinimumSize(dimension);
      hSizeSlider.setMaximumSize(dimension);

      vSizeLabel  = new JLabel("Vertical");
      vSizeLabel.setAlignmentX(CENTER_ALIGNMENT);
      vSizeLabel.setMinimumSize(dimension);
      vSizeLabel.setMaximumSize(dimension);
      
      vSizeSlider = new JSlider(1, 30, 5);
      vSizeSlider.setAlignmentX(CENTER_ALIGNMENT);
      vSizeSlider.setMinimumSize(dimension);
      vSizeSlider.setMaximumSize(dimension);
      
      findClippingsButton = new JButton("Aplicar");
      findClippingsButton.setAlignmentX(CENTER_ALIGNMENT);
      findClippingsButton.setBorder(new EmptyBorder(15, 0, 15, 0));
      findClippingsButton.setMinimumSize(dimension);
      findClippingsButton.setMaximumSize(dimension);
      findClippingsButton.addActionListener(event -> findClippingsButtonActionPerformed(event));
      findClippingsButton.setEnabled(false);
      
      // Sliders Panel
      JPanel sPanel = new JPanel();
      sPanel.setLayout(new BoxLayout(sPanel, BoxLayout.Y_AXIS));
      sPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
      sPanel.add(hSizeLabel);
      sPanel.add(hSizeSlider);
      sPanel.add(vSizeLabel);
      sPanel.add(vSizeSlider);
      sPanel.add(Box.createVerticalStrut(10));
      sPanel.add(findClippingsButton);
      
      EmptyBorder buttonsBorder = new EmptyBorder(5, 5, 5, 5);
      
      typeClippingsLabel = new JLabel("Tipo");
      typeClippingsLabel.setAlignmentX(CENTER_ALIGNMENT);
      typeClippingsLabel.setBorder(buttonsBorder);
      typeClippingsLabel.setMinimumSize(dimension);
      typeClippingsLabel.setMaximumSize(dimension);

      typeClippingsComboBox = new JComboBox<>();
      typeClippingsComboBox.setAlignmentX(CENTER_ALIGNMENT);
      typeClippingsComboBox.setBorder(buttonsBorder);
      typeClippingsComboBox.setMinimumSize(dimension);
      typeClippingsComboBox.setMaximumSize(dimension);
      typeClippingsComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Nenhum", "Enunciado", "Imagem", "Alternativa" }));
      typeClippingsComboBox.addItemListener(event -> typeClippingsComboBoxItemStateChanged(event));
      typeClippingsComboBox.setEnabled(false);

      mergeSelectedClippingsButton = new JButton("Mesclar");
      mergeSelectedClippingsButton.setAlignmentX(CENTER_ALIGNMENT);
      mergeSelectedClippingsButton.setBorder(buttonsBorder);
      mergeSelectedClippingsButton.setMinimumSize(dimension);
      mergeSelectedClippingsButton.setMaximumSize(dimension);
      mergeSelectedClippingsButton.addActionListener(event -> mergeSelectedClippingsButtonActionPerformed(event));
      mergeSelectedClippingsButton.setEnabled(false);

      extractClippings = new JButton("Recortar");
      extractClippings.setAlignmentX(CENTER_ALIGNMENT);
      extractClippings.setBorder(buttonsBorder);
      extractClippings.setMinimumSize(dimension);
      extractClippings.setMaximumSize(dimension);
      extractClippings.addActionListener(event -> extractClippingsActionPerformed(event));
      extractClippings.setEnabled(false);

      deleteSelectedClippings = new JButton("Deletar");
      deleteSelectedClippings.setAlignmentX(CENTER_ALIGNMENT);
      deleteSelectedClippings.setBorder(buttonsBorder);
      deleteSelectedClippings.setMinimumSize(dimension);
      deleteSelectedClippings.setMaximumSize(dimension);
      deleteSelectedClippings.addActionListener(event -> deleteSelectedClippingsActionPerformed(event));
      deleteSelectedClippings.setEnabled(false);
      
      deleteAllClippings = new JButton("Limpar");
      deleteAllClippings.setAlignmentX(CENTER_ALIGNMENT);
      deleteAllClippings.setBorder(buttonsBorder);
      deleteAllClippings.setMinimumSize(dimension);
      deleteAllClippings.setMaximumSize(dimension);
      deleteAllClippings.addActionListener(event -> deleteAllClippingsActionPerformed(event));
      deleteAllClippings.setEnabled(false);
      
      // Clipping Panel
      JPanel cPanel = new JPanel();
      cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.Y_AXIS));
      cPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
      cPanel.add(typeClippingsLabel);
      cPanel.add(typeClippingsComboBox);
      cPanel.add(Box.createVerticalStrut(25));
      cPanel.add(mergeSelectedClippingsButton);
      cPanel.add(deleteSelectedClippings);
      cPanel.add(deleteAllClippings);
      cPanel.add(Box.createVerticalStrut(25));
      cPanel.add(extractClippings);

      buttonsPanel = new JPanel(new BorderLayout());
      buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
      buttonsPanel.setBackground(Color.WHITE);
      buttonsPanel.setPreferredSize(new Dimension(200, panel.getHeight()));
      buttonsPanel.add(sPanel, BorderLayout.NORTH);
      buttonsPanel.add(cPanel, BorderLayout.SOUTH);
   }
   
   private void setWindowConfigs() {
      GraphicsDevice mGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

      int wScreen = mGraphicsDevice.getDisplayMode().getWidth();
      int hScreen = mGraphicsDevice.getDisplayMode().getHeight();

      setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
      setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
      setLocation(wScreen / 2 - DEFAULT_WIDTH / 2, hScreen / 2 - DEFAULT_HEIGHT / 2);
   }

   public void showNewProjectDialog() {
      NewProjectDialog dialog = new NewProjectDialog(this);

      project = dialog.run();
      if(project != null) {
         SwingUtilities.invokeLater(() -> createNewProject());
      }
   }
   
   public void showOpenProjectDialog() { 
      Platform.runLater(() -> { 
         FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("ExamClipper files (*.examclipper-project)", "*.examclipper-project"); 
 
         fileChooser = new FileChooser(); 
         fileChooser.setTitle("Open Project"); 
         fileChooser.setInitialDirectory(project != null ? project.getLocation(): new File(System.getProperty("user.home"))); 
         fileChooser.getExtensionFilters().add(extentionFilter); 
 
         File file = fileChooser.showOpenDialog(null); 
         if(file != null) { 
            System.out.println(" > [ExamClipperGUI] Open Project File: " + file.getAbsolutePath()); 
            SwingUtilities.invokeLater(() -> loadProject(file)); 
         } 
      }); 
   } 
   
   public void showOpenPDFDialog() {
      Platform.runLater(() -> {
         FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");

         fileChooser = new FileChooser();
         fileChooser.setTitle("Open PDF");
         fileChooser.setInitialDirectory(selectedFile != null ? selectedFile : new File(System.getProperty("user.home")));
         fileChooser.getExtensionFilters().add(extentionFilter);

         File file = fileChooser.showOpenDialog(null);
         if(file != null) {
            System.out.println(" > [ExamClipperGUI] Selected File: " + file.getName());
            SwingUtilities.invokeLater(() -> loadPDF(file));
         }
      });
   }
   
   private void showSaveClippingsDialog() {
      Platform.runLater(() -> {
         directoryChooser = new DirectoryChooser();
         directoryChooser.setTitle("Save Clippings");
         directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

         File file = directoryChooser.showDialog(null);
         if(file != null) {
            System.out.println(" > [ExamClipperGUI] Selected File: " + file.getName());
            SwingUtilities.invokeLater(() -> saveClippings(file));
         }
      });
   }
   
   private void createNewProject() {
      progressBarLabel.setText("Criando Projeto...");
      progressBar.setVisible(true);
      
      File file = project.getPDF();
      if(file != null) {
         System.out.println(" > [ExamClipperGUI] Converting before project creation: " + file.getName());
         SwingUtilities.invokeLater(() -> loadPDF(file));
      }
   }
   
   private void loadProject(File file) { 
      progressBarLabel.setText("Abrindo Projeto..."); 
      progressBar.setVisible(true); 
          
      OpenProjectTask opt = new OpenProjectTask(file); 
      opt.execute(); 
   } 
    
   private void saveProject() { 
      System.out.println(" > [ExamClipperGUI] Saving project ..."); 
 
      progressBarLabel.setText("Salvando Projeto..."); 
      progressBarLabel.setVisible(true); 
      progressBar.setVisible(true); 
 
      SaveProjectTask spt = new SaveProjectTask(project); 
      spt.execute(); 
   } 
   
   private void loadPDF(File file) {
      try {
         convertPDF(file);
      }
      catch(Exception e) {
          JOptionPane.showMessageDialog(this, e.getMessage(), "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
      }
   }
   
   private void convertPDF(File pdf) throws IOException {
      // Remember last PDF opened
      selectedFile = pdf;
      
      progressBarLabel.setText("Carregando e Convertendo PDF...");
      progressBar.setVisible(true);
      
      LoadPagesTask lpt = new LoadPagesTask(pdf);
      lpt.execute();
   }
   
   private void findClippings() {
      ClipFinder clipFinder = new ClipFinder(page, hSizeSlider.getValue(), vSizeSlider.getValue());
      clipFinder.start();

      while(clipFinder.isAlive()) {
         // Wait
      }
      
      extractClippings.setEnabled(true);
      mPagePanel.deleteToSmallClippings();
      mPagePanel.repaint();
   }
   
   private void saveClippings(File folder) {
      ClipExtractor opcv = new ClipExtractor();
      opcv.extractClippings(folder.getAbsolutePath(), project.getPages());
   }
   
   private void setStateAfterConvertionFinished(List<Page> lPages) {
      project.setPages(lPages);
      
      CreateProjectTask cpt = new CreateProjectTask(project);
      cpt.execute();
   }
   
   private void setStateAfterProjectCreationFinished(Project project) {
      this.project = project;
      List<Page> lPages = project.getPages();
      
      attFilesList();
      
      System.out.println(" > [ExamClipperGUI] Numbers of pages found: " + lPages.size());
      mPagePanel.setPage(lPages.get(0));

      previewPanel.removeAll();
      previewPanel.add(mPagePanel);

      progressBar.setString("PDF Carregado!");
      progressBar.setVisible(false);
      progressBar.setValue(0);
      progressBar.setString("");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      
      pack();
      menuBar.setVisible(true);
      cardLayout.first(wrapperPanel);
      mPagePanel.repaint();
      setTitle(project.getName() + " [" + project.getLocation().getAbsolutePath() + "] - ExamClipper"); 
      setResizable(true);
      setExtendedState(Frame.MAXIMIZED_BOTH);
      repaint();
   }
   
   private void setStateAfterOpeningProjectFinished(Project project) { 
      this.project = project; 
      List<Page> lPages = project.getPages(); 
       
      attFilesList(); 
       
      System.out.println(" > [ExamClipperGUI] Numbers of pages found: " + lPages.size()); 
      mPagePanel.setPage(lPages.get(0)); 
 
      previewPanel.removeAll(); 
      previewPanel.add(mPagePanel); 
 
      progressBarLabel.setVisible(false); 
      progressBar.setVisible(false); 
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
       
      pack(); 
      menuBar.setVisible(true); 
      cardLayout.first(wrapperPanel); 
      mPagePanel.repaint(); 
      setTitle(project.getName() + " [" + project.getLocation().getAbsolutePath() + "] - ExamClipper"); 
      setResizable(true); 
      setExtendedState(Frame.MAXIMIZED_BOTH); 
      repaint(); 
   } 
    
   private void setStateAfterSavingProjectFinished() { 
      progressBarLabel.setVisible(false); 
      progressBar.setVisible(false); 
   } 
   
   // Events
   private void findClippingsButtonActionPerformed(ActionEvent evt) {                                                    
      System.out.println(" > [ExamClipperGUI] Finding clippings...");
      findClippings();
      deleteAllClippings.setEnabled(true);
   }          
   
   private void deleteSelectedClippingsActionPerformed(ActionEvent evt) {                                                        
      mPagePanel.deleteSelectedClippings();
   }

   private void deleteAllClippingsActionPerformed(ActionEvent evt) {                                                        
      mPagePanel.deleteAllClippings();
      deleteAllClippings.setEnabled(false);
   }
   
   private void mergeSelectedClippingsButtonActionPerformed(ActionEvent evt) {                                                             
      System.out.println(" > [ExamClipperGUI] Merging selected clippings...");
      mPagePanel.mergeSelectedClippings();
   }                                                            

   private void extractClippingsActionPerformed(ActionEvent evt) {                                                 
      System.out.println(" > [ExamClipperGUI] Extracting clippings...");
      showSaveClippingsDialog();
   }                                                

   private void typeClippingsComboBoxItemStateChanged(ItemEvent evt) {                                                       
      System.out.println(" > [ExamClipperGUI] Changing clipping type...");
      mPagePanel.setNewTypeToSelectedRectangle(typeClippingsComboBox.getSelectedIndex());
      mPagePanel.repaint();
   }                                                      
   
   private void pagesListMouseClicked(MouseEvent event) {
      JList list = (JList) event.getSource();
      
      int selectedIndex = list.getSelectedIndex();
      
      findClippingsButton.setEnabled(true);
      typeClippingsComboBox.setEnabled(false);
      mergeSelectedClippingsButton.setEnabled(false);
      deleteAllClippings.setEnabled(true);
      list.ensureIndexIsVisible(selectedIndex);
      loadImage(selectedIndex);
      // Do not find clippings before the user asks us to do so
      //if(page.getNumbersOfClippings()== 0) {
      //   findClippings();
      //}
   }
   
   // Methods
   public void attFilesList() {
      DefaultListModel<Page> listModel = new DefaultListModel<>();

      for(Page p : project.getPages()) {
         listModel.addElement(p);
      }

      pagesList.setModel(listModel);
      pagesList.setCellRenderer(new PageItemList());
   }

   public void attRectanglePanelGUI(int totalClipping, int sizeSelectedClipping, int type) {
      typeClippingsComboBox.setEnabled(sizeSelectedClipping == 1);
      typeClippingsComboBox.setSelectedIndex(type);
      mergeSelectedClippingsButton.setEnabled(sizeSelectedClipping > 1);
      deleteSelectedClippings.setEnabled(sizeSelectedClipping > 1);
      deleteAllClippings.setEnabled(totalClipping > 1);
   }
   
   private void loadImage(int index) {      
      page = project.getPages().get(index);
      mPagePanel.setPage(page);
      System.out.println(" > [ExamClipperGUI] " + page.getFilename() + " loaded");
   }
   
   //####################################
   private Font font = new Font("Meiryo", Font.PLAIN, 12);
   
   private JMenuBar menuBar;
   
   private FileChooser      fileChooser;
   private DirectoryChooser directoryChooser;
   
   private JLabel       progressBarLabel;
   private JProgressBar progressBar;
      
   private JPanel panel;
   
   private JPanel homePanel;
   
   private JScrollPane pagesListScrollPane;
   private JList       pagesList;
   private PagePanel   mPagePanel;
   
   private CardLayout  cardLayout;
   private JPanel      wrapperPanel;
   private JScrollPane scrollPane;
   private JPanel      previewPanel;
   
   private JPanel  buttonsPanel;
   private JButton deleteSelectedClippings;
   private JButton deleteAllClippings;
   private JButton extractClippings;
   private JButton findClippingsButton;
   private JButton mergeSelectedClippingsButton;
   private JLabel  hSizeLabel;
   private JLabel  vSizeLabel;
   private JSlider hSizeSlider;
   private JSlider vSizeSlider;
   private JLabel  typeClippingsLabel;
   private JComboBox<String> typeClippingsComboBox;
   
   private JPanel footerPanel;
   //####################################

   private class LoadPagesTask extends SwingWorker<Void, Void> {
      
      // PDF File
      private final File file;
      // List w/ Mat
      private final List<Page> lPages;
      
      public LoadPagesTask(File file) {
         super();
         
         this.file   = file;
         this.lPages = new ArrayList<>();
      }
      
      @Override
      protected Void doInBackground() throws Exception {
 
         // Conversor
         PDFConversor conversor = new PDFConversor(file, lPages);
         // Total
         int numberOfPages = conversor.getNumberOfPages();
         // Thread Start
         conversor.start();
         
         while(conversor.isAlive()) {
            int page    = conversor.currentPage;           
            int percent = (int) ((page / (float) numberOfPages) * 100);
            progressBar.setValue(percent);
            progressBar.setString(percent + " %");
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
               System.out.println(" > [LoadPagesTask] Error on while");
            }
         }
         return null;
      }

      @Override
      protected void done() {
         setStateAfterConvertionFinished(lPages);
      }
   }
   
   private class CreateProjectTask extends SwingWorker<Void, Void> {

      private final Project project;
      
      public CreateProjectTask(Project project) {
         this.project = project;
      }
      
      @Override
      protected Void doInBackground() throws Exception {
         
         ProjectCreator pc = new ProjectCreator(this.project);
         pc.start();
         
         int numbersOfSteps = pc.numbersOfSteps;
         
         while(pc.isAlive()) {
            int step    = pc.currentStep;           
            int percent = (int) ((step / (float) numbersOfSteps) * 100);
            progressBar.setValue(percent);
            progressBar.setString(percent + " %");
            //progressBar.setString("(" + step + "/" + numbersOfSteps + ") Creating Project...");
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
               System.out.println(" > [CreateProjectTask] Error on while");
            }
         }
         
         return null;
      }
      
      @Override
      protected void done() {
         setStateAfterProjectCreationFinished(project);
      }
   }
   
   private class OpenProjectTask extends SwingWorker<Void, Void> { 
 
      private Project project; 
       
      private File file; 
       
      public OpenProjectTask(File file) { 
         this.file = file; 
      } 
             
      @Override 
      protected Void doInBackground() throws Exception { 
          
         OpenProject op = new OpenProject(this.file); 
         op.start(); 
          
         int numbersOfSteps = op.numbersOfSteps; 
          
         while(op.isAlive()) { 
            int step = op.currentStep; 
            if(step == 2) numbersOfSteps = op.numbersOfSteps; 
            int percent = (int) ((step / (float) numbersOfSteps) * 100); 
            progressBar.setValue(percent); 
            progressBar.setString(percent + " %"); 
            try { 
                Thread.sleep(50); 
            } 
            catch (InterruptedException e) { 
               System.out.println(" > [CreateProjectTask] Error on while"); 
            } 
         } 
          
         project = op.getProject();          
         return null; 
      } 
       
      @Override 
      protected void done() { 
         setStateAfterOpeningProjectFinished(project); 
      } 
   } 
    
   private class SaveProjectTask extends SwingWorker<Void, Void> { 
 
      private Project project; 
       
      public SaveProjectTask(Project project) { 
         this.project = project; 
      } 
             
      @Override 
      protected Void doInBackground() throws Exception { 
          
         SaveProject sp = new SaveProject(project); 
         sp.start(); 
          
         int numbersOfSteps = sp.numbersOfSteps; 
          
         while(sp.isAlive()) { 
            int step = sp.currentStep; 
            if(step == 1) numbersOfSteps = sp.numbersOfSteps; 
            int percent = (int) ((step / (float) numbersOfSteps) * 100); 
            progressBar.setValue(percent); 
            progressBar.setString(percent + " %"); 
            try { 
                Thread.sleep(50); 
            } 
            catch (InterruptedException e) { 
               System.out.println(" > [CreateProjectTask] Error on while"); 
            } 
         } 
            
         return null; 
      } 
       
      @Override 
      protected void done() { 
         setStateAfterSavingProjectFinished(); 
      } 
   } 
}
