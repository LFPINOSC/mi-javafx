package com.empresa.controller;

import com.empresa.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label clientCountLabel;
    @FXML private Label serviceCountLabel;
    @FXML private Label totalServiceCountLabel;
    @FXML private Label activeMembershipCountLabel;
    @FXML private Label totalMembershipCountLabel;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        try {
            int clients = App.getServicioCliente().listar().size();
            var services = App.getServicioServicio().listar();
            long activeServices = services.stream().filter(s -> s.getEstado() == 1).count();
            var memberships = App.getServicioMembresia().listar();
            long activeMemberships = memberships.stream().filter(m -> m.getEstado() == 1).count();
            clientCountLabel.setText(String.valueOf(clients));
            serviceCountLabel.setText(String.valueOf(activeServices));
            totalServiceCountLabel.setText(String.valueOf(services.size()));
            activeMembershipCountLabel.setText(String.valueOf(activeMemberships));
            totalMembershipCountLabel.setText("de " + memberships.size() + " planes");
        } catch (RuntimeException exception) {
            statusLabel.setText("No se pudieron cargar los datos. Verifica la conexión a la base de datos.");
        }
    }

    @FXML private void onClients() { App.showClientList(); }
    @FXML private void onServices() { App.showServiceList(); }
    @FXML private void onMemberships() { App.showMembershipList(); }
}
