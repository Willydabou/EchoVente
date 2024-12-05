package Db_connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ProduitActionImplementation implements ProduitAction {
	Scanner Sc = new Scanner(System.in);
	
	
	// -------------------- AJOUTER
	@Override
	public void ajouterProduit() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
		
		//Statement stmt = con.createStatement();	
		Produit produit_temp = new Produit();
		
		System.out.println("nom du produit: ");
		String nom = Sc.nextLine();
		produit_temp.setNomProduit(nom);
		
		System.out.println("prix du produit: ");
		double prix = Sc.nextDouble();
		produit_temp.setPrixProduit(prix);
		
		System.out.println("quantité du produit: ");
		int quantite = Sc.nextInt();
		produit_temp.setQuantiteProduit(quantite);
		
		System.out.println("cathegorie du produit: ");
		String cathegorie = Sc.next();
		produit_temp.setCategorie(cathegorie);
		
		// -----
		String ajoutQuery = "insert into Produit (nom,prix,quantite,categorie) values(?,?,?,?)";
		
		try (PreparedStatement stmt = con.prepareStatement(ajoutQuery)) {
            stmt.setString(1, produit_temp.getNomProduit());
            stmt.setDouble(2, produit_temp.getPrixProduit());
            stmt.setInt(3, produit_temp.getQuantiteProduit());
            stmt.setString(4, produit_temp.getCategorieProduit());
            stmt.executeUpdate();
            
            System.out.println("Le produit a été bien ajouter");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	
	// -------------------- MODIFIER
	@Override
	public void modifierProduit() throws ClassNotFoundException, SQLException {
		Scanner Sca = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
		
		System.out.println("Entrer le nom du produit");
		String nom = Sc.nextLine();
		
		String query = "SELECT * FROM Produit WHERE nom = ?";
		
		try (PreparedStatement preparedStmt = con.prepareStatement(query)) {     // search the item for change using his name 
	        preparedStmt.setString(1,nom); // set the input value to the query
	        
	        
            ResultSet resultSet = preparedStmt.executeQuery();
            
	        if (resultSet.next()) { // Move the cursor to the first row
	            Produit produit = new Produit();
	            produit.setIdProduit(resultSet.getInt("IdProduit"));
	            produit.setNomProduit(resultSet.getString("nom"));
	            produit.setPrixProduit(resultSet.getDouble("prix"));
	            produit.setQuantiteProduit(resultSet.getInt("quantite"));
	            produit.setCategorie(resultSet.getString("categorie"));

	            while (true) { // Choosing what chqnge to be done on the item
	            
	            	System.out.println("/n Voulez-vous modifier:/n 1 - Nom/n 2 - Prix/n 3 - Quantite/n 4 - Categorie/n 5 - Annuler/n");
	            	int mdf = Sc.nextInt();
	            	String attribu = "";    Object nouvelleValeur = new Object();
	            	
	            	if (mdf == 1) { 
	            		attribu = "nom";
	            		//Scanner Sca = new Scanner(System.in);
	            		System.out.println("/nEntrer la nouvelle valeur : ");
	            		String nv = Sca.nextLine();
	            		nouvelleValeur = nv;
	            	}
	            	
	            	if (mdf == 2) {
	            		attribu = "prix";
	            		System.out.println("/nEntrer la nouvelle valeur : ");
	            		double nv = Sca.nextDouble();
	            		nouvelleValeur = nv;
	            	}
	            	
	            	if (mdf == 3) { 
	            		attribu = "quantite";
	            		System.out.println("/nEntrer la nouvelle valeur : ");
	            		int nv = Sca.nextInt();
	            		nouvelleValeur = nv;
	            	}
	            	
	            	if (mdf == 4) {
	            		attribu = "categorie";
	            		System.out.println("/nEntrer la nouvelle valeur : ");
	            		String nv = Sca.next();
	            		nouvelleValeur = nv;
	            	}
	            	if (mdf == 5) { break; }
	            	
	            	
	            	String query_change = "UPDATE Produit SET " + attribu + " = ? WHERE IdProduit = ?";
	            	
	            	
	            	
	            	try (PreparedStatement pstmt = con.prepareStatement(query_change)) {
	                    // Déterminer le type de la nouvelle valeur
	            		if (attribu.equalsIgnoreCase("nom")) {
	            			pstmt.setString(1, nouvelleValeur.toString());
	            		}
	            		else if (attribu.equalsIgnoreCase("prix")) {
	                        pstmt.setDouble(1, (Double) nouvelleValeur);
	                    } else if (attribu.equalsIgnoreCase("quantite")) {
	                        pstmt.setInt(1, (Integer) nouvelleValeur);
	                    } else {
	                        pstmt.setString(1, nouvelleValeur.toString());
	                    }
	                    pstmt.setInt(2, produit.getIdProduit());
	                    pstmt.executeUpdate();
	                    System.out.println("Le produit a été mis à jour avec succès !");
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                    System.out.println("Erreur lors de la mise à jour du produit.");
	                }
	            			
	            }
	            
	        } else {
	            System.out.println("Aucun produit trouvé avec le nom: " + nom);
	        }
	        
		}
		catch (SQLException e) {
            e.printStackTrace();
        }
		
		
		
	}
	

	// -------------------- SUPPRIMER
	@Override
	public void supprimerProduit() throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
		
		System.out.println("Entrer le nom du produit");
		String nom = Sc.nextLine();
		
		String query = "DELETE FROM Produit WHERE nom = ?";
		
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, nom);
            int x = stmt.executeUpdate();
            if (x!=0) {  System.out.println("Le produit a été bien supprimer"); }
            else {  System.out.println("Aucun produit à ce nom");  }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	
	// -------------------- LISTER 
	@Override
	public List<Produit> listeProduit() throws ClassNotFoundException, SQLException {
		List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM Produit";
        
        Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
        
        try (Statement stmt = con.createStatement();
             ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                Produit produit = new Produit();
                produit.setIdProduit(rset.getInt("IdProduit"));
                produit.setNomProduit(rset.getString("nom"));
                produit.setPrixProduit(rset.getDouble("prix"));
                produit.setQuantiteProduit(rset.getInt("quantite"));
                produit.setCategorie(rset.getString("categorie"));
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
	}
	
	
	// -------------------- UPDATE STOCK
	@Override
	public void updateStock(String nom, int quantite) throws ClassNotFoundException, SQLException {
		// Le nom et la quantiteé du produit vendue
		
		Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
		
		String query = "SELECT * FROM Produit WHERE nom = ?";
		
		try (PreparedStatement preparedStmt = con.prepareStatement(query)) {     // search the item for change using his name 
	        preparedStmt.setString(1,nom); // set the input value to the query
	        
	        
            ResultSet resultSet = preparedStmt.executeQuery();
            
	        if (resultSet.next()) { // Move the cursor to the first row
	            Produit produit = new Produit();
	            produit.setIdProduit(resultSet.getInt("IdProduit"));
	            produit.setNomProduit(resultSet.getString("nom"));
	            produit.setPrixProduit(resultSet.getDouble("prix"));
	            produit.setQuantiteProduit(resultSet.getInt("quantite"));
	            produit.setCategorie(resultSet.getString("categorie"));
	            
	            int nouveau_qtt = produit.getQuantiteProduit() - quantite;
	            
	            String query_change = "UPDATE Produit SET quantite = ? WHERE IdProduit = ?";
	            
	            try (PreparedStatement pstmt = con.prepareStatement(query_change)){
	            	pstmt.setInt(1, (Integer) nouveau_qtt);
	            	pstmt.setInt(2, produit.getIdProduit());
                    pstmt.executeUpdate();
	            	
	            }
	            catch(SQLException e) {
	            	e.printStackTrace();
                    System.out.println("Un erreur s'est produit");
	            	
	            }
	            
	            
	            //System.out.println("Nouveau = " + nouveau_qtt);
	            
	            //System.out.println(produit.getNomProduit() + "  " + produit.getPrixProduit());
	        }
		}
		
		
	}
}
