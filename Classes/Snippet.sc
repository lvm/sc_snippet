Snippet {

	classvar snippetDict, func, changeDefaultsFunc;
	classvar exists = false;
	classvar tempDocSize = 0;

	*enable{

		if(exists.not){

			var snips;

			snippetDict = IdentityDictionary(128);
			snippetDict.addAll([

				//SynthDefs

				\syndf ->
				"SynthDef('--foo--',{|out=0,freq=440,pan=0,amp=1,gate=1| \n\t-- --\n\tOut.ar(--out--, -- --)\n}).add;",

				\syn ->
				"--x-- = Synth('--foo--')",

				//Patterns
				\pdf ->
				"Pdef('--foo--',Pbind(\n\t'instrument','--default--',\n\t'scale',Scale.--major--,\n\t'degree',--Pseq([0,1,2,3],inf)--,\n\t'amp',--Pseq([0.1],inf)--,\n\t'dur',--Pseq([1],inf)--\n));",

				\pdfp ->
				"Pdef('--foo--').play",

				\pdfq ->
				"Pdef('--foo--').quant_(--4--)",

				\pdfs ->
				"Pdef('--foo--').stop",

				\pbdf ->
				"Pbindef('--foo--','-- --', -- --)",

				\pseq ->
				"Pseq([--0, 1, 2, 3--],inf)",

				\pwhite ->
				"Pwhite(--0, 1--,inf)",

				\prand ->
				"Prand([--0,1--],inf)",

				//Ndef
				\ndf ->
				"Ndef('--foo--',{-- --})",

				\ndfp ->
				"Ndef('--foo--').play",

				\ndfq ->
				"Ndef('--foo--').quant_(--4--)",

				\ndfs ->
				"Ndef('--foo--').stop",

				\ndf0->
				"Ndef('--foo--')[0] = {-- --};",

				\ndff ->
				"Ndef('--foo--')[--1--] = '--filter--' -> {|in| -- --};",


				//ProxySpace
				\px ->
				"p = ProxySpace(--s.boot--).push;",


				//control structures
				\if ->
				"if(-- --, {\n-- --\n});",

				\ife ->
				"if(-- --, {\n-- --\n},{\n-- --\n});",

				\while ->
				"while({-- --}, {\n-- --\n});",

				\for ->
				"for(-- --, -- --, {\n-- --\n});",

				\case ->
				"case\n{-- --}{-- --}\n{-- --}{-- --};",

				\switch ->
				"switch(-- --,\n-- --,{-- --},\n-- --,{-- --})",


				//other

				\egen ->
				"EnvGen.kr(--Env.adsr--, gate, doneAction:--2--);\n",

				\out ->
				"Out.ar(--out--, -- --);",

				\oscdf ->
				"OSCdef('--foo--',{|msg| -- --},'--/path--');",

				\tdf ->
				"Tdef('--foo--',{-- --})",

				\tempo ->
				"(\n--t-- = TempoClock.default;\n--t--.tempo = --1--\n)",

				'//' ->
				"//////////////////////////////////////////////////////////////////////"

			]);




			/////////////


			func = {|doc, char, modifiers, unicode, keycode|

				if(unicode == 12 && modifiers.isCtrl, {
					var allSnippets, codeFragment = "";
					var currentPos, snippetSize, thisSnippet, snippetPos, findCode;
					var start, end;

					allSnippets = snippetDict.keys;
					currentPos = doc.selectionStart;

					findCode = {
						var pos, thisChar, code="";
						var lim=7, index=0;

						pos = currentPos;
						while({
							thisChar = doc.getChar(pos);
							((thisChar.ascii[0] == nil) || (thisChar.ascii[0] == 10) || (thisChar.ascii[0] == 32) || (index > lim)).not
						},{
							code = code ++ thisChar;
							pos = pos + 1;
							index = index + 1;
						});

						end = pos - 1;
						code = code.reverse;
						pos = currentPos - 1;
						index = 0;


						while({
							thisChar = doc.getChar(pos);
							((thisChar.ascii[0] == nil) || (thisChar.ascii[0] == 10) || (thisChar.ascii[0] == 32) || (index > lim)).not
						},{
							code = code ++ thisChar;
							pos = pos - 1;
							index = index + 1;
						});

						start = pos + 1;

						code = code.reverse;

					};

					codeFragment = findCode.value.toLower;

					allSnippets.do{|key|
						var loc;
						loc = codeFragment.find(key.asString);
						if(loc.notNil){
							if(thisSnippet.isNil){
								thisSnippet = key;
								snippetPos = loc;
							}{
								if(key.asString.size >= thisSnippet.asString.size){
									thisSnippet = key;
									snippetPos = loc;
								}
							}
						}
					};

					if(snippetPos.notNil){

						var thisCode, removedDashesCode, jumpTo=10;

						thisCode = snippetDict[thisSnippet];
						removedDashesCode = thisCode.replace("--", "");

						currentPos = start + snippetPos;
						snippetSize = thisSnippet.asString.size;
						doc.selectRange(currentPos,snippetSize);
						doc.selectedString = removedDashesCode;

						if(thisCode.contains("--"),{
							var poss;
							poss = thisCode.findAll("--").clump(2);
							poss = poss.collect{|j,i|
								var beginPos, endPos, length;
								beginPos = j[0]-(i*4);
								endPos = beginPos + (j[1]-j[0]) - 2;
								length = endPos-beginPos;
								[beginPos + currentPos, length];
							};

							tempDocSize = doc.string.size;
							doc.selectRange(poss[0][0], poss[0][1]);
							changeDefaultsFunc.value(currentPos, removedDashesCode.size + currentPos - 1, poss);
						});





					};
				});

				//code shortcuts
				if(modifiers.isCmd){
					switch(keycode)
					{18}
					{doc.selectedString = snippetDict[\px]}
					{19}
					{doc.selectedString = (snippetDict[\sin] ++ "\n" ++ snippetDict[\sout] ++ "\n" ++ snippetDict[\snout])}
				};
			};


			changeDefaultsFunc = {
				arg snipStart, snipEnd, defaultsPos;
				var keyJumpFunc, mouseJumpFunc;
				mouseJumpFunc = {|doc|
					var pos = doc.selectionStart;

					if((pos < snipStart) || (pos > snipEnd),{

						Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
						doc.mouseDownAction = doc.mouseDownAction.removeFunc(mouseJumpFunc);
					})
				};
				keyJumpFunc = {|doc, char, modifiers, unicode, keycode|
					var pos = doc.selectionStart;
					if((pos >= snipStart) && (pos <= snipEnd),{
						if((keycode == 30)||(keycode == 33)&&modifiers.isCtrl,{
							var targetPosIndex, targetPos, docSizeDelta;
							targetPosIndex = defaultsPos.detectIndex{|j| j[0] > pos};

							if(targetPosIndex.isNil,{
								Document.current.mouseDownAction = Document.current.mouseDownAction.removeFunc(mouseJumpFunc);
								Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
							},{

								docSizeDelta = doc.string.size-tempDocSize;
								tempDocSize = doc.string.size;
								defaultsPos.do{|j, i| if(i!=0,{j[0] = j[0] + docSizeDelta})};
								snipEnd = snipEnd + docSizeDelta;
								targetPos = switch(keycode,
									30,{defaultsPos.at(targetPosIndex)},
									33,{defaultsPos.clipAt(targetPosIndex-1)}
								);
								doc.selectRange(targetPos[0],targetPos[1])
							});

						})
					},{
						Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
					})
				};
				Document.current.mouseDownAction = Document.current.mouseDownAction.addFunc(mouseJumpFunc);
				Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(keyJumpFunc);
			};


			Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(func);
			exists = true;
			"Snippet mode enabled".postln;

		};


	}


	*disable {
		if(exists == true){
			Document.globalKeyDownAction.removeFunc(func);
			exists = false;
		}
	}



	*keys {
		snippetDict.keys.do{|j| j.postln};
		^snippetDict.keys;
	}


	*keysCodes {
		^snippetDict;
	}

	*isRunning {
		^exists;
	}


}





/////

//
//
// (//strip
// f = {|string|
// 	var startPos, defaultAry, defaultVal;
// 	startPos = string.findAll("$$");
// 	defaultAry = Array.new(12);
// 	startPos.do{|i|
// 		var pos=i, str="";
// 		while({
// 			a[pos-1] != $}
// 			},{
// 				pos = pos + 1;
// 				str = str ++ a[pos-1]
// 		});
// 		defaultAry.add(str);
// 	};
// 	defaultVal = defaultAry.collect{|defaultStr| defaultStr.copyToEnd(5).drop(-1)};
// 	[startPos, defaultAry, defaultVal,  defaultAry.collect{|j| j.size}].postln;
// 	defaultAry.do{|str,i| string = string.replace(str,defaultVal[i])};
// 	string.postln;
// }
// )
//
