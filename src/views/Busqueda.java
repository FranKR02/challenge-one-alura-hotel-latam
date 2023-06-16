package views;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.toedter.calendar.JDateChooserCellEditor;

import controller.HuespedController;
import controller.ReservaController;
import model.Huesped;
import model.Reserva;

/**
 * @author FRANK
 *
 */
/**
 * @author FRANK
 *
 */
/**
 * @author FRANK
 *
 */
@SuppressWarnings("serial")
public class Busqueda extends JFrame {

	private JPanel contentPane;
	private JTextField txtBuscar;
	private JTable tbHuespedes;
	private JTable tbReservas;
	private DefaultTableModel modelo;
	private DefaultTableModel modeloHuesped;
	private JLabel labelAtras;
	private JLabel labelExit;
	private static HuespedController huespedController = new HuespedController();
	private static ReservaController reservaController = new ReservaController();
	int xMouse, yMouse;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Busqueda frame = new Busqueda();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Busqueda() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Busqueda.class.getResource("/imagenes/lupa2.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 910, 571);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setUndecorated(true);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(536, 127, 193, 31);
		txtBuscar.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		contentPane.add(txtBuscar);
		txtBuscar.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("SISTEMA DE BÚSQUEDA");
		lblNewLabel_4.setForeground(new Color(12, 138, 199));
		lblNewLabel_4.setFont(new Font("Roboto Black", Font.BOLD, 24));
		lblNewLabel_4.setBounds(331, 62, 280, 42);
		contentPane.add(lblNewLabel_4);

		JTabbedPane panel = new JTabbedPane(JTabbedPane.TOP);
		panel.setBackground(new Color(12, 138, 199));
		panel.setFont(new Font("Roboto", Font.PLAIN, 16));
		panel.setBounds(20, 169, 865, 328);
		contentPane.add(panel);

		tbReservas = new JTable();
		tbReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbReservas.setFont(new Font("Roboto", Font.PLAIN, 16));
		modelo = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0 && column != 3; // Columna 0 y 3 no es editable
			}
		};
		tbReservas.setModel(modelo);
		modelo.addColumn("Numero de Reserva");
		modelo.addColumn("Fecha Check In");
		modelo.addColumn("Fecha Check Out");
		modelo.addColumn("Valor");
		modelo.addColumn("Forma de Pago");
		TableCellEditor dateEditor = new JDateChooserCellEditor();
		tbReservas.getColumnModel().getColumn(1).setCellEditor(dateEditor);
		tbReservas.getColumnModel().getColumn(2).setCellEditor(dateEditor);

		String[] formasPago = { "Tarjeta de Crédito", "Tarjeta de Débito", "Dinero en efectivo" };
		JComboBox<String> comboBoxEditor = new JComboBox<>(formasPago);
		DefaultCellEditor cellEditor = new DefaultCellEditor(comboBoxEditor);
		TableColumn formaPagoColumn = tbReservas.getColumnModel().getColumn(4);
		formaPagoColumn.setCellEditor(cellEditor);

		añadirReservas();

		tbReservas.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					// Fila y columna
					int row = e.getFirstRow();
					int column = e.getColumn();

					if (column != 4) {
						// Deshabilitar el listener temporalmente
						tbReservas.getModel().removeTableModelListener(this);
						// Establecer la hora, los minutos, los segundos y los milisegundos a cero
						Calendar calActual = Calendar.getInstance();
						calActual.set(Calendar.HOUR_OF_DAY, 0);
						calActual.set(Calendar.MINUTE, 0);
						calActual.set(Calendar.SECOND, 0);
						calActual.set(Calendar.MILLISECOND, 0);
						// Obtengo las fechas
						Object checkIn = tbReservas.getValueAt(row, 1);
						java.util.Date fechaEntrada = (java.util.Date) checkIn;
						Object checkOut = tbReservas.getValueAt(row, 2);
						java.util.Date fechaSalida = (java.util.Date) checkOut;
						// Formato de fechas
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						try {
							switch (column) {
							case 1:
								if (fechaEntrada == null) {
									throw new RuntimeException("Por favor registre la fecha de entrana correctamente");
								}
								Calendar calEntrada = Calendar.getInstance();
								calEntrada.setTime(fechaEntrada);
								if (calEntrada.before(calActual)) {
									throw new RuntimeException(
											"La fecha de entrada no puede ser anterior a la fecha actual, si es anterior a hoy no se puede editar");
								}
								if (fechaEntrada.compareTo(fechaSalida) > 0) {
									throw new RuntimeException("El Checkin no puede ser después del checkout");
								}
								modelo.setValueAt(calcularPrecio(fechaEntrada, fechaSalida), row, 3);
								String fechaEntradaStr = dateFormat.format(fechaEntrada);
								modelo.setValueAt(fechaEntradaStr, row, 1);
								break;
							case 2:
								Calendar calSalida = Calendar.getInstance();
								calSalida.setTime(fechaSalida);
								if (calSalida.before(calActual)) {
									throw new RuntimeException(
											"La fecha de salida no puede ser anterior a la fecha actua");
								}
								if (fechaEntrada.compareTo(fechaSalida) > 0) {
									throw new RuntimeException("El Checkin no puede ser después del checkout");
								}
								modelo.setValueAt(calcularPrecio(fechaEntrada, fechaSalida), row, 3);
								String fechaSalidaStr = dateFormat.format(fechaSalida);
								modelo.setValueAt(fechaSalidaStr, row, 2);
								break;
							default:
								break;
							}
						} catch (Exception ex) {
							Busqueda busqueda = new Busqueda();
							busqueda.setVisible(true);
							dispose();
							JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
						tbReservas.getModel().addTableModelListener(this);
					}
				}
			}

			private double calcularPrecio(Date fechaEntrada, Date fechaSalida) {
				long diferenciaMillis = fechaSalida.getTime() - fechaEntrada.getTime();
				long diferenciaDias = TimeUnit.MILLISECONDS.toDays(diferenciaMillis);
				int dias = (int) diferenciaDias;
				if (dias != 0) {
					return dias * 30000;
				} else {
					return 30000;
				}
			}
		});

		JScrollPane scroll_table = new JScrollPane(tbReservas);
		panel.addTab("Reservas", new ImageIcon(Busqueda.class.getResource("/imagenes/reservado.png")), scroll_table,
				null);
		scroll_table.setVisible(true);

		tbHuespedes = new JTable();
		tbHuespedes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbHuespedes.setFont(new Font("Roboto", Font.PLAIN, 16));
		modeloHuesped = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0 && column != 6; // Columnas 0 y 6 no son editables
			}
		};
		modeloHuesped.addColumn("Número de Huesped");
		modeloHuesped.addColumn("Nombre");
		modeloHuesped.addColumn("Apellido");
		modeloHuesped.addColumn("Fecha de Nacimiento");
		modeloHuesped.addColumn("Nacionalidad");
		modeloHuesped.addColumn("Telefono");
		modeloHuesped.addColumn("Número de Reserva");
		tbHuespedes.setModel(modeloHuesped);
		// ComboBox Nacionalidad
		String[] nacionalidad = { "afgano-afgana", "alemán-", "alemana", "árabe-árabe", "argentino-argentina",
				"australiano-australiana", "belga-belga", "boliviano-boliviana", "brasileño-brasileña",
				"camboyano-camboyana", "canadiense-canadiense", "chileno-chilena", "chino-china",
				"colombiano-colombiana", "coreano-coreana", "costarricense-costarricense", "cubano-cubana",
				"danés-danesa", "ecuatoriano-ecuatoriana", "egipcio-egipcia", "salvadoreño-salvadoreña",
				"escocés-escocesa", "español-española", "estadounidense-estadounidense", "estonio-estonia",
				"etiope-etiope", "filipino-filipina", "finlandés-finlandesa", "francés-francesa", "galés-galesa",
				"griego-griega", "guatemalteco-guatemalteca", "haitiano-haitiana", "holandés-holandesa",
				"hondureño-hondureña", "indonés-indonesa", "inglés-inglesa", "iraquí-iraquí", "iraní-iraní",
				"irlandés-irlandesa", "israelí-israelí", "italiano-italiana", "japonés-japonesa", "jordano-jordana",
				"laosiano-laosiana", "letón-letona", "letonés-letonesa", "malayo-malaya", "marroquí-marroquí",
				"mexicano-mexicana", "nicaragüense-nicaragüense", "noruego-noruega", "neozelandés-neozelandesa",
				"panameño-panameña", "paraguayo-paraguaya", "peruano-peruana", "polaco-polaca", "portugués-portuguesa",
				"puertorriqueño-puertorriqueño", "dominicano-dominicana", "rumano-rumana", "ruso-rusa", "sueco-sueca",
				"suizo-suiza", "tailandés-tailandesa", "taiwanes-taiwanesa", "turco-turca", "ucraniano-ucraniana",
				"uruguayo-uruguaya", "venezolano-venezolana", "vietnamita-vietnamita" };
		comboBoxEditor = new JComboBox<>(nacionalidad);
		cellEditor = new DefaultCellEditor(comboBoxEditor);
		TableColumn nacionanlidadColum = tbHuespedes.getColumnModel().getColumn(4);
		nacionanlidadColum.setCellEditor(cellEditor);
		// Fecha
		tbHuespedes.getColumnModel().getColumn(3).setCellEditor(dateEditor);

		añadirHuespedes();

		tbHuespedes.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					// Fila y columna
					int row = e.getFirstRow();
					int column = e.getColumn();

					if (column != 0 | column != 6) {
						// Deshabilitar el listener temporalmente
						tbHuespedes.getModel().removeTableModelListener(this);

						// Nombre y apellido
						String nombre = ((String) tbHuespedes.getValueAt(row, 1)).toUpperCase();
						String apellido = ((String) tbHuespedes.getValueAt(row, 2)).toUpperCase();
						// Obtengo la fecha de nacimiento
						Object fechaNacimientoObject = tbHuespedes.getValueAt(row, 3);
						java.util.Date fechaNacimiento = (java.util.Date) fechaNacimientoObject;
						// Formato de fechas
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						try {
							switch (column) {
							// Nombre y apellido
							case 1:
							case 2:
								if (nombre.isEmpty() | apellido.isEmpty()) {
									throw new RuntimeException("Todos los campos deben estar llenos");
								} else {
									if (!(nombre.matches("[a-zA-Z ]+") && apellido.matches("[a-zA-Z ]+"))) {
										throw new RuntimeException(
												"Ingrese solo letras (incluyendo espacios) en el nombre y apellido");
									}
								}
								break;

							// Fecha Nacimiento
							case 3:
								if (fechaNacimiento == null) {
									throw new RuntimeException("Ingrese una edad valida");
								}
								LocalDate fechaActual = LocalDate.now();
								LocalDate fechaNacimientoLocalDate = fechaNacimiento.toInstant()
										.atZone(ZoneId.systemDefault()).toLocalDate();
								// Calcular la edad
								Period edad = Period.between(fechaNacimientoLocalDate, fechaActual);
								if (edad.getYears() < 18) {
									throw new RuntimeException("Solo pueden hacer reserva los mayores de edad");
								}
								if (edad.getYears() > 150) {
									throw new RuntimeException("Edad incongruente");
								}

								String fechaNacimientoStr = dateFormat.format(fechaNacimiento);
								modeloHuesped.setValueAt(fechaNacimientoStr, row, 3);
								break;

							// Telefono
							case 5:
								String telefonoString = (String) tbHuespedes.getValueAt(row, 5);
								if (!telefonoString.matches("\\d+")) {
									throw new RuntimeException("Ingrese solo numeros en el telefono");
								}
								break;
							default:
								break;
							}
						} catch (Exception ex) {
							Busqueda busqueda = new Busqueda();
							busqueda.setVisible(true);
							dispose();
							JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
						tbHuespedes.getModel().addTableModelListener(this);
					}
				}
			}
		});

		JScrollPane scroll_tableHuespedes = new JScrollPane(tbHuespedes);
		panel.addTab("Huéspedes", new ImageIcon(Busqueda.class.getResource("/imagenes/pessoas.png")),
				scroll_tableHuespedes, null);

		scroll_tableHuespedes.setVisible(true);

		JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setIcon(new ImageIcon(Busqueda.class.getResource("/imagenes/Ha-100px.png")));
		lblNewLabel_2.setBounds(56, 51, 104, 107);
		contentPane.add(lblNewLabel_2);

		JPanel header = new JPanel();
		header.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				headerMouseDragged(e);
			}
		});
		header.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				headerMousePressed(e);
			}
		});
		header.setLayout(null);
		header.setBackground(Color.WHITE);
		header.setBounds(0, 0, 910, 36);
		contentPane.add(header);

		JPanel btnAtras = new JPanel();
		btnAtras.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MenuUsuario usuario = new MenuUsuario();
				usuario.setVisible(true);
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				btnAtras.setBackground(new Color(12, 138, 199));
				labelAtras.setForeground(Color.white);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnAtras.setBackground(Color.white);
				labelAtras.setForeground(Color.black);
			}
		});
		btnAtras.setLayout(null);
		btnAtras.setBackground(Color.WHITE);
		btnAtras.setBounds(0, 0, 53, 36);
		header.add(btnAtras);

		labelAtras = new JLabel("<");
		labelAtras.setHorizontalAlignment(SwingConstants.CENTER);
		labelAtras.setFont(new Font("Roboto", Font.PLAIN, 23));
		labelAtras.setBounds(0, 0, 53, 36);
		btnAtras.add(labelAtras);

		JPanel btnexit = new JPanel();
		btnexit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MenuUsuario usuario = new MenuUsuario();
				usuario.setVisible(true);
				dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) { // Al usuario pasar el mouse por el botón este cambiará de color
				btnexit.setBackground(Color.red);
				labelExit.setForeground(Color.white);
			}

			@Override
			public void mouseExited(MouseEvent e) { // Al usuario quitar el mouse por el botón este volverá al estado
													// original
				btnexit.setBackground(Color.white);
				labelExit.setForeground(Color.black);
			}
		});
		btnexit.setLayout(null);
		btnexit.setBackground(Color.WHITE);
		btnexit.setBounds(857, 0, 53, 36);
		header.add(btnexit);

		labelExit = new JLabel("X");
		labelExit.setHorizontalAlignment(SwingConstants.CENTER);
		labelExit.setForeground(Color.BLACK);
		labelExit.setFont(new Font("Roboto", Font.PLAIN, 18));
		labelExit.setBounds(0, 0, 53, 36);
		btnexit.add(labelExit);

		JSeparator separator_1_2 = new JSeparator();
		separator_1_2.setForeground(new Color(12, 138, 199));
		separator_1_2.setBackground(new Color(12, 138, 199));
		separator_1_2.setBounds(539, 159, 193, 2);
		contentPane.add(separator_1_2);

		JPanel btnbuscar = new JPanel();
		btnbuscar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		btnbuscar.setLayout(null);
		btnbuscar.setBackground(new Color(12, 138, 199));
		btnbuscar.setBounds(748, 125, 122, 35);
		btnbuscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		contentPane.add(btnbuscar);

		JLabel lblBuscar = new JLabel("BUSCAR");
		lblBuscar.setBounds(0, 0, 122, 35);
		btnbuscar.add(lblBuscar);
		lblBuscar.setHorizontalAlignment(SwingConstants.CENTER);
		lblBuscar.setForeground(Color.WHITE);
		lblBuscar.setFont(new Font("Roboto", Font.PLAIN, 18));

		JPanel btnEditar = new JPanel();
		btnEditar.setLayout(null);
		btnEditar.setBackground(new Color(12, 138, 199));
		btnEditar.setBounds(635, 508, 122, 35);
		btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		contentPane.add(btnEditar);

		JLabel lblEditar = new JLabel("EDITAR");
		lblEditar.setHorizontalAlignment(SwingConstants.CENTER);
		lblEditar.setForeground(Color.WHITE);
		lblEditar.setFont(new Font("Roboto", Font.PLAIN, 18));
		lblEditar.setBounds(0, 0, 122, 35);
		btnEditar.add(lblEditar);
		btnEditar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (editarDatos(panel, tbHuespedes, tbReservas) == 1) {
					JOptionPane.showMessageDialog(null, "Modificacion exitosa", "", JOptionPane.INFORMATION_MESSAGE);
					Busqueda busqueda = new Busqueda();
					busqueda.setVisible(true);
					dispose();
				}
			}
		});

		JPanel btnEliminar = new JPanel();
		btnEliminar.setLayout(null);
		btnEliminar.setBackground(new Color(12, 138, 199));
		btnEliminar.setBounds(767, 508, 122, 35);
		btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		contentPane.add(btnEliminar);

		JLabel lblEliminar = new JLabel("ELIMINAR");
		lblEliminar.setHorizontalAlignment(SwingConstants.CENTER);
		lblEliminar.setForeground(Color.WHITE);
		lblEliminar.setFont(new Font("Roboto", Font.PLAIN, 18));
		lblEliminar.setBounds(0, 0, 122, 35);
		btnEliminar.add(lblEliminar);
		setResizable(false);
		btnEliminar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (eliminarDatos(panel, tbHuespedes, tbReservas) == 1) {
					JOptionPane.showMessageDialog(null, "Eliminacion exitosa", "", JOptionPane.INFORMATION_MESSAGE);
					Busqueda busqueda = new Busqueda();
					busqueda.setVisible(true);
					dispose();
				}
			}
		});
	}

	protected int eliminarDatos(JTabbedPane panel, JTable tbHuespedes, JTable tbReservas) {
		if (panel.getSelectedIndex() == 0) {
			int filaSeleccionada = tbReservas.getSelectedRow();
			if (filaSeleccionada >= 0) {
				Object celdaId = modelo.getValueAt(filaSeleccionada, 0);
				int id = (int) celdaId;
				return reservaController.eliminar(id);
			} else {
				return 0;
			}
		} else {
			int filaSeleccionada = tbHuespedes.getSelectedRow();

			if (filaSeleccionada >= 0) {
				Object celdaId = modeloHuesped.getValueAt(filaSeleccionada, 0);
				int id = (int) celdaId;
				return huespedController.eliminar(id);
			} else {
				return 0;
			}
		}

	}

	protected int editarDatos(JTabbedPane panel, JTable tbHuespedes, JTable tbReservas) {
		if (panel.getSelectedIndex() == 0) {
			int filaSeleccionada = tbReservas.getSelectedRow();
			dejarDeEditar(tbReservas, filaSeleccionada, 3);

			if (filaSeleccionada >= 0) {
				DefaultTableModel modelo = (DefaultTableModel) tbReservas.getModel();
				Object[] fila = obtenerDatosFila(modelo, filaSeleccionada);

				Reserva reserva = crearReservaDesdeFila(fila);
				return reservaController.modificar(reserva);
			} else {
				return 0;
			}
		} else {
			int filaSeleccionada = tbHuespedes.getSelectedRow();
			dejarDeEditar(tbHuespedes, filaSeleccionada, 6);

			if (filaSeleccionada >= 0) {
				DefaultTableModel modeloHuesped = (DefaultTableModel) tbHuespedes.getModel();
				Object[] fila = obtenerDatosFila(modeloHuesped, filaSeleccionada);

				Huesped huesped = crearHuespedDesdeFila(fila);
				return huespedController.modificar(huesped);
			} else {
				return 0;
			}
		}
	}

	private Object[] obtenerDatosFila(DefaultTableModel modelo, int fila) {
		Object[] datos = new Object[modelo.getColumnCount()];
		for (int i = 0; i < modelo.getColumnCount(); i++) {
			datos[i] = modelo.getValueAt(fila, i);
		}
		return datos;
	}

	private Reserva crearReservaDesdeFila(Object[] fila) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Integer idReserva = (Integer) fila[0];
			Date fechaCheckIn = dateFormat.parse(fila[1].toString());
			Date fechaCheckOut = dateFormat.parse(fila[2].toString());
			String formaPago = (String) fila[4];
			Double valor = (Double) fila[3];
			return new Reserva(idReserva, fechaCheckIn, fechaCheckOut, formaPago, valor);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private Huesped crearHuespedDesdeFila(Object[] fila) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Integer idHuesped = (Integer) fila[0];
			String nombre = (String) fila[1];
			String apellido = (String) fila[2];
			Date fechaNacimiento = dateFormat.parse(fila[3].toString());
			String nacionalidad = (String) fila[4];
			String telefonoString = (String) fila[5];
			Long telefono = Long.parseLong(telefonoString);
			Integer idReserva = (Integer) fila[6];
			return new Huesped(idHuesped, nombre.toUpperCase(), apellido.toUpperCase(), fechaNacimiento, nacionalidad,
					telefono, idReserva);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void dejarDeEditar(JTable tb, int filaSeleccionada, int row) {
		if (tb.isEditing()) {
			tb.getCellEditor().stopCellEditing();
		}
		tb.changeSelection(filaSeleccionada, row, false, false);
		Rectangle cellRect = tb.getCellRect(filaSeleccionada, row, false);
		MouseEvent doubleClick = new MouseEvent(tb, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
				MouseEvent.BUTTON1, cellRect.x, cellRect.y, 2, false, MouseEvent.BUTTON1);
		tb.dispatchEvent(doubleClick);
	}

	private void añadirHuespedes() {
		DefaultTableModel modelo = (DefaultTableModel) tbHuespedes.getModel();
		List<Huesped> listaHuespedes = huespedController.listarHuespedes();
		for (Huesped huesped : listaHuespedes) {
			Object[] rowData = { huesped.getId(), huesped.getNombre(), huesped.getApellido(),
					huesped.getFechaNacimiento(), huesped.getNacionalidad(), huesped.getTelefono(),
					huesped.getidReserva() };
			modelo.addRow(rowData);
		}
	}

	private void añadirReservas() {
		DefaultTableModel modelo = (DefaultTableModel) tbReservas.getModel();
		List<Reserva> listarReservas = reservaController.listarReservas();
		for (Reserva reserva : listarReservas) {
			Object[] rowData = { reserva.getId(), reserva.getFechaEntrada(), reserva.getFechaSalida(),
					reserva.getValor(), reserva.getFormaPago() };
			modelo.addRow(rowData);
		}
	}

	private void headerMousePressed(java.awt.event.MouseEvent evt) {
		xMouse = evt.getX();
		yMouse = evt.getY();
	}

	private void headerMouseDragged(java.awt.event.MouseEvent evt) {
		int x = evt.getXOnScreen();
		int y = evt.getYOnScreen();
		this.setLocation(x - xMouse, y - yMouse);
	}
}
