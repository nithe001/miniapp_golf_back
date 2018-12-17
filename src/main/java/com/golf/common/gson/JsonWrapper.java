package com.golf.common.gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.golf.common.model.IPageInfo;

/**
 * json包装类,一般与前台的ext配合使用<br>
 * 满足ext对grid,form的自动解析格式
 *
 * @author 李旭光
 * @version 2012-8-6
 * @version 2012-10-20 <li>修复从数据库中直接load出来数据不能构造json的bug</li>
 */
public class JsonWrapper {

	public static final String SUCCESS = "success";

	public static final String MSG = "msg";

	public static final String DATA = "data";

	public static final String COUNT = "count";

	public static final String JSON = "json";
	
	public static final String HASLOGIN = "hasLogin";

	public static Gson createGson(){
		return new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
	}
	
	/**
     * 成功的json<br>
     * <code>{success:true}</code>
     *
     * @return
     */
    public static JsonElement newSuccessInstance() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(SUCCESS, true);
        return createGson().toJsonTree(map);
    }

    /**
     * 成功的json<br>
     * <code>{success:true}</code>
     *
     * @return
     */
    public static JsonElement newSuccessInstance(String msg) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(SUCCESS, true);
        map.put(MSG, msg == null ? "" : msg);
        return createGson().toJsonTree(map);
    }

	/**
	 * 包含错误信息的json<br>
	 * <code>{success:false,msg:$msg}</code>
	 *
	 * @param msg
	 *            错误信息
	 * @return
	 */
	public static JsonElement newErrorInstance(String msg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SUCCESS, false);
		map.put(MSG, msg == null ? "" : msg);
		return createGson().toJsonTree(map);
	}
	
	/**
	 * 包含错误信息的json<br>
	 * <code>{success:false,msg:$msg}</code>
	 *
	 * @param msg
	 *            错误信息
	 * @return
	 */
	public static JsonElement newErrorInstance4Login(String msg, boolean hasLogin) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SUCCESS, false);
		map.put(HASLOGIN, hasLogin);
		map.put(MSG, msg == null ? "" : msg);
		return createGson().toJsonTree(map);
	}
	
	/**
	 * 包含数据的json
	 *
	 * @param data
	 *            JsonElement或普通的对象
	 * @return
	 */
	public static JsonElement newDataInstance(Object data) {
		data = handleLazeObj(data);
		if (data instanceof JsonElement) {
			return newDataInstance((JsonElement) data);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SUCCESS, true);
		map.put(DATA, data);
		return createGson().toJsonTree(map);
	}
	
	/**
	 * 包含数据的json
	 *
	 * @param data
	 *            JsonElement或普通的对象
	 * @return
	 */
	public static JsonElement newDataInstance4Open(Object data, String errno) {
		data = handleLazeObj(data);
		if (data instanceof JsonElement) {
			return newDataInstance((JsonElement) data);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("errno", errno);
		map.put(DATA, data);
		return createGson().toJsonTree(map);
	}

	private static JsonElement newDataInstance(JsonElement data) {
		JsonElement obj = newSuccessInstance();
		obj.getAsJsonObject().add(DATA, data);
		return obj;
	}

	/**
	 * 生成纯json
	 *
	 * @param data
	 * @return
	 */
	public static JsonElement newJson(Object data) {
		data = handleLazeObj(data);
		if (data instanceof JsonElement) {
			return (JsonElement) data;
		}
		return createGson().toJsonTree(data);
	}

	/**
	 * 包含count属性的json,一般前台的grid需要
	 *
	 * @param data
	 * @param count
	 * @return
	 */
	public static JsonElement newCountInstance(Object data, long count) {
		data = handleLazeObj(data);
		if (data instanceof JsonElement) {
			return newCountInstance((JsonElement) data, count);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SUCCESS, true);
		map.put(DATA, data);
		map.put(COUNT, count);
		return createGson().toJsonTree(map);
	}
	
	/**
	 * 传递数据和其他信息
	 * @param data
	 * @param info
	 * @return
	 */
	public static JsonElement newInfoInstance(Object data, String info) {//自己。
		data = handleLazeObj(data);
		if (data instanceof JsonElement) {
			return newInfoInstance((JsonElement) data, info);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(DATA, data);
		map.put("info", info);
		return createGson().toJsonTree(map);
	}
	private static JsonElement newInfoInstance(JsonElement data, String info) {//自己
		JsonElement obj = newSuccessInstance();
		obj.getAsJsonObject().addProperty("info", info);
		obj.getAsJsonObject().add(DATA, data);
		return obj;
	}

	/**
	 * 包含count属性的json,一般前台的grid需要
	 *
	 * @param pageInfo
	 * @return
	 */
	public static JsonElement newCountInstance(IPageInfo<?> pageInfo) {
		List<?> items = pageInfo.getItems();
		if (items == null) {
			items = new ArrayList<Object>();
		}
		return newCountInstance(items, pageInfo.getCount());
	}

	private static JsonElement newCountInstance(JsonElement data, long count) {
		JsonElement obj = newSuccessInstance();
		obj.getAsJsonObject().addProperty(COUNT, count);
		obj.getAsJsonObject().add(DATA, data);
		return obj;
	}

	private static Object handleLazeObj(Object data) {
		if (data == null) {
			return new Object();
		}else{
			return data;
		}
//		if (!data.getClass().toString().contains("$$_javassist")) {
//			return data;
//		}
//		Object tmp = null;
//		try {
//			tmp = data.getClass().newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		BeanUtils.copyProperties(data, tmp);
//		return tmp;
	}

}
