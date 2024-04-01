package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisZSetMemberAddedMsg extends Event<RedisZSetKeyTreeItem> implements  EventFormatter {

    @Setter
    private String key;

    @Setter
    private Double score;

    @Setter
    private String member;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键:%s(db%s) 新增成员:%s 分数:%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member, this.score
        );
    }
}
