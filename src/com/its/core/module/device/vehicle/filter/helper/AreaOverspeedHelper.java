/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

import java.util.List;

/**
 * �������� 2012-12-18 ����12:19:11
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class AreaOverspeedHelper {
	
	/**
	 * �����豸ID�ͷ�������ȡ������ٶ�
	 * @param sectionsList
	 * @param deviceId
	 * @param direction
	 * @return
	 */
	public static CheckPointBean getCheckPointBean(List<SectionBean> sectionsList,String deviceId,String direction){
		int size = sectionsList.size();
		for(int i=0;i<size;i++){
			SectionBean sectionBean = sectionsList.get(i);
			if(!sectionBean.getDirection().equals(direction)) continue;
			
			CheckPointBean checkPoint = sectionBean.getFirstCheckPointBean();
			do {
				if(checkPoint.getDeviceMap().containsKey(deviceId)){
					return checkPoint;
				}				
			} while ((checkPoint=checkPoint.getNext())!=null);
		}
		return null;
	}	
	
	public static SectionBean getSectionBean(List<SectionBean> sectionsList,String deviceId,String direction){
		int size = sectionsList.size();
		for(int i=0;i<size;i++){
			SectionBean sectionBean = sectionsList.get(i);
			if(!sectionBean.getDirection().equals(direction)) continue;
			
			CheckPointBean checkPoint = sectionBean.getFirstCheckPointBean();
			do {
				if(checkPoint.getDeviceMap().containsKey(deviceId)){
					return sectionBean;
				}				
			} while ((checkPoint=checkPoint.getNext())!=null);
		}
		return null;
	}

}
