package com.dah.desb.domain.endpoint.file;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.dah.desb.domain.endpoint.Endpoint;
import com.dah.desb.domain.endpoint.EndpointType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.util.StringUtils;

public class EndpointFile implements Endpoint {

	@Length(max = 50)
	private String id;

	/**
	 * 代码（英文名，版本号）
	 */
	@NotNull
	@NotBlank
	@Length(max = 50)
	private String code;

	/**
	 * 中文名
	 */
	@NotNull
	@NotBlank
	@Length(max = 50)
	private String name;

	/**
	 * camel组件
	 */
	@NotNull
	@NotBlank
	@Length(max = 50)
	private String component;

	/**
	 * 路径
	 */
	@NotNull
	@NotBlank
	@Length(max = 500)
	private String path;

	/**
	 * 其他选项
	 */
	@Length(max = 200)
	private String options = "";

	//translate
	private String url;

	@Override
	public String buildUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(component).append(":");
		sb.append(path);
		if (!StringUtils.isEmpty(options)) {
			sb.append("?");
			sb.append(options);
		}
		return sb.toString();
	}

	@Override
	public String getType() {
		return EndpointType.file.toString();
	}

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getUrl() {
		return buildUrl();
	}
}
