<center>
</br>
<img border="0px" src="${context.getResourceUrl("logo.png")}"/>
</br>
<a href="${context.getRequestTool("clear").getAction()}">Clear</a>
</br>
</br>
<table cellpadding="0px" cellspacing="0px" border="0px" width="400" height="300">
<tr>
<td>
${context.getChildView("root")}
</td>
</tr>
</table>
</br>
A treemap is a space-constrained visualization of a hierarchical structure, in this case a controller hierarchy.</br>
When you click on a cell, its controller creates two child controllers, each with their own cell, and arranges</br>
the cells of the children within its own. The arrangement alternates between horizontal and vertical as </br>
one descends into the hierarchy. Eventually, you will end up with a binary tree of Controllers.</br>
</br>Clicking <b>Clear</b> clears the map by deleting the root's child controllers.
</center>
