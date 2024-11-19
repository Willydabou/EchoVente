package echoVenteFavafx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AjouterProduit {
    public void ajouterProduit(String nom, double prix, int quantite) {
        String query = "INSERT INTO Produit (nom, prix, quantite) VALUES (?, ?, ?)";

        try (Connection conn = sqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setDouble(2, prix);
            pstmt.setInt(3, quantite);

            pstmt.executeUpdate();
            System.out.println("Produit ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
    }
}
