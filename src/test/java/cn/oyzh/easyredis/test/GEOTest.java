package cn.oyzh.easyredis.test;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/11/22
 */
public class GEOTest {

    // 定义一个32进制字符对应的二进制字符串的映射表
    private static final Map<Character, String> BASE32_TO_BINARY = new HashMap<>();

    static {
        BASE32_TO_BINARY.put('0', "00000");
        BASE32_TO_BINARY.put('1', "00001");
        BASE32_TO_BINARY.put('2', "00010");
        BASE32_TO_BINARY.put('3', "00011");
        BASE32_TO_BINARY.put('4', "00100");
        BASE32_TO_BINARY.put('5', "00101");
        BASE32_TO_BINARY.put('6', "00110");
        BASE32_TO_BINARY.put('7', "00111");
        BASE32_TO_BINARY.put('8', "01000");
        BASE32_TO_BINARY.put('9', "01001");
        BASE32_TO_BINARY.put('b', "01010");
        BASE32_TO_BINARY.put('c', "01011");
        BASE32_TO_BINARY.put('d', "01100");
        BASE32_TO_BINARY.put('e', "01101");
        BASE32_TO_BINARY.put('f', "01110");
        BASE32_TO_BINARY.put('g', "01111");
        BASE32_TO_BINARY.put('h', "10000");
        BASE32_TO_BINARY.put('j', "10001");
        BASE32_TO_BINARY.put('k', "10010");
        BASE32_TO_BINARY.put('m', "10011");
        BASE32_TO_BINARY.put('n', "10100");
        BASE32_TO_BINARY.put('p', "10101");
        BASE32_TO_BINARY.put('q', "10110");
        BASE32_TO_BINARY.put('r', "10111");
        BASE32_TO_BINARY.put('s', "11000");
        BASE32_TO_BINARY.put('t', "11001");
        BASE32_TO_BINARY.put('u', "11010");
        BASE32_TO_BINARY.put('v', "11011");
        BASE32_TO_BINARY.put('w', "11100");
        BASE32_TO_BINARY.put('x', "11101");
        BASE32_TO_BINARY.put('y', "11110");
        BASE32_TO_BINARY.put('z', "11111");
    }

    // 定义一个方法，将52位的整数转换成经纬度
    public static double[] scoreToLatLng(double score) {
        // 将整数转换成二进制字符串
        String binary = Long.toBinaryString((long) score);
        // 补齐52位
        while (binary.length() < 52) {
            binary = "0" + binary;
        }
        // 将二进制字符串转换成32进制字符串
        StringBuilder base32 = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 5) {
            String sub = binary.substring(i, i + 5);
            for (Map.Entry<Character, String> entry : BASE32_TO_BINARY.entrySet()) {
                if (entry.getValue().equals(sub)) {
                    base32.append(entry.getKey());
                    break;
                }
            }
        }
        // 将32进制字符串转换成GeoHash编码
        String geoHash = base32.toString();
        // 将GeoHash编码转换成经纬度
        double[] latLng = new double[2];
        // 初始化经度范围和纬度范围
        double minLng = -180;
        double maxLng = 180;
        double minLat = -90;
        double maxLat = 90;
        // 标记当前位是经度还是纬度
        boolean isEven = true;
        // 遍历GeoHash编码的每一位
        for (int i = 0; i < geoHash.length(); i++) {
            // 获取当前位对应的二进制字符串
            String binarySub = BASE32_TO_BINARY.get(geoHash.charAt(i));
            // 遍历二进制字符串的每一位
            for (int j = 0; j < binarySub.length(); j++) {
                // 获取当前位的值
                char bit = binarySub.charAt(j);
                // 如果是经度
                if (isEven) {
                    // 根据值划分区域
                    double mid = (minLng + maxLng) / 2;
                    if (bit == '0') {
                        maxLng = mid;
                    } else {
                        minLng = mid;
                    }
                }
                // 如果是纬度
                else {
                    // 根据值划分区域
                    double mid = (minLat + maxLat) / 2;
                    if (bit == '0') {
                        maxLat = mid;
                    } else {
                        minLat = mid;
                    }
                }
                // 切换经度和纬度的标记
                isEven = !isEven;
            }
        }
        // 计算经纬度的值
        latLng[0] = (minLng + maxLng) / 2;
        latLng[1] = (minLat + maxLat) / 2;
        // 返回结果
        return latLng;
    }

    @Test
    public void test1() {

        System.out.println(Arrays.toString(scoreToLatLng(10)));
    }
}
