package View;

/**
 * IndividualProjectFrame is a GUI window for managing assets within a specific project.
 * Allows adding, selecting, and organizing object, music, and environment assets.
 * Supports undo/redo functionality for actions.
 */
import Controller.*;
import Model.*;
import javax.swing.*;
import java.awt.Dimension; // for button sizing
import javax.swing.JFileChooser;
import java.util.*;
import java.io.File;

//import java.util.Stack;
import java.awt.*;
import java.awt.Component;
import java.util.List;
import java.util.ArrayList;

public class IndividualProjectFrame extends javax.swing.JFrame {
    // Currently selected asset button
    private JButton selectedAssetButton = null;
    // Currently selected asset type
    private AssetType selectedAssetType = null;
    // Current user controller
    private CurrentUserController userController;
    // Projects controller
    private ProjectsController projectController;
    // List of object assets
    private java.util.List<AssetInfo> objectAssets = new ArrayList<>();
    // List of music assets
    private java.util.List<AssetInfo> musicAssets = new ArrayList<>();
    // List of environment assets
    private java.util.List<AssetInfo> environmentAssets = new ArrayList<>();

    /**
     * Inner class to hold asset information.
     */
    private class AssetInfo {
        JButton button;
        File file;

        AssetInfo(JButton button, File file) {
            this.button = button;
            this.file = file;
        }

        // Helper to get current name for sorting
        String getName() {
            return button.getText(); // use the button text instead of file
        }
    }

    // Selected asset panel
    private JPanel selectedAssetPanel = null;
    // Undo stack for actions
    MyStack<Action> undoStack = new MyStack<>(50);
    // Redo stack for actions
    MyStack<Action> redoStack = new MyStack<>(50);
    // Tracks the next row for buttons in ObjectsPanel
    private int objectPanelRow = 0;

    // Similarly, you can have for MusicPanel and EnvironmentPanel
    private int musicPanelRow = 0;
    private int environmentPanelRow = 0;

    // Optional: limit undo stack size
    private static final int MAX_UNDO = 500;

    private void logAction(String msg) {
        System.out.println(msg); // Optional: print to console

        // Add label to lastActionsPanel
        JLabel label = new JLabel(msg);
        lastActionsPanel.add(label);
        lastActionsPanel.revalidate();
        lastActionsPanel.repaint();
    }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(IndividualProjectFrame.class.getName());

    /**
     * Enum for asset types.
     */
    private enum AssetType { OBJECT, MUSIC, ENVIRONMENT }

    /**
     * Inner class representing an action for undo/redo.
     */
    private class Action {
        enum ActionType { ADD, RENAME, DELETE }

        ActionType type;
        AssetType assetType;
        JPanel panel;
        JButton assetButton;
        String oldName;
        String newName;
        String message;

        Action(ActionType type, AssetType assetType, JPanel panel,
               JButton assetButton, String oldName, String newName, String message) {
            this.type = type;
            this.assetType = assetType;
            this.panel = panel;
            this.assetButton = assetButton;
            this.oldName = oldName;
            this.newName = newName;
            this.message = message;
        }
    }


    
    
