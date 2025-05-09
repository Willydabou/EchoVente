package com.venteSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class AjouterProduitPanel extends JPanel {

    private JTextField nomField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JTextField categorieField;
    private JComboBox<String> categorieComboBox;
    private JButton ajouterButton;
    private JLabel messageLabel;

    public AjouterProduitPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 242, 245)); // Gris clair

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Ajouter un Produit");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(65, 105, 225)); // Bleu roi
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // === Nom du produit ===
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(createLabel("Nom du produit :"), gbc);
        gbc.gridx = 1;
        nomField = createTextField();
        add(nomField, gbc);

        // === Prix ===
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(createLabel("Prix :"), gbc);
        gbc.gridx = 1;
        prixField = createTextField();
        add(prixField, gbc);

        // === Quantité ===
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(createLabel("Quantité :"), gbc);
        gbc.gridx = 1;
        quantiteField = createTextField();
        add(quantiteField, gbc);

     // === Catégorie ===
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(createLabel("Catégorie :"), gbc);

        gbc.gridx = 1;
        categorieComboBox = new JComboBox<>();
        categorieComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categorieComboBox.setPreferredSize(new Dimension(200, 25));
        categorieComboBox.setBackground(Color.WHITE);
        add(categorieComboBox, gbc);

        loadCategories(); // Charger les catégories à l'initialisation


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
        add(ajouterButton, gbc);

        // === Message de succès/erreur ===
        gbc.gridy = 6;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterProduitAction();
            }
        });
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

    private void ajouterProduitAction() {
        String nom = nomField.getText().trim();
        String prixStr = prixField.getText().trim();
        String quantiteStr = quantiteField.getText().trim();
        String categorie = (String) categorieComboBox.getSelectedItem();

        if (nom.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty() || categorie.isEmpty()) {
            showMessage("Veuillez remplir tous les champs.", Color.RED);
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            int quantite = Integer.parseInt(quantiteStr);

            ProduitAction action = new ProduitActionImplementation();
            action.ajouterProduit(nom, prix, quantite, categorie);

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
        categorieComboBox.setSelectedIndex(0);

    }
    
    private void loadCategories() {
        try {
            Connection con = DbConnection.getConnection();
            String query = "SELECT nom FROM Categorie";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categorieComboBox.addItem(rs.getString("nom"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement des catégories.", Color.RED);
        }
    }

}
