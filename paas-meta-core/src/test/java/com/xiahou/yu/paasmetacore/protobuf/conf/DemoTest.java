package com.xiahou.yu.paasmetacore.protobuf.conf;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/3/20 14:05
 * @version 1.0
 */
public class DemoTest {
    @Test
    public void test01() {
        String content = null;
        try {
            URL url = getClass().getResource("/data/test.json");
            System.out.println(url.getPath());
            File currentFile = new File(".");
            String currentPath = currentFile.getAbsolutePath();
            System.out.println("Current Path: " + currentPath);

            Path currentPaths = Paths.get("");
            System.out.println("Current Path: " + currentPaths.toAbsolutePath());

            File file = new File(url.toURI());
            byte[] bytes = Files.readAllBytes(file.toPath());
            content = new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        final Gson gson = new Gson();
        List<Map<String, Object>> list = gson.fromJson(content, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        list.removeIf(Objects::isNull);
        final Map<Object, Object> collect = list.stream().collect(Collectors.toMap(
                item -> ((LinkedTreeMap<String, Object>) item.get("_source")).get("code"),
                item -> ((LinkedTreeMap<String, Object>) item.get("_source")).get("bizType")

        ));
        System.out.println(list);
    }
}
