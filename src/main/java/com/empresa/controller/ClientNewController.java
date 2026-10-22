package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Cliente;
import com.empresa.Servicios.ServicioCliente;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class ClientNewController {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @FXML private TextField identificationNumberField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField heightMetersField;
    @FXML private ToggleButton maleButton;
    @FXML private ToggleButton femaleButton;
    @FXML private Label validationLabel;

    
    
    @FXML
    private void initialize() {
        ToggleGroup sexGroup = new ToggleGroup();
        maleButton.setToggleGroup(sexGroup);
        femaleButton.setToggleGroup(sexGroup);
    }

    @FXML
    private void onSave() {
        if (!isValid()) {
            return;
        }
        String id = "GYM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        Cliente cliente = new Cliente();
        cliente.setCodigo(id);
        cliente.setCedula(identificationNumberField.getText().trim());
        cliente.setNombre(firstNameField.getText().trim());
        cliente.setApellido(lastNameField.getText().trim());
        cliente.setTelefono(phoneField.getText().trim());
        cliente.setCorreo(emailField.getText().trim());
        cliente.setTalla(Double.parseDouble(heightMetersField.getText().trim().replace(',', '.')));
        cliente.setSexo(maleButton.isSelected() ? "MASCULINO" : "FEMENINO");
        try {
            App.getServicioCliente().insert(cliente);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        App.showClientList();
    }

    private boolean isValid() {
        String message = null;
        if (identificationNumberField.getText().trim().isEmpty()) {
            message = "La cédula es obligatoria.";
        } else if (firstNameField.getText().trim().isEmpty()) {
            message = "Los nombres son obligatorios.";
        } else if (lastNameField.getText().trim().isEmpty()) {
            message = "Los apellidos son obligatorios.";
        } else if (phoneField.getText().trim().isEmpty()) {
            message = "El teléfono es obligatorio.";
        } else if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            message = "Ingresa un correo electrónico válido.";
        } else if (!isValidHeight()) {
            message = "Ingresa una talla válida en metros, por ejemplo 1.75.";
        } else if (!maleButton.isSelected() && !femaleButton.isSelected()) {
            message = "Selecciona el sexo.";
        }
        validationLabel.setText(message == null ? "" : message);
        return message == null;
    }

    private boolean isValidHeight() {
        try {
            return new BigDecimal(heightMetersField.getText().trim().replace(',', '.')).signum() > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No se pudo registrar el cliente");
        alert.setHeaderText("Revisa los datos ingresados");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
