package Db_connect;

public class Produit {
	private int idProduit;
	private String nomProduit;
	private double prixProduit;
	private int quantiteProduit;
	private String categorieProduit;
	
	
	//---------- Constructors
	public Produit() {}
	public Produit(String nom, double prix, int quantite, String cathegorie) {
		this.nomProduit = nom;
		this.prixProduit = prix;
		this.quantiteProduit = quantite;
		this.categorieProduit = cathegorie;
	}
	
	
	//---------- Setters
	public void setIdProduit(int id) {
		this.idProduit = id;
	}
	
	public void setNomProduit(String nom) {
		this.nomProduit = nom;
	}
	
	public void setPrixProduit(double prix) {
		this.prixProduit = prix;
	}
	
	public void setQuantiteProduit(int quantite) {
		this.quantiteProduit = quantite;
	}
	
	public void setCategorie(String cathegorie) {
		this.categorieProduit = cathegorie;
	}
	
	
	//---------- Getters
	public int getIdProduit() {  return idProduit;  }
	
	public String getNomProduit() {  return nomProduit;  }
	
	public double getPrixProduit() {  return prixProduit;  }
	
	public int getQuantiteProduit() {  return quantiteProduit;  }
	
	public String getCategorieProduit() {  return categorieProduit;  }

}
