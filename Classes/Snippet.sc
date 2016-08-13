Snippet {

	classvar snippetDict, func;
	classvar exists = false;


	*enable{

		if(exists == false){

			snippetDict = IdentityDictionary(128);
			snippetDict.addAll([

				\egen -> "EnvGen.kr(Env.adsr,\gate.kr,doneAction:2);",
				\pdef -> "Pdef(\\foo,Pbind(\n\t\\instrument,\\default,\n\t\\degree,Pseq([],inf),\n\t\\dur,Pseq([],inf)\n));",
				\px -> "p = ProxySpace(s).push;",
				\ndf -> "Ndef(\\foo)[1] = \\filter -> {|in|};",
				\ndef-> "Ndef(\\foo)[0] = {};",
				\ndefp -> "Ndef(\\foo).quant_(4).fadeTime_(1).play",
				\out -> "Out.ar(out,);",
				\oscdef -> "OSCdef(\\foo,{|msg| },'/');",
				\pseq -> "Pseq([],inf)",
				\pwhite -> "Pwhite([],inf)",
				\dynklank -> "DynKlank.ar(`[[],[],[]],in)",
				\case -> "case\n{}{}\n{}{}",
				\if -> "if(){\n\t\n};",
				\ifelse -> "if(){\n\t\n}{\n\t\n};",
				\syndef -> "SynthDef(\\foo,{|out=0,freq=440,pan=0,amp=1,gate=1| \n\t\n}).add;",
				\sout -> "s.options.outDevice = \"\";",
				\sin -> "s.options.inDevice = \"\";",
				\snout -> "s.options.numOutputBusChannels = ;",
				\snin -> "s.options.numInputBusChannels = ;",
				\tempo -> "(\nt = TempoClock.default;\nt.tempo = 1\n)",
				'//' -> "//////////////////////////////////////////////////////////////////////"
			]);




			func = {|doc, char, modifiers, unicode, keycode|

				if(unicode == 12 && modifiers.isCtrl, {
					var allSnippets, codeFragment = "";
					var currentPos, snippetSize, thisSnippet, snippetPos;

					allSnippets = snippetDict.keys;
					currentPos = doc.selectionStart;

					(-8..8).do{|j| codeFragment = codeFragment ++ doc.getChar(j+currentPos)};
					codeFragment = codeFragment.toLower;

					allSnippets.do{|key|
						var loc;
						loc = codeFragment.find(key.asString);

						if(loc.notNil){

							if(thisSnippet.isNil){
								thisSnippet = key;
								snippetPos = loc - 8;
							}{
								var isNewLine, isLonger;
								isNewLine = codeFragment[snippetPos+9].ascii == 10;
								isLonger = key.asString.size >= thisSnippet.asString.size;
								if(isNewLine || isLonger){
									thisSnippet = key;
									snippetPos = loc-8
								}
							}
						}

					};

					if(snippetPos.notNil){

						currentPos = currentPos + snippetPos;
						snippetSize = thisSnippet.asString.size;

						doc.selectRange(currentPos,snippetSize);
						doc.selectedString = snippetDict[thisSnippet];

					};
				})
			};

			Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(func);
			exists = true;
		}
	}


	*disable {
		if(exists == true){
			Document.globalKeyDownAction.removeFunc(func);
			exists = false;
		}
	}

	*keys {
		^snippetDict.keys;
	}


	*keysCodes {
		^snippetDict;
	}

	*isRunning {
		^exists;
	}


}






