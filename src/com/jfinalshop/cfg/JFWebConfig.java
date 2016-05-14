package com.jfinalshop.cfg;

import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.baidu.ueditor.UeditorConfigKit;
import com.baidu.ueditor.manager.QiniuFileManager;
import com.jfinal.config.*;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.plugin.tablebind.SimpleNameStyles;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.cache.J2CacheByMethodRegex;
import com.jfinal.plugin.cache.J2CacheService;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinalshop.controller.weixin.*;
import com.jfinalshop.handler.JDruidStatViewHandler;
import com.jfinalshop.model.TreeModel;
import com.jfinalshop.util.SerialNumberUtil;
import net.dreamlu.controller.UeditorApiController;
import org.beetl.ext.jfinal.BeetlRenderFactory;

public class JFWebConfig extends JFinalConfig {

    /**
     * 供Shiro插件使用。
     */
    Routes routes;

    @Override
    public void configConstant(Constants me) {
        //SqlReporter.setLogger(true);
        me.setErrorView(401, "/common/401.html");
        me.setErrorView(403, "/common/403.html");
        me.setError404View("/common/404.html");
        me.setError500View("/common/500.html");

        // 加载数据库配置文件
        loadPropertyFile("appconfig.properties");
        PropKit.use("appconfig.properties");
        // 设定Beetl
        me.setMainRenderFactory(new BeetlRenderFactory());
        // 设定为开发者模式
        me.setDevMode(PropKit.getBoolean("devMode", false));
        // ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
        ApiConfigKit.setDevMode(me.getDevMode());
    }

    @Override
    public void configRoute(Routes me) {
        this.routes = me;
        AutoBindRoutes autoBindRoutes = new AutoBindRoutes();
        autoBindRoutes.addExcludeClasses(MsgControllerAdapter.class);
        me.add(autoBindRoutes);
        //ueditor 文件上传插件 配置路由
        me.add("/ueditor/api", UeditorApiController.class);
    }

    private StatFilter getStatFilter() {
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    private Log4jFilter getLog4jFilter() {
        Log4jFilter log4jFilter = new Log4jFilter();
        log4jFilter.setStatementExecuteUpdateAfterLogEnabled(false);
        log4jFilter.setStatementExecuteBatchAfterLogEnabled(false);
        log4jFilter.setStatementExecuteQueryAfterLogEnabled(false);
        log4jFilter.setStatementExecuteAfterLogEnabled(true);
        log4jFilter.setStatementCloseAfterLogEnabled(false);
        log4jFilter.setStatementCreateAfterLogEnabled(false);
        log4jFilter.setStatementLogEnabled(false);
        log4jFilter.setResultSetLogEnabled(false);
        log4jFilter.setConnectionLogEnabled(false);
        log4jFilter.setDataSourceLogEnabled(false);
        log4jFilter.setStatementExecutableSqlLogEnable(true);
        return log4jFilter;
    }

    @Override
    public void configPlugin(Plugins me) {
        // mysql
        String configName = getProperty("db.configName");
        String url = getProperty("jdbcUrl");
        String username = getProperty("user");
        String password = getProperty("password");
        String driverClass = getProperty("driverClass");
        String filters = getProperty("filters");

        // mysql 数据源
        DruidPlugin dsMysql = new DruidPlugin(url, username, password, driverClass, filters);
        dsMysql.addFilter(getStatFilter());
        dsMysql.addFilter(getLog4jFilter());
        dsMysql.setMaxActive(200);
        me.add(dsMysql);

        ActiveRecordPlugin arpMysql = new ActiveRecordPlugin(configName, dsMysql);
        arpMysql.setContainerFactory(new CaseInsensitiveContainerFactory(true));
        me.add(arpMysql);

        AutoTableBindPlugin atbp = new AutoTableBindPlugin(dsMysql, SimpleNameStyles.LOWER);
        atbp.addExcludeClasses(TreeModel.class);
        atbp.setShowSql(false);
        atbp.setDialect(new MysqlDialect());// 配置MySql方言
        me.add(atbp);

        //加载Shiro插件
        me.add(new ShiroPlugin(routes));
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.addGlobalServiceInterceptor(new J2CacheByMethodRegex("(.*get.*|.*find.*|.*list.*|.*search.*)",
                "(drop.*|.*update.*|.*delete.*)"));
    }

    @Override
    public void configHandler(Handlers me) {
        JDruidStatViewHandler viewHandler = new JDruidStatViewHandler("/druid");
        me.add(viewHandler);
    }

    public void afterJFinalStart() {
        super.afterJFinalStart();
        String ak = PropKit.get("qiniu.ak");
        String sk = PropKit.get("qiniu.sk");
        String bucket = PropKit.get("qiniu.bucket");
        UeditorConfigKit.setFileManager(new QiniuFileManager(ak, sk, bucket));
        SerialNumberUtil.lastSnNumberInit();
        J2CacheService.get("test-connection");
    }

}
