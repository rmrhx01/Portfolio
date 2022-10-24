const express = require('express');
const router = express.Router();
const ResultadosJuego = require('../model/resultadosJuego');
const auth = require('../middleware/auth')


//Envios de datos

router.post('/scores/agregar',auth, async (req,res) =>{
    const resultados = new ResultadosJuego(req.body);
    console.log(resultados)
    await resultados.save();
    res.json("New Score added");
    });


router.get('/scores/:idUsuario',auth, async (req,res) =>{
    var usuario = req.params.idUsuario;
        var resultados = await ResultadosJuego.find({idUsuario:usuario});
        res.json(resultados)
        });

router.get('/getAllscores/',auth, async (req,res) =>{
                var resultados = await ResultadosJuego.find({});
                res.json(resultados)
                });
        

module.exports = router;