package com.castlemock.application.Model;

import javax.xml.bind.annotation.XmlAttribute;

public class WADLMethod {

    private String name;

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
