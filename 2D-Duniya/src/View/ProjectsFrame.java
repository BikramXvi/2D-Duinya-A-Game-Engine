/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.*;
import Model.*;
import View.ProjectsFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.nio.file.ProviderMismatchException;
import java.util.List;
/**
 * ProjectsFrame is a GUI window for managing projects, including viewing, creating, favouriting, and deleting projects.
 * Also handles trash functionality.
 */
public class ProjectsFrame extends javax.swing.JFrame {
    
 private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProjectsFrame.class.getName());

    // Projects controller for managing projects
    private ProjectsController projectController;
    // Current user controller
    private CurrentUserController userController;

    // Popup menu for project actions
    private JPopupMenu projectPopup;
    // Menu item for renaming project
    private JMenuItem renameItem;
    // Menu item for deleting project
    private JMenuItem deleteItem;
    // For Favourites


    // For Trash with timestamp
//    private static class TrashItem {
//        Project project;
//        long deletedTime; // in milliseconds
//        TrashItem(Project p) { this.project = p; this.deletedTime = System.currentTimeMillis(); }
//    }
    
    /**
     * Refreshes the projects table with current projects.
     */
    private void refreshProjectsTable() {
    DefaultTableModel model = (DefaultTableModel) projectsTable.getModel();
    model.setRowCount(0);
    for (Project p : projectController.getProjects()) {
        model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
    }
}

