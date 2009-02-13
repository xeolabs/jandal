<script type="text/javascript" language="javascript">

var jandalRequestLocked = false;
var jandalAlertHandler = null
var jandalResetHandler = null;

function jandalPostEvent(
	_controller_id, 
	_controller_synch_key, 
	_request_type,  
	_view_event_name, 
	_resource_name, 
	_output_name, 
	_event_args) {
	
	if (jandalRequestLocked) {
		return;
	}
	jandalRequestLocked = true;
	
	var	paramStr = "_controller_id=" + _controller_id + "&" 
				+ "_controller_synch_key=" + _controller_synch_key + "&" 
				+ "_request_type=" + _request_type + "&" 
				+ "_view_event_name=" + _view_event_name + "&" 
				+ "_resource_name=" + _resource_name + "&" 
				+ "_output_name=" + _output_name + "&" 
				+ "_event_args=" + _event_args + "&" 
				+ "_ajax_enabled=true&";
    jandalMakePOSTRequest(paramStr);
    
    jandalRequestLocked = false;
};

function jandalSetResetHandler(resetHandler) {
	jandalResetHandler = resetHandler;
}

function jandalSetAlertHandler(alertHandler) {
	jandalAlertHandler = alertHandler;
}

function jandalMakePOSTRequest(parameters) {
	var xmlHttp = false;

	try
	{
		// Firefox, Opera 8.0+, Safari
		xmlHttp=new XMLHttpRequest();
	}
	catch (e)
	{
		// Internet Explorer
		try
		{
			xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
			try {
				netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");
			} catch (e) {
				alert("Permission UniversalBrowserRead denied.");
			}
		}
		catch (e)
		{
			try
			{
				xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
			}
			catch (e)
			{
			}
		}
	}
 
	if (!xmlHttp) {
		alert("Your browser does not support AJAX!");
		return false;
	}
	//
	//
	//
  	xmlHttp.onreadystatechange = function() {
      	if (xmlHttp.readyState == 4) {
         	if (xmlHttp.status == 200) {
 					var xmldoc = xmlHttp.responseXML;
					var responses = xmldoc.getElementsByTagName("response");
					for (j=0; j<responses.length; j++) {
					
						var code 	= responses[0].getElementsByTagName("code")[0].firstChild.nodeValue;
						var msg 	= responses[0].getElementsByTagName("message")[0].firstChild.nodeValue;

						switch (code){
							case "alert":
								if (jandalAlertHandler != null) {
									jandalAlertHandler(message);
								} else
								{ 
									alert(msg);
								}	
								break;

							case "reset": 
								if (jandalResetHandler != null) {
									jandalResetHandler(message);
								} else
								{ 
									alert(msg);	
									window.location.href=window.location.href;
								}
								return;
							
							default : 
								alert(msg + " - resetting - check log for details");
								window.location.href=window.location.href;
								return;
						}
					}

					var updates = xmldoc.getElementsByTagName("controller");
					for (j=0;j<updates.length;j++) {
  						id=updates[j].getElementsByTagName("id")[0].firstChild.nodeValue;
						content=updates[j].getElementsByTagName("content")[0].firstChild.nodeValue;
						document.getElementById(id).innerHTML=content;
      				}
         	} else {
            	alert('There was a problem with the request.');
         	}
      	}
   	};

	//
	//
	//
	xmlHttp.open('POST', window.location, true);
  	xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
   	xmlHttp.setRequestHeader("Content-length", parameters.length);
   	xmlHttp.setRequestHeader("Connection", "close");
   	xmlHttp.send(parameters);
}


function jandalPostForm(form) {
	var paramStr = "";
	var inputs = form.getElementsByTagName("*");
  	for (i=0; i<inputs.length; i++) {
  	 	if (inputs[i].type == "file" || inputs[i].type == "FILE") {

              	// ---------------------------------------------------------------------------
              	// File upload field not supported via AJAX yet,
            	// so we'll do something crafty. Instead of extracting the form fields
				// into a HTTP request object, like we do with AJAX, we'll modify the 
				// form so that it can be POSTED the regular way, then we'll submit it.
				//
				// The penalty is that the entire view is re-rendered (the old-fashioned way), 
				// not just the fragment for the current controller (the AJAX way).
				// ---------------------------------------------------------------------------

				var temp = form.action;
				form.action=window.location;
				form.submit();
				form.action = temp; // Don't really need to put this back, but lets be tidy.
              return;
        }
		if (inputs[i].tagName == "input" || inputs[i].tagName == "INPUT") {
   			if (inputs[i].type == "hidden" || inputs[i].type == "HIDDEN") {
               paramStr += inputs[i].name + "=" + encodeURI(inputs[i].value) + "&";
            }
            if (inputs[i].type == "text" || inputs[i].type == "TEXT") {
               paramStr += inputs[i].name + "=" + encodeURI(inputs[i].value) + "&";
            }
            if (inputs[i].type == "checkbox" || inputs[i].type == "CHECKBOX") {
               if (inputs[i].checked) {
                  paramStr += inputs[i].name + "=" + inputs[i].value + "&";
               } else {
                  paramStr += inputs[i].name + "=&";
               }
            }
            if (inputs[i].type == "radio" || inputs[i].type == "RADIO") {
               if (inputs[i].checked) {
                  paramStr += inputs[i].name + "=" + inputs[i].value + "&";
               }
            }
         }   
         if (inputs[i].tagName == "select" || inputs[i].tagName == "SELECT") {
            var sel = inputs[i];
            paramStr += sel.name + "=" + inputs[sel.selectedIndex].value + "&";
         }
	} 
	paramStr += "_ajax_enabled=true&";
	jandalMakePOSTRequest(paramStr);
};
</script>

