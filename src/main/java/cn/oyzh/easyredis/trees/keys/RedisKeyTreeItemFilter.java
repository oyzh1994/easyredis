package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;
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
     * 0. 所有键
     * 1. 收藏键
     * 2. string
     * 3. list
     * 4. set
     * 5. zset
     * 6. hash
     * 7. stream
     */
    @Setter
    @Getter
    private byte type;

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
    private byte matchMode;

    /**
     * 过滤内容列表
     */
    private List<RedisFilter> filters;

    /**
     * 过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 初始化过滤配置
     */
    public void initFilters() {
        this.filters = this.filterStore.loadEnable();
    }

    @Override
    public boolean test(RichTreeItem<?> item) {
        // 根节点不参与过滤
        if (item instanceof RedisRootKeyTreeItem) {
            return true;
        }
        // 根节点直接展示
        if (item instanceof RedisKeyTreeItem treeItem) {
            // 仅收藏
            if (1 == this.type && !treeItem.isCollect()) {
                return false;
            }
            // string
            if (2 == this.type && !(treeItem instanceof RedisStringKeyTreeItem)) {
                return false;
            }
            // list
            if (3 == this.type && !(treeItem instanceof RedisListKeyTreeItem)) {
                return false;
            }
            // set
            if (4 == this.type && !(treeItem instanceof RedisSetKeyTreeItem)) {
                return false;
            }
            // zset
            if (5 == this.type && !(treeItem instanceof RedisZSetKeyTreeItem)) {
                return false;
            }
            // hash
            if (6 == this.type && !(treeItem instanceof RedisHashKeyTreeItem)) {
                return false;
            }
            // stream
            if (7 == this.type && !(treeItem instanceof RedisStreamKeyTreeItem)) {
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
