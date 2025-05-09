package com.venteSwing;

import java.sql.SQLException;
import java.util.List;
	
	public interface ProduitAction {
		
		void ajouterProduit() throws SQLException, ClassNotFoundException;
		void modifierProduit() throws ClassNotFoundException, SQLException;
		void supprimerProduit() throws ClassNotFoundException, SQLException;
		List<Produit> listeProduit() throws ClassNotFoundException, SQLException;
		void updateStock(String nom, int quantite) throws ClassNotFoundException, SQLException;
		void ajouterProduit(String nom, double prix, int quantite, String categorie)
				throws SQLException, ClassNotFoundException;

	}
