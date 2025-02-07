package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.easyredis.query.RedisQueryParam;
import cn.oyzh.easyredis.query.RedisQueryResult;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryDataTab extends DynamicTab {

    public RedisQueryDataTab(Collection<?> list) {
        super();
        super.flush();
        this.controller().init(list);
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
