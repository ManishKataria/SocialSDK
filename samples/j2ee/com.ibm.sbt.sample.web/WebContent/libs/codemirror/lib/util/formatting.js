/*  (c) Copyright IBM Corp. 2010 - Licensed under the Apache License, Version 2.0 */

(function(){CodeMirror.extendMode("css",{commentStart:"/*",commentEnd:"*/",newlineAfterToken:function(_1,_2){return /^[;{}]$/.test(_2);}});CodeMirror.extendMode("javascript",{commentStart:"/*",commentEnd:"*/",newlineAfterToken:function(_3,_4,_5,_6){if(this.jsonMode){return /^[\[,{]$/.test(_4)||/^}/.test(_5);}else{if(_4==";"&&_6.lexical&&_6.lexical.type==")"){return false;}return /^[;{}]$/.test(_4)&&!/^;/.test(_5);}}});CodeMirror.extendMode("xml",{commentStart:"<!--",commentEnd:"-->",newlineAfterToken:function(_7,_8,_9){return _7=="tag"&&/>$/.test(_8)||/^</.test(_9);}});CodeMirror.defineExtension("commentRange",function(_a,_b,to){var cm=this,_c=CodeMirror.innerMode(cm.getMode(),cm.getTokenAt(_b).state).mode;cm.operation(function(){if(_a){cm.replaceRange(_c.commentEnd,to);cm.replaceRange(_c.commentStart,_b);if(_b.line==to.line&&_b.ch==to.ch){cm.setCursor(_b.line,_b.ch+_c.commentStart.length);}}else{var _d=cm.getRange(_b,to);var _e=_d.indexOf(_c.commentStart);var _f=_d.lastIndexOf(_c.commentEnd);if(_e>-1&&_f>-1&&_f>_e){_d=_d.substr(0,_e)+_d.substring(_e+_c.commentStart.length,_f)+_d.substr(_f+_c.commentEnd.length);}cm.replaceRange(_d,_b,to);}});});CodeMirror.defineExtension("autoIndentRange",function(_10,to){var _11=this;this.operation(function(){for(var i=_10.line;i<=to.line;i++){_11.indentLine(i,"smart");}});});CodeMirror.defineExtension("autoFormatRange",function(_12,to){var cm=this;var _13=cm.getMode(),_14=cm.getRange(_12,to).split("\n");var _15=CodeMirror.copyState(_13,cm.getTokenAt(_12).state);var _16=cm.getOption("tabSize");var out="",_17=0,_18=_12.ch==0;function _19(){out+="\n";_18=true;++_17;};for(var i=0;i<_14.length;++i){var _1a=new CodeMirror.StringStream(_14[i],_16);while(!_1a.eol()){var _1b=CodeMirror.innerMode(_13,_15);var _1c=_13.token(_1a,_15),cur=_1a.current();_1a.start=_1a.pos;if(!_18||/\S/.test(cur)){out+=cur;_18=false;}if(!_18&&_1b.mode.newlineAfterToken&&_1b.mode.newlineAfterToken(_1c,cur,_1a.string.slice(_1a.pos)||_14[i+1]||"",_1b.state)){_19();}}if(!_1a.pos&&_13.blankLine){_13.blankLine(_15);}if(!_18){_19();}}cm.operation(function(){cm.replaceRange(out,_12,to);for(var cur=_12.line+1,end=_12.line+_17;cur<=end;++cur){cm.indentLine(cur,"smart");}cm.setSelection(_12,cm.getCursor(false));});});})();