private void refreshFavouritesTable() {
    DefaultTableModel model = (DefaultTableModel) favouritesTable.getModel();
    model.setRowCount(0);
    for (Project p : projectController.getFavourites()) {
        model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
    }
}

    /**
     * Refreshes the trash table with current trash items.
     */
    private void refreshTrashTable() {
    DefaultTableModel model = (DefaultTableModel) trashTable.getModel();
    model.setRowCount(0);

    // get the trash list from the controller
    java.util.List<ProjectsController.TrashItem> trash = projectController.getTrash();

    for (ProjectsController.TrashItem item : trash) {
        Project p = item.project; // access the actual project inside TrashItem
        model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
    }
}



    /**
     * Constructs a ProjectsFrame with the given controllers.
     * Initializes the GUI components and loads data.
     * @param userCtrl the current user controller
     * @param projCtrl the projects controller
     */
    public ProjectsFrame(CurrentUserController userCtrl, ProjectsController projCtrl) {
               this.userController = userCtrl;
        this.projectController = projCtrl;

        initComponents();

        projectController.loadFromFile();
        projectController.loadFavourites();
        projectController.loadTrash();
        refreshProjectsTable();
        refreshFavouritesTable();
        refreshTrashTable();

        loadTable();
        setupPopupMenu();
        setupFavouritePopup();  
        attachTableMouseListener();
        attachFavouriteTableMouseListener();
    }
    /**
     * Sets up the popup menu for project actions.
     */
    private void setupPopupMenu() {
        projectPopup = new JPopupMenu();
        renameItem = new JMenuItem("Rename");
        deleteItem = new JMenuItem("Delete");
        JMenuItem favItem = new JMenuItem("Add to Favourites");
         projectPopup.add(favItem);

        projectPopup.add(renameItem);
        projectPopup.add(deleteItem);

        renameItem.addActionListener(e -> renameSelectedProject());
        deleteItem.addActionListener(e -> deleteSelectedProject());
        favItem.addActionListener(e -> addToFavourites());
    }
    
    
    /**
     * Adds the selected project to favourites.
     */
    private void addToFavourites() {
    int row = projectsTable.getSelectedRow();
    if (row >= 0) {
        Project selected = projectController.getProjects().get(row);
        projectController.addToFavourites(selected);
        refreshFavouritesTable();
        JOptionPane.showMessageDialog(this, "Added to Favourites");
    } else {
        JOptionPane.showMessageDialog(this, "Select a project first");
    }
}


    private void attachTableMouseListener() {
    projectsTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            handlePopup(e);
            handleDoubleClick(e);
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            handlePopup(e);
        }

        private void handlePopup(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                int row = projectsTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    projectsTable.setRowSelectionInterval(row, row);
                    projectPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

    /**
     * Handles double-click on the projects table to open the selected project.
     * @param e the mouse event
     */
    private void handleDoubleClick(java.awt.event.MouseEvent e) {
    if (e.getClickCount() == 2 && !e.isConsumed()) {
        e.consume(); // consume so it doesn't trigger other events
        int row = projectsTable.rowAtPoint(e.getPoint());
        if (row != -1) {
            Project selectedProject = projectController.getProjects().get(row);
            IndividualProjectFrame frame = new IndividualProjectFrame(selectedProject);
            frame.setVisible(true);
        }
    }
}
    });
    }
    
    /**
     * Attaches mouse listener to the favourites table for popup and double-click handling.
     */
    private void attachFavouriteTableMouseListener() {
    favouritesTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            handlePopup(e);
            handleDoubleClick(e);
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            handlePopup(e);
        }

        private void handlePopup(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                int row = favouritesTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    favouritesTable.setRowSelectionInterval(row, row);
                    favouritePopup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        private void handleDoubleClick(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2 && !e.isConsumed()) {
                e.consume();
                int row = favouritesTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    Project selectedProject =
                        projectController.getFavourites().get(row);
                    IndividualProjectFrame frame =
                        new IndividualProjectFrame(selectedProject);
                    frame.setVisible(true);
                }
            }
        }
    });
}

    
    /**
     * Removes the selected project from favourites.
     */
    private void removeFromFavourites() {
    int row = favouritesTable.getSelectedRow();
    if (row == -1) return;

    Project selected =
        projectController.getFavourites().get(row);

    projectController.removeFromFavourites(selected);
    refreshFavouritesTable();

    JOptionPane.showMessageDialog(this, "Removed from Favourites");
}
    
    // Popup menu for favourites
    private JPopupMenu favouritePopup;
    // Menu item to remove from favourites
    private JMenuItem removeFavouriteItem;

    /**
     * Sets up the popup menu for favourites table actions.
     */
    private void setupFavouritePopup() {
    favouritePopup = new JPopupMenu();

    removeFavouriteItem = new JMenuItem("Remove from Favourites");

    favouritePopup.add(removeFavouriteItem);

    removeFavouriteItem.addActionListener(e -> removeFromFavourites());
}


    private void loadTable() {
        DefaultTableModel model = (DefaultTableModel) projectsTable.getModel();
        model.setRowCount(0);
        for (Project p : projectController.getProjects()) {
            model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
        }
    }

    private void createProject() {
        String name = JOptionPane.showInputDialog(this, "Enter Project Name");
        if (name == null || name.trim().isEmpty()) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Project p = projectController.createProject(name, chooser.getSelectedFile());
        DefaultTableModel model = (DefaultTableModel) projectsTable.getModel();
        model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
    }
    
    
    private void renameSelectedProject() {
        int row = projectsTable.getSelectedRow();
        if (row == -1) return;

        String newName = JOptionPane.showInputDialog(this, "Enter new project name");
        if (newName == null || newName.trim().isEmpty()) return;

        projectController.renameProject(row, newName);
        loadTable();
    }

    
