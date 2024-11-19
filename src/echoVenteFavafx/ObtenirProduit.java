package echoVenteFavafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObtenirProduit {
    public void obtenirProduit(int idProduit) {
        String query = "SELECT * FROM Produit WHERE id_produit = ?";

        try (Connection conn = sqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idProduit);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID : " + rs.getInt("id_produit"));
                    System.out.println("Nom : " + rs.getString("nom"));
                    System.out.println("Prix : " + rs.getDouble("prix"));
                    System.out.println("Quantité : " + rs.getInt("quantite"));
                } else {
                    System.out.println("Aucun produit trouvé avec cet ID.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit : " + e.getMessage());
        }
    }
}

