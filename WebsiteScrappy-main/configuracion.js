var configuracion = {
    server: {
		host:'127.0.0.1', // the host for the server
		port: '3000' // the port for the server
	} ,
	 API: {
		 loginurl: "https://chatarrap-api.herokuapp.com/users/login",
		 userurl: "https://chatarrap-api.herokuapp.com/users/",
		 imagesUrl: "https://chatarrap-api.herokuapp.com/images",
		 resultadosJuegoURL: "https://calm-bastion-98916.herokuapp.com/scores/",
		 agregarURL: "https://chatarrap-api.herokuapp.com/users/add",
		 examenesUsuarioURL: "https://chatarrap-api.herokuapp.com/exams/available/TecEquipo10"
	 }

}
module.exports = configuracion