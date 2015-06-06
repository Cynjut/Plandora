<?php
echo("<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
echo("<title>Plandora Project - Thank you</title>");
echo("<link rel='stylesheet' href='style.css' type='text/css' media='screen'>");
echo("</head><body style='background: rgb(170, 170, 170)'>");
echo("<div id='page'>");
echo("<div id='header'><div id='headering'><div><img class='alignleft' src='logo-pequeno.jpg'></div><div style='background: transparent url(degrade.jpg) repeat scroll 0%; margin-left: 220; height: 50px; width: 530'>&nbsp;</div></div></div>");
echo("<div id='pagebar'><li class='page_item'><a href='index.htm'>Home</a></li><li class='page_item'><a href='project.htm' title='Project'>Project</a></li>	<li class='page_item'><a href='screenshot.htm' title='Screenshots'>Screenshots</a></li>	<li class='page_item'><a href='contact.htm' title='Contact'>Contact</a></li></div>");
echo("<div id='grad' style='background: transparent url(topgrad.jpg) repeat scroll 0%; height: 65px; width: 100%'>&nbsp;</div>");
echo("<div id='contentfull'><div class='post'><p>&nbsp;</p><p>&nbsp;</p><center>");


	$message = $_REQUEST['msg'];
	$sender = $_REQUEST['snd'];
	$email = $_REQUEST['email'];
	$country = $_REQUEST['ct'];
	$hostname = $_SERVER['REMOTE_ADDR'];
	$t = date("Y-m-d H:i:s.u");
	$lang = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
	
	@ $arquivo = fopen("/var/www/vhosts/plandora.org/httpdocs/logs/log.txt", "a");

	if ($arquivo) {
		fwrite($arquivo, $t . ";" . $hostname . ";" . $lang . "\n" . "SENDER:" . $sender . "\n" . "EMAIL:" . $email . "\n" . "MSG:" . $message . "\n" . "CONTRY:" . $country . "\n\n"  );
		fclose($arquivo);
		echo("<h2>Thank you!</h2>");
	} else {
		echo("<h2>Sorry! We could't save the form information... :-( </h2>");
	}

echo("</center></div></div>");
echo("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p></div>");
echo("</body></html>");
?>