package com.people.sotp.common.cache;



public interface ICacheService {

	public void updateNoteRedis(String type,String phoneNum,String code) throws Exception;

	public String getNoteCode(String type,String phoneNum);
	
}
