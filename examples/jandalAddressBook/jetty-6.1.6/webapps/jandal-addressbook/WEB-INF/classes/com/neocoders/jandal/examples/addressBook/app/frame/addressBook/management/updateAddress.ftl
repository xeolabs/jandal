<font face="helvetica" size="+1"><b>Updating Address</b></font>
</br>
</br>
${context.getFormTool().getFormOpenTag("save")}
	<table cellspacing="3px" cellpadding="5px">
		<tr>
			<td align="right">Name:</td>
			<td>
				<input name="name" type="text" value="${context.getOutput('name')}"/>&nbsp;<font face="helvetica" color="red"><b>${context.getOutput("nameError")}</b></font>
			</td>
		</tr>
		<tr>
			<td align="right">
			E-Mail:
			</td>
			<td>
				<input name="email" type="text" value="${context.getOutput('email')}"/>&nbsp;<font face="helvetica" color="red"><b>${context.getOutput("emailError")}</b></font>
			</td>
		</tr>
		<tr>
			<td align="right">
			URL:
			</td>
			<td>
				<input name="url" type="text" value="${context.getOutput('url')}"/>&nbsp;<font face="helvetica" color="red"><b>${context.getOutput("urlError")}</b></font>
			</td>
		</tr>
	</table>
	</br>
	<input type="submit" value="Save"/>&nbsp;<input type="button" value="Cancel" onclick="${context.getRequestTool('cancel').getAction()}"/>
${context.getFormTool().getFormCloseTag()}
