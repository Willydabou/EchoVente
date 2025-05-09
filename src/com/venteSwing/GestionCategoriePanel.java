package com.venteSwing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class GestionCategoriePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton ajouterButton;
    private JPanel formPanel;
    private JTextField nomField;
    private JButton enregistrerButton;
    private JLabel messageLabel;

    public GestionCategoriePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245)); // Gris clair

        // === Table ===
        tableModel = new DefaultTableModel(new Object[]{"Nom", "Modifier", "Supprimer"}, 0);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2;
            }
        };
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // Personnaliser les boutons dans la table
        table.getColumn("Modifier").setCellRenderer(new ButtonRenderer("Modifier"));
        table.getColumn("Supprimer").setCellRenderer(new ButtonRenderer("Supprimer"));
        table.getColumn("Modifier").setCellEditor(new ButtonEditor(new JCheckBox(), "Modifier"));
        table.getColumn("Supprimer").setCellEditor(new ButtonEditor(new JCheckBox(), "Supprimer"));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // === Bas : bouton ajouter ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 242, 245));
        ajouterButton = new JButton("Ajouter une catégorie");
        ajouterButton.setBackground(new Color(25, 42, 86)); // Bleu nuit
        ajouterButton.setForeground(Color.WHITE);
        ajouterButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottomPanel.add(ajouterButton);
        add(bottomPanel, BorderLayout.SOUTH);

        ajouterButton.addActionListener(e -> formPanel.setVisible(!formPanel.isVisible()));

        // === Formulaire d'ajout ===
        formPanel = new JPanel(new FlowLayout());
        formPanel.setBackground(new Color(240, 242, 245));
        nomField = new JTextField(20);
        enregistrerButton = new JButton("Enregistrer");
        enregistrerButton.setBackground(new Color(25, 42, 86));
        enregistrerButton.setForeground(Color.WHITE);
        formPanel.add(new JLabel("Nom de la catégorie :"));
        formPanel.add(nomField);
        formPanel.add(enregistrerButton);
        messageLabel = new JLabel();
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel);
        formPanel.setVisible(false);
        add(formPanel, BorderLayout.NORTH);

        enregistrerButton.addActionListener(e -> ajouterCategorie());

        chargerCategories();
    }

    private void ajouterCategorie() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            showMessage("Le nom ne doit pas être vide.", Color.RED);
            return;
        }

        try (Connection con = DbConnection.getConnection()) {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO Categorie(nom) VALUES(?)");
            stmt.setString(1, nom);
            stmt.executeUpdate();
            nomField.setText("");
            showMessage("Catégorie ajoutée avec succès.", new Color(0, 128, 0));
            chargerCategories();
            formPanel.setVisible(!formPanel.isVisible());
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            showMessage("Erreur lors de l'ajout.", Color.RED);
        }
    }

    private void chargerCategories() {
        tableModel.setRowCount(0);
        try (Connection con = DbConnection.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nom FROM Categorie");
            while (rs.next()) {
                String nom = rs.getString("nom");
                tableModel.addRow(new Object[]{nom, "Modifier", "Supprimer"});
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void supprimerCategorie(String nom) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer la catégorie \"" + nom + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DbConnection.getConnection()) {
                PreparedStatement stmt = con.prepareStatement("DELETE FROM Categorie WHERE nom = ?");
                stmt.setString(1, nom);
                stmt.executeUpdate();
                chargerCategories();
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                showMessage("Erreur lors de la suppression.", Color.RED);
            }
        }
    }

    private void modifierCategorie(String ancienNom) {
        String nouveauNom = JOptionPane.showInputDialog(this, "Modifier le nom :", ancienNom);
        if (nouveauNom != null && !nouveauNom.trim().isEmpty()) {
            try (Connection con = DbConnection.getConnection()) {
                PreparedStatement stmt = con.prepareStatement("UPDATE Categorie SET nom = ? WHERE nom = ?");
                stmt.setString(1, nouveauNom.trim());
                stmt.setString(2, ancienNom);
                stmt.executeUpdate();
                chargerCategories();
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                showMessage("Erreur lors de la modification.", Color.RED);
            }
        }
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
    }

    // ==== RENDERER et EDITOR pour boutons dans JTable ====

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setBackground(new Color(100, 149, 237)); // Bleu clair
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String action;
        private JButton button;
        private String currentNom;

        public ButtonEditor(JCheckBox checkBox, String action) {
            super(checkBox);
            this.action = action;
            button = new JButton(action);
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.setBackground(new Color(25, 42, 86));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                if (action.equals("Supprimer")) {
                    supprimerCategorie(currentNom);
                } else if (action.equals("Modifier")) {
                    modifierCategorie(currentNom);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentNom = (String) table.getValueAt(row, 0);
            return button;
        }

        public Object getCellEditorValue() {
            return action;
        }
    }
}
