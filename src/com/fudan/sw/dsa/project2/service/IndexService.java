package com.fudan.sw.dsa.project2.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fudan.sw.dsa.project2.bean.Address;
import com.fudan.sw.dsa.project2.bean.Navigator;
import com.fudan.sw.dsa.project2.bean.ReturnValue;
import com.fudan.sw.dsa.project2.constant.FileGetter;

/**
 * this class is what you need to complete
 * @author zjiehang
 *
 */
@Service
public class IndexService 
{
	//the subway graph
	private Navigator navigator = null;
	
	/**
	 * create the graph use file
	 */
	public void createGraphFromFile()
	{
		//如果图未初始化
		if(navigator==null)
		{
			FileGetter fileGetter= new FileGetter();
			//create the graph from file
			navigator = new Navigator();
			navigator.loadMap(fileGetter.readFileFromClasspath());
		}
	}
	
	
	public ReturnValue travelRoute(Map<String, Object>params)
	{
		String startAddress = 	params.get("startAddress").toString();	
		String startLongitude = params.get("startLongitude").toString();
		String startLatitude = params.get("startLatitude").toString();
		String endAddress = params.get("endAddress").toString();
		String endLongitude = params.get("endLongitude").toString();
		String endLatitude = params.get("endLatitude").toString();
		String choose = params.get("choose").toString();
		
		Address startPoint = new Address(startAddress, startLongitude, startLatitude);
		Address endPoint = new Address(endAddress, endLongitude, endLatitude);
		List<Address> addresses;
		switch (choose)
		{
		case "1":
			//步行最少
			//举个例子
			addresses = navigator.getPath_OF_MINWALIKING(startPoint,endPoint);
			break;
		case "2":
			//换乘最少
			addresses = navigator.getPath_OF_MINCHANGETIMES(startPoint,endPoint);
			break;
		case "3":
			//时间最短:
			addresses = navigator.getPath_OF_MINTIME(startPoint,endPoint);
			break;
		default:
			addresses = navigator.getPath_OF_MINWALIKING(startPoint,endPoint);
			break;
		}
		
		ReturnValue returnValue=new ReturnValue();
		returnValue.setStartPoint(startPoint);
		returnValue.setEndPoint(endPoint);
		returnValue.setSubwayList(addresses);
		returnValue.setMinutes(navigator.getMinutes());
		returnValue.setWalkDistance(navigator.getWalkDistance());
		return returnValue;
	}
}
