package com.example.ql_khachsan.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class MainController {

    @FXML private TabPane mainTable;

    @FXML private PhongController phongViewController;
    @FXML private KhachHangController khachHangViewController;
    @FXML private BaoCaoController baoCaoViewController;
    @FXML private LichSuHoaDonController lichSuHoaDonViewController;

    @FXML public void initialize(){
        System.out.println("Tải MainView");

        if(phongViewController != null){
            System.out.println("Phòng đã có");
        }
        if (khachHangViewController != null){
            System.out.println("Khách hàng đã có");
        }
        if (baoCaoViewController != null){
            System.out.println("Báo cáo đã có");
        }
        if (lichSuHoaDonViewController != null){
            System.out.println("Lịch sử hóa đơn đã có");
        }
    }

}
