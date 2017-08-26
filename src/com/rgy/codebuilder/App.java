package com.rgy.codebuilder;

import com.rgy.codebuilder.controller.CreateCodeCtrl;
import com.rgy.codebuilder.controller.TempAddCtrl;
import com.rgy.codebuilder.controller.TempManegerCtrl;
import com.rgy.codebuilder.service.AppService;
import com.rgy.codebuilder.util.LogTool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    //主舞台
    private Stage stage;
    //App服务层
    private AppService appService;

    @Override
    public void start(Stage primaryStage){
        //得到主舞台
        stage = primaryStage;
        //显示创建代码的页面
        showCreateCode();
        //判断是否为第一次启动程序,如果是第一次启动需要初始化数据库
        appService = new AppService();
        appService.tableIsNotExists();
        //设置代码生成器的图标
        stage.getIcons().add(new Image("file:resource/img/chuizi.png"));
        //设置代码生成器的标题
        stage.setTitle("代码生成器");
        //显示舞台
        stage.show();
    }

    //用于在其他类中获取到主舞台
    public Stage getStage() {
        return stage;
    }

    //显示代码生成器页面
    public void showCreateCode()  {
        //读取页面
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/CreateCode.fxml"));
        BorderPane page = null;
        try {
            page = loader.load();
        } catch (IOException e) {
            LogTool.writeException(e);
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        //绑定控制器
        CreateCodeCtrl controller = loader.getController();
        controller.setApp(this);
    }

    //读取模板管理器页面
    public void showTempManeger(App app) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/TempManeger.fxml"));
        AnchorPane page = null;
        try {
            page = loader.load();
        } catch (Exception e) {
            LogTool.writeException(e);
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        //绑定控制器
        TempManegerCtrl controller = loader.getController();
        controller.setApp(this);
    }

    //读取添加模板页面
    public void showTempAdd(App app,String...ids) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/TempAdd.fxml"));
        AnchorPane page = null;
        try {
            page = loader.load();
        } catch (IOException e) {
            LogTool.writeException(e);
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        //绑定控制器
        TempAddCtrl controller = loader.getController();
        controller.setApp(this,ids);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
