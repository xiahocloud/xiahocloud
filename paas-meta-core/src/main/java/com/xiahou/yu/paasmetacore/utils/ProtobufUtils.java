package com.xiahou.yu.paasmetacore.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.StringValue;
import com.google.protobuf.util.JsonFormat;
import com.xiahou.yu.paasmetacore.protobuf.EnumMessageProto;
import com.xiahou.yu.paasmetacore.protobuf.MessageProto;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2023/2/8 10:36
 * @version 1.0
 */
public class ProtobufUtils {
    private static final JsonFormat.Printer printer;
    private static final JsonFormat.Parser parser;

    static {
        JsonFormat.TypeRegistry registry = JsonFormat.TypeRegistry.newBuilder()
                .add(StringValue.getDescriptor())
                .build();

        printer = JsonFormat
                .printer()
                .usingTypeRegistry(registry)
                .includingDefaultValueFields()
                .omittingInsignificantWhitespace();

        parser = JsonFormat
                .parser()
                .usingTypeRegistry(registry);
    }

    public static MessageProto.User randomUser() {
        final Map<Integer, MessageProto.GradeInfo> gradeInfoMap = new HashMap<>();

        for (EnumMessageProto.SubjectEnum subjectEnum : EnumMessageProto.SubjectEnum.values()) {
            if (subjectEnum == EnumMessageProto.SubjectEnum.DEFAULT_SUBJECT || subjectEnum == EnumMessageProto.SubjectEnum.UNRECOGNIZED) {
                continue;
            }

            gradeInfoMap.put(subjectEnum.getNumber(), MessageProto.GradeInfo.newBuilder()
                    .setScore(RandomUtils.nextDouble(0, 100))
                    .setRank(RandomUtils.nextInt(1, 50))
                    .build());
        }

        final List<MessageProto.ParentUser> parentUserList = Arrays.asList(
                MessageProto.ParentUser.newBuilder().setRelation("father").setTel(RandomStringUtils.randomNumeric(13)).build(),
                MessageProto.ParentUser.newBuilder().setRelation("mother").setTel(RandomStringUtils.randomNumeric(13)).build()
        );


        return MessageProto.User.newBuilder()
                .setName(RandomStringUtils.randomAlphabetic(5))
                .setAge(RandomUtils.nextInt(1, 80))
                .setSex(EnumMessageProto.SexEnum.forNumber(RandomUtils.nextInt(1, 2)))
                .putAllGrade(gradeInfoMap)
                .addAllParent(parentUserList)
                .build();
    }

/*    public static <T> String toJson (T object) {
        final String s = new Gson().toJson(object);
        return s;
    }*/

    public static String toJson(Message message) {
        if (message == null) {
            return "";
        }
        try {
            return printer.print(message);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T extends Message> T toBean(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        try {
            final Method method = clazz.getMethod("newBuilder");
            final Message.Builder builder = (Message.Builder) method.invoke(null);

            parser.merge(json, builder);

            return (T) builder.build();
        } catch (Exception e) {
            throw new RuntimeException("ProtobufUtils toMessage happen error, class: " + clazz + ", json: " + json, e);
        }
    }

    public static String toJson(List<? extends MessageOrBuilder> messageList) {
        if (messageList == null) {
            return "";
        }
        if (messageList.isEmpty()) {
            return "[]";
        }

        try {
            StringBuilder builder = new StringBuilder(1024);
            builder.append("[");
            for (MessageOrBuilder message : messageList) {
                printer.appendTo(message, builder);
                builder.append(",");
            }
            return builder.deleteCharAt(builder.length() - 1).append("]").toString();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static <T extends Message> List<T> toBeanList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }

        final JSONArray jsonArray = JSON.parseArray(json);

        final List<T> resultList = new ArrayList<>(jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            resultList.add(toBean(jsonArray.getString(i), clazz));
        }

        return resultList;
    }

}
