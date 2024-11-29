package cn.oyzh.easyredis.store;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisKeyFilterHistory;
import cn.oyzh.easyredis.domain.RedisSSHConnect;
import cn.oyzh.easyredis.domain.RedisSetting;
import cn.oyzh.easyredis.terminal.RedisTerminalHistory;
import cn.oyzh.store.jdbc.JdbcConst;
import cn.oyzh.store.jdbc.JdbcDialect;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-23
 */
@UtilityClass
public class RedisStoreUtil {

    /**
     * 执行初始化
     */
    public static void init() {
        JdbcConst.dbCacheSize(1024);
        JdbcConst.dbDialect(JdbcDialect.H2);
        JdbcConst.dbFile(RedisConst.STORE_PATH + "db");
    }

    /**
     * 加载旧版本分组数据
     *
     * @return 旧版本分组数据
     */
    public static List<RedisGroup> loadGroups() {
        List<RedisGroup> groups = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_group.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到分组数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RedisGroup group = new RedisGroup();
                if (obj.containsKey("gid")) {
                    group.setGid(obj.getString("gid"));
                }
                if (obj.containsKey("name")) {
                    group.setName(obj.getString("name"));
                }
                if (obj.containsKey("expand")) {
                    group.setExpand(obj.getBooleanValue("Expand"));
                }
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * 加载旧版本连接数据
     *
     * @return 旧版本连接数据
     */
    public static List<RedisConnect> loadConnects() {
        List<RedisConnect> connects = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_info.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到连接数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RedisConnect connect = new RedisConnect();

                if (obj.containsKey("id")) {
                    connect.setId(obj.getString("id"));
                }
                if (obj.containsKey("name")) {
                    connect.setName(obj.getString("name"));
                }
                if (obj.containsKey("host")) {
                    connect.setHost(obj.getString("host"));
                }
                if (obj.containsKey("sshForward")) {
                    connect.setSshForward(obj.getBooleanValue("sshForward"));
                }
                if (obj.containsKey("collects")) {
                    connect.setCollects(obj.getBeanList("collects", String.class));
                }
                if (obj.containsKey("remark")) {
                    connect.setRemark(obj.getString("remark"));
                }
                if (obj.containsKey("groupId")) {
                    connect.setGroupId(obj.getString("groupId"));
                }
                if (obj.containsKey("readonly")) {
                    connect.setReadonly(obj.getBooleanValue("readonly"));
                }
                if (obj.containsKey("connectTimeOut")) {
                    connect.setConnectTimeOut(obj.getIntValue("connectTimeOut"));
                }
                if (obj.containsKey("sshInfo")) {
                    JSONObject object = obj.getJSONObject("sshInfo");
                    RedisSSHConnect sshConnect = new RedisSSHConnect();
                    if (object.containsKey("port")) {
                        sshConnect.setPort(object.getInt("port"));
                    }
                    if (object.containsKey("host")) {
                        sshConnect.setHost(object.getString("host"));
                    }
                    if (object.containsKey("user")) {
                        sshConnect.setUser(object.getString("user"));
                    }
                    if (object.containsKey("timeout")) {
                        sshConnect.setTimeout(object.getInt("timeout"));
                    }
                    if (object.containsKey("password")) {
                        sshConnect.setPassword(object.getString("password"));
                    }
                    connect.setSshConnect(sshConnect);
                }
                connects.add(connect);
            }
        }
        return connects;
    }

    /**
     * 加载旧版本过滤数据
     *
     * @return 旧版本过滤数据
     */
    public static List<RedisFilter> loadFilters() {
        List<RedisFilter> filters = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_filter.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到过滤数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RedisFilter filter = new RedisFilter();
                if (obj.containsKey("kw")) {
                    filter.setKw(obj.getString("kw"));
                }
                if (obj.containsKey("uid")) {
                    filter.setUid(obj.getString("uid"));
                }
                if (obj.containsKey("enable")) {
                    filter.setEnable(obj.getBooleanValue("enable"));
                }
                if (obj.containsKey("partMatch")) {
                    filter.setPartMatch(obj.getBooleanValue("partMatch"));
                }
                filters.add(filter);
            }
        }
        return filters;
    }

    /**
     * 加载旧版本终端历史数据
     *
     * @return 旧版本终端历史数据
     */
    public static List<RedisTerminalHistory> loadTerminalHistory() {
        List<RedisTerminalHistory> histories = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_shell_history.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到终端历史数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RedisTerminalHistory history = new RedisTerminalHistory();
                if (obj.containsKey("tid")) {
                    history.setTid(obj.getString("tid"));
                }
                if (obj.containsKey("line")) {
                    history.setLine(obj.getString("line"));
                }
                if (obj.containsKey("saveTime")) {
                    history.setSaveTime(obj.getLongValue("saveTime"));
                }
                histories.add(history);
            }
        }
        return histories;
    }

    /**
     * 加载旧版本键过滤历史数据
     *
     * @return 旧版本键过滤历史数据
     */
    public static List<RedisKeyFilterHistory> loadKeyFilterHistory() {
        List<RedisKeyFilterHistory> histories = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_key_filter_history.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到键过滤历史数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RedisKeyFilterHistory history = new RedisKeyFilterHistory();
                if (obj.containsKey("uid")) {
                    history.setUid(obj.getString("uid"));
                }
                if (obj.containsKey("pattern")) {
                    history.setPattern(obj.getString("pattern"));
                }
                histories.add(history);
            }
        }
        return histories;
    }

    /**
     * 加载旧版本设置数据
     *
     * @return 旧版本设置数据
     */
    public static RedisSetting loadSetting() {
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_setting.json";
        String json = FileUtil.readUtf8String(file);
        JSONObject object = JSONUtil.parseObject(json);
        RedisSetting setting = new RedisSetting();
        if (object != null) {
            if (object.containsKey("theme")) {
                setting.setTheme(object.getString("theme"));
            }
            if (object.containsKey("fgColor")) {
                setting.setFgColor(object.getString("fgColor"));
            }
            if (object.containsKey("bgColor")) {
                setting.setBgColor(object.getString("bgColor"));
            }
            if (object.containsKey("accentColor")) {
                setting.setAccentColor(object.getString("accentColor"));
            }
            if (object.containsKey("fontFamily")) {
                setting.setFontFamily(object.getString("fontFamily"));
            }
            if (object.containsKey("fontSize")) {
                setting.setFontSize(object.getByteValue("fontSize"));
            }
            if (object.containsKey("fontWeight")) {
                setting.setFontWeight(object.getShortValue("fontWeight"));
            }
            if (object.containsKey("locale")) {
                setting.setLocale(object.getString("locale"));
            }
            if (object.containsKey("exitMode")) {
                setting.setExitMode(object.getByteValue("exitMode"));
            }
            if (object.containsKey("rememberPageSize")) {
                setting.setRememberPageSize(object.getByteValue("rememberPageSize"));
            }
            if (object.containsKey("rememberPageResize")) {
                setting.setRememberPageResize(object.getByteValue("rememberPageResize"));
            }
            if (object.containsKey("rememberPageLocation")) {
                setting.setRememberPageLocation(object.getByteValue("rememberPageLocation"));
            }
            if (object.containsKey("opacity")) {
                setting.setOpacity(object.getFloatValue("opacity"));
            }
        } else {
            JulLog.warn("未找到设置数据");
        }
        file = storePath + File.separator + "page_info.json";
        json = FileUtil.readUtf8String(file);
        object = JSONUtil.parseObject(json);
        if (object != null) {
            if (object.containsKey("width")) {
                setting.setPageWidth(object.getDoubleValue("width"));
            }
            if (object.containsKey("height")) {
                setting.setPageHeight(object.getDoubleValue("height"));
            }
            if (object.containsKey("screenX")) {
                setting.setPageScreenX(object.getDoubleValue("screenX"));
            }
            if (object.containsKey("screenY")) {
                setting.setPageScreenY(object.getDoubleValue("screenY"));
            }
            if (object.containsKey("maximized")) {
                setting.setPageMaximized(object.getBooleanValue("maximized"));
            }
            if (object.containsKey("mainLeftWidth")) {
                setting.setPageLeftWidth(object.getFloatValue("mainLeftWidth"));
            }
        } else {
            JulLog.warn("未找到页面信息");
        }
        return setting;
    }

    /**
     * 忽略迁移
     */
    public static void ignoreMigration() {
        String storePath = SysConst.storeDir();
        String ignore = storePath + File.separator + "ignore.data";
        FileUtil.touch(ignore);
    }

    /**
     * 完成迁移
     */
    public static void doneMigration() {
        String storePath = SysConst.storeDir();
        String done = storePath + File.separator + "done.data";
        FileUtil.touch(done);
    }

    /**
     * 检查旧版本
     *
     * @return 结果
     */
    public static boolean checkOlder() {
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "redis_info.json";
        String done = storePath + File.separator + "done.data";
        String ignore = storePath + File.separator + "ignore.data";
        return FileUtil.exist(file) && !(FileUtil.exist(done) || FileUtil.exist(ignore));
    }

}
