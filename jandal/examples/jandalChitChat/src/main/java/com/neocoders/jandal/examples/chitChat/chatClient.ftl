${context.getFormTool().getFormOpenTag("messagePosted")}
	<table cellpadding="5px" cellspacing="0px" border="0px">
		<tr>
			<td  align="center"><b>${context.getOutput("name")}</b></td>
		</tr>
		<tr>
			<td bgcolor="#AACCFF" align="left"><pre>${context.getOutput("messages")}</pre></td>
		</tr>
		<tr>
			<td align="center">
				<input name="message" type="text" value=""/>
 				</br>
 				<input type="submit" value="Post"/>
			</td>
		</tr>
	</table>
${context.getFormTool().getFormCloseTag()}