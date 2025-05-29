package com.venteSwing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AjouterStockPanel extends JPanel {

    private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel model;

    public AjouterStockPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // === Titre ===
        JLabel title = new JLabel("Gestion du Stock");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(65, 105, 225));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(new Object[]{"ID", "Nom", "Quantité"}, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                } else {
                    c.setBackground(new Color(184, 207, 229));
                }
                return c;
            }
        };
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(100, 149, 237));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // === Événement de clic sur une ligne ===
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int id = (int) model.getValueAt(row, 0);
                    String nom = (String) model.getValueAt(row, 1);
                    int quantiteActuelle = (int) model.getValueAt(row, 2);

                    int response = JOptionPane.showConfirmDialog(
                        AjouterStockPanel.this,
                        "Souhaitez-vous ajouter du stock pour le produit \"" + nom + "\" ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (response == JOptionPane.YES_OPTION) {
                        afficherFormulaireAjout(id, nom, quantiteActuelle);
                    }
                }
            }
        });

        chargerProduits();
    }

    private void afficherFormulaireAjout(int idProduit, String nom, int quantiteActuelle) {
        JTextField quantiteField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Produit :"));
        JTextField nomField = new JTextField(nom);
        nomField.setEditable(false);
        panel.add(nomField);

        panel.add(new JLabel("Quantité à ajouter :"));
        panel.add(quantiteField);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Ajouter au stock", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int ajout = Integer.parseInt(quantiteField.getText().trim());
                if (ajout <= 0) throw new NumberFormatException();

                int nouvelleQuantite = quantiteActuelle + ajout;

                try (Connection con = DbConnection.getConnection()) {
                    String update = "UPDATE Produit SET quantite = ? WHERE IdProduit = ?";
                    PreparedStatement stmt = con.prepareStatement(update);
                    stmt.setInt(1, nouvelleQuantite);
                    stmt.setInt(2, idProduit);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Stock mis à jour !");
                    chargerProduits(); // Refresh table
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.");
            }
        }
    }

    private void chargerProduits() {
        model.setRowCount(0);
        try (Connection con = DbConnection.getConnection()) {
            String query = "SELECT IdProduit, nom, quantite FROM Produit";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("IdProduit"),
                    rs.getString("nom"),
                    rs.getInt("quantite")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des produits.");
        }
    }
}
