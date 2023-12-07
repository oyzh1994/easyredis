package cn.oyzh.easyredis.trees;

import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.search.RedisSearchHandler;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisFilterStore;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Lazy
@Component
public class RedisTreeItemFilter implements RichTreeItemFilter {

    /**
     * 排除set键
     */
    @Setter
    @Getter
    private boolean excludeSetType;

    /**
     * 排除list键
     */
    @Setter
    @Getter
    private boolean excludeListType;

    /**
     * 排除zset键
     */
    @Setter
    @Getter
    private boolean excludeZSetType;

    /**
     * 排除hash键
     */
    @Setter
    @Getter
    private boolean excludeHashType;

    /**
     * 排除string键
     */
    @Setter
    @Getter
    private boolean excludeStringType;

    /**
     * 排除hyperLogLog键
     */
    @Setter
    @Getter
    private boolean excludeHyLogType;

    /**
     * 排除stream键
     */
    @Setter
    @Getter
    private boolean excludeStreamType;

    /**
     * 仅看收藏键
     */
    @Setter
    @Getter
    private boolean onlyCollect;

    /**
     * redis主页搜索处理
     */
    @Autowired
    private RedisSearchHandler searchHandler;

    /**
     * 过滤内容列表
     */
    private final List<RedisFilter> filters = new ArrayList<>();

    /**
     * 过滤配置储存
     */
    private final RedisFilterStore filterStore = RedisFilterStore.INSTANCE;

    /**
     * 初始化过滤配置
     */
    public void initFilters() {
        this.filters.clear();
        this.filters.addAll(this.filterStore.loadEnable());
    }

    @Override
    public boolean  test(RichTreeItem<?> item) {
        if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
            RedisKey node = treeItem.value();
            // 仅看收藏
            if (this.onlyCollect && !treeItem.isCollect()) {
                return false;
            }
            // 过滤hash键
            if (this.excludeHashType && node.isHashKey()) {
                return false;
            }
            // 过滤list键
            if (this.excludeListType && node.isListKey()) {
                return false;
            }
            // 过滤set键
            if (this.excludeSetType && node.isSetKey()) {
                return false;
            }
            // 过滤zset键
            if (this.excludeZSetType && node.isZSetKey()) {
                return false;
            }
            // 过滤string键
            if (this.excludeStringType && node.isStringKey()) {
                return false;
            }
            // 过滤stream键
            if (this.excludeStreamType && node.isStreamKey()) {
                return false;
            }
            // 过滤hyperLogLog键
            if (this.excludeHyLogType && node.isHyLogKey()) {
                return false;
            }
            // 过滤键
            if (RedisKeyUtil.isFiltered(treeItem.key(), this.filters)) {
                return false;
            }
        }
        // 判断是否满足搜索要求
        RedisSearchParam param = this.searchHandler.searchParam();
        if (param != null&& !param.isEmpty() && param.isFilterMode() ) {
            return this.searchHandler.getMatchType(item) != null;
        }
        return true;
    }

}
