<#if config.pkgName??>
package ${config.pkgName};
</#if>

import retrofit2.Call;
import retrofit2.http.*;

<#list imports as import>
import ${import};
</#list>

/**
<#if comment??>
 * ${comment}<br/>
</#if>
 * 
<#if config.declaredComment??>
 * ${config.declaredComment}
</#if>
 */
public interface ${class} {

<#list methods as method>
    /**
<#if method.comment??>
     * ${method.comment}
</#if>
<#if method.params??>
<#list method.params as param>
     * @param ${param.id} ${param.comment} 
</#list>
</#if>
<#if method.returnClass ??>
     * @return 返回由 ${method.returnClass} 对象序列化而成的 json 串。
</#if>
     */
<#if method.annos??>
<#list method.annos as anno>
    ${anno}
</#list>
</#if>
    Call<String> ${method.name}(<#list method.params as param>${param.expression}</#list>);

</#list>
}