package Db_connect;
import java.sql.Connection ;
import java.util.List;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.sql.Statement;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.cj.jdbc.Driver");  //Register the driver
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Echovente","root","13Nov*19");
		
		Scanner Sc = new Scanner(System.in);
		//Statement stmt = con.createStatement();		
		while(true){
			System.out.println("=== Welcome to the Echovente ===");
			System.out.println("1 - AJOUTER un produit");
			System.out.println("2 - MODIFIER un produit");
			System.out.println("3 - SUPPRIMER un produit");
			System.out.println("4 - AFFICHER la liste des produit");
			System.out.println("5 - METTRE à jour le stock");
			System.out.println("6 - Exit");
			int menu = Sc.nextInt();
			
			switch (menu) {
				case 1:
					System.out.println("Ajouter un produit");
					
					ProduitAction produit_a_ajouter = new ProduitActionImplementation();
					produit_a_ajouter.ajouterProduit();
					
					break;
					
					
				case 2:
					System.out.println("Modifier un produit");
					
					ProduitAction produit_modifier = new ProduitActionImplementation();
					produit_modifier.modifierProduit();
					
					break;
					
					
				case 3:
					System.out.println("Supprimer un produit");
					
					ProduitAction produit_a_supprimer = new ProduitActionImplementation();
					produit_a_supprimer.supprimerProduit();
					break;
					
					
				case 4:
					System.out.println("Afficher la liste des produit");
					
					ProduitAction list_produit = new ProduitActionImplementation();
					
					List<Produit> produits = list_produit.listeProduit();
		            for (Produit produit : produits) {
		                System.out.println("Produit: " + produit.getNomProduit() + ", \tPrix: " + produit.getPrixProduit());
		            }
					break;
					
					
				case 5:
					System.out.println("Mettre à jours les stock");
					Scanner Sca = new Scanner(System.in);
					
					String nom = Sca.nextLine();
					int quantite = Sca.nextInt();
					
					ProduitAction stock_produit = new ProduitActionImplementation();
					stock_produit.updateStock(nom, quantite);
					break;
				
					
				case 6:
					System.out.println("Merciii");
					break;
					
				default:
					System.out.println("Erreur");
			}
		}

	}

}
