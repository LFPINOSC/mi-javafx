package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Cliente;
import com.empresa.Servicios.ServicioCliente;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClientListController {
    @FXML private TableView<Cliente> clientsTable;
    @FXML private TableColumn<Cliente, String> idColumn;
    @FXML private TableColumn<Cliente, String> identificationNumberColumn;
    @FXML private TableColumn<Cliente, String> firstNameColumn;
    @FXML private TableColumn<Cliente, String> lastNameColumn;
    @FXML private TableColumn<Cliente, String> emailColumn;
    @FXML private TableColumn<Cliente, String> phoneColumn;
    @FXML private TableColumn<Cliente, String> heightColumn;
    @FXML private TableColumn<Cliente, String> sexColumn;
    @FXML private TextField searchField;

    @FXML
    private void initialize() {
        clientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getCodigo()));
        identificationNumberColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(
                cell.getValue().getCedula()));
        firstNameColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(
                cell.getValue().getNombre()));
        lastNameColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(
                cell.getValue().getApellido()));
        emailColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getCorreo()));
        phoneColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getTelefono()));
        heightColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getTallaString()));
        sexColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getSexo()));

        FilteredList<Cliente> clients = new FilteredList<>(
                FXCollections.observableArrayList(App.getServicioCliente().listar()), client -> true);
        clientsTable.setItems(clients);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String query = newValue == null ? "" : newValue.trim().toLowerCase();
            clients.setPredicate(client -> query.isEmpty()
                    || client.getCodigo().toLowerCase().contains(query)
                    || client.getCedula().toLowerCase().contains(query)
                    || client.getNombre().toLowerCase().contains(query)
                    || client.getCorreo().toLowerCase().contains(query)
                    || client.getTelefono().toLowerCase().contains(query));
        });
    }

    @FXML
    private void onNewClient() {
        App.showNewClient();
    }

    @FXML
    private void onViewDetails() {
        Cliente client = clientsTable.getSelectionModel().getSelectedItem();
        if (client == null) {
            showSelectionWarning();
            return;
        }
        App.showClientDetails(client);
    }

    @FXML
    private void onEditClient() {
        Cliente client = clientsTable.getSelectionModel().getSelectedItem();
        if (client == null) {
            showSelectionWarning();
            return;
        }
        App.showEditClient(client);
    }

    @FXML
    private void onDeleteClient() {
        Cliente client = clientsTable.getSelectionModel().getSelectedItem();
        if (client == null) {
            showSelectionWarning();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Eliminar cliente");
        confirmation.setHeaderText("¿Eliminar a " + client.getNombre() + " " + client.getApellido() + "?");
        confirmation.setContentText("Esta acción quitará al cliente del directorio.");
        if (confirmation.showAndWait().filter(javafx.scene.control.ButtonType.OK::equals).isPresent()) {
            if (App.getServicioCliente().eliminar(client.getCodigo())) {
                App.showClientList();
            } else {
                showDeleteError();
            }
        }
    }

    private void showDeleteError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No se pudo eliminar el cliente");
        alert.setHeaderText("El cliente no fue eliminado");
        alert.setContentText("Intenta nuevamente. Si el problema continúa, verifica si el cliente tiene información relacionada.");
        alert.showAndWait();
    }

    private void showSelectionWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selecciona un cliente");
        alert.setHeaderText(null);
        alert.setContentText("Selecciona un cliente de la tabla para continuar.");
        alert.showAndWait();
    }
}
