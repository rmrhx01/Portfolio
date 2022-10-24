var express = require("express");
var app = express()
var path = require("path");
var bodyParser = require('body-parser')
var configuracion = require('./configuracion')
var cookieParser = require('cookie-parser');
var axios = require("axios");
const verify = require('./middleware/verifyAccess.js')

var PORT = process.env.PORT || 3000


app.set('view engine', 'ejs')
var path = require('path');
app.use(express.static(path.join(__dirname, 'public')));

var main = require('./rutas/main')

app.use(express.urlencoded({ extended: true }))
app.use(express.json())
app.use(cookieParser())

app.use('/', main)


app.listen(PORT, function(){
	console.log('Node Countries Light running at port 3000: http://127.0.0.1:3000')
})
/*
var app = express();
var PORT = process.env.PORT || 4000;

app.use(express.static('public'))
app.use('/unity', express.static(__dirname + 'public/unity'))
app.use('/css', express.static(__dirname + 'public/css'))
app.use('/js', express.static(__dirname + 'public/js'))
app.use('/imgs', express.static(__dirname + 'public/imgs'))

    
app.get('', (req, res)=>{
    res.sendFile(__dirname + '/views/Loginpage.html')
})

app.get('/Perfil.html', (req, res)=>{
    res.sendFile(__dirname + '/views/Perfil.html')
})

app.get('/Registerpage.html', (req, res)=>{
    res.sendFile(__dirname + '/views/Registerpage.html')
})

app.get('/Tablaposiciones.html', (req, res)=>{
    res.sendFile(__dirname + '/views/Tablaposiciones.html')
})

app.get('/Loginpage.html', (req, res)=>{
    res.sendFile(__dirname + '/views/Loginpage.html')
})

app.get('/Examenes.html', (req, res)=>{
    res.sendFile(__dirname + '/views/Examenes.html')
})

app.get('/index.html', (req, res)=>{
    res.sendFile(__dirname + '/views/index.html')
})

app.listen(PORT, function() {
    console.log("App listening on PORT " + PORT);
});

*/