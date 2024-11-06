package com.mock.application.Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
