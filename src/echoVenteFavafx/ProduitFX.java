package echoVenteFavafx;

import javafx.beans.property.*;

public class ProduitFX {

   private final IntegerProperty id;
   private final StringProperty nom;
   private final DoubleProperty prix;
   private IntegerProperty quantite = new SimpleIntegerProperty();

    public ProduitFX(int id, String nom, double prix, int quantite) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prix = new SimpleDoubleProperty(prix);
        this.quantite = new SimpleIntegerProperty(quantite);
    }

    @SuppressWarnings("exports")
	public IntegerProperty idProperty() {
        return id;
    }

    @SuppressWarnings("exports")
	public StringProperty nomProperty() {
        return nom;
    }


    @SuppressWarnings("exports")
	public DoubleProperty prixProperty() {
        return prix;
    }

    @SuppressWarnings("exports")
	public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }



    public double getPrix() {
        return prix.get();
    }

    public int getQuantite() {
        return quantite.get();
    }
}
