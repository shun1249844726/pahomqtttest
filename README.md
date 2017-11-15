# pahomqtttest
paho  MQTT  封装。


# 11-15
源码中
```
public IMqttToken subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) throws MqttException {

		return subscribe(topicFilters, qos, null, null, messageListeners);
	}
```
其中
```
public IMqttToken subscribe(String[] topicFilters, int[] qos, Object userContext, IMqttActionListener callback, IMqttMessageListener[] messageListeners) throws MqttException {
		IMqttToken token = new MqttTokenAndroid(this, userContext, callback, topicFilters);
		String activityToken = storeToken(token);
		mqttService.subscribe(clientHandle, topicFilters, qos, null, activityToken, messageListeners);

		return null;
	}
```
返回了个null ....
所以这个方法不能用。

但是 一个订阅，一个回调的还是可以用的。
```
subscribe(String topicFilter, int qos, IMqttMessageListener messageListener)
```

