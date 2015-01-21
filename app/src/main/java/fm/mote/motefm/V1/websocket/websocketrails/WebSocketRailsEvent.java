package fm.mote.motefm.V1.websocket.websocketrails;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketRailsEvent {

	private String name;
	private Map<String, Object> attr;
	private Integer id;
	private String channel;
	private Object data;
	private String token;
	private String connectionId;
	private boolean success;
	private boolean result;	
	
	private WebSocketRailsDataCallback successCallback;
	private WebSocketRailsDataCallback failureCallback;	
	
	public WebSocketRailsEvent(Object data, WebSocketRailsDataCallback successCallback, WebSocketRailsDataCallback failureCallback)
	{
		if(data instanceof List) {
	        
			List<Object> listOfData = (List<Object>) data;
			
			name = (String) listOfData.get(0);
	        attr = (Map<String, Object>) listOfData.get(1);
	        
	        if (attr != null)
	        {
	            if (attr.get("id") != null)
	                id = (Integer) attr.get("id");
	            else
	                id = (int) new Random().nextInt();
	            
	            if (attr.get("channel") != null)
	                channel = (String) attr.get("channel");
	            
	            if (attr.get("data") != null)
	                this.data = attr.get("data");
	            
	            if (attr.get("token") != null)
	                token = (String) attr.get("token");	            
	            
	            if (listOfData.size() > 2 && listOfData.get(2) != null)
	                connectionId = (String) listOfData.get(2);
	            else
	                connectionId = "";
	            
	            if (attr.get("success") != null)
	            {
	                result = true;
	                success = (Boolean) attr.get("success");
	            }
	        }
	        
	        this.successCallback = successCallback;
	        this.failureCallback = failureCallback;
		}
	}

	public WebSocketRailsEvent(Object data){
	    
		this(data, null, null);
	}
	
	public boolean isPing()
	{
	    return "websocket_rails.ping".equals(name);
	}

	public String serialize()
	{
	    List<Object> array = new ArrayList();
	    
	    array.add(name);
	    array.add(this.attributes());
	    
	    ObjectMapper mapper = new ObjectMapper();
	    try {
			return mapper.writeValueAsString(array);
		} 
	    catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			Log.e("WebSocketRailsEvent", "exception", e);
		}
	    
	    return null;
	}

	public Object attributes()
	{
		Map<String, Object> attributes = new HashMap<String, Object>();		
		
		attributes.put("id", id);
		attributes.put("channel", channel);
		attributes.put("data", data);
		attributes.put("token", token);
		
	    return attributes;
	}	
		
	public void runCallbacks(boolean success, Object eventData) {
	    
		if (success && successCallback != null)
	        successCallback.onDataAvailable(eventData);
	    else {
		    if (failureCallback != null)
		        failureCallback.onDataAvailable(eventData);
	    }
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Object> getAttr() {
		return attr;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getConnectionId() {
		return connectionId;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public boolean isChannel() {
	    return channel != null;
	}

	public boolean isResult() {
		return result;
	}
}
