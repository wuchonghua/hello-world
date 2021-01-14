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
import com.xhdl.model.ConnectLine;
import com.xhdl.model.Device;
import com.xhdl.model.Line;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.lang.Math.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @brief 获取跌落保险设备层级信息
 * @author wuchonghua
 */
public class DeviceConnectServlet extends HttpServlet {

	private static final long serialVersionUID = -3217642862902045578L;
	
	/**
	 * 两杆之间的距离
	 */
	private static final double DISTANCCE_OF_POLE = 0.0005;
	private static final int DISTANCCE_OF_POLE_METER = 50;
	
    /**
 	* 地球半径
     */
    private static final double R = 6371e3;

	/**
	 * @brief 当前接口支持的版本
	 */
	private static final int SUPPORT_API_VERSION_1 = 1;
	
	private final DeviceInfoDao deviceInfoDao = new DeviceInfoDaoImpl();
	private final LineInfoDao lineInfoDao = new LineInfoDaoImpl();
	
	/**
	 * @brief 接口支持的版本
	 */
	private static final int SUPPORT_API_VERSIONS[] = { SUPPORT_API_VERSION_1 };

	public DeviceConnectServlet() {
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
        if (bodyObjectCopy.size() > 1) {
            resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_1002, null, 0, 0);
            resp.getWriter().write(resultObject.toString());
            return;
        }
        if (!bodyObject.containsKey("lineId")) {
            resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_1002, null, 0, 0);
            resp.getWriter().write(resultObject.toString());
            return;
        }
        JSONObject responseJson = getConnectLines(bodyObject.getString("lineId"));
        resultObject = CommonRespBuilder.buildResponseObject(CommonResultCode.COMMON_RESULT_CODE_0, responseJson.toString(), 0, 0);
        resp.getWriter().write(resultObject.toString());
    }
    
    public JSONObject getConnectLines(String lineId) {
        JSONObject responseJson = new JSONObject();
        
        Line mainLine = getMainLineBySubLine(lineId);
        if (mainLine == null) {
        	responseJson.put("mainLineId", "");
        	responseJson.put("connections", new JSONArray());
        	return responseJson;
        } else {
        	responseJson.put("mainLineId", mainLine.getLineID());
        }
        
        List<Line> mainLineList = Arrays.asList(mainLine);
        
        List<Line> descendantLines = lineInfoDao.getSubLinesByLines(mainLineList);
        // 以mainLine为根的所有线路，包括mainLine
        List<Line> lines = new ArrayList<Line>();
        lines.add(mainLine);
        lines.addAll(descendantLines);
        // 得到线路下的设备
        List<Device> devices = deviceInfoDao.getSubDevicesByLines(mainLineList);
        // 移除不符合条件的设备 1.杆塔号不是数字 2.没有经纬度
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
		
		// 筛选之后如果没有设备了 连接不出线路
        if (devices.size() < 1) {
        	responseJson.put("connections", new JSONArray());
        	return responseJson;
        }
        
        
        // 考虑特殊情况
        List<Device> ds = new ArrayList<>();
        for (Device device : devices) {
        	for (Line line : lines) {
				if (device.getLineID().equals(line.getPid()) && Integer.valueOf(device.getPoleID()).equals(line.getTnum())) {
					Device d = new Device();
					d.setLatitude(device.getLatitude());
					d.setLongitude(device.getLongitude());
					d.setLineID(line.getLineID());
					d.setPoleID("0");
					ds.add(d);
				} else if (Integer.valueOf(device.getPoleID()).equals(0) && device.getLineID().equals(line.getLineID())) {
					Device d = new Device();
					d.setLatitude(device.getLatitude());
					d.setLongitude(device.getLongitude());
					d.setLineID(line.getPid());
					d.setPoleID(String.valueOf(line.getTnum()));
					ds.add(d);
				}
			}
		}
        devices.addAll(ds);
        Map<String, List<String>> ls = new HashMap<>();
        for (Line l1 : lines) {
        	for (Line l2 : lines) {
    			if (Objects.equals(l1.getTnum(), l2.getTnum()) && Objects.equals(l1.getPid(), l2.getPid()) && !l1.getLineID().equals(l2.getLineID())) {
    				if (ls.containsKey(l1.getLineID())) {
    					ls.get(l1.getLineID()).add(l2.getLineID());
    				} else {
    					List<String> li = new ArrayList<String>();
    					li.add(l2.getLineID());
    					ls.put(l1.getLineID(), li);
    				}
    			}
    		}
		}
        
        
        // 设备按杆塔号排序
		Collections.sort(devices, this::compareDevice);
        // K 线路id  V 线路下的设备
        Map<String, List<Device>> lineDevicesMap = new LinkedHashMap<>();
        for (Line l : lines) {
        	lineDevicesMap.put(l.getLineID(), new ArrayList<>());
		}
        for (Device d : devices) {
    		List<Device> l = lineDevicesMap.get(d.getLineID());
    		l.add(d);
        }
        
        List<ConnectLine> connections = new ArrayList<>();
		// 1.将有设备的同一级的连接起来
		// 2.从这些连接起来的线路出发，连接他们的父线路  在连接父线路时要判断该父线路是不是已经被画过了
    	for (Map.Entry<String, List<Device>> lineDevices : lineDevicesMap.entrySet()) {
			// 连接同一级的设备
			connectDeviceOfSameLine(lineDevices.getKey(), lineDevices.getValue(), connections, lineDevicesMap, lines);
	        direction.set(false);
        }
    	
    	for (Map.Entry<String, List<Device>> lineDevices : lineDevicesMap.entrySet()) {
			// 连接父线路
			connectParent(mainLine.getLineID(), lineDevices.getKey(), connections, lines, false, lineDevicesMap, ls);
            direction.set(false);
        }
    	
    	for (ConnectLine c : connections) {
			if (c.getToPole() == 99999) {
				for (ConnectLine o : connections) {
					if (o.getLineId().equals(c.getLineId()) && o.getToPole() == c.getFromPole()) {
						double angle = getAngle(o.getFromLatitude(), o.getFromLongitude(), o.getToLatitude(), o.getToLongitude());
						double[] latLng = calLocationByDistanceAndLocationAndDirection(
								angle, c.getFromLongitude(), c.getFromLatitude(), 500);
						c.setToLatitude(latLng[1]);
						c.setToLongitude(latLng[0]);
					}
				}
			}
			if (c.getLineId().equals(mainLine.getLineID()) && c.getFromPole() == 0) {
				for (ConnectLine o : connections) {
					if (o.getLineId().equals(c.getLineId()) && o.getFromPole() == c.getToPole()) {
						double angle = getAngle(o.getToLatitude(), o.getToLongitude(), o.getFromLatitude(), o.getFromLongitude());
						double[] latLng = calLocationByDistanceAndLocationAndDirection(
								angle, c.getToLongitude(), c.getToLatitude(), (c.getToPole() - 0) * DISTANCCE_OF_POLE_METER);
						c.setFromLatitude(latLng[1]);
						c.setFromLongitude(latLng[0]);
					}
				}
			}
		}
    	
    	
    	if (!lineId.equals(mainLine.getLineID())) {
    		Line subLine = new Line();
    		subLine.setLineID(lineId);
    		List<Line> descendantHighLightLines = lineInfoDao.getSubLinesByLines(Arrays.asList(subLine));
    		Set<String> descendantHighLightLineIdSet = new HashSet<>();
    		for (Line descendantHighLightLine : descendantHighLightLines) {
    			descendantHighLightLineIdSet.add(descendantHighLightLine.getLineID());
			}
    		for (ConnectLine connectLine: connections) {
    			if (lineId.equals(connectLine.getLineId()) || descendantHighLightLineIdSet.contains(connectLine.getLineId())) {
    				connectLine.setHighlight(true);
    			}
    		}
    	} else {
    		// 干线全部高亮显示
    		for (ConnectLine connectLine: connections) {
    			connectLine.setHighlight(true);
    		}
    	}
    	
    	JSONArray result = new JSONArray();
    	result.addAll(connections);
    	responseJson.put("connections", result);
    	return responseJson;
    }
    
    
    
    private boolean remove(List<Line> lines, Map<String, List<Device>> lineDevicesMap, String id) {
    	for (Line line : lines) {
			if (id.equals(line.getPid())) {
				if (lineDevicesMap.get(line.getLineID()).size() > 0) {
					return true;
				}
				if (remove(lines, lineDevicesMap, line.getLineID())) {
					return true;
				}
			}
		}
    	return false;
    }
    
    
    
