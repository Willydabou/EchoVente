package echoVenteFavafx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.List;

public class MainApp extends Application {

    // TableView pour afficher les produits
    private TableView<ProduitFX> table = new TableView<>();

    // Champs de texte pour l'ajout et modification de produit
    private TextField txtNom = new TextField();
    private TextField txtDescription = new TextField();
    private TextField txtPrix = new TextField();
    private TextField txtQuantite = new TextField();

    // Instance des classes de gestion
    private AjouterProduit ajouterProduit = new AjouterProduit();
    private ModifierProduit modifierProduit = new ModifierProduit();
    private supprimerProduit supprimerProduit = new supprimerProduit();
    private ObtenirAllProduits obtenirProduit = new ObtenirAllProduits();

    public static void main(String[] args) {
        launch(args);
    }

    @SuppressWarnings("unchecked")
	@Override
    public void start(Stage stage) {
        stage.setTitle("Gestion de Produits");

        // Configuration de la table pour afficher les produits
        initialiserTable();
        
        // Mise en page du formulaire pour ajouter/modifier un produit
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("Nom:"), 0, 0);
        form.add(txtNom, 1, 0);

        form.add(new Label("Prix:"), 0, 2);
        form.add(txtPrix, 1, 2);

        form.add(new Label("Quantité:"), 0, 3);
        form.add(txtQuantite, 1, 3);

        // Boutons
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setOnAction(e -> ajouterProduit());

        Button btnModifier = new Button("Modifier");
        btnModifier.setOnAction(e -> modifierProduit());

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setOnAction(e -> supprimerProduit());

//        Button btnAfficher = new Button("Afficher");
//        btnAfficher.setOnAction(e -> afficherProduits());

        HBox buttonBox = new HBox(10, btnAjouter, btnModifier, btnSupprimer);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout principal
        VBox layout = new VBox(20, form, buttonBox, table);
        layout.setPadding(new javafx.geometry.Insets(20));
        Scene scene = new Scene(layout, 600, 400);

        stage.setScene(scene);
        stage.show();
    }

    // Fonction pour ajouter un produit
    private void ajouterProduit() {
        String nom = txtNom.getText();
        double prix = Double.parseDouble(txtPrix.getText());
        int quantite = Integer.parseInt(txtQuantite.getText());
        ajouterProduit.ajouterProduit(nom, prix, quantite);
        clearFields();
        initialiserTable();
    }

    // Fonction pour modifier un produit
    private void modifierProduit() {
        int id = 1; // Exemple d'ID
        String nom = txtNom.getText();
        double prix = Double.parseDouble(txtPrix.getText());
        int quantite = Integer.parseInt(txtQuantite.getText());
        modifierProduit.modifierProduit(id, nom, prix, quantite);
        clearFields();
    }

    // Fonction pour supprimer un produit
    private void supprimerProduit() {
        int id = 1; // Exemple d'ID
        supprimerProduit.supprimerProduits(id);
        clearFields();
    }

    // Fonction pour afficher les produits
  
//    private void afficherProduits() {
//        table.getItems().clear();  // Effacer les éléments existants
//        List<ProduitFX> produits = obtenirProduit.obtenirTousLesProduits();  // Récupérer les produits
//        ObservableList<ProduitFX> produitsList = FXCollections.observableArrayList(produits);  // Convertir en ObservableList
//        table.setItems(produitsList);  // Ajouter les produits à la TableView
//    }

    // Fonction pour vider les champs de saisie
    private void clearFields() {
        txtNom.clear();
        txtPrix.clear();
        txtQuantite.clear();
    }
    
   
	private void initialiserTable() {
        TableColumn<ProduitFX, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());


        TableColumn<ProduitFX, Double> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());

        TableColumn<ProduitFX, Integer> colQuantite = new TableColumn<>("Quantité");
        colQuantite.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());

        table.getColumns().addAll(colNom,colPrix, colQuantite);
    }
}
