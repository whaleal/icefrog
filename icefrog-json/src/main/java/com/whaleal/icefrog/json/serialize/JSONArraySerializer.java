package com.whaleal.icefrog.json.serialize;

import com.whaleal.icefrog.json.JSONArray;

/**
 * JSON列表的序列化接口，用于将特定对象序列化为{@link JSONArray}
 * 
 * @param <V> 对象类型
 * 
 * @author looly   wh
 */
@FunctionalInterface
public interface JSONArraySerializer<V> extends JSONSerializer<JSONArray, V>{}
