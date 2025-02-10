package cn.oyzh.easyredis.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.domain.RedisConnect;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * redis连接导出对象
 *
 * @author oyzh
 * @since 2023/06/22
 */
//@Slf4j
public class RedisInfoExport {

    /**
     * 导出程序版本号
     */
    @Getter
    private String version;

    /**
     * 平台
     */
    @Getter
    private String platform;

    /**
     * 导出连接数据
     */
    @Getter
    private List<RedisConnect> connects;

    /**
     * 从redis连接数据生成
     *
     * @param redisConnects 连接列表
     * @return RedisInfoExport
     */
    public static RedisInfoExport fromConnects(@NonNull List<RedisConnect> redisConnects) {
        RedisInfoExport export = new RedisInfoExport();
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
    public static RedisInfoExport fromJSON(@NonNull String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        RedisInfoExport export = new RedisInfoExport();
        export.connects = new ArrayList<>(4);
        export.version = object.getString("version");
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
