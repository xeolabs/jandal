<font face="helvetica" size="+1"><b>Address List</b></font>
</br>
</br>
<a href="${context.getRequestTool("create").getAction()}">Create Address</a>
</br>
</br>
<table border="0" cellspacing="5px" cellpadding="5px">
<tr><th>Name</th><th>E-Mail</th><th>URL</th><th>Actions</th></tr>
<#list context.getOutput("addressList") as x>
<tr>
<td bgcolor="#DDDDFF"> ${x.getName()}</td>
<td bgcolor="#DDDDFF"> ${x.getEmail()}</td>
<td bgcolor="#DDDDFF"> ${x.getUrl()}</td>
<td bgcolor="#FFCCFF">&nbsp;<a href="${context.getRequestTool("update").setParam("id", x.getId()).getAction()}">Update</a>&nbsp;|&nbsp;<a href="${context.getRequestTool("delete").setParam("id", x.getId()).getAction()}">Delete</a>&nbsp;</td>
</tr>
</#list>
</table> 
