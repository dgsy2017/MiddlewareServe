/**
 * 
 */
package com.its.core.module.device.vehicle.filter;

import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;
import com.its.core.util.XMLProperties;

/**
 * ������Ҫ����ʵʱ������Ϣ��ģ�������չ����
 * �������� 2012-9-20 ����03:26:58
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IProcessorFilter {
	
	/**
	 * ��������
	 * @param props
	 * @param propertiesPrefix
	 * @param no
	 * @throws Exception
	 */
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception;
	
	/**
	 * ����ʵʱ������Ϣ��RealtimeVehicleInfoBean
	 * @param realtimeVehicleInfoBean
	 * @return ��������true:���Լ���ִ����һ��IProcessorFilter; false:ִֹͣ�к�����IProcessorFilter
	 * @throws Exception
	 */
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception;

}
