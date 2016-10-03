# sc_snippet

A SuperCollider snippet tool.  

- copy the whole folder to the SuperCollider Extensions folder (Platform.userExtensionDir);  
- create a new instance of Snippet to set the global key down function;  
- use Snippet.keys or Snippet.keysCodes to get see all snippets;  
- type the key of the snippet;  
- press ctrl+l;     
- press ctrl+] to jump to default values (marked with -- -- in source code)


## Example:
Snippet.enable; //run this first;  

1.
ndf //press ctrl+l will get the code below;

Ndef('foo',{ })

//the symbol 'foo' is  automatically highlighted, press ctrl+] to jump to the next default value (which is the space within the curly brackets)

2.
pdf  //press ctrl+l will get

Pdef('foo',Pbind(  
'instrument','default',  
'degree',Pseq([0,1,2,3],inf),  
'dur',Pseq([1],inf)  
));


//press ctrl+] to jump to 'default',  Pseq([0,1,2,3],inf) and Pseq([1],inf);


//

