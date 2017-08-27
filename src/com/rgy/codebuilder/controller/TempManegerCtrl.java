package com.rgy.codebuilder.controller;

import com.rgy.codebuilder.App;
import com.rgy.codebuilder.model.Temp;
import com.rgy.codebuilder.service.TempService;
import com.rgy.codebuilder.util.AlertTool;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

/**
 * Created by Administrator on 2017/8/4.
 */
public class TempManegerCtrl {


    @FXML
    private TableView<Temp> tempList;
    @FXML
    private TableColumn<Temp,String> tempName;
    private App app;
    private TempService tempService;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    private void initialize() {
        tempService = new TempService();
        ObservableList<Temp> temps = tempService.queryTemps();
        tempName.setCellValueFactory(cellData -> cellData.getValue().tempNameProperty());
        tempList.setItems(temps);
        tempList.setOnMouseClicked(event -> {
            int clickCount = event.getClickCount();
            if(clickCount==2){
                updateTempTo();
            }
        });



    }
    public void createTempTo() {
        app.showTempAdd(app);
    }

    public void deleteTemp() {

        Temp t = tempList.getSelectionModel().getSelectedItem();
        String id = t.getTempId();
        tempService = new TempService();
        boolean isSuccess = tempService.deleteTemp(id);
        if(isSuccess){
            AlertTool.show("成功");
            app.showTempManeger(app);
        }else{
            AlertTool.show("失败");
        }
    }

    public void updateTempTo() {
        Temp t = tempList.getSelectionModel().getSelectedItem();
        String tempId = t.getTempId();
        app.showTempAdd(app,tempId);
    }

    public void back() throws IOException {
        app.showCreateCode();
    }
}
