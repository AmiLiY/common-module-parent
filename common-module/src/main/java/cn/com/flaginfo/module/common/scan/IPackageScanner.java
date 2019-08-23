package cn.com.flaginfo.module.common.scan;

import java.util.List;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
public interface IPackageScanner {

    /**
     * 扫描路径
     * @param rootPackage
     * @return
     */
    List<Class<?>> scan(String rootPackage);
}
