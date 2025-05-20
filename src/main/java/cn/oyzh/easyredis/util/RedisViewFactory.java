package cn.oyzh.easyredis.util;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.controller.AboutController;
import cn.oyzh.easyredis.controller.MainController;
import cn.oyzh.easyredis.controller.SettingController2;
import cn.oyzh.easyredis.controller.connect.RedisAddConnectController;
import cn.oyzh.easyredis.controller.connect.RedisExportConnectController;
import cn.oyzh.easyredis.controller.connect.RedisImportConnectController;
import cn.oyzh.easyredis.controller.connect.RedisUpdateConnectController;
import cn.oyzh.easyredis.controller.data.RedisExportDataController;
import cn.oyzh.easyredis.controller.data.RedisImportDataController;
import cn.oyzh.easyredis.controller.data.RedisMigrationDataController;
import cn.oyzh.easyredis.controller.data.RedisTransportDataController;
import cn.oyzh.easyredis.controller.jump.RedisAddJumpController;
import cn.oyzh.easyredis.controller.jump.RedisUpdateJumpController;
import cn.oyzh.easyredis.controller.tool.RedisToolController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisJumpConfig;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;

import java.io.File;

/**
 * redis页面工厂
 *
 * @author oyzh
 * @since 2025-05-20
 */
public class RedisViewFactory {

    /**
     * 新增SSH连接
     *
     * @param group 分组
     */
    public static void addConnect(RedisGroup group) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisAddConnectController.class, StageManager.getPrimaryStage());
            adapter.setProp("group", group);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 修改连接
     *
     * @param connect 连接
     */
    public static void updateConnect(RedisConnect connect) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisUpdateConnectController.class, StageManager.getPrimaryStage());
            adapter.setProp("redisConnect", connect);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 关于
     */
    public static void about() {
        try {
            StageManager.showStage(AboutController.class, StageManager.getPrimaryStage());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 传输数据
     *
     * @param connect redis连接
     * @param dbIndex db索引
     */
    public static void transportData(RedisConnect connect, Integer dbIndex) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisTransportDataController.class, StageManager.getPrimaryStage());
            adapter.setProp("sourceConnect", connect);
            adapter.setProp("dbIndex", dbIndex);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 工具
     */
    public static void tool() {
        try {
            StageManager.showStage(RedisToolController.class, StageManager.getPrimaryStage());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 导入连接
     *
     * @param file 文件
     */
    public static void importConnect(File file) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisImportConnectController.class, StageManager.getPrimaryStage());
            adapter.setProp("file", file);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 导出连接
     */
    public static void exportConnect() {
        try {
            StageManager.showStage(RedisExportConnectController.class, StageManager.getPrimaryStage());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 导入数据
     *
     * @param connect redis连接
     */
    public static void importData(RedisConnect connect) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisImportDataController.class, StageManager.getPrimaryStage());
            adapter.setProp("connect", connect);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 导出数据
     *
     * @param connect redis连接
     * @param dbIndex db索引
     */
    public static void exportData(RedisConnect connect, Integer dbIndex) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisExportDataController.class, StageManager.getPrimaryStage());
            adapter.setProp("connect", connect);
            adapter.setProp("dbIndex", dbIndex);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 迁移数据
     */
    public static void migrationData() {
        try {
            StageManager.showStage(RedisMigrationDataController.class, StageManager.getPrimaryStage());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 设置
     */
    public static void setting() {
        try {
            StageAdapter adapter = StageManager.getStage(SettingController2.class);
            if (adapter != null) {
                JulLog.info("front setting.");
                adapter.toFront();
            } else {
                JulLog.info("show setting.");
                StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 主页
     */
    public static void main() {
        try {
            StageAdapter adapter = StageManager.getStage(MainController.class);
            if (adapter != null) {
                JulLog.info("front main.");
                adapter.toFront();
            } else {
                JulLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加跳板
     */
    public static StageAdapter addJump() {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisAddJumpController.class, StageManager.getPrimaryStage());
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 编辑跳板
     *
     * @param config 配置
     */
    public static StageAdapter updateJump(RedisJumpConfig config) {
        try {
            StageAdapter adapter = StageManager.parseStage(RedisUpdateJumpController.class, StageManager.getPrimaryStage());
            adapter.setProp("config", config);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

}