        // Add asset (object, music, environment)
private void addAsset(AssetType type, JPanel panel) {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

    File file = chooser.getSelectedFile();
    String name = file.getName();

    JButton assetButton = new JButton(name);
    assetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
    assetButton.setMaximumSize(new Dimension(360, 32));
    assetButton.setPreferredSize(new Dimension(360, 32));

    // Add button to panel
    panel.add(assetButton);
    panel.add(Box.createVerticalStrut(6));

    // Track selection
    selectedAssetButton = assetButton;
    selectedAssetType = type;
    selectedAssetPanel = panel;
    NameField.setText(name);

    // Action listener
    assetButton.addActionListener(e -> {
        selectedAssetButton = assetButton;
        selectedAssetType = type;
        selectedAssetPanel = panel;
        NameField.setText(assetButton.getText());
    });

    // Add to the corresponding asset list
    AssetInfo assetInfo = new AssetInfo(assetButton, file);
    switch (type) {
        case OBJECT -> objectAssets.add(assetInfo);
        case MUSIC -> musicAssets.add(assetInfo);
        case ENVIRONMENT -> environmentAssets.add(assetInfo);
    }

    String msg = "Added " + type.name().toLowerCase() + ": " + name;
    logAction(msg);
    undoStack.push(new Action(Action.ActionType.ADD, type, panel, assetButton, null, name, msg));
    redoStack.clear();

}

private void sortAssets(AssetType type) {
    List<AssetInfo> list;
    JPanel panel;

    switch (type) {
        case OBJECT -> { list = objectAssets; panel = ObjectsPanel; }
        case MUSIC -> { list = musicAssets; panel = MusicPanel; }
        case ENVIRONMENT -> { list = environmentAssets; panel = EnvironmentPanel; }
        default -> { return; }
    }

    // Get selected sort type from combo box
    String selectedSort = (String) jComboBox1.getSelectedItem();

    // Sort
if (selectedSort != null && selectedSort.contains("Name")) {
    list.sort(Comparator.comparing(a -> a.getName().toLowerCase()));
} else if (selectedSort != null && selectedSort.contains("Date")) {
        list.sort(Comparator.comparing(a -> a.file.lastModified()));
    }

    // Refresh panel
    panel.removeAll();
    for (AssetInfo info : list) {
        panel.add(info.button);
        panel.add(Box.createVerticalStrut(6));
    }
    panel.revalidate();
    panel.repaint();
}



    
        // Rename selected asset
    private void renameSelectedAsset() {
    if (selectedAssetButton == null) return;
    String newName = NameField.getText().trim();
    String oldName = selectedAssetButton.getText();
    if (!newName.isEmpty() && !newName.equals(oldName)) {
        selectedAssetButton.setText(newName);
        String msg = "Renamed " + selectedAssetType.name().toLowerCase() + ": " + oldName + " → " + newName;
        logAction(msg);
        undoStack.push(new Action(Action.ActionType.RENAME, selectedAssetType, selectedAssetPanel, selectedAssetButton, oldName, null, msg));
        redoStack.clear();

        // Re-sort after rename
        sortAssets(selectedAssetType);
    }
    }
    
      // Delete selected asset
    private void deleteSelectedAsset() {
        if (selectedAssetButton == null) return;
        String oldName = selectedAssetButton.getText();
        String msg = "Deleted " + selectedAssetType.name().toLowerCase() + ": " + oldName;
        selectedAssetPanel.remove(selectedAssetButton);
        selectedAssetPanel.revalidate();
        selectedAssetPanel.repaint();
        logAction(msg);
        undoStack.push(new Action(Action.ActionType.DELETE, selectedAssetType, selectedAssetPanel, selectedAssetButton, oldName, null, msg));
        redoStack.clear();
        selectedAssetButton = null;
    }
    
     // Undo
private void undo() {
    if (undoStack.isEmpty()) return;

    Action action = undoStack.pop();

    switch (action.type) {

        case ADD -> {
            action.panel.remove(action.assetButton);
            action.panel.revalidate();
            action.panel.repaint();
            logAction("Undo add: " + action.newName);
        }

        case RENAME -> {
            action.assetButton.setText(action.oldName);
            logAction("Undo rename: " + action.newName + " → " + action.oldName);
        }

        case DELETE -> {
            action.panel.add(action.assetButton);
            action.panel.revalidate();
            action.panel.repaint();
            logAction("Undo delete: " + action.oldName);
        }
    }

    redoStack.push(action);
}

    
      // Redo
private void redo() {
    if (redoStack.isEmpty()) return;

    Action action = redoStack.pop();

    switch (action.type) {

        case ADD -> {
            action.panel.add(action.assetButton);
            action.panel.revalidate();
            action.panel.repaint();
            logAction("Redo add: " + action.newName);
        }

        case RENAME -> {
            action.assetButton.setText(action.newName);
            logAction("Redo rename: " + action.oldName + " → " + action.newName);
        }

        case DELETE -> {
            action.panel.remove(action.assetButton);
            action.panel.revalidate();
            action.panel.repaint();
            logAction("Redo delete: " + action.oldName);
        }
    }

    undoStack.push(action);
}

    
     // Delete history
    private void deleteHistory() {
        undoStack.clear();
        redoStack.clear();
        lastActionsPanel.removeAll();
        lastActionsPanel.revalidate();
        lastActionsPanel.repaint();
    }

