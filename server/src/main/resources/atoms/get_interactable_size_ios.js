function(){return function(){var f=this;var g=window;function k(a,c){this.code=a;this.state=l[a]||m;this.message=c||"";var b=this.state.replace(/((?:^|\s+)[a-z])/g,function(a){return a.toUpperCase().replace(/^[\s\xa0]+/g,"")}),h=b.length-5;if(0>h||b.indexOf("Error",h)!=h)b+="Error";this.name=b;b=Error(this.message);b.name=this.name;this.stack=b.stack||""}(function(){var a=Error;function c(){}c.prototype=a.prototype;k.a=a.prototype;k.prototype=new c})();
var m="unknown error",l={15:"element not selectable",11:"element not visible",31:"ime engine activation failed",30:"ime not available",24:"invalid cookie domain",29:"invalid element coordinates",12:"invalid element state",32:"invalid selector",51:"invalid selector",52:"invalid selector",17:"javascript error",405:"unsupported operation",34:"move target out of bounds",27:"no such alert",7:"no such element",8:"no such frame",23:"no such window",28:"script timeout",33:"session not created",10:"stale element reference",
0:"success",21:"timeout",25:"unable to set cookie",26:"unexpected alert open"};l[13]=m;l[9]="unknown command";k.prototype.toString=function(){return this.name+": "+this.message};function n(a,c){for(var b=0,h=String(a).replace(/^[\s\xa0]+|[\s\xa0]+$/g,"").split("."),F=String(c).replace(/^[\s\xa0]+|[\s\xa0]+$/g,"").split("."),O=Math.max(h.length,F.length),r=0;0==b&&r<O;r++){var P=h[r]||"",Q=F[r]||"",R=RegExp("(\\d*)(\\D*)","g"),S=RegExp("(\\d*)(\\D*)","g");do{var d=R.exec(P)||["","",""],e=S.exec(Q)||["","",""];if(0==d[0].length&&0==e[0].length)break;b=((0==d[1].length?0:parseInt(d[1],10))<(0==e[1].length?0:parseInt(e[1],10))?-1:(0==d[1].length?0:parseInt(d[1],10))>(0==e[1].length?
0:parseInt(e[1],10))?1:0)||((0==d[2].length)<(0==e[2].length)?-1:(0==d[2].length)>(0==e[2].length)?1:0)||(d[2]<e[2]?-1:d[2]>e[2]?1:0)}while(0==b)}return b};function p(){return f.navigator?f.navigator.userAgent:null}var q,s="",t=/WebKit\/(\S+)/.exec(p());q=s=t?t[1]:"";var u={};var v,w,x,y,z,A,B;B=A=z=y=x=w=v=!1;var C=p();C&&(-1!=C.indexOf("Firefox")?v=!0:-1!=C.indexOf("Camino")?w=!0:-1!=C.indexOf("iPhone")||-1!=C.indexOf("iPod")?x=!0:-1!=C.indexOf("iPad")?y=!0:-1!=C.indexOf("Android")?z=!0:-1!=C.indexOf("Chrome")?A=!0:-1!=C.indexOf("Safari")&&(B=!0));var D=v,E=w,G=x,H=y,I=z,J=A,K=B;function L(a){return(a=a.exec(p()))?a[1]:""}var M=function(){if(D)return L(/Firefox\/([0-9.]+)/);if(J)return L(/Chrome\/([0-9.]+)/);if(K)return L(/Version\/([0-9.]+)/);if(G||H){var a=/Version\/(\S+).*Mobile\/(\S+)/.exec(p());if(a)return a[1]+"."+a[2]}else{if(I)return(a=L(/Android\s+([0-9.]+)/))?a:L(/Version\/([0-9.]+)/);if(E)return L(/Camino\/([0-9.]+)/)}return""}();function N(a){I?n(T,a):n(M,a)}var U;if(I){var V=/Android\s+([0-9\.]+)/.exec(p());U=V?V[1]:"0"}else U="0";var T=U;I&&N(2.3);I&&N(4);K&&N(6);function W(a,c){this.width=a;this.height=c}W.prototype.toString=function(){return"("+this.width+" x "+this.height+")"};I&&N(4);u["533"]||(u["533"]=0<=n(q,"533"));function X(a){var c=(a||g).document;a=c.documentElement;var b=c.body;if(!b)throw new k(13,"No BODY element present");c=[a.clientHeight,a.scrollHeight,a.offsetHeight,b.scrollHeight,b.offsetHeight];a=Math.max.apply(null,[a.clientWidth,a.scrollWidth,a.offsetWidth,b.scrollWidth,b.offsetWidth]);c=Math.max.apply(null,c);return new W(a,c)}var Y=["_"],Z=f;Y[0]in Z||!Z.execScript||Z.execScript("var "+Y[0]);for(var $;Y.length&&($=Y.shift());)Y.length||void 0===X?Z=Z[$]?Z[$]:Z[$]={}:Z[$]=X;; return this._.apply(null,arguments);}.apply({navigator:typeof window!=undefined?window.navigator:null,document:typeof window!=undefined?window.document:null}, arguments);}
