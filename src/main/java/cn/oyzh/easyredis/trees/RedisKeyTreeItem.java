package cn.oyzh.easyredis.trees;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.key.RedisKeyCopyController;
import cn.oyzh.easyredis.controller.key.RedisKeyMoveController;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.store.RedisConnectJdbcStore;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisDBTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/6/30
 */
public abstract class RedisKeyTreeItem<K extends RedisKey, V extends RedisKeyTreeItemValue> extends RedisTreeItem<V> {

    /**
     * redis键
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected K value;

    /**
     * db树组件
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected RedisDBTreeItem dbItem;

    /**
     * 键数据属性
     */
    private SimpleObjectProperty<Object> dataProperty;

    /**
     * 获取未保存的键数据属性
     *
     * @return 未保存的键数据属性
     */
    public SimpleObjectProperty<Object> dataProperty() {
        if (this.dataProperty == null) {
            this.dataProperty = new SimpleObjectProperty<>();
        }
        return this.dataProperty;
    }

    /**
     * 设置键数据
     *
     * @param data 未键数据
     */
    public void data(Object data) {
        this.dataProperty().set(data);
    }

    /**
     * 获取键数据
     *
     * @return 键数据
     */
    public Object data() {
        if (this.dataProperty == null) {
            return null;
        }
        return this.dataProperty.get();
    }

    /**
     * 清除键数据
     */
    public void clearData() {
        if (this.dataProperty != null) {
            this.dataProperty.set(null);
            // this.flushGraphic();
        }
    }

    /**
     * 数据是否未保存
     *
     * @return 结果
     */
    public boolean dataUnsaved() {
        if (this.dataProperty == null) {
            return false;
        }
        return this.dataProperty.get() != null;
    }

    public RedisKeyTreeItem(@NonNull K value, @NonNull RedisDBTreeItem dbItem) {
        super(dbItem.getTreeView());
        this.dbItem = dbItem;
        this.value = value;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem rename =  MenuItemHelper.renameKey("12", this::rename);
        FXMenuItem delete = MenuItemHelper.deleteKey("12", this::delete);
        FXMenuItem moveKey = MenuItemHelper.moveKey("12", this::moveKey);
        FXMenuItem copyKey = MenuItemHelper.copyKey("12", this::copyKey);
        items.add(rename);
        items.add(moveKey);
        items.add(copyKey);
        items.add(delete);
        return items;
    }

    /**
     * 移动键
     */
    private void moveKey() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyMoveController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 复制键
     */
    private void copyKey() {
        StageAdapter fxView = StageManager.parseStage(RedisKeyCopyController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 当前节点的连接节点
     *
     * @return 连接节点
     */
    public RedisConnectTreeItem connectTreeItem() {
        if (this.dbItem != null) {
            return this.dbItem.parent();
        }
        return null;
    }

    /**
     * redis信息
     *
     * @return redis信息
     */
    public RedisConnect info() {
        if (this.dbItem != null) {
            return this.dbItem.info();
        }
        return null;
    }

    /**
     * 获取redis连接名称
     *
     * @return redis连接名称
     */
    public String infoName() {
        if (this.dbItem != null) {
            return this.info().getName();
        }
        return null;
    }

    /**
     * 获取db索引
     *
     * @return db索引值
     */
    public int dbIndex() {
        if (this.dbItem != null) {
            return this.dbItem.dbIndex();
        }
        return -1;
    }

    /**
     * 获取键名称
     *
     * @return 键名称
     */
    public String key() {
        return this.value.key();
    }

    /**
     * 获取键二进制名称
     *
     * @return 键二进制名称
     */
    public byte[] keyBinary() {
        return this.value.keyBinary();
    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        if (this.dbItem != null) {
            return this.dbItem.client();
        }
        return null;
    }

    /**
     * 保存节点值
     *
     * @return 结果
     */
    public boolean saveNodeValue() {
        return false;
    }

    /**
     * 设置节点值
     *
     * @param value 值
     */
    protected void setNodeValue(Object value) {
    }

    /**
     * 刷新节点值
     */
    public void refreshNodeValue() {
    }

    /**
     * 键是否被收藏
     */
    public boolean isCollect() {
        if (this.info() != null) {
            return this.info().isCollect(this.dbIndex(), this.key());
        }
        return false;
    }

    /**
     * 收藏键
     */
    public void collect() {
        this.info().addCollect(this.dbIndex(), this.key());
        RedisConnectJdbcStore.INSTANCE.update(this.info());
    }

    /**
     * 取消收藏键
     */
    public void unCollect() {
        if (this.info().removeCollect(this.dbIndex(), this.key())) {
            RedisConnectJdbcStore.INSTANCE.update(this.info());
            this.doFilter();
        }
    }

    @Override
    public void delete() {
        try {
            if (!MessageBox.confirm(I18nHelper.deleteKey() + " " + this.key())) {
                return;
            }
            // 删除此键
            this.client().del(this.dbIndex(), this.key());
            // 取消此键的收藏
            this.unCollect();
            // 移除此键
            this.remove();
            // 发送事件
            RedisEventUtil.keyDeleted(this.dbItem, this.key());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void rename() {
        String newKey = MessageBox.prompt(I18nHelper.contentTip1(), this.value.key());
        // 名称为空或者跟当前名称相同，则忽略
        if (StrUtil.isBlank(newKey) || Objects.equals(newKey, this.value.key())) {
            return;
        }
        // 键已存在
        if (this.client().exists(this.dbIndex(), newKey)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        try {
            String oldKey = this.key();
            String result = this.client().rename(this.dbIndex(), this.key(), newKey);
            if (StrUtil.equalsIgnoreCase(result, "OK")) {
                this.value().key(newKey);
                // this.getValue().name(newKey);
                this.refresh();
                RedisEventUtil.keyRenamed(this, oldKey);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 获取当前ttl值
     *
     * @return 当前ttl值
     */
    public Long ttl() {
        try {
            this.value.ttl(this.client().ttl(this.dbIndex(), this.key()));
            return this.value.ttl();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return -1L;
    }

    /**
     * 键是否过期
     *
     * @return 结果
     */
    public boolean isExpire() {
        if (this.value.ttl() == null) {
            this.ttl();
        }
        if (this.value.ttl() == null) {
            return false;
        }
        return this.value.ttl() == -2;
    }

    /**
     * 获取原始数据
     *
     * @return 原始数据
     */
    public abstract Object rawValue();

    /**
     * 获取键类型
     *
     * @return 键类型
     */
    public RedisKeyType type() {
        return this.value.type();
    }

    /**
     * 获取加载耗时
     *
     * @return 加载耗时
     */
    public short loadTime() {
        return this.value.loadTime() == 0 ? 1 : this.value.loadTime();
    }

    /**
     * 删除键，当键已过期
     */
    public void deleteByExpired() {
        try {
            this.unCollect();
            this.remove();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取内存占用
     *
     * @return 内存占用
     */
    public Long memoryUsage() {
        try {
            return this.client().memoryUsage(this.dbIndex(), this.key());
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1L;
        }
    }
}
