package com.puyoma.maxwell.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (Bootstrap)实体类
 *
 * @author makejava
 * @since 2020-03-04 18:03:51
 */
public class Bootstrap implements Serializable {
    private static final long serialVersionUID = -51084588244571724L;
    
    private Integer id;
    
    private String databaseName;
    
    private String tableName;
    
    private String whereClause;
    
    private Integer isComplete;
    
    private Long insertedRows;
    
    private Long totalRows;
    
    private Date createdAt;
    
    private Date startedAt;
    
    private Date completedAt;
    
    private String binlogFile;
    
    private Integer binlogPosition;
    
    private String clientId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public Integer getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Integer isComplete) {
        this.isComplete = isComplete;
    }

    public Long getInsertedRows() {
        return insertedRows;
    }

    public void setInsertedRows(Long insertedRows) {
        this.insertedRows = insertedRows;
    }

    public Long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getBinlogFile() {
        return binlogFile;
    }

    public void setBinlogFile(String binlogFile) {
        this.binlogFile = binlogFile;
    }

    public Integer getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(Integer binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}