package com.puyoma.maxwell.entity;

import java.io.Serializable;

/**
 * (Tables)实体类
 *
 * @author makejava
 * @since 2020-03-05 09:41:12
 */
public class Tables implements Serializable {
    private static final long serialVersionUID = -70084426881538495L;
    
    private Integer id;
    
    private Integer schemaId;
    
    private Integer databaseId;
    
    private String name;
    
    private String charset;
    
    private String pk;


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

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
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

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

}