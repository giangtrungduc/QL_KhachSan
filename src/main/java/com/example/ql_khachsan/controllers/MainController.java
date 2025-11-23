package com.example.ql_khachsan.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML private TabPane mainTable;

    @FXML private PhongController phongViewController;
    @FXML private KhachHangController khachHangViewController;
    @FXML private LichSuHoaDonController BaoCaoHoaDonViewController;
    @FXML private DoanhThuController DoanhThuViewController;

    @FXML public void initialize(){
        System.out.println("Tải MainView");

        if(phongViewController != null){
            System.out.println("Phòng đã có");
        }
        if (khachHangViewController != null){
            System.out.println("Khách hàng đã có");
        }
        if (DoanhThuViewController != null){
            System.out.println("Doanh thu đã có");
        }
        if (BaoCaoHoaDonViewController != null){
            System.out.println("Báo cáo hóa đơn đã có");
        }
    }

}
