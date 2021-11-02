package com.sabi.supplier.service.helper;

public class SearchCriteria {

    private String key;
    private Object value;
    private com.sabi.supplier.service.helper.SearchOperation operation;


    public SearchCriteria(String key, Object value, com.sabi.supplier.service.helper.SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public com.sabi.supplier.service.helper.SearchOperation getOperation() {
        return operation;
    }

    public void setOperation(com.sabi.supplier.service.helper.SearchOperation operation) {
        this.operation = operation;
    }
}