package com.dah.desb.domain.endpoint.general;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointType;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EndpointGeneral implements Endpoint {

    private String id;

    @NotNull
    @NotBlank
    @Length(max = 50)
    private String code;

    @NotNull
    @NotBlank
    @Length(max = 50)
    private String name;

    private String component;

    private String process;

    private String options;

    //translate
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return EndpointType.general.toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String buildUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(component);
        sb.append("://");
        sb.append(component);
        sb.append("?");
        sb.append("processType=").append(process);
        if (!StringUtils.isEmpty(options)) {
            sb.append("&");
            sb.append(options);
        }
        return sb.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getUrl() {
        return buildUrl();
    }
}
