package com.mock.application.Model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class WADLResources {

    private List<WADLResource> resource;

    @XmlElement(name = "resource")
    public List<WADLResource> getResource() {
        return resource;
    }

    public void setResource(List<WADLResource> resource) {
        this.resource = resource;
    }
}
