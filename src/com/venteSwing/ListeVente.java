package com.venteSwing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class ListeVente extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable tableVentes;
    private DefaultTableModel model;
    private JTextField txtRecherche;
    private JComboBox<String> comboFiltre;
    
    private int marge = 20;
    private int largeur ;
    private int hauteur;
    private JButton btnTelecharger;

    public ListeVente() throws ClassNotFoundException {
        setName("Liste des Ventes");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        largeur = screenSize.width - marge;
        hauteur = screenSize.height - marge;
        
        setSize(largeur, hauteur);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 🔵 Définition des couleurs
        Color bleuRoi = new Color(0, 47, 167); // Bleu roi
        Color bleuClair = new Color(173, 216, 230); // Bleu ciel clair
        Color blanc = Color.WHITE;

        // Table des ventes
        model = new DefaultTableModel();
        tableVentes = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;  // Colonne des cases à cocher
                }
                return super.getColumnClass(column);
            }
        };
        
     // 🔵 Personnalisation des en-têtes
        JTableHeader header = tableVentes.getTableHeader();
        header.setBackground(bleuRoi);
        header.setForeground(blanc);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        
        
     // 🔵 Personnalisation des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableVentes.setDefaultRenderer(String.class, centerRenderer);
        tableVentes.setRowHeight(30);
        tableVentes.setFont(new Font("Arial", Font.PLAIN, 14));
        tableVentes.setBackground(blanc);
        tableVentes.setSelectionBackground(bleuClair);
        
        JScrollPane scrollPane = new JScrollPane(tableVentes);
        add(scrollPane, BorderLayout.CENTER);

        // Panel pour filtres
        JPanel panelFiltre = new JPanel();
        txtRecherche = new JTextField(15);
        comboFiltre = new JComboBox<>(new String[]{"Tout", "Nom du produit", "Date", "Quantité"});
        JButton btnFiltrer = new JButton("Filtrer");
        JComboBox<String> triBox = new JComboBox<>(new String[]{
            "Date décroissante",
            "Nom du produit",
            "Produit le plus vendu"
        });

        btnTelecharger = new JButton("Télécharger la sélection");
        btnTelecharger.setBackground(bleuRoi);
        btnTelecharger.setForeground(blanc);
        btnTelecharger.setFont(new Font("Arial", Font.BOLD, 14));
        btnTelecharger.setFocusPainted(false);
        btnTelecharger.setBorderPainted(false);
        btnTelecharger.setPreferredSize(new Dimension(150, 40));
        
        btnTelecharger.addActionListener(e -> telechargerSelection());
        
        JButton btnSelectionnerTout = new JButton("Sélectionner tout");
        btnSelectionnerTout.addActionListener(e -> selectionnerTout());

        panelFiltre.add(new JLabel("Filtrer par :"));
        panelFiltre.add(comboFiltre);
        panelFiltre.add(txtRecherche);
        panelFiltre.add(btnFiltrer);
        panelFiltre.add(triBox);
        panelFiltre.add(btnSelectionnerTout);

        add(panelFiltre, BorderLayout.NORTH);
        add(btnTelecharger, BorderLayout.SOUTH);
        
        

        // Charger les ventes
        chargerVentes("", triBox.getSelectedItem().toString());

        // Action du bouton filtrer
        btnFiltrer.addActionListener(e -> {
            String critere = (String) comboFiltre.getSelectedItem();
            String valeur = txtRecherche.getText();
            try {
                chargerVentes(critere.equals("Tout") ? "" : critere + ":" + valeur, triBox.getSelectedItem().toString());
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        // Action de triBox
        triBox.addActionListener(e -> {
            try {
                chargerVentes("", triBox.getSelectedItem().toString());  // Recharger avec le tri choisi
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        setVisible(true);
    }
    
    private void selectionnerTout() {
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            model.setValueAt(true, i, 0);  // Cocher la case à la première colonne (index 0)
        }
    }

    private void chargerVentes(String filtre, String ordreTri) throws ClassNotFoundException {
        model.setRowCount(0);  // Effacer les anciennes données
        model.setColumnIdentifiers(new Object[]{"Sélection", "ID", "Produit", "Quantité", "Prix Total", "Date"});

        String query = "SELECT v.idVente, p.nom AS produit, v.quantiteAcheter, v.sommePrix, v.dateVente " +
                       "FROM Vente v JOIN Produit p ON v.idProduit = p.idProduit";
        if (!filtre.isEmpty()) {
            String[] parts = filtre.split(":");
            String critere = parts[0];
            String valeur = parts.length > 1 ? parts[1] : "";

            if (critere.equals("Nom du produit")) {
                query += " WHERE p.nom LIKE '%" + valeur + "%'";
            } else if (critere.equals("Date")) {
                query += " WHERE DATE_FORMAT(v.dateVente, '%Y-%m-%d') LIKE '%" + valeur + "%'";
            } else if (critere.equals("Quantité")) {
                query += " WHERE v.quantiteAcheter >= " + valeur;
            }
        }

        // Appliquer le tri en fonction du choix
        if (ordreTri.equals("Date décroissante")) {
            query += " ORDER BY v.dateVente DESC";
        } else if (ordreTri.equals("Nom du produit")) {
            query += " ORDER BY p.nom ASC";
        } else if (ordreTri.equals("Produit le plus vendu")) {
            query += " ORDER BY v.quantiteAcheter DESC";
        }

        try (Connection con = DbConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(false);  // Initialisation de la case à cocher
                row.add(rs.getInt("idVente"));
                row.add(rs.getString("produit"));
                row.add(rs.getInt("quantiteAcheter"));
                row.add(rs.getDouble("sommePrix"));
                row.add(rs.getString("dateVente"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des ventes");
        }
    }

    private void telechargerSelection() {
        int rowCount = model.getRowCount();
        boolean hasSelection = false;

        // Vérifier s'il y a une sélection
        for (int i = 0; i < rowCount; i++) {
            if ((Boolean) model.getValueAt(i, 0)) {  // Vérifier si la case est cochée
                hasSelection = true;
                break;
            }
        }

        if (!hasSelection) {
            JOptionPane.showMessageDialog(this, "Aucune vente sélectionnée !");
            return;
        }

        // Demander le format
        String[] options = {"Excel", "PDF", "Annuler"};
        int choix = JOptionPane.showOptionDialog(this, "Choisissez le format de téléchargement", "Télécharger",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choix == 0) {
            telechargerVentesExcel();
        } else if (choix == 1) {
            telechargerVentesPDF();
        }
        
     // Décoche toutes les cases après le téléchargement
        for (int i = 0; i < rowCount; i++) {
            model.setValueAt(false, i, 0);  // Décoche la case
        }

        // Rafraîchir la table pour appliquer la modification
        tableVentes.repaint();
    }

    private void telechargerVentesExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer sous...");
        fileChooser.setSelectedFile(new java.io.File("ventes_selection.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Ventes Sélectionnées");

                Row header = sheet.createRow(0);
                String[] columns = {"ID Vente", "Produit", "Quantité", "Prix Total", "Date"};
                for (int i = 0; i < columns.length; i++) {
                    header.createCell(i).setCellValue(columns[i]);
                }

                int rowIndex = 1;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((Boolean) model.getValueAt(i, 0)) {  // Vérifier si la case est cochée
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue((int) model.getValueAt(i, 1));
                        row.createCell(1).setCellValue((String) model.getValueAt(i, 2));
                        row.createCell(2).setCellValue((int) model.getValueAt(i, 3));
                        row.createCell(3).setCellValue((double) model.getValueAt(i, 4));
                        row.createCell(4).setCellValue((String) model.getValueAt(i, 5));
                    }
                }

                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }

                JOptionPane.showMessageDialog(this, "Fichier Excel exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'exportation !");
            }
        }
    }

    private void telechargerVentesPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer sous...");
        fileChooser.setSelectedFile(new java.io.File("ventes_selection.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath))) {
                Document document = new Document(pdfDoc);
                document.add(new Paragraph("Liste des ventes sélectionnées").setBold().setFontSize(16));
                document.add(new Paragraph("\n"));

                Table table = new Table(5);
                table.addCell("ID Vente");
                table.addCell("Produit");
                table.addCell("Quantité");
                table.addCell("Prix Total");
                table.addCell("Date");

                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((Boolean) model.getValueAt(i, 0)) {  // Vérifier si la case est cochée
                        table.addCell(String.valueOf(model.getValueAt(i, 1)));
                        table.addCell((String) model.getValueAt(i, 2));
                        table.addCell(String.valueOf(model.getValueAt(i, 3)));
                        table.addCell(String.valueOf(model.getValueAt(i, 4)));
                        table.addCell((String) model.getValueAt(i, 5));
                    }
                }

                document.add(table);
                document.close();

                JOptionPane.showMessageDialog(this, "Fichier PDF exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur lors de l'exportation !");
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ListeVente();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
