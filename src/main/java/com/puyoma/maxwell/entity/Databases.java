package com.puyoma.maxwell.entity;

import java.io.Serializable;

/**
 * (Databases)实体类
 *
 * @author makejava
 * @since 2020-03-05 09:38:49
 */
public class Databases implements Serializable {
    private static final long serialVersionUID = -49315223319679068L;
    
    private Integer id;
    
    private Integer schemaId;
    
    private String name;
    
    private String charset;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}