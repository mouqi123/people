<#--
表格标签：用于显示列表数据。
	value：列表数据，可以是Pagination也可以是List。
	class：table的class样式。默认"pn-ltable"。
	sytle：table的style样式。默认""。
	width：表格的宽度。默认100%。
-->
<#macro curri_table value listAction="v_list.do" class="pn-ltable" style="" theadClass="pn-lthead" tbodyClass="pn-ltbody" width="100%">
<table class="${class}" style="${style}" width="${width}" cellspacing="1" cellpadding="0" border="0">
<#if value?is_sequence><#local pageList=value/><#else><#local pageList=value.list/></#if>
<#list pageList as row>
<#if row_index==0>
<#assign i=-1/>
<thead class="${theadClass}"><tr><#nested row,i,true/></tr></thead>
</#if>
<#assign i=row_index has_next=row_has_next/>
<#if row_index==0><tbody  class="${tbodyClass}"><tr onmouseover="this.bgColor='#eeeeee'" onmouseout="this.bgColor='#ffffff'"><#else><tr onmouseover="this.bgColor='#eeeeee'" onmouseout="this.bgColor='#ffffff'"></#if><#nested row,row_index,row_has_next/>
<#if !row_has_next>
</tr></tbody>
<#else>
</tr>
</#if>
</#list>
</table>
<#if value?is_sequence>
<table width="100%" border="0" cellpadding="0" cellspacing="0"><tr><td align="center" class="pn-sp">
	<input class="pre-page" type="button" value="上一周" onclick="preWeek();"/>
	<input class="next-page" type="button" value="下一周" onclick="nextWeek();"/>
</td></tr></table>
</#if>
</#macro>