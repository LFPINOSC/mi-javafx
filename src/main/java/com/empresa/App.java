package com.empresa;

import java.io.IOException;
import java.net.URL;

import com.empresa.Entidadas.Cliente;
import com.empresa.Entidadas.Servicio;
import com.empresa.Servicios.ServicioCliente;
import com.empresa.Servicios.ServicioMembresia;
import com.empresa.Servicios.ServicioServicio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static Scene primaryScene;
    
    private static final ServicioCliente servicioCliente =
            new ServicioCliente();
    private static final ServicioServicio servicioServicio =
            new ServicioServicio();
    private static final ServicioMembresia servicioMembresia =
            new ServicioMembresia();
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("UCACUE Gym | Gestión de clientes");
        primaryStage.setMinWidth(1040);
        primaryStage.setMinHeight(680);
        showDashboard();
        primaryStage.show();
    }
    private static Cliente selectedClient;

    private static Servicio selectedService;

    public static void showDashboard() {
        showScreen("/com/empresa/view/dashboard.fxml", "UCACUE Gym | Dashboard");
    }

    public static void showServiceList() {
        selectedService = null;
        showScreen("/com/empresa/view/service-list.fxml", "UCACUE Gym | Servicios");
    }

    public static void showMembershipList() {
        showScreen("/com/empresa/view/membership-list.fxml", "UCACUE Gym | Membresías");
    }

    public static void showEditService(Servicio servicio) {
        selectedService = servicio;
        showScreen("/com/empresa/view/service-list.fxml", "UCACUE Gym | Editar servicio");
    }
     
    public static void showClientList() {
        showScreen("/com/empresa/view/client-list.fxml", "UCACUE Gym | Clientes");
    }

    public static void showNewClient() {
        showScreen("/com/empresa/view/client-new.fxml", "UCACUE Gym | Nuevo cliente");
    }

    public static void showClientDetails(Cliente cliente) {
        selectedClient = cliente;
        showScreen("/com/empresa/view/client-details.fxml", "UCACUE Gym | Detalle del cliente");
    }

    public static void showEditClient(Cliente cliente) {
        selectedClient = cliente;
        showScreen("/com/empresa/view/client-edit.fxml", "UCACUE Gym | Editar cliente");
    }
    public static ServicioCliente getServicioCliente() {
        return servicioCliente;
    }

    public static ServicioServicio getServicioServicio() {
        return servicioServicio;
    }

    public static ServicioMembresia getServicioMembresia() {
        return servicioMembresia;
    }

    public static Servicio getSelectedService() {
        return selectedService;
    }
    public static Cliente getSelectedClient() {
        if (selectedClient == null) {
            throw new IllegalStateException("No hay un cliente seleccionado.");
        }
        return selectedClient;
    }

    private static void showScreen(String resourcePath, String title) {
        URL resource = App.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("No se encontró la vista FXML: " + resourcePath);
        }
        try {
            Parent root = FXMLLoader.load(resource);
            if (primaryScene == null) {
                primaryScene = new Scene(root, 1180, 760);
                URL stylesheet = App.class.getResource("/com/empresa/styles.css");
                if (stylesheet != null) {
                    primaryScene.getStylesheets().add(stylesheet.toExternalForm());
                }
                primaryStage.setScene(primaryScene);
            } else {
                primaryScene.setRoot(root);
            }
            primaryStage.setTitle(title);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar la vista FXML: " + resourcePath, exception);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
