package cn.com.flaginfo.mode.mybatis.code.domain;

import com.baomidou.mybatisplus.generator.config.rules.DateType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * @author LiuMeng
 * @version 1.0
 * @className CodeGenratorConfig
 * @describe TODO
 * @date 2019/7/18 10:32
 */
@Getter
@ToString
public class CodeGeneratorConfig {

    private String author;
    private String database;
    private String userName;
    private String password;
    private String dbHost;
    private String[] tables;

    private String moduleName;
    private String packageName;
    private String[] tablePrefix;
    private String driverName;
    private String url;

    private String controllerName;
    private String entityName;
    private String serviceName;
    private String serviceImplName;
    private String mapperName;
    private String mapperXmlName;

    private String controllerPath;
    private String entityPath;
    private String servicePath;
    private String serviceImplPath;
    private String mapperPath;
    private String mapperXmlPath;

    private String[] superEntityColumns;

    private String superEntityClass;
    private String superControllerClass;
    private String superMapperClass;
    private String superServiceClass;
    private String superServiceImplClass;

    private boolean ifOverrideFile;
    private boolean ifGenerateController;
    private boolean ifGenerateService;
    private boolean ifGenerateServiceImpl;
    private boolean ifGenerateMapper;
    private boolean ifGenerateMapperXml;
    private boolean ifGenerateEntity;

    private DateType dateType;

    CodeGeneratorConfig(String author,
                        String database,
                        String userName,
                        String password,
                        String dbHost,
                        String driverName,
                        String[] tables,
                        String moduleName,
                        String packageName,
                        String[] tablePrefix,

                        String entityName,
                        String serviceName,
                        String controllerName,
                        String serviceImplName,
                        String mapperName,
                        String mapperXmlName,

                        String entityPath,
                        String servicePath,
                        String controllerPath,
                        String serviceImplPath,
                        String mapperPath,
                        String mapperXmlPath,

                        String[] superEntityColumns,
                        String superEntityClass,
                        String superControllerClass,
                        String superMapperClass,
                        String superServiceClass,
                        String superServiceImplClass,
                        Boolean ifOverrideFile,
                        Boolean ifGenerateController,
                        Boolean ifGenerateService,
                        Boolean ifGenerateServiceImpl,
                        Boolean ifGenerateMapper,
                        Boolean ifGenerateMapperXml,
                        Boolean ifGenerateEntity,
                        DateType dateType) {
        this.url = "jdbc:mysql://" + dbHost + "/" + database + "?useUnicode=true&useSSL=false&characterEncoding=utf8";

        this.entityName = StringUtils.isBlank(entityName) ? "%s" : entityName;
        this.serviceName = StringUtils.isBlank(serviceName) ? "%sService" : serviceName;
        this.controllerName = StringUtils.isBlank(controllerName) ? "%sController" : controllerName;
        this.serviceImplName = StringUtils.isBlank(serviceImplName) ? "%sServiceImpl" : serviceImplName;
        this.mapperName = StringUtils.isBlank(mapperName) ? "%sMapper" : mapperName;
        this.mapperXmlName = StringUtils.isBlank(mapperXmlName) ? "%sMapper" : mapperName;

        this.controllerPath = StringUtils.isBlank(controllerPath) ? "controller" : controllerPath;
        this.entityPath = StringUtils.isBlank(entityPath) ? "entity" : entityPath;
        this.servicePath = StringUtils.isBlank(servicePath) ? "service" : servicePath;
        this.serviceImplPath = StringUtils.isBlank(serviceImplPath) ? "service.impl" : serviceImplPath;
        this.mapperPath = StringUtils.isBlank(mapperPath) ? packageName + "mapper" : mapperPath;
        this.mapperXmlPath = StringUtils.isBlank(mapperXmlPath) ? "mapper" : mapperXmlPath;

        this.superEntityColumns = superEntityColumns;

        this.superEntityClass = superEntityClass;
        this.superControllerClass = superControllerClass;
        this.superMapperClass = superMapperClass;
        this.superServiceClass = superServiceClass;
        this.superServiceImplClass = superServiceImplClass;
        this.ifOverrideFile = null == ifOverrideFile ? false : ifOverrideFile;
        this.ifGenerateController = null == ifGenerateController ? true : ifGenerateController;
        this.ifGenerateService = null == ifGenerateService ? true : ifGenerateService;
        this.ifGenerateServiceImpl = null == ifGenerateServiceImpl ? true : ifGenerateServiceImpl;
        this.ifGenerateMapper = null == ifGenerateMapper ? true : ifGenerateMapper;
        this.ifGenerateMapperXml = null == ifGenerateMapperXml ? true : ifGenerateMapperXml;
        this.ifGenerateEntity = null == ifGenerateEntity ? true : ifGenerateEntity;

        this.author = author;
        this.database = database;
        this.userName = userName;
        this.password = password;
        this.dbHost = dbHost;
        this.driverName = StringUtils.isBlank(driverName) ? "com.mysql.cj.jdbc.Driver" : driverName;
        this.tables = tables;

        this.moduleName = moduleName;
        this.packageName = packageName;
        this.tablePrefix = null == tablePrefix ? new String[0] : tablePrefix;
        this.mapperName = StringUtils.isBlank(mapperName) ? "%sMapper" : mapperName;

        this.dateType = null == dateType ? DateType.ONLY_DATE : dateType;
    }

