package echoVenteFavafx;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ObtenirAllProduits {

	    public List<Produit> obtenirTousLesProduits() {
	        List<Produit> produits = new ArrayList<>();
	        String query = "SELECT * FROM Produit"; // SQL pour récupérer tous les produits

	        try (Connection conn = sqlConnection.getConnection(); // Utilisation de la méthode pour obtenir une connexion
	             PreparedStatement pstmt = conn.prepareStatement(query);
	             ResultSet rs = pstmt.executeQuery()) {

	            // Parcourir les résultats et les ajouter à la liste
	            while (rs.next()) {
	                int id = rs.getInt("id");
	                String nom = rs.getString("nom");
	                double prix = rs.getDouble("prix");
	                int quantite = rs.getInt("quantite");

	                // Création de l'objet Produit en utilisant le constructeur du record
	                Produit produit = new Produit(id, nom, prix, quantite);
	                produits.add(produit);
	            }
	        } catch (SQLException e) {
	            System.err.println("Erreur lors de la récupération des produits : " + e.getMessage());
	        }
	        return produits;
	    }

}
