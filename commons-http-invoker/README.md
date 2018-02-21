
### 背景

在互联网繁荣的今天，HTTP + JSON 形式的 API 非常流行（即请求是 HTTP/HTTPS 协议，返回数据是 json 格式)。

然而调用 HTTP API 是一件比较繁琐的事情，虽然有很多开源工具帮助我们简化了 HTTP 调用的一些底层细节（如：HttpClient），但业务层面上，仍有需要编程来处理一些繁琐的细节。

比如我们看一下以下问题：

* HTTP调用时，有很多属性需要设置，如：

&emsp;&emsp; a. 设置方法，有：GET、POST、PUT...等多种方法;

&emsp;&emsp; b. 设置参数，并且对于GET方法，参数要拼在URL后面；对于POST方法，参数要URLEncode后拼起来放在body中;

&emsp;&emsp; c. 设置Http Header属性;

&emsp;&emsp; d. 设置Http Cookie属性;

  在调用HTTP API时，这些细节的实现都耦合在代码中，使代码变得臃肿，如果将来 API 有调整了，就需要修改代码以适应 API 的变化，不易于维护。

* 很多 HTTP 服务是无状态的（因为这样对服务器来说比较容易水平扩展），那这会话状态就需要客户端来维持，比如在很多应用中会话状态是这样维护的： 

&emsp;&emsp;  a. 首先客户端调用一个`/login`的登录请求，服务器会返回一个`access_token`的字段，作为登录成功后的凭证。

&emsp;&emsp;  b. 然后客户端需要保存这个`access_token`字段，以后每次访问其它请求时，都需求将这个`access_token`放在`Http Header或`Http Cookie`中提交。

&emsp;&emsp;  c. 服务端会校验这个`access_token`，并依此获取当前用户是否登录及用户信息，这样就实现了用户会话状态的维护。

&emsp;&emsp;  这类方式对服务器端是简化了，但增加了客户端的工作量，我们在调用 HTTP 服务时，需要编写一些额外的代码实现会话状态的维护及相关细节，这一定程序上增加了代码的复杂性。



### 解决方案

commons-http 提供了一种配置化的方式来使对 HTTP API 调用更加简单，并使代码更易于理解。

下一节，我们将用一个示例程序来体验一下它是如何工作的。


### 示例程序

假设我们要开发一个“累加计算”的应用程序，需求是这样的：
1. 打开应用时，初始值（后面称为total）为0.
2. 用户可以在客户端输入一个数字n，点击提交，让服务器计算 total + n 的和，计算结果 result 返回给客户端。
3. 客户端将 result 值赋值给 total，以保存累加计算的值。
4. 可以不停执行 2 ~ 3 步，以实现“累加计算”。

这个程序有点无聊，但它足够简单，通过它能快速演示 commons-http 提供的功能。

##### 服务端实现

首选，我们实现服务端代码，我们提供一个求和计算的HTTP API，实现两个数字求和计算。
这个 API 的 url 为： `http://${server}/calculator/plus`, 接受 GET 请求，入参为 a, b 返回数据为：
```
{
    "a": 1,
    "b": 2,
    "result": 3
}
```
a, b 表示输入的两个参数值， result 是 a + b 的结果值。
注意：为了实现服务端的无状态性，它并没有在 session 中保存 result 值。

我们编写一个类实现求和计算：
```java

public class Calculator {

	private final long a;
	
	private final long b;
	
	private long result;

	public Calculator(long a, long b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	public long getA() {
		return a;
	}

	public long getB() {
		return b;
	}

	public long getResult() {
		return result;
	}

	public Calculator plus() {
		result = a + b;
		return this;
	}
}
```

然后，我们基于 Spring Boot 编写一个 Controller 来提供一个 HTTP API，代码如下：
```java
@RequestMapping("/calculator")
@RestController
public class CalculatorController {

	@RequestMapping("/plus")
	@ResponseBody
	public Calculator plus(@RequestParam("a") long a, @RequestParam("b") long b) {
		return new Calculator(a, b).plus();
	}
	
}
```

这里，需要读者有一点 Spring Boot 的编程经验，如果您完全不了解 Spring Boot ，请先学习下 Spring Boot 。

最后，我们启动 Spring Boot 应用程序，在浏览器上访问： `http://localhost:8080/calculator/plus?a=3&b=5`，
结果返回： `{"a":3,"b":5,"result":8}`， 表示执行成功。


##### 客户端实现

现在，我们的重点来了，我们将用 commons-http 实现客户端功能。
传统的方式，是用 HttpConnection 或 HttpClient 之类的工具类，直接硬编码实现对 Http 接口的调用，
而在 commons-http 中，我们将先编写一个`http.config.json`文件来定义对这个 HTTP 接口的调用细节：

