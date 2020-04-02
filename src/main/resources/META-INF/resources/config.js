var request = new XMLHttpRequest();
request.open('GET', '/config', false);  // `false` makes the request synchronous
request.send(null);

if (request.status !== 200) {
    alert("Configuration cannot be read from the server.")
    throw new Error("Could not read config file /config");
}

window._com_zemiak_movies_config = request.responseText;
