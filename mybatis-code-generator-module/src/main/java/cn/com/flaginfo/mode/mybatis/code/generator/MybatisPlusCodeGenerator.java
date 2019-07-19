package cn.com.flaginfo.mode.mybatis.code.generator;

import cn.com.flaginfo.mode.mybatis.code.domain.CodeGeneratorConfig;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuMeng
 */
@Getter
@Setter
@ToString
public class MybatisPlusCodeGenerator {

    private CodeGeneratorConfig codeGeneratorConfig;

    private String basePath;

    private String javaHome = "/src/main/java";

    private String resourceHome = "/src/main/resources";

    public MybatisPlusCodeGenerator(CodeGeneratorConfig codeGeneratorConfig) {
        this.codeGeneratorConfig = codeGeneratorConfig;
        Assert.notNull(codeGeneratorConfig, "代码生成配置不能为空");
        Assert.notNull(codeGeneratorConfig.getAuthor(), "代码作者不能为空");
        Assert.notNull(codeGeneratorConfig.getDatabase(), "数据库名称不能为空");
        Assert.notNull(codeGeneratorConfig.getUserName(), "数据库用户名不能为空");
        Assert.notNull(codeGeneratorConfig.getPassword(), "数据库密码不能为空");
        Assert.notNull(codeGeneratorConfig.getDbHost(), "数据库地址不能为空");
        Assert.notNull(codeGeneratorConfig.getTables(), "数据库表名不能为空");
    }


    /**
     * 自动生成代码
     */
    public void generate() {
        // 全局配置
        GlobalConfig gc = initGlobalConfig(codeGeneratorConfig.getAuthor());
        // 数据源配置
        DataSourceConfig dsc = initDataSourceConfig();
        // 包配置
        PackageConfig pc = initPackageConfig();
        // 模板引擎配置
        FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        mpg.setGlobalConfig(gc);
        mpg.setDataSource(dsc);
        mpg.setPackageInfo(pc);
        mpg.setTemplateEngine(templateEngine);
        // 自定义配置
        InjectionConfig cfg = initInjectionConfig();
        mpg.setCfg(cfg);
        // 策略配置
        StrategyConfig strategy = initStrategyConfig(codeGeneratorConfig.getTables(), codeGeneratorConfig.getTablePrefix());
        mpg.setStrategy(strategy);
        TemplateConfig tc = initTemplateConfig();
        mpg.setTemplate(tc);
        //开始执行
        mpg.execute();
    }

    private PackageConfig initPackageConfig() {
        return new PackageConfig()
                .setParent(codeGeneratorConfig.getPackageName())
                .setController(codeGeneratorConfig.getControllerPath())
                .setEntity(codeGeneratorConfig.getEntityPath())
                .setMapper(codeGeneratorConfig.getMapperPath())
                .setService(codeGeneratorConfig.getServicePath())
                .setServiceImpl(codeGeneratorConfig.getServiceImplPath())
                .setXml(codeGeneratorConfig.getMapperXmlPath());

    }


    /**
     * 配置数据源
     *
     * @return
     */
    private DataSourceConfig initDataSourceConfig() {
        return new DataSourceConfig()
                .setUrl(codeGeneratorConfig.getUrl())
                .setDriverName(codeGeneratorConfig.getDriverName())
                .setUsername(codeGeneratorConfig.getUserName())
                .setPassword(codeGeneratorConfig.getPassword());
    }

    /**
     * 全局配置
     *
     * @return
     */
    private GlobalConfig initGlobalConfig(String author) {
        GlobalConfig gc = new GlobalConfig();
        basePath = System.getProperty("user.dir");
        if (StringUtils.isNotBlank(codeGeneratorConfig.getModuleName())) {
            basePath = basePath + "/" + codeGeneratorConfig.getModuleName();
        }
        gc.setOutputDir(basePath + javaHome);
        gc.setAuthor(author);
        gc.setOpen(false);
        gc.setControllerName(codeGeneratorConfig.getControllerName());
        gc.setEntityName(codeGeneratorConfig.getEntityName());
        gc.setServiceName(codeGeneratorConfig.getServiceName());
        gc.setServiceImplName(codeGeneratorConfig.getServiceImplName());
        gc.setFileOverride(codeGeneratorConfig.isIfOverrideFile());
        gc.setDateType(codeGeneratorConfig.getDateType());
        return gc;
    }

