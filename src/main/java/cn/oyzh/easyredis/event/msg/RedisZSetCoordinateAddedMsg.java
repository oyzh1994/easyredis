package cn.oyzh.easyredis.event.msg;

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
public class RedisZSetCoordinateAddedMsg extends Event<RedisZSetKeyTreeItem> implements  EventFormatter {

    private final String name = RedisEventTypes.REDIS_ZSET_COORDINATE_ADDED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private String key;

    @Setter
    private Double longitude;

    @Setter
    private Double latitude;

    @Setter
    private String member;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键:%s(db%s) 新增坐标:%s 经度:%s 纬度:%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member, this.longitude, this.latitude
        );
    }
}
