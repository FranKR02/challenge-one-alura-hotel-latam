package controller;

import dao.ReservaDAO;
import dbConnection.ConnectionFactory;
import model.Reserva;

public class ReservaController {
	private ReservaDAO reservaDAO;
	public ReservaController() {
		this.reservaDAO = new ReservaDAO(new ConnectionFactory().recuperarConexion());
	}
	public void guardarReserva(Reserva reserva) {
		this.reservaDAO.guardar(reserva);
	}
	public int buscarUltimaReserva() {
		return this.reservaDAO.ultimaReserva();
	}
	
}