    /**
     * 自定义配置
     *
     * @return
     */
    private InjectionConfig initInjectionConfig() {
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> fileOutConfigs = new ArrayList<>(1);
        if (codeGeneratorConfig.isIfGenerateMapperXml()) {
            fileOutConfigs.add(this.buildFileOutConfig("/templates/mapper.xml.ftl",
                    resourceHome,
                    codeGeneratorConfig.getMapperXmlPath(),
                    codeGeneratorConfig.getMapperXmlName(),
                    StringPool.DOT_XML));
        }
        cfg.setFileOutConfigList(fileOutConfigs);
        return cfg;
    }

    private FileOutConfig buildFileOutConfig(String templateName, String rootPath, String path, String fileTemplateName, String suffix) {
        if (path.contains(".")) {
            path = path.replaceAll("\\.", "//");
        }
        final String outPath = "/" + path;
        return new FileOutConfig(templateName) {
            @Override
            public String outputFile(TableInfo tableInfo) {

                //自定义输入文件名称
                return basePath + rootPath + outPath + "/" + parseMapperName(fileTemplateName, tableInfo.getEntityName()) + suffix;
            }
        };
    }

    private String parseMapperName(String templateName, String entityName) {
        return String.format(templateName, entityName);
    }

    /**
     * 策略配置
     *
     * @param tableNames 数据库表名
     * @return
     */
    private StrategyConfig initStrategyConfig(String[] tableNames, String... tablePrefix) {
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setTablePrefix(tablePrefix);
        strategy.setInclude(tableNames);
        strategy.setRestControllerStyle(true);
        if (null != codeGeneratorConfig.getSuperEntityColumns()) {
            strategy.setSuperEntityColumns(codeGeneratorConfig.getSuperEntityColumns());
        }
        if (StringUtils.isNotBlank(codeGeneratorConfig.getSuperEntityClass())) {
            strategy.setSuperEntityClass(codeGeneratorConfig.getSuperEntityClass());
        }
        if (StringUtils.isNotBlank(codeGeneratorConfig.getSuperControllerClass())) {
            strategy.setSuperControllerClass(codeGeneratorConfig.getSuperControllerClass());
        }
        if (StringUtils.isNotBlank(codeGeneratorConfig.getSuperMapperClass())) {
            strategy.setSuperMapperClass(codeGeneratorConfig.getSuperMapperClass());
        }
        if (StringUtils.isNotBlank(codeGeneratorConfig.getSuperServiceClass())) {
            strategy.setSuperServiceClass(codeGeneratorConfig.getSuperServiceClass());
        }
        if (StringUtils.isNotBlank(codeGeneratorConfig.getSuperServiceImplClass())) {
            strategy.setSuperServiceImplClass(codeGeneratorConfig.getSuperServiceImplClass());
        }
        return strategy;
    }

    /**
     * 覆盖Entity以及xml
     *
     * @return
     */
    private TemplateConfig initTemplateConfig() {
        TemplateConfig tc = new TemplateConfig();
        tc.setEntityKt(null);
        //不使用默认的xml生成方式，默认生成到了mapper同级目录下
        tc.setXml(null);
        if (!codeGeneratorConfig.isIfGenerateController()) {
            tc.setController(null);
        }
        if (!codeGeneratorConfig.isIfGenerateService()) {
            tc.setService(null);
        }
        if (!codeGeneratorConfig.isIfGenerateServiceImpl()) {
            tc.setServiceImpl(null);
        }
        if (!codeGeneratorConfig.isIfGenerateMapper()) {
            tc.setMapper(null);
        }
        if (!codeGeneratorConfig.isIfGenerateEntity()) {
            tc.setEntity(null);
        }
        return tc;
    }
}