package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Cliente;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class ClientEditController {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private Cliente cliente;

    @FXML private TextField identificationNumberField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField heightMetersField;
    @FXML private ToggleButton maleButton;
    @FXML private ToggleButton femaleButton;
    @FXML private Label clientIdLabel;
    @FXML private Label validationLabel;

    @FXML
    private void initialize() {
        cliente = App.getSelectedClient();
        clientIdLabel.setText(cliente.getCodigo());
        identificationNumberField.setText(cliente.getCedula());
        firstNameField.setText(cliente.getNombre());
        lastNameField.setText(cliente.getApellido());
        phoneField.setText(cliente.getTelefono());
        emailField.setText(cliente.getCorreo());
        //heightMetersField.setText(cliente.getTalla().stripTrailingZeros().toPlainString());
        ToggleGroup sexGroup = new ToggleGroup();
        maleButton.setToggleGroup(sexGroup);
        femaleButton.setToggleGroup(sexGroup);
        (cliente.getSexo().equals("MASCULINO") ? maleButton : femaleButton).setSelected(true);
    }

    @FXML
    private void onSave() {
        /*if (!isValid()) {
            return;
        }
        Cliente updated = new Cliente(cliente.getCodigo(), identificationNumberField.getText().trim(),
                firstNameField.getText().trim(), lastNameField.getText().trim(),
                phoneField.getText().trim(), emailField.getText().trim(),
                new BigDecimal(heightMetersField.getText().trim().replace(',', '.')),
                maleButton.isSelected() ? "MASCULINO" : "FEMENINO");
        try {
            App.getClientRepository().update(updated);
            App.showClientDetails(updated);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No se pudo actualizar el cliente");
            alert.setHeaderText("Revisa los datos ingresados");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
        }*/
    }

    @FXML
    private void onCancel() {
        App.showClientDetails(cliente);
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
}
