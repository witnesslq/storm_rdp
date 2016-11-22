# storm_rdp 数据清洗与数据统计

本项目共包含两个应用：数据清洗和数据统计。

##数据清洗
主要流程是接入kafka来源的数据，根据配置进行清洗工作，转换成数据统计需要的数据结构。
另外承担多类数据的缓存连接工作，过滤工作，持久化工作

例子如下：

输入：
```
{
	"A":"A1",
	"B":"B1",
	"C":"C1",
	"D":"D1",
	"E":{
		"E1":"E11",
		"E2":"E21",
		"AMOUNT":"88.88",
		"E3":"E31"
	},
	"AMOUNT":99.99
}
```
配置：
```
[{
	configName : "HELLO1",
	biz : ["TEST,01", "$$A"],
	key : ["$$B", "$$C"],
	amount : "$$AMOUNT",
	count : "2"
}, {
	configName : "HELLO2",
	filter : [{
			key : "$$AMOUNT",
			compare : ">",
			type:"double",
			value : "$$E.AMOUNT"
		}
	],
	biz : ["TEST,02", "$$A"],
	key : ["$$B", "$$C", "$$E.E1"],
	amount : "$$E.AMOUNT,-,$$AMOUNT",
	count : "1",
	uuids : ["PIN|@@key,$$E.E2", "ORDER|@@key,$$E.E3"],
	kpis : ["JE|A|$$E.AMOUNT,+,$$AMOUNT"]
}
]
```
输出
```
[	{
		"amount" : -11.11,
		"biz" : "HELLO2,TEST,02,A1",
		"count" : 1,
		"key" : "B1,C1,E11",
		"kpis" : [{
				"amount" : 188.87,
				"count" : 0,
				"kpi" : "JE",
				"useCount" : false
			}
		],
		"uuids" : [{
				"name" : "ORDER",
				"value" : "B1,C1,E11,E31"
			}, {
				"name" : "PIN",
				"value" : "B1,C1,E11,E21"
			}
		]
	}, {
		"amount" : 99.99,
		"biz" : "HELLO1,TEST,01,A1",
		"count" : 2,
		"key" : "B1,C1"
	}
]
```
## s数据统计
将接收到的数据根据biz+key（维度）进行预计算，按照分、时、日、周、月实时统计到redis中，最后持久化到Hbasez中

