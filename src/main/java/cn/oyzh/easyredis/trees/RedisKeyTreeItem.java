package cn.oyzh.easyredis.trees;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.key.RedisKeyCopyController;
import cn.oyzh.easyredis.controller.key.RedisKeyMoveController;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.fx.value.RedisKeyTreeItemValue;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisKey;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/6/30
 */
@Slf4j
public abstract class RedisKeyTreeItem<V extends RedisKey> extends BaseTreeItem {

    /**
     * redis键
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected V value;

    /**
     * 可见标志位
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private volatile boolean visible;

    /**
     * 连接键
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected final RedisConnectTreeItem root;

    /**
     * 未保存的键数据属性
     */
    private SimpleObjectProperty<Object> unsavedNodeDataProperty;

    /**
     * 获取未保存的键数据属性
     *
     * @return 未保存的键数据属性
     */
    public SimpleObjectProperty<Object> unsavedNodeDataProperty() {
        if (this.unsavedNodeDataProperty == null) {
            this.unsavedNodeDataProperty = new SimpleObjectProperty<>();
        }
        return this.unsavedNodeDataProperty;
    }

    /**
     * 设置未保存的键数据
     *
     * @param unsavedNodeData 未保存的键数据
     */
    public void unsavedNodeData(Object unsavedNodeData) {
        this.unsavedNodeDataProperty().set(unsavedNodeData);
        this.flushGraphicColor();
    }

    /**
     * 获取未保存的键数据
     *
     * @return 未保存的键数据
     */
    public Object unsavedNodeData() {
        if (this.unsavedNodeDataProperty == null) {
            return null;
        }
        return this.unsavedNodeDataProperty.get();
    }

    /**
     * 清除未保存的键数据
     */
    public void clearUnsavedNodeData() {
        if (this.unsavedNodeDataProperty != null) {
            this.unsavedNodeDataProperty.set(null);
            this.flushGraphicColor();
        }
    }

    /**
     * 是否有未保存的键数据
     *
     * @return 结果
     */
    public boolean hasUnsavedNodeData() {
        if (this.unsavedNodeDataProperty == null) {
            return false;
        }
        return this.unsavedNodeDataProperty.get() != null;
    }

    // /**
    //  * 获取字符集
    //  *
    //  * @return 字符集
    //  */
    // public String getCharset() {
    //     return this.info().getCharset();
    // }
    //
    // /**
    //  * 设置字符集
    //  *
    //  * @param charset 字符集
    //  */
    // public void setCharset(String charset) {
    //     if (this.unsavedNodeDataProperty != null) {
    //         this.unsavedNodeDataProperty.set(TextUtil.changeCharset(this.unsavedNodeData(), this.getCharset(), charset));
    //     }
    //     this.info().setCharset(charset);
    // }

    /**
     * 子节点列表，记录用，非实际展示列表
     */
    private ObservableList<RedisKeyTreeItem<?>> children;

    /**
     * 获取子节点列表
     *
     * @return 子节点列表
     */
    public ObservableList<RedisKeyTreeItem<?>> children() {
        if (this.children == null) {
            this.children = FXCollections.observableArrayList();
        }
        return this.children;
    }

    public RedisKeyTreeItem(@NonNull V value, @NonNull RedisConnectTreeItem root) {
        this.root = root;
        this.treeView(root.treeView());
        RedisKeyTreeItemValue itemValue = new RedisKeyTreeItemValue(value);
        itemValue.type(value.type().toString());
        this.itemValue(itemValue);
        this.value = value;
    }

    /**
     * 父键是否展开
     *
     * @return 结果
     */
    public boolean isParentExpanded() {
        return this.getParent() != null && this.getParent().isExpanded();
    }

