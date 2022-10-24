const { default: axios } = require('axios');
var express = require('express');
const configuracion = require('../configuracion');
const verifyToken = require('../middleware/verifyAccess');
var app = express()
/*
app.get('/Juego', function(req, res) {
	var url = req.protocol + '://' + req.get('host')
    
	// Obtenemos el usuario de la cookie y se lo mandamos a la pagina que muestra el juego
	var user = req.cookies.user || '';
	var token = req.cookies.token || '';
	var title = 'Unity Test';
    res.render('pages/index', {title: title,url,user,token});
 
});
*/
app.get('/',verifyToken, function(req, res) {
	const token = req.cookies.token;
	const user = req.cookies.user;
	const username = req.cookies.username;
	var title = 'Videojuegos';
    res.render('pages/Perfil', {
		title: title,
		token,
		user,
		username
    });

});

app.get('/examenes', verifyToken,function(req, res) {
	const token = req.cookies.token;
	const user = req.cookies.user;
	const username = req.cookies.username;
	var title = 'Login';
    res.render('pages/Examenes', {
		title: title,
		token,
		user,
		username
	});

});

app.get('/juego', verifyToken,function(req, res) {
	// render to views/index.ejs template file
	var title = 'Juego';
    res.render('pages/index', {
		title: title,
	});

});

app.get('/tablero', verifyToken,function(req, res) {
	const token = req.cookies.token;
	const user = req.cookies.user;
	const username = req.cookies.username;
	var title = 'Juego';
    res.render('pages/Tablaposiciones', {
		title: title,
		token,
		user,
		username
	});

});

app.get('/logout', function(req, res) {
		res.cookie("token","",{httpOnly:true,maxAge:-1})
		res.cookie("user","",{httpOnly:true,maxAge:-1})
		res.cookie("username","",{httpOnly:true,maxAge:-1})
	res.redirect("/");
});

app.get('/login', function(req, res) {
	// render to views/index.ejs template file
	var title = 'Login';
    res.render('pages/Loginpage', {title: title});

});

app.post('/login', async (req, res) => {

	try {
	var user = {"username": req.body.user, "password": req.body.password};
	var login_response = await axios.post(configuracion.API.loginurl,user);
	var user_details = await axios.get(configuracion.API.userurl+login_response.data.user);
	var arreglo = user_details.data.usertype;   
	// Converting array into a string separated by coma. 
	var usertype= arreglo.join(); 

		res.cookie("token",login_response.data.token,{httpOnly:true})
		res.cookie("user",login_response.data.user,{httpOnly:true})
		res.cookie("userType", usertype,{httpOnly:true})
		res.cookie("username", user_details.data.username, {httpOnly:true});
		
	} catch (error){
		console.log(error)
	}	
		return res.redirect('/');
});

app.get('/registrar', function(req, res) {
	// render to views/index.ejs template file
	var title = 'registrar';
    res.render('pages/Registerpage', {title: title});

});

app.post('/registrar', async(req, res) =>{

})

module.exports = app;