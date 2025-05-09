package com.venteSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dashboard extends JPanel {

    private JPanel contentPanel;

    public Dashboard() throws ClassNotFoundException {
        setLayout(new BorderLayout());

        // Couleurs
        Color grisClair = new Color(230, 230, 230);
        Color bleuClair = new Color(100, 149, 237); // Cornflower blue

        // Partie gauche : menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(grisClair);
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));

        String[] boutons = {
            "Dashboard",
            "Ajouter produit",
            "Catégorie de produit",
            "Modifier stock"
        };

        for (String label : boutons) {
            JButton btn = new JButton(label);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setBackground(bleuClair);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 14));

            btn.addActionListener(new MenuButtonListener(label));
            menuPanel.add(Box.createVerticalStrut(10));
            menuPanel.add(btn);
        }

        // Partie droite : contenu dynamique
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);

        // Ajouter les vues disponibles
        contentPanel.add(getDashboardPanel(), "Dashboard");
        contentPanel.add(new AjouterProduitPanel(), "Ajouter produit");
        contentPanel.add(new GestionCategoriePanel(), "Catégorie de produit");
        contentPanel.add(new GestionCategoriePanel(), "Modifier stock");

        // Diviser gauche et droite
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, contentPanel);
        splitPane.setDividerLocation(250);
        splitPane.setEnabled(false); // Empêcher le redimensionnement

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel getDashboardPanel() throws ClassNotFoundException {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel venteSemaine = new JLabel("📊 Vente cette semaine : ...", JLabel.LEFT);
        JLabel produitTop = new JLabel("🔥 Produit le plus vendu : ...", JLabel.LEFT);
        JLabel venteHier = new JLabel("🗓️ Produits vendus hier : ...", JLabel.LEFT);
        JLabel stockCritique = new JLabel("⚠️ Produits en rupture ou faible stock : ...", JLabel.LEFT);

        // Requête base de données
        try (Connection conn = DbConnection.getConnection()) {

            // 1. Vente de la semaine (ex : total produits vendus cette semaine)
            PreparedStatement stmtSemaine = conn.prepareStatement(
                "SELECT SUM(quantiteAcheter) FROM Vente WHERE DATE(dateVente) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)"
            );
            ResultSet rsSemaine = stmtSemaine.executeQuery();
            if (rsSemaine.next()) {
                int totalSemaine = rsSemaine.getInt(1);
                venteSemaine.setText("📊 Vente cette semaine : " + totalSemaine);
            }

            // 2. Produit le plus vendu
            PreparedStatement stmtTop = conn.prepareStatement(
                "SELECT nom FROM Produit inner join Vente on Vente.idProduit = Produit.idProduit GROUP BY Vente.idVente ORDER BY SUM(quantiteAcheter) DESC LIMIT 3"
            );
            ResultSet rsTop = stmtTop.executeQuery();
            if (rsTop.next()) {
                String topProduit = rsTop.getString("nom");
                produitTop.setText("🔥 Produit le plus vendu : " + topProduit);
            }

            // 3. Vente d'hier
            PreparedStatement stmtHier = conn.prepareStatement(
                "SELECT SUM(quantiteAcheter) FROM Vente WHERE DATE(dateVente) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)"
            );
            ResultSet rsHier = stmtHier.executeQuery();
            if (rsHier.next()) {
                int totalHier = rsHier.getInt(1);
                venteHier.setText("🗓️ Produits vendus hier : " + totalHier);
            }

            // 4. Produits en stock critique
            PreparedStatement stmtStock = conn.prepareStatement(
                "SELECT COUNT(*) FROM Produit WHERE quantite < 10"
            );
            ResultSet rsStock = stmtStock.executeQuery();
            if (rsStock.next()) {
                int critique = rsStock.getInt(1);
                stockCritique.setText("⚠️ Produits en rupture ou faible stock : " + critique);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Style
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Color bleuTexte = new Color(70, 130, 180);

        for (JLabel label : new JLabel[]{venteSemaine, produitTop, venteHier, stockCritique}) {
            label.setFont(labelFont);
            label.setForeground(bleuTexte);
            panel.add(label);
        }

        return panel;
    }

    private class MenuButtonListener implements ActionListener {
        private final String panelName;

        public MenuButtonListener(String panelName) {
            this.panelName = panelName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CardLayout cl = (CardLayout)(contentPanel.getLayout());
            cl.show(contentPanel, panelName);
        }
    }
}
