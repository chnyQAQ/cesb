package com.dah.desb.infrastructure.camel.component.general.process.impl;

import com.dah.desb.infrastructure.camel.component.general.GeneralEndpoint;
import com.dah.desb.infrastructure.camel.component.general.process.GeneralProcessor;
import org.apache.camel.Exchange;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class HttpParamsToMapProcessor implements GeneralProcessor {

    @Override
    public void process(GeneralEndpoint endpoint, Exchange exchange) {
        HttpServletRequest request = (HttpServletRequest) exchange.getIn().getHeader(Exchange.HTTP_SERVLET_REQUEST);
        Map<String, String[]> params = request.getParameterMap();
        exchange.getOut().setHeader(endpoint.getKey(), params);
    }
}
