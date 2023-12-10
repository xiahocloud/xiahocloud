package com.xiahou.yu.paasmetacore.models.reader;

import com.xiahou.yu.paasmetacore.MetaModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetamodelReaderTest {


    @Test
    public void testListMetaModel() {
        MetaModel result = MetamodelReader.listMetaModel();
        Assertions.assertNotNull(result);
    }

}