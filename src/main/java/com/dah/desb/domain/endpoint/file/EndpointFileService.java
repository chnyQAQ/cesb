package com.dah.desb.domain.endpoint.file;

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
public class EndpointFileService {

    @Autowired
    private EndpointFileMapper endpointFileMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    public EndpointFile save(@Valid EndpointFile endpointFile) {
        EndpointFile saved = endpointFileMapper.getByCode(endpointFile.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = endpointFileMapper.getByName(endpointFile.getName());
        if (saved != null) {
            throw new AppException("保存失败，名称重复！");
        }
        endpointFile.setId(UUID.randomUUID().toString());
        endpointFileMapper.save(endpointFile);
        return endpointFile;
    }

    public EndpointFile update(@Valid EndpointFile endpointFile) {
        EndpointFile saved = endpointFileMapper.getById(endpointFile.getId());
        if (saved == null) {
            throw new AppException("更新失败，端点不存在！");
        }
        saved = endpointFileMapper.getByCode(endpointFile.getCode());
        if (saved != null && !saved.getId().equals(endpointFile.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = endpointFileMapper.getByName(endpointFile.getName());
        if (saved != null && !saved.getId().equals(endpointFile.getId())) {
            throw new AppException("更新失败，名称重复！");
        }
        endpointFileMapper.update(endpointFile);
        return endpointFile;
    }

    public void remove(String id) {
        EndpointFile saved = endpointFileMapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，端点不存在！");
        }

        if (routeEndpointMapper.getListByEndpoint(EndpointType.file.toString(), id).size() > 0) {
            throw new AppException("删除失败，端点正在被使用！");
        }
        endpointFileMapper.remove(id);
    }

}
