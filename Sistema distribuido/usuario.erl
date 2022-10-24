%% Autores - 
%% @ Eber Jesus Aguilera A00829692
%% @ Roberto Rodriguez A00829553
%% @ Rodrigo de la garza a00825271

-module(usuario).
-export([subscribir_socio/1,
         elimina_socio/1,
         crea_pedido/2,
         acepta_pedido/2,
         rechaza_pedido/2]).

subscribir_socio(Socio)->
    io:format("Proceso Invitado ~s: Comprador solicita subscripcion~n",[Socio]),
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {registrar,Socio,self()},
    receive
        {confirmado} -> io:format("Proceso Invitado ~s: Registro aceptado para el usuario~n",[Socio]);
        {existente} -> io:format("Proceso Invitado ~s: Solicitud rechazada, el nombre ya esta en uso~n",[Socio])
    end.

%% Subscribir socio manda una solicitud de registro a tienda, que va a confirmar en la lista de socios 
%% si ya existe. Si no, lo podra agregar. 

elimina_socio(Socio)->
    io:format("Proceso Socio ~s: Comprador solicita darse de baja~n",[Socio]),
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {eliminar,Socio,self()},
    receive
        {confirmado} -> io:format("Proceso Socio ~s: Cancelacion aceptada para el usuario~n",[Socio]);
        {no_existente} -> io:format("Proceso Socio ~s: Solicitud rechazada, el nombre no se ha encontrado~n",[Socio])
    end
    %%Eliminar pedidos pendientes
    .

%% Elimina_socio puede borrar un socio, pero tiene que confirmar con la lista de socios, haciendole 
%% una peticion primero a la tienda. 

crea_pedido(Socio,ListaDeProductos)->     
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {crear_pedido, Socio, ListaDeProductos, self()},
    receive
        {pedidoAjustado, Lista,N}-> imprimir_pedido(Lista,N);
        {socio_no_existente}-> io:format("Error: No se reconoce el socio")
    after 10000->
        io:format("F~n")
    end.

%% Para crear pedidos, se debera especificar el socio y la lista de productos que se le va a asignar para 
%% realizar una compra. Si el socio no existe previamente, habra un error que la tienda reconocera.  

acepta_pedido(Socio,Pedido)-> 
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {aceptar_pedido,Socio,Pedido},
    io:format("Usuario ~s acepta el pedido numero ~w~n",[Socio,Pedido]).

%% El usuario debe confirmar, aceptando o rechazando el pedido.

rechaza_pedido(Socio,Pedido)-> 
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {rechazar_pedido,Socio,Pedido},
    io:format("Usuario ~s rechaza el pedido numero ~w~n",[Socio,Pedido]).

%% Las funciones de imprimir pedido son recursivamente formateadas para imprimir uno por uno el producto 
%% y su cantidad. 

imprimir_pedido([H|T],Num)->
    io:format("Producto ~s: Cantidad ~w ~n", [element(1,H),element(2,H)]),
    imprimir_pedido(T,Num);
imprimir_pedido([],Num)->
    io:format("Numero de Pedido ~w ~n", [Num]).

% ----------- MANUAL DE USO ------------------

%% 1- Para hacer pedidos se debe subscribir un socio primero con subscribir_socio(Socio)
%% 2- Para crear un pedido debes agregar productos desde la tienda 
%% 3- Puedes modificar esta lista de productos libremente
%% 4- Debes aceptar o rechazar el pedido para confirmar y puedes obtener un ticket con imprimir pedido