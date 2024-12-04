package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.store.RedisFilterJdbcStore;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/06/30
 */
public class RedisKeyTreeItemFilter implements RichTreeItemFilter {

    /**
     * 0. 所有节点
     * 1. 收藏节点
     * 2. 持久节点
     */
    @Setter
    @Getter
    private int type;

    /**
     * 关键字
     */
    @Getter
    @Setter
    private String kw;

    /**
     * 0. 包含
     * 1. 包含 + 大小写符合
     * 2. 全字匹配
     * 3. 全字匹配 + 大小写符合
     */
    @Getter
    @Setter
    private int matchMode;

    /**
     * 过滤内容列表
     */
    private List<RedisFilter> filters;

    /**
     * 过滤配置储存
     */
    private final RedisFilterJdbcStore filterStore = RedisFilterJdbcStore.INSTANCE;

    /**
     * 初始化过滤配置
     */
    public void initFilters() {
        this.filters = this.filterStore.loadEnable();
    }

    @Override
    public boolean test(RichTreeItem<?> item) {
            // 根节点不参与过滤
        if(item instanceof RedisKeyRootTreeItem){
            return true;
        }
        // 根节点直接展示
        if (item instanceof RedisKeyTreeItem<?> treeItem) {
            // 仅收藏
            if (1 == this.type && !treeItem.isCollect()) {
                return false;
            }
            String key = treeItem.key();
            // 过滤节点
            if (RedisKeyUtil.isFiltered(key, this.filters)) {
                return false;
            }
            if (StringUtil.isNotBlank(this.kw)) {
                if (this.matchMode == 0) {
                    return StringUtil.containsIgnoreCase(key, this.kw);
                }
                if (this.matchMode == 1) {
                    return StringUtil.contains(key, this.kw);
                }
                if (this.matchMode == 2) {
                    return StringUtil.equalsIgnoreCase(key, this.kw);
                }
                if (this.matchMode == 3) {
                    return StringUtil.equals(key, this.kw);
                }
            }
        }
        return true;
    }

}
