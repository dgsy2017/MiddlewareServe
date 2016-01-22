/**
 * 
 */
package com.its.core.module.task.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

import com.its.core.module.task.ATask;
import com.its.core.module.task.bean.UploadDirBean;

/**
 * �������� 2013-11-19 ����08:11:42
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class UploadToFtpServerTask extends ATask {
	private static final Log log = LogFactory.getLog(UploadToFtpServerTask.class);
	
	private String ftpHost = null;
	
	private int port = 21;
	
	private String ftpAccount = null;
	
	private String ftpPassword = null;
	
	private String fileNameEncoding = null;
	
	private List<UploadDirBean> dirList = new ArrayList<UploadDirBean>(); 

	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.ftpHost = props.getProperty(propertiesPrefix,no,"ftp_server.host");		
		this.port = PropertiesHelper.getInt(propertiesPrefix, no, "ftp_server.port", props, this.port);
		this.ftpAccount = props.getProperty(propertiesPrefix,no,"ftp_server.account");
		this.ftpPassword = props.getProperty(propertiesPrefix,no,"ftp_server.password");
		this.fileNameEncoding = props.getProperty(propertiesPrefix,no,"file_name_encoding");
		
		int dirNum = props.getPropertyNum(propertiesPrefix, no, "dirs.dir");
		for(int i=0;i<dirNum;i++){
			String path 							= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "path");
			String includeSubdirectory 				= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "include_subdirectory");
			String createSubdirectoryAtFtpserver 	= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "create_subdirectory_at_ftpserver");
			String deleteLocalFile 					= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "delete_local_file");
			String deleteLocalEmptySubdirectory 	= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "delete_local_empty_subdirectory");
			
			String fileType 						= props.getProperty(propertiesPrefix, no, "dirs.dir", i, "file_type");
			
			UploadDirBean dirBean = new UploadDirBean();
			dirBean.setPath(path);
			if("true".equalsIgnoreCase(includeSubdirectory) || "y".equalsIgnoreCase(includeSubdirectory)){
				dirBean.setIncludeSubdirectory(true);
			}
			else{
				dirBean.setIncludeSubdirectory(false);
			}
			
			if("true".equalsIgnoreCase(createSubdirectoryAtFtpserver) || "y".equalsIgnoreCase(createSubdirectoryAtFtpserver)){
				dirBean.setCreateSubdirectoryAtFtpserver(true);
			}
			else{
				dirBean.setCreateSubdirectoryAtFtpserver(false);
			}			
			
			if("false".equalsIgnoreCase(deleteLocalFile) || "n".equalsIgnoreCase(deleteLocalFile)){
				dirBean.setDeleteLocalFile(false);
			}
			else{
				dirBean.setDeleteLocalFile(true);
			}		
			
			if("true".equalsIgnoreCase(deleteLocalEmptySubdirectory) || "y".equalsIgnoreCase(deleteLocalEmptySubdirectory)){
				dirBean.setDeleteLocalEmptySubdirectory(true);
			}
			else{
				dirBean.setDeleteLocalEmptySubdirectory(false);
			}				
			
			if(fileType.trim().toLowerCase().startsWith("b")) dirBean.setFileType(FTP.BINARY_FILE_TYPE);
			else dirBean.setFileType(FTP.ASCII_FILE_TYPE);
			
			dirList.add(dirBean);
		}
	}

	@Override
	public void execute() {
		FTPClient ftpClient = new FTPClient();
		
        if(StringHelper.isNotEmpty(this.getFileNameEncoding())){
        	log.debug("file name encoding = "+this.getFileNameEncoding());
        	ftpClient.setControlEncoding(this.getFileNameEncoding());
        }
        
		try {
			ftpClient.connect(this.getFtpHost(), this.getPort());
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)){
            	ftpClient.disconnect();
                log.warn("����FTP Server:"+this.getFtpHost()+"ʧ��!");
                return;
            }
            
            if (!ftpClient.login(this.getFtpAccount(), this.getFtpPassword())){
            	ftpClient.logout();
                log.warn("��¼FTP Server:"+this.getFtpHost()+"ʧ��,�����ʺŻ������Ƿ���ȷ!");
                return;
            }          
            
            //Use passive mode as default because most of us are behind firewalls these days.
            ftpClient.enterLocalPassiveMode();
            
            int size = this.getDirList().size();
            for(int i=0;i<size;i++){
            	UploadDirBean dirBean = (UploadDirBean)this.getDirList().get(i);
            	log.debug("��ʼ�ϴ�'"+dirBean.getPath()+"'Ŀ¼�µ��ļ�...");
            	long startTime = System.currentTimeMillis();
            	ftpClient.setFileType(dirBean.getFileType());
            	File[] fileArr = (new File(dirBean.getPath())).listFiles();
            	int count = this.upload(ftpClient, fileArr, dirBean);
            	long currentTime = System.currentTimeMillis();
            	log.debug("Ŀ¼'"+dirBean.getPath()+"'���ɹ��ϴ��ļ�:"+count+"��,��ʱ:"+((currentTime - startTime) / 1000F)+"��!");
            	
            	//���ص���Ŀ¼
            	//while(ftpClient.changeToParentDirectory()) ftpClient.changeToParentDirectory();
            }
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally{
            if (ftpClient.isConnected()){
                try{
                	ftpClient.disconnect();
                }catch (IOException f){
                	log.error(f);
                }
            }
		}
	}
	
	/**
	 * �ϴ��ļ�
	 * @param ftpClient
	 * @param fileArr
	 * @param includeSubdirectory
	 * @param createSubdirectoryAtFtpserver
	 * @return 
	 * @throws Exception
	 */
	private int upload(FTPClient ftpClient,File[] fileArr,UploadDirBean dirBean) throws Exception{
		int count = 0;
		int len = fileArr.length;
		for(int i=0;i<len;i++){
			File file = fileArr[i];
			if(file.isDirectory()){
				if(dirBean.isIncludeSubdirectory()){
					if(dirBean.isCreateSubdirectoryAtFtpserver()){
						ftpClient.makeDirectory(file.getName());
						ftpClient.changeWorkingDirectory(file.getName());
					}
					
					//�ݹ��ϴ���Ŀ¼
					count += this.upload(ftpClient, file.listFiles(), dirBean);
					
					//�ϴ���ɺ�,���Ŀ¼Ϊ��,��ɾ����
					if(dirBean.isDeleteLocalEmptySubdirectory() && file.listFiles().length==0){
						log.debug("ɾ����Ŀ¼:"+file.getAbsolutePath());
						file.delete();
					}
					
					//���ص���һ��Ŀ¼
					if(dirBean.isCreateSubdirectoryAtFtpserver()) ftpClient.changeToParentDirectory();
				}
			}
			else{
				InputStream inputStream = null;
				try {
					inputStream = new FileInputStream(file);
					boolean success = ftpClient.storeFile(file.getName(), inputStream);
					inputStream.close();
					if(success){
						log.debug("�ɹ��ϴ��ļ�:"+file.getAbsolutePath());
						//�ϴ��ɹ���ɾ���ļ�
						if(dirBean.isDeleteLocalFile()) file.delete();
						count++;
					}
					else{
						log.debug("�ϴ��ļ�:"+file.getAbsolutePath()+"ʧ��!");
					}
				} catch (Exception e) {
					log.error("�ϴ��ļ�'"+file.getAbsolutePath()+"'ʧ��:"+e.getMessage(),e);
				} finally{
					if(inputStream!=null){
						try {
							inputStream.close();
						} catch (Exception e) {
							log.error(e);
						}
					}
				}
				
				try {
					Thread.sleep(10);
				} catch (Exception e) {
				}
			}
		}
		
		return count;
	}

	public List<UploadDirBean> getDirList() {
		return dirList;
	}

	public void setDirList(List<UploadDirBean> dirList) {
		this.dirList = dirList;
	}

	public String getFtpAccount() {
		return ftpAccount;
	}

	public void setFtpAccount(String ftpAccount) {
		this.ftpAccount = ftpAccount;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFileNameEncoding() {
		return fileNameEncoding;
	}

	public void setFileNameEncoding(String fileNameEncoding) {
		this.fileNameEncoding = fileNameEncoding;
	}	
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setControlEncoding("GBK");
		try {
			ftpClient.connect("192.168.1.196");
            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)){
            	ftpClient.disconnect();
                log.warn("����FTP Serverʧ��!");
                return;
            }
            
            if (!ftpClient.login("area_overspeed", "26743021")){
            	ftpClient.logout();
                log.warn("��¼FTP Serverʧ��,�����ʺŻ������Ƿ���ȷ!");
                return;
            }          
            
