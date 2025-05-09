package com.venteSwing;

import java.awt.*;

import javax.swing.*;


public class Menu extends JFrame {
    private int marge = 20;
    private int largeur;
    private int hauteur;
    private JPanel contentPanel; // Panel principal qu'on modifie dynamiquement
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

        // Création du menu
        JMenu accueil = new JMenu("Accueil");
        JMenu produit = new JMenu("Liste de Vente");
        JMenu dashboard = new JMenu("Dashboard");

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(bleuMarine);
        menuBar.setPreferredSize(new Dimension((int)(largeur * 0.7), 40));
        if (role.equals("vendeur")) {
            menuBar.add(accueil);
            menuBar.add(produit);
           
        } else if (role.equals("admin")) {
           
            menuBar.add(dashboard);
            menuBar.add(accueil);
            menuBar.add(produit);
            // tu peux ajouter d'autres menus propres à l'admin
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        topPanel.setBackground(bleuRoi);
        topPanel.add(menuBar);
        add(topPanel, BorderLayout.NORTH);

        // ⚠️ N’utilise pas une nouvelle variable ici — utilise l’attribut !
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Affiche le panel de vente par défaut
        showPanel(new Vendre());

        // Actions des menus
        accueil.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                showPanel(new Vendre());
            }

            @Override public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        produit.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                try {
                    // Tu peux aussi créer un JPanel personnalisé ici si tu veux l’intégrer dans le contentPanel
                    showPanel(new ListeVente()); // à créer
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        dashboard.addMenuListener(new javax.swing.event.MenuListener() {
            @Override
            public void menuSelected(javax.swing.event.MenuEvent e) {
                try {
                    // Tu peux aussi créer un JPanel personnalisé ici si tu veux l’intégrer dans le contentPanel
                    showPanel(new Dashboard()); 
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override public void menuDeselected(javax.swing.event.MenuEvent e) {}
            @Override public void menuCanceled(javax.swing.event.MenuEvent e) {}
        });

        setVisible(true);
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
