/**
 * 
 */
/**
 * 
 */
module echoventeJavafx {
	requires javafx.controls;
	requires java.sql;
    requires javafx.fxml;
    requires javafx.graphics;  // S'assurer que ce module est requis
    requires javafx.base;
    
    exports echoVenteFavafx; // Exportez votre package contenant MainApp

    opens echoVenteFavafx to javafx.fxml;
}