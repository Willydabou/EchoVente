package com.venteSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("serial")
public class Vendre extends JPanel {

    // Colonne gauche : tableau des produits sélectionnés
    private JTable selectedProductsTable;
    private DefaultTableModel selectedProductsModel;

    // Colonne droite : affichage des produits en mode "card"
    private JPanel cardsPanel;
    private JScrollPane cardsScrollPane;

    // Barre de recherche de la colonne droite
    private JTextField txtSearch;
    private JButton btnSearch;

    // Boutons de navigation
    private JButton btnPrevious;
    private JButton btnNext;

    // Liste complète des produits récupérés en base
    private List<Produit> allProducts;
    private int currentPage = 0;
    private final int pageSize = 20; // Nombre d'éléments par page
    private  double totalGlobal = 0.0;
    		
    public Vendre() {
    	

        try {
            initData(); // Chargement des produits depuis la base de données
        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, vous pouvez générer des données d'exemple ou afficher un message
            allProducts = new ArrayList<>();
        }
        initComponents();
    }

    /**
     * Charge la liste des produits depuis la base de données.
     */
    private void initData() throws ClassNotFoundException, SQLException {
        allProducts = new ArrayList<>();
        String query = "SELECT * FROM Produit";
        Connection con = DbConnection.getConnection();
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String categorie = rs.getString("categorie");
                int stock = rs.getInt("quantite");
                allProducts.add(new Produit(nom, prix, categorie, stock));
            }
        } finally {
            con.close();
        }
    }
    

    /**
     * Initialise et dispose les composants de l'interface.
     */
    private void initComponents() {

     // Utilisation d'un JSplitPane pour diviser le panel en deux colonnes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Ajoute le JSplitPane au JPanel courant (this)
        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        // -----------------------
        // Colonne gauche : Produits sélectionnés
        // -----------------------
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.add(new JLabel("Produits sélectionnés"), BorderLayout.NORTH);

        selectedProductsModel = new DefaultTableModel(new Object[]{"Nom produit", "Prix", "Quantité à acheter", "Somme Prix", "Supprimer"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 4; // Quantité et bouton supprimer
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return JButton.class;
                return super.getColumnClass(columnIndex);
            }
        };


        selectedProductsTable = new JTable(selectedProductsModel);

        // Écouteur pour mettre à jour la colonne "Somme Prix" lorsqu'on modifie la quantité
     // Ce JLabel affichera le total final (placé sous le tableau)
        JLabel totalLabel = new JLabel("Total: 0.00 Ar");
        JLabel rowCountLabel = new JLabel("Articles: 0");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(new Color(0, 102, 153)); // Bleu foncé
        leftPanel.add(totalLabel, BorderLayout.SOUTH);

       
     // Mettre à jour la "Somme Prix" quand la quantité est modifiée
        selectedProductsTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 2) { // colonne "Quantité"
                try {
                    Object quantityObj = selectedProductsModel.getValueAt(row, 2);
                    Object priceObj = selectedProductsModel.getValueAt(row, 1);

                    if (quantityObj != null && priceObj != null) {
                        String quantityStr = quantityObj.toString().trim();
                        String priceStr = priceObj.toString().trim();

                        if (!quantityStr.isEmpty() && !priceStr.isEmpty()) {
                            int quantity = Integer.parseInt(quantityStr);
                            double price = Double.parseDouble(priceStr);
                            double total = price * quantity;

                            // Mettre à jour la cellule Somme Prix
                            selectedProductsModel.setValueAt(String.format(Locale.US, "%.2f", total), row, 3);

                            // Lancer le recalcul total APRÈS que Swing ait fini la mise à jour
                            SwingUtilities.invokeLater(() -> {
                               
                            	double totalSomme = 0;
                            	for (int i = 0; i < selectedProductsModel.getRowCount(); i++) {
                            	    Object sommeObj = selectedProductsModel.getValueAt(i, 3);
                            	    if (sommeObj != null) {
                            	        try {
                            	        	totalSomme += Double.parseDouble(sommeObj.toString());
                            	        } catch (NumberFormatException ignored) {}
                            	    }
                            	}
                            	totalLabel.setText("Total: " + String.format("%.2f", totalSomme) + " Ar");

                            });
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Veuillez entrer un nombre valide pour la quantité.",
                            "Erreur de saisie",
                            JOptionPane.ERROR_MESSAGE);
                    selectedProductsModel.setValueAt(1, row, 2); // Remettre à 1
                }
            }
            totalLabel.setText("Total: " + String.format("%.2f", totalGlobal) + " Ar");
        });
        totalLabel.setText("Total: " + String.format("%.2f", totalGlobal) + " Ar");
