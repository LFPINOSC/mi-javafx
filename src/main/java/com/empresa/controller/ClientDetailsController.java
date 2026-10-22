package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Cliente;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

public class ClientDetailsController {
    @FXML private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label identificationNumberLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label heightLabel;
    @FXML private Label sexLabel;

    @FXML
    private void initialize() {
        for (Label value : List.of(nameLabel, identificationNumberLabel, firstNameLabel,
                lastNameLabel, emailLabel, phoneLabel, heightLabel, sexLabel)) {
            value.setWrapText(true);
            value.setMaxWidth(Double.MAX_VALUE);
        }

        Cliente cliente = App.getSelectedClient();
        idLabel.setText(cliente.getCodigo());
        nameLabel.setText(cliente.getNombre() + " " + cliente.getApellido());
        identificationNumberLabel.setText(cliente.getCedula());
        firstNameLabel.setText(cliente.getNombre());
        lastNameLabel.setText(cliente.getApellido());
        emailLabel.setText(cliente.getCorreo());
        phoneLabel.setText(cliente.getTelefono());
        //heightLabel.setText(cliente.getTalla().stripTrailingZeros().toPlainString() + " m");
        sexLabel.setText(cliente.getSexo());
    }

    @FXML
    private void onEdit() {
        App.showEditClient(App.getSelectedClient());
    }

    @FXML
    private void onDelete() {
        Cliente cliente = App.getSelectedClient();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Eliminar cliente");
        confirmation.setHeaderText("¿Eliminar a " + cliente.getNombre() + " " + cliente.getApellido() + "?");
        confirmation.setContentText("Esta acción quitará al cliente del directorio.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (App.getServicioCliente().eliminar(cliente.getCodigo())) {
                App.showClientList();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("No se pudo eliminar el cliente");
                error.setHeaderText("El cliente no fue eliminado");
                error.setContentText("Intenta nuevamente. Si el problema continúa, verifica si el cliente tiene información relacionada.");
                error.showAndWait();
            }
        }
    }

    @FXML
    private void onBack() {
        App.showClientList();
    }
}
