package com.dah.desb.infrastructure.util;

import javax.activation.MimetypesFileTypeMap;

public class ContentTypeUtils {

	private static MimetypesFileTypeMap mimeTypesMap;

	public static String getContentType(String filename) {
		if (mimeTypesMap == null) {
			mimeTypesMap = new MimetypesFileTypeMap(ContentTypeUtils.class.getResourceAsStream("mime.types"));
		}
		return mimeTypesMap.getContentType(filename);
	}

}