    // Reset project
    private void resetProject() {
        ObjectsPanel.removeAll();
        MusicPanel.removeAll();
        EnvironmentPanel.removeAll();
        ObjectsPanel.revalidate();
        MusicPanel.revalidate();
        EnvironmentPanel.revalidate();
        ObjectsPanel.repaint();
        MusicPanel.repaint();
        EnvironmentPanel.repaint();
        deleteHistory();
    }

    // Save project (dummy)
    private void saveProject() {
        logAction("Project saved");
    }
    
    
    
    /**
     * Creates new form IndividualProjectFrame
     */
    public IndividualProjectFrame() {
        initComponents();
    }
    
public IndividualProjectFrame(Project project) {
    this.project = project;
    this.projectDataController = new ProjectDataController(project);
    initComponents();
    projectDataController.load(); // load project-specific data
}



    private Project project;
    private ProjectDataController projectDataController;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        label1 = new java.awt.Label();
        textField2 = new java.awt.TextField();
        textField1 = new java.awt.TextField();
        jButton10 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        LeftMainPanel = new java.awt.Panel();
        jPanel3 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        AddObjectButton = new javax.swing.JButton();
        plusObjectButton = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        ObjectsPanel = new javax.swing.JPanel();
        MusicLabelPanel = new javax.swing.JPanel();
        addMusicButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        MusicPanel = new javax.swing.JPanel();
        EnvironmentLabelPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        addEnvironmentButton = new javax.swing.JButton();
        EnvironmentPanel = new javax.swing.JPanel();
        RightMainPanel = new java.awt.Panel();
        panel9 = new java.awt.Panel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        NameField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jLabel9 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        jSeparator4 = new javax.swing.JSeparator();
        panel10 = new java.awt.Panel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jButton6 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        lastActionsPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        panel3 = new java.awt.Panel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();

        label1.setText("label1");

        textField2.setText("textField2");

        textField1.setText("textField1");

