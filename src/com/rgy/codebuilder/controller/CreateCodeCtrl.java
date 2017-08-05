package com.rgy.codebuilder.controller;

import com.rgy.codebuilder.App;
import com.rgy.codebuilder.model.Temp;
import com.rgy.codebuilder.service.AppService;
import com.rgy.codebuilder.service.TempService;
import com.rgy.codebuilder.util.AlertTool;
import com.rgy.codebuilder.util.FileTool;
import com.rgy.codebuilder.util.Jdbc;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.nashorn.internal.objects.Global;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/8/4.
 */
public class CreateCodeCtrl {

    public ChoiceBox<Temp> tempList;
    public VBox vbox;

    private App app;
    private TempService tempService;
    private AppService appService;
    private TextField pathVal;
    private TextField filePrefixVal;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    private void initialize() {
        tempService = new TempService();
        ObservableList<Temp> temps = tempService.queryTemps();
        tempList.setItems(temps);
        tempList.setOnAction(temp->selectTemp(temp));
    }
    public void TempManeger(ActionEvent actionEvent) throws IOException {
        app.showTempManeger(app);

    }

    public void showFileDirSelect(){
        Stage stage = app.getStage();
        DirectoryChooser dirChiooser = new DirectoryChooser();
        String cwd = System.getProperty("user.dir");
        File file = new File(cwd);
        dirChiooser.setInitialDirectory(file);
        File chosenDir = dirChiooser.showDialog(stage);
        if (chosenDir != null) {
            String outPath = chosenDir.getAbsolutePath();
            pathVal.setText(outPath);
        } else {
            AlertTool.show("未选择文件夹");
        }


    }
    public void selectTemp(ActionEvent evt) {

        clearVbox(vbox);

        SingleSelectionModel<Temp> selectionModel = tempList.getSelectionModel();
        Temp temp = selectionModel.getSelectedItem();
        String tempId = temp.getTempId();
        tempService = new TempService();
        List<Map<String, String>> tempDatas = tempService.queryTempDatas(tempId);
        Set<String> marks  = tempService.getValueMarks(tempDatas);

        HBox hbox = new HBox();
        Label label = new Label();
        label.setText("磁盘路径");
        label.setPrefWidth(150);
        pathVal = new TextField();
        pathVal.setPrefWidth(300);
        pathVal.setOnMouseClicked(e->showFileDirSelect());
        hbox.getChildren().addAll(label, pathVal);
        vbox.getChildren().add(hbox);

        hbox = new HBox();
        label = new Label();
        label.setText("文件前缀");
        label.setPrefWidth(150);
        filePrefixVal = new TextField();
        filePrefixVal.setPrefWidth(300);
        hbox.getChildren().addAll(label, filePrefixVal);
        vbox.getChildren().add(hbox);

        Iterator<String> iterator = marks.iterator();
        while (iterator.hasNext()){
            String mark = iterator.next();
            hbox = new HBox();
            label = new Label();
            label.setText(mark);
            label.setPrefWidth(150);
            TextField txt = new TextField();
            txt.setPrefWidth(300);
            hbox.getChildren().addAll(label, txt);
            vbox.getChildren().add(hbox);
        }

    }

    private void clearVbox(VBox vbox) {
        ObservableList<Node> vboxChl = vbox.getChildren();
        vbox.getChildren().removeAll(vboxChl);

    }

    public void wirteToDisk(ActionEvent actionEvent) {

        Map<String, List<String>> labelAndValue = getLabelAndValue();
        SingleSelectionModel<Temp> selectionModel = tempList.getSelectionModel();
        Temp temp = selectionModel.getSelectedItem();
        String tempId = temp.getTempId();
        //参数
        List<String> labels = labelAndValue.get("labels");
        List<String> values = labelAndValue.get("values");
        Jdbc dao = new Jdbc();
        List<Map<String, String>> tempDatas = dao.query("select * from temp_data where temp_id='"+tempId+"'");

        List<String> fileDatas = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        //模板数据
        for (int i = 0; i < tempDatas.size(); i++) {
            Map<String, String> tempData = tempDatas.get(i);
            String tempName = fmtTempName(tempData.get("LABEL"));
            String tempValue = tempData.get("VALUE");
            //把参数带入到模板中
            String fileData = getFileData(tempValue,labels,values);
            fileDatas.add(fileData);
            fileNames.add(tempName);
        }
        //处理文件名,Emp(参数名)Action(文件名action->Action)
        System.out.println(fileDatas);

        String outPath = pathVal.getText();
        System.out.println();



        boolean isSuccess = true;
        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            String fileData = fileDatas.get(i);
            isSuccess = FileTool.write(outPath, fileName, fileData);
        }
        if(isSuccess){
            AlertTool.show("成功");
        }else{
            AlertTool.show("失败");
        }

    }
    private String getFileData(String tempValue, List<String> labels, List<String> values) {
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            String value = values.get(i);
            tempValue = tempValue.replaceAll("\\$\\{("+label+")\\}", value);
        }
        return tempValue;

    }
    private String fmtTempName(String tempName) {
        String substring = tempName.substring(0, 1);
        if(substring!=null){
            String first = substring.toUpperCase();
            String end = tempName.substring(1);
            String sum = filePrefixVal.getText()+first+end ;
            return sum;
        }else{
            return null;
        }

    }
    public Map<String,List<String>> getLabelAndValue(){
        Map<String,List<String>> result = new HashMap<>();

        //获取页面上动态添加的值
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        ObservableList<Node> children = vbox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            HBox hbox = (HBox)children.get(i);
            for (int j = 0; j < hbox.getChildren().size(); j++) {
                ObservableList<Node> chil = hbox.getChildren();
                if(j==0){
                    Label label = (Label)chil.get(j);
                    labels.add(label.getText());

                }
                if(j==1){
                    TextField value = (TextField)chil.get(j);
                    values.add(value.getText());
                }

            }
        }
        result.put("labels", labels);
        result.put("values", values);
        return result;

    }
}
