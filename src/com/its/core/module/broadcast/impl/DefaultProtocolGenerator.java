/**
 * 
 */
package com.its.core.module.broadcast.impl;

import com.its.core.module.broadcast.IProtocolGenerator;
import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;

/**
 * �������� 2012-9-24 ����09:52:19
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DefaultProtocolGenerator implements IProtocolGenerator {
	
	//��ǰЭ��汾��
	public static final String CURRENT_VERSION	= "1.0.0.0";

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateAreaOverspeedMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateAreaOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateBlacklistVehicleMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateBlacklistVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateOverspeedMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateRealtimeVehicleMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateRealtimeVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}

}
