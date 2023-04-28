package com.xiahou.yu.paasmetacore;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xiahou.yu.paasmetacore.protobuf.FooDemo;
import com.xiahou.yu.paasmetacore.protobuf.MessageProto;
import com.xiahou.yu.paasmetacore.utils.ProtobufUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    public void testBean2Json() {

        // 序列化null
        FooDemo.Foo foo = FooDemo.Foo.newBuilder().setName("whx").build();
        final String fooJson = ProtobufUtils.toJson(foo);
        System.out.println(fooJson);


        // 序列化
        final MessageProto.User common = ProtobufUtils.randomUser();
        final String commonJson = ProtobufUtils.toJson(common);
        System.out.println(commonJson);

        final byte[] bytes = common.toByteArray();
        System.out.println(bytes);

        try {
            final MessageProto.User user = MessageProto.User.parseFrom(bytes);
            System.out.println(ProtobufUtils.toJson(user));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }


        // 反序列化
        final MessageProto.User deserializeCommon = ProtobufUtils.toBean(commonJson, MessageProto.User.class);
        Assertions.assertNotNull(deserializeCommon);
        Assertions.assertEquals(common, deserializeCommon);
    }
}