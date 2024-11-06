package com.mock.application.Model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class WADLResource {

    private String path;
    private List<WADLMethod> method;

    @XmlAttribute(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement(name = "method")
    public List<WADLMethod> getMethod() {
        return method;
    }

    public void setMethod(List<WADLMethod> method) {
        this.method = method;
    }
}
