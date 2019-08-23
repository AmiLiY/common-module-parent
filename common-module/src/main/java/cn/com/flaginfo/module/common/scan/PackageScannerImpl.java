package cn.com.flaginfo.module.common.scan;

import cn.com.flaginfo.module.common.utils.ClassScannerUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @author: LiuMeng
 * @date: 2019/8/21
 * TODO:
 */
@Slf4j
public class PackageScannerImpl implements IPackageScanner {

    @Override
    public List<Class<?>> scan(String rootPackage) {
        try {
            return ClassScannerUtils.scanner(rootPackage);
        } catch (Exception e) {
            log.error("", e);
        }
        return Collections.emptyList();
    }
}
