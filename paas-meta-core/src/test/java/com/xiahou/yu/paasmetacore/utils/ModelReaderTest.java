package com.xiahou.yu.paasmetacore.utils;

import com.xiahou.yu.paasmetacore.MetaModel;
import com.xiahou.yu.paasmetacore.models.reader.MetamodelReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelReaderTest {
    MetamodelReader modelReader = new MetamodelReader();

    @Test
    public void testListMetaModel() {
        MetaModel result = modelReader.listMetaModel();
        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetModelByDomainModel() {
        MetaModel.Model result = modelReader.getModelByDomainModel(new MetaModel.Model());
        Assertions.assertEquals(new MetaModel.Model(), result);
    }

    @Test
    public void testGetModelById() {
        MetaModel.Model result = modelReader.getModelById("id");
        Assertions.assertEquals(new MetaModel.Model(), result);
    }
}