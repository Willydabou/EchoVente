package com.venteSwing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

@SuppressWarnings("serial")
public class AjouterProduitPanel extends JPanel {

    private JTextField nomField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JComboBox<String> categorieComboBox;
    private JButton ajouterButton;
    private JLabel messageLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    public AjouterProduitPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245)); // Gris clair

        // === Panel Formulaire ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Titre ===
        JLabel title = new JLabel("Ajouter un Produit");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(65, 105, 225)); // Bleu roi
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // === Nom ===
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Nom du produit :"), gbc);
        gbc.gridx = 1;
        nomField = createTextField();
        formPanel.add(nomField, gbc);

        // === Prix ===
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Prix :"), gbc);
        gbc.gridx = 1;
        prixField = createTextField();
        formPanel.add(prixField, gbc);

        // === Quantité ===
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Quantité :"), gbc);
        gbc.gridx = 1;
        quantiteField = createTextField();
        formPanel.add(quantiteField, gbc);

        // === Catégorie ===
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(createLabel("Catégorie :"), gbc);
        gbc.gridx = 1;
        categorieComboBox = new JComboBox<>();
        categorieComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categorieComboBox.setPreferredSize(new Dimension(200, 25));
        categorieComboBox.setBackground(Color.WHITE);
        formPanel.add(categorieComboBox, gbc);
        loadCategories();

        // === Bouton Ajouter ===
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        ajouterButton = new JButton("Ajouter");
        ajouterButton.setFocusPainted(false);
        ajouterButton.setBackground(new Color(100, 149, 237)); // Bleu clair
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ajouterButton.setPreferredSize(new Dimension(150, 35));
        formPanel.add(ajouterButton, gbc);

        // === Message ===
        gbc.gridy = 6;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.NORTH);

     // === Table des produits ===
        tableModel = new DefaultTableModel(new Object[]{"Nom", "Prix", "Quantité"}, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE); // Gris clair / blanc
                } else {
                    c.setBackground(new Color(184, 207, 229)); // Bleu sélection
                }
                return c;
            }
        };

        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(100, 149, 237)); // Bleu clair
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(40);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(500, 200));
        add(tableScroll, BorderLayout.CENTER);

        // === Charger les produits existants ===
        loadProduits();

        // === Action bouton ajouter ===
        ajouterButton.addActionListener(this::ajouterProduitAction);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(new Color(51, 51, 51)); // Gris foncé
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return field;
    }

    private void ajouterProduitAction(ActionEvent e) {
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String quantiteStr = quantiteField.getText().trim();
        String categorie = (String) categorieComboBox.getSelectedItem();

        if (nom.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty() || categorie == null) {
            showMessage("Veuillez remplir tous les champs.", Color.RED);
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            int quantite = Integer.parseInt(quantiteStr);

            ProduitAction action = new ProduitActionImplementation();

            // Vérifie si le produit existe déjà
            if (action.produitExiste(nom)) {
                showMessage("Le produit existe déjà !", Color.RED);
                return;
            }

            // Ajouter le produit si non existant
            action.ajouterProduit(nom, prix, quantite, categorie);

            tableModel.addRow(new Object[]{nom, prix, quantite});
            showMessage("Produit ajouté avec succès !", new Color(0, 128, 0)); // Vert
            clearFields();

        } catch (NumberFormatException ex) {
            showMessage("Prix ou quantité invalide !", Color.RED);
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            showMessage("Erreur lors de l'ajout du produit.", Color.RED);
        }
    }


    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    private void clearFields() {
        nomField.setText("");
        prixField.setText("");
        quantiteField.setText("");
        if (categorieComboBox.getItemCount() > 0) {
            categorieComboBox.setSelectedIndex(0);
        }
    }

    private void loadCategories() {
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT nom FROM Categorie");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorieComboBox.addItem(rs.getString("nom"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement des catégories.", Color.RED);
        }
    }

    private void loadProduits() {
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement("SELECT nom, prix, quantite FROM Produit");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement des produits.", Color.RED);
        }
    }
}
