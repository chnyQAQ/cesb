package com.dah.desb.infrastructure.camel.component.general.process.impl;

import com.alibaba.fastjson.JSONObject;
import com.dah.desb.infrastructure.camel.component.general.GeneralEndpoint;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessor;
import org.apache.camel.Exchange;

import java.util.Map;

public class MapToJsonProcessor implements GeneralProcessor {

    @Override
    public void process(GeneralEndpoint endpoint, Exchange exchange) {
        Map<String, Object> map = (Map<String, Object>) exchange.getIn().getHeader(endpoint.getKey());
        JSONObject object = new JSONObject(map);
        exchange.getOut().setHeader(endpoint.getKey(), object);
    }
}
