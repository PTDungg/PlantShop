package com.example.plantshop.data.Utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {
    public static String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " VNĐ";
    }
} 