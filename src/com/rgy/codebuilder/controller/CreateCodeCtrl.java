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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/4.
 */
public class CreateCodeCtrl {

    //显示自定义的模板
//    public ChoiceBox<Temp> tempList;
    public ComboBox<Temp> tempList;
    //用于显示模板的标签和输入框

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
        //查询模板,如果存在模板数据,则在下拉框上显示模板列表
        tempService = new TempService();
        ObservableList<Temp> temps = tempService.queryTemps();
        tempList.setItems(temps);
        tempList.setOnAction(temp->selectTemp(temp));
    }
    public void TempManeger(ActionEvent actionEvent) throws IOException {
        app.showTempManeger(app);

    }

    //点击磁盘路径,弹出文件选择框,用于选择文件路径
    public void showFileDirSelect(){
        Stage stage = app.getStage();
        DirectoryChooser dirChiooser = new DirectoryChooser();
        String cwd = System.getProperty("user.dir");
        File file = new File(cwd);
        dirChiooser.setInitialDirectory(file);
        File chosenDir = dirChiooser.showDialog(stage);
        //如果选择了磁盘的某个路径,则把值赋给路径的输入框
        if (chosenDir != null) {
            String outPath = chosenDir.getAbsolutePath();
            pathVal.setText(outPath);
        }
    }

    //当从下拉框中选择模板后,就把此模板的标签显示到界面上
    //除了标签,还得创建一些输入框,用于输入模板标签的数据
    public void selectTemp(ActionEvent evt) {

        //清空容器,否会不断的追加
        clearVbox(vbox);

        //获取模板列表中,被选中的模板,拿到它的ID,然后查询它的模板数据,从模板数据中提取到标签
        //提取方式是用正则表达式提取
        SingleSelectionModel<Temp> selectionModel = tempList.getSelectionModel();
        Temp temp = selectionModel.getSelectedItem();
        String tempId = temp.getTempId();
        tempService = new TempService();
        List<Map<String, String>> tempDatas = tempService.queryTempDatas(tempId);
        Set<String> marks  = tempService.getValueMarks(tempDatas);//提取模板标签

        //创建公共的字段,公共字段是磁盘路径和文件前缀
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

        //分割线,用于分割公共字段和自定义字段
        hbox = new HBox();
        Separator hr = new Separator();
        hr.setPrefWidth(450);
        hbox.getChildren().addAll(hr);
        vbox.getChildren().add(hbox);

        //迭代自定义字段,把自定义字段添加到页面上
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

    //清空vbox,否则会不断的追加字段
    private void clearVbox(VBox vbox) {
        ObservableList<Node> vboxChl = vbox.getChildren();
        vbox.getChildren().removeAll(vboxChl);

    }

    //点击生成,把生成的代码写入到目标磁盘中
    public void wirteToDisk() {

        //得到页面上的标签和值
        Map<String, List<String>> labelAndValue = getLabelAndValue();

        //得到模板ID
        SingleSelectionModel<Temp> selectionModel = tempList.getSelectionModel();
        Temp temp = selectionModel.getSelectedItem();
        String tempId = temp.getTempId();

        //得到页面上的标签,得到页面上的值,为生成代码做准备
        List<String> labels = labelAndValue.get("labels");
        List<String> values = labelAndValue.get("values");
        //得到模板数据
        tempService = new TempService();
        List<Map<String, String>> tempDatas = tempService.queryTempDatas(tempId);

        //把页面上的标签的值,注入到预先设计好的模板中
        List<String> fileDatas = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < tempDatas.size(); i++) {
            Map<String, String> tempData = tempDatas.get(i);
            String tempName = fmtTempName(tempData.get("LABEL"));
            String tempValue = tempData.get("VALUE");
            //把页面上的输入的值,放入模板中
            String fileData = getFileData(tempValue,labels,values);
            fileDatas.add(fileData);
            fileNames.add(tempName);
        }

        //输出已经替换好的模板,直接输出到磁盘上,如果成功,生成代码操作就完毕了
        boolean isSuccess = true;
        for (int i = 0; i < fileNames.size(); i++) {
            String outPath = pathVal.getText();
            String fileName = fileNames.get(i);
            String fileData = fileDatas.get(i);
            //得到目标文件夹
            outPath = getFileOutPath(fileData,outPath);
            //生成代码到目标文件夹
            isSuccess = FileTool.write(outPath, fileName, fileData);
        }
        if(isSuccess){
            AlertTool.show("成功");
        }else{
            AlertTool.show("失败");
        }

    }

    //得到输出路径
    private String getFileOutPath(String fileData, String outPath) {
        String regex = "package(.*?);.*?\n.*?";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(fileData); // 获取 matcher 对象
        while(m.find()) {
            String packageName = m.group(1);
            outPath = outPath+toFileSysPath(packageName);//把.转换成/

        }
        return outPath;
    }

    //把包名转换成操作系统的路径
    private String toFileSysPath(String packageName) {
        packageName = packageName.trim();
        packageName = packageName.replace(".",File.separator);
        packageName = File.separator+packageName;
        return packageName;
    }


    //把页面上输入的值,放入模板中
    private String getFileData(String tempValue, List<String> labels, List<String> values) {
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            String value = values.get(i);
            //使用正则表达式替换
            tempValue = tempValue.replaceAll("\\$\\{("+label+")\\}", value);
        }
        return tempValue;

    }

    //格式化模板名称
    private String fmtTempName(String tempName) {
        if(tempName!=null&&!"".equals(tempName)){
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
        return null;
    }

    //得到页面上的标签和值
    public Map<String,List<String>> getLabelAndValue(){
        Map<String,List<String>> result = new HashMap<>();

        //获取页面上动态添加的值
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        ObservableList<Node> children = vbox.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if(i!=2){
                HBox hbox = (HBox)children.get(i);
                for (int j = 0; j < hbox.getChildren().size(); j++) {
                    ObservableList<Node> chil = hbox.getChildren();
                    //得到标签
                    if(j==0){
                        Label label = (Label)chil.get(j);
                        labels.add(label.getText());

                    }
                    //得到值
                    if(j==1){
                        TextField value = (TextField)chil.get(j);
                        values.add(value.getText());
                    }

                }
            }

        }
        result.put("labels", labels);
        result.put("values", values);
        return result;

    }

    public void About() {
        String txt = "" +
                "作用:自定义模板,自定义标记" +
                "\r\n" +
                "作者:任冠宇" +
                "";
        AlertTool.show(txt,"代码生成器");
    }

    public void help() {
        String txt = "联系作者,QQ651231292 ";
        AlertTool.show(txt,"代码生成器");
    }
}
