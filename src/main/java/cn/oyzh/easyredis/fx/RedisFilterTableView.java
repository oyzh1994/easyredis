package cn.oyzh.easyredis.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.dto.RedisFilterVO;
import cn.oyzh.fx.plus.controls.table.FlexTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-31
 */
public class RedisFilterTableView extends FlexTableView<RedisFilterVO> {

    /**
     * 当前过滤列表
     */
    private List<RedisFilterVO> list;

    /**
     * 关键字
     */
    private String kw;

    public boolean hasData(){
        return list != null;
    }

    public void setFilters(List<RedisFilter> filters) {
        this.list = RedisFilterVO.convert(filters);
        this.initDataList();
    }

    public void setKw(String kw) {
        this.kw = kw;
        this.initDataList();
    }

    public List<RedisFilter> getFilters() {
        List<RedisFilter> list = new ArrayList<>(this.list.size());
        for (RedisFilterVO filterVO : this.list) {
            if (filterVO != null && StringUtil.isNotBlank(filterVO.getKw())) {
                list.add(filterVO);
            }
        }
        return list;
    }

    private void initDataList() {
        List<RedisFilterVO> list = new ArrayList<>(12);
        if (this.list != null) {
            for (RedisFilterVO filter : this.list) {
                if (StringUtil.isBlank(this.kw) || StringUtil.containsIgnoreCase(filter.getKw(), this.kw)) {
                    list.add(filter);
                }
            }
        }
        super.setItem(list);
    }

    public void addFilter(RedisFilterVO filter) {
        if (this.list == null) {
            this.list = new ArrayList<>(12);
        }
        this.list.add(filter);
        this.initDataList();
    }

    @Override
    public void removeItem(Object item) {
        super.removeItem(item);
        if (this.list != null) {
            this.list.remove(item);
        }
        this.initDataList();
    }
}
