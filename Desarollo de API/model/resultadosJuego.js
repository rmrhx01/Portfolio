const {Schema, model } = require("mongoose");

const ResultadosSchema  = Schema ({
    idUsuario: Number,
    nivel: String,
    score: Number,
    fecha: {
        type: Date,
        default: Date.now
    }
});

module.exports = model('resultadosJuego',ResultadosSchema);