//    private void getLineChildMap(String lineId, Map<String, Set<String>> lineChildMap) {
//    	List<Line> lines = lineInfoDao.getChildLines(lineId);
//    	Set<String> lineIds = new HashSet<>();
//    	for (Line line : lines) {
//    		lineIds.add(line.getLineID());
//    	}
//    	lineChildMap.put(lineId, lineIds);
//    }
    
    private Line getMainLineBySubLine(String lineId) {
		Line line = lineInfoDao.getLineById(lineId);
		if (line == null) {
			return null;
		}
		if (isMainLine(line)) {
			return line;
		} else {
			return getMainLineBySubLine(line.getPid());
		}
	}
    
    
	private int compareDevice(Device d1, Device d2) {
		return Integer.parseInt(d1.getPoleID()) - Integer.parseInt(d2.getPoleID());
	}
	
	
	private int compareLine(Line l1, Line l2) {
		return l1.getTnum() - l2.getTnum();
	}
	
	private boolean isMainLine(Line line) {
		return line.getPid() == null || "0".equals(line.getPid());
	}
    
    
    private void connectParent(String mainLineId, String currentLineId, List<ConnectLine> connections, List<Line> lines, boolean offsetLatitude, Map<String, List<Device>> lineDevicesMap, Map<String, List<String>> ls) {
        if (mainLineId.equals(currentLineId)) {
        	// 干线 结束
        	return;
        } else {
        	Line currentLine = lineInfoDao.getLineById(currentLineId);
			// 绘制上层线路
        	// 找出currentLineId的父亲
        	Line parentLine = null;
        	for (Line l : lines) {
        		if (Objects.equals(currentLine.getPid(), l.getLineID())) {
        			parentLine = l;
        			break;
        		}
        	}
			// 看他的父亲有没有已经被绘制过了
			ConnectLine resetConnect1 = null;
			ConnectLine resetConnect2 = null;
			List<ConnectLine> resetConnects = new ArrayList<>();
			// 查找当前线路有没有和其他线路是从一个杆T接出来的
			for (ConnectLine connect : connections) {
				if (connect.getLineId().equals(parentLine.getLineID()) && connect.getFromPole() == currentLine.getTnum()) {
					resetConnect1 = connect;
				} 
				if (connect.getLineId().equals(parentLine.getLineID())
						&& connect.getToPole() == currentLine.getTnum()) {
					resetConnect2 = connect;
				}
				if (ls.containsKey(currentLineId)) {
					for (String s : ls.get(currentLineId)) {
						if (s.equals(connect.getLineId()) && connect.getFromPole() == 0) {
							resetConnects.add(connect);
						}
					}
				}
			}
			if (resetConnect1 != null && resetConnect2 != null) {
				for (ConnectLine connect : connections) {
					if (currentLineId.equals(connect.getLineId()) && connect.getFromPole() == 0) {
						for (ConnectLine c : resetConnects) {
							c.setFromLatitude(connect.getFromLatitude());
							c.setFromLongitude(connect.getFromLongitude());
						}
						resetConnect1.setFromLatitude(connect.getFromLatitude());
						resetConnect1.setFromLongitude(connect.getFromLongitude());
						resetConnect2.setToLatitude(connect.getFromLatitude());
						resetConnect2.setToLongitude(connect.getFromLongitude());
						break;
					}
				}
			} else {
        		for (ConnectLine connect : connections) {
                    if (currentLineId.equals(connect.getLineId()) && connect.getFromPole() == 0) {
                    	// 1.将没有设备的孩子线路删去 2.将上层线路的孩子线路中的currentLineId线路删去         换句话说：绘制上层线路的其他孩子线路 
                    	List<Line> childLinesOfCurrentLine = lineInfoDao.getChildLines(parentLine.getLineID());
                    	
                    	Iterator<Line> iterator = childLinesOfCurrentLine.iterator();
                		while (iterator.hasNext()) {
                			Line l = iterator.next();
                			if (lineDevicesMap.get(l.getLineID()).size() < 1 && !remove(lines, lineDevicesMap, l.getLineID())) {
                				iterator.remove();
                			}
                		}
                		
                    	Collections.sort(childLinesOfCurrentLine, this::compareLine);
                    	
                    	Line removeLine = null;
                    	for (Line subL : childLinesOfCurrentLine) {
                    		if (subL.getLineID().equals(currentLineId)) {
                    			removeLine = subL;
                    			break;
                    		}
                    	}                    	
                    	childLinesOfCurrentLine.remove(removeLine);
                    	
                    	connectFront(childLinesOfCurrentLine, connect.getFromLatitude(), connect.getFromLongitude(), parentLine.getLineID(), removeLine.getTnum(), connections, !offsetLatitude);
                    	connectBack(childLinesOfCurrentLine, connect.getFromLatitude(), connect.getFromLongitude(), parentLine.getLineID(), removeLine.getTnum(), null, connections, !offsetLatitude);
                        break;
                    }
                }
                // 继续递归往上绘制父线路 直到干线
                connectParent(mainLineId, parentLine.getLineID(), connections, lines, !offsetLatitude, lineDevicesMap, ls);
        	}
        }
    }
    
    /**
     * @param childLinesOfCurrentLine 支线线路集合
     * @param toLatitude 前面一点要连接到的纬度
     * @param toLongitude 前面一点要连接到的经度
     * @param lineId 线路id
     * @param toPoleId 前面一点要连接到的杆塔号
     * @param result 
     */
    private void connectFront(List<Line> childLinesOfCurrentLine, double toLatitude, double toLongitude, String lineId, int toPoleId, List<ConnectLine> connections, boolean offsetLatitude) {
    	// 要连接的点
    	Line l = null;
		for (int j = 0; j < childLinesOfCurrentLine.size(); j++) {
    		if (childLinesOfCurrentLine.get(j).getTnum() < toPoleId) {
    			l = childLinesOfCurrentLine.get(j);
    		}
		}
		if (l != null) {
			ConnectLine frontConnect = new ConnectLine();
			for (ConnectLine c : connections) {
				if (c.getLineId() == lineId && c.getFromPole() == toPoleId) {
					if (c.getToLatitude() > c.getFromLatitude()) {
						frontConnect.setFromLatitude(toLatitude - (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE / sqrt(2));
					} else {
						frontConnect.setFromLatitude(toLatitude + (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE / sqrt(2));
					} 
					if (c.getToLongitude() > c.getFromLongitude()) {
						frontConnect.setFromLongitude(toLongitude - (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE / sqrt(2));
					} else {
						frontConnect.setFromLongitude(toLongitude + (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE / sqrt(2));
					}
					break;
				}
			}
			if (frontConnect.getFromLatitude() == 0 || frontConnect.getFromLongitude() == 0) {
				if (offsetLatitude) {
					frontConnect.setFromLatitude(toLatitude + (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE);
					frontConnect.setFromLongitude(toLongitude);
				} else {
					frontConnect.setFromLatitude(toLatitude);
					frontConnect.setFromLongitude(toLongitude + (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE);
				}
			}
			
            frontConnect.setToLatitude(toLatitude);
            frontConnect.setToLongitude(toLongitude);
            frontConnect.setFromPole(l.getTnum());
            frontConnect.setToPole(toPoleId);
            frontConnect.setLineId(lineId);
            childLinesOfCurrentLine.remove(l);
            connections.add(frontConnect);
            connectFront(childLinesOfCurrentLine, frontConnect.getFromLatitude(), frontConnect.getFromLongitude(), lineId, frontConnect.getFromPole(), connections, offsetLatitude);
		} else {
			ConnectLine frontConnect = new ConnectLine();
			
			for (ConnectLine c : connections) {
				if (c.getLineId() == lineId && c.getFromPole() == toPoleId) {
					if (c.getToLatitude() > c.getFromLatitude()) {
						frontConnect.setFromLatitude(toLatitude - (toPoleId - 0) * DISTANCCE_OF_POLE / sqrt(2));
					} else {
						frontConnect.setFromLatitude(toLatitude + (toPoleId - 0) * DISTANCCE_OF_POLE / sqrt(2));
					} 
					if (c.getToLongitude() > c.getFromLongitude()) {
						frontConnect.setFromLongitude(toLongitude - (toPoleId - 0) * DISTANCCE_OF_POLE / sqrt(2));
					} else {
						frontConnect.setFromLongitude(toLongitude + (toPoleId - 0) * DISTANCCE_OF_POLE / sqrt(2));
					}
					break;
				}
			}
			if (frontConnect.getFromLatitude() == 0 || frontConnect.getFromLongitude() == 0) {
				if (offsetLatitude) {
					frontConnect.setFromLatitude(toLatitude + (toPoleId - 0) * DISTANCCE_OF_POLE);
					frontConnect.setFromLongitude(toLongitude);
				} else {
					frontConnect.setFromLatitude(toLatitude);
					frontConnect.setFromLongitude(toLongitude + (toPoleId - 0) * DISTANCCE_OF_POLE);
				}
			}
			
            frontConnect.setToLatitude(toLatitude);
            frontConnect.setToLongitude(toLongitude);
            frontConnect.setFromPole(0);
            frontConnect.setToPole(toPoleId);
            frontConnect.setLineId(lineId);
            connections.add(frontConnect);
		}
    }
    
    
    /**
     * @param childLinesOfCurrentLine 支线线路集合
     * @param fromLatitude 从哪个纬度出发连之后的线路
     * @param fromLongitude 从哪个经度出发连之后的线路
     * @param lineId 线路id
     * @param fromPoleId 从哪个杆塔号出发连后面的线路
     * @param terminalPoleId 到哪个杆塔号连接终止
     * @param result
     */
    private void connectBack(List<Line> childLinesOfCurrentLine, double fromLatitude, double fromLongitude, String lineId, int fromPoleId, String terminalPoleId, List<ConnectLine> connections, boolean offsetLatitude) {
    	// direction.set(true);
    	if (childLinesOfCurrentLine.size() < 1 && terminalPoleId != null) {
    		return;
    	}
    	
    	// l 要连接的点
    	Line l = null;
		for (int j = 0; j < childLinesOfCurrentLine.size(); j++) {
			// 找到第一个比fromPoleId大的T接杆塔号
    		if (childLinesOfCurrentLine.get(j).getTnum() > fromPoleId) {
    			// 第一个比fromPoleId大的T接杆塔号 是不是 比 terminalPoleId 小
    			if (terminalPoleId != null) {
    				if (Integer.parseInt(terminalPoleId) > childLinesOfCurrentLine.get(j).getTnum()) {
    					l = childLinesOfCurrentLine.get(j);
            			break;
    				} else {
    					return;
    				}
    			} else {
    				l = childLinesOfCurrentLine.get(j);
        			break;
    			}
    		}
		}
		if (l != null) {
			ConnectLine backConnect = new ConnectLine();
			backConnect.setFromLatitude(fromLatitude);
			backConnect.setFromLongitude(fromLongitude);
			if (offsetLatitude) {
				backConnect.setToLatitude(fromLatitude - (l.getTnum() - fromPoleId) * DISTANCCE_OF_POLE);
				backConnect.setToLongitude(fromLongitude);
			} else {
				backConnect.setToLatitude(fromLatitude);
				backConnect.setToLongitude(fromLongitude - (l.getTnum() - fromPoleId) * DISTANCCE_OF_POLE);
			}
            backConnect.setFromPole(fromPoleId);
            backConnect.setToPole(l.getTnum());
            backConnect.setLineId(lineId);
            childLinesOfCurrentLine.remove(l);
            connections.add(backConnect);
            direction.set(true);
            connectBack(childLinesOfCurrentLine, backConnect.getToLatitude(), backConnect.getToLongitude(), lineId, backConnect.getToPole(), terminalPoleId, connections, offsetLatitude);
		} else {
			ConnectLine backConnect = new ConnectLine();
			backConnect.setFromLatitude(fromLatitude);
			backConnect.setFromLongitude(fromLongitude);
			// to经纬度最后单独赋值
			backConnect.setFromPole(fromPoleId);
			backConnect.setToPole(99999);
			backConnect.setLineId(lineId);
			connections.add(backConnect);
		}
    }
    
    /**
 	* 是否正方向 
     * from低杆塔to高杆塔的方向为正方向
     */
    private static ThreadLocal<Boolean> direction = new ThreadLocal<Boolean>(){
    	@Override
    	protected Boolean initialValue() {
    		return false;
    	};
    };
    
    /**
 	* 连接同一线路下的设备
     * @param currentLineId  当前线路id
     * @param devices 当前线路下的设备们
     * @param result 
     */
    private void connectDeviceOfSameLine(String currentLineId, List<Device> devices, List<ConnectLine> connections, Map<String, List<Device>> lineDevicesMap, List<Line> lines) {
    	if (devices.size() < 1) {
    		return;
    	}
    	// 当前线路的孩子线路  如果孩子线路上没有设备 将它抛弃
    	List<Line> subLineOfCurrentLine = lineInfoDao.getChildLines(currentLineId);
        Iterator<Line> iterator = subLineOfCurrentLine.iterator();
		while (iterator.hasNext()) {
			Line l = iterator.next();
			if (lineDevicesMap.get(l.getLineID()).size() < 1 && !remove(lines, lineDevicesMap, l.getLineID())) {
				iterator.remove();
			}
		}
		Collections.sort(subLineOfCurrentLine, this::compareLine);
		
		if (devices.size() == 1) {
			connectFront(subLineOfCurrentLine, devices.get(0).getLatitude(), devices.get(0).getLongitude(), currentLineId, Integer.parseInt(devices.get(0).getPoleID()), connections, false);
		} else {
			double angleSecondDeviceToFirst = getAngle(devices.get(1).getLatitude(), devices.get(1).getLongitude(), devices.get(0).getLatitude(), devices.get(0).getLongitude());
			connectFront(subLineOfCurrentLine, devices.get(0).getLatitude(), devices.get(0).getLongitude(), currentLineId, Integer.parseInt(devices.get(0).getPoleID()), connections, angleSecondDeviceToFirst);
		}
		
        for (int i = 0; i < devices.size() - 1; i++) {
        	Device device = devices.get(i);
        	Device nextDevice = devices.get(i + 1);
        	connectBack(subLineOfCurrentLine, device.getLatitude(), device.getLongitude(), device.getLineID(), Integer.parseInt(device.getPoleID()), nextDevice.getPoleID(), connections, false);
        	// longitude经  latitude纬
        	ConnectLine connect = new ConnectLine();
        	if (direction.get()) {
        		connect.setFromLatitude(connections.get(connections.size() - 1).getToLatitude());
        		connect.setFromLongitude(connections.get(connections.size() - 1).getToLongitude());
        		connect.setToLatitude(nextDevice.getLatitude());
        		connect.setToLongitude(nextDevice.getLongitude());
        		connect.setFromPole(connections.get(connections.size() - 1).getToPole());
        		connect.setToPole(Integer.parseInt(nextDevice.getPoleID()));
        	} else {
        		connect.setFromLatitude(device.getLatitude());
                connect.setFromLongitude(device.getLongitude());
                connect.setToLatitude(nextDevice.getLatitude());
                connect.setToLongitude(nextDevice.getLongitude());
                connect.setFromPole(Integer.parseInt(device.getPoleID()));
                connect.setToPole(Integer.parseInt(nextDevice.getPoleID()));
        	}
			
            connect.setLineId(device.getLineID());
            connections.add(connect);
        }
        
        Device lastDevice = devices.get(devices.size() - 1);
        connectBack(subLineOfCurrentLine, lastDevice.getLatitude(), lastDevice.getLongitude(), lastDevice.getLineID(), Integer.parseInt(lastDevice.getPoleID()), null, connections,  false);
    }

	private void connectFront(List<Line> childLinesOfCurrentLine, double toLatitude, double toLongitude, String lineId, int toPoleId, List<ConnectLine> connections, double angleSecondDeviceToFirst) {
		// 要连接的点
    	Line l = null;
		for (int j = 0; j < childLinesOfCurrentLine.size(); j++) {
    		if (childLinesOfCurrentLine.get(j).getTnum() < toPoleId) {
    			l = childLinesOfCurrentLine.get(j);
    		}
		}
		
		if (l != null) {
			ConnectLine frontConnect = new ConnectLine();
			double[] latLng = calLocationByDistanceAndLocationAndDirection(angleSecondDeviceToFirst, toLongitude, toLatitude, (toPoleId - l.getTnum()) * DISTANCCE_OF_POLE_METER);
			frontConnect.setFromLatitude(latLng[1]);
			frontConnect.setFromLongitude(latLng[0]);
            frontConnect.setToLatitude(toLatitude);
            frontConnect.setToLongitude(toLongitude);
            frontConnect.setFromPole(l.getTnum());
            frontConnect.setToPole(toPoleId);
            frontConnect.setLineId(lineId);
            childLinesOfCurrentLine.remove(l);
            connections.add(frontConnect);
            connectFront(childLinesOfCurrentLine, frontConnect.getFromLatitude(), frontConnect.getFromLongitude(), lineId, frontConnect.getFromPole(), connections, angleSecondDeviceToFirst);
		} else {
			ConnectLine frontConnect = new ConnectLine();
			double[] latLng = calLocationByDistanceAndLocationAndDirection(angleSecondDeviceToFirst, toLongitude, toLatitude, (toPoleId - 0) * DISTANCCE_OF_POLE_METER);
			frontConnect.setFromLatitude(latLng[1]);
			frontConnect.setFromLongitude(latLng[0]);
            frontConnect.setToLatitude(toLatitude);
            frontConnect.setToLongitude(toLongitude);
            frontConnect.setFromPole(0);
            frontConnect.setToPole(toPoleId);
            frontConnect.setLineId(lineId);
            connections.add(frontConnect);
		}
		
	}
	
	/** 
     *  
     * @param long1 经度1 
     * @param lat1 维度1 
     * @param long2 经度2 
     * @param lat2 纬度2 
     * @return 
     */  
    public static double getDistance(double long1, double lat1, double long2,  
                                  double lat2) {  
        double a, b;
        lat1 = lat1 * PI / 180.0;  
        lat2 = lat2 * PI / 180.0;  
        a = lat1 - lat2;  
        b = (long1 - long2) * PI / 180.0;  
        double d;  
        double sa2, sb2;  
        sa2 = sin(a / 2.0);  
        sb2 = sin(b / 2.0);  
        d = 2 * R * asin(sqrt(sa2 * sa2 + cos(lat1) * cos(lat2) * sb2 * sb2));  
        return d;  
    }
    
    
    /** 
 	* 从正北顺时针方向开始计算
     * @param lat1 纬度1 
     * @param lng1 经度1 
     * @param lat2 纬度2 
     * @param lng2 经度2 
     * @return 
     */  
    public double getAngle(double lat1, double lng1, double lat2, double lng2) {
        double dRotateAngle = atan2(abs(lng1 - lng2), abs(lat1 - lat2));
        if (lng2 >= lng1) {
            if (lat2 >= lat1) {
            } else {
                dRotateAngle = PI - dRotateAngle;
            }
        } else {
            if (lat2 >= lat1) {
                dRotateAngle = 2 * PI - dRotateAngle;
            } else {
                dRotateAngle = PI + dRotateAngle;
            }
        }
        dRotateAngle = dRotateAngle * 180 / PI;
        return dRotateAngle;
    }

    
    /**
 	* 根据一点的坐标与距离，以及方向，计算另外一点的位置
     * @param angle 角度，从正北顺时针方向开始计算
     * @param startLong 起始点经度
     * @param startLat 起始点纬度
     * @param distance 距离，单位m
     * @return
     */
	public static double[] calLocationByDistanceAndLocationAndDirection(double angle, double startLong, double startLat,
			double distance) {
		double[] result = new double[2];
		// 将距离转换成经度的计算公式
		double δ = distance / R;
		// 转换为radian，否则结果会不正确
		angle = toRadians(angle);
		startLong = toRadians(startLong);
		startLat = toRadians(startLat);
		double lat = asin(sin(startLat) * cos(δ) + cos(startLat) * sin(δ) * cos(angle));
		double lon = startLong + atan2(sin(angle) * sin(δ) * cos(startLat),
				cos(δ) - sin(startLat) * sin(lat));
		// 转为正常的10进制经纬度
		result[0] = toDegrees(lon);
		result[1] = toDegrees(lat);
		return result;
	}
    
}
