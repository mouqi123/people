<#--
<textarea name="textarea"></textarea>
-->
<#macro ueditor
	name value="" height="230"
	fullPage="false" toolbarSet="My"
	label="" noHeight="false" required="false" colspan="" width="100" help="" helpPosition="2" colon=":" hasColon="true"
	maxlength="65535"
	onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
	>
  
    <script type="text/javascript">
		 $().ready(function(e) {                var editor_content = new baidu.editor.ui.Editor({minFrameHeight:250});    editor_content.render( '${name}' );            });
   </script>
<#include "control.ftl"/><#rt/>
  <div>
    <script id="${name}" type="text/plain" name="${name}" style="width:100%;height:500px;">${value}</script>
	</div>
<#include "control-close.ftl"/><#rt/>
</#macro>