private void deleteSelectedProject() {
    int row = projectsTable.getSelectedRow();
    if (row == -1) return;

    int confirm = JOptionPane.showConfirmDialog(this, "Delete selected project?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        Project selected = projectController.getProjects().remove(row);

        // Remove from favourites if present
        projectController.removeFromFavourites(selected);

        // Add to trash
        projectController.addToTrash(selected);

        // Save projects
        projectController.saveToFile();

        // Refresh all tables
        refreshProjectsTable();
        refreshFavouritesTable();
        refreshTrashTable();

        JOptionPane.showMessageDialog(this, "Project deleted and moved to Trash");
    }}
    
    /**
     * Applies sorting and searching to the projects table.
     */
    private void applySortAndSearchProjects() {
    String query = projectSearch.getText().trim().toLowerCase();
    
    //  Sort first
    String sortBy = (String) projectSort.getSelectedItem();
    if ("Name".equals(sortBy)) {
        projectController.sortByName();
    } else if ("Date Created".equals(sortBy)) {
        projectController.sortByDateCreated();
    }

    // Filter
    DefaultTableModel model = (DefaultTableModel) projectsTable.getModel();
    model.setRowCount(0);

    for (Project p : projectController.getProjects()) {
        if (query.isEmpty() || p.getName().toLowerCase().contains(query)) {
            model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
        }
    }
}


    /**
     * Applies sorting and searching to the favourites table.
     */
    private void applySortAndSearchFavourites() {
    String query = favouriteSearch.getText().trim().toLowerCase();
    List<Project> favourites = projectController.getFavourites();

    // Optional: sort favourites
    String sortBy = (String) favouriteSort.getSelectedItem();
    if ("Name".equals(sortBy)) {
        favourites.sort((a,b) -> a.getName().compareToIgnoreCase(b.getName()));
    } else if ("Date Created".equals(sortBy)) {
        favourites.sort((a,b) -> Long.compare(a.getDateCreated(), b.getDateCreated()));
    }

    // Filter
    DefaultTableModel model = (DefaultTableModel) favouritesTable.getModel();
    model.setRowCount(0);
    for (Project p : favourites) {
        if (query.isEmpty() || p.getName().toLowerCase().contains(query)) {
            model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
        }
    }
}



    /**
     * Applies sorting and searching to the trash table.
     */
    private void applySortAndSearchTrash() {
    String query = trashSearch.getText().trim().toLowerCase();
    List<ProjectsController.TrashItem> trash = projectController.getTrash();

    // Sort
    String sortBy = (String) trashSort.getSelectedItem();
    if ("Name".equals(sortBy)) {
        trash.sort((a, b) -> a.project.getName().compareToIgnoreCase(b.project.getName()));
    } else if ("Deleted Date".equals(sortBy)) {
        trash.sort((a, b) -> Long.compare(a.deletedTime, b.deletedTime));
    }

    // Filter
    DefaultTableModel model = (DefaultTableModel) trashTable.getModel();
    model.setRowCount(0);
    for (ProjectsController.TrashItem item : trash) {
        Project p = item.project;
        if (query.isEmpty() || p.getName().toLowerCase().contains(query)) {
            model.addRow(new Object[]{p.getName(), "0 KB", p.getPath(), "Open"});
        }
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        panel1 = new java.awt.Panel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        createProjectButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        projectsButton = new javax.swing.JButton();
        favouritesButton = new javax.swing.JButton();
        trashButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        ProjectsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectsTable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        projectSearch = new javax.swing.JTextField();
        projectSort = new javax.swing.JComboBox<>();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        FavouritesPanel = new javax.swing.JPanel();
        panel2 = new java.awt.Panel();
        jLabel2 = new javax.swing.JLabel();
        favouriteSearch = new javax.swing.JTextField();
        favouriteSort = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        favouritesTable = new javax.swing.JTable();
        TrashPanel = new javax.swing.JPanel();
        panel3 = new java.awt.Panel();
        trashSearch = new javax.swing.JTextField();
        trashSort = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        trashTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(204, 255, 255));
        jPanel2.setLayout(new java.awt.CardLayout());

        panel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(44, 44, 44));
        jPanel3.setLayout(new java.awt.GridLayout(1, 3, 30, 30));

        jPanel1.setBackground(new java.awt.Color(44, 44, 44));
        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        jPanel6.setBackground(new java.awt.Color(44, 44, 44));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/View/2DLogo.png"))); // NOI18N
        jButton5.setText("jButton5");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel6);

        jPanel3.add(jPanel1);

        createProjectButton.setBackground(new java.awt.Color(179, 156, 208));
        createProjectButton.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        createProjectButton.setText("+ Create Project");
        createProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProjectButtonActionPerformed(evt);
            }
        });
        jPanel3.add(createProjectButton);

        jPanel5.setBackground(new java.awt.Color(44, 44, 44));
        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 5));

        jButton7.setBackground(new java.awt.Color(44, 44, 44));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/View/plus.png"))); // NOI18N
        jButton7.setBorder(null);
        jButton7.setContentAreaFilled(false);
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton7);

        jButton2.setBackground(new java.awt.Color(44, 44, 44));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/View/bell.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton2);

        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/View/profile.png"))); // NOI18N
        jButton9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jButton9.setBorderPainted(false);
        jButton9.setContentAreaFilled(false);
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel5.add(jButton9);

        jPanel3.add(jPanel5);

        panel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setBackground(new java.awt.Color(79, 78, 78));

        projectsButton.setText("Projects");
        projectsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectsButtonActionPerformed(evt);
            }
        });

        favouritesButton.setText("Favourites");
        favouritesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favouritesButtonActionPerformed(evt);
            }
        });

        trashButton.setText("Trash");
        trashButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trashButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(favouritesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(trashButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsButton)
                .addGap(6, 6, 6)
                .addComponent(favouritesButton)
                .addGap(6, 6, 6)
                .addComponent(trashButton))
        );

        panel1.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel7.setBackground(new java.awt.Color(44, 44, 44));
        jPanel7.setLayout(new java.awt.CardLayout());

        ProjectsPanel.setBackground(new java.awt.Color(44, 44, 44));

        jLabel1.setText("Projects");

        projectsTable.setBackground(new java.awt.Color(44, 44, 44));
        projectsTable.setForeground(new java.awt.Color(255, 255, 255));
        projectsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Project Name", "Project Size", "Project Directory", "Open Project"
            }
        ));
        projectsTable.setColumnSelectionAllowed(true);
        projectsTable.setGridColor(new java.awt.Color(204, 204, 204));
        projectsTable.setSelectionForeground(new java.awt.Color(44, 44, 44));
        projectsTable.setShowGrid(true);
        jScrollPane1.setViewportView(projectsTable);
        projectsTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jPanel8.setBackground(new java.awt.Color(44, 44, 44));

        projectSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSearchActionPerformed(evt);
            }
        });

        projectSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Size", "Date Created" }));
        projectSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSortActionPerformed(evt);
            }
        });

        label1.setForeground(new java.awt.Color(204, 204, 204));
        label1.setText("Sort By");

        label2.setForeground(new java.awt.Color(204, 204, 204));
        label2.setName(""); // NOI18N
        label2.setText("Search");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout ProjectsPanelLayout = new javax.swing.GroupLayout(ProjectsPanel);
        ProjectsPanel.setLayout(ProjectsPanelLayout);
        ProjectsPanelLayout.setHorizontalGroup(
            ProjectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProjectsPanelLayout.createSequentialGroup()
                .addGroup(ProjectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ProjectsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(ProjectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1643, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addGap(6, 6, 6))
        );
        ProjectsPanelLayout.setVerticalGroup(
            ProjectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProjectsPanelLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel7.add(ProjectsPanel, "card2");

        FavouritesPanel.setBackground(new java.awt.Color(44, 44, 44));

        panel2.setBackground(new java.awt.Color(44, 44, 44));
        panel2.setPreferredSize(new java.awt.Dimension(419, 32));

        jLabel2.setBackground(new java.awt.Color(44, 44, 44));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Search");

        favouriteSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favouriteSearchActionPerformed(evt);
            }
        });

        favouriteSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Size", "Date Created" }));
        favouriteSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                favouriteSortActionPerformed(evt);
            }
        });

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Sort By");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(favouriteSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(favouriteSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(favouriteSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(favouriteSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Favourite Items");

        favouritesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Project Name", "Project Size ", "Project Directory", "Open"
            }
        ));
        jScrollPane2.setViewportView(favouritesTable);

        javax.swing.GroupLayout FavouritesPanelLayout = new javax.swing.GroupLayout(FavouritesPanel);
        FavouritesPanel.setLayout(FavouritesPanelLayout);
        FavouritesPanelLayout.setHorizontalGroup(
            FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FavouritesPanelLayout.createSequentialGroup()
                .addGroup(FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FavouritesPanelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(FavouritesPanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 825, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(816, Short.MAX_VALUE))
        );
        FavouritesPanelLayout.setVerticalGroup(
            FavouritesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FavouritesPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(634, Short.MAX_VALUE))
        );

        jPanel7.add(FavouritesPanel, "card3");

        TrashPanel.setBackground(new java.awt.Color(44, 44, 44));

        panel3.setBackground(new java.awt.Color(44, 44, 44));
        panel3.setPreferredSize(new java.awt.Dimension(412, 32));

        trashSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trashSearchActionPerformed(evt);
            }
        });

        trashSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Name", "Size", "Deleted Date" }));
        trashSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trashSortActionPerformed(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Search");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Sort By");

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trashSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trashSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trashSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trashSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Deleted Items");

        trashTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Project Name", "Project Size", "Project Directry", "Open"
            }
        ));
        jScrollPane3.setViewportView(trashTable);

        javax.swing.GroupLayout TrashPanelLayout = new javax.swing.GroupLayout(TrashPanel);
        TrashPanel.setLayout(TrashPanelLayout);
        TrashPanelLayout.setHorizontalGroup(
            TrashPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TrashPanelLayout.createSequentialGroup()
                .addGroup(TrashPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TrashPanelLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(TrashPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel7))
                    .addGroup(TrashPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(868, Short.MAX_VALUE))
        );
        TrashPanelLayout.setVerticalGroup(
            TrashPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TrashPanelLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(588, Short.MAX_VALUE))
        );

        jPanel7.add(TrashPanel, "card4");

        panel1.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel2.add(panel1, "card2");

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createProjectButtonActionPerformed

      createProject(); // TODO add your handling code here:
    }//GEN-LAST:event_createProjectButtonActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
