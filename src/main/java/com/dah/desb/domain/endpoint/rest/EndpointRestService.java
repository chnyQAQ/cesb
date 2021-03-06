package com.dah.desb.domain.endpoint.rest;

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
public class EndpointRestService {

    @Autowired
    private EndpointRestMapper endpointRestMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    public EndpointRest save(@Valid EndpointRest endpointRest) {
        EndpointRest saved = endpointRestMapper.getByCode(endpointRest.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = endpointRestMapper.getByName(endpointRest.getName());
        if (saved != null) {
            throw new AppException("保存失败，名称重复！");
        }

        endpointRest.setId(UUID.randomUUID().toString());

        endpointRestMapper.save(endpointRest);
        return endpointRest;
    }

    public EndpointRest update(@Valid EndpointRest endpointRest) {
        EndpointRest saved = endpointRestMapper.getById(endpointRest.getId());
        if (saved == null) {
            throw new AppException("更新失败，端点不存在！");
        }
        saved = endpointRestMapper.getByCode(endpointRest.getCode());
        if (saved != null && !saved.getId().equals(endpointRest.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = endpointRestMapper.getByName(endpointRest.getName());
        if (saved != null && !saved.getId().equals(endpointRest.getId())) {
            throw new AppException("更新失败，名称重复！");
        }

        endpointRestMapper.update(endpointRest);
        return endpointRest;
    }

    public void remove(String id) {
        EndpointRest saved = endpointRestMapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，端点不存在！");
        }
        if (routeEndpointMapper.getListByEndpoint(EndpointType.rest.toString(), id).size() > 0) {
            throw new AppException("删除失败，端点正在被使用！");
        }
        endpointRestMapper.remove(id);
    }

}
