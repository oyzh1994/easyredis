package cn.oyzh.easyredis.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * redis连接导出对象
 *
 * @author oyzh
 * @since 2023/06/22
 */
//@Slf4j
public class RedisConnectExport {

    /**
     * 导出程序版本号
     */
    private String version;

    /**
     * 平台
     */
    private String platform;

    /**
     * 分组
     */
    private List<RedisGroup> groups;

    /**
     * 连接
     */
    private List<RedisConnect> connects;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<RedisGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<RedisGroup> groups) {
        this.groups = groups;
    }

    public List<RedisConnect> getConnects() {
        return connects;
    }

    public void setConnects(List<RedisConnect> connects) {
        this.connects = connects;
    }

    /**
     * 从redis连接数据生成
     *
     * @param redisConnects 连接列表
     * @return RedisInfoExport
     */
    public static RedisConnectExport fromConnects( List<RedisConnect> redisConnects) {
        RedisConnectExport export = new RedisConnectExport();
        Project project = Project.load();
        export.version = project.getVersion();
        export.connects = redisConnects;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return RedisInfoExport
     */
    public static RedisConnectExport fromJSON( String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        RedisConnectExport export = new RedisConnectExport();
        export.connects = new ArrayList<>(4);
        export.version = object.getString("version");
        export.platform = object.getString("platform");
        export.groups = object.getBeanList("groups", RedisGroup.class);
        export.connects = object.getBeanList("connects", RedisConnect.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJson(this);
    }
}
