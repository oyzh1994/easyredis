package cn.oyzh.easyredis.query;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * zk查询参数
 *
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryParam {

    /**
     * db索引
     */
    @Getter
    @Setter
    private int dbIndex;

    /**
     * 内容
     */
    @Getter
    private String content;

    /**
     * 参数
     */
    private List<String> params;

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
        String[] arr = this.content.trim().split(" ");
        this.params = new ArrayList<>();
        for (String s : arr) {
            if (!s.isBlank()) {
                this.params.add(s);
            }
        }
    }

    public String getCommand() {
        return this.params.getFirst();
    }
}
