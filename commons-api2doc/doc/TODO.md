
## 后续开发计划

Api2Doc 项目后续计划要开发的功能如下：

* 参数为对象，参数类型object，可以指出具体类型
* 可以导出为 html 格式的文档文件，可以离线浏览，
   支持在页面上手工操作导出，以及程序调用 API 自定义导出两种方式。
* 可以导出为 md 格式的文档文件，可以放到 md 运行环境中浏览，
   支持在页面上手工操作导出，以及程序调用 API 自定义导出两种方式。
* 对测试进行支持。
* 文档样式设计得更漂亮。
* 文档样式支持自定义。

### 1.0.2 （已发布于 2018-04-06 ）

新增了以下功能：
1.  支持各种用 @XxxMapping 修饰的方法生成文档，包括：
         `@GetMapping`、
         `@PostMapping`、
         `@PutMapping`、
         `@DeleteMapping`、
         `@PatchMapping`；
     （之前只支持 `@RequestMapping` 。）
2. 支持各种形式的参数，包括：
         `@PathVariable`、
         `@RequestHeader`、
         `@CookieValue`、
         `@RequestPart`；
     （之前只支持 `@RequestParam` 。）
     并在文档页面的“请求参数”表格，加上“参数形式”这一列。
3.  文档页面，“URL示例”改为“请求示例”，
     请求示例为 curl 命令格式，并支持所有的 HTTP 方法 ，
    （之前是 URL 格式，并且只支持了 GET 方法）。
    
修复了以下 BUG:

1. 修复当返回类型为简单类型时未能显示在文档的 BUG。