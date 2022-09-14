package com.tharindutech.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.tharindutech.pos.db.DataBase;
import com.tharindutech.pos.model.Customer;
import com.tharindutech.pos.view.tm.CustomerTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class CustomerFormController {


    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colSalary;
    public TableColumn colOptions;
    public JFXButton btnSaveCustomer;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("btn"));
        searchCustomers();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(CustomerTM tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtSalary.setText(String.valueOf(tm.getSalary()));
    }

    private void searchCustomers() {
        ObservableList<CustomerTM> tmList = FXCollections.observableArrayList();
        for (Customer c : DataBase.customerTable) {
            Button btn = new Button("DELETE");
            tmList.add(new CustomerTM(c.getId(), c.getName(), c.getAddress(), c.getSalary(), btn));

            btn.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure that you want to delte?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.get() == ButtonType.YES) {
                    boolean isDeleted = DataBase.customerTable.remove(c);
                    if (isDeleted) {
                        searchCustomers();
                        new Alert(Alert.AlertType.INFORMATION, "Customer Deleted Successfully").show();
                    } else {
                        new Alert(Alert.AlertType.WARNING, "Try Again !").show();
                    }
                }
            });
        }

        tblCustomer.setItems(tmList);
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        Customer c = new Customer(txtId.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText()));

        if (btnSaveCustomer.getText().equalsIgnoreCase("Save Customer")) {
            boolean isSaved = DataBase.customerTable.add(c);
            if (isSaved) {
                searchCustomers();
                clearFields();
                new Alert(Alert.AlertType.INFORMATION, "Customer Saved Successfully").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Try Again !").show();
            }
        } else {
            // update
        }


    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();


    }
}
