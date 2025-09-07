package com.xiahou.yu.paasdomincore.design.metamodel;

import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegister;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 数据模型
 * 用于定义数据的基础，按照元模型配置生成
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("t_data_model")
public class DataModel extends AbstractModel implements EntityRegister {

}
