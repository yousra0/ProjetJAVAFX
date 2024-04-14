package Controller;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GluonMapExample extends Application
{
    public static void main(String[] args)
    {
        launch();
    }
    @Override
    public void start(Stage stage)
    {
        /* Définit la plate-forme pour éviter "javafx.platform is not defined" */
        System.setProperty("javafx.platform", "desktop");
        /*
         * Définit l'user agent pour éviter l'exception
         * "Server returned HTTP response code: 403"
         */
        System.setProperty("http.agent", "Gluon Mobile/1.0.3");
        VBox root = new VBox();

        /* Création de la carte Gluon JavaFX */
        MapView mapView = new MapView();

        /* Création du point avec latitude et longitude */
        MapPoint espritLocation = new MapPoint(36.89830009644064, 10.186927429895055);

        /* Création et ajoute une couche à la carte */

        // MapLayer mapLayer = new CustomPinLayer(mapPoint);
        // MapLayer mapLayer = new CustomCircleMarkerLayer(mapPoint);
        // mapView.addLayer(mapLayer);

        /* Création et ajout d'une couche à la carte */
        CustomCircleMarkerLayer markerLayer = new CustomCircleMarkerLayer(espritLocation);
        mapView.addLayer(markerLayer);

        /* Zoom de 5 */
        mapView.setZoom(5);

        /* Centre la carte sur le point */
        mapView.flyTo(0, espritLocation, 0.1);

        root.getChildren().add(mapView);
        /*
         * IMPORTANT mettre la taille de la fenêtre pour éviter l'erreur
         * java.lang.OutOfMemoryError
         */
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
}