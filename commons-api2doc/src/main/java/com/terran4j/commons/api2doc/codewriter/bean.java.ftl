<#if config.pkgName??>
package ${config.pkgName};
</#if>

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
public class ${class} {

<#list fields as field>
    <#if field.comment ??>
    /**
     * ${field.comment}
     */
    </#if>
    private ${field.type} ${field.name};

</#list>

<#list fields as field>
    <#if field.comment ??>
    /**
     * @return ${field.comment}
     */
    </#if>
	public ${field.type} ${field.getMethod}() {
		return ${field.name};
	}

	public void ${field.setMethod}(${field.type} ${field.name}) {
		this.${field.name} = ${field.name};
	}

</#list>
}