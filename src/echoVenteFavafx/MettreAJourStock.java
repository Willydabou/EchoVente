package echoVenteFavafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MettreAJourStock {
    public void mettreAJourStock(int idProduit, int nouvelleQuantite) {
        String query = "UPDATE Produit SET quantite = ? WHERE id_produit = ?";

        try (Connection conn = sqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idProduit);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Stock mis à jour avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
    }
}
