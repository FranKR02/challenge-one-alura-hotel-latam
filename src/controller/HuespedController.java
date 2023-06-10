package controller;

import java.util.List;

import dao.HuespedDAO;
import dbConnection.ConnectionFactory;
import model.Huesped;

public class HuespedController {
	private HuespedDAO huespedDAO;
	public HuespedController() {
		this.huespedDAO = new HuespedDAO(new ConnectionFactory().recuperarConexion());
	}
	public void guardar(Huesped huesped) {
		this.huespedDAO.guardar(huesped);
	}
	public List<Huesped> listarHuespedes() {
		return this.huespedDAO.listarHuespedes();
	}
}