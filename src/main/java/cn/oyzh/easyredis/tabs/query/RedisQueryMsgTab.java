package cn.oyzh.easyredis.tabs.query;

import cn.oyzh.easyredis.query.RedisQueryParam;
import cn.oyzh.easyredis.query.RedisQueryResult;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryMsgTab extends RichTab {

    public RedisQueryMsgTab(RedisQueryParam param, RedisQueryResult result) {
        super();
        super.flush();
        this.controller().init(param, result);
    }

    @Override
    protected String url() {
        return "/tabs/query/redisQueryMsgTab.fxml";
    }

    @Override
    protected RedisQueryMsgTabController controller() {
        return (RedisQueryMsgTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.message();
    }


}
