package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Membresia;
import com.empresa.Entidadas.Servicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MembershipListController {
    @FXML private TableView<Membresia> membershipsTable;
    @FXML private TableColumn<Membresia, String> codeColumn;
    @FXML private TableColumn<Membresia, String> nameColumn;
    @FXML private TableColumn<Membresia, String> valueColumn;
    @FXML private TableColumn<Membresia, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField valueField;
    @FXML private ComboBox<String> statusFilterBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private VBox serviceOptionsBox;
    @FXML private Label resultCountLabel;
    @FXML private Label selectedServicesLabel;
    @FXML private Label formTitleLabel;
    @FXML private Label validationLabel;
    @FXML private Button saveButton;

    private ObservableList<Membresia> allMemberships;
    private FilteredList<Membresia> filteredMemberships;
    private final Map<String, CheckBox> serviceCheckBoxes = new HashMap<>();
    private Membresia editingMembership;

    @FXML
    private void initialize() {
        membershipsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getCodigo()));
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getNombre()));
        valueColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(formatValue(cell.getValue().getValor())));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(
                cell.getValue().getEstado() == 1 ? "Activa" : "Inactiva"));

        statusFilterBox.setItems(FXCollections.observableArrayList("Todas", "Activas", "Inactivas"));
        statusFilterBox.setValue("Todas");
        statusBox.setItems(FXCollections.observableArrayList("Activa (1)", "Inactiva (0)"));
        statusBox.setValue("Activa (1)");
        codeField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 20 ? change : null));
        nameField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 100 ? change : null));

        allMemberships = FXCollections.observableArrayList(App.getServicioMembresia().listar());
        filteredMemberships = new FilteredList<>(allMemberships, membership -> true);
        membershipsTable.setItems(filteredMemberships);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        statusFilterBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        filteredMemberships.addListener((javafx.collections.ListChangeListener<Membresia>) change -> updateCount());
        buildServiceOptions(App.getServicioServicio().listar());
        updateCount();
    }

    private void buildServiceOptions(List<Servicio> services) {
        serviceOptionsBox.getChildren().clear();
        serviceCheckBoxes.clear();
        if (services.isEmpty()) {
            serviceOptionsBox.getChildren().add(new Label("No hay servicios registrados. Crea servicios para vincularlos."));
            updateSelectedServicesCount();
            return;
        }
        for (Servicio service : services) {
            String state = service.getEstado() == 1 ? "Activo" : "Inactivo";
            CheckBox checkBox = new CheckBox(service.getNombre() + "  ·  " + service.getCodigo() + "  ·  " + state);
            checkBox.setWrapText(true);
            checkBox.getStyleClass().add("membership-service-option");
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateSelectedServicesCount());
            serviceCheckBoxes.put(service.getCodigo(), checkBox);
            serviceOptionsBox.getChildren().add(checkBox);
        }
        updateSelectedServicesCount();
    }

    private void applyFilter() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String state = statusFilterBox.getValue();
        filteredMemberships.setPredicate(membership -> {
            boolean matchesQuery = query.isEmpty()
                    || membership.getCodigo().toLowerCase(Locale.ROOT).contains(query)
                    || membership.getNombre().toLowerCase(Locale.ROOT).contains(query);
            boolean matchesState = "Activas".equals(state) ? membership.getEstado() == 1
                    : "Inactivas".equals(state) ? membership.getEstado() == 0 : true;
            return matchesQuery && matchesState;
        });
        updateCount();
    }

    private void updateCount() {
        resultCountLabel.setText(filteredMemberships.size()
                + (filteredMemberships.size() == 1 ? " plan" : " planes"));
    }

    private void updateSelectedServicesCount() {
        long selected = serviceCheckBoxes.values().stream().filter(CheckBox::isSelected).count();
        selectedServicesLabel.setText(selected + (selected == 1 ? " seleccionado" : " seleccionados"));
    }

    @FXML private void onDashboard() { App.showDashboard(); }
    @FXML private void onClients() { App.showClientList(); }
    @FXML private void onServices() { App.showServiceList(); }

    @FXML
    private void onNewMembership() {
        clearForm();
        codeField.requestFocus();
    }

    @FXML
    private void onEditMembership() {
        Membresia selected = membershipsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(Alert.AlertType.WARNING, "Selecciona una membresía", "Selecciona un plan de la tabla para editarlo.");
            return;
        }
        populateForm(selected);
    }

    private void populateForm(Membresia membership) {
        editingMembership = membership;
        codeField.setText(membership.getCodigo());
        codeField.setDisable(true);
        nameField.setText(membership.getNombre());
        valueField.setText(BigDecimal.valueOf(membership.getValor()).setScale(2).toPlainString());
        statusBox.setValue(membership.getEstado() == 1 ? "Activa (1)" : "Inactiva (0)");
        formTitleLabel.setText("Editar membresía");
        saveButton.setText("Guardar cambios");
        validationLabel.setText("");

        List<String> associatedCodes = App.getServicioMembresia().listarCodigosServicios(membership.getCodigo());
        serviceCheckBoxes.forEach((code, checkBox) -> checkBox.setSelected(associatedCodes.contains(code)));
        membershipsTable.getSelectionModel().select(membership);
    }

    @FXML
    private void onSaveMembership() {
        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        BigDecimal value;
        try {
            value = new BigDecimal(valueField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException exception) {
            validationLabel.setText("Ingresa un arancel válido, por ejemplo 35.00.");
            return;
        }
        if (code.isEmpty() || name.isEmpty()) {
            validationLabel.setText("El código y el nombre del plan son obligatorios.");
            return;
        }
        if (value.signum() < 0 || value.scale() > 2 || value.precision() > 10) {
            validationLabel.setText("El arancel debe ser positivo y tener máximo 10 dígitos y 2 decimales.");
            return;
        }

        int state = "Activa (1)".equals(statusBox.getValue()) ? 1 : 0;
        Membresia membership = new Membresia(code, name, value.doubleValue(), state);
        List<String> serviceCodes = selectedServiceCodes();
        boolean saved = editingMembership == null
                ? App.getServicioMembresia().insertar(membership, serviceCodes)
                : App.getServicioMembresia().actualizar(membership, serviceCodes);
        if (!saved) {
            validationLabel.setText("No se pudo guardar. Revisa que el código sea único y que los servicios existan.");
            return;
        }
        refreshMemberships();
        clearForm();
    }

    private List<String> selectedServiceCodes() {
        List<String> codes = new ArrayList<>();
        serviceCheckBoxes.forEach((code, checkBox) -> {
            if (checkBox.isSelected()) {
                codes.add(code);
            }
        });
        return codes;
    }

    @FXML private void onClearForm() { clearForm(); }

    private void clearForm() {
        editingMembership = null;
        codeField.clear();
        codeField.setDisable(false);
        nameField.clear();
        valueField.clear();
        statusBox.setValue("Activa (1)");
        formTitleLabel.setText("Nueva membresía");
        saveButton.setText("Guardar membresía");
        validationLabel.setText("");
        serviceCheckBoxes.values().forEach(checkBox -> checkBox.setSelected(false));
        membershipsTable.getSelectionModel().clearSelection();
        updateSelectedServicesCount();
    }

    @FXML
    private void onDeleteMembership() {
        Membresia selected = membershipsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(Alert.AlertType.WARNING, "Selecciona una membresía", "Selecciona un plan para darlo de baja o eliminarlo.");
            return;
        }

        ButtonType deactivate = new ButtonType("Desactivar", ButtonBar.ButtonData.OK_DONE);
        ButtonType delete = new ButtonType("Eliminar permanentemente", ButtonBar.ButtonData.OTHER);
        ButtonType cancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Dar de baja o eliminar membresía");
        confirmation.setHeaderText(selected.getCodigo() + " · " + selected.getNombre());
        confirmation.setContentText("Desactivar conserva el plan y sus servicios. Eliminar permanentemente borra también sus vínculos de servicios.");
        confirmation.getButtonTypes().setAll(deactivate, delete, cancel);
        confirmation.showAndWait().ifPresent(choice -> {
            boolean success = false;
            if (choice == deactivate) {
                Membresia inactive = new Membresia(selected.getCodigo(), selected.getNombre(), selected.getValor(), 0);
                success = App.getServicioMembresia().actualizar(inactive,
                        App.getServicioMembresia().listarCodigosServicios(selected.getCodigo()));
            } else if (choice == delete) {
                success = confirmPermanentDelete(selected);
            }
            if (success) {
                refreshMemberships();
                clearForm();
            }
        });
    }

    private boolean confirmPermanentDelete(Membresia membership) {
        Alert finalConfirmation = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK, ButtonType.CANCEL);
        finalConfirmation.setTitle("Confirmar eliminación permanente");
        finalConfirmation.setHeaderText("¿Eliminar " + membership.getCodigo() + "?");
        finalConfirmation.setContentText("Esta acción irreversible también elimina las relaciones de membresia_servicio (ON DELETE CASCADE).");
        return finalConfirmation.showAndWait().filter(ButtonType.OK::equals)
                .map(button -> App.getServicioMembresia().eliminar(membership.getCodigo()))
                .orElse(false);
    }

    private void refreshMemberships() {
        allMemberships.setAll(App.getServicioMembresia().listar());
        applyFilter();
    }

    private String formatValue(double value) {
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        return currency.format(value);
    }

    private void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
