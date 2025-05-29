package com.venteSwing;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ModifierProduitPanel extends JPanel {
    private JTextField nomField, prixField, quantiteField;
    private JComboBox<String> categorieCombo;
    private JButton searchButton, updateButton;
    private int produitId = -1;

    public ModifierProduitPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245)); // fond gris clair

        // --------- Panel principal vertical ---------
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100)); // marges

        // --------- Panel Recherche collé en haut ---------
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JLabel nomLabel = new JLabel("Nom du produit à modifier:");
        nomField = new JTextField(20);
        searchButton = new JButton("Rechercher");

        searchPanel.add(nomLabel);
        searchPanel.add(nomField);
        searchPanel.add(searchButton);

        // --------- Panel Formulaire ---------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 230, 245)); // gris bleuté
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 210), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        prixField = new JTextField(20);
        quantiteField = new JTextField(20);
        categorieCombo = new JComboBox<>();
        updateButton = new JButton("Mettre à jour");

        // Désactiver les champs au départ
        prixField.setEnabled(false);
        quantiteField.setEnabled(false);
        categorieCombo.setEnabled(false);
        updateButton.setEnabled(false);

        // Placement des composants
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Prix:"), gbc);
        gbc.gridx = 1;
        formPanel.add(prixField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantiteField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Catégorie:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categorieCombo, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(updateButton, gbc);

        // Ajouter les deux parties au panel principal
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Remplir les catégories au démarrage
        chargerCategories();

        // --------- Bouton Rechercher ---------
        searchButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nom de produit.");
                return;
            }

            try (Connection con = DbConnection.getConnection()) {
                String query = "SELECT * FROM Produit WHERE nom = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, nom);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    produitId = rs.getInt("IdProduit");
                    prixField.setText(String.valueOf(rs.getDouble("prix")));
                    quantiteField.setText(String.valueOf(rs.getInt("quantite")));
                    categorieCombo.setSelectedItem(rs.getString("categorie"));

                    prixField.setEnabled(true);
                    quantiteField.setEnabled(true);
                    categorieCombo.setEnabled(true);
                    updateButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Produit introuvable.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la recherche.");
            }
        });

        // --------- Bouton Mettre à jour ---------
        updateButton.addActionListener(e -> {
            if (produitId == -1) {
                JOptionPane.showMessageDialog(this, "Aucun produit sélectionné.");
                return;
            }

            try (Connection con = DbConnection.getConnection()) {
                String query = "UPDATE Produit SET prix = ?, quantite = ?, categorie = ? WHERE IdProduit = ?";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setDouble(1, Double.parseDouble(prixField.getText()));
                stmt.setInt(2, Integer.parseInt(quantiteField.getText()));
                stmt.setString(3, categorieCombo.getSelectedItem().toString());
                stmt.setInt(4, produitId);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Produit mis à jour avec succès !");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.");
            }
        });
    }

    private void chargerCategories() {
        try (Connection con = DbConnection.getConnection()) {
            categorieCombo.removeAllItems();
            PreparedStatement stmt = con.prepareStatement("SELECT DISTINCT categorie FROM Produit");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categorieCombo.addItem(rs.getString("categorie"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de chargement des catégories.");
        }
    }
}
