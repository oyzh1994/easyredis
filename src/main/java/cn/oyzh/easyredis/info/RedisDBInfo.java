package cn.oyzh.easyredis.info;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author oyzh
 * @since 2023/6/30
 */
@Data
public class RedisDBInfo {

    private int keys;

    private int index;

    private int expires;

    private double avgTTL;

    public static RedisDBInfo parse(String str) {
        RedisDBInfo dbInfo = new RedisDBInfo();
        if (StrUtil.isNotBlank(str)) {
            str = str.substring(2);
            String indexStr = str.substring(0, str.indexOf(":"));
            dbInfo.index = Integer.parseInt(indexStr);
            str = str.substring(str.indexOf(":") + 1);
            String[] strs = str.split(",");
            for (String s : strs) {
                if (s.startsWith("keys=")) {
                    dbInfo.keys = Integer.parseInt(s.replace("keys=", ""));
                } else if (s.startsWith("expires=")) {
                    dbInfo.expires = Integer.parseInt(s.replace("expires=", ""));
                } else if (s.startsWith("avg_ttl=")) {
                    dbInfo.avgTTL = Integer.parseInt(s.replace("avg_ttl=", ""));
                }
            }
        }
        return dbInfo;
    }
}
