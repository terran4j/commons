
<#if doc.comment??>
**API简介** 
- ${doc.comment}
</#if>

<br/>

**请求URI**
<#list doc.paths as path>
- `${path}`
</#list>

**请求方法**
<#list doc.methods as method>
- `${method}`
</#list>

<#if doc.params?? && (doc.params?size > 0) >
**请求参数**

| 参数名 | 是否必须 | 类型   | 说明 | 示例值 |
|:----  |-----  |-----   |-----    |-----   |
<#list doc.params as param>
| ${param.id} | ${param.requiredName} | ${param.typeName} | ${param.comment!} | ${param.sample!} |
</#list>
</#if>

<br/>

<#if doc.sample??>
**返回示例**

```json
${doc.sample}
```

</#if>

<#if doc.results?? && (doc.results?size > 0) >

<#list doc.results as result>
<#if result.groupId??><span id="${result.groupId}"><br /></span></#if>
**<#if result.groupName??>${result.groupName}字段说明：<#else>返回字段</#if>**


| 参数名 | 类型   | 说明 | 示例值 |
|:---- |----- |-----  |----- |
<#list result.children as item>
| ${item.id} | <#if item.refGroupId??>[${item.typeName}](#${item.refGroupId})<#else>${item.typeName}</#if> | ${item.comment!} | ${item.sample!} |
</#list>

</#list>
</#if>


<#if doc.errors?? && (doc.errors?size > 0) >
**错误码**

| 错误码    | 说明 |
|:----    |-----   |
<#list doc.errors as error>
| ${error.id} | ${error.comment!} |
</#list>
</#if>
