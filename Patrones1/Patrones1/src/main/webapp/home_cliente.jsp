    <%-- 
        Document   : newjsp
        Created on : 27 nov. 2025, 6:10:18 p. m.
        Author     : JHEINS
    --%>

    <%@page import="java.util.List"%>
    <%@page import="Modelo.Cuota"%>
    <%@page import="Modelo.Cliente"%>
    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html>

<head>
    <title>Mi Banca - Inicio</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f0f2f5; padding: 20px; }
        
        /* Tarjeta de Saldo (Estilo Tarjeta de Crédito) */
        .card-saldo {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white; padding: 30px; border-radius: 15px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.2); text-align: center; margin-bottom: 30px;
        }
        .card-saldo h2 { margin: 0; font-weight: 300; font-size: 1.2rem; opacity: 0.9; }
        .card-saldo .monto { font-size: 3rem; font-weight: bold; margin: 10px 0; }
        
        /* Contenedor de Acciones */
        .acciones-container { display: flex; gap: 20px; margin-bottom: 30px; }
        .accion-box {
            flex: 1; background: white; padding: 20px; border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.05); text-align: center;
            border-left: 5px solid #007bff;
        }
        .accion-box h3 { margin-top: 0; color: #333; }
        .btn-accion {
            display: inline-block; padding: 10px 20px; margin-top: 10px;
            text-decoration: none; border-radius: 5px; font-weight: bold;
            transition: 0.2s;
        }
        
        /* ESTILOS DE BOTONES EXISTENTES */
        .btn-azul { background-color: #007bff; color: white; }
        .btn-verde { background-color: #28a745; color: white; }

        /* AÑADIDO: Estilo para Retiro (Rojo) */
        .btn-withdraw { background-color: #dc3545; color: white; }
        
        /* CORREGIDO: Aplicar hover a todos los botones */
        .btn-azul:hover, .btn-verde:hover, .btn-withdraw:hover { opacity: 0.9; transform: translateY(-2px); }

        /* Tabla de Pagos */
        table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; }
        th { background: #343a40; color: white; padding: 12px; }
        td { padding: 12px; border-bottom: 1px solid #eee; text-align: center; }
        .badge { padding: 5px 10px; border-radius: 20px; background: #ffeeba; color: #856404; font-size: 0.8rem; font-weight: bold;}
    </style>
</head>
    <body>
        <%-- En home_cliente.jsp, justo después del <body> --%>
    <% 
        String mensaje = (String) request.getAttribute("msg");
        String error = (String) request.getAttribute("error");
    %>

    <% if (mensaje != null && mensaje.equals("exito_pago")) { %>
        <div style="background-color: #d4edda; color: #155724; padding: 15px; margin-bottom: 20px; border-radius: 5px;">
            ✅ ¡Pago realizado con éxito!
        </div>
    <% } %>

    <% if (error != null && error.equals("saldo_insuficiente")) { %>
        <div style="background-color: #f8d7da; color: #721c24; padding: 15px; margin-bottom: 20px; border-radius: 5px;">
            ⚠️ No tienes saldo suficiente para realizar el pago.
        </div>
    <% } %>
    <%-- Validar Sesión --%>
        <%
            Cliente c = (Cliente) session.getAttribute("cliente");
            if(c == null) { response.sendRedirect("login_cliente.jsp"); return; }

            // --- CORRECCIÓN DE SEGURIDAD PARA EVITAR ERROR 500 ---

            // 1. Recuperar Saldo de forma segura (evita el NullPointerException)
            Double saldoObj = (Double) request.getAttribute("saldoActual");
            double saldo = (saldoObj != null) ? saldoObj : 0.00;

            // 2. Recuperar Cuotas de forma segura
            List<Cuota> cuotas = (List<Cuota>) request.getAttribute("listaCuotas");
            if (cuotas == null) {
                cuotas = new java.util.ArrayList<Cuota>(); // Lista vacía para que no falle el for-loop
            }
        %>

        <div class="card-saldo">
            <h2>Saldo Disponible</h2>
            <div class="monto">S/ <%= String.format("%.2f", saldo) %></div>
            <p>Hola, <%= c.getNombre() %></p>
        </div>


    <div class="acciones-container">
        <div class="accion-box">
            <h3>¿Necesitas Dinero?</h3>
            <p>Solicita un nuevo crédito al instante.</p>
            <a href="solicitar_prestamo.jsp" class="btn-accion btn-azul">Solicitar Préstamo</a>
        </div>

        <div class="accion-box" style="border-left-color: #007bff;">
            <h3>Depositar</h3>
            <p>Añade fondos a tu saldo.</p>
            <a href="TransaccionServlet?tipo=DEPOSITO" class="btn-accion btn-azul">Depositar</a>
        </div>

        <div class="accion-box" style="border-left-color: #dc3545;">
            <h3>Retirar</h3>
            <p>Saca efectivo de tu cuenta.</p>
            <a href="TransaccionServlet?tipo=RETIRO" class="btn-accion btn-withdraw">Retirar</a>
        </div>

        <div class="accion-box" style="border-left-color: #28a745;">
            <h3>Próximos Vencimientos</h3>
            <p>Tienes <%= cuotas.size() %> cuota(s) pendiente(s).</p>
        </div>
    </div>

    <h3>💳 Tus Cuotas Pendientes</h3>
        <table>
            <thead>
                <tr>
                    <th># Cuota</th>
                    <th>Vencimiento</th>
                    <th>Monto</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                <%-- VALIDACIÓN DE SEGURIDAD: Verifica si la lista es nula o está vacía --%>
                <% if (cuotas == null || cuotas.isEmpty()) { %>
                    <tr>
                        <td colspan="5" style="padding: 20px; color: #666;">
                            ¡Estás al día! No tienes deudas pendientes.
                        </td>
                    </tr>
                <% } else { 
                     for(Cuota cuota : cuotas) { %>
                    <tr>
                        <td><%= cuota.getNumeroCuota() %></td>
                        <td><%= cuota.getFechaVencimiento() %></td>
                        <%-- Formato de moneda a 2 decimales --%>
                        <td>S/ <%= String.format("%.2f", cuota.getMonto()) %></td>
                        <td><span class="badge"><%= cuota.getEstado() %></span></td>
                        <td>
                            <form action="PagarCuotaServlet" method="POST">
                                <input type="hidden" name="idCuota" value="<%= cuota.getIdCuota() %>">
                                <input type="hidden" name="monto" value="<%= cuota.getMonto() %>">

                                <button type="submit" class="btn-accion btn-verde" style="margin:0; padding: 5px 15px;">
                                    Pagar
                                </button>
                            </form>
                        </td>
                    </tr>
                <% } } %>
            </tbody>
        </table>
    </body>
    </html>