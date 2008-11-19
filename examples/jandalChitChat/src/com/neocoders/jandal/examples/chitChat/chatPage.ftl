<center>
</br>
<table cellpadding="0px" cellspacing="0px" border="0px" width="570">
	<tr>
		<td width="50%" valign="top" align="right"><img src="${context.getResourceUrl("logo.jpg")}"/></td>
		<td width="50%" valign="top"><font face="Helvetica" size="+2"><b><img src="${context.getResourceUrl("title.png")}"/></b></font>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			</br>
These three chat clients each have a controller. When you post a message from one of them, it is
fired as a <i>child-event</i> at the root controller, which then broadcasts the message
back down to all the child controllers as a <i>parent-event</i>.
			<br/>
			<table cellspacing="10px" cellpadding="8px" border="0px">
				<tr>
					<td>${context.getChildView("gonzo")}</td>
					<td>${context.getChildView("piggy")}</td>
					<td>${context.getChildView("kermit")}</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</center>
