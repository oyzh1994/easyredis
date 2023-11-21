package cn.oyzh.easyredis.trees.zset;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.easyredis.trees.RedisRowKeyTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.GeoCoordinate;

import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
public class RedisZSetKeyTreeItem extends RedisRowKeyTreeItem<RedisZSetKey, RedisZSetRow> {

    /**
     * 当前分数
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private Double currentScore;

    /**
     * 当前经度
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private Double currentLatitude;

    /**
     * 当前纬度
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private Double currentLongitude;

    public void currentScore(Double currentScore) {
        this.currentScore = currentScore;
        this.flushGraphic();
        // this.flushGraphicColor();
    }

    public void currentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
        this.flushGraphic();
        // this.flushGraphicColor();
    }

    public void currentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
        this.flushGraphic();
        // this.flushGraphicColor();
    }

    /**
     * 显示类型 0: 有序集合 1: 地理坐标
     */
    private byte showType;

    @Override
    public RedisZSetKeyTreeItem currentRow(RedisZSetRow currentRow) {
        this.currentRow = currentRow;
        this.currentScore = null;
        this.currentLatitude = null;
        this.currentLongitude = null;
        this.flushGraphic();
        return this;
    }

    public RedisZSetKeyTreeItem(@NonNull RedisZSetKey value, @NonNull RedisConnectTreeItem root) {
        super(value, root);
        this.itemValue(new RedisZSetKeyTreeItemValue(this));
    }

    /**
     * 反转显示类型
     */
    public void reverseView() {
        this.showType = (byte) (this.isGEOView() ? 0 : 1);
    }

    /**
     * 是否地理坐标视图
     *
     * @return 结果
     */
    public boolean isGEOView() {
        return this.showType == 1;
    }

    @Override
    public boolean saveNodeValue() {
        String value = (String) this.unsavedNodeData();
        if (value == null) {
            value = this.currentRow.getValue();
        }
        try {
            this.setNodeValue(value);
            this.currentRow.setValue(value);
            if (this.isGEOView()) {
                if (this.currentLongitude != null) {
                    this.currentRow.setLongitude(this.currentLongitude);
                    this.currentLongitude(null);
                }
                if (this.currentLatitude != null) {
                    this.currentRow.setLatitude(this.currentLatitude);
                    this.currentLatitude(null);
                }
            } else {
                if (this.currentScore != null) {
                    this.currentRow.setScore(this.currentScore);
                    this.currentScore(null);
                }
            }
            this.clearUnsavedNodeData();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    protected void setNodeValue(Object value) {
        try {
            if (!Objects.equals(value, this.currentRow.getValue())) {
                this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
            }
            if (this.isGEOView()) {
                double latitude = this.currentLatitude != null ? this.currentLatitude : this.currentRow.getLatitude();
                double longitude = this.currentLongitude != null ? this.currentLongitude : this.currentRow.getLongitude();
                this.client().geoadd(this.dbIndex(), this.key(), longitude, latitude, (String) value);
            } else {
                double score = this.currentScore != null ? this.currentScore : this.currentRow.getScore();
                this.client().zadd(this.dbIndex(), this.key(), score, (String) value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public boolean deleteRow() {
        try {
            long count = this.client().zrem(this.dbIndex(), this.key(), this.currentRow.getValue());
            if (count > 0) {
                this.nodeValue().remove(this.currentRow);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void refreshNodeValue() {
        try {
            List<String> value = this.client().zrange(this.dbIndex(), this.key());
            if (this.isGEOView()) {
                List<GeoCoordinate> coordinates = this.client().geopos(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfCoordinate(value, coordinates);
            } else {
                List<Double> scores = this.client().zmscore_x(this.dbIndex(), this.key(), ArrayUtil.toArray(value, String.class));
                this.value.valueOfScore(value, scores);
            }
            this.unsavedNodeData(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public String rawValue() {
        if (this.currentRow != null) {
            return this.currentRow.getValue();
        }
        return null;
    }

    @Override
    public boolean checkExists() {
        if (this.unsavedNodeData() == null) {
            return false;
        }
        if (!Objects.equals(this.currentRow.getValue(), this.unsavedNodeData())) {
            Long zrank = this.client().zrank(this.dbIndex(), this.key(), (String) this.unsavedNodeData());
            return zrank != null;
        }
        return false;
    }

    /**
     * 数据是否已改变
     *
     * @return 结果
     */
    public boolean isChanged() {
        if (this.isGEOView()) {
            return this.unsavedNodeData() != null || this.currentLatitude != null || this.currentLongitude != null;
        }
        return this.unsavedNodeData() != null || this.currentScore() != null;
    }

    // @Override
    // protected void flushGraphicColor() {
    //     if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
    //         if (!this.isChanged() && glyph.getColor() != Color.BLACK) {
    //             glyph.setColor(Color.BLACK);
    //         } else if (this.isChanged() && glyph.getColor() != Color.ORANGERED) {
    //             glyph.setColor(Color.ORANGERED);
    //         }
    //     }
    // }

    @Override
    public RedisZSetKeyTreeItemValue itemValue() {
        return (RedisZSetKeyTreeItemValue) super.itemValue();
    }
}
