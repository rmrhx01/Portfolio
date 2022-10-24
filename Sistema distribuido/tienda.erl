%% Autores - 
%% @ Eber Jesus Aguilera A00829692
%% @ Roberto Rodriguez A00829553
%% @ Rodrigo de la garza a00825271

-module(tienda).
-export([abre_tienda/0,
         proceso_tienda/5,
         cierra_tienda/0,
         producto/2,
         pedidoProceso/3]).

%%Proceso para almacenar la cantidad de cada producto, se registran con el 
%%nombre del producto para poder mandar mensajes sin tener el PID
producto(Producto,Cantidad)->
    receive
        {modifica, N} ->
            %%Hace el cambio de la cantidad espeficicada y vuelve a correr el proceso
            Ncantidad = N + Cantidad,
            io:format("Proceso Producto ~s: Cambio de cantidad de ~w a ~w~n", [Producto,Cantidad,Ncantidad]),
            producto(Producto,Ncantidad);
        {pedir,N,Pid}->
            %%Proceso el pedido del usuario, si hay suficientes existencias, se aparta la cantidad
            %%Si no hay suficientes existencias, se aparta todo lo disponible
            io:format("Proceso Producto ~s: Pedido de ~w~n",[Producto,N]),
            if
                (N =< Cantidad)->
                    Pid ! {pedido,N},
                    producto(Producto,Cantidad-N);
                (true)->
                    Pid ! {pedido,Cantidad},
                    producto(Producto,0)
            end;
        %%Cierrar el proceso del producto
        {cerrar}-> io:format("Proceso Producto ~s: Proceso terminado~n", [Producto]);
        %%Procesa errores
        Error1 -> io:format("Error: ~w~n",[Error1])
    end.

%%Se hace el register de el proceso 'tienda'
abre_tienda()-> 
    register(tienda,spawn(tienda,proceso_tienda,[[],[],[],[],0])),
    io:format("La tienda se ha abierto correctamente~n").

%%Este es el proceso principal de tienda en el cual se procesan todas las solicitudes
proceso_tienda(Socios,Productos,PedidosA,PedidosP,N)->
    receive
        %%Checa si el usuario esta dentro de la lista de Socios y si no se encuentra, se agrega
        {registrar, Nombre, Pid} -> 
            io:format("Proceso Tienda : Solicitud de ~s de registro~n",[Nombre]),
            Comparacion = lists:member(Nombre,Socios),
            if 
                (Comparacion) -> 
                    Pid ! {existente},
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP,N);
                (true) -> 
                    Pid ! {confirmado},
                    proceso_tienda(Socios ++ [Nombre],Productos,PedidosA,PedidosP,N)
            end;
        %%Checa si el usuario esta dentro de la lista de Socios y si se encuentra, se elimina
        {eliminar, Nombre, Pid} ->
            io:format("Proceso Tienda : Solicitud de ~s de cancelacion~n",[Nombre]),
            Comparacion = lists:member(Nombre,Socios),
            if 
                (Comparacion) ->
                    Pid ! {confirmado},
                    proceso_tienda(lists:delete(Nombre,Socios),Productos,PedidosA,PedidosP,N); 
                (true) -> 
                    Pid ! {no_existente},
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP,N)
            end;
        %%Checa si el producto ya existe, si no, se registra el proceso y se agrega a la lista
        {registrar_producto, Producto, Cantidad, Pid} ->
            io:format("Proceso Tienda : Solicitud de registro de ~s~n",[Producto]),
            Comparacion = lists:member(Producto,Productos),
            if 
                (Comparacion) ->
                    Pid ! {existente},
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP,N);
                (true) ->
                    Pid ! {confirmado},
                    register(Producto,spawn(act62,producto,[Producto,Cantidad])),
                    proceso_tienda(Socios,Productos ++ [Producto],PedidosA,PedidosP,N)
            end;
        %%Checa si el producto ya existe, en caso de que si, cierra el proceso y lo elimina de la lista
        {eliminar_producto, Producto, Pid} ->
            io:format("Proceso Tienda : Solicitud de ~s de cancelacion~n",[Producto]),
            Comparacion = lists:member(Producto,Productos),
            if 
                (Comparacion) ->
                    Pid ! {confirmado},
                    Producto ! {cerrar},
                    proceso_tienda(Socios,lists:delete(Producto,Productos),PedidosA,PedidosP,N); 
                (true) -> 
                    Pid ! {no_existente},
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP,N)
            end;
        %% Manda mensaje al producto para modificar por la cantidad pedida por el admin
        {modifica, Producto, Cantidad, Pid}->
            Producto ! {modifica, Cantidad},
            Pid ! {confirmado},
            proceso_tienda(Socios,Productos,PedidosA,PedidosP,N);
        %% Inicializa el pedido de los productos que desea el usuario
        {crear_pedido, Socio, Lista, Pid}->
            io:format("Proceso Tienda : Solicitud de pedido de ~s~n",[Socio]),
            %%La comparacion checa que el socio exista en la lista
            Comparacion = lists:member(Socio,Socios),
            if 
                (Comparacion)->
                    io:format("comparacion completada~n"),
                    %%Funcion para actualizar la lista de los pedidos con base a las existencias actuales
                    Lista2 = lists:foldl(fun checar_existencia/2,[],Lista),
                    Pid ! {pedidoAjustado, Lista2,N + 1},
                    io:format("mensaje mandado~n"),
                    %%Se crea el proceso del pedido para esperar
                    Pedido = spawn(tienda, pedidoProceso, [Socio,Lista2,N + 1]),
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP ++ [Pedido],N + 1);
                (true)->
                    Pid ! {socio_no_existente},
                    proceso_tienda(Socios,Productos,PedidosA,PedidosP,N)
            end;
        %%Mensaje para aceptar el pedido de un socio
        {aceptar_pedido,Socio,Pedido}-> 
            %%Se le mandan mensajes a todos los pedidos pendientes para que el correspondiente responda
            lists:foreach(fun(Producto) -> Producto ! {acepta,Socio,Pedido} end, PedidosP),
            proceso_tienda(Socios,Productos,PedidosA,PedidosP,N);
        %%Mensaje para rechazar el pedido de un socio
        {rechazar_pedido,Socio,Pedido}-> 
            %%Se le mandan mensajes a todos los pedidos pendientes para que el correspondiente responda
            lists:foreach(fun(Producto) -> Producto ! {deniega,Socio,Pedido} end, PedidosP),
            proceso_tienda(Socios,Productos,PedidosA,PedidosP,N);
        %%El pedido manda mensaje para procesar el pedido
        {procesaPedidoAceptado, Pid, Socio}-> 
            %%Se aumentan los pedidos del socio en la lista de PedidosAceptados
            PedidosA2 = lists:map(aumentarPedidosSocio(Socio),PedidosA),
            %%Se elimina el pedido en proceso de la lista correspondiente
            PedidosP2 = lists:delete(Pid,PedidosP),
            proceso_tienda(Socios,Productos,PedidosA2,PedidosP2,N);
        %%El pedido manda mensaje para procesar el pedido
        {procesaPedidoDenegado, Pid}->
            %%Se elimina el pedido en proceso de la lista correspondiente
            PedidosP2 = lists:delete(Pid,PedidosP),
            proceso_tienda(Socios,Productos,PedidosA,PedidosP2,N);
        %%Mensaje recibido para cerrar la tienda
        {cerrar} -> io:format("Proceso Tienda : Tienda cerrada~n");
        %%Procesar un mensaje desconocido
        Otro -> io:format("Mensaje ~w desconocido~n",[Otro])
    end.

