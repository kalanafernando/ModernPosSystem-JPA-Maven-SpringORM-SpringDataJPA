/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.business.custom.ItemBO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.main.AppInitializer;
import lk.ijse.pos.util.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ranjith-suranga
 */
public class ManageItemFormController implements Initializable {

    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtItemCode;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private AnchorPane root;
    @FXML
    private TableView<ItemTM> tblItems;

    private ItemBO itemBO =AppInitializer.ctx.getBean(ItemBO.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tblItems.getColumns().get(0).setStyle("-fx-alignment: center");
        tblItems.getColumns().get(2).setStyle("-fx-alignment: center-right");
        tblItems.getColumns().get(3).setStyle("-fx-alignment: center-right");

        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        btnSave.setDisable(true);
        btnDelete.setDisable(true);


        try {
            List<ItemDTO> allItems = itemBO.getAllItems();
            for (ItemDTO item : allItems) {
                tblItems.getItems().add(new ItemTM(item.getCode(), item.getDescription(),
                        item.getQtyOnHand(), item.getUnitPrice()));
            }
        } catch (Exception e) {
            Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
        }

        tblItems.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM selectedItem) {

                if (selectedItem == null) {
                    // Clear Selection
                    return;
                }

                txtItemCode.setText(selectedItem.getCode());
                txtDescription.setText(selectedItem.getDescription());
                txtUnitPrice.setText(selectedItem.getUnitPrice() + "");
                txtQty.setText(selectedItem.getQtyOnHand() + "");

                txtItemCode.setEditable(false);

                btnSave.setDisable(false);
                btnDelete.setDisable(false);

            }
        });
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        AppInitializer.navigateToHome(root, (Stage) root.getScene().getWindow());
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {

        if (txtItemCode.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Item Code is empty", ButtonType.OK).showAndWait();
            txtItemCode.requestFocus();
            return;
        } else if (txtDescription.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Item Description is empty", ButtonType.OK).showAndWait();
            txtDescription.requestFocus();
            return;
        } else if (txtUnitPrice.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Unit Price is empty", ButtonType.OK).showAndWait();
            txtUnitPrice.requestFocus();
            return;
        } else if (txtQty.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Qty On Hand is empty", ButtonType.OK).showAndWait();
            txtQty.requestFocus();
            return;
        } else if (!isDouble(txtUnitPrice.getText()) || Double.parseDouble(txtUnitPrice.getText()) < 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid Unit Price", ButtonType.OK).showAndWait();
            txtUnitPrice.requestFocus();
            return;
        } else if (!isInt(txtQty.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty", ButtonType.OK).showAndWait();
            txtQty.requestFocus();
            return;
        }

        if (tblItems.getSelectionModel().isEmpty()) {
            // New

            ObservableList<ItemTM> items = tblItems.getItems();
            for (ItemTM itemTM : items) {
                if (itemTM.getCode().equals(txtItemCode.getText())) {
                    new Alert(Alert.AlertType.ERROR, "Duplicate Item Codes are not allowed").showAndWait();
                    txtItemCode.requestFocus();
                    return;
                }
            }


            try {
                itemBO.saveItem(new ItemDTO(txtItemCode.getText(),
                        txtDescription.getText(),
                        Double.parseDouble(txtUnitPrice.getText()),
                        Integer.parseInt(txtQty.getText())));

                ItemTM itemTM = new ItemTM(txtItemCode.getText(), txtDescription.getText(),
                        Integer.parseInt(txtQty.getText()), Double.parseDouble(txtUnitPrice.getText()));

                new Alert(Alert.AlertType.INFORMATION, "Item has been saved successfully", ButtonType.OK).showAndWait();
                tblItems.getItems().add(itemTM);
                tblItems.scrollTo(itemTM);

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save the item, try again", ButtonType.OK).showAndWait();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }


        } else {
            // Update
            try {

                itemBO.updateItem(new ItemDTO(txtItemCode.getText(), txtDescription.getText(), Double.parseDouble(txtUnitPrice.getText()), Integer.parseInt(txtQty.getText())));

                ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
                selectedItem.setDescription(txtDescription.getText());
                selectedItem.setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
                selectedItem.setQtyOnHand(Integer.parseInt(txtQty.getText()));
//                tblItems.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Item has been updated successfully", ButtonType.OK).showAndWait();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to update the item, try again").show();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }

        }

        reset();

    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {

        Alert confirmMsg = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this item?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = confirmMsg.showAndWait();

        if (buttonType.get() == ButtonType.YES) {

            try {
                itemBO.deleteItem(txtItemCode.getText());
                tblItems.getItems().remove(tblItems.getSelectionModel().getSelectedItem());
                reset();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete the item, try again").show();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }


        }

    }

    private void reset() {
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        txtItemCode.requestFocus();
        txtItemCode.setEditable(true);
        btnSave.setDisable(false);
        btnDelete.setDisable(true);
        tblItems.getSelectionModel().clearSelection();
    }

    @FXML
    private void btnAddNew_OnAction(ActionEvent actionEvent) {
        reset();
    }

    private boolean isInt(String number) {
//        try {
//            Integer.parseInt(number);
//            return true;
//        }catch (NumberFormatException ex){
//            return false;
//        }
        char[] chars = number.toCharArray();
        for (char aChar : chars) {
            if (!Character.isDigit(aChar)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
