<font face="helvetica" size="+1"><b>Address List</b></font>
</br>
</br>
<a href="${context.getRequestTool("manage").getAction()}">Manage Addresses</a>
</br>
</br>
<table border="0" cellspacing="5px" cellpadding="5px">
<tr><th>Name</th><th>E-Mail</th><th>URL</th></tr>
<#list context.getOutput("addressList") as x>
<tr>
<td bgcolor="#DDDDFF"> ${x.getName()}</td>
<td bgcolor="#DDDDFF"> ${x.getEmail()}</td>
<td bgcolor="#DDDDFF"> ${x.getUrl()}</td>
</tr>
</#list>
</table> 
