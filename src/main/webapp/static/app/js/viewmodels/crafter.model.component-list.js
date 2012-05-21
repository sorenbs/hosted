Crafter.model.ComponentList = function () {

	function createCodeMirror(x) {
		var cm = CodeMirror.fromTextArea(x,
		{
			mode: 'javascript',
			theme: 'ambiance',
			lineNumbers: true,
			indentUnit: 4,
			indentWithTabs: true,
			extraKeys: { "Ctrl-Space": "autocomplete" },
			lineWrapping: true,
			onChange: function () { cm.save(); console.log(cm.getTextArea().value); }

		});

		x.value = "444";

		cm.getWrapperElement().onmouseover = function (e) {
			this.style["-webkit-transform"] = "scale(1, 1)";
			this.style["margin"] = "0px 3px";
		}
		cm.getWrapperElement().onmouseout = function (e) {
			this.style["-webkit-transform"] = "scale(.2, .2)";
			this.style["margin"] = "-300px -197px";
		}
	};

	var self = this;
	self.components = ko.observableArray([new Crafter.model.Component("aaa   lll  fsgfsg fsgfg \n df aaa   lll  fsgfsg fsgfg \n dfaaa   lll  fsgfsg fsgfg \n dfaaa   lll  fsgfsg fsgfg \n df"), new Crafter.model.Component("bbb"), new Crafter.model.Component("ccc")])

	self.renderCodeMirrors = function (e) {
		if (!e)
			return;
		var textarea = e[1];
		createCodeMirror(textarea);
	}

	//self.components.subscribe(function (elem) { self.renderCodeMirrors(); });
	self.components.push(new Crafter.model.Component("dgfdfdf"));
}