package echoVenteFavafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierProduit {
    public void modifierProduit(int idProduit, String nom, double prix, int quantite) {
        String query = "UPDATE Produit SET nom = ?, prix = ?, quantite = ? WHERE id_produit = ?";

        try (Connection conn = sqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setDouble(2, prix);
            pstmt.setInt(3, quantite);
            pstmt.setInt(4, idProduit);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Produit modifié avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit : " + e.getMessage());
        }
    }
}
