package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisZSetMemberAddedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_ZSET_MEMBER_ADDED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisZSetKeyTreeItem item;

    @Setter
    private String key;

    @Setter
    private Double score;

    @Setter
    private String member;

    @Override
    public String formatMsg() {
        return String.format(
                "[%s] 键:%s(db%s) 新增成员:%s 分数:%s",
                this.item.infoName(), this.key, this.item.dbIndex(), this.member, this.score
        );
    }
}