        jButton10.setText("jButton10");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        LeftMainPanel.setBackground(new java.awt.Color(44, 44, 44));
        LeftMainPanel.setPreferredSize(new java.awt.Dimension(426, 1085));
        LeftMainPanel.setLayout(new javax.swing.BoxLayout(LeftMainPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBackground(new java.awt.Color(44, 44, 44));
        jPanel3.setPreferredSize(new java.awt.Dimension(426, 203));

        jComboBox1.setBackground(new java.awt.Color(44, 44, 44));
        jComboBox1.setForeground(new java.awt.Color(255, 255, 255));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort By: Name", "Sort By; Date Created" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Objects");

        jPanel2.setBackground(new java.awt.Color(44, 44, 44));
        jPanel2.setInheritsPopupMenu(true);
        jPanel2.setMaximumSize(new java.awt.Dimension(426, 426));
        jPanel2.setMinimumSize(new java.awt.Dimension(426, 12));
        jPanel2.setPreferredSize(new java.awt.Dimension(426, 12));

        AddObjectButton.setBackground(new java.awt.Color(179, 156, 208));
        AddObjectButton.setForeground(new java.awt.Color(44, 44, 44));
        AddObjectButton.setText("Add Objects");
        AddObjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddObjectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(AddObjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(AddObjectButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        plusObjectButton.setBackground(new java.awt.Color(79, 78, 78));
        plusObjectButton.setForeground(new java.awt.Color(255, 255, 255));
        plusObjectButton.setText("+");
        plusObjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusObjectButtonActionPerformed(evt);
            }
        });

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(plusObjectButton)
                .addGap(15, 15, 15))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, 349, Short.MAX_VALUE)
                            .addComponent(searchField))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(plusObjectButton)))
        );

        LeftMainPanel.add(jPanel3);

        ObjectsPanel.setBackground(new java.awt.Color(44, 44, 44));
        ObjectsPanel.setLayout(new javax.swing.BoxLayout(ObjectsPanel, javax.swing.BoxLayout.Y_AXIS));
        ObjectsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        LeftMainPanel.add(ObjectsPanel);

        MusicLabelPanel.setBackground(new java.awt.Color(44, 44, 44));

        addMusicButton.setBackground(new java.awt.Color(79, 78, 78));
        addMusicButton.setForeground(new java.awt.Color(255, 255, 255));
        addMusicButton.setText("+");
        addMusicButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMusicButtonActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Music");

        javax.swing.GroupLayout MusicLabelPanelLayout = new javax.swing.GroupLayout(MusicLabelPanel);
        MusicLabelPanel.setLayout(MusicLabelPanelLayout);
        MusicLabelPanelLayout.setHorizontalGroup(
            MusicLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MusicLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addMusicButton)
                .addContainerGap())
        );
        MusicLabelPanelLayout.setVerticalGroup(
            MusicLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MusicLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MusicLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addMusicButton)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        LeftMainPanel.add(MusicLabelPanel);

        MusicPanel.setBackground(new java.awt.Color(44, 44, 44));
        MusicPanel.setName(""); // NOI18N
        MusicPanel.setLayout(new javax.swing.BoxLayout(MusicPanel, javax.swing.BoxLayout.Y_AXIS));
        LeftMainPanel.add(MusicPanel);

        EnvironmentLabelPanel.setBackground(new java.awt.Color(44, 44, 44));

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Game Environment");

        addEnvironmentButton.setBackground(new java.awt.Color(79, 78, 78));
        addEnvironmentButton.setForeground(new java.awt.Color(255, 255, 255));
        addEnvironmentButton.setText("+");
        addEnvironmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEnvironmentButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout EnvironmentLabelPanelLayout = new javax.swing.GroupLayout(EnvironmentLabelPanel);
        EnvironmentLabelPanel.setLayout(EnvironmentLabelPanelLayout);
        EnvironmentLabelPanelLayout.setHorizontalGroup(
            EnvironmentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnvironmentLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addEnvironmentButton)
                .addContainerGap())
        );
        EnvironmentLabelPanelLayout.setVerticalGroup(
            EnvironmentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(EnvironmentLabelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(EnvironmentLabelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(EnvironmentLabelPanelLayout.createSequentialGroup()
                        .addComponent(addEnvironmentButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, EnvironmentLabelPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );

        LeftMainPanel.add(EnvironmentLabelPanel);

        EnvironmentPanel.setBackground(new java.awt.Color(44, 44, 44));
        EnvironmentPanel.setLayout(new javax.swing.BoxLayout(EnvironmentPanel, javax.swing.BoxLayout.Y_AXIS));
        LeftMainPanel.add(EnvironmentPanel);

        jPanel1.add(LeftMainPanel, java.awt.BorderLayout.LINE_START);

        RightMainPanel.setBackground(new java.awt.Color(204, 255, 255));
        RightMainPanel.setPreferredSize(new java.awt.Dimension(439, 1108));
        RightMainPanel.setLayout(new javax.swing.BoxLayout(RightMainPanel, javax.swing.BoxLayout.Y_AXIS));

        panel9.setBackground(new java.awt.Color(44, 44, 44));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Properties");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Name");

        NameField.setBackground(new java.awt.Color(217, 217, 217));
        NameField.setForeground(new java.awt.Color(44, 44, 44));
        NameField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        NameField.setPreferredSize(new java.awt.Dimension(321, 21));
        NameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NameFieldActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Type");

        jComboBox2.setBackground(new java.awt.Color(217, 217, 217));
        jComboBox2.setForeground(new java.awt.Color(44, 44, 44));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Player", "Enemy", "Supporting Character", "Leader", "Others" }));
        jComboBox2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jComboBox2.setRequestFocusEnabled(false);

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Health");

        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Damage");

        jSlider1.setBackground(new java.awt.Color(255, 255, 255));
        jSlider1.setForeground(new java.awt.Color(52, 199, 89));
        jSlider1.setCursor(new java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR));

        jSlider2.setBackground(new java.awt.Color(255, 255, 255));
        jSlider2.setForeground(new java.awt.Color(204, 80, 80));
        jSlider2.setCursor(new java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR));
        jSlider2.setRequestFocusEnabled(false);

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Speed");

        jSlider3.setBackground(new java.awt.Color(255, 255, 255));
        jSlider3.setForeground(new java.awt.Color(204, 255, 255));
        jSlider3.setCursor(new java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR));

        jSeparator4.setBackground(new java.awt.Color(255, 255, 255));
        jSeparator4.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panel9Layout = new javax.swing.GroupLayout(panel9);
        panel9.setLayout(panel9Layout);
        panel9Layout.setHorizontalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel9Layout.createSequentialGroup()
                .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)))
                    .addGroup(panel9Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(NameField, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8)
                            .addComponent(jSlider2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSlider3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel9Layout.setVerticalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        RightMainPanel.add(panel9);

        panel10.setBackground(new java.awt.Color(44, 44, 44));

        jButton2.setBackground(new java.awt.Color(217, 217, 217));
        jButton2.setForeground(new java.awt.Color(44, 44, 44));
        jButton2.setText("Choose File");
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.setPreferredSize(new java.awt.Dimension(91, 25));

        jButton3.setBackground(new java.awt.Color(179, 156, 208));
        jButton3.setForeground(new java.awt.Color(79, 78, 78));
        jButton3.setText("Save");
        jButton3.setBorderPainted(false);
        jButton3.setFocusPainted(false);
        jButton3.setFocusable(false);
        jButton3.setPreferredSize(new java.awt.Dimension(72, 25));

        jButton4.setBackground(new java.awt.Color(228, 228, 228));
        jButton4.setForeground(new java.awt.Color(79, 78, 78));
        jButton4.setText("Reset ");
        jButton4.setBorderPainted(false);
        jButton4.setFocusPainted(false);
        jButton4.setFocusable(false);
        jButton4.setPreferredSize(new java.awt.Dimension(72, 25));

        jButton5.setBackground(new java.awt.Color(44, 44, 44));
        jButton5.setForeground(new java.awt.Color(255, 102, 102));
        jButton5.setText("Delete Object");
        jButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 102, 102), 1, true));
        jButton5.setBorderPainted(false);
        jButton5.setFocusPainted(false);
        jButton5.setFocusable(false);
        jButton5.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jSeparator5.setForeground(new java.awt.Color(255, 255, 255));

        jButton6.setBackground(new java.awt.Color(79, 78, 78));
        jButton6.setForeground(new java.awt.Color(255, 102, 102));
        jButton6.setText(" Delete History");
        jButton6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 102)));
        jButton6.setBorderPainted(false);
        jButton6.setFocusPainted(false);
        jButton6.setFocusable(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("History");

        undoButton.setBackground(new java.awt.Color(79, 78, 78));
        undoButton.setForeground(new java.awt.Color(255, 255, 255));
        undoButton.setText("Undo");
        undoButton.setBorderPainted(false);
        undoButton.setFocusPainted(false);
        undoButton.setFocusable(false);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        redoButton.setBackground(new java.awt.Color(79, 78, 78));
        redoButton.setForeground(new java.awt.Color(255, 255, 255));
        redoButton.setText("Redo");
        redoButton.setBorderPainted(false);
        redoButton.setFocusPainted(false);
        redoButton.setFocusable(false);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel10Layout = new javax.swing.GroupLayout(panel10);
        panel10.setLayout(panel10Layout);
        panel10Layout.setHorizontalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel10Layout.createSequentialGroup()
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panel10Layout.createSequentialGroup()
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel10Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel10Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(undoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(redoButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel10Layout.setVerticalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(redoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(undoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        RightMainPanel.add(panel10);

        lastActionsPanel.setBackground(new java.awt.Color(44, 44, 44));
        lastActionsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Last Actions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 493, 367);
        lastActionsPanel.add(jLabel11, gridBagConstraints);

        RightMainPanel.add(lastActionsPanel);

        jPanel1.add(RightMainPanel, java.awt.BorderLayout.LINE_END);

        panel3.setBackground(new java.awt.Color(79, 78, 78));

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1322, Short.MAX_VALUE)
        );

        jPanel1.add(panel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenuBar1.setBackground(new java.awt.Color(102, 102, 102));
        jMenuBar1.setBorder(null);
        jMenuBar1.setForeground(new java.awt.Color(255, 255, 255));

        jMenu1.setBackground(new java.awt.Color(44, 44, 44));
        jMenu1.setForeground(new java.awt.Color(44, 44, 44));
        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setBackground(new java.awt.Color(44, 44, 44));
        jMenu2.setForeground(new java.awt.Color(44, 44, 44));
        jMenu2.setText("Import");
        jMenuBar1.add(jMenu2);

        jMenu3.setBackground(new java.awt.Color(44, 44, 44));
        jMenu3.setForeground(new java.awt.Color(44, 44, 44));
        jMenu3.setText("Export");
        jMenuBar1.add(jMenu3);

        jMenu6.setBackground(new java.awt.Color(44, 44, 44));
        jMenu6.setForeground(new java.awt.Color(44, 44, 44));
        jMenu6.setText("Help");
        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        deleteSelectedAsset();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    sortAssets(AssetType.OBJECT);
    sortAssets(AssetType.MUSIC);
    sortAssets(AssetType.ENVIRONMENT);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        // TODO add your handling code here:
        undo();
        ObjectsPanel.revalidate();
        ObjectsPanel.repaint();
        lastActionsPanel.revalidate();
        lastActionsPanel.repaint();

    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        // TODO add your handling code here:
        redo();
        ObjectsPanel.revalidate();
        ObjectsPanel.repaint();
        lastActionsPanel.revalidate();
        lastActionsPanel.repaint();
    }//GEN-LAST:event_redoButtonActionPerformed

    private void NameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NameFieldActionPerformed
        // TODO add your handling code here:
        renameSelectedAsset();
    }//GEN-LAST:event_NameFieldActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        deleteHistory();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void addMusicButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMusicButtonActionPerformed
        // TODO add your handling code here:
        addAsset(AssetType.MUSIC, MusicPanel);
    }//GEN-LAST:event_addMusicButtonActionPerformed

    private void AddObjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddObjectButtonActionPerformed
        // TODO add your handling code here:
        addAsset(AssetType.OBJECT, ObjectsPanel);
    }//GEN-LAST:event_AddObjectButtonActionPerformed

    private void addEnvironmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEnvironmentButtonActionPerformed
        // TODO add your handling code here:
        addAsset(AssetType.ENVIRONMENT, EnvironmentPanel);
    }//GEN-LAST:event_addEnvironmentButtonActionPerformed

    private void plusObjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusObjectButtonActionPerformed
        // TODO add your handling code here:
        addAsset(AssetType.OBJECT, ObjectsPanel);
    }//GEN-LAST:event_plusObjectButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
            String query = searchField.getText().trim();

    if (query.isEmpty()) {
        // Show all assets if search is empty
        sortAssets(AssetType.OBJECT);
        sortAssets(AssetType.MUSIC);
        sortAssets(AssetType.ENVIRONMENT);
    } else {
        // Show only searched items
        searchAssets(query);
    }
    }//GEN-LAST:event_searchFieldActionPerformed
