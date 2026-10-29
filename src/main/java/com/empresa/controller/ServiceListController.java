package com.empresa.controller;

import com.empresa.App;
import com.empresa.Entidadas.Servicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ServiceListController {
    @FXML private TableView<Servicio> servicesTable;
    @FXML private TableColumn<Servicio, String> codeColumn;
    @FXML private TableColumn<Servicio, String> nameColumn;
    @FXML private TableColumn<Servicio, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> statusFilterBox;
    @FXML private ComboBox<String> statusBox;
    @FXML private Label resultCountLabel;
    @FXML private Label validationLabel;
    @FXML private Label formTitleLabel;
    @FXML private Button saveButton;

    private FilteredList<Servicio> filteredServices;
    private ObservableList<Servicio> allServices;
    private Servicio editingService;

    @FXML
    private void initialize() {
        servicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getCodigo()));
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(cell.getValue().getNombre()));
        statusColumn.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(
                cell.getValue().getEstado() == 1 ? "Activo" : "Inactivo"));

        statusFilterBox.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        statusFilterBox.setValue("Todos");
        statusBox.setItems(FXCollections.observableArrayList("Activo", "Inactivo"));
        statusBox.setValue("Activo");

        allServices = FXCollections.observableArrayList(App.getServicioServicio().listar());
        filteredServices = new FilteredList<>(allServices, service -> true);
        servicesTable.setItems(filteredServices);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        statusFilterBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        filteredServices.addListener((javafx.collections.ListChangeListener<Servicio>) change -> updateCount());
        updateCount();

        Servicio selected = App.getSelectedService();
        if (selected != null) {
            Servicio current = App.getServicioServicio().buscarPorCodigo(selected.getCodigo());
            if (current != null) {
                servicesTable.getSelectionModel().select(current);
                populateForm(current);
            }
        }
    }

    private void applyFilter() {
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String status = statusFilterBox.getValue();
        filteredServices.setPredicate(service -> {
            boolean matchesQuery = query.isEmpty()
                    || service.getCodigo().toLowerCase().contains(query)
                    || service.getNombre().toLowerCase().contains(query);
            boolean matchesStatus = "Activos".equals(status) ? service.getEstado() == 1
                    : "Inactivos".equals(status) ? service.getEstado() != 1 : true;
            return matchesQuery && matchesStatus;
        });
        updateCount();
    }

    private void updateCount() {
        resultCountLabel.setText(filteredServices.size() + (filteredServices.size() == 1 ? " servicio" : " servicios"));
    }

    @FXML private void onDashboard() { App.showDashboard(); }
    @FXML private void onClients() { App.showClientList(); }
    @FXML private void onMemberships() { App.showMembershipList(); }

    @FXML
    private void onNewService() {
        clearForm();
        codeField.requestFocus();
    }

    @FXML
    private void onEditService() {
        Servicio selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(Alert.AlertType.WARNING, "Selecciona un servicio", "Selecciona un registro de la tabla para editarlo.");
            return;
        }
        populateForm(selected);
    }

    private void populateForm(Servicio service) {
        editingService = service;
        codeField.setText(service.getCodigo());
        codeField.setDisable(true);
        nameField.setText(service.getNombre());
        statusBox.setValue(service.getEstado() == 1 ? "Activo" : "Inactivo");
        formTitleLabel.setText("Editar servicio");
        saveButton.setText("Guardar cambios");
        validationLabel.setText("");
    }

    @FXML
    private void onSaveService() {
        String code = codeField.getText() == null ? "" : codeField.getText().trim();
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (code.isEmpty() || name.isEmpty()) {
            validationLabel.setText("El código y el nombre son obligatorios.");
            return;
        }
        if (code.length() > 20 || name.length() > 100) {
            validationLabel.setText("El código admite hasta 20 caracteres y el nombre hasta 100.");
            return;
        }

        Servicio service = new Servicio(code, name, "Activo".equals(statusBox.getValue()) ? 1 : 0);
        boolean saved = editingService == null
                ? App.getServicioServicio().insertar(service)
                : App.getServicioServicio().actualizar(service);
        if (!saved) {
            validationLabel.setText("No se pudo guardar. Verifica la conexión y que el código no esté repetido.");
            return;
        }
        refreshServices();
        clearForm();
    }

    @FXML
    private void onClearForm() {
        clearForm();
    }

    private void clearForm() {
        editingService = null;
        codeField.clear();
        codeField.setDisable(false);
        nameField.clear();
        statusBox.setValue("Activo");
        formTitleLabel.setText("Nuevo servicio");
        saveButton.setText("Guardar servicio");
        validationLabel.setText("");
        servicesTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onDeleteService() {
        Servicio selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(Alert.AlertType.WARNING, "Selecciona un servicio", "Selecciona un registro de la tabla para eliminarlo.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar servicio");
        confirm.setHeaderText("¿Eliminar «" + selected.getNombre() + "»?");
        confirm.setContentText("El registro se eliminará de forma permanente.");
        if (confirm.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            if (App.getServicioServicio().eliminar(selected.getCodigo())) {
                refreshServices();
                clearForm();
            } else {
                showMessage(Alert.AlertType.ERROR, "No se pudo eliminar", "Verifica si el servicio tiene registros relacionados.");
            }
        }
    }

    private void refreshServices() {
        allServices.setAll(App.getServicioServicio().listar());
        applyFilter();
    }

    private void showMessage(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
