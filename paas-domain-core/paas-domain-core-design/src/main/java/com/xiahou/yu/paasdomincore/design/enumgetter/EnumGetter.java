package com.xiahou.yu.paasdomincore.design.enumgetter;

import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;

public interface EnumGetter<E> {
    String getKey();

    static <E extends Enum<E> & EnumGetter<E>> E getByKey(Class<E> enumClass, String key) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        throw new PaaSException(ResultStatusEnum.ENUM_NOT_MATCHES, "No enum constant found with key: " + key);
    }
}