public void showPanel(JPanel panel){
    ProjectsPanel.setVisible(false);
    FavouritesPanel.setVisible(false);
    TrashPanel.setVisible(false);
    
    panel.setVisible(true);
}
    private void projectsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectsButtonActionPerformed
        showPanel(ProjectsPanel);

        // TODO add your handling code here:
    }//GEN-LAST:event_projectsButtonActionPerformed

    private void favouritesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favouritesButtonActionPerformed
       showPanel(FavouritesPanel);
        // TODO add your handling code here:
    }//GEN-LAST:event_favouritesButtonActionPerformed

    private void trashButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trashButtonActionPerformed
        
        showPanel(TrashPanel);
        // TODO add your handling code here:
    }//GEN-LAST:event_trashButtonActionPerformed

    private void projectSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectSearchActionPerformed
        // TODO add your handling code here:
        applySortAndSearchProjects();
    }//GEN-LAST:event_projectSearchActionPerformed

    private void projectSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectSortActionPerformed
        // TODO add your handling code here:
        applySortAndSearchProjects();
    }//GEN-LAST:event_projectSortActionPerformed

    private void trashSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trashSortActionPerformed
        // TODO add your handling code here:
        applySortAndSearchTrash();
    }//GEN-LAST:event_trashSortActionPerformed

    private void favouriteSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favouriteSortActionPerformed
