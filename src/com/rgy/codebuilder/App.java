package com.rgy.codebuilder;

import com.rgy.codebuilder.controller.CreateCodeCtrl;
import com.rgy.codebuilder.controller.TempAddCtrl;
import com.rgy.codebuilder.controller.TempManegerCtrl;
import com.rgy.codebuilder.service.AppService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private Stage stage;
    private AppService appService;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        showCreateCode();
        appService = new AppService();
        appService.tableIsNotExists();
        stage.getIcons().add(new Image("file:resource/img/chuizi.png"));
        stage.setTitle("代码生成器");
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void showCreateCode() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/CreateCode.fxml"));
        BorderPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        CreateCodeCtrl controller = loader.getController();
        controller.setApp(this);
    }

    public void showTempManeger(App app) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/TempManeger.fxml"));
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        TempManegerCtrl controller = loader.getController();
        controller.setApp(this);
    }

    public void showTempAdd(App app,String...ids) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("view/TempAdd.fxml"));
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        TempAddCtrl controller = loader.getController();
        controller.setApp(this,ids);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
