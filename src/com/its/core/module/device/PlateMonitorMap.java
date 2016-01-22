/**
 * 
 */
package com.its.core.module.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.PlateHelper;
import com.its.core.util.StringHelper;

/**
 * �������� 2013-1-30 ����03:29:55
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class PlateMonitorMap {
	private static final Log log = LogFactory.getLog(PlateMonitorMap.class);
	
	//�Ƿ���Ե�һ���ַ������Ϊtrue,��ʹ��ռλ���滻
	private boolean ignoreFirstChar = true;
	
	//ģ��ƥ��ʱ���ټ��λ��
	private int leastCheckBitNum = 3; 
	
	/**
	 * ������Map
	 * Key:���ƺ���_������ɫ (plate_plateColorCode)
	 * Value:BlacklistBean
	 */
	private Map<String,List<BlacklistBean>> blacklistMap = Collections.synchronizedMap(new HashMap<String,List<BlacklistBean>>());

	private IPlateMonitorMapInit plateMonitorMapInit = null;
	
	public PlateMonitorMap(boolean ignoreFirstChar,int leastCheckBitNum,IPlateMonitorMapInit plateMonitorMapInit){
		this.ignoreFirstChar = ignoreFirstChar;
		this.leastCheckBitNum = leastCheckBitNum;
		this.plateMonitorMapInit = plateMonitorMapInit;
	}
	
	/**
	 * ��Ӻ�����
	 * @param blacklist
	 */
	public void put(BlacklistBean blacklist){
		String matchPlate = blacklist.getMatchPlate();
		if(StringHelper.isEmpty(matchPlate)){
			matchPlate = PlateHelper.getMatchPlate(blacklist.getPlate(), this.ignoreFirstChar,0);
			blacklist.setMatchPlate(matchPlate);
		}
		
		String plateColorCode = blacklist.getPlateColorCode();
		if(StringHelper.isEmpty(plateColorCode)){
			plateColorCode = "";
		}
		String key1 = blacklist.getPlate()+"_"+plateColorCode;		
		List<BlacklistBean> blacklistList1 = blacklistMap.get(key1);
		if(blacklistList1==null){
			blacklistList1 = new ArrayList<BlacklistBean>();
			log.debug("put blacklist = "+key1);
			blacklistMap.put(key1, blacklistList1);
		}
		else{
			//��blacklistList�����ԭ������ʷ��¼
			Iterator<BlacklistBean> blacklistIterator = blacklistList1.iterator();
			while(blacklistIterator.hasNext()){
				BlacklistBean tmp = blacklistIterator.next();
				if(tmp.getId().equals(blacklist.getId())){
					blacklistIterator.remove();
				}
			}			
		}
		blacklistList1.add(blacklist);

		if(matchPlate!=null && !matchPlate.equals(blacklist.getPlate())){
			String key2 = matchPlate+"_"+plateColorCode;
			List<BlacklistBean> blacklistList2 = blacklistMap.get(key2);
			if(blacklistList2==null){
				blacklistList2 = new ArrayList<BlacklistBean>();
				log.debug("put blacklist = "+key2);
				blacklistMap.put(key2, blacklistList2);
			}
			else{
				//��blacklistList�����ԭ������ʷ��¼
				Iterator<BlacklistBean> blacklistIterator = blacklistList2.iterator();
				while(blacklistIterator.hasNext()){
					BlacklistBean tmp = blacklistIterator.next();
					if(tmp.getId().equals(blacklist.getId())){
						blacklistIterator.remove();
					}
				}				
			}
			blacklistList2.add(blacklist);
		}
	}
	
	/**
	 * ����������
	 * @param blacklistId
	 */
	public void remove(String blacklistId){
		Iterator iterator = this.blacklistMap.values().iterator();
		while(iterator.hasNext()){
			List<BlacklistBean> blacklistList = (List<BlacklistBean>)iterator.next();
			Iterator blacklistIterator = blacklistList.iterator();
			while(blacklistIterator.hasNext()){
				BlacklistBean blacklist = (BlacklistBean)blacklistIterator.next();
				if(blacklist.getId().equals(blacklistId)){
					log.debug("remove "+blacklist.getId());
					blacklistIterator.remove();
				}
			}
		}
	}
	
	/**
	 * �Ƴ����еĺ�����
	 */
	public void removeAll(){
		Iterator iterator = this.blacklistMap.values().iterator();
		while(iterator.hasNext()){
			List<BlacklistBean> blacklistList = (List<BlacklistBean>)iterator.next();
			blacklistList.clear();
			blacklistList = null;
		}
		this.blacklistMap.clear();
	}
	
	/**
	 * ����װ�غ�������Ϣ
	 */
	public void reload(){
		this.removeAll();
		try {
			List<BlacklistBean> blacklistList = this.getPlateMonitorMapInit().load();
			int size = blacklistList.size();
			for(int i=0;i<size;i++){
				this.put(blacklistList.get(i));
			}
		} catch (Exception e) {
			log.error("����װ�غ�����ʱ����"+e.getMessage(),e);
		}
	}
	
	/**
	 * ƥ����������������з���ƥ�������ĺ�������¼
	 * @param plate
	 * @param plateColorCode
	 * @return
	 */
	public List<BlacklistBean> match(String plate,String plateColorCode){
		List<BlacklistBean> matchList = new ArrayList<BlacklistBean>();
		String matchPlate = PlateHelper.getMatchPlate(plate, this.ignoreFirstChar,0);
		boolean compareMatchPlate = !(StringHelper.isEmpty(matchPlate) || plate.equals(matchPlate));
		int validNumber = PlateHelper.getValidNumber(plate);
			
		compareMatchPlate = compareMatchPlate&(validNumber>=this.leastCheckBitNum);
		
		String key = plate+"_";
		List<BlacklistBean> blacklistList1 = this.blacklistMap.get(key);
		if(blacklistList1!=null){
			this.addNew(plate,matchList, blacklistList1);
			if(blacklistList1.size()==0){
				this.blacklistMap.remove(key);
			}
		}
		
		if(compareMatchPlate){
			key = matchPlate+"_";
			List<BlacklistBean> blacklistList2 = this.blacklistMap.get(key);
			if(blacklistList2!=null){
				this.addNew(plate,matchList, blacklistList2);
				if(blacklistList2.size()==0){
					this.blacklistMap.remove(key);
				}
			}
		}
		
		if(StringHelper.isNotEmpty(plateColorCode)){
			key = plate+"_"+plateColorCode;
			List<BlacklistBean> blacklistList3 = this.blacklistMap.get(key);
			if(blacklistList3!=null){
				this.addNew(plate,matchList, blacklistList3);
				if(blacklistList3.size()==0){
					this.blacklistMap.remove(key);
				}
			}
			
			if(compareMatchPlate){
				key = matchPlate+"_"+plateColorCode;
				List<BlacklistBean> blacklistList4 = this.blacklistMap.get(key);
				if(blacklistList4!=null){
					this.addNew(plate,matchList, blacklistList4);
					if(blacklistList4.size()==0){
						this.blacklistMap.remove(key);
					}
				}
			}
		}
		return matchList;
	}
	
	/**
	 * ��newList��δ�����Ҳ�����oriList�ĺ�������Ϣ��ӵ�oriList��
	 * @param oriList
	 * @param newList
	 */
	private void addNew(String plate,List<BlacklistBean> oriList,List<BlacklistBean> newList){
		int oriSize = oriList.size();
		Iterator<BlacklistBean> newIterator = newList.iterator();
		while(newIterator.hasNext()){
			BlacklistBean blacklist = newIterator.next();
			if(blacklist.getStartTime()!=null && System.currentTimeMillis()<blacklist.getStartTime()){
				//newIterator.remove();
				continue;
			}
			
			//�ѹ��ڣ���ɾ��������
			if(blacklist.getEndTime()!=null && System.currentTimeMillis()>blacklist.getEndTime()){
				newIterator.remove();
				continue;
			}
			
			//����ƥ��λ��С������ƥ��λ�������
			int currentMatchNumber = PlateHelper.getMatchNumber(plate, blacklist.getPlate());	
			if(currentMatchNumber<this.leastCheckBitNum){
				continue;
			}
			
			String blacklistId = blacklist.getId();
			
			boolean found = false;
			for(int j=0;j<oriSize;j++){
				if(blacklistId.equals(oriList.get(j).getId())){
					found = true;
					break;
				}
			}
			if(!found){
				oriList.add(blacklist);
			}			
		}		
	}

	public IPlateMonitorMapInit getPlateMonitorMapInit() {
		return plateMonitorMapInit;
	}

	public void setPlateMonitorMapInit(IPlateMonitorMapInit plateMonitorMapInit) {
		this.plateMonitorMapInit = plateMonitorMapInit;
	}

}
