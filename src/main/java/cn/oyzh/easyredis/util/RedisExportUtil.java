package cn.oyzh.easyredis.util;


import cn.hutool.core.io.FileUtil;
import cn.oyzh.easyredis.dto.RedisNodeExport;
import cn.oyzh.easyredis.redis.RedisKey;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.common.spring.SpringUtil;
import cn.oyzh.fx.common.util.OSUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * redis导出工具类
 *
 * @author oyzh
 * @since 2023/07/07
 */
@Slf4j
@UtilityClass
public class RedisExportUtil {

    /**
     * redis键数据生成json字符串
     *
     * @param nodes        redis键数据
     * @param charset      字符集
     * @param prettyFormat 美化
     * @return 数据json字符串
     */
    public static String nodesToJSON(@NonNull List<RedisKey> nodes, String charset, boolean prettyFormat) {
        Project project = SpringUtil.getBean(Project.class);
        String version = project.getVersion();
        String platform = OSUtil.getOSType();
        RedisNodeExport export = new RedisNodeExport();
        // 元信息
        export.setNodes(new ArrayList<>());
        export.setVersion(version);
        export.setCharset(charset);
        export.setPlatform(platform);
        // 拼接数据
        for (RedisKey n : nodes) {
            Map<String, Object> node = new HashMap<>();
            node.put("key", n.key());
            node.put("type", n.type().toString());
            node.put("dbIndex", n.dbIndex());
            if (n.ttl() != null) {
                node.put("ttl", n.ttl());
            }
            String value = RedisKeyUtil.serializeNode(n);
            if (value != null) {
                node.put("value", value);
            }
            export.getNodes().add(node);
        }
        return export.toJSONString(prettyFormat);
    }

    /**
     * 从文件生成
     *
     * @param file 文件
     * @return RedisNodeExport
     */
    public static RedisNodeExport fromFile(@NonNull File file) {
        String text = FileUtil.readUtf8String(file);
        return fromJSON(text);
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return RedisNodeExport
     */
    public static RedisNodeExport fromJSON(@NonNull String json) {
        log.info("json: {}", json);
        JSONObject object = JSONObject.parseObject(json);
        RedisNodeExport export = new RedisNodeExport();
        export.setNodes(new ArrayList<>());
        export.setVersion(object.getString("version"));
        export.setPlatform(object.getString("platform"));
        JSONArray nodes = object.getJSONArray("nodes");
        for (Object n : nodes) {
            JSONObject o = (JSONObject) n;
            Map<String, Object> node = new HashMap<>();
            node.put("ttl", o.getLong("ttl"));
            node.put("key", o.getString("key"));
            node.put("type", o.getString("type"));
            node.put("value", o.getString("value"));
            node.put("dbIndex", o.getInteger("dbIndex"));
            export.getNodes().add(node);
        }
        return export;
    }
}


