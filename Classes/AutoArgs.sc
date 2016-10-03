/*

Auto arguments completion tool by Tsun Winston Yeung (w@yeungtw.com).

An auto completion tool for quick filling-in default and user-defined values for arguments.
Optimized for UGens, may work with other classes, but not recommanded.

*/
AutoArgs {

	classvar func, exists=false;

	*enable{

		if(exists == false){

			func = {|doc, char, modifiers, unicode, keycode|
				var expendFunc;

				if((unicode == 10)||(unicode == 11) && modifiers.isCtrl,
					{
						var currentPos, existingArgs, findCode;
						var ugen, method, methodEndPos;
						var start, end;

						currentPos = doc.selectionStart;

						findCode = {
							var pos, opCount=0, cpCount=0;
							var ugen_="", method_="";
							var existingArgsString = "", existingArgsAry, splitExistingArgsFunc;
							var thisChar, tempChar;
							var isAnotherParan=false, anotherParanClosed = false;

							pos = currentPos;

							//find args forward
							while({
								thisChar = doc.getChar(pos);
								switch(thisChar)
								{"("}{opCount = opCount + 1}
								{")"}{cpCount = cpCount + 1}
								{"\n"}{isAnotherParan = false}{thisChar = ")"; doc.selectRange(pos+1,0);doc.selectedString = ")"};
								(isAnotherParan || thisChar != ")");
							},{
								existingArgsString = existingArgsString ++ thisChar;
								if(anotherParanClosed){isAnotherParan = false};
								if(cpCount >= opCount){anotherParanClosed = true}{isAnotherParan = true};
								pos = pos + 1
							});


							//set variables
							opCount = 0;
							cpCount = 0;
							end = pos;
							pos = currentPos-1;
							existingArgsString = existingArgsString.reverse;
							anotherParanClosed = false;

							//find Args backward
							while({
								thisChar = doc.getChar(pos);
								switch(thisChar)
								{")"}{cpCount = cpCount + 1}
								{"("}{opCount = opCount + 1};
								(isAnotherParan || thisChar != "(");
							},{
								existingArgsString = existingArgsString ++ thisChar;
								if(anotherParanClosed){isAnotherParan = false};
								if(opCount >= cpCount){anotherParanClosed = true}{isAnotherParan = true};
								pos = pos - 1
							});

							//set variables
							start = pos+1;
							existingArgsString = existingArgsString.reverse;
							pos = pos -1;

							existingArgsAry = Array(12);

							//split arguments
							splitExistingArgsFunc = {
								var codeArray = Array(12);
								var string="", opCount=0, cpCount=0;
								var isAnotherParan=false, anotherParanClosed=false;

								existingArgsString.do{|char|
									switch(char)
									{$(}{opCount = opCount + 1}
									{$)}{cpCount = cpCount + 1};
									if(isAnotherParan || char != $,){
										string = string ++ char;
										if(anotherParanClosed){isAnotherParan = false};
										if(cpCount >= opCount){anotherParanClosed = true}{isAnotherParan = true}
									}{
										codeArray.add(string.replace(" ",""));
										string = "";
									}
								};

								if(string.notEmpty){codeArray.add(string.replace(" ",""))};
								codeArray;
							};

							existingArgsAry = splitExistingArgsFunc.value;

							//////////

							//find method
							while({
								thisChar = doc.getChar(pos);
								thisChar != "."
							},{
								method_ = method_++ thisChar;
								pos = pos - 1;
							});
							method_ = method_.reverse.asSymbol;
							pos = pos-1;

							//find UGen
							while({
								thisChar = doc.getChar(pos);
								tempChar = doc.getChar(pos+1);
								if(thisChar.ascii[0].notNil){
									thisChar[0].isAlphaNum || tempChar[0].isLower;
								}{
									false
								}
							},{
								ugen_ = ugen_ ++ thisChar;
								pos = pos - 1;
							});
							ugen_ = ugen_.reverse.interpret;


							[ugen_, method_, existingArgsAry];
						};



						/////////

						# ugen, method, existingArgs = findCode.value();

						////////

						if(ugen.notNil){
							var arguments, defaults, argString = "";
							var argDict;
							var isUGen=true;

							if(ugen.new.isUGen.not){"Target is not UGen".warn;isUGen=false};
							method = ugen.class.findRespondingMethodFor(method);
							arguments = method.argNames.drop(1);
							defaults = method.prototypeFrame.drop(1);


							if(existingArgs[0].notNil){
								existingArgs.do{|thisArg, i|
									if(thisArg.contains(":").not){
										defaults[i] = thisArg;
									}{
										var which, value, index;
										which = thisArg.copyFromStart(thisArg.find(":")-1).asSymbol;
										value = thisArg.copyToEnd(thisArg.find(":")+1);
										index = arguments.indexOf(which);

										if(index.notNil){
											defaults[index] = value
										}{
											arguments.add(which);
											defaults.add(value);
											"(%) is not a valid argument".format(which).error;
										}
									};
								};
							};

							arguments.do({|argName, i|
								var value;
								value = defaults[i];
								switch(unicode)
								{11}
								{
									if(isUGen){
										if(value.isString.not){
											argString = argString ++ "\\" ++ "%.kr(%), ".format(argName, value ? 0);
										}{
											argString = argString ++ "%, ".format(value ? 0);
										};
									}
								}
								{10}
								{argString = argString ++ "%, ".format(value ? 0)}
							});


							doc.selectRange(start,end-start);
							doc.selectedString = argString.drop(-2);


						}{
							"Target not found".error;
						}
				},)
			};


			Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(func);
			exists = true;
			"AutoArgs mode enabled".postln;
		}

	}

	*disable {
		if(exists == true){
			Document.globalKeyDownAction.removeFunc(func);
			exists=false
		}
	}

	*isRunning {
		^exists;
	}


}