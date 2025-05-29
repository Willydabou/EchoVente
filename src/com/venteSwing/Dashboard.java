package com.venteSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard extends JPanel {

    private JPanel contentPanel;
    private JButton boutonActif = null;

    public Dashboard() throws ClassNotFoundException {
        setLayout(new BorderLayout());

        // Couleurs
        Color grisClair = new Color(230, 230, 230);
        Color bleuClair = new Color(100, 149, 237); // Bleu normal
        Color bleuFonce = new Color(65, 105, 225);  // Actif
        Color bleuHover = new Color(120, 170, 255); // Survol

        // Partie gauche : menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(grisClair);
        menuPanel.setPreferredSize(new Dimension(250, getHeight()));

        String[] boutons = {
            "Dashboard",
            "Ajouter produit",
            "Modifier produit",
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
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover effect
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn != boutonActif) {
                        btn.setBackground(bleuHover);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn != boutonActif) {
                        btn.setBackground(bleuClair);
                    }
                }
            });

            btn.addActionListener(new MenuButtonListener(label, btn, bleuClair, bleuFonce));
            menuPanel.add(Box.createVerticalStrut(10));
            menuPanel.add(btn);
        }

        // Partie droite : contenu dynamique
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);

        // Ajouter les vues disponibles
        contentPanel.add(getDashboardPanel(), "Dashboard");
        contentPanel.add(new AjouterProduitPanel(), "Ajouter produit");
        contentPanel.add(new ModifierProduitPanel(), "Modifier produit");
        contentPanel.add(new GestionCategoriePanel(), "Catégorie de produit");
        contentPanel.add(new AjouterStockPanel(), "Modifier stock");

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

        try (Connection conn = DbConnection.getConnection()) {
            PreparedStatement stmtSemaine = conn.prepareStatement(
                "SELECT SUM(quantiteAcheter) FROM Vente WHERE DATE(dateVente) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)"
            );
            ResultSet rsSemaine = stmtSemaine.executeQuery();
            if (rsSemaine.next()) {
                venteSemaine.setText("📊 Vente cette semaine : " + rsSemaine.getInt(1));
            }

            PreparedStatement stmtTop = conn.prepareStatement(
                "SELECT nom FROM Produit INNER JOIN Vente ON Vente.idProduit = Produit.idProduit " +
                "GROUP BY Produit.idProduit ORDER BY SUM(quantiteAcheter) DESC LIMIT 1"
            );
            ResultSet rsTop = stmtTop.executeQuery();
            if (rsTop.next()) {
                produitTop.setText("🔥 Produit le plus vendu : " + rsTop.getString("nom"));
            }

            PreparedStatement stmtHier = conn.prepareStatement(
                "SELECT SUM(quantiteAcheter) FROM Vente WHERE DATE(dateVente) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)"
            );
            ResultSet rsHier = stmtHier.executeQuery();
            if (rsHier.next()) {
                venteHier.setText("🗓️ Produits vendus hier : " + rsHier.getInt(1));
            }

            PreparedStatement stmtStock = conn.prepareStatement(
                "SELECT COUNT(*) FROM Produit WHERE quantite < 10"
            );
            ResultSet rsStock = stmtStock.executeQuery();
            if (rsStock.next()) {
                stockCritique.setText("⚠️ Produits en rupture ou faible stock : " + rsStock.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        private final JButton bouton;
        private final Color normalColor;
        private final Color activeColor;

        public MenuButtonListener(String panelName, JButton bouton, Color normalColor, Color activeColor) {
            this.panelName = panelName;
            this.bouton = bouton;
            this.normalColor = normalColor;
            this.activeColor = activeColor;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CardLayout cl = (CardLayout) (contentPanel.getLayout());
            cl.show(contentPanel, panelName);

            if (boutonActif != null) {
                boutonActif.setBackground(normalColor);
            }

            bouton.setBackground(activeColor);
            boutonActif = bouton;
        }
    }
}
