package cn.oyzh.easyredis.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.oyzh.easyredis.command.RedisCommand;
import cn.oyzh.easyredis.command.RedisCommandUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author oyzh
 * @since 2024/5/29
 */
public class RedisCmdSpider {

    private String descUrl = "https://redis.io/docs/latest/commands/";

    private String detailUrl = "https://redis.io/docs/latest/commands/";

    private String filePath = "D:\\Workspaces\\OYZH\\easyredis\\src\\main\\resources\\redis_commands.json";

    @Test
    public void test1() throws IOException {
        List<RedisCommand> list = RedisCommandUtil.getCommands();
        List<RedisCommand> list1 = new ArrayList<>();
        Document document = Jsoup.connect(descUrl).get();
        Elements articles = document.getElementsByTag("article");
        int count = Protocol.Command.values().length;
        int sum = 0;
        for (Protocol.Command value : Protocol.Command.values()) {
            try {
                if (!this.isNeedFetch(value)) {
                    sum++;
                    System.out.println("command:" + value.name() + " skip.");
                    continue;
                }
                RedisCommand command = new RedisCommand();
                command.setCommand(value.name());
                for (Element article : articles) {
                    String attr = article.attr("data-name");
                    if (value.name().toUpperCase().equalsIgnoreCase(attr)) {
                        Elements p = article.getElementsByTag("p");
                        String text = p.text();
                        command.setDesc(text);
                        System.out.println(text);
                        System.out.println("-------------------------------------->");
                        break;
                    }
                }

                getDetail(value, command);
                list1.add(command);
                sum++;
                System.out.println("count:" + count + " ,sum:" + sum);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        List<RedisCommand> list2 = new ArrayList<>(list1);
        for (RedisCommand command : list) {
            Optional<RedisCommand> optional = list1.parallelStream().filter(c -> c.getCommand().equalsIgnoreCase(command.getCommand())).findAny();
            if (optional.isEmpty()) {
                list2.add(command);
            }
        }
        String json = JSONUtil.toJsonStr(list2);
        FileUtil.writeString(json, filePath, CharsetUtil.UTF_8);
    }

    private boolean isNeedFetch(Protocol.Command cmd) {
        RedisCommand command = RedisCommandUtil.getCommand(cmd.name());
        if (command == null || StrUtil.isBlank(command.getDesc()) || StrUtil.isBlank(command.getAvailable())
                || StrUtil.isBlank(command.getArgs())) {
            return true;
        }
        return false;
    }

    public void getDetail(Protocol.Command command, RedisCommand redisCommand) throws Exception {
        Document document = Jsoup.connect(detailUrl + command.name() + "/").get();
        Elements args = document.getElementsByClass("command-syntax");
        String argsText = args.text();
        redisCommand.setArgs( argsText);
        Elements available = document.getElementsByClass("prose").getFirst().getElementsByTag("dd");
        String availableText = available.getFirst().text();
        redisCommand.setAvailable( availableText);

        System.out.println(argsText);
        System.out.println(availableText);
        System.out.println("-------------------------------------->");
    }
}