//            FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_NT);
//            config.setServerLanguageCode("zh");
//            ftpClient.configure(config);
            
            //Use passive mode as default because most of us are behind firewalls these days.
            ftpClient.enterLocalPassiveMode();
            
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(new File("D:/work/pictures/overspeed/20070731-01/21/R10086T20070731210005L01I080V040A0&��N21341@@@@&0.P.JPG"));
				boolean success = ftpClient.storeFile("R10086T20070731210005L01I080V040A0&��N21341@@@@&0.P.JPG", inputStream);
//				boolean success = ftpClient.storeUniqueFile(inputStream);
				//OutputStream os = ftpClient.getOutputStream();

				
				inputStream.close();
				if(success){
					System.out.println("�ɹ��ϴ��ļ�!");
					
				}
				else{
					System.out.println("�ϴ��ļ�ʧ��!");
				}
				System.out.println("====="+ftpClient.getControlEncoding());
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(inputStream!=null){
					try {
						inputStream.close();
					} catch (Exception e) {
						log.error(e);
					}
				}
			}            
            
		}catch(Exception ex){
			ex.printStackTrace();
		} finally{
            if (ftpClient.isConnected()){
                try{
                	ftpClient.disconnect();
                }catch (IOException f){
                	log.error(f);
                }
            }
		}
	}

}