private void searchAssets(String query) {
    String lowerQuery = query.toLowerCase();

    JPanel[] panels = {ObjectsPanel, MusicPanel, EnvironmentPanel};
    java.util.List<AssetInfo>[] lists = new java.util.List[]{objectAssets, musicAssets, environmentAssets};

    for (int i = 0; i < panels.length; i++) {
        JPanel panel = panels[i];
        java.util.List<AssetInfo> list = lists[i];

        panel.removeAll(); // remove all buttons first

        for (AssetInfo info : list) {
            if (info.getName().toLowerCase().contains(lowerQuery)) { // match search
                panel.add(info.button);
                panel.add(Box.createVerticalStrut(6));
            }
        }

        panel.revalidate();
        panel.repaint();
    }
}


    
    private void loadProjectData() {
    projectDataController.load();

    // Example: show project name somewhere
    jLabel4.setText("Properties - " + project.getName());

    // If you later add lists/tables, load data here
}
    
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new IndividualProjectFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddObjectButton;
    private javax.swing.JPanel EnvironmentLabelPanel;
    private javax.swing.JPanel EnvironmentPanel;
    private java.awt.Panel LeftMainPanel;
    private javax.swing.JPanel MusicLabelPanel;
    private javax.swing.JPanel MusicPanel;
    private javax.swing.JTextField NameField;
    private javax.swing.JPanel ObjectsPanel;
    private java.awt.Panel RightMainPanel;
    private javax.swing.JButton addEnvironmentButton;
    private javax.swing.JButton addMusicButton;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private java.awt.Label label1;
    private javax.swing.JPanel lastActionsPanel;
    private java.awt.Panel panel10;
    private java.awt.Panel panel3;
    private java.awt.Panel panel9;
    private javax.swing.JButton plusObjectButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JTextField searchField;
    private java.awt.TextField textField1;
    private java.awt.TextField textField2;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables
}
