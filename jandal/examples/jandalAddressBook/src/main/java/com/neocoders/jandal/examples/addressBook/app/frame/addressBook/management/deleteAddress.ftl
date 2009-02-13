<font face="helvetica" size="+1" color="red"><b>Deleting Address</b></font>
</br>
</br>
${context.getFormTool().getFormOpenTag("delete")}
	<table cellspacing="3px" cellpadding="5px" bgcolor="#FFDDDD">
		<tr>
			<td align="right">Name:</td>
			<td>
				${context.getOutput('name')}
			</td>
		</tr>
		<tr>
			<td align="right">
				E-Mail:
			</td>
			<td>
				${context.getOutput('email')}
			</td>
		</tr>
		<tr>
			<td align="right">
				URL:
			</td>
			<td>
				${context.getOutput('url')}
			</td>
		</tr>
		</tr>
	</table>
	</br>
	Are you sure?
	</br>
	</br>
	<input type="submit" value="Delete"/>&nbsp;<input type="button" value="Cancel" onclick="${context.getRequestTool('cancel').getAction()}"/>
${context.getFormTool().getFormCloseTag()}