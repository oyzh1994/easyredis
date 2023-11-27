package cn.oyzh.easyredis.dto;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.fx.common.dto.Project;
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
    private List<RedisInfo> connects;

    /**
     * 从redis连接数据生成
     *
     * @param redisInfos 连接列表
     * @return RedisInfoExport
     */
    public static RedisInfoExport fromConnects(@NonNull List<RedisInfo> redisInfos) {
        RedisInfoExport export = new RedisInfoExport();
        Project project = SpringUtil.getBean(Project.class);
        export.version = project.getVersion();
        export.connects = redisInfos;
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
        StaticLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObj(json);
        RedisInfoExport export = new RedisInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getStr("version");
        export.connects = object.getBeanList("connects", RedisInfo.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJsonStr(this);
    }
}
