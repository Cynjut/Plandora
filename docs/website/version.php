<?php
	$cv = $_REQUEST['cv'];
	$lg = $_REQUEST['lg'];
	$t = date("Y-m-d H:i:s.u");

	$ip;
	if (getenv("HTTP_CLIENT_IP")) {
		$ip = getenv("HTTP_CLIENT_IP");
	} else if(getenv("HTTP_X_FORWARDED_FOR")) {
		$ip = getenv("HTTP_X_FORWARDED_FOR");
	} else if(getenv("REMOTE_ADDR")) {
		$ip = getenv("REMOTE_ADDR");
	} else {
		$ip = "UNKNOWN";
	}

	@ $arquivo = fopen("/var/www/vhosts/plandora.org/httpdocs/logs/version.txt", "a");

	if ($arquivo) {
		fwrite($arquivo, $t . ";" . $ip . ";" . $cv . ";" . $lg . "\n");
		fclose($arquivo);
		echo("v1.12.0");
	} else {
		echo("Sorry! We could't get current version... :-(");
	}
?>