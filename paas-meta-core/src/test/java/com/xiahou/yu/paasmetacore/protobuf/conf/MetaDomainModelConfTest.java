package com.xiahou.yu.paasmetacore.protobuf.conf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaDomainModelConfTest {

    @Test
    void testFetchContextModel() {
        MetaDomainModelConf metaDomainModelConf = new MetaDomainModelConf();
        System.out.println(MetaDomainModelConf.fetchContextModel());
    }
}