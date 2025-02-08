package cn.oyzh.easyredis.domain;


import cn.oyzh.fx.plus.domain.Setting;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * redis设置
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Data
@Table("t_setting")
@EqualsAndHashCode(callSuper = true)
public class RedisSetting extends Setting {

    /**
     * 键加载上限
     */
    @Column
    private Integer keyLoadLimit;

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
     * 记录每页限制
     */
    private Integer recordPageLimit;

    public void setRecordPageLimit(Integer recordPageLimit) {
        if (recordPageLimit == null || recordPageLimit <= 0) {
            this.recordPageLimit = 100;
        } else {
            this.recordPageLimit = recordPageLimit;
        }
    }

    public Integer getRecordPageLimit() {
        if (this.recordPageLimit == null || this.recordPageLimit <= 0) {
            return 100;
        }
        return this.recordPageLimit;
    }
}
