package com.dah.desb.domain.endpoint.general;

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
public class EndpointGeneralService {

	@Autowired
	private EndpointGeneralMapper endpointGeneralMapper;

	@Autowired
	private RouteEndpointMapper routeEndpointMapper;
	
	public EndpointGeneral save(@Valid EndpointGeneral endpointGeneral) {
		EndpointGeneral saved = endpointGeneralMapper.getByCode(endpointGeneral.getCode());
		if (saved != null) {
			throw new AppException("保存失败，代码重复！");
		}
		saved = endpointGeneralMapper.getByName(endpointGeneral.getName());
		if (saved != null) {
			throw new AppException("保存失败，名称重复！");
		}
		endpointGeneral.setId(UUID.randomUUID().toString());
		endpointGeneralMapper.save(endpointGeneral);
		return endpointGeneral;
	}
	
	public EndpointGeneral update(@Valid EndpointGeneral endpointGeneral) {
		EndpointGeneral saved = endpointGeneralMapper.getById(endpointGeneral.getId());
		if (saved == null) {
			throw new AppException("更新失败，端点不存在！");
		}
		saved = endpointGeneralMapper.getByCode(endpointGeneral.getCode());
		if (saved != null && !saved.getId().equals(endpointGeneral.getId())) {
			throw new AppException("更新失败，代码重复！");
		}
		saved = endpointGeneralMapper.getByName(endpointGeneral.getName());
		if (saved != null && !saved.getId().equals(endpointGeneral.getId())) {
			throw new AppException("更新失败，名称重复！");
		}

		endpointGeneralMapper.update(endpointGeneral);
		return endpointGeneral;
	}
	
	public void remove(String id) {
		EndpointGeneral saved = endpointGeneralMapper.getById(id);
		if (saved == null) {
			throw new AppException("删除失败，端点不存在！");
		}
		if (routeEndpointMapper.getListByEndpoint(EndpointType.general.toString(), id).size() > 0) {
			throw new AppException("删除失败，端点正在被使用！");
		}
		endpointGeneralMapper.remove(id);
	}
	
}
