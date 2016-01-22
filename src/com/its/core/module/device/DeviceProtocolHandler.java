/**
 * 
 */
package com.its.core.module.device;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

/**
 * �������� 2012-9-19 ����05:53:28
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceProtocolHandler extends IoHandlerAdapter {
	private static final Log log = LogFactory.getLog(DeviceProtocolHandler.class);
	
	private DeviceCommunicateModule deviceCommunicateModule = null;
	
	public DeviceCommunicateModule getDeviceCommunicateModule() {
		return deviceCommunicateModule;
	}

	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule) {
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

	public DeviceProtocolHandler(DeviceCommunicateModule deviceCommunicateModule){
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionOpened(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.info("������:"+session.getRemoteAddress());
		String sessionKey = IoSessionUtils.generateKey(session);		
		Map<String, IoSession> sessionsMap = this.getDeviceCommunicateModule().getSessionsMap(); 
		if(sessionsMap.containsKey(sessionKey)){
			log.info("�Ѿ����ڸ����ӣ�"+sessionsMap.get(sessionKey).getRemoteAddress()+",�����Ƴ���");
			IoSession existSession = sessionsMap.remove(sessionKey);
			existSession.close();
		}
		
		log.debug("����������:"+session.getRemoteAddress());
		sessionsMap.put(sessionKey, session);		
		
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String theMessage = ((String) message).trim();
		log.info("������Ϣ��"+theMessage);		
		
		Iterator<MessageBean> iterator = MessageHelper.parse(theMessage).iterator();
		while(iterator.hasNext()){
			MessageBean messageBean = iterator.next();
			messageBean.setSessionKey(IoSessionUtils.generateKey(session));
			messageBean.setFromIp(IoSessionUtils.getRemoteIp(session));
			
			String headWord = messageBean.getHead();			
			
//			BlockingQueue<MessageBean> queue = null;

//			if(headWord.startsWith("DS")){
				//����ǰ���豸����Ϣ
			BlockingQueue<MessageBean> queue = this.getDeviceCommunicateModule().getInfoQueue(headWord);				
				if(queue==null){					
					queue = this.getDeviceCommunicateModule().getInfoQueue("other");
				}				
//			}
			
			if(queue==null){
				log.warn("����ͷ����Ϊ��'"+headWord+"'����Ϣû���ҵ���Ӧ�Ĵ������");				
			}
			else{
//				queue.put(messageBean);				
				try {
					boolean success = queue.offer(messageBean, 1, TimeUnit.SECONDS);					
					if(!success){
						log.warn("��������"+headWord+"������ǰ���г���:"+queue.size());
						queue.clear();
					}	
					log.debug("��ǰ���г���:"+queue.size());
				} catch (InterruptedException e) {
					queue.clear();
					log.error(e.getMessage(),e);
				}
			}
			
			iterator.remove();
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageSent(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.info("�Ѿ����ͣ�"+message);
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionClosed(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug("sessionClosed:"+session.getRemoteAddress());
		int remotePort = IoSessionUtils.getRemotePort(session);
		String key = IoSessionUtils.generateKey(session);
		Map<String, IoSession> sessionsMap = this.getDeviceCommunicateModule().getSessionsMap(); 
		if(sessionsMap.containsKey(key)){
			IoSession tempSession = sessionsMap.get(key);
			if(remotePort==IoSessionUtils.getRemotePort(tempSession)){
				log.debug("�ر����ӣ�"+sessionsMap.get(key).getRemoteAddress());
				tempSession = sessionsMap.remove(key);
				tempSession.close();				
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionCreated(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionCreated(session);
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionIdle(org.apache.mina.common.IoSession, org.apache.mina.common.IdleStatus)
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		log.debug("sessionIdle:"+status.toString());
	}	
	
	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#exceptionCaught(org.apache.mina.common.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)	throws Exception {
		log.error("exceptionCaught:"+session+" : "+cause);
		if(session!=null) session.close();
	}

}
