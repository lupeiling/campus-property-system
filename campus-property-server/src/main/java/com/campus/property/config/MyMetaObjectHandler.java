package com.campus.property.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", java.time.LocalDateTime.class, java.time.LocalDateTime.now());
    }
}
