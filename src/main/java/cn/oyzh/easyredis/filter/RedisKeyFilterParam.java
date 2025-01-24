package cn.oyzh.easyredis.filter;

import lombok.Data;


/**
 * @author oyzh
 * @since 2025/01/24
 */
@Data
public class RedisKeyFilterParam {

    /**
     * 匹配大小写
     */
    private boolean matchCase;

    /**
     * 匹配全文
     */
    private boolean matchFull;

    /**
     * 搜索键
     */
    private boolean searchKey = true;

    /**
     * 搜索数据
     */
    private boolean searchData = true;

    @Override
    public boolean equals(Object param) {
        if (param == this) {
            return true;
        }
        if (param instanceof RedisKeyFilterParam searchParam) {
            if (searchParam.matchCase && !this.matchCase) {
                return false;
            }
            if (searchParam.matchFull && !this.matchFull) {
                return false;
            }
            if (searchParam.searchData && !this.searchData) {
                return false;
            }
            return !searchParam.searchKey || this.searchKey;
        }
        return false;
    }
}
