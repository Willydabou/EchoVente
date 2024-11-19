package echoVenteFavafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class supprimerProduit {
	
	public void supprimerProduits(int id) {
        String query = "DELETE FROM Produit WHERE id = ?";  // SQL pour supprimer un produit par son ID

        try (Connection conn = sqlConnection.getConnection();  // Connexion à la base de données
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Définir la valeur du paramètre (ID du produit)
            pstmt.setInt(1, id);

            // Exécuter la requête de suppression
            int rowsAffected = pstmt.executeUpdate();

            // Vérifier si une ligne a été supprimée
            if (rowsAffected > 0) {
                System.out.println("Produit supprimé avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit : " + e.getMessage());
        }
    }
}
