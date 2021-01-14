/**
 * 
 */
package com.xhdl.api.display;

import com.xhdl.common.CommonHttpHandler;
import com.xhdl.common.CommonParaParser;
import com.xhdl.common.CommonRespBuilder;
import com.xhdl.common.CommonResultCode;
import com.xhdl.impl.DeviceInfoDaoImpl;
import com.xhdl.impl.LineInfoDaoImpl;
import com.xhdl.methed.DeviceInfoDao;
import com.xhdl.methed.LineInfoDao;
import com.xhdl.model.Device;
import com.xhdl.model.Line;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @brief 返回故障区段连线
 * @author wuchonghua
 */
public class FaultAreaConnectServlet extends HttpServlet {

	private static final long serialVersionUID = -3217642862902045578L;

	private final DeviceInfoDao deviceInfoDao = new DeviceInfoDaoImpl();
	/**
	 * @brief 当前接口支持的版本
	 */
	private static final int SUPPORT_API_VERSION_1 = 1;
	
	private final DeviceConnectServlet deviceConnect = new DeviceConnectServlet();
	private final LineInfoDao lineInfoDao = new LineInfoDaoImpl();
	/**
	 * @brief 接口支持的版本
	 */
	private static final int SUPPORT_API_VERSIONS[] = { SUPPORT_API_VERSION_1 };

	public FaultAreaConnectServlet() {
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		CommonParaParser.initializeRequest(req);
		CommonRespBuilder.initializeResponse(resp);
		
		// 校验请求方式
		if (CommonHttpHandler.handleHttpMethod(req, resp)) {
			return;
		}
		// 校验JSON体格式
		JSONObject bodyObject = new JSONObject();
		try {
			bodyObject = JSONObject.fromObject(CommonHttpHandler.handleHttpBody(req));
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject errObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_100, null, 0, 0);
			resp.getWriter().write(errObject.toString());
			return;
		}
		// 校验公共参数部分
		if (CommonParaParser.parseCommonParameters(bodyObject, resp)) {
			return;
		}
		
		// 业务逻辑处理
		int iVersion = 0;
		try {
			iVersion = bodyObject.getInt(CommonParaParser.COMMON_PARA_IVERSION);
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject respObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_102, null, 0, 0);
			resp.getWriter().write(respObject.toString());
			return;
		}
		if (CommonParaParser.checkCommonParaIVersion(iVersion, SUPPORT_API_VERSIONS, resp)) {
			return;
		}
		switch (iVersion) {
			case SUPPORT_API_VERSION_1:
				processBusinessVersion_1(bodyObject, resp);
				break;
			default:
				break;
		}
	}

    private void processBusinessVersion_1(JSONObject bodyObject, HttpServletResponse resp) throws IOException {
        JSONObject resultObject = new JSONObject();
        JSONObject bodyObjectCopy = CommonParaParser.removeCommonParameters(bodyObject);
        if (bodyObjectCopy.size() > 3) {
            resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_1002, null, 0, 0);
            resp.getWriter().write(resultObject.toString());
            return;
        }
        if (!bodyObject.containsKey("lineId") || !bodyObject.containsKey("pole") || !bodyObject.containsKey("direction")) {
            resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_1002, null, 0, 0);
            resp.getWriter().write(resultObject.toString());
            return;
        }
        String lineId = bodyObject.getString("lineId");
        int pole = bodyObject.getInt("pole");
        boolean direction = bodyObject.getBoolean("direction");
 		
        JSONObject j = deviceConnect.getConnectLines(lineId);
    	JSONArray connections = j.getJSONArray("connections");
    	JSONArray result = new JSONArray();
    	
    	if (direction) {
    		getForwardConnections(connections, lineId, pole, result);
    	} else {
    		getReverseFaultConnections(connections, lineId, pole, result);
    	}
        
        resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_0, result.toString(), 0, 0);
        resp.getWriter().write(resultObject.toString());
    }
    
	private void getReverseFaultConnections(JSONArray connections, String lineId, int stopPole, JSONArray result) {
        
        List<Device> devices = deviceInfoDao.getDropDevices("and t1.line_id = '" + lineId + "'");
        Iterator<Device> iterator = devices.iterator();
 		while (iterator.hasNext()) {
 			Device d = iterator.next();
 			if (d.getLatitude() == 0 || d.getLongitude() == 0) {
 				iterator.remove();
 			} else {
 				try {
 					Integer.parseInt(d.getPoleID());
 				} catch (Exception e) {
 					iterator.remove();
 				}
 			}
 		}
         // 设备按杆塔号排序
 		Collections.sort(devices, this::compareDevice);
        
 		int startPole = 0;
 		for (Device device : devices) {
			if (Integer.parseInt(device.getPoleID()) < stopPole) {
				startPole = Integer.parseInt(device.getPoleID());
			}
		}
		
		for (Object o : connections) {
    		if (((JSONObject)o).getInt("fromPole") >= startPole && ((JSONObject)o).getInt("toPole") <= stopPole && ((JSONObject)o).getString("lineId").equals(lineId) ) {
    			result.add(o);
    		}
		}
		List<Line> childLines = lineInfoDao.getChildLines(lineId);
		for (Line childLine : childLines) {
			if (childLine.getTnum() != null && childLine.getTnum() <= stopPole && childLine.getTnum() > startPole) {
				getForwardConnections(connections, childLine.getLineID(), -1, result);
			}
		}
	}

	private int compareDevice(Device d1, Device d2) {
		return Integer.parseInt(d1.getPoleID()) - Integer.parseInt(d2.getPoleID());
	}
	
	
	private void getForwardConnections(JSONArray connections, String lineId, int startPole, JSONArray result) {
        
        List<Device> devices = deviceInfoDao.getDropDevices("and t1.line_id = '" + lineId + "'");
        Iterator<Device> iterator = devices.iterator();
 		while (iterator.hasNext()) {
 			Device d = iterator.next();
 			if (d.getLatitude() == 0 || d.getLongitude() == 0) {
 				iterator.remove();
 			} else {
 				try {
 					Integer.parseInt(d.getPoleID());
 				} catch (Exception e) {
 					iterator.remove();
 				}
 			}
 		}
         // 设备按杆塔号排序
 		Collections.sort(devices, this::compareDevice);
        
 		int stopPole = 99999;
 		for (Device device : devices) {
			if (Integer.parseInt(device.getPoleID()) >  startPole) {
				stopPole = Integer.parseInt(device.getPoleID());
				break;
			}
		}
		
		for (Object o : connections) {
    		if (((JSONObject)o).getInt("fromPole") >= startPole && ((JSONObject)o).getInt("toPole") <= stopPole && ((JSONObject)o).getString("lineId").equals(lineId) ) {
    			result.add(o);
    		}
		}
		List<Line> childLines = lineInfoDao.getChildLines(lineId);
		for (Line childLine : childLines) {
			if (childLine.getTnum() != null && childLine.getTnum() >= startPole && childLine.getTnum() < stopPole) {
				getForwardConnections(connections, childLine.getLineID(), -1, result);
			}
		}
	}
	
    
}
