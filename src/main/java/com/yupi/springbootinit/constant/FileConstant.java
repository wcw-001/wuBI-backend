package com.yupi.springbootinit.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件常量
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface FileConstant {

    /**
     * COS 访问地址
     * todo 需替换配置
     */
    String COS_HOST = "https://yupi.icu";
    /**
     * 文件大小 1M
     */
    long FILE_MAX_SIZE = 1 * 1024 * 1024L;

    /**
     * 文件后缀白名单
     */
    List<String> VALID_FILE_SUFFIX= Arrays.asList("xlsx","csv","xls","json");
}
