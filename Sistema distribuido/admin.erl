%% Autores - 
%% @ Eber Jesus Aguilera A00829692
%% @ Roberto Rodriguez A00829553
%% @ Rodrigo de la garza a00825271

-module(admin).
-export([registra_producto/2,
         elimina_producto/1,
         modifica_producto/2]).

% Funcion para registrar producto, tiene de entrada el nombre del producto y cantidad.

registra_producto(Producto,Cantidad)->
    
    % Despues de desplegar que el prosseso de registracion a iniciado se le manda a la tienda el producto, la cantidad y el pid de la funcion. 

    io:format("Proceso Registro Producto ~s: Se inicia el registro del producto~n",[Producto]),
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {registrar_producto, Producto, Cantidad, self()},
    receive
        % La tienda va a contestar confirmado o existente dependiendo si el producto ya existe, el resultado se va desplegar.

        {confirmado} -> io:format("Proceso Registro Producto ~s: Registro aceptado para el producto~n",[Producto]);
        {existente} -> io:format("Proceso Registro Producto ~s: Solicitud rechazada, el nombre ya esta en uso~n",[Producto])
    end. 
% Funcion para eliminar un producto del registro, tiene de entrada el nombre del producto.

elimina_producto(Producto)->

    % Despues de desplegar que el prosseso de la eliminacion a iniciado se manda a la tienda el producto y el pid de la funcion para que regrese el mensaje.

    io:format("Proceso Elimina Producto ~w: Peticion para dar de baja el producto~n",[Producto]),
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {eliminar_producto,Producto,self()},
    receive

        % La tienda manda el mensaje de regreso ya sea confiramdo o no existente y esta funcion desplega el resultado. 
        {confirmado} -> io:format("Proceso Elimina Producto ~s: Cancelacion aceptada para el producto~n",[Producto]);
        {no_existente} -> io:format("Proceso Elimina Producto ~s: Solicitud rechazada, el nombre no se ha encontrado~n",[Producto])
    end. 

% Funcion para modificar la cantidad del producto, tiene de entrada el nombre del producto y la cantidad que deseas cambiar, si le pones manzanas, 5 se le va agregar 5 a la cantidad de manzanas.
% Si le pones manzanas, -5 se le va restrar 5 a la cantidad de manzanas.

modifica_producto(Producto,Cantidad)->
    {tienda,list_to_atom("tienda@" ++ net_adm:localhost())} ! {modifica, Producto, Cantidad, self()},
    receive
% Despues de mandarle a la tienda el nombre del producto, la cantidad y el pid de la funcion la tienda va a regresar ya sea confirmado significando que el cambio fue exitoso o no existente si no se encontro el producto. 
% Se va desplegar el resultado ya sea exitoso o no exitoso. 

        {confirmado} -> io:format("Proceso Modifica Producto ~s: Modificacion de la cantidad aceptada~n",[Producto]);
        {no_existente} -> io:format("Proceso Modifica Producto ~s: Modificacion de la cantidad denegada, el nombre no se ha encontrado~n",[Producto])
    end. 
