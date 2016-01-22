/**
 * 
 */
package com.its.core.module.broadcast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * �������� 2012-9-24 ����10:01:30
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ProtocolHelper {
	private static final Log log = LogFactory.getLog(ProtocolHelper.class);
	
	//Э��ָ���
	public static final String PROTOCOL_SPLITER 					= "|";
	
	/**
	 * Э�����ͣ�ע��ʵʱ������ȡ�ն��豸���ͻ�����ԭʼ����
	 * ���� register:realtime-vehicle-ori
	 */
	public static final String PROTOCOL_REGISTER_REALTIME_VEHICLE_ORI 	= "register:realtime-vehicle-ori";
	
	/**
	 * Э��ͷ����
	 */
	public static final String PROTOCOL_HEAD_REALTIME_VEHICLE		= "realtime-vehicle";
	public static final String PROTOCOL_HEAD_BLACKLIST_ALARM		= "blacklist-alarm";
	public static final String PROTOCOL_HEAD_OVERSPEED				= "overspeed";
	public static final String PROTOCOL_HEAD_AREA_OVERSPEED			= "area-overspeed";	
	public static final String PROTOCOL_HEAD_DUP_PLATE_MONITOR		= "dup-plate-monitor";
	
	/**
	 * Э�����ͣ�ע��ʵʱ����
	 * ����ɴ��豸��Ų���,��ʾֻ���ո��豸����µ�ʵʱ������Ϣ���粻�����������ʾ�������г�����Ϣ
	 * ���� register:realtime-vehicle 10001
	 */
	public static final String PROTOCOL_REGISTER_REALTIME_VEHICLE 	= "register:realtime-vehicle";
	
	/**
	 * Э�����ͣ�ע������������澯��Ϣ
	 * ����ɴ��豸��Ų���,��ʾֻ���ո��豸����µĺ������澯���粻�����������ʾ�������к����������澯��Ϣ
	 * ���� register:blacklist-alarm 10001
	 */
	public static final String PROTOCOL_REGISTER_BLACKLIST_ALARM 	= "register:blacklist-alarm";
	
	/**
	 * Э�����ͣ�ע�ᳬ�ٳ�����Ϣ
	 * ����ɴ��豸��Ų���,��ʾֻ���ո��豸����µĳ��ٳ�����Ϣ���粻�����������ʾ���������豸ץ�ĵĳ��ٳ�����Ϣ
	 */
	public static final String PROTOCOL_REGISTER_OVERSPEED 			= "register:overspeed";	
	
	/**
	 * ���͵�������
	 */
	public static final String PROTOCOL_HEART_BEAT_PACKAGE = "1";
	
	/**
	 * Э�����ͣ�ע��ʵʱ���������Դ��豸��Ų���
	 */
	public static final String PROTOCOL_CANCEL_REALTIME_VEHICLE 	= "cancel:realtime-vehicle";	
	
	/**
	 * Э�����ͣ�ע�������������澯��Ϣ�����Դ��豸��Ų���
	 */
	public static final String PROTOCOL_CANCEL_BLACKLIST_ALARM 		= "cancel:blacklist-alarm";	
	
	/**
	 * Э�����ͣ�ע�����ٳ�����Ϣ�����Դ��豸��Ų���
	 */
	public static final String PROTOCOL_CANCEL_OVERSPEED 			= "cancel:overspeed";	
	
	/**
	 * Э�����ͣ�ע�����䳬�ٳ�����Ϣ����������
	 */
	public static final String PROTOCOL_CANCEL_AREA_OVERSPEED 		= "cancel:area-overspeed";		
	
	/**
	 * Э�����ͣ�ע�����Ƴ���أ���������
	 */
	public static final String PROTOCOL_CANCEL_DUP_PLATE_MONITOR 	= "cancel:dup-plate-monitor";
	
	/**
	 * Э�����ͣ�ע�����䳬�ٳ�����Ϣ
	 * �޲���
	 */
	public static final String PROTOCOL_REGISTER_AREA_OVERSPEED 	= "register:area-overspeed";		
	
	/**
	 * Э�����ͣ�ע�����Ƴ����
	 * ���Ƴ���ؿ��Է�Ϊ���ࣺ�������е����Ƴ���أ����롰������������ء� ��Э�������Զ����Ƴ����
	 * 
	 */
	public static final String PROTOCOL_REGISTER_DUP_PLATE_MONITOR 	= "register:dup-plate-monitor";
	
	/**
	 * Э�����ͣ�ע�������Ѿ�ע���ָ��
	 */
	public static final String PROTOCOL_CANCEL_ALL				 	= "cancel:all";
	
	/**
	 * Э�����ͣ��رյ�ǰSESSION
	 */
	public static final String PROTOCOL_CLOSE				 		= "close";
	
	/**
	 * ����ע��Э��ͷ���ֻ�ȡ��Ӧ��ע��Э��
	 * @param cancelProtocolHead
	 * @return
	 */
	public static final String getRegisterProtocol(String cancelProtocolHead){
		String result = null;
		if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_REALTIME_VEHICLE)){
			result = PROTOCOL_REGISTER_REALTIME_VEHICLE;
		}
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_BLACKLIST_ALARM)){
			result = PROTOCOL_REGISTER_BLACKLIST_ALARM;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_OVERSPEED)){
			result = PROTOCOL_REGISTER_OVERSPEED;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_AREA_OVERSPEED)){
			result = PROTOCOL_REGISTER_AREA_OVERSPEED;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_DUP_PLATE_MONITOR)){
			result = PROTOCOL_REGISTER_DUP_PLATE_MONITOR;
		}			
		
		return result;
	}

}
