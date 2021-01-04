package com.dah.desb.domain.endpoint.http;

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
public class EndpointHttpService {

    @Autowired
    private EndpointHttpMapper endpointHttpMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    public EndpointHttp save(@Valid EndpointHttp endpointHttp) {
        EndpointHttp saved = endpointHttpMapper.getByCode(endpointHttp.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = endpointHttpMapper.getByName(endpointHttp.getName());
        if (saved != null) {
            throw new AppException("保存失败，名称重复！");
        }
        endpointHttp.setId(UUID.randomUUID().toString());

        endpointHttpMapper.save(endpointHttp);
        return endpointHttp;
    }

    public EndpointHttp update(@Valid EndpointHttp endpointHttp) {
        EndpointHttp saved = endpointHttpMapper.getById(endpointHttp.getId());
        if (saved == null) {
            throw new AppException("更新失败，端点不存在！");
        }
        saved = endpointHttpMapper.getByCode(endpointHttp.getCode());
        if (saved != null && !saved.getId().equals(endpointHttp.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = endpointHttpMapper.getByName(endpointHttp.getName());
        if (saved != null && !saved.getId().equals(endpointHttp.getId())) {
            throw new AppException("更新失败，名称重复！");
        }
        endpointHttpMapper.update(endpointHttp);
        return endpointHttp;
    }

    public void remove(String id) {
        EndpointHttp saved = endpointHttpMapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，端点不存在！");
        }

        if (routeEndpointMapper.getListByEndpoint(EndpointType.http.toString(), id).size() > 0) {
            throw new AppException("删除失败，端点正在被使用！");
        }
        endpointHttpMapper.remove(id);
    }

}
