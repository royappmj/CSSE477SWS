var home = "http://localhost:8080/plugins/AddressBook/";

window.onload = function() {
	// ajaxReq(home + "CreateNewContactServlet", populateDiv);
	// ajaxReq(home + "GetAllContactsServlet", populateDiv);
	// ajaxReq(home + "UpdateContactServlet/2", populateDiv);
	//ajaxReq(home + "DeleteContactServlet/1", populateDiv);
	// $("#get").on('click', function() { click("GetOneContactServlet/", "GET"); });
	$("#getall").on('click', function() { click("GetAllContactsServlet", "GET"); });
	$("#postsubmit").on('click', function() { click("CreateNewContactServlet", "POST"); });
}

function loadXMLDoc() {
	var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			document.getElementById("text").innerHTML = xmlhttp.responseText;
		}
	}
	// GET Request to get all contacts
	// xmlhttp.open("GET", "http://localhost:8080/plugins/AddressBook/GetAllContactsServlet", true);
	// xmlhttp.send();

	// GET Request to get one specific contact
	// xmlhttp.open("GET", "http://localhost:8080/plugins/AddressBook/GetOneContactServlet/1", true);
	// xmlhttp.send();

	// POST Request to Create new Contact
	// xmlhttp.open("POST", "http://localhost:8080/plugins/AddressBook/CreateNewContactServlet", true);
	// xmlhttp.send("Jane Doe,5 Peterson Rd,6687712202");

	// PUT Request to Update Contact
	xmlhttp.open("PUT", "http://localhost:8080/plugins/AddressBook/UpdateContactServlet/2", true);
	xmlhttp.send("address:44 Baxter St");

	// DELETE Request
	// xmlhttp.open("DELETE", "http://localhost:8080/plugins/AddressBook/DeleteContactServlet/1", true);
	// xmlhttp.send();
}

$(function () {
	$("[name=post]").keyup(function (e) {
		if (e.keyCode == 13) {
			ajaxReq(home + "CreateNewContactServlet", "POST", $("#namein").val() + "," +
				$("#addrin").val() + "," + $("#phonein").val(), populateDiv);
		}
	});
	$("#getid").keyup(function (e) {
		if (e.keyCode == 13) {
			ajaxReq(home + "GetOneContactServlet/" + $("#getid").val(), "GET", "", populateDiv);
		}
	});
	$("[name=put]").keyup(function (e) {
		if (e.keyCode == 13) {
			ajaxReq(home + "UpdateContactServlet/" + $("#putid").val(), "PUT",
				$("#putfield").val() + ":" + $("#putdata").val(), populateDiv);
		}
	});
	$("#deleteid").keyup(function (e) {
		if (e.keyCode == 13) {
			ajaxReq(home + "DeleteContactServlet/" + $("#deleteid").val(), "DELETE", "", populateDiv);
		}
	});
});

function click(url, type) {
	// console.log("start");
	ajaxReq(home + url, type, "", populateDiv);
	// console.log("done");
}

function ajaxReq(url, type, data, fxn) {
	var header = {};
	if(type == "PUT") {
		type = "POST";
		header = {"X-HTTP-Method-Override": "DELETE"};
	}
	$.ajax({
		url,
		type: type,
		data: data,
		headers: header,
		success: fxn
	});
}

function populateDiv(data) {
	var response = data;
	
	$("#output").html(response);
	console.log(response);
}