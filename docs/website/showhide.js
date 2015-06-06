/*  -*-mode: Java; coding: latin-1;-*- Time-stamp: "2005-05-30 20:03:19 ADT"

 "Show/hide functions" by Sean M. Burke, sburke@cpan.org, 2005

 You can use, modify, and redistribute this only under the terms of the
 Perl Artistic License: 
  http://www.perl.com/pub/a/language/misc/Artistic.html

*/

function toggle (switcher) { // followed by list of IDs of elements to toggle

  if(! document.getElementById)
    throw complainting("Your browser's JavaScript system is too old.  ",
       "Upgrade at getfirefox.com !");

  for(var i = 1; i < arguments.length; i++) {
    var element
    var el = document.getElementById( arguments[i] );
    if(!el) throw complaining("Failed to find element with id='"
     + arguments[i] + "' in " + document.location );

    var behidden;
    if(   (el.style.display || 'block') == 'block')
         { el.style.display =  'none' ; behidden = true ;  }
    else { el.style.display =  'block'; behidden = false;  }
  }

  if(switcher) {
    status_switcher_classchange(switcher, behidden);
    //if( switcher.childNodes[0].nodeType == document.TEXT_NODE)
    // switcher.childNodes[0].data = behidden ? "+" : "-";
  }

  return false;
}

function status_switcher_classchange (targetnode, behidden) {
  var class_to_add = behidden ? "toggleWillShow" : "toggleWillHide";

  var c = targetnode.className || '';
  var c_new =  c.replace( /\btoggleWill(Show|Hide)\b/, class_to_add );
  if( c_new == c)  { c_new = c + " " + class_to_add }
  targetnode.className = c_new;
  return;
}

function complaining () {
  var _ = [];
  for(var i = 0; i < arguments.length; i++) { _.push(arguments[i]) }
  _ = _.join("");
  if(! _.length) out = "Unknown error!?!";
  void alert(_);
  return new Error(_,_);
}


//End
