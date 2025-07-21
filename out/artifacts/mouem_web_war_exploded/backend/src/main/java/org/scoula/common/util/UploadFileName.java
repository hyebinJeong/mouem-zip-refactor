package org.scoula.common.util;

public class UploadFileName {
	public static String getUniqueName(String filename){
		int ix = filename.lastIndexOf(".");
		String name = filename.substring(0, ix); // 파일명
		String ext = filename.substring(ix+1); //확장명

		return String.format("%s-%d.%s", name, System.currentTimeMillis(), ext);
	}
}
