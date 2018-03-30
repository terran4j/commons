
## 后续开发计划

Api2Doc 项目后续的开发计划如下：

### 1.1.x 

计划提供以功能：
1.  支持各种用 @XxxMapping 修饰的方法生成文档，包括：
         @GetMapping、
         @PostMapping、
         @PutMapping、
         @DeleteMapping、
         @PatchMapping；
2. 支持各种参数生成文档，包括：
         @RequestHeader、
         @CookieValue、
         @RequestPart；
     并在文档页面的“请求参数”表格，加上“参数位置”这一列。
3.  文档页面，“URL示例”改为“请求示例”，并支持所有的 METHOD ，
    （目前只支持了 GET 方法）。
4. 可以导出为 html 格式的文档文件，可以离线浏览，
   支持在页面上手工操作导出，以及程序调用 API 自定义导出两种方式。
5. 可以导出为 md 格式的文档文件，可以放到 md 运行环境中浏览，
   支持在页面上手工操作导出，以及程序调用 API 自定义导出两种方式。
   