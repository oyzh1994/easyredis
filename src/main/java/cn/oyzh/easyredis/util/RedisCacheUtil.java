package cn.oyzh.easyredis.util;

import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.easyredis.RedisConst;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * zk缓存工具类
 *
 * @author oyzh
 * @since 2024-11-25
 */
@UtilityClass
public class RedisCacheUtil {

    private static String baseDir(int hashCode) {
        return RedisConst.KEY_CACHE_PATH + hashCode;
    }

    // private static String baseDir(int hashCode, String path) {
    //     return RedisConst.KEY_CACHE_PATH + hashCode + "_" + MD5Util.md5Hex(path);
    // }
    //
    // /**
    //  * 缓存未保存数据
    //  *
    //  * @param hashCode hash码
    //  * @param key      路径
    //  * @param redisKey 数据
    //  * @return 缓存结果
    //  */
    // public static boolean cacheKeyValue(int hashCode, String key, RedisKeyValue<?> redisKey) {
    //     if (redisKey != null) {
    //         try {
    //             String baseDir = baseDir(hashCode, key);
    //             String fileName = baseDir + ".data";
    //             FileUtil.touch(fileName);
    //             byte[] bytes = RedisKeyValue.serializeOf(redisKey);
    //             FileUtil.writeBytes(bytes, fileName);
    //             return true;
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    //     return deleteKeyValue(hashCode, key);
    // }
    //
    // /**
    //  * 加载未保存数据
    //  *
    //  * @param hashCode hash码
    //  * @param key      路径
    //  * @return 未保存数据
    //  */
    // public static RedisKeyValue<?> loadKeyValue(int hashCode, String key) {
    //     try {
    //         String baseDir = baseDir(hashCode, key);
    //         String fileName = baseDir + ".data";
    //         if (FileUtil.exist(fileName)) {
    //             byte[] bytes = FileUtil.readBytes(fileName);
    //             return RedisKeyValue.deserializeOf(bytes);
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 删除未保存数据
    //  *
    //  * @param hashCode hash码
    //  * @param key      路径
    //  */
    // public static boolean deleteKeyValue(int hashCode, String key) {
    //     try {
    //         String baseDir = baseDir(hashCode, key);
    //         String fileName = baseDir + ".data";
    //         FileUtil.del(fileName);
    //         return true;
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return false;
    // }
    //
    // /**
    //  * 是否有未保存数据
    //  *
    //  * @param hashCode hash码
    //  * @param key      路径
    //  * @return 结果
    //  */
    // public static boolean hasKeyValue(int hashCode, String key) {
    //     try {
    //         String baseDir = baseDir(hashCode, key);
    //         String fileName = baseDir + ".data";
    //         File file = new File(fileName);
    //         return file.exists();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    //     return false;
    // }


    /**
     * 缓存未保存数据
     *
     * @param hashCode hash码
     * @return 缓存结果
     */
    public static boolean cacheValue(int hashCode, Object value) {
        if (value != null) {
            try {
                String baseDir = baseDir(hashCode);
                String fileName = baseDir + ".value";
                FileUtil.touch(fileName);
                byte type = 0;
                byte[] bytes = new byte[0];
                if (value instanceof String s) {
                    bytes = s.getBytes(StandardCharsets.UTF_8);
                    type = 1;
                } else if (value instanceof byte[] s) {
                    bytes = s;
                    type = 2;
                } else if (value instanceof Collection) {
                    bytes = JSONUtil.toJson(value).getBytes(StandardCharsets.UTF_8);
                    type = 3;
                } else if (value instanceof Map<?, ?>) {
                    bytes = JSONUtil.toJson(value).getBytes(StandardCharsets.UTF_8);
                    type = 4;
                }
                byte[] bytes1 = new byte[bytes.length + 1];
                ArrayUtil.copy(bytes, bytes1);
                bytes1[bytes.length] = type;
                FileUtil.writeBytes(bytes1, fileName);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return deleteValue(hashCode);
    }

    /**
     * 加载未保存数据
     *
     * @param hashCode hash码
     * @return 未保存数据
     */
    public static Object loadValue(int hashCode) {
        try {
            String baseDir = RedisConst.KEY_CACHE_PATH + hashCode;
            String fileName = baseDir + ".value";
            if (FileUtil.exist(fileName)) {
                byte[] bytes = FileUtil.readBytes(fileName);
                byte type = bytes[bytes.length - 1];
                byte[] bytes1 = ArrayUtil.copy(bytes, bytes.length - 1);
                if (type == 1) {
                    return new String(bytes1);
                }
                if (type == 2) {
                    return bytes1;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 加载未保存数据
     *
     * @param hashCode hash码
     * @return 未保存数据
     */
    public static Object loadValue(int hashCode, Class<?> clazz) {
        try {
            String baseDir = RedisConst.KEY_CACHE_PATH + hashCode;
            String fileName = baseDir + ".value";
            if (FileUtil.exist(fileName)) {
                byte[] bytes = FileUtil.readBytes(fileName);
                byte type = bytes[bytes.length - 1];
                byte[] bytes1 = ArrayUtil.copy(bytes, bytes.length - 1);
                if (type == 3) {
                    String json = new String(bytes1, StandardCharsets.UTF_8);
                    return JSONUtil.parseArray(json).toBeanList(clazz);
                }
                if (type == 4) {
                    String json = new String(bytes1, StandardCharsets.UTF_8);
                    return JSONUtil.parseObject(json).toBean(clazz);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 删除未保存数据
     *
     * @param hashCode hash码
     */
    public static boolean deleteValue(int hashCode) {
        try {
            String baseDir = baseDir(hashCode);
            String fileName = baseDir + ".value";
            FileUtil.del(fileName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * 删除未保存数据
     *
     * @param hashCode hash码
     */
    public static boolean hasValue(int hashCode) {
        try {
            String baseDir = baseDir(hashCode);
            String fileName = baseDir + ".value";
            return  FileUtil.exist(fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
