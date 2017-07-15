function main() {
	title = "Engagement Info";

	// call the repository to get the engagement info
	var json = remote.call("/conexiam/engagement/" + page.url.templateArgs.site);
	if (json.status == 200)	{
		obj = eval("(" + json + ")");
		model.result = obj;
	} else {
		obj = eval("(" + json + ")");
		obj.name = "Error";
		title = "Error";
		model.result = obj;
	}

	model.title = title;
}

main();