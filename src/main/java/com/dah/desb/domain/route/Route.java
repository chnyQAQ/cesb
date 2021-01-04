package com.dah.desb.domain.route;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.dah.desb.domain.route.endpoint.RouteEndpoint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Route {

	@Length(max = 50)
	private String id;

	@NotNull
	@NotBlank
	@Length(max = 50)
	private String code;

	@NotNull
	@NotBlank
	@Length(max = 50)
	private String name;

	private boolean enabled = true;

	private String routeXML;

	/**
	 * 累计执行次数
	 */
	private Integer executeCount;

	/**
	 * 执行平均耗时（ms）
	 */
	private Integer executeAvgDuration;
	
	//------------临时字段----------------
	/**
	 * 运行/停止
	 */
	private boolean loaded;
	private List<RouteEndpoint> routeEndpoints;

}
