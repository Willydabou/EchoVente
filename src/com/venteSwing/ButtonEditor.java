package com.venteSwing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private DefaultTableModel model;
    private JLabel totalLabel;
    private JLabel rowCountLabel;

    public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, JTable table, JLabel totalLabel, JLabel rowCountLabel) {
        super(checkBox);
        this.model = model;
        this.totalLabel = totalLabel;
        this.rowCountLabel = rowCountLabel;

        button = new JButton("Supprimer");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                int row = table.getSelectedRow();
                if (row >= 0) {
                    model.removeRow(row);
                    updateTotal();
                }
            }
        });
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object somme = model.getValueAt(i, 3);
            if (somme != null) {
                try {
                    total += Double.parseDouble(somme.toString());
                } catch (NumberFormatException ignored) {}
            }
        }
        totalLabel.setText("Total: " + String.format("%.2f", total) + " Ar");
        rowCountLabel.setText("Articles: " + model.getRowCount());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "Supprimer";
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}
