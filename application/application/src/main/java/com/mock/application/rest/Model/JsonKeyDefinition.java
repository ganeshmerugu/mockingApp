package com.mock.application.rest.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class JsonKeyDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_name", nullable = false)
    private String keyName;

    @Column(name = "data_type", nullable = false)
    private String dataType;

    private String format;

    @Column(name = "required", nullable = false)
    private boolean required;

    @ManyToOne
    @JoinColumn(name = "response_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private RestMockResponse restMockResponse;

    private String parentId;

    // Constructor for Builder
    private JsonKeyDefinition(String keyName, String dataType, String format, boolean required, RestMockResponse restMockResponse, String parentId) {
        this.keyName = keyName;
        this.dataType = dataType;
        this.format = format;
        this.required = required;
        this.restMockResponse = restMockResponse;
        this.parentId = parentId;
    }

    public RestMockResponse getRestMockResponse() {
        return restMockResponse;
    }

    public void setRestMockResponse(RestMockResponse restMockResponse) {
        this.restMockResponse = restMockResponse;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    // Builder
    public static JsonKeyDefinitionBuilder builder() {
        return new JsonKeyDefinitionBuilder();
    }

    public static class JsonKeyDefinitionBuilder {
        private String keyName;
        private String dataType;
        private String format;
        private boolean required;
        private RestMockResponse restMockResponse;
        private String parentId;

        public JsonKeyDefinitionBuilder keyName(String keyName) {
            this.keyName = keyName;
            return this;
        }

        public JsonKeyDefinitionBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public JsonKeyDefinitionBuilder format(String format) {
            this.format = format;
            return this;
        }

        public JsonKeyDefinitionBuilder required(boolean required) {
            this.required = required;
            return this;
        }

        public JsonKeyDefinitionBuilder restMockResponse(RestMockResponse restMockResponse) {
            this.restMockResponse = restMockResponse;
            return this;
        }

        public JsonKeyDefinitionBuilder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public JsonKeyDefinition build() {
            return new JsonKeyDefinition(keyName, dataType, format, required, restMockResponse, parentId);
        }
    }
}
