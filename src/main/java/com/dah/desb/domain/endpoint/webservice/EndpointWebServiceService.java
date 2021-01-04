package com.dah.desb.domain.endpoint.webservice;

import com.dah.desb.domain.endpoint.EndpointType;
import com.dah.desb.domain.route.endpoint.RouteEndpointMapper;
import com.dah.desb.infrastructure.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Validated
@Service
public class EndpointWebServiceService {

    @Autowired
    private EndpointWebServiceMapper endpointWebServiceMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    public EndpointWebService save(@Valid EndpointWebService endpointWebService) {
        EndpointWebService saved = endpointWebServiceMapper.getByCode(endpointWebService.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = endpointWebServiceMapper.getByName(endpointWebService.getName());
        if (saved != null) {
            throw new AppException("保存失败，名称重复！");
        }
        endpointWebService.setId(UUID.randomUUID().toString());

        endpointWebServiceMapper.save(endpointWebService);
        return endpointWebService;
    }

    public EndpointWebService update(@Valid EndpointWebService endpointWebService) {
        EndpointWebService saved = endpointWebServiceMapper.getById(endpointWebService.getId());
        if (saved == null) {
            throw new AppException("更新失败，端点不存在！");
        }
        saved = endpointWebServiceMapper.getByCode(endpointWebService.getCode());
        if (saved != null && !saved.getId().equals(endpointWebService.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = endpointWebServiceMapper.getByName(endpointWebService.getName());
        if (saved != null && !saved.getId().equals(endpointWebService.getId())) {
            throw new AppException("更新失败，名称重复！");
        }

        endpointWebServiceMapper.update(endpointWebService);
        return endpointWebService;
    }

    public void remove(String id) {
        EndpointWebService saved = endpointWebServiceMapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，端点不存在！");
        }

        if (routeEndpointMapper.getListByEndpoint(EndpointType.webservice.toString(), id).size() > 0) {
            throw new AppException("删除失败，端点正在被使用！");
        }
        endpointWebServiceMapper.remove(id);
    }

}
