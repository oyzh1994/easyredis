package cn.oyzh.easyredis.dto;

import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.fx.common.Index;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * redis过滤vo信息
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RedisFilterVO extends RedisFilter implements Index {

    /**
     * 索引
     */
    private int index;

    /**
     * 复制
     *
     * @param filter redis过滤信息
     * @param index  索引
     * @return redis认证vo
     */
    public static RedisFilterVO copy(RedisFilter filter, int index) {
        RedisFilterVO authVO = new RedisFilterVO();
        authVO.copy(filter);
        authVO.setIndex(index);
        return authVO;
    }

    /**
     * 转换
     *
     * @param list redis过滤列表
     * @return redis过滤vo列表
     */
    public static List<RedisFilterVO> convert(@NonNull List<RedisFilter> list) {
        List<RedisFilterVO> voList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            voList.add(copy(list.get(i), i + 1));
        }
        return voList;
    }
}
