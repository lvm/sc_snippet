# sc_snippet

A SuperCollider snippet tool.  

- copy the whole folder to the SuperCollider Extensions folder (Platform.userExtensionDir);  
- create a new instance of Snippet to set the global key down function;  
- use Snippet.list or Snippet.global to get see all snippets;  
- type the key of the snippet;  
- press ctrl+l  


## Example:
Snippet(); //run this first;  
sout -> "s.options.outDevice = "  
ndef -> "Ndef(\foo)[0] = {}"  
