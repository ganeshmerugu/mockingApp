package com.mock.application.rest.Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "application", namespace = "http://wadl.dev.java.net/2009/02")
public class WADLApplication {

    private WADLResources resources;

    @XmlElement(name = "resources")
    public WADLResources getResources() {
        return resources;
    }

    public void setResources(WADLResources resources) {
        this.resources = resources;
    }
}
