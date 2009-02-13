
<table cellpadding="0px" cellspacing="0px" border="0px" width="570">
	<tr>
		<td width="237px" height="187px" valign="top"><img src="${context.getResourceUrl("cables.jpg")}"/></td>
		<td valign="top"><img src="${context.getResourceUrl("title.png")}"/>
			</br>
			</br>
			Enter some text and click <i>Generate</i> to generate a JPEG image of it in the green area.<br/>
			<br/>
			${context.getChildView("imageGenerationController")}
			<br/>
			<br/>
			${context.getFormTool().getFormOpenTag("generate")}
				Your text: <input name="newImageText" type="text" value=""/>
				<br/>
				<br/>
				<input type="submit" value="Generate"/>
			${context.getFormTool().getFormCloseTag()}
		</td>
	</tr>
</table>


