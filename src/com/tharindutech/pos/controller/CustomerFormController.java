package com.tharindutech.pos.controller;

import com.tharindutech.pos.db.DataBase;
import com.tharindutech.pos.model.Customer;
import com.tharindutech.pos.view.tm.CustomerTM;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CustomerFormController {


    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;

    public  void initialize(){
        searchCustomers();
    }

    private void searchCustomers() {
        ObservableList<CustomerTM> observableList= new   {
        }

    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        Customer c= new Customer(txtId.getText(),txtName.getText(),txtAddress.getText(),Double.parseDouble(txtSalary.getText()));
        boolean isSaved=DataBase.customerTable.add(c);
        if (isSaved){
            new Alert(Alert.AlertType.INFORMATION,"Customer Saved Successfully").show();
        }
        else {
            new Alert(Alert.AlertType.WARNING,"Try Again !").show();
        }


    }
}
