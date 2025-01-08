package cn.oyzh.easyredis.controller.tool;

import cn.oyzh.common.util.NumberUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;


/**
 * redis工具箱业务
 *
 * @author oyzh
 * @since 2025/01/08
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "tool/redisTool.fxml"
)
public class RedisToolController extends StageController {

    /**
     * 缓存文本域
     */
    @FXML
    private FlexTextArea cacheArea;

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.tools();
    }

    /**
     * 计算缓存
     */
    @FXML
    private void calcCache() {
        this.disable();
        try {
            this.cacheArea.setText("calc cache start.");
            File dir = new File(RedisConst.CACHE_PATH);
            this.doCalcCache(dir, new AtomicInteger(0), new LongAdder());
            this.cacheArea.appendLine("calc cache finish.");
        } finally {
            this.enable();
        }
    }

    /**
     * 计算缓存
     *
     * @param file      文件
     * @param fileCount 文件总数
     * @param fileSize  文件大小
     */
    private void doCalcCache(File file, AtomicInteger fileCount, LongAdder fileSize) {
        if (file.isFile()) {
            fileSize.add(file.length());
            fileCount.incrementAndGet();
            String sizeInfo = NumberUtil.formatSize(fileSize.longValue());
            this.cacheArea.setText("find file: " + fileCount.get() + " total size: " + sizeInfo);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    this.doCalcCache(file1, fileCount, fileSize);
                }
            }
        }
    }

    /**
     * 清理缓存
     */
    @FXML
    private void clearCache() {
        try {
            this.cacheArea.setText("clear cache start.");
            File dir = new File(RedisConst.CACHE_PATH);
            this.doClearCache(dir);
            this.cacheArea.appendLine("clear cache finish.");
        } finally {
            this.enable();
        }
    }

    /**
     * 清理缓存
     *
     * @param file 文件
     */
    private void doClearCache(File file) {
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    this.doClearCache(file1);
                }
            }
        }
    }
}
