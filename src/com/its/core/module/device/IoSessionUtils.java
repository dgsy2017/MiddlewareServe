/**
 * 
 */
package com.its.core.module.device;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoSession;

/**
 * �������� 2012-9-20 ����02:00:58
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class IoSessionUtils {
	/*
	 * ����IoSession�����豸����key
	 * @param socket
	 * @return
	 */
	public static String generateKey(IoSession session){
		InetSocketAddress socketAddress = (InetSocketAddress)session.getRemoteAddress();
		return socketAddress.getAddress().getHostAddress();
	}
	
	public static String getRemoteIp(IoSession session){
		return ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
	}
	
	public static int getRemotePort(IoSession session){
		return ((InetSocketAddress)session.getRemoteAddress()).getPort();
	}

}
