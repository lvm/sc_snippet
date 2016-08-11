Snippet {
	classvar global;

	*new{
		Document.globalKeyDownAction = {|doc, char, modifiers, unicode, keycode|

			if(unicode == 12 && modifiers.isCtrl, {
				var allSnippets, currentPos, codeFragment, snippetSize, thisSnippet, snippetPos;
				allSnippets = global.keys;
				currentPos = doc.selectionStart;
				codeFragment = "";
				(-8..8).do{|j| codeFragment = codeFragment ++ doc.getChar(j+currentPos)};
				codeFragment = codeFragment.toLower;

				allSnippets.do{|key|
					var loc;
					loc = codeFragment.find(key.asString);
					if(loc.notNil){
						if(thisSnippet.notNil){
							if(key.asString.size > thisSnippet.asString.size){
								thisSnippet = key;
								snippetPos = loc-8
							}
						}{
							thisSnippet = key;
							snippetPos = loc-8;
						}
				}};
				if(snippetPos.notNil){

					currentPos = currentPos + snippetPos;
					snippetSize = thisSnippet.asString.size;
					{
					doc.selectRange(currentPos,snippetSize);
						doc.selectedString = global[thisSnippet]}.defer(0.05);
				};

			});
		}
	}

	*global{
		^global
	}

	*initClass{
		global = Dictionary(128);

		global.addAll([


			\egen -> "EnvGen.kr(Env.adsr,\gate.kr,doneAction:2);",
			\pdef -> "Pdef(\\foo,Pbind(\n\t\\instrument,\\default,\n\t\\degree,Pseq([],inf),\n\t\\dur,Pseq([],inf)\n));",
			\ndf -> "Ndef(\\foo)[1] = \\filter -> {|in|};",
			\ndef-> "Ndef(\\foo)[0] = {};",
			\ndefp -> "Ndef(\\foo).quant_(4).fadeTime_(0.1).play",
			\out -> "Out.ar(out,);",
			\oscdef -> "OSCdef(\\foo,{|msg| },'/');",
			\pseq -> "Pseq([],inf)",
			\pwhite -> "Pwhite([],inf)",
			\dynklank -> "DynKlank.ar(`[[],[],[]],in)",
			\if -> "if(){\n\t\n};",
			\ifelse -> "if(){\n\t\n}{\n\t\n};",
			\syndef -> "SynthDef(\\foo,{|out=0,freq=440,pan=0,amp=1,gate=1| \n\t\n}).add;",
			\sout -> "s.options.outDevice = \"\";",
			\sin -> "s.options.inDevice = \"\";",
			\snout -> "s.options.numOutputBusChannels = ;",
			\snin -> "s.options.numInputBusChannels = ;",
			\tempo -> "(\nt = TempoClock.default;\nt.tempo = 1\n)"

		]);
	}

	*list{
		^global.keys;
	}

}


+ Symbol {
	s {
		var size, start, str, end;
		str = this.asString;
		size = str.size;
		start = Document.current.string.find(str)-1;
		end = if(Document.current.getChar(start+size) == $'){4}{3};
		Document.current.selectRange(start,size+end);
		Snippet.global[this] !? {Document.current.selectedString = Snippet.global[this]};
	}

}




