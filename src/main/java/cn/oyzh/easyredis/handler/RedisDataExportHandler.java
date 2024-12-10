package cn.oyzh.easyredis.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import cn.oyzh.store.file.FileColumns;
import cn.oyzh.store.file.FileHelper;
import cn.oyzh.store.file.FileRecord;
import cn.oyzh.store.file.FileWriteConfig;
import cn.oyzh.store.file.TypeFileWriter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * @author oyzh
 * @since 2024/11/26
 */
@Setter
@Accessors(fluent = true, chain = false)
public class RedisDataExportHandler extends DataHandler {

    /**
     * 文件格式
     */
    private String fileType;

    /**
     * 客户端
     */
    private RedisClient client;

    /**
     * 数据库
     * null 全部
     * 其他 指定库
     */
    private Integer database;

    /**
     * 过滤内容列表
     */
    private List<RedisFilter> filters;

    /**
     * 批量处理大小
     */
    private int batchSize = 20;

    /**
     * 导出配置
     */
    private FileWriteConfig config = new FileWriteConfig();

    /**
     * 执行导出
     *
     * @throws Exception 异常
     */
    public void doExport() throws Exception {
        this.message("Export Starting");
        FileColumns columns = new FileColumns();
        columns.addColumn("path");
        columns.addColumn("data");
        // 获取写入器
        TypeFileWriter writer = FileHelper.initWriter(this.fileType, this.config, columns);
        if (writer != null) {
            // 批量记录
            List<FileRecord> batchList = new ArrayList<>(this.batchSize);
            // 批量写入函数
            Runnable writeBatch = () -> {
                try {
                    writer.writeRecords(batchList);
                    for (FileRecord record : batchList) {
                        this.message("export key[" + record.get(0) + "] success");
                    }
                    this.processedIncr(batchList.size());
                    batchList.clear();
                } catch (Exception ex) {
                    this.message("write data failed");
                    this.processedDecr();
                }
            };
            try {
                // 写入头
                writer.writeHeader();
                // 节点过滤
                Predicate<String> filter = key -> {
                    if (RedisKeyUtil.isFiltered(key, this.filters)) {
                        this.message("key[" + key + "] is filtered, skip it");
                        this.processedSkip();
                        return false;
                    }
                    return true;
                };

                // 获取节点成功
                BiConsumer<String, byte[]> success = (key, bytes) -> {
                    try {
                        this.checkInterrupt();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // 记录
                    FileRecord record = new FileRecord();
                    record.put(0, key);
                    record.put(1, new String(bytes, StandardCharsets.UTF_8));
                    // 添加到集合
                    batchList.add(record);

                    // 批量写入
                    if (batchList.size() >= this.batchSize) {
                        writeBatch.run();
                    }
                };
                // 获取节点失败
                BiConsumer<String, Exception> error = (key, ex) -> {
                    if (ex instanceof RuntimeException) {
                        ex = (Exception) ex.getCause();
                    } else {
                        ex.printStackTrace();
                    }
                    // 针对中断异常不处理
                    if (ex instanceof InterruptedException) {
                        return;
                    }
                    this.message("export key[" + key + "] failed");
                    this.processedDecr();
                };
            } finally {
                // 写入尾
                writeBatch.run();
                writer.writeTrial();
                writer.close();
                this.message("Exported To -> " + this.config.filePath());
            }
        } else {
            JulLog.error("未找到可用的写入器，文件类型:{}", this.fileType);
        }
        this.message("Export Finished");
    }

    public void prefix(String prefix) {
        if (prefix.isBlank()) {
            this.config.prefix(null);
        } else {
            this.config.prefix(prefix + " ");
        }
    }

    public void charset(String charset) {
        if (StringUtil.isBlank(charset)) {
            this.config.charset(StandardCharsets.UTF_8.name());
        } else {
            this.config.charset(charset);
        }
    }

    public void filePath(String filePath) {
        this.config.filePath(filePath);
    }

    public void txtIdentifier(Character txtIdentifier) {
        this.config.txtIdentifier(txtIdentifier);
    }
}

