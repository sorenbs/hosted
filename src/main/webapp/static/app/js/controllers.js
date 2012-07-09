function AssetsCtrl($scope) {
	$scope.images = [
    { "name": "Nexus S",
    	"snippet": "Fast just got faster with Nexus S."
    },
    { "name": "Motorola ",
    	"snippet": "The Next, Next Generation tablet."
    },
    { "name": "MOTOROLA XOOM™",
    	"snippet": "The Next, Next Generation tablet."
    }
  ];

	function handleFiles(files) {
		for (var i = 0; i < files.length; i++) {
			var f = files[i];
			console.log(f.name);
			$scope.images.push({ name: f.name, snippet: "Cool!" });

		}
		$scope.$apply();
		console.log(files);
	}

	var dropbox = document.getElementById("assetsContainer");
	window.addEventListener("dragenter", function (e) {
		e.stopPropagation();
		e.preventDefault();
		dropbox.style.backgroundColor = "pink";
	}, false);
	window.addEventListener("mouseout", function (e) {
		e.stopPropagation();
		e.preventDefault();
		dropbox.style.backgroundColor = "";
	}, false);
	window.addEventListener("dragover", function (e) {
		e.stopPropagation();
		e.preventDefault();
	}, false);
	dropbox.addEventListener("drop", function (e) {
		e.stopPropagation();
		e.preventDefault();
		var dt = e.dataTransfer;
		var files = dt.files;
		//this.style.backgroundColor = "lime";

		handleFiles(files);
	}, false);
}