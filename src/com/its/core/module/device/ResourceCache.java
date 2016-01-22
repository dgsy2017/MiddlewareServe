/**
 * 
 */
package com.its.core.module.device;

/**
 * �������� 2013-1-30 ����03:28:49
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ResourceCache {
	
	private static boolean PLATE_MATCH_IGNORE_FIRST_CHAR = true;
	
	private static int PLATE_MATCH_LEAST_CHECK_BITNUM = 3;
	
	//�������MAP
	private static PlateMonitorMap plateMonitorMap = null;
	
	public static void init(boolean plateMatchIgnoreFirstChar,int plateMatchLeastCheckBitnum,IPlateMonitorMapInit plateMonitorMapInit){
		PLATE_MATCH_IGNORE_FIRST_CHAR = plateMatchIgnoreFirstChar;
		PLATE_MATCH_LEAST_CHECK_BITNUM = plateMatchLeastCheckBitnum;		
		if(plateMonitorMap==null){
			plateMonitorMap = new PlateMonitorMap(PLATE_MATCH_IGNORE_FIRST_CHAR,PLATE_MATCH_LEAST_CHECK_BITNUM,plateMonitorMapInit);
		}	
		plateMonitorMap.reload();
	}
	
	public static PlateMonitorMap getPlateMonitorMap(){
		return plateMonitorMap;
	}

}