    @Override
    public void filter(@NonNull RedisTreeItemFilter filter) {
        this.visible = filter.apply(this);
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.itemValue().graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/treeNode.svg", 12);
            this.itemValue().graphic(glyph);
        }
    }

    /**
     * 刷新图标颜色
     */
    protected void flushGraphicColor() {
        if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
            if (!this.hasUnsavedNodeData() && glyph.getColor() != Color.BLACK) {
                glyph.setColor(Color.BLACK);
            } else if (this.hasUnsavedNodeData() && glyph.getColor() != Color.ORANGERED) {
                glyph.setColor(Color.ORANGERED);
            }
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem rename = MenuItemExt.newItem("重命名键", new SVGGlyph("/font/edit-square.svg", "12"), "更改键名称(快捷键f2)", this::rename);
        MenuItem delete = MenuItemExt.newItem("删除此键", new SVGGlyph("/font/delete.svg", "12"), "删除此键(快捷键delete)", this::delete);
        MenuItem moveKey = MenuItemExt.newItem("移动此键", new SVGGlyph("/font/move.svg", "12"), "移动此键到其他库", this::moveKey);
        MenuItem copyKey = MenuItemExt.newItem("复制此键", new SVGGlyph("/font/copy.svg", "12"), "复制此键到其他库", this::copyKey);
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
        StageWrapper fxView = StageUtil.parseStage(RedisKeyMoveController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 复制键
     */
    private void copyKey() {
        StageWrapper fxView = StageUtil.parseStage(RedisKeyCopyController.class, this.window());
        fxView.setProp("treeItem", this);
        fxView.display();
    }

    /**
     * 当前节点的db节点
     *
     * @return db节点
     */
    public RedisDBTreeItem parent() {
        return (RedisDBTreeItem) this.getParent();
    }

    /**
     * 当前节点的连接节点
     *
     * @return 连接节点
     */
    public RedisConnectTreeItem connectTreeItem() {
        return this.parent().parent;
    }

    @Override
    public void removeChild(@NonNull TreeItem<?> item) {
        if (!this.isChildEmpty()) {
            super.removeChild(item);
            this.children.remove(item);
        }
    }

    @Override
    public void removeChildes(@NonNull List<TreeItem<?>> items) {
        if (!this.isChildEmpty()) {
            super.removeChildes(items);
            this.children.removeAll(items);
        }
    }

    @Override
    public boolean isChildEmpty() {
        if (this.children != null) {
            return this.children.isEmpty();
        }
        return true;
    }

    /**
     * redis信息
     *
     * @return redis信息
     */
    public RedisInfo info() {
        return this.root().value();
    }

    /**
     * 获取redis连接名称
     *
     * @return redis连接名称
     */
    public String infoName() {
        return this.info().getName();
    }

    /**
     * 获取db索引
     *
     * @return db索引值
     */
    public int dbIndex() {
        return this.parent().dbIndex();
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
        return this.root.client();
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
        return this.info().isCollect(this.dbIndex(), this.key());
    }

    /**
     * 收藏键
     */
    public void collect() {
        this.info().addCollect(this.dbIndex(), this.key());
        RedisInfoStore.INSTANCE.update(this.info());
        MessageBox.okToast("键已收藏");
    }

    /**
     * 取消收藏键
     *
     * @param tips 提示
     */
    public void unCollect(boolean tips) {
        if (this.info().removeCollect(this.dbIndex(), this.key())) {
            RedisInfoStore.INSTANCE.update(this.info());
            this.treeView().filterItem();
            if (tips) {
                MessageBox.okToast("键已取消收藏");
            }
        }
    }

    @Override
    public void delete() {
        try {
            // 删除此键
            this.client().del(this.dbIndex(), this.key());
            // 取消此键的收藏
            this.unCollect(false);
            // 移除此键
            this.remove();
            // 发送事件
            EventUtil.fire(RedisEventTypes.REDIS_KEY_DELETED, this.parent());
            MessageBox.okToast("键已删除");
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public RedisKeyTreeItemValue itemValue() {
        return (RedisKeyTreeItemValue) super.itemValue();
    }

    @Override
    public void rename() {
        String newKey = MessageBox.prompt("请输入新的键名称", this.value.key());

        // 名称为空或者跟当前名称相同，则忽略
        if (StrUtil.isBlank(newKey) || Objects.equals(newKey, this.value.key())) {
            return;
        }

        // 键已存在
        if (this.client().exists(this.dbIndex(), newKey)) {
            MessageBox.warn("键名称[" + newKey + "]已经存在！");
            return;
        }

        try {
            String result = this.client().rename(this.dbIndex(), this.key(), newKey);
            if (StrUtil.equalsIgnoreCase(result, "OK")) {
                this.value().key(newKey);
                this.itemValue().nodeNameText().setText(newKey);
                EventUtil.fire(RedisEventTypes.REDIS_KEY_RENAMED, this);
            } else {
                MessageBox.warn("更改键名称失败！");
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

    // /**
    //  * 获取json数据
    //  *
    //  * @return json数据
    //  */
    // public String jsonValue() {
    //     String rawValue = this.rawValue();
    //     if (!rawValue.contains("{") && !rawValue.contains("[")) {
    //         return rawValue;
    //     }
    //     try {
    //         JSONObject json = JSON.parseObject(rawValue, Feature.OrderedField);
    //         if (json != null) {
    //            return JSONObject.toJSONString(json, true);
    //         }
    //     } catch (JSONException ignore) {
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return rawValue;
    // }
    //
    // /**
    //  * 获取二进制数据
    //  *
    //  * @return 二进制数据
    //  */
    // public String binaryValue() {
    //     return StringUtil.toBinary(this.rawValue());
    // }
}
