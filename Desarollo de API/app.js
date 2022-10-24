const express = require('express');
const path = require('path');
const morgan = require('morgan');
const mongoose = require('mongoose');
require('dotenv').config();


const app = express();

var MongoDB  = process.env.MONGODB || 'mongodb://localhost/api-juego'

mongoose.connect(MongoDB,{
    useNewUrlParser:true,
    useUnifiedTopology:true
})
    .then(db => console.log('db connected'))
    .catch(err => console.log(err));

// importing routes
const indexRoutes = require('./routes/routeindex');

// settings
app.set('port', process.env.PORT || 3000);

// middlewares
app.use(morgan('dev'));
app.use(express.urlencoded({extended:false}));
app.use(express.json())

// routes
app.use('/', indexRoutes);

app.listen(app.get('port'), () =>{
    console.log(`server on port ${app.get('port')}`);
})


