package com.tharindutech.pos.controller;

import com.tharindutech.pos.db.DataBase;
import com.tharindutech.pos.model.Customer;
import com.tharindutech.pos.model.Item;
import com.tharindutech.pos.model.ItemDetails;
import com.tharindutech.pos.model.Order;
import com.tharindutech.pos.view.tm.CartTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class PlaceOrderFormController {
    public TextField txtDate;
    public ComboBox<String> cmbCustomerIds;
    public ComboBox<String> cmbItemCodes;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtQty;
    public TableColumn colCode;
    public TableView tblCart;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;
    public TableColumn colOption;
    public Label lblTotal;
    public TextField txtOrderId;

    public void initialize() {

        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        setDateAndOrderId();
        loadAllCustomerIds();
        loadAllItemCodes();

        cmbCustomerIds.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        setCustomerDetails();
                    }
                });

        cmbItemCodes.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        setItemDetails();
                    }
                });

    }

    private void setItemDetails() {
        for (Item item : DataBase.itemTable) {
            if (item.getCode().equals(cmbItemCodes.getValue())) {
                txtDescription.setText(item.getDescription());
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            }
        }

    }

    private void setCustomerDetails() {
        for (Customer c : DataBase.customerTable) {
            if (c.getId().equals(cmbCustomerIds.getValue())) {
                txtName.setText(c.getName());
                txtAddress.setText(c.getAddress());
                txtSalary.setText(String.valueOf(c.getSalary()));
            }

        }

    }

    private void loadAllItemCodes() {
        for (Item item : DataBase.itemTable) {
            cmbItemCodes.getItems().add(item.getCode());
        }
    }

    private void loadAllCustomerIds() {
        for (Customer c : DataBase.customerTable) {
            cmbCustomerIds.getItems().add(c.getId());
            ;
        }
    }

    private void setDateAndOrderId() {
        /*Date date= new Date();
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
         String d=df.format(date);
         txtDate.setText(d);*/
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }


    ObservableList<CartTM> obList = FXCollections.observableArrayList();

    public void addToCartOnAction(ActionEvent actionEvent) {
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unitPrice * qty;
        Button btn = new Button("Delete");

        int row = isAlreadyExists(cmbItemCodes.getValue());
        if (row == -1) {
            CartTM cartTM = new CartTM(cmbItemCodes.getValue(), txtDescription.getText(), unitPrice, qty, total, btn);
            obList.add(cartTM);
            tblCart.setItems(obList);
        } else {
            int tmpQty = obList.get(row).getQty() + qty;
            double tmpTotal = unitPrice * tmpQty;
            obList.get(row).setQty(tmpQty);
            obList.get(row).setTotal(tmpTotal);
            tblCart.refresh();
        }
        calculateTotal();
        clearFields();
        cmbItemCodes.requestFocus();

        btn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                for (CartTM tm : obList) {
                    obList.remove(tm);
                    calculateTotal();
                    tblCart.refresh();
                    return;
                }
            }
        });
    }


    private int isAlreadyExists(String code) {
        for (int i = 0; i < obList.size(); i++) {
            if (obList.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }


    public void placeOrderOnAction(ActionEvent actionEvent) {
        if (obList.isEmpty()) return;
        ArrayList<ItemDetails> details = new ArrayList<>();
        for (CartTM tm : obList) {
            details.add(new ItemDetails(tm.getCode(), tm.getUnitPrice(), tm.getQty()));
        }

        Order order = new Order(txtOrderId.getText(), new Date(),
                Double.parseDouble(lblTotal.getText()), cmbCustomerIds.getValue(), details);

        DataBase.orderTable.add(order);
        clearAll();
    }

    private void clearAll() {
        obList.clear();
        calculateTotal();

        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();

        clearFields();
        cmbCustomerIds.requestFocus();
    }

    private void calculateTotal() {
        double total = 0.0;
        for (CartTM ctm : obList) {
            total += ctm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    private void clearFields() {
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtQty.clear();
    }

}