```json
{
	"locals": {
		"total": "0"
	},
	"actions": [
		{
			"id": "plus",
			"name": "两数相加",
			"url": "${url}/calculator/plus",
			"method": "POST",
			"params": {
				"a": "${total}",
				"b": "${input}"
			},
			"writes": [
				{ "to": "locals", "key": "total", "value": "${result}" }
			]
		}
	]
}
```

说明一下：
locals:  定义了客户端要维护的本地变量，上面定义了一个名为`total`本地变量，初始值为0。
actions: 定义了所有的 HTTP 接口，如上面定义了一个 id 为 plus 的 HTTP 接口（即`action`），这个`action`的 id 将在编程时用到，如：

&emsp;&emsp;  `session.action("plus").param("input", "3").exe()` 

表示调用了这个 plus 接口，并传入了一个名为input的入参。

这个 plus 接口，按 json 文件中的描述是这样的：

`"url": "${url}/calculator/plus"` 表示这个 HTTP 接口的 url 为`${url}/calculator/plus`，注意: json 定义中的值，可以用 ${...} 来引用一个变量，变量值从以下几个地方读取：

* Spring 容器中的环境配置，比如上面的这个 ${url} 是在`application.yml`文件中定义的：

```yml
url:  http://localhost:8080
```

它完全遵循 Spring Boot 的原理，指定了不同的环境，可以加载对应这个环境的配置值。

*  locals 中的值，也就是 commons-http 给当前会话提供的本地变量，这个本地变量可能会被改写。

* 调用 action 时的入参，如`session.action("plus").param("input", "3").exe()`这次调用，指定了一个入参 input = 3。

以上变量加载的优先级为： action入参 > locals变量 > Spring环境配置。

`"method": "POST"` 表示发起 POST 请求。

`
"params": {
    "a": "${total}",
    "b": "${input}"
}
`
表示 HTTP 请求要提交两个参数：参数 a 的值为 ${total}，即从 locals 变量中取 total 的值；参数 b 的值为 ${input}，即从action入参中取 input 参数的值。

`
"writes": [
    { "to": "locals", "key": "total", "value": "${result}" }
]
`
指示在执行完 HTTP 请求后，要将返回结果写入到一些地方保存起来：
* `"to"` 表示写入的地方，有三个值可选：`locals`表示要写入到本地变量中，后续的请求可以用${...}的方式从本地变量中取到这个值；`headers`表示要写入到 Http Header 中，后续的请求的 HTTP Header 中都会带上这个字段； `cookies`表示要写入到 Http Cookie 中，后续的请求都的 HTTP Cookie 中都会带上这个字段。
* `"key"` 表示要写入字段的 key 。
* `"value"` 表示要写入字段的值，这里可以用 ${...} 引用本次请求中返回数据中的值，可以用.号分隔的路径表达式引用具有嵌套结构的数据，比如返回数据为：
```
{
    "data": {
        "user": {
            "name": "neo4j",
            "title": "CEO",
            "age": 28
        }
    }
}
```
那`${data.user.name}`引用的值即为`neo4j`。

http.config.json 文件放在 classpth 的根路径即可。

在编写完`http.config.json`文件之后，调用 HTTP API 的 java 代码就特别简单了，如下所示：

```java

	@Autowired
	protected ApplicationContext context;
	
	@Test
	public void testHttpClient() throws HttpException {
		
		// 创建一个 httpClient 对象，它会加载 http.config.json 文件中定义的信息。
		HttpClient httpClient = HttpClient.create(context);
		
		// 创建一个 session 对象，它会在客户端维护一些会话信息，如 locals 变量、cookies 变量之类的。
		Session session = httpClient.create();
		
		// 调用 plus 接口，指定入参 input = 3，exe()方法会真正调用 HTTP 请求。
		Response response = session.action("plus").param("input", "3").exe();
		
		// 从返回结果中，取 result 字段的值。
		int total = response.getJson("result").getAsInt();
		Assert.assertEquals(3, total);
		
		// 再调用一次，发现返回结果确实在累加。
		response = session.action("plus").param("input", "5").exe();
		total = response.getJson("result").getAsInt();
		Assert.assertEquals(8, total);
	}
```

上面的代码很好理解吧 :-)


### 总结

commons-http 的好处是对 HTTP 的调用细节从代码提取到配置文件中描述，让真正调用 HTTP 接口时特别简单、容易理解，从而提高了代码的可维护性。

笔者目前用它最多的场景是编写 HTTP 接口的 Test Case 代码。