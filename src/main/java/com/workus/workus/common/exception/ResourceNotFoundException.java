package com.workus.workus.common.exception;

import com.workus.workus.common.constant.ResourceType;

public class ResourceNotFoundException extends RuntimeException {
    private final ResourceType resourceType;
    private final Object id;

    public ResourceNotFoundException(ResourceType resourceType, Object id) {
        super(resourceType + " not found. id=" + id);
        this.resourceType = resourceType;
        this.id = id;
    }

    public ResourceType resourceType() { return resourceType; }
    public Object id() { return id; }
}
