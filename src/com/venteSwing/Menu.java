package com.venteSwing;

import java.awt.*;
import javax.swing.*;

public class Menu extends JFrame {
    private int marge = 20;
    private int largeur;
    private int hauteur;
    private JPanel contentPanel;
    private String role;

    public Menu(String role) {
        this.role = role;
        initialize();
    }

    public void initialize() {
        setTitle("EchoVente");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        largeur = screenSize.width - marge;
        hauteur = screenSize.height - marge;
        setSize(largeur, hauteur);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Couleurs
        Color bleuMarine = new Color(0, 0, 102);
        Color bleuRoi = new Color(65, 105, 225);
        Color boutonActif = new Color(100, 149, 237); // Bleu clair

        // Panel de boutons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        menuPanel.setBackground(bleuRoi);

        // Création des boutons
        JButton accueilBtn = new JButton("Accueil");
        JButton produitBtn = new JButton("Liste de Vente");
        JButton dashboardBtn = new JButton("Dashboard");

        // Regrouper tous les boutons
        JButton[] buttons = {accueilBtn, produitBtn, dashboardBtn};

        // Style commun
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setBackground(bleuMarine);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // Ajouter les boutons selon le rôle
        if (role.equals("vendeur")) {
            menuPanel.add(accueilBtn);
            menuPanel.add(produitBtn);
        } else if (role.equals("admin")) {
            menuPanel.add(dashboardBtn);
            menuPanel.add(accueilBtn);
            menuPanel.add(produitBtn);
        }

        // Ajouter le panel de menu en haut
        add(menuPanel, BorderLayout.NORTH);

        // Panel principal
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Affichage du panel Accueil par défaut
        showPanel(new Vendre());
        setActiveButton(accueilBtn, buttons); // bouton actif par défaut

        // Actions
        accueilBtn.addActionListener(e -> {
            showPanel(new Vendre());
            setActiveButton(accueilBtn, buttons);
        });

        produitBtn.addActionListener(e -> {
            try {
                showPanel(new ListeVente());
                setActiveButton(produitBtn, buttons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        dashboardBtn.addActionListener(e -> {
            try {
                showPanel(new Dashboard());
                setActiveButton(dashboardBtn, buttons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setVisible(true);
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setActiveButton(JButton activeButton, JButton[] allButtons) {
        for (JButton btn : allButtons) {
            if (btn == activeButton) {
                btn.setBackground(new Color(100, 149, 237)); // Bleu clair
            } else {
                btn.setBackground(new Color(0, 0, 102)); // Bleu marine
            }
        }
    }
}
