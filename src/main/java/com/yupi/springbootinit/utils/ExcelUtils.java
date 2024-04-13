package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public class ExcelUtils {
    public static String excelToCsv(MultipartFile multipartFile) {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        //读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误",e);
            throw new RuntimeException(e);
        }
        if(CollUtil.isEmpty(list)){
            return "";
        }
        //转换为csv
        LinkedHashMap<Integer, String> headerMap =(LinkedHashMap<Integer,String>) list.get(0);
        List<String> haeaderList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        System.out.println(StringUtils.join(haeaderList,","));
        LinkedHashMap<Integer,String> dataMap;
        for(int i = 1;i < list.size();i++){
            dataMap = (LinkedHashMap<Integer, String>) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            System.out.println(StringUtils.join(dataList,","));
        }
        String result = list.toString();

        //读取标表头
        System.out.println(result);
        return result;
    }

    public static void main(String[] args) {
        excelToCsv(null);
    }
}