%%Funcion llamada para cada producto para comprobar las cantidades de los productos
checar_existencia(Producto,Lista)->
    Nombre = element(1,Producto),
    Cantidad = element(2, Producto),
    io:format("Mandando mensaje a ~s~n",[Nombre]),
    %%Se le manda el mensaje al producto para que procese el pedido
    whereis(Nombre) ! {pedir, Cantidad, self()},
    receive
        {pedido, Ncantidad}-> Lista ++ [{Nombre,Ncantidad}]
    after 10000->
        io:format("Error en el mensaje~n")
    end.

%%Proceso para llevar el proceso en espera en lo que el usuario lo acepta
pedidoProceso(Socio, Lista, Numero)->
    receive
        %%En caso de que el usuario acepte
        {acepta,SocioM,NumeroM}->
            if
                (SocioM == Socio) and (NumeroM == Numero)->
                    tienda ! {procesaPedidoAceptado, Socio,self()};
                (true) -> pedidoProceso(Socio,Lista,Numero)
            end;
        %%En caso de que el usario lo rechaze
        {deniega,SocioM,NumeroM}->
            if
                (SocioM == Socio) and (NumeroM == Numero)->
                    tienda ! {procesaPedidoDenegado, self()};
                (true) -> pedidoProceso(Socio,Lista,Numero)
            end
    end.

%%Funcion que regresa otra funcion para poder aumentar los pedidos completados del socio especificado
aumentarPedidosSocio(SocioBuscado)->
    fun(Elemento) ->
        Nombre = element(1,Elemento),
        Cantidad = element(2,Elemento),
        if
            (Nombre == SocioBuscado)-> Cantidad + 1;
            (true) -> Cantidad
        end
    end.

%%Funcion para cerrar la tienda
cierra_tienda()-> 
    tienda ! {cerrar}.

%%Manual de uso ->
%%1. Compilar y lanzar una terminal con el shortname de tienda(ej: erl -sname tienda)
%%2. Abrir la tienda con la funcion de abre_tienda()
%%3. Para salir usar la funcion de cierra_tienda()