package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;

import java.util.Collection;

/**
 * @author oyzh
 * @since 2025/02/07
 */
public class RedisQueryDataTab extends DynamicTab {

    public RedisQueryDataTab(Collection<?> list) {
        super();
        super.flush();
        this.controller().init(list);
    }

    public RedisQueryDataTab(Object object) {
        super();
        super.flush();
        this.controller().init(object);
    }

    @Override
    protected String url() {
        return "/tabs/query/redisQueryDataTab.fxml";
    }

    @Override
    protected RedisQueryDataTabController controller() {
        return (RedisQueryDataTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.data();
    }
}
