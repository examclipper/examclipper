package View;

import Controller.CropFinder;
import Controller.PDFConversor;
import Controller.CropExtractor;
import GUI.HomePanel;
import GUI.PageItemList;
import GUI.PagePanel;
import Model.Page;

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
import java.awt.event.KeyEvent;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExtractorGUI extends JFrame {

   private static final int DEFAULT_WIDTH  = 800;
   private static final int DEFAULT_HEIGHT = 600;
   private static final int MIN_WIDTH      = 400;
   private static final int MIN_HEIGHT     = 300;

   private File selectedFile;
   
   private List<Page> lPages;
   
   private Page page;
   
   public ExtractorGUI() {
      super("[Extractor]");
      
      lPages = new ArrayList<>();
      
      initComponents();
   }

   private void initComponents() {
      setLookAndFeel();
      
      setMenuBar();
      
      setHomePanel();
      
      setMainPanel();
      
      /////
      progressBar = new JProgressBar(0, 100);
      progressBar.setStringPainted(true);
      progressBar.setPreferredSize(new Dimension(400, 30));
      progressBar.setVisible(false);

      footerPanel = new JPanel();
      footerPanel.setBackground(Color.WHITE);
      footerPanel.add(progressBar);
      
      add(wrapperPanel, BorderLayout.CENTER);
      add(footerPanel,  BorderLayout.PAGE_END);
      ////

      setWindowConfigs();
      pack();

      setDefaultCloseOperation(EXIT_ON_CLOSE);
   }

   private void setLookAndFeel() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());         
      }
      catch(Exception e) {
         System.out.println(" > [ExtractorGUI] Error to set LookAndFeel: " + e);
         e.printStackTrace();
      } 
   }
   
   private void setMenuBar() {
      // Create the file sub-menu bar.
      JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      
      loadButton = new JMenuItem(("Load PDF"), KeyEvent.VK_L);
      loadButton.setEnabled(true);
      loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
      loadButton.addActionListener(actionListener -> showOpenPDFDialog());

      exitButton = new JMenuItem("Exit", KeyEvent.VK_E);
      exitButton.addActionListener(actionListener -> System.exit(0));

      fileMenu.add(loadButton);
      fileMenu.addSeparator();
      fileMenu.add(exitButton);

      JMenu helpMenu = new JMenu("Help");
      helpMenu.setMnemonic(KeyEvent.VK_H);
      
      menuBar = new JMenuBar();
      menuBar.setVisible(false);
      menuBar.add(fileMenu);
      menuBar.add(helpMenu);
      
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
      ImageIcon icon = new ImageIcon(new ImageIcon(getClass().getClassLoader().getResource("ic_pdf.png")).getImage().getScaledInstance(15, 20, Image.SCALE_SMOOTH));
      homePanel = new HomePanel(icon, actionListener -> showOpenPDFDialog());
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
      buttonsPanel = new JPanel();
      
      hSizeLabel  = new JLabel("Horizontal");
      hSizeSlider = new JSlider(1, 30, 5);

      vSizeLabel  = new JLabel("Vertical");
      vSizeSlider = new JSlider(1, 30, 5);

      findClippingsButton = new JButton();
      findClippingsButton.setText("Aplicar");
      findClippingsButton.setEnabled(false);
      findClippingsButton.addActionListener(event -> findClippingsButtonActionPerformed(event));

      sliderPanel = new JPanel();
      sliderPanel.setBorder(BorderFactory.createTitledBorder("Sliders"));

      GroupLayout sliderPanelLayout = new GroupLayout(sliderPanel);
      sliderPanelLayout.setHorizontalGroup(
         sliderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(hSizeLabel,          GroupLayout.DEFAULT_SIZE,   170,                      Short.MAX_VALUE)
         .addComponent(hSizeSlider,         GroupLayout.PREFERRED_SIZE, 0,                        Short.MAX_VALUE)
         .addComponent(vSizeLabel,          GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addComponent(vSizeSlider,         GroupLayout.PREFERRED_SIZE, 0,                        Short.MAX_VALUE)
         .addComponent(findClippingsButton, GroupLayout.DEFAULT_SIZE,   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      sliderPanelLayout.setVerticalGroup(
         sliderPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
         .addGroup(sliderPanelLayout.createSequentialGroup()
            .addComponent(hSizeLabel)
            .addGap(0, 0, 0)
            .addComponent(hSizeSlider,         GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(vSizeLabel)
            .addGap(0, 0, 0)
            .addComponent(vSizeSlider,         GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(findClippingsButton, GroupLayout.PREFERRED_SIZE, 30,                       GroupLayout.PREFERRED_SIZE))
      );
      sliderPanel.setLayout(sliderPanelLayout);      

      typeClippingsLabel = new JLabel("Tipo");

      typeClippingsComboBox = new JComboBox<>();
      typeClippingsComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Nenhum", "Enunciado", "Imagem", "Alternativa" }));
      typeClippingsComboBox.setEnabled(false);
      typeClippingsComboBox.addItemListener(event -> typeClippingsComboBoxItemStateChanged(event));

      mergeSelectedClippingsButton = new JButton();
      mergeSelectedClippingsButton.setText("Mesclar");
      mergeSelectedClippingsButton.setEnabled(false);
      mergeSelectedClippingsButton.addActionListener(event -> mergeSelectedClippingsButtonActionPerformed(event));

      extractClippings = new JButton();
      extractClippings.setText("Recortar");
      extractClippings.setEnabled(false);
      extractClippings.addActionListener(event -> extractClippingsActionPerformed(event));

      deleteSelectedClippings = new JButton();
      deleteSelectedClippings.setText("Deletar");
      deleteSelectedClippings.setEnabled(false);
      deleteSelectedClippings.addActionListener(event -> deleteSelectedClippingsActionPerformed(event));
      
      clippingsPanel = new JPanel();
      clippingsPanel.setBorder(BorderFactory.createTitledBorder("Recortes"));
      
      GroupLayout clippingsPanelLayout = new GroupLayout(clippingsPanel);
      clippingsPanelLayout.setHorizontalGroup(
         clippingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
         .addComponent(typeClippingsLabel,           GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addComponent(typeClippingsComboBox,        0,                        170,                      Short.MAX_VALUE)
         .addComponent(mergeSelectedClippingsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addComponent(extractClippings,             GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addComponent(deleteSelectedClippings,      GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      clippingsPanelLayout.setVerticalGroup(
         clippingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
         .addGroup(clippingsPanelLayout.createSequentialGroup()
            .addComponent(typeClippingsLabel)
            .addGap(0, 0, 0)
            .addComponent(typeClippingsComboBox,        GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(deleteSelectedClippings,      GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(mergeSelectedClippingsButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(extractClippings,             GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
      );
      clippingsPanel.setLayout(clippingsPanelLayout);

      buttonsPanel = new JPanel();
      buttonsPanel.setBackground(Color.WHITE);
      buttonsPanel.setBorder(BorderFactory.createTitledBorder("Configurações"));
      buttonsPanel.setPreferredSize(new Dimension(200, panel.getHeight()));
      
      GroupLayout buttonsPanelLayout = new GroupLayout(buttonsPanel);
      buttonsPanelLayout.setHorizontalGroup(
         buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
         .addGroup(buttonsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
               .addComponent(sliderPanel,    GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(clippingsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
      );
      buttonsPanelLayout.setVerticalGroup(
         buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
         .addGroup(buttonsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(sliderPanel,    GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(clippingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );
      buttonsPanel.setLayout(buttonsPanelLayout);
   }
   
   private void setWindowConfigs() {
      GraphicsDevice mGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

      int wScreen = mGraphicsDevice.getDisplayMode().getWidth();
      int hScreen = mGraphicsDevice.getDisplayMode().getHeight();

      setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
      setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
      setLocation(wScreen / 2 - DEFAULT_WIDTH / 2, hScreen / 2 - DEFAULT_HEIGHT / 2);
   }

   private void showOpenPDFDialog() {
      FileNameExtensionFilter filter = new FileNameExtensionFilter("PDFs Files (*.pdf)", "pdf");
      
      fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(selectedFile != null ? selectedFile : new File("C:\\Users\\Marlon Leoner\\Downloads\\"));
      fileChooser.setFileFilter(filter);
      
      int result = fileChooser.showOpenDialog(this);
      if(result == JFileChooser.APPROVE_OPTION) {        
         File file = fileChooser.getSelectedFile();
         System.out.println(" > [ExtractorGUI] Selected File: " + file.getName());
         SwingUtilities.invokeLater(() -> loadPDF(file));
      }
   }
   
    private void showSaveCroppingsDialog() {
      fileChooser.setCurrentDirectory(selectedFile);
      fileChooser.setName("Save Croppings");
      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
      int result = fileChooser.showOpenDialog(this);
      if(result == JFileChooser.APPROVE_OPTION) {        
         File file = fileChooser.getSelectedFile();
         System.out.println(" > [ExtractorGUI] Selected File: " + file.getName());
         SwingUtilities.invokeLater(() -> saveClippings(file));
      }
    }
   
   private void loadPDF(File file) {
      try {
         convertPDF(file);
         setTitle("[Extractor] - " + selectedFile.getName());
      }
      catch(Exception e) {
          JOptionPane.showMessageDialog(this, e.getMessage(), "Mensagem de Erro", JOptionPane.ERROR_MESSAGE);
      }
   }
   
   private void convertPDF(File pdf) throws IOException {
      // Remember last PDF opened
      selectedFile = pdf;
      
      progressBar.setVisible(true);
      progressBar.setString("Loading and Converting PDF...");
      
      LoadPagesTask lpt = new LoadPagesTask(pdf);
      lpt.execute();
   }
   
   private void findClippings() {
      CropFinder cropFinder = new CropFinder(page, hSizeSlider.getValue(), vSizeSlider.getValue());
      cropFinder.start();

      while(cropFinder.isAlive()) {
         // Wait
      }
      
      extractClippings.setEnabled(true);
      mPagePanel.deleteToSmallRectangles();
      mPagePanel.repaint();
   }
   
   private void saveClippings(File folder) {
      CropExtractor opcv = new CropExtractor();
      opcv.extractCrops(folder.getAbsolutePath(), lPages);
   }
   
   private void setStateAfterConvertionFinished(List<Page> lPages) {
      this.lPages = lPages;
      attFilesList();
      
      System.out.println(" > [ExtractorGUI] Total pages founded: " + lPages.size());
      mPagePanel.setPage(lPages.get(0));

      previewPanel.removeAll();
      previewPanel.add(mPagePanel);

      progressBar.setString("PDF Carregado!");
      progressBar.setVisible(false);
      progressBar.setValue(0);
      progressBar.setString("");
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      
      pack();
      setExtendedState(Frame.MAXIMIZED_BOTH);
      menuBar.setVisible(true);
      cardLayout.first(wrapperPanel);
      mPagePanel.repaint();
      repaint();
   }
   
   // Events
   private void findClippingsButtonActionPerformed(ActionEvent evt) {                                                    
      System.out.println(" > [GUI] Finding clippings...");
      findClippings();
   }          
   
   private void deleteSelectedClippingsActionPerformed(ActionEvent evt) {                                                        
      mPagePanel.deleteSelectedRectangles();
   }                                                       

   private void mergeSelectedClippingsButtonActionPerformed(ActionEvent evt) {                                                             
      System.out.println(" > [GUI] Merging selected clippings...");
      mPagePanel.mergeSelectedRectangles();
   }                                                            

   private void extractClippingsActionPerformed(ActionEvent evt) {                                                 
      System.out.println(" > [GUI] Extracting clippings...");
      showSaveCroppingsDialog();
   }                                                

   private void typeClippingsComboBoxItemStateChanged(ItemEvent evt) {                                                       
      System.out.println(" > [GUI] Changing clipping type...");
      mPagePanel.setNewTypeToSelectedRectangle(typeClippingsComboBox.getSelectedIndex());
      mPagePanel.repaint();
   }                                                      
   
   private void pagesListMouseClicked(MouseEvent event) {
      JList list = (JList) event.getSource();
      
      int selectedIndex = list.getSelectedIndex();
      
      findClippingsButton.setEnabled(true);
      typeClippingsComboBox.setEnabled(false);
      mergeSelectedClippingsButton.setEnabled(false);
      list.ensureIndexIsVisible(selectedIndex);
      loadImage(selectedIndex);
      if(page.getNumbersOfRectangles() == 0) {
         findClippings();
      }
   }
   
   // Methods
   public void attFilesList() {
      DefaultListModel<Page> listModel = new DefaultListModel<>();

      for(Page page : lPages) {
         listModel.addElement(page);
      }

      pagesList.setModel(listModel);
      pagesList.setCellRenderer(new PageItemList());
   }

   public void attRectanglePanelGUI(int sizeSelectedRect, int type) {
      typeClippingsComboBox.setEnabled(sizeSelectedRect == 1);
      typeClippingsComboBox.setSelectedIndex(type);
      mergeSelectedClippingsButton.setEnabled(sizeSelectedRect > 1);
      deleteSelectedClippings.setEnabled(sizeSelectedRect > 1);
   }
   
   private void loadImage(int index) {
      page = lPages.get(index);
      mPagePanel.setPage(page);
      System.out.println(" > [GUI] " + page.getFilename() + " loaded");
   }
   
   //####################################
   private JMenuBar  menuBar;
   private JMenuItem loadButton;
   private JMenuItem exitButton;
   
   private JFileChooser fileChooser; 
   
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
   private JPanel  clippingsPanel;
   private JButton deleteSelectedClippings;
   private JButton extractClippings;
   private JButton findClippingsButton;
   private JButton mergeSelectedClippingsButton;
   private JPanel  sliderPanel;
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
            progressBar.setString("(" + page + "/" + numberOfPages + ") Loading and Converting PDF...");
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
               System.out.println(" > [Conversor] Error on while");
            }
         }
         return null;
      }

      @Override
      protected void done() {
         setStateAfterConvertionFinished(lPages);
      }
   }
}
