

## 简介
专注于API文档的生成，可以根据代码中的API信息自己生成漂亮实用的文档。

Bug Fixed:
1. 修复参数名重复的问题；
2. 修复 java.util.Date 未 import 的问题；
3. 公共参数 @Header("Authorization") String Authorization，不需要登录的方法不加。

添加功能：
1. 扫描 api2doc/${fid}/*.md 文件，自动添加到文档中。