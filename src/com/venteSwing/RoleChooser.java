package com.venteSwing;

import javax.swing.*;
import java.awt.*;

public class RoleChooser {

    public  void ChoisirRole() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Choisir un rôle");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new FlowLayout());

            JButton vendeurBtn = new JButton("Vendeur");
            JButton adminBtn = new JButton("Admin");

            vendeurBtn.addActionListener(e -> {
                frame.dispose();
                new Menu("vendeur");
            });

            adminBtn.addActionListener(e -> {
                String password = JOptionPane.showInputDialog(frame, "Mot de passe admin :", "Connexion admin", JOptionPane.PLAIN_MESSAGE);

                if (password != null && password.equals("admin123")) { // Mot de passe simple à changer plus tard
                    frame.dispose();
                    new Menu("admin");
                } else {
                    JOptionPane.showMessageDialog(frame, "Mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });

            frame.add(vendeurBtn);
            frame.add(adminBtn);

            frame.setVisible(true);
        });
    }
}