    public static CodeGeneratorConfig.CodeGeneratorConfigBuilder builder() {
        return new CodeGeneratorConfig.CodeGeneratorConfigBuilder();
    }

    @Setter
    @Accessors(chain = true)
    public static class CodeGeneratorConfigBuilder {
        private String author;
        private String database;
        private String userName;
        private String password;
        private String dbHost;
        private String driverName;
        private String[] tables;
        private String moduleName;
        private String packageName;
        private String[] tablePrefix;

        private String entityName;
        private String serviceName;
        private String controllerName;
        private String serviceImplName;
        private String mapperName;
        private String mapperXmlName;

        private String entityPath;
        private String servicePath;
        private String controllerPath;
        private String serviceImplPath;
        private String mapperPath;
        private String mapperXmlPath;

        private String[] superEntityColumns;

        private String superEntityClass;
        private String superControllerClass;
        private String superMapperClass;
        private String superServiceClass;
        private String superServiceImplClass;

        private Boolean ifOverrideFile;
        private Boolean ifGenerateController;
        private Boolean ifGenerateService;
        private Boolean ifGenerateServiceImpl;
        private Boolean ifGenerateMapper;
        private Boolean ifGenerateMapperXml;
        private Boolean ifGenerateEntity;

        private DateType dateType;

        CodeGeneratorConfigBuilder() {
        }

        public CodeGeneratorConfig build() {
            return new CodeGeneratorConfig(this.author,
                    this.database,
                    this.userName,
                    this.password,
                    this.dbHost,
                    this.driverName,
                    this.tables,
                    this.moduleName,
                    this.packageName,
                    this.tablePrefix,

                    this.entityName,
                    this.serviceName,
                    this.controllerName,
                    this.serviceImplName,
                    this.mapperName,
                    this.mapperXmlName,

                    this.entityPath,
                    this.servicePath,
                    this.controllerPath,
                    this.serviceImplPath,
                    this.mapperPath,
                    this.mapperXmlPath,

                    this.superEntityColumns,
                    this.superEntityClass,
                    this.superControllerClass,
                    this.superMapperClass,
                    this.superServiceClass,
                    this.superServiceImplClass,
                    this.ifOverrideFile,
                    this.ifGenerateController,
                    this.ifGenerateService,
                    this.ifGenerateServiceImpl,
                    this.ifGenerateMapper,
                    this.ifGenerateMapperXml,
                    this.ifGenerateEntity,
                    this.dateType);
        }
    }
}