//        JButton btnCalculerTotal = new JButton("Recalculer Total");
//        btnCalculerTotal.addActionListener(e -> {
//            double total = 0;
//            for (int i = 0; i < selectedProductsModel.getRowCount(); i++) {
//                Object sommeObj = selectedProductsModel.getValueAt(i, 3);
//                if (sommeObj != null) {
//                    try {
//                        total += Double.parseDouble(sommeObj.toString());
//                    } catch (NumberFormatException ignored) {}
//                }
//            }
//            totalLabel.setText("Total: " + String.format("%.2f", total) + " Ar");
//        });

        selectedProductsTable.getColumn("Supprimer").setCellRenderer(new ButtonRenderer());
        selectedProductsTable.getColumn("Supprimer").setCellEditor(new ButtonEditor(new JCheckBox(), selectedProductsModel, selectedProductsTable, totalLabel, rowCountLabel));
        // Ajout du tableau dans un JScrollPane pour le défilement
        JScrollPane leftScrollPane = new JScrollPane(selectedProductsTable);
        leftPanel.add(leftScrollPane, BorderLayout.CENTER);
        
     // Panneau pour le bouton d'enregistrement
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEnregistrer = new JButton("Enregistrer");
        

        // Action du bouton
        btnEnregistrer.addActionListener(e -> {
			try {
				enregistrerVente();
				initData();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        
        bottomPanel.add(btnEnregistrer);
        bottomPanel.add(totalLabel);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);



        splitPane.setLeftComponent(leftPanel);

        // -----------------------
        // Colonne droite : Barre de recherche et affichage en "card" des produits
        // -----------------------
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Barre de recherche en haut
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtSearch = new JTextField();
        btnSearch = new JButton("Rechercher");
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Panneau central pour afficher les "cards"
        cardsPanel = new JPanel(new GridLayout(4, 5, 20, 20)); // 5 colonnes de cards
        cardsScrollPane = new JScrollPane(cardsPanel);
        rightPanel.add(cardsScrollPane, BorderLayout.CENTER);

        // Panneau de navigation en bas
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrevious = new JButton("Précédent");
        btnNext = new JButton("Suivant");
        paginationPanel.add(btnPrevious);
        paginationPanel.add(btnNext);
        rightPanel.add(paginationPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);

        // Chargement de la première page de produits
        loadProductPage();

        // -----------------------
        // Actions
        // -----------------------

        // Recherche : filtre simple sur le nom du produit
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProducts();
            }
        });

        // Navigation
        btnPrevious.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 0) {
                    currentPage--;
                    loadProductPage();
                }
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((currentPage + 1) * pageSize < allProducts.size()) {
                    currentPage++;
                    loadProductPage();
                }
            }
        });
    }
    
 // Méthode pour ajouter un produit sélectionné au tableau de gauche
    public void ajouterProduitSelectionne(String nom, double prix) {
        int defaultQuantity = 1;
        double sommePrix = prix * defaultQuantity;

        // Ajouter le produit au tableau avec une quantité par défaut de 1
        selectedProductsModel.addRow(new Object[]{nom, prix, defaultQuantity, sommePrix});
    }


    private void enregistrerVente() throws ClassNotFoundException {
        try (Connection con = DbConnection.getConnection()) {
            // Déclaration de la requête d'insertion pour les ventes
            String venteQuery = "INSERT INTO Vente (quantiteAcheter, idProduit, sommePrix) VALUES (?, ?, ?)";
            
            // Déclaration de la requête de mise à jour du stock
            String updateStockQuery = "UPDATE Produit SET quantite = quantite - ? WHERE idProduit = ?";

            // Préparer la requête d'insertion pour les ventes
            try (PreparedStatement stmtVente = con.prepareStatement(venteQuery);
                 PreparedStatement stmtStock = con.prepareStatement(updateStockQuery)) {
                
                // Parcours du tableau des produits sélectionnés
                for (int i = 0; i < selectedProductsModel.getRowCount(); i++) {
                    // Récupération des informations du produit depuis le tableau
                    String nomProduit = selectedProductsModel.getValueAt(i, 0).toString(); // Nom du produit
                    int quantiteAchetee = Integer.parseInt(selectedProductsModel.getValueAt(i, 2).toString()); // Quantité achetée
                    double sommePrix = Double.parseDouble(selectedProductsModel.getValueAt(i, 3).toString()); // Somme prix

                    // Récupérer l'ID du produit et la quantité en stock
                    int produitId = getProduitId(nomProduit, con);
                    int stockDisponible = getStockDisponible(produitId, con); // Récupérer la quantité en stock
                    
                    // Vérifier si la quantité achetée dépasse la quantité en stock
                    if (quantiteAchetee > stockDisponible) {
                        // Afficher un message d'erreur et arrêter l'enregistrement de la vente
                        JOptionPane.showMessageDialog(null, 
                                "Stock insuffisant pour " + nomProduit + ". Il n'en reste que " + stockDisponible + " en stock.", 
                                "Erreur de stock", JOptionPane.ERROR_MESSAGE);
                        return; // Arrêter l'enregistrement de la vente
                    }

                    // Si le produit existe et qu'il y a assez de stock, procéder à l'insertion
                    if (produitId != -1) {
                        // Insérer la vente dans la table Vente
                        stmtVente.setInt(1, quantiteAchetee); // Quantité achetée
                        stmtVente.setInt(2, produitId); // ID du produit
                        stmtVente.setDouble(3, sommePrix); // Somme du prix

                        stmtVente.executeUpdate(); // Exécuter la requête d'insertion pour la vente

                        // Mettre à jour le stock dans la table Produit
                        stmtStock.setInt(1, quantiteAchetee); // Quantité vendue
                        stmtStock.setInt(2, produitId); // ID du produit
                        stmtStock.executeUpdate(); // Exécuter la requête de mise à jour du stock
                    }
                }
                initData();
                loadProductPage();

                // Message de succès après insertion
                JOptionPane.showMessageDialog(null, "Vente enregistrée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Vider le tableau des produits sélectionnés après enregistrement
                selectedProductsModel.setRowCount(0); 
            }

        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour obtenir la quantité en stock d'un produit
    private int getStockDisponible(int produitId, Connection con) throws SQLException {
        String query = "SELECT quantite FROM Produit WHERE idProduit = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantite");
            }
        }
        return 0; // Si produit non trouvé, retourner 0
    }


		    private int getProduitId(String nomProduit, Connection con) throws SQLException {
		        String query = "SELECT idProduit FROM Produit WHERE nom = ?";
		        try (PreparedStatement stmt = con.prepareStatement(query)) {
		            stmt.setString(1, nomProduit);
		            ResultSet rs = stmt.executeQuery();
		            if (rs.next()) {
		                return rs.getInt("idProduit");
		            }
		        }
		        return -1; // Retourne -1 si le produit n'est pas trouvé
		    }

    /**
     * Charge les produits de la page courante et les affiche sous forme de cartes.
     */
    private void loadProductPage() {
        cardsPanel.removeAll(); // Efface les cartes existantes
        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allProducts.size());
        for (int i = start; i < end; i++) {
            Produit p = allProducts.get(i);
            JPanel card = createProductCard(p);
            cardsPanel.add(card);
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Crée une carte pour un produit.
     *
     * @param p Le produit à afficher.
     * @return Un JPanel représentant la carte du produit.
     */
    private JPanel createProductCard(Produit p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(150, 100));

        // Informations du produit
        JLabel lblName = new JLabel(p.getNom());
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblPrice = new JLabel("Prix : " + p.getPrix());
       
        JLabel lblStock = new JLabel("Stock : " + p.getStock());

        // Disposition verticale des informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(lblName);
        infoPanel.add(lblPrice);
        infoPanel.add(lblStock);

        card.add(infoPanel, BorderLayout.CENTER);

        // Rendre la carte cliquable
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Si le produit n'est pas déjà dans le tableau de la colonne gauche, on l'ajoute avec une quantité par défaut de 1
                if (!isProductSelected(p.getNom())) {
                   // selectedProductsModel.addRow(new Object[]{p.getNom(), p.getPrix(), 1});
                    ajouterProduitSelectionne(p.getNom(), p.getPrix());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    /**
     * Vérifie si un produit est déjà présent dans le tableau des produits sélectionnés.
     *
     * @param nom Le nom du produit.
     * @return true si le produit est déjà sélectionné, false sinon.
     */
    private boolean isProductSelected(String nom) {
        for (int i = 0; i < selectedProductsModel.getRowCount(); i++) {
            if (selectedProductsModel.getValueAt(i, 0).equals(nom)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filtre les produits en fonction du texte saisi dans la barre de recherche.
     */
    private void searchProducts() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        cardsPanel.removeAll();
        for (Produit p : allProducts) {
            if (p.getNom().toLowerCase().contains(keyword)) {
                JPanel card = createProductCard(p);
                cardsPanel.add(card);
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Classe Produit utilisée pour représenter un produit.
     */
    public static class Produit {
        private String nom;
        private double prix;
        private String categorie;
        private int stock;

        public Produit(String nom, double prix, String categorie, int stock) {
            this.nom = nom;
            this.prix = prix;
            this.categorie = categorie;
            this.stock = stock;
        }

        public String getNom() {
            return nom;
        }

        public double getPrix() {
            return prix;
        }

        public String getCategorie() {
            return categorie;
        }

        public int getStock() {
            return stock;
        }
    }

    public static void initialize() {
        SwingUtilities.invokeLater(() -> {
            Vendre ui = new Vendre();
            ui.setVisible(true);
        });
    }
    
    
}
