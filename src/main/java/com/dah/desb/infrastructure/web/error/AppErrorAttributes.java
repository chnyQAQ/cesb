package com.dah.desb.infrastructure.web.error;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;

import com.dah.desb.infrastructure.exception.AppException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class AppErrorAttributes extends org.springframework.boot.web.servlet.error.DefaultErrorAttributes {
	
	private static final String ERROR_VALIDATION = "ValidationException";

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		Throwable error = super.getError(webRequest);
		Map<String, Object> attributes = super.getErrorAttributes(webRequest, includeStackTrace);
		if (error != null) {
			try {
				if (error instanceof BindException) {
					attributes.put("error", ERROR_VALIDATION);
					attributes.put("message", contertExceptionToMessage((BindException) error));
				} else if (error instanceof HttpMessageNotReadableException && error.getCause() instanceof InvalidFormatException) {
					attributes.put("error", ERROR_VALIDATION);
					attributes.put("message", contertExceptionToMessage((InvalidFormatException) error.getCause()));
				} else if (error instanceof ConstraintViolationException) {
					attributes.put("error", ERROR_VALIDATION);
					attributes.put("message", contertExceptionToMessage((ConstraintViolationException) error));
				} else if (error instanceof AppException) {
					attributes.put("error", "应用异常");
					attributes.put("message", error.getMessage());
				} else {
					attributes.put("error", "应用异常");
					attributes.put("message", error.getMessage());
				}
			} catch (Exception e) {
				attributes.put("error", "应用异常");
				attributes.put("message", e);
			}
		} else {
			if ("403".equals(attributes.get("status").toString())) {
				attributes.put("error", "应用异常");
				attributes.put("message", "访问权限不足！");
			}
		}
		return attributes;
	}
	
	private String contertExceptionToMessage(BindException bindException) throws JsonProcessingException {
		BindingResult bindingResult = bindException.getBindingResult();
		Map<String, String> errors = new HashMap<String, String>();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String fieldName = fieldError.getField();
			String fieldTypeName = translateClassName(bindingResult.getFieldType(fieldName));
			if (!errors.containsKey(fieldName)) {
				errors.put(fieldName, "必须为" + fieldTypeName);
			}
		}
		return new ObjectMapper().writeValueAsString(errors);
	}
	
	private String contertExceptionToMessage(InvalidFormatException invalidFormatException) throws JsonProcessingException {
		String fieldName = invalidFormatException.getPath().get(0).getFieldName();
		String fieldTypeName = translateClassName(invalidFormatException.getTargetType());
		Map<String, String> errors = new HashMap<String, String>();
		errors.put(fieldName, "必须为" + fieldTypeName);
		return new ObjectMapper().writeValueAsString(errors);
	}
	
	private String contertExceptionToMessage(ConstraintViolationException e) throws JsonProcessingException {
		Map<String, String> errors = new HashMap<String, String>();
		Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
		for (ConstraintViolation<?> violation : constraintViolations) {
			String name = violation.getPropertyPath().toString();
			Iterator<Node> it = violation.getPropertyPath().iterator();
			while (it.hasNext()) {
				name = it.next().getName();
			}
			errors.put(name, violation.getMessage());
		}
		return new ObjectMapper().writeValueAsString(errors);
	}

	private String translateClassName(Class<?> clazz) {
		if (clazz.equals(Integer.class)) {
			return "整数";
		} else if (clazz.equals(Float.class)) {
			return "浮点数";
		} else if (clazz.equals(Double.class)) {
			return "双精度浮点数";
		} else if (clazz.equals(Boolean.class)) {
			return "布尔型";
		} else {
			return clazz.getSimpleName();
		}
	}

}
