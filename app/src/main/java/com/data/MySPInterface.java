package com.data;

/**
 * 用于简单化保存数据操作
 * Created by 李建华 on 2015/9/14.
 */
public interface MySPInterface {
    String getValue(String key);
    void setValue(String key, String object);
}
