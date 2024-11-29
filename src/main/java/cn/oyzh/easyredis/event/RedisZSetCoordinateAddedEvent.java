package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisZSetCoordinateAddedEvent extends Event<RedisZSetKeyTreeItem> implements EventFormatter {

    private String key;

    private Double longitude;

    private Double latitude;

    private String member;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.coordinatesAdded() + ":%s " + I18nHelper.longitude() + ":%s " + I18nHelper.latitude() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member, this.longitude, this.latitude
        );
    }
}
