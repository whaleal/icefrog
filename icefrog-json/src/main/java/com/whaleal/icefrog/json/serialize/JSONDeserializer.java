package com.whaleal.icefrog.json.serialize;

import com.whaleal.icefrog.json.JSON;

/**
 * JSON反序列话自定义实现类
 * 
 * @author looly   wh
 *
 * @param <T> 反序列化后的类型
 */
@FunctionalInterface
public interface JSONDeserializer<T> {
	
	/**
	 * 反序列化，通过实现此方法，自定义实现JSON转换为指定类型的逻辑
	 * 
	 * @param json {@link JSON}
	 * @return 目标对象
	 */
	T deserialize(JSON json);
}
