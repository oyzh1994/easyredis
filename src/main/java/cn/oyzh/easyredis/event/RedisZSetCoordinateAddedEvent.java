package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisZSetCoordinateAddedEvent extends Event<RedisZSetKeyTreeItem> implements EventFormatter {

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
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.coordinatesAdded() + ":%s " + I18nHelper.longitude() + ":%s " + I18nHelper.latitude() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member, this.longitude, this.latitude
        );
    }
}
