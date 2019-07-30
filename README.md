| 参数名      | 描述      | 类型 |
| -----------|:---------:| -----:|
| accesstype     | 移动端接入网络方式 可选值：移动接入网络：0  wifi接入网络：1   仅gps坐标转换：2     | int |
| imei     | 手机imei号    | string |
| smac     | 手机mac码     | string |
| clientip     | 移动网关IP     | string |
| cdma     | 是否为cdma。非cdma：0; cdma：1      | int |
| imsi     | 移动用户识别码     | string |
| gps     | 手机GPS数据 取值规则： 经度\|纬度\|半径     | string |
| network     | 无线网络类型 GSM/GPRS/EDGE/HSUPA/HSDPA/WCDMA (注意大写)     | string |
| tel     | 手机号码     | string |
| bts     | 基站信息 基站信息，非CDMA格式为：mcc, mnc,lac,cellid,signal；CDMA格式为：sid,nid,bid,lon,lat,signal其中lon,lat可为空，格式为：sid,nid,bid,,,signal     | string |
| nearbts     | 周边基站信息 基站信息1\|基站信息2\|基站信息3….     | string |
| mmac     | 已连热点mac信息 mac,signal,ssid。 如：f0:7d:68:9e:7d:18,-41,TPLink     | string |
| macs     | WI-FI列表中mac信息 单mac信息同mmac，mac之间使用“\|”分隔     | string |


	

	
	
