package cn.oyzh.easyredis;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.controller.MainController;
import cn.oyzh.easyredis.exception.RedisExceptionParser;
import cn.oyzh.easyredis.store.RedisSettingStore;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.spring.SpringApplication;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2023/06/16
 */
@SpringBootApplication(scanBasePackages = "cn.oyzh",
        exclude = {
                AopAutoConfiguration.class,
                CacheAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                MessageSourceAutoConfiguration.class,
                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
        }
)
@EnableSpringUtil
public class EasyRedisApp extends SpringApplication implements CommandLineRunner, DisposableBean {

    public static void main(String[] args) {
        launchSpring(EasyRedisApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化主题
            ThemeManager.currentTheme(RedisSettingStore.SETTING.getTheme());
            // 注册异常处理器
            MessageBox.registerExceptionParser(RedisExceptionParser.INSTANCE);
            // 开启定期gc
            SystemUtil.gcInterval(60_000);
            // 开始执行业务
            super.start(primaryStage);
            // 显示主页面
            StageUtil.showStage(MainController.class);
            // 设置stage全部关闭后不自动销毁进程
            Platform.setImplicitExit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        StaticLog.info("EasyRedisApp destroyed.");
    }

    @Override
    public void run(String... args) {
        StaticLog.info("EasyRedisApp started.");
    }
}
