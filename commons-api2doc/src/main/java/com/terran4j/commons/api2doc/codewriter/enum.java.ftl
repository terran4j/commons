<#if config.pkgName??>
package ${config.pkgName};
</#if>

/**
<#if config.declaredComment??>
 * ${config.declaredComment}
</#if>
 */
public enum ${class} {

<#list enums as enum>
<#if enum.comment??>
	/**
	 * ${enum.comment}
	 */
</#if>
	${enum.name},

</#list>

}