package cn.oyzh.easyredis.domain;


import cn.oyzh.fx.plus.domain.AppSetting;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;

/**
 * redis设置
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Table("t_setting")
public class RedisSetting extends AppSetting {

    public Integer getKeyLoadLimit() {
        return keyLoadLimit;
    }

    public void setKeyLoadLimit(Integer keyLoadLimit) {
        this.keyLoadLimit = keyLoadLimit;
    }

    /**
     * 键加载上限
     */
    @Column
    private Integer keyLoadLimit;

    @Override
    public void copy(Object o) {
        super.copy(o);
        if (o instanceof RedisSetting setting) {
            this.keyLoadLimit = setting.keyLoadLimit;
        }
    }

    // /**
    //  * 搜索-更多-展开状态
    //  * 0|null 不展开
    //  * 1 展开
    //  */
    // private Byte searchMoreExpand;
    //
    // /**
    //  * 是否展开搜索-更多
    //  *
    //  * @return 结果
    //  */
    // public boolean isSearchMoreExpand() {
    //     return this.searchMoreExpand != null && this.searchMoreExpand == 1;
    // }

    public int keyLoadLimit() {
        return this.keyLoadLimit == null ? 0 : this.keyLoadLimit;
    }

//    public int calcLimit(int limit, int count) {
//        if (this.keyLoadLimit() == 0) {
//            return limit;
//        }
//        int size = this.keyLoadLimit() - count;
//        return Math.min(size, limit);
//    }

    /**
     * 行页码限制
     */
    @Column
    private Integer rowPageLimit;

    public void setRowPageLimit(Integer rowPageLimit) {
        if (rowPageLimit == null || rowPageLimit <= 0) {
            this.rowPageLimit = 100;
        } else {
            this.rowPageLimit = rowPageLimit;
        }
    }

    public Integer getRowPageLimit() {
        if (this.rowPageLimit == null || this.rowPageLimit <= 0) {
            return 100;
        }
        return this.rowPageLimit;
    }
}
