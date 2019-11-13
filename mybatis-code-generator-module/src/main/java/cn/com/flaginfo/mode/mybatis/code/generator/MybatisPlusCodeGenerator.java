package cn.com.flaginfo.mode.mybatis.code.generator;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisPlusCodeGenerator {

    private Configuration configuration;

    private String XML = "/templates/mapper.xml.ftl";

    /**
     *
     * @param propertiesFileName 配置文件路径，配置内容可以参考{code-generator-template.properties}
     */
    public MybatisPlusCodeGenerator(String propertiesFileName) {
        configuration = new Configuration(propertiesFileName);
    }

    /**
     * 生成代码主入口
     */
    public void generator() {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        String projectPath = System.getProperty("user.dir");
        String moduleName = configuration.getString("module.name");
        if(StringUtils.isNotBlank(moduleName)){
            projectPath = projectPath + StringPool.SLASH + moduleName;
        }
        // 全局配置
        this.setGlobalConfig(mpg, projectPath);
        // 数据源配置
        this.setDb(mpg);
        //设置输出路径
        this.setOutPath(mpg);
        // 配置需要生成的文件
        this.setGenerateFile(mpg, projectPath);
        //设置策略
        this.setStrategy(mpg);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }


    /**
     * 设置全局配置
     *
     * @param mpg
     */
    private void setGlobalConfig(AutoGenerator mpg, String projectPath) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor(configuration.getString("auth", "Meng.Liu"));
        gc.setOpen(false);
        gc.setEnableCache(configuration.getBoolean("enable.cache", false));
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setFileOverride(configuration.getBoolean("file.override", false));
        setFileName(gc);
        mpg.setGlobalConfig(gc);
    }

    /**
     * 设置文件名称  %s会替换为表名
     *
     * @param gc
     */
    private void setFileName(GlobalConfig gc) {
        if (null != configuration.getString("file.name.controller")) {
            gc.setControllerName(configuration.getString("file.name.controller"));
        }
        if (null != configuration.getString("file.name.entity")) {
            gc.setEntityName(configuration.getString("file.name.entity"));
        }
        if (null != configuration.getString("file.name.service")) {
            gc.setServiceName(configuration.getString("file.name.service"));
        }
        if (null != configuration.getString("file.name.service-impl")) {
            gc.setServiceImplName(configuration.getString("file.name.service-impl"));
        }
        if (null != configuration.getString("file.name.mapper")) {
            gc.setMapperName(configuration.getString("file.name.mapper"));
        }
    }

    /**
     * 设置数据源
     *
     * @param mpg
     */
    private void setDb(AutoGenerator mpg) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://" + configuration.getString("db.host") + "/" + configuration.getString("db.name") + "?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName(configuration.getString("db.driver", "com.mysql.jdbc.Driver"));
        dsc.setUsername(configuration.getString("db.username"));
        dsc.setPassword(configuration.getString("db.password"));
        mpg.setDataSource(dsc);
    }

    /**
     * 设置输出路径
     *
     * @param mpg
     */
    private void setOutPath(AutoGenerator mpg) {
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(configuration.getString("root.package"));
        pc.setController(configuration.getString("out.path.controller"));
        pc.setEntity(configuration.getString("out.path.entity"));
        pc.setService(configuration.getString("out.path.service"));
        pc.setServiceImpl(configuration.getString("out.path.service-impl"));
        pc.setMapper(configuration.getString("out.path.mapper"));
        mpg.setPackageInfo(pc);
    }

    /**
     * 设置需要生成的文件
     *
     * @param mpg
     */
    private void setGenerateFile(AutoGenerator mpg, String projectPath) {
        TemplateConfig templateConfig = new TemplateConfig();
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = this.getMap();
                if( null == map ){
                    map = new HashMap<>();
                }
                this.setMap(map);
                // to do nothing
                String cacheClass = configuration.getString("cache.class");
                if( StringUtils.isNotBlank(cacheClass) ){
                    map.put("cacheClass", cacheClass);
                }
                Boolean isIdSuper = configuration.getBoolean("super.id");
                if( StringUtils.isNotBlank(cacheClass) ){
                    map.put("isIdSuper", isIdSuper);
                }
            }

            @Override
            public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
                if(!CollectionUtils.isEmpty(this.getMap())){
                    objectMap.putAll(this.getMap());
                }
                return objectMap;
            }
        };
        mpg.setCfg(cfg);
        if (!configuration.getBoolean("generate.controller", true)) {
            templateConfig.setController(null);
        }
        if (!configuration.getBoolean("generate.entity", true)) {
            templateConfig.setEntity(null);
        }
        if (!configuration.getBoolean("generate.service", true)) {
            templateConfig.setService(null);
        }
        if (!configuration.getBoolean("generate.service-impl", true)) {
            templateConfig.setServiceImpl(null);
        }
        if (!configuration.getBoolean("generate.mapper", true)) {
            templateConfig.setMapper(null);
        }
        if (configuration.getBoolean("generate.xml", true)) {
            // 名称的自定义配置无法直接通过packageInfo来修改，必须要自定义
            List<FileOutConfig> focList = new ArrayList<>();
            focList.add(new FileOutConfig(XML) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return projectPath + configuration.getString("out.path.xml") + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                }
            });
            cfg.setFileOutConfigList(focList);
        }
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);
    }

    /**
     * 设置生成策略
     */
    private void setStrategy(AutoGenerator mpg) {
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        this.setSuperClass(strategy);
        String superColumns = configuration.getString("super.columns");
        if( StringUtils.isNotBlank(superColumns) ){
            strategy.setSuperEntityColumns(superColumns.split(StringPool.COMMA));
        }
        String tables = configuration.getString("db.tables");
        if( StringUtils.isBlank(tables) ){
            throw new IllegalStateException("db tables is empty.");
        }
        strategy.setInclude(tables.split(StringPool.COMMA));
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setTablePrefix(mpg.getPackageInfo().getModuleName() + "_");
        mpg.setStrategy(strategy);
    }

    /**
     * 设置父类
     *
     * @param strategy
     */
    private void setSuperClass(StrategyConfig strategy) {
        if (null != configuration.getString("super.class.controller")) {
            strategy.setSuperControllerClass(configuration.getString("super.class.controller"));
        }
        if (null != configuration.getString("super.class.entity")) {
            strategy.setSuperEntityClass(configuration.getString("super.class.entity"));
        }
        if (null != configuration.getString("super.class.service")) {
            strategy.setSuperServiceClass(configuration.getString("super.class.service"));
        }
        if (null != configuration.getString("super.class.service-impl")) {
            strategy.setSuperServiceImplClass(configuration.getString("super.class.service-impl"));
        }
        if (null != configuration.getString("super.class.mapper")) {
            strategy.setSuperMapperClass(configuration.getString("super.class.mapper"));
        }
    }
}