applySortAndSearchFavourites();;        // TODO add your handling code here:
        
    }//GEN-LAST:event_favouriteSortActionPerformed

    private void trashSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trashSearchActionPerformed
        // TODO add your handling code here:
        applySortAndSearchTrash();
    }//GEN-LAST:event_trashSearchActionPerformed

    private void favouriteSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_favouriteSearchActionPerformed
        // TODO add your handling code here:
        applySortAndSearchFavourites();
    }//GEN-LAST:event_favouriteSearchActionPerformed

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        CurrentUserController userCtrl = new CurrentUserController();
        userCtrl.login("Bikram");  // "Bikram" is the username for testing

        // Create ProjectsController for this user
        ProjectsController projCtrl = new ProjectsController(userCtrl);

        // Create the ProjectsFrame with the controllers
        javax.swing.SwingUtilities.invokeLater(() -> {
            ProjectsFrame frame = new ProjectsFrame(userCtrl, projCtrl);
            frame.setVisible(true);
        });
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FavouritesPanel;
    private javax.swing.JPanel ProjectsPanel;
    private javax.swing.JPanel TrashPanel;
    private javax.swing.JButton createProjectButton;
    private javax.swing.JTextField favouriteSearch;
    private javax.swing.JComboBox<String> favouriteSort;
    private javax.swing.JButton favouritesButton;
    private javax.swing.JTable favouritesTable;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Panel panel1;
    private java.awt.Panel panel2;
    private java.awt.Panel panel3;
    private javax.swing.JTextField projectSearch;
    private javax.swing.JComboBox<String> projectSort;
    private javax.swing.JButton projectsButton;
    private javax.swing.JTable projectsTable;
    private javax.swing.JButton trashButton;
    private javax.swing.JTextField trashSearch;
    private javax.swing.JComboBox<String> trashSort;
    private javax.swing.JTable trashTable;
    // End of variables declaration//GEN-END:variables
}
