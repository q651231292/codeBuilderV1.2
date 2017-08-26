package com.rgy.codebuilder.controller;

import com.rgy.codebuilder.App;
import com.rgy.codebuilder.service.TempService;
import com.rgy.codebuilder.util.AlertTool;
import com.rgy.codebuilder.util.LogTool;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4.
 */
public class TempAddCtrl {

    @FXML
    public ScrollPane tempData;
    @FXML
    public VBox vbox;
    @FXML
    TextField tempName;
    @FXML
    Button saveBtn;
    @FXML
    TextField tempId;

    private App app;
    private TempService tempService;

    public void setApp(App app,String...ids) {
        this.app = app;
        if(ids.length>0){
            String id = ids[0];
            initialize(id);
        }
    }
    @FXML
    private void initialize(String id) {
        tempService = new TempService();
        Map<String, String> temp = tempService.queryTemp(id);
        tempId.setText(temp.get("TEMP_ID"));
        tempName.setText(temp.get("TEMP_NAME"));
        List<Map<String, String>> tempDatas = tempService.queryTempDatas(id);
        for (int i = 0; i < tempDatas.size(); i++) {
            Map<String, String> tempData = tempDatas.get(i);
            appendTempData(tempData.get("LABEL"),tempData.get("VALUE"),tempData.get("TEMP_DATA_ID"));
        }
        //修改保存按钮的text和点击事件
        saveBtn.setOnAction((evt)->updataTemp());

    }

    public void updataTemp() {

        String tempId = this.tempId.getText();
        String tempName = this.tempName.getText();


        ObservableList<Node> children = vbox.getChildren();
        tempService = new TempService();
        Map<String, List<String>> labelAndValue = tempService.getLabelAndValue(children);
        List<String> labels = labelAndValue.get("labels");
        List<String> values = labelAndValue.get("values");
        List<String> tempDataIds = labelAndValue.get("tempDataIds");


        boolean isSuccess =  tempService.updateTempAndDatas(tempId,tempName,tempDataIds,labels,values);
        if(isSuccess){
            AlertTool.show("成功");
            app.showTempManeger(app);
        }else{
            AlertTool.show("失败");
        }


    }
    public void addTempChil(ActionEvent actionEvent) {
        appendTempData(null,null,null);

    }
    public void appendTempData(String labTxt,String valTxt,String idTxt) {

        HBox hbox = new HBox();
        hbox.setSpacing(8);

        TextField id = new TextField();
        if(idTxt!=null) id.setText(idTxt);
        id.setManaged(false);


        TextField label = new TextField();
        label.setId("labels");
        if(labTxt!=null)label.setText(labTxt);

        TextArea value = new TextArea();
        value.setId("values");
        if(valTxt!=null)value.setText(valTxt);

        Button delBtn = new Button("移除");
        delBtn.setOnAction(event -> removeTempData(delBtn,idTxt));

        hbox.getChildren().addAll(id,label, value,delBtn);
        vbox.getChildren().addAll(hbox);
    }

    private void removeTempData(Button delBtn,String id) {
        HBox hbox = (HBox)delBtn.getParent();
        VBox vbox = (VBox) hbox.getParent();
        vbox.getChildren().remove(hbox);
        boolean isSuccess = tempService.deleteTempData(id);
        if(!isSuccess) AlertTool.show("移除失败");
    }

    public void addTemp(ActionEvent actionEvent)  {
        ObservableList<Node> children = vbox.getChildren();
        tempService = new TempService();
        Map<String, List<String>> labelAndValue = tempService.getLabelAndValue(children);
        List<String> labels = labelAndValue.get("labels");
        List<String> values = labelAndValue.get("values");
        String tempNameVal = tempName.getText();
        boolean isSuccess = tempService.saveTempAndData(tempNameVal,labels,values);
        if(isSuccess){
            AlertTool.show("成功");
            app.showTempManeger(app);
        }else{
            AlertTool.show("失败");
        }
    }

    public void back(ActionEvent actionEvent) throws IOException {
        app.showTempManeger(app);
    }

}
