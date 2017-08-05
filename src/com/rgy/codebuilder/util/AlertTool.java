package com.rgy.codebuilder.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * Created by Administrator on 2017/8/4.
 */
public class AlertTool {

    public static void show(String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("消息提示框");
        alert.setHeaderText(text);
        alert.setContentText("操作成功!");
        alert.showAndWait();
    }
}
