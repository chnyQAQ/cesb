package com.dah.desb.domain.endpoint.ftp;

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
public class EndpointFtpService {

    @Autowired
    private EndpointFtpMapper endpointHttpMapper;

    @Autowired
    private RouteEndpointMapper routeEndpointMapper;

    public EndpointFtp save(@Valid EndpointFtp endpointFtp) {
        EndpointFtp saved = endpointHttpMapper.getByCode(endpointFtp.getCode());
        if (saved != null) {
            throw new AppException("保存失败，代码重复！");
        }
        saved = endpointHttpMapper.getByName(endpointFtp.getName());
        if (saved != null) {
            throw new AppException("保存失败，名称重复！");
        }
        endpointFtp.setId(UUID.randomUUID().toString());

        endpointHttpMapper.save(endpointFtp);
        return endpointFtp;
    }

    public EndpointFtp update(@Valid EndpointFtp endpointFtp) {
        EndpointFtp saved = endpointHttpMapper.getById(endpointFtp.getId());
        if (saved == null) {
            throw new AppException("更新失败，端点不存在！");
        }
        saved = endpointHttpMapper.getByCode(endpointFtp.getCode());
        if (saved != null && !saved.getId().equals(endpointFtp.getId())) {
            throw new AppException("更新失败，代码重复！");
        }
        saved = endpointHttpMapper.getByName(endpointFtp.getName());
        if (saved != null && !saved.getId().equals(endpointFtp.getId())) {
            throw new AppException("更新失败，名称重复！");
        }
        endpointHttpMapper.update(endpointFtp);
        return endpointFtp;
    }

    public void remove(String id) {
        EndpointFtp saved = endpointHttpMapper.getById(id);
        if (saved == null) {
            throw new AppException("删除失败，端点不存在！");
        }

        if (routeEndpointMapper.getListByEndpoint(EndpointType.ftp.toString(), id).size() > 0) {
            throw new AppException("删除失败，端点正在被使用！");
        }
        endpointHttpMapper.remove(id);
    }

}
