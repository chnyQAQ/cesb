package com.dah.desb.controller.endpoint;

import com.dah.desb.domain.endpoint.EndpointMapperRegistry;
import com.dah.desb.domain.endpoint.EndpointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EndpointController {

    @Autowired
    private EndpointMapperRegistry endpointMapperRegistry;

    @GetMapping(path = "/endpoint/list-search", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getSearch(@RequestParam(value = "search", defaultValue = "") String search) {
        Map<String, Object> result = new HashMap<>();
        for (Enum endndpointType : EndpointType.values()) {
            result.put(endndpointType.toString(), endpointMapperRegistry.determineEndpoints(endndpointType.toString(), search));
        }
        return result;
    }
}
